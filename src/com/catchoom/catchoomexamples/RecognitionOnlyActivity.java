package com.catchoom.catchoomexamples;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

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

public class RecognitionOnlyActivity extends CatchoomActivity implements CatchoomResponseHandler,CatchoomImageHandler, OnClickListener {

	private final String TAG = "CatchoomTrackingExample";
	private final static String COLLECTION_TOKEN="craftarexamples1";

	private View mScanningLayout;
	private View mTapToScanLayout;
	
	CatchoomCamera mCamera;
	
	CatchoomCloudRecognition mCloudRecognition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public void onPostCreate() {
		
		View mainLayout= (View) getLayoutInflater().inflate(R.layout.activity_recognition_only, null);
		CatchoomCameraView cameraView = (CatchoomCameraView) mainLayout.findViewById(R.id.camera_preview);
		super.setCameraView(cameraView);
		setContentView(mainLayout);
		
		mScanningLayout = findViewById(R.id.layout_scanning);
		mTapToScanLayout = findViewById(R.id.tap_to_scan);
		mTapToScanLayout.setClickable(true);
		mTapToScanLayout.setOnClickListener(this);
		
		
		//Initialize the SDK. From this SDK, you will be able to retrieve the necessary modules to use the SDK (camera, tracking, and cloud-recgnition)
		CatchoomSDK.init(getApplicationContext(),this);
		
		//Get the camera to be able to do single-shot (if you just use finder-mode, this is not necessary)
		mCamera= CatchoomSDK.getOFCamera();
		mCamera.setImageHandler(this); //Tell the camera who will receive the image after takePicture()
		
		//Setup cloud recognition
		mCloudRecognition= CatchoomSDK.getCloudRecognition();//Obtain the cloud recognition module
		mCloudRecognition.setResponseHandler(this); //Tell the cloud recognition who will receive the responses from the cloud
		mCloudRecognition.setCollectionToken(COLLECTION_TOKEN); //Tell the cloud-recognition which token to use from the finder mode
		
		
		mCloudRecognition.connect(COLLECTION_TOKEN);
		
	}
	
	@Override
	public void searchCompleted(ArrayList<CatchoomCloudRecognitionItem> results) {
		mScanningLayout.setVisibility(View.GONE);
		if(results.size()==0){
			Log.d(TAG,"Nothing found");
		}else{
			CatchoomCloudRecognitionItem item = results.get(0);
			if (!item.isAR()) {
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
				startActivity(launchBrowser);
				mTapToScanLayout.setVisibility(View.VISIBLE);
				mCamera.restartCameraPreview();
				return;
			}
		}
		Toast.makeText(getBaseContext(),getString(R.string.recognition_only_toast_nothing_found), Toast.LENGTH_SHORT).show();
		mTapToScanLayout.setVisibility(View.VISIBLE);
		mCamera.restartCameraPreview();
	}
	
	@Override
	public void connectCompleted(){
		Log.i(TAG,"Collection token is valid");
	}
	
	@Override
	public void requestFailedResponse(int requestCode,
			CatchoomCloudRecognitionError responseError) {
		Log.d(TAG,"requestFailedResponse");	
		Toast.makeText(getBaseContext(),getString(R.string.recognition_only_toast_nothing_found), Toast.LENGTH_SHORT).show();
		mScanningLayout.setVisibility(View.GONE);
		mTapToScanLayout.setVisibility(View.VISIBLE);
		mCamera.restartCameraPreview();
		
	}

	//Callback received for SINGLE-SHOT only (after takePicture).
	@Override
	public void requestImageReceived(CatchoomImage image) {
		mCloudRecognition.searchWithImage(COLLECTION_TOKEN,image);
	}
	@Override
	public void requestImageError(String error) {
		//Take picture failed
		Toast.makeText(getBaseContext(),getString(R.string.recognition_only_toast_picture_error), Toast.LENGTH_SHORT).show();
		mScanningLayout.setVisibility(View.GONE);
		mTapToScanLayout.setVisibility(View.VISIBLE);
		mCamera.restartCameraPreview();
	}

	@Override
	public void onClick(View v) {
		if (v == mTapToScanLayout) {
			mTapToScanLayout.setVisibility(View.GONE);
			mScanningLayout.setVisibility(View.VISIBLE);
			mCamera.takePicture();
		}
	}

	

}
