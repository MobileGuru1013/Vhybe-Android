package com.planet1107.welike.activities;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends Activity {

	Button btnSend;
	String emailID;
	EditText editTextEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		btnSend=(Button)findViewById(R.id.btnSend);

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callforgotpassword();
			}
		});
		editTextEmail=(EditText)findViewById(R.id.editTextEmail);
	}

	public void callforgotpassword() {


		editTextEmail.setError(null);

		emailID = editTextEmail.getText().toString();	

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(emailID)) {
			editTextEmail.setError(getString(R.string.error_field_required));
			focusView = editTextEmail;
			cancel = true;
		} 

		if (cancel) {
			focusView.requestFocus();
		} else {

			new UserLoginTask().execute();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forgot_password, menu);
		return true;
	}
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mLoadingDialog = new ProgressDialog(ForgotPasswordActivity.this);

		@Override
		protected void onPreExecute() {

			mLoadingDialog.setMessage("Loading...");
			mLoadingDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {			
			return Connect.getInstance(getBaseContext()).forgotPassword(emailID);

		}

		@Override
		protected void onPostExecute(final Boolean success) {			
			mLoadingDialog.dismiss();
			if (success) {
				Intent main = new Intent(ForgotPasswordActivity.this, MainActivity.class);
				startActivity(main);
			}
		}

		@Override
		protected void onCancelled() {
			mLoadingDialog.dismiss();
		}
	}
}
