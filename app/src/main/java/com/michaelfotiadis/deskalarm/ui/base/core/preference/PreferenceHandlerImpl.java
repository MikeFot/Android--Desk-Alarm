package com.michaelfotiadis.deskalarm.ui.base.core.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Android application methods
 *
 * @author Michael Fotiadis
 */
public class PreferenceHandlerImpl implements PreferenceHandler {

    private final Context mContext;
    /**
     * Map for storing String prefs and their default value
     */
    private final Map<PreferenceKey, Pair<String, String>> mStringMap;
    private final Map<PreferenceKey, Pair<String, Long>> mLongMap;
    private final Map<PreferenceKey, Pair<String, Integer>> mIntegerMap;
    private final Map<PreferenceKey, Pair<String, Boolean>> mBooleanMap;

    public PreferenceHandlerImpl(final Context context) {
        this.mContext = context;

        /**
         * Setup the string map
         */
        this.mStringMap = new HashMap<>();
        putToStringMap(
                PreferenceKey.RINGTONE,
                resolveString(R.string.pref_ringtones_key),
                resolveString(R.string.pref_ringtones_default));
        putToStringMap(
                PreferenceKey.SENSOR_MODE,
                resolveString(R.string.pref_sensor_modes_key),
                resolveString(R.string.pref_sensor_modes_default));
        putToStringMap(
                PreferenceKey.FONT_TYPE,
                resolveString(R.string.pref_font_key),
                resolveString(R.string.pref_font_default_value)
        );
        putToStringMap(
                PreferenceKey.FONT_COLOR,
                resolveString(R.string.pref_font_color_key),
                resolveString(R.string.pref_font_color_default_value)
        );
        putToStringMap(
                PreferenceKey.CLOCK_TYPE,
                resolveString(R.string.pref_clock_type_key),
                resolveString(R.string.pref_clock_type_default)
        );

        /**
         * Setup the long map
         */
        this.mLongMap = new HashMap<>();
        putToLongMap(
                PreferenceKey.TIME_STARTED,
                resolveString(R.string.pref_time_started),
                0L);

        /**
         * Setup the integer map
         */
        this.mIntegerMap = new HashMap<>();
        putToIntegerMap(
                PreferenceKey.ALARM_INTERVAL,
                resolveString(R.string.pref_alarm_interval_key),
                15);
        putToIntegerMap(
                PreferenceKey.SNOOZE_INTERVAL,
                resolveString(R.string.pref_snooze_interval_key),
                1);

        this.mBooleanMap = new HashMap<>();
        putToBooleanMap(
                PreferenceKey.AUTO_START,
                resolveString(R.string.pref_auto_start_key),
                false
        );

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
    public Long getLong(final PreferenceKey key) {

        if (mLongMap.containsKey(key)) {
            final Pair<String, Long> pair = mLongMap.get(key);
            return getAppSharedPreferences().getLong(pair.first, pair.second);
        } else {
            AppLog.w("Invalid long preference requested for key " + key.toString());
            return Long.MIN_VALUE;
        }
    }

    @Override
    public Integer getInt(final PreferenceKey key) {

        if (mIntegerMap.containsKey(key)) {
            final Pair<String, Integer> pair = mIntegerMap.get(key);
            return getAppSharedPreferences().getInt(pair.first, pair.second);
        } else {
            AppLog.w("Invalid int preference requested for key " + key.toString());
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public Boolean getBoolean(final PreferenceKey key) {

        if (mBooleanMap.containsKey(key)) {
            final Pair<String, Boolean> pair = mBooleanMap.get(key);
            return getAppSharedPreferences().getBoolean(pair.first, pair.second);
        } else {
            AppLog.w("Invalid boolean preference requested for key " + key.toString());
            return false;
        }
    }

    @Override
    public void writeInt(final PreferenceKey key, final Integer value) {
        if (mIntegerMap.containsKey(key)) {
            final Pair<String, Integer> pair = mIntegerMap.get(key);
            final SharedPreferences.Editor editor = getEditor();
            editor.putInt(pair.first, value);
            editor.apply();
        } else {
            AppLog.w("Attempted to write invalid long preference key " + key.toString());
        }
    }

    @Override
    public void writeLong(final PreferenceKey key, final Long value) {
        if (mLongMap.containsKey(key)) {
            final Pair<String, Long> pair = mLongMap.get(key);
            final SharedPreferences.Editor editor = getEditor();
            editor.putLong(pair.first, value);
            editor.apply();
        } else {
            AppLog.w("Attempted to write invalid long preference key " + key.toString());
        }
    }

    @Override
    public String getString(final PreferenceKey key) {

        if (mStringMap.containsKey(key)) {
            final Pair<String, String> pair = mStringMap.get(key);
            return getAppSharedPreferences().getString(pair.first, pair.second);
        } else {
            AppLog.w("Invalid string preference requested " + key.toString());
            return "";
        }
    }

    @Override
    public void writeString(final PreferenceKey key, final String value) {

        if (mStringMap.containsKey(key)) {
            final Pair<String, String> pair = mStringMap.get(key);
            final SharedPreferences.Editor editor = getEditor();
            editor.putString(pair.first, value);
            editor.apply();
        } else {
            AppLog.w("Attempted to write invalid string preference key " + key.toString());
        }
    }

    @Override
    public void clearPreferences() {
        final SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.apply();
    }

    /**
     * Returns the default shared prefs object for the given context
     *
     * @return {@link SharedPreferences}
     */
    private SharedPreferences getAppSharedPreferences() {
        PreferenceManager.setDefaultValues(mContext, R.xml.prefs, false);
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    private String resolveString(final int resId) {
        return mContext.getString(resId);
    }

    private void putToStringMap(final PreferenceKey key, final String stringKey, final String stringFallback) {
        mStringMap.put(key, new Pair<>(stringKey, stringFallback));
    }

    private void putToLongMap(final PreferenceKey key, final String stringKey, final long fallbackValue) {
        mLongMap.put(key, new Pair<>(stringKey, fallbackValue));
    }

    private void putToIntegerMap(final PreferenceKey key, final String stringKey, final int fallbackValue) {
        mIntegerMap.put(key, new Pair<>(stringKey, fallbackValue));
    }

    private void putToBooleanMap(final PreferenceKey key, final String stringKey, final boolean fallbackValue) {
        mBooleanMap.put(key, new Pair<>(stringKey, fallbackValue));
    }

    private SharedPreferences.Editor getEditor() {
        return getAppSharedPreferences().edit();
    }


}
