package com.planet1107.welike.connect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;



public class User extends BaseObject implements Serializable {

	public enum UserType {
		
		UserTypeUnknown,
		UserTypePerson,
		UserTypeCompany
	};
	
	private static final long serialVersionUID = 1L;
	public int userID;
	public UserType userType;
	public String userPassword;
	public String userEmail;
	public String userFullName;
	public String userInfo;
	public String userAvatarPath;
	public String userUsername;
	public String channnelId;
	public boolean followingUser;
	public int userFollowersCount;
	public int userFollowingCount;

	//when userType == WLIUserTypeCompany
	public double latitude;
	public double longitude;
	public String companyAddress;
	public String companyPhone;
	public String companyWeb;
	public String companyEmail;
	
	public String gender;
	public String birthDate,deviceToken;
	public String maritalStatus;
	public String location, occupation;
	public String interests,isdevice;
	public int friendCount,friendshipStatus;
	public String timeAgo;

	String title;
	String subtitle;
	
	public User(JSONObject jsonObject) {
		
		this.userID = getIntFromJSONObject(jsonObject, "userID");
		this.userType = (UserType.values()[getIntFromJSONObject(jsonObject, "userTypeID")]);
		this.userPassword = getStringFromJSONObject(jsonObject, "password");
		this.userEmail = getStringFromJSONObject(jsonObject, "email");
		this.userFullName = getStringFromJSONObject(jsonObject, "userFullname");
		this.userInfo = getStringFromJSONObject(jsonObject, "userInfo");
		this.userAvatarPath = getStringFromJSONObject(jsonObject, "userAvatar");
		this.userUsername = getStringFromJSONObject(jsonObject, "username");
		this.followingUser = getIntFromJSONObject(jsonObject, "followingUser") == 1;
		this.userFollowersCount = getIntFromJSONObject(jsonObject, "followersCount");
		this.userFollowingCount = getIntFromJSONObject(jsonObject, "followingCount");
		this.companyAddress = getStringFromJSONObject(jsonObject, "userAddress");
		this.companyPhone = getStringFromJSONObject(jsonObject, "userPhone");
		this.companyWeb = getStringFromJSONObject(jsonObject, "userWeb");
		this.companyEmail = getStringFromJSONObject(jsonObject, "userEmail");		
		this.latitude = getDoubleFromJSONObject(jsonObject, "userLat");	
		this.longitude = getDoubleFromJSONObject(jsonObject, "userLong");	
		this.gender = getStringFromJSONObject(jsonObject, "gender");
		this.birthDate = getStringFromJSONObject(jsonObject, "birthDate");
		this.maritalStatus = getStringFromJSONObject(jsonObject, "maritalStatus");
		this.location = getStringFromJSONObject(jsonObject, "location");
		this.occupation = getStringFromJSONObject(jsonObject, "occupations");
		this.interests = getStringFromJSONObject(jsonObject, "interests");
		this.friendCount = getIntFromJSONObject(jsonObject, "friendCount");
		this.timeAgo = getStringFromJSONObject(jsonObject, "timeAgo");
		this.friendshipStatus=getIntFromJSONObject(jsonObject, "friendshipStatus");
		this.channnelId=getStringFromJSONObject(jsonObject, "channnelId");
		this.deviceToken=getStringFromJSONObject(jsonObject, "deviceToken");
		this.isdevice=getStringFromJSONObject(jsonObject, "isdevice");
		if(this.interests.equals("null"))this.interests="";
		if(this.location.equals("null"))this.location="";
		if(this.maritalStatus.equals("null"))this.maritalStatus="";
		
	}
	
	public void saveOnDisk(Context context) {
		
		FileOutputStream fos;
		try {
			fos = context.openFileOutput("currentUser", Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static User loadFromDisk(Context context) {
		
		FileInputStream fis;
		try {
			fis = context.openFileInput("currentUser");
			ObjectInputStream is = new ObjectInputStream(fis);
			User user = (User)is.readObject();
			is.close();
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean deleteFromDisk(Context context) {
		
		context.deleteFile("currentUser");
		return true;
	}
}
