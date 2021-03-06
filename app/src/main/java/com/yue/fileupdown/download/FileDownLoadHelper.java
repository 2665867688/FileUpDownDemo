package com.yue.fileupdown.download;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author shimy
 * @create 2019/11/19 13:17
 * @desc 文件下载 此工具类不处理将下载过程保存在数据库以持久化，app退出 下载全部结束，要实现数据库持久化，请在自己的app下实现
 */
public class FileDownLoadHelper {


    private static volatile FileDownLoadHelper Instance = null;
    private static final String TAG = FileDownLoadHelper.class.getSimpleName();
    private OkHttpClient client;
    private HashMap<String, IDownLoadThread> mHashThreadManager = new HashMap<>();


    public static FileDownLoadHelper getInstance() {
        FileDownLoadHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileDownLoadHelper.class) {
                localInstance = Instance;
                if (localInstance == null)
                    Instance = localInstance = new FileDownLoadHelper();
            }
        }
        return localInstance;
    }

    private FileDownLoadHelper() {
        client = new OkHttpClient().newBuilder()
//                .readTimeout(30000, TimeUnit.MILLISECONDS)
//                .connectTimeout(30000, TimeUnit.MILLISECONDS)
//                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
    }

    public OkHttpClient getClient() {
        return client;
    }

    /**
     * 断点续传下载方法
     *
     * @param downloadUrl      下载url
     * @param directory        文件保存路径
     * @param fileName         保存文件名
     * @param key              某次下载的key 用于下载的暂停 开始 取消操作
     * @param downloadListener 监听接口
     */
    public void downLoadRang(String key, String downloadUrl, String directory, String fileName, DownloadRangListener downloadListener) throws MyDownloadException {
        if (mHashThreadManager.containsKey(key) && mHashThreadManager.get(key) != null) {
            throw new MyDownloadException("此key下载已存在，下载重复，请确认你的下载是否正在进行中！！！", MyDownloadException.Code.REPEAT);
        }
        if (directory.endsWith("/"))
            directory = directory.substring(0, (directory.length() - 1));

        if (fileName.startsWith("/"))
            fileName = fileName.substring(1, fileName.length());

        DownLoadRangThread thread = new DownLoadRangThread(key, downloadUrl, directory, fileName, new DownloadRangListener() {
            @Override
            public void existed(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.existed(key, filePath);
            }

            @Override
            public void pause(String key, String filePath) {
                downloadListener.pause(key, filePath);
            }

            @Override
            public void canle(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.canle(key, filePath);
            }

            @Override
            public void failure(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.failure(key, filePath);
            }

            @Override
            public void success(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.success(key, filePath);
            }

            @Override
            public void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed) {
                downloadListener.progress(key, filePath, progress, downloadedLength, contentLength, speed);
            }

            @Override
            public void error(String key, String filePath, Exception e) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.error(key, filePath, e);
            }

            @Override
            public void responseError(String key, String filePath, Response response) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.responseError(key, filePath, response);
            }


        });
        mHashThreadManager.put(key, thread);
        thread.start();
    }

    /**
     * 普通下载
     *
     * @param downloadUrl      下载url
     * @param directory        文件保存路径
     * @param fileName         保存文件名
     * @param key              某次下载的key 用于下载的暂停 开始 取消操作
     * @param downloadListener 监听接口
     */
    public void downLoad(String key, String downloadUrl, String directory, String fileName, DownloadListener downloadListener) throws MyDownloadException {
        if (mHashThreadManager.containsKey(key) && mHashThreadManager.get(key) != null) {
            throw new MyDownloadException("此key下载已存在，下载重复，请确认你的下载是否正在进行中！！！", MyDownloadException.Code.REPEAT);
        }
        if (directory.endsWith("/"))
            directory = directory.substring(0, (directory.length() - 1));

        if (fileName.startsWith("/"))
            fileName = fileName.substring(1, fileName.length());
        DownLoadThread thread = new DownLoadThread(downloadUrl, directory, fileName, key, new DownloadListener() {
            @Override
            public void canle(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.canle(key, filePath);
            }

            @Override
            public void failure(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.failure(key, filePath);
            }

            @Override
            public void success(String key, String filePath) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.success(key, filePath);
            }

            @Override
            public void progress(String key, String filePath, int progress, long downloadedLength, long contentLength, double speed) {
                downloadListener.progress(key, filePath, progress, downloadedLength, contentLength, speed);
            }


            @Override
            public void error(String key, String filePath, Exception e) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.error(key, filePath, e);
            }

            @Override
            public void responseError(String key, String filePath, Response response) {
                if (mHashThreadManager.containsKey(key))
                    mHashThreadManager.remove(key);
                downloadListener.responseError(key, filePath, response);
            }


        });
        mHashThreadManager.put(key, thread);
        thread.start();
    }


    /**
     * 恢复下载 与pause相对应
     */
    public void resumeDownload(String key) throws MyDownloadException {
        if (mHashThreadManager.containsKey(key)) {
            if (mHashThreadManager.get(key) != null) {
                IDownLoadThread thread = mHashThreadManager.get(key);
                thread.resumeDownload();
            } else {
                throw new MyDownloadException("线程查找失败，管理器中没有此线程的运行01", MyDownloadException.Code.NOTHREAD);
            }
        } else {
            throw new MyDownloadException("线程查找失败，管理器中没有此线程的运行02", MyDownloadException.Code.NOTHREAD);
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload(String key) throws MyDownloadException {
        if (mHashThreadManager.containsKey(key)) {
            if (mHashThreadManager.get(key) != null) {
                IDownLoadThread thread = mHashThreadManager.get(key);
                thread.pauseDownload();
            } else {
                throw new MyDownloadException("线程查找失败，管理器中没有此线程的运行01", MyDownloadException.Code.NOTHREAD);
            }
        } else {
            throw new MyDownloadException("线程查找失败，管理器中没有此线程的运行02", MyDownloadException.Code.NOTHREAD);
        }
    }

    /**
     * 取消下载会删除掉源文件
     */
    public void cancelDownload(String key) throws MyDownloadException {
        if (mHashThreadManager.containsKey(key)) {
            if (mHashThreadManager.get(key) != null) {
                IDownLoadThread thread = mHashThreadManager.get(key);
                thread.cancelDownload();
                mHashThreadManager.remove(key);
            } else {
                throw new MyDownloadException("下载线程查找失败，管理器中没有此线程的运行01", MyDownloadException.Code.NOTHREAD);
            }
        } else {
            throw new MyDownloadException("下载线程查找失败，管理器中没有此线程的运行02", MyDownloadException.Code.NOTHREAD);
        }
    }

    private long getContentLength(String downloadUrl) throws IOException {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }


}
