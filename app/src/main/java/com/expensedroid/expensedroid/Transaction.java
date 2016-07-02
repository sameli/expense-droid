package com.expensedroid.expensedroid;

import android.text.Html;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {

    private String title;
    private double amount;
    private Date date;
    private int database_id;

    public int getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(int database_id) {
        this.database_id = database_id;
    }

    public Transaction(String title, double amount, Date date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public String toString() {

        return getDateString() + "\t" + title + "\t" + amount;
    }

    public double getAmount() {
        return amount;
    }
}
