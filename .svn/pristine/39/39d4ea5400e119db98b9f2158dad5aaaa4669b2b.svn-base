
package com.orange.browser.provider.blackwhitelist.operation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.orange.browser.provider.blackwhitelist.databaseObject.ListObject;
import com.orange.browser.provider.blackwhitelist.json.JSonList;
import com.orange.browser.provider.blackwhitelist.json.JSonListObject;
import com.orange.browser.provider.blackwhitelist.json.JSonWhiteBlackListParser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbsList<T extends ListObject> {

    public static final String MATCH_TYPE_EXACTLY = "exactly";
    public static final String MATCH_TYPE_PREFIX = "prefix";
    public static final String MATCH_TYPE_REGEX = "regex";
    public static final String WILDCARD_HOST = "*";

    private final static String DATABASE_AUTHORITY = "com.orange.browser.blackwhitelist";

    public boolean contains(Context context, URL url) {
        if (context == null || url == null) {
            return false;
        }

        final String host = url.getHost();

        List<T> matherCandidates = findInDatabase(context, host);

        if (matherCandidates != null) {
            final String urlstr = url.toString();
            if (urlstr == null) {
                return false;
            }

            for (T candidate : matherCandidates) {
                if (matchUrl(candidate, urlstr)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchUrl(T candidiate, String url) {
        if (candidiate == null || url == null) {
            return false;
        }

        final String matchType = candidiate.getMatchType();
        final String urlPattern = candidiate.getUrlPattern();
        if (matchType == null || urlPattern == null) {
            return false;
        }

        boolean ret = false;
        if (MATCH_TYPE_EXACTLY.equals(matchType)) {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            ret = url.equals(urlPattern);
        } else if (MATCH_TYPE_PREFIX.equals(matchType)) {
            ret = url.startsWith(urlPattern);
        } else if (MATCH_TYPE_REGEX.equals(matchType)) {
            ret = Pattern.matches(urlPattern, url);
        }

        return ret;
    }
 
    
    public List<T> findInDatabase(Context context, String host) {
        if (context == null || host == null) {
            return null;
        }

        return queryPrefixInDatabase(context, host);
    }

    @SuppressWarnings("unchecked")
    private List<T> queryPrefixInDatabase(Context context, String host) {
        if (context == null || host == null) {
            return null;
        }

        DatabaseConnectivity connectivity =
                new DatabaseConnectivity(context,
                        DATABASE_AUTHORITY,
                        getListObjectClass());

        Query query = new Query(getListObjectClass());

        ExpressionToken selToken =
                ListObject.COLUMN_HOST.eq(host)
                        .or(ListObject.COLUMN_HOST.eq("*"));
        if (selToken == null) {
            return null;
        }

        query.setSelection(selToken);

        List<DatabaseObject> objects = connectivity.query(query);
        if (objects == null || objects.size() <= 0) {
            return null;
        }

        List<T> matcherCandidiates = new ArrayList<T>();

        for (DatabaseObject o : objects) {
            matcherCandidiates.add((T) o);
        }

        /*
         * Logger.debug("host:%s, cached[%s]", host, matcherCandidiates );
         */
        return matcherCandidiates;
    }

    private void cachePrefixesInDatabase(Context context,
            String host, List<T> matcherCandidiates) {
        if (context == null || host == null || matcherCandidiates == null) {
            return;
        }

        final int N = matcherCandidiates.size();

        if (N <= 0) {
            return;
        }

        for (T candidiate : matcherCandidiates) {
            cachePrefixesInDatabase(context, candidiate.getHost(), candidiate);
        }

    }

    private void cachePrefixesInDatabase(Context context,
            String host, T candidiates) {
        DatabaseConnectivity connectivity =
                new DatabaseConnectivity(context,
                        DATABASE_AUTHORITY,
                        getListObjectClass());

        Query query = new Query(getListObjectClass());

        ExpressionToken selToken =
                ListObject.COLUMN_HOST.eq(host)
                        .and(ListObject.COLUMN_MATCH_TYPE.eq(candidiates.getMatchType()))
                        .and(ListObject.COLUMN_URL_PATTERN.eq(candidiates.getUrlPattern()));
        if (selToken == null) {
            return;
        }
        query.setSelection(selToken);
        List<DatabaseObject> objects = connectivity.query(query);
        // the record has already exists,don't insert repeatly
        if (null != objects && objects.size() > 0) {
            return;
        }
        connectivity.insert(candidiates);
    }

    private StringBuilder getDatabasePath(Context context, StringBuilder builder) {
        return builder.append("/data/data/").append(context.getPackageName())
                .append("/databases/");
    }

    private void removeDatabaseFileIfExist(Context context) {
        StringBuilder builder = new StringBuilder();
        String dir = getDatabasePath(context, builder)
                .append(getListObjectClass().getName())
                .append(".db")
                .toString();
        File file = new File(dir);
        if (file.exists()) {
            file.delete();
        }
    }

    private void saveBlackWhiteListVersion(Context context, JSonList jsonList) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(getVersionKey(), jsonList.version).commit();
    }

    public void saveBlackWhiteListToDatabase(Context context, String content) {
        removeDatabaseFileIfExist(context);
        JSonList jsonList = parseFromString(context, content);
        if (jsonList == null) {
            return;
        }
        saveBlackWhiteListVersion(context, jsonList);
        DatabaseConnectivity connectivity =
                new DatabaseConnectivity(context,
                        DATABASE_AUTHORITY,
                        getListObjectClass());

        JSonListObject[] jsonObjects =
                pickJSonObjects(jsonList);
        if (jsonObjects == null) {
            return;
        }

        URL jsonObjectUrl;
        String jsonObjectHost;
        T databaseObject;
        for (JSonListObject jsonListObject : jsonObjects) {
            jsonObjectHost = jsonListObject.url_host;

            if (jsonObjectHost == null) {
                try {
                    jsonObjectUrl = new URL(jsonListObject.url_pattern);
                } catch (MalformedURLException e) {
                    Logger.warnning("string to URL failure: %s",
                            e.toString());
                    jsonObjectUrl = null;
                }

                if (jsonObjectUrl != null) {
                    jsonObjectHost = jsonObjectUrl.getHost();
                }
            }

            if (jsonObjectHost == null) {
                continue;
            }

            databaseObject = createListObject(context);

            databaseObject.setHost(jsonObjectHost);
            databaseObject.setMatchType(jsonListObject.match_type);
            databaseObject.setUrlPattern(jsonListObject.url_pattern);

            connectivity.insert(databaseObject);
        }

    }

    public JSonList parseFromString(Context context, String content) {
        return JSonWhiteBlackListParser.parseFromString(context, content);
    }

    abstract public Class<? extends ListObject> getListObjectClass();

    abstract public T createListObject(Context context);

    abstract public JSonListObject[] pickJSonObjects(JSonList jsonList);

    abstract public String getVersionKey();

}
