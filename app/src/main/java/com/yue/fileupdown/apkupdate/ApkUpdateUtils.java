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

    private static final String TAG = ApkUpdateUtils.class.getSimpleName();


    /**
     * 跳转应用安装界面
     *
     * @param context
     * @param path      安装包路径
     * @param authority fileprovider
     */
    public static void install(Context context, String path, String authority) {
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
    public static Intent getInstallIntent(Context context, String path, String authority) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {  //判读版本是否在7.0以上
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkURI = FileProvider.getUriForFile(context, authority, file);
            intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        }
        return intent;
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


    public static String byteHandle(long byteLength) {
        String text = "0kb";
        double byteLengthCopy = byteLength;
        if (byteLengthCopy < 0)
            return byteLength + "";
        if (byteLengthCopy < 1024 * 1024) {
            text = String.format("%.2fkb", byteLengthCopy / 1024.00);
        } else {
            text = String.format("%.2fmb", byteLengthCopy / (1024.00 * 1024.00));
        }
        return text;
    }
}
