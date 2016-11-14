package com.planet1107.welike.fragments;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.LocationClient;
import com.planet1107.welike.R;
import com.planet1107.welike.adapters.ChatAdapter;
import com.planet1107.welike.adapters.FriendsAdapter;
import com.planet1107.welike.adapters.RequestAdapter;
import com.planet1107.welike.connect.Chat;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.loaders.ChatLoader;
import com.planet1107.welike.loaders.FriendsLoader;
import com.planet1107.welike.loaders.RequestLoader;
import com.planet1107.welike.nodechat.DatabaseAdapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendsFragment extends BaseFragment  {

	LocationClient locationClient;
	Location lastLocation;
	Button btnChat,btnFriends,btnRequest;
	ListView lstChat,lstFriends,lstRequest;
	ChatAdapter mChatAdapter;
	static FriendsAdapter mFriendAdapter;
	static RequestAdapter mRequestAdapter;
	ProgressBar mProgressBarLoadingChat,mProgressBarLoadingFriend,mProgressBarLoadingRequest;
	ChatLoader mChatLoader;
	FriendsLoader mFriendsLoader;
	RequestLoader mRequestLoader;
	TextView mTextViewNoItemsChat,mTextViewNoItemsFriend,mTextViewNoItemsRequest;
	int  mUserID;
	RelativeLayout RL_Chat,RL_Friends,RL_Request;
	int Selectedtab=0;
	public static List<User> requestlist=new ArrayList<User>();
	public static List<User> friendslist=new ArrayList<User>();
	public static Boolean isChatRefresh=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		setHasOptionsMenu(true);
	
		mUserID=new Connect(getActivity()).getCurrentUser().userID;
		RL_Chat=(RelativeLayout)getActivity().findViewById(R.id.RL_Chat);
		RL_Friends=(RelativeLayout)getActivity().findViewById(R.id.RL_Friend);
		RL_Request=(RelativeLayout)getActivity().findViewById(R.id.RL_Request);
		btnChat=(Button)getActivity().findViewById(R.id.btnChat);
		btnFriends=(Button)getActivity().findViewById(R.id.btnFriends);
		btnRequest=(Button)getActivity().findViewById(R.id.btnRequest);
		lstChat=(ListView)getActivity().findViewById(R.id.lstChat);
		lstFriends=(ListView)getActivity().findViewById(R.id.lstFriends);
		lstRequest=(ListView)getActivity().findViewById(R.id.lstRequest);
		mTextViewNoItemsChat = (TextView) getActivity().findViewById(R.id.textViewNoItemsChat);
		mProgressBarLoadingChat = (ProgressBar) getActivity().findViewById(R.id.progressBarLoadingChat);
		mTextViewNoItemsFriend = (TextView) getActivity().findViewById(R.id.textViewNoItemsFriend);
		mProgressBarLoadingFriend = (ProgressBar) getActivity().findViewById(R.id.progressBarLoadingFriend);
		mTextViewNoItemsRequest = (TextView) getActivity().findViewById(R.id.textViewNoItemsRequest);
		mProgressBarLoadingRequest = (ProgressBar) getActivity().findViewById(R.id.progressBarLoadingRequest);
		mRequestAdapter = new RequestAdapter(getActivity());
		mChatAdapter=new ChatAdapter(getActivity());
		mFriendAdapter=new FriendsAdapter(getActivity());
		lstFriends.setEmptyView(mProgressBarLoadingFriend);
		lstFriends.setAdapter(mFriendAdapter);		
		lstFriends.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		lstChat.setEmptyView(mProgressBarLoadingChat);
		lstChat.setAdapter(mChatAdapter);
		lstChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		lstRequest.setEmptyView(mProgressBarLoadingRequest);
		lstRequest.setAdapter(mRequestAdapter);	
		lstRequest.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		getLoaderManager().initLoader(0, null,mChatCallback);
		btnChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//gender="male";
				btnFriends.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnFriends.setTextColor(Color.WHITE);
				btnChat.setBackgroundResource(R.drawable.button_border);
				btnChat.setTextColor(getResources().getColor(R.color.app_color));
				btnRequest.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnRequest.setTextColor(Color.WHITE);
				RL_Friends.setVisibility(View.GONE);
				RL_Chat.setVisibility(View.VISIBLE);				
				RL_Request.setVisibility(View.GONE);
				Selectedtab=1;
				getLoaderManager().destroyLoader(0);
				getLoaderManager().initLoader(0, null,mChatCallback);
			}
		});
		btnFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//gender="female";
				btnChat.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnChat.setTextColor(Color.WHITE);
				btnFriends.setBackgroundResource(R.drawable.button_border);
				btnFriends.setTextColor(getResources().getColor(R.color.app_color));
				btnRequest.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnRequest.setTextColor(Color.WHITE);
				RL_Friends.setVisibility(View.VISIBLE);
				RL_Chat.setVisibility(View.GONE);
				RL_Request.setVisibility(View.GONE);	
				Selectedtab=2;
				getLoaderManager().destroyLoader(0);
				getLoaderManager().initLoader(0, null,mFriendsCallback);

			}
		});
		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//gender="female";
				btnChat.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnChat.setTextColor(Color.WHITE);
				btnFriends.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnFriends.setTextColor(Color.WHITE);
				btnRequest.setBackgroundResource(R.drawable.button_border);
				btnRequest.setTextColor(getResources().getColor(R.color.app_color));
				RL_Request.setVisibility(View.VISIBLE);
				RL_Friends.setVisibility(View.GONE);
				RL_Chat.setVisibility(View.GONE);	
				Selectedtab=3;
				getLoaderManager().destroyLoader(0);
				getLoaderManager().initLoader(0, null,mRequestCallback);

			}
		});
		lstRequest.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
					if (mRequestLoader.loadMore() && !mRequestLoader.loading()) {
						mRequestLoader.onContentChanged();
					}
				}

			}
		});
		lstFriends.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (firstVisibleItem + visibleItemCount >= totalItemCount && visibleItemCount != 0) {
					if (mFriendsLoader.loadMore() && !mFriendsLoader.loading()) {
						mFriendsLoader.onContentChanged();
					}
				}
			}
		});
	}

	private LoaderManager.LoaderCallbacks<List<User>> mFriendsCallback = new  LoaderManager.LoaderCallbacks<List<User>>() {

		@Override
		public Loader<List<User>> onCreateLoader(int id, Bundle args) {

			mFriendsLoader = new FriendsLoader(getActivity(), mUserID);
			return mFriendsLoader;

		}

		@Override
		public void onLoadFinished(Loader<List<User>> arg0, List<User> data) {
			friendslist.clear();
			friendslist=data;
			mFriendAdapter.setData(friendslist);
			mFriendAdapter.notifyDataSetChanged();
			mTextViewNoItemsFriend.setVisibility(View.VISIBLE);
			mProgressBarLoadingFriend.setVisibility(View.INVISIBLE);
			lstFriends.setEmptyView(mTextViewNoItemsFriend);
		}
		@Override
		public void onLoaderReset(Loader<List<User>> arg0) {

			/*mPopularAdapter.setData(null);*/

			mFriendAdapter.setData(null);

		}
	};
	private LoaderManager.LoaderCallbacks<List<Chat>> mChatCallback = new  LoaderManager.LoaderCallbacks<List<Chat>>() {


		@Override
		public Loader<List<Chat>> onCreateLoader(int id, Bundle args) {

			mChatLoader = new ChatLoader(getActivity());
			return mChatLoader;

		}

		@Override
		public void onLoadFinished(Loader<List<Chat>> arg0, List<Chat> data) {

			mChatAdapter.setData(data);
			mChatAdapter.notifyDataSetChanged();
			mTextViewNoItemsChat.setVisibility(View.VISIBLE);
			mProgressBarLoadingChat.setVisibility(View.INVISIBLE);
			lstChat.setEmptyView(mTextViewNoItemsChat);
		}
		@Override
		public void onLoaderReset(Loader<List<Chat>> arg0) {

			mChatAdapter.setData(null);

			mChatAdapter.setData(null);

		}
	};


	private LoaderManager.LoaderCallbacks<List<User>> mRequestCallback = new  LoaderManager.LoaderCallbacks<List<User>>() {


		@Override
		public Loader<List<User>> onCreateLoader(int id, Bundle args) {
			System.out.println("onCreateLoader");
			mRequestLoader = new RequestLoader(getActivity(), mUserID);
			return mRequestLoader;

		}

		@Override
		public void onLoadFinished(Loader<List<User>> arg0, List<User> data) {
			System.out.println("onLoadFinished");
			requestlist.clear();
			requestlist=data;
			mRequestAdapter.setData(requestlist);
			mRequestAdapter.notifyDataSetChanged();
			mTextViewNoItemsRequest.setVisibility(View.VISIBLE);
			mProgressBarLoadingRequest.setVisibility(View.INVISIBLE);
			lstRequest.setEmptyView(mTextViewNoItemsRequest);

		}
		@Override
		public void onLoaderReset(Loader<List<User>> arg0) {		
			System.out.println("onLoaderReset");
			mRequestAdapter.setData(null);

		}
	};
	public static void updateRequest(User user){
		requestlist.remove(user);
		mRequestAdapter.setData(requestlist);
		mRequestAdapter.notifyDataSetChanged();
	}
	public static void updateFriend(User user){
		friendslist.remove(user);
		mFriendAdapter.setData(friendslist);
		mFriendAdapter.notifyDataSetChanged();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					DatabaseAdapter db=new DatabaseAdapter(getActivity());
					Connect mConnect = new Connect(getActivity());
					db.open();
					String lastSynchDate = db.getlastupdateDate();
					mConnect.getchatDetail(lastSynchDate);
					db.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} 
		}).start();

		if(isChatRefresh){
		getLoaderManager().destroyLoader(0);
		getLoaderManager().initLoader(0, null,mChatCallback);
		isChatRefresh=false;
		}
	}
}
