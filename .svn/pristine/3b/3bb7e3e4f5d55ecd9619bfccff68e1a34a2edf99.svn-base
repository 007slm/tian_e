
package com.orange.browser;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerResultsIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orange.browser.UrlInputView.UrlInputListener;

import java.util.List;

public class NavigationBarBase extends LinearLayout implements OnClickListener, UrlInputListener,
        OnFocusChangeListener, TextWatcher {
    protected BaseUi mBaseUi;
    protected TitleBar mTitleBar;
    protected UiController mUiController;
    protected UrlInputView mUrlInput;
    protected LinearLayout mUrlContainer;
    protected ImageButton mVoiceButton;
    protected ImageView mVoiceDivide;
    protected boolean mInVoiceMode = false;

    public NavigationBarBase(Context context) {
        super(context);
    }

    public NavigationBarBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUrlInput = (UrlInputView) findViewById(R.id.url);
        mUrlContainer = (LinearLayout)findViewById(R.id.url_container);
        mUrlInput.setUrlInputListener(this);
        mUrlInput.setOnFocusChangeListener(this);
        mUrlInput.setSelectAllOnFocus(true);
        mUrlInput.addTextChangedListener(this);
        mVoiceButton = (ImageButton)findViewById(R.id.voice);
        mVoiceDivide = (ImageView)findViewById(R.id.voice_divide_line);

    }
    public LinearLayout getUrlContainer(){
        return mUrlContainer;
    }

    public void setTitleBar(TitleBar titleBar) {
        mTitleBar = titleBar;
        mBaseUi = mTitleBar.getUi();
        mUiController = mTitleBar.getUiController();
        mUrlInput.setController(mUiController);
    }
    public void hideReadingBar(){

    }

    public  void updateBookmarkIcons(){};
    public void showOrHideBookmarkIconsAccoringToProgress(){};

//    public void setLock(Drawable d) {
//        if (d != null) {
//            mUrlInput.setFavicon(d);
//        }
//    }

