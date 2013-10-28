//
// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// http://github.com/Catchoom/catchoom-example-android/blob/master/LICENSE
//  All warranties and liabilities are disclaimed.
//
package com.catchoom.crsexample.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.catchoom.crsexample.application.CatchoomApplication;
import com.example.crsexampleapp.R;

public class MainActivity extends Activity{
	
	public static final String TAG="CRSExample";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// The device does not have a camera available
			Log.e(TAG, "Camera intent aborted due to camera unavailable");
			Toast.makeText(MainActivity.this, "No camera on the device! Finishing activity", Toast.LENGTH_LONG).show();
			finish();
		}	
		Intent cameraIntent;
		switch (CatchoomApplication.CAMERA_USED) {
			case CatchoomApplication.CAMERA_TYPE_SINGLE_SHOT:
				cameraIntent = new Intent(this,SingleShotActivity.class);	
				startActivityForResult(cameraIntent,CatchoomApplication.CAMERA_USED);
				break;
			case CatchoomApplication.CAMERA_TYPE_FINDER:
				cameraIntent = new Intent(this,FinderActivity.class);	
				startActivityForResult(cameraIntent,CatchoomApplication.CAMERA_USED);
				break;
			default:
				Log.e(TAG,"Invalid camera request");
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
}
