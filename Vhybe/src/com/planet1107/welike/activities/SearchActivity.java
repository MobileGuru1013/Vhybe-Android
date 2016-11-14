package com.planet1107.welike.activities;

import java.util.List;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.SearchAdapter;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends ListActivity implements OnQueryTextListener, OnItemClickListener {

	ListView mListViewSearchUsers;
	SearchView mSearchViewUsers;
	static String interests="";
	static String gender="";
	static String maxage="";
	static String minage="";
	static String maritalstatus="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setTitle("Search users");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mSearchViewUsers = (SearchView)findViewById(R.id.searchViewSearchUsers);
		mSearchViewUsers.setFocusable(true);
		mSearchViewUsers.setIconified(false);
		mSearchViewUsers.requestFocus();
		mSearchViewUsers.requestFocusFromTouch();
		mSearchViewUsers.setOnQueryTextListener(this);

		mListViewSearchUsers = (ListView)findViewById(android.R.id.list);
		mListViewSearchUsers.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		
		new AsyncTask<Void, Void, List<User>>() {

			private ProgressDialog mLoadingDialog = new ProgressDialog(SearchActivity.this);

			@Override
		    protected void onPreExecute() {
		    	
		    	mLoadingDialog.setMessage("Searching...");
		    	mLoadingDialog.show();
		    }
			
			@Override
			protected List<User> doInBackground(Void... params) {
				
				Connect sharedConnect = Connect.getInstance(SearchActivity.this);
				return sharedConnect.getUsersForString(query,interests, gender, maxage, minage, maritalstatus, 1, 100);
			}
			
			protected void onPostExecute(List<User> results) {
		         
				mLoadingDialog.dismiss();
				mSearchViewUsers.clearFocus();
		        SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this);
		        searchAdapter.setData(results);
		        mListViewSearchUsers.setAdapter(searchAdapter);
		     }
			
		}.execute();
		return true;
	}

	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

		User user = (User) mListViewSearchUsers.getAdapter().getItem(position);
		Intent intentUser = new Intent(this, ProfileActivity.class);
		intentUser.putExtra("userID", user.userID);
		startActivity(intentUser);
	}
}
