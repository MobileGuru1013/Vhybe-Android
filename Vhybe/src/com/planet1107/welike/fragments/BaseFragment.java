package com.planet1107.welike.fragments;

import com.planet1107.welike.connect.Connect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {

	Connect sharedConnect;
	
	public void onActivityCreated (Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		sharedConnect = Connect.getInstance(getActivity());
		  final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}	
}
