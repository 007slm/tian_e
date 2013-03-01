
package com.orange.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (BrowserGlobals.isChinaVersion(this)) {
            Intent mainIntent = new Intent(SplashActivity.this, TutorialPageActivity.class);
            startActivity(mainIntent);
            finish();
            return;
        }
        setContentView(R.layout.splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, TutorialPageActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
