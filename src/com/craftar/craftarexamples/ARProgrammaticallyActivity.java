// com.craftar.craftarexamples is free software. You may use it under the MIT license, which is copied
// below and available at http://opensource.org/licenses/MIT
//
// Copyright (c) 2014 Catchoom Technologies S.L.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.craftar.craftarexamples;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.craftar.CraftARActivity;
import com.craftar.CraftARCamera;
import com.craftar.CraftARCameraView;
import com.craftar.CraftARCloudRecognition;
import com.craftar.CraftARCloudRecognitionError;
import com.craftar.CraftARImage;
import com.craftar.CraftARImageHandler;
import com.craftar.CraftARItem;
import com.craftar.CraftARItemAR;
import com.craftar.CraftARResponseHandler;
import com.craftar.CraftARSDK;
import com.craftar.CraftARSDKException;
import com.craftar.CraftARTracking;
import com.craftar.CraftARTrackingContent;
import com.craftar.CraftARTrackingContentImage;

public class ARProgrammaticallyActivity extends CraftARActivity implements CraftARResponseHandler,CraftARImageHandler, OnClickListener {

	private final String TAG = "CraftARTrackingExample";
	private final static String COLLECTION_TOKEN="craftarexamples1";
	
	private View mScanningLayout;
	
	private CraftARItemAR myARItem;
	private boolean isAttached = false;
	private boolean isTrackingEnabled = false;
	
	CraftARCamera mCamera;
	
	CraftARCloudRecognition mCloudRecognition;
	CraftARTracking mCraftARTracking;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public void onPostCreate() {
		
		View mainLayout= (View) getLayoutInflater().inflate(R.layout.activity_ar_programmatically_ar_from_craftar, null);
		CraftARCameraView cameraView = (CraftARCameraView) mainLayout.findViewById(R.id.camera_preview);
		super.setCameraView(cameraView);
		setContentView(mainLayout);
		
		mScanningLayout = findViewById(R.id.layout_scanning);
		
		ImageView attachToScreen = (ImageView)findViewById(R.id.attach_to_screen);
		attachToScreen.setOnClickListener(this);
		
		
		//Initialize the SDK. From this SDK, you will be able to retrieve the necessary modules to use the SDK (camera, tracking, and cloud-recgnition)
		CraftARSDK.init(getApplicationContext(),this);
		
		//Get the camera to be able to do single-shot (if you just use finder-mode, this is not necessary)
		mCamera= CraftARSDK.getCamera();
		mCamera.setImageHandler(this); //Tell the camera who will receive the image after takePicture()
		
		//Setup the finder-mode: Note! PRESERVE THE ORDER OF THIS CALLS
		mCloudRecognition= CraftARSDK.getCloudRecognition();//Obtain the cloud recognition module
		mCloudRecognition.setResponseHandler(this); //Tell the cloud recognition who will receive the responses from the cloud
		mCloudRecognition.setCollectionToken(COLLECTION_TOKEN); //Tell the cloud-recognition which token to use from the finder mode
		
		
		
		//Start finder mode
		mCloudRecognition.startFinding();
		
		//Obtain the tracking module
		mCraftARTracking = CraftARSDK.getTracking();
		
		mCloudRecognition.connect(COLLECTION_TOKEN);
		
	}
	
	@Override
	public void searchCompleted(ArrayList<CraftARItem> results) {
		if(results.size()!=0){
			CraftARItem item = results.get(0);
			if (item.isAR() && item.getItemName().equals("AR programmatically")) {
				// Stop Finding
				mCloudRecognition.stopFinding();
				
				// Cast the found item to an AR item
				myARItem = (CraftARItemAR)item;
				
				// Create an ImageContent from a local image (in raw/res, copied to the sdcard by the SDK)
				String url = (getAppDataDirectory() + "/ar_programmatically_content.png");
				CraftARTrackingContentImage imageContent = new CraftARTrackingContentImage(url);
				imageContent.setWrapMode(CraftARTrackingContent.ContentWrapMode.WRAP_MODE_ASPECT_FIT);
				
				// Add content to the item
				myARItem.addContent(imageContent);
				
				// Add content to the tracking SDK and start AR experience
				try {
					mCraftARTracking.addItem(myARItem);
					mCraftARTracking.startTracking();
					isTrackingEnabled = true;
					mScanningLayout.setVisibility(View.GONE);
				} catch (CraftARSDKException e) {
					//The item could not be added
					e.printStackTrace();
				}
	
			}
			
		}
	}
	
	@Override
	public void connectCompleted(){
		Log.i(TAG,"Collection token is valid");
	}
	
	@Override
	public void requestFailedResponse(int requestCode,
			CraftARCloudRecognitionError responseError) {
		Log.d(TAG,"requestFailedResponse");	
		
	}

	//Callback received for SINGLE-SHOT only (after takePicture).
	@Override
	public void requestImageReceived(CraftARImage image) {
		mCloudRecognition.searchWithImage(COLLECTION_TOKEN,image);
	}
	@Override
	public void requestImageError(String error) {
		//Take picture failed
	}

	@Override
	public void onClick(View v) {
		if (!isAttached && isTrackingEnabled) {
			myARItem.setDrawOffTracking(true);
			mCraftARTracking.stopTracking();
			isAttached = true;
		} else if (myARItem != null){
			myARItem.setDrawOffTracking(false);
			isAttached = false;
			mCraftARTracking.startTracking();
			isTrackingEnabled = true;
			
		}
	}

	

}
