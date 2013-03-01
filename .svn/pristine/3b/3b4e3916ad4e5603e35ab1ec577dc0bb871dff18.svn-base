package com.orange.browser;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Browser;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;

import com.orange.browser.reflect.InvokeWebSettingsMethod;
import com.orange.browser.search.SearchEngine;
import com.orange.browser.search.SearchEngines;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.WeakHashMap;

//import com.android.browser.WebSettings.AutoFillProfile;
/**
 * Class for managing settings
 */
public class BrowserSettings extends Observable implements OnSharedPreferenceChangeListener,
        PreferenceKeys {

    // TODO: Do something with this UserAgent stuff
    private static final String DESKTOP_USERAGENT = "Mozilla/5.0 (X11; " +
        "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) " +
        "Chrome/11.0.696.34 Safari/534.24";

    private static final String IPHONE_USERAGENT = "Mozilla/5.0 (iPhone; U; " +
        "CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 " +
        "(KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";

    private static final String IPAD_USERAGENT = "Mozilla/5.0 (iPad; U; " +
        "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 " +
        "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    private static final String FROYO_USERAGENT = "Mozilla/5.0 (Linux; U; " +
        "Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 " +
        "(KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    private static final String HONEYCOMB_USERAGENT = "Mozilla/5.0 (Linux; U; " +
        "Android 3.1; en-us; Xoom Build/HMJ25) AppleWebKit/534.13 " +
        "(KHTML, like Gecko) Version/4.0 Safari/534.13";

    private static final String USER_AGENTS[] = { null,
            DESKTOP_USERAGENT,
            IPHONE_USERAGENT,
            IPAD_USERAGENT,
            FROYO_USERAGENT,
            HONEYCOMB_USERAGENT,
    };

    // The minimum min font size
    // Aka, the lower bounds for the min font size range
    // which is 1:5..24
    private static final int MIN_FONT_SIZE_OFFSET = 5;
    // The initial value in the text zoom range
    // This is what represents 100% in the SeekBarPreference range
    private static final int TEXT_ZOOM_START_VAL = 10;
    // The size of a single step in the text zoom range, in percent
    private static final int TEXT_ZOOM_STEP = 5;
    // The initial value in the double tap zoom range
    // This is what represents 100% in the SeekBarPreference range
    private static final int DOUBLE_TAP_ZOOM_START_VAL = 5;
    // The size of a single step in the double tap zoom range, in percent
    private static final int DOUBLE_TAP_ZOOM_STEP = 5;
    // Value to truncate strings when adding them to a TextView within
    // a ListView
    public final static int MAX_TEXTVIEW_LEN = 80;

    private static BrowserSettings sInstance;

    private static Context mContext;
    private SharedPreferences mPrefs;
    private LinkedList<WeakReference<WebSettings>> mManagedSettings;
    private Controller mController;
    private WebStorageSizeManager mWebStorageSizeManager;
    //private AutofillHandler mAutofillHandler;
    private WeakHashMap<WebSettings, String> mCustomUserAgents;
    private static boolean sInitialized = false;
    private boolean mNeedsSharedSync = true;
    private float mFontSizeMult = 1.0f;

    // Cached values
    private int mPageCacheCapacity = 1;
    private String mAppCachePath;

    // Cached settings
    private SearchEngine mSearchEngine;

    private static String sFactoryResetUrl;

    //add by gaoge 2012-02-03,for add BrowserSettings Preference start
    public final static String PREF_EXTRAS_RESET_DEFAULTS =
            "reset_default_preferences";
    public final static String MORE_APP =
            "gfan_more_app";


    // Preference keys that are used outside this class
    public final static String PREF_CLEAR_CACHE = "privacy_clear_cache";
    public final static String PREF_CLEAR_COOKIES = "privacy_clear_cookies";
    public final static String PREF_CLEAR_HISTORY = "privacy_clear_history";
    public final static String PREF_HOMEPAGE = "homepage";
    public final static String PREF_SEARCH_ENGINE = "search_engine";
    public final static String PREF_CLEAR_FORM_DATA =
            "privacy_clear_form_data";
    public final static String PREF_CLEAR_PASSWORDS =
            "privacy_clear_passwords";
    public final static String PREF_CLEAR_GEOLOCATION_ACCESS =
            "privacy_clear_geolocation_access";

    void clearCache(Context context) {
        WebIconDatabase.getInstance().removeAllIcons();
        if (mTabControl != null) {
            WebView current = mTabControl.getCurrentWebView();
            if (current != null) {
                current.clearCache(true);
            }
        }
    }

    /*package*/ void clearDatabases(Context context) {
        WebStorage.getInstance().deleteAllData();
        maybeDisableWebsiteSettings(context);
    }

    /*package*/ void clearLocationAccess(Context context) {
        GeolocationPermissions.getInstance().clearAll();
        maybeDisableWebsiteSettings(context);
    }

    private void maybeDisableWebsiteSettings(Context context) {
        PreferenceActivity activity = (PreferenceActivity) context;
        final PreferenceScreen screen = (PreferenceScreen)
            activity.findPreference(PreferenceKeys.PREF_WEBSITE_SETTINGS);
        screen.setEnabled(false);
        WebStorage.getInstance().getOrigins(new ValueCallback<Map>() {
            @Override
            public void onReceiveValue(Map webStorageOrigins) {
                if ((webStorageOrigins != null) && !webStorageOrigins.isEmpty()) {
                    screen.setEnabled(true);
                }
            }
        });

        GeolocationPermissions.getInstance().getOrigins(new ValueCallback<Set<String> >() {
            @Override
            public void onReceiveValue(Set<String> geolocationOrigins) {
                if ((geolocationOrigins != null) && !geolocationOrigins.isEmpty()) {
                    screen.setEnabled(true);
                }
            }
        });
    }

    /*package*/ void clearCookies(Context context) {
        CookieManager.getInstance().removeAllCookie();
    }

    /* package */void clearHistory(Context context) {
        ContentResolver resolver = context.getContentResolver();
        dep.android.provider.Browser.clearHistory(resolver);
        dep.android.provider.Browser.clearSearches(resolver);
    }

    /* package */ void clearFormData(Context context) {
        WebViewDatabase.getInstance(context).clearFormData();
        if (mTabControl != null) {
            WebView currentTopView = mTabControl.getCurrentTopWebView();
            if (currentTopView != null) {
                currentTopView.clearFormData();
            }
        }
    }

    /*package*/ void clearPasswords(Context context) {
        WebViewDatabase db = WebViewDatabase.getInstance(context);
        db.clearUsernamePassword();
        db.clearHttpAuthUsernamePassword();
    }

    private WebStorageSizeManager webStorageSizeManager;
    /*package*/ void resetDefaultPreferences(Context ctx) {
        reset();
        SharedPreferences p =
            PreferenceManager.getDefaultSharedPreferences(ctx);
//        p.edit().clear().apply();
        p.edit().clear().commit();
        PreferenceManager.setDefaultValues(ctx, R.xml.browser_preferences,
                true);
        // reset homeUrl
        setHomePage(ctx, getFactoryResetHomeUrl(ctx));
        // reset appcache max size
        appCacheMaxSize = webStorageSizeManager.getAppCacheMaxSize();
    }

    public void setHomePage(Context context, String url) {
        Editor ed = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        ed.putString(PREF_HOMEPAGE, url);
//        ed.apply();
        ed.commit();
        homeUrl = url;
    }



    private final static String TAG = "BrowserSettings";
    private boolean useWideViewPort = true;
    private int userAgent = 0;
    private boolean tracing = false;
    private boolean lightTouch = false;
    private boolean navDump = false;
    private boolean loadsImagesAutomatically;
    private boolean javaScriptEnabled;
    private WebSettings.PluginState pluginState;
    private boolean javaScriptCanOpenWindowsAutomatically;
    private boolean showSecurityWarnings;
    private boolean rememberPasswords;
    private boolean saveFormData;
    private boolean openInBackground;
    private String defaultTextEncodingName;
    private String homeUrl = "";
    private SearchEngine searchEngine;
    private boolean autoFitPage;
    private boolean landscapeOnly;
    private boolean loadsPageInOverviewMode;
    // HTML5 API flags
    private boolean appCacheEnabled;
    private boolean databaseEnabled;
    private boolean domStorageEnabled;
    private boolean geolocationEnabled;
    private boolean workersEnabled;  // only affects V8. JSC does not have a similar setting
    // HTML5 API configuration params
    private long appCacheMaxSize = Long.MAX_VALUE;
    private TabControl mTabControl;
    private static WebSettings.TextSize textSize =
            WebSettings.TextSize.NORMAL;
        private static WebSettings.ZoomDensity zoomDensity =
            WebSettings.ZoomDensity.MEDIUM;
        public WebSettings.LayoutAlgorithm layoutAlgorithm =
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS;





    // By default the error console is shown once the user navigates to about:debug.
    // The setting can be then toggled from the settings menu.
    private boolean showConsole = true;
    public final static String PREF_DEBUG_SETTINGS = "debug_menu";
    private String jsFlags = "";

 // Private preconfigured values
    private static int minimumFontSize = 8;
    private static int minimumLogicalFontSize = 8;
    private static int defaultFontSize = 16;
    private static int defaultFixedFontSize = 13;
    private static int pageCacheCapacity;

    private String appCachePath;  // default value set in loadFromDb().
    private String databasePath; // default value set in loadFromDb()
    private String geolocationDatabasePath; // default value set in loadFromDb()


    void syncSharedPreferences(Context ctx, SharedPreferences p) {

        homeUrl =
            p.getString(PREF_HOMEPAGE, homeUrl);

        String defaultSearchEngine = mContext.getString(R.string.default_searchengine);
        String searchEngineName = p.getString(PREF_SEARCH_ENGINE,
                defaultSearchEngine);
        if (searchEngine == null || !searchEngine.getName().equals(searchEngineName)) {
            if (searchEngine != null) {
                if (searchEngine.supportsVoiceSearch()) {
                    // One or more tabs could have been in voice search mode.
                    // Clear it, since the new SearchEngine may not support
                    // it, or may handle it differently.
                    for (int i = 0; i < mTabControl.getTabCount(); i++) {
                        mTabControl.getTab(i).revertVoiceSearchMode();
                    }
                }
                searchEngine.close();
            }
            searchEngine = SearchEngines.get(ctx, searchEngineName);
        }
        LogHelper.d(TAG, "Selected search engine: " + searchEngine);

        //NOTE:alway true
        loadsImagesAutomatically = true;
//        loadsImagesAutomatically = p.getBoolean("load_images",
//                loadsImagesAutomatically);
        javaScriptEnabled = p.getBoolean("enable_javascript",
                javaScriptEnabled);
        pluginState = WebSettings.PluginState.valueOf(
                p.getString("plugin_state", pluginState.name()));
        javaScriptCanOpenWindowsAutomatically = !p.getBoolean(
            "block_popup_windows",
            !javaScriptCanOpenWindowsAutomatically);
        showSecurityWarnings = p.getBoolean("show_security_warnings",
                showSecurityWarnings);
        rememberPasswords = p.getBoolean("remember_passwords",
                rememberPasswords);
        saveFormData = p.getBoolean("save_formdata",
                saveFormData);
        boolean accept_cookies = p.getBoolean("accept_cookies",
                CookieManager.getInstance().acceptCookie());
        CookieManager.getInstance().setAcceptCookie(accept_cookies);
        openInBackground = p.getBoolean("open_in_background", openInBackground);
        textSize = WebSettings.TextSize.valueOf(
                p.getString(PREF_TEXT_SIZE, textSize.name()));
        zoomDensity = WebSettings.ZoomDensity.valueOf(
                p.getString(PREF_DEFAULT_ZOOM, zoomDensity.name()));
        autoFitPage = p.getBoolean("autofit_pages", autoFitPage);
        loadsPageInOverviewMode = p.getBoolean("load_page",
                loadsPageInOverviewMode);
        boolean landscapeOnlyTemp =
                p.getBoolean("landscape_only", landscapeOnly);
        if (landscapeOnlyTemp != landscapeOnly) {
            landscapeOnly = landscapeOnlyTemp;
        }
        useWideViewPort = true; // use wide view port for either setting
        if (autoFitPage) {
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
        } else {
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
        }
        defaultTextEncodingName =
                p.getString(PREF_DEFAULT_TEXT_ENCODING,
                        defaultTextEncodingName);

        // Debug menu items have precidence if the menu is visible
        if (isDebugEnabled()) {
            boolean small_screen = p.getBoolean("small_screen",
                    layoutAlgorithm ==
                    WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            if (small_screen) {
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
            } else {
                boolean normal_layout = p.getBoolean("normal_layout",
                        layoutAlgorithm == WebSettings.LayoutAlgorithm.NORMAL);
                if (normal_layout) {
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL;
                } else {
                    layoutAlgorithm =
                            WebSettings.LayoutAlgorithm.NARROW_COLUMNS;
                }
            }
            useWideViewPort = p.getBoolean("wide_viewport", useWideViewPort);
            tracing = p.getBoolean("enable_tracing", tracing);
            lightTouch = p.getBoolean("enable_light_touch", lightTouch);
            navDump = p.getBoolean("enable_nav_dump", navDump);
            userAgent = Integer.parseInt(p.getString("user_agent", "0"));
        }
        // JS flags is loaded from DB even if showDebugSettings is false,
        // so that it can be set once and be effective all the time.
        jsFlags = p.getString("js_engine_flags", "");

        // Read the setting for showing/hiding the JS Console always so that should the
        // user enable debug settings, we already know if we should show the console.
        // The user will never see the console unless they navigate to about:debug,
        // regardless of the setting we read here. This setting is only used after debug
        // is enabled.
        showConsole = p.getBoolean("javascript_console", showConsole);

        // HTML5 API flags
        appCacheEnabled = p.getBoolean("enable_appcache", appCacheEnabled);
        databaseEnabled = p.getBoolean("enable_database", databaseEnabled);
        domStorageEnabled = p.getBoolean("enable_domstorage", domStorageEnabled);
        geolocationEnabled = p.getBoolean("enable_geolocation", geolocationEnabled);
        workersEnabled = p.getBoolean("enable_workers", workersEnabled);

        update();
    }

    void update() {
        setChanged();
        notifyObservers();
    }

    /**
     * Load settings from the browser app's database.
     * NOTE: Strings used for the preferences must match those specified
     * in the browser_preferences.xml
     * @param ctx A Context object used to query the browser's settings
     *            database. If the database exists, the saved settings will be
     *            stored in this BrowserSettings object. This will update all
     *            observers of this object.
     */
    public void loadFromDb(final Context ctx) {
        SharedPreferences p =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        // Set the default value for the Application Caches path.
        appCachePath = ctx.getDir("appcache", 0).getPath();
        // Determine the maximum size of the application cache.
        webStorageSizeManager = new WebStorageSizeManager(
                ctx,
                new WebStorageSizeManager.StatFsDiskInfo(appCachePath),
                new WebStorageSizeManager.WebKitAppCacheInfo(appCachePath));
        appCacheMaxSize = webStorageSizeManager.getAppCacheMaxSize();
        // Set the default value for the Database path.
        databasePath = ctx.getDir("databases", 0).getPath();
        // Set the default value for the Geolocation database path.
        geolocationDatabasePath = ctx.getDir("geolocation", 0).getPath();

        if (p.getString(PREF_HOMEPAGE, "") == "") {
            // No home page preferences is set, set it to default.
            setHomePage(ctx, getFactoryResetHomeUrl(ctx));
        }

        // the cost of one cached page is ~3M (measured using nytimes.com). For
        // low end devices, we only cache one page. For high end devices, we try
        // to cache more pages, currently choose 5.
        ActivityManager am = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am.getMemoryClass() > 16) {
            pageCacheCapacity = 5;
        } else {
            pageCacheCapacity = 1;
        }

    // Load the defaults from the xml
        // This call is TOO SLOW, need to manually keep the defaults
        // in sync
        //PreferenceManager.setDefaultValues(ctx, R.xml.browser_preferences);
        syncSharedPreferences(ctx, p);
    }
    /**
     * Add a WebSettings object to the list of observers that will be updated
     * when update() is called.
     *
     * @param s A WebSettings object that is strictly tied to the life of a
     *            WebView.
     */
    private HashMap<WebSettings,Observer> mWebSettingsToObservers =
            new HashMap<WebSettings,Observer>();

    public Observer addObserver(WebSettings s) {
        Observer old = mWebSettingsToObservers.get(s);
        if (old != null) {
            super.deleteObserver(old);
        }
        Observer o = new Observer(s);
        mWebSettingsToObservers.put(s, o);
        super.addObserver(o);
        return o;
    }

    /**
     * Delete the given WebSettings observer from the list of observers.
     * @param s The WebSettings object to be deleted.
     */
    public void deleteObserver(WebSettings s) {
        Observer o = mWebSettingsToObservers.get(s);
        if (o != null) {
            mWebSettingsToObservers.remove(s);
            super.deleteObserver(o);
        }
    }

    /*
     * An observer wrapper for updating a WebSettings object with the new
     * settings after a call to BrowserSettings.update().
     */
    static class Observer implements java.util.Observer {
        // Private WebSettings object that will be updated.
        private WebSettings mSettings;

        Observer(WebSettings w) {
            mSettings = w;
        }

        @Override
        public void update(Observable o, Object arg) {
            BrowserSettings b = (BrowserSettings)o;
            WebSettings s = mSettings;
            s.setLayoutAlgorithm(b.layoutAlgorithm);
            if (b.userAgent == 0) {
                // use the default ua string
                s.setUserAgentString(null);

                String applicationName = mContext.getResources().getString(R.string.useragent_name);
                PackageInfo pInfo = null;
                try {
                    pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String version = pInfo != null ? pInfo.versionName : "1.0";

                s.setUserAgentString(s.getUserAgentString()+" "+applicationName+"/"+version);
            } else if (b.userAgent == 1) {
                s.setUserAgentString(DESKTOP_USERAGENT);
            } else if (b.userAgent == 2) {
                s.setUserAgentString(IPHONE_USERAGENT);
            } else if (b.userAgent == 3) {
                s.setUserAgentString(IPAD_USERAGENT);
            } else if (b.userAgent == 4) {
                s.setUserAgentString(FROYO_USERAGENT);
            }
            s.setUseWideViewPort(b.useWideViewPort);
            //NOTE:always true
            s.setLoadsImagesAutomatically(true);
            //s.setLoadsImagesAutomatically(b.loadsImagesAutomatically);
            s.setJavaScriptEnabled(b.javaScriptEnabled);
            s.setPluginState(b.pluginState);
            s.setJavaScriptCanOpenWindowsAutomatically(
                    b.javaScriptCanOpenWindowsAutomatically);
            s.setDefaultTextEncodingName(b.defaultTextEncodingName);
            s.setMinimumFontSize(BrowserSettings.minimumFontSize);
            s.setMinimumLogicalFontSize(BrowserSettings.minimumLogicalFontSize);
            s.setDefaultFontSize(BrowserSettings.defaultFontSize);
            s.setDefaultFixedFontSize(BrowserSettings.defaultFixedFontSize);
            s.setNavDump(b.navDump);
            s.setTextSize(BrowserSettings.textSize);
            s.setDefaultZoom(BrowserSettings.zoomDensity);
            s.setLightTouchEnabled(b.lightTouch);
            s.setSaveFormData(b.saveFormData);
            s.setSavePassword(b.rememberPasswords);
            s.setLoadWithOverviewMode(b.loadsPageInOverviewMode);
//            s.setPageCacheCapacity(pageCacheCapacity);

            // WebView inside Browser doesn't want initial focus to be set.
            s.setNeedInitialFocus(false);
            // Browser supports multiple windows
            s.setSupportMultipleWindows(true);
            // disable content url access
//            s.setAllowContentAccess(false);

            // HTML5 API flags
            s.setAppCacheEnabled(b.appCacheEnabled);
            s.setDatabaseEnabled(b.databaseEnabled);
            s.setDomStorageEnabled(b.domStorageEnabled);
//            s.setWorkersEnabled(b.workersEnabled);  // This only affects V8.
            s.setGeolocationEnabled(b.geolocationEnabled);

            // HTML5 configuration parameters.
            s.setAppCacheMaxSize(b.appCacheMaxSize);
            s.setAppCachePath(b.appCachePath);
            s.setDatabasePath(b.databasePath);
            s.setGeolocationDatabasePath(b.geolocationDatabasePath);

            b.updateTabControlSettings();
        }
    }

    void setTabControl(TabControl tabControl) {
        mTabControl = tabControl;
        updateTabControlSettings();
    }

    private void updateTabControlSettings() {
        // Enable/disable the error console.
        mTabControl.getController().setShouldShowErrorConsole(
                isDebugEnabled() && showConsole);
        mTabControl.getBrowserActivity().setRequestedOrientation(
            landscapeOnly ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    //add by gaoge 2012-02-03,for add BrowserSettings Preference end
    public static void initialize(final Context context) {
        sInstance = new BrowserSettings(context);
    }

    public static BrowserSettings getInstance() {
        return sInstance;
    }

    private BrowserSettings(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        //mAutofillHandler = new AutofillHandler(mContext);
        mManagedSettings = new LinkedList<WeakReference<WebSettings>>();
        mCustomUserAgents = new WeakHashMap<WebSettings, String>();
        //mAutofillHandler.asyncLoadFromDb();
        BackgroundHandler.execute(mSetup);
        reset();
    }

    private void reset() {
        // Private variables for settings
        // NOTE: these defaults need to be kept in sync with the XML
        // until the performance of PreferenceManager.setDefaultValues()
        // is improved.
        loadsImagesAutomatically = true;
        javaScriptEnabled = true;
        pluginState = WebSettings.PluginState.ON;
        javaScriptCanOpenWindowsAutomatically = false;
        showSecurityWarnings = true;
        rememberPasswords = true;
        saveFormData = true;
        openInBackground = false;
        autoFitPage = true;
        landscapeOnly = false;
        loadsPageInOverviewMode = true;
        // HTML5 API flags
        appCacheEnabled = true;
        databaseEnabled = true;
        domStorageEnabled = true;
        geolocationEnabled = true;
        workersEnabled = true;  // only affects V8. JSC does not have a similar setting
    }

    public void setController(Controller controller) {
        mController = controller;
        if (sInitialized) {
            syncSharedSettings();
        }

        //TODO
        /*
        if (mController != null && (mSearchEngine instanceof InstantSearchEngine)) {
             ((InstantSearchEngine) mSearchEngine).setController(mController);
        }
        */
    }

    public void startManagingSettings(WebSettings settings) {
        if (mNeedsSharedSync) {
            syncSharedSettings();
        }
        synchronized (mManagedSettings) {
            syncStaticSettings(settings);
            syncSetting(settings);
            mManagedSettings.add(new WeakReference<WebSettings>(settings));
        }
    }

    private Runnable mSetup = new Runnable() {

        @Override
        public void run() {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            mFontSizeMult = metrics.scaledDensity / metrics.density;
            // the cost of one cached page is ~3M (measured using nytimes.com). For
            // low end devices, we only cache one page. For high end devices, we try
            // to cache more pages, currently choose 5.
//            if (ActivityManager.staticGetMemoryClass() > 16) {
//                mPageCacheCapacity = 5;
//            }
            mWebStorageSizeManager = new WebStorageSizeManager(mContext,
                    new WebStorageSizeManager.StatFsDiskInfo(getAppCachePath()),
                    new WebStorageSizeManager.WebKitAppCacheInfo(getAppCachePath()));
            // Workaround b/5253777
            CookieManager.getInstance().acceptCookie();
            // Workaround b/5254577
            mPrefs.registerOnSharedPreferenceChangeListener(BrowserSettings.this);
            if (Build.VERSION.CODENAME.equals("REL")) {
                // This is a release build, always startup with debug disabled
                setDebugEnabled(false);
            }
            if (mPrefs.contains(PREF_TEXT_SIZE)) {
                /*
                 * Update from TextSize enum to zoom percent
                 * SMALLEST is 50%
                 * SMALLER is 75%
                 * NORMAL is 100%
                 * LARGER is 150%
                 * LARGEST is 200%
                 */
                switch (getTextSize()) {
                case SMALLEST:
                    setTextZoom(50);
                    break;
                case SMALLER:
                    setTextZoom(75);
                    break;
                case LARGER:
                    setTextZoom(150);
                    break;
                case LARGEST:
                    setTextZoom(200);
                    break;
                }
                mPrefs.edit().remove(PREF_TEXT_SIZE).commit();
//                mPrefs.edit().remove(PREF_TEXT_SIZE).apply();
            }

            sFactoryResetUrl = Utils.parseHomepage(mContext);

            /*
            if (sFactoryResetUrl.indexOf("{CID}") != -1) {
                sFactoryResetUrl = sFactoryResetUrl.replace("{CID}",
                    BrowserProvider.getClientId(mContext.getContentResolver()));
            }
            */

            synchronized (BrowserSettings.class) {
                sInitialized = true;
                BrowserSettings.class.notifyAll();
            }
        }
    };

    private static void requireInitialization() {
        synchronized (BrowserSettings.class) {
            while (!sInitialized) {
                try {
                    BrowserSettings.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Syncs all the settings that have a Preference UI
     */
    private void syncSetting(WebSettings settings) {
        settings.setGeolocationEnabled(enableGeolocation());
        settings.setJavaScriptEnabled(enableJavascript());
        settings.setLightTouchEnabled(enableLightTouch());
        settings.setNavDump(enableNavDump());
//        settings.setHardwareAccelSkiaEnabled(isSkiaHardwareAccelerated());
//        settings.setShowVisualIndicator(enableVisualIndicator());
        settings.setDefaultTextEncodingName(getDefaultTextEncoding());
        // TODO the setDefaultZoom method will cause system crash Intermittently
        try{
            settings.setDefaultZoom(getDefaultZoom());
        }catch(Exception e){
            e.printStackTrace();
        }
        settings.setMinimumFontSize(getMinimumFontSize());
        settings.setMinimumLogicalFontSize(getMinimumFontSize());
//        settings.setForceUserScalable(forceEnableUserScalable());
        settings.setPluginState(getPluginState());
        //TODO: settings.setTextZoom(getTextZoom());
//        settings.setDoubleTapZoom(getDoubleTapZoom());
//        settings.setAutoFillEnabled(isAutofillEnabled());
        settings.setLayoutAlgorithm(getLayoutAlgorithm());
        settings.setJavaScriptCanOpenWindowsAutomatically(!blockPopupWindows());
        //NOTE:always true
        settings.setLoadsImagesAutomatically(true);
        //settings.setLoadsImagesAutomatically(loadImages());
        settings.setLoadWithOverviewMode(loadPageInOverviewMode());
        settings.setSavePassword(rememberPasswords());
        settings.setSaveFormData(saveFormdata());
        settings.setUseWideViewPort(isWideViewport());
//        settings.setAutoFillProfile(getAutoFillProfile());

        String ua = mCustomUserAgents.get(settings);
        if (ua != null) {
            settings.setUserAgentString(ua);
        } else {
            // Do not change user agent we set before.
            // settings.setUserAgentString(USER_AGENTS[getUserAgent()]);
        }

//        settings.setProperty(WebViewProperties.gfxInvertedScreen,
//                useInvertedRendering() ? "true" : "false");
//
//        settings.setProperty(WebViewProperties.gfxInvertedScreenContrast,
//                Float.toString(getInvertedContrast()));
//
//        settings.setProperty(WebViewProperties.gfxEnableCpuUploadPath,
//                enableCpuUploadPath() ? "true" : "false");
    }

    /**
     * Syncs all the settings that have no UI
     * These cannot change, so we only need to set them once per WebSettings
     */
    private void syncStaticSettings(WebSettings settings) {
        settings.setDefaultFontSize(16);
        settings.setDefaultFixedFontSize(13);
//        settings.setPageCacheCapacity(getPageCacheCapacity());
        InvokeWebSettingsMethod.setPageCacheCapacity(settings, getPageCacheCapacity());

        // WebView inside Browser doesn't want initial focus to be set.
        settings.setNeedInitialFocus(false);
        // Browser supports multiple windows
        settings.setSupportMultipleWindows(true);
        // enable smooth transition for better performance during panning or
        // zooming
        if (BrowserGlobals.isPlatformHoneycombAndAbove())
         {
            settings.setEnableSmoothTransition(true);
        // disable content url access
//        settings.setAllowContentAccess(false);
        }

        // HTML5 API flags
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
//        settings.setWorkersEnabled(true);  // This only affects V8.

        // HTML5 configuration parametersettings.
        settings.setAppCacheMaxSize(getWebStorageSizeManager().getAppCacheMaxSize());
        settings.setAppCachePath(getAppCachePath());
        settings.setDatabasePath(mContext.getDir("databases", 0).getPath());
        settings.setGeolocationDatabasePath(mContext.getDir("geolocation", 0).getPath());
    }

    private void syncSharedSettings() {
        mNeedsSharedSync = false;
        CookieManager.getInstance().setAcceptCookie(acceptCookies());
        if (mController != null) {
            mController.setShouldShowErrorConsole(enableJavascriptConsole());
        }
    }

    private void syncManagedSettings() {
        syncSharedSettings();
        synchronized (mManagedSettings) {
            Iterator<WeakReference<WebSettings>> iter = mManagedSettings.iterator();
            while (iter.hasNext()) {
                WeakReference<WebSettings> ref = iter.next();
                WebSettings settings = ref.get();
                if (settings == null) {
                    iter.remove();
                    continue;
                }
                syncSetting(settings);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
        syncManagedSettings();
        if (PREF_SEARCH_ENGINE.equals(key)) {
            updateSearchEngine(false);
        }
        if (PREF_USE_INSTANT_SEARCH.equals(key)) {
            updateSearchEngine(true);
        }
        if (PREF_FULLSCREEN.equals(key)) {
            if (mController.getUi() != null) {
                mController.getUi().setFullscreen(useFullscreen());
            }
        } else if (PREF_ENABLE_QUICK_CONTROLS.equals(key)) {
//            if (mController.getUi() != null) {
//                mController.getUi().setUseQuickControls(sharedPreferences.getBoolean(key, false));
//            }
        }
    }

    public static String getFactoryResetHomeUrl(Context context) {
        requireInitialization();
        return sFactoryResetUrl;
    }

    public LayoutAlgorithm getLayoutAlgorithm() {
        LayoutAlgorithm layoutAlgorithm = LayoutAlgorithm.NORMAL;
        if (autofitPages()) {
            layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS;
        }
        if (isDebugEnabled()) {
            if (isSmallScreen()) {
                layoutAlgorithm = LayoutAlgorithm.SINGLE_COLUMN;
            } else {
                if (isNormalLayout()) {
                    layoutAlgorithm = LayoutAlgorithm.NORMAL;
                } else {
                    layoutAlgorithm = LayoutAlgorithm.NARROW_COLUMNS;
                }
            }
        }
        return layoutAlgorithm;
    }

    public int getPageCacheCapacity() {
        requireInitialization();
        return mPageCacheCapacity;
    }

    public WebStorageSizeManager getWebStorageSizeManager() {
        requireInitialization();
        return mWebStorageSizeManager;
    }

    private String getAppCachePath() {
        if (mAppCachePath == null) {
            mAppCachePath = mContext.getDir("appcache", 0).getPath();
        }
        return mAppCachePath;
    }

    private void updateSearchEngine(boolean force) {
        String searchEngineName = getSearchEngineName();
        if (force || mSearchEngine == null ||
                !mSearchEngine.getName().equals(searchEngineName)) {
            if (mSearchEngine != null) {
                if (mSearchEngine.supportsVoiceSearch()) {
                     // One or more tabs could have been in voice search mode.
                     // Clear it, since the new SearchEngine may not support
                     // it, or may handle it differently.
                     for (int i = 0; i < mController.getTabControl().getTabCount(); i++) {
                         mController.getTabControl().getTab(i).revertVoiceSearchMode();
                     }
                 }
                mSearchEngine.close();
             }
            mSearchEngine = SearchEngines.get(mContext, searchEngineName);

            /*TODO
             if (mController != null && (mSearchEngine instanceof InstantSearchEngine)) {
                 ((InstantSearchEngine) mSearchEngine).setController(mController);
             }
             */
         }
    }

    public SearchEngine getSearchEngine() {
        if (mSearchEngine == null) {
            updateSearchEngine(false);
        }
        return mSearchEngine;
    }

    public boolean isDebugEnabled() {
        requireInitialization();
        return mPrefs.getBoolean(PREF_DEBUG_MENU, false);
    }

    public void setDebugEnabled(boolean value) {
        Editor edit = mPrefs.edit();
        edit.putBoolean(PREF_DEBUG_MENU, value);
        if (!value) {
            // Reset to "safe" value
            edit.putBoolean(PREF_ENABLE_HARDWARE_ACCEL_SKIA, false);
        }
//        edit.apply();
        edit.commit();
    }

    public void clearCache() {
        WebIconDatabase.getInstance().removeAllIcons();
        if (mController != null) {
            WebView current = mController.getCurrentWebView();
            if (current != null) {
                current.clearCache(true);
            }
        }
    }

    public void clearCookies() {
        CookieManager.getInstance().removeAllCookie();
    }

    public void clearHistory() {
        ContentResolver resolver = mContext.getContentResolver();
        Browser.clearHistory(resolver);
        Browser.clearSearches(resolver);
    }

    public void clearFormData() {
        WebViewDatabase.getInstance(mContext).clearFormData();
        if (mController!= null) {
            WebView currentTopView = mController.getCurrentTopWebView();
            if (currentTopView != null) {
                currentTopView.clearFormData();
            }
        }
    }

    public void clearPasswords() {
        WebViewDatabase db = WebViewDatabase.getInstance(mContext);
        db.clearUsernamePassword();
        db.clearHttpAuthUsernamePassword();
    }

    public void clearDatabases() {
        WebStorage.getInstance().deleteAllData();
    }

    public void clearLocationAccess() {
        GeolocationPermissions.getInstance().clearAll();
    }

    public void resetDefaultPreferences() {
        // Preserve autologin setting
    	//TODO
//        long gal = mPrefs.getLong(GoogleAccountLogin.PREF_AUTOLOGIN_TIME, -1);
//        mPrefs.edit()
//                .clear()
//                .putLong(GoogleAccountLogin.PREF_AUTOLOGIN_TIME, gal)
//                .apply();
        syncManagedSettings();
    }

    /*
    public AutoFillProfile getAutoFillProfile() {
        mAutofillHandler.waitForLoad();
        return mAutofillHandler.getAutoFillProfile();
    }

    public void setAutoFillProfile(AutoFillProfile profile, Message msg) {
        mAutofillHandler.waitForLoad();
        mAutofillHandler.setAutoFillProfile(profile, msg);
        // Auto-fill will reuse the same profile ID when making edits to the profile,
        // so we need to force a settings sync (otherwise the SharedPreferences
        // manager will optimise out the call to onSharedPreferenceChanged(), as
        // it thinks nothing has changed).
        syncManagedSettings();
    }
    */

    public void toggleDebugSettings() {
        setDebugEnabled(!isDebugEnabled());
    }

    public boolean hasDesktopUseragent(WebView view) {
        return view != null && mCustomUserAgents.get(view.getSettings()) != null;
    }

    public void toggleDesktopUseragent(WebView view) {
        if (view == null) {
            return;
        }
        WebSettings settings = view.getSettings();
        if (mCustomUserAgents.get(settings) != null) {
            mCustomUserAgents.remove(settings);
            settings.setUserAgentString(USER_AGENTS[getUserAgent()]);
        } else {
            mCustomUserAgents.put(settings, DESKTOP_USERAGENT);
            settings.setUserAgentString(DESKTOP_USERAGENT);
        }
    }

    public static int getAdjustedMinimumFontSize(int rawValue) {
        rawValue++; // Preference starts at 0, min font at 1
        if (rawValue > 1) {
            rawValue += (MIN_FONT_SIZE_OFFSET - 2);
        }
        return rawValue;
    }

    public int getAdjustedTextZoom(int rawValue) {
        rawValue = (rawValue - TEXT_ZOOM_START_VAL) * TEXT_ZOOM_STEP;
        return (int) ((rawValue + 100) * mFontSizeMult);
    }

    static int getRawTextZoom(int percent) {
        return (percent - 100) / TEXT_ZOOM_STEP + TEXT_ZOOM_START_VAL;
    }

    public int getAdjustedDoubleTapZoom(int rawValue) {
        rawValue = (rawValue - DOUBLE_TAP_ZOOM_START_VAL) * DOUBLE_TAP_ZOOM_STEP;
        return (int) ((rawValue + 100) * mFontSizeMult);
    }

    static int getRawDoubleTapZoom(int percent) {
        return (percent - 100) / DOUBLE_TAP_ZOOM_STEP + DOUBLE_TAP_ZOOM_START_VAL;
    }

    public SharedPreferences getPreferences() {
        return mPrefs;
    }

    // -----------------------------
    // getter/setters for accessibility_preferences.xml
    // -----------------------------

    @Deprecated
    private TextSize getTextSize() {
        String textSize = mPrefs.getString(PREF_TEXT_SIZE, "NORMAL");
        return TextSize.valueOf(textSize);
    }

    public int getMinimumFontSize() {
        int minFont = mPrefs.getInt(PREF_MIN_FONT_SIZE, 0);
        return getAdjustedMinimumFontSize(minFont);
    }

    public boolean forceEnableUserScalable() {
        return mPrefs.getBoolean(PREF_FORCE_USERSCALABLE, false);
    }

    public int getTextZoom() {
        requireInitialization();
        int textZoom = mPrefs.getInt(PREF_TEXT_ZOOM, 10);
        return getAdjustedTextZoom(textZoom);
    }

    public void setTextZoom(int percent) {
//        mPrefs.edit().putInt(PREF_TEXT_ZOOM, getRawTextZoom(percent)).apply();
        mPrefs.edit().putInt(PREF_TEXT_ZOOM, getRawTextZoom(percent)).commit();
    }

    public void setFullscreen(boolean enable) {
        mPrefs.edit().putBoolean(PREF_FULLSCREEN, enable).commit();
    }

    public int getDoubleTapZoom() {
        requireInitialization();
        int doubleTapZoom = mPrefs.getInt(PREF_DOUBLE_TAP_ZOOM, 5);
        return getAdjustedDoubleTapZoom(doubleTapZoom);
    }

    public void setDoubleTapZoom(int percent) {
//        mPrefs.edit().putInt(PREF_DOUBLE_TAP_ZOOM, getRawDoubleTapZoom(percent)).apply();
        mPrefs.edit().putInt(PREF_DOUBLE_TAP_ZOOM, getRawDoubleTapZoom(percent)).commit();
    }

    // -----------------------------
    // getter/setters for advanced_preferences.xml
    // -----------------------------

    public String getSearchEngineName() {
        String defaultSearchEngine = mContext.getString(R.string.default_searchengine);
        return mPrefs.getString(PREF_SEARCH_ENGINE, defaultSearchEngine);
    }

    public boolean openInBackground() {
        return mPrefs.getBoolean(PREF_OPEN_IN_BACKGROUND, false);
    }

    public boolean enableJavascript() {
        return mPrefs.getBoolean(PREF_ENABLE_JAVASCRIPT, true);
    }

    // TODO: Cache
    public PluginState getPluginState() {
        String state = mPrefs.getString(PREF_PLUGIN_STATE, "ON");
        return PluginState.valueOf(state);
    }

    // TODO: Cache
    public ZoomDensity getDefaultZoom() {
        String zoom = mPrefs.getString(PREF_DEFAULT_ZOOM, "MEDIUM");
        return ZoomDensity.valueOf(zoom);
    }

    public boolean loadPageInOverviewMode() {
        return mPrefs.getBoolean(PREF_LOAD_PAGE, true);
    }

    public boolean autofitPages() {
        return mPrefs.getBoolean(PREF_AUTOFIT_PAGES, true);
    }

    public boolean blockPopupWindows() {
        return mPrefs.getBoolean(PREF_BLOCK_POPUP_WINDOWS, true);
    }

    public boolean loadImages() {
        return mPrefs.getBoolean(PREF_LOAD_IMAGES, true);
    }

    public String getDefaultTextEncoding() {
        return mPrefs.getString(PREF_DEFAULT_TEXT_ENCODING, null);
    }

    // -----------------------------
    // getter/setters for general_preferences.xml
    // -----------------------------

    public String getHomePage() {
        return mPrefs.getString(PREF_HOMEPAGE, getFactoryResetHomeUrl(mContext));
    }

    public void setHomePage(String value) {
//        mPrefs.edit().putString(PREF_HOMEPAGE, value).apply();
        mPrefs.edit().putString(PREF_HOMEPAGE, value).commit();
    }

    public static String getErrorPage() {
        // return sErrPage;
        return mContext.getResources().getString(R.string.errpage);
    }

    public boolean isAutofillEnabled() {
        return mPrefs.getBoolean(PREF_AUTOFILL_ENABLED, true);
    }

    public void setAutofillEnabled(boolean value) {
//        mPrefs.edit().putBoolean(PREF_AUTOFILL_ENABLED, value).apply();
        mPrefs.edit().putBoolean(PREF_AUTOFILL_ENABLED, value).commit();
    }

    public boolean getAnalyticFeature() {
        return mPrefs.getBoolean(PREF_ANALYTIC_ENABLED, false);
    }

    public void setAnalyticFeature(boolean value) {
        mPrefs.edit().putBoolean(PREF_ANALYTIC_ENABLED, value).commit();
    }

    // -----------------------------
    // getter/setters for debug_preferences.xml
    // -----------------------------

    public boolean isHardwareAccelerated() {
        if (!isDebugEnabled()) {
            return true;
        }
        return mPrefs.getBoolean(PREF_ENABLE_HARDWARE_ACCEL, true);
    }

    public boolean isSkiaHardwareAccelerated() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_HARDWARE_ACCEL_SKIA, false);
    }

    public int getUserAgent() {
        if (!isDebugEnabled()) {
            return 0;
        }
        return Integer.parseInt(mPrefs.getString(PREF_USER_AGENT, "0"));
    }

    // -----------------------------
    // getter/setters for hidden_debug_preferences.xml
    // -----------------------------

    public boolean enableVisualIndicator() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_VISUAL_INDICATOR, false);
    }

    public boolean enableCpuUploadPath() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_CPU_UPLOAD_PATH, false);
    }

    public boolean enableJavascriptConsole() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_JAVASCRIPT_CONSOLE, false);
    }

    public boolean isSmallScreen() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_SMALL_SCREEN, false);
    }

    public boolean isWideViewport() {
        if (!isDebugEnabled()) {
            return true;
        }
        return mPrefs.getBoolean(PREF_WIDE_VIEWPORT, true);
    }

    public boolean isNormalLayout() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_NORMAL_LAYOUT, false);
    }

    public boolean isTracing() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_TRACING, false);
    }

    public boolean enableLightTouch() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_LIGHT_TOUCH, false);
    }

    public boolean enableNavDump() {
        if (!isDebugEnabled()) {
            return false;
        }
        return mPrefs.getBoolean(PREF_ENABLE_NAV_DUMP, false);
    }

    public String getJsEngineFlags() {
        if (!isDebugEnabled()) {
            return "";
        }
        return mPrefs.getString(PREF_JS_ENGINE_FLAGS, "");
    }

    // -----------------------------
    // getter/setters for lab_preferences.xml
    // -----------------------------

    public boolean useQuickControls() {
        return mPrefs.getBoolean(PREF_ENABLE_QUICK_CONTROLS, false);
    }

    public boolean useMostVisitedHomepage() {
    	return false;
        //TODO: return HomeProvider.MOST_VISITED.equals(getHomePage());
    }

    public boolean useInstantSearch() {
        return mPrefs.getBoolean(PREF_USE_INSTANT_SEARCH, false);
    }

    public boolean useFullscreen() {
        return mPrefs.getBoolean(PREF_FULLSCREEN, false);
    }

    public boolean useInvertedRendering() {
        return mPrefs.getBoolean(PREF_INVERTED, false);
    }

    public float getInvertedContrast() {
        return 1 + (mPrefs.getInt(PREF_INVERTED_CONTRAST, 0) / 10f);
    }

    // -----------------------------
    // getter/setters for privacy_security_preferences.xml
    // -----------------------------

    public boolean showSecurityWarnings() {
        return mPrefs.getBoolean(PREF_SHOW_SECURITY_WARNINGS, true);
    }

    public boolean acceptCookies() {
        return mPrefs.getBoolean(PREF_ACCEPT_COOKIES, true);
    }

    public boolean saveFormdata() {
        return mPrefs.getBoolean(PREF_SAVE_FORMDATA, true);
    }

    public boolean enableGeolocation() {
        return mPrefs.getBoolean(PREF_ENABLE_GEOLOCATION, true);
    }

    public boolean rememberPasswords() {
        return mPrefs.getBoolean(PREF_REMEMBER_PASSWORDS, true);
    }

    // -----------------------------
    // getter/setters for bandwidth_preferences.xml
    // -----------------------------

    public static String getPreloadOnWifiOnlyPreferenceString(Context context) {
        return context.getResources().getString(R.string.pref_data_preload_value_wifi_only);
    }

    public static String getPreloadAlwaysPreferenceString(Context context) {
        return context.getResources().getString(R.string.pref_data_preload_value_always);
    }

    private static final String DEAULT_PRELOAD_SECURE_SETTING_KEY =
            "browser_default_preload_setting";

    public String getDefaultPreloadSetting() {
        String preload = Settings.Secure.getString(mContext.getContentResolver(),
                DEAULT_PRELOAD_SECURE_SETTING_KEY);
        if (preload == null) {
            preload = mContext.getResources().getString(R.string.pref_data_preload_default_value);
        }
        return preload;
    }

    public String getPreloadEnabled() {
        return mPrefs.getString(PREF_DATA_PRELOAD, getDefaultPreloadSetting());
    }

}
