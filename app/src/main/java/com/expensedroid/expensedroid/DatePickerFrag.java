package com.expensedroid.expensedroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;


/**
 * Created by S. Ameli on 05/07/16.
 */
public class DatePickerFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int id_btn_to_modify;

    public DatePickerFrag(){
        id_btn_to_modify = -1;
    }

    public void set_id_btn_to_modify(int id){
        this.id_btn_to_modify = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    // this implements the interface method from DatePickerDialog.OnDateSetListener
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(id_btn_to_modify != -1) {
            TextView btn_date = (TextView) getActivity().findViewById(id_btn_to_modify);//R.id.btn_date);
            String monthStr = String.format("%02d", (month+1));
            String dayStr = String.format("%02d", day);
            String stringOfDate = year + "-" + monthStr + "-" + dayStr;
            btn_date.setText(stringOfDate);
        }
    }
}