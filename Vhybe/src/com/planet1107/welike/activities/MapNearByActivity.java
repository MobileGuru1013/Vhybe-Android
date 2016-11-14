package com.planet1107.welike.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.location.Location;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.view.Menu;
import android.view.MenuItem;

public class MapNearByActivity extends Activity implements OnConnectionFailedListener, LocationListener {

	LocationClient locationClient;
	Location lastLocation;
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_near_by);

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		locationClient = new LocationClient(MapNearByActivity.this, new GooglePlayServicesClient.ConnectionCallbacks() {
			
			@Override
			public void onDisconnected() {
				
			}
			
			@Override
			public void onConnected(Bundle connectionHint) {
				LocationRequest locationRequest = LocationRequest.create();
				locationClient.requestLocationUpdates(locationRequest, MapNearByActivity.this);
			}
		}, this);
		locationClient.connect();
		mMap.setMyLocationEnabled(true);
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}
	@Override
	public void onLocationChanged(Location location) {

	    if (lastLocation == null || location.distanceTo(lastLocation) > 1000) {
	        getLoaderManager().initLoader(0, new Bundle(), a);
	        lastLocation = location;
	    }
	}
	
	LoaderCallbacks<List<User>>  a=new LoaderCallbacks<List<User>>() {

		

		@Override
		public void onLoadFinished(android.content.Loader<List<User>> arg0,
				List<User> users) {
			// TODO Auto-generated method stub
			for (User user : users) {
				MarkerOptions options = new MarkerOptions();
				options.position(new LatLng(user.latitude, user.longitude));
				mMap.addMarker(options);
			}
			
		}

		@Override
		public void onLoaderReset(android.content.Loader<List<User>> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public android.content.Loader<List<User>> onCreateLoader(int id,
				Bundle args) {
			// TODO Auto-generated method stub
			return null;
		}

		
	};
	public class NearbyLoader extends AsyncTaskLoader<List<User>> {

	    List<User> users;

	    public NearbyLoader(Context context) {
	        
	    	super(context);
	    }

	    @Override 
	    public List<User> loadInBackground() {
	    	Connect sharedConnect = Connect.getInstance(MapNearByActivity.this);
	    	Location lastLocation = MapNearByActivity.this.lastLocation;
	       ArrayList<User> users = sharedConnect.getUsersAround(lastLocation.getLatitude(), lastLocation.getLongitude(), 10000, 1, 20);
	       return users;
	    }

	    @Override
	    public void deliverResult(List<User> users) {
	        
	    	if (isReset()) {
	            if (users != null) {
	                onReleaseResources(users);
	            }
	        }
	        List<User> oldUsers = users;
	        this.users = users;

	        if (isStarted()) {
	            super.deliverResult(users);
	        }

	        if (oldUsers != null) {
	            onReleaseResources(oldUsers);
	        }
	        
	    }

	    @Override 
	    protected void onStartLoading() {
	        
	    	if (users != null) {
	            deliverResult(users);
	        }

	        if (takeContentChanged() || this.users == null) {
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
	        onReleaseResources(users);
	    }

	    @Override 
	    protected void onReset() {
	    
	    	super.onReset();
	        onStopLoading();
	        if (this.users != null) {
	            onReleaseResources(this.users);
	            this.users = null;
	        }
	    }

	    protected void onReleaseResources(List<User> users) {

	    }
	}

}
