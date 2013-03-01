
package com.orange.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orange.browser.Controller.OnWebViewChangedListener;
import com.orange.browser.TabControl.OnWindowNumChangedListener;

import java.util.List;



public class BottomBar extends RelativeLayout implements OnClickListener {
    private UiController mUiController;
    private BaseUi mBaseUi;
    private FrameLayout mParent;
    private ImageView mBackward, mForward, mRead, mHome, mWindow, mFunction;
    protected View mBottomBarNav;
//    private TextView mBatchModeCancel, mBatchModeConfirm;
    private boolean mShowing = false;

    public BottomBar(Context context, UiController controller, BaseUi ui,
            FrameLayout parent) {
        super(context, null);
        mUiController = controller;
        mBaseUi = ui;
        mParent = parent;
        mUiController.getTabControl().setOnWindowNumChangedListener(new OnWindowNumChangedListener() {

            @Override
            public void onWindowNumChanged(int num) {
                ((TextImageView)mWindow).updateNum(num);
            }
        });
        mUiController.getTabControl().getController().setWebViewChangedListener(new OnWebViewChangedListener() {
            @Override
                    public void onChanged(Tab tab) {
                        mForward.setEnabled(tab.canGoForward());
                        mBackward.setEnabled(tab.canGoBack());
                        mHome.setEnabled(!mBaseUi.isHomePageShowing());
                    }
        });
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.bottom_bar, this);

        mBottomBarNav = findViewById(R.id.bottom_bar_nav);

        mBackward = (ImageView) findViewById(R.id.bottom_button_backward);
        mForward = (ImageView) findViewById(R.id.bottom_button_forward);
//        mRead = (ImageView) findViewById(R.id.bottom_button_read);
        mHome = (ImageView) findViewById(R.id.bottom_button_home);
        mWindow = (ImageView) findViewById(R.id.bottom_button_window);
        mFunction = (ImageView) findViewById(R.id.bottom_button_function);
//        mBatchModeCancel = (TextView) findViewById(R.id.batch_cancel);
//        mBatchModeConfirm = (TextView) findViewById(R.id.batch_confirm);

//        mBatchModeCancel.setOnClickListener(this);
//        mBatchModeConfirm.setOnClickListener(this);
        mBackward.setOnClickListener(this);
        mForward.setOnClickListener(this);
//        mRead.setOnClickListener(this);
        mHome.setOnClickListener(this);
        mWindow.setOnClickListener(this);
        mFunction.setOnClickListener(this);
    }

    UiController getUiController() {
        return mUiController;
    }

    BaseUi getUi() {
        return mBaseUi;
    }

    void show() {
        if (getParent() == null) {
            mParent.addView(this);
        }
        setVisibility(View.VISIBLE);
        mShowing = true;
    }

    void hide() {
        setVisibility(View.GONE);
        mShowing = false;
    }

    boolean isShowing() {
        return mShowing;
    }


    public void showNavigationBar() {
        mBottomBarNav.setVisibility(View.VISIBLE);
    }

    public void hideNavigationBar() {
        mBottomBarNav.setVisibility(View.GONE);
    }

    public static final String EXTRA_URI_LIST = "URI_LIST";

    @Override
    public void onClick(View v) {
        if(v!=mFunction){
            if(mBaseUi.mMenu.isShown()){
                mBaseUi.mMenu.hide();
            }
        }
        switch (v.getId()) {
            case R.id.bottom_button_backward:
                mUiController.getCurrentTab().goBack();
                break;
            case R.id.bottom_button_forward:
                mUiController.getCurrentTab().goForward();
                break;
//            case R.id.bottom_button_read:
//
//                break;
            case R.id.bottom_button_home:
                if (!mBaseUi.isHomePageShowing()) {
                    mUiController.openTabToHomePage(true);
                }
                break;
            case R.id.bottom_button_window:
//                mBaseUi.mWindowsManager.show();
                mBaseUi.openWindowManager();
                break;
            case R.id.bottom_button_function:
//                mBaseUi.mFunctionMap.show();
                if(mBaseUi.mMenu.isShown()){
                    if (BrowserGlobals.isPlatformHoneycombAndAbove()) {
                        WebView wv = mUiController.getCurrentWebView();
                        if (wv != null && wv.getLayerType() != View.LAYER_TYPE_NONE){
                            wv.setLayerType(View.LAYER_TYPE_NONE, null);
                        }
                    }
                    mBaseUi.mMenu.hide();
                }else{
                    if (BrowserGlobals.isPlatformHoneycombAndAbove() && mBaseUi.isHomePageShowing()) {
                        WebView wv = mUiController.getCurrentWebView();
                        if (wv != null && wv.getLayerType() != View.LAYER_TYPE_SOFTWARE){
                            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        }
                    }
                    mBaseUi.mMenu.show();
                }
                break;
//            case R.id.batch_cancel:
//                Intent intent = new Intent(Constants.ACTION_CLEAR_QUICK_ITEMS);
//                mBaseUi.mActivity.sendBroadcast(intent);
//                resetBatchMode();
//                break;
//            case R.id.batch_confirm:
//                final ArrayList<String> collectionList = mUiController.getUrlCollectionList();
//                if(collectionList.size()==0) {
//                    break;
//                }
//                Intent readingIntent = new Intent(Constants.ACTION_READING_PAGER);
//                readingIntent.putExtra(EXTRA_URI_LIST, collectionList);
//                mBaseUi.mActivity.startActivity(readingIntent);
//
//                resetBatchMode();
//                break;
        }
    }

//    private void resetBatchMode() {
//        mUiController.setCollectorMode(false);
//        mUiController.clearUrlCollectionList();
//        mBaseUi.hideBatchModeTitleBar();
//        mBaseUi.getWebView().loadUrl("javascript:window.deselect()");
//        mBaseUi.showBottomBarexitBatch();
//    }

    public void checkWindowCount(List<Tab> tabs){
        ((TextImageView)mWindow).updateNum(tabs.size());
    }
    public void disableWindowButton(){
        mWindow.setEnabled(false);
    }
    public void enableWindowButton(){
        mWindow.setEnabled(true);
    }

}
