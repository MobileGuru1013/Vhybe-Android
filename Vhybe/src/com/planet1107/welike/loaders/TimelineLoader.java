package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.ListView;

import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;

public class TimelineLoader extends AsyncTaskLoader<List<Post>> {

    List<Post> mPosts;
    boolean mLoadMore;
    boolean loading;
    ListView mListViewTimeline;

    public TimelineLoader(Context context, ListView listViewTimeline) {
        
    	super(context);
    	mListViewTimeline = listViewTimeline;
    }

    @Override 
    public List<Post> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	int page = mPosts != null ? (mPosts.size() / 7 + 1) : 1;
    	ArrayList<Post> posts = sharedConnect.getTimeline(sharedConnect.getCurrentUser().userID, page, 7);
        return posts;
    }

    @Override
    public void deliverResult(List<Post> posts) {
        
    	loading = false;
    	if (mPosts == null) {
    		mPosts = posts;
    	} else if (mPosts != posts) {
            mPosts.addAll(posts);
    	}
    	mPosts = mPosts != null ? new ArrayList<Post>(mPosts) : new ArrayList<Post>();
        mLoadMore = posts != null ? posts.size() == 7 : false;
        if (isStarted()) {
            super.deliverResult(mPosts);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mPosts != null) {
            deliverResult(mPosts);
        }

        if (takeContentChanged() || mPosts == null) {
            forceLoad();
        }
    }

    @Override 
    protected void onStopLoading() {

    	cancelLoad();
    }

    @Override 
    public void onCanceled(List<Post> posts) {
        
    	super.onCanceled(posts);
    }

    @Override 
    protected void onReset() {
    
    	super.onReset();
        onStopLoading();
        mPosts = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}