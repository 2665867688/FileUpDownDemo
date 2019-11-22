package com.yue.fileupdown.apkupdate;

import java.util.Observable;

/**
 * @author shimy
 * @create 2019/11/22 13:48
 * @desc apk 更新被观察者 向此类注册观察者可监听更新过程
 */
public class ApkUpdateObserver extends Observable {

    public enum Type {
        EXISTED,
        PAUSE,
        CANLE,
        FAILURE,
        SUCCESS,
        PROGRESS,
        ERROR
    }


    private volatile static ApkUpdateObserver instance;


    public static ApkUpdateObserver getInstance() {
        if (instance == null) {
            synchronized (ApkUpdateObserver.class) {
                if (instance == null) {
                    instance = new ApkUpdateObserver();
                }
            }
        }
        return instance;
    }


    void update(ApkUpdateEvent event) {
        setChanged();
        notifyObservers(event);
    }


    public static class ApkUpdateEvent {
        private Type type;
        private String filePath;
        private String threadKey;
        /*progress 参数*/
        private int progress;
        private long downloadedLength;
        private long contentLength;

        /*错误情况参数*/
        private Exception exception;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getThreadKey() {
            return threadKey;
        }

        public void setThreadKey(String threadKey) {
            this.threadKey = threadKey;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public long getDownloadedLength() {
            return downloadedLength;
        }

        public void setDownloadedLength(long downloadedLength) {
            this.downloadedLength = downloadedLength;
        }

        public long getContentLength() {
            return contentLength;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }


}
