
package com.orange.browser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MenuIndicator extends View {
    private float startx;
    private float endx;
    private float mWidth;
    private float mOffsetX;
    private int mCurrentPostion;
    public MenuIndicator(Context context) {
        super(context);
    }

    public MenuIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        stop(mCurrentPostion);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();
        Paint p1 = new Paint();
        p1.setColor(Color.rgb(80, 80, 80));
        p1.setAntiAlias(true);
        p1.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, mWidth, getHeight(), p1);
        Paint p2 = new Paint();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] {R.attr.colorFocused});
        p2.setColor(a.getColor(0,  0));
        a.recycle();
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.FILL);
        startx = startx + mOffsetX;
        if (startx >= mWidth / 2) {
            startx = mWidth / 2;
        }
        if (startx <= 0) {
            startx = 0;
        }
        endx = startx + mWidth / 2;
        canvas.drawRect(startx, 0, endx, getHeight(), p2);
    }

    public void update(float offset) {
        this.mOffsetX = offset;
        invalidate();
    }
    public void stop(int postion){
        mCurrentPostion=postion;
        mOffsetX=0;
        startx=postion*(mWidth / 2);
        invalidate();
    }
}
