package com.planet1107.welike.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.planet1107.welike.R;
import com.planet1107.welike.connect.Connect;
import com.planet1107.welike.connect.User;
import com.planet1107.welike.fragments.FriendsFragment;
import com.planet1107.welike.nodechat.ChatModel;
import com.planet1107.welike.nodechat.DatabaseAdapter;
import com.planet1107.welike.nodechat.NetClient;
import com.planet1107.welike.nodechat.Utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	String macAddress;
	ArrayList<ChatModel> ChatList=new ArrayList<ChatModel>();
	ListView chatlistview;
	Activity context;
	ChatAdapter adapter;
	EditText etText; 
	DatabaseAdapter db;
	NetClient nc;	
	User mchatUSer;
	Connect mConnect;

	String mCameraFileName;
	public static String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	private Uri mCapturedImageURI;
	String filePath;
	protected String selectedImagePath="",update_profile_url;
	protected TextView txtCamera;
	protected TextView txtGallary;
	protected TextView txtCancel;
	private Bitmap bitmap;
	String updatedImageUrl;
	String lastSynchDate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		db=new DatabaseAdapter(this);
		mConnect=new Connect(this);
		mchatUSer=(User)getIntent().getSerializableExtra("ChatUser");
		

		    

		    getActionBar().setCustomView(R.layout.my_custom_title);
		    getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		//setTitle(mchatUSer.userFullName);
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		macAddress= wInfo.getMacAddress(); 
		etText=(EditText) findViewById(R.id.etText);



		TEMP_PHOTO_FILE_NAME=System.currentTimeMillis()+TEMP_PHOTO_FILE_NAME;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
		}
		else {
			new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
		}

		ChatList.clear();
		context=ChatActivity.this;
		chatlistview=(ListView)findViewById(R.id.lstchat);
		new Thread(new Runnable() {
			public void run() {
				try{
						//nc = new NetClient(ChatActivity.this,"10.10.10.125",1337, macAddress);
					nc = new NetClient(ChatActivity.this,"10.10.1.100",1337, macAddress);//mac address maybe not for you
					System.out.println(mchatUSer.channnelId+ " channel id");
					nc.connectWithServer();
					nc.sendDataWithString("__SUBSCRIBE__"+mchatUSer.channnelId+"__ENDSUBSCRIBE__");					
					nc.receiveDataFromServer();					
				}catch(NullPointerException e){

				}
			}
		}).start();
		db.open();
		Cursor cursor= db.getchat(mConnect.getCurrentUser().userID, mchatUSer.userID);
		if(cursor.equals(null)){			
		}else {			
			if (cursor.moveToFirst()){
				do{
					cursor.getString(cursor.getColumnIndex("to_user_id"));
					String fromUser = cursor.getString(cursor.getColumnIndex("from_user_id"));
					String message = cursor.getString(cursor.getColumnIndex("message"));
					String date = cursor.getString(cursor.getColumnIndex("server_date"));
					String formattedDate = date;
					try{
						Date date2=null;															
						DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
						originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						DateFormat targetFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a"); 	
						targetFormat.setTimeZone(TimeZone.getTimeZone("UTC+5:30"));
						try {
							date2 = originalFormat.parse(date);
						} catch (java.text.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						formattedDate = targetFormat.format(date2); 
					}catch(NullPointerException e){

					}

					if(fromUser.equals(mConnect.getCurrentUser().userID+"")) {

						ChatList.add(new ChatModel(message,"" ,formattedDate+""));	  
					}else{						

						ChatList.add(new ChatModel("", message,formattedDate+""));						                    
					}
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
		db.close();
		adapter=new ChatAdapter(context,ChatList);
		chatlistview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		chatlistview.setSelection(ChatList.size());
	}

	public class ChatAdapter extends ArrayAdapter<ChatModel> {

		private final Activity context;
		public ChatAdapter(Activity context, List<ChatModel> list) {
			super(context, R.layout.chat_lsit_row, list);
			this.context = context;
		}

		class ViewHolder {
			protected TextView txtreciever,txtReciverDate;
			protected TextView txtsender,txtSenderDate;
			protected ImageView imgsender,imageViewReceiverUserImage;
			protected ImageView imgreciever,imageViewSenderUserImage;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				view = inflator.inflate(R.layout.chat_lsit_row, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.imageViewReceiverUserImage=(ImageView)view.findViewById(R.id.imageViewReceiverUserImage);
				viewHolder.txtreciever = (TextView) view.findViewById(R.id.txtreciever);
				viewHolder.txtSenderDate = (TextView) view.findViewById(R.id.txtSenderDate);
				viewHolder.txtReciverDate = (TextView) view.findViewById(R.id.txtReciverDate);
				viewHolder.txtsender = (TextView) view.findViewById(R.id.txtsender);
				viewHolder.imgreciever = (ImageView) view.findViewById(R.id.imgreciever);
				viewHolder.imgsender = (ImageView) view.findViewById(R.id.imgsender);
				viewHolder.imageViewSenderUserImage = (ImageView) view.findViewById(R.id.imageViewSenderUserImage);
				view.setTag(viewHolder);
			} else {
				view = convertView;
			}
			ViewHolder holder = (ViewHolder) view.getTag();

			if(ChatList.get(position).getReceiverMessage().isEmpty()){
				holder.imageViewReceiverUserImage.setVisibility(View.INVISIBLE);
				holder.imageViewSenderUserImage.setVisibility(View.VISIBLE);
				holder.txtReciverDate.setVisibility(View.GONE);
				holder.txtSenderDate.setText(ChatList.get(position).getDate());
				holder.txtSenderDate.setVisibility(View.VISIBLE);
				if(mConnect.getCurrentUser().userAvatarPath.equals("null")||mConnect.getCurrentUser().userAvatarPath.equals("")){
					holder.imageViewSenderUserImage.setImageResource(R.drawable.avatar_empty);
				}else{

					UrlImageViewHelper.setUrlDrawable(holder.imageViewSenderUserImage, mConnect.getCurrentUser().userAvatarPath);
				}
			}else{
				holder.imageViewSenderUserImage.setVisibility(View.INVISIBLE);
				holder.imageViewReceiverUserImage.setVisibility(View.VISIBLE);
				holder.txtSenderDate.setVisibility(View.GONE);
				holder.txtReciverDate.setText(ChatList.get(position).getDate());
				holder.txtReciverDate.setVisibility(View.VISIBLE);
				if(mchatUSer.userAvatarPath.equals("null")||mchatUSer.userAvatarPath.equals("")){
					holder.imageViewReceiverUserImage.setImageResource(R.drawable.avatar_empty);
				}else{

					UrlImageViewHelper.setUrlDrawable(holder.imageViewReceiverUserImage, mchatUSer.userAvatarPath);
				}
			}
			if(ChatList.get(position).getReceiverMessage().contains("http://183.182.84.29:90/")){
				holder.imgreciever.setVisibility(View.VISIBLE);
				holder.txtreciever.setVisibility(View.INVISIBLE);				
				UrlImageViewHelper.setUrlDrawable(holder.imgreciever, ChatList.get(position).getReceiverMessage());
			}else{
				holder.txtreciever.setText(ChatList.get(position).getReceiverMessage());
				holder.txtreciever.setVisibility(View.VISIBLE);
				holder.imgreciever.setVisibility(View.GONE);
			}
			if(ChatList.get(position).getSenderMessage().contains("http://183.182.84.29:90/")) {
				holder.imgsender.setVisibility(View.VISIBLE);
				holder.txtsender.setVisibility(View.INVISIBLE);
				UrlImageViewHelper.setUrlDrawable(holder.imgsender, ChatList.get(position).getSenderMessage());
			}else {
				holder.txtsender.setText(ChatList.get(position).getSenderMessage());
				holder.txtsender.setVisibility(View.VISIBLE);
				holder.imgsender.setVisibility(View.GONE);
			}
			return view;
		}
	}
	@SuppressLint("SimpleDateFormat")
	public void sendMessage(View v) {
		if (etText.getText().toString().trim().length() > 0) {
			/*   hub.invoke( "SendCustomType",
	                new CustomType() {{ Name = "Universe"; Id = 42; }} ).get();*/           

			System.out.println("Sending message to server.");
			JSONObject jsonObj = new JSONObject();
			try {
				Calendar c = Calendar.getInstance();
				;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				String strDate = sdf.format(c.getTime());	
				System.out.println("strDate "+strDate);
				DateFormat targetFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a"); 				
				String formattedDate = targetFormat.format(c.getTime()); 				
				ChatList.add(new ChatModel(etText.getText().toString(), "",formattedDate));
				adapter.notifyDataSetChanged();
				chatlistview.setSelection(ChatList.size());
				jsonObj.put("key", etText.getText().toString().trim());		
				jsonObj.put("userId", "1");	
				jsonObj.put("fromId", mConnect.getCurrentUser().userID);
				jsonObj.put("isImage","0");
				jsonObj.put("user_name",mConnect.getCurrentUser().userFullName);
				jsonObj.put("toId",mchatUSer.userID);
				jsonObj.put("devicetoken",mchatUSer.deviceToken);
				jsonObj.put("isdevice",mchatUSer.isdevice);
				jsonObj.put("date",strDate);

				nc.sendDataWithString("__JSON__START__" + jsonObj.toString() + "__JSON__END__");		
				/*db.open();
				db.insertChat(mchatUSer.userID, mConnect.getCurrentUser().userID, etText.getText().toString().trim(), "0", strDate);
				db.close();*/
				mConnect.addchat(mchatUSer.userID, etText.getText().toString().trim());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			etText.setText("");
		}
	}

	public  void onJsonRecieved(final JSONObject resposnejson) {
		System.out.println("onJsonRecieved "+resposnejson);
		System.out.println(mConnect.getCurrentUser().userID);
		try{

			System.out.println( "resposnejson.getString " +resposnejson.getString("fromId"));
			if(!resposnejson.getString("fromId").equals(mConnect.getCurrentUser().userID+"")){

				runOnUiThread(new Runnable() {
					public void run(){   
						try {
							Date date2=null;															
							DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
							originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
							DateFormat targetFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a"); 	
							originalFormat.setTimeZone(TimeZone.getTimeZone("UTC+5:30"));

							try {
								date2 = originalFormat.parse(resposnejson.getString("date"));
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
							String formattedDate = targetFormat.format(date2); 
							System.out.println("formattedDate "+formattedDate);
							ChatList.add(new ChatModel("", resposnejson.getString("key"),formattedDate));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						adapter.notifyDataSetChanged();
						chatlistview.setSelection(ChatList.size());

						/*	db.open();
						try{
							db.insertChat(mConnect.getCurrentUser().userID, mchatUSer.userID, resposnejson.getString("key"), "0", resposnejson.getString("date"));
						}	catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						db.close();*/
					}
				});
			}
		}catch(JSONException e){

		}
	}

	public void SendImage(View v){
		selectedImagePath="";
		final Dialog dialogMenu = new Dialog(ChatActivity.this);
		dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogMenu.setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams wmlp = dialogMenu.getWindow().getAttributes();
		wmlp.gravity =  Gravity.BOTTOM;
		wmlp.width= ViewGroup.LayoutParams.MATCH_PARENT;
		dialogMenu.setContentView(R.layout.contextmenu);
		txtCamera = (TextView)dialogMenu.findViewById(R.id.txtCamera);
		txtCamera.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMenu.hide();
				//open camera
				try {
					String fileName = "temp.png";
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, fileName);
					mCapturedImageURI = getContentResolver()
							.insert(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									values);
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					//   intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/")));
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							mCapturedImageURI);
					startActivityForResult(intent, 0);
				} catch (NullPointerException e) {
					Toast.makeText(ChatActivity.this, "File path did not found.Please try another file", Toast.LENGTH_SHORT).show();

					//System.out.println("with in camera");
				}
				catch (OutOfMemoryError e) {
					Toast.makeText(ChatActivity.this, "File path did not found.Please try another file", Toast.LENGTH_SHORT).show();


				}
				catch (Exception e) {
					// Log.e("", "", e);
				}


			}
		});
		txtGallary = (TextView)dialogMenu.findViewById(R.id.txtGallary);
		txtGallary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMenu.hide();
				//open gallery
				Intent pickPhoto = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto , 1);
			}
		});
		txtCancel = (TextView)dialogMenu.findViewById(R.id.txtCancel);
		txtCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMenu.dismiss();
			}
		});
		dialogMenu.show();
	}

	public static void onStringRecieved(String resposneMessage) {
		System.out.println("onStringRecieved "+resposneMessage);
	}


	@SuppressLint("SimpleDateFormat")
	public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch(requestCode) {
		case 0:
			if (resultCode == ChatActivity.this.RESULT_OK) {
				try{
					String[] projection = { MediaStore.Images.Media.DATA };
					@SuppressWarnings("deprecation")
					Cursor cursor = ChatActivity.this.managedQuery(mCapturedImageURI, projection, null,
							null, null);
					int column_index_data = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					//THIS IS WHAT YOU WANT!
					filePath = cursor.getString(column_index_data);
					selectedImagePath=filePath;
					// startCropImage1();
					BitmapFactory.Options bmOptions = new BitmapFactory.Options();
					bmOptions.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(filePath, bmOptions);
					int photoW = bmOptions.outWidth;
					int photoH = bmOptions.outHeight;

					// Determine how much to scale down the image
					int scaleFactor = Math.min(photoW/100, photoH/100);
					// Decode the image file into a Bitmap sized to fill the View
					bmOptions.inJustDecodeBounds = false;
					bmOptions.inSampleSize = scaleFactor;
					bmOptions.inPurgeable = true;

					Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
					/*	bitmap = Bitmap.createScaledBitmap(bitmap,
							100, 100, false);*/
					//mImageView.setImageBitmap(bitmap);
					ExifInterface ei = new ExifInterface(selectedImagePath);
					int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

					switch(orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						bitmap=	RotateBitmap(bitmap, 90);
						storeImage(bitmap, "image1");
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						bitmap=	RotateBitmap(bitmap, 180);
						storeImage(bitmap, "image1");
						break;
						// etc.
					}
					//img_Profile.setImageBitmap(bitmap);
					// uploadImage();

					String imageURL = mConnect.AddChatImage(mchatUSer.userID, bitmap);
					System.out.println("Sending message to server.");
					JSONObject jsonObj = new JSONObject();
					try {
						Calendar c = Calendar.getInstance();
						c.setTimeZone(TimeZone.getTimeZone("GMT+0"));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String strDate = sdf.format(c.getTime());
						DateFormat targetFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a"); 
						String formattedDate = targetFormat.format(c.getTime()); 
						jsonObj.put("key", imageURL);	
						jsonObj.put("userId", "1");	
						jsonObj.put("fromId", mConnect.getCurrentUser().userID);
						jsonObj.put("isImage","1");
						jsonObj.put("user_name",mConnect.getCurrentUser().userFullName);
						jsonObj.put("toId",mchatUSer.userID);
						jsonObj.put("devicetoken",mchatUSer.deviceToken);
						jsonObj.put("isdevice",mchatUSer.isdevice);
						jsonObj.put("date",strDate);

						nc.sendDataWithString("__JSON__START__" + jsonObj.toString() + "__JSON__END__");		
						/*db.open();
						db.insertChat(mchatUSer.userID, mConnect.getCurrentUser().userID, imageURL, "0", strDate);
						db.close();*/
						ChatList.add(new ChatModel(imageURL, "",formattedDate));
						adapter.notifyDataSetChanged();
						//chatlistview.setSelection(ChatList.size());
						chatlistview.setSelection(ChatList.size());
						//	mConnect.addchat(mchatUSer.userID, imageURL);
					}catch(JSONException e){

					}
				} catch (OutOfMemoryError e) {
					Toast.makeText(ChatActivity.this, "File path did not found.Please try another file", Toast.LENGTH_SHORT).show();
				}

				catch (NullPointerException e) {
					Toast.makeText(ChatActivity.this, "File path did not found.Please try another file", Toast.LENGTH_SHORT).show();
					//System.out.println("with in camera");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case 1:
			if (resultCode == ChatActivity.this.RESULT_OK) {
				if (imageReturnedIntent != null) {

					Cursor cursor ;
					try{
						Uri imageUri = imageReturnedIntent.getData();
						final String[] cursorColumns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
						// some devices (OS versions return an URI of com.android instead of com.google.android
						if (imageUri.toString().startsWith("content://com.android.gallery3d.provider"))  {
							// use the com.google provider, not the com.android provider.
							imageUri = Uri.parse(imageUri.toString().replace("com.android.gallery3d","com.google.android.gallery3d"));
						}
						cursor = ChatActivity.this.getContentResolver().query(imageUri, cursorColumns, null, null, null);
						if (cursor != null) {
							int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
							cursor.moveToFirst();
							// if it is a picasa image on newer devices with OS 3.0 and up
							if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
								selectedImagePath = downloadImage(imageUri);

							} else if(imageUri.toString().startsWith("content://com.android.sec.gallery3d")){
								selectedImagePath = downloadImage(imageUri);
							}else { // it is a regular local image file
								selectedImagePath = cursor.getString(dataColumnIndex);
							}
							cursor.close();
						} else {
							selectedImagePath = downloadImage(imageUri);
						}
						bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
						// preview
						// image
						/*	bitmap = Bitmap.createScaledBitmap(bitmap,
								100, 100, false);*/
						ExifInterface ei = new ExifInterface(selectedImagePath);
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

						switch(orientation) {
						case ExifInterface.ORIENTATION_ROTATE_90:
							bitmap=	RotateBitmap(bitmap, 90);
							storeImage(bitmap, "image1");
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							bitmap=	RotateBitmap(bitmap, 180);
							storeImage(bitmap, "image1");
							break;
							// etc.
						}
						// bmpDrawable = new BitmapDrawable(bitmapPreview);

						//img_Profile.setImageBitmap(bitmap);
						//   uploadImage();
						String imageURL = mConnect.AddChatImage(mchatUSer.userID, bitmap);
						System.out.println("Sending message to server.");
						JSONObject jsonObj = new JSONObject();
						try {
							Calendar c = Calendar.getInstance();
							c.setTimeZone(TimeZone.getTimeZone("GMT+0"));
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String strDate = sdf.format(c.getTime());
							DateFormat targetFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a"); 
							String formattedDate = targetFormat.format(c.getTime()); 
							jsonObj.put("key", imageURL);	
							jsonObj.put("userId", "1");	
							jsonObj.put("fromId", mConnect.getCurrentUser().userID);
							jsonObj.put("isImage","1");
							jsonObj.put("user_name",mConnect.getCurrentUser().userFullName);
							jsonObj.put("toId",mchatUSer.userID);
							jsonObj.put("devicetoken",mchatUSer.deviceToken);
							jsonObj.put("isdevice",mchatUSer.isdevice);
							jsonObj.put("date",strDate);

							nc.sendDataWithString("__JSON__START__" + jsonObj.toString() + "__JSON__END__");		
							/*db.open();
							db.insertChat(mchatUSer.userID, mConnect.getCurrentUser().userID, imageURL, "0", strDate);
							db.close();*/
							ChatList.add(new ChatModel(imageURL, "",formattedDate));
							adapter.notifyDataSetChanged();
							chatlistview.setSelection(ChatList.size());
							//mConnect.addchat(mchatUSer.userID, imageURL);
						}catch(JSONException e){

						}
					}
					catch(Exception e){
						Toast.makeText(ChatActivity.this, "Please try again",Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(ChatActivity.this, "Cancelled",
							Toast.LENGTH_SHORT).show();
				}
			} else if (resultCode == ChatActivity.this.RESULT_CANCELED) {
				Toast.makeText(ChatActivity.this, "Cancelled",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}

	}
	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	private String downloadImage(Uri imageUri) {

		File cacheDir;
		// if the device has an SD card
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),".OCFL311");
		} else {
			// it does not have an SD card
			cacheDir = ChatActivity.this.getCacheDir();
		}

		if(!cacheDir.exists()) cacheDir.mkdirs();
		File f = new File(cacheDir, "vhybe.jpg");
		try {
			InputStream is = null;
			if (imageUri.toString().startsWith("content://com.google.android.gallery3d")) {
				is=ChatActivity.this.getContentResolver().openInputStream(imageUri);
			} else if(imageUri.toString().startsWith("content://com.android.sec.gallery3d")){
				is=ChatActivity.this.getContentResolver().openInputStream(imageUri);
			}else {
				is=new URL(imageUri.toString()).openStream();
			}
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);

			// OutputStream os = new FileOutputStream(f);
			// Utils.InputToOutputStream(is, os);

			return f.getAbsolutePath();
		} catch (Exception ex) {
			Log.d(this.getClass().getName(), "Exception: " + ex.getMessage());
			// something went wrong
			ex.printStackTrace();
			return null;
		}
	}
	private void storeImage(Bitmap image, String imageName) {
		File pictureFile = getOutputMediaFile(imageName);
		if (pictureFile == null) {
			Log.d("TAG", "Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("TAG", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("TAG", "Error accessing file: " + e.getMessage());
		}
		//expenseImage = null;
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(String fileName) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory() + "/Android/data/"
						+ ChatActivity.this.getApplicationContext().getPackageName()
						+ "/vhybe");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		File mediaFile;
		String mImageName = fileName + ".png";
		String finalPath = mediaStorageDir.getPath() + File.separator
				+ mImageName;
		selectedImagePath=finalPath;
		mediaFile = new File(finalPath);

		return mediaFile;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		nc.disConnectWithServer();
		FriendsFragment.isChatRefresh=true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
	    if (event.getAction() == KeyEvent.ACTION_DOWN) {
	        switch (keyCode) {
	        case KeyEvent.KEYCODE_HOME:
	            // Implement starting Home Activity with Clear Top
	        	nc.disConnectWithServer();
	            return true;
	        }
	    }

	    return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		nc.connectWithServer();
		nc.sendDataWithString("__SUBSCRIBE__"+mchatUSer.channnelId+"__ENDSUBSCRIBE__");		
		super.onRestart();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	/*	new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					DatabaseAdapter db=new DatabaseAdapter(ChatActivity.this);
					Connect mConnect = new Connect(ChatActivity.this);
					db.open();
					String lastSynchDate = db.getlastupdateDate();
					mConnect.getchatDetail(lastSynchDate);
					db.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} 
		}).start();*/

	}
}