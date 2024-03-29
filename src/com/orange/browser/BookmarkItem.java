package com.orange.browser;

import com.orange.browser.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *  Custom layout for an item representing a bookmark in the browser.
 */
class BookmarkItem extends LinearLayout {

    protected TextView    mTextView;
    protected TextView    mUrlText;
    protected ImageView   mImageView;
    protected String      mUrl;
    protected String      mTitle;

    /**
     *  Instantiate a bookmark item, including a default favicon.
     *
     *  @param context  The application context for the item.
     */
    BookmarkItem(Context context) {
        super(context);

        LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.history_item, this);
        mTextView = (TextView) findViewById(R.id.title);
        mUrlText = (TextView) findViewById(R.id.url);
        mImageView = (ImageView) findViewById(R.id.favicon);
        View star = findViewById(R.id.star);
        star.setVisibility(View.GONE);
    }

    /**
     *  Copy this BookmarkItem to item.
     *  @param item BookmarkItem to receive the info from this BookmarkItem.
     */
    /* package */ void copyTo(BookmarkItem item) {
        item.mTextView.setText(mTextView.getText());
        item.mUrlText.setText(mUrlText.getText());
        item.mImageView.setImageDrawable(mImageView.getDrawable());
    }

    /**
     * Return the name assigned to this bookmark item.
     */
    /* package */ String getName() {
        return mTitle;
    }

    /**
     * Return the TextView which holds the name of this bookmark item.
     */
    /* package */ TextView getNameTextView() {
        return mTextView;
    }

    /* package */ String getUrl() {
        return mUrl;
    }

    /**
     *  Set the favicon for this item.
     *
     *  @param b    The new bitmap for this item.
     *              If it is null, will use the default.
     */
    /* package */ void setFavicon(Bitmap b) {
        if (b != null) {
            mImageView.setImageBitmap(b);
        } else {
            mImageView.setImageResource(R.drawable.bookmark_small);
        }
    }

    /**
     *  Set the new name for the bookmark item.
     *
     *  @param name The new name for the bookmark item.
     */
    /* package */ void setName(String name) {
        if (name == null) {
            return;
        }

        mTitle = name;

        if (name.length() > BrowserSettings.MAX_TEXTVIEW_LEN) {
            name = name.substring(0, BrowserSettings.MAX_TEXTVIEW_LEN);
        }

        mTextView.setText(name);
    }
    
    /**
     *  Set the new url for the bookmark item.
     *  @param url  The new url for the bookmark item.
     */
    /* package */ void setUrl(String url) {
        if (url == null) {
            return;
        }

        mUrl = url;

        if (url.length() > BrowserSettings.MAX_TEXTVIEW_LEN) {
            url = url.substring(0, BrowserSettings.MAX_TEXTVIEW_LEN);
        }

        mUrlText.setText(url);
    }
}
