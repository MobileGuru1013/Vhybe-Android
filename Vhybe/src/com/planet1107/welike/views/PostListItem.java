package com.planet1107.welike.views;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.activities.CommentsActivity;
import com.planet1107.welike.activities.LikesActivity;
import com.planet1107.welike.activities.PostDetailActivity;
import com.planet1107.welike.activities.ProfileActivity;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PostListItem extends RelativeLayout implements OnClickListener {

	public Post mPost;
	Connect sharedConnect;
	Button buttonLike;
	Button buttonLikes;
	Button buttonComment;
	ImageView imageViewPostImage;
	LinearLayout LL_info;
	TextView textViewPostText;
	GestureDetector gestureDetector;
	Context context;
	RelativeLayout relativeLayout1;

	public PostListItem(Context context) {

		super(context);
		this.context = context;
	}

	public PostListItem(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;
	}

	public PostListItem(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	public void onClick(final View button) {

		if (button == buttonLike) {
			if (mPost.likedThisPost == 0) {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
			} else {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
			}

			if ( mPost.likedThisPost==0) {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
				mPost.likedThisPost = 1;
				mPost.postLikesCount++;
			} else {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
				mPost.likedThisPost = 0;
				mPost.postLikesCount--;
			}
			System.out.println("like count single"+ mPost.postLikesCount);
			if(mPost.postLikesCount>1){

				buttonLikes.setText( mPost.postLikesCount + " likes");
			}else
			{
				buttonLikes.setText( mPost.postLikesCount + " like");

			}
			reloadView();
		} else if (button == buttonLikes) {
			Intent likesIntent = new Intent(PostListItem.this.getContext(), LikesActivity.class);
			likesIntent.putExtra("postID",mPost.postID);
			PostListItem.this.getContext().startActivity(likesIntent);
		} else if (button == buttonComment) {
			Intent commentIntent = new Intent(PostListItem.this.getContext(), CommentsActivity.class);
			commentIntent.putExtra("postID",mPost.postID);
			PostListItem.this.getContext().startActivity(commentIntent);
		} else {

		}
	
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				Connect sharedConnect = Connect.getInstance(getContext());
				if (button == buttonLike) {
					if (mPost.likedThisPost == 0) {
						//buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);

						return sharedConnect.unlikePost(mPost.postID);
					} else {
						//	buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
						return sharedConnect.likePost(mPost.postID);
					}
				} else if (button == buttonLikes) {
					return true;
				} else if (button == buttonComment) {
					return true;
				} else {
					return null;
				}
			}

			protected void onPostExecute(Boolean result) {
				if (button == buttonLike) {
					/*if (result && mPost.likedThisPost==0) {
						//buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
						mPost.likedThisPost = 1;
						mPost.postLikesCount++;
					} else if (result) {
						//buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
						mPost.likedThisPost = 0;
						mPost.postLikesCount--;
					}
					System.out.println("like count single"+ mPost.postLikesCount);
					if(mPost.postLikesCount>1){

						buttonLikes.setText( mPost.postLikesCount + " likes");
					}else
					{
						buttonLikes.setText( mPost.postLikesCount + " like");

					}
					reloadView();*/
					reloadView();
				} else if (button == buttonLikes) {

				} else if (button == buttonComment) {

				} else {
					showUser();
				}
			}

		}.execute();
	}

	public void setPost(Post post) {

		mPost = post;
		reloadView();
	}	

	protected void onFinishInflate () {

		buttonLike = (Button) findViewById(R.id.buttonLike);
		buttonLikes = (Button) findViewById(R.id.buttonLikes);
		buttonComment = (Button) findViewById(R.id.buttonComment);
		imageViewPostImage=(ImageView)findViewById(R.id.imageViewPostImage);
		LL_info=(LinearLayout)findViewById(R.id.LL_info);
		textViewPostText=((TextView)findViewById(R.id.textViewPostText));

		buttonLike.setOnClickListener(this);
		buttonLikes.setOnClickListener(this);
		buttonComment.setOnClickListener(this);
		
		TextView textViewUsername = (TextView) findViewById(R.id.textViewUsername);
		ImageView imageViewUserimage = (ImageView)findViewById(R.id.imageViewUserImage);
		textViewUsername.setClickable(true);
		imageViewUserimage.setClickable(true);
		textViewUsername.setOnClickListener(this);
		imageViewUserimage.setOnClickListener(this);
		sharedConnect = Connect.getInstance(getContext());
		relativeLayout1=(RelativeLayout)findViewById(R.id.relativeLayout1);
		gestureDetector = new GestureDetector(context,new GestureListener());
		relativeLayout1.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}

		});
	}

	private void reloadView() {

		if (mPost.likedThisPost == 1) {
			buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
		} else {
			buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
		}
		if(mPost.postLikesCount>1){
			buttonLikes.setText(mPost.postLikesCount + " likes");
		}else{
			buttonLikes.setText(mPost.postLikesCount + " like");
		}
		buttonComment.setText(mPost.postCommentsCount+" ");
		UrlImageViewHelper.setUrlDrawable(((ImageView)findViewById(R.id.imageViewUserImage)), mPost.postUser.userAvatarPath);
		if( mPost.postImagePath.equalsIgnoreCase("")|| mPost.postImagePath.equals("null")){
			/*int wt_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
			RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, wt_px);
			imageViewPostImage.setLayoutParams(parms);*/
			imageViewPostImage.setVisibility(View.GONE);
			int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 58, getResources().getDisplayMetrics());

			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT,      
					LayoutParams.WRAP_CONTENT
					);
			params.setMargins(0, margin, 0, 0);

			LL_info.setLayoutParams(params);
			int margintop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
			int marginleft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
			LayoutParams paramstext = new LayoutParams(
					LayoutParams.MATCH_PARENT,      
					LayoutParams.WRAP_CONTENT
					);
			paramstext.setMargins(marginleft, margintop, marginleft, 0);
			textViewPostText.setLayoutParams(paramstext);
			//LL_info.setPadding(0, margin, 0, 0);
		}
		else{
			UrlImageViewHelper.setUrlDrawable(imageViewPostImage, mPost.postImagePath);
		}
		textViewPostText.setText(mPost.postTitle);
		((TextView)findViewById(R.id.textViewUsername)).setText(mPost.postUser.userFullName);
		((TextView)findViewById(R.id.textViewTimeAgo)).setText(mPost.timeAgo);
	}

	public void showUser() {

		Intent intentUser = new Intent(getContext(), ProfileActivity.class);
		intentUser.putExtra("userID", mPost.postUser.userID);
		getContext().startActivity(intentUser);
	}
	public void showPost() {

		Intent intentUser = new Intent(getContext(), PostDetailActivity.class);
		intentUser.putExtra("com.planet1107.welike.connect.Post", mPost);
		getContext().startActivity(intentUser);
	}
	public class GestureListener extends
	GestureDetector.SimpleOnGestureListener {

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

			if (mPost.likedThisPost==1) {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
			} else {
				buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_like, 0, 0, 0);
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
			if(mPost.postLikesCount>0){
				buttonLikes.setText( mPost.postLikesCount + " likes");
			}else
			{
				buttonLikes.setText( mPost.postLikesCount + " like");

			}
			reloadView();
			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {


					Connect sharedConnect = Connect.getInstance(getContext());
					if (mPost.likedThisPost==0) {
						return sharedConnect.unlikePost(mPost.postID);

					} else {
						return sharedConnect.likePost(mPost.postID);
					}
				}

				protected void onPostExecute(Boolean result) {

					/*if (result && mPost.likedThisPost==0) {
						buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_liked, 0, 0, 0);
						mPost.likedThisPost = 1;
						mPost.postLikesCount++;
					} else if (result) {
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
					//reloadView();
					 */				}

			}.execute();
			Log.d("Double Tap: ", "double tap called");
			return true;

		}
	}
}
