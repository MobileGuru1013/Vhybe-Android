package com.planet1107.welike.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity   {


	String password;	
	String email;
	EditText mEditTextPassword;
	EditText mEditTextEmail;	
	   public static String regid;
	    public static GoogleCloudMessaging gcm;
	    public static Context context;
	UserRegisterTask mUserRegisterTask;
	   public static final String PROPERTY_REG_ID = "registration_id";
	    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);	

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		  if (checkPlayServices()) {
	            gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(context);

	            if (regid.isEmpty()) {
	            	//registerInBackground();
	            }
	        } else {
	            Log.i("", "No valid Google Play Services APK found.");
	        }
		mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
		mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	public void buttonLogInHereOnClick(View v){
		finish();
		startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
	}


	public void buttonRegisterOnClick(View v) {

		attemptRregister();
	}



	public void attemptRregister() {

		if (mUserRegisterTask == null) {
			mEditTextPassword.setError(null);
			mEditTextEmail.setError(null);
			password = mEditTextPassword.getText().toString().trim();
			email = mEditTextEmail.getText().toString().trim();
			boolean cancel = true;
			View focusView = null;
			if (TextUtils.isEmpty(email)) {
				mEditTextEmail.setError(getString(R.string.error_field_required));
				focusView = mEditTextEmail;
			}  else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
				mEditTextEmail.setError("Email is not in valid format");
				focusView = mEditTextEmail;
			} else	if (TextUtils.isEmpty(password)) {
				mEditTextPassword.setError(getString(R.string.error_field_required));
				focusView = mEditTextPassword;
			} else if (password.length() < 6) {
				mEditTextPassword.setError(getString(R.string.error_invalid_password));
				focusView = mEditTextPassword;
			} else  {
				cancel = false;
				mUserRegisterTask = new UserRegisterTask();
				mUserRegisterTask.execute();
			}

			if (cancel) {
				focusView.requestFocus();
			}
		}
	}

	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mLoadingDialog = new ProgressDialog(RegisterActivity.this);

		@Override
		protected void onPreExecute() {
			mLoadingDialog.setMessage("Registering...");
			mLoadingDialog.show();
		}
		@Override
		protected Boolean doInBackground(Void... params) {

			User user = Connect.getInstance(getBaseContext()).registerUser(password, email,regid);
			if (user != null && user.userEmail != null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			mUserRegisterTask = null;
			mLoadingDialog.dismiss();

			if (success) {
				PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit().putBoolean("IS_Profile_complete", false).commit(); 
				PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit().putBoolean("is_logout", false).commit();
				Intent main = new Intent(RegisterActivity.this, EditProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(main);
					
			}else{
				Toast.makeText(RegisterActivity.this, "User already registered.Please try again", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {

			mUserRegisterTask = null;
			mLoadingDialog.dismiss();

		}
	}
	
	// GCM configuration

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.

        // int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
        // Integer.MIN_VALUE);

        // int currentVersion = getAppVersion(context);
        // if (registeredVersion != currentVersion) {
        // Log.i(TAG, "App version changed.");
        // return "";
        // }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

}
