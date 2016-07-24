package com.expensedroid.expensedroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by S. Ameli on 01/07/16.
 */
public class MainActivity extends AppCompatActivity implements DialogListener {

    protected ArrayList<Transaction> data;
    DatabaseHelper mydb;
    public static final String INTENT_EDIT_MSG_ID = "IDEDIT1000";
    public static final int DATABASE_VERSION = 3;
    int baseAcctMenuStartID = 15000; // some random large number to set for the ids of the auto generated menu items for acccounts
    Map<Integer, AccountItem> map_MenuID_accountItem;
    private boolean filterActivated = false;

    private DialogFilterDate dialogFilterDate;
    private DialogFilterAmount dialogFilterAmount;
    private DialogAddAccount dialogAddAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View view1 = (View) findViewById(R.id.mainview1);
        //view1.setBackgroundColor(Color.argb(100,0,200,0));
        //view1.setBackgroundColor(Color.argb(255, 204, 255, 204));

        dialogFilterDate = new DialogFilterDate();
        dialogFilterAmount = new DialogFilterAmount();
        dialogAddAccount = new DialogAddAccount();


        printMsg(">>>>>>>MainActivity onCreate ------");


        //data = new ArrayList<>();
        /*
        for(int i = 0;i<30;i++) {
            data.add(new Transaction("transaction# " + i, 500 + i, 100+i));
        }
        */


        mydb = new DatabaseHelper(this, MainActivity.DATABASE_VERSION);


        if(mydb.numberOfRowsInAccounts() == 0){
            int acct_id = (int) mydb.insertAccount("Default Acct");
            if(acct_id != -1) {
                SettingsIO.saveData(this, acct_id, "selected_acct_id");
            }
        }

        /*
        int numberOfRowsInTransactions = mydb.numberOfRowsInTransactions();

        for(int i = 0;i<3;i++) {
            mydb.insertTransaction(new Transaction("transaction# " + numberOfRowsInTransactions, 500 + i, 100+i));
            numberOfRowsInTransactions++;
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

        int selected_acct_id = SettingsIO.readData(this, -1, "selected_acct_id");
        if(selected_acct_id != -1) {
            String acct_name = mydb.getAccountName(selected_acct_id);
            TextView accountTextview = (TextView)findViewById(R.id.textView_account);
            accountTextview.setText(acct_name);

            //textView_account
        }


        showTotal();

    }

    private void showTotal(){
        TextView amountTotal = (TextView)findViewById(R.id.textView_amount_total);
        double sum = calcTotal(data);

        String sign = (sum >= 0) ? "" : "-";
        double num = Math.abs(sum);
        amountTotal.setText(sign + "$" + String.format("%.2f", num));
        int color = (sum > 0) ? Color.parseColor("#008000") : Color.parseColor("#cc0000"); // first color is green, second is red
        amountTotal.setTextColor(color);
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

        List<AccountItem> listOfAccts = mydb.getAccounts();

        map_MenuID_accountItem = new HashMap<Integer, AccountItem>();
        int selected_acct_id = SettingsIO.readData(this, -1, "selected_acct_id");


        for(AccountItem item : listOfAccts){
            map_MenuID_accountItem.put(baseAcctMenuStartID, item);
            menu.add(Menu.NONE, baseAcctMenuStartID, Menu.NONE, item.acct_id + "- " + item.acct_name);
            MenuItem menuItem = menu.findItem(baseAcctMenuStartID);
            menuItem.setCheckable(true);

            if(selected_acct_id == item.acct_id) {
                menuItem.setChecked(true);
            }
            baseAcctMenuStartID++;

        }



        /*
        menu.addSubMenu(Menu.NONE, 3004, Menu.NONE,"Menu1");
        SubMenu themeMenu = menu.findItem(3004).getSubMenu();
        themeMenu.clear();
        themeMenu.add(Menu.NONE, 3005, Menu.NONE,"Menu1");
        themeMenu.add(Menu.NONE, 3006, Menu.NONE,"Menu2");
        */

        //mydb.get


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

        //---------------------------

