package com.orange.browser;

import android.content.Context;
import android.net.Uri;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for Url manipulation
 */
public class UrlUtils {

    static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile(
            "(?i)" + // switch on case insensitive matching
            "(" +    // begin group for schema
            "(?:http|https|file):\\/\\/" +
            "|(?:inline|data|about|javascript):" +
            ")" +
            "(.*)" );

    static final Pattern ARTICLE_URL = Pattern.compile("[0-9]{6}");
    // Google search
    private final static String QUICKSEARCH_G = "http://www.google.com/m?q=%s";
    private final static String QUERY_PLACE_HOLDER = "%s";

    private final static String ANALYTICS_PREFIX = "http://go.redirectingat.com?id=30979X886011&xs=1&url=";
    private final static String ANALYTICS_CONTAINS = "redirectingat";

    // Regular expression to strip http:// and optionally
    // the trailing slash
    private static final Pattern STRIP_URL_PATTERN =
            Pattern.compile("^http://(.*?)/?$");

    private static final String WHITELIST_URL = "france24|lesechos|lexpress|liberation|espn|wikipedia";
    private static final Pattern WHITELIST_PATTERN = Pattern.compile(WHITELIST_URL,
            Pattern.CASE_INSENSITIVE);


    private UrlUtils() { /* cannot be instantiated */ }

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
    public static String stripUrl(String url) {
        if (url == null) {
            return null;
        }

        String originUrl = url;

        if (originUrl.contains(ANALYTICS_CONTAINS)) {
            url = originUrl.substring(originUrl.lastIndexOf("http%3A%2F%2F"));
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                url = originUrl;
            }
        }

        Matcher m = STRIP_URL_PATTERN.matcher(url);
        if (m.matches()) {
            return m.group(1);
        } else {
            return url;
        }
    }

    public static String encodeUrl(String url) {
        if (url == null) {
            return null;
        }

        if (!BrowserSettings.getInstance().getAnalyticFeature()) {
            return url;
        }


        if (url != null) {

            Matcher matcher = WHITELIST_PATTERN.matcher(url);
            boolean result = matcher.find();
            if (result) {
                return url;
            }

        	String encodeUrl = null;
            try {
                encodeUrl = ANALYTICS_PREFIX + URLEncoder.encode(url, "UTF-8");
    		} catch (UnsupportedEncodingException e) {
    			encodeUrl = url;
    		}

            url = encodeUrl;
        }

        return url;
    }

    protected static String smartUrlFilter(Uri inUri) {
        if (inUri != null) {
            return smartUrlFilter(inUri.toString());
        }
        return null;
    }

    /**
     * Attempts to determine whether user input is a URL or search
     * terms.  Anything with a space is passed to search.
     *
     * Converts to lowercase any mistakenly uppercased schema (i.e.,
     * "Http://" converts to "http://"
     *
     * @return Original or modified URL
     *
     */
    public static String smartUrlFilter(String url) {
        return smartUrlFilter(url, true);
    }

    /**
     * Attempts to determine whether user input is a URL or search
     * terms.  Anything with a space is passed to search if canBeSearch is true.
     *
     * Converts to lowercase any mistakenly uppercased schema (i.e.,
     * "Http://" converts to "http://"
     *
     * @param canBeSearch If true, will return a search url if it isn't a valid
     *                    URL. If false, invalid URLs will return null
     * @return Original or modified URL
     *
     */
    public static String smartUrlFilter(String url, boolean canBeSearch) {
        String inUrl = url.trim();
        boolean hasSpace = inUrl.indexOf(' ') != -1;

        Matcher matcher = ACCEPTED_URI_SCHEMA.matcher(inUrl);
        if (matcher.matches()) {
            // force scheme to lowercase
            String scheme = matcher.group(1);
            String lcScheme = scheme.toLowerCase();
            if (!lcScheme.equals(scheme)) {
                inUrl = lcScheme + matcher.group(2);
            }
            if (hasSpace && Patterns.WEB_URL.matcher(inUrl).matches()) {
                inUrl = inUrl.replace(" ", "%20");
            }
            return inUrl;
        }
        if (!hasSpace) {
            if (Patterns.WEB_URL.matcher(inUrl).matches()) {
                return URLUtil.guessUrl(inUrl);
            }
        }
        if (canBeSearch) {
            return URLUtil.composeSearchUrl(inUrl,
                    QUICKSEARCH_G, QUERY_PLACE_HOLDER);
        }
        return null;
    }

    /* package */ public static String fixUrl(String inUrl) {
        // FIXME: Converting the url to lower case
        // duplicates functionality in smartUrlFilter().
        // However, changing all current callers of fixUrl to
        // call smartUrlFilter in addition may have unwanted
        // consequences, and is deferred for now.
        int colon = inUrl.indexOf(':');
        boolean allLower = true;
        for (int index = 0; index < colon; index++) {
            char ch = inUrl.charAt(index);
            if (!Character.isLetter(ch)) {
                break;
            }
            allLower &= Character.isLowerCase(ch);
            if (index == colon - 1 && !allLower) {
                inUrl = inUrl.substring(0, colon).toLowerCase()
                        + inUrl.substring(colon);
            }
        }
        if (inUrl.startsWith("http://") || inUrl.startsWith("https://")) {
            return inUrl;
        }
        if (inUrl.startsWith("http:") ||
                inUrl.startsWith("https:")) {
            if (inUrl.startsWith("http:/") || inUrl.startsWith("https:/")) {
                inUrl = inUrl.replaceFirst("/", "//");
            } else {
                inUrl = inUrl.replaceFirst(":", "://");
            }
        }
        return inUrl;
    }

    // Returns the filtered URL. Cannot return null, but can return an empty string
    /* package */ static String filteredUrl(String inUrl) {
        if (inUrl == null) {
            return "";
        }
        if (inUrl.startsWith("content:")
                || inUrl.startsWith("browser:")) {
            return "";
        }
        return inUrl;
    }

    public static boolean isArticleAccordingtoUrl(String url) {
        if (null == url) {
            return false;
        }
        Matcher matcher = ARTICLE_URL.matcher(url);
        if (matcher != null && matcher.find()) {
            return true;
        }

        return false;
    }
}
