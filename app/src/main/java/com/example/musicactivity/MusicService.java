package com.example.musicactivity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer player;
    private Timer timer;
    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
    }

    public void addTimer(){
        if(timer == null){
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(player == null){
                        return;
                    }
                    int duration = player.getDuration();
                    int currentPosition = player.getCurrentPosition();
                    //将duration与currentPosition通过handler发送
                    Message msg = MainActivity.handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    msg.setData(bundle);
                    MainActivity.handler.sendMessage(msg);
                }
            };
            timer.schedule(task,5,500);
        }
    }

    class MusicControl extends Binder{
        public void play(){
            try{
                player.reset();
                player = MediaPlayer.create(getApplicationContext(),R.raw.nightwillowherb);
                player.start();
                addTimer();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void pause(){
            player.pause();
        }

        public void continuePlayer(){
            player.start();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(player!=null){
            if(player.isPlaying()){
                player.stop();
            }
            player.release();
            player = null;
        }
    }
}