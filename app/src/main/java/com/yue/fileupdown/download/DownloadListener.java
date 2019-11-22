package com.yue.fileupdown.download;

/**
 * 进度监听
 */
public interface DownloadListener {

    /**
     * 下载取消
     *
     * @param key
     */
    void canle(String key, String filePath);

    /**
     * 下载失败
     */
    void failure(String key, String filePath);

    /**
     * 下载完成
     */
    void success(String key, String filePath);

    /**
     * 进度
     *
     * @param key
     * @param progress
     * @param downloadedLength 已下载长度
     * @param contentLength    文件总长度
     * @param speed            下载速度
     */
    void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed);

    /**
     * 异常
     */
    void error(String key, String filePath, Exception e);
}
