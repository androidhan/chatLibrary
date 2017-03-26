/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hxwrapper.hanshao.chatlibrary.db;

import android.content.ContentValues;
import android.content.Context;


import com.hxwrapper.hanshao.chatlibrary.domain.InviteMessage;

import java.util.List;


/**
 * 消息存储管理的数据库对象
 */
public class InviteMessgeDao {
	static final String TABLE_NAME = "new_friends_msgs";
	static final String COLUMN_NAME_ID = "id";
	static final String COLUMN_NAME_FROM = "username";
	static final String COLUMN_NAME_GROUP_ID = "groupid";
	static final String COLUMN_NAME_GROUP_Name = "groupname";
	
	static final String COLUMN_NAME_TIME = "time";
	static final String COLUMN_NAME_REASON = "reason";
	public static final String COLUMN_NAME_STATUS = "status";
	static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
	static final String COLUMN_NAME_GROUPINVITER = "groupinviter";
	
	static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";
	
		
	public InviteMessgeDao(Context context){
	}
	
	/**
	 * save message
	 * @param message
	 * @return  return cursor of the message
	 */
	public Integer saveMessage(InviteMessage message){
		return ChatDBManager.getInstance().saveMessage(message);
	}
	
	/**
	 * update message
	 * @param msgId
	 * @param values
	 */
	public void updateMessage(int msgId,ContentValues values){
	    ChatDBManager.getInstance().updateMessage(msgId, values);
	}
	
	/**
	 * get messges
	 * @return
	 */
	public List<InviteMessage> getMessagesList(){
		return ChatDBManager.getInstance().getMessagesList();
	}
	
	public void deleteMessage(String from){
	    ChatDBManager.getInstance().deleteMessage(from);
	}
	
	public int getUnreadMessagesCount(){
	    return ChatDBManager.getInstance().getUnreadNotifyCount();
	}
	
	public void saveUnreadMessageCount(int count){
	    ChatDBManager.getInstance().setUnreadNotifyCount(count);
	}
}
