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
    public static final String TABLE_NAME_ACCOUNTS= "accts";
    public static final String TABLE_NAME_TRANSACTIONS = "trs";
    public static final String TABLE_NAME_V1= "tr"; // table for v1 of database

    public static final String DATE_FORMAT = "yyyy-MM-dd";


    public DatabaseHelper(Context context, int dbVersion) {
        super(context, DATABASE_NAME, null, dbVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME_ACCOUNTS +
                        " (acct_id INTEGER PRIMARY KEY, acct_name TEXT NOT NULL);"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME_TRANSACTIONS +
                        " (id INTEGER PRIMARY KEY, title TEXT,amount REAL,date DATETIME, notes TEXT, " +
                        "acct_id INTEGER NOT NULL, " +
                        "FOREIGN KEY (acct_id) REFERENCES accts(acct_id) ON UPDATE CASCADE ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRANSACTIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_V1);
        onCreate(sqLiteDatabase);
    }

    public long insertAccount(String acctName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acct_name", acctName);
        return db.insert(TABLE_NAME_ACCOUNTS, null, contentValues);
    }

    public long insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", trans.getTitle());
        contentValues.put("amount", trans.getAmount());
        contentValues.put("date", trans.getDateString());
        contentValues.put("notes", trans.getNotes());
        contentValues.put("acct_id", trans.getAccount_id());
        return db.insert(TABLE_NAME_TRANSACTIONS, null, contentValues);
    }

    public Cursor getData(int transaction_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_TRANSACTIONS + " where id="+transaction_id+"", null );
        return res;
    }

    public int numberOfRowsInAccounts(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_ACCOUNTS);
        return numRows;
    }

    public int numberOfRowsInTransactions(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_TRANSACTIONS);
        return numRows;
    }

    public boolean updateTransaction(Integer id, String title, double amount, String dateStr, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("amount", amount);
        contentValues.put("date", dateStr);
        contentValues.put("notes", notes);
        db.update(TABLE_NAME_TRANSACTIONS, contentValues, "id = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    public Integer deleteTransaction(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_TRANSACTIONS, "id = ? ", new String[] {Integer.toString(id)});
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
        Cursor res = db.rawQuery( "select * from " + TABLE_NAME_TRANSACTIONS + " " + filtersSql, null );
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
            trans.setTransaction_id(id);

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
