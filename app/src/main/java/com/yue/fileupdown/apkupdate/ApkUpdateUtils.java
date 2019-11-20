package com.yue.fileupdown.apkupdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.yue.fileupdown.MainActivity;
import com.yue.fileupdown.R;
import com.yue.fileupdown.download.DownloadListener;
import com.yue.fileupdown.download.DownloadRangListener;
import com.yue.fileupdown.download.FileDownLoadUtils;
import com.yue.fileupdown.download.MyDownloadException;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * @author shimy
 * @create 2019/11/19 11:06
 * @desc apk更新工具类
 */
public class ApkUpdateUtils {
    private static volatile ApkUpdateUtils Instance = null;
    private static final String TAG = ApkUpdateUtils.class.getSimpleName();
    private OkHttpClient mOkHttpClient;

    private boolean isDowning = false;

    public static ApkUpdateUtils getInstance() {
        ApkUpdateUtils localInstance = Instance;
        if (localInstance == null) {
            synchronized (ApkUpdateUtils.class) {
                localInstance = Instance;
                if (localInstance == null)
                    Instance = localInstance = new ApkUpdateUtils();
            }
        }
        return localInstance;
    }

    /**
     * 支持断点续传
     *
     * @param downloadUrl      下载地址
     * @param directory        存储路径
     * @param fileName         存储的文件名
     * @param downloadListener 下载进度监听
     */
    public void updateApkRang(Context context, final String downloadUrl, final String directory, final String fileName, String key, final DownloadRangListener downloadListener) {
        if (isDowning) {
            Toast.makeText(context, "下载中，请勿重复点击", Toast.LENGTH_SHORT).show();
            return;
        }
        isDowning = true;
        try {
            FileDownLoadUtils.getInstance().downLoadRang(downloadUrl, directory, fileName, key, downloadListener);
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
     * @param downloadListener
     */
    public void updateApk(Context context, final String downloadUrl, final String directory, final String fileName, String key, final DownloadListener downloadListener) {
        if (isDowning) {
            Toast.makeText(context, "下载中，请勿重复点击", Toast.LENGTH_SHORT).show();
            return;
        }
        isDowning = true;
        try {
            FileDownLoadUtils.getInstance().downLoad(downloadUrl, directory, fileName, key, downloadListener);
        } catch (MyDownloadException e) {
            e.printStackTrace();
        }
    }


    /**
     * 跳转应用安装界面
     *
     * @param context
     * @param path      安装包路径
     * @param authority provider
     */
    public void install(Context context, String path, String authority) {
        File file = new File(path);
        Uri apkURI = FileProvider.getUriForFile(context, authority, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {  //判读版本是否在7.0以上
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 返回安装intent 用在页面通知中
     *
     * @param context
     * @param path
     * @param authority
     * @return
     */
    public Intent getInstallIntent(Context context, String path, String authority) {
        File file = new File(path);
        Uri apkURI = FileProvider.getUriForFile(context, authority, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {  //判读版本是否在7.0以上
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        }
        return intent;
    }

    public void notifactionProgress(Context context, int progress, String path, String authority, String channelId, String channelName,
                                    String channelDescription, int smallIcon, Bitmap largeIcon,
                                    String contentTitle, String contextText, int notifactionId, boolean isRetry) {
        NotificationCompat.Action retryAction = null;
        if (isRetry) {
            Intent dismissIntent = new Intent(context, MainActivity.class);
            PendingIntent dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, 0);
            retryAction =
                    new NotificationCompat.Action.Builder(
                            R.drawable.ic_launcher_background,
                            "重试",
                            dismissPendingIntent)
                            .build();
        }
        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        Intent intent = getInstallIntent(context, path, authority);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        progress > 100 ? "下载完成" : progress + "/100"
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                createNotificationChannel(context, channelId, channelName, channelDescription))
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(contentTitle)
                .setContentText(contextText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true);
        if (retryAction != null)
            builder.addAction(retryAction);

        int PROGRESS_MAX = 100;
        builder.setProgress(PROGRESS_MAX, progress, false);
        Notification notificationCompat = builder.build();
        /*不可清除*/
        notificationCompat.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManagerCompat.notify(notifactionId, notificationCompat);
    }


    public static String createNotificationChannel(
            Context context, String channelId, String channelName,
            String channelDescription) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //渠道
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            //渠道是否震动 设置此渠道上的通知是否震动！！！
            boolean channelEnableVibrate = false;
            //锁屏显示 设置此渠道上的通知是否锁屏！！！
            int channelLockscreenVisibility =
                    NotificationCompat.VISIBILITY_PUBLIC;

            // 初始化通知渠道
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }

}
