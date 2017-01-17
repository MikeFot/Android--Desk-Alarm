package com.michaelfotiadis.deskalarm.ui.base.core;

import android.annotation.SuppressLint;

/**
 *
 */
public interface NotificationManager {
    void cancelAlarmNotification();

    @SuppressLint("InlinedApi")
    void fireAlarmNotification();
}
