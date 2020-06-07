package com.admin.mymusic.network;

import android.content.Context;
import android.os.AsyncTask;

import com.admin.mymusic.network.mvp.NetWorkCallback;
import com.admin.mymusic.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Headers;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/8 20:26
 **/
public class DownloadTask extends AsyncTask<String, Double, Integer> {

    public static final int TYPE_SUCCESS = 0;//成功

    public static final int TYPE_FAILED = 1;//错误

    public static final int TYPE_PAUSED = 2;//暂停

    public static final int TYPE_CANCELED = 3;//取消

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private DownloadListener listener;

    private Context context;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String params = strings[0];
        String filePath = strings[1];
        long contentLength = DownLoad.getContentLength(params);//获取网络文件大小，int 超过2G报错，所以用long
        long fileLength = 0;//保存本地文件大小(假如存在)
        File file = new File(filePath);
        if (file.isFile()) {
            fileLength = file.length();//获取本地文件大小
        }
        if (contentLength == 0 || contentLength < fileLength) {
            return TYPE_FAILED;
        }

        if (contentLength == fileLength) {
            publishProgress(100D);
            return TYPE_SUCCESS;
        } else {
            publishProgress((fileLength / (double) contentLength) * 100);
        }
        InputStream inputStream = null;
        RandomAccessFile accessFile = null;
        try {
            Headers headers = new Headers.Builder()
                    .add("RANGE", "bytes=" + fileLength + "-").build();
            inputStream = DownLoad.okGet(params, headers).execute().body().byteStream();//获取下载
            accessFile = new RandomAccessFile(filePath, "rw");//
            accessFile.seek(fileLength);//跳过已下载的部分
            FileUtil.saveRecord(context, filePath);
            int len = 0;
            double progress = 0;//进度
            double oldprogress = 0;//旧进度
            byte[] buffer = new byte[1024 * 8];//缓冲区
            while ((len = inputStream.read(buffer)) != -1) {
                if (isCanceled) {
                    return TYPE_CANCELED;//取消了下载
                } else if (isPaused) {
                    return TYPE_PAUSED;//暂停下载
                } else {
                    accessFile.write(buffer, 0, len);//写入文件
                    fileLength += len;
                    progress = (fileLength / (double) contentLength) * 100;//计算百分比进度
                    if (progress - oldprogress > 0.01) {//进度添加 0.01就发送
                        publishProgress(progress);//替换
                        oldprogress = progress;
                    }
                }
            }

            FileUtil.deleteRecord(context, filePath);//删除缓存文件
            publishProgress(100D);//进度100%
            return TYPE_SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
            return TYPE_FAILED;//错误返回
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();//关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();//关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
        if (listener != null) {
            listener.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (listener != null)
            switch (status) {
                case TYPE_SUCCESS:
                    listener.onSuccess();
                    break;
                case TYPE_FAILED:
                    listener.onFailed();
                    break;
                case TYPE_PAUSED:
                    listener.onPaused();
                    break;
                case TYPE_CANCELED:
                    listener.onCanceled();
                    break;
                default:
                    break;
            }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }
}
