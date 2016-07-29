package com.expensedroid.expensedroid.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.expensedroid.expensedroid.R;
import com.expensedroid.expensedroid.SettingsIO;

/**
 * Created by S. Ameli on 21/07/16.
 */
public class DialogFilterAmount extends DialogFragment {
    private String selectedOperator; // Equals, Before or After
    private int selectedAmount = 0;
    private int selectedAmoutEnd = 0;

    private static final String EQUAL_STR = "equal to";
    private static final String BEFORE_STR = "smaller than";
    private static final String AFTER_STR = "larger than";
    private static final String BETWEEN_STR = "between";
    private int position_BETWEEN_in_spinner = 3;


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



        String[] items = new String[] { EQUAL_STR, BEFORE_STR, AFTER_STR, BETWEEN_STR};

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
        else if(previous_selectedOperator.equals(BETWEEN_STR)) {
            spinnerPos = 3;
            position_BETWEEN_in_spinner = spinnerPos;

            layoutGroupEnd.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(spinnerPos);


        final EditText editText = (EditText) view.findViewById(R.id.editText_filter_dialog_amount);
        final EditText editTextEnd = (EditText) view.findViewById(R.id.editText_filter_dialog_amount_end);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        int previous_filterAmount = SettingsIO.readData(getContext(), 0, "menu_filter_amount_checkbox_selectedamount");
        editText.setText(String.valueOf(previous_filterAmount));
        editText.setSelection(editText.getText().length());


        final View v = view;

        builder
                .setView(view)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //if(selectedOperator.equals())

                        String amountStr = editText.getText().toString();
                        //String amountStrEnd = editTextEnd.getText().toString();
                        if(amountStr == null || amountStr.isEmpty()) {
                            Toast.makeText(v.getContext(), "Amount field is empty", Toast.LENGTH_SHORT).show();
                        }else {
                            DialogListener activity = (DialogListener) getActivity();
                            try {
                                //reloadSelectedDates();
                                selectedAmount = Integer.parseInt(editText.getText().toString());
                                activity.onApplyFilterAmountBtn(selectedOperator, selectedAmount);

                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
