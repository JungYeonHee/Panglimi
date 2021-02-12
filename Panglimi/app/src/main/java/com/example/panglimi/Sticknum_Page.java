package com.example.panglimi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutionException;

public class Sticknum_Page extends AppCompatActivity {

    private EditText stick;
    private Button okBtn;
    public static String stickNum="";
    static String num="";
    String[] curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticknum_page);
        stick = (EditText) findViewById(R.id.stickNum);
        okBtn = (Button) findViewById(R.id.okBtn);

        final String id = Login_Page.input_id;

        //지팡이번호 디비에서 가져와서 보여주기
        curUser = new String[6];

        try {
            final String[] result = {""};
            String urlstr = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/user_select_final.php?user_id=" + id; //서버로 보낼 url
            result[0] = new GetJSONObject().execute(urlstr).get(); //서버에서 json 파일 읽어오기
            curUser = GetJSONObject.jsonParser_user(result[0]); //json 파일 파싱하기
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(curUser != null)
            stickNum = curUser[5];
        num = stick.getText().toString();
        stick.setText(stickNum);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 지팡이 번호
                stickNum = stick.getText().toString();
                MyService.ip = stickNum;
                MainActivity.userStickNum = stickNum;
                final String[] result2 = {""};
                if (stickNum.length() != 0) {     //stickNum에 값이 있다면
                    try {
                        String url = "http://ec2-54-180-105-138.ap-northeast-2.compute.amazonaws.com/stickNum.php?user_id=" + id + "&user_stickNum=" + stickNum;
                        result2[0] = new GetJSONObject().execute(url).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "등록 완료: " + stickNum, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Sticknum_Page.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {   //stickNum에 값이 없다면
                    Toast.makeText(getApplicationContext(), "지팡이 번호가 등록되지 않았습니다" + stickNum, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}