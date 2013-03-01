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
 * FileName : DgilGestureOverlayView.java
 *
 * Created  : Aug 16, 2012
 * Author   : Christophe Maldivi
 *
 */

package com.orange.browser.dgil;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orange.dgil.DgilGestureOverlayViewBase;
import com.orange.dgil.TrailDrawingBasic;



/**
 * @author Christophe Maldivi
 *
 */


//
// Main top view, whose children are in most cases the Activity layouts
// and widgets (ListView, HS Workspaces, ...):
// So in your main.xml, this view will often be the top container.
//
// This way, it lets Dgil grab the mouse events of the whole screen:
// the DgilGestureOverlayViewBase class overloads the dispatchTouchEvent
// function. It also overloads the draw function to draw the trail above
// its children.
//
// In the ctor, we basically connect the components instances:
//    - TrailDrawing: manages the drawing of the trail
//    - DgilInstance: interface with Dgil engine, gives it mouse events,
//      and receives gesture detection events via some callbacks (*Detected)
//
public final class DgilGestureOverlayView extends DgilGestureOverlayViewBase {

	private final TrailDrawingBasic mTrailDrawer = new TrailDrawingBasic(this);
	private final DgilInstance      mDgilInstance;
	private final Rect              mGestureRect = new Rect();

	private boolean     mMultitouchMode;
    private DgilActions mDgilActions;


	/**
	 * Create a DgilInstance object, and connect it to a drawer
	 *
	 * @param ctx activity context
	 * @param attrs xml properties
	 */
	public DgilGestureOverlayView ( final Context ctx, final AttributeSet attrs ) {

		super(ctx.getApplicationContext(), attrs);

		mDgilInstance = new DgilInstance (this, mTrailDrawer, ctx, this);
		connectComponents(mDgilInstance, mTrailDrawer);
	}



	/**
	 * Return the Rect containing the gesture
	 *
	 * @return a Rect object whose geometry contains the gesture
	 */
	public Rect getGestureRect ( ) { return mGestureRect; }



	/**
	 * Set the DgilActions object
	 * @param dgilActions
	 */
    public void setDgilActions ( final DgilActions dgilActions ) {
        mDgilActions = dgilActions;
    }



	/**
	 * <ul>
	 * <li>compute the Rect containing the gesture
	 * <li>detect if more than 2 fingers are on the screen to trig the multiselection mode
	 *
	 * @see com.orange.dgil.DgilGestureOverlayViewBase#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent ( final MotionEvent event ) {

	    final int x      = (int)event.getRawX();
	    final int y      = (int)event.getRawY();
	    final int action = event.getAction();

	    // Compute the Gesture Rect
        if      (action == MotionEvent.ACTION_DOWN) mGestureRect.set(x, y, x, y);
        else if (action == MotionEvent.ACTION_MOVE) mGestureRect.union(x, y);
            
	    // Detect multitouch gestures
        if ( (action == MotionEvent.ACTION_DOWN) && (event.getPointerCount() == 1) ) {
            mMultitouchMode = false;
        } else if ( (mMultitouchMode == false) && (event.getPointerCount() > 2) ) {
            mMultitouchMode = true;
            mDgilActions.enterMultiselectionMode();
        }


        final boolean ret = super.dispatchTouchEvent(event);
        final boolean down = (event.getAction() == MotionEvent.ACTION_DOWN);

        if (down && isEnabled()) {
            mDgilInstance.postPressToDgil();
        }

        return ret;
    }

}
