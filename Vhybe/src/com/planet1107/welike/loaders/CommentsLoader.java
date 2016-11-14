package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.ListView;

import com.planet1107.welike.connect.Comment;
import com.planet1107.welike.connect.Connect;

public class CommentsLoader extends AsyncTaskLoader<List<Comment>> {

    List<Comment> mComments;
    boolean mLoadMore;
    boolean loading;
    ListView mListViewComments;
    int mPostID;

    public CommentsLoader(Context context, ListView listViewComments, int postID) {
        
    	super(context);
    	mListViewComments = listViewComments;
    	mPostID = postID;
    }

    @Override 
    public List<Comment> loadInBackground() {
       
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());
    	int page = mComments != null ? (mComments.size() / 50 + 1) : 1;
    	ArrayList<Comment> comments = sharedConnect.getComments(mPostID, page, 50);
        return comments;
    }

    @Override
    public void deliverResult(List<Comment> comments) {
        
    	loading = false;
    	if (mComments == null) {
    		mComments = comments;
    	} else if (mComments != comments) {
            mComments.addAll(comments);
    	}
    	mComments = mComments != null ? new ArrayList<Comment>(mComments) : new ArrayList<Comment>();
        mLoadMore = comments != null ? comments.size() == 50 : false;
        if (isStarted()) {
            super.deliverResult(mComments);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mComments != null) {
            deliverResult(mComments);
        }

        if (takeContentChanged() || mComments == null) {
            forceLoad();
        }
    }

    @Override 
    protected void onStopLoading() {

    	cancelLoad();
    }

    @Override 
    public void onCanceled(List<Comment> posts) {
        
    	super.onCanceled(posts);
    }

    @Override 
    protected void onReset() {
    
    	super.onReset();
        onStopLoading();
        mComments = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}