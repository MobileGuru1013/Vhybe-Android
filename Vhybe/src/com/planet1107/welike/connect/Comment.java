package com.planet1107.welike.connect;

import java.util.Date;

import org.json.JSONObject;

public class Comment extends BaseObject {

	public int commentID;
	public String commentText;
	public Date commentDate;
	public User user;
	
	public Comment(JSONObject jsonObject) {
		
		this.commentID = getIntFromJSONObject(jsonObject, "commentID");
		this.commentText = getStringFromJSONObject(jsonObject, "commentText");
		//this.commentDate = getStringFromJSONObject(jsonObject, "commentDate");
		this.user = new User(getJSONFromJSONObject(jsonObject, "user"));
	}

	public Comment() {
	}
}
