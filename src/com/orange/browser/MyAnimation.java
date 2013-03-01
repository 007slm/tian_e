
package com.orange.browser;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class MyAnimation {
    public static Animation enterWindowManager(Context context, View parent, View chilid) {
        Animation enter = new ScaleAnimation(1.0f / 0.65f, 1.0f, 1.0f / 0.65f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        // enter.setFillAfter(true);
        enter.setDuration(300l);
        return enter;
    }

    public static Animation exitWindowManager(Context context, View parent, View chilid) {
        int width = parent.getWidth();
        int height = parent.getHeight();
        float scalewidth = width * 0.65f;
        float scaleheight = height * 0.6f;
        float scaleX = scalewidth / chilid.getWidth();
        float scaleY = scaleheight / chilid.getHeight();
        Animation enter = new ScaleAnimation(scaleX, 1.0f, scaleY, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        // enter.setFillAfter(true);
        enter.setDuration(200l);
        return enter;
    }
}
