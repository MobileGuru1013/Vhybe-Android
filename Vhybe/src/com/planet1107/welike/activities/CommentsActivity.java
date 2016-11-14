package com.planet1107.welike.activities;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.CommentsAdapter;
import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.loaders.CommentsLoader;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CommentsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Comment>>, OnRefreshListener, OnScrollListener {

	CommentsAdapter mCommentsAdapter;
    CommentsLoader mCommentsLoader;
    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListViewComments;
    ProgressBar mProgressBarLoading;
    TextView mTextViewNoItems;
    int mPostID;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		setTitle("Comments");

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

        mCommentsAdapter = new CommentsAdapter(this);

        mListViewComments = (ListView) findViewById(R.id.listViewComments);
        mTextViewNoItems.setVisibility(View.INVISIBLE);
        mListViewComments.setEmptyView(mProgressBarLoading);
        mListViewComments.setAdapter(mCommentsAdapter);
        mListViewComments.setOnScrollListener(this);
        
        getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}

	@Override
    public Loader<List<Comment>> onCreateLoader(int id, Bundle args) {
        
		mCommentsLoader = new CommentsLoader(this, mListViewComments, mPostID);
		return mCommentsLoader;
    }

	@Override
	public void onLoadFinished(Loader<List<Comment>> arg0, List<Comment> data) {
        
		mCommentsAdapter.setData(data);
		mCommentsAdapter.notifyDataSetChanged();
		mPullToRefreshLayout.setRefreshComplete();
		mTextViewNoItems.setVisibility(View.VISIBLE);
		mProgressBarLoading.setVisibility(View.INVISIBLE);
		mListViewComments.setEmptyView(mTextViewNoItems);
	}

	@Override
	public void onLoaderReset(Loader<List<Comment>> arg0) {

        mCommentsAdapter.setData(null);
	}

	@Override
	public void onRefreshStarted(View view) {
		
		mTextViewNoItems.setVisibility(View.INVISIBLE);
		mProgressBarLoading.setVisibility(View.VISIBLE);
		mListViewComments.setEmptyView(mProgressBarLoading);
		getSupportLoaderManager().destroyLoader(0);
		getSupportLoaderManager().initLoader(0, null, this);
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
	
	public void buttonSendOnClick(View v) {
		
		new AsyncTask<Void, Void, Comment>() {
			
		    private ProgressDialog mLoadingDialog = new ProgressDialog(CommentsActivity.this);
			
		    @Override
		    protected void onPreExecute() {
		    	
		    	mLoadingDialog.setMessage("Sending comment...");
		    	mLoadingDialog.show();
		    }
		    
		    @Override
			protected Comment doInBackground(Void... params) {
				
				EditText editTextComment = (EditText)findViewById(R.id.editTextCommentText);
				Connect sharedConnect = Connect.getInstance(CommentsActivity.this);
				return sharedConnect.sendComment(mPostID, editTextComment.getText().toString());
			}
			
			protected void onPostExecute(Comment result) {
				
				mLoadingDialog.dismiss();
				EditText editTextComment = (EditText)findViewById(R.id.editTextCommentText);
				editTextComment.clearFocus();
				editTextComment.setText("");
				if (result != null && result.commentID > 0) {
					mCommentsAdapter.insert(result, 0);
					mCommentsAdapter.notifyDataSetChanged();
				}
		     }
		}.execute();
	}
}
