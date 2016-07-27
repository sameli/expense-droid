package com.expensedroid.expensedroid;

/**
 * Created by S. Ameli on 12/07/16.
 */
public interface DialogListener {
    void onApplyFilterDateBtn(String selectedOperator, String selectedDate);
    void onApplyFilterAmountBtn(String selectedOperator, int selectedAmount);
    void onApplyCreateAccountBtn(String accountName);
    void onApplyRenameAccountBtn(String accountName);

}
