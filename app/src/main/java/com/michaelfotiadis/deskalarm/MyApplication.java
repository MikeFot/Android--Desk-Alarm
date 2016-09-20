package com.michaelfotiadis.deskalarm;

import android.app.Application;

import com.michaelfotiadis.deskalarm.utils.Logger;

public class MyApplication extends Application {

    private final String TAG = "DeskAlarmApp";


    @Override
    public void onCreate() {
        Logger.d(TAG, "Starting Application");
        super.onCreate();
    }

}
