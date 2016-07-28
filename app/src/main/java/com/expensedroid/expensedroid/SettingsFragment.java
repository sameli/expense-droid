package com.expensedroid.expensedroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by S. Ameli on 27/07/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        addLicenseListener();
        addResetDatabaseListener();

    }

    private void addLicenseListener(){
        Preference preference = (Preference) findPreference("pref_key_license");

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {

                String str = "\n\n \t\tExpense Droid Version "+ getString(R.string.app_version) +" (revision "+getString(R.string.app_revision)+")\n\n" +
                        "\t\tLicense:\n" +
                        "\t\tGNU General Public License 3\n" +
                        "\t\thttps://www.gnu.org/licenses/gpl.html\n"+
                        "\t\t© 2016 S. Ameli";

                TextView msg = new TextView(getActivity());
                msg.setText(str);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("License");
                builder.setView(msg);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();

                return true;
            }
        });
    }


    private void addResetDatabaseListener(){

        Preference preference = (Preference) findPreference("pref_key_reset_database");

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Reset Database");
                builder.setMessage("Are you sure you want to reset the database?"
                        + " All your transactions and accounts within this app will be lost.");
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper mydb = new DatabaseHelper(getActivity());
                        mydb.resetDatabase();

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();


                return true;
            }
        });
    }
}