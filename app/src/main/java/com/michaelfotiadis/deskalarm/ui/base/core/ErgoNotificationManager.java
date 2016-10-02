package com.michaelfotiadis.deskalarm.ui.base.core;

import android.annotation.SuppressLint;

/**
 *
 */
public interface ErgoNotificationManager {
    void cancelAlarmNotification();

    @SuppressLint("InlinedApi")
    void fireAlarmNotification();
}