        boolean isChecked_filter_amount = SettingsIO.readData(this, false, "menu_filter_amount_checkbox");//readSettings_booleanItem("menu_filter_amount_checkbox", false);
        MenuItem item_filter_amount = menu.findItem(R.id.submenu_filter_amount);
        item_filter_amount.setChecked(isChecked_filter_amount);

        if(isChecked_filter_amount){
            // if filter date is checked, then we read the selected date and selected equality from settings file and update the title of menu:
            int selectedamount = SettingsIO.readData(this, 0, "menu_filter_amount_checkbox_selectedamount");
            String selectedEquality = SettingsIO.readData(this, "", "menu_filter_amount_checkbox_selectedequality");

            item_filter_amount.setTitle("Amount " + selectedEquality + " " + selectedamount);
        }else {
            item_filter_amount.setTitle("Amount");
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if(map_MenuID_accountItem.containsKey(itemID)){

            AccountItem accountItem = map_MenuID_accountItem.get(itemID);
            SettingsIO.saveData(this, accountItem.acct_id, "selected_acct_id");
            refreshActivity();
            return true;
        }

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
                return true;
            case R.id.menu_id_add_account:
            // open dialog to add new account
                FragmentManager fm = getSupportFragmentManager();
                dialogAddAccount.show(fm, "Add account dialog");
                return true;
            //case R.id.menu_id_exit:
                //return true;
            case R.id.menu_id_delete_current_account:
                // open dialog, ask for confirmation to delete the current account
                new AlertDialog.Builder(this)
                        .setTitle("Delete Current Account")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                int selected_acct_id = SettingsIO.readData(MainActivity.this, -1, "selected_acct_id");
                                printMsg(">> selected_acct_id: " + selected_acct_id);
                                if(selected_acct_id != -1) {
                                    mydb.deleteAccount(selected_acct_id);
                                    Toast.makeText(MainActivity.this, "Account deleted", Toast.LENGTH_LONG).show();
                                    if(mydb.numberOfRowsInAccounts() > 0) {
                                        List<AccountItem> acctItems = mydb.getAccounts();
                                        int first_accountID = acctItems.get(0).acct_id;
                                        SettingsIO.saveData(MainActivity.this, first_accountID, "selected_acct_id");
                                    }
                                    refreshActivity();
                                }else{
                                    Toast.makeText(MainActivity.this, "Error: Account ID not valid", Toast.LENGTH_LONG).show();
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;

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
        Toast.makeText(this, "Date filter applied", Toast.LENGTH_LONG).show();

        refreshActivity();


        printMsg(">>>> onApplyFilterDateBtn: " + selectedEquality + ", " + selectedDate);

    }

    @Override
    public void onApplyFilterAmountBtn(String selectedEquality, int selectedAmount) {

        SettingsIO.saveData(this, true, "menu_filter_amount_checkbox");
        SettingsIO.saveData(this, selectedEquality, "menu_filter_amount_checkbox_selectedequality");
        SettingsIO.saveData(this, selectedAmount, "menu_filter_amount_checkbox_selectedamount");
        //invalidateOptionsMenu();
        Toast.makeText(this, "Amount filter applied", Toast.LENGTH_LONG).show();

        refreshActivity();
        printMsg(">>>> onApplyFilterAmountBtn: " + selectedEquality + ", " + selectedAmount);

    }

    @Override
    public void onApplyCreateAccountBtn(String accountName) {
        printMsg(">>>> onApplyFilterAmountBtn: " + accountName);
        SettingsIO.saveData(this, accountName, "startup_account_to_view");
        int acct_id = mydb.insertAccount(accountName);
        if(acct_id != -1) {
            SettingsIO.saveData(this, acct_id, "selected_acct_id");
            Toast.makeText(this, "New account created", Toast.LENGTH_LONG).show();
            refreshActivity();
        }else{
            Toast.makeText(this, "Error: Creating new account failed", Toast.LENGTH_LONG).show();
        }

    }

    private void refreshActivity(){
        finish();
        startActivity(getIntent());
    }

}