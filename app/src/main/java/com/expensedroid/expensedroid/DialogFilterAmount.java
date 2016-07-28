package com.expensedroid.expensedroid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by S. Ameli on 21/07/16.
 */
public class DialogFilterAmount extends DialogFragment {
    private String selectedOperator; // Equals, Before or After
    private int selectedAmount = 0;

    private static final String EQUAL_STR = "=";
    private static final String BEFORE_STR = "<";
    private static final String AFTER_STR = ">";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_filter_amount, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button dismissButton = (Button) rootView.findViewById(R.id.dialog_filter_amount_btn_cancel);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final Spinner spinner = (Spinner) rootView.findViewById(R.id.filter_amount_spinner);

        // Spinner click listener
        // spinner.setOnItemSelectedListener(new setonitemclicklistener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String str = (String) spinner.getItemAtPosition(i);
                //System.out.println(">>>> onItemSelected: " + i + ", str: " + str);
                selectedOperator = str;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        String[] items = new String[] { EQUAL_STR, BEFORE_STR, AFTER_STR};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);

        ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_spinner_layout, items);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(adapter);


        String previous_selectedOperator = SettingsIO.readData(getContext(), "=", "menu_filter_amount_checkbox_selected_operator");
        System.out.println(">>> previous_selectedOperator: " + previous_selectedOperator);
        int spinnerPos = 0;
        if(previous_selectedOperator.equals(EQUAL_STR))
            spinnerPos = 0;
        else if(previous_selectedOperator.equals(BEFORE_STR))
            spinnerPos = 1;
        else if(previous_selectedOperator.equals(AFTER_STR))
            spinnerPos = 2;
        spinner.setSelection(spinnerPos);


        final EditText editText = (EditText) rootView.findViewById(R.id.editText_filter_dialog_amount);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        int previous_filterAmount = SettingsIO.readData(getContext(), 0, "menu_filter_amount_checkbox_selectedamount");
        editText.setText(String.valueOf(previous_filterAmount));
        editText.setSelection(editText.getText().length());

        Button applyFilterButton = (Button) rootView.findViewById(R.id.dialog_filter_amount_btn_applyfilter);
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String amountStr = editText.getText().toString();
                if(amountStr == null || amountStr.isEmpty()) {
                    Toast.makeText(v.getContext(), "Amount field is empty", Toast.LENGTH_SHORT).show();
                }else {
                    DialogListener activity = (DialogListener) getActivity();
                    try {
                        selectedAmount = Integer.parseInt(editText.getText().toString());
                        activity.onApplyFilterAmountBtn(selectedOperator, selectedAmount);

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    dismiss();
                }
            }
        });


        return rootView;
    }


}
