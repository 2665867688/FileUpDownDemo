package com.yue.fileupdown.ui.download;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yue.fileupdown.R;
import com.yue.fileupdown.databinding.ActivityTdownLoadBinding;

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

}
