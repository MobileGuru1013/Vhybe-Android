package com.planet1107.welike.activities;

import com.planet1107.welike.R;
import com.splunk.mint.Mint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Window;

public class Splash extends Activity {
	/** Called when the activity is first created. */
	private Thread spalsh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	  //  getActionBar().setCustomView(R.layout.my_custom_title);
	  //  getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_splash);
		Mint.initAndStartSession(Splash.this, "de004907");
		spalsh=new Thread(){
			@SuppressWarnings("static-access")
			public void run(){
				try{
					spalsh.sleep(1000);
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				messagehandler.sendEmptyMessage(0);
			}

		};
		spalsh.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler messagehandler=new Handler(){

		public void handleMessage(Message msg){
			super.handleMessage(msg);
			Intent intent=new Intent(Splash.this,WelcomeActivity.class);
			startActivity(intent);
			finish();

		}
	};
	@Override
	public void onBackPressed() {
		finish();
	}

}
