package com.example.panglimi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Nonnetwork_Page extends AppCompatActivity {

    String loginId=Login_Page.loginId;
    String loginPwd=Login_Page.loginPwd;
    String input_id=Login_Page.input_id;
    String input_passwd=Login_Page.input_passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonnetwork_page);
        Button btn=(Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status=NetworkStatus.getConnectivityStatus(getApplicationContext());

                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    if (loginId != null && loginPwd != null) {
                        input_id=loginId;
                        input_passwd=loginPwd;
                        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else if (loginId == null && loginPwd == null) {
                        Intent intent=new Intent(getApplicationContext(), Login_Page.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "다시 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private long lastTimeBackPressed; //뒤로가기 버튼이 클릭된 시간
    @Override
    public void onBackPressed()
    {
        //2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000)
        {
            finish();
            return;
        }
        //'뒤로' 버튼 한번 클릭 시 메시지
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        //lastTimeBackPressed에 '뒤로'버튼이 눌린 시간을 기록
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
