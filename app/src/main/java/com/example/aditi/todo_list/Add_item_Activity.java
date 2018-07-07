package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;



public class Add_item_Activity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    EditText editText1, editText2;
    Spinner spinner;
    TextView dateTextView, timeTextView;
    Button setDateButton, setTimeButton;
    String title = "";
    String desc = "";
    String date = "";
    String time = "";
    String spinnner_item = "";

    private Calendar calendar;
    private int year, month, day, hour, minute;
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String CATEGORY = "category";
    public static final String ID="id";
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText1 = findViewById(R.id.titleEdittext);
        editText2 = findViewById(R.id.descripEditText);
        dateTextView = findViewById(R.id.dateTextView);
        setDateButton = findViewById(R.id.setdateButton);
        timeTextView = findViewById(R.id.timeTextView);
        setTimeButton = findViewById(R.id.setTimeButton);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // Spinner element
        spinner = findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Other");
        categories.add("Work");
        categories.add("Home");
        categories.add("Personal");
        categories.add("College");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //Implicit Intent to handle SEND action
        intent = getIntent();

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null) {
            editText2.setText(text);
            editText2.setSelection(editText2.getText().length());
        }
    }


    public void setDate(View view) {


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void setTime(View view) {


        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        timeTextView.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }


    public void saveItem(View view) {


        title = editText1.getText().toString();
        desc = editText2.getText().toString();
        date = dateTextView.getText().toString();
        time = timeTextView.getText().toString();
        if (time.equals("") || title.equals("") || desc.equals("") || date.equals("")) {
            Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        add(title, desc, date, time, spinnner_item); //add to database

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinnner_item = adapterView.getItemAtPosition(i).toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //TODO
    }

    //addtoDB

    public void add(String title, String desc, String date, String time, String category) {

        Items item = new Items(title, desc, date, time, category);

        ItemOpenHelper openHelper = ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Item.COL_TITLE, item.getTitle());
        contentValues.put(Contract.Item.COL_DESC, item.getDescription());
        contentValues.put(Contract.Item.COL_DATE, item.getDate());
        contentValues.put(Contract.Item.COL_TIME, item.getTime());
        contentValues.put(Contract.Item.COL_CATEGORY, item.getCategory());

        long id = database.insert(Contract.Item.TABLE_NAME, null, contentValues);


        Bundle b2 = new Bundle();
        b2.putLong(ID,id);
        //Set alarm for that date and time
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(this,AlarmReceiver.class);
        intent1.putExtras(b2);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(this,(int)id,intent1,0);
        setAlarm(item.getDate(),item.getTime(),alarmManager,pendingIntent);

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

