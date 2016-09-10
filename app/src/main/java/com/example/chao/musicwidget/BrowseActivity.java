package com.example.chao.musicwidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by chao on 9/8/16.
 */
public class BrowseActivity extends AppCompatActivity{
    private Notification notification;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建Builder，设置Notification内容
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Music Title")
                .setContentText("content");
        Notification notification= builder.build();
        //builder.setStyle()
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(001,notification);
//        prepareNotification(this);
//        NotificationManager manager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(002,notification);


    }

    public void prepareNotification(Context context){
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context);
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.notification_layout);

        remoteViews.setOnClickPendingIntent(R.id.notify_music_start,getPendingIntent(context,NotificationBroadcast.NOTIFICATIONACTION));
        remoteViews.setOnClickPendingIntent(R.id.notify_music_next,getPendingIntent(context,MusicService.SERVICE_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.notify_music_previous,getPendingIntent(context,MusicService.SERVICE_PREViOUS));

        mBuilder.setContent(remoteViews);
        mBuilder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Music Title")
                .setContentText("content");
        mBuilder.setContentIntent(PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0));
        notification = mBuilder.build();
        Log.d("MainActivity","prepare notification");


    }

    public PendingIntent getPendingIntent(Context context,String action){
        Intent intent=new Intent(context,NotificationBroadcast.class);
        intent.setAction(NotificationBroadcast.NOTIFICATIONACTION);
        return PendingIntent.getBroadcast(context,0,intent,0);

    }
}
