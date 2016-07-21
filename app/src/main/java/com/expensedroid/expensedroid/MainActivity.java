package com.expensedroid.expensedroid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by S. Ameli on 01/07/16.
 */
public class MainActivity extends AppCompatActivity implements DialogFilterListener {

    protected ArrayList<Transaction> data;
    DatabaseHelper mydb;
    public static final String INTENT_EDIT_MSG_ID = "IDEDIT1000";
    private boolean filterActivated = false;

    private DialogFilterDate dialogFilterDate;
    private DialogFilterAmount dialogFilterAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View view1 = (View) findViewById(R.id.mainview1);
        //view1.setBackgroundColor(Color.argb(100,0,200,0));
        view1.setBackgroundColor(Color.argb(255, 204, 255, 204));

        dialogFilterDate = new DialogFilterDate();
        dialogFilterAmount = new DialogFilterAmount();


        printMsg(">>>>>>>MainActivity onCreate ------");


        //data = new ArrayList<>();
        /*
        for(int i = 0;i<30;i++) {
            data.add(new Transaction("transaction# " + i, 500 + i, 100+i));
        }
        */


        mydb = new DatabaseHelper(this);

        /*
        int numberOfRows = mydb.numberOfRows();

        for(int i = 0;i<3;i++) {
            mydb.insertTransaction(new Transaction("transaction# " + numberOfRows, 500 + i, 100+i));
            numberOfRows++;
        }
        */


        data = mydb.getAllTransactions(this);


        //ArrayAdapter<Transaction> transArrayAdapter = new ArrayAdapter<Transaction>(this, android.R.layout.simple_list_item_1, data);
        ListView listview = (ListView) findViewById(R.id.listView);
        //listview.setAdapter(transArrayAdapter);
        listview.setAdapter(new CustomListViewAdapter(this, data));


