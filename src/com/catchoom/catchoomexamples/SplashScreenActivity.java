package com.catchoom.catchoomexamples;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashScreenActivity extends Activity {

	private static final long SPLASH_SCREEN_DELAY = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);

		TimerTask task = new TimerTask() {
			public void run() {
				Intent launchersActivity = new Intent( SplashScreenActivity.this, LaunchersActivity.class);
				startActivity(launchersActivity);
				finish();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, SPLASH_SCREEN_DELAY);
	}

}
