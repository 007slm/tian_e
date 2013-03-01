package com.orange.browser;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.Browser.BookmarkColumns;
import android.util.Log;
import android.webkit.WebIconDatabase;
import android.widget.Toast;

import dep.android.provider.Browser;
import dep.android.provider.BrowserContract.Combined;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 *  This class is purely to have a common place for adding/deleting bookmarks.
 */
public class Bookmarks {
    public static final int TYPE_BOOKMARK = 1;
    public static final int TYPE_HISTORY = 0;
    // We only want the user to be able to bookmark content that
    // the browser can handle directly.
    private static final String acceptableBookmarkSchemes[] = {
            "http:",
            "https:",
            "about:",
            "data:",
            "javascript:",
            "file:",
            "content:"
    };

    private final static String LOGTAG = "Bookmarks";
    /**
     *  Add a bookmark to the database.
     *  @param context Context of the calling Activity.  This is used to make
     *          Toast confirming that the bookmark has been added.  If the
     *          caller provides null, the Toast will not be shown.
     *  @param cr The ContentResolver being used to add the bookmark to the db.
     *  @param url URL of the website to be bookmarked.
     *  @param name Provided name for the bookmark.
     *  @param thumbnail A thumbnail for the bookmark.
     *  @param retainIcon Whether to retain the page's icon in the icon database.
     *          This will usually be <code>true</code> except when bookmarks are
     *          added by a settings restore agent.
     */
    /* package */static boolean addBookmark(Context context,
            ContentResolver cr, String url, String name,
            Bitmap thumbnail, boolean retainIcon) {
        // Want to append to the beginning of the list
        long creationTime = new Date().getTime();
        ContentValues map = new ContentValues();
        Cursor cursor = null;
        boolean updateBookmark = false;
        try {
            cursor = Browser.getVisitedLike(cr, url);
            if (cursor.moveToFirst() && cursor.getInt(
                    Browser.HISTORY_PROJECTION_BOOKMARK_INDEX) == TYPE_HISTORY) {
                // This means we have been to this site but not bookmarked
                // it, so convert the history item to a bookmark
                map.put(Browser.BookmarkColumns.CREATED, creationTime);
                map.put(Browser.BookmarkColumns.TITLE, name);
                map.put(Browser.BookmarkColumns.BOOKMARK, TYPE_BOOKMARK);
                map.put(Browser.BookmarkColumns.THUMBNAIL,
                        bitmapToBytes(thumbnail));
                cr.update(Browser.BOOKMARKS_URI, map,
                        "_id = " + cursor.getInt(0), null);
            } else {
                int count = cursor.getCount();
                boolean matchedTitle = false;
                for (int i = 0; i < count; i++) {
                    // One or more bookmarks already exist for this site.
                    // Check the names of each
                    cursor.moveToPosition(i);
                    if (cursor.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX)
                            .equals(name)
                            && cursor.getInt(Browser.HISTORY_PROJECTION_BOOKMARK_INDEX) == TYPE_BOOKMARK) {
                        // The old bookmark has the same name.
                        // Update its creation time.
                        map.put(Browser.BookmarkColumns.CREATED,
                                creationTime);
                        updateBookmark = true;
                        cr.update(Browser.BOOKMARKS_URI, map,
                                "_id = " + cursor.getInt(0), null);
                        matchedTitle = true;
                        break;
                    }
                }
                if (!matchedTitle) {
                    // Adding a bookmark for a site the user has visited,
                    // or a new bookmark (with a different name) for a site
                    // the user has visited
                    map.put(Browser.BookmarkColumns.TITLE, name);
                    map.put(Browser.BookmarkColumns.URL, url);
                    map.put(Browser.BookmarkColumns.CREATED, creationTime);
                    map.put(Browser.BookmarkColumns.BOOKMARK, TYPE_BOOKMARK);
                    map.put(Browser.BookmarkColumns.DATE, 0);
                    map.put(Browser.BookmarkColumns.THUMBNAIL,
                            bitmapToBytes(thumbnail));
                    int visits = 0;
                    if (count > 0) {
                        // The user has already bookmarked, and possibly
                        // visited this site.  However, they are creating
                        // a new bookmark with the same url but a different
                        // name.  The new bookmark should have the same
                        // number of visits as the already created bookmark.
                        visits = cursor.getInt(
                                Browser.HISTORY_PROJECTION_VISITS_INDEX);
                    }
                    // Bookmark starts with 3 extra visits so that it will
                    // bubble up in the most visited and goto search box
                    map.put(Browser.BookmarkColumns.VISITS, visits + 3);
                    cr.insert(Browser.BOOKMARKS_URI, map);
                }
            }
        } catch (IllegalStateException e) {
            Log.e(LOGTAG, "addBookmark", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (retainIcon) {
            WebIconDatabase.getInstance().retainIconForPageUrl(url);
        }
        if (context != null) {
            Toast.makeText(context, R.string.added_to_bookmarks,
                    Toast.LENGTH_LONG).show();
        }
        return updateBookmark;
    }

    /**
     *  Add a bookmark to the database.
     *  @param context Context of the calling Activity.  This is used to make
     *          Toast confirming that the bookmark has been added.  If the
     *          caller provides null, the Toast will not be shown.
     *  @param url URL of the website to be bookmarked.
     *  @param name Provided name for the bookmark.
     *  @param thumbnail A thumbnail for the bookmark.
     *  @param retainIcon Whether to retain the page's icon in the icon database.
     *          This will usually be <code>true</code> except when bookmarks are
     *          added by a settings restore agent.
     *  @param parent ID of the parent folder.
     */
    /*
//     static void addBookmark(Context context, boolean showToast, String url,
//            String name, Bitmap thumbnail, long parent) {
//        // Want to append to the beginning of the list
//        ContentValues values = new ContentValues();
//        try {
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//            values.put(BrowserContract.Bookmarks.TITLE, name);
//            values.put(BrowserContract.Bookmarks.URL, url);
//            values.put(BrowserContract.Bookmarks.IS_FOLDER, 0);
//            values.put(BrowserContract.Bookmarks.THUMBNAIL,
//                    bitmapToBytes(thumbnail));
//            values.put(BrowserContract.Bookmarks.PARENT, parent);
//            context.getContentResolver().insert(BrowserContract.Bookmarks.CONTENT_URI, values);
//        } catch (IllegalStateException e) {
//            Log.e(LOGTAG, "addBookmark", e);
//        }
//        if (showToast) {
//            Toast.makeText(context, R.string.added_to_bookmarks,
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    /**
     *  Remove a bookmark from the database.  If the url is a visited site, it
     *  will remain in the database, but only as a history item, and not as a
     *  bookmarked site.
     *  @param context Context of the calling Activity.  This is used to make
     *          Toast confirming that the bookmark has been removed and to
     *          lookup the correct content uri.  It must not be null.
     *  @param cr The ContentResolver being used to remove the bookmark.
     *  @param url URL of the website to be removed.
     */
    /* package */ static void removeFromBookmarks(Context context,
            ContentResolver cr, String url, String title) {
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    Browser.BOOKMARKS_URI,
                    Browser.HISTORY_PROJECTION,
                    "url = ? AND title = ? AND bookmark= ?",
                    new String[] { url, title , "1"},
                    null);
            boolean first = cursor.moveToFirst();
            // Should be in the database no matter what
            if (!first) {
                throw new AssertionError("URL is not in the database! " + url
                        + " " + title);
            }
            // Remove from bookmarks
            WebIconDatabase.getInstance().releaseIconForPageUrl(url);
            Uri uri = ContentUris.withAppendedId(Browser.BOOKMARKS_URI,
                    cursor.getInt(Browser.HISTORY_PROJECTION_ID_INDEX));
            int numVisits = cursor.getInt(
                    Browser.HISTORY_PROJECTION_VISITS_INDEX);
            if (0 == numVisits) {
                cr.delete(uri, null, null);
            } else {
                // It is no longer a bookmark, but it is still a visited
                // site.
                ContentValues values = new ContentValues();
                values.put(Browser.BookmarkColumns.BOOKMARK, 0);
                try {
                    cr.update(uri, values, null, null);
                } catch (IllegalStateException e) {
                    Log.e("removeFromBookmarks", "no database!");
                }
            }
            if (context != null) {
                Toast.makeText(context, R.string.removed_from_bookmarks,
                        Toast.LENGTH_LONG).show();
            }
        } catch (IllegalStateException e) {
            Log.e(LOGTAG, "removeFromBookmarks", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static byte[] bitmapToBytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /* package */ public static boolean urlHasAcceptableScheme(String url) {
        if (url == null) {
            return false;
        }

        for (int i = 0; i < acceptableBookmarkSchemes.length; i++) {
            if (url.startsWith(acceptableBookmarkSchemes[i])) {
                return true;
            }
        }
        return false;
    }

    static final String QUERY_BOOKMARKS_WHERE =
            Combined.URL + " == ? OR " +
            Combined.URL + " == ?";

    public static Cursor queryCombinedForUrl(ContentResolver cr,
            String originalUrl, String url) {
        if (cr == null || url == null) {
            return null;
        }

        // If originalUrl is null, just set it to url.
        if (originalUrl == null) {
            originalUrl = url;
        }

        // Look for both the original url and the actual url. This takes in to
        // account redirects.
        String originalUrlNoQuery = removeQuery(originalUrl);
        String urlNoQuery = removeQuery(url);
        originalUrl = originalUrlNoQuery + '?';
        url = urlNoQuery + '?';

        // Use NoQuery to search for the base url (i.e. if the url is
        // http://www.yahoo.com/?rs=1, search for http://www.yahoo.com)
        // Use url to match the base url with other queries (i.e. if the url is
        // http://www.google.com/m, search for
        // http://www.google.com/m?some_query)
        final String[] selArgs = new String[] {
            originalUrlNoQuery, urlNoQuery, originalUrl, url };
        String where = BookmarkColumns.URL + " == ? OR "
                + BookmarkColumns.URL + " == ? OR "
                + BookmarkColumns.URL + " LIKE ? || '%' OR "
                + BookmarkColumns.URL + " LIKE ? || '%'";

        final String[] projection =
            new String[] { BaseColumns._ID };
        return cr.query(Browser.BOOKMARKS_URI, projection, where, selArgs,
            null);
//
//        if (cr == null || url == null) {
//            return null;
//        }
//
//        // If originalUrl is null, just set it to url.
//        if (originalUrl == null) {
//            originalUrl = url;
//        }
//
//        // Look for both the original url and the actual url. This takes in to
//        // account redirects.
//
//        final String[] selArgs = new String[] { originalUrl, url };
//        final String[] projection = new String[] { Combined.URL };
//        return cr.query(Combined.CONTENT_URI, projection, QUERY_BOOKMARKS_WHERE, selArgs, null);
    }

    // Strip the query from the given url.
    static String removeQuery(String url) {
        if (url == null) {
            return null;
        }
        int query = url.indexOf('?');
        String noQuery = url;
        if (query != -1) {
            noQuery = url.substring(0, query);
        }
        return noQuery;
    }

    /**
     * Update the bookmark's favicon. This is a convenience method for updating
     * a bookmark favicon for the originalUrl and url of the passed in WebView.
     * @param cr The ContentResolver to use.
     * @param originalUrl The original url before any redirects.
     * @param url The current url.
     * @param favicon The favicon bitmap to write to the db.
     */
    /* package */ static void updateFaviconWithAsyncTask(final ContentResolver cr,
            final String originalUrl, final String url, final Bitmap favicon) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unused) {
                final Cursor c =
                        queryBookmarksForUrl(cr, originalUrl, url, false);
                if (c == null) {
                    return null;
                }
                if (c.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    final ByteArrayOutputStream os =
                            new ByteArrayOutputStream();
                    favicon.compress(Bitmap.CompressFormat.PNG, 100, os);
                    values.put(Browser.BookmarkColumns.FAVICON,
                                os.toByteArray());
                    do {
                        cr.update(ContentUris.withAppendedId(
                                        Browser.BOOKMARKS_URI, c.getInt(0)),
                                    values, null, null);
                    } while (c.moveToNext());
                }
                c.close();
                return null;
            }
        }.execute();
    }

    /* package */static void updateFaviconWithoutAsyncTask(final ContentResolver cr,
            final String originalUrl, final String url, final Bitmap favicon) {

        final Cursor c =
                queryBookmarksForUrl(cr, originalUrl, url, false);
        if (c == null) {
            return;
        }
        if (c.moveToFirst()) {
            ContentValues values = new ContentValues();
            final ByteArrayOutputStream os =
                    new ByteArrayOutputStream();
            favicon.compress(Bitmap.CompressFormat.PNG, 100, os);
            values.put(Browser.BookmarkColumns.FAVICON,
                    os.toByteArray());
            do {
                cr.update(ContentUris.withAppendedId(
                        Browser.BOOKMARKS_URI, c.getInt(0)),
                        values, null, null);
            } while (c.moveToNext());
        }
        c.close();
        return;

    }
    
    /* package */ static Cursor queryBookmarksForUrl(ContentResolver cr,
            String originalUrl, String url, boolean onlyBookmarks) {
        if (cr == null || url == null) {
            return null;
        }

        // If originalUrl is null, just set it to url.
        if (originalUrl == null) {
            originalUrl = url;
        }

        // Look for both the original url and the actual url. This takes in to
        // account redirects.
        String originalUrlNoQuery = removeQuery(originalUrl);
        String urlNoQuery = removeQuery(url);
        originalUrl = originalUrlNoQuery + '?';
        url = urlNoQuery + '?';

        // Use NoQuery to search for the base url (i.e. if the url is
        // http://www.yahoo.com/?rs=1, search for http://www.yahoo.com)
        // Use url to match the base url with other queries (i.e. if the url is
        // http://www.google.com/m, search for
        // http://www.google.com/m?some_query)
        final String[] selArgs = new String[] {
            originalUrlNoQuery, urlNoQuery, originalUrl, url };
        String where = BookmarkColumns.URL + " == ? OR "
                + BookmarkColumns.URL + " == ? OR "
                + BookmarkColumns.URL + " LIKE ? || '%' OR "
                + BookmarkColumns.URL + " LIKE ? || '%'";
        if (onlyBookmarks) {
            where = "(" + where + ") AND " + BookmarkColumns.BOOKMARK + " == 1";
        }
        final String[] projection =
                new String[] { BaseColumns._ID };
        return cr.query(Browser.BOOKMARKS_URI, projection, where, selArgs,
                null);
    }

}
