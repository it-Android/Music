package com.admin.mymusic.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.admin.mymusic.R;
import com.admin.mymusic.activity.MainActivity;
import com.admin.mymusic.activity.SearchActivity;
import com.admin.mymusic.javaBean.MusicPlayData;
import com.admin.mymusic.network.DownLoad;
import com.admin.mymusic.receiver.MusicPlayReceiver;
import com.admin.mymusic.utils.MediaUtil;
import com.admin.mymusic.utils.Setting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * 音乐播放服务
 *
 * @作者(author)： JQ
 * @创建时间(date)： 2020/3/22 18:16
 **/
public class MusicPlayService extends Service implements MusicPlayReceiver.OnPlayMusicReceiverListen {
    private final static String TAG = "数据监控_音乐服务";
    public final static int SERVICE_ID = 0;//服务的id

    public final static String RECEIVER_PLAY_STAR = "com.admin.mymusic.receiver.play.start";
    public final static String RECEIVER_PLAY_STOP = "com.admin.mymusic.receiver.play.stop";
    public final static String RECEIVER_UP = "com.admin.mymusic.receiver.up";
    public final static String RECEIVER_NEXT = "com.admin.mymusic.receiver.next";
    public final static String RECEIVER_CLOSE = "com.admin.mymusic.receiver.close";
    public final static String RECEIVER_MUSIC_UPDATA = "com.admin.mymusic.receiver.music.updata";
    public final static String RECEIVER_MUSIC_ERROR = "com.admin.mymusic.receiver.music.error";
    public final static String RECEIVER_MUSIC_LYR_OK = "com.admin.mymusic.receiver.music.lyric.ok";

    public static final int[] WAS = {0, 1, 2, 404};
    private final static int NOTIFICATION_ID = 10;
    private Notification notification;
    private RemoteViews views;
    private MusicObserver musicObserver;
    private MusicPlayReceiver playReceiver;
    private Timer timer = new Timer();//时钟，定时执行
    private boolean isChecking = false;
    private int allow = 15;//允许点击

