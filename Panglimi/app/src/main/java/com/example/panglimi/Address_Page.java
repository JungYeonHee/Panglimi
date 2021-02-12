package com.example.panglimi;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Address_Page extends AppCompatActivity {

    private WebView daum_webView;
    static public TextView daum_result, savedad;
    static String address, savedaddress = "";
    String[] curUser = new String[7];
    String[] curUserk = new String[12];
    final String id = Login_Page.input_id;

    Button btn;
    final Geocoder geocoder = new Geocoder(this);
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_page);

        daum_result = (TextView) findViewById(R.id.daum_result);
        btn = (Button) findViewById(R.id.btn);
        savedad = (TextView) findViewById(R.id.savedad);

        daum_result.setText("");

        if (id.length() == 0) {     //카카오로그인일때
            try {
                final String[] result3 = {""};
                String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/sticknum_select.php?user_name=" + Login_Page.nickName; //서버로 보낼 url
                result3[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
                curUserk = GetJSONObject.jsonParser_users(result3[0]); //json 파일 파싱하기
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(curUserk[6] != null)
                savedaddress = curUserk[6];
            savedad.setText(savedaddress);

        } else {
            final String[] result2 = {""};
            try {
                String urlsel = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + id; //서버로 보낼 url
                result2[0] = new GetJSONObject().execute(urlsel).get(); //서버에서 json 파일 읽어오기
                curUser = GetJSONObject.jsonParser_user(result2[0]); //json 파일 파싱하기
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(curUser[6] != null)
                savedaddress = curUser[6];
            savedad.setText(savedaddress);

        }
        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daum_result.setText("");
                Intent intent = new Intent(Address_Page.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "주소가 변경되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void init_webView() {

        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);
        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());
        daum_webView.getSettings().setSupportMultipleWindows(false);
        daum_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                daum_webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/address.php");
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        //<<webview에 팝업창 덮어씌우기
        daum_webView.dispatchWindowFocusChanged(true);
        daum_webView.dispatchSetSelected(true);
        daum_webView.setFocusable(true);
        daum_webView.setFocusableInTouchMode(false);
        daum_webView.setVerticalFadingEdgeEnabled(true);
        daum_webView.setVerticalScrollBarEnabled(true);
        daum_webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        daum_webView.getSettings().setJavaScriptEnabled(true);
        daum_webView.getSettings().setSupportMultipleWindows(true);
        daum_webView.getSettings().setGeolocationEnabled(true);
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        daum_webView.getSettings().setBuiltInZoomControls(true);
        daum_webView.setWebViewClient(new WebViewClient());
        daum_webView.setWebChromeClient(new WebChromeClient() {


            @Override

            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {

                view.removeAllViews();
                WebView childView = new WebView(view.getContext());

                childView.getSettings().setJavaScriptEnabled(true);
                childView.setWebChromeClient(this);
                childView.setWebViewClient(new WebViewClient());
                childView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

                view.addView(childView);

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(childView);
                resultMsg.sendToTarget();

                return true;
            }

        });
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {

            handler.post(new Runnable() {

                @Override

                public void run() {
                    daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    address = daum_result.getText().toString();

                    //Geo코딩 시작
                    List<Address> list = null;
                    final String[] result1 = {""};
                    try {
                        list = geocoder.getFromLocationName(
                                address, // 지역 이름
                                30); // 읽을 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.w("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                    }

                    if (list != null) {
                        if (list.size() == 0) {
                            Log.w("address No", "해당되는 주소 정보는 없습니다");
                        } else {
                            String latitude = Double.toString(list.get(0).getLatitude());
                            String longtitude = Double.toString(list.get(0).getLongitude());

                            try {
                                String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/add-la.php?user_id=" + id + "&user_latitude=" + latitude + "&user_longtitude=" + longtitude;
                                result1[0] = new GetJSONObject().execute(url).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (id.length() == 0) {         //카카오로그인
                        final String[] result = {""};
                        try {
                            String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/address_update_kakao.php?user_name=" + Login_Page.nickName + "&user_address=" + address;
                            result[0] = new GetJSONObject().execute(url).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {                        //일반로그인
                        final String[] result = {""};
                        try {
                            String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/address_update.php?user_id=" + id + "&user_address=" + address;
                            result[0] = new GetJSONObject().execute(url).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }

            });
        }
    }
}