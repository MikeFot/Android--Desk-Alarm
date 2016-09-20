package com.michaelfotiadis.deskalarm.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.dialogs.DialogClearDataFragment;
import com.michaelfotiadis.deskalarm.dialogs.DialogClearPreferencesFragment;
import com.michaelfotiadis.deskalarm.dialogs.DialogTimeToAlarmPickerFragment;
import com.michaelfotiadis.deskalarm.dialogs.DialogTimeToSnoozePickerFragment;
import com.michaelfotiadis.deskalarm.utils.Logger;

public class ErgoSettingsFragment extends PreferenceFragment implements OnPreferenceClickListener, OnSharedPreferenceChangeListener {

    private final String TAG = "Settings Fragment";

    private Preference mTimeToAlarmSetButton;
    private Preference mTimeToSnoozeSetButton;
    private Preference mClearDataButton;
    private Preference mClearPreferenceButton;

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the action bar
        if (preference instanceof PreferenceScreen) {
            initializeActionBar((PreferenceScreen) preference);
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mTimeToAlarmSetButton = (Preference) findPreference(getActivity()
                .getString(R.string.pref_alarm_interval_key));
        mTimeToAlarmSetButton.setOnPreferenceClickListener(this);

        mTimeToSnoozeSetButton = (Preference) findPreference(getActivity()
                .getString(R.string.pref_snooze_interval_key));
        mTimeToSnoozeSetButton.setOnPreferenceClickListener(this);

        mClearDataButton = (Preference) findPreference(getActivity().getString(
                R.string.pref_clear_data_key));
        mClearDataButton.setOnPreferenceClickListener(this);

        mClearPreferenceButton = (Preference) findPreference(getActivity().getString(
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

    private final String TAG_TTA_PICKER = "TimeToAlarmPickerDialog";
    private final String TAG_TTS_PICKER = "TimeToSnoozePickerDialog";
    private final String TAG_CLEAR_DATA_DIALOG = "ClearDataDialog";
    private final String TAG_CLEAR_PREF_DIALOG = "ClearPreferencesDialog";

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearPreferencesDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_CLEAR_PREF_DIALOG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog
        DialogClearPreferencesFragment generatedFragment = new DialogClearPreferencesFragment();
        generatedFragment.show(fragmentTransaction, TAG_CLEAR_PREF_DIALOG);
    }

    /**
     * Starts a confirmation dialog for clearing user data
     */
    private void showClearDataDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_CLEAR_DATA_DIALOG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog
        DialogClearDataFragment generatedFragment = new DialogClearDataFragment();
        generatedFragment.show(fragmentTransaction, TAG_CLEAR_DATA_DIALOG);
    }

    /**
     * Starts a number picker dialog for Snooze
     */
    private void showSetSnoozeNumberPickerDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_TTS_PICKER);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog
        DialogTimeToSnoozePickerFragment generatedFragment = new DialogTimeToSnoozePickerFragment();
        generatedFragment.show(fragmentTransaction, TAG_TTS_PICKER);
    }

    /**
     * Starts a number picker dialog for Time to Alarm
     */
    private void showSetAlarmNumberPickerDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(TAG_TTA_PICKER);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog
        DialogTimeToAlarmPickerFragment generatedFragment = new DialogTimeToAlarmPickerFragment();

        generatedFragment.show(fragmentTransaction, TAG_TTA_PICKER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //	/** Sets up the action bar for an {@link PreferenceScreen} */
    public void initializeActionBar(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        if (dialog != null) {
            // Inialize the action bar
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

            // Apply custom home button area click listener to close the
            // PreferenceScreen because PreferenceScreens are dialogs which
            // swallow
            // events instead of passing to the activity
            // Related Issue:
            // https://code.google.com/p/android/issues/detail?id=4611
            View homeBtn = dialog.findViewById(android.R.id.home);

            if (homeBtn != null) {
                OnClickListener dismissDialogClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };

                ViewParent homeBtnContainer = homeBtn.getParent();

                // The home button is an ImageView inside a FrameLayout
                if (homeBtnContainer instanceof FrameLayout) {
                    ViewGroup containerParent = (ViewGroup) homeBtnContainer
                            .getParent();

                    if (containerParent instanceof LinearLayout) {
                        // this view also contains the title text, set the whole
                        // view as clickable
                        ((LinearLayout) containerParent)
                                .setOnClickListener(dismissDialogClickListener);
                    } else {
                        // set it on the home button
                        ((FrameLayout) homeBtnContainer)
                                .setOnClickListener(dismissDialogClickListener);
                    }
                } else {
                    // The 'If all else fails' default case
                    homeBtn.setOnClickListener(dismissDialogClickListener);
                }
            }
        }
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(getString(R.string.pref_theme_key))) {
            setPreferenceSummary(key);
        }
    }

    private void setPreferenceSummary(String key) {
        Logger.i(TAG, key);
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }
}
