package com.disruption.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /*Class for holding the settings fragment*/
    public static class NewsArticlesFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //Find the order by preference value
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            //Find the query preference
            Preference queryTopic = findPreference(getString(R.string.settings_query_key));
            bindPreferenceSummaryToValue(queryTopic);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            //Convert the newValue to a string
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preferenceIndex = listPreference.findIndexOfValue(stringValue);
                if (preferenceIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[preferenceIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        /*Helper method for binding the current preference value to display*/
        private void bindPreferenceSummaryToValue(Preference preference) {
            //Set the Earthquake Fragment to listen to changes
            preference.setOnPreferenceChangeListener(this);

            //Read the current value of the preference stored in the device and display it as the summary of the preference
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
