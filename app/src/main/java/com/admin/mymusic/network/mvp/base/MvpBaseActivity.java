package com.admin.mymusic.network.mvp.base;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/23 16:25
 **/
public class MvpBaseActivity extends AppCompatActivity implements MvpBaseView{


    @Override
    public void showLoading(int id) {

    }

    @Override
    public void hideLoading(int id) {

    }

    @Override
    public void showToast(String msg, int id) {
        runOnUiThread(()->{
            Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        });
    }

    @Override
    public void showErr(int id) {
        Log.e("数据监控_BA","异常id--->"+id);
    }

    @Override
    public Context getContext() {
        return MvpBaseActivity.this;
    }
}
