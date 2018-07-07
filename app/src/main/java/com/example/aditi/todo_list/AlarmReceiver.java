package com.example.aditi.todo_list;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final Bundle bundle = intent.getExtras();

        //Alarm
        try{
            if(bundle!=null){
                Toast.makeText(context,"Alarm",Toast.LENGTH_LONG).show();
                String title="",description="";

                long id = bundle.getLong(MainActivity.ID);
                String [] selectionargs = {id+""};
                ItemOpenHelper openHelper =ItemOpenHelper.getInstance(context.getApplicationContext());
                SQLiteDatabase database= openHelper.getReadableDatabase();
                Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_ID + " = ? "
                        ,selectionargs,null,null,null);

                while (cursor.moveToNext()) {
                    title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
                    //description = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
                }

                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    NotificationChannel channel = new NotificationChannel("mychannelid","Notes Channel",NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                    channel.setShowBadge(true);

                }

                String GROUP_KEY = "com.android.example.TODO_ITEM";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"mychannelid");

                builder.setContentTitle("REMINDER");
                builder.setContentText(title);
                builder.setSmallIcon(R.drawable.ic_launcher_foreground);

                builder.setGroup(GROUP_KEY);
                builder.setAutoCancel(true); //remove the notification when user taps it
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));

                builder.setPriority(Notification.PRIORITY_MAX);
                if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[]{250, 250, 250, 250});


                Intent intent2 = new Intent(context,ItemDescription.class);
                intent2.putExtras(bundle);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(context, (int)id ,intent2,0);
                builder.setContentIntent(pendingIntent2);

                Notification notification = builder.build();
                manager.notify(1,notification);

            }

        }
        catch (Exception e){
            Log.i("Exception",e+"");
        }



    }
}
