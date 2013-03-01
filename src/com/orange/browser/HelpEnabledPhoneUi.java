
package com.orange.browser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class HelpEnabledPhoneUi extends PhoneUi {
    private BrowserAssociatedHelp mHelp;
    private OrangeGestureHelp mOrangeGestureHelp;
    

    public HelpEnabledPhoneUi(Activity browser, UiController controller) { super(browser, controller);
        mHelp = new BrowserAssociatedHelp(browser, this, controller);
        mOrangeGestureHelp = new OrangeGestureHelp(browser, this);
    }

    public BrowserAssociatedHelp getBrowserAssociatedHelp() {
        return mHelp;
    }
    
    public OrangeGestureHelp getOrangeGestureHelp() {
        return mOrangeGestureHelp;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        mHelp.onConfigurationChanged(config);
    }

    @Override
    public boolean onBackKey() {
        if (mHelp.onBackKey() || mOrangeGestureHelp.onBackKey()) {
            return true;
        }
        return super.onBackKey();
    }
    @Override
    public boolean onMenuKey() {
        if(mHelp.onMenukey()){
            return true;
        }
        return super.onMenuKey();
    }

    @Override
    public void suggestHideTitleBar() {
        super.suggestHideTitleBar();
        /**
         * modify by gaoge 2012-04-20 because in BaseUi constructor,will call
         * setFullscreen, which will call showTitleBar,but now the mHelp is not
         * instanite already
         */
        if (null != mHelp) {
            mHelp.suggestHideTitleBar();
        }
    }

    @Override
    public void showTitleBar() {
        super.showTitleBar();
        /**
         * modify by gaoge 2012-04-20 because in BaseUi constructor,will call
         * setFullscreen, which will call showTitleBar,but now the mHelp is not
         * instanite already
         */
        if (null != mHelp) {
            mHelp.showTitleBar();
        }
    }

    public void showEfficientTip() {
            mHelp.showEfficientTip();
    }

    public void showSelectArticlesTip() {
            mHelp.showSelectArticlesTip();
    }

    @Override
    public void invokeShowEfficientReadTipOrNot() {
        super.invokeShowEfficientReadTipOrNot();
        boolean isReadingButtonVisible = true;
        if(null != mNavigationBar){
             isReadingButtonVisible = mNavigationBar.getReadingButton().isShown();
        }
        LogHelper.d(LogHelper.TAG_HELP, "HelpEnabledPhoneUi.invokeWhenUrlIsWhiteList(),isReadingButtonVisible: " + isReadingButtonVisible);
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(mTitleBar
                .getContext());
        if (!mSharedPref.getBoolean(PreferenceKeys.PREF_FULLSCREEN, false) && isReadingButtonVisible) {
            showEfficientTip();
        }
    }

    @Override
    public void invokeBeforeStartEfficientRead() {
        super.invokeBeforeStartEfficientRead();
        showSelectArticlesTip();
    }

    @Override
    public void onQuitFullScreen() {
        super.onQuitFullScreen();
        showEfficientTip();
    }

    @Override
    public void onSelectArticlesNumChange(int articleNum) {
        super.onSelectArticlesNumChange(articleNum);
        mHelp.updateSelectArticleTip(articleNum);
    }
    @Override
    public boolean onkeyDown() {
        return mHelp.isSelectArticleTipShowing();
    }

    @Override
    public void confirmBtnClick() {
        super.confirmBtnClick();
        mHelp.confirmBtnClick();
    }

}
