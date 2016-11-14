package com.planet1107.welike.fragments;

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
import com.planet1107.welike.activities.EditProfileActivity;
import com.planet1107.welike.activities.FriendsActivity;
import com.planet1107.welike.activities.PostDetailActivity;
import com.planet1107.welike.activities.WelcomeActivity;
import com.planet1107.welike.adapters.TimelineAdapter;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.loaders.RecentActivityLoader;
import com.planet1107.welike.views.NestedListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Post>>, OnRefreshListener, OnItemClickListener{

	NestedListView listViewRecent;
	TimelineAdapter mTimelineAdapter;
	RecentActivityLoader mRecentLoader;
	//	ProgressBar mProgressBarLoading;
	TextAwesome textAwesomeLogout,textAwesomeOccupation;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
	
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		//	mProgressBarLoading = (ProgressBar) getActivity().findViewById(R.id.progressBarLoading);
		textAwesomeLogout=(TextAwesome)getActivity().findViewById(R.id.textAwesomeLogout);
		mTimelineAdapter = new TimelineAdapter(getActivity());
		textAwesomeOccupation=(TextAwesome)getActivity().findViewById(R.id.textAwesomeOccupation);
		listViewRecent=(NestedListView)getActivity().findViewById(R.id.listViewRecentActivities);
		//	listViewRecent.setEmptyView(mProgressBarLoading);
		listViewRecent.setAdapter(mTimelineAdapter);
		listViewRecent.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewRecent.setOnItemClickListener(this);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(true);
		
		
	}

	@SuppressLint("SimpleDateFormat")
	public void onResume () {

		super.onResume();
		((Button)getActivity().findViewById(R.id.buttonFriends)).setTextColor(Color.WHITE);
		((Button)getActivity().findViewById(R.id.buttonFriends)).setBackgroundColor(getActivity().getResources().getColor(R.color.app_color) );
		final User user = sharedConnect.getCurrentUser();
		if(user.gender.equalsIgnoreCase("female")){
			((TextAwesome)getActivity().findViewById(R.id.textawesome_gender)).setText(R.string.fa_venus );
		}else if(user.gender.equalsIgnoreCase("male")){
			((TextAwesome)getActivity().findViewById(R.id.textawesome_gender)).setText(R.string.fa_mars );
		}
		String testDateString = user.birthDate;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 

		Date dob=null;
		try {
			dob = (Date) df.parse(testDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		System.out.println(dob);
		((TextView)getActivity().findViewById(R.id.textViewStatus)).setText(titleize(user.maritalStatus));
		((TextView)getActivity().findViewById(R.id.textViewAge)).setText(getDiffYears(dob, new Date())+" Years, ");
		((TextView)getActivity().findViewById(R.id.textViewProfileUsername)).setText(String.valueOf(sharedConnect.getCurrentUser().userFullName));

		if(sharedConnect.getCurrentUser().userAvatarPath.equals("null")){

			((ImageView)getView().findViewById(R.id.imageViewProfileUserImage)).setImageResource(R.drawable.avatar_empty);
		}else{
			UrlImageViewHelper.setUrlDrawable((ImageView)getView().findViewById(R.id.imageViewProfileUserImage), sharedConnect.getCurrentUser().userAvatarPath);
		}

		((TextView)getActivity().findViewById(R.id.textViewLocation)).setText(user.location);
		if(user.interests.equals("")){
			((TextView)getActivity().findViewById(R.id.lableInterest)).setVisibility(View.GONE);
			((TextView)getActivity().findViewById(R.id.textViewInterest)).setVisibility(View.GONE);
		}
		((TextView)getActivity().findViewById(R.id.textViewInterest)).setText(user.interests);
		((Button)getActivity().findViewById(R.id.buttonAdd)).setVisibility(View.GONE);

		((Button)getActivity().findViewById(R.id.buttonFriends)).setText(user.friendCount+ " Friends");
		if(user.occupation.equals("")||user.occupation.equals("null")){
			textAwesomeOccupation.setVisibility(View.GONE);
			((TextView)getActivity().findViewById(R.id.textViewOccupation)).setVisibility(View.GONE);
		}
		((TextView)getActivity().findViewById(R.id.textViewOccupation)).setText(String.valueOf(user.occupation));
		((Button)getActivity().findViewById(R.id.buttonFriends)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((Button)getActivity().findViewById(R.id.buttonFriends)).setTextColor(Color.BLACK);
				((Button)getActivity().findViewById(R.id.buttonFriends)).setBackgroundResource(R.drawable.button_border);
				startActivity(new Intent(getActivity(),FriendsActivity.class).putExtra("userID", user.userID));

			}
		});
		((Button)getActivity().findViewById(R.id.buttonPosts)).setText("Posts");

		textAwesomeLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Connect sharedConnect = Connect.getInstance(getActivity());

				if(sharedConnect.getCurrentUser().deleteFromDisk(getActivity())){
					PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("is_logout", true).commit();
					startActivity(new Intent(getActivity(),WelcomeActivity.class).putExtra("is_logout", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					getActivity().finish();
				}
			}
		});
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {		      
		    	((ScrollView)getActivity().findViewById(R.id.scrollView)).smoothScrollTo(0,0);
		    }
		}, 500);
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		String editProfileFontAwesomeString = getString(R.string.fa_pencil_square);
		Typeface FONT_AWESOME = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf" );
		//inflater.inflate(R.menu.fragment_timeline_menu, menu);
		final TextView t = new TextView(getActivity());
		t.setText(editProfileFontAwesomeString);
		t.setTypeface(FONT_AWESOME);
		t.setTextColor(Color.WHITE);
		t.setTextSize(35);
		t.setPadding(0, 0, 20, 0);
		menu.add("editprofile").setActionView(t).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		t.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuItemEditOnClick();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.action_edit) {
			menuItemEditOnClick();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void menuItemEditOnClick() {

		Intent editItent = new Intent(getActivity(), EditProfileActivity.class);
		startActivity(editItent);
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
	public Loader<List<Post>> onCreateLoader(int id, Bundle args) {

		mRecentLoader = new RecentActivityLoader(getActivity(), listViewRecent,sharedConnect.getCurrentUser().userID);
		return mRecentLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<Post>> arg0, List<Post> data) {

		mTimelineAdapter.setData(data);
		mTimelineAdapter.notifyDataSetChanged();
		//mPullToRefreshLayout.setRefreshComplete();
		//mTextViewNoItems.setVisibility(View.VISIBLE);
		//	mProgressBarLoading.setVisibility(View.INVISIBLE);

		((Button)getActivity().findViewById(R.id.buttonPosts)).setText(mTimelineAdapter.getCount()+ " Posts");
		if(mTimelineAdapter.getCount()==0){
			((TextView)getActivity().findViewById(R.id.lableActivity)).setVisibility(View.GONE);	
		}
		((ScrollView)getActivity().findViewById(R.id.scrollView)).smoothScrollTo(0,0);
		//listViewRecent.setEmptyView(mTextViewNoItems);
	}
	@Override
	public void onRefreshStarted(View view) {

		getLoaderManager().destroyLoader(0);
		getLoaderManager().initLoader(0, null, this);
		// mTextViewNoItems.setVisibility(View.INVISIBLE);
		//	mProgressBarLoading.setVisibility(View.VISIBLE);
		//	mListViewTimeline.setEmptyView(mProgressBarLoading);
	}


	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

		Intent intentPostDetail = new Intent(getActivity(), PostDetailActivity.class);
		Post post = mTimelineAdapter.getItem(position);
		intentPostDetail.putExtra("com.planet1107.welike.connect.Post", post);	
		startActivity(intentPostDetail);
	}
	@Override
	public void onLoaderReset(Loader<List<Post>> arg0) {

		mTimelineAdapter.setData(null);
	
	}
	/**** Method for Setting the Height of the ListView dynamically.
	 **** Hack to fix the issue of not showing all the items of the ListView
	 **** when placed inside a ScrollView  ****/
	/*	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	//	listView.requestLayout();

	}*/

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