//    public void setFavicon(Bitmap icon) {
//        mUrlInput.setFavicon(mBaseUi.getFaviconDrawable(icon));
//    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        // if losing focus and not in touch mode, leave as is
        if (hasFocus || view.isInTouchMode() || mUrlInput.needsUpdate()) {
            //if bottom bar menu opened,close it
            if(mBaseUi.mMenu.isShown()){
                mBaseUi.mMenu.hide();
            }
            setFocusState(hasFocus);
        }
        if (hasFocus) {
            mBaseUi.showTitleBar();
            mUrlInput.forceIme();
            if (mInVoiceMode) {
                mUrlInput.forceFilter();
            }
        } else if (!mUrlInput.needsUpdate()) {
            mUrlInput.dismissDropDown();
            mUrlInput.hideIME();
            if (mUrlInput.getText().length() == 0) {
                Tab currentTab = mUiController.getTabControl().getCurrentTab();
                if (currentTab != null) {
                    setDisplayTitle(currentTab.getUrl());
                }
            }
            mBaseUi.suggestHideTitleBar();
        }
        mUrlInput.clearNeedsUpdate();
    }

    protected void setFocusState(boolean focus) {
    }

    protected void setSearchMode(boolean voiceSearchEnabled) {}

    public boolean isEditingUrl() {
        return mUrlInput.hasFocus();
    }

    void stopEditingUrl() {
        mUrlInput.clearFocus();
    }

    void setDisplayTitle(String title) {
        if (!isEditingUrl()) {
//            mUrlInput.setText(title, false);
            mUrlInput.setText(title);
        }
    }

    // voicesearch

    public void setInVoiceMode(boolean voicemode, List<String> voiceResults) {
        mInVoiceMode = voicemode;
        mUrlInput.setVoiceResults(voiceResults);
    }

    void setIncognitoMode(boolean incognito) {
        mUrlInput.setIncognitoMode(incognito);
    }

    void clearCompletions() {
        mUrlInput.dismissDropDown();
    }

 // UrlInputListener implementation

    /**
     * callback from suggestion dropdown
     * user selected a suggestion
     */
    @Override
    public void onAction(String text, String extra, String source) {
        WebView currentTopWebView = mUiController.getCurrentTopWebView();
        if (currentTopWebView != null) {
            currentTopWebView.requestFocus();
        }
        if (UrlInputView.TYPED.equals(source)) {
            String url = UrlUtils.smartUrlFilter(text, false);
            Tab t = mBaseUi.getActiveTab();
            // Only shortcut javascript URIs for now, as there is special
            // logic in UrlHandler for other schemas
            if (url != null && t != null && url.startsWith("javascript:")) {
                mUiController.loadUrl(t, url);
                setDisplayTitle(text);
                return;
            }
        }
        Intent i = new Intent();
        String action = null;
        if (UrlInputView.VOICE.equals(source)) {
            action = RecognizerResultsIntent.ACTION_VOICE_SEARCH_RESULTS;
            source = null;
        } else {
            action = Intent.ACTION_SEARCH;
        }
        i.setAction(action);
        i.putExtra(SearchManager.QUERY, text);
        if (extra != null) {
            i.putExtra(SearchManager.EXTRA_DATA_KEY, extra);
        }
        // if (source != null) {
        // Bundle appData = new Bundle();
        // appData.putString(com.android.common.Search.SOURCE, source);
        // i.putExtra(SearchManager.APP_DATA, appData);
        // }
        mUiController.handleNewIntent(i);
        setDisplayTitle(text);
    }

    @Override
    public void onDismiss() {
        final Tab currentTab = mBaseUi.getActiveTab();
//        mBaseUi.hideTitleBar();
        mBaseUi.suggestHideTitleBar();
        post(new Runnable() {
            @Override
            public void run() {
                clearFocus();
                if ((currentTab != null) && !mInVoiceMode) {
                    setDisplayTitle(currentTab.getUrl());
                }
            }
        });
    }

    /**
     * callback from the suggestion dropdown
     * copy text to input field and stay in edit mode
     */
    @Override
    public void onCopySuggestion(String text) {
//        mUrlInput.setText(text, true);
        mUrlInput.setText(text);
        if (text != null) {
            mUrlInput.setSelection(text.length());
        }
    }


    @Override
    public void onFaviconClicked() {
        if (!mBaseUi.isHomePageShowing()) {
            mUiController.showPageInfo();
        }
    }

    @Override
    public void onComboButtonClicked() {
        if (isEditingUrl()) {
            mUrlInput.setText("");
        } else if (mTitleBar.isInLoad()) {
            if (!mBaseUi.isHomePageShowing()) {
                mUiController.stopLoading();
            }
        } else {
            WebView web = mBaseUi.getWebView();
            if (web != null) {
                stopEditingUrl();
                if (BrowserSettings.getErrorPage().equals(web.getUrl())) {
                    web.loadUrl(BrowserSettings.getInstance().getHomePage());
                } else {
                    web.reload();
                }
            }
        }
    }

    public void setCurrentUrlIsBookmark(boolean isBookmark) {
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // catch back key in order to do slightly more cleanup than usual
            mUrlInput.clearFocus();
            return true;
        }
        return super.dispatchKeyEventPreIme(evt);
    }

    /**
     * called from the Ui when the user wants to edit
     * @param clearInput clear the input field
     */
    void startEditingUrl(boolean clearInput) {
        // editing takes preference of progress
        setVisibility(View.VISIBLE);
        mVoiceButton.setVisibility(VISIBLE);
        mVoiceDivide.setVisibility(VISIBLE);
        if (mTitleBar.useQuickControls()) {
            mTitleBar.getProgressView().setVisibility(View.GONE);
        }
        if (!mUrlInput.hasFocus()) {
            mUrlInput.requestFocus();
        }
        if (clearInput) {
            mUrlInput.setText("");
        } else if (mInVoiceMode) {
            mUrlInput.showDropDown();
        }
    }

    public void onProgressStarted() {
    }

    public void onProgressStopped() {
    }

    public boolean isMenuShowing() {
        return false;
    }

    public void onTabDataChanged(Tab tab) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mUrlInput.hasFocus()) {
            // clear voice mode when user types
            setInVoiceMode(false, null);
            mVoiceButton.setVisibility(GONE);
            mVoiceDivide.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    public void onPause(){}

    public void startEffieientRead(){

    }
    public BaseUi getUI(){
        return mBaseUi;
    }

    public void onPageFinished() {

    }

}
