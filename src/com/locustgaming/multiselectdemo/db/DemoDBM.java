package com.locustgaming.multiselectdemo.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DemoDBM
{
    // Database
    public static final String TABLENAME = "items";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String EUSR = "entryUsr";
    public static final String EDATE = "entryDate";
    public static final String UUSR = "updateUsr";
    public static final String UDATE = "updateDate";
    public static final String SEL = "selected";

    private final Context androidContext;
    private DemoDBH dbHelper;
    private SQLiteDatabase db;

    public DemoDBM(Context ctx) {
        this.androidContext = ctx;
    }

    public void open() throws SQLException
    {
        dbHelper = new DemoDBH(androidContext);
        db = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        db.close();
        dbHelper.close();
    }

    public SQLiteDatabase getDB()
    {
        return db;
    }

    public int insert(String Name, int usr)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        values.put(NAME, Name);
        values.put(EUSR, usr);
        values.put(EDATE, timeStamp);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        return (int) db.insert(TABLENAME, null, values);
    }

    public Cursor getList(int userId)
    {
        String[] columns = new String[] { ID, NAME, SEL };
        String whereClause = EUSR + " = " + userId;
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getSelectedList(int userId)
    {
        String[] columns = new String[] { ID, NAME, SEL };
        String whereClause = EUSR + " = " + userId + " and " + SEL + " = 1";
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public boolean setChecked(int itemId, int userId, boolean newVal)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(UUSR, userId);
        values.put(UDATE, timeStamp);
        values.put(SEL, newVal);

        whereClause = ID + "=" + itemId;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

}
