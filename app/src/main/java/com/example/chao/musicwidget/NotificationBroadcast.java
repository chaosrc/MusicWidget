package com.example.chao.musicwidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Created by chao on 9/8/16.
 */
public class NotificationBroadcast extends BroadcastReceiver{
    public static String NOTIFICATIONACTION="com.chao.example.notification_start";
    private Notification notification;
    private boolean isPrepare=false;
    private boolean playing=false;
    private NotificationManager manager;
    private NotificationCompat.Builder mBuilder;


    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews rmViews=new RemoteViews(context.getPackageName(), R.layout.notification_layout);


        if(!isPrepare){
            prepareNotification(context,rmViews);
            Log.d("MainActivity",isPrepare+" isPrepare");
        }
        if(TextUtils.equals(intent.getAction(),NOTIFICATIONACTION)){
            Log.d("MainActivity",playing+" playing");
//            setStartButton(rmViews);
//            notificationUpdate(context,rmViews);
            Intent intentService=new Intent();
            intentService.setAction(MusicService.SERVICE_PLAY);
            context.sendBroadcast(intentService);

        }
//        if(TextUtils.equals(intent.getAction(),WidgetBroadcast.WIDGET_START)){
//            Log.d("MainActivity","notification start"+"  "+playing);
//            setStartButton(rmViews);
//            notificationUpdate(context,rmViews);
//
//        }
        if(TextUtils.equals(intent.getAction(),MusicService.MUSIC_DATA)){
            String title= intent.getStringExtra("TITLE");
            playing=intent.getBooleanExtra("MUSIC_STATE",false);

            rmViews.setTextViewText(R.id.notify_music_name,title);
            rmViews.setImageViewBitmap(R.id.notify_picture, BitmapFactory.decodeByteArray(
                    intent.getByteArrayExtra("PICTURE"),0,intent.getByteArrayExtra("PICTURE").length));
            setStartButton(rmViews);
            notificationUpdate(context,rmViews);

        }

    }

    public void notificationUpdate(Context context, RemoteViews remoteViews){
        prepareNotification(context,remoteViews);

        notification = mBuilder.build();
        Log.d("MainActivity","prepare notification");
        isPrepare=true;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(002,notification);
        Log.d("MainActivity","notification update");
    }

    public void setStartButton(RemoteViews remoteViews){
        Log.d("MainActivity","setStartButton"+" 1"+playing);
        if(playing){
            remoteViews.setImageViewResource(R.id.notify_music_start,R.drawable.ic_pause_white_24dp);
           // playing=true;
        }else{
            remoteViews.setImageViewResource(R.id.notify_music_start,R.drawable.ic_play_arrow_white_24dp);
           // playing=false;
        }
        Log.d("MainActivity","setStartButton"+" 2"+playing);
    }

    public void prepareNotification(Context context,RemoteViews remoteViews){
        mBuilder = new NotificationCompat.Builder(context);

        remoteViews.setOnClickPendingIntent(R.id.notify_music_start,getPendingIntent(context,NOTIFICATIONACTION));
        remoteViews.setOnClickPendingIntent(R.id.notify_music_next,getPendingIntent(context,MusicService.SERVICE_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.notify_music_previous,getPendingIntent(context,MusicService.SERVICE_PREViOUS));

        mBuilder.setContent(remoteViews);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentText("music");
        mBuilder.setContentText("Hello");

//        Intent resultIntent=new Intent(context,MainActivity.class);
//        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);

        mBuilder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0));


    }

    public PendingIntent getPendingIntent(Context context,String action){
//        Intent intent=new Intent(context,NotificationBroadcast.class);
        Intent intent=new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(context,0,intent,0);

    }


}
