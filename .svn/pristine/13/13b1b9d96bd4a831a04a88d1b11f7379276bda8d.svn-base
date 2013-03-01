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
 * FileName : DgilInstance.java
 *
 * Created  : Aug 16, 2012
 * Author   : Christophe Maldivi
 *
 */

package com.orange.browser.dgil;



import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.browser.BrowserGlobals;
import com.orange.browser.R;
import com.orange.dgil.DgilInterfaceAndroid;
import com.orange.dgil.DgilParamGestures;
import com.orange.dgil.DgilParamTouchscreenAndroid;
import com.orange.dgil.TrailDrawingInterface;

//
// The DgilInterface, its base class (DgilInterfaceAndroid) receives mouse events
// via DgilGestureOverlayViewBase (dispatchTouchEvents),
// Here we implement Dgil callbacks (*Detected) to react to the gesture analysis results,
// to control the trail drawing feedback.
// To control Dgil itself, use DgilGestureOverlayView.getDgilInstance().
//
public final class DgilInstance extends DgilInterfaceAndroid {

	private static final String  LOGTAG = DgilInstance.class.getSimpleName();
	private static final boolean LOG    = true;

	public static final boolean DYNAMIC_MODE    = false;
	public static final boolean LONG_PRESS_MODE = true;

    private static final int FADING_DURATION   = 100; /* ms */
    private static final int FADING_DELAY_LONG = 200; /* ms */

    private static final int COL_SYMBOLIC     = Color.BLACK;
    private static final int COL_SYMBOLIC_NOK = Color.RED;

    private final ColorDrawable mFgNoColor = new ColorDrawable(Color.argb(0, 0, 0, 0));
	private final ColorDrawable mFgColor   = new ColorDrawable(Color.argb(150, 255, 255, 255));

	private static final int SHORT_PRESS     = ViewConfiguration.getTapTimeout(); /* ms */
    private static final int LONG_PRESS      = ViewConfiguration.getLongPressTimeout(); /* ms */
	private static final int LONG_LONG_PRESS = 9999; /* ms */      /* not used */
	private static final int ENTER_ROTATION  = 9999; /* degrees */ /* not used */

	private static final int SYMBOL_MIN_SQUARE_SIDE_LEN = 7000; /* 0.7 cm */


	private final TrailDrawingInterface mTrailDrawer;
	private final FrameLayout           mView;

    private DgilActions mDgilActions = null;

    private static boolean mInLongPressSymbolicMode = false;


    private final DgilRepeatHandler mRepeatHandler = new DgilRepeatHandler();


	public DgilInstance ( final DgilGestureOverlayView fromDgilEvents, final TrailDrawingInterface trailDrawer,
                          final Context ctx, final FrameLayout view ) {

		super( fromDgilEvents, trailDrawer,
				   new DgilParamTouchscreenAndroid(
						   ctx.getApplicationContext(),
					   0, /* use system dip square */ /* screenSquare_um          */
					   view.getLeft(), view.getTop()
	               ),
	               new DgilParamGestures(
	            	   SHORT_PRESS,                   /* thresholdShortPress           */
		               LONG_PRESS,                    /* thresholdLongPress            */
		               LONG_LONG_PRESS,               /* thresholdLongLongPress        */
		               ENTER_ROTATION,                /* thresholdRotation             */
		               false                          /* disable flick forward feature */
		           ),
		           ctx.getApplicationContext()
			);

		mView        = view;
		mTrailDrawer = trailDrawer;

		setDgilMode();
   	}



	@Override
	public void onPause ( ) {
		super.onPause();
		showOpaqueLayer(false);
		mRepeatHandler.reset();
	}



    @Override
    public void Reset ( ) {
    	super.Reset();
    	showOpaqueLayer(false);
    }



    private void setDgilMode ( ) {

        if (DYNAMIC_MODE) {
            SetDynamicPointingMode();
        } else {
            // Best mode for panning
            SetPointingAndroidMode(SYMBOLIC_PATTERN_WIDGET_OMNIDIRECTIONAL);
        }
    }



    public void postPressToDgil ( ) {

        if (DYNAMIC_MODE == false) {
            ForwardStart(true);
        }

        showOpaqueLayer(false);
    }



    public void setDgilActions ( final DgilActions dgilActions ) {
        mDgilActions = dgilActions;
    }



    private void showSymbolicLayer ( ) {

    	if (!isInGesture()) {
            return;
        }

        showOpaqueLayer(true);

		mTrailDrawer.visibleMode(COL_SYMBOLIC, true, false, 0, 0);
    }



    @Override
    public void animationFinished ( ) { showOpaqueLayer(false); }



    private void showOpaqueLayer ( final boolean visible ) {
        mView.setForeground(visible ? mFgColor : mFgNoColor);
    }



/////////////////////////////////// Dgil callbacks: *Detected events /////////////////////////////////////

