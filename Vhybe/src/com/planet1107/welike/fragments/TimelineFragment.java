package com.planet1107.welike.fragments;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.planet1107.welike.R;
import com.planet1107.welike.activities.NewPostActivity;
import com.planet1107.welike.activities.PostDetailActivity;
import com.planet1107.welike.adapters.TimelineAdapter;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.loaders.TimelineLoader;

import android.support.v4.content.Loader;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v4.app.LoaderManager;

public class TimelineFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Post>>, OnRefreshListener, OnScrollListener, OnItemClickListener {

    TimelineAdapter mTimelineAdapter;
    TimelineLoader mTimelineLoader;
    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListViewTimeline;
    ProgressBar mProgressBarLoading;
    TextView mTextViewNoItems;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timeline, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	    mPullToRefreshLayout = (PullToRefreshLayout) getActivity().findViewById(R.id.ptr_layout);
	    ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable().listener(this).setup(mPullToRefreshLayout);
	  
	    mProgressBarLoading = (ProgressBar) getActivity().findViewById(R.id.progressBarLoading);
	    mTextViewNoItems = (TextView) getActivity().findViewById(R.id.textViewNoItems);
        mListViewTimeline = (ListView) getActivity().findViewById(R.id.listViewTimeline);
        mTimelineAdapter = new TimelineAdapter(getActivity());

        mListViewTimeline.setEmptyView(mProgressBarLoading);
        mListViewTimeline.setAdapter(mTimelineAdapter);
        mListViewTimeline.setOnScrollListener(this);
        mListViewTimeline.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListViewTimeline.setOnItemClickListener(this);
        mTextViewNoItems.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity( new Intent(getActivity(),NewPostActivity.class));
			}
		});
        /*mListViewTimeline.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});*/
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
    public Loader<List<Post>> onCreateLoader(int id, Bundle args) {
        
        mTimelineLoader = new TimelineLoader(getActivity(), mListViewTimeline);
		return mTimelineLoader;
    }

	@Override
	public void onLoadFinished(Loader<List<Post>> arg0, List<Post> data) {
        
		mTimelineAdapter.setData(data);
		mTimelineAdapter.notifyDataSetChanged();
		mPullToRefreshLayout.setRefreshComplete();
		mTextViewNoItems.setVisibility(View.VISIBLE);
		mProgressBarLoading.setVisibility(View.INVISIBLE);
		mListViewTimeline.setEmptyView(mTextViewNoItems);
	}

	@Override
	public void onLoaderReset(Loader<List<Post>> arg0) {

        mTimelineAdapter.setData(null);
	}

	@Override
	public void onRefreshStarted(View view) {
		
		getLoaderManager().destroyLoader(0);
        getLoaderManager().initLoader(0, null, this);
        mTextViewNoItems.setVisibility(View.INVISIBLE);
		mProgressBarLoading.setVisibility(View.VISIBLE);
		mListViewTimeline.setEmptyView(mProgressBarLoading);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
			if (mTimelineLoader.loadMore() && !mTimelineLoader.loading()) {
				mTimelineLoader.onContentChanged();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		String newPostFontAwesomeString = getString(R.string.fa_edit);
		Typeface FONT_AWESOME = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf" );
		//inflater.inflate(R.menu.fragment_timeline_menu, menu);
		final TextView t = new TextView(getActivity());
	    t.setText(newPostFontAwesomeString);
	    t.setTypeface(FONT_AWESOME);
	    t.setTextColor(Color.WHITE);
	    t.setTextSize(35);
	    t.setPadding(0, 0, 20, 0);
	    t.setId(R.id.action_new_post);
	    menu.add("newpost").setActionView(t).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(0, FILTER_ID, 1, R.string.matchmacking).setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    	t.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuNewPostOnClick();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		
		Intent intentPostDetail = new Intent(getActivity(), PostDetailActivity.class);
		Post post = mTimelineAdapter.getItem(position);
		intentPostDetail.putExtra("com.planet1107.welike.connect.Post", post);	
		startActivity(intentPostDetail);
	}
	public void menuNewPostOnClick() {
		
		Intent editItent = new Intent(getActivity(), NewPostActivity.class);
		startActivity(editItent);
	}
}
