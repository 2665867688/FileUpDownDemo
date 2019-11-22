package com.yue.fileupdown.apkupdate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.yue.fileupdown.download.DownloadListener;
import com.yue.fileupdown.download.DownloadRangListener;
import com.yue.fileupdown.download.FileDownLoadHelper;
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
    private String tempSuffix = ".temp";

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
            String path = ApkUpdateUtils.slashEndRemove(directory) + File.separator + ApkUpdateUtils.slashStartRemove(fileName);
            String fileNameCopy = fileName;
            File file = new File(path);
            if (file.exists()) {
                mNotificationHelper.notifiactionProgress(100, "文件已存在，点击可安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                return;
            } else {
                fileNameCopy = fileNameCopy + tempSuffix;
            }
            FileDownLoadHelper.getInstance().downLoadRang(key, downloadUrl, directory, fileNameCopy, new DownloadRangListener() {
                private int currentProgress;

                @Override
                public void existed(String key, String filePath) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(100, "文件已存在，点击可安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void pause(String key, String filePath) {

                }

                @Override
                public void canle(String key, String filePath) {

                }

                @Override
                public void failure(String key, String filePath) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载失败", ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
                }

                @Override
                public void success(String key, String filePath) {
                    File file = new File(path + tempSuffix);
                    if (file.exists()) {
                        file.renameTo(new File(path));
                    }
                    mainHandle().post(() -> {
                        currentProgress = 100;
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载成功，点击安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed) {
                    //progress > 100 ? "下载完成" : progress + "/100"
                    currentProgress = progress;
                    String text = ApkUpdateUtils.byteHandle(downloadedLength) + "/" + ApkUpdateUtils.byteHandle(contentLength);
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(progress, text, ApkNotificationHelper.ApkNotificationType.PROGRESS);
                    });
                }

                @Override
                public void error(String key, String filePath, Exception e) {
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
            FileDownLoadHelper.getInstance().downLoad(key, downloadUrl, directory, fileName, new DownloadListener() {
                private int currentProgress;

                @Override
                public void canle(String key, String filePath) {

                }

                @Override
                public void failure(String key, String filePath) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载失败", ApkNotificationHelper.ApkNotificationType.ERROR);
                    });
                }

                @Override
                public void success(String key, String filePath) {
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(currentProgress, "下载成功，点击安装", ApkNotificationHelper.ApkNotificationType.SUCCESS);
                    });
                }

                @Override
                public void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed) {
                    currentProgress = progress;
                    String text = ApkUpdateUtils.byteHandle(downloadedLength) + "/" + ApkUpdateUtils.byteHandle(contentLength);
                    mainHandle().post(() -> {
                        mNotificationHelper.notifiactionProgress(progress, text, ApkNotificationHelper.ApkNotificationType.PROGRESS);
                    });
                }

                @Override
                public void error(String key, String filePath, Exception e) {
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
