package com.michaelfotiadis.deskalarm.ui.base.core.preference;

import android.content.SharedPreferences;

/**
 *
 */
public interface PreferenceHandler {
    void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    Long getLongPreference(PreferenceKey key);

    void writeLongPreference(PreferenceKey key, Long value);

    String getStringPreference(PreferenceKey key);

    void writeStringPreference(PreferenceKey key, String value);

    void clearPreferences();

    enum PreferenceKey {
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
