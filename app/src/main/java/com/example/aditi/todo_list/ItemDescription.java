package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.aditi.todo_list.MainActivity.ID;
import static java.security.AccessController.getContext;

public class ItemDescription extends AppCompatActivity {
    TextView t1,t2,t3,t4,t5;
    Button button;
    String title,description,date,time,category;
    int important;
    Bundle b;
    long id;
    public static final int DELETE_RESULT_CODE= 11011;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDesc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.whiteback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        button=findViewById(R.id.impShow);
        button.setEnabled(false);
        t1 =findViewById(R.id.textView1);
        t2=findViewById(R.id.textView2);
        t3=findViewById(R.id.textView3);
        t4=findViewById(R.id.textView4);
        t5=findViewById(R.id.textView5);

        //obtain id from bundle and hence data from the database
        Intent intent = getIntent();
        if(intent.getExtras()!=null)
             b = intent.getExtras();
        id = b.getLong(ID);

        String [] selectionargs = {id+""};
        ItemOpenHelper openHelper =ItemOpenHelper.getInstance(getApplicationContext());
        SQLiteDatabase database= openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_ID + " = ? "
                ,selectionargs,null,
                null,null);

        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
            description = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
            date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
            time = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
            category = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
            important=cursor.getInt(cursor.getColumnIndex(Contract.Item.COL_IMP));
        }
        t1.setText(title);
        t2.setText(description);
        t3.setText(date);
        t4.setText(time);
        t5.setText(category);
        if(important==1){
            button.setVisibility(View.VISIBLE);
            button.setBackground(getResources().getDrawable(R.drawable.colorstar2));
        }
        else{
            button.setVisibility(View.GONE);
        }

        fab=(FloatingActionButton) findViewById(R.id.editFab);
        fab.setImageResource(R.drawable.pencil);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to a new activity for edit the item
                Intent intent = new Intent(ItemDescription.this,Edit_Activity.class);
                intent.putExtras(b); //pass the bundle to Edit_Activity
                startActivityForResult(intent,MainActivity.EDIT_REQUEST_CODE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menu_id = item.getItemId();

        if(menu_id == R.id.delete){
            //delete
            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setTitle("Delete Item");
            builder.setMessage("Do you really want to delete this item ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    ItemOpenHelper openHelper = ItemOpenHelper.getInstance(getApplicationContext());
                    SQLiteDatabase database = openHelper.getWritableDatabase();
                    String[] selectionArgs = {id + ""};
                    Cursor cursor = database.query(Contract.Item.TABLE_NAME,null,Contract.Item.COL_ID + " = ? "
                            ,selectionArgs,null,null,null);
                    while (cursor.moveToNext()) {
                        MainActivity.deleted_title = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TITLE));
                        MainActivity.deleted_desc = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DESC));
                        MainActivity.deleted_date = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_DATE));
                        MainActivity.deleted_time = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_TIME));
                        MainActivity.deleted_cat = cursor.getString(cursor.getColumnIndex(Contract.Item.COL_CATEGORY));
                    }

                    database.delete(Contract.Item.TABLE_NAME,Contract.Item.COL_ID + " = ?",selectionArgs);
                    cursor.close();
                    //pass the id to main activity
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(DELETE_RESULT_CODE,intent);
                    ItemDescription.this.finish();



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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        setResult(resultCode,data); //set result for main activity
        super.onActivityResult(requestCode, resultCode, data);
        this.finish(); //remove this activity from stack
    }


}
