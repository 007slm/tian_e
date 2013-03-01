
package com.orange.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

import com.orange.browser.provider.blackwhitelist.operation.WhiteBlackList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Utils {
    public static Bitmap createScreenshot(WebView view, int thumbnailWidth, int thumbnailHeight) {
        if (view != null) {
            Bitmap mCapture;
            try {
                mCapture = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight,
                        Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                return null;
            }
            Canvas c = new Canvas(mCapture);
            final int left = view.getScrollX();
            final int top = view.getScrollY();
            c.translate(-left, -top);
            c.scale(0.65f, 0.65f, left, top);
            try {
                // draw webview may nullpoint
                view.draw(c);
            } catch (Exception e) {
            }
            return mCapture;
        }
        return null;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static boolean isInWhiteList(Context context, String url) {
        return WhiteBlackList.getWhiteList().isInList(context, url);
    }

    public static boolean isInBlackList(Context context, String url) {
        return WhiteBlackList.getBlackList().isInList(context, url);
    }

    public static Bitmap createScreenshot(WebView view) {
        return createScreenshot(view, (int) (view.getWidth() * 0.65f),
                (int) (view.getHeight() * 0.65f));
    }

    // 将图片的四角圆化
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPx, boolean needAlpha) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        // 得到画布
        Canvas canvas = new Canvas(output);

        // 将画布的四角圆化
        final int color = Color.RED;
        final Paint paint = new Paint();
        // 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // 值越大角度越明显
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (needAlpha) {
            canvas.drawColor(Color.argb(255, 99, 99, 99), Mode.MULTIPLY);
        }
        return output;
    }

    public static String getSimCountry(Context context) {
        String mcc = null;

        TelephonyManager telmgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (telmgr != null) {
            if (telmgr.getSimState() != TelephonyManager.SIM_STATE_READY) {
                return null;
            }

            mcc = telmgr.getSimCountryIso();

            if (mcc != null && mcc.length() == 0) {
                mcc = null;
            }
        }
        return mcc;
    }

    public final static int INDEX_DEFAULT_COUNTRY = 0;
    public final static String JSON_HOME_URL_KEY = "home_url";
    public final static String JSON_COUNTRY_KEY = "country";
    public final static String JSON_LANGUAGE_KEY = "language";

    public static String parseHomepage(Context context) {
        String simCountry = getSimCountry(context);
        JSONArray jsonArray = null;
        String homepageUrl = null;
        String defaultHomePageUrl = null;
        try {
            String homepageContentJson = context.getString(R.string.homepage_base_json);
            jsonArray = new JSONArray(homepageContentJson);
            defaultHomePageUrl = jsonArray.getJSONObject(INDEX_DEFAULT_COUNTRY).getString(
                    JSON_HOME_URL_KEY);
            if (null != simCountry) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String country = jsonObject.getString(JSON_COUNTRY_KEY);
                    if (simCountry.equals(country)) {
                        homepageUrl = jsonObject.getString(JSON_HOME_URL_KEY);
                    }
                }
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String languageJson = jsonObject.getString(JSON_LANGUAGE_KEY);
                    if (languageJson.equals(Locale.getDefault().getLanguage())) {
                        homepageUrl = jsonObject.getString(JSON_HOME_URL_KEY);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null == homepageUrl) {
            homepageUrl = defaultHomePageUrl;
        }
        return homepageUrl;
    }

}
