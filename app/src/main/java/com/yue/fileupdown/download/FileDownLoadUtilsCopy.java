package com.yue.fileupdown.download;

import com.yue.fileupdown.constant.Constanct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author shimy
 * @create 2019/11/19 13:17
 * @desc 文件下载
 */
public class FileDownLoadUtilsCopy {


    private static volatile FileDownLoadUtilsCopy Instance = null;
    private static final String TAG = FileDownLoadUtilsCopy.class.getSimpleName();
    private OkHttpClient client;

//    private boolean isCanceled = false;
//    private boolean isPaused = false;

    public static FileDownLoadUtilsCopy getInstance() {
        FileDownLoadUtilsCopy localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileDownLoadUtilsCopy.class) {
                localInstance = Instance;
                if (localInstance == null)
                    Instance = localInstance = new FileDownLoadUtilsCopy();
            }
        }
        return localInstance;
    }

    private FileDownLoadUtilsCopy() {
        client = new OkHttpClient().newBuilder()
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
     * @param downloadListener 监听接口
     */
    public void downLoadRang(final String downloadUrl, final String directory, final String fileName, final DownloadRangListener downloadListener) {
        new Thread() {
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
                    file = new File(directory + File.separator + fileName);
                    if (file.exists()) {
                        downloadedLength = file.length();
                    }
                    long contentLength = getContentLength(downloadUrl);
                    if (contentLength == 0) {
                        downloadListener.error(new Exception("contentLength==0"));
                        return;
                    } else if (contentLength == downloadedLength) {
                        // 已下载字节和文件总字节相等，说明已经下载完成了
                        downloadListener.success();
                        return;
                    }

                    Request request = new Request.Builder()
                            // 断点下载，指定从哪个字节开始下载
                            .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                            .url(downloadUrl)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response != null) {
                        is = response.body().byteStream();//。响应体中的流通管道
                        savedFile = new RandomAccessFile(file, "rw");
                        savedFile.seek(downloadedLength); // 跳过已下载的字节
                        byte[] b = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is.read(b)) != -1) {
//                            if (isCanceled) {
//                                downloadListener.canle();
//                                return;
//                            } else if (isPaused) {
//                                downloadListener.pause();
//                                return;
//                            } else {
                            total += len;
                            savedFile.write(b, 0, len);
                            // 计算已下载的百分比
                            int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                            downloadListener.progress(progress);
//                            }
                        }
                        response.body().close();
                        downloadListener.success();
                        return;
                    }
                } catch (Exception e) {
                    downloadListener.error(e);
                    return;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (savedFile != null) {
                            savedFile.close();
                        }
//                        if (isCanceled && file != null) {
//                            file.delete();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                downloadListener.failure();
            }
        }.start();
    }

    /**
     * 普通下载
     *
     * @param downloadUrl
     * @param directory
     * @param fileName
     * @param downloadListener
     */
    public void downLoad(final String downloadUrl, final String directory, final String fileName, final DownloadRangListener downloadListener) {
        new Thread() {
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
                    file = new File(directory + File.separator + fileName);
                    if (file.exists()) {
                        //删除重新下载
                        downloadListener.error(new Exception("文件已存在"));
                        return;
                    }

                    //文件总长度
                    final long contentLength = getContentLength(Constanct.downLoadUrl);
                    /*url请求拿到下载数据*/
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 断点下载，指定从哪个字节开始下载
//                        .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                            .url(Constanct.downLoadUrl)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response != null) {
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
                            downloadListener.progress(progress);
                        }
                        response.body().close();
                        downloadListener.success();
                        return;
                    }

                } catch (IOException e) {
                    downloadListener.error(e);
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
                downloadListener.failure();
            }
        }.start();
    }

//    public void pauseDownload() {
//        isPaused = true;
//    }
//
//    public void cancelDownload() {
//        isCanceled = true;
//    }

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
