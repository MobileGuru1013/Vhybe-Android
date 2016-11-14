package com.planet1107.welike.connect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;


public class Post extends BaseObject implements Serializable {

	private static final long serialVersionUID = 1L;
	public String postTitle;
	public String postImagePath;
	public Date postDate;
	public ArrayList<String> postKeywords;
	public User postUser;
	public int postID;
	public int postLikesCount;
	public int postCommentsCount;
	public int likedThisPost;
	public boolean commentedThisPost;
	public String timeAgo;
	
	public Post(JSONObject jsonObject) {
		
		this.postID = getIntFromJSONObject(jsonObject, "postID");
		this.postImagePath = getStringFromJSONObject(jsonObject, "postImage");
		this.postTitle = getStringFromJSONObject(jsonObject, "postTitle");
		this.postDate = getDateFromJSONObject(jsonObject, "postDate");
		this.postUser = new User(getJSONFromJSONObject(jsonObject, "user"));
		this.postLikesCount = getIntFromJSONObject(jsonObject, "totalLikes");
		this.postCommentsCount = getIntFromJSONObject(jsonObject, "totalComments");
		likedThisPost = getIntFromJSONObject(jsonObject, "isLiked");
		this.commentedThisPost = getBooleanFromJSONObject(jsonObject, "isCommented");
		this.timeAgo = getStringFromJSONObject(jsonObject, "timeAgo");
	}

	/*
	public Post(Parcel in) { 
	
		readFromParcel(in); 
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	private void readFromParcel(Parcel in) {  
		
		intValue = in.readInt();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
		
		public Post createFromParcel(Parcel in) { 
			
			return new Post(in); 
		}   
		
		public Post[] newArray(int size) { 
			
			return new ObjectA[size]; 
		} 
	};
	*/
}
