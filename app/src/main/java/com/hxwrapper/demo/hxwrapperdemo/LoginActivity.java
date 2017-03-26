package com.hxwrapper.demo.hxwrapperdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/20.
 * ACTION:
 */

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.top)
    LinearLayout mTop;
    @Bind(R.id.user)
    EditText mUser;  //账号
    @Bind(R.id.password)
    EditText mPassword;  //密码
    @Bind(R.id.login)
    Button mLogin; //登录


    @OnClick(R.id.register)
    void onClickForRegister(){

        RegisterActivity.startActivity(this);
    }

    @OnClick(R.id.login)
    void onClickForLogin(){


        String user = mUser.getText().toString();
        String password = mPassword.getText().toString();

        EMClient.getInstance().login(user, password,  new EMCallBack() {
            @Override
            public void onSuccess() {

                //登录成功加载数据
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                //获取用户的信息（这应该是从应用程序的服务器或第三方服务）
                ChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                //跳转页面
                MainActivity.startActivity(LoginActivity.this);
                finish();
            }

            @Override
            public void onError(int mI, String mS) {
                if(EaseCommonUtils.isNetWorkConnected(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(int mI, String mS) {

            }
        });

    }


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initData() {
    }


    private void initView() {

        mTextTitle.setText("登录");

    }
}
