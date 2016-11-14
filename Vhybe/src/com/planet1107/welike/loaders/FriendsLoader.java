package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

public class FriendsLoader extends AsyncTaskLoader<List<User>> {

	List<User> mFollowing;
    boolean mLoadMore;
    boolean loading;
    int mUserID;

    public FriendsLoader(Context context, int userID) {
        
    	super(context);
    	mUserID = userID;
    }

    @Override 
    public List<User> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	int page = mFollowing != null ? (mFollowing.size() / 50 + 1) : 1;
    	ArrayList<User> likes = sharedConnect.getFriend(mUserID, page, 50);
        return likes;
    }

    @Override
    public void deliverResult(List<User> users) {
        
    	loading = false;
    	if (mFollowing == null) {
    		mFollowing = users;
    	} else if (mFollowing != users) {
    		mFollowing.addAll(users);
    	}
    	mFollowing = mFollowing != null ? new ArrayList<User>(mFollowing) : new ArrayList<User>();
        mLoadMore = users != null ? users.size() == 50 : false;
        if (isStarted()) {
            super.deliverResult(mFollowing);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mFollowing != null) {
            deliverResult(mFollowing);
        }

        if (takeContentChanged() || mFollowing == null) {
            forceLoad();
        }
    }

    @Override 
    protected void onStopLoading() {

    	cancelLoad();
    }

    @Override 
    public void onCanceled(List<User> users) {
        
    	super.onCanceled(users);
    }

    @Override 
    protected void onReset() {
    
    	super.onReset();
        onStopLoading();
        mFollowing = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}