package com.lakala.shoudan.common;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.SplashActivity;

/**
 * Created by More on 14/11/27.
 */
public class NotificationPusher {

    public static void showNotification(String title, String alert){

        Context context = ApplicationEx.getInstance();

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.app_icon, title, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;

        Intent intent = new Intent(context,NotificationClickedReceiver.class);
        intent.setAction("com.lakala.shoudan.action.Notification_Clicked");
        PendingIntent contentIntent = PendingIntent.getBroadcast(context,0,intent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setLatestEventInfo(
                context,
                title,
                alert,
                contentIntent);
        notificationManager.notify(R.string.app_name, notification);
    }



}
