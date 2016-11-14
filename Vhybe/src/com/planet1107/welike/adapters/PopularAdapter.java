package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.views.PostGridItem;

public class PopularAdapter extends ArrayAdapter<Post> {
    
    public PopularAdapter(Context context) {
    	
        super(context, R.layout.grid_item_post);
    }

    public void setData(List<Post> data) {
        
    	clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override 
    public View getView(final int position, View convertView, ViewGroup parent) {
        
        Post post = getItem(position);
        PostGridItem view;
        if (convertView == null) {
        	view = new PostGridItem(getContext());
        	view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        	view.setBackgroundResource(R.color.gray_color);
        } else {
            view = (PostGridItem) convertView;
        }
        UrlImageViewHelper.setUrlDrawable(view, post.postImagePath);
        return view;
    }
}