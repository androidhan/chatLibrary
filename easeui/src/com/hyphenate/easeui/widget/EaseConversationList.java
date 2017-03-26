package com.hyphenate.easeui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseConversationAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ListView;


/**
 * 显示会话的列表
 */
public class EaseConversationList extends ListView {
    
    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;
    

    protected final int MSG_REFRESH_ADAPTER_DATA = 0;
    
    protected Context context;
    //会话list的adatper
    protected EaseConversationAdapter adapter;
    //存储了会话的列表信息
    protected List<EMConversation> conversations = new ArrayList<EMConversation>();
    protected List<EMConversation> passedListRef = null;
    
    
    public EaseConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public EaseConversationList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * 自定义属性进行设置
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationList);
        primaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListPrimaryTextColor, R.color.list_itease_primary_color);
        secondaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListSecondaryTextColor, R.color.list_itease_secondary_color);
        timeColor = ta.getColor(R.styleable.EaseConversationList_cvsListTimeTextColor, R.color.list_itease_secondary_color);
        primarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
        secondarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
        timeSize = ta.getDimension(R.styleable.EaseConversationList_cvsListTimeTextSize, 0);

        this.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        this.setDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        ta.recycle();
        
    }

    public void init(List<EMConversation> conversationList){
        this.init(conversationList, null);
    }

    /**
     * 初始化listView并设置适配器
     * @param conversationList
     * @param helper
     */
    public void init(List<EMConversation> conversationList, EaseConversationListHelper helper){

        conversations = conversationList;
        if(helper != null){
            this.conversationListHelper = helper;
        }

        //初始化adapter
        adapter = new EaseConversationAdapter(context, 0, conversationList);
        adapter.setCvsListHelper(conversationListHelper);
        adapter.setPrimaryColor(primaryColor);
        adapter.setPrimarySize(primarySize);
        adapter.setSecondaryColor(secondaryColor);
        adapter.setSecondarySize(secondarySize);
        adapter.setTimeColor(timeColor);
        adapter.setTimeSize(timeSize);
        setAdapter(adapter);
    }

    /**
     * 重新刷新listView的handler
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case MSG_REFRESH_ADAPTER_DATA:
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
            }
        }
    };

    /**
     * 得到对应的Item数据
     * @param position
     * @return
     */
    public EMConversation getItem(int position) {
        return (EMConversation)adapter.getItem(position);
    }

    /**
     * 实际就是notifyData
     */
    public void refresh() {
    	if(!handler.hasMessages(MSG_REFRESH_ADAPTER_DATA)){
    		handler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
    	}
    }

    /**
     * 作为过滤字符的作用
     * @param str
     */
    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }

    /**
     * listView辅助类
     */
    private EaseConversationListHelper conversationListHelper;


    /**
     * 一个监听器
     */
    public interface EaseConversationListHelper{
        /**
         * 当设置第二行的时间进行监听操作
         * @param lastMessage
         * @return
         */
        String onSetItemSecondaryText(EMMessage lastMessage);
    }
    public void setConversationListHelper(EaseConversationListHelper helper){
        conversationListHelper = helper;
    }

    /**
     * 兼容ScrollView
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
