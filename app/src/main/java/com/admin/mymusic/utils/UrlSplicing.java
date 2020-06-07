package com.admin.mymusic.utils;

import com.admin.mymusic.javaBean.Music;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 18:31
 **/
public class UrlSplicing {
    public final static String REGION = "http://www.kuwo.cn/";//api域

    //http://www.kuwo.cn/search/list?key=

    public static String getSearch(String keyword, int pageNumber, int page) {
        StringBuffer buffer = new StringBuffer();
        if(page<0)page=0;
        if(pageNumber<1)pageNumber=1;
        if(page>99)page=99;
        try {
            keyword= URLEncoder.encode(keyword,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            keyword="1";
        }
        buffer.append(REGION)
                .append("api/www/search/searchMusicBykeyWord?")
                .append("pn=")
                .append(pageNumber)
                .append("&rn=")
                .append(page)
                .append("&key=")
                .append(keyword);
        return buffer.toString();
    }

    /**
     * 拼接音乐连接
     * @param songId
     * @param quality
     * @return http://www.kuwo.cn/url?response=url&type=convert_url3&from=pc&t=1574850384300&reqId=0&format=mp3&rid=76323299&br=1000kape
     */
    public static String getMusic(String songId, String quality){
        StringBuffer buffer = new StringBuffer();
        buffer.append(REGION)
                .append("url?response=url&type=convert_url3&from=pc&t=1574850384300&reqId=0&format=mp3&rid=")
                .append(songId)
                .append("&br=")
                .append(quality);
        return buffer.toString();
    }


}
