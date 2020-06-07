package com.admin.mymusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.admin.mymusic.R;
import com.admin.mymusic.fragment.search.ButlerFragmentSearch;
import com.admin.mymusic.fragment.search.SearchResultFragment;
import com.admin.mymusic.javaBean.MusicPlayData;
import com.admin.mymusic.myview.SearchView;
import com.admin.mymusic.service.MusicPlayService;
import com.admin.mymusic.service.ServiceData;
import com.admin.mymusic.utils.MediaUtil;
import com.bumptech.glide.Glide;

import java.util.Observable;

public class SearchActivity extends BaseActivity implements SearchView.OnSearchListen {
    private final static String TAG = "数据监控_搜索页面";
    private ButlerFragmentSearch butlerSearch;//搜索页面碎片管理器
    private SearchView searchView;
    private Fragment fragment;
    private CheckBox checkPlay;
    private TextView tv_name, tv_lyr;
    private ImageView iv_img;//头像


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        butlerSearch = new ButlerFragmentSearch(getSupportFragmentManager());//初始化碎片管理器
        searchView = findViewById(R.id.search_toolbar_search);
        searchView.setOnSearchListen(this);
        fragment = butlerSearch.replace(0);//默认开启第一个
        initPlayService();//初始化服务
        initViews();
        sendBroadcast(new Intent(MusicPlayService.RECEIVER_MUSIC_UPDATA));
    }

    private void initViews() {
        checkPlay = findViewById(R.id.music_control_cb_play);
        checkPlay.setOnCheckedChangeListener(this::onCheckedChanged);
        tv_name = findViewById(R.id.music_control_tv_name);
        iv_img = findViewById(R.id.music_control_iv_img);
        checkedPlay(MediaUtil.getMedia().isPlay());
    }

    /**
     * 搜索
     *
     * @param data
     */
    private void search(String data) {
        fragment = butlerSearch.replace(1);//切换到搜索页面
        if (fragment instanceof SearchResultFragment) {
            new Handler().postDelayed(() -> {
                ((SearchResultFragment) fragment).search(data);
            }, 100);
        }
    }


    public void checkedPlay(boolean checked) {
        runOnUiThread(() -> {
            checkPlay.setOnCheckedChangeListener(null);
            checkPlay.setChecked(checked);
            checkPlay.setOnCheckedChangeListener(this::onCheckedChanged);
        });
    }

    @Override
    public void onClick(View v, String data) {
        super.onClick(v);
        search(data);
    }

    @Override
    public void onTextChange(String str) {

    }

    @Override
    public void onItemClick(int position, String str) {
        search(str);
        /*Intent intentPlay = new Intent(context, MusicPlayActivity.class);
        ActivityOptionsCompat optionsPlay = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, "songName");//元素共享
        startActivity(intentPlay, optionsPlay.toBundle());*/

    }

    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        ServiceData serviceData = (ServiceData) arg;
        if (serviceData == null) return;
        switch (serviceData.getWas()) {
            case 0:
                checkedPlay((boolean) serviceData.getObject());
                break;
            case 1:
                //Log.e(TAG,"-----------"+serviceData.getObject());
                break;
            case 2://有数据更新
                upData();
                break;

        }
    }

    /**
     * 更新数据
     */
    private void upData() {
        MusicPlayData playData = MediaUtil.getMedia().getPlayData();
        try {
            if (playData.getImagePath() == null || playData.getImagePath().equals("")) {
                Glide.with(this).load(R.mipmap.logo).into(iv_img);//显示图片
            } else {
                Glide.with(this).load(playData.getImagePath()).into(iv_img);//显示图片
            }
        } catch (Exception e) {}
        tv_name.setText(playData.getMusicName());//音乐名称
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        switch (buttonView.getId()) {
            case R.id.music_control_cb_play:

                checkedPlay(!isChecked);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkedPlay(MediaUtil.getMedia().isPlay());
        upData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //closePlayServic();
    }


}
