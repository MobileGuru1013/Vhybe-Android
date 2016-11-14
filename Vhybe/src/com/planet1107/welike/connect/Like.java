package com.planet1107.welike.connect;

import org.json.JSONObject;

public class Like extends BaseObject {

	public int likeID;
	public User likeUser;
	
	public Like(JSONObject jsonObject) {
		
		this.likeID = getIntFromJSONObject(jsonObject, "likeID");
		this.likeUser = new User(getJSONFromJSONObject(jsonObject, "user"));
	}
}
