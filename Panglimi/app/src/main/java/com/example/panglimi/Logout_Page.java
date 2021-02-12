package com.example.panglimi;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class Logout_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_page);

        Login_Page.input_id="";
        //SharedPreferences에 저장된 값들을 로그아웃 버튼을 누르면 삭제하기 위해
        //SharedPreferences를 불러옵니다. 메인에서 만든 이름으로
        Intent intent = new Intent(Logout_Page.this, Login_Page.class);
        startActivity(intent);
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = auto.edit();
        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
        editor.clear();
        editor.commit();

        finish();

        onClickLogout();
        Toast.makeText(Logout_Page.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Logout_Page.this, Login_Page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  //현재 액티비티에를 스택에서 제거
        startActivity(intent);
    }
}