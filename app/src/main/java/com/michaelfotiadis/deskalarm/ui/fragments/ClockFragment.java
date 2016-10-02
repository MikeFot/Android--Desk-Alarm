package com.michaelfotiadis.deskalarm.ui.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.services.step.StepService;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.fragment.BaseFragment;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.ToastHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;
import com.michaelfotiadis.deskalarm.views.AnalogClock;
import com.michaelfotiadis.deskalarm.views.Clock;
import com.michaelfotiadis.deskalarm.views.DigitalClock;
import com.michaelfotiadis.deskalarm.views.FusionClock;

/**
 * Custom Fragment for storing a clock View
 *
 * @author Michael Fotiadis
 */
public class ClockFragment extends BaseFragment {
    private ToastHelper mToastHelper;
    // Fields for storing the Clock Interface and its 2 implementations
    private Clock mCurrentClock;
    private AnalogClock mAnalogClock;
    private DigitalClock mDigitalClock;
    private FusionClock mFusionClock;
    // variable for storing instance of Broadcast Receiver
    private ResponseReceiver mResponseReceiver;
    // variable for storing clock preference
    private String mClockPreference;

    /**
     * Creates a new instance of the clock fragment
     *
     * @return instance of the ClockFragment
     */
    public static BaseFragment newInstance() {
        final BaseFragment fragment = new ClockFragment();
        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @return Long value of service time started from Shared Preferences
     */
    public long getTimeStartedFromPreferences() {
        return getPreferenceHandler().getLongPreference(PreferenceHandler.PreferenceKey.TIME_STARTED);
    }

    public long getIntervalFromPreferences() {
        return getPreferenceHandler().getLongPreference(PreferenceHandler.PreferenceKey.ALARM_INTERVAL);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mToastHelper = new ToastHelper((Activity) context);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // inflate the view
        final View view = inflater.inflate(R.layout.fragment_clock, container, false);

        // create 2 clock views implementing the same interface
        mAnalogClock = (AnalogClock) view.findViewById(R.id.analogClock);
        mDigitalClock = (DigitalClock) view.findViewById(R.id.digitalClock);
        mFusionClock = (FusionClock) view.findViewById(R.id.fusionClock);
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
        AppLog.d("Preference is " + mClockPreference);
        mClockPreference = getPreferenceHandler().getStringPreference(PreferenceHandler.PreferenceKey.CLOCK_TYPE);
        // Set clock by preference
        if (mClockPreference.equals(getActivity().getString(R.string.clock_digital))) {
            mAnalogClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.GONE);
            mDigitalClock.setVisibility(View.VISIBLE);
            mCurrentClock = mDigitalClock;
        } else if (mClockPreference.equals(getActivity().getString(R.string.clock_fusion))) {
            mAnalogClock.setVisibility(View.GONE);
            mDigitalClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.VISIBLE);
            mCurrentClock = mFusionClock;
        } else {
            mAnalogClock.setVisibility(View.VISIBLE);
            mDigitalClock.setVisibility(View.GONE);
            mFusionClock.setVisibility(View.GONE);
            mCurrentClock = mAnalogClock;
        }

        // set an initial alarm indication
        mCurrentClock.setMinutesToAlarm(getIntervalFromPreferences());

        // Resume the clock only if step service is still running
        if (StepService.isServiceRunning()) {
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
        mCurrentClock.pauseClock();
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
    private void startTheClock(final long time, final long interval) {
        mCurrentClock.startClock(time, interval);
    }

    /**
     * Send the command to stop the clock to the interface and toast the time ran
     */
    private void stopTheClock() {
        final long timeRunning = mCurrentClock.getTimeRunning();


        // create a report message for the toast
        if (timeRunning > 0) {
            final String sb = String.format("Timer ran for \t%s", PrimitiveConversions.getTimeStringFromSeconds(timeRunning));

            mToastHelper.makeInfoToast(sb);
        }
        // subsequently stop the clock
        mCurrentClock.stopClock();
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
