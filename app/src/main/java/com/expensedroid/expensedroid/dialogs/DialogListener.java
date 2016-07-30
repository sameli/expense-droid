package com.expensedroid.expensedroid.dialogs;

/**
 * Created by S. Ameli on 12/07/16.
 */
public interface DialogListener {
    void onApplyFilterDateBtn(String selectedOperator, String selectedDate, String selectedDateEnd);
    void onApplyFilterAmountBtn(String selectedOperator, Integer selectedAmount, Integer selectedAmountEnd);
    void onApplyCreateAccountBtn(String accountName);
    void onApplyRenameAccountBtn(String accountName);

}
