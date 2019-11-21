package com.yue.fileupdown.apkupdate;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Toast;

import com.yue.fileupdown.constant.Constanct;
import com.yue.fileupdown.download.DownloadListener;
import com.yue.fileupdown.download.DownloadRangListener;
import com.yue.fileupdown.download.FileDownLoadUtils;
import com.yue.fileupdown.download.MyDownloadException;

import java.io.File;

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
    private static final String ARG_NOTIFICATIONPARAMS = "ARG_NOTIFICATIONPARAMS";
    private ApkNotificationParams mNotificationParams;

    private ApkNotificationHelper mNotificationHelper;

    private final String KEY_APK = "apk_update";

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
        mNotificationParams = intent.getParcelableExtra(ARG_NOTIFICATIONPARAMS);
        updateApkRang(this, KEY_APK, mDownLoadUrl, mDir, mFileName);
        if (mDir.lastIndexOf("/") != -1)
            mDir = mDir.substring(0, (mDir.length() - 1));
        if (mFileName.indexOf("/", 0) != -1)
            mFileName = mFileName.substring(1, mFileName.length());
        mNotificationHelper = new ApkNotificationHelper(this, mNotificationParams);
        updateApkRang(this, KEY_APK, mDownLoadUrl, mDir, mFileName);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 支持断点续传
     *
     * @param downloadUrl 下载地址
     * @param directory   存储路径
     * @param fileName    存储的文件名
     */
    public void updateApkRang(Context context, String key, final String downloadUrl, final String directory, final String fileName) {
        try {
            FileDownLoadUtils.getInstance().downLoadRang(downloadUrl, directory, fileName, key, new DownloadRangListener() {
                @Override
                public void existed(String key) {

                }

                @Override
                public void pause(String key) {

                }

                @Override
                public void canle(String key) {

                }

                @Override
                public void failure(String key) {

                }

                @Override
                public void success(String key) {

                }

                @Override
                public void progress(String key, int progress, long downloadedLength, long contentLength, double speed) {

                }

                @Override
                public void error(String key, Exception e) {

                }
            });
        } catch (MyDownloadException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不支持断点续传
     *
     * @param context
     * @param downloadUrl
     * @param directory
     * @param fileName
     */
    public void updateApk(Context context, String key, final String downloadUrl, final String directory, final String fileName) {
        try {
            FileDownLoadUtils.getInstance().downLoad(downloadUrl, directory, fileName, key, new DownloadListener() {
                @Override
                public void canle(String key) {

                }

                @Override
                public void failure(String key) {

                }

                @Override
                public void success(String key) {

                }

                @Override
                public void progress(String key, int progress, long downloadedLength, long contentLength, double speed) {

                }

                @Override
                public void error(String key, Exception e) {

                }
            });
        } catch (MyDownloadException e) {
            e.printStackTrace();
        }
    }


    public static void startService(Context context, String downLoadUrl, String dir, String fileName, ApkNotificationParams params) {
        Intent intent = new Intent(context, ApkUpdateService.class);
        intent.putExtra(ARG_DOWNLOADURL, downLoadUrl);
        intent.putExtra(ARG_SAVEDIR, dir);
        intent.putExtra(ARG_FILENAME, fileName);
        intent.putExtra(ARG_NOTIFICATIONPARAMS, params);
        context.startService(intent);
    }


}
