package com.yue.fileupdown;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.yue.fileupdown.adapter.MainAdapter;
import com.yue.fileupdown.bean.MainItem;
import com.yue.fileupdown.databinding.ActivityMainBinding;

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
        initData();
    }

    private void initData() {
        mList.add(new MainItem("测试1", "测试1", null));
        mList.add(new MainItem("测试1", "测试1", null));
        mList.add(new MainItem("测试1", "测试1", null));
        mAdapter.notifyDataSetChanged();
    }
}
