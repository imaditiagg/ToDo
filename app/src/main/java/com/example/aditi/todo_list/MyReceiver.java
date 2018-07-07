package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {
    String message;
    String senderNum;
    String msgTime;
    String msgDate;

    @Override
    public void onReceive(Context context, Intent intent) {


        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.i("my receiver","Started");

        final Bundle bundle = intent.getExtras();
        //TODO from SMS
        try {
            if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        senderNum = phoneNumber;
                        message = currentMessage.getDisplayMessageBody();
                        String format = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                        msgTime = sdf.format(Calendar.getInstance().getTime());
                        msgDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    }


                }
            }
              catch (Exception e){
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);


                }


        if(senderNum!=null && message!=null && msgDate!=null && msgDate!=null && msgTime!=null){

            String title =senderNum;
            String desc =message;
            String  date=msgDate;
            String time=msgTime;
            String category="Other";
            add(title,desc,date,time,category,context);

        }





    }

    public void add(String title,String desc,String date,String time,String category,Context context){
       Items item =new Items(title,desc,date,time,category);

        ItemOpenHelper openHelper = ItemOpenHelper.getInstance(context.getApplicationContext());
        SQLiteDatabase database=  openHelper.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(Contract.Item.COL_TITLE,item.getTitle());
        contentValues.put(Contract.Item.COL_DESC,item.getDescription());
        contentValues.put(Contract.Item.COL_DATE,item.getDate());
        contentValues.put(Contract.Item.COL_TIME,item.getTime());
        contentValues.put(Contract.Item.COL_CATEGORY,item.getCategory());

        long id = database.insert(Contract.Item.TABLE_NAME,null,contentValues);
        if(id>-1) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(context,AlarmReceiver.class);
            Bundle b2 = new Bundle();
            b2.putLong(MainActivity.ID,id);
            intent1.putExtras(b2);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context,(int)id,intent1,0);
            Log.i("Time",item.getTime());
            setAlarm(item.getDate(), item.getTime(), alarmManager, pendingIntent); //call set alarm method
        }

        }

    public void setAlarm(String d,String t, AlarmManager manager,PendingIntent pendingIntent){


        String[] splitString1 = d.split("-");
        int year = Integer.parseInt(splitString1[2]);
        int month = Integer.parseInt(splitString1[1]);
        int day = Integer.parseInt(splitString1[0]);

        String[] splitString2 = t.split(":");
        int hour = Integer.parseInt(splitString2[0]);
        int minute = Integer.parseInt(splitString2[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day,hour,minute+3);
        Log.i("Minutes+3",minute+3+"");

        long alarm_time = calendar.getTimeInMillis();

        manager.set(AlarmManager.RTC_WAKEUP,alarm_time,pendingIntent);
    }
}
