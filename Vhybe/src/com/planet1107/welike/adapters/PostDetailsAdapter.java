package com.planet1107.welike.adapters;

import java.util.List;

import android.content.Context;
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
import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.views.CommentListItem;
import com.planet1107.welike.views.PostListItem;

public class PostDetailsAdapter extends ArrayAdapter<Comment> {
    
	private final LayoutInflater mInflater;
	Post mPost;
    PostListItem postListItem;
    Context mContext;
    Button buttonLike, buttonLikes;
    GestureDetector gestureDetector;
    
    public PostDetailsAdapter(Context context, Post post) {
    	
        super(context, R.layout.list_item_comment);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPost = post;
        this.mContext = context;
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
        View view;
        if (position == 0) {
        	 System.out.println( " in f ");
        	if (postListItem == null) {
        		postListItem =  (PostListItem) mInflater.inflate(R.layout.list_item_post, parent, false);
            	view = postListItem;
        	} else {
        		view = postListItem;
        	}
            postListItem.setPost(mPost);
            buttonLike = (Button) postListItem.findViewById(R.id.buttonLike);
    		buttonLikes = (Button) postListItem.findViewById(R.id.buttonLikes);
          //  gestureDetector = new GestureDetector(mContext,new GestureListener(mPost, buttonLike, buttonLikes));
          /*  view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return gestureDetector.onTouchEvent(event);
            }});*/
        } else {
        	   System.out.println( " in else ");
        	if (convertView == null || convertView instanceof PostListItem) {
        		 System.out.println( " in inflate ");
            	view =  mInflater.inflate(R.layout.list_item_comment, parent, false);
            } else {
            	 System.out.println( " in comments ");
            	view = convertView;
            }
            if (view instanceof CommentListItem) {
            
            
            	((CommentListItem) view).setComment(comment);
            	
            }
        }        
        return view;
    }
    
    public class GestureListener extends
    GestureDetector.SimpleOnGestureListener {

    	Post getPostId;
    	Button buttonLike, buttonLikes;
    	
	public GestureListener(Post post, Button buttonLike, Button buttonLikes) {
			// TODO Auto-generated constructor stub
			getPostId = post;
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

					if (result && getPostId.likedThisPost==0) {
						buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
						getPostId.likedThisPost = 1;
						getPostId.postLikesCount++;
					} else if (result) {
						buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
						getPostId.likedThisPost = 0;
						getPostId.postLikesCount--;
					}
					buttonLikes.setText( getPostId.postLikesCount + " like");
			}

		}.execute();
		Log.d("PostDetail", "Double tap called");
	    return true;
	}
	}
}   
