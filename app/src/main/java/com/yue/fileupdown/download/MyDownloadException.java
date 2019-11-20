package com.yue.fileupdown.download;

/**
 * @author shimy
 * @create 2019/11/20 9:33
 * @desc 下载异常
 */
public class MyDownloadException extends Exception {

    public enum Code {
        UNKNOWN,//未知异常
        REPEAT,//下载重复异常 重复下载某一个key
        NOTHREAD,//无此线程异常 出现在暂停 恢复 取消下载的情况
    }

    private Code code = Code.UNKNOWN;

    public MyDownloadException(String message, Code code) {
        super(message);
        this.code = code;
    }

    public Code code() {
        return code;
    }
}
