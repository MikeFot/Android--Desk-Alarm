package com.michaelfotiadis.deskalarm.ui.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.dialogs.ClearDataDialogFragment;
import com.michaelfotiadis.deskalarm.dialogs.ClearPreferencesDialogFragment;
import com.michaelfotiadis.deskalarm.dialogs.TimePickerDialogWrapper;
import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener,
        OnSharedPreferenceChangeListener {

    private static final String TAG_CLEAR_DATA_DIALOG = "ClearDataDialog";
    private static final String TAG_CLEAR_PREF_DIALOG = "ClearPreferencesDialog";
    private Preference mTimeToAlarmSetButton;
    private Preference mTimeToSnoozeSetButton;
    private Preference mClearDataButton;
    private Preference mClearPreferenceButton;

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {

        AppLog.d("onCreatePreferences " + rootKey);
        setPreferencesFromResource(R.xml.prefs, rootKey);

        setUpListeners();

        // remove accelerometer prefs if version less than KITKAT
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            final PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.pref_category_sensor_key));
            getPreferenceScreen().removePreference(category);
        }

    }

    private void setUpListeners() {
        mTimeToAlarmSetButton = findPreference(getString(R.string.pref_alarm_interval_key));
        if (mTimeToAlarmSetButton != null) {
            mTimeToAlarmSetButton.setOnPreferenceClickListener(this);
        }

        mTimeToSnoozeSetButton = findPreference(getString(R.string.pref_snooze_interval_key));
        if (mTimeToSnoozeSetButton != null) {
            mTimeToSnoozeSetButton.setOnPreferenceClickListener(this);
        }

        mClearDataButton = findPreference(getString(R.string.pref_clear_data_key));
        if (mClearDataButton != null) {
            mClearDataButton.setOnPreferenceClickListener(this);
        }

        mClearPreferenceButton = findPreference(getString(R.string.pref_clear_preferences_key));
        if (mClearPreferenceButton != null) {
            mClearPreferenceButton.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {

        AppLog.d("Clicked on preference: " + preference.getKey());

        if (mTimeToAlarmSetButton != null && preference.hashCode() == mTimeToAlarmSetButton.hashCode()) {
            showSetAlarmNumberPickerDialog();
            return true;
        } else if (mTimeToSnoozeSetButton != null && preference.hashCode() == mTimeToSnoozeSetButton.hashCode()) {
            showSetSnoozeNumberPickerDialog();
            return true;
        } else if (mClearDataButton != null && preference.hashCode() == mClearDataButton.hashCode()) {
            showClearDataDialog();
            return true;
        } else if (mClearPreferenceButton != null && preference.hashCode() == mClearPreferenceButton.hashCode()) {
            showClearPreferencesDialog();
            return true;
        }

        return false;

    }

    @Override
    public void onResume() {
        super.onResume();
        // register a shared prefs listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        refreshPreferencesPreview();

    }

    @Override
    public void onPause() {
        super.onPause();
        // remove the preference listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                          final String key) {

        final Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            final ListPreference listPreference = (ListPreference) preference;
            final int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (key.equals(getString(R.string.pref_alarm_interval_key))) {
            showMinutesPreview(preference, PreferenceHandler.PreferenceKey.ALARM_INTERVAL);
        } else if (key.equals(getString(R.string.pref_snooze_interval_key))) {
            showMinutesPreview(preference, PreferenceHandler.PreferenceKey.SNOOZE_INTERVAL);
        }

    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    private void refreshPreferencesPreview() {
        if (mTimeToAlarmSetButton != null) {
            showMinutesPreview(findPreference(getString(R.string.pref_alarm_interval_key)), PreferenceHandler.PreferenceKey.ALARM_INTERVAL);
        }
        if (mTimeToSnoozeSetButton != null) {
            showMinutesPreview(findPreference(getString(R.string.pref_snooze_interval_key)), PreferenceHandler.PreferenceKey.SNOOZE_INTERVAL);
        }
    }

    private void showMinutesPreview(final Preference preference,
                                    final PreferenceHandler.PreferenceKey key) {

        final int minutes = getPreferenceHandler().getInt(key);
        final String suffix;
        if (minutes == 1) {
            suffix = getString(R.string.text_minute);
        } else {
            suffix = getString(R.string.text_minutes);
        }
        preference.setSummary(
                String.format("%s %s",
                        String.valueOf(minutes),
                        suffix));
    }


    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearPreferencesDialog() {
        ClearPreferencesDialogFragment.newInstance().show(getActivity().getSupportFragmentManager(), TAG_CLEAR_PREF_DIALOG);
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearDataDialog() {
        ClearDataDialogFragment.newInstance().show(getActivity().getSupportFragmentManager(), TAG_CLEAR_DATA_DIALOG);
    }

    /**
     * Starts a number picker dialog for Snooze
     */
    private void showSetSnoozeNumberPickerDialog() {
        TimePickerDialogWrapper.newSnoozeInstance(
                getActivity(),
                getPreferenceHandler()).show();
    }

    /**
     * Starts a number picker dialog for Time to Alarm
     */
    private void showSetAlarmNumberPickerDialog() {
        TimePickerDialogWrapper.newAlarmInstance(
                getActivity(),
                getPreferenceHandler()).show();
    }

    private PreferenceHandler getPreferenceHandler() {
        if (getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getPreferenceHandler();
        } else {
            return new PreferenceHandlerImpl(getContext());
        }
    }

    public static PreferenceFragmentCompat newInstance() {
        return new SettingsFragment();
    }

}
