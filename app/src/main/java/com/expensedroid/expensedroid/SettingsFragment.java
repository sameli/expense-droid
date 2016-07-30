package com.expensedroid.expensedroid;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by S. Ameli on 27/07/16.
 *
 * This class defines fragment for the settings panel
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        addLicenseListener();
        addResetDatabaseListener();
        addResetSettingsListener();
        addGenerateRandomDataListener();

    }

    private void addLicenseListener(){
        Preference preference = (Preference) findPreference(Tools.PREFERENCE_LICENSE);

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

        Preference preference = (Preference) findPreference(Tools.PREFERENCE_RESET_DATABASE);

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
                        Toast.makeText(getActivity(), "Database has been reset", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();


                return true;
            }
        });
    }

    private void addResetSettingsListener(){

        Preference preference = (Preference) findPreference(Tools.PREFERENCE_RESET_SETTINGS);

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Reset Settings");
                builder.setMessage("Are you sure you want to reset all the settings within this app?");
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        int selected_acct_id = SettingsIO.readData(getActivity(), -1, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);

                        DatabaseHelper mydb = new DatabaseHelper(getActivity());
                        SharedPreferences settings = getActivity().getSharedPreferences(Tools.SETTINGS_TITLE, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(getActivity(), "All settings has been reset", Toast.LENGTH_SHORT).show();

                        if(selected_acct_id != -1) {
                            SettingsIO.saveData(getActivity(), selected_acct_id, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);
                        }

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();


                return true;
            }
        });
    }

    private void addGenerateRandomDataListener(){

        Preference preference = (Preference) findPreference(Tools.PREFERENCE_GENERATE_RANDOM_DATA);

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Generate Random Data");
                builder.setMessage("Are you sure you want to generate random data? All previous data will be lost.");
                builder.setPositiveButton("Generate", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Tools.generateRandomData(getActivity());

                        int selected_acct_id = SettingsIO.readData(getActivity(), -1, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);

                        // set the account to load to 1
                        if(selected_acct_id != -1) {
                            SettingsIO.saveData(getActivity(), 1, Tools.PREFERENCE_SELECTED_ACCOUNT_ID);
                        }
                        Toast.makeText(getActivity(), "Database has been populated with random data", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();


                return true;
            }
        });
    }

}