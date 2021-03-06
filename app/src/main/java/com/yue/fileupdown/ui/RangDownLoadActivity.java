package com.yue.fileupdown.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yue.fileupdown.R;
import com.yue.fileupdown.apkupdate.ApkUpdateHelper;
import com.yue.fileupdown.apkupdate.ApkUpdateObserver;
import com.yue.fileupdown.constant.Constanct;
import com.yue.fileupdown.databinding.ActivityRangDownLoadBinding;
import com.yue.fileupdown.download.DownloadRangListener;
import com.yue.fileupdown.download.FileDownLoadHelper;
import com.yue.fileupdown.download.MyDownloadException;

import java.util.Observable;
import java.util.Observer;

import okhttp3.Response;

/**
 * @author shimy
 * @create 2019/11/19 14:09
 * @desc 断点下载
 */
public class RangDownLoadActivity extends AppCompatActivity {

    private ActivityRangDownLoadBinding mBinding;
    private String url = "https://app-10034140.cos.ap-shanghai.myqcloud.com/app/pad-1.4.0.apk";
    //    private String url = "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk";
//    private String fileName = "/alipay_wap_main.apk";
    private String fileName = "pad-1.4.0.apk";
    private String path = Constanct.downloadPath;

    private ApkUpdateHelper apkUpdateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rang_down_load);
        apkUpdateHelper = new ApkUpdateHelper(this, url, fileName, path, "com.yue.fileupdown.fileprovider");
        initView();
    }

    private void initView() {
        mBinding.btnDown.setOnClickListener((v) -> {
            try {
                FileDownLoadHelper.getInstance().downLoadRang(url, path, fileName, "hello", new DownloadRangListener() {

                    @Override
                    public void existed(String key, String filePath) {
                        runOnUiThread(() -> {
                            Toast.makeText(RangDownLoadActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
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
                        runOnUiThread(() -> {
                            mBinding.tvShow.setText("下载失败");
                        });

                    }

                    @Override
                    public void success(String key, String filePath) {
                        runOnUiThread(() -> {
                            mBinding.tvShow.setText("下载完成");
                        });

                    }

                    @Override
                    public void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed) {
                        runOnUiThread(() -> {
                            mBinding.tvShow.setText(progress + "/100");
                        });
                    }


                    @Override
                    public void error(String key, String filePath, Exception e) {
                        runOnUiThread(() -> {
                            mBinding.tvShow.setText("下载异常：" + e.getMessage());
                        });

                    }

                    @Override
                    public void responseError(String key, String filePath, Response response) {
                        runOnUiThread(() -> {
                            mBinding.tvShow.setText("下载异常：" + response.code());
                        });
                    }


                });
            } catch (MyDownloadException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    switch (e.code()) {
                        case REPEAT:
                            Toast.makeText(this, "重复下载", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
            }
        });
        mBinding.btnDown2.setOnClickListener(v -> {
            apkUpdateHelper.updateApk();
//            Intent intent = new Intent(this, NotificationService.class);
//            startService(intent);
        });
        ApkUpdateObserver.getInstance().addObserver((o, arg) -> {
            if (arg != null && arg instanceof ApkUpdateObserver.ApkUpdateEvent) {
                ApkUpdateObserver.ApkUpdateEvent event = (ApkUpdateObserver.ApkUpdateEvent) arg;
                runOnUiThread(() -> {
                    switch (event.getType()) {
                        case SUCCESS:
                            mBinding.tvShow.setText("下载成功");
                            break;
                        case PROGRESS:
                            mBinding.tvShow.setText(event.getProgress() + "");
                            break;
                        case ERROR:

                            break;
                        case CANLE:
                            break;
                        case PAUSE:
                            break;
                        case EXISTED:
                            mBinding.tvShow.setText("文件已存在");
                            break;
                        case FAILURE:
                            break;
                    }
                });

            }

        });
    }

}
