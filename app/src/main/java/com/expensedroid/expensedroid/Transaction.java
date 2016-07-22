package com.expensedroid.expensedroid;

import android.text.Html;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by S. Ameli on 03/07/16.
 */
public class Transaction implements Serializable { // we need serializable so we can send the entire object to other activities

    private String title;
    private double amount;
    private Date date;
    private String notes;
    private int database_id; // this is row ID in the database for this transaction

    public int getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(int database_id) {
        this.database_id = database_id;
    }

    public Transaction(String title, double amount, Date date, String notes) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd"); //("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() { // this is called in the main menu when adding to listview

        return getDateString() + "\t" + title + "\t" + amount;
    }
}
