package com.expensedroid.expensedroid;

/**
 * Created by S. Ameli on 12/07/16.
 */
public interface DialogFilterListener {
    void onApplyFilterDateBtn(String selectedEquality, String selectedDate);
    void onApplyFilterAmountBtn(String selectedEquality, int selectedAmount);
}
