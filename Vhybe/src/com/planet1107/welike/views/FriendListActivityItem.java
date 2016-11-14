package com.planet1107.welike.views;

import com.fontawesome.TextAwesome;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.activities.ChatActivity;
import com.planet1107.welike.activities.ProfileActivity;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.fragments.FriendsFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class FriendListActivityItem extends RelativeLayout implements OnClickListener {

	User mUser;
	TextAwesome mImageButtonDelete,imageButtonChat;
	Connect sharedConnect;
	Context context;
	public FriendListActivityItem(Context context) {

		super(context);
		this.context=context;
	}

	public FriendListActivityItem(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context=context;
	}

	public FriendListActivityItem(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		this.context=context;
	}

	public void setUser(User user) {

		mUser = user;	
		reloadView();
	}

	protected void onFinishInflate () {

		mImageButtonDelete = (TextAwesome) this.findViewById(R.id.imageButtonDelete);
		imageButtonChat=(TextAwesome)this.findViewById(R.id.imageButtonChat);
		imageButtonChat.setFocusable(false);
		imageButtonChat.setOnClickListener(this);
		mImageButtonDelete.setFocusable(false);
		mImageButtonDelete.setOnClickListener(this);
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

		if (v == mImageButtonDelete) {
			if (mUser.userID == sharedConnect.getCurrentUser().userID) {
				return;
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				// set title
				alertDialogBuilder.setTitle("Remove Friend");
				// set dialog message
				alertDialogBuilder
				.setMessage("Are you sure you want to remove friend?")
				.setCancelable(false)
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				})
				.setPositiveButton("Remove",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {									
						dialog.cancel();
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {

								sharedConnect.removeFriend(mUser.userID);
								return null;
							}
							 
							protected void onPostExecute(Void result) {
								reloadView();
							};
						}.execute();
						mUser.followingUser = false;
						sharedConnect.getCurrentUser().friendCount--;
						FriendsFragment.updateFriend(mUser);
					}
				});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
			}
			reloadView();
		} else if (v == imageButtonChat){
			Intent intentChat = new Intent(getContext(), ChatActivity.class);
			intentChat.putExtra("ChatUser", mUser);
			getContext().startActivity(intentChat);
		}else
		{
			showUser();
		}

	}

	private void reloadView() {		
		imageButtonChat.setVisibility(View.GONE);
		mImageButtonDelete.setVisibility(View.GONE);
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
