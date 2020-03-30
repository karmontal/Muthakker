package com.karmon.muthakker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by karmo on 16/02/2016.
 */
public class AlarmService extends IntentService {
    private static final String TAG = "MUTHAKKER";
    String CHANNEL_ID = "my_channel_01";
    MediaPlayer mp;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    SharedPreferences times, thekrTyp;
    Resources res;
    NotificationCompat.Builder builder;

    public AlarmService(){
        super("AlarmService");
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.i(TAG, "Alarm Service has started");
        String msgText, fileName;
        Random r = new Random();
        SQLiteDatabase database = openOrCreateDatabase("MuthakkerDB", Context.MODE_PRIVATE,null);
        int thekr = r.nextInt(30);
        Cursor allrows = database.rawQuery("Select * from ATHKAR", null);
        //allrows.moveToFirst();
        allrows.moveToPosition(thekr);
        msgText = allrows.getString(1);
        fileName = allrows.getString(2);
        allrows.close();
        database.close();
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this,MainActivity.class);
        Intent tIntent = new Intent(this,AthkarSabah.class);
        Bundle bundle = new Bundle();
        bundle.putString("test", "test");
        mIntent.putExtras(bundle);
        tIntent.putExtras(bundle);

        //============== Shared Prefs =====================//
        times = getSharedPreferences("AM_H_ALARM", Context.MODE_PRIVATE);
        times = getSharedPreferences("AM_M_ALARM", Context.MODE_PRIVATE);
        times = getSharedPreferences("PM_H_ALARM", Context.MODE_PRIVATE);
        times = getSharedPreferences("PM_M_ALARM", Context.MODE_PRIVATE);
        thekrTyp = getSharedPreferences("TYP", 0);
        SharedPreferences.Editor editor = thekrTyp.edit();

        Calendar currentTime = Calendar.getInstance();

        if(times.getInt("AM_H_ALARM", 70) == currentTime.get(Calendar.HOUR_OF_DAY) && times.getInt("AM_M_ALARM", 70) == currentTime.get(Calendar.MINUTE)) {
            // ===================== AM ========================//
            editor.putInt("TYP",1);
            pendingIntent = PendingIntent.getActivity(context, 1, tIntent, PendingIntent.FLAG_ONE_SHOT);
            res = this.getResources();
            builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.minilogo)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                    .setTicker("أذكار الصباح")
                    .setAutoCancel(true)
                    .setContentTitle("أذكار الصباح")
                    .setContentText("اضغط هنا لأذكار الصباح")
                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/masa"));
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
        else if (times.getInt("PM_H_ALARM", 70) == currentTime.get(Calendar.HOUR_OF_DAY) && times.getInt("PM_M_ALARM", 70) == currentTime.get(Calendar.MINUTE)) {

            // ===================== PM ========================//
            editor.putInt("TYP",2);
            pendingIntent = PendingIntent.getActivity(context, 2, tIntent, PendingIntent.FLAG_ONE_SHOT);
            res = this.getResources();
            builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.minilogo)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                    .setTicker("أذكار المساء")
                    .setAutoCancel(false)
                    .setContentTitle("أذكار المساء")
                    .setContentText("اضغط هنا لأذكار المساء")
                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/masa"));
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(2, builder.build());
        }
        else{
            // ===================== Repeater ========================//
//            pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            res = this.getResources();
//            builder = new NotificationCompat.Builder(this);
//            builder.setContentIntent(pendingIntent)
//                    .setSmallIcon(R.drawable.minilogo)
//                    .setLargeIcon(BitmapFactory.decodeResource(res,R.drawable.logo))
//                    .setTicker("المذكر:" + msgText)
//                    .setAutoCancel(true)
//                    .setContentTitle("لاتنسى ذكر الله")
//                    .setContentText(msgText)
//                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/v" + fileName + ".mp3")
//                    );
//            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(3, builder.build());
            Toast.makeText(getApplicationContext(),msgText,Toast.LENGTH_LONG).show();
            mp = MediaPlayer.create(getApplicationContext(),Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/v" + fileName));
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });
            mp.start();
        }
        editor.apply();

        Log.i(TAG,"Notification Sent " + String.valueOf(thekr));

    }
}
