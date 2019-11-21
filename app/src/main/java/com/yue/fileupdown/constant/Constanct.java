package com.yue.fileupdown.constant;

import android.os.Environment;

/**
 * time：2018/4/20 15:25
 * author：shimy
 * classname：Constanct
 * description：存储常量
 */

public class Constanct {

    /*下载源----url*/
    public final static String downLoadUrl = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";

    /*下载位置*/
    public final static String downloadPath = Environment.getExternalStorageDirectory().getPath() + "/aashimydown/";
}
