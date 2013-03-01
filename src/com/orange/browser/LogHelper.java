
package com.orange.browser;

import android.util.Log;

public class LogHelper {

    private static final boolean LOGD_ENABLED = Browser.LOGD_ENABLED;
    public static final String TAG_REFLACTOR = "reflactor";
    public static final String TAG_STARBTN = "starBtn";
    public static final String TAG_STARBTN_SPPED = "starBtn_speed";
    public static final String TAG_SPEEDDIAL = "speedDial";
    public static final String TAG_PROGRESS = "progress";
    public static final String TAG_TITLEBAR = "titleBar";
    public static final String TAG_BOTTOMBAR = "bottomBar";
    public static final String TAG_HELP = "help";
    public static final String TAG_KEYCODE = "keycode";
    public static final String TAG_VOICE = "voice";
    public static final String TAG_TOUCH = "touch";
    public static final String TAG_WM = "WindowManager";

    public static void d(String TAG, String msg) {
        if (LOGD_ENABLED) {
            Log.d(TAG, msg);
        }
    }
}
