package com.expensedroid.expensedroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.expensedroid.expensedroid.dialogs.DialogDatePicker;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by S. Ameli on 03/07/16.
 * This class is used to edit a transaction
 */
public class EditActivity extends AppCompatActivity {

    private int database_id = -1;
    private Date date = null; // we need this date object so when we are in edit mode, we need to get a hold of date so we can send it to the datepicker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.Blue_1));
        }



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
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            getSupportActionBar().setTitle("Edit");

            Transaction trans = (Transaction) intent.getExtras().getSerializable(Tools.INTENT_EDIT_MSG_ID);

            EditText editText_title = (EditText) findViewById(R.id.editText_title);
            editText_title.setText(trans.getTitle());

            EditText editText_amount = (EditText) findViewById(R.id.editText_amount);
            DecimalFormat formatter = new DecimalFormat("#.00");
            editText_amount.setText(formatter.format(trans.getAmount()));

            Button btn_date = (Button) findViewById(R.id.btn_date);
            btn_date.setText(String.valueOf(trans.getDateString()));
            date = trans.getDate(); // we store date object so we can send it to datepicker when user clicks on the date button

            EditText editText_notes = (EditText) findViewById(R.id.editText_notes);
            editText_notes.setText(trans.getNotes());

            database_id = trans.getTransaction_id();

        }else{
            getSupportActionBar().setTitle("Add new item");
            database_id = -1;
            Button btnDelete = (Button) findViewById(R.id.btn_delete);
            btnDelete.setVisibility(View.INVISIBLE);
        }

    }

    public void btnSave(View view) {
        EditText editText_title = (EditText) findViewById(R.id.editText_title);
        String title = editText_title.getText().toString().trim();

        EditText editText_amount = (EditText) findViewById(R.id.editText_amount);
        String amount_str = editText_amount.getText().toString();

        Button btn_date = (Button) findViewById(R.id.btn_date);
        String date_str = btn_date.getText().toString();

        EditText editText_notes = (EditText) findViewById(R.id.editText_notes);
        String notes = editText_notes.getText().toString();


        if(title.length() == 0 || amount_str.length() == 0 || date_str.length() == 0){
            Toast.makeText(EditActivity.this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amount_str);
        String dateStr = ((Button) findViewById(R.id.btn_date)).getText().toString();

        int selected_acct_id = SettingsIO.readData(this, -1, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);
        if(selected_acct_id == -1){
            Toast.makeText(EditActivity.this, "Error: Account has not been selected", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper mydb = new DatabaseHelper(this);
        if(database_id == -1){ // this means database row id has not been set, and we add new item to database

            Transaction tmpTransaction = new Transaction(title, amount , DatabaseHelper.parseDate(dateStr), notes);
            tmpTransaction.setAccount_id(selected_acct_id);

            int result = mydb.insertTransaction(tmpTransaction);
            String msg = "";
            if(result != -1)
                msg = "Added new item to database";
            else
                msg = "Error: item was NOT added to the database";

            Toast.makeText(EditActivity.this, msg, Toast.LENGTH_SHORT).show();
        }else{ // we have an database_id. we need to update that row on database
            mydb.updateTransaction(database_id, title, amount, dateStr, notes);
        }


        gotoMainActivity();

    }

    private void gotoMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void btnOpenDateDialog(View view) {
        DialogDatePicker dialogDatePicker = new DialogDatePicker();
        if(database_id != 0 && date != null){
            dialogDatePicker.setDate(date);
        }
        dialogDatePicker.set_id_btn_to_modify(R.id.btn_date);
        dialogDatePicker.set_id_next_element_focus(R.id.editText_notes);
        dialogDatePicker.show(getFragmentManager(),"Date Picker");
    }

    public void btnDiscard(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent); // this will switch to MainActivity
    }

    public void btnDelete(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Current Transaction")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(database_id != -1){
                            DatabaseHelper mydb = new DatabaseHelper(EditActivity.this);
                            mydb.deleteTransaction(database_id);
                            Toast.makeText(EditActivity.this, "Deleted item", Toast.LENGTH_SHORT).show();
                            gotoMainActivity();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();


    }
}
