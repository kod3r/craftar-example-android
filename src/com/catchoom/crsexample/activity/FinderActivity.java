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
import android.view.animation.AlphaAnimation;
import android.view.Window;
import android.view.WindowManager;
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
import com.catchoom.camera.CatchoomFinderActivity;
import com.catchoom.camera.CatchoomImage;
import com.catchoom.camera.CatchoomImageHandler;
import com.catchoom.crsexample.application.CatchoomApplication;
import com.catchoom.crsexample.widget.ScanningBar;
import com.example.crsexampleapp.R;

public class FinderActivity extends CatchoomFinderActivity implements
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
	private static final int STATE_SCANNING=1;
	private static final int STATE_DONE=2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.finder_camera);
	
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
	    setImageHandler(mCatchoomImageHandler);

		//Set the button to take pictures.
		mSnapPhotoButton= (Button) findViewById(R.id.cameraButton);
  		mSnapPhotoButton.setOnClickListener((OnClickListener)this);
  	
		//Create the Catchoom object.
		mCatchoom= new Catchoom();
		mCatchoom.setResponseHandler((CatchoomResponseHandler)this); 
		
		mResultView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
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
			}});
	}	
	@Override
	public void requestImageReceived(CatchoomImage image) {
		mCatchoom.search(CatchoomApplication.token, image);		
	}

	@Override
	public void requestImageError(String error) {
		Toast.makeText(mContext, "Error message received:" + error,
				Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void requestCompletedResponse(int requestCode, Object responseData) {
		
		ArrayList<CatchoomSearchResponseItem> array = (ArrayList<CatchoomSearchResponseItem>) responseData;
		//If we found at least one match, update the content just if the user have not pressed "Stop" before.
		if ((array.size() > 0)&&(mButtonState==STATE_SCANNING)) {
			//Stop receiving images
			stopFinding();
			//Freeze the camera preview
			freezeCameraView();
			//Remove the scanning bar
			mPreview.removeView(mScanningBarView);
			//Little trick to set alpha transparency in a view for device with API level <11
			if (Build.VERSION.SDK_INT < 11) {
		        final AlphaAnimation animation = new AlphaAnimation(0.6f, 0.6f);
		        animation.setDuration(0);
		        animation.setFillAfter(true);
		        mShadowLayout.startAnimation(animation);
		    }
			mShadowLayout.setVisibility(View.VISIBLE);
			mResultView.setVisibility(View.VISIBLE);
			mSnapPhotoButton.bringToFront();
			//The first result is the one with the highest score.
			CatchoomSearchResponseItem bestMatch= array.get(0);
			updateContent(bestMatch);
		}
	}

	@Override
	public void requestFailedResponse(CatchoomErrorResponseItem responseError) {
		// Notify the error using Toasts. Don't finish the Camera Activity.
		if (null == responseError) {
			Toast.makeText(mContext, "Connection error", Toast.LENGTH_SHORT)
					.show();
		} else {
			Log.d(TAG,
					responseError.getErrorCode() + ": "
							+ responseError.getErrorPhrase());
			switch (responseError.getErrorCode()) {
			case 401:
			case 403:
				Toast.makeText(mContext, "Invalid token", Toast.LENGTH_SHORT)
						.show();
				break;
			case 400:
			case 500:
			default:
				Toast.makeText(mContext,
						"The request has failed. Try again later",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//Tell the Catchoom object who  will receive the responses 
		mCatchoom.setResponseHandler((CatchoomResponseHandler) this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.cameraButton:
				switch(mButtonState){
				case STATE_START:
					scan();
					break;
				case STATE_SCANNING:
					cancel();
					break;
				case STATE_DONE:
					restart();
					break;
				}
		}
	}
	private void cancel(){
		stopFinding();
		mPreview.removeView(mScanningBarView);
		mSnapPhotoButton.setText(R.string.button_start_scanning);
		mButtonState=STATE_START;
		//TODO: Should we cancel incoming results if the user pressed stop?
	}
	private void restart(){
		restartPreview();
		mShadowLayout.setVisibility(View.INVISIBLE);
		if (Build.VERSION.SDK_INT < 11) {
	        mShadowLayout.clearAnimation();
	    }
		mResultView.setVisibility(View.INVISIBLE);
		mSnapPhotoButton.setText(R.string.button_start_scanning);
		mButtonState=STATE_START;
	}
	
	private void scan(){
		mShadowLayout.setVisibility(View.INVISIBLE);
		startFinding();
		mScanningBarView= new ScanningBar(mContext,mPreview);
		//Start scanning
		mScanningBarView.initScan();
		mSnapPhotoButton.setText(R.string.button_stop_scanning);
		mButtonState=STATE_SCANNING;
	}
	private void updateContent(CatchoomSearchResponseItem item) {
		if(item!=null){
			TextView itemName = (TextView) findViewById(R.id.itemName);
			ImageView viewport = (ImageView) findViewById(R.id.viewport);
	
			Bundle metadata= item.getMetadata();
			String name = metadata.getString("name");
			String thumbnailUrl = metadata.getString("thumbnail");
			
			itemName.setText(name);
			if (null != thumbnailUrl) {
				CatchoomApplication.imageManager.loadImageInView(thumbnailUrl, viewport);
			}
			mResult=item;
			mSnapPhotoButton.setText(R.string.button_done);
			mButtonState=STATE_DONE;
		}
	}

}
