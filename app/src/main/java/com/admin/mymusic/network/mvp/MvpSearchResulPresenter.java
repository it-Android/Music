package com.admin.mymusic.network.mvp;

import android.util.Log;

import com.admin.mymusic.javaBean.gson.SearchDataBean;
import com.admin.mymusic.javaBean.gson.SearchResultSongData;
import com.admin.mymusic.network.DownLoad;
import com.admin.mymusic.network.mvp.base.MvpBasePresenter;
import com.admin.mymusic.utils.Setting;
import com.admin.mymusic.utils.UrlSplicing;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;


/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 16:34
 **/
public class MvpSearchResulPresenter extends MvpBasePresenter<MvpSearchResulView<List<SearchResultSongData>>> {
    private final static String TAG = "数据监控_MVP_Se_P";
    private Gson gson = new Gson();

    /**
     * @param params 搜索关键字
     * @param num 第几页
     * @param was 数据标识id
     */
    public void getSearchData(String params, int num, int was) {
        if (!isViewAttached()) {
            Log.e(TAG, "没有初始化，数据请求停止！");
            return;
        }
        params= UrlSplicing.getSearch(params,num,50);
        getView().showLoading(was);//开始加载了
        Headers headers = DownLoad.getHeaders(Setting.set().getCsrf());//获取默认请求头
        headers=headers.newBuilder()
                .add("Referer", "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=")
                .build();
        Call call = DownLoad.okGet(params, headers);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (isViewAttached()) {
                    getView().showErr(was);
                    getView().hideLoading(was);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (isViewAttached()) {
                    try {
                        String json=response.body().string();
                        SearchDataBean searchDataBean = gson.fromJson(json, SearchDataBean.class);
                        List<SearchResultSongData> data = searchDataToList(searchDataBean);
                        getView().showData(data,num, was );
                    } catch (Exception e) {
                        getView().showToast("搜索数据解析错误", was);
                    }
                    getView().hideLoading(was);
                }
            }
        });
    }


    private List<SearchResultSongData> searchDataToList(SearchDataBean dataBean) {
        List<SearchResultSongData> list = new ArrayList<>();
        for (SearchDataBean.DataBean.ListBean listBean : dataBean.getData().getList()) {
            SearchResultSongData songData = new SearchResultSongData();
            songData.setName(listBean.getName());//名称
            songData.setSinger(listBean.getArtist());//作者
            songData.setSongerId(listBean.getRid() + "");//id
            songData.setTitle(listBean.getAlbum());//
            //小图
            if(listBean.getPic120()!=null&&!listBean.getPic120().equals("")){
                songData.setImgPath(listBean.getPic120());//图片
            }else if(listBean.getPic()!=null&&!listBean.getPic().equals("")){
                songData.setImgPath(listBean.getPic());//图片
            }else if(listBean.getAlbumpic()!=null&&!listBean.getAlbumpic().equals("")){
                songData.setImgPath(listBean.getAlbumpic());//图片
            }

            //大图
            if(listBean.getAlbumpic()!=null&&!listBean.getAlbumpic().equals("")){
                songData.setImgBigPath(listBean.getAlbumpic());
            }else if (listBean.getPic()!=null&&!listBean.getPic().equals("")){
                songData.setImgBigPath(listBean.getPic());
            }else {
                songData.setImgBigPath(songData.getImgPath());
            }


            list.add(songData);
        }
        return list;
    }
}
