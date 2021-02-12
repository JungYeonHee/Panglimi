package com.example.panglimi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kyleduo.switchbutton.SwitchButton;
import java.util.concurrent.ExecutionException;

public class Setting_Page extends Fragment {
    Button editBtn, addressBtn,logoutBtn, withdrawBtn,versionBtn,stickBtn;
    SwitchButton alarmButton;
    TextView realstickNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.setting_page, container, false);

        editBtn = (Button)rootView.findViewById(R.id.editBtn);
        addressBtn = (Button)rootView.findViewById(R.id.addressBtn);
        logoutBtn = (Button)rootView.findViewById(R.id.logoutBtn);
        withdrawBtn = (Button)rootView.findViewById(R.id.withdrawBtn);
        versionBtn = (Button)rootView.findViewById(R.id.versionBtn);
        stickBtn = (Button)rootView.findViewById(R.id.stickBtn);
        alarmButton= (SwitchButton) rootView.findViewById(R.id.alarm_state);

        String[] addressInform = new String[2];

        //주소를 수정했을 때
        try {
            final String[] result = {""};
            String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + Login_Page.input_id; //서버로 보낼 url
            result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
            addressInform = GetJSONObject.jsonParser_address(result[0]); //json 파일 파싱하기
            if(addressInform != null){
                if(addressInform[0] != null && addressInform[1] != null){
                    if(!addressInform[0].equals("null") && !addressInform[1].equals("null")) {
                        MyService.userLatitude = Double.parseDouble(addressInform[0]);
                        MyService.userLongtitude = Double.parseDouble(addressInform[1]);
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
        }

        alarmButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    MainActivity.alarmState = "on";
                }
                else{
                    MainActivity.alarmState = "off";
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Login_Page.input_id.length()==0) {
                    Toast.makeText(getActivity().getApplicationContext(), "카카오로그인은 정보수정이 불가합니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.onFragmentChanged(0);
                }
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Address_Page.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Logout_Page.class);
                startActivity(intent);
            }
        });

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Withdraw_Page.class);
                startActivity(intent);
            }
        });

        versionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.onFragmentChanged(1);
            }
        });

        stickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Login_Page.input_id.length()== 0) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Kakaosticknum_Page.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), Sticknum_Page.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getActivity(), Login_Page.class);
        startActivity(intent);
    }
}


