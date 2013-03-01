
package com.orange.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FunctionMap extends LinearLayout implements OnClickListener {
    private ImageView mScreenshot;
    private WebView mWebView;
    private View mTop, mBottom, mLeft, mRight;
    private UiController mUiController;
    private BaseUi mBaseUi;
    private FrameLayout mParent;
    private Button mHistory, mDownload, mHelp, mSetting, mFind, mChangeMode, mInfo, mAdd, mShare,
            mSave, mBookmark, mRead, mWindow, mQuit;

    public FunctionMap(Context context, UiController controller, BaseUi ui,
            FrameLayout parent) {
        super(context, null);
        mUiController = controller;
        mBaseUi = ui;
        mParent = parent;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.function_map, this);
        mScreenshot = (ImageView) findViewById(R.id.fm_screenshot);
        mScreenshot.setOnClickListener(this);
        mTop = findViewById(R.id.fm_top);
        mBottom = findViewById(R.id.fm_bottom);
        mLeft = findViewById(R.id.fm_left);
        mRight = findViewById(R.id.fm_right);
        mHistory = (Button) findViewById(R.id.fm_bt_history);
        mHistory.setOnClickListener(this);
        mDownload = (Button) findViewById(R.id.fm_bt_dm);
        mDownload.setOnClickListener(this);
        mHelp = (Button) findViewById(R.id.fm_bt_help);
        mHelp.setOnClickListener(this);
        mSetting = (Button) findViewById(R.id.fm_bt_setting);
        mSetting.setOnClickListener(this);
        mFind = (Button) findViewById(R.id.fm_bt_find);
        mFind.setOnClickListener(this);
        mChangeMode = (Button) findViewById(R.id.fm_bt_screen_mode);
        mChangeMode.setOnClickListener(this);
        mInfo = (Button) findViewById(R.id.fm_bt_pageinfo);
        mInfo.setOnClickListener(this);
        mAdd = (Button) findViewById(R.id.fm_bt_add);
        mAdd.setOnClickListener(this);
        mShare = (Button) findViewById(R.id.fm_bt_share);
        mShare.setOnClickListener(this);
        mSave = (Button) findViewById(R.id.fm_bt_save);
        mSave.setOnClickListener(this);
        mBookmark = (Button) findViewById(R.id.fm_bt_bookmark);
        mBookmark.setOnClickListener(this);
        mRead = (Button) findViewById(R.id.fm_bt_readlist);
        mRead.setOnClickListener(this);
        mWindow = (Button) findViewById(R.id.fm_bt_wm);
        mWindow.setOnClickListener(this);
        mQuit = (Button) findViewById(R.id.fm_bt_quit);
        mQuit.setOnClickListener(this);
    }

    public void setBitamp() {
        mWebView = mUiController.getCurrentTopWebView();
        if (mWebView != null) {
            mWebView.buildDrawingCache();
            Bitmap tmp = mWebView.getDrawingCache();
            if (tmp != null) {
                Bitmap result = Bitmap.createScaledBitmap(tmp, tmp.getWidth(),
                        tmp.getHeight(), true);
                mWebView.destroyDrawingCache();
                if (tmp != null && !tmp.isRecycled()) {
                    tmp.recycle();
                }
                mScreenshot.setImageBitmap(result);
            }
        }
        
    }

    void show() {
        setBitamp();
        mParent.addView(this);
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.fm_screenshot_width),(int)getResources().getDimension(R.dimen.fm_screenshot_height));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mScreenshot.setLayoutParams(lp);
        Animation enterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
        mScreenshot.startAnimation(enterAnim);
        mTop.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
        mBottom.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
        mLeft.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
        mRight.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_bt_history:
                mUiController.openHistory();
                break;
            case R.id.fm_bt_dm:
                mUiController.openDownLoadManager();
                break;
            case R.id.fm_bt_help:

                break;
            case R.id.fm_bt_setting:
                mUiController.openSetting();
                break;
            case R.id.fm_bt_find:
                mBaseUi.hideTitleBar();
                mUiController.openFindDialog();
                break;
            case R.id.fm_bt_screen_mode:
                BrowserSettings settings = BrowserSettings.getInstance();
                settings.setFullscreen(!settings.useFullscreen());
//                mUiController.setFullscreen(!mUiController.isFullscreen());
                break;
            case R.id.fm_bt_pageinfo:
                mUiController.showPageInfo();
                break;
            case R.id.fm_bt_add:

                break;
            case R.id.fm_bt_share:
                mUiController.sharePage();
                break;
            case R.id.fm_bt_save:
                mUiController.savePage();
                break;
            case R.id.fm_bt_bookmark:
                mUiController.openBookMark();
                break;
            case R.id.fm_bt_readlist:

                break;
            case R.id.fm_bt_wm:
                mUiController.openWindowManager();
                break;
            case R.id.fm_bt_quit:
                mUiController.exitBrowser();
                break;
            case R.id.fm_screenshot:
                WebView webview=mBaseUi.getWebView();
                if(webview!=null){
                    webview.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_bigger));
                }
                mBaseUi.suggestHideTitleBar();
                break;
        }
    }
}
