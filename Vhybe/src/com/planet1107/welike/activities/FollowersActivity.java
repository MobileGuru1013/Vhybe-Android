package com.planet1107.welike.activities;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.RequestAdapter;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.loaders.RequestLoader;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class FollowersActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<User>>, OnRefreshListener, OnScrollListener {

	RequestAdapter mFollowersAdapter;
	RequestLoader mFollowersLoader;
    ListView mListViewFollowers;
    ProgressBar mProgressBarLoading;
    TextView mTextViewNoItems;
    int mUserID;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_followers);
		setTitle("Followers");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    mUserID = extras.getInt("userID");
		}
		
		mProgressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
	    mTextViewNoItems = (TextView) findViewById(R.id.textViewNoItems);
		
        mFollowersAdapter = new RequestAdapter(this);

        mListViewFollowers = (ListView) findViewById(R.id.listViewFollowers);
        mListViewFollowers.setEmptyView(mProgressBarLoading);
        mListViewFollowers.setAdapter(mFollowersAdapter);
        mListViewFollowers.setOnScrollListener(this);
        mListViewFollowers.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        
        getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.followers, menu);
		return true;
	}

	@Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        
		mFollowersLoader = new RequestLoader(this, mUserID);
		return mFollowersLoader;
    }

	@Override
	public void onLoadFinished(Loader<List<User>> arg0, List<User> data) {
        
		mFollowersAdapter.setData(data);
		mFollowersAdapter.notifyDataSetChanged();
		mTextViewNoItems.setVisibility(View.VISIBLE);
		mProgressBarLoading.setVisibility(View.INVISIBLE);
		mListViewFollowers.setEmptyView(mTextViewNoItems);
	}

	@Override
	public void onLoaderReset(Loader<List<User>> arg0) {

        mFollowersAdapter.setData(null);
	}

	@Override
	public void onRefreshStarted(View view) {
		
		getSupportLoaderManager().destroyLoader(0);
		getSupportLoaderManager().initLoader(0, null, this);
		mTextViewNoItems.setVisibility(View.INVISIBLE);
		mProgressBarLoading.setVisibility(View.VISIBLE);
		mListViewFollowers.setEmptyView(mProgressBarLoading);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
			if (mFollowersLoader.loadMore() && !mFollowersLoader.loading()) {
				mFollowersLoader.onContentChanged();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

}
