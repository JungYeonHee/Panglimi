package com.example.panglimi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Successfind_Page extends AppCompatActivity {

    Button okBtn;
    TextView etid, etpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successfind_page);

        etid = (TextView) findViewById(R.id.etid);
        etpw = (TextView) findViewById(R.id.etpw);

        etid.setText(Findaccount_Page.find_id);
        etpw.setText(Findaccount_Page.find_passwd);

        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Successfind_Page.this, Login_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}