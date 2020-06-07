package com.admin.mymusic.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.admin.mymusic.R;
import com.admin.mymusic.service.MusicPlayService;
import com.admin.mymusic.service.ServiceData;
import com.admin.mymusic.utils.MediaUtil;
import com.admin.mymusic.utils.Setting;

import java.util.Observable;
import java.util.Observer;


/**
 * 基础界面
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/31 15:05
 **/
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, CheckBox.OnCheckedChangeListener, Observer {

    private MusicPlayService musicPlayService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayService = ((MusicPlayService.IBinder) service).getService();//获取服务
            musicPlayService.addObserver(BaseActivity.this);//添加观察者
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicPlayService = null;//异常退出执行这里，正常退出不执行
            Log.e("数据监控", "***********************非正常退出！******************************");
        }
    };


    /**
     * 初始化服务
     */

    public void initPlayService() {
        bindService(new Intent(BaseActivity.this, MusicPlayService.class), connection, Service.BIND_AUTO_CREATE);
        if (musicPlayService != null) {
            musicPlayService.addObserver(this::update);
        }
    }


    /**
     * 断开服务
     */
    public void closePlayServic() {
        unbindService(connection);
        if (musicPlayService != null) {
            musicPlayService.deleteObserver(BaseActivity.this);//删除观察者
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (musicPlayService != null) {
            musicPlayService.addObserver(BaseActivity.this);
        }
    }

    /**
     * 按钮点击事件监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_control_fra_box:
                if (MediaUtil.getMedia().isCache()) {
                    Intent intentPlay = new Intent(BaseActivity.this, MusicPlayActivity.class);
                    ActivityOptionsCompat optionsPlay = ActivityOptionsCompat.makeSceneTransitionAnimation(BaseActivity.this, findViewById(R.id.music_control_iv_img), "songPic");//元素共享
                    startActivity(intentPlay, optionsPlay.toBundle());
                } else {
                    Toast.makeText(this, "还没装载，不可以进入播放！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.music_control_iv_img:
                Intent intentImage = new Intent(BaseActivity.this, ImageActivity.class);
                intentImage.putExtra("imagePic", MediaUtil.getMedia().getPlayData().getImageBigPath());
                ActivityOptionsCompat optionsPlay = ActivityOptionsCompat.makeSceneTransitionAnimation(BaseActivity.this, v, "songPic");//元素共享
                startActivity(intentImage, optionsPlay.toBundle());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!Setting.set().isPlay()) {
            initPlayService();
            new Handler().postDelayed(() -> {
                sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STAR));
                MediaUtil.getMedia().setFirst(true);
            }, 100);
        } else {
            if (isChecked) {
                sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STAR));
            } else {
                sendBroadcast(new Intent(MusicPlayService.RECEIVER_PLAY_STOP));
            }
        }
    }

    /**
     * 被观察者，发送过来的数据
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        ServiceData serviceData = (ServiceData) arg;
        if (serviceData != null) {
            if (serviceData.getWas() == MusicPlayService.WAS[MusicPlayService.WAS.length - 1]) {//404关闭
                musicPlayService.clearNotification();//关闭通知
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

}
