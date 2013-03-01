
package com.orange.browser;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public final class TutorialGifHelperView extends ImageView {

    private int[] mResourcesIdArray;
    private int[] mResourcesDurationArray;
    private int mResourceIndex = -1;
    private int defaultDuration = 150;

    private final ImageRunnable mImageRunnable = new ImageRunnable();

    public TutorialGifHelperView(final Context ctx, final AttributeSet attrs) {
        super(ctx, attrs);
    }

    public void init(final int[] resourcesIdArray, final int[] resourcesDurationArray
            ) {
        mResourcesIdArray = resourcesIdArray;
        if(null != mResourcesDurationArray){
            mResourcesDurationArray = resourcesDurationArray;
        }

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
    }

    public void initView() {
        mResourceIndex = Math.max(-1, mResourceIndex - 1);
        setImageResource(mResourcesIdArray[0]);
        postDelayed(mImageRunnable, 0);
    }

    public void restart() {
        mResourceIndex = -1;
        postDelayed(mImageRunnable, mResourcesDurationArray[0]);

    }

    public void stop() {
        if (mResourceIndex != -2) {
            removeCallbacks(mImageRunnable);
            mResourceIndex = -2;
        }
    }

    class ImageRunnable implements Runnable {

        @Override
        public void run() {

            if (mResourceIndex < -1) {
                return;
            }
            mResourceIndex++;
            int delay = defaultDuration;
            if(null != mResourcesDurationArray){
               delay = mResourcesDurationArray[mResourceIndex];;
            }
            
            TutorialGifHelperView.this.setImageResource(mResourcesIdArray[mResourceIndex]);

            if (mResourceIndex == (mResourcesIdArray.length - 1)) {
                mResourceIndex = -1;
            }
            postDelayed(this, delay);
        }
    }

}
