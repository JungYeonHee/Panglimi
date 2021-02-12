package com.example.panglimi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import java.util.concurrent.ExecutionException;

public class Withdraw_Page extends AppCompatActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_page);

        final String id = Login_Page.input_id;
        final String passwd = Login_Page.input_passwd;

        String nextid = id.trim();

        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dig = new AlertDialog.Builder(Withdraw_Page.this);

                dig.setMessage("정말로 탈퇴하신건가요?");
                dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder dig2 = new AlertDialog.Builder(Withdraw_Page.this);
                        dig2.setMessage("그 동안 팡리미를 이용해주셔서 감사합니다.");
                        dig2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String[] result = {""};
                                try {
                                    String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/delete_info.php?id=" + id + "&passwd=" + passwd ;
                                    result[0] = new GetJSONObject().execute(url).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                onClickLogout();
                            }
                        });
                        dig2.show();
                    }
                });
                dig.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickLogout2();
                    }
                });
                dig.show();
            }
        });
    }
    private void onClickLogout() {
        if(Login_Page.input_id.length()==0) {       //카카오로그인일때
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    redirectLoginActivity();
                }
            });
        }
        else {                                      //일반로그인일때
            //SharedPreferences에 저장된 값들을 로그아웃 버튼을 누르면 삭제하기 위해
            //SharedPreferences를 불러옵니다. 메인에서 만든 이름으로
            Login_Page.input_id="";
            Intent intent = new Intent(Withdraw_Page.this, Login_Page.class);
            startActivity(intent);
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
            Toast.makeText(Withdraw_Page.this, "탈퇴 완료", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), Login_Page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void onClickLogout2() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity2();
            }
        });
    }

    private void redirectLoginActivity2() {
        Intent intent = new Intent(getApplicationContext(), Withdraw_Page.class);
        startActivity(intent);
    }
}
