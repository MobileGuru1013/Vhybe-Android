package com.planet1107.welike.views;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.activities.ProfileActivity;
import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.connect.Connect;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class CommentListItem extends RelativeLayout implements OnClickListener {

	Comment mComment;
	ImageButton mImageButtonFollowUnfollow;
	Connect sharedConnect;

	public CommentListItem(Context context) {

		super(context);
	}

	public CommentListItem(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	public CommentListItem(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
	}

	public void setComment(Comment comment) {

		mComment = comment;	
		reloadView();
	}

	protected void onFinishInflate () {

		TextView textViewUsername = (TextView) findViewById(R.id.textViewCommentUsername);
		ImageView imageViewUserimage = (ImageView)findViewById(R.id.imageViewCommentUserImage);
		textViewUsername.setClickable(true);
		imageViewUserimage.setClickable(true);
		textViewUsername.setOnClickListener(this);
		imageViewUserimage.setOnClickListener(this);
	}

	private void reloadView() {
		try{
			UrlImageViewHelper.setUrlDrawable(((ImageView)findViewById(R.id.imageViewCommentUserImage)), mComment.user.userAvatarPath);
			((TextView)findViewById(R.id.textViewCommentUsername)).setText(mComment.user.userFullName);
			((TextView)findViewById(R.id.textViewCommentText)).setText(mComment.commentText);
		}catch(NullPointerException e){

		}
	}

	@Override
	public void onClick(View v) {

		showUser();
	}

	public void showUser() {

		if (mComment != null && mComment.user != null && mComment.user.userID > 0) {
			Intent intentUser = new Intent(getContext(), ProfileActivity.class);
			intentUser.putExtra("userID", mComment.user.userID);
			getContext().startActivity(intentUser);
		}
	}
}
