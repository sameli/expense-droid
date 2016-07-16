package com.expensedroid.expensedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

    private void gotoMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to DetailActivity
    }
    
    // this is set from the xml
    public void btnOpenDateDialog(View view) {
        DatePickerFrag newFragment = new DatePickerFrag();
        newFragment.set_id_btn_to_modify(R.id.btn_date);
        newFragment.show(getFragmentManager(),"Date Picker");
    }


    public void btnDiscard(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to DetailActivity
    }

    public void btnDelete(View view) {
        if(database_id != -1){
            DatabaseHelper mydb = new DatabaseHelper(this);
            mydb.deleteTransaction(database_id);
            Toast.makeText(EditActivity.this, "Deleted item", Toast.LENGTH_LONG).show();
            gotoMainActivity();
        }
    }

}
