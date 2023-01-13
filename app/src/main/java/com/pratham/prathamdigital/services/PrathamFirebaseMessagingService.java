package com.pratham.prathamdigital.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.ui.splash.ActivitySplash_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PrathamFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        //  Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String key = "";
        String value = "";
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject jsonData = new JSONObject(remoteMessage.getData());
                if (jsonData.has("key")) key = jsonData.getString("key");
                if (jsonData.has("value")) value = jsonData.getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(), key, value);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param key
     * @param value
     */
    private void sendNotification(String messageBody, String key, String value) {
        Log.d("sendNotification::", messageBody);
        try {
            FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = null;
        if (PD_Utility.isActivityRunning(ActivitySplash_.class))
            intent = new Intent(this, ActivitySplash_.class);
        else if (PD_Utility.isActivityRunning(AttendanceActivity_.class))
            intent = new Intent(this, AttendanceActivity_.class);
        else if (PD_Utility.isActivityRunning(ActivityMain_.class))
            intent = new Intent(this, ActivityMain_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(PD_Constant.PUSH_NOTI_KEY, key);
        intent.putExtra(PD_Constant.PUSH_NOTI_VALUE, value);
        Log.d(TAG, "sendNotification::" + key + "::" + value);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getApplicationContext().getPackageName() + "/" + R.raw.bubble_pop);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_app_logo_)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.fcm_message),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri, attributes);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}