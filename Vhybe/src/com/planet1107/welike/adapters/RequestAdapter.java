package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.views.RequestListItem;

public class RequestAdapter extends ArrayAdapter<User> {
    
	private final LayoutInflater mInflater;

    public RequestAdapter(Context context) {
    	
        super(context, R.layout.list_item_request);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        RequestListItem view;
        if (convertView == null) {
        	view =  (RequestListItem) mInflater.inflate(R.layout.list_item_request, parent, false);
        } else {
        	view = (RequestListItem) convertView;
        }
        view.setUser(user);
        return view;
    }
}