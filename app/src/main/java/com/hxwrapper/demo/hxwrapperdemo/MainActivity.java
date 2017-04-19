package com.hxwrapper.demo.hxwrapperdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hxwrapper.hanshao.chatlibrary.Constant;
import com.hxwrapper.hanshao.chatlibrary.ChatHelper;
import com.hxwrapper.hanshao.chatlibrary.ui.ConversationActivity;
import com.hxwrapper.hanshao.chatlibrary.ui.SettingActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.util.EMLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AUTHOR: hanshao
 * DATE: 17/3/23.
 * ACTION:主页
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.top)
    LinearLayout mTop;
    @Bind(R.id.chat)
    TextView mChat;
    @Bind(R.id.logout)
    TextView mLogout;



    @OnClick(R.id.chat)
    void onClickForChat(){
        ConversationActivity.startActivity(this);
    }

    @OnClick(R.id.logout)
    void onClickForLogout(){
        logout();
    }

    @OnClick(R.id.setiing)
    void onClickForSetting(){
        SettingActivity.startActivity(this);
    }


    private boolean isExceptionDialogShow = false;
    private AlertDialog.Builder exceptionBuilder ;  //对话框构建器


    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        showExceptionDialogFromIntent(getIntent());
        initView();
        initData();
    }

    /**
     *
     */
    private void initData() {

    }

    /**
     *
     */
    private void initView() {
        mTextTitle.setText("主页");
    }

    //退出登录
    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        ChatHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // show login screen
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     *当用户遇到一些异常时显示对话框：例如在另一设备上登录，用户删除或用户禁止
     */
    private void showExceptionDialog(String exceptionType) {
        isExceptionDialogShow = true;
        ChatHelper.getInstance().logout(false,null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new AlertDialog.Builder(MainActivity.this);
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));
                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exceptionBuilder = null;
                        isExceptionDialogShow = false;
                        finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.create().show();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 异常对话框判断
     * @param intent
     */
    private void showExceptionDialogFromIntent(Intent intent) {

        if(intent == null){
            return ;
        }

        if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            //表示设备冲突
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            //账号被移除
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            //账号被禁用
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showExceptionDialogFromIntent(intent);
    }

    private int getExceptionMessageId(String exceptionType) {

        if(exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
            return R.string.connect_conflict;
        } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
            return R.string.em_user_remove;
        } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
            return R.string.user_forbidden;
        }
        return R.string.Network_error;
    }
}
