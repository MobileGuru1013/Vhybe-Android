package com.planet1107.welike.connect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;


public class BaseObject {
	
	static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public static Date getDateFromJSONObject(JSONObject jsonObject, String key) {
		
		String value = "";
		Date date = null;
		try {
			value = jsonObject.getString(key);
			date = dateFormatter.parse(value);
		} catch (Exception e) {
		}
		return date;
		
	}
	public static String getStringFromJSONObject(JSONObject jsonObject, String key) {
		
		String value = "";
		try {
			value = jsonObject.getString(key);
		} catch (Exception e) {
		}
		return value;
	}
	
	public static int getIntFromJSONObject(JSONObject jsonObject, String key) {
		
		int value = 0;
		try {
			value = jsonObject.getInt(key);
		} catch (Exception e) {
		}
		return value;
	}
	
	public static boolean getBooleanFromJSONObject(JSONObject jsonObject, String key) {
		
		boolean value = false;
		try {
			value = jsonObject.getBoolean(key);
		} catch (Exception e) {
		}
		return value;
	}
	
	public static double getDoubleFromJSONObject(JSONObject jsonObject, String key) {
		
		double value = 0;
		try {
			value = jsonObject.getDouble(key);
		} catch (Exception e) {
		}
		return value;
	}
	
	public static JSONObject getJSONFromJSONObject(JSONObject jsonObject, String key) {
		
		JSONObject value = null;
		try {
			value = jsonObject.getJSONObject(key);
		} catch (Exception e) {
		}
		return value;
	}
}
