/*--------------------------------------------------------
* Module Name : EffieientReadMode
* Version : 1.0.0
*
* Software Name : Cygnus Browser
* Version : 1.6
*
* Copyright © 2012 France Télécom All right reserved
*
*--------------------------------------------------------
* File Name   : ${EffieientReadMode.java}
*
* Created     : ${20121205}
* Author(s)   : ${HAN Liang}
*
* Description :
* 1.Add javascript to enable changing link color when press the link in effieient read mode
* 2.Add Handler to notify caching page
*
*--------------------------------------------------------
* ${Log}
*
*/

package com.orange.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.orange.cygnus.reading.Constants;
import com.orange.cygnus.reading.database.modal.ReadingItemObjectDatabaseModal;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class EffieientReadMode {
    
    private static String mUrlCollectionUrlKey = "url";
    private static String mUrlCollectionTitleKey = "title";
    
    private Controller mController;
    private Context mContext;
    
    private List<HashMap<String, String>> mUrlCollectionList = new ArrayList<HashMap<String, String>>();
    
    public EffieientReadMode(Controller controller) {
        mController = controller;
        mContext = mController.getActivity();
    }
    
    public void startEffieientRead(BaseUi baseUi){
        //TODO:HAN Liang
        mController.setCollectorMode(true);
        //int count = mUiController.getCollectorModeTimes();
        //if(count == 0) {
        baseUi.getWebView().loadUrl("javascript:!function(){window.linkArray=[];function d(){var e=arguments[0];var f=e.target;if(f.style.backgroundColor=='rgba(46,172,204,0.5)'){b(e)}else{if(c(f)){linkArray.push({target:f,_switch:true});f.style.backgroundColor='rgba(46,172,204,0.5)'}}if(arguments[1]){b(e)}}function c(f){var e=f;while(e!=document.body){if(e.tagName=='A'){return true}else{e=e.parentNode}}return false}function b(f){if(f.stopPropagation&&f.preventDefault){f.stopPropagation();f.preventDefault()}}window.select=function(){document.body.onclick=d;window.iframes=a(document);for(var f=0;f<window.iframes.length;f++){try{window.iframes[f].contentDocument.body.onclick=function(h){d(h,'iframe')}}catch(g){console.log(g.message)}}};function a(g){try{var j=Array.prototype.slice.call(g.getElementsByTagName('iframe'));for(var f=0;f<j.length;f++){j.concat(a(j[f].contentDocument))}}catch(h){console.log(h.message)}return j}select();window.deselect=function(){for(var e=0;e<linkArray.length;e++){linkArray[e].target.style.backgroundColor=''}linkArray=[];document.body.onclick=null;for(var e=0;e<window.iframes.length;e++){window.iframes[e].contentDocument.body.onclick=null}window.iframes=[];return(e==linkArray.length)?(true):false};window.turnOff=function(e){window.linkArray[e]._switch=false;console.log('true off completed on id:'+e)}}()");
        baseUi.showBatchModeTitleBar();
        baseUi.hideBottomBarinBatch();
        baseUi.invokeBeforeStartEfficientRead();
    }
    
    
    public List<HashMap<String, String>> getUrlCollectionList() {
        return mUrlCollectionList;
    }
    
    public Handler getHandler() {
        return mHandler;
    }

    public int getUrlCollectionListSize() {
        return mUrlCollectionList.size();
    }

    public void clearUrlCollectionList() {
        final Message msg = mHandler.obtainMessage(
                CLEAR_HREF);
        mHandler.sendMessage(msg);
    }

    public void cancelUrlCollectionList() {
        final Message msg = mHandler.obtainMessage(
                CANCEL_HREF);
        mHandler.sendMessage(msg);
    }
    
    Handler mDelayHandler=new Handler();
    Runnable mDelayRunnable=new Runnable() {
        @Override
        public void run() {
            if(mController.getUi().isBatchNotificationBarShow()) {
                mController.getUi().hideBatchNotificationBar();
            }
        }
    };
    
    // Message Ids
    public static final int GET_NODE_HREF = 202;
    public static final int CLEAR_HREF = 203;
    public static final int CANCEL_HREF = 204;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_NODE_HREF:
                {
                    String url = (String) msg.getData().get("url");
                    String title = (String) msg.getData().get("title");
                    String originUrl = (String) msg.getData().get("originurl");

                    boolean isDuplicate = false;

                    for (HashMap map : mUrlCollectionList) {
                        if (map.containsValue(originUrl)) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    ReadingItemObjectDatabaseModal readingItemDatabaseModal = new ReadingItemObjectDatabaseModal();
                    if (readingItemDatabaseModal.checkUrlExistence(mContext, originUrl)) {
                        isDuplicate = true;
                    }

                    if(isDuplicate) {
                        if(!mController.getUi().isBatchNotificationBarShow()) {
                            mController.getUi().showBatchNotificationBar();
                        }
                        mController.getUi().updateBatchNotificationBar(mContext.getResources().getString(R.string.already_addingurl), true, true);
//                        Toast.makeText(mActivity, mActivity.getResources().getString(R.string.already_addingurl),
//                                Toast.LENGTH_SHORT).show();
                        mDelayHandler.removeCallbacks(mDelayRunnable);
                        mDelayHandler.postDelayed(mDelayRunnable, 2000);
                        //((Vibrator)mActivity.getSystemService(mActivity.VIBRATOR_SERVICE)).vibrate(300);
                        break;
                    }

                    if(title != null) {
                        title = title.replaceAll(" +", " ");
                    }

                    if(url != null) {
                        url = url.replaceAll(" +", " ");
                    }

                    String toastStr = null;
                    if(title != null && !title.equals("")&& !title.equals(" ")) {
                        toastStr = title;
                    } else if(url != null && !url.equals("")&& !url.equals(" ")){
                        toastStr = url;
                    } else {
                        toastStr = originUrl;
                    }

//                    if(toastStr.length() > 6) {
//                        toastStr = toastStr.substring(0, 6) + "...";
//                    }


                    //this is to decode some
                    String extra_url = null;
                    if(url!=null) {
                        extra_url = url;
                    } else {
                        extra_url = originUrl;
                    }


                    try {
                        extra_url = URLDecoder.decode(extra_url);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(mUrlCollectionUrlKey, originUrl);
                    map.put(mUrlCollectionTitleKey, toastStr);
                    mUrlCollectionList.add(map);
                    mController.setSelectedTextView(mUrlCollectionList.size());
                    mController.setConfirmText(true);

                    if(!mController.getUi().isBatchNotificationBarShow()) {
                        mController.getUi().showBatchNotificationBar();
                    }

                    boolean isFirstItem = (mUrlCollectionList.size() == 1);
                    mController.getUi().updateBatchNotificationBar(toastStr, isFirstItem, false);


//                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.batch_addingurl)+toastStr,
//                            Toast.LENGTH_SHORT).show();
                    mDelayHandler.removeCallbacks(mDelayRunnable);
                    mDelayHandler.postDelayed(mDelayRunnable, 2000);
                    //((Vibrator)mActivity.getSystemService(mActivity.VIBRATOR_SERVICE)).vibrate(300);

                    Intent i = new Intent(Constants.ACTION_SYNC);
                    i.putExtra(Constants.EXTRA_SYNC_NAME, Constants.SYNC_SINGLE_ARTICLE);
                    i.putExtra(Constants.EXTRA_URL, extra_url);
                    mContext.startService(i);


                    break;
                }
                case CLEAR_HREF:
                {

                    //TODO
/*                    for(int i = 0; i < mUrlCollectionList.size(); i++) {
                        String url = mUrlCollectionList.get(i);
                        Intent intent = new Intent("com.orange.cygnus.reading.ACTION_DELETE_ITEM");
                        intent.putExtra("com.orange.cygnus.reading.EXTRA_URL", url);
                        mActivity.sendBroadcast(intent);
                    }*/

                    mUrlCollectionList.clear();

                    break;
                }
                case CANCEL_HREF:
                {

                    mUrlCollectionList.clear();

                    break;
                }

            }
        }
    };
    
    
    

}
