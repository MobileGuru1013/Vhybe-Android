package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Chat;
import com.planet1107.welike.views.ChatsListItem;


public class ChatAdapter extends ArrayAdapter<Chat> {
    
	private final LayoutInflater mInflater;

    public ChatAdapter(Context context) {
    	
        super(context, R.layout.list_item_chats);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Chat> data) {
        
    	clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override 
    public View getView(final int position, View convertView, ViewGroup parent) {
    	ChatsListItem view = null;
        try{
    	Chat Chat = getItem(position);   	
        if (convertView == null) {
        	view =  (ChatsListItem) mInflater.inflate(R.layout.list_item_chats, parent, false);
        } else {
        	view = (ChatsListItem) convertView;
        }
        view.setChat(Chat);
        }catch(ClassCastException e){
        	
        }
        return view;
    }
}