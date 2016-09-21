package com.michaelfotiadis.deskalarm.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.common.base.fragment.BaseFragment;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.PreferenceKeys;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.ToastHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;
import com.michaelfotiadis.deskalarm.views.ErgoAnalogClock;
import com.michaelfotiadis.deskalarm.views.ErgoClockInterface;
import com.michaelfotiadis.deskalarm.views.ErgoDigitalClock;
import com.michaelfotiadis.deskalarm.views.ErgoFusionClock;

/**
 * Custom Fragment for storing a clock View
 *
 * @author Michael Fotiadis
 */
public class ErgoClockFragment extends BaseFragment {

    // Key for the integer extra position, used once in the New Instance method
    private static final String ARG_POSITION = "position";
    private ToastHelper mToastHelper;
    // Fields for storing the Clock Interface and its 2 implementations
    private ErgoClockInterface mCurrentClockInterface;
    private ErgoAnalogClock mAnalogClock;
    private ErgoDigitalClock mDigitalClock;
    private ErgoFusionClock mFusionClock;
    // variable for storing instance of Broadcast Receiver
    private ResponseReceiver mResponseReceiver;
    // variable for storing clock preference
    private String mClockPreference;

    /**
     * Creates a new instance of the clock fragment
     *
     * @param position integer position of the fragment
     * @return instance of the ClockFragment
     */
    public static BaseFragment newInstance(final int position) {
        final BaseFragment fragment = new ErgoClockFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

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
        return getPreferenceHandler().getAppSharedPreferences().getInt(
                getString(R.string.pref_alarm_interval_key),
                getResources().getInteger(R.integer.time_to_alarm));
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mToastHelper = new ToastHelper((Activity) context);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        AppLog.d("Fragment created at position: " + getConstructorArguments());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // inflate the view
        final View view = inflater.inflate(R.layout.fragment_clock, container, false);

        // create 2 clock views implementing the same interface
        mAnalogClock = (ErgoAnalogClock) view.findViewById(R.id.analogClock);
        mDigitalClock = (ErgoDigitalClock) view.findViewById(R.id.digitalClock);
        mFusionClock = (ErgoFusionClock) view.findViewById(R.id.fusionClock);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        AppLog.d("onResume");
        super.onResume();

        registerResponseReceiver();

        // read the clock preferences from the shared preference object
        mClockPreference = getPreferenceHandler().getAppSharedPreferences().getString(
                getActivity().getString(R.string.pref_clock_type_key),
                getActivity().getString(R.string.pref_clock_type_default));
        AppLog.d("Preference is " + mClockPreference);

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
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        AppLog.d("onPause");
        super.onPause();

        pauseTheClock();
        unregisterResponseReceiver();
        mToastHelper.dismissActiveToasts();
    }

    /**
     * Send the command to pause the clock to the interface
     */
    private void pauseTheClock() {
        mToastHelper.dismissActiveToasts();
        mCurrentClockInterface.pauseClock();
    }

    /**
     * Register broadcast receiver
     */
    private void registerResponseReceiver() {
        unregisterResponseReceiver();

        AppLog.d("Registering Response Receiver");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcasts.DATA_CHANGED.getString());

        mResponseReceiver = new ResponseReceiver();
        getActivity().registerReceiver(mResponseReceiver, intentFilter);
    }

    /**
     * Send the command to start the clock to the interface
     *
     * @param time
     */
    private void startTheClock(final long time, final int interval) {
        mCurrentClockInterface.startClock(time, interval);
    }

    /**
     * Send the command to stop the clock to the interface and toast the time ran
     */
    private void stopTheClock() {
        final long timeRunning = mCurrentClockInterface.getTimeRunning();


        // create a report message for the toast
        if (timeRunning > 0) {
            final String sb = String.format("Timer ran for \t%s", PrimitiveConversions.getTimeStringFromSeconds(timeRunning));

            mToastHelper.makeInfoToast(sb);
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
            AppLog.d("Receiver Unregistered Successfully");
        } catch (final Exception e) {
            AppLog.e(String.format("Response Receiver Not Registered or Already Unregistered. Exception : %s", e.getLocalizedMessage()));
        }
    }

    /**
     * Custom Broadcast Receiver class
     *
     * @author Michael Fotiadis
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {

            final String action = intent.getAction();

            if (action.equalsIgnoreCase(Broadcasts.DATA_CHANGED.getString())) {
                AppLog.d("Resetting Clock");
                if (getTimeStartedFromPreferences() > 0) {
                    startTheClock(getTimeStartedFromPreferences(), getIntervalFromPreferences());
                } else {
                    stopTheClock();
                }
            }
        }
    }

}
