package com.admin.mymusic.network;


import android.content.Context;
import android.util.Log;

import com.admin.mymusic.network.mvp.NetWorkCallback;
import com.admin.mymusic.utils.FileUtil;
import com.admin.mymusic.utils.MediaUtil;
import com.admin.mymusic.utils.Setting;
import com.admin.mymusic.utils.SpUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/30 22:35
 **/
public class DownLoad {


    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient()
                .newBuilder()
                .connectTimeout(9_000, TimeUnit.MILLISECONDS)//连接超时
                .build();
    }

    /**
     * 获取网址连接
     *
     * @param params
     * @return
     */
    public static long getContentLength(String params) {
        long contentLength = 0;
        try {
            Response execute = okGet(params).execute();
            contentLength = execute.body().contentLength();
            execute.close();
        } catch (IOException e) {
            contentLength = 0;
        }
        return contentLength;
    }

    /***************************************************************************************************************************************************************************************************************************************************************************
     *                                                                                         对外Aip
     ***************************************************************************************************************************************************************************************************************************************************************************/

    public static Headers getHeaders(String csrf) {
        //Host: www.kuwo.cn
        return new Headers.Builder()
                .add("Host", "www.kuwo.cn")
                .add("csrf", csrf)
                .add("Cookie", "kw_token=" + csrf)
                .build();
    }

    /**
     * 获取 ok的Call方法
     *
     * @param params
     * @return
     */
    public static Call okGet(String params) {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(params)
                .build();
        return client.newCall(request);
    }

    /**
     * @param params
     * @param headers
     * @return
     */
    public static Call okGet(String params, Headers headers) {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(params)
                .build();
        return client.newCall(request);
    }

    /**
     * 获取 csrf
     */
    public static void getCsrf() {
        Call call = okGet("http://www.kuwo.cn/");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("数据监控", "Cookie更新失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String csrf = "";//kw_token=05I9BRSLW2EM;
                List<String> headers = response.headers("Set-Cookie");
                String[] str = headers.get(0).split(";");
                csrf = str[0].replace("kw_token=", "");
                Setting.set().setCsrf(csrf);//添加全局csrf
                response.close();
                Log.i("数据监控", "Cookie更新了---》" + csrf);
                //MediaUtil.getMedia().start();
            }
        });
    }

    /***
     *音乐下载保存
     * @param context 上下文对象
     * @param params 连接
     * @param filePath 保存的文件路径（含名称）
     */
    public static void downMusic(Context context, String params, String filePath) {
        long contentLength = getContentLength(params);//获取网络文件大小，int 超过2G报错，所以用long
        long fileLength = 0;//保存本地文件大小(假如存在)
        File file = new File(filePath);
        if (file.isFile()) {
            fileLength = file.length();//获取本地文件大小
        }
        if(contentLength==0||contentLength <fileLength){
            return;
        }
        if(contentLength==fileLength){
            FileUtil.deleteRecord(context, filePath);//删除缓存文件
            return;
        }
        InputStream inputStream = null;
        RandomAccessFile accessFile = null;
        try {
            Headers headers = new Headers.Builder()
                    .add("RANGE", "bytes=" + fileLength + "-").build();
            inputStream = okGet(params, headers).execute().body().byteStream();
            accessFile = new RandomAccessFile(filePath, "rw");//
            accessFile.seek(fileLength);//跳过已下载的部分
            FileUtil.saveRecord(context, filePath);
            int len = 0;
            byte[] buffer = new byte[1024 * 8];//缓冲区
            while ((len = inputStream.read(buffer)) != -1) {
                accessFile.write(buffer, 0, len);//写入文件
            }
            FileUtil.deleteRecord(context, filePath);//删除缓存文件
        } catch (IOException e) {
            e.printStackTrace();
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

}
