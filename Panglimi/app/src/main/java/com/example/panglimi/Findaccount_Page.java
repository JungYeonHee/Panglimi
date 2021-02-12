package com.example.panglimi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

public class Findaccount_Page extends AppCompatActivity {

    EditText name , email;
    Button okBtn;

    static String find_id="";
    static String find_passwd="";
    String[] curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findaccount_page);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        okBtn = (Button)findViewById(R.id.okBtn);



        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkname = name.getText().toString();
                String checkemail = email.getText().toString();

                curUser = new String[5];
                final String[] result1 = {""};
                try {
                    String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/findid.php?user_name=" + checkname+ "&user_email=" + checkemail;
                    result1[0] = new GetJSONObject().execute(url).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (result1[0].trim().equals("유")) {
                    //맞는 아이디 비번 가져오기
                    curUser = new String[5];
                    final String[] result2 = {""};
                    try {
                        String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/getid.php?user_name=" + checkname;
                        result2[0] = new GetJSONObject().execute(url).get();
                        curUser = GetJSONObject.jsonParser_user(result2[0]); //json 파일 파싱하기
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    find_id = curUser[1];
                    find_passwd = curUser[2];

                    Intent intent = new Intent(Findaccount_Page.this, Successfind_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if (result1[0].trim().equals("무")) {
                    Toast.makeText(getApplicationContext(),"이름 또는 이메일을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}