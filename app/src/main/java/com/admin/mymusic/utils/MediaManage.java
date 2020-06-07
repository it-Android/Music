package com.admin.mymusic.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.admin.mymusic.javaBean.MusicPlayData;
import com.admin.mymusic.javaBean.gson.DataBean;
import com.admin.mymusic.network.DownLoad;
import com.admin.mymusic.service.MusicPlayService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * 媒体播放管理类
 *
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/31 18:16
 **/
public class MediaManage {
    private final static String TAG = "数据监控_媒体管理";
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isCache = false;//是否缓存有播放
    private boolean isOk = true;//装载过程监控
    private boolean isFirst = true;
    private boolean isOkPlay = false;
    private int[] playNumber = {0, 0, 0};//上一首，当前，下一首
    private List<MusicPlayData> listMusic = new ArrayList<>();//播放列表
    private MusicPlayData playData = new MusicPlayData();
    private int flags = 0;
    private Gson gson = new Gson();
    private String quality = "1000kape";//音质
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * 重组数组
     *
     * @param position
     */
    private void recombination(int position) {
        if (listMusic.size() == 0) {
            playNumber[0] = 0;//上一个
            playNumber[1] = 0;//当前
            playNumber[2] = 0;//下一首
        } else {
            int now = 0;
            if (position < 0) {
                now = 0;
            } else if (position > listMusic.size() - 1) {
                now = listMusic.size() - 1;
            } else {
                now = position;
            }
            playNumber[1] = now;//当前播放的下标
            int up = playNumber[1] - 1;
            if (up < 0) {
                up = listMusic.size() - 1;
            }
            playNumber[0] = up;
            int next = playNumber[1] + 1;
            if (next > listMusic.size() - 1) {
                next = 0;
            }
            playNumber[2] = next;
        }
        Log.i(TAG, "上一首下标" + playNumber[0] + "，当前音乐下标" + playNumber[1] + "，下一首下标" + playNumber[2]);
    }

    /**
     * 是否为http连接
     *
     * @param path
     * @return
     */

    private boolean isUrl(String path) {
        if (path == null) return false;
        if (path.indexOf("https://") != -1 || path.indexOf("http://") != -1) {
            return true;
        }
        return false;
    }

    /**
     * 切换音乐
     *
     * @param path
     * @throws IOException
     */
    private void replaceMusic(String path) throws IOException {
        if (!isOk) {
            Log.i(TAG, "上一首没装载完，禁止继续下载！");
            return;
        }
        //Log.e(TAG, "调用了一次");
        close();//清空上一首的缓存数据
        isOk = false;//准备缓存，禁止继续调用
        init().setDataSource(path);//添加音乐播放连接
        init().prepareAsync();//步的方式装载流媒体文件
        //init().prepare();//同步加载
        playData = listMusic.get(flags);
        cacMusic(path, flags);//缓存音乐
    }

