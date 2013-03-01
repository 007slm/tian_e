package com.orange.browser;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.dailystudio.GlobalContextWrapper;
import com.orange.cygnus.reading.CygReadApplication.CygReadAppWrapper;

import java.util.Iterator;
import java.util.List;

public class Browser extends Application {

    private final static String LOGTAG = "browser";

    private final static String READING_PROCESS_NAME = ":ReadingProcess";

    private final static int BROWSER_MAIN_PROCESS = 0;
    private final static int READING_PROCESS = 1;

    // Set to true to enable verbose logging.
    final static boolean LOGV_ENABLED = true;

    // Set to true to enable extra debug logging.
    final static boolean LOGD_ENABLED = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LOGV_ENABLED) {
            Log.v(LOGTAG, "Browser.onCreate: this=" + this);
        }

        switch (getProcessID()) {
            case READING_PROCESS:
                CygReadAppWrapper.getInst(this).onCreate();
                break;
            default:
                // create CookieSyncManager with current Context
                GlobalContextWrapper.bindContext(getApplicationContext());
                CookieSyncManager.createInstance(this);
                BrowserSettings.initialize(getApplicationContext());
                BrowserSettings.getInstance().loadFromDb(this);
                Preloader.initialize(getApplicationContext());
                break;
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogHelper.d(LOGTAG, "Browser.onTerminate: this=" + this);
        if (getProcessID() == READING_PROCESS) {
            CygReadAppWrapper.getInst(this).onTerminate();
        } else {
            GlobalContextWrapper.unbindContext(getApplicationContext());
        }
    }

    private int getProcessID() {
        int processID = BROWSER_MAIN_PROCESS;

        final String processName = getProcessName(android.os.Process.myPid());
        if ((getPackageName() + READING_PROCESS_NAME).equals(processName)) {
            processID = READING_PROCESS;
        }

        return processID;
    }

    private String getProcessName(int pID)
    {
        String processName = "";
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = getPackageManager();
        while (i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
                    .next());
            try
            {
                if (info.pid == pID)
                {
                    if (LOGV_ENABLED) {
                        Log.d(LOGTAG, "Id: " + info.pid + " ProcessName: " + info.processName);
                    }

                    processName = info.processName;
                }
            } catch (Exception e)
            {
                // ignore, nothing I can do.
            }
        }
        return processName;
    }

}
