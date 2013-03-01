package com.orange.browser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orange.cygnus.reading.Constants;
import com.orange.cygnus.reading.UrlInfo;

import java.util.HashMap;
import java.util.List;

public class TitleBar extends RelativeLayout  implements OnClickListener{

    private static final int PROGRESS_MAX = 100;

    private UiController mUiController;
    private BaseUi mBaseUi;
    private FrameLayout mParent;
    private PageProgressView mProgress;

    private NavigationBarBase mNavBar;
    private View mBatchModeTitleBar;

    private boolean mUseQuickControls;

    // state
    private boolean mShowing;
    private boolean mInLoad;
    private boolean mSkipTitleBarAnimations;
//    Animation mEnterAnimation;
//    Animation mExitAnimation;

    private TextView mSelectedTextView;
    //private LinearLayout mConfirmLayout;
    //private TextView mConfirmTextView;
    private ImageView mConfirmBtn;

    public TitleBar(Context context, UiController controller, BaseUi ui,
            FrameLayout parent) {
        super(context, null);
        mUiController = controller;
        mBaseUi = ui;
        mParent = parent;
//        mEnterAnimation = AnimationUtils.loadAnimation(context, R.anim.title_bar_enter);
//        mExitAnimation = AnimationUtils.loadAnimation(context, R.anim.title_bar_exit);
//        addExitAnimationListener();
        initLayout(context);
    }

//    private void addExitAnimationListener(){
//        mExitAnimation.setAnimationListener(new AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // TODO Auto-generated method stub
//                TitleBar.this.setVisibility(View.GONE);
//            }
//        });
//    }

