
package com.orange.browser;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrangeGestureHelp {

    protected Activity mActivity;
    ViewGroup mParent;
    private BaseUi mBaseUi;
    protected View mTip;
    TextView mSelectArticle;

    private final static int JIANBAO_SHOW_LIMIT = 3;
    private final static int TRY_SHOW_LIMIT = 2;
    private final static int TRY_TIP_INDEX = 5;

    boolean isTryTipShowing = false;

    public OrangeGestureHelp(Activity activity, BaseUi baseUi) {
        mActivity = activity;
        mParent = baseUi.mWebViewContainer;
        mBaseUi = baseUi;
    }

    private void showJianBaoHelpTip(String url) {
        if ((null != url) && UrlUtils.isArticleAccordingtoUrl(url)) {
            showHelpTip(R.drawable.rectangle_tip, R.string.gesture_jianbao_tip);
            BrowserGlobals.setRestartForJianBaoTip(false);
        }
    }

    private void showShareTip() {
        showHelpTip(R.drawable.gesture_share, R.string.gesture_share_tip);
        setShowShareTip(false);
    }

    private void showHelpTip(int picId, int textId) {
        mTip = LayoutInflater.from(mActivity).inflate(R.layout.tip_gesture,
                (ViewGroup) mActivity.findViewById(R.id.tip_toast));
        mSelectArticle = (TextView) mTip.findViewById(R.id.gesture_tip);

        mSelectArticle.setText(textId);
        mSelectArticle
                .setCompoundDrawablesWithIntrinsicBounds(picId, 0, 0, 0);
        Toast toast = new Toast(mActivity);
        toast.setView(mTip);
        toast.show();
    }

    private void showTryTip(final ViewGroup parent) {
        mTip = LayoutInflater.from(mActivity).inflate(R.layout.tip_gesture_try, null);
        if (mTip.isShown()) {
            return;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mTip, params);
        isTryTipShowing = true;
        mTip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                removeTryTip(mBaseUi.getWebViewContainer());
                Intent intentAct = new Intent();
                intentAct.setClassName(mActivity.getPackageName(),
                        GestureHelpActivity.class.getName());
                mActivity.startActivity(intentAct);
            }
        });
        BrowserGlobals.setRestartForTryTip(false);
    }

    public void removeTryTip(ViewGroup parent) {
        if (isTryTipShowing) {
            if (null != parent && (null != mTip)) {
                parent.removeView(mTip);
                mTip = null;
            }
            isTryTipShowing = false;
        }
    }

    public void onPageFinished(String url, boolean homepage, ViewGroup parent) {
        if (!homepage && !BrowserAssociatedHelp.isHelpShowing(mActivity)) {
            if (!getIfUseAnyGesture(mActivity) && BrowserGlobals.getRestartForTryTip()
                    && !(BrowserSettings.getInstance().getErrorPage().equals(url))
                    && isLaunchCountEnoughForShowTryTip()
                    && !BrowserGlobals.getEfficientReadTipShowedThisTime()
                    && BrowserAssociatedHelp.getIfAlreadyUseEfficientRead(mActivity)) {
                showTryTip(parent);
            }
            else {
                boolean showTipThisLaunch = BrowserGlobals.getRestartForJianBaoTip();
                boolean launchCountEnough = isLaunchCountEnoughForShowJianBao();
                boolean alreadyUsed = getIfAlreadyUseJianBaoGesture();
                if (launchCountEnough && showTipThisLaunch && !alreadyUsed) {
                    showJianBaoHelpTip(url);
                }
            }
        }
    }

    public static void setIfAlreadyUseShareGesture(Activity act, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean(act,
                SharedPreferenceUtils.ALREADY_USE_SHARE_GESTURE, flag);
    }

    public boolean getIfAlreadyUseShareGesture() {
        return SharedPreferenceUtils.getPrefsBooleanValue(mActivity,
                SharedPreferenceUtils.ALREADY_USE_SHARE_GESTURE, false);
    }

    public static void setIfAlreadyUseJianBaoGesture(Activity act, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean(act,
                SharedPreferenceUtils.ALREADY_USE_JIANBAO_GESTURE, flag);
    }

    public boolean getIfAlreadyUseJianBaoGesture() {
        return SharedPreferenceUtils.getPrefsBooleanValue(mActivity,
                SharedPreferenceUtils.ALREADY_USE_JIANBAO_GESTURE, false);
    }

    public static void setIfUseAnyGesture(Activity act, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean(act,
                SharedPreferenceUtils.ALREADY_USE_ANY_GESTURE, flag);
    }

    public static boolean getIfUseAnyGesture(Activity act) {
        return SharedPreferenceUtils.getPrefsBooleanValue(act,
                SharedPreferenceUtils.ALREADY_USE_ANY_GESTURE, false);
    }

    public boolean isLaunchCountEnoughForShowJianBao() {
        return SharedPreferenceUtils.getAppLaunchCount(mActivity) >= JIANBAO_SHOW_LIMIT;
    }

    public boolean isLaunchCountEnoughForShowTryTip() {
        return SharedPreferenceUtils.getAppLaunchCount(mActivity) >= TRY_SHOW_LIMIT;
    }

    public boolean getShowShareTip() {
        return SharedPreferenceUtils.getPrefsBooleanValue(mActivity,
                SharedPreferenceUtils.SHOW_SHARE_TIP, true);
    }

    public void setShowShareTip(boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean(mActivity,
                SharedPreferenceUtils.SHOW_SHARE_TIP, flag);
    }

    public static boolean isHelpEnabled(Activity activity) {
        return !getIfUseAnyGesture(activity);
    }

    public boolean onBackKey() {
        if (isTryTipShowing) {
            return true;
        }
        return false;
    }
}
