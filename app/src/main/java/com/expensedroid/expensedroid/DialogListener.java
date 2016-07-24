package com.expensedroid.expensedroid;

/**
 * Created by S. Ameli on 12/07/16.
 */
public interface DialogListener {
    void onApplyFilterDateBtn(String selectedEquality, String selectedDate);
    void onApplyFilterAmountBtn(String selectedEquality, int selectedAmount);
    void onApplyCreateAccountBtn(String accountName);

}
