package com.expensedroid.expensedroid;

/**
 * Created by S. Ameli on 30/07/16.
 * This class contains some of the frequently used constants and methods in this program
 */
public class Tools {

    // These constants are used in DialogFilterAmount, DialogFilterDate, MainActivity and Databasehelpter:
    // These constants are used in the menus
    public static final String EQUAL_STR = "equal to";
    public static final String BEFORE_STR = "before";
    public static final String SMALLER_THAN_STR = "smaller than";
    public static final String LARGER_THAN_STR = "larger than";
    public static final String AFTER_STR = "after";
    public static final String BETWEEN_STR = "between";


    // All setting keys start with SETTING_ prefix:
    public static final String SETTING_MENU_FILTER_AMOUNT_VALUE_1 = "menu_filter_amount_value_1";
    public static final String SETTING_MENU_FILTER_AMOUNT_VALUE_2 = "menu_filter_amount_value_2";
    public static final String SETTING_MENU_FILTER_AMOUNT_SELECTED_OPERATOR = "menu_filter_amount_selected_operator";

    public static final String SETTING_MENU_FILTER_DATE_VALUE_1 = "menu_filter_date_value_1";
    public static final String SETTING_MENU_FILTER_DATE_VALUE_2 = "menu_filter_date_value_2";
    public static final String SETTING_MENU_FILTER_DATE_SELECTED_OPERATOR = "menu_filter_date_selected_operator";

    public static final String SETTING_MENU_FILTER_DATE_CHECKBOX = "menu_filter_date_checkbox_value";
    public static final String SETTING_MENU_FILTER_AMOUNT_CHECKBOX = "menu_filter_amount_checkbox_value";


    public static final String PREFERENCE_LICENSE = "pref_key_license";
    public static final String PREFERENCE_RESET_DATABASE = "pref_key_reset_database";
    public static final String PREFERENCE_RESET_SETTINGS = "pref_key_reset_settings";
    public static final String PREFERENCE_SELECTED_ACCOUNT_ID = "selected_acct_id";

    public static final String SETTINGS_TITLE = "settings";

    public static final String INTENT_EDIT_MSG_ID = "IDEDIT1000"; // Message id to transfer data from main to edit activity

    /*
     * This method converts the given string to an operator compatible with sqlite
     */
    public static String getSmallOperatorStr(String str){
        if(str == null) return "";
        String operatorStr = "";

        if(str.equals(EQUAL_STR)){
            operatorStr = "=";
        }else if(str.equals(BEFORE_STR) || str.equals(SMALLER_THAN_STR)){
            operatorStr = "<";
        }else if(str.equals(AFTER_STR) || str.equals(LARGER_THAN_STR)){
            operatorStr = ">";
        }else if(str.equals(BETWEEN_STR)){
            operatorStr = BETWEEN_STR;
        }
        return operatorStr;
    }

}
