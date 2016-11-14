package com.planet1107.welike.activities;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.fragments.FriendsFragment;
import com.planet1107.welike.fragments.PopularFragment;
import com.planet1107.welike.fragments.ProfileFragment;
import com.planet1107.welike.fragments.SearchFragment;
import com.planet1107.welike.fragments.TimelineFragment;
import com.planet1107.welike.nodechat.DatabaseAdapter;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	ActionBar actionBar;
	int actionBarSelectedTabPosition;
	Connect mConnect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
		setContentView(R.layout.activity_main);		
		getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.my_custom_title );*/

		 setContentView(R.layout.activity_main);

		  
		actionBar = getActionBar();
		
		actionBar.setCustomView(R.layout.my_custom_title);
		
		 getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();
		mConnect=new Connect(this);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	DatabaseAdapter db=new DatabaseAdapter(MainActivity.this);
		        	db.open();
		    		String lastSynchDate = db.getlastupdateDate();
		    		db.close();
		    		mConnect.getchatDetail(lastSynchDate);
		    	
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    } 
		}).start();
	

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		Typeface FONT_AWESOME = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

		//String tabBarIcons[] = {"tabbar_timeline", "tabbar_popular", "tabbar_newpost", "tabbar_nearby", "tabbar_profile"};
		String tabBarIcons[] = {getString(R.string.fa_paper_plane_o), getString(R.string.fa_heart_o), getString(R.string.fa_search), getString(R.string.fa_comments_o), getString(R.string.fa_user)};
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout parentLinearLayout = (LinearLayout) inflater.inflate(R.layout.custom_navigation_item, null, true);

			final TextView t = (TextView)parentLinearLayout.findViewById(R.id.navigation_icon);
			t.setText(tabBarIcons[i]);
			t.setTypeface(FONT_AWESOME);
			t.setTextSize(27);
			t.setPadding(0, 0, 0, 0);

		
			Tab newTab = actionBar.newTab();			
			newTab.setCustomView(parentLinearLayout).setTabListener(this);
			actionBar.addTab(newTab);			
		}
	}

	@Override
	protected void onResume() {		
		super.onResume();
		actionBar.setSelectedNavigationItem(actionBarSelectedTabPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	
		mViewPager.setCurrentItem(tab.getPosition());
		actionBarSelectedTabPosition = tab.getPosition();
		
	}
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new TimelineFragment();
			} else if (position == 1) {
				return new PopularFragment();
			} else if (position == 2) {
				return new SearchFragment();
			} else if (position == 3) {
				return new FriendsFragment();
			} else {
				return new ProfileFragment();
			}
		}

		@Override
		public int getCount() {
			return 5;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	public void textViewFollowersOnClick(View v) {		
		Connect sharedConnect = Connect.getInstance(this);
		Intent followersIntent = new Intent(this, FollowersActivity.class);
		followersIntent.putExtra("userID",sharedConnect.getCurrentUser().userID);
		startActivity(followersIntent);
	}

	public void textViewFollowingOnClick(View v) {		
		Connect sharedConnect = Connect.getInstance(this);
		Intent followingIntent = new Intent(this, FriendsActivity.class);
		followingIntent.putExtra("userID",sharedConnect.getCurrentUser().userID);
		startActivity(followingIntent);
	}

	public void buttonSearchUsersOnClick(View v) {	
		Intent searchIntent = new Intent(this, SearchActivity.class);
		startActivity(searchIntent);
	}

	public void buttonLogoutOnClick(View v) {		
		Connect sharedConnect = Connect.getInstance(this);
		sharedConnect.getCurrentUser().deleteFromDisk(this);
		finish();
	}

	@Override
	public void onBackPressed() {

	}
}
