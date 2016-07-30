package com.expensedroid.expensedroid.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.expensedroid.expensedroid.R;
import com.expensedroid.expensedroid.SettingsIO;
import com.expensedroid.expensedroid.Tools;

/**
 * Created by S. Ameli on 21/07/16.
 */
public class DialogFilterAmount extends DialogFragment {
    private String selectedOperator; // Equals, Before or After
    private int selectedAmountStart = 0;
    private int selectedAmountEnd = 0;

    private int position_BETWEEN_in_spinner = 3;
    private boolean isAlertDialogReady;


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter_amount, null);

        //setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        //View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_filter_amount, container, false);
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        final LinearLayout layoutGroupEnd = (LinearLayout) view.findViewById(R.id.layout_group_end);
        layoutGroupEnd.setVisibility(View.INVISIBLE);

        final Spinner spinner = (Spinner) view.findViewById(R.id.filter_amount_spinner);

        // Spinner click listener
        // spinner.setOnItemSelectedListener(new setonitemclicklistener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String str = (String) spinner.getItemAtPosition(i);
                //System.out.println(">>>> onItemSelected: " + i + ", str: " + str);
                selectedOperator = str;
                if(i == position_BETWEEN_in_spinner){
                    layoutGroupEnd.setVisibility(View.VISIBLE);
                }else{
                    layoutGroupEnd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        String[] items = new String[] { Tools.EQUAL_STR, Tools.SMALLER_THAN_STR, Tools.LARGER_THAN_STR, Tools.BETWEEN_STR};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);

        ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_spinner_layout, items);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(adapter);




        String previous_selectedOperator = SettingsIO.readData(getContext(), Tools.EQUAL_STR, Tools.SETTING_MENU_FILTER_AMOUNT_SELECTED_OPERATOR);
        //System.out.println(">>> previous_selectedOperator: " + previous_selectedOperator);
        int spinnerPos = 0;
        if(previous_selectedOperator.equals(Tools.EQUAL_STR))
            spinnerPos = 0;
        else if(previous_selectedOperator.equals(Tools.SMALLER_THAN_STR))
            spinnerPos = 1;
        else if(previous_selectedOperator.equals(Tools.LARGER_THAN_STR))
            spinnerPos = 2;
        else if(previous_selectedOperator.equals(Tools.BETWEEN_STR)) {
            spinnerPos = 3;
            position_BETWEEN_in_spinner = spinnerPos;

            layoutGroupEnd.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(spinnerPos);


        final EditText editTextStart = (EditText) view.findViewById(R.id.editText_filter_dialog_amount);
        final EditText editTextEnd = (EditText) view.findViewById(R.id.editText_filter_dialog_amount_end);

        Integer previous_filterAmount = SettingsIO.readData(getContext(), 0, Tools.SETTING_MENU_FILTER_AMOUNT_VALUE_1);
        if(previous_filterAmount != 0) {
            editTextStart.setText(String.valueOf(previous_filterAmount));
            editTextStart.setSelection(editTextStart.getText().length());
        }

        Integer previous_filterAmountEnd = SettingsIO.readData(getContext(), 0, Tools.SETTING_MENU_FILTER_AMOUNT_VALUE_2);
        if(previous_filterAmountEnd != 0) {
            editTextEnd.setText(String.valueOf(previous_filterAmountEnd));
        }

        showKeyboard();

        final View v = view;

        builder
                .setView(view)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // The listener is overwritten by alertDialog.setOnShowListener
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hideKeyboard(v);
                        dismiss();
                    }
                });


        final AlertDialog alertDialog = builder.create();

        this.isAlertDialogReady = false;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (isAlertDialogReady == false) {
                    Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogListener activity = (DialogListener) getActivity();

                            Integer amount1 = getNumberFromEditText(v, editTextStart);
                            Integer amount2 = getNumberFromEditText(v, editTextEnd);


                            if(selectedOperator.equals(Tools.BETWEEN_STR)) {
                                if(amount1 != null && amount2 != null) {

                                    if (amount1 < amount2) {
                                        activity.onApplyFilterAmountBtn(selectedOperator, amount1, amount2);
                                        hideKeyboard(v);
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "First amount must be smaller than the second amount", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }else { // operator is either equals to, before or after:
                                if(amount1 != null) {
                                    activity.onApplyFilterAmountBtn(selectedOperator, amount1, 0);
                                    hideKeyboard(v);
                                    dismiss();
                                }else{
                                    Toast.makeText(getActivity(), "Field is empty", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
                    isAlertDialogReady = true;
                }
            }
        });

        return alertDialog;
    }

    private Integer getNumberFromEditText(View view, EditText editText){

        try {
            Integer number = Integer.parseInt(editText.getText().toString());
            return number;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


}
