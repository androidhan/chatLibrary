package com.hxwrapper.hanshao.chatlibrary.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hxwrapper.hanshao.chatlibrary.Constant;
import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hxwrapper.hanshao.chatlibrary.db.InviteMessgeDao;
import com.hxwrapper.hanshao.chatlibrary.eventbean.ContactEvent;
import com.hxwrapper.hanshao.chatlibrary.fragment.ConversationListFragment;
import com.hxwrapper.hanshao.chatlibrary.runtimepermissions.PermissionsManager;
import com.hxwrapper.hanshao.chatlibrary.runtimepermissions.PermissionsResultAction;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/20.
 * ACTION:会话界面
 */

public class ConversationActivity extends AppCompatActivity {


    private ConversationListFragment mConversationListFragment;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver mBroadcastReceiver;
    private InviteMessgeDao inviteMessgeDao; //Dao层数据

    public static void startActivity(Activity activity) {

        Intent intent = new Intent(activity, ConversationActivity.class);
        activity.startActivity(intent);
    }



    /**
     * 消息监听器
     */
    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                ChatHelper.getInstance().getNotifier().onNewMsg(message);
            }

            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };


    /**
     *动态注册广播接受者
     */
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();

        //群组改变与联系人改变
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


//                if (mConversationListFragment != null) {
//                    mConversationListFragment.refresh();
//            }
                    //自定义添加执行的方法
                    refreshForNewMessage();
        }};
        broadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    };


    /**
     * 刷新当前的信息
     */
    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                    if (mConversationListFragment != null) {
                        mConversationListFragment.refresh();
                    }
                }
        });
    }

    /**
     * 自定义添加的方法
     */
    public void refreshForNewMessage(){
        //判断是否来了联系人有所该改变
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(ConversationActivity.this);
        }
        if(inviteMessgeDao.getUnreadMessagesCount() > 0){
            //进行获取未读的数量
            mConversationListFragment.setNewMessage(true);
        }else{
            mConversationListFragment.setNewMessage(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.hxwrapper.demo.chatlibrary.R.layout.em_activity_chat);
        mConversationListFragment = new ConversationListFragment();
        getSupportFragmentManager().beginTransaction().add(com.hxwrapper.demo.chatlibrary.R.id.container, mConversationListFragment).commit();
        //注册订阅者
        EventBus.getDefault().register(this);
        init();
    }

    /**
     * IM的控制
     */
    private void init() {
        //提供6.0 省电模式的通告
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        requestPermissions();

        registerBroadcastReceiver();
    }

    /**
     * android 6.0 权限请求
     */
    @TargetApi(23)
    private void requestPermissions() {

        //权限请求工具
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //权限申请成功回调
				Toast.makeText(ConversationActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //权限申请失败回调
//                Toast.makeText(ConversationActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();


        ChatHelper sdkHelper = ChatHelper.getInstance();
        sdkHelper.pushActivity(this);
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        ChatHelper sdkHelper = ChatHelper.getInstance();
        sdkHelper.popActivity(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 反注册广播
     */
    private void unregisterBroadcastReceiver(){
        broadcastManager.unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    /**
     * 订阅阅读消息事件
     * @param event
     */
    public void onEventMainThread(ContactEvent event){
        mConversationListFragment.setNewMessage(false);
    }
}

