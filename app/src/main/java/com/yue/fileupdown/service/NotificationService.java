package com.yue.fileupdown.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.yue.fileupdown.R;
import com.yue.fileupdown.apkupdate.ApkNotificationHelper;
import com.yue.fileupdown.apkupdate.ApkNotificationParams;
import com.yue.fileupdown.apkupdate.ApkUpdateUtils;
import com.yue.fileupdown.constant.Constanct;

import java.io.File;

/**
 * @author shimy
 * @create 2019/11/20 11:03
 * @desc 测试通知
 */
public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifaction();
        return super.onStartCommand(intent, flags, startId);
    }

    private void notifaction() {
        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        Intent intent = ApkUpdateUtils.getInstallIntent(this, Constanct.downloadPath + File.separator + "pad-1.4.0.apk", "com.yue.fileupdown.fileprovider");
        /*清除栈*/
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /*通知栏点击事件*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                ApkUpdateUtils.createNotificationChannel(this, "test_channle", "test", "test"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("测试")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                /*点击完通知自动消失*/
                .setAutoCancel(true);
        builder.setContentText("测试");
        builder.setContentIntent(pendingIntent);
        mNotificationManagerCompat.notify(1521,builder.build());
    }
}
