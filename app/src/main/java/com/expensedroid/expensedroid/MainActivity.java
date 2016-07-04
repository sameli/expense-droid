package com.expensedroid.expensedroid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected ArrayList<Transaction> data;
    public static final String INTENT_EDIT_MSG_ID = "IDEDIT1000";
    DatabaseHelper mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view1 = (View) findViewById(R.id.mainview1);
        //view1.setBackgroundColor(Color.argb(100,0,200,0));
        view1.setBackgroundColor(Color.argb(255, 204, 255, 204));

        data = mydb.getAllTransactions(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    private void displayDetail(Transaction trans) {
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(INTENT_EDIT_MSG_ID, trans);
        startActivity(editIntent);
    }
}
