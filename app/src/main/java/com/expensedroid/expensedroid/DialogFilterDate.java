package com.expensedroid.expensedroid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by S. Ameli on 11/07/16.
 */
public class DialogFilterDate extends DialogFragment {

    private String selectedEquality; // Equals, Before or After
    private String selectedDate; // 2016-12-30

    private static final String EQUAL_STR = "Equal";
    private static final String BEFORE_STR = "Before";
    private static final String AFTER_STR = "After";


    // private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        View rootView = inflater.inflate(R.layout.dialog_filter_date, container, false);

        //getDialog().setTitle("Filter Date");

        Button dismissButton = (Button) rootView.findViewById(R.id.btn_filter_date_cancel);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);

        ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_spinner_layout, items);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(adapter);



        String previous_selectedEquality = SettingsIO.readData(getContext(), "=", "menu_filter_date_checkbox_selectedequality");
        System.out.println(">>> previous_selectedEquality: " + previous_selectedEquality);
        int spinnerPos = 0;
        if(previous_selectedEquality.equals(EQUAL_STR))
            spinnerPos = 0;
        else if(previous_selectedEquality.equals(BEFORE_STR))
            spinnerPos = 1;
        else if(previous_selectedEquality.equals(AFTER_STR))
            spinnerPos = 2;
        spinner.setSelection(spinnerPos);


        setDatePickerDate(rootView);



        Button applyFilterButton = (Button) rootView.findViewById(R.id.btn_filter_date_applyfilter);
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogListener activity = (DialogListener) getActivity();
                try{
                    activity.onApplyFilterDateBtn(selectedEquality, getDatePickerDate());

                }catch(Exception e){
                    System.out.println(e);
                }
                dismiss();
            }
        });


        /*
        Button btn_show_date = (Button) rootView.findViewById(R.id.btn_filter_date_showdatefrag);
        btn_show_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFrag newFragment = new DatePickerFrag();
                newFragment.set_id_btn_to_modify(R.id.btn_filter_date_showdatefrag);
                //FragmentManager fm = getSupportFragmentManager();
                //newFragment.show(rootView.getfr,"Date Picker");
            }
        });
*/


        return rootView;
    }


    private String getDatePickerDate(){
        String str = "";
        DatePicker datePicker = (DatePicker) this.getDialog().findViewById(R.id.filter_date_datePicker);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String monthStr = String.format("%02d", (month+1));
        String dayStr = String.format("%02d", day);

        str = year + "-" + monthStr + "-" + dayStr;
        //System.out.println(">>>> str: " + str);


        return str;
    }

    /*
     * This method is used to load previously selected date from settings and load it in the datePicker object
     */
    private void setDatePickerDate(View view){
        String previous_selectedDate = SettingsIO.readData(getContext(), "", "menu_filter_date_checkbox_selecteddate");
        System.out.println(">>>> previous_selectedDate: " + previous_selectedDate);
        if(previous_selectedDate != null & previous_selectedDate != "" & previous_selectedDate.isEmpty() == false){

            Date date = DatabaseHelper.parseDate(previous_selectedDate);
            DatePicker datePicker = (DatePicker) view.findViewById(R.id.filter_date_datePicker);

            if(datePicker != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
            }else{
                System.out.println(">>>> setDatePickerDate: datePicker is null");
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
        }
        return operatorStr;
    }

    /*

    public DialogFilterDate() {

        //super(context);
        //fragmentManager = this.getOwnerActivity().getFragmentManager()


        //setting custom layout to dialog
        setContentView(R.layout.dialog_filter_date);
        setTitle("Custom Dialog");







        //show();
    }
    */


}
