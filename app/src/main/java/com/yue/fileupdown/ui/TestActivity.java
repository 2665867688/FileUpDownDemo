package com.yue.fileupdown.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yue.fileupdown.R;
import com.yue.fileupdown.apkupdate.ApkUpdateService;
import com.yue.fileupdown.apkupdate.ApkUpdateUtils;
import com.yue.fileupdown.constant.Constanct;
import com.yue.fileupdown.databinding.ActivityTestBinding;

import java.io.File;

/**
 * @author shimy
 * @create 2019/11/20 10:45
 * @desc 测试类
 */
public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding mBinding;

    private String url = "https://www.baidu.com/hello/";
    private String fileName = "/hello.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        initView();
    }

    private void initView() {
        mBinding.btnTest01.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append("url:");
            if (url.endsWith("/"))
                url = url.substring(0, url.length() - 1);
            sb.append(url);
            if (fileName.startsWith("/"))
                fileName = fileName.substring(1, fileName.length());
            sb.append("\nfilename:");
            sb.append(fileName);
            mBinding.tvShow.setText(sb.toString());
        });

        mBinding.btnTest02.setOnClickListener(v -> {

            try {
                mBinding.tvShow.setText("哈哈哈");
                return;
            } catch (Exception e) {
            } finally {
                mBinding.tvShow.setText("呵呵呵");
            }
        });

        mBinding.btnTest03.setOnClickListener(v -> {
//            long num1 = 1024*10;

//            int num1 = 3<<5;
//            mBinding.tvShow.setText(num1+"");
            String fileName = "pad-1.4.0.apk";
            String path = Constanct.downloadPath;
//            ApkUpdateUtils.install(this, Constanct.downloadPath + File.separator +"pad-1.4.0.apk", "com.yue.fileupdown.fileprovider");
            File file = new File(Constanct.downloadPath + File.separator + "pad-1.4.0.apk");
            Uri apkURI = FileProvider.getUriForFile(this, "com.yue.fileupdown.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {  //判读版本是否在7.0以上
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.parse("file://" + Constanct.downloadPath + File.separator + "pad-1.4.0.apk"), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        });
        mBinding.btnTest04.setOnClickListener(v -> {
            notifaction();
        });
    }


    private void notifaction() {
        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//        Intent intent = ApkUpdateUtils.getInstallIntent(this, Constanct.downloadPath + File.separator + "pad-1.4.0.apk", "com.yue.fileupdown.fileprovider");
        File file = new File(Constanct.downloadPath + File.separator + "pad-1.4.0.apk");
        Uri apkURI = FileProvider.getUriForFile(this, "com.yue.fileupdown.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {  //判读版本是否在7.0以上
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + Constanct.downloadPath + File.separator + "pad-1.4.0.apk"), "application/vnd.android.package-archive");
        }
        /*清除栈*/
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /*通知栏点击事件*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                ApkUpdateUtils.createNotificationChannel(this, "test_channle", "test", "test"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("测试")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                /*点击完通知自动消失*/
                .setAutoCancel(true);
        builder.setContentText("测试");
        builder.setContentIntent(pendingIntent);
        mNotificationManagerCompat.notify(1521,builder.build());
    }
}
