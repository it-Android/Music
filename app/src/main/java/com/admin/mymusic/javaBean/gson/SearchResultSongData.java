package com.admin.mymusic.javaBean.gson;
public class SearchResultSongData {
    private String name;//名称
    private String singer;//歌手
    private String title;//消息
    private String songerId;//歌曲Id
    private String imgPath;
    private String imgBigPath;

    public String getImgBigPath() {
        return imgBigPath;
    }

    public void setImgBigPath(String imgBigPath) {
        this.imgBigPath = imgBigPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSongerId() {
        return songerId;
    }

    public void setSongerId(String songerId) {
        this.songerId = songerId;
    }
}

