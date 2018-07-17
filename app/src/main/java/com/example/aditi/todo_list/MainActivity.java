package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.aditi.todo_list.Add_item_Activity.ID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{


    ListView listView;
    LayoutInflater layoutInflater;
    ArrayList<Items> items;
    ItemAdaptor adapter;
    public static final String ID = "id";
    public static final int EDIT_RESULT_CODE=2;
    public static final int EDIT_REQUEST_CODE=2;
    FrameLayout rootLayout;
    RelativeLayout layout;
    static String deleted_title="",deleted_desc="",deleted_date="",deleted_cat="",deleted_time="";
    static int deleted_imp,deleted_completed;
    String selectedRingtone;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout=findViewById(R.id.rootLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //go to Add_item_Activity
                Intent intent =  new Intent(MainActivity.this,Add_item_Activity.class);
                startActivity(intent);

            }
        });


        listView =findViewById(R.id.listView);
        layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        items =new ArrayList<>();
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        adapter =new ItemAdaptor(MainActivity.this, items, new ButtonClickListener() {
            @Override
            public void rowButtonClicked(int position, Items item) {

                delete(position, item);
            }
        }, new CheckBoxClickListener() {
            @Override
            public void checkBoxClicked(Items item,int value) {
                setComplete(item,value);
            }
        });
        listView.setAdapter(adapter);
        displayall();

        }


    public boolean onCreateOptionsMenu(Menu menu) { //for showing menu to add new item
        getMenuInflater().inflate(R.menu.main_menu,menu);
        // Find the menuItem to add your SubMenu
        MenuItem myMenuItem = menu.findItem(R.id.sortitem);

        getMenuInflater().inflate(R.menu.sort_menu, myMenuItem.getSubMenu());
        MenuItem mymenu =menu.findItem(R.id.displayitem);
        getMenuInflater().inflate(R.menu.display_menu,mymenu.getSubMenu());

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //edit item
        if(requestCode==EDIT_REQUEST_CODE && resultCode==EDIT_RESULT_CODE) {
            displayall();

        }
        //delete from item_description activity
            else if(resultCode==ItemDescription.DELETE_RESULT_CODE){
            displayall();
            Bundle b = data.getExtras();
            long id = b.getLong(MainActivity.ID);

            Snackbar.make(fab, "Item Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            undo(); //undo fn
                        }
                    }).setActionTextColor(getResources().getColor(R.color.skyblue1)).show();
            //cancel alarm also
            // Toast.makeText(MainActivity.this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(this,AlarmReceiver.class);
            Bundle b2 = new Bundle();
            b2.putLong(ID,id);
            intent1.putExtras(b2);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(MainActivity.this,(int)id,intent1,0);
            alarmManager.cancel(pendingIntent);


            }
         else if (requestCode == 0000)
         {

             Uri uri = null;
             if (data != null) {
                 uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
             }

             if (uri != null)
            {
                this.selectedRingtone = uri.toString();
                SharedPreferences prefs = getSharedPreferences("sound",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("sound_uri", selectedRingtone);
                editor.apply();
            }
            else
            {
                this.selectedRingtone = null;
            }
        }
    }





    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.setting){
            //go to permission_setting activity
            Intent intent =  new Intent(this,PermissionSettings.class);
            startActivity(intent);

        }
        else if(id ==R.id.feedback){
            Intent mail_intent = new Intent();
            mail_intent.setAction(Intent.ACTION_SENDTO);

            Uri uri = Uri.parse("mailto:aggarwal.aditi97@gmail.com");
            mail_intent.setData(uri);
            startActivity(mail_intent);
        }
        else if(id ==R.id.reset){
            AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Reset Data ");
            builder.setMessage("Do you really want to clear all data ? ");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ItemOpenHelper openHelper = ItemOpenHelper.getInstance(getApplicationContext());
                            SQLiteDatabase database= openHelper.getReadableDatabase();
                            Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,  null,null,null,null,null);
                            while(cursor.moveToNext()) {
                                //Delete all alarms first
                                long item_id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
                                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
                                Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
                                Bundle b = new Bundle();
                                b.putLong(ID, item_id);
                                intent1.putExtras(b);
                                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(MainActivity.this, (int) item_id, intent1, 0);
                                alarmManager.cancel(pendingIntent1);


                            }
                            //Delete items from database
                            database.delete(Contract.Item.TABLE_NAME,null,null);

                            displayall();


                        }
                    });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Don't delete
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            }
        else if(id == R.id.titleSort){
            sortdisplay(Contract.Item.COL_TITLE);
        }
        else if (id  == R.id.descriptionSort){
            sortdisplay(Contract.Item.COL_DESC);
        }

        else if(id == R.id.dateSort) {
            sortdisplay(Contract.Item.COL_DATE);
        }
        else if (id == R.id.categorySort){
            sortdisplay(Contract.Item.COL_CATEGORY);
        }

        else if(id==R.id.all){
            displayall();
        }

        else if (id == R.id.work){
            String[] selectionargs ={"Work"};
            showOnly(selectionargs,Contract.Item.COL_CATEGORY);
            }

        else if (id == R.id.home){
            String[] selectionargs ={"Home"};
            showOnly(selectionargs,Contract.Item.COL_CATEGORY);

        }
        else if (id == R.id.personal){
            String[] selectionargs ={"Personal"};
            showOnly(selectionargs,Contract.Item.COL_CATEGORY);

        }
        else if (id == R.id.college){

            String[] selectionargs ={"College"};
            showOnly(selectionargs,Contract.Item.COL_CATEGORY);

        }
        else if (id == R.id.other){
            String[] selectionargs ={"Other"};
            showOnly(selectionargs,Contract.Item.COL_CATEGORY);

            }

        else if(id==R.id.completed){
            String[] selectionargs={1+""};
            showOnly(selectionargs,Contract.Item.COL_COMPLETED);
        }
        else if(id == R.id.pending){
            String[] selectionargs={0+""};
            showOnly(selectionargs,Contract.Item.COL_COMPLETED);

        }
        else if(id ==R.id.important){
            String[] selectionargs={1+""};
            showOnly(selectionargs,Contract.Item.COL_IMP);

        }
        else if(id==R.id.setSound){
            SharedPreferences prefs_sound = this.getSharedPreferences("sound",Context.MODE_PRIVATE);
            String sound = prefs_sound.getString("sound_uri", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
            Uri alarmSound = Uri.parse(sound);
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select sound");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarmSound);
            this.startActivityForResult(intent, 0000);
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Go to item description activity
        Intent intent = new Intent(MainActivity.this,ItemDescription.class);
        Items it = items.get(i); //get the clicked item
        Bundle bundle = new Bundle();
        bundle.putLong(ID,it.getId());
        bundle.putInt("Request_code",MainActivity.EDIT_REQUEST_CODE);

        intent.putExtras(bundle); //pass the bundle to ItemDescription Activity
        startActivityForResult(intent,EDIT_REQUEST_CODE);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //delete item
        final Items item = items.get(i);
        final int position=i;
        delete(position,item);

        return true;
    }

    public  void delete(final int position,final Items item){


        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Item");
        builder.setMessage("Do you really want to delete this item ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ItemOpenHelper openHelper = ItemOpenHelper.getInstance(getApplicationContext());
                SQLiteDatabase database = openHelper.getWritableDatabase();

                long id = item.getId();
                String[] selectionArgs = {id + ""};
                Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_ID + " = ? "
                        ,selectionArgs,null,null,null);
                while (cursor.moveToNext()) {
                    deleted_title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
                    deleted_desc = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
                    deleted_date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
                    deleted_time = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
                    deleted_cat = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
                    deleted_imp=cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_IMP));
                    deleted_completed=cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_COMPLETED));
                }

                database.delete(Contract.Item.TABLE_NAME,Contract.Item.COL_ID + " = ?",selectionArgs);

                    items.remove(position);
                    adapter.notifyDataSetChanged(); //notify the adapter
                    Snackbar.make(fab, "Item Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                undo();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.skyblue1)).show();
                    cursor.close();

                //cancel alarm also
               // Toast.makeText(MainActivity.this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(MainActivity.this,AlarmReceiver.class);
                Bundle b = new Bundle();
                b.putLong(ID,id);
                intent1.putExtras(b);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(MainActivity.this,(int)id,intent1,0);
                alarmManager.cancel(pendingIntent);
                checkEmpty(items);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Don't delete
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();



    }


    public void sortdisplay(String orderbyargument){
        items.clear();
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,null
                ,null,null,null,orderbyargument);


        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            String description =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            String time =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            String category =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
            long item_id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
            int important =cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_IMP));
            int completed=cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_COMPLETED));

            Items it = new Items(title,description,date,time,category);
            it.setId(item_id);
            it.setCompleted(completed);
            it.setImportant(important);
            items.add(it);


        }


        adapter.notifyDataSetChanged();
        cursor.close();

    }

    public void showOnly(String[]  args,String columnName){
        items.clear();
        if (rootLayout.indexOfChild(layout) > -1) {
            // Remove initial layout if it's previously added
            rootLayout.removeView(layout);
        }
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,columnName + " = ? "
                ,args,null,null,null);


        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            String description =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            String time =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            long item_id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
            String category =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
            int important =cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_IMP));
            int completed=cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_COMPLETED));
            Items it = new Items(title,description,date,time,category);
            it.setId(item_id);
            it.setCompleted(completed);
            it.setImportant(important);
            items.add(it);

        }

        adapter.notifyDataSetChanged();
        checkEmpty(items);
        cursor.close();
    }

    public void displayall(){

        items.clear();
        if (rootLayout.indexOfChild(layout) > -1) {
            // Remove initial layout if it's previously added
            rootLayout.removeView(layout);
        }
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,null
                ,null,null,null,null);

        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            String description =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            String time =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            String category =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
            long id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
            int important = cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_IMP));
            int completed = cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_COMPLETED));
            Items item = new Items(title,description,date,time,category);
            item.setId(id);
            item.setImportant(important);
            item.setCompleted(completed);
            items.add(item);
        }
        adapter.notifyDataSetChanged();
        checkEmpty(items);
        cursor.close();
    }

    public void undo(){
        Items item = new Items(deleted_title, deleted_desc, deleted_date, deleted_time, deleted_cat);
        item.setImportant(deleted_imp);
        item.setCompleted(deleted_completed);

        //store data in dB and notify adapter
        ItemOpenHelper openHelper = ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Item.COL_TITLE, item.getTitle());
        contentValues.put(Contract.Item.COL_DESC, item.getDescription());
        contentValues.put(Contract.Item.COL_DATE, item.getDate());
        contentValues.put(Contract.Item.COL_TIME, item.getTime());
        contentValues.put(Contract.Item.COL_CATEGORY, item.getCategory());
        contentValues.put(Contract.Item.COL_IMP,item.isImportant());
        contentValues.put(Contract.Item.COL_COMPLETED,item.isCompleted());

        long id = database.insert(Contract.Item.TABLE_NAME, null, contentValues);
        item.setId(id);

        displayall();

        //setAlarm again

            Bundle b2 = new Bundle();
            b2.putLong(ID, id);
            //Set alarm for that date and time
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(this, AlarmReceiver.class);
            intent1.putExtras(b2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) id, intent1, 0);
            setAlarm(item.getDate(), item.getTime(), alarmManager, pendingIntent);


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

    public void checkEmpty(ArrayList<Items> items){
        if(items.isEmpty()){
            //show initial layout
            LayoutInflater inflater =(LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflater.inflate(R.layout.initial_layout,rootLayout,false);
            rootLayout.addView(layout);




        }
    }

    public void setComplete(Items item,int value){
        long id = item.getId();
        ItemOpenHelper openHelper1 = ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database1=  openHelper1.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(Contract.Item.COL_COMPLETED,value);
        String[] selectionArgs = {id + ""};
        database1.update(Contract.Item.TABLE_NAME,contentValues,Contract.Item.COL_ID + " = ?",selectionArgs);
        displayall();

    }



}
