package com.example.aditi.todo_list;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Telephony.Mms.Part.FILENAME;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences pref_reminder = context.getSharedPreferences(FILENAME,MODE_PRIVATE);
        if(pref_reminder.getInt("ReminderFlag",0)==1){

            PermissionSettings.reminderFlag=1;
        }
        else {
            PermissionSettings.reminderFlag=0;
        }
        SharedPreferences pref_sound = context.getSharedPreferences("sound", MODE_PRIVATE);
        String sound = pref_sound.getString("sound_uri",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
        Uri alarmSound = Uri.parse(sound);

        final Bundle bundle = intent.getExtras();
        if(PermissionSettings.reminderFlag==1) {
            //Alarm
            try {
                if (bundle != null) {
                    //  Toast.makeText(context,"Alarm",Toast.LENGTH_LONG).show();
                    String title = "";
                    int completed = 0;

                    long id = bundle.getLong(MainActivity.ID);
                    String[] selectionargs = {id + ""};
                    ItemOpenHelper openHelper = ItemOpenHelper.getInstance(context.getApplicationContext());
                    SQLiteDatabase database = openHelper.getReadableDatabase();
                    Cursor cursor = database.query(Contract.Item.TABLE_NAME, null, Contract.Item.COL_ID + " = ? "
                            , selectionargs, null, null, null);

                    while (cursor.moveToNext()) {
                        title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
                        completed = cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_COMPLETED));
                    }
                    if (completed == 0) { //if task is not completed yet
                        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("mychannelid", "Notes Channel", NotificationManager.IMPORTANCE_HIGH);
                            manager.createNotificationChannel(channel);
                            channel.setShowBadge(true); //to show badge


                        }

                        String GROUP_KEY = "TODO_ITEM_NOTIFICATION";
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "mychannelid");

                        builder.setContentTitle("REMINDER");
                        builder.setContentText(title);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder.setSmallIcon(R.drawable.todoicon);
                            builder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
                        } else {
                            builder.setSmallIcon(R.drawable.todoicon);

                        }
                        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.todoicon));
                        builder.setGroup(GROUP_KEY); //Group notifications
                        builder.setGroupSummary(true);
                        builder.setAutoCancel(true); //remove the notification when user taps it
                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(title)); //Expandable notification
                        if(alarmSound==null) {
                            Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//Set sound
                            builder.setSound(uriSound);
                        }
                        else {
                            builder.setSound(alarmSound);
                        }
                        if (Build.VERSION.SDK_INT >= 21)
                            builder.setVibrate(new long[]{250, 250, 250, 250});


                        Intent intent2 = new Intent(context, ItemDescription.class);
                        intent2.putExtras(bundle);
                        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, (int) id, intent2, 0);
                        builder.setContentIntent(pendingIntent2); //setNotification click intent

                        Intent intent3 = new Intent(context, Edit_Activity.class);
                        intent3.putExtras(bundle);
                        PendingIntent pendingIntent3 = PendingIntent.getActivity(context, (int) id, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.addAction(R.drawable.todoicon, "Edit", pendingIntent3); //add action


                        Notification notification = builder.build();
                        manager.notify((int) id, notification);

                    }
                }

            } catch (Exception e) {
                Log.i("Exception", e + "");
            }
        }



    }
}
