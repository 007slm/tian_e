/*---------------------------------------------------------------------------
 *
 * Software Name : OrangeBrowser
 *
 * Copyright (c) 2011 France Telecom.
 *
 * This software is the confidential and proprietary information of France
 * Telecom. You shall not disclose such confidential information and shall
 * use it only in accordance with the terms of the license agreement you
 * entered into with France Telecom.
 *
 *---------------------------------------------------------------------------
 * FileName : DgilRepeatHandler.java
 *
 * Created  : Nov 11, 2012
 * Author   : Christophe Maldivi
 *
 */
package com.orange.browser.dgil;

import android.os.Handler;
import android.os.Message;


public final class DgilRepeatHandler extends Handler {

    static private final int PERIOD = 1000; /* 1 second */
    static private final int WHAT   = 0;

    private String      currentSymbol;
    private DgilActions dgilActions;

    private boolean mRepeatRunning = false;

    public boolean isRunning ( ) { return mRepeatRunning; }


    public void reset ( ) {
        mRepeatRunning = false;
        removeMessages(WHAT);
    }

    public boolean start (final String symbol, final DgilActions dgilActions) {
        currentSymbol    = symbol;
        this.dgilActions = dgilActions;

        final boolean repeatAllowed = dgilActions.action(symbol, true);

        if (repeatAllowed) {
            mRepeatRunning = true;
            sendEmptyMessageDelayed(WHAT, PERIOD);
        }

        return repeatAllowed;
    }

    @Override
    public void handleMessage(final Message msg) {
        dgilActions.action(currentSymbol, true);
        sendEmptyMessageDelayed(WHAT, PERIOD);
    }
}
