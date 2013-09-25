//
// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// http://github.com/Catchoom/catchoom-example-android/blob/master/LICENSE
//  All warranties and liabilities are disclaimed.
//
package com.catchoom.crsexample.application;

import android.app.Application;

import com.catchoom.crsexample.util.ImageManager;

public class CatchoomApplication extends Application {

	
	public static final String APP_LOG_TAG = "Catchoom app";
	public static final int CAMERA_TYPE_FINDER=1;
	public static final int CAMERA_TYPE_SINGLE_SHOT=2;
	public static ImageManager imageManager = null;

	//Which Camera will the app use
	public static int CAMERA_USED=CAMERA_TYPE_FINDER;
	//FIXME: Put here your collection token. (Need Help? Go to http://catchoom.com/documentation/where-do-i-get-my-token )
	public static final String token="catchoomcooldemo";
	
	@Override
	public void onCreate() {
		super.onCreate();
		imageManager = new ImageManager();
	}
}
