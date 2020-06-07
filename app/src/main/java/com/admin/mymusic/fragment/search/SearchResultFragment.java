package com.admin.mymusic.fragment.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.admin.mymusic.R;
import com.admin.mymusic.activity.ImageActivity;
import com.admin.mymusic.adapter.ResultSongRecycleAdapter;
import com.admin.mymusic.javaBean.MusicPlayData;
import com.admin.mymusic.javaBean.gson.SearchResultSongData;
import com.admin.mymusic.myview.RecycleViewLisitenter;
import com.admin.mymusic.network.mvp.MvpSearchResulPresenter;
import com.admin.mymusic.network.mvp.MvpSearchResulView;
import com.admin.mymusic.network.mvp.base.BaseFragment;
import com.admin.mymusic.service.MusicPlayService;
import com.admin.mymusic.utils.MediaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果碎片界面
 *
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/30 23:02
 **/
public class SearchResultFragment extends BaseFragment implements MvpSearchResulView<List<SearchResultSongData>>, RecycleViewLisitenter.onItemClickLisitenter {
    private Context context;
    private RecyclerView recyclerView;
    private ResultSongRecycleAdapter recycleAdapter;
    private LinearLayoutManager manager;
    private List<SearchResultSongData> songAllData = new ArrayList<>();
    private MvpSearchResulPresenter presenter;
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private String  oldParamer="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        presenter = new MvpSearchResulPresenter();
        presenter.attachView(this);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        swipeRefreshLayout=view.findViewById(R.id.fr_sc_res_song_refresh);//下拉刷新
        swipeRefreshLayout.setOnRefreshListener(()->{//下拉监听
            search(oldParamer);
        });
        recyclerView = view.findViewById(R.id.fr_sc_res_song_recycler);
        manager = new LinearLayoutManager(context);
        recycleAdapter = new ResultSongRecycleAdapter(songAllData);
        recycleAdapter.setOnItemClickLisitenter(this);
        recyclerView.setLayoutManager(manager);//添加布局管理器
        recyclerView.setAdapter(recycleAdapter);//添加数据适配器
    }

    private void onShow(Object obj, int num, int was) {
        switch (was) {
            case 0:
                List<SearchResultSongData> allData = (List<SearchResultSongData>) obj;
                if (num == 0 || num == 1) {
                    songAllData.clear();//第一页
                    songAllData.addAll(allData);
                    MediaUtil.getMedia().replaceList(songDataToMusicData(allData));//替换列表
                } else {
                    songAllData.addAll(allData);
                    MediaUtil.getMedia().addList(songDataToMusicData(allData));//追加列表播放数据
                }
                getActivity().runOnUiThread(() -> {
                    recycleAdapter.notifyDataSetChanged();
                });
                break;
        }
    }

    private List<MusicPlayData> songDataToMusicData(List<SearchResultSongData> allData) {
        List<MusicPlayData> musicAllData = new ArrayList<>();
        for (SearchResultSongData songData : allData) {
            MusicPlayData playData = new MusicPlayData();
            playData.setMusicName(songData.getName());//名字
            playData.setMusicSongerId(songData.getSongerId());//音乐Id
            playData.setImagePath(songData.getImgPath());//图片地址
            playData.setImageBigPath(songData.getImgBigPath());//大图片地址
            musicAllData.add(playData);
        }
        return musicAllData;
    }

    @Override
    public void showLoading(int id) {
        super.showLoading(id);
        swipeRefreshLayout.setRefreshing(true);//显示正在刷新
    }

    @Override
    public void showData(List<SearchResultSongData> data, int id, int was) {
        onShow(data, id, was);
    }

    @Override
    public void hideLoading(int id) {
        super.hideLoading(id);
        getActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(false);//取消正在刷新
        });
    }

    public void search(String paramer) {
        songAllData.clear();
        getActivity().runOnUiThread(() -> {
            recycleAdapter.notifyDataSetChanged();
        });
        oldParamer=paramer;
        presenter.getSearchData(paramer, 1, 0);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.recycle_item_result_song_more:
                Toast.makeText(context, "还没可以下载喔", Toast.LENGTH_SHORT).show();
                break;
            case R.id.recycle_item_result_song_img:
                Intent intentImage = new Intent(getActivity(), ImageActivity.class);
                intentImage.putExtra("imagePic", songAllData.get(position).getImgBigPath());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "songPic");//元素共享
                startActivity(intentImage, options.toBundle());
                //startActivity(intentImage);
                break;
            default:
                try {
                    if (!MediaUtil.getMedia().getPlayData().getMusicName().equals(songAllData.get(position).getName())) {
                        MediaUtil.getMedia().replaceMusic(position, true);
                    }
                    /*Intent intentPlay = new Intent(context, MusicPlayActivity.class);
                    ActivityOptionsCompat optionsPlay = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view,"songName");//元素共享
                    startActivity(intentPlay,optionsPlay.toBundle());
                    startActivity(intentPlay);*/
                    context.sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STAR));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

}
