package com.admin.mymusic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.admin.mymusic.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageActivity extends AppCompatActivity {
    private ImageView imagePic;
    private ImageView image_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String imagePath = getIntent().getStringExtra("imagePic");
        imagePic = findViewById(R.id.image_iv_pic);
        image_bg = findViewById(R.id.image_iv_picbg);
        RequestOptions options = new RequestOptions()
                //.placeholder(R.mipmap.loading_2)  //加载成功之前占位图
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
        if (imagePath == null || imagePath.equals("")) {
            Glide.with(this).load(R.mipmap.logo).apply(options).into(imagePic);
        } else {
            Glide.with(this).load(imagePath).apply(options).into(imagePic);
            //Glide.with(this).load(R.mipmap.loading_2).into(image_bg);
        }
        imagePic.setOnClickListener((view) -> {
            new Thread(()->{
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            }).start();
        });
    }

}
