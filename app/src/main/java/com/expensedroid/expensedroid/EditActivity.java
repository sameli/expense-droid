package com.expensedroid.expensedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_add:
                Intent editIntent = new Intent(this, EditActivity.class);
                //editIntent.putExtra(INTENT_EDIT_MSG_ID, "some message here blabla");
                startActivity(editIntent); // this will switch to DetailActivity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void btnDiscard(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to DetailActivity
    }

}
