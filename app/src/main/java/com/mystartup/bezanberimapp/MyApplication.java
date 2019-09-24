package com.mystartup.bezanberimapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.parse.Parse;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "musicPlayerChannel";


    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("RQyHUnZ7XIUDLVfp2RKVfhWfZf9YHTBWQ0epZxw3")
                .clientKey("0Z4OGDA78vel7CV0Nd6NHRNISk3ySx6vt19w5quU")
                .server("https://parseapi.back4app.com")
                .build()

        );
        createNotificationChannel();

    }


    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,"Dwipp media player",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
