package com.planet1107.welike.activities;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.internal.fi;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.adapters.GPSTracker;
import com.planet1107.welike.adapters.InterestsAutoCompleteAdapter;
import com.planet1107.welike.adapters.OccupationsAutoCompleteAdapter;
import com.planet1107.welike.adapters.PlacesAutoCompleteAdapter;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends Activity implements DatePickerDialog.OnDateSetListener {

	private static final int SELECT_PHOTO = 100;
	Bitmap userAvatar;
	ImageView mImageViewUserAvatar;	
	TextView mEditTextPassword;
	EditText mEditTextEditFullName,mEditTextEditDOB, mEditTextEditEmail;
	AutoCompleteTextView mEditTextLocation,mEditTextOccupation;
	MultiAutoCompleteTextView mEditTextInterest;
	String mlocation, mInterest,mOccupation;
	String gender,maritalStatus;
	Button btnMale,btnFeMale,btnSingle,btnMarried,btnSave;
	private static final int MY_DATE_DIALOG_ID = 3;
	Date dob;
	String locationText = "";
	String latitude="", longitude = "";
	String selectedDOB="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		setTitle("Edit Profile");

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		Connect sharedConnect = Connect.getInstance(this);
		btnMale=(Button)findViewById(R.id.btnMale);
		btnFeMale=(Button)findViewById(R.id.btnFeMale);
		btnSingle=(Button)findViewById(R.id.btnSingle);
		btnMarried=(Button)findViewById(R.id.btnMarried);
		btnSave=(Button)findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuItemSaveOnClick();
			}
		});
		mEditTextEditEmail = (EditText) findViewById(R.id.editTextEditEmail);

		mEditTextPassword=(TextView)findViewById(R.id.editTextPassword);
		mEditTextPassword.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getActionMasked();
				if (action == MotionEvent.ACTION_DOWN)
				{
					Bundle b = new Bundle();
					b.putString("email", mEditTextEditEmail.getText().toString().trim());
					Intent changePasswordIntent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
					changePasswordIntent.putExtras(b);
					startActivity(changePasswordIntent);
				}
				return false;
			}
		});

		mEditTextOccupation=(AutoCompleteTextView)findViewById(R.id.autoTextOccupation);
		mEditTextOccupation.setThreshold(1);
		mEditTextOccupation.setAdapter(new OccupationsAutoCompleteAdapter(EditProfileActivity.this,android.R.layout.simple_dropdown_item_1line));
		mEditTextOccupation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				mOccupation = (String)parent.getItemAtPosition(position);
				//TODO Do something with the selected text
				//pref.setLocation(selection);
				System.out.println(mOccupation+" mOccupation ");
			}
		});

		mEditTextInterest=(MultiAutoCompleteTextView)findViewById(R.id.editTextInterest);
		mEditTextInterest.setThreshold(1);
		mEditTextInterest.setAdapter(new InterestsAutoCompleteAdapter(EditProfileActivity.this,android.R.layout.simple_dropdown_item_1line));
		mEditTextInterest.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		mEditTextInterest.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				mInterest = (String)parent.getItemAtPosition(position);
				//TODO Do something with the selected text
				//pref.setLocation(selection);
				System.out.println(mInterest+" mInterest ");
			}
		});
		mEditTextEditDOB=(EditText)findViewById(R.id.editTextEditDOB);
		mEditTextEditDOB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDatePickerDialog(MY_DATE_DIALOG_ID);
			}
		});
		/*mEditTextEditDOB.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onClick(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getActionMasked();
				 if (action == MotionEvent.ACTION_DOWN)
				 {
						showDatePickerDialog(MY_DATE_DIALOG_ID);
				 }
				return false;
			}
		});*/
		mImageViewUserAvatar = (ImageView) findViewById(R.id.imageViewProfileUserImage);
		mEditTextEditEmail.setKeyListener(null);
		mEditTextEditFullName= (EditText) findViewById(R.id.editTextEditFullName);
		mEditTextLocation=(AutoCompleteTextView)findViewById(R.id.autoTextLocation);
		mEditTextLocation.setThreshold(1);
		mEditTextLocation.setAdapter(new PlacesAutoCompleteAdapter(	EditProfileActivity.this,android.R.layout.simple_dropdown_item_1line));
		mEditTextLocation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				mlocation = (String)parent.getItemAtPosition(position);
				//TODO Do something with the selected text
				//pref.setLocation(selection);
				System.out.println(mlocation+" mlocation ");
			}
		});

		mEditTextOccupation=(AutoCompleteTextView)findViewById(R.id.autoTextOccupation);
		mEditTextOccupation.setThreshold(1);
		mEditTextOccupation.setAdapter(new OccupationsAutoCompleteAdapter(EditProfileActivity.this,android.R.layout.simple_dropdown_item_1line));
		mEditTextOccupation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				mOccupation = (String)parent.getItemAtPosition(position);
				//TODO Do something with the selected text
				//pref.setLocation(selection);
				System.out.println(mOccupation+" mOccupation ");
			}
		});

		mEditTextInterest.setText((sharedConnect.getCurrentUser().interests.equals("null"))? "": sharedConnect.getCurrentUser().interests);

		if(sharedConnect.getCurrentUser().birthDate.equals("null")){
			mEditTextEditDOB.setText("");
		}else{
			try{
			DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = null;
			try {
				date = originalFormat.parse(sharedConnect.getCurrentUser().birthDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			selectedDOB=originalFormat.format(date);
			String formattedDate = targetFormat.format(date); 
			mEditTextEditDOB.setText(formattedDate);
			}catch(NullPointerException e){
				
			}
		}

		//mEditTextEditDOB.setText((sharedConnect.getCurrentUser().birthDate.equals("null"))? "":sharedConnect.getCurrentUser().birthDate);
		mEditTextEditEmail.setText(sharedConnect.getCurrentUser().userEmail);
		if(sharedConnect.getCurrentUser().location.equals("null")||sharedConnect.getCurrentUser().location.equals("")){
			GPSTracker gps = new GPSTracker(this);
			if (gps.canGetLocation()) {
				double lati = gps.getLatitude();
				double longi = gps.getLongitude();
				System.out.println("lati "+lati);
				System.out.println("longi "+longi);
				Geocoder geocoder;
				List<Address> addresses;
				geocoder = new Geocoder(this, Locale.getDefault());

				try {
					addresses = geocoder.getFromLocation(lati, longi, 1);

					String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
					String city = addresses.get(0).getLocality();
					String state = addresses.get(0).getAdminArea();
					String country = addresses.get(0).getCountryName();
					mEditTextLocation.setText(city+", "+ state+", "+country);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IndexOutOfBoundsException e) {
					// TODO: handle exception
				} // Here 1 represent max location result to returned, by documents it recommended 1 to 5
			}
		}else{
			mEditTextLocation.setText(sharedConnect.getCurrentUser().location);
		}

		mEditTextEditFullName.setText(sharedConnect.getCurrentUser().userFullName);
		try{
			mEditTextOccupation.setText((sharedConnect.getCurrentUser().occupation.equals("null"))? "":sharedConnect.getCurrentUser().occupation);
		}catch(NullPointerException e){
			mEditTextOccupation.setText("");
		}

		if(sharedConnect.getCurrentUser().gender.equalsIgnoreCase("female")){
			gender="female";
			btnFeMale.setBackgroundColor(getResources().getColor(R.color.app_color));
			btnFeMale.setTextColor(Color.WHITE);
			btnMale.setBackgroundResource(R.drawable.button_border);
			btnMale.setTextColor(getResources().getColor(R.color.app_color));
		}else{
			gender="male";
			btnMale.setBackgroundColor(getResources().getColor(R.color.app_color));
			btnMale.setTextColor(Color.WHITE);
			btnFeMale.setBackgroundResource(R.drawable.button_border);
			btnFeMale.setTextColor(getResources().getColor(R.color.app_color));
		}
		btnMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gender="male";
				btnMale.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnMale.setTextColor(Color.WHITE);
				btnFeMale.setBackgroundResource(R.drawable.button_border);
				btnFeMale.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		btnFeMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gender="female";
				btnFeMale.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnFeMale.setTextColor(Color.WHITE);
				btnMale.setBackgroundResource(R.drawable.button_border);
				btnMale.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		if(sharedConnect.getCurrentUser().maritalStatus.equalsIgnoreCase("married")){
			maritalStatus="married";
			btnMarried.setBackgroundColor(getResources().getColor(R.color.app_color));
			btnMarried.setTextColor(Color.WHITE);
			btnSingle.setBackgroundResource(R.drawable.button_border);
			btnSingle.setTextColor(getResources().getColor(R.color.app_color));
		}else{
			maritalStatus="single";
			btnSingle.setBackgroundColor(getResources().getColor(R.color.app_color));
			btnSingle.setTextColor(Color.WHITE);
			btnMarried.setBackgroundResource(R.drawable.button_border);
			btnMarried.setTextColor(getResources().getColor(R.color.app_color));
		}
		btnSingle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				maritalStatus="single";
				btnSingle.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnSingle.setTextColor(Color.WHITE);
				btnMarried.setBackgroundResource(R.drawable.button_border);
				btnMarried.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		btnMarried.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				maritalStatus="married";
				btnMarried.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnMarried.setTextColor(Color.WHITE);
				btnSingle.setBackgroundResource(R.drawable.button_border);
				btnSingle.setTextColor(getResources().getColor(R.color.app_color));
			}
		});



		if(sharedConnect.getCurrentUser().userAvatarPath.equals("null")){

			mImageViewUserAvatar.setImageResource(R.drawable.avatar_empty);
		}else{
			UrlImageViewHelper.setUrlDrawable(mImageViewUserAvatar, sharedConnect.getCurrentUser().userAvatarPath);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == R.id.action_save) {
			menuItemSaveOnClick();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void menuItemSaveOnClick() {
		if(mEditTextEditFullName.getText().toString().equals("") || mEditTextEditFullName==null){
			showToast(getString(R.string.pleaseenterfullname));
		}else if(mEditTextEditEmail.getText().toString().equals("") || mEditTextEditEmail==null){
			showToast(getString(R.string.pleaseenteremail));
		}else if(mEditTextEditDOB.getText().toString().equals("") || mEditTextEditDOB==null){
			showToast(getString(R.string.pleaseenterdob));
		}else if(!validateAge()){
			showToast(getString(R.string.minimumageshouldbeeleven));
		}
		else{ 

			locationText = mEditTextLocation.getText().toString().trim();
			if(!locationText.equals("")){
				String latLongResult;
				
					latLongResult = GeoCodingLocation.getAddressFromLocation(locationText,
							EditProfileActivity.this, new GeocoderHandler()  	);
			
				
				if(latLongResult!=null){
					String[] latLongArray = latLongResult.split(",");
					try{
						latitude = latLongArray[0];
						longitude = latLongArray[1];
						System.out.println(" latitude "+ latitude);
						System.out.println(" longitude "+ longitude);
						if(latitude.equals("") || latitude == null || latitude.equals("0.00000000")||longitude.equals("0.00000000") ||longitude.equals("") || longitude == null){
							locationText = "";
							latitude = "";
							longitude = "";

						}
					}catch (ArrayIndexOutOfBoundsException e) {
						// TODO: handle exception
						locationText = "";
						latitude = "";
						longitude = "";
					}
				}else{
					locationText = "";
				}
			}
			else{
				latitude = "";
				longitude = "";
				locationText="";
			}

			new AsyncTask<Void, Void, User>() {

				private ProgressDialog mLoadingDialog = new ProgressDialog(EditProfileActivity.this);

				@Override
				protected void onPreExecute() {

					mLoadingDialog.setMessage("Saving changes..");
					mLoadingDialog.show();
				}

				@Override
				protected User doInBackground(Void... params) {

					Connect sharedConnect = Connect.getInstance(EditProfileActivity.this);
					int userType;
					if (sharedConnect.getCurrentUser().userType == User.UserType.UserTypeCompany) {
						userType = 2;
					} else {
						userType = 1;
					}
					System.out.println("locationText "+locationText );
					//return sharedConnect.updateUser(sharedConnect.getCurrentUser().userID, mEditTextEditEmail.getText().toString(), userAvatar, userType, mEditTextEditFullName.getText().toString(), sharedConnect.getCurrentUser().userInfo, sharedConnect.getCurrentUser().latitude, sharedConnect.getCurrentUser().longitude, mEditTextLocation.getText().toString());				
					return sharedConnect.updateUser(sharedConnect.getCurrentUser().userID, mEditTextEditEmail.getText().toString(), userAvatar, userType, mEditTextEditFullName.getText().toString(), gender, maritalStatus, selectedDOB, mEditTextInterest.getText().toString(),locationText, latitude, longitude, mEditTextOccupation.getText().toString());
				}

				protected void onPostExecute(User results) {

					mLoadingDialog.dismiss();

					if(PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this).getBoolean("IS_Profile_complete", true) ){
						//	Intent main = new Intent(this, MainActivity.class);
						//startActivity(main);
					}else{
						Intent main = new Intent(EditProfileActivity.this, MainActivity.class);
						startActivity(main);
					}
					PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this).edit().putBoolean("IS_Profile_complete", true).commit(); 

				}

			}.execute();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this).getBoolean("IS_Profile_complete", true) ){
			super.onBackPressed();
		}else{
		
		}

	}

	public void imageViewEditProfileOnClick(View v) {

		Intent intentPhotoPicker = new Intent(Intent.ACTION_PICK);
		intentPhotoPicker.setType("image/*");
		startActivityForResult(intentPhotoPicker, SELECT_PHOTO);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data); 
		switch (requestCode) { 
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {  
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				userAvatar = BitmapFactory.decodeFile(filePath);
				mImageViewUserAvatar.setImageBitmap(userAvatar);
			}
			break;
		}
	}

	public void showToast(String message){
		Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	public int getDiffYears(Date first, Date last) {

		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || 
				(a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}
	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	private void showDatePickerDialog(int myDateDialogId) {
		// TODO Auto-generated method stub
		int iDay,iMonth,iYear;

		Calendar cal = Calendar.getInstance();
		iDay = cal.get(Calendar.DAY_OF_MONTH);
		iMonth = cal.get(Calendar.MONTH);
		iYear = cal.get(Calendar.YEAR);
		final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, iYear, iMonth, iDay);
		datePickerDialog.show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

		if(view.isShown()) {
			String monthOfYearString = "", dayOfMonthString = "";
			if(monthOfYear<10){
				monthOfYearString = "0"+monthOfYear;
			}else{
				monthOfYearString = monthOfYear+"";
			}

			if(dayOfMonth<10){
				dayOfMonthString = "0"+dayOfMonth;
			}else{
				dayOfMonthString = dayOfMonth+"";
			}

			String selectedDate = dayOfMonthString+"-"+monthOfYearString + "-" + year;
			selectedDOB =year +"-"+monthOfYearString + "-" + dayOfMonthString;
			mEditTextEditDOB.setText(selectedDate);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			dob=null;
			try {
				try {
					dob = (Date) df.parse(selectedDate);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Boolean validateAge(){
		Boolean isAgeValid = false;
		//Current date
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String currentDateString = df.format(c.getTime());
		Date currentDate = null;
		try {
			currentDate = df.parse(currentDateString);
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(dob==null){
			try {
				dob = df.parse(mEditTextEditDOB.getText().toString());
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int age = getDiffYears(dob, currentDate);

		if(age>=11){
			isAgeValid = true;
		}

		return isAgeValid;
	}

	public static class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String locationAddress = "";
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				locationAddress = bundle.getString("address");
				break;
			default:
				locationAddress = null;
			}
		}
	}
}
