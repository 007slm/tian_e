
package com.orange.browser.dgil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.browser.BaseUi;
import com.orange.browser.BrowserSettings;
import com.orange.browser.GestureHelpActivity;
import com.orange.browser.HelpEnabledPhoneUi;
import com.orange.browser.OrangeGestureHelp;
import com.orange.browser.R;
import com.orange.browser.UiController;
import com.orange.browser.tools.OBrowserUtils;



public final class DgilActions {

    // DGIL symbols mapping
    private static final String BACK_TO_HOME        = "triangle";

    private static final String PREV_PAGE           = "inf";
    private static final String NEXT_PAGE           = "sup";
    private static final String TOP_PAGE            = "curve_up";
    private static final String BOTTOM_PAGE         = "curve_down";

    private static final String FAVORITE            = "favorite";

    private static final String NEW_TAB             = "top_down_arch";
    private static final String CLOSE_TAB           = "alpha";
    private static final String PREV_TAB            = "right_left_arch";
    private static final String NEXT_TAB            = "left_right_arch";

    private static final String ADD_TO_READING_LIST = "ribbon_down";

    private static final String SEARCH_IN_PAGE      = "magnifying_glass";

    private static final String SELECT_TEXT_AREA_AC = "anticlockwise_circle";
    private static final String SELECT_TEXT_AREA_CC = "clockwise_circle";

    private static final String FULLSCREEN          = "hat";

    private static final String SHARE               = "ribbon_up";

    private static final String SELECT_TEXT_AREA    = "square";

    private static final String HELP                = "question_mark";


    public static final String FLICK_N = "N-Flick";
    public static final String FLICK_S = "S-Flick";


    private final Activity mActivity;
    private final UiController mController;

    private DgilGestureOverlayView mDgilGestureOverlayView;


    public DgilActions(final Activity activity, final UiController controller) {
        mActivity = activity;
        mController = controller;
    }



    public void setDgilGestureOverlayView ( final DgilGestureOverlayView dgilGestureOverlayView ) {
        mDgilGestureOverlayView = dgilGestureOverlayView;
    }



    public boolean action ( final String s, final boolean repeat ) {

        if (repeat) {
            final boolean repeatAllowed = s.equals(FLICK_N) || s.equals(FLICK_S) || s.equals(PREV_PAGE) || s.equals(NEXT_PAGE) || s.equals(PREV_TAB) || s.equals(NEXT_TAB);

            if (repeatAllowed == false) {
                return false;
            }
        }

        if (s.equals(FLICK_N)) {
            flickN();
        } else if (s.equals(FLICK_S)) {
            flickS();
        } else if (s.equals(BACK_TO_HOME)) {
            backToHome();
        } else if (s.equals(PREV_PAGE)) {
            prevPage();
            OBrowserUtils.showToast(mActivity, R.string.use_backward_gesture);
        } else if (s.equals(NEXT_PAGE)) {
            nextPage();
            OBrowserUtils.showToast(mActivity, R.string.use_forward_gesture);
        } else if (s.equals(FAVORITE)) {
            favorite();
        } else if (s.equals(NEW_TAB)) {
            newTab();
        } else if (s.equals(CLOSE_TAB)) {
            closeTab();
        } else if (s.equals(PREV_TAB)) {
            prevTab();
        } else if (s.equals(NEXT_TAB)) {
            nextTab();
        } else if (s.equals(ADD_TO_READING_LIST)) {
            addToReadingList();
        } else if (s.equals(SEARCH_IN_PAGE)) {
            searchInPage();
        } else if (s.equals(FULLSCREEN)) {
            fullscreen();
            OBrowserUtils.showToast(mActivity, R.string.use_fullscreen_gesture);
        } else if ( s.equals(SELECT_TEXT_AREA) || s.equals(SELECT_TEXT_AREA_AC) || s.equals(SELECT_TEXT_AREA_CC) ) {
            selectTextArea();
        } else if (s.equals(SHARE)) {
            share();
        } else if (s.equals(HELP)) {
            help();
        }

        // else {
        // Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
        // }
        OrangeGestureHelp.setIfUseAnyGesture(mActivity,true);
        return true;
    }



