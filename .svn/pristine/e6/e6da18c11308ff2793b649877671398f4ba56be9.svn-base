/*
 *
 */

package com.orange.browser.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for Orange Browser manipulation
 */
public class OBrowserUtils {

    private OBrowserUtils() { /* cannot be instantiated */ }
    /**
     * Concatenates two SQL WHERE clauses, handling empty or null values.
     */
    public static String concatenateWhere(String a, String b) {
        if (TextUtils.isEmpty(a)) {
            return b;
        }
        if (TextUtils.isEmpty(b)) {
            return a;
        }

        return "(" + a + ") AND (" + b + ")";
    }

    /**
     * Appends one set of selection args to another. This is useful when adding a selection
     * argument to a user provided set.
     */
    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        String[] result = new String[originalValues.length + newValues.length ];
        System.arraycopy(originalValues, 0, result, 0, originalValues.length);
        System.arraycopy(newValues, 0, result, originalValues.length, newValues.length);
        return result;
    }

    public static String trim(String str){
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static Bitmap createScreenshot(View view, int thumbnailWidth, int thumbnailHeight,
            int left, int top) {
        int width = view.getWidth() > thumbnailWidth ? thumbnailWidth : view.getWidth();
        int height = view.getHeight() > thumbnailHeight ? thumbnailHeight : view.getHeight();
        if (view != null) {
            Bitmap capture;
            try {
                capture = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
            Canvas c = new Canvas(capture);
            c.translate(-left, -top);
            try {
                view.draw(c);
            } catch (Exception e) {
            }
            return capture;
        }
        return null;
    }

    public static boolean saveScreenshot(Activity activity, String fileName, String ext,
            Bitmap screenshot, boolean sdcard) {
        try {
            FileOutputStream fos = null;
            if (!sdcard) {
                fos = activity.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            } else {
                File f = new File(fileName);
                f.createNewFile();
                fos = new FileOutputStream(f);
            }
            if (ext.equalsIgnoreCase("png")) {
                screenshot.compress(Bitmap.CompressFormat.PNG, 50, fos);
            } else if (ext.equalsIgnoreCase("jpeg")) {
                screenshot.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            }
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showToast(Context context, int msgId) {
        Toast.makeText(context, context.getString(msgId), Toast.LENGTH_SHORT).show();
    }
}
