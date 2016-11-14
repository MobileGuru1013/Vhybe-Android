package com.planet1107.welike.fragments;

import java.util.List;

import com.planet1107.welike.R;
import com.planet1107.welike.activities.FilterActivity;
import com.planet1107.welike.activities.MapNearByActivity;
import com.planet1107.welike.adapters.GPSTracker;
import com.planet1107.welike.adapters.SearchAdapter;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchFragment extends BaseFragment implements OnQueryTextListener{
	Button btnNearBy,btnCustom;
	SearchView etCustomSearch;
	ListView lstNearBy,lstCustom;
	int searchPerpageNearBy=10;
	int	searchCurrentPageCustom=1;
	int searchPerpageCustom=10;
	int	searchCurrentPageNearBy=1;
	private GPSTracker gps;
	double lati ,longi;
	public static String interests="";
	public static String gender="";
	public static String maxage="";
	public static String minage="";
	public static String maritalstatus="";
	public static String location="";
	public static String occupation="";
	public static Boolean is_filter=false;
	TextView txt_no_Item;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		return rootView;
	}
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		btnNearBy=(Button)getActivity().findViewById(R.id.btnNearBy);
		btnCustom=(Button)getActivity().findViewById(R.id.btnCustom);
		etCustomSearch=(SearchView)getActivity().findViewById(R.id.etCustomSearch);
		txt_no_Item=(TextView)getActivity().findViewById(R.id.txt_no_Item);
		lstNearBy=(ListView)getActivity().findViewById(R.id.lstNearBy);
		lstCustom=(ListView)getActivity().findViewById(R.id.lstCustom);
		btnNearBy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//gender="male";
				btnCustom.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnCustom.setTextColor(Color.WHITE);
				btnNearBy.setBackgroundResource(R.drawable.button_border);
				btnNearBy.setTextColor(getResources().getColor(R.color.app_color));
				lstCustom.setVisibility(View.GONE);
				lstNearBy.setVisibility(View.VISIBLE);
				etCustomSearch.setVisibility(View.GONE);
				onLatLongSubmit();
			}
		});
		btnCustom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//gender="female";
				btnNearBy.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnNearBy.setTextColor(Color.WHITE);
				btnCustom.setBackgroundResource(R.drawable.button_border);
				btnCustom.setTextColor(getResources().getColor(R.color.app_color));
				lstCustom.setVisibility(View.VISIBLE);
				lstNearBy.setVisibility(View.GONE);
				etCustomSearch.setVisibility(View.VISIBLE);

			}
		});

		etCustomSearch.setFocusable(true);
		etCustomSearch.setIconified(false);
		etCustomSearch.requestFocus();
		etCustomSearch.requestFocusFromTouch();
		etCustomSearch.setOnQueryTextListener(this);
		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {
			lati = gps.getLatitude();
			longi = gps.getLongitude();
			System.out.println("lati "+lati);
			System.out.println("longi "+longi);
			onLatLongSubmit();
		}
		 final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		    imm.hideSoftInputFromWindow(etCustomSearch.getWindowToken(), 0);
		    etCustomSearch.clearFocus();
		
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		String mapString = getString(R.string.fa_map_marker);
		String filterString = getString(R.string.fa_filter);
		
		Typeface FONT_AWESOME = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf" );
		//inflater.inflate(R.menu.fragment_timeline_menu, menu);
		final TextView tv1 = new TextView(getActivity());
		tv1.setText(mapString);
		tv1.setTypeface(FONT_AWESOME);
		tv1.setTextColor(Color.WHITE);
		tv1.setTextSize(35);
		tv1.setPadding(0, 0, 30, 0);
	    
	    final TextView tv2 = new TextView(getActivity());
	    tv2.setText(filterString);
	    tv2.setTypeface(FONT_AWESOME);
	    tv2.setTextColor(Color.WHITE);
	    tv2.setTextSize(35);
	    tv2.setPadding(0, 0, 20, 0);
	    
	    LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    LinearLayout linearLayout = new LinearLayout(getActivity());
	    linearLayout.setLayoutParams(lparams);
	    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
	    
	    linearLayout.addView(tv1);
	    linearLayout.addView(tv2);
	    
	    menu.add("newpost").setActionView(linearLayout).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    
	    tv1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuMapOnClick();
			}
		});
	    
	    tv2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuFilterOnClick();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.action_map_near_by) {
			menuMapOnClick();
			return true;
		} else if (itemId == R.id.action_search_filter) {
			menuFilterOnClick();
			return true;
		} else{
			return super.onOptionsItemSelected(item);
		}
	}

	public void menuMapOnClick() {
		Intent editItent = new Intent(getActivity(), MapNearByActivity.class);
		startActivity(editItent);
	}
	public void menuFilterOnClick() {
		Intent editItent = new Intent(getActivity(), FilterActivity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         .class);
		startActivity(editItent);
	}
	@Override
	public boolean onQueryTextChange(String arg0) {

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		System.out.println("onQueryTextSubmit");
		new AsyncTask<Void, Void, List<User>>() {

			private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());

			@Override
			protected void onPreExecute() {

				//mLoadingDialog.setMessage("Searching...");
				//mLoadingDialog.show();
			}

			@Override
			protected List<User> doInBackground(Void... params) {

				Connect sharedConnect = Connect.getInstance(getActivity());
				return sharedConnect.getUsersForString(query,interests, gender, maxage, minage, maritalstatus, 1, 100);
			}

			protected void onPostExecute(List<User> results) {
				if(results.size()==0){
					txt_no_Item.setVisibility(View.VISIBLE);
				}else{
					txt_no_Item.setVisibility(View.GONE);
				}
				//mLoadingDialog.dismiss();
				etCustomSearch.clearFocus();
				SearchAdapter searchAdapter = new SearchAdapter(getActivity());
				searchAdapter.setData(results);
				lstCustom.setAdapter(searchAdapter);
			}

		}.execute();
		return true;
	}
	public void onQueryTextFilterSubmit() {
		System.out.println("onQueryTextFilterSubmit");
		new AsyncTask<Void, Void, List<User>>() {

			private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());

			@Override
			protected void onPreExecute() {

			//	mLoadingDialog.setMessage("Searching...");
			//	mLoadingDialog.show();
			}

			@Override
			protected List<User> doInBackground(Void... params) {

				Connect sharedConnect = Connect.getInstance(getActivity());
				return sharedConnect.getUsersForFilter("",interests, gender, maxage, minage, maritalstatus, 1, 100);
			}

			protected void onPostExecute(List<User> results) {
				if(results.size()==0){
					txt_no_Item.setVisibility(View.VISIBLE);
				}else{
					txt_no_Item.setVisibility(View.GONE);
				}
			//	mLoadingDialog.dismiss();
				etCustomSearch.clearFocus();
				SearchAdapter searchAdapter = new SearchAdapter(getActivity());
				searchAdapter.setData(results);
				lstCustom.setAdapter(searchAdapter);
			}

		}.execute();
		
	}
	public void onLatLongSubmit() {
		System.out.println("onLatLongSubmit");
		new AsyncTask<Void, Void, List<User>>() {

			private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());
		
			@Override
		    protected void onPreExecute() {
		    	
		    //	mLoadingDialog.setMessage("Searching...");
		    //	mLoadingDialog.show();
		    }
			
			@Override
			protected List<User> doInBackground(Void... params) {
				
				Connect sharedConnect = Connect.getInstance(getActivity());
				return sharedConnect.getUsersForLocation(lati+"", longi+"", 1, 100);
			}
			
			protected void onPostExecute(List<User> results) {
				if(results.size()==0){
					txt_no_Item.setVisibility(View.VISIBLE);
				}else{
					txt_no_Item.setVisibility(View.GONE);
				}
			//	mLoadingDialog.dismiss();
			//	etCustomSearch.clearFocus();
		        SearchAdapter searchAdapter = new SearchAdapter(getActivity());
		        searchAdapter.setData(results);
		        lstNearBy.setAdapter(searchAdapter);
		     }
			
		}.execute();
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("isfilter " + is_filter);
		if (is_filter) {		
			btnNearBy.setBackgroundColor(getResources().getColor(R.color.app_color));
			btnNearBy.setTextColor(Color.WHITE);
			btnCustom.setBackgroundResource(R.drawable.button_border);
			btnCustom.setTextColor(getResources().getColor(R.color.app_color));
			lstCustom.setVisibility(View.VISIBLE);
			lstNearBy.setVisibility(View.GONE);
			etCustomSearch.setVisibility(View.VISIBLE);
			onQueryTextFilterSubmit();
			is_filter=false;
		}
	}
}
