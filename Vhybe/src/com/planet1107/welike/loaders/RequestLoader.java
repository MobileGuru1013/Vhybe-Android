package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

public class RequestLoader extends AsyncTaskLoader<List<User>> {

    List<User> mFollowers;
    boolean mLoadMore;
    boolean loading;
    int mUserID;

    public RequestLoader(Context context, int userID) {
        
    	super(context);
    	mUserID = userID;
    }

    @Override 
    public List<User> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	int page = mFollowers != null ? (mFollowers.size() / 50 + 1) : 1;
    	ArrayList<User> likes = sharedConnect.getFriendInvitations(mUserID, page, 50);
        return likes;
    }

    @Override
    public void deliverResult(List<User> users) {
        
    	loading = false;
    	if (mFollowers == null) {
    		mFollowers = users;
    	} else if (mFollowers != users) {
    		mFollowers.addAll(users);
    	}
    	mFollowers = mFollowers != null ? new ArrayList<User>(mFollowers) : new ArrayList<User>();
        mLoadMore = users != null ? users.size() == 50 : false;
        if (isStarted()) {
            super.deliverResult(mFollowers);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mFollowers != null) {
            deliverResult(mFollowers);
        }

        if (takeContentChanged() || mFollowers == null) {
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
        mFollowers = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}