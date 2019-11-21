package com.yue.fileupdown.download;

/**
 * 进度监听 断点续传
 */
public interface DownloadRangListener {

    /**
     * 文件已存在
     */
    void existed(String key);

    /**
     * 下载暂停
     */
    void pause(String key);

    /**
     * 下载取消
     */
    void canle(String key);

    /**
     * 下载失败
     */
    void failure(String key);

    /**
     * 下载完成
     */
    void success(String key);

    /**
     * 进度
     *
     * @param key
     * @param progress
     * @param downloadedLength 已下载长度 单位：字节
     * @param contentLength    文件总长度 单位：字节 1kb=1024byte 1m=1024kb
     * @param speed            下载速度
     */
    void progress(String key, int progress, long downloadedLength, long contentLength, double speed);

    /**
     * 异常
     */
    void error(String key, Exception e);


}
