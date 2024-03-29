package com.orange.browser.provider;

import com.orange.browser.BrowserSettings;
import com.orange.browser.R;
import com.orange.browser.search.SearchEngine;

import dep.android.provider.Browser;
import dep.android.provider.BrowserContract;
import dep.android.provider.Browser.BookmarkColumns;

import android.app.SearchManager;
import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Process;
import android.preference.PreferenceManager;
import android.speech.RecognizerResultsIntent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BrowserProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;
    private BackupManager mBackupManager;
    public static final String sDatabaseName = "browser.db";
    private static final String TAG = "BrowserProvider";
    private static final String ORDER_BY = "visits DESC, date DESC";

    public static final String AUTHORITY = "com.orange.browser";
    
    private static final String PICASA_URL = "http://picasaweb.google.com/m/" +
            "viewer?source=androidclient";

    private static final String[] TABLE_NAMES = new String[] {
        "bookmarks", "searches", "thumbnails", "omnibox_suggestions", "homelinks"
    };
    private static final String[] SUGGEST_PROJECTION = new String[] {
            "_id", "url", "title", "bookmark", "user_entered"
    };
    private static final String SUGGEST_SELECTION =
            "(url LIKE ? OR url LIKE ? OR url LIKE ? OR url LIKE ?"
                + " OR title LIKE ?) AND (bookmark = 1 OR user_entered = 1)";
    private String[] SUGGEST_ARGS = new String[5];
    
    /** A content:// style uri to the authority for the browser provider */
    public static final Uri CONTRACT_AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
    public static interface Thumbnails {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                CONTRACT_AUTHORITY_URI, "thumbnails");
        public static final String _ID = "_id";
        public static final String THUMBNAIL = "thumbnail";
    }
    
    public static interface OmniboxSuggestions {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                BrowserContract.AUTHORITY_URI, "omnibox_suggestions");
        public static final String _ID = "_id";
        public static final String URL = "url";
        public static final String TITLE = "title";
        public static final String IS_BOOKMARK = "bookmark";
    }
    
    public static interface homelinks {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                CONTRACT_AUTHORITY_URI, "homelinks");
        public static final String _ID = "_id";
        public static final String URL = "url";
        public static final String TITLE = "title";
        public static final String POSITION = "position";
        public static final String ICON = "icon";
    }
    
    // shared suggestion array index, make sure to match COLUMNS
    private static final int SUGGEST_COLUMN_INTENT_ACTION_ID = 1;
    private static final int SUGGEST_COLUMN_INTENT_DATA_ID = 2;
    private static final int SUGGEST_COLUMN_TEXT_1_ID = 3;
    private static final int SUGGEST_COLUMN_TEXT_2_ID = 4;
    private static final int SUGGEST_COLUMN_TEXT_2_URL_ID = 5;
    private static final int SUGGEST_COLUMN_ICON_1_ID = 6;
    private static final int SUGGEST_COLUMN_ICON_2_ID = 7;
    private static final int SUGGEST_COLUMN_QUERY_ID = 8;
    private static final int SUGGEST_COLUMN_INTENT_EXTRA_DATA = 9;

    // shared suggestion columns
    private static final String[] COLUMNS = new String[] {
            "_id",
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_TEXT_2_URL,
            SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_ICON_2,
            SearchManager.SUGGEST_COLUMN_QUERY,
            SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA};

    private static final int MAX_SUGGESTION_SHORT_ENTRIES = 3;
    private static final int MAX_SUGGESTION_LONG_ENTRIES = 6;
    private static final String MAX_SUGGESTION_LONG_ENTRIES_STRING =
            Integer.valueOf(MAX_SUGGESTION_LONG_ENTRIES).toString();

    // make sure that these match the index of TABLE_NAMES
    private static final int URI_MATCH_BOOKMARKS = 0;
    private static final int URI_MATCH_SEARCHES = 1;
    private static final int URI_MATCH_THUMBNAILS = 2;
    private static final int URI_MATCH_OMNIBOX_SUGGESTIONS = 3;
    private static final int URI_MATCH_HOMELINKS = 4;
    
    // (id % 10) should match the table name index
    private static final int URI_MATCH_BOOKMARKS_ID = 10;
    private static final int URI_MATCH_SEARCHES_ID = 11;
    private static final int URI_MATCH_THUMBNAILS_ID = 12;
    private static final int URI_MATCH_OMNIBOX_SUGGESTIONS_ID = 13;
    private static final int URI_MATCH_HOMELINKS_ID = 14;
    //
    private static final int URI_MATCH_SUGGEST = 20;
    private static final int URI_MATCH_BOOKMARKS_SUGGEST = 21;

    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_BOOKMARKS],
                URI_MATCH_BOOKMARKS);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_BOOKMARKS] + "/#",
                URI_MATCH_BOOKMARKS_ID);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_SEARCHES],
                URI_MATCH_SEARCHES);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_SEARCHES] + "/#",
                URI_MATCH_SEARCHES_ID);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_THUMBNAILS], URI_MATCH_THUMBNAILS);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_THUMBNAILS] + "/#", URI_MATCH_THUMBNAILS_ID);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_OMNIBOX_SUGGESTIONS], URI_MATCH_OMNIBOX_SUGGESTIONS);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_OMNIBOX_SUGGESTIONS] + "/#", URI_MATCH_OMNIBOX_SUGGESTIONS_ID);
        URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,
                URI_MATCH_SUGGEST);
        URI_MATCHER.addURI(AUTHORITY,
                TABLE_NAMES[URI_MATCH_BOOKMARKS] + "/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                URI_MATCH_BOOKMARKS_SUGGEST);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_HOMELINKS],
                URI_MATCH_HOMELINKS);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAMES[URI_MATCH_HOMELINKS] + "/#",
                URI_MATCH_HOMELINKS_ID);
    }

    // 1 -> 2 add cache table
    // 2 -> 3 update history table
    // 3 -> 4 add passwords table
    // 4 -> 5 add settings table
    // 5 -> 6 ?
    // 6 -> 7 ?
    // 7 -> 8 drop proxy table
    // 8 -> 9 drop settings table
    // 9 -> 10 add form_urls and form_data
    // 10 -> 11 add searches table
    // 11 -> 12 modify cache table
    // 12 -> 13 modify cache table
    // 13 -> 14 correspond with Google Bookmarks schema
    // 14 -> 15 move couple of tables to either browser private database or webview database
    // 15 -> 17 Set it up for the SearchManager
    // 17 -> 18 Added favicon in bookmarks table for Home shortcuts
    // 18 -> 19 Remove labels table
    // 19 -> 20 Added thumbnail
    // 20 -> 21 Added touch_icon
    // 21 -> 22 Remove "clientid"
    // 22 -> 23 Added user_entered
    // 25 -> 26 Remove jifeng from homelink
    private static final int DATABASE_VERSION = 26;

    // Regular expression which matches http://, followed by some stuff, followed by
    // optionally a trailing slash, all matched as separate groups.
    private static final Pattern STRIP_URL_PATTERN = Pattern.compile("^(http://)(.*?)(/$)?");

    private BrowserSettings mSettings;

    public BrowserProvider() {
    }

    // XXX: This is a major hack to remove our dependency on gsf constants and
    // its content provider. http://b/issue?id=2425179
    static String getClientId(ContentResolver cr) {
        String ret = "android-google";
        Cursor c = null;
        try {
            c = cr.query(Uri.parse("content://com.google.settings/partner"),
                    new String[] { "value" }, "name='client_id'", null, null);
            if (c != null && c.moveToNext()) {
                ret = c.getString(0);
            }
        } catch (RuntimeException ex) {
            // fall through to return the default
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }

    private static CharSequence replaceSystemPropertyInString(Context context, CharSequence srcString) {
        StringBuffer sb = new StringBuffer();
        int lastCharLoc = 0;

        final String client_id = getClientId(context.getContentResolver());

        for (int i = 0; i < srcString.length(); ++i) {
            char c = srcString.charAt(i);
            if (c == '{') {
                sb.append(srcString.subSequence(lastCharLoc, i));
                lastCharLoc = i;
          inner:
                for (int j = i; j < srcString.length(); ++j) {
                    char k = srcString.charAt(j);
                    if (k == '}') {
                        String propertyKeyValue = srcString.subSequence(i + 1, j).toString();
                        if (propertyKeyValue.equals("CLIENT_ID")) {
                            sb.append(client_id);
                        } else {
                            sb.append("unknown");
                        }
                        lastCharLoc = j + 1;
                        i = j;
                        break inner;
                    }
                }
            }
        }
        if (srcString.length() - lastCharLoc > 0) {
            // Put on the tail, if there is one
            sb.append(srcString.subSequence(lastCharLoc, srcString.length()));
        }
        return sb;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private Context mContext;

        public DatabaseHelper(Context context) {
            super(context, sDatabaseName, null, DATABASE_VERSION);
            mContext = context;
        }

        void createThumbnails(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAMES[URI_MATCH_THUMBNAILS] + " (" +
                    Thumbnails._ID + " INTEGER PRIMARY KEY," +
                    Thumbnails.THUMBNAIL + " BLOB NOT NULL" +
                    ");");
        }
        
        void createHomelinks(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS homelinks (" +
                    "_id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "url TEXT," +
                    "position INTEGER," +
                    "icon TEXT DEFAULT NULL" +
                    ");");
        }
        
        void createOmniboxSuggestions(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_VIEW_OMNIBOX_SUGGESTIONS);
        }
        
        private static final String SQL_CREATE_VIEW_OMNIBOX_SUGGESTIONS =
                "CREATE VIEW IF NOT EXISTS v_omnibox_suggestions "
                + " AS "
                + "  SELECT _id, url, title, bookmark, visits, date"
                + "  FROM bookmarks "
