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
 *
 * This class has methods to create, read, update and delete a database.
 * We have two tables:
 * 1- Accounts table: we store account information in this table.
 * 2- Transactions table: we store transaction information in this table. Many transactions can be linked to one account.
 * Deleting an account will also delete all transactions that match primary key of that account
 *
 * When retrieving data from the database, the settings file will be checked to see if there are any filters that needs to be applied:
 * For example when we set the "filter > amount > equal to 100" from the menu of the app, the app will add few lines to the settings file:
 *
 * <boolean name="menu_filter_amount_checkbox_value" value="true" />
 * <string name="menu_filter_amount_selected_operator">equal to</string>
 * <int name="menu_filter_amount_value_1" value="100" />
 *
 * The line above in the settings file, means that the database has to apply a filter based on the amount chosen by the user.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db"; // name of database file
    public static final int DATABASE_VERSION = 3; // current version of the database schema
    private final String TABLE_NAME_ACCOUNTS= "accts";
    private final String TABLE_NAME_TRANSACTIONS = "trs";
    private final String TABLE_NAME_V1= "tr"; // table for first version of database

    // These are names used in the header of transaction table
    private final String TABLE_HEADER_TITLE = "title";
    private final String TABLE_HEADER_AMOUNT = "amount";
    private final String TABLE_HEADER_DATE = "date";
    private final String TABLE_HEADER_NOTES = "notes";
    private final String TABLE_HEADER_ACCOUNT_ID = "acct_id"; // this is used in both accounts table and transactions
    private final String TABLE_HEADER_ACCOUNT_NAME = "acct_name";

    /*
     * This method is the constructor
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * This method is called whenever we open access to the database. We need to call "PRAGMA foreign_keys=ON;" to activate sqlite to apply constraints
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /*
     * This method will be called automatically whenever new tables need to be created for the first time
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME_ACCOUNTS +
                        " ("+ TABLE_HEADER_ACCOUNT_ID +" INTEGER PRIMARY KEY, "+ TABLE_HEADER_ACCOUNT_NAME +" TEXT NOT NULL);"
        );

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_NAME_TRANSACTIONS +
                        " (id INTEGER PRIMARY KEY, "+ TABLE_HEADER_TITLE +" TEXT,"+ TABLE_HEADER_AMOUNT +" REAL,"+ TABLE_HEADER_DATE +" DATETIME, "+ TABLE_HEADER_NOTES +" TEXT, " +
                        ""+ TABLE_HEADER_ACCOUNT_ID +" INTEGER NOT NULL, " +
                        "FOREIGN KEY ("+ TABLE_HEADER_ACCOUNT_ID +") REFERENCES "+ TABLE_NAME_ACCOUNTS+"("+ TABLE_HEADER_ACCOUNT_ID +") ON UPDATE CASCADE ON DELETE CASCADE)"
        );
    }

    /*
     * This method will be called automatically whenever the version of the database is changed
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRANSACTIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_V1);
        onCreate(sqLiteDatabase);
    }

    /*
     * This method resets all tables in the database
     */
    public void resetDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, DATABASE_VERSION);
    }

    /*
     * This method inserts an account into accounts table
     */
    public int insertAccount(String acctName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_HEADER_ACCOUNT_NAME, acctName);
        return (int)db.insert(TABLE_NAME_ACCOUNTS, null, contentValues);
    }

    /*
     * This method inserts a transaction into the transactions table.
     * Note that the given transaction has to have a valid account id that exists in the accounts table.
     */
    public int insertTransaction(Transaction trans){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_HEADER_TITLE, trans.getTitle());
        contentValues.put(TABLE_HEADER_AMOUNT, trans.getAmount());
        contentValues.put(TABLE_HEADER_DATE, trans.getDateString());
        contentValues.put(TABLE_HEADER_NOTES, trans.getNotes());
        contentValues.put(TABLE_HEADER_ACCOUNT_ID, trans.getAccount_id());
        return (int)db.insert(TABLE_NAME_TRANSACTIONS, null, contentValues);
    }

    /*
     * This method returns the account name of the given account id
     */
    public String getAccountName(int acct_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_ACCOUNTS + " where "+ TABLE_HEADER_ACCOUNT_ID +"="+acct_id+"", null );
        res.moveToFirst();
        String acct_name = res.getString(res.getColumnIndex(TABLE_HEADER_ACCOUNT_NAME));
        return acct_name;
    }

    /*
     * This method returns all accounts
     */
    public List<AccountItem> getAccounts(){
        List<AccountItem> listOfAccts = new ArrayList<AccountItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_ACCOUNTS, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            int acct_id = res.getInt(res.getColumnIndex(TABLE_HEADER_ACCOUNT_ID));
            String acct_name = res.getString(res.getColumnIndex(TABLE_HEADER_ACCOUNT_NAME));
            AccountItem actItem = new AccountItem();
            actItem.acct_id = acct_id;
            actItem.acct_name = acct_name;

            listOfAccts.add(actItem);
            res.moveToNext();
        }

        return listOfAccts;
    }

    /*
     * This method deletes an account from the the accounts table.
     * Note that the transactions table is referencing from the accounts table and it performs "DELETE CASCADE".
     * So when we delete a row from the accounts table, all rows from the transactions table that match the primary key of the given account id will also be removed.
     */
    public boolean deleteAccount(int acct_id){
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            db.delete(TABLE_NAME_ACCOUNTS, TABLE_HEADER_ACCOUNT_ID + " = ?", new String[] { String.valueOf(acct_id) });
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * This method retrieves a transaction that matches the given id. It returns a cursor to the item
     */
    public Cursor getData(int transaction_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME_TRANSACTIONS + " where id="+transaction_id+"", null );
        return res;
    }

    /*
     * This method returns number of rows in the accounts table
     */
    public int numberOfRowsInAccounts(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_ACCOUNTS);
        return numRows;
    }

    /*
     * This method returns number of rows in the transactions table
     */
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
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_TRANSACTIONS, TABLE_HEADER_ACCOUNT_ID + " = ?", new String[] {Integer.toString(acct_id)});
        return numRows;
    }

    /*
     * This method updates the accounts table
     */
    public boolean updateAccounts(Integer acct_id, String acct_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_HEADER_ACCOUNT_NAME, acct_name);
        db.update(TABLE_NAME_ACCOUNTS, contentValues, TABLE_HEADER_ACCOUNT_ID + " = ? ", new String[] {Integer.toString(acct_id)});
        return true;
    }

    /*
     * This method updates the transaction table
     */
    public boolean updateTransaction(Integer id, String title, double amount, String dateStr, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_HEADER_TITLE, title);
        contentValues.put(TABLE_HEADER_AMOUNT, amount);
        contentValues.put(TABLE_HEADER_DATE, dateStr);
        contentValues.put(TABLE_HEADER_NOTES, notes);
        db.update(TABLE_NAME_TRANSACTIONS, contentValues, "id = ? ", new String[] {Integer.toString(id)});
        return true;
    }

    /*
     * This method deletes the given transaction based on the given id from the Transactions table
     */
    public Integer deleteTransaction(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_TRANSACTIONS, "id = ? ", new String[] {Integer.toString(id)});
    }

    /*
     * This method builds a sql statement based on filters, and retrieves transactions from database
     */
    public ArrayList<Transaction> getAllTransactions(Context context){

        ArrayList<Transaction> listOfTransactions = new ArrayList<Transaction>();


        // load filters
        Boolean isDateFilterActive = SettingsIO.readData(context, false, Tools.SETTING_MENU_FILTER_DATE_CHECKBOX);

        String dateFilterSQL = "";
        if(isDateFilterActive){

            String selectedOperator = SettingsIO.readData(context, "", Tools.SETTING_MENU_FILTER_DATE_SELECTED_OPERATOR);
            String operatorSign = Tools.getSmallOperatorStr(selectedOperator);

            String selectedDate = SettingsIO.readData(context, "", Tools.SETTING_MENU_FILTER_DATE_VALUE_1);
            dateFilterSQL = "date("+ TABLE_HEADER_DATE +") " + operatorSign + " date('" + selectedDate + "')";

            if(operatorSign.equals(Tools.BETWEEN_STR)){
                String selectedDateEnd = SettingsIO.readData(context, "", Tools.SETTING_MENU_FILTER_DATE_VALUE_2);
                dateFilterSQL += " AND date('" + selectedDateEnd + "')";
            }
            // select * from trs where date(date) BETWEEN date("2016-07-25") AND date("2016-07-28");
            //System.out.println("dateFilterSQL: " + dateFilterSQL);
        }

        Boolean isAmountFilterActive = SettingsIO.readData(context, false, Tools.SETTING_MENU_FILTER_AMOUNT_CHECKBOX);

        String amountFilterSQL = "";
        if(isAmountFilterActive){

            String selectedOperator = SettingsIO.readData(context, "", Tools.SETTING_MENU_FILTER_AMOUNT_SELECTED_OPERATOR);
            String operatorSign = Tools.getSmallOperatorStr(selectedOperator);

            int selectedAmount = SettingsIO.readData(context, 0, Tools.SETTING_MENU_FILTER_AMOUNT_VALUE_1);

            amountFilterSQL = TABLE_HEADER_AMOUNT + " " + operatorSign + " " + selectedAmount;

            if(selectedOperator.equals(Tools.BETWEEN_STR)){
                int selectedAmountEnd = SettingsIO.readData(context, 0, Tools.SETTING_MENU_FILTER_AMOUNT_VALUE_2);
                amountFilterSQL += " AND " + selectedAmountEnd;
            }
            System.out.println("amountFilterSQL: " + amountFilterSQL);
        }

        String filtersSql = "";
        if(isDateFilterActive && !isAmountFilterActive){
            filtersSql = "AND " + dateFilterSQL;
        }else if(!isDateFilterActive && isAmountFilterActive){
            filtersSql = "AND " + amountFilterSQL;
        }else if (isDateFilterActive && isDateFilterActive){
            filtersSql = "AND " + dateFilterSQL + " AND " + amountFilterSQL;
        }

        int selected_acct_id = SettingsIO.readData(context, -1, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);
        String accountSql = "WHERE "+ TABLE_HEADER_ACCOUNT_ID +" = " + selected_acct_id;


        //System.out.println("filtersSql: " + filtersSql);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from " + TABLE_NAME_TRANSACTIONS + " " + accountSql + " " + filtersSql, null );
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            int id = res.getInt(res.getColumnIndex("id"));
            String title = res.getString(res.getColumnIndex(TABLE_HEADER_TITLE));
            double amount = res.getDouble(res.getColumnIndex(TABLE_HEADER_AMOUNT));

            String dateStr = res.getString(res.getColumnIndex(TABLE_HEADER_DATE));
            String notes = res.getString(res.getColumnIndex(TABLE_HEADER_NOTES));
            int acct_id = res.getInt(res.getColumnIndex(TABLE_HEADER_ACCOUNT_ID));
            Date date = parseDate(dateStr);

            //System.out.println("Results from DB: " + title + " " + amount + " " + dateStr);
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
        DateFormat df = new SimpleDateFormat(Tools.DATE_FORMAT);
        try {
            date =  df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
