package com.yue.fileupdown.apkupdate;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.yue.fileupdown.MainActivity;
import com.yue.fileupdown.R;

/**
 * 进度条通知
 */
public class ApkNotificationHelper {

    public enum ApkNotificationType {
        SUCCESS,
        ERROR,
        PROGRESS
    }

    private ApkNotificationParams params;
    private Context context;
    private NotificationManagerCompat mNotificationManagerCompat;

    public ApkNotificationHelper(Context context, ApkNotificationParams params) {
        this.params = params;
        this.context = context;
        mNotificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
    }

    /**
     * @param progress    进度
     * @param contentText 显示内容
     *                    progress > 100 ? "下载完成" : progress + "/100"
     */
    public void notifiactionProgress(int progress, String contentText, ApkNotificationType type) {
        NotificationCompat.Action retryAction = null;
//        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        Intent intent = ApkUpdateUtils.getInstallIntent(context, params.getPath(), params.getAuthority());
        /*通知栏点击事件*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                ApkUpdateUtils.createNotificationChannel(context, params.getChannelId(), params.getChannelName(), params.getChannelDescription()))
                .setSmallIcon(params.getSmallIcon())
                .setLargeIcon(params.getLargeIcon())
                .setContentTitle(params.getContentTitle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                /*点击完通知自动消失*/
                .setAutoCancel(true);

        int PROGRESS_MAX = 100;
        switch (type) {
            case SUCCESS:
                builder.setContentText(contentText);
                builder.setProgress(PROGRESS_MAX, progress, false);
                builder.setContentIntent(pendingIntent);
                break;
            case PROGRESS:
                builder.setContentText(contentText);
                builder.setProgress(PROGRESS_MAX, progress, false);
                break;
            case ERROR:
                builder.setContentText(contentText);
                Intent retryIntent = new Intent(context, ApkUpdateService.class);
                PendingIntent retryPendingIntent = PendingIntent.getService(context, 0, retryIntent, 0);
                retryAction = new NotificationCompat.Action.Builder(
                        R.drawable.ic_launcher_background,
                        "重试",
                        retryPendingIntent)
                        .build();
                builder.addAction(retryAction);
                break;
        }

        Notification notificationCompat = builder.build();
        /*不可清除*/
        if (type == ApkNotificationType.PROGRESS)
            notificationCompat.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManagerCompat.notify(params.getNotifactionId(), notificationCompat);
    }

    public void cancelNotifaction() {
        mNotificationManagerCompat.cancel(params.getNotifactionId());
    }


}
