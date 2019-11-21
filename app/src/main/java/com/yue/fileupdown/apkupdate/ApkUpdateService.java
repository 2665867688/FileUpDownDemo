package com.yue.fileupdown.apkupdate;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
        if (mDir.endsWith("/"))
            mDir = mDir.substring(0, (mDir.length() - 1));
        if (mFileName.startsWith("/"))
            mFileName = mFileName.substring(1, mFileName.length());
        mNotificationHelper = new ApkNotificationHelper(this, mNotificationParams);
        updateApkRang(this, KEY_APK, mDownLoadUrl, mDir, mFileName);
        mNotificationHelper.notifiactionProgress(0, "", ApkNotificationHelper.ApkNotificationType.PROGRESS);
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
            FileDownLoadUtils.getInstance().downLoadRang(key, downloadUrl, directory, fileName, new DownloadRangListener() {
                private int currentProgress;
                private long contentLengthL;

                @Override
                public void existed(String key) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "文件已存在，点击可安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void pause(String key) {

                }

                @Override
                public void canle(String key) {

                }

                @Override
                public void failure(String key) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载失败", ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
                }

                @Override
                public void success(String key) {
                    mainHandle().post(() -> {
                        currentProgress = 100;
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载成功，点击安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void progress(String key, int progress, long downloadedLength, long contentLength, double speed) {
                    //progress > 100 ? "下载完成" : progress + "/100"
                    currentProgress = progress;
                    contentLengthL = contentLength;
                    String contentText = "";
                    if (contentLength < 1024 * 1024)
                        contentText = downloadedLength / 1024.00 + "kb/" + contentLength / 1024 + "kb";
                    else {
                        if (downloadedLength < 1024 * 1024)
                            contentText = downloadedLength / 1024 + "kb/" + contentLength / (1024 * 1024) + "m";
                        else
                            contentText = downloadedLength / (1024 * 1024) + "m/" + contentLength / (1024 * 1024) + "m";
                    }
                    String finalContentText = contentText;
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(progress, finalContentText, ApkNotificationHelper.ApkNotificationType.PROGRESS);
                    });
                }

                @Override
                public void error(String key, Exception e) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载出错：" + e.getMessage(), ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
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
            FileDownLoadUtils.getInstance().downLoad(key, downloadUrl, directory, fileName, new DownloadListener() {
                private int currentProgress;

                @Override
                public void canle(String key) {

                }

                @Override
                public void failure(String key) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载失败", ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
                }

                @Override
                public void success(String key) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载成功，点击安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void progress(String key, int progress, long downloadedLength, long contentLength, double speed) {
                    currentProgress = progress;
                    String text = ApkUpdateUtils.byteHandle(downloadedLength) + "/" + ApkUpdateUtils.byteHandle(contentLength);
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(progress, text, ApkNotificationHelper.ApkNotificationType.PROGRESS);
                    });
                }

                @Override
                public void error(String key, Exception e) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载出错：" + e.getMessage(), ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
                }
            });
        } catch (MyDownloadException e) {
            e.printStackTrace();
        }
    }

    private Handler mainHandle() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // If we finish marking off of the main thread, we need to
            // actually do it on the main thread to ensure correct ordering.
            Handler mainThread = new Handler(Looper.getMainLooper());
            return mainThread;
        }
        return null;
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
