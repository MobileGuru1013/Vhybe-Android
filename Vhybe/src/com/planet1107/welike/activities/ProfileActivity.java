package com.planet1107.welike.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.fontawesome.TextAwesome;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.adapters.TimelineAdapter;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.loaders.OthersRecentActivityLoader;
import com.planet1107.welike.nodechat.DatabaseAdapter;
import com.planet1107.welike.views.NestedListView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class ProfileActivity extends Activity implements  OnRefreshListener, OnItemClickListener {

	int mUserID;
	NestedListView listViewRecent;
	TimelineAdapter mTimelineAdapter;
	OthersRecentActivityLoader mRecentLoader;
	TextAwesome textAwesomeLogout,textAwesomeOccupation;
	//ProgressBar mProgressBarLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_profile);
		setTitle("Profile");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mUserID = extras.getInt("userID");
		}
		//	mProgressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
		textAwesomeLogout=(TextAwesome)findViewById(R.id.textAwesomeLogout);
		textAwesomeOccupation=(TextAwesome)findViewById(R.id.textAwesomeOccupation);
		mTimelineAdapter = new TimelineAdapter(this);
		listViewRecent=(NestedListView)findViewById(R.id.listViewRecentActivities);
		//	listViewRecent.setEmptyView(mProgressBarLoading);
		listViewRecent.setAdapter(mTimelineAdapter);

		listViewRecent.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewRecent.setOnItemClickListener(this);


		new AsyncTask<Void, Void, User>() {

			@Override
			protected User doInBackground(Void... params) {

				Connect sharedConnect = Connect.getInstance(ProfileActivity.this);
				return sharedConnect.getUser(mUserID);
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			protected void onPostExecute(final User user) {
				try{
					if(user.gender.equalsIgnoreCase("female")){
						((TextAwesome)findViewById(R.id.textawesome_gender)).setText(R.string.fa_venus );
					}
				}catch(NullPointerException e){

				}
				Date dob=null;
				try{
					String testDateString = user.birthDate;
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 


					try {
						dob = (Date) df.parse(testDateString);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
					System.out.println(dob);
				}catch(NullPointerException e){

				}
				try{
					((TextView)findViewById(R.id.textViewStatus)).setText(titleize(user.maritalStatus));
				}
				catch (NullPointerException e) {
					// TODO: handle exception
				}
				((TextView)findViewById(R.id.textViewAge)).setText(getDiffYears(dob, new Date())+" Years, ");
				((TextView)findViewById(R.id.textViewProfileUsername)).setText(String.valueOf(user.userFullName));
				((TextView)findViewById(R.id.textViewOccupation)).setText(String.valueOf(user.occupation));

				if(user.userAvatarPath.equals("null")){

					((ImageView)findViewById(R.id.imageViewProfileUserImage)).setImageResource(R.drawable.avatar_empty);
				}else{
					UrlImageViewHelper.setUrlDrawable((ImageView)findViewById(R.id.imageViewProfileUserImage), user.userAvatarPath);
				}

				//UrlImageViewHelper.setUrlDrawable(((ImageView)findViewById(R.id.imageViewProfileUserImage)), user.userAvatarPath);
				getLoaderManager().initLoader(0, null, mRequestCallback);
				if(user.occupation.equals("")||user.occupation.equals("null")){
					textAwesomeOccupation.setVisibility(View.GONE);
					((TextView)findViewById(R.id.textViewOccupation)).setVisibility(View.GONE);
				}
				((TextView)findViewById(R.id.textViewLocation)).setText(user.location);
				if(user.interests.equals("")){					
					((TextView)findViewById(R.id.textViewInterest)).setVisibility(View.GONE);
					((TextView)findViewById(R.id.lableInterest)).setVisibility(View.GONE);
				}else{
					((TextView)findViewById(R.id.textViewInterest)).setText(user.interests);
				}
				if(user.userID==(new Connect(ProfileActivity.this).getCurrentUser().userID)){
					textAwesomeLogout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							DatabaseAdapter db =new DatabaseAdapter(ProfileActivity.this);
							db.open();
							db.clean();
							db.close();
							Connect sharedConnect = Connect.getInstance(ProfileActivity.this);
							sharedConnect.getCurrentUser().deleteFromDisk(ProfileActivity.this);
							PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit().putBoolean("is_logout", true).commit();
							startActivity(new Intent(ProfileActivity.this,WelcomeActivity.class).putExtra("is_logout", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
							finish();
						}
					});
					((Button)findViewById(R.id.buttonAdd)).setVisibility(View.GONE);
				}
				else{
					textAwesomeLogout.setVisibility(View.GONE);
					switch (user.friendshipStatus) {
					case 0:
						((Button)findViewById(R.id.buttonAdd)).setText("Add Friend");
						break;
					case 1:
						((Button)findViewById(R.id.buttonAdd)).setText("Unfriend");
						break;
					case 2:
						((Button)findViewById(R.id.buttonAdd)).setText("Accept");
						break;
					case 3:
						((Button)findViewById(R.id.buttonAdd)).setText("Request Sent");
						break;

					default:
						break;
					}
				}
				((Button)findViewById(R.id.buttonAdd)).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						((Button)findViewById(R.id.buttonAdd)).setTextColor(Color.BLACK);
						((Button)findViewById(R.id.buttonAdd)).setBackgroundResource(R.drawable.button_border);
						if(user.friendshipStatus ==0){
							new AsyncTask<Void, Void, Boolean>() {

								@Override
								protected Boolean doInBackground(Void... params) {
									Connect sharedConnect = Connect.getInstance(ProfileActivity.this);
									return sharedConnect.addFriend(user.userID);
								}

								@SuppressLint("SimpleDateFormat")
								@Override
								protected void onPostExecute(final Boolean status) {
									if(status){
										((Button)findViewById(R.id.buttonAdd)).setText("Request Sent");
										((Button)findViewById(R.id.buttonAdd)).setTextColor(Color.WHITE);
										((Button)findViewById(R.id.buttonAdd)).setBackgroundColor(getApplicationContext().getResources().getColor(R.color.app_color) );
									}

								}
							}.execute();

						}else if(user.friendshipStatus ==2){
							new AsyncTask<Void, Void, Boolean>() {

								@Override
								protected Boolean doInBackground(Void... params) {
									Connect sharedConnect = Connect.getInstance(ProfileActivity.this);
									return sharedConnect.friendRequestResponse(user.userID,1);
								}

								@SuppressLint("SimpleDateFormat")
								@Override
								protected void onPostExecute(final Boolean status) {
									if(status){
										((Button)findViewById(R.id.buttonAdd)).setText("Already Added");
										((Button)findViewById(R.id.buttonAdd)).setTextColor(Color.WHITE);
										((Button)findViewById(R.id.buttonAdd)).setBackgroundColor(getApplicationContext().getResources().getColor(R.color.app_color) );
									}

								}
							}.execute();							
						}else if(user.friendshipStatus ==1){
							new AsyncTask<Void, Void, Boolean>() {

								@Override
								protected Boolean doInBackground(Void... params) {
									Connect sharedConnect = Connect.getInstance(ProfileActivity.this);
									return sharedConnect.removeFriend(user.userID);
								}

								@SuppressLint("SimpleDateFormat")
								@Override
								protected void onPostExecute(final Boolean status) {
									if(status){
										((Button)findViewById(R.id.buttonAdd)).setTextColor(Color.WHITE);
										((Button)findViewById(R.id.buttonAdd)).setBackgroundColor(getApplicationContext().getResources().getColor(R.color.app_color) );
										((Button)findViewById(R.id.buttonAdd)).setText("Add Friend");
									}

								}
							}.execute();


						}else{
							((Button)findViewById(R.id.buttonAdd)).setTextColor(Color.WHITE);
							((Button)findViewById(R.id.buttonAdd)).setBackgroundColor(getApplicationContext().getResources().getColor(R.color.app_color) );
						}
					

					}
				});

				((Button)findViewById(R.id.buttonFriends)).setText(user.friendCount+ " Friends");
				((Button)findViewById(R.id.buttonFriends)).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						((Button)findViewById(R.id.buttonFriends)).setTextColor(Color.BLACK);
						((Button)findViewById(R.id.buttonFriends)).setBackgroundResource(R.drawable.button_border);
						startActivity(new Intent(ProfileActivity.this,FriendsActivity.class).putExtra("userID", user.userID));

					}
				});
				// ((Button)findViewById(R.id.buttonPosts)).setText("Posts");
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.profile_menu, menu);
		return true;
	}


	public int getDiffYears(Date first, Date last) {
		try{
			Calendar a = getCalendar(first);
			Calendar b = getCalendar(last);
			int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
			if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || 
					(a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
				diff--;
			}
			return diff;
		}catch(NullPointerException e){
			return 0;
		}
	}
	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}


	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

		Intent intentPostDetail = new Intent(this, PostDetailActivity.class);
		Post post = mTimelineAdapter.getItem(position);
		intentPostDetail.putExtra("com.planet1107.welike.connect.Post", post);	
		startActivity(intentPostDetail);
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((Button)findViewById(R.id.buttonFriends)).setTextColor(Color.WHITE);
		((Button)findViewById(R.id.buttonFriends)).setBackgroundColor(getApplicationContext().getResources().getColor(R.color.app_color) );
	}
	private LoaderManager.LoaderCallbacks<List<Post>> mRequestCallback = new  LoaderManager.LoaderCallbacks<List<Post>>() {


		@Override
		public Loader<List<Post>> onCreateLoader(int id, Bundle args) {

			mRecentLoader = new OthersRecentActivityLoader(ProfileActivity.this, listViewRecent,mUserID);
			return mRecentLoader;

		}

		@Override
		public void onLoadFinished(Loader<List<Post>> arg0, List<Post> data) {
			mTimelineAdapter.setData(data);
			mTimelineAdapter.notifyDataSetChanged();
			//mPullToRefreshLayout.setRefreshComplete();
			//mTextViewNoItems.setVisibility(View.VISIBLE);
			///	mProgressBarLoading.setVisibility(View.INVISIBLE);

			((Button)findViewById(R.id.buttonPosts)).setText(mTimelineAdapter.getCount()+ " Posts");
			((ScrollView)findViewById(R.id.scrollView)).smoothScrollTo(0,0);
			if(mTimelineAdapter.getCount()==0){
				((TextView)findViewById(R.id.lableActivity)).setVisibility(View.GONE);	
			}
			//listViewRecent.setEmptyView(mTextViewNoItems);

		}
		@Override
		public void onLoaderReset(Loader<List<Post>> arg0) {		

			getLoaderManager().destroyLoader(0);
			getLoaderManager().initLoader(0, null,  mRequestCallback);
			// mTextViewNoItems.setVisibility(View.INVISIBLE);
			//	mProgressBarLoading.setVisibility(View.VISIBLE);
			//	mListViewTimeline.setEmptyView(mProgressBarLoading);

		}
	};

	String titleize(String source){
		boolean cap = true;
		char[]  out = source.toCharArray();
		int i, len = source.length();
		for(i=0; i<len; i++){
			if(Character.isWhitespace(out[i])){
				cap = true;
				continue;
			}
			if(cap){
				out[i] = Character.toUpperCase(out[i]);
				cap = false;
			}
		}
		return new String(out);
	}
}
