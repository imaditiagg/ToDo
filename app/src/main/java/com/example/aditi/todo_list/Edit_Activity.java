package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Edit_Activity extends AppCompatActivity {
    EditText e1,e2;
    TextView t1,t2;
    Bundle b;
    String title,date,description,time,category;
    int year,month,day,hour,minute;

    public static final String ID="id";
    FloatingActionButton fab;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.whiteback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );

        fab=(FloatingActionButton) findViewById(R.id.saveEdit);
        fab.setImageResource(R.drawable.whitetick);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEditItem(view);
            }
        });

        e1= findViewById(R.id.titleEdittext);
        e2= findViewById(R.id.descripEditText);
        t1 =findViewById(R.id.dateEditTextView);
        t2=findViewById(R.id.timeEditTextView);
        //obtain id from bundle and hence display data from database
        Intent intent = getIntent();
        b = intent.getExtras();
         id = b.getLong(MainActivity.ID);

        String [] selectionargs = {id+""};
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_ID + " = ? "
                ,selectionargs,null,null,null);
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            description = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            time = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            category = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
        }
        e1.setText(title);
        e2.setText(description);
        t1.setText(date);
        t2.setText(time);

        e1.setSelection(e1.getText().length());
        e2.setSelection(e2.getText().length());

    }

    public void saveEditItem(View view){

        String title1 = e1.getText().toString();
        String description1 = e2.getText().toString();
        String date1 = t1.getText().toString();
        String time1 = t2.getText().toString();


        update(title1, description1, date1, time1, category); //add to database
        if(b.getInt("Request_Code")==MainActivity.EDIT_REQUEST_CODE){
            // send the new edited bundle of data
            Intent intent = new Intent();
            setResult(MainActivity.EDIT_RESULT_CODE,intent);
            finish();
        }
        else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }


    }


    public void changeDate(View view){

        String temp = date;
        String[] splitString = temp.split("-");

        year = Integer.parseInt(splitString[2]);
        month = Integer.parseInt(splitString[1]);
        day = Integer.parseInt(splitString[0]);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String day;
                        day=String .valueOf(dayOfMonth);
                        if(dayOfMonth<10){

                            day="0"+day;
                        }

                        t1.setText(day + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, year, month-1, day);
        datePickerDialog.show();

    }


    public void changeTime(View view){

        String temp = time;
        String[] splitString = temp.split(":");

        hour = Integer.parseInt(splitString[0]);
        minute = Integer.parseInt(splitString[1]);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {


                        t2.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    //update in DB
    public void update(String title1, String desc1, String date1, String time1, String category1) {
        Items item = new Items(title1,desc1,date1,time1,category1);

        ItemOpenHelper openHelper1 = ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database1=  openHelper1.getWritableDatabase();
        ContentValues contentValues =new ContentValues();

        contentValues.put(Contract.Item.COL_TITLE, item.getTitle());
        contentValues.put(Contract.Item.COL_DESC, item.getDescription());
        contentValues.put(Contract.Item.COL_DATE, item.getDate());
        contentValues.put(Contract.Item.COL_TIME, item.getTime());
        contentValues.put(Contract.Item.COL_CATEGORY, item.getCategory());

        String[] selectionArgs = {id + ""};
        database1.update(Contract.Item.TABLE_NAME,contentValues,Contract.Item.COL_ID + " = ?",selectionArgs);

        //if date or time has changed,set the alarm again
        if(!item.getDate().equals(date)  || !item.getTime().equals(time)) {
            Bundle b2 = new Bundle();
            b2.putLong(ID, id);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(this, AlarmReceiver.class);
            intent1.putExtras(b2);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, (int) id, intent1, 0);
            alarmManager.cancel(pendingIntent1);//cancel already set alarm


            Intent intent2 = new Intent(this, AlarmReceiver.class);
            intent2.putExtras(b2);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, (int) id, intent2, 0);
            //set new alarm

                setAlarm(item.getDate(), item.getTime(), alarmManager, pendingIntent2);
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
        calendar.set(year,month-1,day,hour,minute-1);

        long alarm_time = calendar.getTimeInMillis();
        manager.set(AlarmManager.RTC_WAKEUP,alarm_time,pendingIntent);

    }



}
