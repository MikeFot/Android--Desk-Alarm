package com.michaelfotiadis.deskalarm.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.fragments.ErgoSettingsFragment;
import com.michaelfotiadis.deskalarm.managers.ErgoAlarmManager;
import com.michaelfotiadis.deskalarm.managers.ErgoServiceManager;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

public class ErgoPreferencesActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    private final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // switch activity theme according to settings
        new AppUtils().changeAppTheme(this);

        super.onCreate(savedInstanceState);

        // Set the action bar up
        getActionBar().setDisplayHomeAsUpEnabled(true);


        Logger.d(TAG, "Starting Settings Fragment");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ErgoSettingsFragment())
                .commit();
    }


    @Override
    protected void onResume() {
        // register a shared preferences listener
        new AppUtils().getAppSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        // remove the preference listener
        new AppUtils().getAppSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        // dismiss the dialog
        if (mSensorDialog != null) {
            mSensorDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(getString(R.string.pref_sensor_modes_key))) {
            final String mode = new AppUtils().getAppSharedPreferences(this)
                    .getString(this.getString(R.string.pref_sensor_modes_key),
                            this.getString(R.string.pref_sensor_modes_default));
            // enable accelerometer if KITKAT or higher
            if (!mode.equals(getString(R.string.pref_sensor_modes_default))
                    && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                checkForAccelerometer();
            }
            // stop the service on sensor change
            new ErgoServiceManager().stopStepService(getApplicationContext());
            new ErgoAlarmManager().cancelAlarm(getApplicationContext());
        } else if (key.equals(getString(R.string.pref_ringtones_key))) {
            new ErgoServiceManager().startAudioService(getApplicationContext());
        }
    }

    /**
     * Checks for the presence of a step detector using 3 booleans
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void checkForAccelerometer() {
        PackageManager PM = this.getPackageManager();
        boolean hasStepDetector = PM
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        boolean hasStepCounter = PM
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        boolean hasAccelerometer = PM
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        // if any of the detectors is absent, open a dialog that will terminate
        // the application
        if (!hasStepDetector || !hasStepCounter || !hasAccelerometer) {
            // ensure that the edit will persist by creating an Editor instance
            final SharedPreferences.Editor editor = new AppUtils()
                    .getAppSharedPreferences(ErgoPreferencesActivity.this)
                    .edit();
            editor.putString(ErgoPreferencesActivity.this
                            .getString(R.string.pref_sensor_modes_key),
                    ErgoPreferencesActivity.this
                            .getString(R.string.pref_sensor_modes_default));
            editor.commit();
            makeSensorAlertDialog();
        }
    }

    private AlertDialog mSensorDialog;

    private void makeSensorAlertDialog() {
        Logger.d(TAG, "Showing Sensor Alert Dialog");
        // Show a warning dialog and exit the application
        mSensorDialog = new AlertDialog.Builder(this)
                .setMessage(this.getString(R.string.dialog_msg_sensor_failed))
                .setTitle(this.getString(R.string.dialog_title_warning))
                .setCancelable(false)
                .setPositiveButton(this.getString(R.string.dialog_user_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                ErgoPreferencesActivity.this.recreate();
                            }
                        }).create();
        mSensorDialog.show();
    }

}
