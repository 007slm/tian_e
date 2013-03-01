/*============================================================================
 Name:            Carousel.java
 Author/Modifier: Dmitry Novikov, Dmitry Kopylov, Yura Zarovsky, Oleg Osipovich
 Version:         1.1
 Copyright:       Copyright (c) 2010 Abaxia Ltd. All rights reserved.
 Description:     container for views which can be swiped 
============================================================================*/

package com.orange.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class Carousel extends ViewGroup
{
    public interface CarouselAdapter {
    	int getItemCount();
    	View getView(int itemIndex);
    	void setItem(View view, int itemIndex);
    }

    public interface OnItemChangedListener
    {
        void onItemChanged(int screen, int carouselId);
    }

    public interface OnStartScrollingListener
    {
        void onStartScrolling(int carouselId);
    }

    public interface OnStopScrollingListener
    {
        void onStopScrolling(int carouselId);
    }
    public interface OnScrollingListener
    {
        void onScrolling(float offset);
    }
    public Carousel(Context context)
    {
        super(context);
        init(context);
    }

    public Carousel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public Carousel(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public int getCurrentItemId() {
    	return getItemIndex(currentScreen);
    }
    
    public void setOnItemChangedListener(OnItemChangedListener listener)
    {
        onItemChangedListener = listener;
        if (currentScreen != INVALID_SCREEN)
        	notifyOnItemChanged(currentScreen);
    }

    public void setOnStartScrollingListener(OnStartScrollingListener listener)
    {
        onStartScrollingListener = listener;
    }

    public void setOnStopScrollingListener(OnStopScrollingListener listener)
    {
        onStopScrollingListener = listener;
    }
    public void setScrollingListener(OnScrollingListener listener)
    {
        onScrollingListener = listener;
    }
    public void setCarouselAdapter(CarouselAdapter adapter) {
    	carouselAdapter = adapter;	
    }

    public android.view.View getCarouselChildAt(int index)
    {
    	return super.getChildAt(index - startScreen);
    }

    public void setCyclability(boolean value) {
    	cycled = value;
    }
    
    //Override
    public void removeAllViews()
    {
        currentScreen = INVALID_SCREEN;
        startScreen = 0;
        super.removeAllViews();
    }

    //Override
    public void addView(View child)
    {
        super.addView(child, getChildCount(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    //Override
    public void addView(View child, int index)
    {
    	super.addView(child, index, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    //Override
    public void addView(View child, int width, int height)
    {
        super.addView(child, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    //Override
    public void addView(View child, LayoutParams params)
    {
		addView(child);
    }

    //Override
    public void addView(View child, int index, LayoutParams params)
    {
        addView(child, index);
    }

    //Override
    public void computeScroll()
    {
    	if (scroller.computeScrollOffset())
        {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
        else if (nextScreen != INVALID_SCREEN)
        {
            currentScreen = nextScreen;
            nextScreen = INVALID_SCREEN;
            notifyOnStopScrolling();
        }
    }

    //Override
    public boolean onTouchEvent(MotionEvent event)
    {

    	if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            	//Log.d("CAROUSEL", "onTouchEvent: ACTION_DOWN");

            	// Remember where the motion event started
                lastMotionX = x;
                touchState = TOUCH_STATE_SCROLLING;
                notifyOnStartScrolling();
                break;
            case MotionEvent.ACTION_MOVE:
            	//Log.d("CAROUSEL", "onTouchEvent: ACTION_MOVE");
            	if (touchState == TOUCH_STATE_SCROLLING)
                {
                    // Scroll to follow the motion event
                    int deltaX = (int) (lastMotionX - x);
                    lastMotionX = x;

                    if (deltaX != 0){
                        scrollBy(deltaX, 0);
                        if(onScrollingListener!=null){
                            onScrollingListener.onScrolling(deltaX);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            	//Log.d("CAROUSEL", "onTouchEvent: ACTION_UP");
                if (touchState == TOUCH_STATE_SCROLLING)
                {
                    VelocityTracker tracker = velocityTracker;
                    tracker.computeCurrentVelocity(1000, maximumVelocity);

                    int velocityX = (int) -tracker.getXVelocity();

                    int screen = currentScreen;
                    if (Math.abs(velocityX) > SNAP_VELOCITY)
                    {
                        // Fling hard enough to move horisontaly
                        //screen = Math.max(0, Math.min(screen + Math.abs(velocityX) / velocityX, getChildCount() - 1));
                        screen = Math.max(startScreen, Math.min(screen + Math.abs(velocityX) / velocityX, startScreen + getChildCount() - 1));
                    }

                    if (screen != currentScreen) {
                    	snapToScreen(screen);
                    } else {
                    	snapToDestination();
                    }

                    if (velocityTracker != null)
                    {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }
                touchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
            	//Log.d("CAROUSEL", "onTouchEvent: ACTION_CANCEL");
                touchState = TOUCH_STATE_REST;
                notifyOnStopScrolling();
        }

        return true;
    }

    //Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
    	
    	// Cache event to use it to simulate ACTION_UP when user will notify us that we lost focus
    	cachedMotionEvent = ev;
    	
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE && touchState != TOUCH_STATE_REST)
        {
            return true;
        }

        final float x = ev.getX();
        switch (action)
        {
            case MotionEvent.ACTION_MOVE:
            	//Log.d("CAROUSEL", "onInterceptTouchEvent: ACTION_MOVE");
                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                final int xDiff = (int) Math.abs(x - lastMotionX);
                boolean xMoved = xDiff > (touchSlop * 2);

                if (xMoved)
                {
                    // Scroll if the user moved far enough along the X axis
                    touchState = TOUCH_STATE_SCROLLING;
                    notifyOnStartScrolling();
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                else
                {
                    // Either way, cancel any pending longpress
                    if (allowLongPress)
                    {
                        allowLongPress = false;
                        // Try canceling the long press. It could also have been scheduled
                        // by a distant descendant, so use the mAllowLongPress flag to block
                        // everything
                        final View screen = getCarouselChildAt(currentScreen);
                        if (screen != null)
                        	screen.cancelLongPress();
                    }
                }
                break;

            case MotionEvent.ACTION_DOWN:
            	//Log.d("CAROUSEL", "onInterceptTouchEvent: ACTION_DOWN");
                // Remember location of down touch
                lastMotionX = x;
                allowLongPress = true;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                touchState = scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            	//Log.d("CAROUSEL", "onInterceptTouchEvent: ACTION_UP || ACTION_CANCEL");
                // Release the drag
                touchState = TOUCH_STATE_REST;
                allowLongPress = false;
                notifyOnStopScrolling();
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return touchState != TOUCH_STATE_REST;
    }

    public void snapToCurrentScreenWithoutScrolling()
    {
    	//Log.d("CAROUSEL", "snapToCurrentScreenWithoutScrolling: currentScreen=" + currentScreen);

    	if (!scroller.isFinished())
        {
            scroller.forceFinished(true);
            currentScreen = nextScreen;
            nextScreen = INVALID_SCREEN;
        }

        scrollTo(currentScreen * getWidth(), 0);
        checkCyclability();
    }
    
    public void snapToItemWithoutScrolling(int itemId)
    {
    	//Log.d("CAROUSEL", "snapToItemWithoutScrolling: itemIndex=" + screen);
    	
        currentScreen = startScreen + itemId;
    	setCurrentItem(itemId);
        snapToCurrentScreenWithoutScrolling();
    }
    
    public void snapToNextItemWithoutScrolling()
    {
    	currentScreen++;
    	setCurrentItem(currentScreen);
    	snapToCurrentScreenWithoutScrolling();
    }
    
    public void snapToPrevItemWithoutScrolling()
    {
    	currentScreen--;
    	setCurrentItem(currentScreen);
    	snapToCurrentScreenWithoutScrolling();
    }
    
    public void snapToNextItemScrolling()
    {
    	if (scroller.isFinished()) {
        	currentScreen++;
        	checkCyclability();
        	setCurrentItem(currentScreen);
        	snapToScreen(currentScreen);
    	}
    }
    
    public void snapToPrevItemScrolling()
    {
    	if (scroller.isFinished()) {
        	currentScreen--;
        	checkCyclability();
        	setCurrentItem(currentScreen);
        	snapToScreen(currentScreen);
    	}
    }
    
    public void onWindowFocusChanged(boolean hasWindowFocus) {
    	if (!hasWindowFocus && cachedMotionEvent != null) {
    		/* Create ACTION_UP event using params from cached event, 
    		 * so our ACTION_UP event will contain info about position of the (for example) drag event.
    		 * Dispatching this event will simulate "finger-up" action, so our view will act accordingly
    		 * if we lost focus. Needed to correctly handle suddenly appearing applications (like incoming call) 
    		 * while scrolling the carousel.*/
    		
    		MotionEvent upEvent = MotionEvent.obtain(cachedMotionEvent.getDownTime(), cachedMotionEvent.getEventTime(), 
    													MotionEvent.ACTION_UP, cachedMotionEvent.getX(), 
    													cachedMotionEvent.getY(), cachedMotionEvent.getMetaState());
    		dispatchTouchEvent(upEvent);
    	}
    }

    //Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
    	//Log.d("CAROUSEL", "onLayout: currentScreen=" + currentScreen + ", startScreen=" + startScreen);

    	int w = getWidth();
        int h = getHeight();

        int count = getChildCount();
        for (int i = 0; i < count; ++i)
        {
            final View child = super.getChildAt(i);
            if (child != null)
            	child.layout((i + startScreen) *  w, 0, (i + startScreen + 1) * w, h);
        }

        if (changed)
            snapToCurrentScreenWithoutScrolling();
    }

    //Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Log.d("CAROUSEL", "onMeasure");

    	final int width = MeasureSpec.getSize(widthMeasureSpec);

        int count = getChildCount();
        int maxHeight = 0;
        for (int i = 0; i < count; ++i)
        {
            final android.view.View child = super.getChildAt(i);
            if (child != null) {
                child.measure(widthMeasureSpec, heightMeasureSpec);
                int childHeight = child.getMeasuredHeight();
                if (childHeight > maxHeight) {
                    maxHeight = childHeight;
                }
            }
        }
        setMeasuredDimension(width, maxHeight);
    }

    private void init(Context context)
    {
    	//Log.d("CAROUSEL", "init");

    	final ViewConfiguration config = ViewConfiguration.get(context);
        touchSlop = config.getScaledTouchSlop();
        maximumVelocity = config.getScaledMaximumFlingVelocity();
        scroller = new Scroller(context);
    }
    private void snapToDestination()
    {
    	//Log.d("CAROUSEL", "snapToDestination");

    	int width = getWidth();
        int screen = Math.max(startScreen, Math.min((getScrollX() + (width / 2)) / width, startScreen + getChildCount() - 1));
        screen = currentScreen;
        snapToScreen(screen);
    }
    private void snapToScreen(int screen)
    {
    	//Log.d("CAROUSEL", "snapToScreen(" + screen + ")");
    	if (!scroller.isFinished())
        {
            scroller.forceFinished(true);
            currentScreen = nextScreen;
            nextScreen = INVALID_SCREEN;
        }

        if (currentScreen != screen) {
        	if (carouselAdapter != null) {
            	currentItem = getItemIndex(screen);
        	}
        }    
        notifyOnItemChanged(screen);
        nextScreen = screen;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && focusedChild == getCarouselChildAt(screen))
        {
            focusedChild.clearFocus();
        }

        final int oldX = getScrollX();
        final int newX = screen * getWidth();
        final int deltaX = newX - oldX;

        scroller.startScroll(oldX, 0, deltaX, 0, Math.abs(deltaX));
        invalidate();
    }
    private void setCurrentItem(int value) {
    	currentItem = value;
    	if (carouselAdapter != null && getChildCount() == 0) {
    		int screenCount = SCREEN_COUNT;
    		
  			screenCount = carouselAdapter.getItemCount();
  			
  			if (screenCount == 1) {
  				// special case: we don't need cycling for 1 element, so force cycled to false
  				cycled = false;
  			}
    		
    		for (int i = 0; i < screenCount; ++i) {
    			final View view = carouselAdapter.getView(getItemIndex(i)); 
    			addView(view);
    			carouselAdapter.setItem(view, getItemIndex(i));
    		}	
    	}
    }
    private void checkCyclability() {
    	//Log.d("CAROUSEL", "checkCyclability(" + (currentScreen - startScreen) + ")");

    	if (cycled && (currentScreen - startScreen == getChildCount() - 1)) {
    		moveFirstToLast();
        }

        if (cycled && (currentScreen == startScreen)) {
        	moveLastToFront();
        }
    }
    private void moveFirstToLast() {
    	++startScreen;
    	final View firstChild = getChildAt(0);
    	removeViewAt(0);
    	addView(firstChild);
    	if (carouselAdapter != null) {
    		carouselAdapter.setItem(firstChild, getItemIndex(currentScreen + 1));
    	}
    }
    private void moveLastToFront() {
    	--startScreen;
    	final int lastIndex = getChildCount() - 1;
    	final View lastChild = getChildAt(lastIndex);
    	removeViewAt(lastIndex);
    	addView(lastChild, 0);
    	if (carouselAdapter != null) {
    		carouselAdapter.setItem(lastChild, getItemIndex(currentScreen - 1));
    	}
    }
    
    private int getItemIndex(int screen) {
    	int itemIndex = currentItem + screen - currentScreen;
    	int itemCount = carouselAdapter.getItemCount();
    	
    	if (itemIndex < 0 && !cycled) {
    		return 0;
    	}
    	
    	if (itemIndex > itemCount && !cycled) {
    		return itemCount - 1;
    	}
    	
    	int realId = itemIndex % itemCount;
    	if (realId < 0) {
    		realId = itemCount + realId;
    	}
    	
    	return realId;
    }
    
    private void notifyOnItemChanged(int newScreen)
    {
        if (onItemChangedListener != null) {
            onItemChangedListener.onItemChanged(newScreen, getId());
        }    
    }
    private void notifyOnStartScrolling()
    {
        if (onStartScrollingListener != null)
            onStartScrollingListener.onStartScrolling(getId());
    }
    private void notifyOnStopScrolling()
    {
        checkCyclability();
        	
    	if (onStopScrollingListener != null)
            onStopScrollingListener.onStopScrolling(getId());
    }

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int SNAP_VELOCITY = 500;
    private final static int INVALID_SCREEN = -10000;
    private final static int SCREEN_COUNT = 3;
    
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private MotionEvent cachedMotionEvent = null;

    private int touchState = TOUCH_STATE_REST;

    private float lastMotionX;

    private int currentScreen = INVALID_SCREEN;
    private int nextScreen = INVALID_SCREEN;
    private int currentItem = INVALID_SCREEN;
    
    private int touchSlop;
    private int maximumVelocity;
    private int startScreen = 0;

    private boolean allowLongPress = false;
    private boolean cycled = false;
    
    private OnItemChangedListener onItemChangedListener = null;
    private OnStartScrollingListener onStartScrollingListener = null;
    private OnStopScrollingListener onStopScrollingListener = null;
    private OnScrollingListener onScrollingListener = null;
    private CarouselAdapter carouselAdapter = null;
}
