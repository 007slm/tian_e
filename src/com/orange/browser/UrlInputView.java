package com.orange.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.orange.browser.SuggestionsAdapter.CompletionListener;
import com.orange.browser.SuggestionsAdapter.SuggestItem;
import com.orange.browser.reflect.InvokeInputMethodManagerMethod;
import com.orange.browser.search.SearchEngine;
import com.orange.browser.search.SearchEngineInfo;
import com.orange.browser.search.SearchEngines;

import java.util.List;

/**
 * url/search input view
 * handling suggestions
 */
public class UrlInputView extends AutoCompleteTextView
        implements OnEditorActionListener,
        CompletionListener, OnItemClickListener, TextWatcher, View.OnLongClickListener {

    static final String TYPED = "browser-type";
    static final String SUGGESTED = "browser-suggest";
    static final String VOICE = "voice-search";

    static final int POST_DELAY = 100;

    static interface StateListener {
        static final int STATE_NORMAL = 0;
        static final int STATE_HIGHLIGHTED = 1;
        static final int STATE_EDITED = 2;

        public void onStateChanged(int state);
    }

    private UrlInputListener   mListener;
    private InputMethodManager mInputManager;
    private SuggestionsAdapter mAdapter;
    private View mContainer;
    private boolean mLandscape;
    private boolean mIncognitoMode;
    private boolean mNeedsUpdate;
    
    private Context mContext;

    private int mState;
    private StateListener mStateListener;
    private Rect mPopupPadding;
    
//    private Drawable mFaviconDrawable;
    private Drawable mComboButtonDrawable;

    public UrlInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PopupWindow,
                R.attr.autoCompleteTextViewStyle, 0);

        Drawable popupbg = a.getDrawable(R.styleable.PopupWindow_popupBackground);
        a.recycle();
        mPopupPadding = new Rect();

        //popupbg.getPadding(mPopupPadding);
        mPopupPadding.set(0, 0, 0, 0);
        init(context);
    }

    public UrlInputView(Context context, AttributeSet attrs) {
    	this(context, attrs, R.attr.autoCompleteTextViewStyle);
    }

    public UrlInputView(Context context) {
        this(context, null);
    }

    private void init(Context ctx) {
        mInputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        setOnEditorActionListener(this);
        mAdapter = new SuggestionsAdapter(ctx, this);
        setAdapter(mAdapter);
        setSelectAllOnFocus(true);
        onConfigurationChanged(ctx.getResources().getConfiguration());
        setThreshold(1);
        setOnItemClickListener(this);
        mNeedsUpdate = false;
        addTextChangedListener(this);

        mState = StateListener.STATE_NORMAL;
        setOnLongClickListener(this);
    }

    protected void onFocusChanged(boolean focused, int direction, Rect prevRect) {
        super.onFocusChanged(focused, direction, prevRect);
        int state = -1;
        if (focused) {
            if (hasSelection()) {
                state = StateListener.STATE_HIGHLIGHTED;
            } else {
                state = StateListener.STATE_EDITED;
            }
        } else {
            // reset the selection state
            state = StateListener.STATE_NORMAL;
        }
        final int s = state;
        post(new Runnable() {
            public void run() {
                changeState(s);
                if(StateListener.STATE_NORMAL == s){
                    NavigationBarBase nbb = (NavigationBarBase)mStateListener;
                    nbb.getUI().invokeShowEfficientReadTipOrNot();
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        boolean hasSelection = hasSelection();
        if (MotionEvent.ACTION_DOWN == evt.getActionMasked()) {
            int actionX = (int) evt.getX();
            int actionY = (int) evt.getY();
            int h = getHeight();
            // TODO: width of touch area need to be confirmed
            Rect touchBounds = new Rect(0, 0, h, h);
//            if (touchBounds.contains(actionX, actionY)) {
//                mListener.onFaviconClicked();
//                return true;
//            }
            if (mComboButtonDrawable != null) {
                int w = getWidth();
                // TODO: width of touch area need to be confirmed
                touchBounds = new Rect(w - 40, 0, w, h);
                if (touchBounds.contains(actionX, actionY)) {
                    mListener.onComboButtonClicked();
                    return true;
                }
            }
            if (hasSelection) {
                postDelayed(new Runnable() {
                    public void run() {
                        changeState(StateListener.STATE_EDITED);
                    }
                }, POST_DELAY);
            }
        }
        return super.onTouchEvent(evt);
    }

    /**
     * check if focus change requires a title bar update
     */
    boolean needsUpdate() {
        return mNeedsUpdate;
    }

    /**
     * clear the focus change needs title bar update flag
     */
    void clearNeedsUpdate() {
        mNeedsUpdate = false;
    }

    void setController(UiController controller) {
      //TODO:from 3.0
//        UrlSelectionActionMode urlSelectionMode
//                = new UrlSelectionActionMode(controller);
//        
//        setCustomSelectionActionModeCallback(urlSelectionMode);
    }

    void setContainer(View container) {
        mContainer = container;
    }

    public void setUrlInputListener(UrlInputListener listener) {
        mListener = listener;
    }

    public void setStateListener(StateListener listener) {
        mStateListener = listener;
        // update listener
        changeState(mState);
    }

    private void changeState(int newState) {
        mState = newState;
        if (mStateListener != null) {
            mStateListener.onStateChanged(mState);
        }
    }

    int getState() {
        return mState;
    }

    void setVoiceResults(List<String> voiceResults) {
        mAdapter.setVoiceResults(voiceResults);
    }

    @Override
    protected void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        mLandscape = (config.orientation &
                Configuration.ORIENTATION_LANDSCAPE) != 0;
        mAdapter.setLandscapeMode(mLandscape);
        if (isPopupShowing() && (getVisibility() == View.VISIBLE)) {
            setupDropDown();
            performFiltering(getText(), 0);
        }
    }

    @Override
    public void showDropDown() {
        setupDropDown();
        super.showDropDown();
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        mAdapter.clearCache();
    }

    private void setupDropDown() {
        int width = mContainer != null ? mContainer.getWidth() : getWidth();
        width += mPopupPadding.left + mPopupPadding.right;
        if (width != getDropDownWidth()) {
            setDropDownWidth(width);
        }
        int left = getLeft();
        left += mPopupPadding.left;
        if (left != -getDropDownHorizontalOffset()) {
            setDropDownHorizontalOffset(-left);
        }
        if(null != mListener){
            NavigationBarBase navBase = (NavigationBarBase)mListener;
            int yOffset = navBase.getUrlContainer().getHeight() - getBottom();
            setDropDownVerticalOffset(yOffset);
            Log.d("auto", "getDropDownVerticalOffset(): " + getDropDownVerticalOffset()
                    + ",this.getBottom(): " + this.getBottom()
                    + ",getUrlContainer.getHeight(): " + navBase.getUrlContainer().getHeight());
        }
       
        
        Log.d("auto", "getDropDownVerticalOffset(): " + getDropDownVerticalOffset()
                + ",this.getBottom(): " + this.getBottom());
                
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        finishInput(getText().toString(), null, TYPED);
        return true;
    }

    void forceFilter() {
        showDropDown();
    }

    void forceIme() {
        //TODO:
        //mInputManager.focusIn(this);
        InvokeInputMethodManagerMethod.focusIn(mInputManager, this);
        mInputManager.showSoftInput(this, 0);
    }

    void hideIME() {
        mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private void finishInput(String url, String extra, String source) {
        mNeedsUpdate = true;
        dismissDropDown();
        mInputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        if (TextUtils.isEmpty(url)) {
            mListener.onDismiss();
        } else {
            if (mIncognitoMode && isSearch(url)) {
                // To prevent logging, intercept this request
                // TODO: This is a quick hack, refactor this
                SearchEngine searchEngine = BrowserSettings.getInstance()
                        .getSearchEngine();
                if (searchEngine == null) return;
                SearchEngineInfo engineInfo = SearchEngines
                        .getSearchEngineInfo(mContext, searchEngine.getName());
                if (engineInfo == null) return;
                url = engineInfo.getSearchUriForQuery(url);
                // mLister.onAction can take it from here without logging
            }
            mListener.onAction(url, extra, source);
        }
    }

    boolean isSearch(String inUrl) {
        String url = UrlUtils.fixUrl(inUrl).trim();
        if (TextUtils.isEmpty(url)) return false;

        if (Patterns.WEB_URL.matcher(url).matches()
                || UrlUtils.ACCEPTED_URI_SCHEMA.matcher(url).matches()) {
            return false;
        }
        return true;
    }

    // Completion Listener

    @Override
    public void onSearch(String search) {
        mListener.onCopySuggestion(search);
    }

    @Override
    public void onSelect(String url, int type, String extra) {
        finishInput(url, extra, (type == SuggestionsAdapter.TYPE_VOICE_SEARCH)
                ? VOICE : SUGGESTED);
    }

    @Override
    public void onItemClick(
            AdapterView<?> parent, View view, int position, long id) {
        SuggestItem item = mAdapter.getItem(position);
        onSelect(SuggestionsAdapter.getSuggestionUrl(item), item.type, item.extra);
    }

    interface UrlInputListener {

        public void onDismiss();

        public void onAction(String text, String extra, String source);

        public void onCopySuggestion(String text);

        public void onFaviconClicked();
        
        public void onComboButtonClicked();
    }

    public void setComboButton(Drawable d) {
        if (mComboButtonDrawable != d) {
            mComboButtonDrawable = d;
            setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
        }
    }

//    public void setFavicon(Drawable d) {
//        if (BrowserGlobals.getInstance().isHomePage()) {
//            mFaviconDrawable = null;
//            return;
//        }
//
//        if (mFaviconDrawable != d) {
//            mFaviconDrawable = d;
//            // TODO: this bound need to be confirmed
//            d.setBounds(0, 0, 30, 30);
//            setCompoundDrawables(d, null, mComboButtonDrawable, null);
//        }
//    }
//
//    public void showFavicon() {
//        setCompoundDrawables(mFaviconDrawable, null, mComboButtonDrawable, null);
//    }

//    public void hideFavicon() {
//        setCompoundDrawables(null, null, mComboButtonDrawable, null);
//    }

    public void setIncognitoMode(boolean incognito) {
        mIncognitoMode = incognito;
        mAdapter.setIncognitoMode(mIncognitoMode);
    }

    
    //TODO:
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent evt) {
//        if (keyCode == KeyEvent.KEYCODE_ESCAPE && !isInTouchMode()) {
//            finishInput(null, null, null);
//            return true;
//        }
        return super.onKeyDown(keyCode, evt);
    }

    public SuggestionsAdapter getAdapter() {
        return mAdapter;
    }

    /*
     * no-op to prevent scrolling of webview when embedded titlebar
     * gets edited
     */
    @Override
    public boolean requestRectangleOnScreen(Rect rect, boolean immediate) {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (StateListener.STATE_HIGHLIGHTED == mState) {
            changeState(StateListener.STATE_EDITED);
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public boolean onLongClick(View v) {
        if (!BrowserGlobals.longPressEnabled()) {
            return true;
        }
        return false;
    }
}
