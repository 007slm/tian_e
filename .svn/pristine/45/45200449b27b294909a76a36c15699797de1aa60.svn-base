package com.orange.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.orange.browser.R;


public class AppRateActivity extends Activity implements OnClickListener{
    
    private final static String APP_PNAME = "com.orange.browser";
    
    
    TextView mRateNow;
    TextView mRateLater;
    TextView mNoRate;
    
    SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_rate);
        
        mRateNow = (TextView)findViewById(R.id.rate_now);
        mRateLater = (TextView)findViewById(R.id.rate_later);
        mNoRate = (TextView)findViewById(R.id.no_thanks);
        
        mRateNow.setOnClickListener(this);
        mRateLater.setOnClickListener(this);
        mNoRate.setOnClickListener(this);
        
        SharedPreferences prefs = getSharedPreferences("apprater", 0);
        mEditor = prefs.edit();
        
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rate_now:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=" + APP_PNAME)));
                finish();
                break;
            case R.id.rate_later:
                finish();
                break;
            case R.id.no_thanks:
                if (mEditor != null) {
                    mEditor.putBoolean("dontshowagain", true);
                    mEditor.commit();
                }
                finish();
                break;
        }
    }

}