    @Override
    protected void LongPressDetected ( final int x, final int y ) {
        super.LongPressDetected(x, y);
        //Add a help toast.
        Context c = mView.getContext();
        View toastView = LayoutInflater.from(c).inflate(R.layout.tip_gesture_help, null);
        
        Toast toast = new Toast(c);
        toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, (int) (48 * BrowserGlobals.sDensity) );
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        
        enterLongPressSymbolicMode();
    }



    private void enterLongPressSymbolicMode ( ) {
        if (LONG_PRESS_MODE) {
            SetExtendedSymbolicMode();
            SetCancelMotion(true);

            mInLongPressSymbolicMode = true;

            // Display the mid-translucent layer and the "Orange" trail color
            showSymbolicLayer();

            if (LOG) Log.d(LOGTAG, "enterLongPressSymbolicMode");
        }
    }

    public static boolean inLongPressMode(){
        return mInLongPressSymbolicMode;
    }

    // The current gesture can not be a scroll gesture (not in the vertical/horizontal axis)
	// This event indicates that we are now in a symbolic gesture (not finished yet)
    // Here we can provide a visual feedback to indicate this change
	@Override
	protected void SymbolicPatternDetected ( ) {
		super.SymbolicPatternDetected();

		// Display the mid-translucent layer and the "Orange" trail color
		showSymbolicLayer();

		mRepeatHandler.reset();

		if (LOG) {
            Log.d(LOGTAG, "SymbolicPatternDetected");
        }
	}



	@Override
	protected void BreakSymbolicDetected ( final String symbol1, final String symbol2,
                                           final int symbolSim1, final int symbolSim2, final int symbolSquareSideMaxlen ) {
        final boolean sym1Reject = symbol1.equals("rejected") || symbol1.equals("junk");
        final boolean diff       = (symbolSim1 - symbolSim2) <= 5;
        final boolean sizeReject = symbolSquareSideMaxlen < SYMBOL_MIN_SQUARE_SIDE_LEN;

        final boolean reject = sym1Reject || sizeReject || diff;

        if (LOG) {
            Log.d(LOGTAG, "BreakSymbolicDetected / s1 = " + symbol1 + " s2 = " + symbol2 + " sim1 = " + symbolSim1 + " sim2 = " + symbolSim2 + " max side len = " + symbolSquareSideMaxlen);
        }

        if (reject) {
            return;
        } else {
            if (mRepeatHandler.start(symbol1, mDgilActions)) {
                showOpaqueLayer(false);
            }
        }
	}




	// The gesture is finished and it was a symbolic gesture. Here we receive the analysis:
	// symbol1 can be "rejected"/"junk" if it is an invalid gesture, or the name of the recognized
	// one. Following args can be used to implement advanced reject strategy.
	@Override
	protected void SymbolicDetected ( final String symbol1, final String symbol2,
			                          final int symbolSim1, final int symbolSim2, final int symbolSquareSideMaxlen ) {

		final boolean sym1Reject = symbol1.equals("rejected") || symbol1.equals("junk");
		final boolean diff       = (symbolSim1 - symbolSim2) <= 5;
		final boolean sizeReject = symbolSquareSideMaxlen < SYMBOL_MIN_SQUARE_SIDE_LEN;

		final boolean reject = sym1Reject || sizeReject || diff;

		if (LOG) Log.d(LOGTAG, "s1 = " + symbol1 + " s2 = " + symbol2 + " sim1 = " + symbolSim1 + " sim2 = " + symbolSim2 + " max side len = " + symbolSquareSideMaxlen);


		mRepeatHandler.reset();

		mTrailDrawer.disableShadow();

		if (reject) {
            /* Bad symbol */
			mTrailDrawer.visibleMode(COL_SYMBOLIC_NOK, true, true, FADING_DELAY_LONG, FADING_DURATION);
        } else {
			/* Ok, we have recognized something, let's go ahead */
		    showOpaqueLayer(false);
			mTrailDrawer.notVisibleMode();
			mDgilActions.action(symbol1, false);
		}
	}



	// If we come here, we are int the DYNAMIC_MODE
    @Override
    protected void FlickDetected ( final int velocityEnd, final int velocityEndX, final int velocityEndY, final int pixelPitch,
                                   final int flickDirection, final int flickDirectionAngularError ) {

        if (LOG) Log.d(LOGTAG, "FlickDetected");

        final boolean repeatRunning = mRepeatHandler.isRunning();

        mRepeatHandler.reset();

        if (repeatRunning) return;

        switch (flickDirection) {

        case FLICK_DIRECTION_W:
        case FLICK_DIRECTION_E:
            break;

        case FLICK_DIRECTION_N:
        case FLICK_DIRECTION_NW:
        case FLICK_DIRECTION_NE:
            mDgilActions.action(DgilActions.FLICK_N, false);
            break;

        case FLICK_DIRECTION_S:
        case FLICK_DIRECTION_SE:
        case FLICK_DIRECTION_SW:
            mDgilActions.action(DgilActions.FLICK_S, false);
            break;

        default: Log.e(LOGTAG, "Invalid direction"); break;
        }
    }

    // If we come here, we are int the DYNAMIC_MODE
    @Override
    protected void BreakFlickDetected ( final int velocityEnd, final int velocityEndX, final int velocityEndY, final int pixelPitch,
                                        final int flickDirection, final int flickDirectionAngularError ) {

        if (LOG) Log.d(LOGTAG, "BreakFlickDetected");

        switch (flickDirection) {

        case FLICK_DIRECTION_W:
        case FLICK_DIRECTION_E:
            break;

        case FLICK_DIRECTION_N:
        case FLICK_DIRECTION_NW:
        case FLICK_DIRECTION_NE:
            mRepeatHandler.start(DgilActions.FLICK_N, mDgilActions);
            break;

        case FLICK_DIRECTION_S:
        case FLICK_DIRECTION_SE:
        case FLICK_DIRECTION_SW:
            mRepeatHandler.start(DgilActions.FLICK_S, mDgilActions);
            break;

        default: Log.e(LOGTAG, "Invalid direction"); break;
        }
    }



	@Override
    protected void NoGestureDetected ( ) {
	    mRepeatHandler.reset();
	    setDgilMode();

	    if (mInLongPressSymbolicMode) {
	        showOpaqueLayer(false);
	        mTrailDrawer.notVisibleMode();
	        mInLongPressSymbolicMode = false;
	    }
	}
}
