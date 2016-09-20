package com.michaelfotiadis.deskalarm.services;

import android.app.IntentService;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.managers.ErgoNotificationManager;
import com.michaelfotiadis.deskalarm.utils.Logger;

public class ErgoAlarmService extends IntentService {

    private final String TAG = "ErgoAlarmService";

    public ErgoAlarmService() {
        super("ErgoAlarmService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final int extra = intent.getIntExtra(
                AppConstants.Payloads.ALARM_PAYLOAD.getString(), 0);

		/* Cancel all alarms if the service has been destroyed in the meantime.
         * This actually protects against the user or system destroying the application.
		 */
        if (!ErgoStepService.isServiceRunning()) {
            Logger.d(TAG, "Service Dismissed. Cancelling all alarms.");
            return;
        }

        if (extra == AppConstants.Requests.REQUEST_CODE_1.getCode()) {
            // notify the notification manager to fire an alarm notification
            new ErgoNotificationManager().fireAlarmNotification(getBaseContext());
            // broadcast that the alarm has been triggered (to be picked up by Main activity)
            broadcastAlarmTriggered();
        }
    }

    /**
     * Generates an intent signifying that the alarm has been triggered
     */
    private void broadcastAlarmTriggered() {
        Logger.d(TAG, "Broadcasting Alarm Triggered");

        Intent broadcastIntent = new Intent(AppConstants.Broadcasts.ALARM_TRIGGERED.getString());
        this.sendBroadcast(broadcastIntent);
    }

}
