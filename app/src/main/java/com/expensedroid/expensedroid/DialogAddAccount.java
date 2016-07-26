package com.expensedroid.expensedroid;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by S. Ameli on 23/07/16.
 */
public class DialogAddAccount extends DialogFragment{

        private final int MAXIMUM_ACCOUNT_NAME_LENGTH = 20;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            View rootView = inflater.inflate(R.layout.dialog_add_account, container, false);
            //getDialog().setTitle("Filter amount");

            Button cancelButton = (Button) rootView.findViewById(R.id.dialog_add_account_cancel_btn);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });


            final EditText editText = (EditText) rootView.findViewById(R.id.dialog_add_account_edittext);


            Button createButton = (Button) rootView.findViewById(R.id.dialog_add_account_create_btn);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String accountNameStr = editText.getText().toString();
                    if(accountNameStr == null || accountNameStr.isEmpty()) {
                        Toast.makeText(v.getContext(), "Account name field is empty", Toast.LENGTH_LONG).show();
                    }else if(accountNameStr.length() >= MAXIMUM_ACCOUNT_NAME_LENGTH){
                        Toast.makeText(v.getContext(), "Account name must be less than "+MAXIMUM_ACCOUNT_NAME_LENGTH+" characters", Toast.LENGTH_LONG).show();
                    } else{
                        DialogListener activity = (DialogListener) getActivity();
                        activity.onApplyCreateAccountBtn(accountNameStr);

                        dismiss();
                    }
                }
            });



            return rootView;
        }
}