    private void selectTextArea() {
        if (!DgilInstance.inLongPressMode()){
            //Add a help toast.
            View toastView = LayoutInflater.from(mActivity).inflate(R.layout.tip_gesture_help, null);
            TextView tv = (TextView) toastView.findViewById(R.id.toast_message);
            tv.setText(R.string.gesture_toast_no_longpress);
            Toast toast = new Toast(mActivity);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setView(toastView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }

        final Rect rect = mDgilGestureOverlayView.getGestureRect();
        View v = mActivity.getWindow().getDecorView();
        int screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();

        //Adjust rect
        int deviation = 10;
        Rect modRect = new Rect(rect.left - deviation < 0 ? 0 : rect.left - deviation,
                rect.top - deviation < 0 ? 0 : rect.top - deviation,
                rect.right + deviation, rect.bottom + deviation + 20); //20 is because we mask the picture at bottom.

        if ((rect.left * 1.0) / screenWidth < 0.1) {
            modRect.left = 0;
        }

        if ((rect.right * 1.0) / screenWidth > 0.9) {
            modRect.right = screenWidth;
        }

        Bitmap bmp = OBrowserUtils.createScreenshot(v, modRect.width(), modRect.height(),
                modRect.left, modRect.top);
        if (bmp != null) {
            mController.shareUserSelect(bmp, DgilInstance.inLongPressMode());
        }
        OrangeGestureHelp.setIfAlreadyUseJianBaoGesture(mActivity,true);
    }

    private void backToHome ( ) {
        if (!mController.getUi().isHomePageShowing()) {
            mController.openTabToHomePage(true);
        }
    }

    @TargetApi(14)
    private void prevPage ( ) {
        mController.getCurrentTab().goBack();
    }

    private void nextPage ( ) {
        mController.getCurrentTab().goForward();
    }

    private void topPage ( ) {
        mController.getCurrentTopWebView().pageUp(true);
    }

    private void bottomPage ( ) {
        mController.getCurrentTopWebView().pageDown(true);
    }

    private void favorite ( ) {
        mController.openBookMark();
    }

    private void closeTab ( ) {
        mController.closeCurrentTab();
    }

    private void newTab ( ) {
        mController.openTabToHomePage(false);
    }

    private void prevTab ( ) {

        mController.setActiveTab(mController.getPrevTab());
    }

    private void nextTab ( ) {
        mController.setActiveTab(mController.getNextTab());
    }

    private void addToReadingList ( ) {
        mController.getUi().getMenu().addToReadlist();
    }

    private void searchInPage ( ) {
        mController.openFindDialog();
    }

    private void fullscreen ( ) {
        final BrowserSettings settings = BrowserSettings.getInstance();
        settings.setFullscreen(!settings.useFullscreen());
    }

    public void enterMultiselectionMode ( ) {
        final View v = mActivity.findViewById(R.id.reading);
        if (v.isClickable()) {
            mActivity.findViewById(R.id.reading).performClick();
        }
    }

    private void share ( ) {
        mController.sharePage();
        OrangeGestureHelp.setIfAlreadyUseShareGesture(mActivity,true);
    }

    private void help ( ) {
        Intent intentAct = new Intent();
        intentAct.setClassName(mActivity.getPackageName(),
                GestureHelpActivity.class.getName());
        mActivity.startActivity(intentAct);
        BaseUi baseUi = (BaseUi) mController.getUi();
        if (baseUi instanceof HelpEnabledPhoneUi) {
            OrangeGestureHelp orangeGestureHelp = ((HelpEnabledPhoneUi) baseUi)
                    .getOrangeGestureHelp();
            orangeGestureHelp.removeTryTip(baseUi.getWebViewContainer());
        }
    }

    private void flickN ( ) {
        mController.getCurrentTopWebView().pageDown(false);
    }

    private void flickS ( ) {
        mController.getCurrentTopWebView().pageUp(false);
    }
}
