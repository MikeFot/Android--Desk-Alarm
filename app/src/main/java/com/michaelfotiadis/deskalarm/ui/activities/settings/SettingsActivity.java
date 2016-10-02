package com.michaelfotiadis.deskalarm.ui.activities.settings;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.fragments.SettingsFragment;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class SettingsActivity extends BaseActivity implements OnSharedPreferenceChangeListener {

    private static final String FRAGMENT_TAG = SettingsActivity.class.getSimpleName() + ".fragment_tag";
    private AlertDialog mSensorDialog;

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                          final String key) {
        if (key.equals(getString(R.string.pref_sensor_modes_key))) {
            final String mode = getPreferenceHandler().getStringPreference(PreferenceHandler.PreferenceKey.SENSOR_MODE);
            // enable accelerometer if KITKAT or higher
            if (!mode.equals(getString(R.string.pref_sensor_modes_default))
                    && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                checkForAccelerometer();
            }
            // stop the service on sensor change
            getServiceManager().stopStepService();
            getAlarmManager().cancelAlarm();
        } else if (key.equals(getString(R.string.pref_ringtones_key))) {
            getServiceManager().startAudioService();
        }
    }

    /**
     * Checks for the presence of a step detector using 3 booleans
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void checkForAccelerometer() {
        final PackageManager PM = this.getPackageManager();
        final boolean hasStepDetector = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        final boolean hasStepCounter = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        final boolean hasAccelerometer = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        // if any of the detectors is absent, open a dialog that will terminate
        // the application
        if (!hasStepDetector || !hasStepCounter || !hasAccelerometer) {
            // ensure that the edit will persist by creating an Editor instance
            getPreferenceHandler().writeStringPreference(PreferenceHandler.PreferenceKey.SENSOR_MODE, getString(R.string.pref_sensor_modes_default));
            makeSensorAlertDialog();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the action bar up
        setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
        AppLog.d("Starting Settings Fragment");

        addContentFragmentIfMissing(SettingsFragment.newInstance(), R.id.content_frame, FRAGMENT_TAG);
    }

    @Override
    protected void onPause() {
        // remove the preference listener
        getPreferenceHandler().unregisterOnSharedPreferenceChangeListener(this);
        // dismiss the dialog
        if (mSensorDialog != null) {
            mSensorDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // register a shared preferences listener
        getPreferenceHandler().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_default_fragment_container;
    }

    private void makeSensorAlertDialog() {
        AppLog.d("Showing Sensor Alert Dialog");
        // Show a warning dialog and exit the application

        mSensorDialog = getAlertDialogFactory().create(
                R.string.dialog_msg_sensor_failed,
                R.string.dialog_title_warning,
                R.string.dialog_user_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        SettingsActivity.this.recreate();
                    }
                }
        );
        mSensorDialog.setCancelable(false);
        mSensorDialog.show();
    }

}
