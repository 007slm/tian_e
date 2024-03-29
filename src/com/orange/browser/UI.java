package com.orange.browser;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;

import java.util.List;

/**
 * UI interface definitions
 */
public interface UI {

    public static enum ComboViews {
        History,
        Bookmarks,
        Snapshots,
    }

    public void onPause();

    public void onResume();

    public void onDestroy();

    public void onConfigurationChanged(Configuration config);

    public boolean onBackKey();

    public boolean onMenuKey();

    public boolean needsRestoreAllTabs();

    public void addTab(Tab tab);

    public void removeTab(Tab tab);

    public void setActiveTab(Tab tab);

    public void updateTabs(List<Tab> tabs);

    public void detachTab(Tab tab);

    public void attachTab(Tab tab);

    public void onSetWebView(Tab tab, WebView view);

    public void createSubWindow(Tab tab, WebView subWebView);

    public void attachSubWindow(View subContainer);

    public void removeSubWindow(View subContainer);

    public void onTabDataChanged(Tab tab);

    public void onPageStopped(Tab tab);

    public void onProgressChanged(Tab tab);

    public void showActiveTabsPage();

    public void removeActiveTabsPage();

//    public void showComboView(ComboViews startingView, Bundle extra);

    public void showCustomView(View view, int requestedOrientation,
            CustomViewCallback callback);

    public void onHideCustomView();

    public boolean isCustomViewShowing();

//    public void showVoiceTitleBar(String title, List<String> results);

//    public void revertVoiceTitleBar(Tab tab);

    public boolean onPrepareOptionsMenu(Menu menu);

    public void updateMenuState(Tab tab, Menu menu);

    public void onOptionsMenuOpened();

    public void onExtendedMenuOpened();

    public boolean onOptionsItemSelected(MenuItem item);

    public void onOptionsMenuClosed(boolean inLoad);

    public void onExtendedMenuClosed(boolean inLoad);

    public void onContextMenuCreated(Menu menu);

    public void onContextMenuClosed(Menu menu, boolean inLoad);

//    public void onActionModeFinished(boolean inLoad);

    public void setShouldShowErrorConsole(Tab tab, boolean show);

    // returns if the web page is clear of any overlays (not including sub windows)
    public boolean isWebShowing();

    public void showWeb(boolean animate);

    Bitmap getDefaultVideoPoster();

    View getVideoLoadingProgressView();

    void bookmarkedStatusHasChanged(Tab tab);

    void showMaxTabsWarning();

    void onSearchRequested();
    void editUrl(boolean clearInput);

    boolean isEditingUrl();

    boolean dispatchKey(int code, KeyEvent event);

    public static interface DropdownChangeListener {
        void onNewDropdownDimensions(int height);
    }
//    void registerDropdownChangeListener(DropdownChangeListener d);

//    void showAutoLogin(Tab tab);
//
//    void hideAutoLogin(Tab tab);

    void setFullscreen(boolean enabled);
    boolean isFullscreen();
    void showTitleBar();

    public void suggestHideTitleBar();

//    void setUseQuickControls(boolean enabled);

    public boolean shouldCaptureThumbnails();
    public void openWindowManager();
    public boolean isMainViewShowing();
    public void backToMainPage();

    public void setSelectedTextView(int selectedCount);

    public void setConfirmText(Boolean isChangetoConfirm);

    public void updateBookmarkIcons();

    public void resetBatchModeUi();

    public boolean isHomePageShowing();

    public boolean onkeyDown();

    public boolean isBatchNotificationBarShow();

    public void hideBatchNotificationBar();

    public void showBatchNotificationBar();

    public void updateBatchNotificationBar(String toastStr, boolean isFirst, boolean isDuplicate);


    public BottomBarMenu getMenu ( );

    public void setGestureEnabled(boolean enable);

    public boolean isGestureEnabled();
}
