package com.example.aditi.todo_list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemOpenHelper extends SQLiteOpenHelper {
    //Singleton class
    private static  ItemOpenHelper instance;

    public static  ItemOpenHelper getInstance(Context context) {
        if(instance == null){
            instance = new ItemOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private ItemOpenHelper(Context context) {
        super(context, "items_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String itemquery = "Create table " + Contract.Item.TABLE_NAME + " ( " +
                Contract.Item.COL_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Contract.Item.COL_TITLE + "  TEXT, " +
                Contract.Item.COL_DESC + " TEXT , " +
                Contract.Item.COL_DATE + " TEXT, "  +
                Contract.Item.COL_TIME  + " TEXT, " +
                Contract.Item.COL_CATEGORY + " TEXT, " +
                Contract.Item.COL_IMP + " Boolean, " +
                Contract.Item.COL_COMPLETED + " Boolean )";

        sqLiteDatabase.execSQL(itemquery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
