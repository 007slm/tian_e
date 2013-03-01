 
package com.orange.browser;

import com.orange.browser.R;

import dep.android.provider.Browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  Layout representing a history item in the classic history viewer.
 */
/* package */ class HistoryItem extends BookmarkItem {

    private CompoundButton  mStar;      // Star for bookmarking
    private CompoundButton.OnCheckedChangeListener  mListener;
    private Context mContext;
    /**
     *  Create a new HistoryItem.
     *  @param context  Context for this HistoryItem.
     */
    /* package */ HistoryItem(Context context) {
        super(context);
        mContext = context;

        mStar = (CompoundButton) findViewById(R.id.star);
        mStar.setVisibility(View.VISIBLE);
        mListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    Bookmarks.addBookmark(mContext,
                            mContext.getContentResolver(), mUrl, getName(), null, true);
                    LogTag.logBookmarkAdded(mUrl, "history");
                } else {
                    Bookmarks.removeFromBookmarks(mContext,
                            mContext.getContentResolver(), mUrl, getName());
                }
            }
        };
    }
    
    /* package */ void copyTo(HistoryItem item) {
        item.mTextView.setText(mTextView.getText());
        item.mUrlText.setText(mUrlText.getText());
        // No star for UI to coincide with other context UI like most visited.
        // Or else, we should do more to avoid crash.
        //item.setIsBookmark(mStar.isChecked());
        item.mStar.setVisibility(View.GONE);
        item.mImageView.setImageDrawable(mImageView.getDrawable());
    }

    /**
     * Whether or not this item represents a bookmarked site
     */
    /* package */ boolean isBookmark() {
        return mStar.isChecked();
    }

    /**
     *  Set whether or not this represents a bookmark, and make sure the star
     *  behaves appropriately.
     */
    /* package */ void setIsBookmark(boolean isBookmark) {
        mStar.setOnCheckedChangeListener(null);
        mStar.setChecked(isBookmark);
        mStar.setOnCheckedChangeListener(mListener);
    }
}
