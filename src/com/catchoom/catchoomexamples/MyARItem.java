package com.catchoom.catchoomexamples;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.catchoom.CatchoomARItem;
import com.catchoom.CatchoomSDKException;

public class MyARItem extends CatchoomARItem {

	private Context mContext;
	private String mVideoURL;
	
	public MyARItem(JSONObject object, Context context) throws CatchoomSDKException {
		super(object);
		mContext = context;
		mVideoURL = getVideoURLFromJSON(object);
	}

	
	protected void trackingStarted() {
		super.trackingStarted();
		Log.d("Catchoom Example", "Tracking started for item: " + this.getItemName());
	}
	
	protected void trackingLost() {
		super.trackingLost();
		Log.d("Catchoom Example", "Tracking lost for item: " + this.getItemName());
	
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse(mVideoURL), "video/mp4");
		mContext.startActivity(intent);

	}
	
	private String getVideoURLFromJSON(JSONObject object) {
		// look for the video url in the JSON
		try {
			JSONObject contentMain = object.getJSONObject("item").getJSONObject("content");
			int version = contentMain.getInt("version");
			String contentsTag = "contents";
			if (version == 2) {
				contentsTag = "contents_v2";
			}
			JSONArray contents = contentMain.getJSONArray(contentsTag);
			Log.d("contents", contents.toString());
			for (int i = 0; i < contents.length(); i++) {
				JSONObject obj = contents.getJSONObject(i);
				if (obj.getString("type").equals("video")) {
					return obj.getString("video_url");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
