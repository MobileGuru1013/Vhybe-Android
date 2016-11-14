package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.views.CommentListItem;

public class CommentsAdapter extends ArrayAdapter<Comment> {
    
	private final LayoutInflater mInflater;

    public CommentsAdapter(Context context) {
    	
        super(context, R.layout.list_item_comment);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Comment> data) {
        
    	clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override 
    public View getView(final int position, View convertView, ViewGroup parent) {
        
    	Comment comment = getItem(position);
        CommentListItem view;
        if (convertView == null) {
        	view =  (CommentListItem) mInflater.inflate(R.layout.list_item_comment, parent, false);
        } else {
        	view = (CommentListItem) convertView;
        }
        view.setComment(comment);
        return view;
    }
}