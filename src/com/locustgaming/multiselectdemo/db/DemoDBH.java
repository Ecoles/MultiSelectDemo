package com.locustgaming.multiselectdemo.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DemoDBH extends SQLiteOpenHelper
{
    static final String DATABASE_NAME = "multiSelectDemo";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_ITEM = "create table items (_id integer primary key autoincrement,"
            + " name text not null, entryUsr integer not null, entryDate integer not null,"
            + " updateUsr integer, updateDate integer, selected integer DEFAULT 0);";

    DemoDBH(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_ITEM);
        setupList(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub
    }

    private void setupList(SQLiteDatabase db)
    {
        // I know this isn't the best way but it works for this demo
        String[] items = new String[] { "Monitor", "Keyboard", "Mouse", "Tower" };
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        // Make a list long enough to scroll off the window
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < items.length; i++)
            {
                values.put("name", items[i]);
                values.put("entryUsr", 1);
                values.put("entryDate", timeStamp);
                values.put("updateUsr", 1);
                values.put("updateDate", timeStamp);
                db.insert("items", null, values);
            }

    }
}
