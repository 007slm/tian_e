package com.orange.browser;

import android.content.Context;

import dep.com.android.providers.downloads.Constants;

/*
 * This class is used for version control.
 * All codes related to differente version should be included here.
 */
public class VersionController {
    
    public static String getDownloadDirectory(Context context){
        if (BrowserGlobals.isChinaVersion(context)){
            return Constants.CHINA_DEFAULT_DL_SUBDIR;
        } else {
            return Constants.FRENCH_DEFAULT_DL_SUBDIR;
        }
    }
    
    public static String getDownloadDestination(Context context){
        if (BrowserGlobals.isChinaVersion(context)){
            return Constants.CHINA_DL_DESTINATION;
        } else {
            return Constants.FRENCH_DL_DESTINATION;
        }
    }
}