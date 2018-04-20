package com.yue.fileupdown.ui.download;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yue.fileupdown.R;
import com.yue.fileupdown.databinding.ActivityTdownLoadBinding;

/**
 * time：2018/4/20 15:24
 * author：shimy
 * classname：TDownLoadActivity
 * description：简单测试下下载
 */

public class TDownLoadActivity extends AppCompatActivity {

    private ActivityTdownLoadBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tdown_load);
    }




}
