
package com.orange.browser;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.webkit.WebIconDatabase;
import android.widget.Toast;

import dep.android.provider.Browser;
import dep.android.provider.Browser.BookmarkColumns;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class HomeLinks {
    private static final String LOGTAG = "homelinks";
    
    public static enum HomeLinkActions {
        Clear,
        Edit,
        Add,
    }
    public static final String SHAREPREFRENCE_NAME = "obrowser";
    public static final String IS_FIRST_LAUNCH = "firstlaunch";
    public static final String PREF_IS_CACHED = "is_cached";
    public static final int MAX_HOMELINK_SIZE = 9;
    public static final String ILLEGAL_POSTION="-99";
    //TODO:Add a HomeLink to the database,this may 
    /**
     * @param context Context of the calling Activity. This is used to make
     *            Toast confirming that the bookmark has been added. If the
     *            caller provides null, the Toast will not be shown.
     * @param cr The ContentResolver being used to add the bookmark to the db.
     * @param url URL of the website to be bookmarked.
     * @param name Provided name for the bookmark.
     * @param thumbnail A thumbnail for the bookmark.
     * @param retainIcon Whether to retain the page's icon in the icon database.
     *            This will usually be <code>true</code> except when bookmarks
     *            are added by a settings restore agent.
     */
    public static void addHomeLink(Context context,
            ContentResolver cr, String url, String name,
            Bitmap thumbnail, boolean retainIcon, int position) {
        // Want to append to the beginning of the list
        // TODO:not dispaly homelink in history tab,so set creationTime 0
        long creationTime = new Date().getTime();
        ContentValues map = new ContentValues();
        Cursor cursor = null;
        try {
            cursor = Browser.getVisitedLike(cr, url);
            int count = cursor.getCount();
            boolean matchedTitle = false;
            for (int i = 0; i < count; i++) {
                // One bookmark or homelink already exist for this site.
                // Check the names of each
                cursor.moveToPosition(i);
                if (cursor.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX)
                        .equals(name)) {
                    // The old homelink has the same name.
                    // Update its creation time.
                    map.put(Browser.BookmarkColumns.CREATED, creationTime);
                    map.put(Browser.BookmarkColumns.THUMBNAIL, bitmapToBytes(thumbnail));
                        
                    map.put("description", "position:"+position);
                    map.put("is_homelink", 1);
                        
                    cr.update(Browser.BOOKMARKS_URI, map, "_id = " + cursor.getInt(0), null);
                    matchedTitle = true;
                    break;
                }
            }
            if (!matchedTitle) {
                // Adding a homelink for a site the user has visited,
                // or a new homelink (with a different name) for a site
                // the user has visited
                map.put(Browser.BookmarkColumns.TITLE, name);
                map.put(Browser.BookmarkColumns.URL, url);
                map.put(Browser.BookmarkColumns.CREATED, creationTime);
                map.put("is_homelink", 1);
                map.put(Browser.BookmarkColumns.DATE, 0);
                map.put(Browser.BookmarkColumns.THUMBNAIL,
                        bitmapToBytes(thumbnail));
                    
                map.put("description", "position:"+position);
                int visits = 0;
                if (count > 0) {
                    visits = cursor.getInt(
                            Browser.HISTORY_PROJECTION_VISITS_INDEX);
                }
                map.put(Browser.BookmarkColumns.VISITS, visits);
                cr.insert(Browser.BOOKMARKS_URI, map);
            }
        } catch (IllegalStateException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (retainIcon) {
            WebIconDatabase.getInstance().retainIconForPageUrl(url);
        }
        if (context != null) {
            //TODO: internationalize
            Toast.makeText(context, "add homelink sucess",
                    Toast.LENGTH_LONG).show();
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

    // TODO: homelink init for first lauch browser activty
    public static void homeLinkInit(Context context, ContentResolver cr) {
        //Obsolete.
        if (false) {
            Cursor cursor = null;
            try {
                String orderBy = Browser.BookmarkColumns.VISITS + " DESC";
                cursor = cr.query(Uri.parse("content://browser/bookmarks"),
                        null, null, null, orderBy);
                if (cursor != null) {
                    int count = cursor.getCount();
                    for (int i = 0; i < count; i++) {
                        if (i == MAX_HOMELINK_SIZE - 1) {
                            break;
                        }
                        cursor.moveToPosition(i);

                        String url = cursor.getString(cursor.getColumnIndex(BookmarkColumns.URL));
                        byte[] bitmap = cursor
                                .getBlob(cursor.getColumnIndex(BookmarkColumns.THUMBNAIL));
                        addHomeLink(null, cr, cursor
                                .getString(cursor.getColumnIndex(BookmarkColumns.URL)),
                                cursor.getString(cursor.getColumnIndex(BookmarkColumns.TITLE)),
                                bitmap == null ? null : BitmapFactory.decodeByteArray(bitmap, 0,
                                        bitmap.length),
                                true, i);
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public static String getAvailablePosition(ContentResolver cr) {
        Cursor cursor = null;
        String[] posArray = new String[MAX_HOMELINK_SIZE];
        try {
            cursor = cr.query(
                    Browser.HOMELINKS_URI, null,
                    null,
                    null,
                    null);
            if (cursor != null) {
                int count = cursor.getCount();
                for (int i = 0; i < count; i++) {
                    cursor.moveToPosition(i);
                    int pos = cursor.getInt(cursor.getColumnIndex("position"));
                    posArray[pos] = "1";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        for (int i = 0; i < MAX_HOMELINK_SIZE; i++){
            if (posArray[i] == null){
                return String.valueOf(i);
            }
        }
        return ILLEGAL_POSTION;
    }
    /**
     *  Return a cursor pointing to a list of all visited site urls.
     *  Requires {@link android.Manifest.permission#READ_HISTORY_BOOKMARKS}
     *  @param cr   The ContentResolver used to access the database.
     */
    public static final Cursor getAllVisitedUrlsOderByVisit(ContentResolver cr) throws
            IllegalStateException {
        return cr.query(Browser.BOOKMARKS_URI,null, null, null, BookmarkColumns.VISITS + " DESC");
    }

    public static final Cursor getAllHomeLink(ContentResolver cr) throws
            IllegalStateException {
        return cr.query(Browser.HOMELINKS_URI,null, null, null, null);
    }
    
    public static boolean checkIsExisted(String url, ContentResolver cr) {
        Cursor cursor = null;
        try {
            cursor =cr.query(
                    Browser.HOMELINKS_URI, null,
                    "url = '" + url+"'",
                    null,
                    null);
            if (cursor != null) {
                return cursor.getCount() > 0 ? true : false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static int getHomelinkSize(ContentResolver cr) {
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    Browser.HOMELINKS_URI, null, null, null,
                    null);
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
    //TODO:copy from Bookmarks
    /**
     *  Remove a bookmark from the database.  If the url is a visited site, it
     *  will remain in the database, but only as a history item, and not as a
     *  bookmarked site.
     *  @param context Context of the calling Activity.  This is used to make
     *          Toast confirming that the bookmark has been removed.  If the
     *          caller provides null, the Toast will not be shown.
     *  @param cr The ContentResolver being used to remove the bookmark.
     *  @param url URL of the website to be removed.
     */
    public static void removeFromHomelinks(Context context,
            ContentResolver cr, String url, String title) {
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    Browser.BOOKMARKS_URI,
                    Browser.HISTORY_PROJECTION,
                    "url = ? AND title = ? AND is_homelink = ?",
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
            cr.delete(uri, null, null);
            if (context != null) {
              //TODO: internationalize
                Toast.makeText(context, "removed from homelinks",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IllegalStateException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //TODO: get all homelink positon 
    public static String getHomeLinkPositions(Context context) {
        Cursor cursor = null;
        int[] posArray = new int[MAX_HOMELINK_SIZE];
        int size = 0;
        try {
            cursor = context.getContentResolver().query(
                    Browser.HOMELINKS_URI, null,
                    null,
                    null,
                    null);
            if (cursor != null) {
                int count = cursor.getCount();
                for (int i = 0; i < count; i++) {
                    cursor.moveToPosition(i);
                    posArray[i] = cursor.getInt(cursor.getColumnIndex("position"));
                }
                size = count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //sort
        for (int i = 1; i < size; i++){
            int t = posArray[i];
            int j = i;
            for(; j > 0 && posArray[j - 1] > t; j--) {
                posArray[j] = posArray[j - 1];
            }
            posArray[j] = t;
        }
        String positions = "";
        for (int i = 0; i < size; i++){
            positions += posArray[i] + ";";
        }
        return positions;
    }

    //copy from OrangeApps
    private static int sIconWidth = -1;
    private static int sIconHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconTextureHeight = -1;
    private static DisplayMetrics sMetrics;
    private static final Rect sOldBounds = new Rect();
    public static final String POSITION = "position";
    
    private static void initStatics(Context context) {
        final Resources resources = context.getResources();
        sMetrics = resources.getDisplayMetrics();
        sIconWidth = sIconHeight = (int) resources.getDimension(android.R.dimen.app_icon_size);
        sIconTextureWidth = sIconTextureHeight = sIconWidth + 2;
    }
    public static Bitmap createIconBitmap(Drawable icon, Context context) {
        if (sIconWidth == -1) {
            initStatics(context);
        }

        int width = sIconWidth;
        int height = sIconHeight;

        if (icon instanceof PaintDrawable) {
            PaintDrawable painter = (PaintDrawable) icon;
            painter.setIntrinsicWidth(width);
            painter.setIntrinsicHeight(height);
        } else if (icon instanceof BitmapDrawable) {
            // Ensure the bitmap has a density.
            BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                bitmapDrawable.setTargetDensity(sMetrics);
            }
        }
        int sourceWidth = icon.getIntrinsicWidth();
        int sourceHeight = icon.getIntrinsicHeight();

        if (sourceWidth > 0 && sourceWidth > 0) {
            // There are intrinsic sizes.
            if (width < sourceWidth || height < sourceHeight) {
                // It's too big, scale it down.
                final float ratio = (float) sourceWidth / sourceHeight;
                if (sourceWidth > sourceHeight) {
                    height = (int) (width / ratio);
                } else if (sourceHeight > sourceWidth) {
                    width = (int) (height * ratio);
                }
            } else if (sourceWidth < width && sourceHeight < height) {
                // It's small, use the size they gave us.
                width = sourceWidth;
                height = sourceHeight;
            }
        }

        // no intrinsic size --> use default size
        int textureWidth = sIconTextureWidth;
        int textureHeight = sIconTextureHeight;

        final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);

        final int left = (textureWidth - width) / 2;
        final int top = (textureHeight - height) / 2;

        sOldBounds.set(icon.getBounds());
        icon.setBounds(left, top, left + width, top + height);
        icon.draw(canvas);
        icon.setBounds(sOldBounds);

        return bitmap;
    }

    public static boolean isFull(ContentResolver cr) {
//        Cursor cursor = null;
//        try {
//            cursor = cr.query(
//                    Browser.HOMELINKS_URI, null, null, null,
//                    null);
//            if (cursor != null) {
//                return cursor.getCount() >= MAX_HOMELINK_SIZE;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
        return false;
    }

    public static boolean saveHomeLink(Context context, String url, String title) {
        ContentResolver cr = context.getContentResolver();
        if (HomeLinks.checkIsExisted(url, cr)) {
            Toast.makeText(context, context.getResources().getString(R.string.homelinks_err_exist),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (HomeLinks.isFull(cr)) {
            Toast.makeText(context, context.getResources().getString(R.string.homelinks_err_full), 
                    Toast.LENGTH_LONG).show();
            return false;
        }
        DataController.getInstance(context).addToHomeLink(url, title);
        Toast.makeText(context, context.getResources().getString(R.string.homelinks_err_add_ok), Toast.LENGTH_LONG).show();
        return true;
    }

    public static boolean deleteHomeLink(Context context, String url) {
        ContentResolver cr = context.getContentResolver();
        DataController.getInstance(context).removeHomeLink(url);
        Toast.makeText(context, context.getResources().getString(R.string.homelinks_err_del_ok), Toast.LENGTH_LONG).show();
        return true;
    }

    public static boolean editHomeLink(Context context, String url, String newUrl, String newTitle) {
        ContentResolver cr = context.getContentResolver();
        DataController.getInstance(context).editHomeLink(url, newUrl, newTitle);
        Toast.makeText(context, context.getResources().getString(R.string.homelinks_err_edit_ok), Toast.LENGTH_LONG).show();
        return true;
    }

}
