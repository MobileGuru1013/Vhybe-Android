package com.planet1107.welike.activities;

import java.util.ArrayList;

import com.planet1107.welike.R;
import com.planet1107.welike.adapters.InterestsAutoCompleteAdapter;
import com.planet1107.welike.adapters.OccupationsAutoCompleteAdapter;
import com.planet1107.welike.adapters.PlacesAutoCompleteAdapter;
import com.planet1107.welike.fragments.SearchFragment;
import com.planet1107.welike.views.RangeSeekBar;
import com.planet1107.welike.views.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class FilterActivity extends Activity  {
	String[] Gender = { "Male", "Female", "Both" };
	String[] Status = { "Single", "Married", "Both" };
	ArrayList<String> locationArraylist = new ArrayList<String>();
	RangeSeekBar<Integer> seekBar;
	TextView txtMinCost,txtMaxCost;
	Button btn_Search;
	AutoCompleteTextView mEditTextLocation,mEditTextOccupation;
	MultiAutoCompleteTextView edt_interests;
	String gender,status;
	Button btnMale,btnFeMale,btnSingle,btnMarried,btnGenderBoth, btnStatusBoth;
	int a=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		//Spinner spin_Gender = (Spinner) findViewById(R.id.spinner_gender);
		//Spinner spin_status = (Spinner) findViewById(R.id.spinner_status);

	    getActionBar().setCustomView(R.layout.my_custom_title);
	    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		btnMale=(Button)findViewById(R.id.btnMale);
		btnFeMale=(Button)findViewById(R.id.btnFeMale);
		btnSingle=(Button)findViewById(R.id.btnSingle);
		btnMarried=(Button)findViewById(R.id.btnMarried);
		btnGenderBoth=(Button)findViewById(R.id.btnBoth);
		btnStatusBoth=(Button)findViewById(R.id.btnStatusBoth);
		txtMinCost = (TextView)findViewById(R.id.txt_min_cost);
		txtMaxCost = (TextView)findViewById(R.id.txt_max_cost);
		
		btnMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gender="male";
				btnMale.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnMale.setTextColor(Color.WHITE);
				btnFeMale.setBackgroundResource(R.drawable.button_border);
				btnFeMale.setTextColor(getResources().getColor(R.color.app_color));
				btnGenderBoth.setBackgroundResource(R.drawable.button_border);
				btnGenderBoth.setTextColor(getResources().getColor(R.color.app_color));
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
				btnGenderBoth.setBackgroundResource(R.drawable.button_border);
				btnGenderBoth.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		btnGenderBoth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gender="female";
				btnGenderBoth.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnGenderBoth.setTextColor(Color.WHITE);
				btnMale.setBackgroundResource(R.drawable.button_border);
				btnMale.setTextColor(getResources().getColor(R.color.app_color));
				btnFeMale.setBackgroundResource(R.drawable.button_border);
				btnFeMale.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		
		btnSingle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				status="single";
				btnSingle.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnSingle.setTextColor(Color.WHITE);
				btnMarried.setBackgroundResource(R.drawable.button_border);
				btnMarried.setTextColor(getResources().getColor(R.color.app_color));
				btnStatusBoth.setBackgroundResource(R.drawable.button_border);
				btnStatusBoth.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		btnMarried.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				status="married";
				btnMarried.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnMarried.setTextColor(Color.WHITE);
				btnSingle.setBackgroundResource(R.drawable.button_border);
				btnSingle.setTextColor(getResources().getColor(R.color.app_color));
				btnStatusBoth.setBackgroundResource(R.drawable.button_border);
				btnStatusBoth.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		btnStatusBoth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				status="both";
				btnStatusBoth.setBackgroundColor(getResources().getColor(R.color.app_color));
				btnStatusBoth.setTextColor(Color.WHITE);
				btnSingle.setBackgroundResource(R.drawable.button_border);
				btnSingle.setTextColor(getResources().getColor(R.color.app_color));
				btnMarried.setBackgroundResource(R.drawable.button_border);
				btnMarried.setTextColor(getResources().getColor(R.color.app_color));
			}
		});
		
		//-- Gender Spinner --
		//spin_Gender.setOnItemSelectedListener(this);
		ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Gender);
		adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		/*spin_Gender.setAdapter(adapterGender);
		spin_Gender.setSelection(2);
		spin_Gender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					gender="male";
					break;
				case 1:
					gender="female";
					break;

				case 2:
					gender="";
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/
		//-- Status Spinner --
		//spin_status.setOnItemSelectedListener(this);
		ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Status);
		adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		/*spin_status.setAdapter(adapterStatus);
		spin_status.setSelection(2);
		spin_status.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					status="single";
					break;
				case 1:
					status="married";
					break;

				case 2:
					status="";
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

		mEditTextLocation=(AutoCompleteTextView)findViewById(R.id.autoTextLocation);
		mEditTextLocation.setThreshold(1);
		mEditTextLocation.setAdapter(new PlacesAutoCompleteAdapter(	FilterActivity.this,android.R.layout.simple_dropdown_item_1line));
		
		mEditTextOccupation=(AutoCompleteTextView)findViewById(R.id.autoTextOccupation);
		mEditTextOccupation.setThreshold(1);
		mEditTextOccupation.setAdapter(new OccupationsAutoCompleteAdapter(FilterActivity.this,android.R.layout.simple_dropdown_item_1line));

		edt_interests=(MultiAutoCompleteTextView)findViewById(R.id.edt_interests);
		edt_interests.setThreshold(1);
		edt_interests.setAdapter(new InterestsAutoCompleteAdapter(FilterActivity.this,android.R.layout.simple_dropdown_item_1line));
		edt_interests.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		
		seekBar = new RangeSeekBar<Integer>(11, 100, this);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				// handle changed range values
				//Toast.makeText(Refine.this, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue, Toast.LENGTH_LONG).show();
				System.out.println("User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
				txtMinCost.setText(minValue+" years");				
				txtMaxCost.setText(maxValue+" years");
			}
		});
		
		seekBar.setOnDragListener(new OnDragListener() {
			
			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				System.out.println("on drag"+ a++);
				return false;
			}
		});
	
		ViewGroup layout = (ViewGroup) findViewById(R.id.ll_seekbar); 
		seekBar.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.setPadding(5, 5, 5, 5);
		layout.addView(seekBar);
		btn_Search=(Button)findViewById(R.id.btn_Search);
		btn_Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchFragment.interests=edt_interests.getText().toString().trim();				
				SearchFragment.gender=gender;
				SearchFragment.maxage=txtMaxCost.getText().toString();
				SearchFragment.minage=txtMinCost.getText().toString();
				SearchFragment.maritalstatus=status;
				SearchFragment.location=mEditTextLocation.getText().toString().trim();
				SearchFragment.occupation=mEditTextOccupation.getText().toString().trim();
				
				SearchFragment.is_filter=true;
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.filter, menu);
		return true;
	}



}
