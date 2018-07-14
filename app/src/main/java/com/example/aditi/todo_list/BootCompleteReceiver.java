package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.example.aditi.todo_list.Add_item_Activity.ID;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        //set alarms again after boot completion
        ItemOpenHelper openHelper = ItemOpenHelper.getInstance(context);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,  null,null,null,null,null);
        while(cursor.moveToNext()){

            long id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
            String date =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            String time = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            Intent intent1 = new Intent(context,AlarmReceiver.class);
            Bundle b2 = new Bundle();
            b2.putLong(ID,id);
            intent1.putExtras(b2);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context,(int)id ,intent1,0);
            setAlarm(date,time,alarmManager,pendingIntent);


        }
        cursor.close();



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
        calendar.set(year,month-1,day,hour,minute-1);

        long alarm_time = calendar.getTimeInMillis();
        manager.set(AlarmManager.RTC_WAKEUP,alarm_time,pendingIntent);

    }

}

