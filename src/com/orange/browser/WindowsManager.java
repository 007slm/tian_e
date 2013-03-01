
package com.orange.browser;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.browser.OGallery.OnItemChangedListener;
import com.orange.browser.OGallery.OnItemClickListener;
import com.orange.browser.OGallery.OnStartMovingListener;

public class WindowsManager extends FrameLayout implements OnClickListener {
    private ImageView mNew;
    private TextView mCurrentWindowTitle;
    private TextView mCurrentWindowUrl;
    private OGallery mWindowGallery;
    private UiController mUiController;
    private BaseUi mBaseUi;
    private FrameLayout mParent;
    private WindowIndicator mDot;
    private TabControl mTabControl;
    private HashMap<Tab, Bitmap> mBitmapCache;
    public boolean mWindowManagerIsShowing;
    public static final int ENABLE_ADD_BUTTON=1;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(msg.what==ENABLE_ADD_BUTTON){
                mNew.setEnabled(true);
            }else{
                mBaseUi.mBottomBar.enableWindowButton();
            }
        }

    };

    public WindowsManager(Context context, UiController controller, BaseUi ui,
            FrameLayout parent) {
        super(context);
        mUiController = controller;
        mBaseUi = ui;
        mParent = parent;
        mTabControl = mUiController.getTabControl();
        mBitmapCache = new HashMap<Tab, Bitmap>();
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.window_manager, this);
        mNew = (ImageView) findViewById(R.id.wm_new);
        mNew.setOnClickListener(this);
        mCurrentWindowTitle = (TextView) findViewById(R.id.wm_title);
        mCurrentWindowUrl = (TextView) findViewById(R.id.wm_url);
        mDot = (WindowIndicator) findViewById(R.id.wm_dot);
        mWindowGallery = (OGallery) findViewById(R.id.wm_gallery);
        mWindowGallery.setOnItemChangedListener(new
                OnItemChangedListener() {

                    @Override
                    public void onItemChanged(int currentItem) {
                        for (int i = 0; i < mWindowGallery.getChildCount(); i++) {
                            OWindow window = (OWindow) mWindowGallery.getChildAt(i);
                            if (currentItem == i) {
                                window.setCloseBtVisibility(View.VISIBLE);
                                mCurrentWindowTitle.setText(window.getTitle());
                                mCurrentWindowUrl.setText(window.getUrl());
                                window.setAlpha(255);
                            } else {
                                window.setCloseBtVisibility(View.INVISIBLE);
                                window.setAlpha(0);
                            }
                        }
                        if (mWindowGallery.getChildCount() != 0) {
                            mDot.setCurrentPosition(mWindowGallery
                                    .getChildCount(), currentItem);
                        }
                    }
                });
