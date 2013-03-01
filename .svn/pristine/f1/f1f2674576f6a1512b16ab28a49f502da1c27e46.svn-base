
package com.orange.browser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.browser.Carousel.OnItemChangedListener;
import com.orange.browser.Carousel.OnScrollingListener;
import com.orange.cygnus.reading.Constants;
import com.orange.cygnus.reading.database.modal.ReadingItemObjectDatabaseModal;

public class BottomBarMenu extends LinearLayout implements OnClickListener {
    private static final int TIME_INTERVAL = 25;
    private static final int HEIGHT_STEP = 50;
    private static final int ACCELERATON_FACTOR = 2;
    private Carousel mCarousel;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private int mVisibleHeight;
    private UiController mUiController;
    private BaseUi mBaseUi;
    ViewGroup mParent;
    private TextView mBookmark, mHistory, mDownload, mShare, mInFullScreen, mSetting, mPageInfo,
            mFind, mSave, mHelp, mExit, mRead, mAddToReadList, mGesture, mGestureSwitcher;
    boolean mIsShown;
    private MenuIndicator mIndicator;
    private Context mContext;
    boolean mAnimating;

    public BottomBarMenu(Context context, UiController controller, BaseUi ui,
            ViewGroup parent) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.browser_menu, this);
        // setOrientation(VERTICAL);
        // int padding =
        // context.getResources().getDimensionPixelSize(R.dimen.bottom_bar_menu_padding);
        // setPadding(padding, padding, padding, 0);
        setWillNotDraw(false);
        mCarousel = (Carousel) findViewById(R.id.carousel);
        mIndicator = (MenuIndicator) findViewById(R.id.menu_indicator);
        mUiController = controller;
        mBaseUi = ui;
        mParent = parent;
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater flater = LayoutInflater.from(context);
        View page_one = flater.inflate(R.layout.menu_page_1, null);
        View page_two = flater.inflate(R.layout.menu_page_2, null);

        mCarousel.addView(page_one);
        mBookmark = (TextView) page_one.findViewById(R.id.menu_bookmark);
        mBookmark.setOnClickListener(this);
        mHistory = (TextView) page_two.findViewById(R.id.menu_history);
        mHistory.setOnClickListener(this);
        mAddToReadList = (TextView) page_one.findViewById(R.id.menu_add_to_readlist);
        mAddToReadList.setOnClickListener(this);
        mShare = (TextView) page_one.findViewById(R.id.menu_share);
        mShare.setOnClickListener(this);
        mInFullScreen = (TextView) page_one.findViewById(R.id.menu_in_fullscreen);
        mInFullScreen.setOnClickListener(this);
        mExit = (TextView) page_one.findViewById(R.id.menu_exit);
        mExit.setOnClickListener(this);
        mDownload = (TextView) page_one.findViewById(R.id.menu_download);
        mDownload.setOnClickListener(this);
        mRead = (TextView) page_one.findViewById(R.id.menu_read);
        mRead.setOnClickListener(this);

        mCarousel.addView(page_two);
        mSetting = (TextView) page_two.findViewById(R.id.menu_setting);
        mSetting.setOnClickListener(this);
        mSave = (TextView) page_two.findViewById(R.id.menu_save);
        mSave.setOnClickListener(this);
        mPageInfo = (TextView) page_two.findViewById(R.id.menu_pageinfo);
        mPageInfo.setOnClickListener(this);
        mHelp = (TextView) page_two.findViewById(R.id.menu_help);
        mHelp.setOnClickListener(this);

        mGesture = (TextView) page_one.findViewById(R.id.menu_gesture);
        mGesture.setOnClickListener(this);

        mGestureSwitcher = (TextView) page_two.findViewById(R.id.menu_gesture_switcher);
        mGestureSwitcher.setOnClickListener(this);

        mFind = (TextView) page_two.findViewById(R.id.menu_find);
        mFind.setOnClickListener(this);
        mCarousel.snapToItemWithoutScrolling(0);
        mCarousel.setScrollingListener(new OnScrollingListener() {

            @Override
            public void onScrolling(float offset) {
                // TODO Auto-generated method stub
                mIndicator.update(offset);
            }
        });
        mCarousel.setOnItemChangedListener(new OnItemChangedListener() {

            @Override
            public void onItemChanged(int screen, int carouselId) {
                // TODO Auto-generated method stub
                mIndicator.stop(screen);
            }
        });
    }

    private void setGestureSwitcherState() {
        if (mUiController.isGestureEnabled()) {
            mGestureSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.menu_gesture_close, 0,
                    0);
            mGestureSwitcher.setText(R.string.close_gesture);
        } else {
            mGestureSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.menu_gesture_open, 0,
                    0);
            mGestureSwitcher.setText(R.string.open_gesture);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(new Rect(0, mMeasuredHeight - mVisibleHeight, mMeasuredWidth,
                mMeasuredHeight));
        super.onDraw(canvas);
    }

    public void show() {
        if (!mAnimating) {
            setGestureSwitcherState();
            mAnimating = true;
            mParent.addView(this, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
            bringToFront();
            postDelayed(mUnveil, TIME_INTERVAL);
            mCarousel.snapToItemWithoutScrolling(0);
            mIndicator.stop(0);
            setButtonEnable(!mBaseUi.isHomePageShowing());
            if (!mBaseUi.isHomePageShowing()) {
                mAddToReadList.setEnabled(!Utils.isInBlackList(mBaseUi.getActivity(),
                        mBaseUi.getCurrentUrl()));
            }
            BrowserSettings settings = BrowserSettings.getInstance();
            mInFullScreen.setCompoundDrawablesWithIntrinsicBounds(null,
                    settings.useFullscreen() ? mContext
                            .getResources().getDrawable(R.drawable.menu_out_fullscreen) : mContext
                            .getResources().getDrawable(R.drawable.menu_in_fullscreen), null, null);
            mInFullScreen.setText(settings.useFullscreen() ? R.string.menu_out_fullscreen
                    : R.string.menu_in_fullscreen);
            invalidate();
        }
        LogHelper.d(LogHelper.TAG_BOTTOMBAR, "BottomBarMenu,init(),mBaseUi.isHomePageShowing(): "
                + mBaseUi.isHomePageShowing());
    }

    public void hide() {
        if (!mAnimating) {
            mAnimating = true;
            setClickable(false);
            postDelayed(mVeil, TIME_INTERVAL);
            invalidate();
        }
    }

    private Runnable mUnveil = new Runnable() {

        @Override
        public void run() {
            int distance = mMeasuredHeight - mVisibleHeight;
            if (distance > HEIGHT_STEP * ACCELERATON_FACTOR) {
                mVisibleHeight += HEIGHT_STEP;
            } else if (distance > ACCELERATON_FACTOR) {
                mVisibleHeight += distance / ACCELERATON_FACTOR;
            } else {
                mVisibleHeight += 1;
            }
            if (mMeasuredHeight > mVisibleHeight) {
                postDelayed(this, TIME_INTERVAL);
            } else {
                mVisibleHeight = mMeasuredHeight;
                mIsShown = true;
                mAnimating = false;
                setClickable(true);
            }
            mBaseUi.mFullscreenSwitcher.setMenuHeight(mVisibleHeight);
            invalidate();
        }
    };

    private Runnable mVeil = new Runnable() {

        @Override
        public void run() {
            if (mVisibleHeight > HEIGHT_STEP * ACCELERATON_FACTOR) {
                mVisibleHeight -= HEIGHT_STEP;
            } else if (mVisibleHeight > ACCELERATON_FACTOR) {
                mVisibleHeight -= mVisibleHeight / ACCELERATON_FACTOR;
            } else {
                mVisibleHeight -= 1;
            }
            if (mVisibleHeight > 0) {
                postDelayed(this, TIME_INTERVAL);
            } else {
                mVisibleHeight = 0;
                mParent.removeView(BottomBarMenu.this);
                mIsShown = false;
                mAnimating = false;
            }
            mBaseUi.mFullscreenSwitcher.setMenuHeight(mVisibleHeight);
            invalidate();
        }
    };

    void setButtonEnable(boolean enabled) {
        mAddToReadList.setEnabled(enabled);
        mShare.setEnabled(enabled);
        // mInFullScreen.setEnabled(enabled);
        mSave.setEnabled(enabled);
        mPageInfo.setEnabled(enabled);
        mFind.setEnabled(enabled);
    }

    @Override
    public boolean isShown() {
        return mIsShown;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_bookmark:
                mUiController.openBookMark();
                break;
            case R.id.menu_history:
                mUiController.openHistory();
                break;
            case R.id.menu_download:
                mUiController.openDownLoadManager();
                break;
            case R.id.menu_share:
                mUiController.sharePage();
                break;
            case R.id.menu_in_fullscreen:
                BrowserSettings settings = BrowserSettings.getInstance();
                settings.setFullscreen(!settings.useFullscreen());
                break;
            case R.id.menu_add_to_readlist:
                addToReadlist();
                break;
            case R.id.menu_setting:
                mUiController.openSetting();
                break;
            case R.id.menu_pageinfo:
                mUiController.showPageInfo();
                break;
            case R.id.menu_find:
                mUiController.openFindDialog();
                break;
            case R.id.menu_save:
                mUiController.savePage();
                break;
            case R.id.menu_help:
                showHelpDialog();
                break;
            case R.id.menu_read:
                Intent intent = new Intent(Constants.ACTION_READING_LIST);

                final ComponentName source =
                        new ComponentName(v.getContext(), BrowserActivity.class);

                intent.putExtra(Constants.EXTRA_LAUNCH_SOURCE, source.flattenToString());
                intent.putExtra(Constants.EXTRA_BROWSER_TASKID, mBaseUi.mActivity.getTaskId());

                mContext.startActivity(intent);
                break;
            case R.id.menu_exit:
                mUiController.exitBrowser();
                break;
            case R.id.menu_gesture:
                Intent intentAct = new Intent();
                intentAct.setClassName(getContext().getPackageName(),
                        GestureHelpActivity.class.getName());
                getContext().startActivity(intentAct);
                break;
            case R.id.menu_gesture_switcher:
                boolean enabled = mUiController.isGestureEnabled();
                mUiController.setGestureEnabled(!enabled);
                setGestureSwitcherState();
                BrowserGlobals.enableLongPress(enabled);
                break;
        }
        hide();
    }

    public void addToReadlist() {
        try {
            if (mBaseUi.isHomePageShowing()) {
                return;
            }

            String currentUrl = mBaseUi.mTabControl.getCurrentTab().getUrl();
            String currentTitle = mBaseUi.mTabControl.getCurrentTab().getTitle();

            if (currentTitle.equals(mContext.getString(R.string.title_bar_loading))) {
                currentTitle = "";
            }

            ReadingItemObjectDatabaseModal readingItemDatabaseModal = new ReadingItemObjectDatabaseModal();
            if (readingItemDatabaseModal.checkUrlExistence(mContext, currentUrl)) {
                Toast.makeText(mContext, mContext.getString(R.string.batch_alreadyadded),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO Auto-generated method stub
            Intent syncIntent = new Intent(Constants.ACTION_SYNC);
            syncIntent.putExtra(Constants.EXTRA_SYNC_NAME, Constants.SYNC_SINGLE_ARTICLE);
            syncIntent.putExtra(Constants.EXTRA_URL, currentUrl);
            mContext.startService(syncIntent);

            Intent i = new Intent(Constants.ACTION_ADD_ITEM);
            i.putExtra(Constants.EXTRA_URL, currentUrl);
            i.putExtra(Constants.EXTRA_TITLE, currentTitle);
            mBaseUi.mActivity.sendBroadcast(i);
            if (currentTitle != null) {
                Toast.makeText(mBaseUi.mActivity,
                        mContext.getString(R.string.batch_addingurl, currentTitle),
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(mBaseUi.mActivity,
                        mContext.getString(R.string.batch_addingurl, currentUrl),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            LogHelper.d(LogHelper.TAG_BOTTOMBAR, "addToReadlist() exception: " + e.toString());
        }

    }

    private void showHelpDialog() {
        String aboutUrl = Utils.parseHomepage(mContext) +
                mContext.getString(R.string.about_url);
        mUiController.loadUrl(mUiController.getCurrentTab(), aboutUrl);
    }
}
