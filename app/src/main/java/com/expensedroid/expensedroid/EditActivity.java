package com.expensedroid.expensedroid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#FF303F9F"));


        final Button btnDate = (Button) findViewById(R.id.btn_date);
        btnDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnDate.performClick();
                }
            }

        });

        Intent intent = getIntent();

        if(intent.getExtras() != null){
            getSupportActionBar().setTitle("Edit");

            Transaction trans = (Transaction) intent.getExtras().getSerializable(MainActivity.INTENT_EDIT_MSG_ID);

            EditText editText_title = (EditText) findViewById(R.id.editText_title);
            editText_title.setText(trans.getTitle());

            EditText editText_amount = (EditText) findViewById(R.id.editText_amount);
            editText_amount.setText(String.valueOf(trans.getAmount()));

            Button btn_date = (Button) findViewById(R.id.btn_date);
            btn_date.setText(String.valueOf(trans.getDateString()));

            EditText editText_notes = (EditText) findViewById(R.id.editText_notes);
            editText_notes.setText(trans.getNotes());

            database_id = trans.getTransaction_id();

            //TextView textview = (TextView) findViewById(R.id.textView2);
            //textview.setText("id is: " + String.valueOf(database_id));



        }else{
            getSupportActionBar().setTitle("Add new item");
            //System.out.println("Intent extra is empty, this is from add btn on main page");
            database_id = -1;
            Button btnDelete = (Button) findViewById(R.id.btn_delete);
            btnDelete.setVisibility(View.INVISIBLE);
        }

    }

    public void btnSave(View view) {
        EditText editText_title = (EditText) findViewById(R.id.editText_title);
        String title = editText_title.getText().toString();

        EditText editText_amount = (EditText) findViewById(R.id.editText_amount);
        String amount_str = editText_amount.getText().toString();

        Button btn_date = (Button) findViewById(R.id.btn_date);
        String date_str = btn_date.getText().toString();

        EditText editText_notes = (EditText) findViewById(R.id.editText_notes);
        String notes = editText_notes.getText().toString();


        if(title.length() == 0 || amount_str.length() == 0 || date_str.length() == 0){
            Toast.makeText(EditActivity.this, "Fill all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        double amount = Double.parseDouble(amount_str);
        //int expenses = Integer.parseInt(expenses_str); // replace with date parse from string
        String dateStr = ((Button) findViewById(R.id.btn_date)).getText().toString();
        //Date date = DatabaseHelper.parseDate(dateStr);

        int selected_acct_id = SettingsIO.readData(this, -1, "selected_acct_id");
        if(selected_acct_id == -1){
            Toast.makeText(EditActivity.this, "Error: Account has not been selected", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseHelper mydb = new DatabaseHelper(this, MainActivity.DATABASE_VERSION);
        if(database_id == -1){ // this means database row id has not been set, and we add new item to database

            Transaction tmpTransaction = new Transaction(title, amount , DatabaseHelper.parseDate(dateStr), notes);
            tmpTransaction.setAccount_id(selected_acct_id);

            int result = mydb.insertTransaction(tmpTransaction);
            String msg = "";
            if(result != -1)
                msg = "Added new item to database";
            else
                msg = "Error: item was NOT added to the database";

            Toast.makeText(EditActivity.this, msg, Toast.LENGTH_LONG).show();
        }else{ // we have an database_id. we need to update that row on database
            mydb.updateTransaction(database_id, title, amount, dateStr, notes);

            //Toast.makeText(EditActivity.this, "Updated item", Toast.LENGTH_LONG).show();
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
        newFragment.set_id_next_element_focus(R.id.editText_notes);
        newFragment.show(getFragmentManager(),"Date Picker");
    }

    public void btnDiscard(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to MainActivity
    }

    public void btnDelete(View view) {
        if(database_id != -1){
            DatabaseHelper mydb = new DatabaseHelper(this, MainActivity.DATABASE_VERSION);
            mydb.deleteTransaction(database_id);
            Toast.makeText(EditActivity.this, "Deleted item", Toast.LENGTH_LONG).show();
            gotoMainActivity();

        }
    }
}
