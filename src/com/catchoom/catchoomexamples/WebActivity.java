package com.catchoom.catchoomexamples;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity implements OnClickListener {
	private WebView webView1;

	public static final String WEB_ACTIVITY_URL="url";
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		webView1 = (WebView) findViewById(R.id.activity_web);
		webView1.getSettings().setSupportZoom(true);
		webView1.getSettings().setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT > 11)
			webView1.getSettings().setDisplayZoomControls(false);

		webView1.setInitialScale(100);
		webView1.setWebViewClient(new WebViewClient() {

		});

		Bundle bundle = getIntent().getExtras();
		webView1.loadUrl(bundle.getString(WEB_ACTIVITY_URL));
	}

	@Override
	public void onClick(View v) {

	}

}
