package com.expensedroid.expensedroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by S. Ameli on 01/07/16.
 *
 * This is the main class of Expense Droid.
 * This class initializes the database and GUI elements.
 */
public class MainActivity extends AppCompatActivity implements DialogListener {

    // Constants:
    public static final String INTENT_EDIT_MSG_ID = "IDEDIT1000"; // Message id to transfer data from main to edit activity
    private final String DEFAULT_ACCOUNT_NAME = "Default Acct"; // default account name to be created for first time use
    private final int MENU_ACCOUNTS_ID = 103;
    int baseAcctMenuStartID = 15000; // some random large number to set for the ids of the auto generated menu items for acccounts

    private DatabaseHelper databaseHelper;
    private Map<Integer, AccountItem> map_MenuID_accountItem; // this variable is used to map generated menu ID with the accounts
    private boolean filterActivated = false; // if true, then the color of "filter" menu item will be green

    // these are dialog instances for filters and adding new account:
    private DialogFilterDate dialogFilterDate;
    private DialogFilterAmount dialogFilterAmount;
    private DialogAddAccount dialogAddAccount;
    private DialogRenameAccount dialogRenameAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    /*
     * This method loads the database , sets up the listview for transactions and shows the total amount for current account
     */
    private void initialize(){

        dialogFilterDate = new DialogFilterDate();
        dialogFilterAmount = new DialogFilterAmount();
        dialogAddAccount = new DialogAddAccount();
        dialogRenameAccount = new DialogRenameAccount();

        databaseHelper = new DatabaseHelper(this);

        if(databaseHelper.numberOfRowsInAccounts() == 0){
            int acct_id = (int) databaseHelper.insertAccount(DEFAULT_ACCOUNT_NAME);
            if(acct_id != -1) {
                SettingsIO.saveData(this, acct_id, "selected_acct_id");
                Toast.makeText(MainActivity.this, "New account created: "+ DEFAULT_ACCOUNT_NAME, Toast.LENGTH_SHORT).show();

            }
        }

        final ArrayList<Transaction> data  = databaseHelper.getAllTransactions(this);

        ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(new CustomListViewAdapter(this, data));

        // adding events to ListView
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Transaction trans = data.get(position);
                gotoEditActivity(trans);
            }
        });

        int selected_acct_id = SettingsIO.readData(this, -1, "selected_acct_id");
        if(selected_acct_id != -1) {
            String acct_name = databaseHelper.getAccountName(selected_acct_id);
            TextView accountTextview = (TextView)findViewById(R.id.textView_account);
            accountTextview.setText(acct_name);
        }

        showTotal(data);
    }

    /*
     * This method and sets the label for total amount of shown transactions
     */
    private void showTotal(ArrayList<Transaction> data){
        TextView amountTotal = (TextView)findViewById(R.id.textView_amount_total);
        double sum = calcTotal(data);

        String sign = (sum >= 0) ? "" : "-";
        double num = Math.abs(sum);
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        amountTotal.setText(sign + "$" + formatter.format(num));

        int color = ContextCompat.getColor(this, R.color.Black);
        if(sum > 0){
            color = ContextCompat.getColor(this, R.color.Green);
        }else if(sum < 0){
            color = ContextCompat.getColor(this, R.color.Red);
        }        amountTotal.setTextColor(color);
    }

    /*
     * This method calculates the total sum of the given data
     */
    private double calcTotal(ArrayList<Transaction> data){
        double sum = 0;
        for(int i =0;i<data.size();i++){
            sum += data.get(i).getAmount();
        }
        return sum;
    }

    /*
     * This method changes the color of the given menu item
     */
    private void changeMenuColor(Menu menu, int color){
        MenuItem menuItem = menu.findItem(R.id.menu_id_filter);
        CharSequence menuTitle = menuItem.getTitle();
        SpannableString styledMenuTitle = new SpannableString(menuTitle);
        styledMenuTitle.setSpan(new ForegroundColorSpan(color), 0, menuTitle.length(), 0);
        menuItem.setTitle(styledMenuTitle);
    }

    /*
     * This method is called when the program creates the menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        List<AccountItem> listOfAccts = databaseHelper.getAccounts();

        map_MenuID_accountItem = new HashMap<Integer, AccountItem>();
        int selected_acct_id = SettingsIO.readData(this, -1, "selected_acct_id");

        int numberOfAccounts = databaseHelper.numberOfRowsInAccounts();

        //SubMenu accounts_menu = menu.findItem(R.id.menu_id_accounts).getSubMenu();
        menu.addSubMenu(Menu.NONE, MENU_ACCOUNTS_ID, Menu.NONE,"Accounts [" + numberOfAccounts +"]");
        SubMenu accounts_submenu = menu.findItem(MENU_ACCOUNTS_ID).getSubMenu();

        for(AccountItem item : listOfAccts){
            map_MenuID_accountItem.put(baseAcctMenuStartID, item);

            // we add a submenu item here, the title will be renamed by spanStr
            accounts_submenu.add(Menu.NONE, baseAcctMenuStartID, Menu.NONE, item.acct_id + "- " + item.acct_name);
            MenuItem menuItem = accounts_submenu.findItem(baseAcctMenuStartID);

            int numberOfTransactions = databaseHelper.numberOfRowsInTransactions(item.acct_id);

            SpannableString spanStr = new SpannableString(item.acct_name + " [" + numberOfTransactions +"]");

            if(selected_acct_id == item.acct_id) {
                spanStr.setSpan(new StyleSpan(Typeface.BOLD), 0, spanStr.length(), 0);

            }

            menuItem.setTitle(spanStr);
            baseAcctMenuStartID++;

        }

        /*
        menu.addSubMenu(Menu.NONE, 3004, Menu.NONE,"Menu1");
        SubMenu themeMenu = menu.findItem(3004).getSubMenu();
        themeMenu.clear();
        themeMenu.add(Menu.NONE, 3005, Menu.NONE,"Menu1");
        themeMenu.add(Menu.NONE, 3006, Menu.NONE,"Menu2");
        */

        checkIfAllFilterItemsChecked();
        if(filterActivated) {
            changeMenuColor(menu, Color.GREEN);
        }else{
            changeMenuColor(menu, Color.WHITE);
        }

        boolean isChecked_filter_date = SettingsIO.readData(this, false, "menu_filter_date_checkbox");
        MenuItem item_filter_date = menu.findItem(R.id.submenu_filter_date);
        item_filter_date.setChecked(isChecked_filter_date);

        if(isChecked_filter_date){
            // if filter date is checked, then we read the selected date and selected operator from settings file and update the title of menu:
            String dateStr = SettingsIO.readData(this, "", "menu_filter_date_checkbox_selecteddate");
            String selectedOperator = SettingsIO.readData(this, "", "menu_filter_date_checkbox_selected_operator");
            String operatorStr = DialogFilterDate.getSmallOperatorStr(selectedOperator);

            if(operatorStr.equals(DialogFilterDate.BETWEEN_STR)){
                String dateEndStr = SettingsIO.readData(this, "", "menu_filter_date_checkbox_selecteddate_end");

                item_filter_date.setTitle(dateEndStr + " < " + "Date" + " < " + dateStr);
            }else{
                item_filter_date.setTitle("Date " + operatorStr + " " + dateStr);
            }
        }else {
            item_filter_date.setTitle("Date");
        }


        boolean isChecked_filter_amount = SettingsIO.readData(this, false, "menu_filter_amount_checkbox");
        MenuItem item_filter_amount = menu.findItem(R.id.submenu_filter_amount);
        item_filter_amount.setChecked(isChecked_filter_amount);

        if(isChecked_filter_amount){
            // if filter date is checked, then we read the selected date and selected operator from settings file and update the title of menu:
            int selectedamount = SettingsIO.readData(this, 0, "menu_filter_amount_checkbox_selectedamount");
            String selectedOperator = SettingsIO.readData(this, "", "menu_filter_amount_checkbox_selected_operator");

            item_filter_amount.setTitle("Amount " + selectedOperator + " " + selectedamount);
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
                startActivity(editIntent); // this will switch to EditActivity
                return true;
            case R.id.submenu_filter_date:


                //printMsg("item.isChecked(): " + item.isChecked());

                if(item.isChecked() == false){
                    FragmentManager fm = getSupportFragmentManager();
                    dialogFilterDate.show(fm, "Date filter dialog");
                }else{
                    // if menu item is checked, then we set the menu date checkbox to false in the settings
                    // then we refresh activity so that the onCreate of MainActivity can removes the filter
                    SettingsIO.saveData(this, false, "menu_filter_date_checkbox");
                    refreshActivity();
                }

                return true;
            case R.id.submenu_filter_amount:
                if(item.isChecked() == false){
                    FragmentManager fm = getSupportFragmentManager();
                    dialogFilterAmount.show(fm, "Amount filter dialog");
                }else {
                    SettingsIO.saveData(this, false, "menu_filter_amount_checkbox");
                    refreshActivity();
                }

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
                                //printMsg("selected_acct_id: " + selected_acct_id);
                                if(selected_acct_id != -1) {
                                    databaseHelper.deleteAccount(selected_acct_id);
                                    Toast.makeText(MainActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                    if(databaseHelper.numberOfRowsInAccounts() > 0) {
                                        List<AccountItem> acctItems = databaseHelper.getAccounts();
                                        int first_accountID = acctItems.get(0).acct_id;
                                        SettingsIO.saveData(MainActivity.this, first_accountID, "selected_acct_id");
                                    }
                                    refreshActivity();
                                }else{
                                    Toast.makeText(MainActivity.this, "Error: Account ID not valid", Toast.LENGTH_SHORT).show();
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;

            case R.id.menu_id_rename_account:
            // open dialog and rename account
                FragmentManager fm1 = getSupportFragmentManager();
                dialogRenameAccount.show(fm1, "Rename account dialog");
                return true;

            case R.id.menu_id_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // check if all of the menu items are checked from the settings
    private void checkIfAllFilterItemsChecked(){
        boolean isChecked_menu_filter_date = SettingsIO.readData(this, false, "menu_filter_date_checkbox");
        boolean isChecked_menu_filter_amount = SettingsIO.readData(this, false, "menu_filter_amount_checkbox");

        if(isChecked_menu_filter_date || isChecked_menu_filter_amount){
            filterActivated = true;
        }else{
            filterActivated = false;
        }
    }

    /*
     * This method sends a Transaction object to Edit activity and starts that activity
     */
    private void gotoEditActivity(Transaction trans) {
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(INTENT_EDIT_MSG_ID, trans);
        startActivity(editIntent);
    }

    /*
     * This method simplifies the System.out.println
     */
    private void printMsg(String str){
        System.out.println(str);
    }

    /*
     * this method will be called when the Exit menu item is clicked
     */
    public void onMenuExit(MenuItem item) {
        this.finishAffinity();
    }

    /*
     * This method implements a method from the DialogListener interface. This is called from DialogFilterDate class
     */
    @Override
    public void onApplyFilterDateBtn(String selectedOperator, String selectedDate, String selectedDateEnd) {

        SettingsIO.saveData(this, true, "menu_filter_date_checkbox");
        SettingsIO.saveData(this, selectedOperator, "menu_filter_date_checkbox_selected_operator");
        SettingsIO.saveData(this, selectedDate, "menu_filter_date_checkbox_selecteddate");
        SettingsIO.saveData(this, selectedDateEnd, "menu_filter_date_checkbox_selecteddate_end");

        Toast.makeText(this, "Date filter applied", Toast.LENGTH_SHORT).show();

        refreshActivity(); // once we refresh the activity, the database will call getAllTransactions and that method loads the filters
        //printMsg("onApplyFilterDateBtn: " + selectedOperator + ", " + selectedDate);
    }

    /*
     * This method implements a method from the DialogListener interface. This is called from DialogFilterAmount class
     */
    @Override
    public void onApplyFilterAmountBtn(String selectedOperator, int selectedAmount) {

        SettingsIO.saveData(this, true, "menu_filter_amount_checkbox");
        SettingsIO.saveData(this, selectedOperator, "menu_filter_amount_checkbox_selected_operator");
        SettingsIO.saveData(this, selectedAmount, "menu_filter_amount_checkbox_selectedamount");
        Toast.makeText(this, "Amount filter applied", Toast.LENGTH_SHORT).show();

        refreshActivity();
        printMsg("onApplyFilterAmountBtn: " + selectedOperator + ", " + selectedAmount);
    }

    /*
     * This method implements a method from the DialogListener interface. This is called from DialogAddAccount class
     */
    @Override
    public void onApplyCreateAccountBtn(String accountName) {
        //printMsg("onApplyFilterAmountBtn: " + accountName);
        int acct_id = databaseHelper.insertAccount(accountName);
        if(acct_id != -1) {
            SettingsIO.saveData(this, acct_id, "selected_acct_id");
            Toast.makeText(this, "New account created", Toast.LENGTH_SHORT).show();
            refreshActivity();
        }else{
            Toast.makeText(this, "Error: Creating new account failed", Toast.LENGTH_SHORT).show();
        }

    }

    /*
     * This method implements a method from the DialogListener interface. This is called from DialogRenameAccount class
     */
    @Override
    public void onApplyRenameAccountBtn(String accountName) {

        // We rename the current shown account. We can get the id of current account from settings:
        int acct_id = SettingsIO.readData(this, -1, "selected_acct_id");
        if(acct_id != -1) {
            databaseHelper.updateAccounts(acct_id, accountName);
            Toast.makeText(this, "Account renamed to " + accountName, Toast.LENGTH_SHORT).show();
            refreshActivity();
        }else{
            Toast.makeText(this, "Error: Renaming account failed", Toast.LENGTH_SHORT).show();
        }

    }

    /*
     * This method refreshes the Main activity and also the menu items. This is called whenever we need to update menu or listview on the main activity
     */
    public void refreshActivity(){
        initialize();
        invalidateOptionsMenu();
    }

    /*
     * This method is called when the main activity is resumed.
     * It calls the refreshActivity to reload new data. This is necessary when the user resets the database from the settings activity
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshActivity();
    }


}