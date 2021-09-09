package com.example.fit5120_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Animation extends AppCompatActivity {
 ImageView a1,a2,a3,a4;
 AnimationDrawable animation;
 Button btn;
    private static final int NOT_NOTICE = 2;
    private AlertDialog alertDialog;
    private final int permissionCode = 100;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        a1=findViewById(R.id.a1);
        btn=findViewById(R.id.btnst);
        animation=new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.mipmap.a1),3000);
        animation.addFrame(getResources().getDrawable(R.mipmap.a2),3000);
        animation.addFrame(getResources().getDrawable(R.mipmap.a3),3000);
        animation.addFrame(getResources().getDrawable(R.mipmap.a4),3000);
        animation.setOneShot(true);
        a1.setImageDrawable(animation);
        animation.start();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Animation.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder2.build());
        builder2.detectFileUriExposure();
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            int l = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
            int m = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[2]);
            int n = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[3]);
            int j = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[4]);
            int k = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[5]);
            int p = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[6]);
            int q = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[7]);

            if (i != PackageManager.PERMISSION_GRANTED || l != PackageManager.PERMISSION_GRANTED || m != PackageManager.PERMISSION_GRANTED ||
                    n != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED || k != PackageManager.PERMISSION_GRANTED
                    || p != PackageManager.PERMISSION_GRANTED || q != PackageManager.PERMISSION_GRANTED) {
                startRequestPermission();
            }
        }


    }
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 54321);
    }

    //检查权限
    private void checkPermission() {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() <= 0) {

        } else {
            permissionDialog();
            //存在未允许的权限
            ActivityCompat.requestPermissions(this, permissions, permissionCode);
        }
    }

    //授权后回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions[0].equals(Manifest.permission.CAMERA)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Animation.this, "已授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Animation.this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }

        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Animation.this, "已授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Animation.this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void permissionDialog() {
        if (alertDialog == null) {
            alertDialog = new android.app.AlertDialog.Builder(this)
                    .setTitle("提示信息")
                    .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    private void cancelPermissionDialog() {
        alertDialog.cancel();
    }




}