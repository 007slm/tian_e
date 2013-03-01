
package com.orange.browser;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.Scroller;

public class OGallery extends ViewGroup {
    public interface OnItemChangedListener {
        void onItemChanged(int currentItem);
    }

    public interface OnItemClickListener {
        void onItemClick(int currentItem);
    }

    public interface OnStartMovingListener {
        void onStartMoving(int XDiff, int currentItem);
    }

    private OnItemChangedListener mItemChangedListener;
    private OnItemClickListener mItemClickListener;
    private OnStartMovingListener mStartMovingListener;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int SNAP_VELOCITY = 500;
    private final static String TAG = "wds";
    private final static int ANIMATION_DURATION = 500;

    private int mSpace;
    private float mScale = 0.74f;
    private int mItemWidth;
    private int mItemHeight;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchState = TOUCH_STATE_REST;
    private float mLastMotionX;
    private int mCurrentItem = 0;
    private int mNextItem;
    private int mXDiff;
    private float mXOri;
    private int mTouchSlop;
    private int MmaximumVelocity;
    private boolean mFirstLayout = true;
    public OGallery(Context context) {
        super(context);
        init(context);
    }

    public OGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(11)
    private void init(Context context) {
        mScroller = new Scroller(context);
        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        MmaximumVelocity = config.getScaledMaximumFlingVelocity();
        this.setClickable(true);
        //disable hardwareAccelerated
        if(BrowserGlobals.isPlatformHoneycombAndAbove()) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
//        setStaticTransformationsEnabled(true);
    }

    public void setOnItemChangedListener(OnItemChangedListener itemChangedListener) {
        mItemChangedListener = itemChangedListener;

    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;

    }

