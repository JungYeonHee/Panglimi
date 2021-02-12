package com.example.panglimi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;

public class Edit_Page extends Fragment {

    Button  signupBtn;
    EditText name,id,pw,repw,email,phone;
    String[] curUser, modUser;
    String modName, modId, modPw, modEmail, modPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.edit_page, container, false);

        signupBtn = (Button)rootView.findViewById(R.id.signupBtn);
        name = (EditText)rootView.findViewById(R.id.name);
        id = (EditText)rootView.findViewById(R.id.id);
        pw = (EditText)rootView.findViewById(R.id.pw);
        repw = (EditText)rootView.findViewById(R.id.repw);
        email = (EditText)rootView.findViewById(R.id.email);
        phone = (EditText)rootView.findViewById(R.id.phone);

        curUser = new String[5];
        modUser = new String[5];

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

        final String curName, curId, curPw, curEmail, curPhone;
        curName = curUser[0];
        curId = curUser[1];
        curPw = curUser[2];
        curEmail = curUser[3];
        curPhone = curUser[4];
        name.setText(curName);
        id.setText(curId);
        pw.setText(curPw);
        repw.setText(curPw);
        email.setText(curEmail);
        phone.setText(curPhone);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pw.getText().toString().equals(repw.getText().toString())){
                    modName = name.getText().toString();
                    modId = id.getText().toString();
                    modPw = pw.getText().toString();
                    modEmail = email.getText().toString();
                    modPhone = phone.getText().toString();
                    modUser[0] = modName;
                    modUser[1] = modId;
                    modUser[2] = modPw;
                    modUser[3] = modEmail;
                    modUser[4] = modPhone;
                    if(!curId.equals(modId) || !curPw.equals(modPw) || !curEmail.equals(modEmail) || !curPhone.equals(modPhone)){
                        final String[] result = {""};
                        try {
                            String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_update_final.php?before_id=" + curId + "&user_name=" + modName + "&user_id=" + modId + "&user_passwd=" + modPw
                                    +"&user_email=" + modEmail + "&user_phone=" + modPhone;
                            result[0] = new GetJSONObject().execute(url).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.onFragmentChanged(1);

                    Toast.makeText(getActivity(),"정보수정 완료", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"비밀번호확인이 틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;

    }
}
