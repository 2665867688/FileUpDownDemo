package com.yue.fileupdown.apkupdate;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.yue.fileupdown.constant.Constanct;

/**
 * @author shimy
 * @create 2019/11/19 10:56
 * @desc apk文件更新service
 */
public class ApkUpdateService extends Service {

    private static final String ARG_DOWNLOADURL = "ARG_DOWNLOADURL";
    private String mDownLoadUrl;
    private static final String ARG_FILENAME = "ARG_FILENAME";
    private String mFileName;
    private static final String ARG_SAVEDIR = "ARG_SAVEDIR";
    private String mDir;

    public ApkUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownLoadUrl = intent.getStringExtra(ARG_DOWNLOADURL);
        mFileName = intent.getStringExtra(ARG_FILENAME);
        mDir = intent.getStringExtra(ARG_SAVEDIR);
        return super.onStartCommand(intent, flags, startId);
    }


    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    public static void startService(Context context, String downLoadUrl, String dir, String fileName) {
        Intent intent = new Intent(context, ApkUpdateService.class);
        intent.putExtra(ARG_DOWNLOADURL, downLoadUrl);
        intent.putExtra(ARG_SAVEDIR, dir);
        intent.putExtra(ARG_FILENAME, fileName);
        context.startService(intent);
    }


}
