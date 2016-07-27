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
import java.util.List;

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

    public int insertAccount(String acctName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acct_name", acctName);
        return (int)db.insert(TABLE_NAME_ACCOUNTS, null, contentValues);
    }

    public int insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", trans.getTitle());
        contentValues.put("amount", trans.getAmount());
        contentValues.put("date", trans.getDateString());
        contentValues.put("notes", trans.getNotes());
        contentValues.put("acct_id", trans.getAccount_id());
        return (int)db.insert(TABLE_NAME_TRANSACTIONS, null, contentValues);
    }

    public String getAccountName(int acct_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_ACCOUNTS + " where acct_id="+acct_id+"", null );
        res.moveToFirst();
        String acct_name = res.getString(res.getColumnIndex("acct_name"));
        return acct_name;
    }

    public List<AccountItem> getAccounts(){
        List<AccountItem> listOfAccts = new ArrayList<AccountItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_ACCOUNTS, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            int acct_id = res.getInt(res.getColumnIndex("acct_id"));
            String acct_name = res.getString(res.getColumnIndex("acct_name"));
            AccountItem actItem = new AccountItem();
            actItem.acct_id = acct_id;
            actItem.acct_name = acct_name;

            listOfAccts.add(actItem);
            res.moveToNext();
        }

        return listOfAccts;
    }

    public boolean deleteAccount(int acct_id){
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            db.delete(TABLE_NAME_ACCOUNTS, "acct_id = ?", new String[] { String.valueOf(acct_id) });
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
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

    /*
     * If an argument is given to numberOfRowsInTransactions, then this method counts only those rows that belong to the given acct_id
     */
    public int numberOfRowsInTransactions(int acct_id){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_TRANSACTIONS, "acct_id = ?", new String[] {Integer.toString(acct_id)});
        return numRows;
    }

    public boolean updateAccounts(Integer acct_id, String acct_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acct_name", acct_name);
        db.update(TABLE_NAME_ACCOUNTS, contentValues, "acct_id = ? ", new String[] {Integer.toString(acct_id)});
        return true;
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

        ArrayList<Transaction> listOfTransactions = new ArrayList<Transaction>();


        // load filters
        Boolean isDateFilterActive = SettingsIO.readData(context, false, "menu_filter_date_checkbox");

        String dateFilterSQL = "";
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
            filtersSql = "AND " + dateFilterSQL;
        }else if(!isDateFilterActive && isAmountFilterActive){
            filtersSql = "AND " + amountFilterSQL;
        }else if (isDateFilterActive && isDateFilterActive){
            filtersSql = "AND " + dateFilterSQL + " AND " + amountFilterSQL;
        }

        int selected_acct_id = SettingsIO.readData(context, -1, "selected_acct_id");
        String accountSql = "WHERE acct_id = " + selected_acct_id;


        System.out.println(">>> filtersSql: " + filtersSql);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from " + TABLE_NAME_TRANSACTIONS + " " + accountSql + " " + filtersSql, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            int id = res.getInt(res.getColumnIndex("id"));
            String title = res.getString(res.getColumnIndex("title"));
            double amount = res.getDouble(res.getColumnIndex("amount"));
            String dateStr = res.getString(res.getColumnIndex("date"));
            String notes = res.getString(res.getColumnIndex("notes"));
            int acct_id = res.getInt(res.getColumnIndex("acct_id"));
            Date date = parseDate(dateStr);

            System.out.println("Results from DB: " + title + " " + amount + " " + dateStr);
            Transaction trans = new Transaction(title, amount, date, notes);
            trans.setTransaction_id(id);
            trans.setAccount_id(acct_id);

            listOfTransactions.add(trans);
            res.moveToNext();
        }

        return listOfTransactions;
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
