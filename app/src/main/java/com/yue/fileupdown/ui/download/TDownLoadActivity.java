package com.yue.fileupdown.ui.download;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yue.fileupdown.R;
import com.yue.fileupdown.constant.Constanct;
import com.yue.fileupdown.databinding.ActivityTdownLoadBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * time：2018/4/20 15:24
 * author：shimy
 * classname：TDownLoadActivity
 * description：简单测试下下载
 */

public class TDownLoadActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityTdownLoadBinding mBinding;

    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tdown_load);
        requestPermissions();
        mBinding.btnTdlStart.setOnClickListener(this);
        mBinding.btnTdlCancle.setOnClickListener(this);
        mThread = new Thread(downRun);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tdl_start:
                startDl();
                break;
            case R.id.btn_tdl_cancle:
                cancleDl();
                break;
        }
    }

    /**
     * 开始下载
     */
    private void startDl() {
        mThread.start();
    }

    /**
     * 取消下载 删除文件
     */
    private void cancleDl() {
        if (!mThread.isInterrupted())
            mThread.interrupt();
    }

    Runnable downRun = new Runnable() {
        @Override
        public void run() {
            File file = null;
            InputStream is = null;
            FileOutputStream fileOutputStream = null;//写入到文件
            try {
//                long downloadedLength = 0;      // 记录已下载的文件长度
                String fileName = Constanct.downLoadUrl.substring(Constanct.downLoadUrl.lastIndexOf("/"));
                File directory = new File(Constanct.downloadPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                file = new File(Constanct.downloadPath + fileName);
                if (!file.exists()) {
                    //删除重新下载
                    file.delete();
                }

                //文件总长度
                final long contentLength = getContentLength(Constanct.downLoadUrl);

                /*url请求拿到下载数据*/
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        // 断点下载，指定从哪个字节开始下载
//                        .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                        .url(Constanct.downLoadUrl)
                        .build();
                Response response = client.newCall(request).execute();
                if (response != null) {
                    if (fileOutputStream == null) {
                        fileOutputStream = new FileOutputStream(file);
                    }
                    /*拿到响应体数据流*/
                    is = response.body().byteStream();
                    byte[] b = new byte[1024];
                    int total = 0;
                    int len;
                    /*将数据写入文件*/
                    while ((len = is.read(b)) != -1) {
                        total += len;
                        fileOutputStream.write(b, 0, len);
                        final int progress = (int) ((total * 100) / contentLength);
                        final int finalTotal = total;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.progressTdl.setProgress(progress);
                                mBinding.tvTdlProgress.setText("progress:" + progress + "  \ntotal:" + finalTotal + " \ncontentLength:" + contentLength);
                            }
                        });
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    };


    /**
     * 得到文件的大小 根据url
     *
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                               @Override
                               public void accept(Permission permission) throws Exception {
                                   if (permission.granted) {
                                       // 所有权限都已授权
//                                       Toast.makeText(TSimpleMapActivity.this, "权限请求成功", Toast.LENGTH_SHORT).show();
                                   } else if (permission.shouldShowRequestPermissionRationale) {
                                       // 至少有一个权限未被授予 没有选中 [不在询问按钮]
                                       Toast.makeText(TDownLoadActivity.this, "有权限未申请成功" + permission.name, Toast.LENGTH_SHORT).show();
                                       if (permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION) || permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                           Toast.makeText(TDownLoadActivity.this, "定位权限未被授予", Toast.LENGTH_SHORT).show();
                                       } else if (permission.name.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                           Toast.makeText(TDownLoadActivity.this, "存储权限未被授予", Toast.LENGTH_SHORT).show();
                                       }
                                   } else {
                                       // 至少有一个权限未被授予 选中了[不在询问按钮]
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable t) {
                                   Toast.makeText(TDownLoadActivity.this, "权限请求错误", Toast.LENGTH_SHORT).show();
                               }
                           },
                        new Action() {
                            @Override
                            public void run() {
//                                Toast.makeText(TSimpleMapActivity.this, "权限请求完成", Toast.LENGTH_SHORT).show();
                            }
                        });
    }


}
