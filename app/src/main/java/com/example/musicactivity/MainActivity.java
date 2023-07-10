package com.example.musicactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static SeekBar seekBar;
    private static TextView tv_progress, tv_total;
    private MusicService.MusicControl musicControl;
    private Intent intent;
    private MyServiceConn myConn;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(myConn != null){
            unbindService(myConn);
            myConn = null;
        }
    }

    private void init(){
        tv_progress = (TextView) findViewById(R.id.tv_time);
        tv_total = (TextView) findViewById(R.id.tv_totaltime);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        intent = new Intent(this,MusicService.class);
        myConn = new MyServiceConn();
        bindService(intent,myConn,BIND_AUTO_CREATE);
        //(唱片旋转)
        ImageView iv_music = (ImageView) findViewById(R.id.iv_record);
        animator = ObjectAnimator.ofFloat(iv_music,"rotation",0f, 360f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                //播放
                musicControl.play();
                animator.start();
                break;
            case R.id.btn_pause:
                //暂停
                musicControl.pause();
                animator.pause();
                break;
            case R.id.btn_continue:
                //继续
                musicControl.continuePlayer();
                animator.start();
                break;
            case R.id.btn_exit:
                //退出
                if(myConn!=null){
                    unbindService(myConn);
                    myConn = null;
                }
                finish();
                break;
            default:
                break;
        }
    }

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //接收消息，设置到进度条的对应位置
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);

            int minute = duration/1000/60;
            int second = duration/1000%60;
            String strMin = null;
            String strSec = null;
            if(minute<10){
                strMin = "0" + minute;
            }
            else {
                strMin = minute + "";
            }
            if (second<10){
                strSec = "0" + second;
            }
            else {
                strSec = second + "";
            }
            tv_total.setText(strMin+":"+strSec);

            minute = currentPosition/1000/60;
            second = currentPosition/1000%60;
            if(minute<10){
                strMin = "0" + minute;
            }
            else {
                strMin = minute + "";
            }
            if (second<10){
                strSec = "0" + second;
            }
            else {
                strSec = second + "";
            }
            tv_progress.setText(strMin+":"+strSec);
        }
    };

    class MyServiceConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicControl = (MusicService.MusicControl) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}