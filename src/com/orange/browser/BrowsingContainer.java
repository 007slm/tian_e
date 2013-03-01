
package com.orange.browser;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BrowsingContainer extends RelativeLayout {

    public BrowsingContainer(Context context) {
        super(context);
    }

    public BrowsingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View web = findViewById(R.id.main_content);
        web.requestFocus(direction, previouslyFocusedRect);
        return true;
    }

}
