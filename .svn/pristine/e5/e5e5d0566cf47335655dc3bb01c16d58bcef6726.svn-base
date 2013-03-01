package com.orange.browser;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

/* package */ class WebDialog extends LinearLayout {
    protected WebView         mWebView;
//    protected BrowserActivity mBrowserActivity;
    protected Controller mController;
    private boolean           mIsVisible;

    /* package */ WebDialog(Controller mController ) {
        super(mController.getContext());
        this.mController = mController;
    }

    /* dialogs that have cancel buttons can optionally share code by including a
     * view with an id of 'done'.
     */
    protected void addCancel() {
        View button = findViewById(R.id.done);
        if (button != null) button.setOnClickListener(mCancelListener);
    }

    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            mController.closeDialogs();
        }
    };

    protected void dismiss() {
        startAnimation(AnimationUtils.loadAnimation(mController.getContext(),
                R.anim.dialog_exit));
        mIsVisible = false;
    }

    /*
     * Remove the soft keyboard from the screen.
     */
    protected void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)
                mController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
    }

    protected boolean isVisible() {
        return mIsVisible;
    }

    /* package */ void setWebView(WebView webview) {
        mWebView = webview;
    }

    protected void show() {
        startAnimation(AnimationUtils.loadAnimation(mController.getContext(),
            R.anim.dialog_enter));
        mIsVisible = true;
    }

}
