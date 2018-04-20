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

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * time：2018/4/20 15:24
 * author：shimy
 * classname：TDownLoadActivity
 * description：简单测试下下载
 */

public class TDownLoadActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityTdownLoadBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tdown_load);
        requestPermissions();
        mBinding.btnTdlStart.setOnClickListener(this);
        mBinding.btnTdlCancle.setOnClickListener(this);
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
    }

    /**
     * 取消下载 删除文件
     */
    private void cancleDl() {

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
