
package com.orange.browser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.orange.browser.UrlInputView.StateListener;

import dep.com.android.providers.downloads.Helpers;

public class NavigationBarPhone extends NavigationBarBase implements StateListener {
    private ImageButton mReadingButton;
    private ImageButton mStarButton;
    private ImageView mStarLine;
    private ImageView mVoiceButton;
    private ImageView mVoiceDivideLine;
    private Drawable mStopDrawable;
    private Drawable mRefreshDrawable;
    private Drawable mClearDrawable;

    public NavigationBarPhone(Context context) {
        super(context);
    }

    public NavigationBarPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // @Override
    // public void onWindowFocusChanged(boolean hasWindowFocus) {
    // // TODO Auto-generated method stub
    // super.onWindowFocusChanged(hasWindowFocus);
    // if(hasWindowFocus){
    // makeReadingButtonFlick();
    // }
    // }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mReadingButton = (ImageButton) findViewById(R.id.reading);
        mReadingButton.setOnClickListener(this);
        mStarButton = (ImageButton) findViewById(R.id.star);
        mStarButton.setOnClickListener(this);
        mStarLine = (ImageView) findViewById(R.id.star_divide_line);

        mVoiceButton = (ImageView) findViewById(R.id.voice);
        mVoiceButton.setOnClickListener(this);
        mVoiceDivideLine = (ImageView) findViewById(R.id.voice_divide_line);
        Resources res = getContext().getResources();
        mStopDrawable = res.getDrawable(R.drawable.ic_stop);
        mRefreshDrawable = res.getDrawable(R.drawable.ic_refresh);
        mClearDrawable = res.getDrawable(R.drawable.ic_stop);
        mUrlInput.setStateListener(this);
        // mUrlInput.setOnFocusChangeListener(this);
        mUrlInput.setSelectAllOnFocus(true);
        // mUrlInput.addTextChangedListener(this);
    }

    @Override
    public void onProgressStarted() {
        super.onProgressStarted();
        mUrlInput.setComboButton(mStopDrawable);
        if (null != frameAnimation && frameAnimation.isRunning()) {
            frameAnimation.stop();
        }
        mReadingButton.setBackgroundResource(R.drawable.btn_open_disabled);
        mReadingButton.setClickable(false);
        // updateBookmarkIcons();
    }

    @Override
    public void updateBookmarkIcons() {
        LogHelper.d(LogHelper.TAG_STARBTN_SPPED, "updateBookmarkIcons,null != mBaseUi: "
                + (null != mBaseUi));
        if (null != mBaseUi) {
            LogHelper.d(LogHelper.TAG_STARBTN_SPPED,
                    "mBaseUi.isHomePageShowing(): " + mBaseUi.isHomePageShowing());
        }
        if (null != mBaseUi && mBaseUi.isHomePageShowing()) {
            mStarButton.setVisibility(View.GONE);
            mStarLine.setVisibility(View.GONE);
        } else if (null != mBaseUi && !mBaseUi.isHomePageShowing()) {
            mStarButton.setVisibility(View.VISIBLE);
            mStarLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showOrHideBookmarkIconsAccoringToProgress() {
        WebView current = mBaseUi.getWebView();
        String url = null;
        if (null != current) {
            url = current.getUrl();
        }
        LogHelper.d(LogHelper.TAG_STARBTN_SPPED, "showOrHideBookmarkIconsAccoringToProgress,url: "
                + url
                + ",homepage: " + BrowserSettings.getInstance().getHomePage());
        if (null == url) {
            mStarButton.setVisibility(View.GONE);
            mStarLine.setVisibility(View.GONE);
        }
        else if (null != url && url.equals(BrowserSettings.getInstance().getHomePage())) {
            LogHelper.d(LogHelper.TAG_STARBTN_SPPED,
                    "showOrHideBookmarkIconsAccoringToProgress,set star gone");
            mStarButton.setVisibility(View.GONE);
            mStarLine.setVisibility(View.GONE);
        } else {
            LogHelper.d(LogHelper.TAG_STARBTN_SPPED,
                    "showOrHideBookmarkIconsAccoringToProgress,set star visible");
            mStarButton.setVisibility(View.VISIBLE);
            mStarLine.setVisibility(View.VISIBLE);
        }
    }

    Resources res;

    @Override
    public void onProgressStopped() {
        super.onProgressStopped();
        mUrlInput.setComboButton(mRefreshDrawable);
        onStateChanged(mUrlInput.getState());
        suggestHideAddToBookmarkQuickSpeechDialog();
        // judgeWhetherReadingButtonFlick();
    }

    @Override
    public void onPageFinished() {
        super.onPageFinished();
        judgeWhetherReadingButtonFlick();
        // add for fix bug when press back key when a whiteUrl hasn't load
        // finished,then backward to homepage,the efficient read help shows in
        // homepage,which is incorrect
        if (!Utils.isInWhiteList(mBaseUi.getActivity(),
                mBaseUi.getCurrentUrl())) {
            if (mBaseUi instanceof HelpEnabledPhoneUi) {
                BrowserAssociatedHelp browserAssociatedHelp = ((HelpEnabledPhoneUi) mBaseUi)
                        .getBrowserAssociatedHelp();
                browserAssociatedHelp.removeAllTip();
            }
        }
    }

    /**
     * add by gaoge 2012-04-01,to change the Reading Button background
     */
    private void judgeWhetherReadingButtonFlick() {
        // null or homepage
        if (!Helpers.isNetworkAvailable(getContext()) || null == mBaseUi.getCurrentUrl()
                || (mBaseUi.getCurrentUrl().trim().equals(""))
                || (null != mBaseUi.getCurrentUrl() && mBaseUi.isHomePageShowing())) {
            mReadingButton.setBackgroundResource(R.drawable.btn_open_disabled);
            mReadingButton.setClickable(false);
        }
        // white list
        else if (Utils.isInWhiteList(mBaseUi.getActivity(), mBaseUi.getCurrentUrl())) {
            mBaseUi.invokeShowEfficientReadTipOrNot();
            if (!mReadingButton.isClickable()) {
                mReadingButton.setClickable(true);
            }
            initReadingButtonFlickAnimation();
            if (frameAnimation.isRunning()) {
                frameAnimation.stop();
            }
            frameAnimation.start();
        }
        // blacklist
        else if (Utils.isInBlackList(mBaseUi.getActivity(), mBaseUi.getCurrentUrl())) {
            mReadingButton.setBackgroundResource(R.drawable.btn_open_disabled);
            mReadingButton.setClickable(false);
        }
        // unknown url
        else {
            if (BrowserGlobals.isChinaVersion(getContext())) {
                mReadingButton.setClickable(true);
                mReadingButton.setBackgroundResource(R.drawable.btn_open_light);
            }
            else {
                mReadingButton.setClickable(false);
                mReadingButton.setBackgroundResource(R.drawable.btn_open_disabled);
            }
        }
    }

    AnimationDrawable frameAnimation;

    private void initReadingButtonFlickAnimation() {
        mReadingButton.setBackgroundResource(R.drawable.reading_button_flick);
        frameAnimation = (AnimationDrawable) mReadingButton.getBackground();
    }

    /**
     * add by gaoge 2012-04-01 end
     */
    private void suggestHideAddToBookmarkQuickSpeechDialog() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * Update the text displayed in the title bar.
     * 
     * @param title String to display. If null, the new tab string will be
     *            shown.
     */
    @Override
    void setDisplayTitle(String title) {
        mUrlInput.setTag(title);
        if (!isEditingUrl()) {
            if (title == null) {
                mUrlInput.setText(R.string.new_tab);
            } else {
                // mUrlInput.setText(UrlUtils.stripUrl(title), false);
                if (mBaseUi.isHomePageShowing() || mBaseUi.isErrorPageShowing()) {
                    mUrlInput.setText(null);
                } else {
                    mUrlInput.setText(UrlUtils.stripUrl(title));
                }
            }
            mUrlInput.setSelection(0);
        }
    }

    /**
     * add by gaoge 2012-03-08 for pop up window
     */
    LinearLayout add_to_bookmark_or_homescreen;
    PopupWindow mPopupWindow;
    public static final int POPLEFT = 10;
    public static final int POPTOP = 70;
    public static final int POPUP_WINDOWN_TRANSPARENT_VALUE = 230;

    private void initPopUpWindow() {
        LayoutInflater mLayoutInflater = (LayoutInflater) mUiController.getActivity()
                .getSystemService(mUiController.getActivity().LAYOUT_INFLATER_SERVICE);
        add_to_bookmark_or_homescreen = (LinearLayout) mLayoutInflater.inflate(
                R.layout.add_to_bookmark_homescreen, null);
        mPopupWindow = new PopupWindow(add_to_bookmark_or_homescreen,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
                    mPopupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        /**
         * add by gaoge 2012-04-09,this is for fix PopupWindow bug ,when haven't
         * this sentence,the PopupWindow won't dismiss when press back key.
         */
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void registPopUnWindowControlListener() {
        View tv_add_to_bookmark = add_to_bookmark_or_homescreen
                .findViewById(R.id.tv_add_to_bookmark);
        View tv_add_to_homescreen = add_to_bookmark_or_homescreen
                .findViewById(R.id.tv_add_to_homescreen);

        tv_add_to_bookmark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mUiController.openBookMarkDirectly();

            }
        });

        tv_add_to_homescreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                WebView current = mBaseUi.getWebView();
                String url = current.getUrl();
                LogHelper.d(LogHelper.TAG_STARBTN_SPPED, "add_to_homescreen,url: " + url
                        + ",homepage: " + BrowserSettings.getInstance().getHomePage());
                if (null != url && url.equals(BrowserSettings.getInstance().getHomePage())) {
                    Toast.makeText(
                            mBaseUi.getActivity(),
                            mBaseUi.getActivity().getResources()
                                    .getString(R.string.homelinks_err_add_fail), Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                String title = current.getTitle();
                Context context = mUiController.getActivity();
                HomeLinks.saveHomeLink(context, url, title);
            }
        });

    }

    private void showPopUpWindow() {
        initPopUpWindow();
        registPopUnWindowControlListener();
        float left = mReadingButton.getRight()
                + getResources().getDimension(R.dimen.readingbutton_margin_right);
        LogHelper.d(LogHelper.TAG_TITLEBAR, "mReadingButton.getLeft(): " + mReadingButton.getLeft()
                + ",this.getLeft(): " + getLeft());
        int top = mBaseUi.getTitleBarContainer().getHeight();
        // mPopupWindow.showAtLocation(mStarButton,
        // Gravity.NO_GRAVITY, left,top );
        mPopupWindow.showAsDropDown(mTitleBar, (int) left, 0);
    }

    @Override
    public void startEffieientRead() {
        // TODO:HAN Liang
        // mUiController.setCollectorMode(true);
        // mBaseUi.getWebView().loadUrl("javascript:!function(){window.linkArray=[];function d(){var e=arguments[0];var f=e.target;if(f.style.backgroundColor=='rgba(46,172,204,0.5)'){b(e)}else{if(c(f)){linkArray.push({target:f,_switch:true});f.style.backgroundColor='rgba(46,172,204,0.5)'}}if(arguments[1]){b(e)}}function c(f){var e=f;while(e!=document.body){if(e.tagName=='A'){return true}else{e=e.parentNode}}return false}function b(f){if(f.stopPropagation&&f.preventDefault){f.stopPropagation();f.preventDefault()}}window.select=function(){document.body.onclick=d;window.iframes=a(document);for(var f=0;f<window.iframes.length;f++){try{window.iframes[f].contentDocument.body.onclick=function(h){d(h,'iframe')}}catch(g){console.log(g.message)}}};function a(g){try{var j=Array.prototype.slice.call(g.getElementsByTagName('iframe'));for(var f=0;f<j.length;f++){j.concat(a(j[f].contentDocument))}}catch(h){console.log(h.message)}return j}select();window.deselect=function(){for(var e=0;e<linkArray.length;e++){linkArray[e].target.style.backgroundColor=''}linkArray=[];document.body.onclick=null;for(var e=0;e<window.iframes.length;e++){window.iframes[e].contentDocument.body.onclick=null}window.iframes=[];return(e==linkArray.length)?(true):false};window.turnOff=function(e){window.linkArray[e]._switch=false;console.log('true off completed on id:'+e)}}()");
        // mBaseUi.showBatchModeTitleBar();
        // mBaseUi.hideBottomBarinBatch();
        // mBaseUi.invokeBeforeStartEfficientRead();
        mUiController.getEffieientReadMode().startEffieientRead(mBaseUi);
    }

    @Override
    public void onClick(View v) {
        if (v == mStarButton) {
            if (mBaseUi.isHomePageShowing()) {
                mUiController.openBookMark();
            } else {
                showPopUpWindow();
            }
        } else if (v == mVoiceButton) {
            mUiController.startVoiceSearch();
        } else if (v == mReadingButton) {
            startEffieientRead();
        }
        else {
            super.onClick(v);
        }
        // if bottom bar menu opened,close it
        if (mBaseUi.mMenu.isShown()) {
            mBaseUi.mMenu.hide();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view == mUrlInput) {
            if (hasFocus && !mUrlInput.getText().toString().equals(mUrlInput.getTag())) {
                if (mBaseUi.isHomePageShowing() || mBaseUi.isErrorPageShowing()) {
                    mUrlInput.setText(null);
                } else {
                    // only change text if different
                    // mUrlInput.setText((String) mUrlInput.getTag(), false);
                    mUrlInput.setText((String) mUrlInput.getTag());
                    mUrlInput.selectAll();
                }
            } else {
                setDisplayTitle(mUrlInput.getText().toString());
            }
        }
        super.onFocusChange(view, hasFocus);
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case StateListener.STATE_NORMAL:
                mReadingButton.setVisibility(View.VISIBLE);
                updateBookmarkIcons();
                mUrlInput
                        .setComboButton(mTitleBar == null || !mTitleBar.isInLoad() ? mRefreshDrawable
                                : mStopDrawable);
                mVoiceButton.setVisibility(View.GONE);
                mVoiceDivideLine.setVisibility(View.GONE);
                // mUrlInput.showFavicon();
                break;
            case StateListener.STATE_HIGHLIGHTED:
                mReadingButton.setVisibility(View.GONE);
                mStarButton.setVisibility(View.GONE);
                mStarLine.setVisibility(View.GONE);
                mVoiceButton.setVisibility(View.VISIBLE);
                mVoiceDivideLine.setVisibility(View.VISIBLE);
                mUrlInput.setComboButton(mClearDrawable);
                // mUrlInput.hideFavicon();
                break;
            case StateListener.STATE_EDITED:
                mReadingButton.setVisibility(View.GONE);
                mStarButton.setVisibility(View.GONE);
                mStarLine.setVisibility(View.GONE);
                mVoiceButton.setVisibility(View.VISIBLE);
                mVoiceDivideLine.setVisibility(View.VISIBLE);
                mUrlInput.setComboButton(mClearDrawable);
                // mUrlInput.hideFavicon();
                break;
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        // TODO Auto-generated method stub
        super.setTitleBar(titleBar);
    }

    @Override
    public void hideReadingBar() {
        // comment by gaoge ,because this icon won't show any more
        // mReadingButton.setImageResource(R.drawable.btn_readmode_close);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public ImageButton getReadingButton() {
        return mReadingButton;
    }

}
