package com.michaelfotiadis.deskalarm.services;

import android.app.IntentService;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.common.base.core.Core;
import com.michaelfotiadis.deskalarm.common.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.model.Requests;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoAlarmService extends IntentService {

    private final Core mCore;

    public ErgoAlarmService() {
        super("ErgoAlarmService");
        mCore = new CoreProvider(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final int extra = intent.getIntExtra(Payloads.ALARM_PAYLOAD.getString(), 0);

		/* Cancel all alarms if the service has been destroyed in the meantime.
         * This actually protects against the user or system destroying the application.
		 */
        if (!ErgoStepService.isServiceRunning()) {
            AppLog.d("Service Dismissed. Cancelling all alarms.");
            return;
        }

        if (extra == Requests.REQUEST_CODE_1.getCode()) {
            // notify the notification manager to fire an alarm notification
            mCore.getNotificationManager().fireAlarmNotification();
            // broadcast that the alarm has been triggered (to be picked up by Main activity)
            broadcastAlarmTriggered();
        }
    }

    /**
     * Generates an intent signifying that the alarm has been triggered
     */
    private void broadcastAlarmTriggered() {
        AppLog.d("Broadcasting Alarm Triggered");

        final Intent broadcastIntent = new Intent(Broadcasts.ALARM_TRIGGERED.getString());
        this.sendBroadcast(broadcastIntent);
    }

}
