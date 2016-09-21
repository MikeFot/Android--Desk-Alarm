package com.michaelfotiadis.deskalarm.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.common.base.dialog.BaseDialogFragment;
import com.michaelfotiadis.deskalarm.dialogs.DialogClearDataFragment;
import com.michaelfotiadis.deskalarm.dialogs.DialogClearPreferencesFragment;
import com.michaelfotiadis.deskalarm.dialogs.TimePickerDialogWrapper;
import com.michaelfotiadis.deskalarm.dialogs.TimeToSnoozeDialogWrapper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG_TTA_PICKER = "TimeToAlarmPickerDialog";
    private static final String TAG_TTS_PICKER = "TimeToSnoozePickerDialog";
    private static final String TAG_CLEAR_DATA_DIALOG = "ClearDataDialog";
    private static final String TAG_CLEAR_PREF_DIALOG = "ClearPreferencesDialog";
    private Preference mTimeToAlarmSetButton;
    private Preference mTimeToSnoozeSetButton;
    private Preference mClearDataButton;
    private Preference mClearPreferenceButton;


    public static PreferenceFragmentCompat newInstance() {
        return new ErgoSettingsFragment();
    }


    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        mTimeToAlarmSetButton = findPreference(getActivity().getString(R.string.pref_alarm_interval_key));
        mTimeToAlarmSetButton.setOnPreferenceClickListener(this);

        mTimeToSnoozeSetButton = findPreference(getActivity()
                .getString(R.string.pref_snooze_interval_key));
        mTimeToSnoozeSetButton.setOnPreferenceClickListener(this);

        mClearDataButton = findPreference(getActivity().getString(
                R.string.pref_clear_data_key));
        mClearDataButton.setOnPreferenceClickListener(this);

        mClearPreferenceButton = findPreference(getActivity().getString(
                R.string.pref_clear_preferences_key));
        mClearPreferenceButton.setOnPreferenceClickListener(this);

        // remove accelerometer preferences if version less than KITKAT
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            final PreferenceCategory category = (PreferenceCategory) findPreference(
                    getString(R.string.pref_category_sensor_key));
            getPreferenceScreen().removePreference(category);
        }
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {

        if (preference.hashCode() == mTimeToAlarmSetButton.hashCode()) {
            showSetAlarmNumberPickerDialog();
            return true;
        } else if (preference.hashCode() == mTimeToSnoozeSetButton.hashCode()) {
            showSetSnoozeNumberPickerDialog();
            return true;
        } else if (preference.hashCode() == mClearDataButton.hashCode()) {
            showClearDataDialog();
            return true;
        } else if (preference.hashCode() == mClearPreferenceButton.hashCode()) {
            showClearPreferencesDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        // register a shared preferences listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        // remove the preference listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
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
        if (key.equals(getString(R.string.pref_theme_key))) {
            setPreferenceSummary(key);
        }
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearPreferencesDialog() {
        final BaseDialogFragment dialog = DialogClearPreferencesFragment.newInstance();
        dialog.show(getActivity().getSupportFragmentManager(), TAG_CLEAR_PREF_DIALOG);
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearDataDialog() {
        final BaseDialogFragment generatedFragment = DialogClearDataFragment.newInstance();
        generatedFragment.show(getActivity().getSupportFragmentManager(), TAG_CLEAR_DATA_DIALOG);
    }

    /**
     * Starts a number picker dialog for Snooze
     */
    private void showSetSnoozeNumberPickerDialog() {
        final TimeToSnoozeDialogWrapper dialogWrapper = TimeToSnoozeDialogWrapper.newInstance(getActivity(), getPreferenceScreen().getSharedPreferences());
        dialogWrapper.show();
    }

    /**
     * Starts a number picker dialog for Time to Alarm
     */
    private void showSetAlarmNumberPickerDialog() {
        final TimePickerDialogWrapper dialogWrapper = TimePickerDialogWrapper.newInstance(getActivity(), getPreferenceScreen().getSharedPreferences());
        dialogWrapper.show();
    }

    private void setPreferenceSummary(final String key) {
        AppLog.i(key);
        final Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            final ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }
}
