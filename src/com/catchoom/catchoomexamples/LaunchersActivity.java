package com.catchoom.catchoomexamples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LaunchersActivity extends Activity implements OnClickListener {

	// Howto links
	private TextView mHowToLink;
	private LinearLayout mAboutArProgrammatically;
	private LinearLayout mAboutArFromCraftAR;
	private LinearLayout mAboutRecognitionOnly;
	
	// Launch example links
	private LinearLayout mArProgrammatically;
	private LinearLayout mArFromCraftAR;
	private LinearLayout mRecognitionOnly;
	
	// Bottom links
	private ImageButton mButtonCatchoom;
	private Button mButtonSignUp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launchers);
		
		// Setup howto links
		mHowToLink = (TextView)findViewById(R.id.howto_link);
		mHowToLink.setClickable(true);
		mHowToLink.setOnClickListener(this);
		mAboutArProgrammatically = (LinearLayout)findViewById(R.id.howto_link_ar_programmatically);
		mAboutArProgrammatically.setClickable(true);
		mAboutArProgrammatically.setOnClickListener(this);
		mAboutArFromCraftAR = (LinearLayout)findViewById(R.id.howto_link_ar_from_craftar);
		mAboutArFromCraftAR.setClickable(true);
		mAboutArFromCraftAR.setOnClickListener(this);
		mAboutRecognitionOnly = (LinearLayout)findViewById(R.id.howto_link_recognition_only);
		mAboutRecognitionOnly.setClickable(true);
		mAboutRecognitionOnly.setOnClickListener(this);

		// Setup example links
		mArProgrammatically = (LinearLayout)findViewById(R.id.play_ar_programmatically);
		mArProgrammatically.setClickable(true);
		mArProgrammatically.setOnClickListener(this);
		mArFromCraftAR = (LinearLayout)findViewById(R.id.play_ar_from_craftar);
		mArFromCraftAR.setClickable(true);
		mArFromCraftAR.setOnClickListener(this);
		mRecognitionOnly = (LinearLayout)findViewById(R.id.play_recognition_only);
		mRecognitionOnly.setClickable(true);
		mRecognitionOnly.setOnClickListener(this);
		
		
		// Setup bottom Links
		mButtonCatchoom = (ImageButton)findViewById(R.id.imageButton_logo);
		mButtonCatchoom.setOnClickListener(this);
		mButtonSignUp = (Button)findViewById(R.id.button_signUp);
		mButtonSignUp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		// Clicked on title or howto links
		Intent launchHowto = null;
		if (v == mHowToLink) {
			launchHowto = new Intent(this, HowToActivity.class);
			launchHowto.putExtra(HowToActivity.HOWTO_LAYOUT_EXTRA, R.layout.activity_howto);
		} else if (v == mAboutArProgrammatically) {
			launchHowto = new Intent(this, HowToActivity.class);
			launchHowto.putExtra(HowToActivity.HOWTO_LAYOUT_EXTRA, R.layout.activity_howto_ar_programmatically);
		} else if (v == mAboutArFromCraftAR) {
			launchHowto = new Intent(this, HowToActivity.class);
			launchHowto.putExtra(HowToActivity.HOWTO_LAYOUT_EXTRA, R.layout.activity_howto_ar_from_craftar);
		} else if (v == mAboutRecognitionOnly) {
			launchHowto = new Intent(this, HowToActivity.class);
			launchHowto.putExtra(HowToActivity.HOWTO_LAYOUT_EXTRA, R.layout.activity_howto_recognition_only);
		}
		if (launchHowto != null) {
			startActivity(launchHowto);
			return;
		}
		
		// Clicked on play links
		Intent playExampleIntent = null;
		if (v == mArProgrammatically) {
			playExampleIntent = new Intent(this, ARProgrammaticallyActivity.class);
		} else if (v == mArFromCraftAR) {
			playExampleIntent = new Intent(this, ARFromCraftARActivity.class);
		} else if (v == mRecognitionOnly) {
			playExampleIntent = new Intent(this, RecognitionOnlyActivity.class);
		}
		if (playExampleIntent != null) {
			startActivity(playExampleIntent);
			return;
		}
		
		
		// Clicked on bottom links
		if (v == mButtonCatchoom || v == mButtonSignUp) {
			// mButtonCatchoom
			String url = "http://catchoom.com/product/?utm_source=CraftARExamplesApp&amp;utm_medium=Android&amp;utm_campaign=HelpWithAPI";
			if (v == mButtonSignUp) {
				url = "https://crs.catchoom.com/try-free?utm_source=CraftARExamplesApp&amp;utm_medium=Android&amp;utm_campaign=HelpWithAPI";
			}
			
			Intent launchWebView = new Intent(this, WebActivity.class);
			launchWebView.putExtra(WebActivity.WEB_ACTIVITY_URL, url);
			startActivity(launchWebView);			
			return;
		}
	}

}
