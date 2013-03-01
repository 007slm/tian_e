
package com.orange.browser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class WindowIndicator extends View {
    private int mTotal;
    private int mCurrent;
    private Context mContext;

    public WindowIndicator(Context context) {
        super(context);
        mContext = context;
    }

    public WindowIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public WindowIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int space = (int) getResources().getDimension(R.dimen.window_indicator_space);
        Bitmap notHight = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.wm_ic_not_currentpage);
        Bitmap hight = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.wm_ic_currentpage);
        int notHightW = notHight.getWidth();
        int notHightH = notHight.getHeight();
        int hightW = hight.getWidth();
        int hightH = hight.getHeight();
        int diffX = (getWidth() - (space + notHightW) * mTotal) / 2;
        for (int i = 0; i < mTotal; i++) {
            canvas.drawBitmap(notHight, diffX + (space + notHightW) * i,
                    (getHeight() - notHightH) / 2,
                    null);
        }
        canvas.drawBitmap(hight, diffX + (notHightW + space) * mCurrent - (hightW - notHightW) / 2,
                (getHeight() - hightH) / 2, null);
        Paint paintText = new Paint();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimaryInverse });
        paintText.setColor(a.getColor(0, 0));
        a.recycle();
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
        paintText.setTextSize(getResources().getDimension(R.dimen.window_indicator_text_size));
        Rect rect = new Rect();
        paintText.getTextBounds(String.valueOf(mCurrent + 1), 0, String.valueOf(mCurrent + 1)
                .length(), rect);
        canvas.drawText(String.valueOf(mCurrent + 1), diffX + (notHightW + space) * mCurrent
                + notHightW / 2, (getHeight()
                + rect.height()) / 2, paintText);
    }

    public void setCurrentPosition(int total, int positon) {
        mTotal = total;
        mCurrent = positon;
        invalidate();
    }
}
