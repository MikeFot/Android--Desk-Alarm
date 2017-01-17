package com.michaelfotiadis.deskalarm.ui.base.core;

/**
 *
 */
public interface AlarmManager {
    void setAlarm(ALARM_MODE mode);

    void cancelAlarm();

    void saveTimeToPreferencesAndStore(long timeStarted);

    public enum ALARM_MODE {
        NORMAL, SNOOZE, REPEAT, STOPPED, AUTO
    }
}
