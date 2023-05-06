package com.sklerbidi.therapistmobileapp.CustomClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sklerbidi.therapistmobileapp.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "IPainNotif")
                .setSmallIcon(R.drawable.ipain_icon_rounded)
                .setContentTitle("IPain Reminder")
                .setContentText("You have pending task")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200, builder.build());
    }
}
