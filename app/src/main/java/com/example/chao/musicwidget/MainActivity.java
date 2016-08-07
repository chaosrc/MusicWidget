package com.example.chao.musicwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent;
    private boolean playState=false;
    private ImageButton startButton;
    private String TAG=MainActivity.class.getSimpleName();
    private TextView musicName;
    private ImageView picture;
    public static String PLAY_ANY="com.example.music_widget.PLAY_ANY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启服务
        intent = new Intent(this,MusicService.class);
        startService(intent);

        musicName= (TextView) findViewById(R.id.music_name);
        picture=(ImageView) findViewById(R.id.picture);
        findViewById(R.id.music_previous).setOnClickListener(this);
        findViewById(R.id.music_next).setOnClickListener(this);
        startButton=(ImageButton) findViewById(R.id.music_start);
        startButton.setOnClickListener(this);

        IntentFilter filter=new IntentFilter();
        filter.addAction(MusicService.SERVICE_NEXT);
        filter.addAction(MusicService.SERVICE_PREViOUS);
        filter.addAction(MusicService.SERVICE_PLAY);
        filter.addAction(MusicService.MUSIC_DATA);
        filter.addAction(PLAY_ANY);
        registerReceiver(receiver,filter);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,getMusicInfo());
        ListView listView= (ListView) findViewById(R.id.music_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               sendBroadcast(getItemIntent(PLAY_ANY,position));
                Log.d(TAG,"POSITION:"+position+"id: "+id);
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(intent);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.music_start:
                //setStartButton();
                sendBroadcast(getSendIntent(WidgetBroadcast.WIDGET_START));
                Log.d(TAG,"send broadcast");
                break;
            case R.id.music_next:
                sendBroadcast(getSendIntent(WidgetBroadcast.WIDGET_NEXT));
                break;
            case R.id.music_previous:
                sendBroadcast(getSendIntent(WidgetBroadcast.WIDGET_PREVIOUS));

        }

    }

    public void setStartButton(){
        if(!playState){
            startButton.setImageResource(R.drawable.ic_pause_white_24dp);
            playState=true;
        }else{
            startButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            playState=false;
        }
    }
    public Intent getSendIntent(String action,int i){
        Intent intent=new Intent(this,WidgetBroadcast.class);
        intent.setAction(action);
        intent.putExtra("NUMBER",i);
        return intent;
    }

    public Intent getItemIntent(String action,int i){
        Intent intent=new Intent();
        intent.setAction(action);
        intent.putExtra("NUMBER",i);
        return intent;
    }

    public Intent getSendIntent(String action){
        return getSendIntent(action,0);
    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                if(TextUtils.equals(intent.getAction(),MusicService.SERVICE_PLAY)){
                    setStartButton();
                }
                if(TextUtils.equals(intent.getAction(),MusicService.SERVICE_NEXT)||
                        TextUtils.equals(intent.getAction(),MusicService.SERVICE_PREViOUS)||
                        TextUtils.equals(intent.getAction(),PLAY_ANY)){
                    startButton.setImageResource(R.drawable.ic_pause_white_24dp);
                    playState=true;
                }
                if(TextUtils.equals(intent.getAction(),MusicService.MUSIC_DATA)){
                    musicName.setText(intent.getStringExtra("TITLE"));
                    if(intent.getByteArrayExtra("PICTURE").length!=0){
                        picture.setImageBitmap(BitmapFactory.decodeByteArray(
                                intent.getByteArrayExtra("PICTURE"),0,intent.getByteArrayExtra("PICTURE").length
                        ));
                    }


                }
            }
        }
    };


    public String[] getMusicInfo(){
        MusicService.Music music=new MusicService.Music(this);
        int n=music.getMusicNumber();
        String[] musicName=new String[n];
        for(int i=0;i<n;i++){
            music.setCurrentPlaying(i);
            musicName[i]=music.getCurrentMusicTitle();
        }

        return musicName;
    }
}
