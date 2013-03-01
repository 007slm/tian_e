package com.orange.browser;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class GestureHelpActivity extends Activity implements OnClickListener {
    private int[] extractAnimArray = {
            R.drawable.extract_1, R.drawable.extract_2, R.drawable.extract_3, R.drawable.extract_4,
            R.drawable.extract_5, R.drawable.extract_6, R.drawable.extract_7, R.drawable.extract_8,
            R.drawable.extract_9, R.drawable.extract_10, R.drawable.extract_11,
            R.drawable.extract_12,
            R.drawable.extract_13, R.drawable.extract_14, R.drawable.extract_15,
            R.drawable.extract_16,
            R.drawable.extract_17, R.drawable.extract_18, R.drawable.extract_19,
            R.drawable.extract_20,
            R.drawable.extract_21, R.drawable.extract_22, R.drawable.extract_23,
            R.drawable.extract_24, R.drawable.extract_25
    };

    private int[] shareAnimArray = {
            R.drawable.share_1, R.drawable.share_2, R.drawable.share_3, R.drawable.share_4,
            R.drawable.share_5, R.drawable.share_6, R.drawable.share_7, R.drawable.share_8,
            R.drawable.share_9, R.drawable.share_10
    };

    private int[] backwardAnimArray = {
            R.drawable.back_1, R.drawable.back_2, R.drawable.back_3, R.drawable.back_4,
            R.drawable.back_5, R.drawable.back_6, R.drawable.back_7
    };

    private int[] forwardAnimArray = {
            R.drawable.prev_1, R.drawable.prev_2, R.drawable.prev_3, R.drawable.prev_4,
            R.drawable.prev_5, R.drawable.prev_6, R.drawable.prev_7
    };

    private int[] helpAnimArray = {
            R.drawable.help_1, R.drawable.help_2,R.drawable.help_3, R.drawable.help_4,
            R.drawable.help_5, R.drawable.help_6, R.drawable.help_7, R.drawable.help_8,
            R.drawable.help_9, R.drawable.help_10
    };

    TextView mPrev;
    TextView mBack;

    // TextView mIncrease;
    // TextView mDecrease;

    TextView mShare;
    TextView mJianBao;

    TextView mHelp;

    View mTipView;
    TutorialGifHelperView mTipAnimation;
    FrameLayout mParent;
    TextView mTitle;
    ImageView mClose;


    RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
    Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initControls();
        BrowserGlobals.setHelpActivityShowedThisTime(true);

    }

    private void initControls() {
        setContentView(R.layout.gesture_grid);

        mClose = (ImageView)findViewById(R.id.close);
        mClose.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                GestureHelpActivity.this.finish();
            }
        });
        mPrev = (TextView) findViewById(R.id.prev);
        mPrev.setOnClickListener(this);

        mBack = (TextView) findViewById(R.id.back);
        mBack.setOnClickListener(this);

        // mIncrease = (TextView) findViewById(R.id.increase);
        // mIncrease.setOnClickListener(this);
        //
        // mDecrease = (TextView) findViewById(R.id.decrease);
        // mDecrease.setOnClickListener(this);

        mShare = (TextView) findViewById(R.id.share);
        mShare.setOnClickListener(this);

        mHelp = (TextView) findViewById(R.id.help);
        mHelp.setOnClickListener(this);

        mJianBao = (TextView) findViewById(R.id.jianbao);
        mJianBao.setOnClickListener(this);

        mParent = (FrameLayout) getWindow()
                .getDecorView().findViewById(android.R.id.content);

        mTipView = LayoutInflater.from(this).inflate(R.layout.gesture_animation, null);

        mTipView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mParent.removeView(mTipView);
                mTipAnimation.stop();

            }
        });
        mTipAnimation = (TutorialGifHelperView) mTipView.findViewById(R.id.animation);
        mTitle = (TextView) mTipView.findViewById(R.id.animation_title);

    }

    @Override
    public void onClick(View v) {
        attachView();
        switch (v.getId()) {
            case R.id.prev:
                startGestureAnimation(R.string.gesture_prev, forwardAnimArray, null);
                break;
            case R.id.back:
                startGestureAnimation(R.string.gesture_back, backwardAnimArray, null);
                break;
            // case R.id.increase:
            // startGestureAnimation(R.string.gesture_increase,
            // R.drawable.gesture_increase_animation);
            // break;
            // case R.id.decrease:
            // startGestureAnimation(R.string.gesture_decrease,
            // R.drawable.gesture_decrease_animation);
            // break;
            case R.id.share:
                startGestureAnimation(R.string.gesture_share, shareAnimArray, null);
                break;
            case R.id.jianbao:
                startGestureAnimation(R.string.jianbao_toast_msg, extractAnimArray, null);
                break;
            case R.id.help:
                startGestureAnimation(R.string.gesture_help, helpAnimArray, null);
                break;
            default:
                break;
        }
    }

    private void attachView() {
        ViewGroup parent = (ViewGroup) mTipView.getParent();
        if (parent != mParent) {
            if (parent != null) {
                parent.removeView(mTipView);
            }
            mParent.addView(mTipView);
        } else {
            // Already added.
        }
    }

    private void startGestureAnimation(int titleId, int[] resId, int[] duration) {
        mTitle.setText(titleId);
        mTipAnimation.init(resId, duration);
    }

    @Override
    public void onBackPressed() {
        if (mTipView.isShown()) {
            mParent.removeView(mTipView);
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initControls();
    }

}
