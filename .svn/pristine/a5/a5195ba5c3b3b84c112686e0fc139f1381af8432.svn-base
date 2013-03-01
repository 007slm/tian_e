package com.orange.browser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.orange.browser.UI.ComboViews;
import com.orange.browser.UI.DropdownChangeListener;

import java.util.HashMap;
import java.util.List;


/**
 * UI aspect of the controller
 */
public interface UiController {

    UI getUi();

    WebView getCurrentWebView();

    WebView getCurrentTopWebView();

    Tab getCurrentTab();

    TabControl getTabControl();

    List<Tab> getTabs();

    // Use following to load home url.
    // Do NOT use loadUrl(t, mSettings.getHomePage())!!!
    Tab openTabToHomePage(boolean useCurrentTab);

    Tab openIncognitoTab();

    Tab openTab(String url, boolean incognito, boolean setActive,
            boolean useCurrent);

    void setActiveTab(Tab tab);

    boolean switchToTab(Tab tab);

    void closeCurrentTab();

    void closeTab(Tab tab);

    void stopLoading();

    Intent createBookmarkCurrentPageIntent(boolean canBeAnEdit);

    void bookmarksOrHistoryPicker(ComboViews startView);

    void startVoiceSearch();

    boolean supportsVoiceSearch();

    void showVoiceSearchResults(String title);

    void editUrl();

    void handleNewIntent(Intent intent);

    boolean shouldShowErrorConsole();

    void hideCustomView();

    void attachSubWindow(Tab tab);

    void removeSubWindow(Tab tab);

    boolean isInCustomActionMode();

    void shareCurrentPage();

    void updateMenuState(Tab tab, Menu menu);

    void registerDropdownChangeListener(DropdownChangeListener d);

    boolean onOptionsItemSelected(MenuItem item);

    void loadUrl(Tab tab, String url);

    void setBlockEvents(boolean block);

    Activity getActivity();

    void showPageInfo();

    void exitBrowser();

    void openHistory();

    void openBookMark();

    void openBookMarkDirectly();

    void openSetting();

    void openDownLoadManager();

    void openWindowManager();

    void sharePage();

    void openFindDialog();

    void savePage();

    void setFullscreen(boolean enabled);

    boolean isFullscreen();

    public boolean isCollectorMode();

    public List<HashMap<String, String>> getUrlCollectionList();

    public int getUrlCollectionListSize();

    public void setCollectorMode(boolean isCollectorMode);

    public void setCollectorModeTimes(int count);

    public int getCollectorModeTimes();

    public void clearUrlCollectionList();

    public void cancelUrlCollectionList();

    public boolean isFindDialogShowing();

    public void resetBatchMode();

    public void cancelBatchMode();

    public Tab getPrevTab();

    public Tab getNextTab();

    public void shareUserSelect(final Bitmap screenshot, boolean longPress);

    public void setGestureEnabled(boolean enable);

    public boolean isGestureEnabled();
    
    public EffieientReadMode getEffieientReadMode();
}
