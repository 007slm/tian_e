package com.orange.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;

import com.android.tools.layoutlib.annotations.VisibleForTesting;
import com.mobclick.android.MobclickAgent;
import com.orange.browser.provider.blackwhitelist.operation.WhiteBlackList;
import com.orange.browser.tools.AppRater;
import com.tendcloud.tenddata.TCAgent;

import dep.com.android.providers.downloads.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class BrowserActivity extends Activity implements OnLongClickListener {

    public static final String ACTION_SHOW_BOOKMARKS = "show_bookmarks";
    public static final String ACTION_SHOW_BROWSER = "show_browser";
    public static final String ACTION_RESTART = "--restart--";
    private static final String EXTRA_STATE = "state";
    private boolean mIsPaused = false;

    private final static String LOGTAG = "browser";

    private final static boolean LOGV_ENABLED = Browser.LOGV_ENABLED;

    private Controller mController;
    private UI mUi;
    public LocationInfo mLocationInfo;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        new Thread(new Runnable() {
            @Override
            public void run() {
                WhiteBlackList.synchronizeBlackWhiteListWithServer(getApplicationContext());
            }
        }).start();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // If this was a web search request, pass it on to the default web
        // search provider and finish this activity.
        if (IntentHandler.handleWebSearchIntent(this, null, getIntent())) {
            finish();
            return;
        }

        mController = new Controller(this, icicle == null, (getIntent().getData() == null) ? true
                : false);
        mUi = BrowserAssociatedHelp.getUI(this, mController);
        mController.setUi(mUi);

        BrowserGlobals.initialize(this);
        Bundle state = getIntent().getBundleExtra(EXTRA_STATE);
        if (state != null && icicle == null) {
            icicle = state;
        }

        mController.start(icicle, getIntent());
        if (BrowserGlobals.isChinaVersion(this)) {            
            mUpgradeChecker.execute(new String[] {
                    Utils.parseHomepage(this) +
                    getString(R.string.new_package_info_patchsegment)
            });
            mCheckAnalyticFeatureTask.execute(new String[] {
                    getString(R.string.analyticfeaturecn_api)
            });

        } else {
            mCheckAnalyticFeatureTask.execute(new String[] {
                    getString(R.string.analyticfeaturefr_api)
            });
            
            AppRater.app_launched(this);
        }

        if (BrowserSettings.getInstance().getAnalyticFeature()
                && BrowserGlobals.isChinaVersion(this)) {
            MobclickAgent.onError(this);
            TCAgent.init(this);
            TCAgent.setReportUncaughtExceptions(true);

            mAnalyticsTask.execute(new String[] {
                    getString(R.string.analytics_api)
            });
        }
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public Controller getController() {
        return mController;
    }
    


    @Override
    protected void onNewIntent(Intent intent) {
        if (mController == null){
            //TODO: Why this happens?
            Log.w(LOGTAG, "onNewIntent mController is null");
            return;
        }
        mController.handleNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mController != null) {
            mController.onResume();
        }

        if (BrowserSettings.getInstance().getAnalyticFeature()
                && BrowserGlobals.isChinaVersion(this)) {
            MobclickAgent.onResume(this);
            TCAgent.onResume(this);
        }

        BrowserGlobals.setBrowserActivity(this);
        mIsPaused = false;

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (Window.FEATURE_OPTIONS_PANEL == featureId) {
            mController.onMenuOpened(featureId, menu);
        }
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        mController.onOptionsMenuClosed(menu);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        mController.onContextMenuClosed(menu);
    }

    /**
     * onSaveInstanceState(Bundle map) onSaveInstanceState is called right
     * before onStop(). The map contains the saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mController.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mController != null) {
            mController.onWindowFocusChanged(hasFocus);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        if (mController != null) {
            mController.onPause();
        }
        super.onPause();

        if (BrowserSettings.getInstance().getAnalyticFeature()
                && BrowserGlobals.isChinaVersion(this)) {
            MobclickAgent.onPause(this);
            TCAgent.onPause(this);
        }

        mIsPaused = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.onDestroy();
        }
        mUi = null;
        mController = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mController.onConfgurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mController.onLowMemory();
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // super.onCreateOptionsMenu(menu);
    // return mController.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onPrepareOptionsMenu(Menu menu) {
    // super.onPrepareOptionsMenu(menu);
    // return mController.onPrepareOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // if (!mController.onOptionsItemSelected(item)) {
    // return super.onOptionsItemSelected(item);
    // }
    // return true;
    // }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        mController.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return mController.onContextItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mController.getUi().onkeyDown()) {
                return true;
            }
            if (mController.isCollectorMode()) {
                mController.resetBatchMode();
                return true;
            }
        }

        return mController.onKeyDown(keyCode, event) ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mController.onKeyLongPress(keyCode, event) ||
                super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mController.onKeyUp(keyCode, event) ||
                super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        mController.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onSearchRequested() {
        return mController.onSearchRequested();
        // return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mController.dispatchKeyEvent(event)
                || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mController.dispatchTouchEvent(ev)
                || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        return mController.dispatchTrackballEvent(ev)
                || super.dispatchTrackballEvent(ev);
    }

    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-generated method stub
        if (!BrowserGlobals.longPressEnabled()) {
            return true;
        } else {
            return mController.onLongClick(v);
        }
    }

    static final String UPGRADE_KEY_STATUS = "status";
    static final String UPGRADE_KEY_VERSION = "version";
    static final String UPGRADE_KEY_URL = "apk";
    static final String UPGRADE_KEY_MD5 = "md5";
    static final String UPGRADE_KEY_RELEASENOTES = "description";
    static final String UPGRADE_VALUE_STATUS_OK = "ok";
    private AsyncTask<String, Void, String> mUpgradeChecker = new AsyncTask<String, Void, String>() {
        private String mMd5sum;
        private String mReleaseNotes;

        @Override
        protected String doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet(urls[0]);
                HttpResponse response = client.execute(httpGet);
                // 16 means length of {"status":"ok"}
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK
                        || response.getEntity().getContentLength() <= 16) {
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
                JSONObject respObject = new JSONObject(respStringBuilder.toString());
                String state = respObject.getString(UPGRADE_KEY_STATUS);
                int newVersionCode = respObject.getInt(UPGRADE_KEY_VERSION);
                int localVersionCode = 0;
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    localVersionCode = pInfo.versionCode;
                } catch (NameNotFoundException e) {
                    // Handle exception
                }

                if (UPGRADE_VALUE_STATUS_OK.equals(state) && newVersionCode > localVersionCode) {
                    mMd5sum = respObject.getString(UPGRADE_KEY_MD5);
                    mReleaseNotes = respObject.getString(UPGRADE_KEY_RELEASENOTES);
                    return respObject.getString(UPGRADE_KEY_URL);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String apk) {
            if (apk == null) {
                return;
            }
            showUpgradeDialog(apk);
        }

        private void showUpgradeDialog(String apk) {
            if (mIsPaused) {
                return;
            }
            final CustomDialog dialog = new CustomDialog(BrowserActivity.this);
            final String apkUrl = apk;
            final String downloadDir = VersionController.getDownloadDirectory(BrowserActivity.this);
            dialog.setTitle(R.string.upgrade_title);
            StringBuilder messageBulder = new StringBuilder(getText(R.string.upgrade_content));
            messageBulder.append("\n\n");
            messageBulder.append(mReleaseNotes);
            dialog.setMessage(messageBulder.toString());
            dialog.setConfirmButton(R.string.upgrade_button_confirm, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    String root = Environment.getExternalStorageDirectory().getPath();
                    StringBuilder apkFile = new StringBuilder(root);
                    apkFile.append(downloadDir);
                    apkFile.append(apkUrl.substring(apkUrl.lastIndexOf('/')));
                    if (mMd5sum != null && mMd5sum.toUpperCase().equals(md5sum(apkFile.toString()))) {
                        NewPackageDownloadedReceiver.installPackage(BrowserActivity.this,
                                apkFile.toString(), null);
                    } else {
                        DownloadHandler.downloadNewPackage(BrowserActivity.this, apkUrl, "");
                    }
                }
            });
            dialog.setCancleButton(R.string.upgrade_button_cancel, null);
            dialog.show();
        }

    };

    static final String PARA_KEY = "key";
    static final String PARA_V = "v";
    static final String PARA_IMSI = "imsi";
    static final String PARA_RANDOMKEY = "randomkey";
    static final String PARA_PROD = "prod";
    static final String PARA_LNG = "lng";
    static final String PARA_LAT = "lat";
    static final String PARA_MNC = "mnc";
    static final String PARA_LAC = "lac";
    static final String PARA_CELLID = "cellid";
    public static String LAT = "lat";
    public static String LNG = "lng";

    // public static String API_KEY = "625f735323dd67dce51763a8025ccf6d";
    static String apiKey;
    static String apiV = "1.0";
    static String apiProd = "9.3browser";
    
    static String storeName;

    private AsyncTask<String, Void, String> mAnalyticsTask = new AsyncTask<String, Void, String>() {

        private TelephonyManager tm;

        private String getImsi() {
            String imsi = null;
            if (tm == null) {
                return null;
            }
            try{
                imsi = tm.getSubscriberId();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            if (!TextUtils.isEmpty(imsi) && imsi.length() < 15)
            {
                String simSN = tm.getSimSerialNumber();
                if (simSN != null && simSN.length() > 6)
                {
                    imsi = imsi + simSN.substring(6);
                }
            }
            if (imsi == null) {
                imsi = tm.getSimSerialNumber();
            }
            if (imsi == null) {
                imsi = tm.getDeviceId();
            }
            if (imsi != null && imsi.equals("310260000000000")) {
                imsi = "460099064111033";
                // imsi = "46000106411102211";//"";
            }
            return imsi;
        }

        @Override
        protected String doInBackground(String... urls) {

            tm = (TelephonyManager) BrowserActivity.this.getSystemService(Context.TELEPHONY_SERVICE);

            String apiUrl = urls[0];
            Map<String, String> vars = new HashMap<String, String>();
            // int randomNum = AESUtil.getRandomNumber();
            // String randomkey = AESUtil.getEncryptValue(randomNum,
            // "startClient", null);
            String imsi = getImsi();
            if(TextUtils.isEmpty(imsi)) {
                return null;
            }
            String imsiEncrypt = AESUtil.getEncryptValue(imsi);
            
            boolean isGetLocationSuccess = LocationUtil.getCurrentCell(BrowserActivity.this);

            try {
                ApplicationInfo appInfo = BrowserActivity.this.getPackageManager()
                        .getApplicationInfo(getPackageName(),
                                PackageManager.GET_META_DATA);
                apiKey = appInfo.metaData.getString("OBROWSER_APPKEY");
                storeName = appInfo.metaData.getString("UMENG_CHANNEL");
            } catch (NameNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(TextUtils.isEmpty(imsiEncrypt)) {
                return null;
            }
            vars.put(PARA_V, apiV);
            vars.put(PARA_KEY, apiKey);
            vars.put(PARA_IMSI, imsiEncrypt);
            vars.put(PARA_PROD, apiProd);
            LocationManager loctionManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = null;
            try {
                loctionManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (IllegalArgumentException e) {
                //ignore
                return null;
            }
            
            
            String adwoStore1 = BrowserActivity.this.getResources().getString(R.string.adwo_store_first);
            String adwoStore2 = BrowserActivity.this.getResources().getString(R.string.adwo_store_second);
            String adwoStore3 = BrowserActivity.this.getResources().getString(R.string.adwo_store_third);
            
            
            if(storeName.equals(adwoStore1)||storeName.equals(adwoStore2)||storeName.equals(adwoStore3)) {
                if(isGetLocationSuccess && mLocationInfo != null) {
                    vars.put(PARA_MNC, mLocationInfo.getMnc());
                    vars.put(PARA_LAC, mLocationInfo.getLac());
                    vars.put(PARA_CELLID, mLocationInfo.getCellid());
                }else{
                    if(null != location){
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        vars.put(LAT, lat + "");
                        vars.put(LNG, lng + "");
                    }
                }
            }
            // vars.put(APIConstants.PARA_MCC, posiInfo.getMcc());
            // vars.put(APIConstants.PARA_MNC, posiInfo.getMnc());
            // vars.put(PARA_RANDOMKEY, randomkey);
            
            try {
                String response = NetworkConnector.doPost(apiUrl, vars);
                // InputStream content = response.getEntity().getContent();
                //
                // BufferedReader buffer = new BufferedReader(
                // new InputStreamReader(content));
                // String s;
                // StringBuilder respStringBuilder = new StringBuilder();
                // while ((s = buffer.readLine()) != null) {
                // respStringBuilder.append(s);
                // }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    };

    private AsyncTask<String, Void, String> mCheckAnalyticFeatureTask = new AsyncTask<String, Void, String>() {

        @Override
        protected String doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet(urls[0]);
                HttpResponse response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK
                        || response.getEntity().getContentLength() <= 16) {
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
                JSONObject respObject = new JSONObject(respStringBuilder.toString());
                int state = respObject.getInt("analyticfeature");

                if (state == 0) {
                    BrowserSettings.getInstance().setAnalyticFeature(false);
                } else {
                    BrowserSettings.getInstance().setAnalyticFeature(true);
                }

            } catch (Exception e) {
                BrowserSettings.getInstance().setAnalyticFeature(false);
                e.printStackTrace();
            }
            return null;
        }

    };

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    static String toHexString(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            sb.append(HEX_DIGITS[(b & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b & 0x0f]);
        }
        return sb.toString();
    }

    static String md5sum(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }
}
