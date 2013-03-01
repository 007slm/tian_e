package com.orange.browser;

import android.webkit.WebView;
import com.orange.browser.R;

/**
 * Factory for WebViews
 */
public interface WebViewFactory {

    public WebView createWebView(boolean privateBrowsing);

    public WebView createSubWebView(boolean privateBrowsing);

}
