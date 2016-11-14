package com.planet1107.welike.activities;

import com.planet1107.welike.R;
import com.planet1107.welike.activities.RegisterActivity;
import com.planet1107.welike.activities.LoginActivity;
import com.planet1107.welike.connect.Connect;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
		setContentView(R.layout.activity_welcome);
		setTitle("Welcome");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		Boolean is_logout=PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this).getBoolean("is_logout", false);
		if(!is_logout){
		Connect sharedConnect = Connect.getInstance(getBaseContext());
		if (sharedConnect.getCurrentUser() != null) {
			
			if(PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this).getBoolean("IS_Profile_complete", true) ){
				Intent main = new Intent(this, MainActivity.class);
				//finish();
				startActivity(main);
				//finish();
			}else{
				finish();
				Intent main = new Intent(this, EditProfileActivity.class);
				startActivity(main);
				//finish();
			}
		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	
	public void buttonRegisterOnClick(View v) {		
	//	finish();
		Intent register = new Intent(WelcomeActivity.this, RegisterActivity.class);
		startActivity(register);
	}
	
	public void buttonLoginOnClick(View v) {	
	//	finish();
		Intent login = new Intent(WelcomeActivity.this, LoginActivity.class);
		startActivity(login);
	}

}
