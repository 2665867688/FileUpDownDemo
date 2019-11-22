package com.yue.fileupdown;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
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
import com.yue.fileupdown.ui.TestActivity;
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
        checkPermission();
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
        mList.add(new MainItem("测试", "测试", TestActivity.class));
        mAdapter.notifyDataSetChanged();
    }

    OnRcyItemClickListener onRcyItemClickListener = new OnRcyItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            MainItem item = mList.get(position);
            startActivity(new Intent(MainActivity.this, item.getaClass()));
        }


    };


    //////////////////////////////////    动态权限申请   ////////////////////////////////////////
    private final static int REQ_PERMISSION_CODE = 0x1000;

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();

            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        (String[]) permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        Toast.makeText(this, "用户没有允许需要的权限，使用可能会受到限制！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
