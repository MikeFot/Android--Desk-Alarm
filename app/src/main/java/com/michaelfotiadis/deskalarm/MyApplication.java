package com.michaelfotiadis.deskalarm;

import android.app.Application;

import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        AppLog.d("Starting Application");
        super.onCreate();
    }

}
