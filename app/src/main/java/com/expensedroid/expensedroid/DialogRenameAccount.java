package com.expensedroid.expensedroid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by S. Ameli on 25/07/16.
 */
public class DialogRenameAccount extends DialogFragment{

    private final int MAXIMUM_ACCOUNT_NAME_LENGTH = 20;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_rename_account, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button cancelButton = (Button) rootView.findViewById(R.id.dialog_rename_account_cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        final EditText editText = (EditText) rootView.findViewById(R.id.dialog_rename_account_edittext);
        editText.setText("");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        Button renameButton = (Button) rootView.findViewById(R.id.dialog_rename_account_rename_btn);

        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String accountNameStr = editText.getText().toString().trim();
                if(accountNameStr == null || accountNameStr.isEmpty()) {
                    Toast.makeText(v.getContext(), "Account name field is empty", Toast.LENGTH_LONG).show();
                }else if(accountNameStr.length() >= MAXIMUM_ACCOUNT_NAME_LENGTH){
                    Toast.makeText(v.getContext(), "Account name must be less than "+MAXIMUM_ACCOUNT_NAME_LENGTH+" characters", Toast.LENGTH_LONG).show();
                } else{
                    DialogListener activity = (DialogListener) getActivity();
                    activity.onApplyRenameAccountBtn(accountNameStr);
                    editText.setText("");

                    dismiss();
                }
            }
        });


        return rootView;
    }

}
