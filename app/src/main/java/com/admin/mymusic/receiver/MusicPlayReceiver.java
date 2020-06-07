package com.admin.mymusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicPlayReceiver extends BroadcastReceiver {
    private final static String TAG="音乐播放广播监听";
    private OnPlayMusicReceiverListen onPlayMusicReceiverListen;

    @Override
    public void onReceive(Context context, Intent intent) {
        String tag=intent.getAction();
        if(onPlayMusicReceiverListen!=null){
            onPlayMusicReceiverListen.onReceiverListen(tag);
        }
    }

    public void setOnPlayMusicReceiverListen(OnPlayMusicReceiverListen onPlayMusicReceiverListen) {
        this.onPlayMusicReceiverListen = onPlayMusicReceiverListen;
    }

    public interface OnPlayMusicReceiverListen{
        void onReceiverListen(String tag);
    }
}
