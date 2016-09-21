package com.michaelfotiadis.deskalarm.common.base.core;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.activities.MainActivity;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ErgoNotificationManager {

    private static final String PEBBLE_INTENT_DESCRIPTOR = "com.getpebble.action.SEND_NOTIFICATION";
    private static final String NOTIFICATION_SENDER_DESCRIPTOR = "com.michaelfotiadis.deskalarm";
    private static final String PEBBLE_ALERT = "PEBBLE_ALERT";

    private final Context mContext;
    private final PreferenceHandler mPreferenceHandler;
    private final NotificationManager mNotificationManager;

    protected ErgoNotificationManager(final Context context) {
        this.mContext = context;
        this.mPreferenceHandler = new PreferenceHandler(context);
        this.mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    /**
     * Method which cancel a notification by ID
     */
    public void cancelAlarmNotification() {
        try {
            mNotificationManager.cancel(AppConstants.ALARM_NOTIFICATION_ID);
            AppLog.d("Notification " + AppConstants.ALARM_NOTIFICATION_ID
                    + " cancelled.");
        } catch (final Exception e) {
            AppLog.e("Error while cancelling notification");
        }
    }

    /**
     * Method which fires a notification
     */
    @SuppressLint("InlinedApi")
    public void fireAlarmNotification() {

        final String notificationTitle = mContext.getString(R.string.app_name);
        final String notificationBody = mContext.getString(R.string.dialog_alarm_body);


        final Intent resultIntent = new Intent(mContext, MainActivity.class);

        // add a boolean to trigger the alert dialog once the activity is opened
        resultIntent.putExtra(Payloads.PAYLOAD_1.getString(), true);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setOngoing(true)
                .setContentText(notificationBody);

        // add a button to the notification if 4.1 or higher
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final Intent broadcastIntent = new Intent(Broadcasts.ALARM_STOPPED.getString());
            final PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_clear_grey_500_18dp, "Dismiss", pIntent);
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        // retrieve the notification type from the shared preferences object
        final String notificationPreference = mPreferenceHandler.getAppSharedPreferences().getString(
                mContext.getString(R.string.pref_ringtones_key),
                mContext.getString(R.string.pref_ringtones_default));

        if (notificationPreference.length() < 1) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else {
            // handle the shared preferences
            builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            final int resID = mContext.getResources().getIdentifier(notificationPreference, "raw", mContext.getPackageName());
            AppLog.i("RESOURCE " + resID);
            if (resID != 0) {
                builder.setSound(Uri.parse("android.resource://com.michaelfotiadis.deskalarm/" + resID));
            }
        }

		/*
         * The stack builder object will contain an artificial back stack for
		 * the started Activity. This ensures that navigating backward from the
		 * Activity leads out of your application to the Home screen.
		 */
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        AppLog.d(String.format(Locale.UK, "Sending Notification with ID: %d", AppConstants.ALARM_NOTIFICATION_ID));

        mNotificationManager.notify(AppConstants.ALARM_NOTIFICATION_ID, builder.build());

        // also send the request to Pebble
        sendPebble(notificationTitle, notificationBody);
    }

    /**
     * Broadcasts a notification to Pebble
     *
     * @param title Notification String title
     * @param body  Notification String body
     */
    private void sendPebble(final String title, final String body) {
        try {
            final Intent pebbleIntent = new Intent(PEBBLE_INTENT_DESCRIPTOR);

            // create a map with the data
            final Map<String, String> data = new HashMap<String, String>();
            data.put("title", title);
            data.put("body", body);

            // convert data to JSON
            final JSONObject jsonData = new JSONObject(data);
            final String notificationData = new JSONArray().put(jsonData).toString();
            pebbleIntent.putExtra("messageType", PEBBLE_ALERT);
            pebbleIntent.putExtra("sender", NOTIFICATION_SENDER_DESCRIPTOR);
            pebbleIntent.putExtra("notificationData", notificationData);

            AppLog.d("Sending to Pebble: " + notificationData);
            mContext.sendBroadcast(pebbleIntent);
        } catch (final Exception e) {
            AppLog.e("Error while transmiting to Pebble " + e.getLocalizedMessage());
        }
    }

}
