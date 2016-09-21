package com.michaelfotiadis.deskalarm.common.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.services.ErgoAudioService;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoServiceManager {

    private final Context mContext;

    protected ErgoServiceManager(final Context context) {
        this.mContext = context;
    }

    /**
     * Starts the step service
     */
    public void startStepService() {
        if (!ErgoStepService.isServiceRunning()) {
            AppLog.d("Starting Service");
            final Intent intent = new Intent(mContext, ErgoStepService.class);
            mContext.startService(intent);
        } else {
            AppLog.d("Service Already Running");
        }
    }

    /**
     * Stops the step service
     */
    public void stopStepService() {
        AppLog.d("Stopping Service");
        final Intent intent = new Intent(mContext, ErgoStepService.class);
        mContext.stopService(intent);
    }

    /**
     * Starts the audio service
     */
    public void startAudioService() {
        final Intent intent = new Intent(mContext, ErgoAudioService.class);
        mContext.startService(intent);
    }

}
