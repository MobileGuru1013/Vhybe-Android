package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Like;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.views.UserListItem;

public class LikesAdapter extends ArrayAdapter<Like> {
    
	private final LayoutInflater mInflater;

    public LikesAdapter(Context context) {
    	
        super(context, R.layout.list_item_user);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Like> data) {
        
    	clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override 
    public View getView(final int position, View convertView, ViewGroup parent) {
        
    	Like like = getItem(position);
    	User user = like.likeUser;
        UserListItem view;
        if (convertView == null) {
        	view =  (UserListItem) mInflater.inflate(R.layout.list_item_user, parent, false);
        } else {
        	view = (UserListItem) convertView;
        }
        view.setUser(user);
        return view;
    }
}