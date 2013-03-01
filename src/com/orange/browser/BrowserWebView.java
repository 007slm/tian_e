package com.orange.browser;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import com.orange.browser.R;

import java.util.Map;

/**
 * Manage WebView scroll events
 */
public class BrowserWebView extends WebView {

    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private boolean mBackgroundRemoved = false;
    private TitleBar mTitleBar;
    private OnScrollChangedListener mOnScrollChangedListener;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     * @param javascriptInterfaces
     */
    public BrowserWebView(Context context, AttributeSet attrs, int defStyle,
            Map<String, Object> javascriptInterfaces, boolean privateBrowsing) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BrowserWebView(
            Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle);
        //super(context, attrs, defStyle, privateBrowsing);
    }

    /**
     * @param context
     * @param attrs
     */
    public BrowserWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     */
    public BrowserWebView(Context context) {
        super(context);
    }

    protected int getTitleHeight() {
        return (mTitleBar != null) ? mTitleBar.getEmbeddedHeight() : 0;
    }

//    void hideEmbeddedTitleBar() {
//        scrollBy(0, getVisibleTitleHeight());
//    }

    public void setEmbeddedTitleBar(final View title) {
        mTitleBar = (TitleBar) title;
    }

    public boolean hasTitleBar() {
        return (mTitleBar != null);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        if (!mBackgroundRemoved && getRootView().getBackground() != null) {
            mBackgroundRemoved = true;
            post(new Runnable() {
                public void run() {
                    getRootView().setBackgroundDrawable(null);
                }
            });
        }
    }

    public void drawContent(Canvas c) {
        onDraw(c);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if (!BrowserGlobals.isPlatformHoneycombAndAbove()) {
            if (ev.getPointerCount() > 1) {
                getSettings().setBuiltInZoomControls(true);
            } else {
                getSettings().setBuiltInZoomControls(false);
            }
        }
        try{
            return super.onTouchEvent(ev);
        } catch(NullPointerException e){
            return false;
        }
    }
}
