package com.admin.mymusic.utils;

import android.util.Log;

import com.admin.mymusic.javaBean.MusicPlayData;

import java.io.IOException;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/31 21:08
 **/
public class MediaUtil extends MediaManage {
    private static MediaUtil mediaUtil = new MediaUtil();

    public static MediaUtil getMedia() {
        return mediaUtil;
    }

    /**
     * 下一首歌
     *
     * @throws IOException
     */
    public void next() throws IOException {
        next(true);
    }

    /**
     * 上一首歌
     *
     * @throws IOException
     */
    public void prev() throws IOException {
        prev(true);
    }


}
