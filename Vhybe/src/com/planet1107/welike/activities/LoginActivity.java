package com.planet1107.welike.activities;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.gcm.CommonUtilities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	private UserLoginTask userLoginTask = null;

	private String username;
	private String password;

	private EditText editTextUsername;
	private EditText editTextPassword;
	// GCM
    public static String regid;
    public static GoogleCloudMessaging gcm;
    public static Context context;
    // private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "registration_id";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	TextView txtForgotPassword;
	Button buttonSignUpHere;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("Login");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// Check for Google Play services
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i("", "No valid Google Play Services APK found.");
        }
		txtForgotPassword=(TextView)findViewById(R.id.txtForgotPassword);
		txtForgotPassword.setText(Html.fromHtml("<u>Forgot Password?</u>"));
		txtForgotPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
			}
		});
		
		username = getIntent().getStringExtra(EXTRA_EMAIL);
		editTextUsername = (EditText) findViewById(R.id.editTextEmail);
		editTextUsername.setText(username);

		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});


		findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		buttonSignUpHere=(Button)findViewById(R.id.buttonSignUpHere);
		buttonSignUpHere.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin() {
		
		if (userLoginTask == null) {
			editTextUsername.setError(null);
			editTextPassword.setError(null);
			username = editTextUsername.getText().toString().trim();
			password = editTextPassword.getText().toString().trim();

			boolean cancel = false;
			View focusView = null;

			if   (TextUtils.isEmpty(username)) {
				editTextUsername.setError(getString(R.string.error_field_required));
				focusView = editTextUsername;
				cancel = true;
			}else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
				editTextUsername.setError("Email is not in valid format");
				focusView = editTextUsername;
				cancel = true;
			} else if(TextUtils.isEmpty(password)) {
				editTextPassword.setError(getString(R.string.error_field_required));
				focusView = editTextPassword;
				cancel = true;
			} else if (password.length() < 6) {
				editTextPassword.setError(getString(R.string.error_invalid_password));
				focusView = editTextPassword;
				cancel = true;
			}

			if (cancel) {
				focusView.requestFocus();
			} else {
				userLoginTask = new UserLoginTask();
				userLoginTask.execute();
			}
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		private ProgressDialog mLoadingDialog = new ProgressDialog(LoginActivity.this);

		@Override
	    protected void onPreExecute() {
	    	
	    	mLoadingDialog.setMessage("Signing in...");
	    	mLoadingDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {			
			User user = Connect.getInstance(getBaseContext()).loginUser(username, password,regid);
			if (user != null && user.userEmail != null) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			userLoginTask = null;
			mLoadingDialog.dismiss();
			if (success) {
				PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("IS_Profile_complete", true).commit();
				PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("is_logout", false).commit();
				Intent main = new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(main);
			} else {
				editTextPassword.setError(getString(R.string.error_incorrect_password));
				editTextPassword.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			
			userLoginTask = null;
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

    // /**
    // * @return Application's version code from the {@code PackageManager}.
    // */
    // private static int getAppVersion(Context context) {
    // try {
    // PackageInfo packageInfo = context.getPackageManager()
    // .getPackageInfo(context.getPackageName(), 0);

    // return packageInfo.versionCode;
    // } catch (NameNotFoundException e) {
    // // should never happen
    // throw new RuntimeException("Could not get package name: " + e);
    // }
    // }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(CommonUtilities.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    // storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	System.out.println(msg);

            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
      
    }
    
 
    
   
}
