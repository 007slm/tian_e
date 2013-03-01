
package com.orange.browser.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.orange.browser.AppRateActivity;

public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 7;

    // private final static float DAYS_UNTIL_PROMPT = 0.005f;
    // private final static int LAUNCHES_UNTIL_PROMPT = 2;
    private final static float APPRATER_SHOW_INTERVAL_TIME = DAYS_UNTIL_PROMPT * 24 * 60 * 60
            * 1000;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        android.util.Log.d("rate", "launch count: " + launch_count
                + ",\n System.currentTimeMillis(): "
                + System.currentTimeMillis()
                + ",\n trigger time : "
                + (APPRATER_SHOW_INTERVAL_TIME + date_firstLaunch)
                + "\n start show rater ? "
                + (System.currentTimeMillis() >= date_firstLaunch
                        + APPRATER_SHOW_INTERVAL_TIME));
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + APPRATER_SHOW_INTERVAL_TIME) {
                mContext.startActivity(new Intent(mContext, AppRateActivity.class));
            }
        }

        editor.commit();
    }

}
