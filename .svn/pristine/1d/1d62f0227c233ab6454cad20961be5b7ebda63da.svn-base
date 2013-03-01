
package com.orange.browser;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

/*
 * The global variables for browser application.
 */
public class BrowserGlobals {

    private static BrowserGlobals sInstance;
    private static BrowserActivity mActivity;
    private static boolean sFirstTime = true;
    private static boolean sRestartForJianBaoTip = true;
    private static boolean sRestartForTryTip = true;
    private static boolean sRestartForEfficientReadTip = true;
    private static boolean sHelpActivityShowedThisTime = false;
    private static boolean mEfficientTipShowedThisTime = false;
    public static int sDeviceWidth;
    public static int sDeviceHeight;
    public static float sDensity;
    public static int sDensityDpi;

    // Disable long press. We use long press for square gesture.
    public static boolean sEnableLongPress = false;

    public BrowserGlobals(BrowserActivity browserActivity) {
        // TODO Auto-generated constructor stub
        setBrowserActivity(browserActivity);
    }

    public static void initialize(BrowserActivity browserActivity) {
        if (sInstance == null) {
            // Here, it is process-once.
            sInstance = new BrowserGlobals(browserActivity);
            setupDevices();
        }

        setLaunchCount(browserActivity, getLaunchCount(browserActivity) + 1);
        setRestartForJianBaoTip(true);
        setRestartForTryTip(true);
        setRestartForEfficientReadTip(true);
        setHelpActivityShowedThisTime(false);
        setEfficientReadTipShowedThisTime(false);

    }

    public static void setBrowserActivity(BrowserActivity browserActivity) {
        mActivity = browserActivity;
    }

    private static void setupDevices() {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        sDeviceWidth = metric.widthPixels;
        sDeviceHeight = metric.heightPixels;
        sDensity = metric.density;
        sDensityDpi = metric.densityDpi;
    }

    public static int getDeviceWidth() {
        return sDeviceWidth;
    }

    public static void setLaunchCount(BrowserActivity browserActivity, int count) {
        SharedPreferenceUtils.setAppLaunchCount(browserActivity, count);
    }

    public static int getLaunchCount(Activity browserActivity) {
        return SharedPreferenceUtils.getAppLaunchCount(browserActivity);
    }

    public static void setRestartForEfficientReadTip(boolean restartForEfficientReadTip) {
        sRestartForEfficientReadTip = restartForEfficientReadTip;
    }

    public static boolean getRestartForEfficientReadTip() {
        return sRestartForEfficientReadTip;
    }

    public static void setHelpActivityShowedThisTime(boolean helpActivityShowedThisTime) {
        sHelpActivityShowedThisTime = helpActivityShowedThisTime;
    }

    public static void setEfficientReadTipShowedThisTime(boolean efficientTipShowedThisTime) {
        mEfficientTipShowedThisTime = efficientTipShowedThisTime;
    }

    public static boolean getEfficientReadTipShowedThisTime() {
        return mEfficientTipShowedThisTime;
    }

    public static boolean getHelpActivityShowedThisTime() {
        return sHelpActivityShowedThisTime;
    }

    public static void setRestartForJianBaoTip(boolean restartForJianBaoTip) {
        sRestartForJianBaoTip = restartForJianBaoTip;
    }

    public static boolean getRestartForJianBaoTip() {
        return sRestartForJianBaoTip;
    }

    public static void setRestartForTryTip(boolean restartForTryTip) {
        sRestartForTryTip = restartForTryTip;
    }

    public static boolean getRestartForTryTip() {
        return sRestartForTryTip;
    }

    public static BrowserGlobals getInstance() {
        return sInstance;
    }

    public boolean isHomePage() {
        Controller controller = mActivity.getController();
        if (controller != null) {
            Tab t = controller.getTabControl().getCurrentTab();
            if (t != null && !BrowserSettings.getInstance().getHomePage().equals(t.getUrl())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPlatformHoneycombAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isPlatformICSAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isFirstTime() {
        return sFirstTime;
    }

    public static void removeFirstTime() {
        sFirstTime = false;
    }

    public static boolean longPressEnabled() {
        return sEnableLongPress;
    }

    public static void enableLongPress(boolean enable) {
        sEnableLongPress = enable;
    }

    public static boolean isChinaVersion(Context context) {
        return "cn".equals(context.getString(R.string.version_country));
    }
}
