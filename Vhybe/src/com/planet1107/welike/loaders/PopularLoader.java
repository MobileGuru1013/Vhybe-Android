package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.Post;

public class PopularLoader extends AsyncTaskLoader<List<Post>> {

	public enum PopularOption {
		
		PopularOptionPopular,
		PopularOptionRecent
	}
	
    List<Post> mPosts;
    boolean loading;
    PopularOption mOption;

    public PopularLoader(Context context, PopularOption option) {
        
    	super(context);
    	mOption = option;
    }

    @Override 
    public List<Post> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	ArrayList<Post> posts = null;
    	if (mOption == PopularOption.PopularOptionPopular) {
    		posts = sharedConnect.getPopular(1, 20);
    	} else {
    		posts = sharedConnect.getRecent(1, 20);
    	}
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
    	mPosts = new ArrayList<Post>(mPosts);
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
    
    public boolean loading() {
    	
    	return loading;
    }
}