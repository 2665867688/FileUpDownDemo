package com.yue.fileupdown.apkupdate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

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
    public void updateApkRang(Context context, final String downloadUrl, final String directory, final String fileName,String key, final DownloadRangListener downloadListener) {
        if (isDowning) {
            Toast.makeText(context, "下载中，请勿重复点击", Toast.LENGTH_SHORT).show();
            return;
        }
        isDowning = true;
        try {
            FileDownLoadUtils.getInstance().downLoadRang(downloadUrl, directory, fileName, key,downloadListener);
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
    public void updateApk(Context context, final String downloadUrl, final String directory, final String fileName, String key,final DownloadListener downloadListener) {
        if (isDowning) {
            Toast.makeText(context, "下载中，请勿重复点击", Toast.LENGTH_SHORT).show();
            return;
        }
        isDowning = true;
        try {
            FileDownLoadUtils.getInstance().downLoad(downloadUrl, directory, fileName,key, downloadListener);
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
}
