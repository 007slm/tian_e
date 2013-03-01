
package com.orange.browser;
import android.os.Looper;
import android.webkit.WebView;

/**
 * Centralised point for controlling WebView timers pausing and resuming.
 *
 * All methods on this class should only be called from the UI thread.
 */
public class WebViewTimersControl {

    private static final boolean LOGD_ENABLED = com.orange.browser.Browser.LOGD_ENABLED;
    private static final String LOGTAG = "WebViewTimersControl";

    private static WebViewTimersControl sInstance;

    private boolean mBrowserActive;
    private boolean mPrerenderActive;

    /**
     * Get the static instance. Must be called from UI thread.
     */
    public static WebViewTimersControl getInstance() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("WebViewTimersControl.get() called on wrong thread");
        }
        if (sInstance == null) {
            sInstance = new WebViewTimersControl();
        }
        return sInstance;
    }

    private WebViewTimersControl() {
    }

    private void resumeTimers(WebView wv) {
        if (wv != null) {
            wv.resumeTimers();
        }
    }

    private void maybePauseTimers(WebView wv) {
        if (!mBrowserActive && !mPrerenderActive && wv != null) {
            wv.pauseTimers();
        }
    }

    public void onBrowserActivityResume(WebView wv) {
        mBrowserActive = true;
        resumeTimers(wv);
    }

    public void onBrowserActivityPause(WebView wv) {
        mBrowserActive = false;
        maybePauseTimers(wv);
    }

    public void onPrerenderStart(WebView wv) {
        mPrerenderActive = true;
        resumeTimers(wv);
    }

    public void onPrerenderDone(WebView wv) {
        mPrerenderActive = false;
        maybePauseTimers(wv);
    }

}
