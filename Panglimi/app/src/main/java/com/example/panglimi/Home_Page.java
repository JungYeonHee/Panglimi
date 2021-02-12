package com.example.panglimi;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Home_Page extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    static String[] map;
    private RequestQueue mQueue;
    static int cautionCnt=0;
    TextView addressText;
    String[] curUser;
    String[] curUser2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.home_page, container, false);
        int status = NetworkStatus.getConnectivityStatus(getActivity().getApplicationContext());
        if(status == NetworkStatus.TYPE_NOT_CONNECTED) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Nonnetwork_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            addressText = rootView.findViewById(R.id.home_address);

            //지팡이번호 디비에서 가져와서 보여주기 - 일반
            curUser = new String[6];
            try {
                final String[] result = {""};
                String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + Login_Page.input_id; //서버로 보낼 url
                result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                curUser = GetJSONObject.jsonParser_user(result[0]); //json 파일 파싱하기
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(curUser != null)
                Sticknum_Page.stickNum = curUser[5];

            //지팡이번호 디비에서 가져와서 보여주기 - 카카오
            curUser2 = new String[6];
            try {
                final String[] result = {""};
                String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/sticknum_select.php?user_name=" + Login_Page.nickName; //서버로 보낼 url
                result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                curUser2 = GetJSONObject.jsonParser_users(result[0]); //json 파일 파싱하기
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(Login_Page.input_id.length() == 0){
                if(curUser2 != null) {
                    Kakaosticknum_Page.kakaosticknum = curUser2[5];
                }
                else{
                    if (Kakaosticknum_Page.stickNum.length()==0)        //첫 로그인일때
                        Toast.makeText(getActivity().getApplicationContext(), "설정에서 지팡이 번호를 입력해주세요", Toast.LENGTH_LONG).show();
                }
            }

            map = new String[3*Caution_Page.cautionCnt];

            mQueue = Volley.newRequestQueue(getActivity());
            final String[] result = {""};
            try {
                String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/caution_select_final.php?stickNum=" + MainActivity.userStickNum; //서버로 보낼 url
                result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                map = GetJSONObject.jsonParser_location(result[0]); //json 파일 파싱하기
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addressText.bringToFront();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map, mapFragment);
            fragmentTransaction.commit();
            mapFragment.getMapAsync(this);
        }

        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        mMap.animateCamera(zoom);
        double v=37.5973535, v1=127.01889609999999; //아무것도 없다면 학교가 나오도록 구현
        String address = null;

        for(int c=0; c<cautionCnt; c++){
            MarkerOptions makerOptions = new MarkerOptions();
            if(map != null){
                try{
                    v = Double.parseDouble(map[3*c+0]);
                    v1 = Double.parseDouble(map[3*c+1]);
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<Address> list = null;
                    list = geocoder.getFromLocation(v, v1, 1);
                    String[] tokenizer = list.get(0).getAddressLine(0).split("\\s");
                    StringBuilder tokenAddress = new StringBuilder();
                    for(int j=1; j<tokenizer.length; j++)
                        tokenAddress.append(tokenizer[j] + " ");
                    address = tokenAddress.toString().trim();
                    makerOptions.position(new LatLng(v, v1)).title(address);

                    // 2. 마커 생성 (마커를 나타냄)
                    mMap.addMarker(makerOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(v, v1), 16));
        addressText.setText(address);
    }
}


