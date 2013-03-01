package com.orange.browser;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dep.android.net.ParseException;
import dep.android.net.WebAddress;

import java.net.URI;
import java.net.URISyntaxException;

public class AddBookmarkPage extends Activity {

	public static final String TOUCH_ICON_URL = "touch_icon_url";
    public static final int SAVE_BOOKMARK_SUCCESSFUL = 1;
    public static final int SAVE_BOOKMARK_FAIL = 0;
    public static final int UPDATE_BOOKMARK_SUCCESSFUL = 2;


    private EditText    mTitle;
    private EditText    mAddress;
    private TextView    mButton;
    private View        mCancelButton;
    private boolean     mEditingExisting;
    private Bundle      mMap;
    private String      mTouchIconUrl;
    private Bitmap      mThumbnail;
    private String      mOriginalUrl;

    // Message IDs
    private static final int SAVE_BOOKMARK = 100;

    private Handler mHandler;

    private View.OnClickListener mSaveBookmark = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (save()) {
                finish();
            }
        }
    };

    private View.OnClickListener mCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.browser_add_bookmark);
        setTitle(R.string.save_to_bookmarks);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.add_to_bookmrak_icon);
        TypedArray a = getTheme().obtainStyledAttributes(new int[] {R.attr.colorFocused});
        getWindow().setTitleColor(a.getColor(0, 0));
        a.recycle();
        getWindow().setBackgroundDrawableResource(R.drawable.panel_background);
        String title = null;
        String url = null;
        mMap = getIntent().getExtras();
        if (mMap != null) {
            Bundle b = mMap.getBundle("bookmark");
            if (b != null) {
                mMap = b;
                mEditingExisting = true;
                setTitle(R.string.edit_bookmark);
            }
            title = mMap.getString("title");
            url = mOriginalUrl = mMap.getString("url");
            mTouchIconUrl = mMap.getString(TOUCH_ICON_URL);
            mThumbnail = (Bitmap) mMap.getParcelable("thumbnail");
        }

        mTitle = (EditText) findViewById(R.id.title);
        mTitle.setText(title);
        mAddress = (EditText) findViewById(R.id.address);
        mAddress.setText(url);

        View.OnClickListener accept = mSaveBookmark;
        mButton = (TextView) findViewById(R.id.OK);
        mButton.setOnClickListener(accept);

        mCancelButton = findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(mCancel);

        if (!getWindow().getDecorView().isInTouchMode()) {
            mButton.requestFocus();
        }
    }

    /**
     * Runnable to save a bookmark, so it can be performed in its own thread.
     */
    private class SaveBookmarkRunnable implements Runnable {
        private Message mMessage;
        private Context mContext;
        public SaveBookmarkRunnable(Context ctx, Message msg) {
        	mContext = ctx.getApplicationContext();
            mMessage = msg;
        }
        @Override
        public void run() {
            // Unbundle bookmark data.
            Bundle bundle = mMessage.getData();
            String title = bundle.getString("title");
            String url = bundle.getString("url");
            boolean invalidateThumbnail = bundle.getBoolean(
                    "invalidateThumbnail");
            Bitmap thumbnail = invalidateThumbnail ? null
                    : (Bitmap) bundle.getParcelable("thumbnail");
            String touchIconUrl = bundle.getString(TOUCH_ICON_URL);

            // Save to the bookmarks DB.
            try {
                final ContentResolver cr = getContentResolver();
                boolean updateBookmark = Bookmarks.addBookmark(null, cr, url, title, thumbnail,
                        true);
                if (touchIconUrl != null) {
                    new DownloadTouchIcon(mContext, cr, url).execute(mTouchIconUrl);
                }
                if (updateBookmark) {
                    mMessage.arg1 = UPDATE_BOOKMARK_SUCCESSFUL;
                } else {
                    mMessage.arg1 = SAVE_BOOKMARK_SUCCESSFUL;
                }
            } catch (IllegalStateException e) {
                mMessage.arg1 = SAVE_BOOKMARK_FAIL;
            }
            mMessage.sendToTarget();
        }
    }

    private void createHandler() {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SAVE_BOOKMARK:
                            if (SAVE_BOOKMARK_SUCCESSFUL == msg.arg1) {
                                Toast.makeText(AddBookmarkPage.this, R.string.bookmark_saved,
                                        Toast.LENGTH_LONG).show();
                            } else if (UPDATE_BOOKMARK_SUCCESSFUL == msg.arg1) {
                                Toast.makeText(AddBookmarkPage.this, R.string.bookmark_updated,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(AddBookmarkPage.this, R.string.bookmark_not_saved,
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }
            };
        }
    }

    /**
     * Parse the data entered in the dialog and post a message to update the bookmarks database.
     */
    boolean save() {
        createHandler();

        String title = mTitle.getText().toString().trim();
        String unfilteredUrl =
        	UrlUtils.fixUrl(mAddress.getText().toString());
        boolean emptyTitle = title.length() == 0;
        boolean emptyUrl = unfilteredUrl.trim().length() == 0;
        Resources r = getResources();
        if (emptyTitle || emptyUrl) {
            if (emptyTitle) {
                mTitle.setError(r.getText(R.string.bookmark_needs_title));
            }
            if (emptyUrl) {
                mAddress.setError(r.getText(R.string.bookmark_needs_url));
            }
            return false;
        }
        String url = unfilteredUrl.trim();
        try {
            // We allow bookmarks with a javascript: scheme, but these will in most cases
            // fail URI parsing, so don't try it if that's the kind of bookmark we have.

            if (!url.toLowerCase().startsWith("javascript:")) {
                URI uriObj = new URI(url);
                String scheme = uriObj.getScheme();
                if (!Bookmarks.urlHasAcceptableScheme(url)) {
                    // If the scheme was non-null, let the user know that we
                    // can't save their bookmark. If it was null, we'll assume
                    // they meant http when we parse it in the WebAddress class.
                    if (scheme != null) {
                        mAddress.setError(r.getText(R.string.bookmark_cannot_save_url));
                        return false;
                    }
                    WebAddress address;
                    try {
                        address = new WebAddress(unfilteredUrl);
                    } catch (ParseException e) {
                        throw new URISyntaxException("", "");
                    }
                    if (address.mHost.length() == 0) {
                        throw new URISyntaxException("", "");
                    }
                    url = address.toString();
                }
            }
        } catch (URISyntaxException e) {
            mAddress.setError(r.getText(R.string.bookmark_url_not_valid));
            return false;
        }

        if (mEditingExisting) {
            mMap.putString("title", title);
            mMap.putString("url", url);
            mMap.putBoolean("invalidateThumbnail", !url.equals(mOriginalUrl));
            setResult(RESULT_OK, (new Intent()).setAction(
                    getIntent().toString()).putExtras(mMap));
        } else {
            // Post a message to write to the DB.
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("url", url);
            bundle.putParcelable("thumbnail", mThumbnail);
            bundle.putBoolean("invalidateThumbnail", !url.equals(mOriginalUrl));
            bundle.putString(TOUCH_ICON_URL, mTouchIconUrl);
            Message msg = Message.obtain(mHandler, SAVE_BOOKMARK);
            msg.setData(bundle);
            // Start a new thread so as to not slow down the UI
            Thread t = new Thread(new SaveBookmarkRunnable(getApplicationContext(), msg));
            t.start();
            setResult(RESULT_OK);
            LogTag.logBookmarkAdded(url, "bookmarkview");
        }
        return true;
    }

	@Override
	protected void onPause() {
		super.onPause();
		//OverTheTopVisibleSwitch.setOttButtonVisible(this, true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//OverTheTopVisibleSwitch.setOttButtonVisible(this, false);
	}
}
