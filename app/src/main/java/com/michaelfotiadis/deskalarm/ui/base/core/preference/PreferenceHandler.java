package com.michaelfotiadis.deskalarm.ui.base.core.preference;

import android.content.SharedPreferences;

/**
 *
 */
public interface PreferenceHandler {
    void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    Long getLong(PreferenceKey key);

    Integer getInt(PreferenceKey key);

    void writeInt(PreferenceKey key, Integer value);

    void writeLong(PreferenceKey key, Long value);

    String getString(PreferenceKey key);

    void writeString(PreferenceKey key, String value);

    void clearPreferences();

    public enum PreferenceKey {
        RINGTONE,
        SENSOR_MODE,
        TIME_STARTED,
        ALARM_INTERVAL,
        SNOOZE_INTERVAL,
        FONT_TYPE,
        FONT_COLOR,
        CLOCK_TYPE
    }
}
