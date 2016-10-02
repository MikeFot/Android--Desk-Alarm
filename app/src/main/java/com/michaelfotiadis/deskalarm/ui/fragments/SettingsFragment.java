package com.michaelfotiadis.deskalarm.ui.fragments;

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
import com.michaelfotiadis.deskalarm.dialogs.ClearDataDialogFragment;
import com.michaelfotiadis.deskalarm.dialogs.ClearPreferencesDialogFragment;
import com.michaelfotiadis.deskalarm.dialogs.TimePickerDialogWrapper;
import com.michaelfotiadis.deskalarm.dialogs.TimeToSnoozeDialogWrapper;
import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.ui.base.dialog.BaseDialogFragment;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG_TTA_PICKER = "TimeToAlarmPickerDialog";
    private static final String TAG_TTS_PICKER = "TimeToSnoozePickerDialog";
    private static final String TAG_CLEAR_DATA_DIALOG = "ClearDataDialog";
    private static final String TAG_CLEAR_PREF_DIALOG = "ClearPreferencesDialog";
    private Preference mTimeToAlarmSetButton;
    private Preference mTimeToSnoozeSetButton;
    private Preference mClearDataButton;
    private Preference mClearPreferenceButton;


    public static PreferenceFragmentCompat newInstance() {
        return new SettingsFragment();
    }


    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        mTimeToAlarmSetButton = findPreference(getActivity().getString(R.string.pref_alarm_interval_key));
        mTimeToAlarmSetButton.setOnPreferenceClickListener(this);

        mTimeToSnoozeSetButton = findPreference(getActivity().getString(R.string.pref_snooze_interval_key));
        mTimeToSnoozeSetButton.setOnPreferenceClickListener(this);

        mClearDataButton = findPreference(getActivity().getString(R.string.pref_clear_data_key));
        mClearDataButton.setOnPreferenceClickListener(this);

        mClearPreferenceButton = findPreference(getActivity().getString(R.string.pref_clear_preferences_key));
        mClearPreferenceButton.setOnPreferenceClickListener(this);

        // remove accelerometer preferences if version less than KITKAT
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            final PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.pref_category_sensor_key));
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
        super.onResume();
        // register a shared preferences listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

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
        } else if (key.equals(getActivity().getString(R.string.pref_alarm_interval_key))) {
            preference.setSummary(
                    String.format("%s %s",
                            String.valueOf(getPreferenceHandler().getInt(PreferenceHandler.PreferenceKey.ALARM_INTERVAL)),
                            getString(R.string.text_minutes)));
        }
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearPreferencesDialog() {
        final BaseDialogFragment dialog = ClearPreferencesDialogFragment.newInstance();
        dialog.show(getActivity().getSupportFragmentManager(), TAG_CLEAR_PREF_DIALOG);
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearDataDialog() {
        final BaseDialogFragment generatedFragment = ClearDataDialogFragment.newInstance();
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
        final TimePickerDialogWrapper dialogWrapper = TimePickerDialogWrapper.newInstance(getActivity(), getPreferenceHandler());
        dialogWrapper.show();
    }

    private PreferenceHandler getPreferenceHandler() {
        if (getActivity() instanceof BaseActivity) {
            return ((BaseActivity) getActivity()).getPreferenceHandler();
        } else {
            return new PreferenceHandlerImpl(getContext());
        }
    }

}
