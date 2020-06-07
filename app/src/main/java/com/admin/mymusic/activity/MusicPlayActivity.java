package com.admin.mymusic.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.admin.mymusic.R;
import com.admin.mymusic.myview.LyricView;
import com.admin.mymusic.service.MusicPlayService;
import com.admin.mymusic.service.ServiceData;
import com.admin.mymusic.utils.MediaUtil;
import com.admin.mymusic.utils.PreferenceUtil;
import com.admin.mymusic.utils.Setting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.text.DecimalFormat;
import java.util.Observable;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private LyricView lyricView;
    private TextView tv_tltle;
    private SeekBar seekBar;
    private TextView display_total, display_position;
    private ImageView img_bg;
    private ImageView btnPlay, btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        //lyricView.setLyricFile(MediaUtil.getMedia().getPlayData().getMusicSongerId());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initPlayService();
        initViews();
        initData();
        sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STAR));//发送播放广播
        sendBroadcast(new Intent(MusicPlayService.RECEIVER_MUSIC_UPDATA));//提示有消息更新
        if (MediaUtil.getMedia().isPlay()) {
            btnPlay.setImageResource(R.mipmap.m_icon_player_pause_normal);
        } else {
            btnPlay.setImageResource(R.mipmap.m_icon_player_play_normal);
        }
    }

    private void initViews() {
        lyricView = findViewById(R.id.music_play_lyric_view);
        tv_tltle = findViewById(R.id.music_play_title_view);//标题啊
        lyricView.reset("正在加载歌词...");
        lyricView.setLineSpace(PreferenceUtil.getInstance(MusicPlayActivity.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 12.0f));//行距
        lyricView.setTextSize(PreferenceUtil.getInstance(MusicPlayActivity.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 15.0f));//字体大小
        lyricView.setHighLightTextColor(PreferenceUtil.getInstance(MusicPlayActivity.this).getInt(PreferenceUtil.KEY_HIGHLIGHT_COLOR, Color.parseColor("#4FC5C7")));//字体颜色

        seekBar = findViewById(android.R.id.progress);
        seekBar.setMax(1000 * 200);
        seekBar.setOnSeekBarChangeListener(this);


        img_bg = findViewById(R.id.music_iv_picbg);
        btnPlay = (ImageView) findViewById(android.R.id.button2);
        btnSetting = (ImageView) findViewById(R.id.music_play_setting);

        display_position = (TextView) findViewById(android.R.id.text1);
        display_total = (TextView) findViewById(android.R.id.text2);

    }

    private void initData() {
        int duration = MediaUtil.getMedia().getDuration();
        seekBar.setMax(duration);
        display_total.setText(timeArrange(duration));
    }

    //时间计算整理
    private String timeArrange(int duration) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(duration / 1000 / 60) + ":" + format.format(duration / 1000 % 60);
    }


    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
        ServiceData serviceData = (ServiceData) arg;
        if (serviceData == null) return;
        switch (serviceData.getWas()) {
            case 0:
                boolean state = (boolean) serviceData.getObject();
                if (state) {
                    btnPlay.setImageResource(R.mipmap.m_icon_player_pause_normal);
                } else {
                    btnPlay.setImageResource(R.mipmap.m_icon_player_play_normal);
                }
                break;
            case 1://时刻进度,只要音乐不停就会一直发送
                runOnUiThread(() -> {
                    int number = (int) serviceData.getObject();
                    lyricView.setCurrentTimeMillis(number);
                    seekBar.setProgress(number);
                });
                break;
            case 2://有数据更新
                lyricView.reset("正在加载歌词...");
                lyricView.setLyricFile(MediaUtil.getMedia().getPlayData().getMusicSongerId());//
                tv_tltle.setText(MediaUtil.getMedia().getPlayData().getMusicName());
                runOnUiThread(() -> {
                    int duration = MediaUtil.getMedia().getDuration();
                    seekBar.setMax(duration);
                    display_total.setText(timeArrange(duration));
                });
                /*Glide.with(this)
                        .load(MediaUtil.getMedia().getPlayData().getImageBigPath())
                    //.apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))) //高斯模糊图
                    .into(img_pic);//图片*/
                try {
                    Glide.with(this)
                            .load(MediaUtil.getMedia().getPlayData().getImageBigPath())
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 1))) //高斯模糊图
                            .into(img_bg);//背景
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case android.R.id.button1:
                sendBroadcast(new Intent(MusicPlayService.RECEIVER_UP));
                lyricView.reset("正在加载歌词...");
                break;
            case android.R.id.button2:
                if (MediaUtil.getMedia().isPlay()) {
                    sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STOP));
                } else {
                    if (!Setting.set().isPlay())
                        MediaUtil.getMedia().setFirst(true);
                    sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STAR));
                }
                break;
            case android.R.id.button3:
                sendBroadcast(new Intent(MusicPlayService.RECEIVER_NEXT));
                lyricView.reset("正在加载歌词...");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        display_position.setText(timeArrange(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        MediaUtil.getMedia().seekTo(progress);//设置进度
    }

}
