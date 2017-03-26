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
package com.hxwrapper.hanshao.chatlibrary.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.hxwrapper.demo.chatlibrary.R;
import com.hxwrapper.hanshao.chatlibrary.adapter.NewFriendsMsgAdapter;
import com.hxwrapper.hanshao.chatlibrary.db.InviteMessgeDao;
import com.hxwrapper.hanshao.chatlibrary.domain.InviteMessage;

import java.util.List;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_friends_msg);

		ListView listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();

		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
		
	}

	public void back(View view) {
		finish();
	}
}