//                + "  WHERE deleted = 0 AND folder = 0 "
//                + "  UNION ALL "
//                + "  SELECT _id, url, title, 0 AS bookmark, visits, date "
//                + "  FROM history "
//                + "  WHERE url NOT IN (SELECT url FROM bookmarks"
//                + "    WHERE deleted = 0 AND folder = 0) "
                + "  ORDER BY bookmark DESC, visits DESC, date DESC ";
        
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE bookmarks (" +
                    "_id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "url TEXT," +
                    "visits INTEGER," +
                    "date LONG," +
                    "created LONG," +
                    "description TEXT," +
                    "bookmark INTEGER," +
                    "favicon BLOB DEFAULT NULL," +
                    "thumbnail BLOB DEFAULT NULL," +
                    "touch_icon BLOB DEFAULT NULL," +
                    "user_entered INTEGER," +
                    "is_homelink" + " INTEGER NOT NULL DEFAULT 0," +
                    "icon_file TEXT DEFAULT NULL," +
                    "user_title TEXT DEFAULT NULL" +
                    ");");

            final CharSequence[] bookmarks = mContext.getResources()
                    .getTextArray(R.array.bookmarks);
            int size = bookmarks.length;
            Resources res = mContext.getResources();
            TypedArray preloads = res.obtainTypedArray(R.array.bookmark_preloads);
            
            try {
                for (int i = 0, j = 0; i < size; i = i + 2, ++j) {
                    CharSequence bookmarkDestination = replaceSystemPropertyInString(mContext, bookmarks[i + 1]);
                    db.execSQL("INSERT INTO bookmarks (title, url, visits, " +
                            "date, created, bookmark)" + " VALUES('" +
                            bookmarks[i] + "', '" + bookmarkDestination +
                            "', 0, 0, 0, 1);");
                    ContentValues values = new ContentValues();
                    byte[] thumb = null;
                    int thumbId = preloads.getResourceId(j, 0);
                    thumb = readRaw(res, thumbId);
                    values.put(Browser.BookmarkColumns.THUMBNAIL, thumb);
                    db.update("bookmarks", values, "url=?",
                            new String[] { bookmarkDestination.toString() });

                }
            } catch (ArrayIndexOutOfBoundsException e) {
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Add for homelinks
            createHomelinks(db);

            final CharSequence[] homelinks = mContext.getResources()
                    .getTextArray(R.array.homelinks);
            size = homelinks.length;
            
            try {
                for (int i = 0; i < size; i = i + 4) {
                    db.execSQL("INSERT INTO homelinks (title, url, position, icon)" + 
                            " VALUES('" + homelinks[i] + "', '" + homelinks[i+1] +
                            "', '"  + homelinks[i+2] +"', '" + homelinks[i+3] + "');");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            
            db.execSQL("CREATE TABLE searches (" +
                    "_id INTEGER PRIMARY KEY," +
                    "search TEXT," +
                    "date LONG" +
                    ");");
            createThumbnails(db);
            //TODO
            createOmniboxSuggestions(db);

        }
        
        private byte[] readRaw(Resources res, int id) throws IOException {
            if (id == 0) {
                return null;
            }
            InputStream is = res.openRawResource(id);
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int read;
                while ((read = is.read(buf)) > 0) {
                    bos.write(buf, 0, read);
                }
                bos.flush();
                return bos.toByteArray();
            } finally {
                is.close();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion);
            //Our initial version is 25
//            if (oldVersion == 18) {
//                db.execSQL("DROP TABLE IF EXISTS labels");
//            }
//            if (oldVersion <= 19) {
//                db.execSQL("ALTER TABLE bookmarks ADD COLUMN thumbnail BLOB DEFAULT NULL;");
//            }
//            if (oldVersion < 21) {
//                db.execSQL("ALTER TABLE bookmarks ADD COLUMN touch_icon BLOB DEFAULT NULL;");
//            }
//            if (oldVersion < 22) {
//                db.execSQL("DELETE FROM bookmarks WHERE (bookmark = 0 AND url LIKE \"%.google.%client=ms-%\")");
//                removeGears();
//            }
//            if (oldVersion < 23) {
//                db.execSQL("ALTER TABLE bookmarks ADD COLUMN user_entered INTEGER;");
//            } else {
//                db.execSQL("DROP TABLE IF EXISTS bookmarks");
//                db.execSQL("DROP TABLE IF EXISTS searches");
//                onCreate(db);
//            }
            if (oldVersion < 26) {
                db.execSQL("DELETE FROM homelinks WHERE (url LIKE \"http://www.gfan.com/juhe\")");
                //Hard code here, not good.
                db.execSQL("INSERT INTO homelinks (title, url, position, icon) VALUES(\"环球网\", \"http://wap.huanqiu.com/\", \"2\", \"/html/images/huanqiu.png\")");
            }
        }

        private void removeGears() {
            new Thread() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    String browserDataDirString = mContext.getApplicationInfo().dataDir;
                    final String appPluginsDirString = "app_plugins";
                    final String gearsPrefix = "gears";
                    File appPluginsDir = new File(browserDataDirString + File.separator
                            + appPluginsDirString);
                    if (!appPluginsDir.exists()) {
                        return;
                    }
                    // Delete the Gears plugin files
                    File[] gearsFiles = appPluginsDir.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String filename) {
                            return filename.startsWith(gearsPrefix);
                        }
                    });
                    for (int i = 0; i < gearsFiles.length; ++i) {
                        if (gearsFiles[i].isDirectory()) {
                            deleteDirectory(gearsFiles[i]);
                        } else {
                            gearsFiles[i].delete();
                        }
                    }
                    // Delete the Gears data files
                    File gearsDataDir = new File(browserDataDirString + File.separator
                            + gearsPrefix);
                    if (!gearsDataDir.exists()) {
                        return;
                    }
                    deleteDirectory(gearsDataDir);
                }

                private void deleteDirectory(File currentDir) {
                    File[] files = currentDir.listFiles();
                    for (int i = 0; i < files.length; ++i) {
                        if (files[i].isDirectory()) {
                            deleteDirectory(files[i]);
                        }
                        files[i].delete();
                    }
                    currentDir.delete();
                }
            }.start();
        }
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new DatabaseHelper(context);
        mBackupManager = new BackupManager(context);
        // we added "picasa web album" into default bookmarks for version 19.
        // To avoid erasing the bookmark table, we added it explicitly for
        // version 18 and 19 as in the other cases, we will erase the table.
        if (DATABASE_VERSION == 18 || DATABASE_VERSION == 19) {
            SharedPreferences p = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean fix = p.getBoolean("fix_picasa", true);
            if (fix) {
                fixPicasaBookmark();
                Editor ed = p.edit();
                ed.putBoolean("fix_picasa", false);
                ed.commit();
            }
        }
        mSettings = BrowserSettings.getInstance();
        return true;
    }

    private void fixPicasaBookmark() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM bookmarks WHERE " +
                "bookmark = 1 AND url = ?", new String[] { PICASA_URL });
        try {
            if (!cursor.moveToFirst()) {
                // set "created" so that it will be on the top of the list
                db.execSQL("INSERT INTO bookmarks (title, url, visits, " +
                        "date, created, bookmark)" + " VALUES('" +
                        getContext().getString(R.string.picasa) + "', '"
                        + PICASA_URL + "', 0, 0, " + new Date().getTime()
                        + ", 1);");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
     * Subclass AbstractCursor so we can combine multiple Cursors and add
     * "Search the web".
     * Here are the rules.
     * 1. We only have MAX_SUGGESTION_LONG_ENTRIES in the list plus
     *      "Search the web";
     * 2. If bookmark/history entries has a match, "Search the web" shows up at
     *      the second place. Otherwise, "Search the web" shows up at the first
     *      place.
     */
    private class MySuggestionCursor extends AbstractCursor {
        private Cursor  mHistoryCursor;
        private Cursor  mSuggestCursor;
        private int     mHistoryCount;
        private int     mSuggestionCount;
        private boolean mIncludeWebSearch;
        private String  mString;
        private int     mSuggestText1Id;
        private int     mSuggestText2Id;
        private int     mSuggestText2UrlId;
        private int     mSuggestQueryId;
        private int     mSuggestIntentExtraDataId;

        public MySuggestionCursor(Cursor hc, Cursor sc, String string) {
            mHistoryCursor = hc;
            mSuggestCursor = sc;
            mHistoryCount = hc.getCount();
            mSuggestionCount = sc != null ? sc.getCount() : 0;
            if (mSuggestionCount > (MAX_SUGGESTION_LONG_ENTRIES - mHistoryCount)) {
                mSuggestionCount = MAX_SUGGESTION_LONG_ENTRIES - mHistoryCount;
            }
            mString = string;
            mIncludeWebSearch = string.length() > 0;

            // Some web suggest providers only give suggestions and have no description string for
            // items. The order of the result columns may be different as well. So retrieve the
            // column indices for the fields we need now and check before using below.
            if (mSuggestCursor == null) {
                mSuggestText1Id = -1;
                mSuggestText2Id = -1;
                mSuggestText2UrlId = -1;
                mSuggestQueryId = -1;
                mSuggestIntentExtraDataId = -1;
            } else {
                mSuggestText1Id = mSuggestCursor.getColumnIndex(
                                SearchManager.SUGGEST_COLUMN_TEXT_1);
                mSuggestText2Id = mSuggestCursor.getColumnIndex(
                                SearchManager.SUGGEST_COLUMN_TEXT_2);
                mSuggestText2UrlId = mSuggestCursor.getColumnIndex(
                        SearchManager.SUGGEST_COLUMN_TEXT_2_URL);
                mSuggestQueryId = mSuggestCursor.getColumnIndex(
                                SearchManager.SUGGEST_COLUMN_QUERY);
                mSuggestIntentExtraDataId = mSuggestCursor.getColumnIndex(
                                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);
            }
        }

        @Override
        public boolean onMove(int oldPosition, int newPosition) {
            if (mHistoryCursor == null) {
                return false;
            }
            if (mIncludeWebSearch) {
                if (mHistoryCount == 0 && newPosition == 0) {
                    return true;
                } else if (mHistoryCount > 0) {
                    if (newPosition == 0) {
                        mHistoryCursor.moveToPosition(0);
                        return true;
                    } else if (newPosition == 1) {
                        return true;
                    }
                }
                newPosition--;
            }
            if (mHistoryCount > newPosition) {
                mHistoryCursor.moveToPosition(newPosition);
            } else {
                mSuggestCursor.moveToPosition(newPosition - mHistoryCount);
            }
            return true;
        }

        @Override
        public int getCount() {
            if (mIncludeWebSearch) {
                return mHistoryCount + mSuggestionCount + 1;
            } else {
                return mHistoryCount + mSuggestionCount;
            }
        }

        @Override
        public String[] getColumnNames() {
            return COLUMNS;
        }

        @Override
        public String getString(int columnIndex) {
            if ((mPos != -1 && mHistoryCursor != null)) {
                int type = -1; // 0: web search; 1: history; 2: suggestion
                if (mIncludeWebSearch) {
                    if (mHistoryCount == 0 && mPos == 0) {
                        type = 0;
                    } else if (mHistoryCount > 0) {
                        if (mPos == 0) {
                            type = 1;
                        } else if (mPos == 1) {
                            type = 0;
                        }
                    }
                    if (type == -1) type = (mPos - 1) < mHistoryCount ? 1 : 2;
                } else {
                    type = mPos < mHistoryCount ? 1 : 2;
                }

                switch(columnIndex) {
                    case SUGGEST_COLUMN_INTENT_ACTION_ID:
                        if (type == 1) {
                            return Intent.ACTION_VIEW;
                        } else {
                            return Intent.ACTION_SEARCH;
                        }

                    case SUGGEST_COLUMN_INTENT_DATA_ID:
                        if (type == 1) {
                            return mHistoryCursor.getString(1);
                        } else {
                            return null;
                        }

                    case SUGGEST_COLUMN_TEXT_1_ID:
                        if (type == 0) {
                            return mString;
                        } else if (type == 1) {
                            return getHistoryTitle();
                        } else {
                            if (mSuggestText1Id == -1) return null;
                            return mSuggestCursor.getString(mSuggestText1Id);
                        }

                    case SUGGEST_COLUMN_TEXT_2_ID:
                        if (type == 0) {
                            return getContext().getString(R.string.search_the_web);
                        } else if (type == 1) {
                            return null;  // Use TEXT_2_URL instead
                        } else {
                            if (mSuggestText2Id == -1) return null;
                            return mSuggestCursor.getString(mSuggestText2Id);
                        }

                    case SUGGEST_COLUMN_TEXT_2_URL_ID:
                        if (type == 0) {
                            return null;
                        } else if (type == 1) {
                            return getHistoryUrl();
                        } else {
                            if (mSuggestText2UrlId == -1) return null;
                            return mSuggestCursor.getString(mSuggestText2UrlId);
                        }

                    case SUGGEST_COLUMN_ICON_1_ID:
                        if (type == 1) {
                            if (mHistoryCursor.getInt(3) == 1) {
                                return Integer.valueOf(
                                        R.drawable.ic_search_category_bookmark)
                                        .toString();
                            } else {
                                return Integer.valueOf(
                                        R.drawable.ic_search_category_history)
                                        .toString();
                            }
                        } else {
                            return Integer.valueOf(
                                    R.drawable.ic_search_category_suggest)
                                    .toString();
                        }

                    case SUGGEST_COLUMN_ICON_2_ID:
                        return "0";

                    case SUGGEST_COLUMN_QUERY_ID:
                        if (type == 0) {
                            return mString;
                        } else if (type == 1) {
                            // Return the url in the intent query column. This is ignored
                            // within the browser because our searchable is set to
                            // android:searchMode="queryRewriteFromData", but it is used by
                            // global search for query rewriting.
                            return mHistoryCursor.getString(1);
                        } else {
                            if (mSuggestQueryId == -1) return null;
                            return mSuggestCursor.getString(mSuggestQueryId);
                        }

                    case SUGGEST_COLUMN_INTENT_EXTRA_DATA:
                        if (type == 0) {
                            return null;
                        } else if (type == 1) {
                            return null;
                        } else {
                            if (mSuggestIntentExtraDataId == -1) return null;
                            return mSuggestCursor.getString(mSuggestIntentExtraDataId);
                        }
                }
            }
            return null;
        }

        @Override
        public double getDouble(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(int column) {
            if ((mPos != -1) && column == 0) {
                return mPos;        // use row# as the _Id
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNull(int column) {
            throw new UnsupportedOperationException();
        }

        // TODO Temporary change, finalize after jq's changes go in
        public void deactivate() {
            if (mHistoryCursor != null) {
                mHistoryCursor.deactivate();
            }
            if (mSuggestCursor != null) {
                mSuggestCursor.deactivate();
            }
            super.deactivate();
        }

        public boolean requery() {
            return (mHistoryCursor != null ? mHistoryCursor.requery() : false) |
                    (mSuggestCursor != null ? mSuggestCursor.requery() : false);
        }

        // TODO Temporary change, finalize after jq's changes go in
        public void close() {
            super.close();
            if (mHistoryCursor != null) {
                mHistoryCursor.close();
                mHistoryCursor = null;
            }
            if (mSuggestCursor != null) {
                mSuggestCursor.close();
                mSuggestCursor = null;
            }
        }

        /**
         * Provides the title (text line 1) for a browser suggestion, which should be the
         * webpage title. If the webpage title is empty, returns the stripped url instead.
         *
         * @return the title string to use
         */
        private String getHistoryTitle() {
            String title = mHistoryCursor.getString(2 /* webpage title */);
            if (TextUtils.isEmpty(title) || TextUtils.getTrimmedLength(title) == 0) {
                title = stripUrl(mHistoryCursor.getString(1 /* url */));
            }
            return title;
        }

        /**
         * Provides the subtitle (text line 2) for a browser suggestion, which should be the
         * webpage url. If the webpage title is empty, then the url should go in the title
         * instead, and the subtitle should be empty, so this would return null.
         *
         * @return the subtitle string to use, or null if none
         */
        private String getHistoryUrl() {
            String title = mHistoryCursor.getString(2 /* webpage title */);
            if (TextUtils.isEmpty(title) || TextUtils.getTrimmedLength(title) == 0) {
                return null;
            } else {
                return stripUrl(mHistoryCursor.getString(1 /* url */));
            }
        }

    }

    private static class ResultsCursor extends AbstractCursor {
        // Array indices for RESULTS_COLUMNS
        private static final int RESULT_ACTION_ID = 1;
        private static final int RESULT_DATA_ID = 2;
        private static final int RESULT_TEXT_ID = 3;
        private static final int RESULT_ICON_ID = 4;
        private static final int RESULT_EXTRA_ID = 5;

        private static final String[] RESULTS_COLUMNS = new String[] {
                "_id",
                SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
        };
        private final ArrayList<String> mResults;
        public ResultsCursor(ArrayList<String> results) {
            mResults = results;
        }
        public int getCount() { return mResults.size(); }

        public String[] getColumnNames() {
            return RESULTS_COLUMNS;
        }

        public String getString(int column) {
            switch (column) {
                case RESULT_ACTION_ID:
                    return RecognizerResultsIntent.ACTION_VOICE_SEARCH_RESULTS;
                case RESULT_TEXT_ID:
                // The data is used when the phone is in landscape mode.  We
                // still want to show the result string.
                case RESULT_DATA_ID:
                    return mResults.get(mPos);
                case RESULT_EXTRA_ID:
                    // The Intent's extra data will store the index into
                    // mResults so the BrowserActivity will know which result to
                    // use.
                    return Integer.toString(mPos);
                case RESULT_ICON_ID:
                    return Integer.valueOf(R.drawable.magnifying_glass)
                            .toString();
                default:
                    return null;
            }
        }
        public short getShort(int column) {
            throw new UnsupportedOperationException();
        }
        public int getInt(int column) {
            throw new UnsupportedOperationException();
        }
        public long getLong(int column) {
            if ((mPos != -1) && column == 0) {
                return mPos;        // use row# as the _id
            }
            throw new UnsupportedOperationException();
        }
        public float getFloat(int column) {
            throw new UnsupportedOperationException();
        }
        public double getDouble(int column) {
            throw new UnsupportedOperationException();
        }
        public boolean isNull(int column) {
            throw new UnsupportedOperationException();
        }
    }

    private ResultsCursor mResultsCursor;

    /**
     * Provide a set of results to be returned to query, intended to be used
     * by the SearchDialog when the BrowserActivity is in voice search mode.
     * @param results Strings to display in the dropdown from the SearchDialog
     */
    /* package */ void setQueryResults(ArrayList<String> results) {
        if (results == null) {
            mResultsCursor = null;
        } else {
            mResultsCursor = new ResultsCursor(results);
        }
    }
    

    //TODO:query
    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sortOrder)
            throws IllegalStateException {
        int match = URI_MATCHER.match(url);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }
        if (match == URI_MATCH_SUGGEST && mResultsCursor != null) {
            Cursor results = mResultsCursor;
            mResultsCursor = null;
            return results;
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        if (match == URI_MATCH_SUGGEST || match == URI_MATCH_BOOKMARKS_SUGGEST) {
            // Handle suggestions
            return doSuggestQuery(selection, selectionArgs, match == URI_MATCH_BOOKMARKS_SUGGEST);
        }

        String[] projection = null;
        if (projectionIn != null && projectionIn.length > 0) {
            projection = new String[projectionIn.length + 1];
            System.arraycopy(projectionIn, 0, projection, 0, projectionIn.length);
            projection[projectionIn.length] = "_id AS _id";
        }

        StringBuilder whereClause = new StringBuilder(256);
        if (match == URI_MATCH_BOOKMARKS_ID || match == URI_MATCH_SEARCHES_ID) {
            whereClause.append("(_id = ").append(url.getPathSegments().get(1))
                    .append(")");
        }

        // Tack on the user's selection, if present
        if (selection != null && selection.length() > 0) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }

            whereClause.append('(');
            whereClause.append(selection);
            whereClause.append(')');
        }
        Cursor c = null;
        if (match == URI_MATCH_OMNIBOX_SUGGESTIONS) {
            c = db.query("v_omnibox_suggestions", projection,
                    whereClause.toString(), selectionArgs, null, null, sortOrder,
                    null);
            
            c.setNotificationUri(getContext().getContentResolver(), url);
        } else {
            c = db.query(TABLE_NAMES[match % 10], projection,
                whereClause.toString(), selectionArgs, null, null, sortOrder,
                null);
        	c.setNotificationUri(getContext().getContentResolver(), url);
        }
        
        return c;
    }

    private Cursor doSuggestQuery(String selection, String[] selectionArgs, boolean bookmarksOnly) {
        String suggestSelection;
        String [] myArgs;
        if (selectionArgs[0] == null || selectionArgs[0].equals("")) {
            suggestSelection = null;
            myArgs = null;
        } else {
            String like = selectionArgs[0] + "%";
            if (selectionArgs[0].startsWith("http")
                    || selectionArgs[0].startsWith("file")) {
                myArgs = new String[1];
                myArgs[0] = like;
                suggestSelection = selection;
            } else {
                SUGGEST_ARGS[0] = "http://" + like;
                SUGGEST_ARGS[1] = "http://www." + like;
                SUGGEST_ARGS[2] = "https://" + like;
                SUGGEST_ARGS[3] = "https://www." + like;
                // To match against titles.
                SUGGEST_ARGS[4] = like;
                myArgs = SUGGEST_ARGS;
                suggestSelection = SUGGEST_SELECTION;
            }
        }

        Cursor c = mOpenHelper.getReadableDatabase().query(TABLE_NAMES[URI_MATCH_BOOKMARKS],
                SUGGEST_PROJECTION, suggestSelection, myArgs, null, null,
                ORDER_BY, MAX_SUGGESTION_LONG_ENTRIES_STRING);

        if (bookmarksOnly
                || Patterns.WEB_URL.matcher(selectionArgs[0]).matches()) {
            return new MySuggestionCursor(c, null, "");
        } else {
            // get search suggestions if there is still space in the list
            if (myArgs != null && myArgs.length > 1
                    && c.getCount() < (MAX_SUGGESTION_SHORT_ENTRIES - 1)) {
                SearchEngine searchEngine = mSettings.getSearchEngine();
                if (searchEngine != null && searchEngine.supportsSuggestions()) {
                    Cursor sc = searchEngine.getSuggestions(getContext(), selectionArgs[0]);
                    return new MySuggestionCursor(c, sc, selectionArgs[0]);
                }
            }
            return new MySuggestionCursor(c, null, selectionArgs[0]);
        }
    }

    @Override
    public String getType(Uri url) {
        int match = URI_MATCHER.match(url);
        switch (match) {
            case URI_MATCH_BOOKMARKS:
                return "vnd.android.cursor.dir/com.orange.browser.bookmark";

            case URI_MATCH_BOOKMARKS_ID:
                return "vnd.android.cursor.item/com.orange.browser.bookmark";
                
            case URI_MATCH_HOMELINKS:
                return "vnd.android.cursor.dir/com.orange.browser.homlinks";

            case URI_MATCH_HOMELINKS_ID:
                return "vnd.android.cursor.item/com.orange.browser.homelinks";

            case URI_MATCH_SEARCHES:
                return "vnd.android.cursor.dir/com.orange.browser.searches";

            case URI_MATCH_SEARCHES_ID:
                return "vnd.android.cursor.item/com.orange.browser.searches";

            case URI_MATCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
                
            case URI_MATCH_THUMBNAILS:
                return "vnd.android.cursor.dir/com.orange.browser.thumbnails";

            case URI_MATCH_THUMBNAILS_ID:
                return "vnd.android.cursor.item/com.orange.browser.thumbnails";
                
            case URI_MATCH_OMNIBOX_SUGGESTIONS:
                return "vnd.android.cursor.dir/com.orange.browser.omnibox_suggestions";

            case URI_MATCH_OMNIBOX_SUGGESTIONS_ID:
                return "vnd.android.cursor.item/com.orange.browser.omnibox_suggestions";
                
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    
    //TODO:insert
    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        boolean isBookmarkTable = false;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(url);
        Uri uri = null;
        long rowID = 0;
        switch (match) {
            case URI_MATCH_BOOKMARKS: {
                // Insert into the bookmarks table
                rowID = db.insert(TABLE_NAMES[URI_MATCH_BOOKMARKS], "url",
                        initialValues);
                if (rowID > 0) {
                    uri = ContentUris.withAppendedId(Browser.BOOKMARKS_URI,
                            rowID);
                }
                isBookmarkTable = true;
                break;
            }
            
            case URI_MATCH_HOMELINKS: {
                // Insert into the bookmarks table
                rowID = db.insert(TABLE_NAMES[URI_MATCH_HOMELINKS], "url",
                        initialValues);
                if (rowID > 0) {
                    uri = ContentUris.withAppendedId(Browser.HOMELINKS_URI,
                            rowID);
                }
                break;
            }

            case URI_MATCH_SEARCHES: {
                // Insert into the searches table
                rowID = db.insert(TABLE_NAMES[URI_MATCH_SEARCHES], "url",
                        initialValues);
                if (rowID > 0) {
                    uri = ContentUris.withAppendedId(Browser.SEARCHES_URI,
                            rowID);
                }
                break;
            }
            case URI_MATCH_THUMBNAILS:{
                // Insert into the thumbnails table
                rowID = db.insert(TABLE_NAMES[URI_MATCH_THUMBNAILS], null,
                        initialValues);
                if (rowID > 0) {
                    uri = ContentUris.withAppendedId(CONTRACT_AUTHORITY_URI,
                            rowID);
                }
                break;
            } 

            default:
                throw new IllegalArgumentException("Unknown URL with match " + match);
        }

        if (uri == null) {
            Log.w(TAG, "db insert error with row id " + rowID);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Back up the new bookmark set if we just inserted one.
        // A row created when bookmarks are added from scratch will have
        // bookmark=1 in the initial value set.
        if (isBookmarkTable
                && initialValues.containsKey(BookmarkColumns.BOOKMARK)
                && initialValues.getAsInteger(BookmarkColumns.BOOKMARK) != 0) {
            mBackupManager.dataChanged();
        }
        return uri;
    }

    
    //TODO:delete
    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(url);        
        if (match == -1 || match == URI_MATCH_SUGGEST) {
            throw new IllegalArgumentException("Unknown URL");
        }
        
        // need to know whether it's the bookmarks table for a couple of reasons
        boolean isBookmarkTable = (match == URI_MATCH_BOOKMARKS_ID);
        String id = null;

        if (isBookmarkTable || match == URI_MATCH_SEARCHES_ID || 
        		match == URI_MATCH_THUMBNAILS_ID || match == URI_MATCH_HOMELINKS_ID) {
            StringBuilder sb = new StringBuilder();
            if (where != null && where.length() > 0) {
                sb.append("( ");
                sb.append(where);
                sb.append(" ) AND ");
            }
            id = url.getPathSegments().get(1);
            sb.append("_id = ");
            sb.append(id);
            where = sb.toString();
        }

        ContentResolver cr = getContext().getContentResolver();

        // we'lll need to back up the bookmark set if we are about to delete one
        if (isBookmarkTable) {
            Cursor cursor = cr.query(Browser.BOOKMARKS_URI,
                    new String[] { BookmarkColumns.BOOKMARK },
                    "_id = " + id, null, null);
            if (cursor.moveToNext()) {
                if (cursor.getInt(0) != 0) {
                    // yep, this record is a bookmark
                    mBackupManager.dataChanged();
                }
            }
            cursor.close();
        }

        int count = db.delete(TABLE_NAMES[match % 10], where, whereArgs);
        cr.notifyChange(url, null);
        return count;
    }

    
    //TODO:update
    @Override
    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = URI_MATCHER.match(url);
        if (match == -1 || match == URI_MATCH_SUGGEST) {
            throw new IllegalArgumentException("Unknown URL");
        }

        if (match == URI_MATCH_BOOKMARKS_ID || match == URI_MATCH_SEARCHES_ID
        		|| match == URI_MATCH_THUMBNAILS_ID || match == URI_MATCH_HOMELINKS_ID) {
            StringBuilder sb = new StringBuilder();
            if (where != null && where.length() > 0) {
                sb.append("( ");
                sb.append(where);
                sb.append(" ) AND ");
            }
            String id = url.getPathSegments().get(1);
            sb.append("_id = ");
            sb.append(id);
            where = sb.toString();
        }

        ContentResolver cr = getContext().getContentResolver();

        // Not all bookmark-table updates should be backed up.  Look to see
        // whether we changed the title, url, or "is a bookmark" state, and
        // request a backup if so.
        if (match == URI_MATCH_BOOKMARKS_ID || match == URI_MATCH_BOOKMARKS) {
            boolean changingBookmarks = false;
            // Alterations to the bookmark field inherently change the bookmark
            // set, so we don't need to query the record; we know a priori that
            // we will need to back up this change.
            if (values.containsKey(BookmarkColumns.BOOKMARK)) {
                changingBookmarks = true;
            } else if ((values.containsKey(BookmarkColumns.TITLE)
                     || values.containsKey(BookmarkColumns.URL))
                     && values.containsKey(BookmarkColumns._ID)) {
                // If a title or URL has been changed, check to see if it is to
                // a bookmark.  The ID should have been included in the update,
                // so use it.
                Cursor cursor = cr.query(Browser.BOOKMARKS_URI,
                        new String[] { BookmarkColumns.BOOKMARK },
                        BookmarkColumns._ID + " = "
                        + values.getAsString(BookmarkColumns._ID), null, null);
                if (cursor.moveToNext()) {
                    changingBookmarks = (cursor.getInt(0) != 0);
                }
                cursor.close();
            }

            // if this *is* a bookmark row we're altering, we need to back it up.
            if (changingBookmarks) {
                mBackupManager.dataChanged();
            }
        }

        int ret = db.update(TABLE_NAMES[match % 10], values, where, whereArgs);
        cr.notifyChange(url, null);
        return ret;
    }

    /**
     * Strips the provided url of preceding "http://" and any trailing "/". Does not
     * strip "https://". If the provided string cannot be stripped, the original string
     * is returned.
     *
     * TODO: Put this in TextUtils to be used by other packages doing something similar.
     *
     * @param url a url to strip, like "http://www.google.com/"
     * @return a stripped url like "www.google.com", or the original string if it could
     *         not be stripped
     */
    private static String stripUrl(String url) {
        if (url == null) return null;
        Matcher m = STRIP_URL_PATTERN.matcher(url);
        if (m.matches() && m.groupCount() == 3) {
            return m.group(2);
        } else {
            return url;
        }
    }

}
