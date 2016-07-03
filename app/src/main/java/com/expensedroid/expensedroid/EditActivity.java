package com.expensedroid.expensedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by S. Ameli on 03/07/16.
 */
public class EditActivity extends AppCompatActivity {

    private int database_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }


    public void btnDiscard(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to DetailActivity
    }

}
