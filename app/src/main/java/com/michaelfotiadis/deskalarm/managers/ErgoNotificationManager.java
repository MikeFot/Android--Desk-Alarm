package com.michaelfotiadis.deskalarm.managers;

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
import com.michaelfotiadis.deskalarm.activities.DeskAlarmMainActivity;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ErgoNotificationManager {

    private final String TAG = "MyNotificationManager";

    private final String PEBBLE_INTENT_DESCRIPTOR = "com.getpebble.action.SEND_NOTIFICATION";
    private final String NOTIFICATION_SENDER_DESCRIPTOR = "com.michaelfotiadis.deskalarm";
    private final String PEBBLE_ALERT = "PEBBLE_ALERT";

    /**
     * Method which cancel a notification by ID
     *
     * @param context
     */
    public void cancelAlarmNotification(final Context context) {
        try {
            final NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(AppConstants.ALARM_NOTIFICATION_ID);
            Logger.d(TAG, "Notification " + AppConstants.ALARM_NOTIFICATION_ID
                    + " cancelled.");
        } catch (Exception e) {
            Logger.e(TAG, "Error while cancelling notification");
        }
    }

    /**
     * Method which fires a notification
     *
     * @param context
     */
    @SuppressLint("InlinedApi")
    public void fireAlarmNotification(final Context context) {

        final String notificationTitle = context.getString(R.string.app_name);
        final String notificationBody = context.getString(R.string.dialog_alarm_body);


        final Intent resultIntent = new Intent(context, DeskAlarmMainActivity.class);

        // add a boolean to trigger the alert dialog once the activity is opened
        resultIntent.putExtra(AppConstants.Payloads.PAYLOAD_1.getString(), true);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setOngoing(true)
                .setContentText(notificationBody);

        // add a button to the notification if 4.1 or higher
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final Intent broadcastIntent = new Intent(AppConstants.Broadcasts.ALARM_STOPPED.getString());
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.addAction(R.drawable.ic_clear_grey_500_18dp, "Dismiss", pIntent);
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // retrieve the notification type from the shared preferences object
        final String notificationPreference = new AppUtils().getAppSharedPreferences(context.getApplicationContext()).getString(
                context.getString(R.string.pref_ringtones_key),
                context.getString(R.string.pref_ringtones_default));

        if (notificationPreference.length() < 1) {
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        } else {
            // handle the shared preferences
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
            int resID = context.getResources().getIdentifier(notificationPreference, "raw", context.getPackageName());
            Logger.i(TAG, "RESOURCE " + resID);
            if (resID != 0) {
                mBuilder.setSound(Uri.parse("android.resource://com.michaelfotiadis.deskalarm/" + resID));
            }
        }

		/*
         * The stack builder object will contain an artificial back stack for
		 * the started Activity. This ensures that navigating backward from the
		 * Activity leads out of your application to the Home screen.
		 */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DeskAlarmMainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Logger.d(TAG, "Sending Notification with ID: "
                + AppConstants.ALARM_NOTIFICATION_ID);

        mNotificationManager.notify(AppConstants.ALARM_NOTIFICATION_ID, mBuilder.build());

        // also send the request to Pebble
        sendPebble(context, notificationTitle, notificationBody);
    }

    /**
     * Broadcasts a notification to Pebble
     *
     * @param context Context transmitting the broadcast
     * @param title   Notification String title
     * @param body    Notification String body
     */
    public void sendPebble(final Context context, final String title, final String body) {
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

            Logger.d(TAG, "Sending to Pebble: " + notificationData);
            context.sendBroadcast(pebbleIntent);
        } catch (Exception e) {
            Logger.e(TAG, "Error while transmiting to Pebble " + e.getLocalizedMessage());
        }
    }

}
