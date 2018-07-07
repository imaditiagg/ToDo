package com.example.aditi.todo_list;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public static final String TITLE ="title";
    public static final String DESCRIPTION ="description";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String ID="id";
    public static final  String CATEGORY="CATEGORY";
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        String title = e1.getText().toString();
        String description = e2.getText().toString();
        String date = t1.getText().toString();
        String time = t2.getText().toString();

        Bundle b2 = new Bundle();
        b2.putString(this.TITLE,title);
        b2.putString(this.DESCRIPTION,description);
        b2.putString(this.DATE,date);
        b2.putString(this.TIME,time);
        b2.putLong(this.ID,id);
        b2.putString(this.CATEGORY,category);

        // send the new edited bundle of data
        Intent intent = new Intent();
        intent.putExtras(b2);
        setResult(MainActivity.EDIT_RESULT_CODE,intent);
        finish();

    }


    public void changeDate(View view){

        String temp = date;
        String[] splitString = temp.split("-");

        year = Integer.parseInt(splitString[2]);
        month = Integer.parseInt(splitString[1]);
        day = Integer.parseInt(splitString[0]);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        t1.setText(dayOfMonth + "-" + (monthOfYear+1) + "-" + year);

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
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        t2.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }


}
