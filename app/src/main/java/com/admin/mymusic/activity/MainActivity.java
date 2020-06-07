package com.admin.mymusic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.mymusic.R;
import com.admin.mymusic.javaBean.MusicPlayData;
import com.admin.mymusic.service.ServiceData;
import com.admin.mymusic.utils.MediaUtil;
import com.bumptech.glide.Glide;

import java.util.Observable;

public class MainActivity extends BaseActivity {

    private CheckBox checkPlay;
    private TextView tv_name, tv_lyr;
    private ImageView iv_img;//头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(null);
        /**
         * getWindow().getDecorView()拿到当前活动DecorView，再setSystemUiVisibility()改变系统UI的显示，View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和View.SYSTEM_UI_FLAG_LAYOUT_STABLE就表示活动的布局会显示在状态栏上面，再将状态栏设置为透明色setStatusBarColor()
         */
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//将状态栏字体设置为黑色
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);*/
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        initPlayService();//开启服务
        initViews();
    }

    public void checkedPlay(boolean checked) {
        runOnUiThread(()->{
            checkPlay.setOnCheckedChangeListener(null);
            checkPlay.setChecked(checked);
            checkPlay.setOnCheckedChangeListener(this::onCheckedChanged);
        });
    }

    private void initViews() {
        checkPlay = findViewById(R.id.music_control_cb_play);
        checkPlay.setOnCheckedChangeListener(this::onCheckedChanged);
        tv_name = findViewById(R.id.music_control_tv_name);
        iv_img = findViewById(R.id.music_control_iv_img);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Toast.makeText(this,"目前只有搜索功能哦！",Toast.LENGTH_SHORT).show();
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
        if (playData.getImagePath() == null || playData.getImagePath().equals("")) {
            Glide.with(this).load(R.mipmap.logo).into(iv_img);//显示图片
        } else {
            Glide.with(this).load(playData.getImagePath()).into(iv_img);//显示图片
        }
        tv_name.setText(playData.getMusicName());//音乐名称
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        switch (buttonView.getId()){
            case R.id.music_control_cb_play:
                checkedPlay(!isChecked);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                //startActivity();
                Intent intentPlay = new Intent(MainActivity.this, SearchActivity.class);
                //ActivityOptionsCompat optionsPlay = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, iv_img, "img");//元素共享
                startActivity(intentPlay);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaUtil.getMedia().closeAll();
    }
}
