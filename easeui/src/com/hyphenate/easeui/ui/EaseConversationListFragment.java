package com.hyphenate.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.EaseConversationList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 会话列表
 *
 */
public class EaseConversationListFragment extends EaseBaseFragment{
	private final static int MSG_REFRESH = 2;
    protected EditText query;
    protected ImageButton clearSearch;
    protected boolean hidden;
    //会话列表的信息
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView; //列表listView
    protected FrameLayout errorItemContainer;

    protected boolean isConflict;


    //无用代码
    protected EMConversationListener convListener = new EMConversationListener(){

		@Override
		public void onCoversationUpdate() {
			refresh();
		}
    	
    };
    //新添加
    protected LinearLayout mContactItem;
    protected TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //冲突处理
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {

        //输入法管理器
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //获取list列表
        conversationListView = (EaseConversationList) getView().findViewById(R.id.list);
        //输入框
        query = (EditText) getView().findViewById(R.id.query);
        //清除搜索输入框
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        //错误显示提供的布局
        errorItemContainer = (FrameLayout) getView().findViewById(R.id.fl_error_item);

        //自定义添加
        getView().findViewById(R.id.search).setVisibility(View.GONE);
        mContactItem = (LinearLayout) getView().findViewById(R.id.contact_item);
        mTextView = (TextView) getView().findViewById(R.id.text_contact_item);

    }
    
    @Override
    protected void setUpView() {

        //加载数据
        conversationList.addAll(loadConversationList());
        //初始化到adapter中
        conversationListView.init(conversationList);

        //listView的监听点击
        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EMConversation conversation = conversationListView.getItem(position);
                    //另外的监听器
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }

        //添加链接服务端监听器
        EMClient.getInstance().addConnectionListener(connectionListener);

        //添加的editText监听
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {

                }      clearSearch.setVisibility(View.INVISIBLE);
                }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        //清除按钮监听
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        //listView 触摸监听
        conversationListView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }
    
    //服务器链接状态监听
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED) {
                isConflict = true;
            } else {
               handler.sendEmptyMessage(0);
            }
        }
        
        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;


    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                //服务端链接失败
                onConnectionDisconnected();
                break;
            case 1:
                //服务端链接成功
                onConnectionConnected();
                break;
            
            case MSG_REFRESH:
	            {
                    //重加载数据重刷新
	            	conversationList.clear();
                    conversationList.addAll(loadConversationList());
	                conversationListView.refresh();
	                break;
	            }
            default:
                break;
            }
        }
    };
    
    /**
     * 状态链接
     */
    protected void onConnectionConnected(){
        errorItemContainer.setVisibility(View.GONE);
    }
    
    /**
     * 状态链接失效通知
     */
    protected void onConnectionDisconnected(){
        errorItemContainer.setVisibility(View.VISIBLE);
    }
    

    /**
     * 进行对会话的列表刷新处理
     */
    public void refresh() {
    	if(!handler.hasMessages(MSG_REFRESH)){
    		handler.sendEmptyMessage(MSG_REFRESH);
    	}
    }

    /**
     * 加载会话数据 存在数据库中
     * @return
     */
    protected List<EMConversation> loadConversationList(){
        // 获取所有的会话
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        //存储了时间，以及对应的会话联系人信息
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();

        //如果在排序期间有新消息，lastMsgTime将更改
        // 所以使用synchronized来确保最后一条消息的时间戳不会改变。
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
           //会话列表数据排序
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 进行时间降序列
     * 
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    /**
     * 隐藏输入框
     */
    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除链接监听
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isConflict){
            outState.putBoolean("isConflict", true);
        }
    }
    
    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }
    
    /**
     * set conversation list item click listener
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }

}
