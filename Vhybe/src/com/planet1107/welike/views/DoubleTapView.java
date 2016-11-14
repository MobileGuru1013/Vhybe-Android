package com.planet1107.welike.views;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DoubleTapView extends ImageView {
	 
    private class MyGestureListener extends
        GestureDetector.SimpleOnGestureListener {
 
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(TAG, "onDoubleTap");
        return true;
    }
 
    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }
    }
 
    private final static String TAG = DoubleTapView.class.getName();
    private GestureDetectorCompat mDetector;
 
    public DoubleTapView(Context context, AttributeSet attrs) {
    super(context, attrs);
 
    mDetector = new GestureDetectorCompat(context, new MyGestureListener());
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    return mDetector.onTouchEvent(e);
    }
 
}
