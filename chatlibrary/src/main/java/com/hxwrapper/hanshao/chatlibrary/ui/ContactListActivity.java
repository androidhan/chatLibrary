package com.hxwrapper.hanshao.chatlibrary.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.hxwrapper.hanshao.chatlibrary.Constant;
import com.hxwrapper.hanshao.chatlibrary.fragment.ConversationListFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/23.
 * ACTION:通讯录
 */

public class ContactListActivity extends BaseActivity {

    private ContactListFragment mContactListFragment;
    private LocalBroadcastManager broadcastManager;//广播管理器
    private BroadcastReceiver  broadcastReceiver; //广播

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.hxwrapper.demo.chatlibrary.R.layout.em_activity_chat);
        mContactListFragment = new ContactListFragment();
        getSupportFragmentManager().beginTransaction().add(com.hxwrapper.demo.chatlibrary.R.id.container, mContactListFragment).commit();

        registerBroadcastReceiver();
    }




    /**
     *动态注册广播接受者
     */
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();

        //群组改变与联系人改变
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
//        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                mContactListFragment.refresh();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 反注册广播
     */
    private void unregisterBroadcastReceiver(){
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }
}
