package motion.in.education.popularmovies;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity
   implements Preference.OnPreferenceChangeListener {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_general);

      bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_method_key)));

   }

   /**
    * Attaches a listener so the summary is always updated with the preference value.
    * Also fires the listener once, to initialize the summary (so it shows up before the value
    * is changed.)
    */
   private void bindPreferenceSummaryToValue(Preference preference) {
      // Set the listener to watch for value changes.
      preference.setOnPreferenceChangeListener(this);

      // Trigger the listener immediately with the preference's
      // current value.
      onPreferenceChange(preference,
            PreferenceManager
                  .getDefaultSharedPreferences(preference.getContext())
                  .getString(preference.getKey(), ""));
   }

   /**
    * Called when a Preference has been changed by the user. This is
    * called before the state of the Preference is about to be updated and
    * before the state is persisted.
    *
    * @param preference The changed Preference.
    * @param newValue   The new value of the Preference.
    * @return True to update the state of the Preference with the new value.
    */
   @Override
   public boolean onPreferenceChange(Preference preference, Object newValue) {
      String stringValue = newValue.toString();

      if (preference instanceof ListPreference) {
         // For list preferences, look up the correct display value in
         // the preference's 'entries' list (since they have separate labels/values).
         ListPreference listPreference = (ListPreference) preference;
         int prefIndex = listPreference.findIndexOfValue(stringValue);
         if (prefIndex >= 0) {
            preference.setSummary(listPreference.getEntries()[prefIndex]);
         }
      } else {
         // For other preferences, set the summary to the value's simple string representation.
         preference.setSummary(stringValue);
      }
      return true;
   }
}
