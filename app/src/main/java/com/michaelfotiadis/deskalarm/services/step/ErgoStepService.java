package com.michaelfotiadis.deskalarm.services.step;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoAlarmManager.ALARM_MODE;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;
import java.util.Locale;


public class ErgoStepService extends IntentService implements SensorEventListener {

    private static final String TAG = ErgoStepService.class.getSimpleName();

    private final Core mCore;

    private static boolean sIsServiceRunning = false;
    // Thread fields
    private final long THREAD_WAIT_TIME_SHORT = 1000;
    private final long THREAD_WAIT_TIME_LONG = 5000;


    private final int maxTimesIdle = 15;
    private final int MINIMUM_TIME_BETWEEN_STEPS = 10000;


    private FLAGS mFlag;
    // Sensor fields
    private SensorManager mSensorManager;

    private Sensor mStepCounterSensor;

    private Sensor mStepDetectorSensor;

    // Receiver fields
    private BroadcastReceiver mReceiver;
    // Wake Lock fields
    private WakeLock mWakeLock;
    // Variables
    private int stepCount = 0;
    private int timesIdle = 0;
    private long mStepValue;
    private long mTimeOfLastStep = 0;
    private long mTimeStepInterval = Long.MAX_VALUE;

    /**
     * Main constructor
     */
    public ErgoStepService() {
        super("StepService");
        mCore = new CoreProvider(this);
    }

    public static boolean isServiceRunning() {
        return sIsServiceRunning;
    }

    public static void setServiceRunning(final boolean isServiceRunning) {
        ErgoStepService.sIsServiceRunning = isServiceRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setServiceRunning(true);
        mCore.getAlarmManager().setAlarm(ALARM_MODE.NORMAL);
    }

    @Override
    public void onDestroy() {
        AppLog.d("onDestroy");
        setServiceRunning(false);

        mCore.getAlarmManager().cancelAlarm();

        // Release the wake lock
        releaseWakeLock();
        unregisterSensorListeners();

        // broadcast that the service has stopped
        broadcastNotificationInt(Broadcasts.STEP_SERVICE_STOPPED.getString(), -1);

        unregisterScreenReceiver();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        registerScreenReceiver();

        // check version and process flags
        assignServiceFlags();

        final int version = android.os.Build.VERSION.SDK_INT;

        if (mFlag != FLAGS.MANUAL && version >= Build.VERSION_CODES.KITKAT) {
            AppLog.d("Starting Service in Automatic Mode");
            processAutomaticMode();
        } else {
            AppLog.d("Starting Service in Manual Mode");
            processManualMode();
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        final Sensor sensor = event.sensor;
        final float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER && isServiceRunning()) {
            // calculate how long has passed since the last step
            mTimeStepInterval = Calendar.getInstance().getTimeInMillis() - mTimeOfLastStep;

            if (value > mStepValue) {
                // compare time passed since last step with minimum time between steps
                if (mTimeStepInterval < MINIMUM_TIME_BETWEEN_STEPS) {
                    stepCount++;
                    AppLog.d("TYPE_STEP_COUNTER " + value + " at an interval of " + mTimeStepInterval);
                    mCore.getAlarmManager().setAlarm(ALARM_MODE.AUTO);
                    mStepValue = value;
                } else {
                    AppLog.i("Steps Not Far Apart Enough");
                }
            } else {
                AppLog.i("Repeating Step Event");
            }
            mTimeOfLastStep = Calendar.getInstance().getTimeInMillis();
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // Same Sensor but always transmits -1
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // do nothing
    }

    /**
     * Get a partial wake lock for the service
     */
    private void acquireWakeLock() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        AppLog.d("Wake Lock with Flags : " + "Partial Wake Lock");
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
    }

    /**
     * Reads shared preferences and assigns the Service Flag
     */
    private void assignServiceFlags() {
        final String mode = mCore.getPreferenceHandler().getAppSharedPreferences().getString(
                this.getString(R.string.pref_sensor_modes_key),
                this.getString(R.string.pref_sensor_modes_default));

        final String[] data = this.getResources().getStringArray(R.array.array_sensor_modes);

        if (mode.equals(data[0])) {
            mFlag = FLAGS.MANUAL;
        } else if (mode.equals(data[1])) {
            mFlag = FLAGS.LOW_ACCURACY;
        } else if (mode.equals(data[2])) {
            mFlag = FLAGS.HIGH_ACCURACY;
        } else {
            mFlag = FLAGS.MANUAL;
        }
    }

