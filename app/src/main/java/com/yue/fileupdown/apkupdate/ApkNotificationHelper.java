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


    private ApkNotificationParams params;
    private Context context;
    NotificationManagerCompat mNotificationManagerCompat;

    public ApkNotificationHelper(Context context, ApkNotificationParams params) {
        this.params = params;
        this.context = context;
        mNotificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
    }

    /**
     * @param progress    进度
     * @param contentText 显示内容
     * @param isRetry     失败时是否显示重试按钮，正在进行中请设置成false
     */
    public void notifiaction(int progress, String contentText, boolean isRetry) {
        NotificationCompat.Action retryAction = null;
        if (isRetry) {
            /**
             * 重试按钮点击事件 下载失败时由isRetry决定
             */
            Intent retryIntent = new Intent(context, ApkUpdateService.class);
            PendingIntent dismissPendingIntent = PendingIntent.getService(context, 0, retryIntent, 0);
            retryAction =
                    new NotificationCompat.Action.Builder(
                            R.drawable.ic_launcher_background,
                            "重试",
                            dismissPendingIntent)
                            .build();
        }
//        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        Intent intent = ApkUpdateUtils.getInstallIntent(context, params.getPath(), params.getAuthority());
        /*清除栈*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /*通知栏点击事件*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        progress > 100 ? "下载完成" : progress + "/100"
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                ApkUpdateUtils.createNotificationChannel(context, params.getChannelId(), params.getChannelName(), params.getChannelDescription()))
                .setSmallIcon(params.getSmallIcon())
                .setLargeIcon(params.getLargeIcon())
                .setContentTitle(params.getContentTitle())
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                /*点击完通知自动消失*/
                .setAutoCancel(true);

        /**
         * 下载完成点击可完成安装
         */
        if (progress == 100)
            builder.setContentIntent(pendingIntent);
        if (retryAction != null)
            builder.addAction(retryAction);

        int PROGRESS_MAX = 100;
        builder.setProgress(PROGRESS_MAX, progress, false);
        Notification notificationCompat = builder.build();
        /*不可清除*/
        notificationCompat.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManagerCompat.notify(params.getNotifactionId(), notificationCompat);
    }

    public void cancelNotifaction() {
        mNotificationManagerCompat.cancel(params.getNotifactionId());
    }


}
