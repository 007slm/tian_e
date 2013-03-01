
package com.orange.browser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TextImageView extends ImageView {
    private int mNum=1;

    public TextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextImageView(Context context) {
        super(context);
        init();
    }

    void init() {
        setImageResource(R.drawable.btn_window_regist);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable windowBG = getResources().getDrawable(R.drawable.bottom_window_normal);
        Rect ar = new Rect();
        windowBG.getPadding(ar);
        super.onDraw(canvas);
        Paint p = new Paint();
        TypedArray a = getContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimaryInverse });
        p.setColor(a.getColor(0, 0));
        a.recycle();
        p.setAntiAlias(true);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(getResources().getDimension(R.dimen.bottom_bar_window_button_text_size));
        Rect rect = new Rect();
        p.getTextBounds(String.valueOf(mNum), 0, String.valueOf(mNum)
                .length(), rect);
        canvas.drawText(String.valueOf(mNum), (getWidth() - ar.right + ar.left) / 2, (getHeight()
                + rect.height() + ar.top - ar.bottom) / 2, p);
    }

    public void updateNum(int num) {
        mNum = num;
        invalidate();
    }
}
