package com.expensedroid.expensedroid;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by S. Ameli on 27/07/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}