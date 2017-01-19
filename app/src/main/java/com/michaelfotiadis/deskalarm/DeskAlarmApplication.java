package com.michaelfotiadis.deskalarm;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import io.fabric.sdk.android.Fabric;

public class DeskAlarmApplication extends Application {
    @Override
    public void onCreate() {
        AppLog.d("Starting Application");
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

}
