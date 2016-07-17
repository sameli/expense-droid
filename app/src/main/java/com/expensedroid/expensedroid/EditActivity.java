package com.expensedroid.expensedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


    public void btnSave(View view) {
        EditText editText_title = (EditText) findViewById(R.id.editText_title);
        String title = editText_title.getText().toString();

        EditText editText_amount = (EditText) findViewById(R.id.editText_amount);
        String amount_str = editText_amount.getText().toString();

        Button btn_date = (Button) findViewById(R.id.btn_date);
        String date_str = btn_date.getText().toString();


        if(title.length() == 0 || amount_str.length() == 0 || date_str.length() == 0){
            Toast.makeText(EditActivity.this, "fill all blank spots", Toast.LENGTH_LONG).show();
            return;
        }

        double amount = Double.parseDouble(amount_str);
        //int expenses = Integer.parseInt(expenses_str); // replace with date parse from string
        String dateStr = ((Button) findViewById(R.id.btn_date)).getText().toString();
        //Date date = DatabaseHelper.parseDate(dateStr);


        DatabaseHelper mydb = new DatabaseHelper(this);
        if(database_id == -1){ // this means database row id has not been set, and we add new item to database
            // title, amount, expenses
            mydb.insertTransaction(new Transaction(title, amount , DatabaseHelper.parseDate(dateStr)));

            Toast.makeText(EditActivity.this, "Added new item to database", Toast.LENGTH_LONG).show();
        }else{ // we have an database_id. we need to update that row on database
            mydb.updateTransaction(database_id, title, amount, dateStr);

            Toast.makeText(EditActivity.this, "Updated item", Toast.LENGTH_LONG).show();
        }


        gotoMainActivity();

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
