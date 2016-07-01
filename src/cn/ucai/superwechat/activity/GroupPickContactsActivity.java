/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
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
package cn.ucai.superwechat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.utils.Utils;
import cn.ucai.superwechat.widget.Sidebar;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.adapter.ContactAdapter;
import cn.ucai.superwechat.domain.EMUser;

public class GroupPickContactsActivity extends BaseActivity {
	public static final String TAG = GroupPickContactsActivity.class.getName();
	private ListView listView;
	/** 是否为一个新建的群组 */
	protected boolean isCreatingNewGroup;
	/** 是否为单选 */
	private boolean isSignleChecked;
	private PickContactAdapter contactAdapter;
	/** group中一开始就有的成员 */
	private List<String> exitingMembers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_pick_contacts);

		// String groupName = getIntent().getStringExtra("groupName");
		String groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// 创建群组
			isCreatingNewGroup = true;
		} else {
			// 获取此群组的成员列表
			EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
			exitingMembers = group.getMembers();
		}
		if(exitingMembers == null)
			exitingMembers = new ArrayList<String>();
		// 获取好友列表
		final ArrayList<Contact> alluserList = new ArrayList<Contact>();
		for (Contact user : SuperWeChatApplication.getInstance().getUserList().values()) {
			if (!user.getMContactCname().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getMContactCname().equals(Constant.GROUP_USERNAME) & !user.getMContactCname().equals(Constant.CHAT_ROOM) & !user.getMContactCname().equals(Constant.CHAT_ROBOT))
				alluserList.add(user);
		}
		// 对list进行排序
		Collections.sort(alluserList, new Comparator<Contact>() {
			@Override
			public int compare(Contact lhs, Contact rhs) {
				return (lhs.getHeader().compareTo(rhs.getHeader()));

			}
		});

		listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this, R.layout.row_contact_with_checkbox, alluserList);
		listView.setAdapter(contactAdapter);
		((Sidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();

			}
		});
	}

	/**
	 * 确认选择的members
	 * 
	 * @param v
	 */
	public void save(View v) {
		setResult(RESULT_OK, new Intent()
				.putExtra("newmembersid", getToBeAddMembersId())
				.putExtra("newmembersname",getToBeAddMembersName()));
		finish();
	}

	private String[] getToBeAddMembersName() {
		String[] mContactCname = new String[0];
		int length = contactAdapter.isCheckedArray.length;
		Log.e(TAG, "length=" + length);
		for (int i = 0; i < length; i++) {
			Contact contact = contactAdapter.getItem(i);
			if (contactAdapter.isCheckedArray[i] && !exitingMembers.contains(contact.getMContactCname())) {
				mContactCname = Utils.add(mContactCname, contact.getMContactCname());
			}
		}
		if (mContactCname.length>0) {
			for (String con : mContactCname) {
				con.toString();
				Log.e(TAG, "con.toString()=" + con.toString());
			}
			Log.e(TAG, "contacts=" + mContactCname);
			Log.e(TAG, "contacts-length=" + mContactCname.length);
			return mContactCname;
		}

		return null;
	}

	/**
	 * 获取要被添加的成员
	 * 
	 * @return
	 */
	private String[] getToBeAddMembersId() {
		String[] mContactId = new String[0];
		int length = contactAdapter.isCheckedArray.length;
		Log.e(TAG, "length=" + length);
		for (int i = 0; i < length; i++) {
			Contact contact = contactAdapter.getItem(i);
			if (contactAdapter.isCheckedArray[i] && !exitingMembers.contains(contact.getMContactCname())) {
				mContactId = Utils.add(mContactId, contact.getMContactId()+"");
			}
		}
		if (mContactId.length>0) {
			for (String con : mContactId) {
				con.toString();
				Log.e(TAG, "con.toString()=" + con.toString());
			}
			Log.e(TAG, "contacts=" + mContactId);
			Log.e(TAG, "contacts-length=" + mContactId.length);
			return mContactId;
		}

		return null;
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends ContactAdapter {

		private boolean[] isCheckedArray;

		public PickContactAdapter(Context context, int resource, List<Contact> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
//			if (position > 0) {
				final String username = getItem(position).getMContactCname();
				// 选择框checkbox
				final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				if(exitingMembers != null && exitingMembers.contains(username)){
					checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
				}else{
					checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
				}
				if (checkBox != null) {
					// checkBox.setOnCheckedChangeListener(null);

					checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// 群组中原来的成员一直设为选中状态
							if (exitingMembers.contains(username)) {
									isChecked = true;
									checkBox.setChecked(true);
							}
							isCheckedArray[position] = isChecked;
							//如果是单选模式
							if (isSignleChecked && isChecked) {
								for (int i = 0; i < isCheckedArray.length; i++) {
									if (i != position) {
										isCheckedArray[i] = false;
									}
								}
								contactAdapter.notifyDataSetChanged();
							}
							
						}
					});
					// 群组中原来的成员一直设为选中状态
					if (exitingMembers.contains(username)) {
							checkBox.setChecked(true);
							isCheckedArray[position] = true;
					} else {
						checkBox.setChecked(isCheckedArray[position]);
					}
				}
//			}
			return view;
		}
	}

	public void back(View view){
		finish();
	}
	
}
