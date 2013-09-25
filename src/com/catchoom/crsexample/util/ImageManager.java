// (c) Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-crc/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.crsexample.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.catchoom.crsexample.application.CatchoomApplication;
import com.example.crsexampleapp.R;

public class ImageManager extends Handler {

	private static final int MAX_EXECUTOR_THREADS = 5;
	private final HashMap<String, Drawable> imagesMap;
	private final ExecutorService executor;
	private WeakHashMap<ImageView, Future<LoadImageInView>> loadImageQueue;
	
	public ImageManager() {
		imagesMap = new HashMap<String, Drawable>();
		executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
		loadImageQueue = new WeakHashMap<ImageView, Future<LoadImageInView>>();
	}

	public void loadImageInView(String urlString,ImageView imageView) {
    	if (!loadImageQueue.containsKey(imageView)) {
    	 	imageView.setImageResource(R.drawable.viewport);
    		Future<LoadImageInView> f = (Future<LoadImageInView>) executor.submit(new LoadImageInView(imageView, urlString));
    		loadImageQueue.put(imageView, f);
    	}
    }
	private Drawable getDrawableFromURL(String url) {
        if (imagesMap.containsKey(url)) {
            return imagesMap.get(url);
        }

        try {
        	InputStream is = (InputStream) new URL(url).getContent();
            Drawable drawable = Drawable.createFromStream(is, url);

            if (drawable != null) {
            	imagesMap.put(url, drawable);
            } else {
            	Log.e(CatchoomApplication.APP_LOG_TAG, "Error downloading thumbnail from " + url);
            }

            return drawable;
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        return null;
    }
	
    @Override
	public void handleMessage(Message message) {
    	Pair<ImageView, Drawable> imageData = (Pair<ImageView, Drawable>) message.obj;
    	if (null != imageData) {
    		ImageView placeHolder = imageData.first;
    		try{
    			placeHolder.setImageDrawable(imageData.second);
    			loadImageQueue.remove(imageData.first);
    		}catch(Exception e){
    			//Sometimes we got an exception with the device rotation at this point.
    			e.printStackTrace();
    		}
    		
    	}
    }
    
    private class LoadImageInView implements Runnable {
    	
    	private WeakReference<ImageView> mImageViewReference;
    	private String mImageUrl;
    	
    	public LoadImageInView(ImageView imageView, String imageUrl) {
    		mImageViewReference = new WeakReference<ImageView>(imageView);
    		mImageUrl = imageUrl;
    	}

		@Override
		public void run() {
			Drawable drawable = getDrawableFromURL(mImageUrl);
            Pair<ImageView, Drawable> imageData = new Pair<ImageView, Drawable>(mImageViewReference.get(), drawable);
            Message message = obtainMessage(1, imageData);
            sendMessage(message);
		}
    	
    }
}
