package com.orange.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


import java.util.ArrayList;

public class TutorialPageActivity extends Activity {
    private ViewPager mViewPagerHelpGuide;
    private ArrayList<View> mPageViews;
    public static final int FIRST_TIME = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        int time = BrowserGlobals.getLaunchCount(this);
        Log.d("final", "time is:  " + time);
        if(FIRST_TIME != time){
            startBrowserActivity();
            return;
        }
        setContentView(R.layout.tutorial_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mViewPagerHelpGuide = (ViewPager)findViewById(R.id.guidePages);
        LayoutInflater inflater = getLayoutInflater();
        mPageViews = new ArrayList<View>();

        mViewPagerHelpGuide.setAdapter(new GuidePageAdapter(mPageViews));
        int[]resourece=new int[]{R.drawable.guide_one,R.drawable.guide_two};

        for(int i=0;i<resourece.length;i++){
            View view=inflater.inflate(R.layout.reding_guide_item, null);
            ((ImageView)view.findViewById(R.id.reading_guide_image)).setImageResource(resourece[i]);
            if( (resourece.length -1) == i){
                view.findViewById(R.id.close_guide).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mViewPagerHelpGuide.setVisibility(View.GONE);
                        startBrowserActivity();
                    }
                });
            }
            else{
                view.findViewById(R.id.close_guide).setVisibility(View.GONE);
            }
            mPageViews.add(view);

        }
    }

    private void startBrowserActivity(){
        Intent mainIntent = new Intent(this, BrowserActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
