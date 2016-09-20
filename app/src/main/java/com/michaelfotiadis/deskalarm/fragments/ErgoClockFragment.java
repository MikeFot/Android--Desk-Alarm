package com.michaelfotiadis.deskalarm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.constants.AppConstants.PreferenceKeys;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.ToastUtils;
import com.michaelfotiadis.deskalarm.views.ErgoAnalogClock;
import com.michaelfotiadis.deskalarm.views.ErgoClockInterface;
import com.michaelfotiadis.deskalarm.views.ErgoDigitalClock;
import com.michaelfotiadis.deskalarm.views.ErgoFusionClock;

/**
 * Custom Fragment for storing a clock View
 *
 * @author Michael Fotiadis
 */
public class ErgoClockFragment extends Fragment {

    /**
     * Custom Broadcast Receiver class
     *
     * @author Michael Fotiadis
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {

            final String action = intent.getAction();

            if (action.equalsIgnoreCase(AppConstants.Broadcasts.DATA_CHANGED.getString())) {
                Logger.d(TAG, "Resetting Clock");
                long time = getTimeStartedFromPreferences();

                if (time > 0) {
                    startTheClock(getTimeStartedFromPreferences(), getIntervalFromPreferences());
                } else {
                    stopTheClock();
                }
            }
        }
    }

    /**
     * Creates a new instance of the clock fragment
     *
     * @param position integer position of the fragment
     * @return instance of the ClockFragment
     */
    public static ErgoClockFragment newInstance(int position) {
        ErgoClockFragment fragmentInstance = new ErgoClockFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragmentInstance.setArguments(bundle);

        return fragmentInstance;
    }

    // Class Logger TAG
    private final String TAG = "Fragment Clock";

    // Key for the integer extra position, used once in the New Instance method
    private static final String ARG_POSITION = "position";

    // Fields for storing the Clock Interface and its 2 implementations
    private ErgoClockInterface mCurrentClockInterface;
    private ErgoAnalogClock mAnalogClock;
    private ErgoDigitalClock mDigitalClock;
    private ErgoFusionClock mFusionClock;

    // variable for storing instance of Broadcast Receiver
    private ResponseReceiver mResponseReceiver;
    // variable for storing clock preference
    private String mClockPreference;

    public int getConstructorArguments() {
        return getArguments().getInt(ARG_POSITION);
    }

    /**
     * @return Long value of service time started from Shared Preferences
     */
    public long getTimeStartedFromPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(
                PreferenceKeys.KEY_1.getString(), 0);
    }

    public int getIntervalFromPreferences() {
        return new AppUtils().getAppSharedPreferences(getActivity()).getInt(
                getString(R.string.pref_alarm_interval_key),
                getResources().getInteger(R.integer.time_to_alarm));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "Fragment created at position: " + getConstructorArguments());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the view
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        // create 2 clock views implementing the same interface
        mAnalogClock = (ErgoAnalogClock) view.findViewById(R.id.analogClock);
        mDigitalClock = (ErgoDigitalClock) view.findViewById(R.id.digitalClock);
        mFusionClock = (ErgoFusionClock) view.findViewById(R.id.fusionClock);
        return view;
    }

    @Override
    public void onPause() {
        Logger.d(TAG, "onPause");
        super.onPause();

        pauseTheClock();
        unregisterResponseReceiver();

        if (mSuperActivityToast != null && mSuperActivityToast.isShowing()) {
            mSuperActivityToast.dismiss();
        }
    }

    ;

    @Override
    public void onResume() {
        Logger.d(TAG, "onResume");
        super.onResume();

        registerResponseReceiver();

        // read the clock preferences from the shared preference object
        mClockPreference = new AppUtils().getAppSharedPreferences(getActivity()).getString(
                getActivity().getString(R.string.pref_clock_type_key),
                getActivity().getString(R.string.pref_clock_type_default));
        Logger.d(TAG, "Preference is " + mClockPreference);

        // Set clock by preference
        if (mClockPreference.equals(getActivity().getString(R.string.clock_digital))) {
            mAnalogClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.GONE);
            mDigitalClock.setVisibility(View.VISIBLE);
            mCurrentClockInterface = mDigitalClock;
        } else if (mClockPreference.equals(getActivity().getString(R.string.clock_fusion))) {
            mAnalogClock.setVisibility(View.GONE);
            mDigitalClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.VISIBLE);
            mCurrentClockInterface = mFusionClock;
        } else {
            mAnalogClock.setVisibility(View.VISIBLE);
            mDigitalClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.GONE);
            mCurrentClockInterface = mAnalogClock;
        }

        // set an initial alarm indication
        mCurrentClockInterface.setMinutesToAlarm(getIntervalFromPreferences());

        // Resume the clock only if step service is still running
        if (ErgoStepService.isServiceRunning()) {
            startTheClock(getTimeStartedFromPreferences(), getIntervalFromPreferences());
        } else {
            stopTheClock();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     * Send the command to pause the clock to the interface
     */
    private void pauseTheClock() {
        new ToastUtils().dismissActiveToasts();
        mCurrentClockInterface.pauseClock();
    }

    /**
     * Register broadcast receiver
     */
    private void registerResponseReceiver() {
        unregisterResponseReceiver();

        Logger.d(TAG, "Registering Response Receiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Broadcasts.DATA_CHANGED.getString());

        mResponseReceiver = new ResponseReceiver();
        getActivity().registerReceiver(mResponseReceiver, intentFilter);
    }

    /**
     * Send the command to start the clock to the interface
     *
     * @param time
     */
    private void startTheClock(long time, int interval) {
        mCurrentClockInterface.startClock(time, interval);
    }

    private SuperActivityToast mSuperActivityToast;

    /**
     * Send the command to stop the clock to the interface and toast the time ran
     */
    private void stopTheClock() {
        long timeRunning = mCurrentClockInterface.getTimeRunning();


        // create a report message for the toast
        if (timeRunning > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Timer ran for \t");
            sb.append(PrimitiveConversions.getTimeStringFromSeconds(timeRunning));

            // make an info toast dismissing active ones first
            if (mSuperActivityToast != null && mSuperActivityToast.isShowing()) {
                SuperActivityToast.cancelAllSuperToasts();
                mSuperActivityToast.dismiss();
            }
            mSuperActivityToast = new ToastUtils().makeInfoToast(getActivity(), sb.toString());
        }
        // subsequently stop the clock
        mCurrentClockInterface.stopClock();
    }

    /**
     * Method which unregisters the custom BroadcastReceiver
     */
    private void unregisterResponseReceiver() {
        if (mResponseReceiver == null) {
            return;
        }
        try {
            getActivity().unregisterReceiver(mResponseReceiver);
            Logger.d(TAG, "Receiver Unregistered Successfully");
        } catch (Exception e) {
            Logger.d(TAG,
                    "Response Receiver Not Registered or Already Unregistered. Exception : "
                            + e.getLocalizedMessage());
        }
    }

}
