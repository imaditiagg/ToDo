package com.example.aditi.todo_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.aditi.todo_list.MainActivity.ID;

public class ItemDescription extends AppCompatActivity {
    TextView t1,t2,t3,t4,t5;
    String title,description,date,time,category;
    Bundle b;
    long id;
    public static final int DELETE_RESULT_CODE= 11011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


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
        }
        t1.setText(title);
        t2.setText(description);
        t3.setText(date);
        t4.setText(time);
        t5.setText(category);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menu_id = item.getItemId();
        if(menu_id == R.id.edit){
            //go to a new activity for edit the item
            Intent intent = new Intent(this,Edit_Activity.class);
            intent.putExtras(b); //pass the bundle to Edit_Activity
            startActivityForResult(intent,MainActivity.EDIT_REQUEST_CODE);
        }
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
                    Log.i("id in description",id+"");
                    database.delete(Contract.Item.TABLE_NAME,Contract.Item.COL_ID + " = ?",selectionArgs);
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
