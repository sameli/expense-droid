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

    public static final String DATABASE_NAME = "mydatabase.db";
    public static final String TABLE_NAME = "tr";
    public static final String DATE_FORMAT = "yyyy-MM-dd";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + TABLE_NAME +
                        " (id integer primary key, title TEXT,amount REAL,date DATETIME, notes TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", trans.getTitle());
        contentValues.put("amount", trans.getAmount());
        contentValues.put("date", trans.getDateString());
        contentValues.put("notes", trans.getNotes());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME + " where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateTransaction(Integer id, String title, double amount, String dateStr, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("amount", amount);
        contentValues.put("date", dateStr);
        contentValues.put("notes", notes);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    public Integer deleteTransaction(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ? ", new String[] {Integer.toString(id)});
    }

    public ArrayList<Transaction> getAllTransactions(Context context){

        ArrayList<Transaction> array = new ArrayList<Transaction>();


        // load filters
        Boolean isDateFilterActive = SettingsIO.readData(context, false, "menu_filter_date_checkbox");

        String dateFilterSQL = "";//"where date(date) = '2016-07-30'";
        if(isDateFilterActive){

            //where date(dateofbirth)>date('1980-12-01')
            String selectedEquality = SettingsIO.readData(context, "", "menu_filter_date_checkbox_selectedequality");
            String operatorSign = DialogFilterDate.getSmallOperatorStr(selectedEquality);

            String selectedDate = SettingsIO.readData(context, "", "menu_filter_date_checkbox_selecteddate");

            dateFilterSQL = "date(date) " + operatorSign + " date('" + selectedDate + "')";
            System.out.println(">>> dateFilterSQL: " + dateFilterSQL);
        }

        Boolean isAmountFilterActive = SettingsIO.readData(context, false, "menu_filter_amount_checkbox");

        String amountFilterSQL = "";
        if(isAmountFilterActive){

            String selectedEquality = SettingsIO.readData(context, "", "menu_filter_amount_checkbox_selectedequality");

            int selectedAmount = SettingsIO.readData(context, 0, "menu_filter_amount_checkbox_selectedamount");

            amountFilterSQL = "amount " + selectedEquality + " " + selectedAmount;
            System.out.println(">>> amountFilterSQL: " + amountFilterSQL);
        }

        String filtersSql = "";
        if(isDateFilterActive && !isAmountFilterActive){
            filtersSql = "where " + dateFilterSQL;
        }else if(!isDateFilterActive && isAmountFilterActive){
            filtersSql = "where " + amountFilterSQL;
        }else if (isDateFilterActive && isDateFilterActive){
            filtersSql = "where " + dateFilterSQL + " and " + amountFilterSQL;
        }


        System.out.println(">>> filtersSql: " + filtersSql);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from " + TABLE_NAME + " " + filtersSql, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {

            int id = res.getInt(res.getColumnIndex("id"));
            String title = res.getString(res.getColumnIndex("title"));
            double amount = res.getDouble(res.getColumnIndex("amount"));
            String dateStr = res.getString(res.getColumnIndex("date"));
            String notes = res.getString(res.getColumnIndex("notes"));
            Date date = parseDate(dateStr);

            System.out.println("Results from DB: " + title + " " + amount + " " + dateStr);
            Transaction trans = new Transaction(title, amount, date, notes);
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
