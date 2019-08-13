package com.example.android.camera2video;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * 作者: hzx
 * 时间: 2019/8/13
 * ========================================
 * 描述: 后台打开摄像头  流程：启动 -> 绑定 -> 打开
 */

public class SplashActivity extends Activity {
    private Intent mJewxon_service;
    private AutoFitTextureView mAutoFitTextureView;
    private MyJewxonCameraConn mMyJewxonCameraConn;
    private JewxonCameraService mService;
    private boolean isBindService = false;
    private boolean mIsRecordingVideo = false;
    private Button mButton5;

    private final String P1 = Manifest.permission.CAMERA;
    private final String P2 = Manifest.permission.RECORD_AUDIO;
    private final String P3 = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{P1 , P2 , P3} , 100);
        }

        mAutoFitTextureView = findViewById(R.id.texture);
        mJewxon_service = new Intent(SplashActivity.this, JewxonCameraService.class);
        mMyJewxonCameraConn = new MyJewxonCameraConn();

        findViewById(R.id.btn0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(mJewxon_service);
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(mJewxon_service);
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindService(mJewxon_service ,  mMyJewxonCameraConn , BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAutoFitTextureView.isAvailable()){
                    if (isBindService) {
                        mService.startPreview(mAutoFitTextureView);
                    }
                }

            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindService(mMyJewxonCameraConn);
            }
        });

        mButton5 = findViewById(R.id.btn5);

        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecordingVideo) {
                    mService.stopRecordingVideo(mAutoFitTextureView);
                    mButton5.setText("录制");
                    mIsRecordingVideo = false;
                } else {
                    mService.startRecordingVideo(mAutoFitTextureView);
                    mIsRecordingVideo = true;
                    mButton5.setText("结束");
                }
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.takePicture();
            }
        });

    }

    private class MyJewxonCameraConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            HzxLoger.HzxLog("onServiceConnected----componentName:"  + componentName);
            isBindService = true;
            JewxonCameraService.LocalServiceBinder localServiceBinder = (JewxonCameraService.LocalServiceBinder) iBinder;
            mService = localServiceBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBindService = false;
            HzxLoger.HzxLog("onServiceDisconnected");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            isBindService = false;
            HzxLoger.HzxLog("onBindingDied");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMyJewxonCameraConn);
        stopService(mJewxon_service);
    }
}
