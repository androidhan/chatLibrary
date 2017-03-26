package com.hxwrapper.demo.hxwrapperdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hxwrapper.hanshao.chatlibrary.ui.VideoCallActivity;
import com.hxwrapper.hanshao.chatlibrary.ui.VoiceCallActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/20.
 * ACTION:闪屏页
 */

public class SplashActivity extends AppCompatActivity   {

    @Bind(R.id.image_view)
    ImageView mImageView;

    private long sleepTime = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setFillAfter(true);
        aa.setDuration(1500);
        mImageView.startAnimation(aa);

        new Thread(new Runnable() {
            public void run() {

                //之前登录过
                if (ChatHelper.getInstance().isLoggedIn()) {

                    //自动登录模式，在进入主屏幕之前，确保所有组和对话都已满
                    long start = System.currentTimeMillis();
                    //加载所有对话框
                    EMClient.getInstance().chatManager().loadAllConversations();
                    //加载所有的组
                    EMClient.getInstance().groupManager().loadAllGroups();
                    long costTime = System.currentTimeMillis() - start;
                    //对比加载时间，进行wait
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());

                    //通话与语音为栈顶
                    if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {

                        // 避免主屏幕重叠调用Activity,该情况为当有语音或视频来电，用户按下Home键，之后再进入应用
                    } else {
                        //enter main screen
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    LoginActivity.startActivity(SplashActivity.this);
                    finish();
                }
            }
        }).start();

    }

}
