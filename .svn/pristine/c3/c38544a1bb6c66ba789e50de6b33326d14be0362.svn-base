package com.orange.browser;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebIconDatabase;
import android.webkit.WebIconDatabase.IconListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import dep.android.provider.Browser;

import java.util.HashMap;
import java.util.Vector;

public class CombinedBookmarkHistoryActivity extends ActivityGroup
        implements OnClickListener {
    private TextView mBookmark, mRecently, mHistory;
    private BookmarkIndicator mIndicator;
    private ViewGroup mContainer;
    private Intent mBookmarkIntent,mVisitedIntent,mHistoryIntent;
    private LocalActivityManager mLocalActivityManager;
    /**
     * Used to inform BrowserActivity to remove the parent/child relationships
     * from all the tabs.
     */
    private String mExtraData;
    /**
     * Intent to be passed to calling Activity when finished. Keep a pointer to
     * it locally so mExtraData can be added.
     */
    private Intent mResultData;
    /**
     * Result code to pass back to calling Activity when finished.
     */
    private int mResultCode;

    /* package */public static String BOOKMARKS_TAB = "bookmark";
    /* package */static String VISITED_TAB = "visited";
    /* package */static String HISTORY_TAB = "history";
    /* package */public static String STARTING_TAB = "tab";

    static class IconListenerSet implements IconListener {
        // Used to store favicons as we get them from the database
        // FIXME: We use a different method to get the Favicons in
        // BrowserBookmarksAdapter. They should probably be unified.
        private HashMap<String, Bitmap> mUrlsToIcons;
        private Vector<IconListener> mListeners;

        public IconListenerSet() {
            mUrlsToIcons = new HashMap<String, Bitmap>();
            mListeners = new Vector<IconListener>();
        }

        public void onReceivedIcon(String url, Bitmap icon) {
            mUrlsToIcons.put(url, icon);
            for (IconListener listener : mListeners) {
                listener.onReceivedIcon(url, icon);
            }
        }

        public void addListener(IconListener listener) {
            mListeners.add(listener);
        }

        public void removeListener(IconListener listener) {
            mListeners.remove(listener);
        }

        public Bitmap getFavicon(String url) {
            return (Bitmap) mUrlsToIcons.get(url);
        }
    }

    private static IconListenerSet sIconListenerSet;

    static IconListenerSet getIconListenerSet() {
        if (null == sIconListenerSet) {
            sIconListenerSet = new IconListenerSet();
        }
        return sIconListenerSet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combined_bookmark_history_layout);
        mBookmark = (TextView) findViewById(R.id.bookmark);
        mBookmark.setOnClickListener(this);
        mRecently = (TextView) findViewById(R.id.recently);
        mRecently.setOnClickListener(this);
        mHistory = (TextView) findViewById(R.id.history);
        mHistory.setOnClickListener(this);
        mIndicator=(BookmarkIndicator) findViewById(R.id.indicator);
        mContainer = (ViewGroup) findViewById(R.id.container);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        mLocalActivityManager=getLocalActivityManager();

        // getTabHost().setOnTabChangedListener(this);

        Bundle extras = getIntent().getExtras();

        mBookmarkIntent = new Intent(this, BrowserBookmarksPage.class);
        if (extras != null) {
            mBookmarkIntent.putExtras(extras);
        }

        mVisitedIntent = new Intent(this, BrowserBookmarksPage.class);
        // Need to copy extras so the bookmarks activity and this one will be
        // different
        Bundle visitedExtras = extras == null ? new Bundle() : new Bundle(extras);
        visitedExtras.putBoolean("mostVisited", true);
        mVisitedIntent.putExtras(visitedExtras);

     mHistoryIntent = new Intent(this, BrowserHistoryPage.class);
        String defaultTab = null;
        if (extras != null) {
            mHistoryIntent.putExtras(extras);
            defaultTab = extras.getString(STARTING_TAB);
        }
        // Just defaultTab is bookmark or history
        if (defaultTab != null && defaultTab.equals(BOOKMARKS_TAB)) {
            setCurrentTab(0);
        } else {
            setCurrentTab(2);
        }

        // XXX: Must do this before launching the AsyncTask to avoid a
        // potential crash if the icon database has not been created.
        WebIconDatabase.getInstance();
        // Do this every time we launch the activity in case a new favicon was
        // added to the webkit db.
        (new AsyncTask<Void, Void, Void>() {
            public Void doInBackground(Void... v) {
                Browser.requestAllIcons(getContentResolver(),
                        Browser.BookmarkColumns.FAVICON + " is NULL",
                        getIconListenerSet());
                return null;
            }
        }).execute();
    }


    // private void createTab(Intent intent, int labelResId, int iconResId,
    // String tab) {
    // Resources resources = getResources();
    // TabHost tabHost = getTabHost();
    // tabHost.addTab(tabHost.newTabSpec(tab).setIndicator(
    // resources.getText(labelResId), resources.getDrawable(iconResId))
    // .setContent(intent));
    // }
    // Copied from DialTacts Activity
    /** {@inheritDoc} */
    public void onTabChanged(String tabId) {
        Activity activity = getLocalActivityManager().getActivity(tabId);
        if (activity != null) {
            activity.onWindowFocusChanged(true);
        }
    }

    /**
     * Store extra data in the Intent to return to the calling Activity to tell
     * it to clear the parent/child relationships from all tabs.
     */
    /* package */void removeParentChildRelationShips() {
        mExtraData = PreferenceKeys.PREF_PRIVACY_CLEAR_HISTORY;
    }

    /**
     * Custom setResult() method so that the Intent can have extra data attached
     * if necessary.
     * 
     * @param resultCode Uses same codes as Activity.setResult
     * @param data Intent returned to onActivityResult.
     */
    /* package */void setResultFromChild(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }

    @Override
    public void finish() {
        if (mExtraData != null) {
            mResultCode = RESULT_OK;
            if (mResultData == null)
                mResultData = new Intent();
            mResultData.putExtra(Intent.EXTRA_TEXT, mExtraData);
        }
        setResult(mResultCode, mResultData);
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // OverTheTopVisibleSwitch.setOttButtonVisible(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // OverTheTopVisibleSwitch.setOttButtonVisible(this, false);
    }

    private void switchActivity(int buttonid, Intent intent, String tag) {
        // changeButtonColor(buttonid);
        mContainer.removeAllViews();
        Window subActivity = mLocalActivityManager
                    .startActivity(tag, intent);
        mContainer.addView(subActivity.getDecorView(), LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bookmark:
              setCurrentTab(0);
                break;
            case R.id.recently:
                setCurrentTab(1);
                break;
            case R.id.history:
                setCurrentTab(2);
                break;
        }
    }
    private void setCurrentTab(int i) {
        // TODO Auto-generated method stub
        switch(i){
            case 0:
                switchActivity(R.id.bookmark,mBookmarkIntent,"bookmrak");
                break;
            case 1:
                switchActivity(R.id.recently,mVisitedIntent,"recently");
                break;
            case 2:
                switchActivity(R.id.history,mHistoryIntent,"history");
                break;
        }
        mIndicator.setCurrentPostion(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mLocalActivityManager.getCurrentActivity().openOptionsMenu();
        }
        return super.onKeyDown(keyCode, event);
    }
}
