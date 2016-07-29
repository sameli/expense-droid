package com.expensedroid.expensedroid;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by S. Ameli on 11/07/16.
 */
public class DialogFilterDate extends DialogFragment {

    private String selectedOperator; // Equals, Before or After
    private String selectedDate; // 2016-12-30
    private String selectedDateEnd;

    private static final String EQUAL_STR = "Equal";
    private static final String BEFORE_STR = "Before";
    private static final String AFTER_STR = "After";
    public static final String BETWEEN_STR = "Between"; // This is also used in DatabaseHelper class
    private int position_BETWEEN_in_spinner = 3; // position of "Between" element in the spinner (default is 3)



    // private FragmentManager fragmentManager;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter_date, null);

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.filter_date_datePicker_end);
        datePicker.setVisibility(View.INVISIBLE);

        final Spinner spinner = (Spinner) view.findViewById(R.id.filter_date_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String str = (String) spinner.getItemAtPosition(i);
                selectedOperator = str;
                if(i == position_BETWEEN_in_spinner){
                    datePicker.setVisibility(View.VISIBLE);
                }else{
                    datePicker.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] items = new String[] { EQUAL_STR, BEFORE_STR, AFTER_STR, BETWEEN_STR};

        ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_spinner_layout, items);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(adapter);




        String previous_selectedOperator = SettingsIO.readData(getContext(), EQUAL_STR, "menu_filter_date_checkbox_selected_operator");
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
            datePicker.setVisibility(View.VISIBLE);

        }
        spinner.setSelection(spinnerPos);


        setDatePickerDate(view);

        builder
                .setView(view)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DialogListener activity = (DialogListener) getActivity();
                        try{
                            reloadSelectedDates();
                            activity.onApplyFilterDateBtn(selectedOperator, selectedDate, selectedDateEnd);

                        }catch(Exception e){
                            System.out.println(e);
                        }
                        dismiss();
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

    private void reloadSelectedDates(){
        selectedDate = getDatePickerDate(R.id.filter_date_datePicker);
        selectedDateEnd = getDatePickerDate(R.id.filter_date_datePicker_end);
    }

    private String getDatePickerDate(int elementID){
        String str = "";
        DatePicker datePicker = (DatePicker) this.getDialog().findViewById(elementID);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String monthStr = String.format("%02d", (month+1));
        String dayStr = String.format("%02d", day);

        str = year + "-" + monthStr + "-" + dayStr;

        return str;
    }

    private void setDatePickerDate(View view){
        setDatePickerDate(view,"menu_filter_date_checkbox_selecteddate", R.id.filter_date_datePicker);
        setDatePickerDate(view,"menu_filter_date_checkbox_selecteddate_end", R.id.filter_date_datePicker_end);
    }

    /*
     * This method is used to load previously selected date from settings and load it in the datePicker object
     */
    private void setDatePickerDate(View view, String settingsKey, int elementID){
        String previous_selectedDate = SettingsIO.readData(getContext(), "", settingsKey);
        //System.out.println("previous_selectedDate: " + previous_selectedDate);
        if(previous_selectedDate != null & previous_selectedDate != "" & previous_selectedDate.isEmpty() == false){

            Date date = DatabaseHelper.parseDate(previous_selectedDate);
            DatePicker datePicker = (DatePicker) view.findViewById(elementID);

            if(datePicker != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
            }else{
                System.out.println("setDatePickerDate: datePicker is null");
            }

        }

    }

    public static String getSmallOperatorStr(String str){
        if(str == null) return "";
        String operatorStr = "";

        if(str.equals(EQUAL_STR)){
            operatorStr = "=";
        }else if(str.equals(BEFORE_STR)){
            operatorStr = "<";
        }else if(str.equals(AFTER_STR)){
            operatorStr = ">";
        }else if(str.equals(BETWEEN_STR)){
            operatorStr = BETWEEN_STR;
        }
        return operatorStr;
    }


}
