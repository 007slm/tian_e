package com.orange.browser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebView;
import com.orange.browser.R;

/**
 * Handle network state changes
 */
public class NetworkStateHandler {

    Activity mActivity;
    Controller mController;

    // monitor platform changes
    private IntentFilter mNetworkStateChangedFilter;
    private BroadcastReceiver mNetworkStateIntentReceiver;
    private boolean mIsNetworkUp;

    public NetworkStateHandler(Activity activity, Controller controller) {
        mActivity = activity;
        mController = controller;
        // Find out if the network is currently up.
        ConnectivityManager cm = (ConnectivityManager) mActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            mIsNetworkUp = info.isAvailable();
        }

        /*
         * enables registration for changes in network status from http stack
         */
        mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(
                ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {

                    NetworkInfo info = intent.getParcelableExtra(
                            ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (info == null)
                        return;
                    String typeName = info.getTypeName();
                    String subtypeName = info.getSubtypeName();
                    sendNetworkType(typeName.toLowerCase(),
                            (subtypeName != null ? subtypeName.toLowerCase() : ""));

                    boolean noConnection = intent.getBooleanExtra(
                            ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                    onNetworkToggle(!noConnection);
                }
            }
        };

    }

    void onPause() {
        // unregister network state listener
        mActivity.unregisterReceiver(mNetworkStateIntentReceiver);
    }

    void onResume() {
        mActivity.registerReceiver(mNetworkStateIntentReceiver,
                mNetworkStateChangedFilter);
    }

    /**
     * connectivity manager says net has come or gone... inform the user
     * @param up true if net has come up, false if net has gone down
     */
    void onNetworkToggle(boolean up) {
        if (up == mIsNetworkUp) {
            return;
        }
        mIsNetworkUp = up;
        WebView w = mController.getCurrentWebView();
        if (w != null) {
            w.setNetworkAvailable(up);
        }
    }

    boolean isNetworkUp() {
        return mIsNetworkUp;
    }

    private void sendNetworkType(String type, String subtype) {
        // No use now.
//        WebView w = mController.getCurrentWebView();
//        if (w != null) {
//        	w.setNetworkType(type, subtype);
//        }
    }

}
