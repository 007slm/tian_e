
package com.orange.browser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BookmarkIndicator extends View {
    private int mLeft;
    private int mWidth;
    private int mCurrentPostion;
    private int mRight;
    public BookmarkIndicator(Context context) {
        super(context);
    }

    public BookmarkIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookmarkIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        setCurrentPostion(mCurrentPostion);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();
        Paint paint = new Paint();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] {R.attr.colorFocused});
        paint.setColor(a.getColor(0,  0));
        a.recycle();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        Rect rect=new Rect(mLeft, 0, mRight, getHeight());
        canvas.drawRect(rect, paint);
    }
    public void setCurrentPostion(int postion){
        float dividerWidth=getContext().getResources().getDimensionPixelSize(R.dimen.divider_width);
        mLeft=(int) ((mWidth-2*dividerWidth)/3*postion+dividerWidth*postion);
        mRight=(int) (mLeft+(mWidth-2*dividerWidth)/3);
        mCurrentPostion=postion;
        invalidate();
    }
}
