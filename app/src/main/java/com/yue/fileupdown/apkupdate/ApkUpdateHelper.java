package com.yue.fileupdown.apkupdate;

import android.content.Context;

import java.io.File;

/**
 * @author shimy
 * @create 2019/11/21 11:33
 * @desc 应用更新辅助类
 */
public class ApkUpdateHelper {
    private Context context;
    private String downLoadUrl;
    private String fileName;
    private String dir;
    private ApkNotificationParams notificationParams;


    /**
     * @param context
     * @param downLoadUrl 下载地址
     * @param fileName    存储文件名
     * @param dir         存储路径
     */
    public ApkUpdateHelper(Context context, String downLoadUrl, String fileName, String dir, String authority) {
        this(context, downLoadUrl, fileName, dir, authority, null);
    }

    /**
     * @param context
     * @param downLoadUrl        下载地址
     * @param fileName           存储文件名
     * @param dir                存储路径
     * @param authority          下载完成时自动跳转到安装页面需要此参数
     * @param notificationParams 通知帮助类
     */
    public ApkUpdateHelper(Context context, String downLoadUrl, String fileName, String dir, String authority, ApkNotificationParams notificationParams) {
        this.context = context;
        this.downLoadUrl = downLoadUrl;
        this.fileName = fileName;
        this.dir = dir;
        this.notificationParams = notificationParams;
        if (this.notificationParams == null) {
            if (dir.endsWith("/"))
                dir = dir.substring(0, (dir.length() - 1));
            if (fileName.startsWith("/"))
                fileName = fileName.substring(1, fileName.length());
            this.notificationParams = new ApkNotificationParams.Builder(context, dir + File.separator + fileName, authority).create();
        }
    }


    public void updateApk() {
        ApkUpdateService.startService(context, downLoadUrl, dir, fileName, notificationParams);
    }
}
