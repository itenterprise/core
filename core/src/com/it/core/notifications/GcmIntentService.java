package com.it.core.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.it.core.R;
import com.it.core.application.ApplicationBase;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class GcmIntentService extends IntentService {
    public static int NOTIFICATION_ID = 0;
    private int NOTIFICATION_RANGE = 100;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public static final String TAG = "GCM IT-Enterprise Desktop";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //     sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //   sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                sendNotification(extras.getString("alert"), extras.getString("additional"));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String message, String additional) {
        Map<String, Serializable> additionalData = null;
        if (additional != null && !additional.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                additionalData = mapper.readValue(additional, Map.class);

            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NotificationProperties properties = ApplicationBase.getInstance().getNotificationProperties(additionalData);
        Class<?> notificationStartActivity = null;
        PendingIntent contentIntent = null;

        if (properties != null) {
            notificationStartActivity =properties.getNotificationStartActivity();
        }

        if (notificationStartActivity != null) {
            Intent notificationIntent = new Intent(this, properties.getNotificationStartActivity());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (additionalData != null) {
                for (String key : additionalData.keySet()) {
                    notificationIntent.putExtra(key, additionalData.get(key));
                }
            }
            contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        updateNotificationID();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setVibrate(new long[]{400, 400})
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText(message)
                        .setAutoCancel(true);

        if (properties != null)
        {
            mBuilder.setSmallIcon(properties.getApplicationIcon());
            mBuilder.setContentTitle(properties.getApplicationName());
        }

        if (contentIntent != null) {
            mBuilder.setContentIntent(contentIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void updateNotificationID() {
        if (NOTIFICATION_ID < NOTIFICATION_RANGE) {
            NOTIFICATION_ID++;
        } else {
            NOTIFICATION_ID = 1;
        }
    }
}