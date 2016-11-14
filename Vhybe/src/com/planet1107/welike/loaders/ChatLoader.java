package com.planet1107.welike.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.planet1107.welike.connect.Chat;
import com.planet1107.welike.connect.Connect;

public class ChatLoader extends AsyncTaskLoader<List<Chat>> {

	List<Chat> mchat;
    boolean mLoadMore;
    boolean loading;


    public ChatLoader(Context context ) {        
    	super(context);
    }

    @Override 
    public List<Chat> loadInBackground() {
    	loading = true;
    	Connect sharedConnect = Connect.getInstance(getContext());    	
    	ArrayList<Chat> chats = sharedConnect.getchatData();
        return chats;
    }

    @Override
    public void deliverResult(List<Chat> Chats) {      
    	loading = false;
    	if (mchat == null) {
    		mchat = Chats;
    	} else if (mchat != Chats) {
    		mchat.addAll(Chats);
    	}
    	mchat = mchat != null ? new ArrayList<Chat>(mchat) : new ArrayList<Chat>();
        mLoadMore = Chats != null ? Chats.size() == 70 : false;
        if (isStarted()) {
            super.deliverResult(mchat);
        }
    }

    @Override 
    protected void onStartLoading() {
        
    	if (mchat != null) {
            deliverResult(mchat);
        }

        if (takeContentChanged() || mchat == null) {
            forceLoad();
        }
    }

    @Override 
    protected void onStopLoading() {

    	cancelLoad();
    }

    @Override 
    public void onCanceled(List<Chat> Chats) {
        
    	super.onCanceled(Chats);
    }

    @Override 
    protected void onReset() {
    
    	super.onReset();
        onStopLoading();
        mchat = null;
    }
    
    public boolean loadMore() {
    	
    	return mLoadMore;
    }
    
    public boolean loading() {
    	
    	return loading;
    }
}