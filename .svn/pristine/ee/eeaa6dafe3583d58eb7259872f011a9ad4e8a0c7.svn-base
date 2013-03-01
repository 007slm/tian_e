
package com.orange.browser;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BrowserAssociatedHelp {

    static final int SHOW_EFFICIENT_READ_LAUNCH_COUNT_LIMIT = 1;
    BaseUi mBaseUi;
    private UiController mUiController;
    private FullscreenTip mFullscreenTip;
    private EfficientReadTip mEfficientReadTip;
    private SelectArticlesTip mSelectArticlesTip;
    private Context mContext;

    public BrowserAssociatedHelp(Context context, BaseUi baseUi, UiController controller) {
        mBaseUi = baseUi;
        mContext = context;
        mUiController = controller;
        if (shouldShowFullScreenTip(context)) {
            mFullscreenTip = new FullscreenTip(context, null);
        }
        if (!getIfAlreadyUseEfficientRead(context)) {
            mEfficientReadTip = new EfficientReadTip(context, null);
        }
        if (!getIfAlreadyUseEfficientRead(context)) {
            mSelectArticlesTip = new SelectArticlesTip(context, mBaseUi.mWebViewContainer);
        }
    }

    public void removeAllTip() {
        if (null != mFullscreenTip && mFullscreenTip.isShown()) {
            mFullscreenTip.remove();
        }
        if (null != mEfficientReadTip && mEfficientReadTip.isShown()) {
            mEfficientReadTip.remove();
        }
        if (null != mSelectArticlesTip && mSelectArticlesTip.isShown()) {
            mSelectArticlesTip.remove();
        }
    }

    public void removeEfficientReadTip() {
        if (null != mEfficientReadTip && mEfficientReadTip.isShown()) {
            mEfficientReadTip.remove();
        }
    }

    public static UI getUI(Activity activity, Controller controller) {
        UI ui;
        if (isHelpEnabled(activity) || OrangeGestureHelp.isHelpEnabled(activity)) {
            ui = new HelpEnabledPhoneUi(activity, controller);
        } else {
            ui = new PhoneUi(activity, controller);
        }
        return ui;
    }

    private static boolean isHelpEnabled(Activity activity) {
        return shouldShowFullScreenTip(activity) || !getIfAlreadyUseEfficientRead(activity);
    }

    public void onConfigurationChanged(Configuration config) {
        if (null != mSelectArticlesTip) {
            mSelectArticlesTip.update();
        }
    }

    public boolean onBackKey() {
        if (mFullscreenTip != null && mFullscreenTip.isShown()) {
            return true;
        }
        if (mEfficientReadTip != null && mEfficientReadTip.isShown()) {
            return true;
        }
        if (null != mSelectArticlesTip && mSelectArticlesTip.isShown()) {
            return true;
        }
        return false;
    }

    public boolean onMenukey() {
        if (mFullscreenTip != null && mFullscreenTip.isShown()) {
            return true;
        }
        if (mEfficientReadTip != null && mEfficientReadTip.isShown()) {
            return true;
        }
        if (null != mSelectArticlesTip && mSelectArticlesTip.isShown()) {
            return true;
        }
        return false;
    }

    public void suggestHideTitleBar() {
        if (mFullscreenTip != null && mBaseUi.canHideTitleBar()
                && !mUiController.isFindDialogShowing() && !mBaseUi.isHomePageShowing()
                && !mBaseUi.getCurrentUrl().trim().equals("") && shouldShowFullScreenTip(mContext)) {
            setFullScreenTipIsReadyToShow(mContext, true);
            mFullscreenTip.show();
        }

        if (mBaseUi.canHideTitleBar()) {
            if (null != mEfficientReadTip && mEfficientReadTip.isShown()) {
                mEfficientReadTip.remove();
            }
        }
    }

    private boolean isLaunchCountEnough() {
        return SharedPreferenceUtils.getAppLaunchCount(mBaseUi
                .getActivity()) >= SHOW_EFFICIENT_READ_LAUNCH_COUNT_LIMIT;
    }

    public void showEfficientTip() {
        showEfficientTipAccordingUrl();
    }

    public void showSelectArticlesTip() {
        if (isLaunchCountEnough()) {
            if (null != mSelectArticlesTip) {
                mSelectArticlesTip.show();
            }
        }
    }

    public void updateSelectArticleTip(int articleNum) {
        if (isLaunchCountEnough()) {
            if (null != mSelectArticlesTip) {
                mSelectArticlesTip.updateTip(articleNum);
            }
        }
    }

    public void confirmBtnClick() {
        if (null != mSelectArticlesTip) {
            mSelectArticlesTip.remove();
        }
    }

    public boolean isSelectArticleTipShowing() {
        if (null != mSelectArticlesTip) {
            return mSelectArticlesTip.isShown();
        }
        return false;
    }

    public void showTitleBar() {
    }

    private void showEfficientTipAccordingUrl() {
        String currentUrl = mBaseUi.getCurrentUrl();
        boolean isWhiteUrl = Utils.isInWhiteList(mContext, currentUrl);
        if (isLaunchCountEnough() && BrowserGlobals.getRestartForEfficientReadTip()
                && !getIfAlreadyUseEfficientRead(mContext)
                && !mBaseUi.isHomePageShowing() && isWhiteUrl
                // && OrangeGestureHelp.getIfUseAnyGesture((Activity) mContext)
                && !BrowserGlobals.getHelpActivityShowedThisTime()
                && !UrlUtils.isArticleAccordingtoUrl(currentUrl)) {
            /**
             * this judge statment is used for the case which first close
             * efficient tip,then down fling on the fullscreen tip
             */
            if (BrowserGlobals.isChinaVersion(mContext)) {
                if (HomeLinks.checkIsExisted(currentUrl, mContext.getContentResolver())
                        && null != mEfficientReadTip) {
                    setEfficientReadTipIsReadyToShow(mContext, true);
                    mEfficientReadTip.show();
                }
            } else {
                if (null != mEfficientReadTip) {
                    setEfficientReadTipIsReadyToShow(mContext, true);
                    mEfficientReadTip.show();
                }
            }
        } else {
            if (null != mEfficientReadTip && mEfficientReadTip.isShown()) {
                mEfficientReadTip.remove();
            }
        }
    }

    public static void setEfficientReadTipIsReadyToShow(Context context, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean((Activity) context,
                SharedPreferenceUtils.IS_EFFICIENT_READ_READY_TO_SHOW, flag);
    }

    public static boolean getEfficientReadTipIsReadyToShow(Context context) {
        return SharedPreferenceUtils.getPrefsBooleanValue((Activity) context,
                SharedPreferenceUtils.IS_EFFICIENT_READ_READY_TO_SHOW, false);
    }

    public static void setFullScreenTipIsReadyToShow(Context context, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean((Activity) context,
                SharedPreferenceUtils.IS_FULLSCREEN_READY_TO_SHOW, flag);
    }

    public static boolean getFullScreenTipIsReadyToShow(Context context) {
        return SharedPreferenceUtils.getPrefsBooleanValue((Activity) context,
                SharedPreferenceUtils.IS_FULLSCREEN_READY_TO_SHOW, false);
    }

    public static boolean isHelpShowing(Context context) {
        return getEfficientReadTipIsReadyToShow(context) || getFullScreenTipIsReadyToShow(context);
    }

    abstract class HelpTip {
        protected Context mContext;
        protected View mTip;
        protected boolean mShowTip = true;

        private SharedPreferences mSharedPref;
        private String mPreferenceKey;
        protected int mResId;

        WindowManager mWindowManager;
        WindowManager.LayoutParams params;
        ViewGroup mParent;

        public HelpTip(Context context, int resId, ViewGroup parent, String preferenceKey) {
            mPreferenceKey = preferenceKey;
            mContext = context;
            mResId = resId;
            mParent = parent;
            mSharedPref = SharedPreferenceUtils.getSharedPreferences((Activity) context,
                    SharedPreferenceUtils.HELP_SHARED_FILE);
        }

        public boolean isShown() {
            return mTip != null && mTip.isShown();
        }

        public View getTip() {
            return mTip;
        }

        public void show() {
            if (!mShowTip) {
                return;
            }
            setWindowManagerLayoutParams();
            if (mTip != null) {
                removeTip();
            }
            mTip = LayoutInflater.from(mContext).inflate(mResId, null);
            final View tip = mTip;
            mWindowManager.addView(mTip, params);
            View close = tip.findViewById(R.id.close);
            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove();
                }
            });
        }

        private void removeTip() {
            if (null == mParent) {
                mWindowManager.removeView(mTip);
            } else {
                mParent.removeView(mTip);
            }
        }

        private void setWindowManagerLayoutParams() {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            params = new WindowManager.LayoutParams();

            params = new WindowManager.LayoutParams();
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION + 3;
            params.format = PixelFormat.RGBA_8888;

            if (mResId == R.layout.tip_fullscreen) {

                params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            else {
                params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
        }

        public void update() {
            if (isShown()) {
                show();
            }
        }

        public boolean remove() {
            if (mTip != null) {
                mSharedPref.edit().putBoolean(mPreferenceKey, false).commit();
                mShowTip = false;
                removeTip();
                mTip = null;
                resetWM();
                return true;
            }
            return false;
        }

        private void resetWM() {
            mWindowManager = null;
            params = null;
        }

    }

    private class FullscreenTip extends HelpTip {

        public FullscreenTip(Context context, ViewGroup parent) {
            super(context, R.layout.tip_fullscreen, parent,
                    SharedPreferenceUtils.SHOW_FULLSCREEN_TIP);
        }

        @Override
        public void show() {
            super.show();
            if (!mShowTip) {
                return;
            }
            mTip.setOnTouchListener(new CloseFullscreenListener(mContext, mBaseUi) {
                @Override
                protected void onQuitFullscreen() {
                    LogHelper.d(LogHelper.TAG_TOUCH,
                            "BrowserAssociatedHelp.CloseFullscreenListener.onQuitFullscreen()");
                    remove();
                    showEfficientTipAccordingUrl();
                }
            });
            LogHelper.d(LogHelper.TAG_TOUCH,
                    "BrowserAssociatedHelp set help CloseFullscreenListener  ");
        }

        @Override
        public boolean remove() {
            setShouldShowFullScreenTip(mContext, false);
            setFullScreenTipIsReadyToShow(mContext, false);
            return super.remove();
        }
    }

    private class EfficientReadTip extends HelpTip {
        float dismiss_x;
        float dismiss_y;

        public EfficientReadTip(Context context, ViewGroup parent) {
            super(context, R.layout.tip_efficient_read, parent,
                    null);
            dismiss_x = context.getResources().getDimension(R.dimen.help_guide_read_dismiss_x);
            dismiss_y = context.getResources().getDimension(R.dimen.help_guide_read_dismiss_y);
        }

        @Override
        public boolean remove() {
            setEfficientReadTipIsReadyToShow(mContext, false);
            BrowserGlobals.setRestartForEfficientReadTip(false);
            return super.remove();
        }

        @Override
        public void show() {
            super.show();
            BrowserGlobals.setEfficientReadTipShowedThisTime(true);
            if (!mShowTip) {
                return;
            }
            mTip.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        float x = event.getX();
                        float y = event.getY();
                        if ((x > 0 && x < dismiss_x) && (y > 0 && y < dismiss_y)) {
                            remove();
                            mBaseUi.getTitleBar().getNavigationBar().startEffieientRead();
                        }
                    }
                    return false;
                }
            });
        }

    }

    private class SelectArticlesTip extends HelpTip {
        TextView mSelectArticle;
        View mSunlight;
        View mHand;
        private static final int FIRST_TIP = 0;
        private static final int SECOND_TIP = 1;
        private static final int THIRD_TIP = 2;
        private boolean mInitViews = false;
        RelativeLayout.LayoutParams mParams;
        private int mCurrentTipIndex = FIRST_TIP;

        public SelectArticlesTip(Context context, ViewGroup parent) {
            super(context, R.layout.tip_select_articles, parent,
                    null);
        }

        private void showFirstTip() {
            final String lightStr = mContext.getString(R.string.light_seperator);
            Drawable d = mContext.getResources().getDrawable(R.drawable.bulb);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BOTTOM);
            SpannableString ss = new SpannableString(mContext.getString(
                    R.string.help_select_first_link, lightStr));
            int start = ss.toString().indexOf(lightStr);
            ss.setSpan(span, start, start + lightStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mSelectArticle.setText(ss);
        }

        public void updateTip(int index) {
            mCurrentTipIndex = index;
            /**
             * after click light confirm button,this method will also
             * invoked,even now the preference value is false,so we need a if
             * judgment here
             */
            if (getIfAlreadyUseEfficientRead(mContext)) {
                return;
            }
            initViews();
            switch (mCurrentTipIndex) {
                case FIRST_TIP:
                    showFirstTip();
                    break;
                case SECOND_TIP:
                    mSelectArticle.setText(mContext.getString(R.string.help_select_second_link));
                    break;
                default:
                    mSelectArticle
                            .setText(mContext.getString(R.string.help_select_link_start_read));
                    mHand.setVisibility(View.VISIBLE);
                    mSunlight.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void show() {
            if (!mShowTip) {
                return;
            }
            if (mTip != null) {
                mParent.removeView(mTip);
                mTip = null;
                mSelectArticle = null;
                mHand = null;
                mSunlight = null;
                mInitViews = false;
            }
            initViews();
            updateTip(mCurrentTipIndex);
        }

        private void initViews() {
            if (!mInitViews) {
                mTip = LayoutInflater.from(mContext).inflate(mResId, null);
                mSelectArticle = (TextView) mTip.findViewById(R.id.select_article);
                mHand = mTip.findViewById(R.id.hand);
                mSunlight = mTip.findViewById(R.id.sunlight);
                mInitViews = true;
                DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                int offsetMidVertical = mContext.getResources().getDimensionPixelSize(
                        R.dimen.help_offset_center_vertical);
                /**
                 * when set this help tip layout center in vertical,it's still
                 * not high enough,so here we shortcut the tip's height,which
                 * can meet this requirment
                 */
                mParams = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        displayMetrics.heightPixels - offsetMidVertical);
                mParent.addView(mTip, mParams);
            }

        }

    }

    public static void setIfAlreadyUseEfficientRead(Context context, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean((Activity) context,
                SharedPreferenceUtils.ALREADY_USE_EFFICIENT_READ, flag);
    }

    public static boolean getIfAlreadyUseEfficientRead(Context context) {
        return SharedPreferenceUtils.getPrefsBooleanValue((Activity) context,
                SharedPreferenceUtils.ALREADY_USE_EFFICIENT_READ, false);
    }

    public static boolean shouldShowFullScreenTip(Context context) {
        return SharedPreferenceUtils.getPrefsBooleanValue((Activity) context,
                SharedPreferenceUtils.SHOW_FULLSCREEN_TIP, true);
    }

    public static void setShouldShowFullScreenTip(Context context, boolean flag) {
        SharedPreferenceUtils.setPrefsBoolean((Activity) context,
                SharedPreferenceUtils.SHOW_FULLSCREEN_TIP, flag);
    }

}
