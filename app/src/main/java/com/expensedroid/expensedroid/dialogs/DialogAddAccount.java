package com.expensedroid.expensedroid.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.expensedroid.expensedroid.R;
import com.expensedroid.expensedroid.Tools;

/**
 * Created by S. Ameli on 23/07/16.
 *
 * This class shows a dialog so the user can add a new account.
 */
public class DialogAddAccount extends DialogFragment{

    private final int MAXIMUM_ACCOUNT_NAME_LENGTH = 20;
    private boolean isAlertDialogReady;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_account, null);


        final EditText editText = (EditText) view.findViewById(R.id.dialog_add_account_edittext);
        showKeyboard();

        final View v = view;

        builder
                .setView(view)
                .setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
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

                            String accountNameStr = editText.getText().toString().trim();
                            if(accountNameStr == null || accountNameStr.isEmpty()) {
                                Toast.makeText(v.getContext(), "Account name field is empty", Toast.LENGTH_SHORT).show();
                            }else if(accountNameStr.length() >= MAXIMUM_ACCOUNT_NAME_LENGTH){
                                Toast.makeText(v.getContext(), "Account name must be less than "+MAXIMUM_ACCOUNT_NAME_LENGTH+" characters", Toast.LENGTH_SHORT).show();
                            } else{
                                activity.onApplyCreateAccountBtn(accountNameStr.trim());
                                editText.setText("");
                                hideKeyboard(v);
                                dismiss();
                            }

                        }
                    });
                    isAlertDialogReady = true;
                }
            }
        });

        return alertDialog;
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
