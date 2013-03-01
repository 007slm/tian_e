
package com.orange.browser;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SwipeFromEdgeListener implements View.OnTouchListener {
    private static final String TAG = "SwipeEdgeListener";
    private float mSlop;
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean mStartSwipeTop;
    private boolean mStartSwipeLeft;
    private boolean mStartSwipeRight;
    private int mScreenWidth;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public SwipeFromEdgeListener(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            mScreenWidth = size.x;
        } else {
            mScreenWidth = display.getWidth();
        }
        mSlop = context.getResources().getDimension(R.dimen.qc_slop);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y < mSlop) {
                    mStartSwipeTop = true;
                    mTouchStartY = y;
                    return true;
                } else if (x < mSlop) {
                    mStartSwipeLeft = true;
                    mTouchStartX = x;
                    return true;
                } else if (x > (mScreenWidth - mSlop)) {
                    mStartSwipeRight = true;
                    mTouchStartX = x;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartSwipeTop) {
                    float dy = y - mTouchStartY;
                    if (dy > mSlop) {
                        mStartSwipeTop = false;
                        onSwipeFromTopEdge();
                    }
                    return true;
                } else if (mStartSwipeLeft) {
                    float dx = x - mTouchStartX;
                    if (dx > mSlop) {
                        mStartSwipeLeft = false;
                        onSwipeFromLeftEdge();
                    }
                    return true;
                } else if (mStartSwipeRight) {
                    float dx = mTouchStartX;
                    if (dx > mSlop) {
                        mStartSwipeRight = false;
                        onSwipeFromRightEdge();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mStartSwipeTop) {
                    mStartSwipeTop = false;
                    return true;
                } else if (mStartSwipeLeft) {
                    mStartSwipeLeft = false;
                    return true;
                } else if (mStartSwipeRight) {
                    mStartSwipeRight = false;
                    return true;
                }
        }
        return false;
    }

    protected void onSwipeFromRightEdge() {
    }

    protected void onSwipeFromLeftEdge() {
    }

    protected void onSwipeFromTopEdge() {
    }

}