    /**
     * Broadcast method for integer values
     *
     * @param broadcastString String to broadcast
     * @param value           Value to broadcast as extra
     */
    private void broadcastNotificationInt(final String broadcastString, final int value) {
        AppLog.d(String.format(Locale.UK, "Broadcasting %s with value %d", broadcastString, value));
        final Intent broadcastIntent = new Intent(broadcastString);

        if (value > 0) {
            broadcastIntent.putExtra(Payloads.PAYLOAD_1.getString(), value);
        }
        this.sendBroadcast(broadcastIntent);
    }

    /**
     * Method which uses a wake lock and the step sensor to detect phone movement
     */
    private void processAutomaticMode() {


        final long SLEEP_TIME;

        if (mFlag == FLAGS.LOW_ACCURACY) {
            SLEEP_TIME = THREAD_WAIT_TIME_SHORT;
        } else {
            SLEEP_TIME = 0;
        }

        synchronized (this) {
            while (isServiceRunning()) {
                try {
                    // Zero the counter
                    stepCount = 0;

                    acquireWakeLock();
                    registerSensorListeners();
                    AppLog.i("Running for : " + THREAD_WAIT_TIME_LONG);

                    wait(THREAD_WAIT_TIME_LONG);

                    // release the locks while waiting
                    releaseWakeLock();
                    unregisterSensorListeners();

                    long waitTime = 0;

                    // Broadcast that the phone has not moved
                    if (stepCount == 0) {
                        timesIdle++;
                        broadcastNotificationInt(Broadcasts.IDLE_DETECTED.getString(), timesIdle);
                        if (timesIdle < maxTimesIdle) {
                        } else {
                            timesIdle = maxTimesIdle;
                        }
                        waitTime = SLEEP_TIME * timesIdle;
                    } else {
                        timesIdle = 0;
                        waitTime = SLEEP_TIME;
                    }

                    if (waitTime > 0) {
                        AppLog.d("Waiting for : " + waitTime);
                        wait(waitTime);
                    } else {
                        AppLog.d("Sensor running consecutively");
                    }
                } catch (final Exception e) {
                    setServiceRunning(false);
                    AppLog.d("Interrupted; " + e.getMessage());
                }
            }
        }
    }

    /**
     * Method which starts a thread to keep alive the Service until dismissed
     */
    private void processManualMode() {

        synchronized (this) {
            while (isServiceRunning()) {
                try {
                    wait();
                } catch (final Exception e) {
                    setServiceRunning(false);
                    AppLog.d("Interrupted; " + e.getMessage());
                }
            }
        }
    }

    /**
     * Method for registering the screen receiver
     */
    private void registerScreenReceiver() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    /**
     * Method for registering sensor detectors
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void registerSensorListeners() {
        AppLog.d("Initialising Sensor Manager");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // Do a check, just in case
        if (mSensorManager == null || mStepCounterSensor == null || mStepDetectorSensor == null) {
            AppLog.e("Error initialising sensors");
        }

        // Register the listeners
        AppLog.d("Registering Listeners");
        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Checks for the wake lock and releases it if it exists
     */
    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            AppLog.d("Releasing Wake Lock");
            mWakeLock.release();
        } else {
            AppLog.d("No Wake Lock to release");
        }
    }

    /**
     * Method for unregistering the screen receiver
     */
    private void unregisterScreenReceiver() {
        if (mReceiver == null) {
            AppLog.d("No Screen Receiver to unregister");
            return;
        }
        try {
            unregisterReceiver(mReceiver);
            AppLog.d("Screen Receiver Unregistered Successfully");
        } catch (final Exception e) {
            AppLog.d(String.format("Screen Receiver Not Registered or Already Unregistered. Exception : %s", e.getLocalizedMessage()));
        }
    }

    /**
     * Method for unregistering the sensor listeners
     */
    private void unregisterSensorListeners() {
        if (mSensorManager == null) {
            AppLog.d("No Sensor Receiver to unregister");
            return;
        }
        try {
            AppLog.d("Removing Sensor Receivers");
            mSensorManager.unregisterListener(this, mStepCounterSensor);
            mSensorManager.unregisterListener(this, mStepDetectorSensor);
        } catch (final Exception e) {
            AppLog.d(String.format("Sensor Receiver Not Registered or Already Unregistered. Exception : %s", e.getLocalizedMessage()));
        }

    }


    // enumerator for accuracy
    private enum FLAGS {
        MANUAL, LOW_ACCURACY, HIGH_ACCURACY
    }

    /**
     * Screen receiver which extends broadcast receiver
     *
     * @author Michael Fotiadis
     */
    public class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                AppLog.i("Screen Off");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                AppLog.i("Screen On");
                // Reset the counter
                timesIdle = 0;
            }
        }
    }

}
