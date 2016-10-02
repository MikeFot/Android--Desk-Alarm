package com.michaelfotiadis.deskalarm.ui.base.core.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.HashMap;

/**
 * Utility class for Android application methods
 *
 * @author Michael Fotiadis
 */
public class PreferenceHandlerImpl implements PreferenceHandler {

    private final Context mContext;
    /**
     * Map for storing String preferences and their default value
     */
    private final HashMap<PreferenceKey, Pair<String, String>> mStringMap;
    private final HashMap<PreferenceKey, Pair<String, Long>> mLongMap;

    public PreferenceHandlerImpl(final Context context) {
        this.mContext = context;

        /**
         * Setup the string map
         */
        this.mStringMap = new HashMap<>();
        putToStringMap(
                PreferenceKey.RINGTONE,
                getString(R.string.pref_ringtones_key),
                getString(R.string.pref_ringtones_default));
        putToStringMap(
                PreferenceKey.SENSOR_MODE,
                getString(R.string.pref_sensor_modes_key),
                getString(R.string.pref_sensor_modes_default));
        putToStringMap(
                PreferenceKey.FONT_TYPE,
                getString(R.string.pref_font_key),
                getString(R.string.pref_font_default_value)
        );
        putToStringMap(
                PreferenceKey.FONT_COLOR,
                getString(R.string.pref_font_color_key),
                getString(R.string.pref_font_color_default_value)
        );
        putToStringMap(
                PreferenceKey.CLOCK_TYPE,
                getString(R.string.pref_clock_type_key),
                getString(R.string.pref_clock_type_default)
        );

        /**
         * Setup the long map
         */
        this.mLongMap = new HashMap<>();
        putToLongMap(
                PreferenceKey.TIME_STARTED,
                getString(R.string.pref_time_started),
                0L);
        putToLongMap(
                PreferenceKey.ALARM_INTERVAL,
                getString(R.string.pref_alarm_interval_key),
                1L);
        putToLongMap(
                PreferenceKey.SNOOZE_INTERVAL,
                getString(R.string.pref_snooze_interval_key),
                1L);

    }

    @Override
    public void registerOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getAppSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getAppSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public Long getLongPreference(final PreferenceKey key) {

        if (mStringMap.containsKey(key)) {
            final Pair<String, Long> pair = mLongMap.get(key);
            return getAppSharedPreferences().getLong(pair.first, pair.second);
        } else {
            AppLog.w("Invalid preference requested");
            return Long.MIN_VALUE;
        }
    }

    @Override
    public void writeLongPreference(final PreferenceKey key, final Long value) {
        if (mLongMap.containsKey(key)) {
            final Pair<String, Long> pair = mLongMap.get(key);
            final SharedPreferences.Editor editor = getEditor();
            editor.putLong(pair.first, value);
            editor.apply();
        } else {
            AppLog.w("Attempted to write invalid preference key");
        }
    }

    @Override
    public String getStringPreference(final PreferenceKey key) {

        if (mStringMap.containsKey(key)) {
            final Pair<String, String> pair = mStringMap.get(key);
            return getAppSharedPreferences().getString(pair.first, pair.second);
        } else {
            AppLog.w("Invalid preference requested");
            return "";
        }
    }

    @Override
    public void writeStringPreference(final PreferenceKey key, final String value) {

        if (mStringMap.containsKey(key)) {
            final Pair<String, String> pair = mStringMap.get(key);
            final SharedPreferences.Editor editor = getEditor();
            editor.putString(pair.first, value);
            editor.apply();
        } else {
            AppLog.w("Attempted to write invalid preference key");
        }
    }

    /**
     * Returns the default shared preferences object for the given context
     *
     * @return {@link SharedPreferences}
     */
    private SharedPreferences getAppSharedPreferences() {
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void clearPreferences() {
        final SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.apply();
    }

    private String getString(final int resId) {
        return mContext.getString(resId);
    }

    private void putToStringMap(final PreferenceKey key, final String stringKey, final String stringFallback) {
        mStringMap.put(key, new Pair<>(stringKey, stringFallback));
    }

    private void putToLongMap(final PreferenceKey key, final String stringKey, final long fallbackValue) {
        mLongMap.put(key, new Pair<>(stringKey, fallbackValue));
    }

    private SharedPreferences.Editor getEditor() {
        return getAppSharedPreferences().edit();
    }


}
