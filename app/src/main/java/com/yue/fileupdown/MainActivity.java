package com.yue.fileupdown;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.yue.fileupdown.adapter.MainAdapter;
import com.yue.fileupdown.bean.MainItem;
import com.yue.fileupdown.databinding.ActivityMainBinding;
import com.yue.fileupdown.listeners.OnRcyItemClickListener;
import com.yue.fileupdown.ui.download.TDownLoadActivity;

import java.util.ArrayList;
import java.util.List;

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
        mAdapter.notifyDataSetChanged();
    }

    OnRcyItemClickListener onRcyItemClickListener = new OnRcyItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            MainItem item = mList.get(position);
            startActivity(new Intent(MainActivity.this, item.getaClass()));
        }
    };
}
