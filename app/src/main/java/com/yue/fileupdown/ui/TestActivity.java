package com.yue.fileupdown.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yue.fileupdown.R;
import com.yue.fileupdown.databinding.ActivityTestBinding;

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
            if (url.lastIndexOf("/") != -1)
                url = url.substring(0, url.length() - 1);
            sb.append(url);
            if (fileName.indexOf("/", 0) != -1)
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
            }finally {
                mBinding.tvShow.setText("呵呵呵");
            }
        });
    }
}
