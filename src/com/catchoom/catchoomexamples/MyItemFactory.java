package com.catchoom.catchoomexamples;

import org.json.JSONObject;

import android.content.Context;

import com.catchoom.CatchoomCloudRecognitionItem;
import com.catchoom.CatchoomCloudRecognitionItemFactory;
import com.catchoom.CatchoomSDKException;

public class MyItemFactory extends CatchoomCloudRecognitionItemFactory {
	
	private Context mContext;
	
	public MyItemFactory(Context context) {
		mContext = context;
	}

	public CatchoomCloudRecognitionItem itemFromJSONObject(JSONObject object) throws CatchoomSDKException {
		int itemType = CatchoomCloudRecognitionItem.getItemTypeFromJSONObject(object);
		switch (itemType) {
		case CatchoomCloudRecognitionItem.ITEM_TYPE_RECOGNITION_ONLY:
			return new CatchoomCloudRecognitionItem(object);
		case CatchoomCloudRecognitionItem.ITEM_TYPE_AR:
			return new MyARItem(object, mContext);
		default:
			return null;
		}
	}

}