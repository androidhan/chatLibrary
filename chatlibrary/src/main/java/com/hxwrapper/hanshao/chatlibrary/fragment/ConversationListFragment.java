package com.hxwrapper.hanshao.chatlibrary.fragment;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hxwrapper.hanshao.chatlibrary.Constant;
import com.hxwrapper.demo.chatlibrary.R;
import com.hxwrapper.hanshao.chatlibrary.db.InviteMessgeDao;
import com.hxwrapper.hanshao.chatlibrary.ui.ChatActivity;
import com.hxwrapper.hanshao.chatlibrary.ui.ContactListActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.util.NetUtils;


/**
 * 会话列表的包装
 */
public class ConversationListFragment extends EaseConversationListFragment{

    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);

        //添加错误view
        errorItemContainer.addView(errorView);
        //错误字符
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);

        //自定义添加
        mContactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ContactListActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //自定义更新UI
        if (new InviteMessgeDao(getActivity()).getUnreadMessagesCount() > 0) {
            //进行获取未读的数量
            setNewMessage(true);
        } else {
            setNewMessage(false);
        }



        titleBar.setLeftImageResource(R.drawable.back_control);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }
    
    @Override
    protected void setUpView() {
        super.setUpView();
        //注册菜单
        registerForContextMenu(conversationListView);

        //监听会话的点击
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                //获取对方的账号
                String username = conversation.conversationId();

                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    //不能自己和自己聊天
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // 启动聊天室
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversationType.ChatRoom){
                            // 聊天工作房间
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        }else{
                            //群组
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }
                        
                    }
                     //单一聊天室
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });
        // 红包回执消息在会话列表最后一条消息的展示，这是监听当赋值为当前item数据的时候，进行执行
//        conversationListView.setConversationListHelper(new EaseConversationListHelper() {
//            @Override
//            public String onSetItemSecondaryText(EMMessage lastMessage) {
//                if (lastMessage.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
//                    String sendNick = lastMessage.getStringAttribute(RPConstant.EXTRA_RED_PACKET_SENDER_NAME, "");
//                    String receiveNick = lastMessage.getStringAttribute(RPConstant.EXTRA_RED_PACKET_RECEIVER_NAME, "");
//                    String msg;
//                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
//                        msg = String.format(getResources().getString(R.string.msg_someone_take_red_packet), receiveNick);
//                    } else {
//                        if (sendNick.equals(receiveNick)) {
//                            msg = getResources().getString(R.string.msg_take_red_packet);
//                        } else {
//                            msg = String.format(getResources().getString(R.string.msg_take_someone_red_packet), sendNick);
//                        }
//                    }
//                    return msg;
//                } else if (lastMessage.getBooleanAttribute(RPConstant.MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE, false)) {
//                    String transferAmount = lastMessage.getStringAttribute(RPConstant.EXTRA_TRANSFER_AMOUNT, "");
//                    String msg;
//                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
//                        msg =  String.format(getResources().getString(R.string.msg_transfer_to_you), transferAmount);
//                    } else {
//                        msg =  String.format(getResources().getString(R.string.msg_transfer_from_you),transferAmount);
//                    }
//                    return msg;
//                }
//                return null;
//            }
//        });
        super.setUpView();
        //end of red packet code
    }

    /**
     * 链接失效
     */
    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
         errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
          errorText.setText(R.string.the_current_network);
        }
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu); 
    }

    //注册长按菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            //删除消息
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            //删除联系人
            deleteMessage = false;
        }

        //获取对应的位置的数据
    	EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
    	if (tobeDeleteCons == null) {
    	    return true;
    	}

        //查看会话所属类型
        if(tobeDeleteCons.getType() == EMConversationType.GroupChat){
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {

            //删除对应的信息
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //重新刷新UI
        refresh();

        //更新会话的数量 可能删除的就是未读信息
//        ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }

    public void setNewMessage(boolean newMessage) {

        if(newMessage){
            mTextView.setText("通讯录(有新消息了)");
        }else{
            mTextView.setText("通讯录");

        }
    }
}
