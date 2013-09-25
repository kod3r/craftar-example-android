//
// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// http://github.com/Catchoom/catchoom-example-android/blob/master/LICENSE
//  All warranties and liabilities are disclaimed.
//
package com.catchoom.crsexample.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.catchoom.api.Catchoom;
import com.catchoom.api.CatchoomErrorResponseItem;
import com.catchoom.api.CatchoomResponseHandler;
import com.catchoom.api.CatchoomSearchResponseItem;
import com.catchoom.camera.CatchoomImage;
import com.catchoom.camera.CatchoomImageHandler;
import com.catchoom.camera.CatchoomSingleShotActivity;
import com.catchoom.crsexample.application.CatchoomApplication;
import com.catchoom.crsexample.widget.ScanningBar;
import com.example.crsexampleapp.R;


public class SingleShotActivity extends CatchoomSingleShotActivity implements
		CatchoomResponseHandler, CatchoomImageHandler,OnClickListener {
	private static final String TAG = "CRSExampleApp";
	private FrameLayout mPreview;
	private Button mSnapPhotoButton;

	private Context mContext;
	private Catchoom mCatchoom;
	private CatchoomImageHandler mCatchoomImageHandler;
	private ScanningBar mScanningBarView = null;
	private CatchoomSearchResponseItem mResult = null;
	private FrameLayout mShadowLayout;
	private RelativeLayout mResultView;
	
	private int mButtonState=STATE_START;

	private static final int STATE_START=0;
	private static final int STATE_DONE=2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Make the activity fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.single_shot_camera);
	
	    mContext=(Context)this;
	    mPreview = (FrameLayout) findViewById(R.id.camera_preview);
	    mShadowLayout = (FrameLayout) findViewById(R.id.black_shadow);
	    mResultView = (RelativeLayout) findViewById(R.id.result);
		mCatchoomImageHandler=(CatchoomImageHandler)this;

	    /*
	     * Set the necessary params in the parent class:
	     * 		Call setCameraParams with your context and the FrameLayout of your Activity.
	     * 		NOTE: Your FrameLayout must have layout_width and layout_height set to match_parent
		 */		
		setCameraParams(mContext,mPreview);
		//Tell the parent activity who will receive the takePicture() callback
	    setImageHandler(mCatchoomImageHandler);

		//Set the button to take pictures.
		mSnapPhotoButton= (Button) findViewById(R.id.cameraButton);
  		mSnapPhotoButton.setOnClickListener((OnClickListener)this);
  		//Set the results view click event (go to url)
  		mResultView.setOnClickListener((OnClickListener)this);
  		
		//Create the Catchoom object.
		mCatchoom= new Catchoom();
		mCatchoom.setResponseHandler((CatchoomResponseHandler)this); 
		
	

	}
	//Callback of the takePicture() function when a picture could be taken.
	@Override
	public void requestImageReceived(CatchoomImage image) {
		mCatchoom.search(CatchoomApplication.token, image);		
	}

	//Callback of the takePicture() function when a picture could not be taken due to an error.
	@Override
	public void requestImageError(String error) {
		Toast.makeText(mContext, "Error message received:" + error,
				Toast.LENGTH_SHORT).show();
	}

	//Callback of the Catchoom.search() function when the search was completed succesfully
	@SuppressWarnings("unchecked")
	@Override
	public void requestCompletedResponse(int requestCode, Object responseData) {
		
		ArrayList<CatchoomSearchResponseItem> array = (ArrayList<CatchoomSearchResponseItem>) responseData;
		//Remove the scanning bar
		mPreview.removeView(mScanningBarView);
		
		//Little trick to set alpha transparency in a view in a device with API level <11
		if (Build.VERSION.SDK_INT < 11) {
	        final AlphaAnimation animation = new AlphaAnimation(0.6f, 0.6f);
	        animation.setDuration(0);
	        animation.setFillAfter(true);
	        mShadowLayout.startAnimation(animation);
	    }
		mShadowLayout.setVisibility(View.VISIBLE);
		mResultView.setVisibility(View.VISIBLE);
		mSnapPhotoButton.setVisibility(View.VISIBLE);
		mSnapPhotoButton.bringToFront();
		//The first result is the one with the highest score.
		CatchoomSearchResponseItem bestMatch=null;
		//If the array is not empty, then we have a succesful match
		if(array.size()>0){
			bestMatch= array.get(0);
		}
		updateContent(bestMatch);		
	}

	//Callback of the Catchoom.search() function when an error occurred while searching using the CRS.
	@Override
	public void requestFailedResponse(CatchoomErrorResponseItem responseError) {
		// Notify the error using Toasts.
		if (null == responseError) {
			Toast.makeText(mContext, "Connection error", Toast.LENGTH_SHORT)
					.show();
		} else {
			Log.d(TAG,responseError.getErrorCode() + ": "+ responseError.getErrorPhrase());
			switch (responseError.getErrorCode()) {
			case 401:
			case 403:
				Toast.makeText(mContext, "Invalid token", Toast.LENGTH_SHORT).show();
				break;
			case 400:
			case 500:
			default:
				Toast.makeText(mContext,"The request has failed. Try again later",Toast.LENGTH_SHORT).show();
			}
		}
		//Start again
		mSnapPhotoButton.setVisibility(View.VISIBLE);
		mSnapPhotoButton.bringToFront();
		mPreview.removeView(mScanningBarView);
		restartCamera();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.cameraButton:
				switch(mButtonState){
				case STATE_START:
					scan();
					break;
				case STATE_DONE:
					restartCamera();
				}
				break;
			case R.id.result:
				if(mResult!=null){
					Intent goToWeb = new Intent(Intent.ACTION_VIEW);
					String url = mResult.getMetadata()
							.getString("url");
					if ((null != url)&&(!url.isEmpty())) {
						// Little hack to prevent Uri parser to crash with
						// malformed URLs
						if (!url.matches("https?://.*"))
							url = "http://" + url;
						goToWeb.setData(Uri.parse(url));
						startActivity(goToWeb);
					}
				}
		}
	}
	
	private void scan(){
		//Stop receiving images
		mShadowLayout.setVisibility(View.INVISIBLE);
		mSnapPhotoButton.setVisibility(View.INVISIBLE);
		takePicture();
		//Start the scanning bar
		mScanningBarView= new ScanningBar(mContext,mPreview);
		mScanningBarView.initScan();
		//requestFailedResponse(null);

	}
	private void restartCamera(){
		restartPreview();
		mShadowLayout.setVisibility(View.INVISIBLE);
		if (Build.VERSION.SDK_INT < 11) {
	        mShadowLayout.clearAnimation();
	    }
		mResultView.setVisibility(View.INVISIBLE);
		mSnapPhotoButton.setText(R.string.button_take_picture);
		mButtonState=STATE_START;	
	}
	private void updateContent(CatchoomSearchResponseItem item) {
		TextView itemName = (TextView) findViewById(R.id.itemName);
		ImageView viewport = (ImageView) findViewById(R.id.viewport);
		if(item!=null){
			Bundle metadata= item.getMetadata();
			String name = metadata.getString("name");
			String thumbnailUrl = metadata.getString("thumbnail");
			itemName.setText(name);
			if (null != thumbnailUrl) {
				CatchoomApplication.imageManager.loadImageInView(thumbnailUrl, viewport);
			}	
		
		}else{
			viewport.setImageResource(R.drawable.viewport);
			itemName.setText(R.string.no_match_found);
		}
		mSnapPhotoButton.setText(R.string.button_done);	
		mResult=item;
		mButtonState=STATE_DONE;
	}

}
