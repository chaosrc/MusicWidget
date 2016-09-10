package com.example.chao.musicwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by chao on 8/5/16.
 */
public class WidgetBroadcast extends AppWidgetProvider {
    private String TAG = WidgetBroadcast.class.getSimpleName();
    public static final String WIDGET_START="com.example.music_widget.START";
    public static String PAUSE="com.example.music_widget.PAUSE";
    public static String WIDGET_NEXT="com.example.music_widget.NEXT";
    public static String WIDGET_PREVIOUS="com.example.music_widget.PREVIEW";
    public static String WIDGET_CREATE="com.example.music_widget.CREATE";
    private static Boolean state=false;
    private static Boolean DEFAULT=false;


    @Override
    public void onReceive(Context context, Intent intent) {
       // boolean state=false;
        super.onReceive(context, intent);
//        if(TextUtils.equals(intent.getAction(),WIDGET_CREATE)){
//            Log.d("MainActivity","wiget");
//        }

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.widget_layout);

        //收到widget的开始或暂停点击事件后发送广播给service，state为false为暂停反之为开始
        if(TextUtils.equals(intent.getAction(),WIDGET_START)){
            if(!state) {
                context.sendBroadcast(getIntentForService(MusicService.SERVICE_PLAY,state));
                remoteViews.setImageViewResource(R.id.music_start,R.drawable.ic_pause_white_24dp);
                Log.d(TAG,"start");
                state=true;
            }else{
                Log.d(TAG,"pause");
                context.sendBroadcast(getIntentForService(MusicService.SERVICE_PLAY,state));
                remoteViews.setImageViewResource(R.id.music_start,R.drawable.ic_play_arrow_white_24dp);
                state=false;
            }
        }
        //
        if(TextUtils.equals(intent.getAction(),WIDGET_NEXT)){
            state=true;
            context.sendBroadcast(getIntentForService(MusicService.SERVICE_NEXT,DEFAULT));
            remoteViews.setImageViewResource(R.id.music_start,R.drawable.ic_pause_white_24dp);

        }
        //
        if(TextUtils.equals(intent.getAction(),WIDGET_PREVIOUS)){
            state=true;
            context.sendBroadcast(getIntentForService(MusicService.SERVICE_PREViOUS,DEFAULT));
            remoteViews.setImageViewResource(R.id.music_start,R.drawable.ic_pause_white_24dp);

        }
        if(TextUtils.equals(intent.getAction(),MainActivity.PLAY_ANY)){
            state=true;
            remoteViews.setImageViewResource(R.id.music_start,R.drawable.ic_pause_white_24dp);

        }
        if(TextUtils.equals(intent.getAction(),MusicService.MUSIC_DATA)){
            remoteViews.setTextViewText(R.id.music_name,intent.getStringExtra("TITLE"));
        }


        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        ComponentName componentName=new ComponentName(context,WidgetBroadcast.class);
        appWidgetManager.updateAppWidget(componentName,remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intentService=new Intent(context,MusicService.class);
        context.startService(intentService);
        RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.widget_layout);

        views.setOnClickPendingIntent(R.id.music_start,getIntentForRemoteView(context,WIDGET_START));
        views.setOnClickPendingIntent(R.id.music_next,getIntentForRemoteView(context,WIDGET_NEXT));
        views.setOnClickPendingIntent(R.id.music_previous,getIntentForRemoteView(context,WIDGET_PREVIOUS));

        appWidgetManager.updateAppWidget(appWidgetIds,views);

    }

    //设置发送给服务的Intent
    public Intent getIntentForService(String action,boolean mState){
        Intent intent =new Intent();
        intent.setAction(action);
        intent.putExtra("state",mState);

        return intent;

    }
    //设置RemoteView的PendingIntent
    public PendingIntent getIntentForRemoteView(Context context,String action){
        Intent intent=new Intent();
        intent.setClass(context,WidgetBroadcast.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context,0,intent,0);

    }

}
