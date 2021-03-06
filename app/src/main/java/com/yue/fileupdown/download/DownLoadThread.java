package com.yue.fileupdown.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author shimy
 * @create 2019/11/20 8:29
 * @desc 下载线程 无断点续传 暂时无暂停 恢复 取消下载功能
 */
public class DownLoadThread extends Thread implements IDownLoadThread {

    private DownloadListener downloadListener;
    private OkHttpClient client;
    private String downloadUrl;//下载地址
    private String directory;//文件存放路径
    private String fileName;//下载文件命名
    private String key;//线程key

    /*文件的绝对路径*/
    private String filePath;

    public DownLoadThread(String key, String downloadUrl, String directory, String fileName, DownloadListener downloadListener) {
        super();
        this.downloadUrl = downloadUrl;
        this.downloadListener = downloadListener;
        this.directory = directory;
        this.fileName = fileName;
        this.key = key;
        filePath = FileDownloadUtils.slashEndRemove(directory) + File.separator + FileDownloadUtils.slashStartRemove(fileName);
        client = FileDownLoadHelper.getInstance().getClient();
    }

    @Override
    public void run() {
        super.run();
        File file = null;
        InputStream is = null;
        FileOutputStream fileOutputStream = null;//写入到文件
        try {
            File directoryFile = new File(directory);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }
            file = new File(filePath);
            if (file.exists()) {
                //删除重新下载
                downloadListener.error(key, filePath, new MyDownloadException("文件已存在", MyDownloadException.Code.UNKNOWN));
                return;
            }

            //文件总长度
//            final long contentLength = getContentLength(downloadUrl);
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
            }
            /*url请求拿到下载数据*/
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
//                        .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null&&response.isSuccessful()) {
                if (fileOutputStream == null) {
                    fileOutputStream = new FileOutputStream(file);
                }
                /*拿到响应体数据流*/
                is = response.body().byteStream();
                byte[] b = new byte[1024];
                long total = 0;
                int len;
                /*将数据写入文件*/
                while ((len = is.read(b)) != -1) {
                    total += len;
                    fileOutputStream.write(b, 0, len);
                    final int progress = (int) ((total * 100) / contentLength);
                    downloadListener.progress(key, filePath, progress, total, contentLength, 100);
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
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        downloadListener.failure(key, filePath);
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


    public String getKey() {
        return key;
    }

    @Override
    public void resumeDownload() {

    }

    @Override
    public void pauseDownload() {

    }

    @Override
    public void cancelDownload() {

    }
}
