package com.example.panglimi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import java.util.concurrent.ExecutionException;

public class Login_Page extends AppCompatActivity {
    //로그인 페이지

    Button signupBtn, findBtn;
    Button loginBtn;
    EditText id, password;
    CheckBox autoLogin;
    static String loginId, loginPwd, stickNum = "";
    private SessionCallback callback;
    String[] curUser;
    static String nickName,email="";
    /**
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    public static String input_id = "";
    public static String input_passwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        loginBtn = (Button) findViewById(R.id.loginbtn);
        findBtn = (Button) findViewById(R.id.findBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);
        final String str_id = "panglimi";
        final String str_pw = "1234";

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
            Log.d("debug", "안됨");
            Intent intent = new Intent(getApplicationContext(), Nonnetwork_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        //자동로그인
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId", null);
        loginPwd = auto.getString("inputPwd", null);

        //회원가입 페이지로
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                    Log.d("debug", "안됨");
                    Intent intent = new Intent(getApplicationContext(), Nonnetwork_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getApplicationContext(), Signup_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        //아이디비번찾기 페이지로
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                    Log.d("debug", "안됨");
                    Intent intent = new Intent(getApplicationContext(), Nonnetwork_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), Findaccount_Page.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        //SharedPreferences에 id,pw정보가 있을때
        //값을 가져와 자동적으로 액티비티 이동.
        if (loginId != null && loginPwd != null) {
            input_id = loginId;
            input_passwd = loginPwd;
            Toast.makeText(Login_Page.this, loginId + "님 자동로그인 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login_Page.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (loginId == null && loginPwd == null) {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input_id=id.getText().toString();
                    input_passwd=password.getText().toString();

                    int status=NetworkStatus.getConnectivityStatus(getApplicationContext());
                    if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                        Log.d("debug", "안됨");
                        Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else {
                        curUser=new String[6];
                        try {
                            final String[] result2={""};
                            String urlstr="http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + input_id; //서버로 보낼 url
                            result2[0]=new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                            curUser=GetJSONObject.jsonParser_user(result2[0]); //json 파일 파싱하기
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    stickNum = curUser[5];

                        final String[] result={""};
                        try {
                            String url="http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/login.php?id=" + input_id + "&passwd=" + input_passwd;
                            result[0]=new GetJSONObject().execute(url).get();

                            if (input_id.equals("") || input_passwd.equals("")) {
                                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                            } else if (result[0].trim().equals("로그인 가능")) {
                                if (autoLogin.isChecked()) {
                                    SharedPreferences auto=getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                    //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                                    SharedPreferences.Editor autoLogin=auto.edit();
                                    autoLogin.putString("inputId", id.getText().toString());
                                    autoLogin.putString("inputPwd", password.getText().toString());
                                    //꼭 commit()을 해줘야 값이 저장됨
                                    autoLogin.commit();
                                    if (stickNum.length() == 0) {
                                        Toast.makeText(getApplicationContext(), "지팡이번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                                            Log.d("debug", "안됨");
                                            Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        } else {
                                            Intent intent=new Intent(Login_Page.this, Sticknum_Page.class); //파라메터는 현재 액티비티, 전환될 액티비티
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent); //엑티비티 요청
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                                            Log.d("debug", "안됨");
                                            Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Intent intent=new Intent(Login_Page.this, MainActivity.class); //파라메터는 현재 액티비티, 전환될 액티비티
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent); //엑티비티 요청
                                            finish();
                                        }
                                    }
                                } else {
                                    if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                                        Log.d("debug", "안됨");
                                        Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else if (stickNum.length() == 0) {   //지팡이 번호 저장 안되어있을때
                                        Toast.makeText(getApplicationContext(), "지팡이번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                                            Log.d("debug", "안됨");
                                            Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        } else {
                                            Intent intent=new Intent(Login_Page.this, Sticknum_Page.class); //파라메터는 현재 액티비티, 전환될 액티비티
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent); //엑티비티 요청
                                        }
                                    } else {   //지팡이 번호 저장 되어있을때
                                        Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                        if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                                            Log.d("debug", "안됨");
                                            Intent intent=new Intent(getApplicationContext(), Nonnetwork_Page.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        } else {
                                            Intent intent=new Intent(Login_Page.this, MainActivity.class); //파라메터는 현재 액티비티, 전환될 액티비티
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent); //엑티비티 요청
                                        }
                                    }
                                }
                            } else if (result[0].trim().equals("틀림"))
                                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        //카카오로그인
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }

    protected void redirectSignupActivity() {
        if (stickNum.length()==0) {         //첫 로그인일때
            //Toast.makeText(getApplicationContext(), "설정에서 지팡이 번호를 입력해주세요", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
        requestMe();
    }

    public void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override

            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
            }

            @Override
            public void onSuccess(MeV2Response result) {
                UserAccount kakaoAccount = result.getKakaoAccount();

                final String[] result2 = {""};
                if (kakaoAccount != null) {

                    // 이메일
                    String email = kakaoAccount.getEmail();

                    if (email != null) {
                        Log.i("KAKAO_API", "email: " + email);
                    }
                    nickName = result.getNickname();
                    try {
                        String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/sign_up.php?nickName=" + nickName + "&email=" + email;
                        result2[0] = new GetJSONObject().execute(url).get();
                        //System.out.println(result);
                        Log.d("debug", "nickname: " + nickName);
                        Log.d("debug", "email: " + email);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
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