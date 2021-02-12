package com.example.panglimi;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.time.chrono.MinguoChronology;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Caution_Page extends Fragment {
    static final CautionAdapter adapter = new CautionAdapter();
    String[] cautionString;
    private RequestQueue mQueue;
    static String userStickNum = "88"; //사용자의 stickNum 입력 받는 곳에서 입력값을 이 변수에 넣어줌. 지금은 임의로 24
    static int cautionCnt=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.caution_page, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.caution_recyclerView);
        cautionString = new String[3*cautionCnt];
        Spinner spinner = (Spinner)rootView.findViewById(R.id.spinner);
        String[] spinnerItem = new String[]{"최신순", "오래된순"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, spinnerItem);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1)); //구분선 넣기

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).toString().equals("최신순")){
                    CautionAdapter.items.clear(); // 어댑터 초기화
                    mQueue = Volley.newRequestQueue(getActivity());
                    final String[] result = {""};
                    try {
                        String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/caution_select_final.php?stickNum=" + MainActivity.userStickNum + "&align=desc"; //서버로 보낼 url
                        result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                        cautionString = GetJSONObject.jsonParser_location(result[0]); //json 파일 파싱하기
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(cautionString != null){
                        for(int k=0; k<cautionCnt; k++){
                            Geocoder geocoder = new Geocoder(getActivity());
                            List<Address> list = null;
                            try{
                                double d1 = Double.parseDouble(cautionString[3*k+0]);
                                double d2 = Double.parseDouble(cautionString[3*k+1]);
                                list = geocoder.getFromLocation(d1, d2, 1);
                                String[] tokenizer = list.get(0).getAddressLine(0).split("\\s");
                                StringBuilder address = new StringBuilder();
                                address.append(" ");
                                for(int j=1; j<tokenizer.length; j++)
                                    address.append(tokenizer[j] + " ");
                                adapter.addItem(new Caution('\"'+address.toString()+"\"에서 접촉 발생", cautionString[3*k+2]));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        recyclerView.setAdapter(adapter);
                    }
                }
                else if(adapterView.getItemAtPosition(i).toString().equals("오래된순")) {
                    CautionAdapter.items.clear(); // 어댑터 초기화
                    mQueue = Volley.newRequestQueue(getActivity());
                    final String[] result = {""};
                    try {
                        String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/caution_select_final.php?stickNum=" + MainActivity.userStickNum + "&align=asc"; //서버로 보낼 url
                        result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                        cautionString = GetJSONObject.jsonParser_location(result[0]); //json 파일 파싱하기
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(cautionString != null){
                        for(int k=0; k<cautionCnt; k++){
                            Geocoder geocoder = new Geocoder(getActivity());
                            List<Address> list = null;
                            try{
                                double d1 = Double.parseDouble(cautionString[3*k+0]);
                                double d2 = Double.parseDouble(cautionString[3*k+1]);
                                list = geocoder.getFromLocation(d1, d2, 1);
                                String[] tokenizer = list.get(0).getAddressLine(0).split("\\s");
                                StringBuilder address = new StringBuilder();
                                address.append(" ");
                                for(int j=1; j<tokenizer.length; j++)
                                    address.append(tokenizer[j] + " ");
                                adapter.addItem(new Caution('\"'+address.toString()+"\"에서 접촉 발생", cautionString[3*k+2]));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return rootView;
    }
}


