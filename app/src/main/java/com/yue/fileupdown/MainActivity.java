package com.yue.fileupdown;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yue.fileupdown.adapter.MainAdapter;
import com.yue.fileupdown.bean.MainItem;
import com.yue.fileupdown.databinding.ActivityMainBinding;
import com.yue.fileupdown.listeners.OnRcyItemClickListener;
import com.yue.fileupdown.ui.RangDownLoadActivity;
import com.yue.fileupdown.ui.download.TDownLoadActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * time：2018/4/20 10:59
 * author：shimy
 * classname：MainActivity
 * description：文件上传下载
 */


public class MainActivity extends AppCompatActivity {

    private List<MainItem> mList;
    private MainAdapter mAdapter;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        requestPermissions();
        mList = new ArrayList<>();
        mAdapter = new MainAdapter(this, mList);

        mBinding.rcyMain.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rcyMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.rcyMain.setAdapter(mAdapter);
        mAdapter.setOnRcyItemClickListener(onRcyItemClickListener);
        initData();
    }

    private void initData() {
        mList.add(new MainItem("测试简单下载", "测试简单下载", TDownLoadActivity.class));
        mList.add(new MainItem("断点下载", "断点下载", RangDownLoadActivity.class));
        mAdapter.notifyDataSetChanged();
    }

    OnRcyItemClickListener onRcyItemClickListener = new OnRcyItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            MainItem item = mList.get(position);
            startActivity(new Intent(MainActivity.this, item.getaClass()));
        }


    };


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
                                       Toast.makeText(MainActivity.this, "有权限未申请成功" + permission.name, Toast.LENGTH_SHORT).show();
                                       if (permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION) || permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                           Toast.makeText(MainActivity.this, "定位权限未被授予", Toast.LENGTH_SHORT).show();
                                       } else if (permission.name.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                           Toast.makeText(MainActivity.this, "存储权限未被授予", Toast.LENGTH_SHORT).show();
                                       }
                                   } else {
                                       // 至少有一个权限未被授予 选中了[不在询问按钮]
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable t) {
                                   Toast.makeText(MainActivity.this, "权限请求错误", Toast.LENGTH_SHORT).show();
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
