package com.planet1107.welike.activities;

import java.util.List;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.PostDetailsAdapter;
import com.planet1107.welike.adapters.TimelineAdapter.GestureListener;
import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.connect.Post;
import com.planet1107.welike.loaders.CommentsLoader;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class PostDetailActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Comment>>, OnScrollListener {

	ListView mListViewPostDetail;
	PostDetailsAdapter mPostDetailsAdapter;
	CommentsLoader mCommentsLoader;
	Post mPost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_post_detail);

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		setTitle("Post detail");		
		mPost = (Post) getIntent().getSerializableExtra("com.planet1107.welike.connect.Post");
		System.out.println(mPost.postID+mPost.postTitle+"  post detail");
		mPostDetailsAdapter = new PostDetailsAdapter(this, mPost);		
		mListViewPostDetail = (ListView) findViewById(R.id.listViewPostDetail);
		mListViewPostDetail.setAdapter(mPostDetailsAdapter);
		mListViewPostDetail.setOnScrollListener(this);
        getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSupportLoaderManager().destroyLoader(0);
		 getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.post_detail, menu);
		return true;
	}

	@Override
	public Loader<List<Comment>> onCreateLoader(int arg0, Bundle arg1) {
		
		mCommentsLoader = new CommentsLoader(this, mListViewPostDetail, mPost.postID);
		return mCommentsLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<Comment>> arg0, List<Comment> data) {
        
		if (data != null && data.size() > 0 && data.get(0).commentID != 0) {			
			data.add(0, new Comment());
		}else{
			data.add(0, new Comment());
		}		
		mPostDetailsAdapter.setData(data);
		mPostDetailsAdapter.notifyDataSetChanged();	
	}

	@Override
	public void onLoaderReset(Loader<List<Comment>> arg0) {
		mPostDetailsAdapter.setData(null);	
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
			if (mCommentsLoader.loadMore() && !mCommentsLoader.loading()) {
				mCommentsLoader.onContentChanged();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

}
