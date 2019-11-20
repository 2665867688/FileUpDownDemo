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
     */
    void progress(String key, int progress);

    /**
     * 异常
     */
    void error(String key, Exception e);
}
