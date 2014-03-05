package com.catchoom.catchoomexamples;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HowToActivity extends Activity {
	
	public static final String HOWTO_LAYOUT_EXTRA = "howto_layout";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutRes = getIntent().getExtras().getInt(HOWTO_LAYOUT_EXTRA);
		setContentView(layoutRes);
		
		TextView linksView = (TextView)findViewById(R.id.text_with_links_1);
		if (linksView != null) {
			linksView.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}

}
