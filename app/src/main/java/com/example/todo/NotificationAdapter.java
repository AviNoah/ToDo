package com.example.todo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationAdapter {

    private String CHANNEL_ID;
    private Context context;

    private static int notificationId = 0;

    public NotificationAdapter(String CHANNEL_ID, Context context) {
        // Provide a unique channel_id, and provide a context referring to the activity you are calling the constructor from
        this.CHANNEL_ID = CHANNEL_ID;
        this.context = context;
    }

    public void showNotification(String textTitle, String textContent, Intent intent)
    {
        createNotificationChannel();
        callNotification(createNotificationBuilder(textTitle, textContent, intent));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        // Must be executed first and foremost to post notifications to android 8 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.context.getString(R.string.channel_name);
            String description = this.context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder createNotificationBuilder(String textTitle, String textContent, Intent intent)   {
        // Where the notification will take the user once clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, this.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                // Automatically remove the notification once clicked.
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder;
    }

    private void callNotification(NotificationCompat.Builder builder) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId++, builder.build());
    }
}
