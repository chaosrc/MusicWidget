package com.example.chao.musicwidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by chao on 8/5/16.
 */
public class MusicService extends Service {
    private static final String TAG=MusicService.class.getSimpleName();
    public static final String SERVICE_PLAY="com.example.music_service.SERVICE_PLAY";
    public static final String SERVICE_NEXT="com.example.music_service.SERVICE_NEXT";
    public static final String SERVICE_PREViOUS="com.example.music_service.SERVICE_PREVIOUS";
    public static final String MUSIC_DATA="com.example.music_service.MUSIC_DATA";


    public  BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if (TextUtils.equals(intent.getAction(), SERVICE_PLAY)) {
                    if(!intent.getBooleanExtra("state",true))
                    {
                        Log.d(TAG,"play");
                        mMusic.mMusicPlayer.start();
                        MusicService.this.sendBroadcast(mMusic.getMusicIntent());
                    }
                    else if( mMusic.mMusicPlayer.isPlaying()){
                        mMusic.mMusicPlayer.pause();
                    }
                }
                if(TextUtils.equals(intent.getAction(),SERVICE_NEXT)){
                    Log.d(TAG,"NEXT");
                    mMusic.playNext();
                    MusicService.this.sendBroadcast(mMusic.getMusicIntent());

                }
                if(TextUtils.equals(intent.getAction(),SERVICE_PREViOUS)){
                    Log.d(TAG,"previous");
                    mMusic.playPrevious();
                    MusicService.this.sendBroadcast(mMusic.getMusicIntent());

                }
                if(TextUtils.equals(intent.getAction(),MainActivity.PLAY_ANY)){
                    Log.d(TAG,"play any");
                    mMusic.prepare(intent.getIntExtra("NUMBER",0));
                    mMusic.start();
                    MusicService.this.sendBroadcast(mMusic.getMusicIntent());

                }
            }
        }
    };
    //private static MediaPlayer musicPlayer;
    private Music mMusic;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusic=new Music(this);
        mMusic.prepare(0);
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        IntentFilter filter=new IntentFilter();
        filter.addAction(SERVICE_NEXT);
        filter.addAction(SERVICE_PREViOUS);
        filter.addAction(SERVICE_PLAY);
        filter.addAction(MainActivity.PLAY_ANY);
        registerReceiver(receiver,filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mMusic.mMusicPlayer.stop();
    }

    public static class Music{
        private String[] musicFileName;
        private AssetManager am;
        private String currentPlaying;
        private int counter=0;
        private MediaPlayer mMusicPlayer;

        Music( Context context){
            //得到音乐文件名数组
            am=context.getAssets();
            try {
                musicFileName=am.list("music");
                Log.d(TAG, Arrays.toString(musicFileName));
                currentPlaying=musicFileName[0];
                mMusicPlayer=new MediaPlayer();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context,"没有找到音乐文件",Toast.LENGTH_SHORT).show();
            }
        }

        public void setCurrentPlaying(int name){
            if(name>=0||name<musicFileName.length){
                counter=name;
                currentPlaying="music/"+musicFileName[counter];
                Log.d(TAG, "counter:"+counter+"  "+currentPlaying);
            }
        }

        public AssetFileDescriptor getAssetFD(){
            try {
                AssetFileDescriptor afd =am.openFd(currentPlaying);
                //am.close();
                return afd;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public FileDescriptor getFileDescriptor(){
            FileDescriptor fd=getAssetFD().getFileDescriptor();
            Log.d(TAG, "getFileDescriptor");
            return fd;
        }

        public void prepare(int fileName){
            counter=fileName;
            Log.d(TAG, "playing"+"  counter:"+counter);
            setCurrentPlaying(counter);
            if(mMusicPlayer.isPlaying()){
                mMusicPlayer.stop();
            }
            mMusicPlayer.reset();
            Log.d(TAG, "media player reset");
            try {
                mMusicPlayer.setDataSource(getFileDescriptor(),getAssetFD().getStartOffset(),
                       getAssetFD().getLength());
                Log.d(TAG, "setDataSource");
                mMusicPlayer.prepare();
                Log.d(TAG, "media prepare");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start(){
            mMusicPlayer.start();
        }

        public void playNext(){
            counter++;
            if(counter>=musicFileName.length){
                counter=0;
            }
            prepare(counter);
            start();
        }
        public void playPrevious(){
            counter--;
            if(counter<0){
               counter=musicFileName.length-1;
            }
            prepare(counter);
            start();

        }

        public MediaMetadataRetriever getMediaData(){
            MediaMetadataRetriever mediaData=new MediaMetadataRetriever();
            mediaData.setDataSource(getFileDescriptor(),getAssetFD().getStartOffset(),getAssetFD().getLength());
            return mediaData;
        }

        public String getCurrentMusicTitle(){
            return getMediaData().extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }

        public String getCurrentMusicArtist(){
            return getMediaData().extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        }

        public byte[] getAblumGraphic(){
            return getMediaData().getEmbeddedPicture();
        }

        public Intent getMusicIntent(){
            Intent intent=new Intent();
            intent.setAction(MUSIC_DATA);
            intent.putExtra("TITLE",getCurrentMusicTitle());
            intent.putExtra("ARTIST",getCurrentMusicArtist());
            intent.putExtra("PICTURE",getAblumGraphic());
            return intent;
        }

        public int getMusicNumber(){
            return musicFileName.length;
        }

    }
}