    /**
     * 缓存音乐
     *
     * @param path
     */
    private void cacMusic(String path, int number) {
        //延迟5秒后缓存
        if (isUrl(path)) {
            if (context != null) {
                executorService.execute(() -> {
                    try {
                        if (number != playNumber[1] && number != playNumber[2]) {
                            Log.e(TAG, "缓存加载错误，下标不是当前播放或者下一个播放！");
                            return;
                        }
                        Thread.sleep(6000);//延迟下载
                        MusicPlayData musicPlayData;
                        if (number == playNumber[1]) {
                            cacMusic(listMusic.get(playNumber[2]).getMusicSongerId(), playNumber[2], 0);//缓存下一首
                            musicPlayData = listMusic.get(number);
                        } else if (number == playNumber[2]) {
                            musicPlayData = listMusic.get(number);
                        } else {
                            Log.e(TAG, "缓存加载错误，下标不是当前播放或者下一个播放！");
                            return;
                        }
                        String suffix = path.substring(path.lastIndexOf('.') - 1);//获取后缀
                        String musicName = musicPlayData.getMusicName() + "_Id" + musicPlayData.getMusicSongerId() + suffix;//音乐名称
                        String filepath = FilePath.getMusicCacFilesPath(context) + musicName;//拼接路径
                        String musicCacFilesPath = FilePath.getMusicCacFilesPath(context, musicName);//读取音乐路径
                        String readRecord = FileUtil.readRecord(context, filepath);//读取是否完整缓存在本地
                        if (musicCacFilesPath == null) {
                            //不存在
                            DownLoad.downMusic(context, path, filepath);
                        } else if (readRecord != null) {
                            //存在且没下载完整
                            DownLoad.downMusic(context, path, filepath);
                        } else {
                            //Log.e(TAG,"文件存在不需要缓存");
                        }
                    } catch (Exception e) {
                    }
                });
            }
        } else {
            executorService.execute(() -> {
                try {
                    Thread.sleep(1000);
                    if (number == playNumber[1]) {
                        cacMusic(listMusic.get(playNumber[2]).getMusicSongerId(), playNumber[2], 0);//缓存下一首
                    } else if (number == playNumber[2]) {

                    } else {
                        Log.e(TAG, "缓存加载错误，下标不是当前播放或者下一个播放！");
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
    }

    /**
     * 缓存音乐
     *
     * @param songId
     * @param number
     * @param num
     */
    private void cacMusic(String songId, int number, int num) {
        String musicPath = UrlSplicing.getMusic(songId, quality);//音乐
        Headers headers = DownLoad.getHeaders(Setting.set().getCsrf());
        Call call = DownLoad.okGet(musicPath, headers);
        //Log.e(TAG, "调用一次"+musicPath);
        if (num > 5) {
            Log.e(TAG, "音乐连接获取失败！ 5次" + musicPath);
            return;
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "音乐连接获取失败！" + num);
                try {
                    if (num == 2) {
                        DownLoad.getCsrf();
                    }
                    Thread.sleep(200);
                    cacMusic(musicPath, number);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                String json = null;
                try {
                    json = response.body().string();
                    DataBean dataBean = gson.fromJson(json, DataBean.class);
                    cacMusic(dataBean.getUrl(), number);
                } catch (Exception e) {
                    Log.e(TAG, "音乐连接解析失败！" + json);
                    try {
                        Thread.sleep(200);
                        cacMusic(musicPath, number, num + 1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


    }

    /**
     * 切换音乐
     *
     * @param url
     */
    private void replaceMusic(String url, int num) {
        if (!isOk) {
            Log.i(TAG, "上一首没装载完，禁止继续下载！");
            return;
        }
        Headers headers = DownLoad.getHeaders(Setting.set().getCsrf());
        Call call = DownLoad.okGet(url, headers);
        //Log.e(TAG, "调用一次"+url);
        if (num > 5) {
            Log.e(TAG, "音乐连接获取失败！ 5次" + url);
            isOk = true;
            return;
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "音乐连接获取失败！" + num);
                try {
                    if (num == 3) {
                        DownLoad.getCsrf();
                    }
                    Thread.sleep(200);
                    replaceMusic(url, num + 1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = null;
                try {
                    MusicPlayData musicPlayData = listMusic.get(flags);

                    json = response.body().string();
                    DataBean dataBean = gson.fromJson(json, DataBean.class);
                    String suffix = dataBean.getUrl().substring(dataBean.getUrl().lastIndexOf('.') - 1);//获取后缀
                    String musicName = musicPlayData.getMusicName() + "_Id" + musicPlayData.getMusicSongerId() + suffix;//音乐名称
                    String fileCacpath = FilePath.getMusicCacFilesPath(context) + musicName;//拼接路径
                    String fileDowpath = FilePath.getDowFilesPath() + musicName;//拼接下载路径

                    String musicCacFilesPath = FilePath.getMusicCacFilesPath(context, musicName);//读取音乐缓存路径
                    String musicFilesPath = FilePath.getDowFilesPath(musicName);//读取音乐下载路径

                    String readCacRecord = FileUtil.readRecord(context, fileCacpath);//读取是否完整缓存在本地
                    String readRecord = FileUtil.readRecord(context, fileDowpath);//读取是否完整下载到本地

                    String musicPath = "";
                    if (musicCacFilesPath != null && readCacRecord == null) {
                        //缓存有音乐文件&&文件完整
                        musicPath = musicCacFilesPath;//
                    } else if (musicFilesPath != null && readRecord == null) {
                        //下载有文件&&文件完整
                        musicPath = musicFilesPath;
                    } else {
                        //缓存与本地都没有文件
                        musicPath = dataBean.getUrl();
                        Log.e(TAG, "文件不存在");
                    }
                    isOk = true;
                    replaceMusic(musicPath);

                } catch (Exception e) {
                    Log.e(TAG, "音乐连接解析失败！" + json);
                    try {
                        Thread.sleep(200);
                        replaceMusic(url, num + 1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     ****************************************************************************************************************************************************************************************************************************************************************
     *                                                                                              对外Api
     *****************************************************************************************************************************************************************************************************************************************************************
     **/

    /**
     * 获取媒体播放类对象
     *
     * @return
     */
    public MediaPlayer init() {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            //Log.e(TAG, "媒体播放器初始成功");
            //装载完毕监听
            this.mediaPlayer.setOnBufferingUpdateListener((player, percent) -> {
                isCache = true;
            });
            this.mediaPlayer.setOnPreparedListener((play) -> {
                isCache = true;
                start();
                isOk = true;
                isOkPlay = true;
                recombination(flags);//装载完成就重组
                if (context != null) {
                    context.sendBroadcast(new Intent(MusicPlayService.RECEIVER_MUSIC_UPDATA));
                }
            });
            //播放结束监听
            this.mediaPlayer.setOnCompletionListener((play) -> {
                if (!init().isLooping()) {
                    pause();//暂停
                    isOkPlay = false;
                    if (isCache) {
                        try {
                            next(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    isCache = false;//是否有缓存
                }
            });
            //播放错误
            this.mediaPlayer.setOnErrorListener((play, what, extra) -> {
                isOkPlay = false;
                isCache = false;
                if (context != null) {
                    context.sendBroadcast(new Intent(MusicPlayService.RECEIVER_MUSIC_ERROR));//发送播放错误广播
                }
                return false;
            });
        }
        return this.mediaPlayer;
    }

    /**
     * 是否在播放
     *
     * @return
     */
    public boolean isPlay() {
        if (listMusic == null || listMusic.size() == 0 || !isCache) {
            return false;
        }
        if (this.mediaPlayer == null) return false;

        try {
            return init().isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOkStart() {
        if (listMusic.size() != 0 && !isPlay()) {
            return true;
        }
        return false;
    }

    public boolean isOkPlay() {
        return isOkPlay;
    }

    public boolean isCache() {
        return isCache;
    }

    /**
     * 启动播放
     */
    public void start() {
        if (!isPlay()) {
            //Log.e(TAG, "-----------------------------------------------------------" );
            if (isFirst) {
                try {
                    replaceMusic(playNumber[1], true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                init().start();
                isOkPlay = true;
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (isPlay()) {
            init().pause();
            isOkPlay = false;
            if (context != null) {
                context.sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STOP));
            }
        }
    }

    /**
     * 关闭并清空数据
     */
    public void close() {
        if (isPlay()) {
            init().stop();
        }
        isOkPlay = false;
        init().release();//回收流媒体资源
        this.mediaPlayer = null;
        isCache = false;
        isOk = true;//可以继续了
        ///mediaPlayer.reset();// 重置MediaPlayer至未初始化状态
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    /**
     * 获取当前的播放位置
     *
     * @return
     */
    public int getCurrentPosition() {
        if (isCache) {
            try {
                return init().getCurrentPosition();
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取文件的总时长
     *
     * @return
     */
    public int getDuration() {
        if (isCache) {
            try {
                return init().getDuration();
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 指定播放位置
     *
     * @param msec 位置（毫秒）
     */
    public void seekTo(int msec) {
        init().seekTo(msec);
    }

    /**
     * 却换一首音乐
     *
     * @param position
     */
    public void replaceMusic(int position, boolean isMusic) throws IOException {
        if (position < 0 || position >= listMusic.size()) {
            Log.e(TAG, "音乐连接下标定位失败，超出范围播放终止");
            return;
        }
        isFirst = false;
        MusicPlayData replacePlayData = listMusic.get(position);
        flags = position;
        String musicPath = replacePlayData.getMusicPath();//获取音乐地址
        //判断地址是否存在
        if (musicPath != null && !musicPath.equals("")) {
            if (isUrl(musicPath)) {
                //是一个连接
                if (musicPath.indexOf(UrlSplicing.REGION) != -1) {
                    //是一个API连接,还要继续获取地址
                    replaceMusic(musicPath, 0);
                } else {
                    //是一个网络连接，可以直接播放，异常在这里抛出
                    replaceMusic(musicPath);
                }
            } else {
                //是一个本地地址
                replaceMusic(musicPath);
            }
        } else {
            String songId = replacePlayData.getMusicSongerId();
            if (isMusic) {
                musicPath = UrlSplicing.getMusic(songId, quality);//音乐
            } else {
                musicPath = UrlSplicing.getMusic(songId, quality);//视频
            }
            replaceMusic(musicPath, 0);
        }
    }

    /**
     * 上一首
     *
     * @param isMusic
     * @throws IOException
     */
    public void prev(boolean isMusic) throws IOException {
        replaceMusic(playNumber[0], isMusic);
    }

    /**
     * 下一首
     *
     * @param isMusic
     * @throws IOException
     */
    public void next(boolean isMusic) throws IOException {
        replaceMusic(playNumber[2], isMusic);
    }

    /**
     * 获取当前播放的音乐数据
     *
     * @return
     */
    public MusicPlayData getPlayData() {
        return this.playData;
    }

    /**
     * 替换播放列表
     *
     * @param listMusic
     */
    public void replaceList(List<MusicPlayData> listMusic) {
        this.listMusic.clear();
        addList(listMusic);
        recombination(0);
    }

    public void stopThread() {
        executorService.shutdownNow();
    }

    /**
     * 追加一条记录到播放列表
     *
     * @param playData
     */
    public void addList(MusicPlayData playData) {
        this.listMusic.add(playData);
    }

    /**
     * 追加多个数据到播放列表
     *
     * @param listMusic
     */
    public void addList(List<MusicPlayData> listMusic) {
        this.listMusic.addAll(listMusic);
    }

    public void closeAll() {
        close();
        this.listMusic.clear();
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 初始化上下文对象
     *
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

}
