package com.yue.fileupdown.download;

/**
 * @author shimy
 * @create 2019/11/20 9:18
 * @desc 下载类必须继承的接口方法
 */
public interface IDownLoadThread {


    /**
     * 恢复下载 与pause相 对应 取决于实现
     */
    void resumeDownload();

    /**
     * 暂停下载 取决于实现
     */
    void pauseDownload();

    /**
     * 取消下载会删除掉源文件 取决于实现
     */
    void cancelDownload();
}