    public void setOnStartMovingListener(OnStartMovingListener startMovingListener) {
        mStartMovingListener = startMovingListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = mSpace;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.layout(childLeft, 0, childLeft + mItemWidth,
                        getMeasuredHeight());
                childLeft += (mItemWidth + mSpace);
            }
        }
        if (mFirstLayout || changed) {
            snapToScreenWithoutScrolling(mCurrentItem);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mItemWidth = (int) (getMeasuredWidth() * mScale);
        mItemHeight = getMeasuredHeight();
        mSpace=(int) (getMeasuredWidth() * 0.038f);
        int mMeasuredWidth = MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY);
        int mMeasuredHeight = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(mMeasuredWidth, mMeasuredHeight);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float x = ev.getX();
        if(action==MotionEvent.ACTION_DOWN){
            mLastMotionX = x;
            mXOri = x;
            mTouchState = TOUCH_STATE_SCROLLING;
            return false;
        }else if(action==MotionEvent.ACTION_MOVE){
            if (mTouchState == TOUCH_STATE_SCROLLING) {
                // Scroll to follow the motion event
                int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                mXDiff = (int) (mXOri - x);
                if (deltaX != 0&&Math.abs(mXDiff) > mTouchSlop)
                    return true;
            }
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                // Remember where the motion event started
                mLastMotionX = x;
                mXOri = x;
                mTouchState = TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    mXDiff = (int) (mXOri - x);
                    if (mStartMovingListener != null && Math.abs(mXDiff) > mTouchSlop) {
                        mStartMovingListener.onStartMoving(mXDiff, mCurrentItem);
                    }
                    if (deltaX != 0&&Math.abs(mXDiff) > mTouchSlop)
                        scrollBy(deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                mXDiff = (int) (mXOri - x);
//                if (!(Math.abs(mXDiff) > mTouchSlop)) {
//                    if (mItemClickListener != null) {
//                        mItemClickListener.onItemClick(mCurrentItem);
//                    }
//                }
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    VelocityTracker tracker = mVelocityTracker;
                    tracker.computeCurrentVelocity(1000, MmaximumVelocity);

                    int velocityX = (int) -tracker.getXVelocity();
                    int tmpitem = mCurrentItem;
                    if (Math.abs(velocityX) > SNAP_VELOCITY) {
                        mNextItem = Math.max(0, Math.min(tmpitem
                                + Math.abs(velocityX) / velocityX, 0
                                + getChildCount() - 1));
                    } else {
                        if (Math.abs(mXDiff) > mItemWidth / 2) {
                            mNextItem = Math.max(0, Math.min(tmpitem
                                    + Math.abs(mXDiff) / mXDiff, 0
                                    + getChildCount() - 1));
                        }
                    }
                    if (tmpitem != mNextItem) {
                        snapToScreen(mNextItem);
                    } else {
                        snapToScreen(mCurrentItem);
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
        }
        return false;
    }

    void snapToScreen(int item) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mCurrentItem = item;
        final int distanceToCenter = (int) ((1 - mScale) * getMeasuredWidth()
                / 2 - mSpace);
        final int oldX = getScrollX();
        final int newX = (int) (mCurrentItem * (mItemWidth + mSpace) - distanceToCenter);
        final int deltaX = newX - oldX;
        mScroller.startScroll(oldX, 0, deltaX, 0, ANIMATION_DURATION);
        if (mItemChangedListener != null) {
            mItemChangedListener.onItemChanged(mCurrentItem);
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    @Override
    public void removeViewAt(int index) {
        if (getChildAt(index) == null){
            return;
        }
        super.removeViewAt(index);
        if (index == getChildCount()) {
            snapPrevionScreen();
        } else {
            snapNextScreen();
        }
    }

    private void snapNextScreen() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        final int distanceToCenter = (int) ((1 - mScale) * getMeasuredWidth()
                / 2 - mSpace);
        final int oldX = getScrollX() - mItemWidth - mSpace;
        final int newX = (int) (mCurrentItem * (mItemWidth + mSpace) - distanceToCenter);
        final int deltaX = newX - oldX;
        mScroller.startScroll(oldX, 0, deltaX, 0, ANIMATION_DURATION);
        if (mItemChangedListener != null) {
            mItemChangedListener.onItemChanged(mCurrentItem);
        }
    }

    private void snapPrevionScreen() {
        mCurrentItem--;
        snapToScreen(mCurrentItem);
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public int getItemWidth() {
        return mItemWidth;
    }

    public void setSelection(int position) {
        snapToScreenWithoutScrolling(position);
    }

    private void snapToScreenWithoutScrolling(int position) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mCurrentItem = position;
        final int distanceToCenter = (int) ((1 - mScale) * getMeasuredWidth()
                / 2 - mSpace);
        final int oldX = getScrollX();
        final int newX = (int) (mCurrentItem * (mItemWidth + mSpace) - distanceToCenter);
        final int deltaX = newX - oldX;
        mScroller.startScroll(oldX, 0, deltaX, 0, 0);
        if (mItemChangedListener != null) {
            mItemChangedListener.onItemChanged(mCurrentItem);
        }
    }
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        // TODO Auto-generated method stub
//        t.clear();
//        t.setAlpha(child == getChildAt(mCurrentItem) ? 1.0f : 0.5f);
//        t.setTransformationType(Transformation.TYPE_MATRIX);
//        if(getChildAt(mCurrentItem)==child){
//            transformationImageView(child,t,0);
//        }else if(getChildAt(mCurrentItem+1)==child){
//            transformationImageView(child,t,-10);
//        }else if(getChildAt(mCurrentItem-1)==child){
//            transformationImageView(child,t,10);
//        }
        return true;
       }
    private void transformationImageView(View child, Transformation t, int angle) {
        int childWidth = child.getWidth();
        int childHeight = child.getHeight();
        Camera canvas = new Camera();
        canvas.save();
        int absAngle = Math.abs(angle);
        canvas.translate(0, 0, absAngle*5);
//        canvas.rotateY(angle);
        Matrix matrix = t.getMatrix() ;
        canvas.getMatrix(matrix);
        matrix.preTranslate(-(childWidth/2), -(childHeight/2));
        matrix.postTranslate((childWidth/2), (childHeight/2));
        canvas.restore();
       }
}