//        mWindowGallery.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(int currentItem) {
//                mUiController.switchToTab(mUiController.getTabControl().getTab(currentItem));
//                hidden();
//            }
//        });
        mWindowGallery.setOnStartMovingListener(new
                OnStartMovingListener() {

                    @Override
                    public void onStartMoving(int XDiff, int currentItem) {
                        int nextitem = XDiff > 0 ? currentItem + 1 : currentItem - 1;
                        int ap = Math.abs(XDiff) * 255 / mWindowGallery.getItemWidth();
                        if (ap > 255) {
                            ap = 255;
                        }
                        try {
                            ((OWindow) (mWindowGallery.getChildAt(currentItem))).setAlpha(255
                                    - ap);
                            ((OWindow) (mWindowGallery.getChildAt(nextitem))).setAlpha(ap);
                            ((OWindow) (mWindowGallery.getChildAt(nextitem)))
                                    .setCloseBtVisibility(View.VISIBLE);
                        } catch (Exception e) {
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wm_new:
                mUiController.openTabToHomePage(false);
                break;
        }
        mBaseUi.backToMainPage();
    }

    void exitBrowser(final OWindow window,final Tab tab){
        final CustomDialog builder=new CustomDialog(mUiController.getActivity());
        builder.setIcon(R.mipmap.ic_launcher_browser);
        builder.setTitle(R.string.exit_browser_title);
        builder.setMessage(R.string.exit_browser_msg);
        builder.setConfirmButton(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                cleanup(window,tab);
                mUiController.getActivity().finish();
                builder.dismiss();
            }
        });
        builder.setCancleButton(null);
        builder.show();
    }
    
    void cleanup(OWindow window,Tab tab){
        int index = mWindowGallery.getCurrentItem();
        window.setBitmap(null);
        mWindowGallery.removeViewAt(index);
        removeFromCache(tab);
        mUiController.closeTab(tab);
        for(int i=index;i<mWindowGallery.getChildCount();i++){
            ((OWindow)mWindowGallery.getChildAt(i)).setIndex(i);
        }
    }
    void show() {
        mNew.setEnabled(false);
        if(mParent.indexOfChild(this)==-1){
        mParent.addView(this);
        }
        mParent.setVisibility(View.VISIBLE);
        mBaseUi.mWebViewContainer.setVisibility(View.GONE);
        int thumbnailWidth=(int) (mBaseUi.mContentView.getWidth()*0.65f);
        int thumbnailHeight=(int) (mBaseUi.mContentView.getHeight()*0.65f);
        int tabcount = mTabControl.getTabCount();
        for (int i = 0; i < tabcount; i++) {
            final Tab tab = mTabControl.getTab(i);
            final OWindow window = new OWindow(getContext());
            window.setTab(tab);
            Bitmap bitmap = mBitmapCache.get(tab);
            if (bitmap == null || tab == mTabControl.getCurrentTab()
                    || tab == mTabControl.getCurrentTab().getParent() ) {
                if (bitmap != null ) {
                    Utils.recycleBitmap(bitmap);
                }
                bitmap =Utils.createScreenshot(tab.getWebView(),thumbnailWidth,thumbnailHeight);
                mBitmapCache.put(tab, bitmap);
            }
            window.setBitmap(bitmap);
            window.getCloseBt().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTabControl.getTabCount() == 1) {
                        exitBrowser(window,tab);
                    }else{
                        int index = mWindowGallery.getCurrentItem();
                        window.setBitmap(null);
                        mWindowGallery.removeViewAt(index);
                        removeFromCache(tab);
                        mUiController.closeTab(tab);
                        updateWindowIndex(index);
                    }
                   
                }

                private void updateWindowIndex(int index) {
                    for(int i=index;i<mWindowGallery.getChildCount();i++){
                        ((OWindow)mWindowGallery.getChildAt(i)).setIndex(i);
                    }
                }
            });
            window.getThumnail().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = mWindowGallery.getCurrentItem();
                    if(index==window.getIndex()){
                        mUiController.switchToTab(mUiController.getTabControl().getTab(index));
//                        hidden();
                        mBaseUi.backToMainPage();
                    }else{
                        mWindowGallery.snapToScreen(window.getIndex());
                    }
                }
            });
            window.setIndex(i);
            mWindowGallery.addView(window);
        }
        mWindowGallery.setSelection(mTabControl.getCurrentPosition());
        final ImageView view = (ImageView) findViewById(R.id.animation_view);
        LayoutParams params=new LayoutParams((int)(mBaseUi.mWebViewContainer.getWidth()*0.65f), (int)(mBaseUi.mWebViewContainer.getHeight()*0.6f));
        params.gravity=Gravity.CENTER;
        view.setLayoutParams(params);
        view.setImageBitmap(mBitmapCache.get(mTabControl.getCurrentTab()));
        view.setVisibility(View.VISIBLE);
        Animation enter = MyAnimation.enterWindowManager(getContext(), mBaseUi.mWebViewContainer,
                mBaseUi.mContentView);
        view.startAnimation(enter);
        enter.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                view.setImageBitmap(null);
                mHandler.sendEmptyMessageDelayed(ENABLE_ADD_BUTTON, 500);
            }
        });
        mWindowManagerIsShowing=true;
    }

    void hidden() {
        // mParent.removeView(this);
        mParent.setVisibility(View.GONE);
        mBaseUi.mWebViewContainer.setVisibility(View.VISIBLE);
        mBaseUi.mBottomBar.disableWindowButton();
        final ImageView view = mBaseUi.mMainPageAnimationView;
        final WebView webview = mBaseUi.getWebView();
        try {
            view.setImageBitmap(mBitmapCache.get(mTabControl.getCurrentTab()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        webview.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        Animation enter = MyAnimation.exitWindowManager(getContext(), mBaseUi.mWebViewContainer,
                mBaseUi.mContentView);
        view.startAnimation(enter);
        enter.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                view.setImageBitmap(null);
                webview.setVisibility(View.VISIBLE);
                mBaseUi.suggestHideTitleBar();
                mHandler.sendEmptyMessageDelayed(0, 500);
            }
        });
        for(int i=0;i<mWindowGallery.getChildCount();i++){
            ((OWindow)mWindowGallery.getChildAt(i)).setBitmap(null);
        }
        mWindowGallery.removeAllViews();
        mWindowManagerIsShowing = false;
    }

    public void destroyBitmapCache() {
        if (mBitmapCache != null) {
            for (Bitmap bitmap : mBitmapCache.values()) {
                if (bitmap != null)
                    bitmap.recycle();
            }
            mBitmapCache.clear();
            mBitmapCache = null;
        }
    }

    private void removeFromCache(Tab tab) {
        Utils.recycleBitmap(mBitmapCache.get(tab));
        mBitmapCache.remove(tab);
    }
}
