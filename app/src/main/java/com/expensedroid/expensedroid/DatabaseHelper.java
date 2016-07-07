package com.expensedroid.expensedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by S. Ameli on 02/07/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TABLE_NAME = "tr";

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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from"+TABLE_NAME+"where id="+id+"", null );
        return res;
    }

    public boolean insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", trans.getTitle());
        contentValues.put("amount", trans.getAmount());
        contentValues.put("date", trans.getDateString());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateTransaction(Integer id, String title, double amount, String dateStr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("amount", amount);
        contentValues.put("date", dateStr);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    public Integer deleteTransaction(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ? ", new String[] {Integer.toString(id)});
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "tr");
        return numRows;
    }

    public ArrayList<Transaction> getAllTransactions(Context context){

        ArrayList<Transaction> array = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from "+ TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {

            int id = res.getInt(res.getColumnIndex("id"));
            String title = res.getString(res.getColumnIndex("title"));
            double amount = res.getDouble(res.getColumnIndex("amount"));
            String dateStr = res.getString(res.getColumnIndex("date"));
            Date date = parseDate(dateStr);

            System.out.println("Results from DB: " + title + " " + amount + " " + dateStr);
            Transaction trans = new Transaction(title, amount, date);
            trans.setDatabase_id(id);

            array.add(trans);
            res.moveToNext();
        }

        return array;
    }

    public static Date parseDate(String dateStr){
        Date date = null;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        try {
            date =  df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
