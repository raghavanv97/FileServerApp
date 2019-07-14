package com.example.testingserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testingserver.Service.ServerService;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.example.testingserver.Service.ServerService.SERVERONOFF;

public class MainActivity extends AppCompatActivity {

    private TextView tv_diplay_msg;
    private Button btn_start_sharing;
    public static String localipAddress;


    String batteryPercentage = "";
    BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryPercentage = (String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        tv_diplay_msg = findViewById(R.id.tv_display_ip);
        btn_start_sharing = findViewById(R.id.btn_server);
        if (SERVERONOFF){
            btn_start_sharing.setText("STOP SHARING");
            try{
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                localipAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

                tv_diplay_msg.setText("Connected : " + "Please access! http://" + localipAddress + ":8080"  +" From a web browser");
            }catch (Exception e) {

                tv_diplay_msg.setText("Connection failed");
            }
        }else {
            btn_start_sharing.setText("START SHARING");
            tv_diplay_msg.setText("Click to Start Server");
        }

        btn_start_sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!SERVERONOFF) {
                    SERVERONOFF = true;
                    btn_start_sharing.setText("STOP SHARING");
                    try{
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                        localipAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

                        tv_diplay_msg.setText("Connected : " + "Please access! http://" + localipAddress + ":8080"  +" From a web browser");
                    }catch (Exception e) {

                        tv_diplay_msg.setText("Connection failed");
                    }
                    Intent intent = new Intent(MainActivity.this, ServerService.class);
                    startService(intent);
                }else {
                    SERVERONOFF = false;
                    btn_start_sharing.setText("START SHARING");
                    tv_diplay_msg.setText("Click to Start Server");
                    Intent intent = new Intent(MainActivity.this, ServerService.class);
                    stopService(intent);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
