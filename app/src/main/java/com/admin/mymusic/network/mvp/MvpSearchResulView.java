package com.admin.mymusic.network.mvp;

import com.admin.mymusic.network.mvp.base.MvpBaseView;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 16:32
 **/
public interface MvpSearchResulView<T> extends MvpBaseView {
    void showData(T data, int id,int was);
}
