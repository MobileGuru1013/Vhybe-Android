package com.planet1107.welike.fragments;

import java.util.List;

import com.planet1107.welike.R;
import com.planet1107.welike.activities.NewPostActivity;
import com.planet1107.welike.activities.PostDetailActivity;
import com.planet1107.welike.adapters.TimelineAdapter;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.loaders.PopularLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class PopularFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Post>>, TabHost.OnTabChangeListener, OnItemClickListener {
	
	TimelineAdapter mPopularAdapter;
    PopularLoader mPopularLoader;
    ListView listViewPopular;
    ListView listViewRecent;
    ProgressBar mProgressBarLoading;
    TextView mTextViewNoItems;
    TabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_popular, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		  final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		setHasOptionsMenu(true);
		mTabHost = (TabHost)getActivity().findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);
		
		setupTab(new TextView(getActivity()), "Popular");
		setupTab(new TextView(getActivity()), "Recent");
	  
	    mProgressBarLoading = (ProgressBar) getActivity().findViewById(R.id.progressBarLoading);
	    mTextViewNoItems = (TextView) getActivity().findViewById(R.id.textViewNoItems);

        mPopularAdapter = new TimelineAdapter(getActivity());

        listViewPopular = (ListView) getActivity().findViewById(R.id.listViewPopular);
        listViewPopular.setEmptyView(mProgressBarLoading);
        listViewPopular.setAdapter(mPopularAdapter);
        listViewPopular.setOnItemClickListener(this);
        
        listViewRecent = (ListView) getActivity().findViewById(R.id.listViewRecent);
        listViewRecent.setEmptyView(mProgressBarLoading);
        listViewRecent.setOnItemClickListener(this);
        
        getLoaderManager().initLoader(0, null, this);
	}

	@Override
    public Loader<List<Post>> onCreateLoader(int id, Bundle args) {
        
		if (mTabHost.getCurrentTab() == 0) {
			mPopularLoader = new PopularLoader(getActivity(), PopularLoader.PopularOption.PopularOptionPopular);
		} else {
			mPopularLoader = new PopularLoader(getActivity(), PopularLoader.PopularOption.PopularOptionRecent);
		}
		return mPopularLoader;
    }

	@Override
	public void onLoadFinished(Loader<List<Post>> arg0, List<Post> data) {
        
		mPopularAdapter.setData(data);
		mPopularAdapter.notifyDataSetChanged();
		mTextViewNoItems.setVisibility(View.VISIBLE);
		mProgressBarLoading.setVisibility(View.INVISIBLE);
		listViewPopular.setEmptyView(mTextViewNoItems);
		listViewRecent.setEmptyView(mTextViewNoItems);
	}

	@Override
	public void onLoaderReset(Loader<List<Post>> arg0) {

		mPopularAdapter.setData(null);
	}

	@Override
	public void onTabChanged(String tabId) {
		
		getLoaderManager().destroyLoader(0);
        getLoaderManager().initLoader(0, null, this);        
	}
	
	private void setupTab(final View view, final String tag) {
		
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
			public View createTabContent(String tag) {return view;}
		});
		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
		
		Intent intentPostDetail = new Intent(getActivity(), PostDetailActivity.class);
		Post post = mPopularAdapter.getItem(position);
		intentPostDetail.putExtra("com.planet1107.welike.connect.Post", post);	
		startActivity(intentPostDetail);
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
	    menu.add("newpost").setActionView(t).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    
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

		/*int itemId = item.getItemId();
		if (itemId == R.id.action_new_post) {
			menuNewPostOnClick();
			return true;
		} else {*/
			return super.onOptionsItemSelected(item);
		//}
	}
	
	public void menuNewPostOnClick() {
		
		Intent editItent = new Intent(getActivity(), NewPostActivity.class);
		startActivity(editItent);
	}

	
}
