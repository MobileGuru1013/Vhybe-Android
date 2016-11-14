package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.planet1107.welike.R;
import com.planet1107.welike.activities.PostDetailActivity;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.other.DoubleClickListener;
import com.planet1107.welike.views.PostListItem;

public class TimelineAdapter extends ArrayAdapter<Post> {
    
	private final LayoutInflater mInflater;
	Context mContext;
	public Post mPost;
	Button buttonLike, buttonLikes;
	
    public TimelineAdapter(Context context) {
    	
        super(context, R.layout.list_item_post);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
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
        
        final Post post = getItem(position);
        mPost = post;
        PostListItem view;
        if (convertView == null) {
        	view =  (PostListItem) mInflater.inflate(R.layout.list_item_post, parent, false);
        } else {
        	view = (PostListItem) convertView;
        }
        buttonLike = (Button) view.findViewById(R.id.buttonLike);
		buttonLikes = (Button) view.findViewById(R.id.buttonLikes);
        view.setPost(post);
      /*  final GestureDetector gestureDetector = new GestureDetector(mContext,new GestureListener(position, buttonLike, buttonLikes));
        view.setOnTouchListener(new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            return gestureDetector.onTouchEvent(event);
        }

    	});*/
      
        return view;
    }
    
    public class GestureListener extends
    GestureDetector.SimpleOnGestureListener {

    	Post getPostId;
    	Button buttonLike, buttonLikes;
    	
	public GestureListener(final int position, Button buttonLike, Button buttonLikes) {
			// TODO Auto-generated constructor stub
			getPostId =  getItem(position);
			this.buttonLike = buttonLike;
			this.buttonLikes = buttonLikes;
		}

	@Override
	public boolean onDown(MotionEvent e) {
	
	    return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Intent intentPostDetail = new Intent(mContext, PostDetailActivity.class);
		intentPostDetail.putExtra("com.planet1107.welike.connect.Post", getPostId);	
		mContext.startActivity(intentPostDetail);
		return super.onSingleTapConfirmed(e);
	}

	// event when double tap occurs
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if (getPostId.likedThisPost==0) {
			this.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
		} else {
			this.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
		}
		
		if ( mPost.likedThisPost==0) {
			buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
			mPost.likedThisPost = 1;
			mPost.postLikesCount++;
		} else  {
			buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
			mPost.likedThisPost = 0;
			mPost.postLikesCount--;
		}
		System.out.println("like count double"+ mPost.postLikesCount);
		if(mPost.postLikesCount>1){
			buttonLikes.setText( mPost.postLikesCount + " likes");
		}else
		{
			buttonLikes.setText( mPost.postLikesCount + " like");
			
		}
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {

				Connect sharedConnect = Connect.getInstance(getContext());
					if (getPostId.likedThisPost==0) {
						return sharedConnect.likePost(getPostId.postID);
					} else {
						return sharedConnect.unlikePost(getPostId.postID);
					}
			}

			protected void onPostExecute(Boolean result) {

				/*	if (result && getPostId.likedThisPost==0) {
						buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
						getPostId.likedThisPost = 1;
						getPostId.postLikesCount++;
					} else if (result) {
						buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
						getPostId.likedThisPost = 0;
						getPostId.postLikesCount--;
					}
					buttonLikes.setText( getPostId.postLikesCount + " like");*/
			}

		}.execute();
	    return true;
	}
	}
}