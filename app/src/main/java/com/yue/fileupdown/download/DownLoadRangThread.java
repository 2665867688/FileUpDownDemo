package com.yue.fileupdown.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author shimy
 * @create 2019/11/20 8:29
 * @desc 下载线程 断点续传
 */
public class DownLoadRangThread extends Thread implements IDownLoadThread {

    private DownloadRangListener downloadListener;
    private OkHttpClient client;
    private String downloadUrl;//下载地址
    private String directory;//文件存放路径
    private String fileName;//下载文件命名
    private String key;//线程key
    private boolean isCanceled = false;
    private boolean isPaused = false;
    /*文件的绝对路径*/
    private String filePath;

    public DownLoadRangThread(String key, String downloadUrl, String directory, String fileName, DownloadRangListener downloadListener) {
        super();
        this.downloadListener = downloadListener;
        this.downloadUrl = downloadUrl;
        this.directory = directory;
        this.fileName = fileName;
        this.key = key;
        filePath = FileDownloadUtils.slashEndRemove(directory) + File.separator + FileDownloadUtils.slashStartRemove(fileName);
        client = FileDownLoadHelper.getInstance().getClient();
    }

    @Override
    public void run() {
        super.run();
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0; // 记录已下载的文件长度
            File dirctoryFile = new File(directory);
            if (!dirctoryFile.exists())
                dirctoryFile.mkdirs();
            file = new File(filePath);
            if (file.exists()) {
                downloadedLength = file.length();
            }
//            long contentLength = getContentLength(downloadUrl);
            long contentLength = 0;

            Request requestLength = new Request.Builder()
                    .url(downloadUrl)
                    .build();
            Response responseLength = client.newCall(requestLength).execute();
            if (responseLength != null && responseLength.isSuccessful()) {
                contentLength = responseLength.body().contentLength();
                responseLength.close();
            }else {
                downloadListener.responseError(key,filePath,responseLength);
                return;
            }

            if (contentLength == 0) {
                downloadListener.error(key, filePath, new MyDownloadException("contentLength==0", MyDownloadException.Code.UNKNOWN));
                return;
            } else if (contentLength == downloadedLength) {
                // 已下载字节和文件总字节相等，说明已经下载完成了
                downloadListener.existed(key, filePath);
                return;
            }

            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null&&response.isSuccessful()) {
                is = response.body().byteStream();//。响应体中的流通管道
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength); // 跳过已下载的字节
                byte[] b = new byte[1024 << 7];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        downloadListener.canle(key, filePath);
                        return;
                    } else if (isPaused) {
                        downloadListener.pause(key, filePath);
                        wait();
//                        return;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        downloadListener.progress(key, filePath, progress, total + downloadedLength, contentLength, 100);
                    }
                }
                response.body().close();
                downloadListener.success(key, filePath);
                return;
            }else {
                downloadListener.responseError(key,filePath,response);
            }
        } catch (IOException e) {
            downloadListener.error(key, filePath, e);
            return;
        } catch (InterruptedException e) {
            downloadListener.error(key, filePath, e);
            return;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        downloadListener.failure(key, filePath);
    }

    public String getKey() {
        return key;
    }

    /**
     * 恢复下载 与pause相对应
     */
    @Override
    public void resumeDownload() {
        isPaused = false;
        notify();
    }

    /**
     * 暂停下载
     */
    @Override
    public void pauseDownload() {
        isPaused = true;
    }

    /**
     * 取消下载会删除掉源文件
     */
    @Override
    public void cancelDownload() {
        isCanceled = true;
    }

    /**
     * 获取长度 同步请求
     *
     * @param downloadUrl
     * @return
     * @throws IOException
     */
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
