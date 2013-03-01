
package com.orange.browser;

import android.content.Context;

public class CloseFullscreenListener extends SwipeFromEdgeListener {
    private static final String TAG = "CloseFullscreenListener";
    private BaseUi mBaseUi;
    private float mSlop;

    public CloseFullscreenListener(Context context, BaseUi baseUi) {
        super(context);
        mBaseUi = baseUi;
        mSlop = context.getResources().getDimension(R.dimen.qc_slop);
        LogHelper.d(LogHelper.TAG_TOUCH,
                "CloseFullscreenListener.CloseFullscreenListener(): mSlop: " + mSlop);
    }

    @Override
    protected void onSwipeFromTopEdge() {
        onQuitFullscreen();
        /**
         * this can't be executed in baseUi's showTitleBar method, because every
         * time load a url,the system should invoke showTitleBar method,which
         * will cause a bug on efficient read tip
         */
        mBaseUi.showTitleBar();
        mBaseUi.onQuitFullScreen();
    }

    protected void onQuitFullscreen() {
    }

}
