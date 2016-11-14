package com.planet1107.welike.other;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

public class ChoosePictureUtility {

	private Uri outputFileUri;
	Bitmap pickedImage;
	Activity activity;
	
	public Bitmap getPickedImage() {
		return pickedImage;
	}
	
	public Uri getPickedImageUri() {
		return outputFileUri;
	}
	
	private static ChoosePictureUtility instance = new ChoosePictureUtility();
	
	public static ChoosePictureUtility getInstance() {
		
		return instance;
	}
	
	public static final int SELECT_PHOTO = 345;
	
	public void choosePicture(Activity activity) {
		
		this.activity = activity;
		final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Images" + File.separator);
		root.mkdirs();
		final String fname = getUniqueImageFilename();
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		    // Camera.
		    final List<Intent> cameraIntents = new ArrayList<Intent>();
		    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    final PackageManager packageManager = activity.getPackageManager();
		    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		    for(ResolveInfo res : listCam) {
		        final String packageName = res.activityInfo.packageName;
		        final Intent intent = new Intent(captureIntent);
		        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        intent.setPackage(packageName);
		        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		        cameraIntents.add(intent);
		    }

		    // Filesystem.
		    final Intent galleryIntent = new Intent();
		    galleryIntent.setType("image/*");
		    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		    // Chooser of filesystem options.
		    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		    // Add the camera options.
		    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

		    activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
	}
	
	public String getUniqueImageFilename() {
		
		return "img_"+ System.currentTimeMillis() + ".jpg";
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    
		switch (requestCode) { 
		    case SELECT_PHOTO:
		        if (resultCode == Activity.RESULT_OK) {  
		        	final boolean isCamera;
		            if (data == null) {
		                isCamera = true;
		            } else {
		                final String action = data.getAction();
		                if(action == null) {
		                    isCamera = false;
		                } else {
		                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		                }
		            }
		            if (isCamera) {
			            pickedImage = BitmapFactory.decodeFile(outputFileUri.getPath());
		            } else {
			            Uri selectedImageUri;
		                selectedImageUri = data == null ? null : data.getData();
			            String[] filePathColumn = {MediaStore.Images.Media.DATA};
			            Cursor cursor = activity.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
			            cursor.moveToFirst();
			            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			            String filePath = cursor.getString(columnIndex);
			            outputFileUri = Uri.parse(filePath);
			            cursor.close();
			            pickedImage = BitmapFactory.decodeFile(filePath);
		            }
		        }
                break;
		}
	}
}
