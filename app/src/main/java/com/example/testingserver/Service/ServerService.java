package com.example.testingserver.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.testingserver.MainActivity;
import com.example.testingserver.R;
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

import static com.example.testingserver.MainActivity.localipAddress;
import static com.example.testingserver.Notification.NotificationChannelForService.CHANNEL_ID;

public class ServerService extends Service {

    public static boolean SERVERONOFF = false;
    StringBuilder pathOfallDir = new StringBuilder();

    AsyncHttpServer asyncHttpServer = new AsyncHttpServer();
    AsyncServer asyncServer = new AsyncServer();


    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sharing with Desktop")
                .setContentText("Visit http://" + localipAddress + " in your Browser")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(8456, notification);
        startserver();
    }

    private void startserver(){
        asyncHttpServer.get("/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
//                response.send("This is first page to display " + batteryPercentage);
                File file = null;
                try {
                    file = new File(Environment.getExternalStorageDirectory().getPath() + URLDecoder.decode(request.getPath(), StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                String extension = request.getPath().substring(request.getPath().lastIndexOf(".")!=-1?request.getPath().lastIndexOf(".")+1:0).toLowerCase();
                if (file.isFile()){
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        response.sendStream(fis, fis.available());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }


                StringBuilder stringBuilder = new StringBuilder("");
                InputStream inputStream;
                String line = "";
                try{
                    inputStream = getResources().getAssets().open("index.html");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line=bufferedReader.readLine()) != null){
                        if (line.contains("[All_Files]")){
                            for (File f : file.listFiles()){
                                stringBuilder.append("<a style='display:block' href='").append((request.getPath().equals("/")?"":request.getPath()) +"/"+ f.getName()).append("'a>").append(f.getName()).append("</a> <br/>");
                            }
                            continue;
                        }
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                stringBuilder.append("<html><body>");
//                for (File f : file.listFiles()) {
//
//                        stringBuilder.append("<a href='").append((request.getPath().equals("/")?"":request.getPath()) +"/"+ f.getName()).append("'a>").append(f.getName()).append("</a> <br/>");
//
//                    // do whatever you want with filename
//                }
////                response.send(stringBuilder.toString());
//                stringBuilder.append("</body></html>");
//                response.send(stringBuilder.toString());
                response.send("text/html", stringBuilder.toString());


            }
        });
        asyncHttpServer.listen(asyncServer, 8080);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncHttpServer.stop();
        asyncServer.stop();
        Log.e("Vijay", "onDestroy: Closed connection");
    }
}
