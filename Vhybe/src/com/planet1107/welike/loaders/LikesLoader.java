package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.ListView;

import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Like;

public class LikesLoader extends AsyncTaskLoader<List<Like>> {

    List<Like> mLikes;
    boolean mLoadMore;
    boolean loading;
    ListView mListViewLikes;
    int mPostID;

    public LikesLoader(Context context, ListView listViewLikes, int postID) {
        
    	super(context);
    	mListViewLikes = listViewLikes;
    	mPostID = postID;
    }

    @Override 
    public List<Like> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	int page = mLikes != null ? (mLikes.size() / 7 + 1) : 1;
    	ArrayList<Like> likes = sharedConnect.getLikes(mPostID, page, 7);
        return likes;
    }

    @Override
    public void deliverResult(List<Like> likes) {
        
    	loading = false;
    	if (mLikes == null) {
    		mLikes = likes;
    	} else if (mLikes != likes) {
            mLikes.addAll(likes);
    	}
    	mLikes = mLikes != null ? new ArrayList<Like>(mLikes) : new ArrayList<Like>();
        mLoadMore = likes != null ? likes.size() == 7 : false;
        if (isStarted()) {
            super.deliverResult(mLikes);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mLikes != null) {
            deliverResult(mLikes);
        }

        if (takeContentChanged() || mLikes == null) {
            forceLoad();
        }
    }

    @Override 
    protected void onStopLoading() {

    	cancelLoad();
    }

    @Override 
    public void onCanceled(List<Like> posts) {
        
    	super.onCanceled(posts);
    }

    @Override 
    protected void onReset() {
    
    	super.onReset();
        onStopLoading();
        mLikes = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}