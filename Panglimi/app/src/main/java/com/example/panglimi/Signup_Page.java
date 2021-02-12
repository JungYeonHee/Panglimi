package com.example.panglimi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class Signup_Page extends AppCompatActivity {

    Button checkBtn, signupBtn;
    EditText name;
    EditText id;
    EditText pw;
    EditText repw;
    EditText email;
    EditText phone;
    TextView overlap_confrim; // 아이디 중복체크 뜨는 텍스트
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        checkBtn = (Button)findViewById(R.id.checkBtn);
        signupBtn = (Button)findViewById(R.id.signupBtn);
        overlap_confrim = (TextView)findViewById(R.id.overlap_confrim);
        checkBox = (CheckBox)findViewById(R.id.checkBox);

        name = (EditText)findViewById(R.id.name);
        id = (EditText)findViewById(R.id.id);
        pw = (EditText)findViewById(R.id.pw);
        repw = (EditText)findViewById(R.id.repw);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);

        //  mContext = this;

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String overid = id.getText().toString();

                final String[] result1 = {""};
                try {
                    String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/overlap_name.php?user_id=" + overid;
                    Log.d("debug", overid);
                    result1[0] = new GetJSONObject().execute(url).get();
                    Log.d("debug", result1[0]);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(result1[0].trim().equals("확인") ) {
                    overlap_confrim.setText("사용가능한 아이디입니다");
                    overlap_confrim.setTextColor(Color.parseColor("#0054FF"));
                    checkBtn.setText("중복확인");
                    checkBtn.setTextColor(Color.parseColor("#0054FF"));
                }
                else if(result1[0].trim().equals("중복")) {
                    overlap_confrim.setText("사용 불가 아이디입니다.");
                    checkBtn.setText("중복체크");
                    checkBtn.setTextColor(Color.parseColor("#000000"));
                    overlap_confrim.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String input_name = name.getText().toString();
                String input_id = id.getText().toString();
                String input_pw = pw.getText().toString();
                String input_repw= repw.getText().toString();
                String input_email = email.getText().toString();
                String input_phone = phone.getText().toString();
                final String[] result2 = {""};
                String btnM = checkBtn.getText().toString();

                if(input_name.equals("") || input_id.equals("") || input_email.equals("") || input_phone.equals("") || input_pw.equals("") || input_repw.equals(""))
                    Toast.makeText(getApplicationContext(), "입력하지 않은 부분이 있습니다.", Toast.LENGTH_SHORT).show();
                else if(!checkBox.isChecked())
                    Toast.makeText(getApplicationContext(), "약관에 동의체크를 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                else if(btnM.equals("중복체크"))
                    Toast.makeText(getApplicationContext(), "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                else if (overlap_confrim.equals("사용 불가 아이디입니다."))
                    Toast.makeText(getApplicationContext(), "아이디 중복체크를 다시 해주세요.", Toast.LENGTH_SHORT).show();
                else if(!input_pw.equals(input_repw))
                    Toast.makeText(getApplicationContext(), "비밀번호확인이 틀렸습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/origin_signup.php?input_name=" + input_name + "&input_id=" + input_id +
                                "&input_pw=" + input_pw + "&input_email=" + input_email + "&input_phone=" + input_phone;

                        result2[0] = new GetJSONObject().execute(url).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent=new Intent(Signup_Page.this, Login_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}