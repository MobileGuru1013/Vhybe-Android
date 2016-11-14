package com.planet1107.welike.views;

import com.fontawesome.TextAwesome;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.activities.ProfileActivity;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.fragments.FriendsFragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class RequestListItem extends RelativeLayout implements OnClickListener {

	User mUser;
	TextAwesome mImageButtonAccept;
	TextAwesome mImageButtonReject;
	Connect sharedConnect;

	public RequestListItem(Context context) {

		super(context);
	}

	public RequestListItem(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	public RequestListItem(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
	}

	public void setUser(User user) {

		mUser = user;	
		reloadView();
	}

	protected void onFinishInflate () {

		mImageButtonAccept = (TextAwesome) this.findViewById(R.id.imageButtonAccept);
		mImageButtonAccept.setFocusable(false);
		mImageButtonAccept.setOnClickListener(this);
		mImageButtonReject = (TextAwesome) this.findViewById(R.id.imageButtonReject);
		mImageButtonReject.setFocusable(false);
		mImageButtonReject.setOnClickListener(this);
		TextView textViewUsername = (TextView) findViewById(R.id.textViewUserUsername);
		ImageView imageViewUserimage = (ImageView)findViewById(R.id.imageViewUserUserImage);
		textViewUsername.setClickable(true);
		imageViewUserimage.setClickable(true);
		textViewUsername.setOnClickListener(this);
		imageViewUserimage.setOnClickListener(this);
		sharedConnect = Connect.getInstance(getContext());
	}

	@Override
	public void onClick(View v) {

		if (v == mImageButtonAccept) {
			if (mUser.userID == sharedConnect.getCurrentUser().userID) {
				return;
			} else {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						sharedConnect.friendRequestResponse(mUser.userID,1);
						return null;
					}
					protected void onPostExecute(Void result) {
						mUser.followingUser = false;
						sharedConnect.getCurrentUser().userFollowingCount--;
						FriendsFragment.updateRequest(mUser);
					};
				}.execute();
				
			}

		}else if(v == mImageButtonReject)
		{
			if (mUser.userID == sharedConnect.getCurrentUser().userID) {
				return;
			} else {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						sharedConnect.friendRequestResponse(mUser.userID,0);
						return null;
					}
					protected void onPostExecute(Void result) {
						mUser.followingUser = true;
						sharedConnect.getCurrentUser().userFollowingCount++;
						FriendsFragment.updateRequest(mUser);
					};
				}.execute();
				
			}
			reloadView();
		} else {
			showUser();
		}

	}

	private void reloadView() {

		/*if (mUser.userID == sharedConnect.getCurrentUser().userID) {
			mImageButtonFollowUnfollow.setVisibility(View.INVISIBLE);
		} else if (mUser.followingUser) {
			mImageButtonFollowUnfollow.setVisibility(View.VISIBLE);
			mImageButtonFollowUnfollow.setImageResource(R.drawable.btn_unfollow);
		} else {
			mImageButtonFollowUnfollow.setVisibility(View.VISIBLE);
			mImageButtonFollowUnfollow.setImageResource(R.drawable.btn_follow);
		}*/

		if(mUser.userAvatarPath.equals("null")||mUser.userAvatarPath.equals("")){
			((ImageView)findViewById(R.id.imageViewUserUserImage)).setImageResource(R.drawable.avatar_empty);
		}else{
        UrlImageViewHelper.setUrlDrawable(((ImageView)findViewById(R.id.imageViewUserUserImage)), mUser.userAvatarPath);
		}
		((TextView)findViewById(R.id.textViewUserUsername)).setText(mUser.userFullName);
	}

	public void showUser() {		
		Intent intentUser = new Intent(getContext(), ProfileActivity.class);
		intentUser.putExtra("userID", mUser.userID);
		getContext().startActivity(intentUser);
	}
}
