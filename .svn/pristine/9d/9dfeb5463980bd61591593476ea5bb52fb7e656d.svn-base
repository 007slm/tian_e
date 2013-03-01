
package com.orange.browser;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import com.orange.browser.extendjs.OrangeHomeLink;
import com.orange.cygnus.reading.javascript.ReadingInterface;

/**
 * Web view factory class for creating {@link BrowserWebView}'s.
 */
public class BrowserWebViewFactory implements WebViewFactory {

    private final Context mContext;

    public BrowserWebViewFactory(Context context) {
        mContext = context;
    }

    protected WebView instantiateWebView(AttributeSet attrs, int defStyle,
            boolean privateBrowsing) {
        return new BrowserWebView(mContext, attrs, defStyle, privateBrowsing);
    }

    @Override
    public WebView createSubWebView(boolean privateBrowsing) {
        return createWebView(privateBrowsing);
    }

    @Override
    public WebView createWebView(boolean privateBrowsing) {
        WebView w = instantiateWebView(null, android.R.attr.webViewStyle, privateBrowsing);
        initWebViewSettings(w);
        return w;
    }

    @TargetApi(11)
    protected void initWebViewSettings(WebView w) {
//        if (BrowserGlobals.isPlatformHoneycombAndAbove()) {
//            if (BrowserGlobals.isFirstTime()) {
//                w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                BrowserGlobals.removeFirstTime();
//            } else if (w.getLayerType() == View.LAYER_TYPE_SOFTWARE)
//                w.setLayerType(View.LAYER_TYPE_NONE, null);
//        }
        w.setScrollbarFadingEnabled(true);
        w.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        w.setMapTrackballToArrowKeys(false); // use trackball directly
        // Enable the built-in zoom
        if (BrowserGlobals.isPlatformHoneycombAndAbove()) {
            w.getSettings().setBuiltInZoomControls(true);
            w.getSettings().setDisplayZoomControls(false);
        }

        // Add this WebView to the settings observer list and update the
        // settings
        final BrowserSettings s = BrowserSettings.getInstance();
        s.startManagingSettings(w.getSettings());

        //add by gaoge for andriod 2.3 implement way
        s.addObserver(w.getSettings()).update(s, null);

        // Add begin by Orange.
        // We can expand JS here.
        // TODO: User can not disable JavaScript?!
        w.getSettings().setJavaScriptEnabled(true);

        // Add orangeHomeLink
        OrangeHomeLink homeLink = new OrangeHomeLink(mContext);
        w.addJavascriptInterface(homeLink, "orangeHomeLink");

        ReadingInterface readingInterface = new ReadingInterface(mContext);
        w.addJavascriptInterface(readingInterface, "readingInterface");
        readingInterface.bindWebView(w);

        // Disable long click for webview.
        // w.setOnLongClickListener(new View.OnLongClickListener() {
        // @Override
        // public boolean onLongClick(View v) {
        // return true;
        // }
        // });
        // w.setLongClickable(false);

        // Add end.
    }

}
