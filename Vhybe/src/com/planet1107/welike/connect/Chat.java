package com.planet1107.welike.connect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;


public class Chat extends BaseObject implements Serializable {

	private static final long serialVersionUID = 1L;
	public String channel_id;
	public String text;
	public Date serverdatetime;
	public User userdetail;
	public int to_id;
	public String timestamp;
	public int unread;
	public int isImage;
	
	
	
	public Chat(JSONObject jsonObject) {
		
		this.to_id = getIntFromJSONObject(jsonObject, "to_id");
		this.channel_id = getStringFromJSONObject(jsonObject, "channel_id");
		this.text = getStringFromJSONObject(jsonObject, "text");
		this.serverdatetime = getDateFromJSONObject(jsonObject, "serverdatetime");
		this.userdetail = new User(getJSONFromJSONObject(jsonObject, "userdetail"));
		this.timestamp = getStringFromJSONObject(jsonObject, "timestamp");
		this.unread = getIntFromJSONObject(jsonObject, "unread");
		this.isImage = getIntFromJSONObject(jsonObject, "isImage");
		

	}
}