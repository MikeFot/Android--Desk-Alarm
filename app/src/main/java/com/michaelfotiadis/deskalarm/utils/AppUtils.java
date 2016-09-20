package com.michaelfotiadis.deskalarm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.constants.AppConstants.PreferenceKeys;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Utility class for Android application methods
 *
 * @author Michael Fotiadis
 */
public class AppUtils {

    private final String TAG = "AppUtils";

    /**
     * Changes the theme of a given context
     *
     * @param context
     */
    public void changeAppTheme(final Context context) {
        final String defaultThemeValue = context.getString(R.string.pref_theme_default);

        // get the stored value
        final String themeKey = getAppSharedPreferences(context).
                getString(context.getString(R.string.pref_theme_key), defaultThemeValue);

        if (themeKey.equals(defaultThemeValue)) {
            context.setTheme(R.style.AppTheme);
        } else {
            context.setTheme(R.style.AppLightTheme);
        }
        Logger.d(TAG, "Theme changed to " + themeKey);
    }

    /**
     * Returns the default shared preferences object for the given context
     *
     * @param context
     * @return
     */
    public SharedPreferences getAppSharedPreferences(final Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Long value of service time started from Shared Preferences
     */
    public long getTimeStartedFromPreferences(final Context context) {
        return new AppUtils().getAppSharedPreferences(context).getLong(PreferenceKeys.KEY_1.getString(), 0);
    }


    /**
     * List files stored in the "Assets" folder
     *
     * @param context
     * @param path
     * @return
     */
    public boolean listAssetFiles(final Context context, final String path) {
        Logger.i("Assets", "LISTING ASSETS");
        String[] list;
        try {
            list = context.getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(context, path + "/" + file)) {
                        Logger.i("Assets", file);
                        return false;
                    } else {
                        Logger.i("Assets", file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String getSystemTimeFormat(final Context context, final Calendar calendar) {
        // Gets system TF
        DateFormat tf = android.text.format.DateFormat.getTimeFormat(context.getApplicationContext());
        return tf.format(calendar.getTime());
    }
}
