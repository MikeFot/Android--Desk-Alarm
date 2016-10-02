package com.michaelfotiadis.deskalarm.ui.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.services.AudioService;
import com.michaelfotiadis.deskalarm.services.step.StepService;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoServiceManagerImpl implements ErgoServiceManager {

    private final Context mContext;

    /*package*/ ErgoServiceManagerImpl(final Context context) {
        this.mContext = context;
    }

    /**
     * Starts the step service
     */
    @Override
    public void startStepService() {
        if (!StepService.isServiceRunning()) {
            AppLog.d("Starting Service");
            final Intent intent = new Intent(mContext, StepService.class);
            mContext.startService(intent);
        } else {
            AppLog.d("Service Already Running");
        }
    }

    /**
     * Stops the step service
     */
    @Override
    public void stopStepService() {
        AppLog.d("Stopping Service");
        final Intent intent = new Intent(mContext, StepService.class);
        mContext.stopService(intent);
    }

    /**
     * Starts the audio service
     */
    @Override
    public void startAudioService() {
        final Intent intent = new Intent(mContext, AudioService.class);
        mContext.startService(intent);
    }

}
