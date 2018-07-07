package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{


    ListView listView;
    LayoutInflater layoutInflater;
    ArrayList<Items> items;
    ItemAdaptor adapter;
    public static final String ID = "id";
    public static final int EDIT_RESULT_CODE=2;
    public static final int EDIT_REQUEST_CODE=2;

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //go to Add_item_Activity
                Intent intent =  new Intent(MainActivity.this,Add_item_Activity.class);
            //    intent.putExtra("Request_Code",ADD_REQUEST_CODE);
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
            public void rowButtonClicked(int position,Items item) {

                delete(position,item);
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
            //cancel alarm also
            Snackbar.make(fab, "Item Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", null).show();
           // Toast.makeText(MainActivity.this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(this,AlarmReceiver.class);
            Bundle b2 = new Bundle();
            b2.putLong(ID,id);
            intent1.putExtras(b2);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(MainActivity.this,(int)id,intent1,0);
            alarmManager.cancel(pendingIntent);

            }
    }





    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.setting){
            //go to permission_setting activity
            Intent intent =  new Intent(this,PermissionSettings.class);
            startActivity(intent);

        }
     /*   else if(id == R.id.newItem)
        {
            //go to Add_item_Activity
            Intent intent =  new Intent(this,Add_item_Activity.class);
            intent.putExtra("Request_Code",ADD_REQUEST_CODE);
            startActivityForResult(intent,ADD_REQUEST_CODE);
        }*/
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

        else if (id == R.id.work){
            String[] selectionargs ={"Work"};
            showOnly(selectionargs);
            }

        else if (id == R.id.home){
            String[] selectionargs ={"Home"};
            showOnly(selectionargs);

        }
        else if (id == R.id.personal){
            String[] selectionargs ={"Personal"};
            showOnly(selectionargs);

        }
        else if (id == R.id.college){

            String[] selectionargs ={"College"};
            showOnly(selectionargs);

        }
        else if (id == R.id.other){
            String[] selectionargs ={"Other"};
            showOnly(selectionargs);

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

                database.delete(Contract.Item.TABLE_NAME,Contract.Item.COL_ID + " = ?",selectionArgs);

                    items.remove(position);
                    adapter.notifyDataSetChanged(); //notify the adapter
                    Snackbar.make(fab, "Item Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", null).show();

                //cancel alarm also
              // Toast.makeText(MainActivity.this,"Alarm cancelled",Toast.LENGTH_SHORT).show();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent1 = new Intent(MainActivity.this,AlarmReceiver.class);
                Bundle b = new Bundle();
                b.putLong(ID,id);
                intent1.putExtras(b);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(MainActivity.this,(int)id,intent1,0);
                alarmManager.cancel(pendingIntent);

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

            Items it = new Items(title,description,date,time,category);
            it.setId(item_id);
            items.add(it);

        }


        adapter.notifyDataSetChanged();
        cursor.close();

    }

    public void showOnly(String[]  args){
        items.clear();
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_CATEGORY + " = ? "
                ,args,null,null,null);


        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            String description =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            String date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            String time =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            long item_id = cursor.getLong(cursor.getColumnIndex(Contract.Item.COL_ID));
            String category =cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
            Items it = new Items(title,description,date,time,category);
            it.setId(item_id);
            items.add(it);

        }

        adapter.notifyDataSetChanged();

        cursor.close();
    }

    public void displayall(){

        items.clear();

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
            Items item = new Items(title,description,date,time,category);
            item.setId(id);
            items.add(item);
        }
        adapter.notifyDataSetChanged();

        cursor.close();
    }



}
