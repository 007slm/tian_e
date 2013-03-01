package com.orange.browser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebStorage;

import java.util.Map;
import java.util.Set;

public class BrowserPreferencesPage extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private String LOGTAG = "BrowserPreferencesPage";
    /* package */ static final String CURRENT_PAGE = "currentPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.browser_preferences);
//        getListView().setCacheColorHint(Color.TRANSPARENT);
//        getListView().setDivider(null);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.reading_item_slide_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setDelay(0.3f);
        getListView().setLayoutAnimation(controller);
        Preference e = findPreference(BrowserSettings.PREF_EXTRAS_RESET_DEFAULTS);
        e.setOnPreferenceChangeListener(this);

        String channel = null;
        try {
            channel = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA).metaData
                    .getString("TD_CHANNEL_ID");
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        e = findPreference(PreferenceKeys.PREF_TEXT_SIZE);
        e.setOnPreferenceChangeListener(this);
        e.setSummary(getVisualTextSizeName(
                getPreferenceScreen().getSharedPreferences()
                .getString(PreferenceKeys.PREF_TEXT_SIZE, null)) );
        //comment by gaoge for remove this setting
//        e = findPreference(BrowserSettings.PREF_DEFAULT_ZOOM);
//        e.setOnPreferenceChangeListener(this);
//        e.setSummary(getVisualDefaultZoomName(
//                getPreferenceScreen().getSharedPreferences()
//                .getString(BrowserSettings.PREF_DEFAULT_ZOOM, null)) );

        e = findPreference(PreferenceKeys.PREF_DEFAULT_TEXT_ENCODING);
        e.setSummary(getPreferenceScreen().getSharedPreferences().getString(PreferenceKeys.PREF_DEFAULT_TEXT_ENCODING, null));
        e.setOnPreferenceChangeListener(this);

        e = findPreference(BrowserSettings.PREF_CLEAR_HISTORY);
        e.setOnPreferenceChangeListener(this);

        //add by gaoge to disable the javascript preference
        e = findPreference(PreferenceKeys.PREF_ENABLE_JAVASCRIPT);
        e.setEnabled(false);

        if (BrowserSettings.getInstance().isDebugEnabled()) {
            addPreferencesFromResource(R.xml.debug_preferences);
            BrowserHomepagePreference homePagePref = (BrowserHomepagePreference) findPreference(PreferenceKeys.PREF_HOMEPAGE);
            homePagePref.setCurrentPage(getIntent().getStringExtra(CURRENT_PAGE));
        }

        PreferenceScreen websiteSettings = (PreferenceScreen)
            findPreference(PreferenceKeys.PREF_WEBSITE_SETTINGS);
        Intent intent = new Intent(this, WebsiteSettingsActivity.class);
        websiteSettings.setIntent(intent);
    }

    /*
     * We need to set the PreferenceScreen state in onResume(), as the number of
     * origins with active features (WebStorage, Geolocation etc) could have
     * changed after calling the WebsiteSettingsActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        final PreferenceScreen websiteSettings = (PreferenceScreen)
            findPreference(PreferenceKeys.PREF_WEBSITE_SETTINGS);
        websiteSettings.setEnabled(false);
        WebStorage.getInstance().getOrigins(new ValueCallback<Map>() {
            @Override
            public void onReceiveValue(Map webStorageOrigins) {
                if ((webStorageOrigins != null) && !webStorageOrigins.isEmpty()) {
                    websiteSettings.setEnabled(true);
                }
            }
        });
        GeolocationPermissions.getInstance().getOrigins(new ValueCallback<Set<String> >() {
            @Override
            public void onReceiveValue(Set<String> geolocationOrigins) {
                if ((geolocationOrigins != null) && !geolocationOrigins.isEmpty()) {
                    websiteSettings.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        LogHelper.d(LOGTAG, "onPause()");
//         sync the shared preferences back to BrowserSettings
        BrowserSettings.getInstance().syncSharedPreferences(
                getApplicationContext(),
                getPreferenceScreen().getSharedPreferences());
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        if (pref.getKey().equals(BrowserSettings.PREF_EXTRAS_RESET_DEFAULTS)) {
            Boolean value = (Boolean) objValue;
            if (value.booleanValue() == true) {
                finish();
            }
        }else if (pref.getKey().equals(BrowserSettings.PREF_HOMEPAGE)) {
            String value = (String) objValue;
            boolean needUpdate = value.indexOf(' ') != -1;
            if (needUpdate) {
                value = value.trim().replace(" ", "%20");
            }
            if (value.length() != 0 && Uri.parse(value).getScheme() == null) {
                value = "http://" + value;
                needUpdate = true;
            }
            // Set the summary value.
            pref.setSummary(value);
            if (needUpdate) {
                // Update through the EditText control as it has a cached copy
                // of the string and it will handle persisting the value
                ((EditTextPreference) pref).setText(value);

                // as we update the value above, we need to return false
                // here so that setText() is not called by EditTextPref
                // with the old value.
                return false;
            } else {
                return true;
            }
        } else if (pref.getKey().equals(PreferenceKeys.PREF_TEXT_SIZE)) {
            pref.setSummary(getVisualTextSizeName((String) objValue));
            return true;
        } else if (pref.getKey().equals(PreferenceKeys.PREF_DEFAULT_ZOOM)) {
            pref.setSummary(getVisualDefaultZoomName((String) objValue));
            return true;
        } else if (pref.getKey().equals(
                PreferenceKeys.PREF_DEFAULT_TEXT_ENCODING)) {
            pref.setSummary((String) objValue);
            return true;
        } else if (pref.getKey().equals(BrowserSettings.PREF_CLEAR_HISTORY)
                && ((Boolean) objValue).booleanValue() == true) {
            // Need to tell the browser to remove the parent/child relationship
            // between tabs
            setResult(RESULT_OK, (new Intent()).putExtra(Intent.EXTRA_TEXT,
                    pref.getKey()));
            return true;
        }

        return false;
    }

    private CharSequence getVisualTextSizeName(String enumName) {
        CharSequence[] visualNames = getResources().getTextArray(
                R.array.pref_text_size_choices);
        CharSequence[] enumNames = getResources().getTextArray(
                R.array.pref_text_size_values);

        // Sanity check
        if (visualNames.length != enumNames.length) {
            return "";
        }

        for (int i = 0; i < enumNames.length; i++) {
            if (enumNames[i].equals(enumName)) {
                return visualNames[i];
            }
        }

        return "";
    }

    private CharSequence getVisualDefaultZoomName(String enumName) {
        CharSequence[] visualNames = getResources().getTextArray(
                R.array.pref_default_zoom_choices);
        CharSequence[] enumNames = getResources().getTextArray(
                R.array.pref_default_zoom_values);

        // Sanity check
        if (visualNames.length != enumNames.length) {
            return "";
        }

        for (int i = 0; i < enumNames.length; i++) {
            if (enumNames[i].equals(enumName)) {
                return visualNames[i];
            }
        }

        return "";
    }
}
