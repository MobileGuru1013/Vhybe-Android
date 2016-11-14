package com.planet1107.welike.connect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.planet1107.welike.activities.GeoCodingLocation;
import com.planet1107.welike.gcm.CommonUtilities;
import com.planet1107.welike.nodechat.DatabaseAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Connect {

	private static Connect mSharedInstance = null;
	DefaultHttpClient mHttpClient1;
	DefaultHttpClient mHttpClient2;
	DefaultHttpClient mHttpClient3;
	DefaultHttpClient mHttpClient4;
	User mCurrentUser;
	Context mContext;
	DatabaseAdapter db;

	public Connect(Context context) {
		mHttpClient1 = new DefaultHttpClient();
		mHttpClient2 = new DefaultHttpClient();
		mHttpClient3 = new DefaultHttpClient();
		mHttpClient4 = new DefaultHttpClient();
		mContext = context;
		db= new DatabaseAdapter(context);
		User oldUser = User.loadFromDisk(context);
		if (oldUser != null) {
			mCurrentUser = oldUser;
		}
	}

	public static Connect getInstance(Context context) {

		if (mSharedInstance == null) {
			mSharedInstance = new Connect(context.getApplicationContext());
		}
		return mSharedInstance;
	}

	//http://183.182.84.29:90/welikeapp/
	public User loginUser(String username, String password ,String deviceToken) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", username));
		parameters.add(new BasicNameValuePair("password", password));
		parameters.add(new BasicNameValuePair("deviceToken", deviceToken));
		parameters.add(new BasicNameValuePair("isdevice", "android"));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/login", parameters);
		try {
			System.out.println("login result "+jsonObject);
			if (jsonObject.has("item") && !jsonObject.getJSONObject("item").isNull("userID")) {
				User user = new User(jsonObject.getJSONObject("item"));
				user.saveOnDisk(mContext);
				mCurrentUser = user;
				System.out.println("logged in user "+user.toString());
				return user;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public User registerUser(String password, String email, String deviceToken) {	

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();	
		mpEntity.addTextBody("password", password);
		mpEntity.addTextBody("email", email);
		mpEntity.addTextBody("deviceToken", deviceToken);
		mpEntity.addTextBody("isdevice", "android");

		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/register");
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			System.out.println("Resgister Json "+ jsonObject);
			if(jsonObject !=null && jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("CONFLICT")){
				return null;
			}
			if (jsonObject != null && jsonObject.has("item")) {
				try {
					User user = new User(jsonObject.getJSONObject("item"));
					user.saveOnDisk(mContext);
					mCurrentUser = user;
					return user;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Void getchatDetail(String lastSyncDate) {

	

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();

		mpEntity.addTextBody("lastSyncDate", lastSyncDate);
		mpEntity.addTextBody("userId", String.valueOf(mCurrentUser.userID));
		System.out.println( "lastSyncDate "+lastSyncDate);


		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/getchatDetail");
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient1.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			System.out.println("getchatDetail Json "+ jsonObject);
			if (jsonObject != null && jsonObject.has("item")) {
				try {
					JSONArray jsonarray = jsonObject.getJSONArray("item");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject chatonject=jsonarray.getJSONObject(i);
						db.open();
						System.out.println(chatonject.getJSONObject("to_id_detail").getInt("userID") +" to " +chatonject.getJSONObject("from_id_detail").getInt("userID")+" from "+ chatonject.getString("text")+" message "+ chatonject.getString("isImage")+" is image "+  chatonject.getString("serverdatetime")+ " server date ");
						db.insertChat(chatonject.getJSONObject("to_id_detail").getInt("userID"), chatonject.getJSONObject("from_id_detail").getInt("userID"), chatonject.getString("text"), chatonject.getString("isImage"), chatonject.getString("serverdatetime"));
						db.close();
					}
					
				
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public String AddChatImage(int toId,  Bitmap userAvatar) {
		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		if (userAvatar != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			userAvatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			mpEntity.addBinaryBody("userAvatar", byteArray, ContentType.DEFAULT_BINARY, "userAvatar.jpg");
		}		
		mpEntity.addTextBody("fromId", String.valueOf(mCurrentUser.userID));
		mpEntity.addTextBody("toId", String.valueOf(toId));
		mpEntity.addTextBody("text", "");
		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/addchat");
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient3.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			System.out.println("jsonObject " +jsonObject);
			if (jsonObject != null && jsonObject.has("image")) {
				try {
					String image =jsonObject.getString("image");

					return image;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	public User updateUser(int userID, String email, Bitmap userAvatar, int userType, String fullName, String userGender, String userStatus ,String userDob,String userInterests,String userLocation, String latitude, String longitude, String occupation) {

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		if (userAvatar != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			userAvatar.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			mpEntity.addBinaryBody("userAvatar", byteArray, ContentType.DEFAULT_BINARY, "userAvatar.jpg");
		}

		mpEntity.addTextBody("userID", String.valueOf(userID));
		if (email != null && email.length() > 0) {
			mpEntity.addTextBody("email", email);
		}
		if (fullName != null && fullName.length() > 0) {
			mpEntity.addTextBody("userFullname", fullName);
		}
		if (userGender != null && userGender.length() > 0) {
			mpEntity.addTextBody("userGender", userGender != null ? userGender : "");
		}
		if (userStatus != null && userStatus.length() > 0) {
			mpEntity.addTextBody("userStatus", userStatus != null ? userStatus : "");
		}
		if (userDob != null && userDob.length() > 0) {
			mpEntity.addTextBody("userDob", userDob != null ? userDob : "");
		}
		if (userInterests != null && userInterests.length() > 0) {
			mpEntity.addTextBody("userInterests", userInterests != null ? userInterests : "");
		}
		if (userInterests != null && userInterests.length() > 0) {
			mpEntity.addTextBody("userInterests", userInterests != null ? userInterests : "");
		}
		if (userLocation != null && userLocation.length() > 0) {
			mpEntity.addTextBody("userLocation", userLocation != null ? userLocation : "");
		}
		if (latitude != null && latitude.length() > 0) {
			mpEntity.addTextBody("userLat", latitude != null ? latitude : "");
		}
		if (longitude != null && longitude.length() > 0) {
			mpEntity.addTextBody("userLong", longitude != null ? longitude : "");
		}
		if (occupation != null && occupation.length() > 0) {
			mpEntity.addTextBody("userOccupation", occupation != null ? occupation : "");
		}
		mpEntity.addTextBody("userTypeID", String.valueOf(userType));
		//mpEntity.addTextBody("userLat", String.valueOf(latitude));
		//	mpEntity.addTextBody("userLong", String.valueOf(longitude));


		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/setProfile");
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			System.out.println("jsonObject " +jsonObject);
			if (jsonObject != null && jsonObject.has("item")) {
				try {
					User user = new User(jsonObject.getJSONObject("item"));
					user.saveOnDisk(mContext);
					mCurrentUser = user;
					System.out.println("updated user "+ user.toString());
					return user;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUser(int userID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("forUserID", String.valueOf(userID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getProfile", parameters);
		System.out.println("getUser "+ jsonObject);
		try {
			if (jsonObject.has("item")) {
				User user = new User(jsonObject.getJSONObject("item"));
				return user;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Post> getTimeline(int userID, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("forUserID", String.valueOf(userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Post> posts = new ArrayList<Post>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getTimeline", parameters);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Post post = new Post(jsonArray.getJSONObject(i));
					posts.add(post);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}
	public ArrayList<Chat> getchatData() {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));

		ArrayList<Chat> chats = new ArrayList<Chat>();
		JSONObject jsonObject = this.getJSONObject3("http://183.182.84.29:90/welikeapp//api/getchatData", parameters);
		if (jsonObject != null && jsonObject.has("item")) {
			JSONArray jsonArray;
			
			try {
				System.out.println(" chat data " +jsonObject.getJSONArray("item"));
				jsonArray = jsonObject.getJSONArray("item");
				for (int i = 0; i < jsonArray.length(); i++) {
					Chat chat = new Chat(jsonArray.getJSONObject(i));
					chats.add(chat);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		/*	if(jsonObject.has("last_sync_date")){
			try {
				CommonUtilities.last_sync_date=jsonObject.getString("last_sync_date");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		return chats;
	}

	public ArrayList<Post> getPopular(int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Post> posts = new ArrayList<Post>();
		JSONObject jsonObject = this.getJSONObject4("http://183.182.84.29:90/welikeapp//api/getPopularPosts", parameters);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Post post = new Post(jsonArray.getJSONObject(i));
					posts.add(post);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}

	public String addchat(int toId, String text) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("fromId", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("toId", String.valueOf(toId)));
		parameters.add(new BasicNameValuePair("text", text));		
		JSONObject jsonObject = this.getJSONObject2("http://183.182.84.29:90/welikeapp//api/addchat", parameters);
		if (jsonObject != null && jsonObject.has("status")) {
			try {
				return jsonObject.getString("status");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}


	public ArrayList<Post> getRecent(int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Post> posts = new ArrayList<Post>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getRecentPosts", parameters);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Post post = new Post(jsonArray.getJSONObject(i));
					posts.add(post);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}

	public ArrayList<Post> getRecentActivity(int userID,int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("forUserID", String.valueOf(userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Post> posts = new ArrayList<Post>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getRecentActivity", parameters);
		System.out.println("getRecentActivity  "+ jsonObject );
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Post post = new Post(jsonArray.getJSONObject(i));
					posts.add(post);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}

	// Comments

	public ArrayList<Comment> getComments(int postID, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("postID", String.valueOf(postID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Comment> comments = new ArrayList<Comment>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getComments", parameters);
		System.out.println("commets " +jsonObject );
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Comment comment = new Comment(jsonArray.getJSONObject(i));
					comments.add(comment);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return comments;
	}

	public Comment sendComment(int postID, String commentText) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("postID", String.valueOf(postID)));
		parameters.add(new BasicNameValuePair("commentText", commentText));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/setComment", parameters);
		if (jsonObject != null && jsonObject.has("item")) {
			return new Comment(BaseObject.getJSONFromJSONObject(jsonObject, "item"));
		} else {
			return null;
		}
	}

	public boolean removeComment(int commentID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("commentID", String.valueOf(commentID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/removeComment", parameters);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}

	//Likes

	public ArrayList<Like> getLikes(int postID, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("postID", String.valueOf(postID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<Like> likes = new ArrayList<Like>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getLikes", parameters);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					Like like = new Like(jsonArray.getJSONObject(i));
					likes.add(like);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return likes;
	}

	public boolean likePost(int postID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("postID", String.valueOf(postID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/setLike", parameters);
		if (jsonObject != null && jsonObject.has("item")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean unlikePost(int postID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("postID", String.valueOf(postID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/removeLike", parameters);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}

	// Follows

	public ArrayList<User> getFriendInvitations(int userID, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(userID)));
		//parameters.add(new BasicNameValuePair("forUserID", String.valueOf(userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<User> users = new ArrayList<User>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getFriendInvitations", parameters);
		System.out.println("getFriendInvitations "+jsonObject);	
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i).getJSONObject("user"));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	public ArrayList<User> getFriend(int userID, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(userID)));
		parameters.add(new BasicNameValuePair("forUserID", String.valueOf(userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<User> users = new ArrayList<User>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getFriends", parameters);
		System.out.println("getFriend "+jsonObject);	
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i).getJSONObject("user"));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	public boolean addFriend(int userID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("friendID", String.valueOf(userID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/addFriend", parameters);
		System.out.println("follow user "+jsonObject);
		if (jsonObject != null && jsonObject.has("item")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean unfollowUser(int followID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("followingID", String.valueOf(followID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/removeFriend", parameters);
		System.out.println("unfollow user "+jsonObject);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}
	public boolean removeFriend(int friendID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("friendID", String.valueOf(friendID)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/removeFriend", parameters);
		System.out.println("unfollow user "+jsonObject);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}
	public boolean friendRequestResponse(int requesterID,int is_accept) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("responderID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("requesterID", String.valueOf(requesterID)));
		parameters.add(new BasicNameValuePair("approvalFlag", String.valueOf(is_accept)));
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/friendRequestResponse", parameters);
		System.out.println("friendRequestResponse "+jsonObject);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}

	// Search

	public ArrayList<User> getUsersForString(String query,String interests,String gender ,String maxage,String minage,String maritalstatus, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		parameters.add(new BasicNameValuePair("searchTerm", query));
		/*		parameters.add(new BasicNameValuePair("interests", interests));
		parameters.add(new BasicNameValuePair("gender", gender ));
		parameters.add(new BasicNameValuePair("maxage", maxage));
		parameters.add(new BasicNameValuePair("minage", minage));
		parameters.add(new BasicNameValuePair("maritalstatus", maritalstatus ));*/
		ArrayList<User> users = new ArrayList<User>();
		System.out.println(parameters+"parameters");
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/findUsers", parameters);
		System.out.println("jsonObject filter "+ jsonObject);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}
	public ArrayList<User> getUsersForFilter(String query,String interests,String gender ,String maxage,String minage,String maritalstatus, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		parameters.add(new BasicNameValuePair("searchTerm", query));
		parameters.add(new BasicNameValuePair("interests", interests));
		parameters.add(new BasicNameValuePair("gender", gender ));
		parameters.add(new BasicNameValuePair("maxage", maxage));
		parameters.add(new BasicNameValuePair("minage", minage));
		parameters.add(new BasicNameValuePair("maritalstatus", maritalstatus ));
		ArrayList<User> users = new ArrayList<User>();
		System.out.println(parameters+"parameters");
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/findUsers", parameters);
		System.out.println("jsonObject filter "+ jsonObject);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	public ArrayList<User> getUsersForLocation(String lat,String lng, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));		
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));

		parameters.add(new BasicNameValuePair("lat", lat));
		parameters.add(new BasicNameValuePair("long", lng));
		ArrayList<User> users = new ArrayList<User>();
		System.out.println(parameters+"parameters");
		JSONObject jsonObject = this.getJSONObject2("http://183.182.84.29:90/welikeapp//api/findUsers", parameters);
		System.out.println("jsonObject filter "+ jsonObject);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}
	public ArrayList<User> getFriends(String forUserID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("forUserID", forUserID));		

		ArrayList<User> users = new ArrayList<User>();
		System.out.println(parameters+"parameters");
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getFriends", parameters);
		System.out.println("jsonObject filter "+ jsonObject);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}
	public JSONObject getJSONObject(String url, List<NameValuePair> parameters) {

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			try{
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			response = mHttpClient1.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			return jsonObject;
			}catch(IllegalStateException e){
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public JSONObject getJSONObject2(String url, List<NameValuePair> parameters) {

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			response = mHttpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			return jsonObject;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public JSONObject getJSONObject3(String url, List<NameValuePair> parameters) {

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			response = mHttpClient3.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			return jsonObject;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public JSONObject getJSONObject4(String url, List<NameValuePair> parameters) {

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			response = mHttpClient4.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			return jsonObject;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public ArrayList<User> getUsersAround(double latitude, double longitude, double distance, int page, int pageSize) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("userID", String.valueOf(mCurrentUser.userID)));
		parameters.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		parameters.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
		parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
		parameters.add(new BasicNameValuePair("take", String.valueOf(pageSize)));
		ArrayList<User> users = new ArrayList<User>();
		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/getLocationsForLatLong", parameters);
		if (jsonObject != null && jsonObject.has("items")) {
			JSONArray jsonArray;
			try {
				jsonArray = jsonObject.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					User user = new User(jsonArray.getJSONObject(i));
					users.add(user);
				}
				return users;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return users;
	}

	public Post sendPost(String postTitle, Bitmap postImage) {
		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		try{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			postImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();


			mpEntity.addBinaryBody("postImage", byteArray, ContentType.DEFAULT_BINARY, "image.jpg");
		}catch(NullPointerException e){

		}
		mpEntity.addTextBody("postTitle", postTitle);
		mpEntity.addTextBody("userID", String.valueOf(mCurrentUser.userID));


		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/sendPost");
		httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient1.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			if (jsonObject != null && jsonObject.has("item")) {
				try {
					return new Post(jsonObject.getJSONObject("item"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getCurrentUser() {
		return mCurrentUser;
	}


	public Boolean forgotPassword(String emailID) {

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("email", emailID));

		JSONObject jsonObject = this.getJSONObject("http://183.182.84.29:90/welikeapp//api/forgotPassword", parameters);
		if (jsonObject != null && jsonObject.has("status") && BaseObject.getStringFromJSONObject(jsonObject, "status").equals("OK")) {
			return true;
		} else {
			return false;
		}
	}

	public User changePassword(String email,String oldPassword, String newPassword) {

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		mpEntity.addTextBody("email", email);
		mpEntity.addTextBody("oldpassword", oldPassword);
		mpEntity.addTextBody("newpassword", newPassword);

		HttpPost httpPost = new HttpPost("http://183.182.84.29:90/welikeapp//api/changePassword");
		//httpPost.addHeader("api_key", "!#wli!sdWQDScxzczFžŽYewQsq_?wdX09612627364[3072∑34260-#");
		HttpResponse response = null;
		try {
			httpPost.setEntity(mpEntity.build());
			response = mHttpClient2.execute(httpPost);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			System.out.println("change password Json "+ jsonObject);
			if (jsonObject != null && jsonObject.has("item")) {
				try {
					User user = new User(jsonObject.getJSONObject("item"));
					user.saveOnDisk(mContext);
					mCurrentUser = user;
					return user;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
