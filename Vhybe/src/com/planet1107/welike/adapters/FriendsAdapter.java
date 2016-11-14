package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.views.FriendListActivityItem;
import com.planet1107.welike.views.FriendListItem;


public class FriendsAdapter extends ArrayAdapter<User> {

	private final LayoutInflater mInflater;
	Boolean isActivity=false;
	public FriendsAdapter(Context context) {

		super(context, R.layout.list_item_friend);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public FriendsAdapter(Context context, Boolean isActivity) {

		super(context, R.layout.list_item_friend);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.isActivity=isActivity;
	}


	public void setData(List<User> data) {

		clear();
		if (data != null) {
			addAll(data);
			notifyDataSetChanged();
		}
	}

	@Override 
	public View getView(final int position, View convertView, ViewGroup parent) {

		User user = getItem(position);
		
		if(isActivity){
			FriendListActivityItem view;
			if (convertView == null) {
				view =  (FriendListActivityItem) mInflater.inflate(R.layout.list_item_friend_activity, parent, false);
			} else {
				view = (FriendListActivityItem) convertView;
			}
			view.setUser(user);
			return view;
		}else{
			FriendListItem view;
			if (convertView == null) {
				view =  (FriendListItem) mInflater.inflate(R.layout.list_item_friend, parent, false);
			} else {
				view = (FriendListItem) convertView;
			}
			view.setUser(user);
			return view;
		}
		
	}
}