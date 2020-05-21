package com.example.timemanagment.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.example.timemanagment.R;

public class PrevTaskAlarmService extends IntentService {


    public PrevTaskAlarmService() {
        super("prev alarm service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_prev_notification_layout);

        NotificationChannel mChannel = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);

        Notification.Builder notification = new Notification.Builder(this, "1");

        notification.setSmallIcon(R.drawable.ic_todo_list);
        notification.setCustomBigContentView(contentView);
        notification.setStyle(new Notification.DecoratedCustomViewStyle());

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(1, notification.build());


        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringTon = RingtoneManager.getRingtone(getApplicationContext(), alert);
        ringTon.play();
    }
}
