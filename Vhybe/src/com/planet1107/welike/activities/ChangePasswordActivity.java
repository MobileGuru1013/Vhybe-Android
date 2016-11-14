package com.planet1107.welike.activities;

import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    String email = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Bundle b = getIntent().getExtras();
        email = b.getString("email");
        
        edtOldPassword = (EditText) findViewById(R.id.edt_old_password);
        edtNewPassword = (EditText) findViewById(R.id.edt_new_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm_password);

        Button bttn_change_password = (Button) findViewById(R.id.bttn_change_password);
        bttn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String OldPasswordTrim = edtOldPassword.getText().toString().trim();
                String NewPassword = edtNewPassword.getText().toString().trim();
                String ConfirmPassword = edtConfirmPassword.getText().toString().trim();



                if (OldPasswordTrim.equals("")) {
                    edtOldPassword.setError("Empty Field");
                }
                if (NewPassword.equals("")) {
                    edtNewPassword.setError("Empty Field");
                }
                if (ConfirmPassword.equals("")) {
                    edtConfirmPassword.setError("Empty Field");
                } else if (!NewPassword.equals(null) && !ConfirmPassword.equals(null)) {

                    if (NewPassword.length() >= 6 && ConfirmPassword.length() >= 6) {

                        if (NewPassword.equals(ConfirmPassword)) {

                        	new ChangePassword_Async().execute(email, OldPasswordTrim, NewPassword);

                        } else {

                            Toast.makeText(ChangePasswordActivity.this, "password not matched", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        Toast.makeText(ChangePasswordActivity.this, "password should be atleast 6 char", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(ChangePasswordActivity.this, "Empty Field", Toast.LENGTH_LONG).show();

                }


            }
        });


        GPS_Setting_Checker();

    }

// Check location and ask user for enabling gps location setting
    public void GPS_Setting_Checker(){
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

    {
        // Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage(R.string.enable_location_messaqe);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
  }
    
    public class ChangePassword_Async extends AsyncTask<String, Void, Void> {

		private ProgressDialog mLoadingDialog = new ProgressDialog(ChangePasswordActivity.this);

		@Override
		protected void onPreExecute() {

			mLoadingDialog.setMessage("Changing Password...");
			mLoadingDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			String email = params[0];
			String OldPasswordTrim = params[1];
			String NewPassword = params[2];
			
			Connect sharedConnect = Connect.getInstance(ChangePasswordActivity.this);
        	sharedConnect.changePassword(email, OldPasswordTrim, NewPassword);
        	return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

				mLoadingDialog.dismiss();
				
				Toast.makeText(ChangePasswordActivity.this, "Password changed", Toast.LENGTH_LONG).show();
				
					Intent main = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
					finish();
					startActivity(main);
		}
}
}