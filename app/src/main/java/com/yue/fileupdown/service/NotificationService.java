package com.yue.fileupdown.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yue.fileupdown.apkupdate.ApkNotificationHelper;
import com.yue.fileupdown.apkupdate.ApkNotificationParams;

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
        ApkNotificationParams params = new ApkNotificationParams.Builder(this, "", "com.yue.fileupdown.fileprovider").create();
        ApkNotificationHelper mNotificationHelper = new ApkNotificationHelper(this, params);
        mNotificationHelper.notifiactionProgress(100, "下载成功,点击安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
        return super.onStartCommand(intent, flags, startId);
    }
}
