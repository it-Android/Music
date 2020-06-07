package com.admin.mymusic.javaBean;

import java.io.InputStream;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/31 19:21
 **/
public class MusicPlayData {
    private boolean isLive;//是否收藏
    private String musicSongerId="";//音乐id
    private String musicPath="";//音乐路径
    private String[] singer=null;//歌手
    private String imagePath;//音乐图片地址
    private String imageBigPath="";
    private String musicName="暂无数据";//音乐名称
    private int progress=0;//进度
    private long duration=0;//时长
    private String musicLyrPath="";//歌词地址


    public String getImageBigPath() {
        return imageBigPath;
    }

    public void setImageBigPath(String imageBigPath) {
        this.imageBigPath = imageBigPath;
    }

    public String[] getSinger() {
        return singer;
    }

    public void setSinger(String[] singer) {
        this.singer = singer;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMusicSongerId() {
        return musicSongerId;
    }

    public void setMusicSongerId(String musicSongerId) {
        this.musicSongerId = musicSongerId;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMusicLyrPath() {
        return musicLyrPath;
    }

    public void setMusicLyrPath(String musicLyrPath) {
        this.musicLyrPath = musicLyrPath;
    }

}
