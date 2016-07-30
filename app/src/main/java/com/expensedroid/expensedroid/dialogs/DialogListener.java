package com.expensedroid.expensedroid.dialogs;

/**
 * Created by S. Ameli on 12/07/16.
 *
 * This interface is used to send data from dialogs to the main activity
 */
public interface DialogListener {
    void onApplyFilterDateBtn(String selectedOperator, String selectedDate, String selectedDateEnd);
    void onApplyFilterAmountBtn(String selectedOperator, Integer selectedAmount, Integer selectedAmountEnd);
    void onApplyCreateAccountBtn(String accountName);
    void onApplyRenameAccountBtn(String accountName);

}
