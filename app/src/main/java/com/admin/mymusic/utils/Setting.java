package com.admin.mymusic.utils;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/4/2 22:00
 **/
public class Setting {
    private String csrf="";

    private boolean isPlay=false;

    private static Setting setting=new Setting();


    public static Setting set(){
        return setting;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getCsrf() {
        return csrf;
    }

    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }
}
