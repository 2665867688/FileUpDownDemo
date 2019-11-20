package com.yue.fileupdown.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
}
