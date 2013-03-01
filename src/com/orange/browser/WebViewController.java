package com.orange.browser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.orange.browser.R;

import java.util.List;

/**
 * WebView aspect of the controller
 */
public interface WebViewController {

    Context getContext();

    Activity getActivity();

    TabControl getTabControl();

    WebViewFactory getWebViewFactory();

    void onSetWebView(Tab tab, WebView view);

    void createSubWindow(Tab tab);

    void onPageStarted(Tab tab, WebView view, Bitmap favicon);

    void onPageFinished(Tab tab);

    void onProgressChanged(Tab tab);

    void onReceivedTitle(Tab tab, final String title);

    void onFavicon(Tab tab, WebView view, Bitmap icon);

    boolean shouldOverrideUrlLoading(Tab tab, WebView view, String url);

    boolean shouldOverrideKeyEvent(KeyEvent event);

    void onUnhandledKeyEvent(KeyEvent event);

    void doUpdateVisitedHistory(Tab tab, boolean isReload);

    void getVisitedHistory(final ValueCallback<String[]> callback);

    void onReceivedHttpAuthRequest(Tab tab, WebView view, final HttpAuthHandler handler,
            final String host, final String realm);

    void onDownloadStart(Tab tab, String url, String useragent, String contentDisposition,
            String mimeType, long contentLength);

    void showCustomView(Tab tab, View view, int requestedOrientation,
            WebChromeClient.CustomViewCallback callback);

    void hideCustomView();

    Bitmap getDefaultVideoPoster();

    View getVideoLoadingProgressView();

    void showSslCertificateOnError(WebView view, SslErrorHandler handler,
            SslError error);

    void onUserCanceledSsl(Tab tab);

    void activateVoiceSearchMode(String title, List<String> results);

    void revertVoiceSearchMode(Tab tab);

    boolean shouldShowErrorConsole();

    void onUpdatedSecurityState(Tab tab);

    void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);

    void attachSubWindow(Tab tab);

    void dismissSubWindow(Tab tab);

    Tab openTab(String url, boolean incognito, boolean setActive,
            boolean useCurrent);

    Tab openTab(String url, Tab parent, boolean setActive,
            boolean useCurrent);

    boolean switchToTab(Tab tab);

    void closeTab(Tab tab);

    void setupAutoFill(Message message);

    void bookmarkedStatusHasChanged(Tab tab);

    boolean shouldCaptureThumbnails();

    public boolean isCollectorMode();
}