        // adding events to ListView
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Transaction trans = data.get(position);
                displayDetail(trans);
            }
        });


        TextView amountTotal = (TextView)findViewById(R.id.textView_amount_total);
        double sum = calcTotal(data);
        amountTotal.setText("$" + String.format("%.2f", sum));

    }

    private double calcTotal(ArrayList<Transaction> data){
        double sum = 0;
        for(int i =0;i<data.size();i++){
            sum += data.get(i).getAmount();
        }
        return sum;
    }

    private void changeFilterMenuColor(Menu menu, int color){
        MenuItem menuItem = menu.findItem(R.id.menu_id_filter);
        CharSequence menuTitle = menuItem.getTitle();
        SpannableString styledMenuTitle = new SpannableString(menuTitle);
        styledMenuTitle.setSpan(new ForegroundColorSpan(color), 0, menuTitle.length(), 0);
        menuItem.setTitle(styledMenuTitle);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //menu.add("Connected").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        //TextView filterItem = (TextView) menu.findItem(R.id.menu_id_filter).getActionView();
        //filterItem.setTextColor(Color.RED);


        checkIfAllFilterItemsChecked();
        if(filterActivated) {
            changeFilterMenuColor(menu, Color.GREEN);
        }else{
            changeFilterMenuColor(menu, Color.WHITE);
        }

        boolean isChecked_filter_date = SettingsIO.readData(this, false, "menu_filter_date_checkbox");// readSettings_booleanItem("menu_filter_date_checkbox", false);
        MenuItem item_filter_date = menu.findItem(R.id.submenu_filter_date);
        item_filter_date.setChecked(isChecked_filter_date);

        /*
        SettingsIO.saveData(this, selectedEquality, "menu_filter_date_checkbox_selectedequality");
        SettingsIO.saveData(this, selectedDate, "menu_filter_date_checkbox_selecteddate");
         */
        if(isChecked_filter_date){
            // if filter date is checked, then we read the selected date and selected equality from settings file and update the title of menu:
            String dateStr = SettingsIO.readData(this, "", "menu_filter_date_checkbox_selecteddate");
            String selectedEquality = SettingsIO.readData(this, "", "menu_filter_date_checkbox_selectedequality");
            String operatorStr = DialogFilterDate.getSmallOperatorStr(selectedEquality);

            item_filter_date.setTitle("Date " + operatorStr + " " + dateStr);
        }else {
            item_filter_date.setTitle("Date");
        }

        boolean isChecked_filter_amount = SettingsIO.readData(this, false, "menu_filter_amount_checkbox");//readSettings_booleanItem("menu_filter_amount_checkbox", false);
        MenuItem item_filter_amount = menu.findItem(R.id.submenu_filter_amount);
        item_filter_amount.setChecked(isChecked_filter_amount);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_id_add:
                Intent editIntent = new Intent(this, EditActivity.class);
                //editIntent.putExtra(INTENT_EDIT_MSG_ID, "some message here blabla");
                startActivity(editIntent); // this will switch to DetailActivity
                return true;
            case R.id.submenu_filter_date:


                printMsg("item.isChecked(): " + item.isChecked());

                if(item.isChecked() == false){
                    FragmentManager fm = getSupportFragmentManager();
                    dialogFilterDate.show(fm, "Date filter dialog");
                }else{
                    // if menu item is checked, then we set the menu date checkbox to false in the settings
                    // then we refresh activity so that the onCreate of MainActivity can removes the filter
                    SettingsIO.saveData(this, false, "menu_filter_date_checkbox");
                    //invalidateOptionsMenu();
                    refreshActivity();
                }

                /*
                isMenuItemChecked_put(item, "menu_filter_date_checkbox");
                checkIfAllFilterItemsChecked();
                invalidateOptionsMenu();
                */

                return true;
            case R.id.submenu_filter_amount:
                if(item.isChecked() == false){
                    FragmentManager fm = getSupportFragmentManager();
                    dialogFilterAmount.show(fm, "Amount filter dialog");
                }else {
                    SettingsIO.saveData(this, false, "menu_filter_amount_checkbox");
                    refreshActivity();
                }
                //isMenuItemChecked_put(item, "menu_filter_amount_checkbox");
                //checkIfAllFilterItemsChecked();
                //invalidateOptionsMenu();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // check if all of the menu items are checked from the settings
    private void checkIfAllFilterItemsChecked(){
        boolean isChecked_menu_filter_date = SettingsIO.readData(this, false, "menu_filter_date_checkbox");// settings.getBoolean("menu_filter_date_checkbox", false);
        boolean isChecked_menu_filter_amount = SettingsIO.readData(this, false, "menu_filter_amount_checkbox");// settings.getBoolean("menu_filter_amount_checkbox", false);

        if(isChecked_menu_filter_date || isChecked_menu_filter_amount){
            filterActivated = true;
        }else{
            filterActivated = false;
        }
    }

    private void displayDetail(Transaction trans) {
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(INTENT_EDIT_MSG_ID, trans);
        startActivity(editIntent);
    }


    private void printMsg(String str){
        System.out.println(str);
    }

    public void onMenuExit(MenuItem item) {
        this.finishAffinity();
    }

    @Override
    public void onApplyFilterDateBtn(String selectedEquality, String selectedDate) {

        SettingsIO.saveData(this, true, "menu_filter_date_checkbox");
        SettingsIO.saveData(this, selectedEquality, "menu_filter_date_checkbox_selectedequality");
        SettingsIO.saveData(this, selectedDate, "menu_filter_date_checkbox_selecteddate");
        //invalidateOptionsMenu();
        Toast.makeText(this, "Filter applied", Toast.LENGTH_LONG).show();

        refreshActivity();


        printMsg(">>>> onApplyFilterDateBtn: " + selectedEquality + ", " + selectedDate);

    }

    @Override
    public void onApplyFilterAmountBtn(String selectedEquality, int selectedAmount) {
        printMsg(">>>> onApplyFilterAmountBtn: " + selectedEquality + ", " + selectedAmount);

    }

    private void refreshActivity(){
        finish();
        startActivity(getIntent());
    }
}