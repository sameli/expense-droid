package com.expensedroid.expensedroid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by S. Ameli on 11/07/16.
 */
public class DialogFilterDate  extends DialogFragment {

    private String selectedEquality; // Equals, Before or After
    private String selectedDate; // 2016-12-30
    private boolean isChecked;

    private static final String EQUAL_STR = "Equal";
    private static final String BEFORE_STR = "Before";
    private static final String AFTER_STR = "After";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_filter_date, container, false);
        getDialog().setTitle("Simple Dialog");

        Button dismissButton = (Button) rootView.findViewById(R.id.btn_filter_date_cancel);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                isChecked = false;
            }
        });

        final Spinner spinner = (Spinner) rootView.findViewById(R.id.filter_date_spinner);

        // Spinner click listener
        // spinner.setOnItemSelectedListener(new setonitemclicklistener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String str = (String) spinner.getItemAtPosition(i);
                //System.out.println(">>>> onItemSelected: " + i + ", str: " + str);
                selectedEquality = str;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] items = new String[] { EQUAL_STR, BEFORE_STR, AFTER_STR};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, items);

        spinner.setAdapter(adapter);





        return rootView;
    }
}