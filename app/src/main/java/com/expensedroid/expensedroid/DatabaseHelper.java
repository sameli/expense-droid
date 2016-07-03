package com.expensedroid.expensedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by S. Ameli on 02/07/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table tr (id integer primary key, title TEXT,amount REAL,date DATETIME)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tr");
        onCreate(sqLiteDatabase);
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tr where id="+id+"", null );
        return res;
    }

    public boolean insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", trans.getTitle());
        contentValues.put("amount", trans.getAmount());
        contentValues.put("date", trans.getDateString());
        db.insert("tr", null, contentValues);
        return true;
    }

    public boolean updateTransaction(Integer id, String title, double amount, String dateStr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("amount", amount);
        contentValues.put("date", dateStr);
        db.update("tr", contentValues, "id = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    public Integer deleteTransaction(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tr", "id = ? ", new String[] {Integer.toString(id)});
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "tr");
        return numRows;
    }
}
