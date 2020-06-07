package com.admin.mymusic.network.mvp.base;

import android.content.Context;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 16:16
 **/
public interface MvpBaseView {
    /**
     * 显示正在加载view
     */
    void showLoading(int id);

    /**
     * 关闭正在加载view
     */
    void hideLoading(int id);

    /**
     * 显示提示
     * @param msg
     */
    void showToast(String msg, int id);

    /**
     * 显示请求错误提示
     */
    void showErr(int id);

    /**
     * 获取上下文
     * @return 上下文
     */
    Context getContext();
}
