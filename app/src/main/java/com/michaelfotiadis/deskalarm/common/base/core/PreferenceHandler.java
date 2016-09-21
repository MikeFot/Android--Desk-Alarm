package com.michaelfotiadis.deskalarm.common.base.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.model.PreferenceKeys;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.io.IOException;

/**
 * Utility class for Android application methods
 *
 * @author Michael Fotiadis
 */
public class PreferenceHandler {

    private final Context mContext;

    public PreferenceHandler(final Context context) {
        this.mContext = context;
    }

    /**
     * Changes the theme of a given context
     */
    public void changeAppTheme() {
        final String defaultThemeValue = mContext.getString(R.string.pref_theme_default);

        // get the stored value
        final String themeKey = getAppSharedPreferences().getString(mContext.getString(R.string.pref_theme_key), defaultThemeValue);

        if (themeKey.equals(defaultThemeValue)) {
            mContext.setTheme(R.style.AppTheme);
        } else {
            mContext.setTheme(R.style.AppLightTheme);
        }
        AppLog.d("Theme changed to " + themeKey);
    }

    /**
     * Returns the default shared preferences object for the given context
     *
     * @return {@link SharedPreferences}
     */
    public SharedPreferences getAppSharedPreferences() {
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * @return Long value of service time started from Shared Preferences
     */
    public long getTimeStartedFromPreferences() {
        return getAppSharedPreferences().getLong(PreferenceKeys.KEY_1.getString(), 0);
    }


    /**
     * List files stored in the "Assets" folder
     *
     * @param path
     * @return
     */
    public boolean listAssetFiles(final String path) {
        final String[] list;
        try {
            list = mContext.getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (final String file : list) {
                    if (!listAssetFiles(path + "/" + file)) {
                        return false;
                    }
                }
            }
        } catch (final IOException e) {
            return false;
        }
        return true;
    }


}
