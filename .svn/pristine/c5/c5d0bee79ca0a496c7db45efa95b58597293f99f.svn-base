package com.orange.browser;

import android.app.Activity;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    public static final String HELP_SHARED_FILE = "help";
    public static final String APP_LAUNCH_COUNT = "app_launch_count";
    public static final String SHOW_SHARE_TIP = "show_share_tip";
    public static final String ALREADY_USE_JIANBAO_GESTURE = "already_use_jianbao_gesture";
    public static final String ALREADY_USE_SHARE_GESTURE = "already_use_share_gesture";
    public static final String ALREADY_USE_ANY_GESTURE = "already_use_any_gesture";
    public static final String ALREADY_USE_EFFICIENT_READ = "already_use_efficient_read";
    public static final String TRY_TIP_SHOW_COUNT = "try_tip_show_count";
    static final String SHOW_FULLSCREEN_TIP = "show_fullscreen_tip";
    public static final String IS_EFFICIENT_READ_READY_TO_SHOW = "is_efficient_read_ready_to_show";
    public static final String IS_FULLSCREEN_READY_TO_SHOW = "is_fullscreen_ready_to_show";


    public static void setPrefsBoolean(Activity activity, String key, boolean flag) {
        getSharedPreferences(activity, HELP_SHARED_FILE).edit().putBoolean(key,
                flag).commit();
    }

    public static SharedPreferences getSharedPreferences(Activity activity, String fileName) {
        SharedPreferences prefs =
                activity.getSharedPreferences(fileName, 0);
        return prefs;
    }

    public static boolean getPrefsBooleanValue(Activity activity, String key, boolean defVal) {
        return getSharedPreferences(activity, HELP_SHARED_FILE).getBoolean(key, defVal);

    }

    public static int getPrefsIntValue(Activity activity, String key) {
        return getSharedPreferences(activity, HELP_SHARED_FILE).getInt(key, 0);

    }

    public static void setPrefsInt(Activity activity, String key, int val) {
        getSharedPreferences(activity, HELP_SHARED_FILE).edit().putInt(key,
                val).commit();
    }

    public static void setAppLaunchCount(Activity activity, int val) {
        setPrefsInt(activity, APP_LAUNCH_COUNT, val);
    }

    public static int getAppLaunchCount(Activity activity) {
        return getPrefsIntValue(activity, APP_LAUNCH_COUNT);
    }
}
