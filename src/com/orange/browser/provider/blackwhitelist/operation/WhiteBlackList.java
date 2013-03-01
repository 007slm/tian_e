
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dailystudio.development.Logger;
import com.orange.browser.R;
import com.orange.browser.Utils;
import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WhiteBlackList {
    public static final int TYPE_WHITE = 1;
    public static final int TYPE_BLACK = 2;

    public static String BLACKLIST_VERSION = "black_version";
    public static String WHITELIST_VERSION = "white_version";
    public static String WHITELIST_TYPE = "multiselect_whitelist";
    public static String BLACKLIST_TYPE = "multiselect_blacklist";

    private static WhiteBlackList sWhiteList = new WhiteBlackList(TYPE_WHITE);
    private static WhiteBlackList sBlackList = new WhiteBlackList(TYPE_BLACK);

    protected AbsList<ListObject> mInclusionList;
    protected AbsList<ListObject> mExclusionList;

    private WhiteBlackList(int type) {
        if (TYPE_WHITE == type) {
            mInclusionList = new InclusionWhiteList();
            mExclusionList = new ExclusionWhiteList();
        } else if (TYPE_BLACK == type) {
            mInclusionList = new InclusionBlackList();
            mExclusionList = new ExclusionBlackList();
        }
    }

    public static WhiteBlackList getWhiteList() {
        return sWhiteList;
    }

    public static WhiteBlackList getBlackList() {
        return sBlackList;
    }

    public boolean isInList(Context context, String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);
            return isInList(context, url);
        } catch (MalformedURLException e) {
            Logger.warnning("string to URL failure: %s",
                    e.toString());
            return false;
        }
    }

    public boolean isInList(Context context, URL url) {
        if (context == null || url == null) {
            return false;
        }
        if (mInclusionList == null || mExclusionList == null) {
            return false;
        }
        if (mInclusionList.contains(context, url) && !mExclusionList.contains(context, url)) {
            return true;
        }
        return false;
    }


    public void saveBlackWhiteListToDatabase(Context context, String content) {
        mInclusionList.saveBlackWhiteListToDatabase(context, content);
        mExclusionList.saveBlackWhiteListToDatabase(context, content);
    }

    public static String downloadBlackWhiteListContent(String type, String versionKey,
            Context context) {
        DefaultHttpClient client = new DefaultHttpClient();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int localVersionCode = sp.getInt(versionKey, 0);
        String requestUrl = Utils.parseHomepage(context)
                + context.getString(R.string.blackwhitelist_url_patchsegment, type,
                localVersionCode);
        HttpGet httpGet = new HttpGet(requestUrl);
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
            }
            InputStream content = response.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String s;
            StringBuilder respStringBuilder = new StringBuilder();
            while ((s = buffer.readLine()) != null) {
                respStringBuilder.append(s);
            }
            return respStringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void synchronizeBlackWhiteListWithServer(Context context) {
        String whiteListContent = WhiteBlackList.downloadBlackWhiteListContent(
                WhiteBlackList.WHITELIST_TYPE,
                WhiteBlackList.WHITELIST_VERSION, context);
        if (null != whiteListContent) {
            WhiteBlackList.getWhiteList().saveBlackWhiteListToDatabase(
                    context,
                    whiteListContent);
        }
        String blackListContent = WhiteBlackList.downloadBlackWhiteListContent(
                WhiteBlackList.BLACKLIST_TYPE,
                WhiteBlackList.BLACKLIST_VERSION, context);
        if (null != blackListContent) {
            WhiteBlackList.getBlackList().saveBlackWhiteListToDatabase(
                    context,
                    blackListContent);
        }
    }
}