    private void initLayout(Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.title_bar, this);
        mProgress = (PageProgressView) findViewById(R.id.progress);
        mNavBar = (NavigationBarBase) findViewById(R.id.taburlbar);
        mNavBar.setTitleBar(this);
        mNavBar.updateBookmarkIcons();
        mBatchModeTitleBar = findViewById(R.id.batchmode_title_bar);
        mSelectedTextView = (TextView) findViewById(R.id.already_text_line2);
        //mConfirmLayout = (LinearLayout) this.findViewById(R.id.confirm_layout);
        //mConfirmTextView = (TextView) this.findViewById(R.id.confirm_read);
        mConfirmBtn = (ImageView) findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(this);
        setConfirmText(false);
    }

    public BaseUi getUi() {
        return mBaseUi;
    }

    public UiController getUiController() {
        return mUiController;
    }

    void setShowProgressOnly(boolean progress) {
        if (progress && !wantsToBeVisible()) {
            mNavBar.setVisibility(View.GONE);
        } else {
            mNavBar.setVisibility(View.VISIBLE);
        }
    }

    void setSkipTitleBarAnimations(boolean skip) {
        mSkipTitleBarAnimations = skip;
    }


    void show() {
        // TODO: add animation
        if (getParent() == null) {
            mParent.addView(this);
        }
//        this.startAnimation(mEnterAnimation);
        setVisibility(View.VISIBLE);
        mShowing = true;
    }

    void hide() {
//        this.startAnimation(mExitAnimation);
        // TODO: add animation
        setVisibility(View.GONE);
        mShowing = false;
    }

    boolean isShowing() {
        return mShowing;
    }

    public void showBatchModeTitleBar() {
        mBatchModeTitleBar.setVisibility(View.VISIBLE);
    }

    public void hideBatchModeTitleBar() {
        mBatchModeTitleBar.setVisibility(View.GONE);
    }

    public void showNavigationBar() {
        mNavBar.setVisibility(View.VISIBLE);
    }

    public void hideNavigationBar() {
        mNavBar.setVisibility(View.GONE);
    }

    void hideReadingBar(){
        mNavBar.hideReadingBar();
    }
    /**
     * Update the progress, from 0 to 100.
     */
    public void setProgress(int newProgress) {
        if (newProgress >= PROGRESS_MAX) {
            mProgress.setProgress(PageProgressView.MAX_PROGRESS);
            mProgress.setVisibility(View.GONE);
            mInLoad = false;
            mNavBar.onProgressStopped();
            // check if needs to be hidden
            if (!isEditingUrl() && !wantsToBeVisible()) {
//                hide();
//                mBaseUi.hideTitleBottomBar();
                if (mUseQuickControls) {
                    setShowProgressOnly(false);
                }
            }
        } else {
            if (!mInLoad) {
                mProgress.setVisibility(View.VISIBLE);
                mInLoad = true;
                mNavBar.onProgressStarted();
            }
            mProgress.setProgress(newProgress * PageProgressView.MAX_PROGRESS
                    / PROGRESS_MAX);
            if (!mShowing) {
                if (mUseQuickControls && !isEditingUrl()) {
                    setShowProgressOnly(true);
                }
//                show();
//                mBaseUi.showTitleBar();
            }
            mNavBar.showOrHideBookmarkIconsAccoringToProgress();
        }
    }

    public void setSelectedTextView(int selectedCount) {
        String value = selectedCount+"";
        if(selectedCount > 0){
            if(1 == selectedCount){
                mSelectedTextView.setText(mUiController.getActivity().getResources().getString(R.string.one_links_downloading, value));
            }else{
                mSelectedTextView.setText(mUiController.getActivity().getResources().getString(R.string.more_than_one_links_downloading, value));
            }
            
        }else{
            mSelectedTextView.setText(mUiController.getActivity().getResources().getString(R.string.add_to_readinglist_hint));
        }
        mBaseUi.onSelectArticlesNumChange(selectedCount);
    }

    public void setConfirmText(Boolean isChangetoConfirm) {
        if(isChangetoConfirm) {
            //mConfirmTextView.setText(this.getResources().getString(R.string.actbar_normal_confirm));
            //mConfirmBtn.setVisibility(View.VISIBLE);
            mConfirmBtn.setClickable(true);
            mConfirmBtn.setEnabled(true);
        } else {
            //mConfirmTextView.setText(this.getResources().getString(R.string.actbar_normal_unconfirm));
            //mConfirmBtn.setVisibility(View.GONE);
            mConfirmBtn.setClickable(false);
            mConfirmBtn.setEnabled(false);
        }

    }


    public int getEmbeddedHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean wantsToBeVisible() {
        // return inAutoLogin()
        // || (mSnapshotBar != null && mSnapshotBar.getVisibility() ==
        // View.VISIBLE
        // && mSnapshotBar.isAnimating());
        return false;
    }

    public boolean isEditingUrl() {
        return mNavBar.isEditingUrl();
    }

    public PageProgressView getProgressView() {
        return mProgress;
    }

    public NavigationBarBase getNavigationBar() {
        return mNavBar;
    }

    public boolean useQuickControls() {
        return mUseQuickControls;
    }

    public boolean isInLoad() {
        return mInLoad;
    }

    public void onTabDataChanged(Tab tab) {
    }

    public void onPause(){
        mNavBar.onPause();
    }


    public static final String EXTRA_URI_LIST = "URI_LIST";
    private static final String URL_KEY = "url";
    private static final String TITLE_KEY = "title";
    @Override
    public void onClick(View v) {
        BrowserAssociatedHelp.setIfAlreadyUseEfficientRead(mUiController.getActivity(), true);
        if (false) {
        if (v.getId() == R.id.confirm_btn) {

            final List<HashMap<String, String>> quickAddArticles = mUiController
                    .getUrlCollectionList();

            final int size = quickAddArticles.size();
            if (size == 0) {
                return;
            }
            String[] articleTitles = new String[size];

            UrlInfo[] urlInfoArray = new UrlInfo[size];
            for (int i = 0; i < size; i++) {
                HashMap<String, String> map = quickAddArticles.get(i);
                String url = map.get(URL_KEY);
                String title = map.get(TITLE_KEY);
                UrlInfo urlInfo = new UrlInfo(url, title);
                // reading list need reverse order
                urlInfoArray[size - 1 - i] = urlInfo;

                if (title != null) {
                    articleTitles[i] = title;
                } else {
                    articleTitles[i] = url;
                }
            }

            Gson gson = new Gson();
            String urlInfos = gson.toJson(urlInfoArray);
            Intent i = new Intent(Constants.ACTION_ADD_ITEMS);
            i.putExtra(Constants.EXTRA_URL_INFOS, urlInfos);
            mBaseUi.mActivity.sendBroadcast(i);

            final CustomDialog dialog = new CustomDialog(mBaseUi.mActivity);
            // ListView booksToAdd = new ListView(mBaseUi.mActivity);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mBaseUi.mActivity,
                   R.layout.readingmode_confirmdialog_item, articleTitles);
            // booksToAdd.setAdapter(adapter);
            // booksToAdd.setLayoutParams(new
            // LayoutParams(LayoutParams.FILL_PARENT,300));
            // bDialog.setCustomView(booksToAdd);
            dialog.setTitle(R.string.readingmode_confirmdialog);
            dialog.setItems(adapter, null);
            ListView items = dialog.getItems();
            items.setItemsCanFocus(false);
            items.setSelector(android.R.color.transparent);
            items.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 300));
            dialog.setConfirmButton(R.string.confirm_launch_readingmode, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent readingIntent = new Intent(Constants.ACTION_VIEW_ARTICLES);
                    final ComponentName source =
                            new ComponentName(mBaseUi.mActivity, BrowserActivity.class);
                    readingIntent.putExtra(Constants.EXTRA_LAUNCH_SOURCE,
                            source.flattenToString());
                    readingIntent.putExtra(Constants.EXTRA_BROWSER_TASKID,
                            mBaseUi.mActivity.getTaskId());
                    mBaseUi.mActivity.startActivity(readingIntent);

                    mUiController.cancelBatchMode();
                    dialog.dismiss();
                }
            });
            dialog.setCancleButton(R.string.cancel_launch_readingmode, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mUiController.cancelBatchMode();
                    dialog.dismiss();
                }

            });
            dialog.show();
            mBaseUi.confirmBtnClick();
        }
        }

        if(v.getId() == R.id.confirm_btn) {
            final List<HashMap<String,String>> collectionList = mUiController.getUrlCollectionList();
            if(collectionList.size()==0) {
                return;
            }

            int size = collectionList.size();
            UrlInfo[] sUrls = new UrlInfo[size];
            for(int i = size-1; i >= 0; i--) {
                HashMap<String, String> map = collectionList.get(i);
                String url = map.get(URL_KEY);
                String title = map.get(TITLE_KEY);
                UrlInfo urlInfo = new UrlInfo(url, title);
                sUrls[size-1-i] = urlInfo;
//                Intent addItemIntent = new Intent(Constants.ACTION_ADD_ITEM);
//                addItemIntent.putExtra(Constants.EXTRA_TITLE, title);
//                addItemIntent.putExtra(Constants.EXTRA_URL, url);
//                mBaseUi.mActivity.sendBroadcast(addItemIntent);
            }

            Gson gson = new Gson();
            String urlInfos = gson.toJson(sUrls);
            Intent i = new Intent(Constants.ACTION_ADD_ITEMS);
            i.putExtra(Constants.EXTRA_URL_INFOS, urlInfos);
            mBaseUi.mActivity.sendBroadcast(i);


            Intent readingIntent = new Intent(Constants.ACTION_VIEW_ARTICLES);

            final ComponentName source =
                new ComponentName(mBaseUi.mActivity, BrowserActivity.class);

            readingIntent.putExtra(Constants.EXTRA_LAUNCH_SOURCE,
                    source.flattenToString());
            readingIntent.putExtra(Constants.EXTRA_BROWSER_TASKID, mBaseUi.mActivity.getTaskId());

            mBaseUi.mActivity.startActivity(readingIntent);

            mUiController.cancelBatchMode();

            mBaseUi.confirmBtnClick();
        }


    }


}
