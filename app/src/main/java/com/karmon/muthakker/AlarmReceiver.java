package com.karmon.muthakker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by karmo on 16/02/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MUTHAKKER";
    Intent intent;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context,Intent intent){
        Log.i(TAG,"BroadCastReceiver has received alarm intent");
        Intent service1 = new Intent(context,AlarmService.class);
        context.startService(service1);
    }
}
