package com.planet1107.welike.activities;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.LikesAdapter;
import com.planet1107.welike.connect.Like;
import com.planet1107.welike.loaders.LikesLoader;

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

public class LikesActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Like>>, OnRefreshListener, OnScrollListener {

	LikesAdapter mLikesAdapter;
    LikesLoader mLikesLoader;
    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListViewLikes;
    ProgressBar mProgressBarLoading;
    TextView mTextViewNoItems;
    int mPostID;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likes);
		setTitle("Likes");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    mPostID = extras.getInt("postID");
		}
		
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
	    ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(this).setup(mPullToRefreshLayout);
	  
	    mProgressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
	    mTextViewNoItems = (TextView) findViewById(R.id.textViewNoItems);
	    
        mLikesAdapter = new LikesAdapter(this);

        mListViewLikes = (ListView) findViewById(R.id.listViewLikes);
        mListViewLikes.setEmptyView(mProgressBarLoading);
        mListViewLikes.setAdapter(mLikesAdapter);
        mListViewLikes.setOnScrollListener(this);
        mListViewLikes.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        
        getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.likes, menu);
		return true;
	}

	@Override
    public Loader<List<Like>> onCreateLoader(int id, Bundle args) {
        
		mLikesLoader = new LikesLoader(this, mListViewLikes, mPostID);
		return mLikesLoader;
    }

	@Override
	public void onLoadFinished(Loader<List<Like>> arg0, List<Like> data) {
        
		mLikesAdapter.setData(data);
		mLikesAdapter.notifyDataSetChanged();
		mPullToRefreshLayout.setRefreshComplete();
		mTextViewNoItems.setVisibility(View.VISIBLE);
		mProgressBarLoading.setVisibility(View.INVISIBLE);
		mListViewLikes.setEmptyView(mTextViewNoItems);
	}

	@Override
	public void onLoaderReset(Loader<List<Like>> arg0) {

        mLikesAdapter.setData(null);
	}

	@Override
	public void onRefreshStarted(View view) {
		
		getSupportLoaderManager().destroyLoader(0);
		getSupportLoaderManager().initLoader(0, null, this);
		mTextViewNoItems.setVisibility(View.INVISIBLE);
		mProgressBarLoading.setVisibility(View.VISIBLE);
		mListViewLikes.setEmptyView(mProgressBarLoading);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
			if (mLikesLoader.loadMore() && !mLikesLoader.loading()) {
				mLikesLoader.onContentChanged();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

}
