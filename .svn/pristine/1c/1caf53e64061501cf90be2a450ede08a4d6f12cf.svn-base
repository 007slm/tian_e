package com.orange.browser;


import android.content.Context;
import android.util.AttributeSet;


class BrowserYesNoPreference extends YesNoPreference {

    // This is the constructor called by the inflater
    public BrowserYesNoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            setEnabled(false);

            Context context = getContext();
            if (BrowserSettings.PREF_CLEAR_CACHE.equals(getKey())) {
                BrowserSettings.getInstance().clearCache(context);
                BrowserSettings.getInstance().clearDatabases(context);
            } else if (BrowserSettings.PREF_CLEAR_COOKIES.equals(getKey())) {
                BrowserSettings.getInstance().clearCookies(context);
            } else if (BrowserSettings.PREF_CLEAR_HISTORY.equals(getKey())) {
                BrowserSettings.getInstance().clearHistory(context);
            } else if (BrowserSettings.PREF_CLEAR_FORM_DATA.equals(getKey())) {
                BrowserSettings.getInstance().clearFormData(context);
            } else if (BrowserSettings.PREF_CLEAR_PASSWORDS.equals(getKey())) {
                BrowserSettings.getInstance().clearPasswords(context);
            } else if (BrowserSettings.PREF_EXTRAS_RESET_DEFAULTS.equals(
                    getKey())) {
                BrowserSettings.getInstance().resetDefaultPreferences(context);
                setEnabled(true);
            } else if (BrowserSettings.PREF_CLEAR_GEOLOCATION_ACCESS.equals(
                    getKey())) {
                BrowserSettings.getInstance().clearLocationAccess(context);
            }
        }
    }
}
