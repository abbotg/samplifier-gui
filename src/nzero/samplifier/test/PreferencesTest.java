package nzero.samplifier.test;

import java.util.prefs.Preferences;

public class PreferencesTest {
    public static void main(String[] args) {
        boolean write = false;

        if (write) {
            String myPref = "my pref";
            Preferences preferences = Preferences.userRoot().node("nzero/samplifier/testpref");
            preferences.put("testpref1", myPref);
        } else {
            Preferences preferences = Preferences.userRoot().node("nzero/samplifier/testpref");
            System.out.println(preferences.get("testpref1", "default"));
            System.out.println(preferences.absolutePath());
        }

    }
}
