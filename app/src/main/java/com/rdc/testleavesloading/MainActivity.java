package com.rdc.testleavesloading;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rdc.leavesloading.LeavesLoading;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "LYT";
    private TextView mTvProgress;
    private SeekBar mSbProgress;
    private TextView mTvLeafFloatSpeed;
    private SeekBar mSbLeafFloatSpeed;
    private TextView mTvLeafRotateSpeed;
    private SeekBar mSbLeafRotateSpeed;
    private TextView mTvFanSpeed;
    private SeekBar mSbFanSpeed;
    private AppCompatCheckBox mCbFan1;
    private AppCompatCheckBox mCbFan2;
    private AppCompatCheckBox mCbFan3;
    private AppCompatCheckBox mCbLeaf1;
    private AppCompatCheckBox mCbLeaf2;
    private AppCompatCheckBox mCbLeaf3;

    private LeavesLoading mLeavesLoading;
    private int[] mFanResIds = new int[]{
            R.drawable.iv_fan_1,R.drawable.iv_fan_2,
            R.drawable.iv_fan_3
    };
    private int[] mLeafResIds = new int[]{
            R.drawable.iv_leaf_1,R.drawable.iv_leaf_2,
            R.drawable.iv_leaf_3
    };
    private AppCompatCheckBox[] mCbFans;
    private AppCompatCheckBox[] mCbLeaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
    }
    private void initListeners(){
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeavesLoading.setProgress(progress);
                mTvProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbLeafFloatSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeavesLoading.setLeafFloatTime(progress);
                mTvLeafFloatSpeed.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbLeafRotateSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeavesLoading.setLeafRotateTime(progress);
                mTvLeafRotateSpeed.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbFanSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeavesLoading.setFanRotateSpeed(progress);
                mTvFanSpeed.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        for (int i = 0; i < mCbFans.length; i++) {
            final int j = i;
            mCbFans[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCbCheckedChanged(j,mCbFans);
                    mLeavesLoading.setFanSrc(mFanResIds[j]);
                }
            });
        }
        for (int i = 0; i < mCbLeaves.length; i++) {
            final int j = i;
            mCbLeaves[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCbCheckedChanged(j,mCbLeaves);
                    mLeavesLoading.setLeafSrc(mLeafResIds[j]);
                }
            });
        }
    }


    private void initViews(){
        mLeavesLoading = findViewById(R.id.myleaf);
        mTvProgress = findViewById(R.id.progress);
        mTvFanSpeed = findViewById(R.id.fan_speed);
        mTvLeafFloatSpeed = findViewById(R.id.leaf_float_speed);
        mTvLeafRotateSpeed = findViewById(R.id.leaf_rotate_speed);
        mSbProgress = findViewById(R.id.progress_bar);
        mSbFanSpeed = findViewById(R.id.fan_speed_bar);
        mSbLeafRotateSpeed = findViewById(R.id.leaf_rotate_speed_bar);
        mSbLeafFloatSpeed = findViewById(R.id.leaf_float_speed_bar);
        mCbFan1 = findViewById(R.id.cb_fan_1);
        mCbFan2 = findViewById(R.id.cb_fan_2);
        mCbFan3 = findViewById(R.id.cb_fan_3);
        mCbLeaf1 = findViewById(R.id.cb_leaf_1);
        mCbLeaf2 = findViewById(R.id.cb_leaf_2);
        mCbLeaf3 = findViewById(R.id.cb_leaf_3);

        mCbFans = new AppCompatCheckBox[]{
                mCbFan1,mCbFan2,mCbFan3
        };
        mCbLeaves = new AppCompatCheckBox[]{
                mCbLeaf1,mCbLeaf2,mCbLeaf3
        };
    }

    /**
     * 用于实现CheckBox单选
     * @param j 当前选中下标
     * @param cbs CheckBoxs
     */
    private void onCbCheckedChanged(int j,AppCompatCheckBox[] cbs){
        for (int i = 0; i < cbs.length; i++) {
            if (i == j){
                cbs[i].setClickable(false);
                cbs[i].setChecked(true);
            }else {
                cbs[i].setChecked(false);
                cbs[i].setClickable(true);
            }
        }
    }
}
