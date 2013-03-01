
package com.orange.browser;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.orange.browser.R;

import dep.android.provider.Browser;
import dep.android.provider.Browser.BookmarkColumns;
import dep.android.provider.BrowserContract;
import dep.android.provider.BrowserContract.History;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataController {
    private static final String LOGTAG = "DataController";
    // Message IDs
    private static final int HISTORY_UPDATE_VISITED = 100;
    private static final int HISTORY_UPDATE_TITLE = 101;
    private static final int UPDATE_TO_HOMELINK = 102;
    private static final int REMOVE_FROM_HOMELINK = 103;
    private static final int EDIT_HOMELINK = 104;
    private static final int ADD_TO_HOMELINK = 105;
    public static final int QUERY_URL_IS_BOOKMARK = 200;
    
    public static final int BOOKMARK_UPDATE_FAVICON = 300;
    private static DataController sInstance;

    private Context mContext;
    private DataControllerHandler mDataHandler;
    private Handler mCbHandler; // To respond on the UI thread

    /* package */ static interface OnQueryUrlIsBookmark {
        void onQueryUrlIsBookmark(String url, boolean isBookmark);
    }
    private static class CallbackContainer {
        Object replyTo;
        Object[] args;
    }

    private static class DCMessage {
        int what;
        Object obj;
        Object replyTo;
        DCMessage(int w, Object o) {
            what = w;
            obj = o;
        }
    }

    /* package */ static DataController getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new DataController(c);
        }
        return sInstance;
    }

    private DataController(Context c) {
        mContext = c.getApplicationContext();
        mDataHandler = new DataControllerHandler();
        mDataHandler.setDaemon(true);
        mDataHandler.start();
        mCbHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                CallbackContainer cc = (CallbackContainer) msg.obj;
                switch (msg.what) {
                    case QUERY_URL_IS_BOOKMARK: {
                        OnQueryUrlIsBookmark cb = (OnQueryUrlIsBookmark) cc.replyTo;
                        String url = (String) cc.args[0];
                        boolean isBookmark = (Boolean) cc.args[1];
                        cb.onQueryUrlIsBookmark(url, isBookmark);
                        break;
                    }
                }
            }
        };
    }

    public void updateVisitedHistory(String url) {
        mDataHandler.sendMessage(HISTORY_UPDATE_VISITED, url);
    }

    public void updateHistoryTitle(String url, String title) {
        mDataHandler.sendMessage(HISTORY_UPDATE_TITLE, new String[] { url, title });
    }

    public void updateFavicon(String originalUrl, String url, Bitmap favicon) {
        mDataHandler.sendMessage(BOOKMARK_UPDATE_FAVICON, new Object[] {originalUrl, url, favicon});
    }
    
    public void queryBookmarkStatus(String url, OnQueryUrlIsBookmark replyTo) {
        if (url == null || url.trim().length() == 0) {
            // null or empty url is never a bookmark
            replyTo.onQueryUrlIsBookmark(url, false);
            return;
        }
        mDataHandler.sendMessage(QUERY_URL_IS_BOOKMARK, url.trim(), replyTo);
    }

    public void addToHomeLink(String url, String title) {
        mDataHandler.sendMessage(ADD_TO_HOMELINK,  new String[] { url, title});
    }    
    
    public void updateToHomeLink(String url) {
        mDataHandler.sendMessage(UPDATE_TO_HOMELINK, url);
    }

    public void removeHomeLink(String url) {
        mDataHandler.sendMessage(REMOVE_FROM_HOMELINK, url);
    }
    
    public void editHomeLink(String url, String newUrl, String newTitle) {
        mDataHandler.sendMessage(EDIT_HOMELINK, new String[] { url, newUrl, newTitle });
    }
    // The standard Handler and Message classes don't allow the queue manipulation
    // we want (such as peeking). So we use our own queue.
    class DataControllerHandler extends Thread {
        private BlockingQueue<DCMessage> mMessageQueue
                = new LinkedBlockingQueue<DCMessage>();

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    handleMessage(mMessageQueue.take());
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }

        void sendMessage(int what, Object obj) {
            DCMessage m = new DCMessage(what, obj);
            mMessageQueue.add(m);
        }

        void sendMessage(int what, Object obj, Object replyTo) {
            DCMessage m = new DCMessage(what, obj);
            m.replyTo = replyTo;
            mMessageQueue.add(m);
        }

        private void handleMessage(DCMessage msg) {
            switch (msg.what) {
            case HISTORY_UPDATE_VISITED:
                doUpdateVisitedHistory((String) msg.obj);
                break;
            case HISTORY_UPDATE_TITLE:
                String[] args = (String[]) msg.obj;
                doUpdateHistoryTitle(args[0], args[1]);
                break;
            case BOOKMARK_UPDATE_FAVICON:
                Object[] myargs = (Object[]) msg.obj;
                doUpdateFavicon((String)myargs[0], (String)myargs[1], (Bitmap)myargs[2]);
                break;
            case QUERY_URL_IS_BOOKMARK:
                // TODO: Look for identical messages in the queue and remove them
                // TODO: Also, look for partial matches and merge them (such as
                //       multiple callbacks querying the same URL)
                doQueryBookmarkStatus((String) msg.obj, msg.replyTo);
                break;
            case ADD_TO_HOMELINK:
                String[] addParams = (String[]) msg.obj;
                doAddToHomeLink(addParams[0], addParams[1]);
                break;
            case REMOVE_FROM_HOMELINK:
                doRemoveHomeLink((String) msg.obj);
                break;
            case EDIT_HOMELINK:
                String[] params = (String[]) msg.obj;
                doEditHomeLink(params[0], params[1], params[2]);
                break;
            }
        }

        private void doEditHomeLink(String oldUrl, String newUrl, String newTitle) {
            ContentResolver cr = mContext.getContentResolver();
            Browser.editHomeLink(cr, oldUrl, newUrl, newTitle);
        }
        
        private void doAddToHomeLink(String url, String title) {
            ContentResolver cr = mContext.getContentResolver();
            Browser.addToHomeLink(cr, url, title);
        }

        private void doRemoveHomeLink(String url) {
            ContentResolver cr = mContext.getContentResolver();
            Browser.removeHomeLink(cr, url);
        }
        
        private void doUpdateVisitedHistory(String url) {
            ContentResolver cr = mContext.getContentResolver();
            Browser.updateVisitedHistory(cr, url, true);
//            try {
//                c = cr.query(History.CONTENT_URI, new String[] { History._ID, History.VISITS },
//                        History.URL + "=?", new String[] { url }, null);
//                if (c.moveToFirst()) {
//                    ContentValues values = new ContentValues();
//                    values.put(History.VISITS, c.getInt(1) + 1);
//                    values.put(History.DATE_LAST_VISITED, System.currentTimeMillis());
//                    cr.update(ContentUris.withAppendedId(History.CONTENT_URI, c.getLong(0)),
//                            values, null, null);
//                } else {
//                    dep.android.provider.Browser.truncateHistory(cr);
//                    ContentValues values = new ContentValues();
//                    values.put(History.URL, url);
//                    values.put(History.VISITS, 1);
//                    values.put(History.DATE_LAST_VISITED, System.currentTimeMillis());
//                    values.put(History.TITLE, url);
//                    values.put(History.DATE_CREATED, 0);
//                    values.put(History.USER_ENTERED, 0);
//                    cr.insert(History.CONTENT_URI, values);
//                }
//            } finally {
//                if (c != null) c.close();
//            }
        }

        private void doQueryBookmarkStatus(String url, Object replyTo) {
            ContentResolver cr = mContext.getContentResolver();
            // Check to see if the site is bookmarked
            Cursor cursor = null;
            boolean isBookmark = false;
            try {
                cursor = mContext.getContentResolver().query(
                        BookmarkUtils.getBookmarksUri(mContext),
                        new String[] { BrowserContract.Bookmarks.URL },
                        BrowserContract.Bookmarks.URL + " == ?",
                        new String[] { url },
                        null);
                isBookmark = cursor.moveToFirst();
            } catch (SQLiteException e) {
                Log.e(LOGTAG, "Error checking for bookmark: " + e);
            } finally {
                if (cursor != null) cursor.close();
            }
            CallbackContainer cc = new CallbackContainer();
            cc.replyTo = replyTo;
            cc.args = new Object[] { url, isBookmark };
            mCbHandler.obtainMessage(QUERY_URL_IS_BOOKMARK, cc).sendToTarget();
        }

        private void doUpdateHistoryTitle(String url, String title) {
            ContentResolver cr = mContext.getContentResolver();
            Browser.updateHistoryTitle(cr, url, title);
//            ContentValues values = new ContentValues();
//            values.put(History.TITLE, title);
//            cr.update(History.CONTENT_URI, values, History.URL + "=?",
//                    new String[] { url });
        }
        
        private void doUpdateFavicon(String originalUrl, String url, Bitmap favicon) {
            ContentResolver cr = mContext.getContentResolver();
            Bookmarks.updateFaviconWithoutAsyncTask(cr, originalUrl, url, favicon);
        }
        
    }


}
