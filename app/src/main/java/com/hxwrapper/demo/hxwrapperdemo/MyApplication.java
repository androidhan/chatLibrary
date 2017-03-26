package com.hxwrapper.demo.hxwrapperdemo;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;

import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hxwrapper.hanshao.chatlibrary.db.ChatDBManager;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/20.
 * ACTION:
 */

public class MyApplication extends Application {


    public static Context mContext;

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = this;
        ChatDBManager.register(mContext);
        ChatHelper.getInstance().init(mContext);

        new Thread(){
            @Override
            public void run() {
                super.run();
                //延迟一秒注册字节码对象 防止虚拟机还没加载到字节码文件 调用参数报错
                SystemClock.sleep(1000);
                ChatHelper.register(MainActivity.class);
            }
        }.start();
    }
}
