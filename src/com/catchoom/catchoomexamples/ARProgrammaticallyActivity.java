// com.catchoom.catchoomexamples is free software. You may use it under the MIT license, which is copied
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

package com.catchoom.catchoomexamples;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.catchoom.CatchoomARItem;
import com.catchoom.CatchoomActivity;
import com.catchoom.CatchoomCamera;
import com.catchoom.CatchoomCameraView;
import com.catchoom.CatchoomCloudRecognition;
import com.catchoom.CatchoomCloudRecognitionError;
import com.catchoom.CatchoomCloudRecognitionItem;
import com.catchoom.CatchoomImage;
import com.catchoom.CatchoomImageHandler;
import com.catchoom.CatchoomResponseHandler;
import com.catchoom.CatchoomSDK;
import com.catchoom.CatchoomTracking;
import com.catchoom.CatchoomTrackingContent;
import com.catchoom.CatchoomTrackingContentImage;

public class ARProgrammaticallyActivity extends CatchoomActivity implements CatchoomResponseHandler,CatchoomImageHandler {

	private final String TAG = "CatchoomTrackingExample";
	private final static String COLLECTION_TOKEN="craftarexamples1";
	
	private View mScanningLayout;
	
	CatchoomCamera mCamera;
	
	CatchoomCloudRecognition mCloudRecognition;
	CatchoomTracking mCatchoomTracking;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public void onPostCreate() {
		
		View mainLayout= (View) getLayoutInflater().inflate(R.layout.activity_ar_programmatically_ar_from_craftar, null);
		CatchoomCameraView cameraView = (CatchoomCameraView) mainLayout.findViewById(R.id.camera_preview);
		super.setCameraView(cameraView);
		setContentView(mainLayout);
		
		mScanningLayout = findViewById(R.id.layout_scanning);
		
		
		//Initialize the SDK. From this SDK, you will be able to retrieve the necessary modules to use the SDK (camera, tracking, and cloud-recgnition)
		CatchoomSDK.init(getApplicationContext(),this);
		
		//Get the camera to be able to do single-shot (if you just use finder-mode, this is not necessary)
		mCamera= CatchoomSDK.getCamera();
		mCamera.setImageHandler(this); //Tell the camera who will receive the image after takePicture()
		
		//Setup the finder-mode: Note! PRESERVE THE ORDER OF THIS CALLS
		mCloudRecognition= CatchoomSDK.getCloudRecognition();//Obtain the cloud recognition module
		mCloudRecognition.setResponseHandler(this); //Tell the cloud recognition who will receive the responses from the cloud
		mCloudRecognition.setCollectionToken(COLLECTION_TOKEN); //Tell the cloud-recognition which token to use from the finder mode
		
		
		
		//Start finder mode
		mCloudRecognition.startFinding();
		
		//Obtain the tracking module
		mCatchoomTracking = CatchoomSDK.getTracking();
		
		mCloudRecognition.connect(COLLECTION_TOKEN);
		
	}
	
	@Override
	public void searchCompleted(ArrayList<CatchoomCloudRecognitionItem> results) {
		if(results.size()!=0){
			CatchoomCloudRecognitionItem item = results.get(0);
			if (item.isAR() && item.getItemName().equals("AR programmatically")) {
				// Stop Finding
				mCloudRecognition.stopFinding();
				
				// Cast the found item to an AR item
				CatchoomARItem myARItem = (CatchoomARItem)item;
				
				// Create an ImageContent from a local image (in raw/res, copied to the sdcard by the SDK)
				String url = (getAppDataDirectory() + "/ar_programmatically_content.png");
				CatchoomTrackingContentImage imageContent = new CatchoomTrackingContentImage(url);
				imageContent.setWrapMode(CatchoomTrackingContent.ContentWrapMode.WRAP_MODE_ASPECT_FIT);
				
				// Add content to the item
				myARItem.addContent(imageContent);
				
				// Add content to the tracking SDK and start AR experience
				mCatchoomTracking.addItem(myARItem);
				mCatchoomTracking.startTracking();
				
				mScanningLayout.setVisibility(View.GONE);
			}
			
		}
	}
	
	@Override
	public void connectCompleted(){
		Log.i(TAG,"Collection token is valid");
	}
	
	@Override
	public void requestFailedResponse(int requestCode,
			CatchoomCloudRecognitionError responseError) {
		Log.d(TAG,"requestFailedResponse");	
		
	}

	//Callback received for SINGLE-SHOT only (after takePicture).
	@Override
	public void requestImageReceived(CatchoomImage image) {
		mCloudRecognition.searchWithImage(COLLECTION_TOKEN,image);
	}
	@Override
	public void requestImageError(String error) {
		//Take picture failed
	}

	

}
