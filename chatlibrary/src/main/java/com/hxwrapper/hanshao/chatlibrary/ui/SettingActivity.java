package com.hxwrapper.hanshao.chatlibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.hxwrapper.demo.chatlibrary.R;
import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hxwrapper.hanshao.chatlibrary.ChatModel;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/24.
 * ACTION: 设置页面
 */

public class SettingActivity extends BaseActivity {


    private LinearLayout mLrNotify;
    private LinearLayout mLrVoice;
    private LinearLayout mLrShock;
    private SwitchButton mSwitchNotify;
    private SwitchButton mSwitchVoice;
    private SwitchButton mSwitchShock;
    private ChatModel mSettingsModel;


    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity,SettingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_setting);
        mSettingsModel = ChatHelper.getInstance().getModel();
        initView();
        setListener();
    }

    private void setListener() {
        mSwitchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSettingsModel.setSettingMsgNotification(true);
                    mLrVoice.setVisibility(View.VISIBLE);
                    mLrShock.setVisibility(View.VISIBLE);

                }else{
                    mSettingsModel.setSettingMsgNotification(false);
                    mLrVoice.setVisibility(View.GONE);
                    mLrShock.setVisibility(View.GONE);
                }
            }
        });

        mSwitchVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSettingsModel.setSettingMsgSound(true);
                }else{
                    mSettingsModel.setSettingMsgSound(false);
                }
            }
        });


        mSwitchShock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    mSettingsModel.setSettingMsgVibrate(true);
                }else{
                    mSettingsModel.setSettingMsgVibrate(false);
                }
            }
        });

    }



    private void initView() {
        mLrVoice = (LinearLayout) findViewById(R.id.lr_voice);
        mLrShock = (LinearLayout) findViewById(R.id.lr_shock);
        mSwitchNotify = (SwitchButton) findViewById(R.id.switch_notify);
        mSwitchVoice = (SwitchButton) findViewById(R.id.switch_voice);
        mSwitchShock = (SwitchButton) findViewById(R.id.switch_shock);

        if(mSettingsModel.getSettingMsgNotification()){
            mSwitchNotify.setChecked(true);
            mLrVoice.setVisibility(View.VISIBLE);
            mLrShock.setVisibility(View.VISIBLE);
        }else{
            mSwitchNotify.setChecked(false);
            mLrVoice.setVisibility(View.GONE);
            mLrShock.setVisibility(View.GONE);
        }

        if(mSettingsModel.getSettingMsgSound()){
            mSwitchVoice.setChecked(true);
        }else{
            mSwitchVoice.setChecked(false);
        }

        if(mSettingsModel.getSettingMsgVibrate()){
            mSwitchShock.setChecked(true);
        }else{
            mSwitchShock.setChecked(false);
        }
    }



}
