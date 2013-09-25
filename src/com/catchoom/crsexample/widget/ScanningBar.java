//
// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// http://github.com/Catchoom/catchoom-example-android/blob/master/LICENSE
//  All warranties and liabilities are disclaimed.
//
package com.catchoom.crsexample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class ScanningBar extends View {

    private ObjectAnimator mProgressBar;
    private boolean mIsInitialized=false;
	private FrameLayout mTargetView;
	
    public static int w=20; //Number of pixels of the scanning bar
    public static int period=1000; //Time to go from the top to the bottom of the view.
    public static int initA=255;  //Initial ALPHA value
    public static int initR= 176; //Initial RED   value
    public static int initG=1;    //Initial GREEN value
    public static int initB=36;   //Initial BLUE  value
    public static int endA=0;     //End     ALPHA value
    public static int endR=0;     //End 	RED   value
    public static int endG=0;     //End 	GREEN value
    public static int endB=0;     //End 	BLUE  value
   
    private float x1; //Initial px 'x' coordinate to draw the line
    private float y1; //Initial px 'y' coordinate to draw the line
    private float x2; //Final   px 'x' coordinate to draw the line
    private float y2; //Final   px 'y' coordinate to draw the line
    
    private int bottom;
    private static int dA=(endA-initA)/w;
	private static int dR=(endR-initR)/w;
	private static int dG=(endG-initG)/w;
	private static int dB=(endB-initB)/w;
    private final Paint mPaint;
    private int A,R,G,B;
    public ScanningBar(Context context){
    	super(context);
    	mTargetView=null;
    	mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    public ScanningBar(Context context,FrameLayout targetView) {
    	
        super(context);
        mTargetView=targetView;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
    }
    
    public void setTargetView(FrameLayout targetView){
    	mTargetView=targetView;
    }
 
    public void initScan(){
    	
        if(mIsInitialized){
        	mTargetView.removeView(this);
			//Create the scanning bar object in the top corners of the mPreview
			mTargetView.addView(this);
			//Animate the scanning bar and move it vertically from the top to the bottom of the mPreview
			mProgressBar= ObjectAnimator.ofFloat(this,
			          "translationY", bottom);
			mProgressBar.setDuration(ScanningBar.period);
			//When the scanning bar arrives to the bottom, repeat the animation from the bottom to the top.
			mProgressBar.setRepeatMode(ValueAnimator.REVERSE);
			//Repeat the animation infinite times (until it is cancelled)
			mProgressBar.setRepeatCount(ValueAnimator.INFINITE);
			mProgressBar.start();
		}else{
			int width  = mTargetView.getMeasuredWidth();
			int height = mTargetView.getMeasuredHeight();
			//Ensure that the view has been initialized
			if(width>0&&height>0){
				x1=mTargetView.getLeft();
	    		x2=mTargetView.getRight();
	    		y1=mTargetView.getTop();
	    		y2=mTargetView.getTop();
	    		bottom= mTargetView.getBottom();
				mIsInitialized=true;
			    initScan();
	    	}else{
	    		//Wait until the view is initialized
				ViewTreeObserver vto = mTargetView.getViewTreeObserver(); 
				vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
				    @SuppressWarnings("deprecation")
					@Override 
				    public void onGlobalLayout() {
				    	if(!mIsInitialized){
					        mTargetView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					        int width  = mTargetView.getMeasuredWidth();
					        int height = mTargetView.getMeasuredHeight();
					        //Ensure that the view has been initialized
					        if(width>0&&height>0){
					        	x1=mTargetView.getLeft();
					    		x2=mTargetView.getRight();
					    		y1=mTargetView.getTop();
					    		y2=mTargetView.getTop();
					    		bottom= mTargetView.getBottom();
					        	mIsInitialized=true;
					        	initScan();
					        }
				    	}
				    }
				});
	    	}
		}
    }  
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw every line of the bar, each one with it's corresponding color and alpha.
        for(int i=0;i<w;i++){
	    	A=(int)((initA)+(dA*i));
	    	R=(int)((initR)+(dR*i));
	    	G=(int)((initG)+(dG*i));
	    	B=(int)((initB)+(dB*i));
	    	mPaint.setColor(Color.argb(A,R,G,B));
	        canvas.drawLine(x1, y1+i, x2, y2+i, mPaint);
	    }
     
    }
    public void setName(String propertyName){
    	Log.d("CatchoomScanningBar","Setting property="+propertyName);
    }
   
}
