package com.admin.mymusic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.admin.mymusic.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 加载页面
 */
public class LoadActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ADDRESS = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        checkPermission();
    }

    private void checkPermission() {
        List<String> permissionList = new ArrayList<>();//集合来保存需要申请的权限
        //申请读写权限
        if (ContextCompat.checkSelfPermission(LoadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);//未授权的就添加到集合里面
        }
        if (ContextCompat.checkSelfPermission(LoadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);//将集合全部转换为字符串数组
            //权限申请
            ActivityCompat.requestPermissions(LoadActivity.this, permissions, REQUEST_CODE_ADDRESS);//REQUEST_CODE_ADDRESS返回码，可以随便写
        } else {
            //全部权限通过
            startActivity(new Intent(LoadActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ADDRESS:
                if (grantResults.length > 0) {
                    //判断每一个权限是否通过
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(LoadActivity.this, "必须同意全部权限才可以使用本程序！", Toast.LENGTH_SHORT).show();
                            finish();//退出界面
                            return;//终止
                        }
                    }
                    //全部通过
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoadActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
