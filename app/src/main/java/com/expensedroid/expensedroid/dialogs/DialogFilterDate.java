package com.expensedroid.expensedroid.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.expensedroid.expensedroid.DatabaseHelper;
import com.expensedroid.expensedroid.R;
import com.expensedroid.expensedroid.SettingsIO;
import com.expensedroid.expensedroid.Tools;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by S. Ameli on 11/07/16.
 *
 * This class shows a dialog so the user can apply a filter based on the selected date.
 * This filter will persist for the life of application unless it is manually disabled by the user (from the menu)
 */
public class DialogFilterDate extends DialogFragment {

    private String selectedOperator; // Equals, Before or After
    private String selectedDateStart; // 2016-12-30
    private String selectedDateEnd;


    private int position_BETWEEN_in_spinner = 3; // position of "Between" element in the spinner (default is 3)

    private boolean isAlertDialogReady;


    // private FragmentManager fragmentManager;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter_date, null);


        final LinearLayout layoutGroupEnd = (LinearLayout) view.findViewById(R.id.layout_group_end);
        layoutGroupEnd.setVisibility(View.INVISIBLE);

        final Spinner spinner = (Spinner) view.findViewById(R.id.filter_date_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String str = (String) spinner.getItemAtPosition(i);
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

        String[] items = new String[] { Tools.EQUAL_STR, Tools.BEFORE_STR, Tools.AFTER_STR, Tools.BETWEEN_STR};

        ArrayAdapter adapter = new ArrayAdapter<CharSequence>(getContext(), R.layout.custom_spinner_layout, items);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(adapter);


        String previous_selectedOperator = SettingsIO.readData(getContext(), Tools.EQUAL_STR, Tools.SETTING_MENU_FILTER_DATE_SELECTED_OPERATOR);
        int spinnerPos = 0;
        if(previous_selectedOperator.equals(Tools.EQUAL_STR))
            spinnerPos = 0;
        else if(previous_selectedOperator.equals(Tools.BEFORE_STR))
            spinnerPos = 1;
        else if(previous_selectedOperator.equals(Tools.AFTER_STR))
            spinnerPos = 2;
        else if(previous_selectedOperator.equals(Tools.BETWEEN_STR)) {
            spinnerPos = 3;
            position_BETWEEN_in_spinner = spinnerPos;
            layoutGroupEnd.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(spinnerPos);


        setDatePickerDate(view);

        builder
                .setView(view)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        /*
                         * Body of this method will be overwritten by the setOnShowListener.
                         * Because showing Toast message closes the dialog, but we need to keep the dialog open.
                         * More details: When the user selects a start date that is after the end date, we have to
                         * show the user a message about the issue. But showing a toast message on Dialog closes the dialog,
                         * even if we don't call dismiss(). To fix this, we overwrite the onClickListener from the alertDialog object.
                         */

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                            try{
                                if(selectedOperator.equals(Tools.BETWEEN_STR)) {
                                    boolean isStartDateBeforeEndDate = reloadSelectedDates(); // check if start date is before end date
                                    if(isStartDateBeforeEndDate){
                                        activity.onApplyFilterDateBtn(selectedOperator, selectedDateStart, selectedDateEnd);
                                        dismiss();
                                    }else{
                                        Toast.makeText(getActivity(), "Start date must be before the End date", Toast.LENGTH_LONG).show();
                                    }
                                }else { // operator is either equals to, before or after:
                                    reloadStartDate();
                                    activity.onApplyFilterDateBtn(selectedOperator, selectedDateStart, "");
                                    dismiss();
                                }

                            }catch(Exception e){
                                System.out.println(e);
                            }


                        }
                    });
                    isAlertDialogReady = true;
                }
            }
        });

        return alertDialog;
    }

    private void reloadStartDate(){
        DatePicker datePickerStart = (DatePicker) this.getDialog().findViewById(R.id.filter_date_datePicker_start);
        selectedDateStart = datePickerToString(datePickerStart);
    }

    private boolean reloadSelectedDates(){

        DatePicker datePickerStart = (DatePicker) this.getDialog().findViewById(R.id.filter_date_datePicker_start);
        DatePicker datePickerEnd = (DatePicker) this.getDialog().findViewById(R.id.filter_date_datePicker_end);

        if(isStartDateBeforeDateEnd(datePickerStart, datePickerEnd)){
            selectedDateStart = datePickerToString(datePickerStart);
            selectedDateEnd = datePickerToString(datePickerEnd);
            return true;
        }else{
            return false;
        }
    }

    /*
     * This method converts the date of the given DatePicker to string
     */
    private String datePickerToString(DatePicker datePicker){
        String str = "";

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String monthStr = String.format("%02d", (month+1));
        String dayStr = String.format("%02d", day);

        str = year + "-" + monthStr + "-" + dayStr;

        return str;
    }

    /*
     * This method checks if the given start DatePicker is before end date
     */
    private boolean isStartDateBeforeDateEnd(DatePicker datePickerStart, DatePicker datePickerEnd){
        if(getDateOfDatePicker(datePickerStart).compareTo(getDateOfDatePicker(datePickerEnd)) < 0){
            return true; // dateStart is before dateEnd
        }
        return false; // this is when two dates are equal or dateStart is after dateEnd
    }

    /*
     * This method accepts a DatePicker object and returns its date
     */
    private Date getDateOfDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /*
     * This method read the settings file and loads the previously selected start and end dates
     */
    private void setDatePickerDate(View view){
        setDatePickerDate(view, Tools.SETTING_MENU_FILTER_DATE_VALUE_1, R.id.filter_date_datePicker_start);
        setDatePickerDate(view, Tools.SETTING_MENU_FILTER_DATE_VALUE_2, R.id.filter_date_datePicker_end);
        setEndDateOneDayAfterStartDate(view);
    }

    /*
     * This method sets the end date one day after start date in case the end date is equal to or before start date
     */
    private void setEndDateOneDayAfterStartDate(View view){

        DatePicker datePickerStart = (DatePicker) view.findViewById(R.id.filter_date_datePicker_start);
        DatePicker datePickerEnd = (DatePicker) view.findViewById(R.id.filter_date_datePicker_end);

        if(isStartDateBeforeDateEnd(datePickerStart, datePickerEnd) == false){
            Date dateStart = getDateOfDatePicker(datePickerStart);
            Date dateStartPlusOneDay = new Date(dateStart.getTime() + (1000 * 60 * 60 * 24)); // add one day to dateStart
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateStartPlusOneDay);
            int month = cal.get(Calendar.MONTH);
            datePickerEnd.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
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




}
