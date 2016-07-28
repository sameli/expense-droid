package com.expensedroid.expensedroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by S. Ameli on 27/07/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference reset = (Preference) findPreference("pref_key_license");

        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            public boolean onPreferenceClick(Preference pref)
            {

                String str = "\n\n \t\tExpense Droid Version "+ getString(R.string.app_version) +" (revision "+getString(R.string.app_revision)+")\n\n" +
                        "\t\tLicense:\n" +
                        "\t\tGNU General Public License 3\n" +
                        "\t\thttps://www.gnu.org/licenses/gpl.html\n"+
                        "\t\tCopyright 2016 S. Ameli";

                TextView msg = new TextView(getActivity());
                msg.setText(str);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("License");
                builder.setView(msg);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                       // resetSettings(SettingsFragment.this.getActivity());
                    }
                });
                builder.create().show();
                //resetSettings(SettingsFragment.this.getActivity());


                return true;
            }
        });
    }
}