    /**
     * 初始化通知
     */
    private void initNotification() {
        views = new RemoteViews(getPackageName(), R.layout.notification_play_musice_layout);
        views.setOnClickPendingIntent(R.id.notif_item_up, PendingIntent.getBroadcast(this, 12, new Intent(RECEIVER_UP), 0));//绑定上一首
        views.setOnClickPendingIntent(R.id.notif_item_play, PendingIntent.getBroadcast(this, 13, new Intent(RECEIVER_PLAY_STAR), 0));//绑定播放按钮
        views.setOnClickPendingIntent(R.id.notif_item_next, PendingIntent.getBroadcast(this, 14, new Intent(RECEIVER_NEXT), 0));//绑定下一首
        views.setOnClickPendingIntent(R.id.notif_item_close, PendingIntent.getBroadcast(this, 15, new Intent(RECEIVER_CLOSE), 0));//绑定关闭
        Intent intent = new Intent(this, MainActivity.class);
        views.setOnClickPendingIntent(R.id.notif_item_image, PendingIntent.getActivity(this, 16, intent, 0));//绑定
        // 在API>=26的时候创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("xxx", "xxx", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, "xxx")
                    .setCustomBigContentView(views)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)//长按后的图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))//平时的图标
                    .build();
            startForegroundService(intent);
        } else {
            notification = new Notification.Builder(this.getApplicationContext())
                    .setContent(views)
                    .setSmallIcon(R.mipmap.ic_launcher)//长按后的图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))//平时的图标
                    .setOngoing(true)
                    .build();//常驻
        }
        startForeground(NOTIFICATION_ID, notification);//启动
    }

    /**
     * 初始化广播
     */
    private void initReceiver() {
        playReceiver = new MusicPlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_PLAY_STAR);//音乐播放广播
        intentFilter.addAction(RECEIVER_PLAY_STOP);//音乐停止广播
        intentFilter.addAction(RECEIVER_UP);//上一首歌
        intentFilter.addAction(RECEIVER_NEXT);//下一首歌
        intentFilter.addAction(RECEIVER_CLOSE);//关闭前台服务广播
        intentFilter.addAction(RECEIVER_MUSIC_UPDATA);//音乐数据变更广播
        intentFilter.addAction(RECEIVER_MUSIC_ERROR);//音乐播放错误广播
        intentFilter.addAction(RECEIVER_MUSIC_LYR_OK);//音乐歌词下载成功
        registerReceiver(playReceiver, intentFilter);//动态注册广播
        playReceiver.setOnPlayMusicReceiverListen(this);
    }

    /**
     * 进度获取
     */
    private void onlisten() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (MediaUtil.getMedia().isOkPlay()) {
                        musicObserver.notifyChanged(new ServiceData(MediaUtil.getMedia().getCurrentPosition(), WAS[1], SERVICE_ID));//添加发送数据
                    }
                } catch (Exception e) {
                    Log.e(TAG, "读取定时错误一次");
                }
            }
        }, 0, 500);
    }

    private int checkId = 0;

    private void checkMusic() {
        new Thread(() -> {
            Log.e("数据", "开始检测");
            isChecking = true;
            while (checkId < 60) {
                checkId++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!MediaUtil.getMedia().isCache()) {
                force(false);//关闭
                MediaUtil.getMedia().setFirst(true);
                MediaUtil.getMedia().close();
                Looper.prepare();
                Toast.makeText(MusicPlayService.this, "播放错误，强制关闭！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            isChecking = false;
            Log.e("数据", "检测完毕");
        }).start();
    }

    private void checkClick() {
        allow = 0;
        new Thread(() -> {
            while (allow < 15) {
                allow++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 提示消息框有数据更新
     *
     * @param imagePath 图片
     * @param text      名称
     */
    public void upDataNotification(String imagePath, String text) {
        new Thread(() -> {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.logo)  //加载成功之前占位图
                    .error(R.mipmap.logo)    //加载错误之后的错误图
                    //.override(400,400)  //指定图片的尺寸
                    //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                    .fitCenter()
                    //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                    //.circleCrop()//指定图片的缩放类型为centerCrop （圆形）
                    //.skipMemoryCache(true)  //跳过内存缓存
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)   //缓存所有版本的图像
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)  //跳过磁盘缓存
                    //.diskCacheStrategy(DiskCacheStrategy.DATA)  //只缓存原来分辨率的图片
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE);  //只缓存最终的图片
                    ;
            Bitmap bitmap=null;
            try {
                bitmap=Glide.with(this).asBitmap().load(imagePath).apply(options).submit().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(bitmap==null)bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.logo);
            views.setImageViewBitmap(R.id.notif_item_image, bitmap);//更新图片
            startForeground(NOTIFICATION_ID, notification);//启动刷新
        }).start();
        views.setTextViewText(R.id.notif_item_name, text);//更新文字
        startForeground(NOTIFICATION_ID, notification);//启动刷新
    }

    /**
     * 添加观察者
     *
     * @param observer
     */
    public void addObserver(Observer observer) {
        musicObserver.addObserver(observer);
    }

    /**
     * 删除观察者
     *
     * @param observer
     */
    public void deleteObserver(Observer observer) {
        musicObserver.deleteObserver(observer);
    }


    @Override
    public IBinder onBind(Intent intent) {
        musicObserver = new MusicObserver();//
        return new IBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "音乐播放服务启动成功！");
        DownLoad.getCsrf();
        initReceiver();//初始化广播
        initNotification();//初始化Notification
        MediaUtil.getMedia().setContext(this);
        onlisten();//启动爬取
    }

    @Override
    public void onReceiverListen(String tag) {
        switch (tag) {
            case RECEIVER_UP:
                Log.i(TAG, "上一首歌");
                try {
                    if (allow >= 15) {
                        MediaUtil.getMedia().prev();
                        checkClick();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVER_PLAY_STAR:
                Log.i(TAG, "音乐开始");
                starMusic();
                break;
            case RECEIVER_PLAY_STOP:
                Log.i(TAG, "音乐停止");
                pauseMusic();
                break;
            case RECEIVER_NEXT:
                Log.i(TAG, "下一首歌");
                try {
                    if (allow >= 15) {
                        checkClick();
                        MediaUtil.getMedia().next();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVER_CLOSE:
                Log.i(TAG, "关闭");
                //clearNotification();//关闭
                musicObserver.notifyChanged(new ServiceData(RECEIVER_CLOSE, WAS[WAS.length - 1], SERVICE_ID));//添加发送数据，通知关闭咯
                break;
            case RECEIVER_MUSIC_UPDATA:
                Log.i(TAG, "有数据更新");
                String name = MediaUtil.getMedia().getPlayData().getMusicName();
                if (name.equals("")) name = "暂无数据";
                upDataNotification(MediaUtil.getMedia().getPlayData().getImagePath(), name);
                musicObserver.notifyChanged(new ServiceData(true, WAS[2]));
                break;
            case RECEIVER_MUSIC_LYR_OK:
                Log.i(TAG, "收到歌词下载成功的广播！");
                break;
            case RECEIVER_MUSIC_ERROR:

                break;
        }
    }

    //暂停音乐
    private void pauseMusic() {
        if (!MediaUtil.getMedia().isOkStart()) {
            force(false);
        } else if (MediaUtil.getMedia().isOkPlay()) {
            force(false);
        }
    }

    //启动或者播放音乐
    private void starMusic() {
        if (allow >= 15) {
            if (MediaUtil.getMedia().isOkStart()) {
                force(true);
            } else if (!MediaUtil.getMedia().isOkPlay()) {
                force(false);
            }
            checkClick();//检测点击
        }
    }

    /**
     * 强制执行
     *
     * @param state
     */
    private void force(boolean state) {
        Setting.set().setPlay(true);//不显示
        if (state) {
            MediaUtil.getMedia().start();
            views.setOnClickPendingIntent(R.id.notif_item_play, PendingIntent.getBroadcast(this, 13, new Intent(RECEIVER_PLAY_STOP), 0));//绑定
            views.setImageViewResource(R.id.notif_item_play, R.mipmap.m_icon_player_pause_normal);
            musicObserver.notifyChanged(new ServiceData(true, WAS[0], SERVICE_ID));//添加发送数据，通知主界面更新按钮形状
            startForeground(NOTIFICATION_ID, notification);
            if (!isChecking) {
                checkMusic();//检测
            }
            checkId = 0;
        } else {
            MediaUtil.getMedia().pause();
            views.setOnClickPendingIntent(R.id.notif_item_play, PendingIntent.getBroadcast(this, 13, new Intent(RECEIVER_PLAY_STAR), 0));//绑定
            views.setImageViewResource(R.id.notif_item_play, R.mipmap.m_icon_player_play_normal);
            startForeground(NOTIFICATION_ID, notification);
            musicObserver.notifyChanged(new ServiceData(false, WAS[0], SERVICE_ID));//添加发送数据，通知主界面更新按钮形状
        }
    }

    //停止前台服务
    public void clearNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            MediaUtil.getMedia().close();//关闭音乐
            stopForeground(true);// 取消CancelNoticeService的前台
            // 移除DaemonService弹出的通知
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTIFICATION_ID);
            // 任务完成，终止自己
            //stopSelf();
            musicObserver.notifyChanged(new ServiceData(false, WAS[0], SERVICE_ID));//添加发送数据，通知主界面更新按钮形状
            Setting.set().setPlay(false);//不显示
            MediaUtil.getMedia().setFirst(true);
            MediaUtil.getMedia().stopThread();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaUtil.getMedia().close();//关闭音乐
        MediaUtil.getMedia().stopThread();
        unregisterReceiver(playReceiver);//关闭广播
        timer.cancel();
        Log.e(TAG, "音乐播放服务关闭成功！");
    }

    /***********服务绑定****************/
    public final class IBinder extends Binder {
        public MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    /***********观察者模式************/
    public final class MusicObserver extends Observable {
        public void notifyChanged(ServiceData object) {
            this.setChanged();
            this.notifyObservers(object);
        }
    }

}
