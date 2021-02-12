package com.example.panglimi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.RequestQueue;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainActivity extends AppCompatActivity {
    SpaceNavigationView navigationView;
    Edit_Page editPage;
    Address_Page addressPage;
    Setting_Page setting_page;
    Home_Page homePage;
    private RequestQueue mQueue;
    private static final String CHANNEL_ID = "channel_id" ;
    static final String TAG = MainActivity.class.getSimpleName();
    static String userStickNum = "";
    static String alarmState = "on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main);

           editPage = new Edit_Page();
           addressPage = new Address_Page();
           setting_page = new Setting_Page();
           homePage = new Home_Page();

           navigationView = findViewById(R.id.space);
           navigationView.initWithSaveInstanceState(savedInstanceState);
           navigationView.addSpaceItem(new SpaceItem("주의", R.drawable.caution_off));
           navigationView.addSpaceItem(new SpaceItem("설정", R.drawable.setting_off));

           Intent intent = new Intent(MainActivity.this,MyService.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           startService(intent);


           navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
               @Override
               public void onCentreButtonClick() {
                   navigationView.setCentreButtonSelectable(true);
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_Page()).commit();
               }

               @Override
               public void onItemClick(int itemIndex, String itemName) {
                   Fragment selectedFragment = null;
                   switch (itemIndex) {
                       case 0:
                           selectedFragment = new Caution_Page();
                           break;
                       case 1:
                           selectedFragment = new Setting_Page();
                           break;
                   }
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
               }

               @Override
               public void onItemReselected(int itemIndex, String itemName) {
               }
           });

           navigationView.changeCurrentItem(-1);
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_Page()).commit();
       }

    public void onFragmentChanged(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (index) {
            case 0:
                transaction.replace(R.id.fragment_container, editPage);
                break;
            case 1:
                transaction.replace(R.id.fragment_container, setting_page);
                break;
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
