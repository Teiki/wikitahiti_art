package fr.wikitahiti.teiki.wikitahiti.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Antoine GALTIER on 2020-02-11.
 */

public class MyPermissionAsker {

	private Activity activity;
	public static int TAG_LOCATIONANDWRITE = 3;
	public static int TAG_WRITE = 1;
	public static int TAG_CAMERA = 2;


	public MyPermissionAsker(Activity activity) {
		this.activity = activity;
	}

	public boolean checkAndAskLocationAndWritePermission(){
		int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
		int permission2Check = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
		int permission3Check = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permission4Check = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck == PackageManager.PERMISSION_GRANTED &&
				permission2Check == PackageManager.PERMISSION_GRANTED &&
				permission3Check == PackageManager.PERMISSION_GRANTED &&
				permission4Check == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		else {
			ActivityCompat.requestPermissions(activity, new String[]{
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE
			}, TAG_LOCATIONANDWRITE);
			return false;
		}
	}


	public  boolean checkAndAskCameraPermission(){
		int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
		if (permissionCheck == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		else {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, TAG_CAMERA);
			return false;
		}
	}


	public  boolean checkAndAskStoragePermission(){
		int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permissionCheck == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		else {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAG_WRITE);
			return false;
		}
	}



}
