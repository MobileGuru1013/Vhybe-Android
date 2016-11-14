package com.planet1107.welike.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PostGridItem extends ImageView {

	public PostGridItem(Context context) {
		
		super(context);
	}

	public PostGridItem(Context context, AttributeSet attrs) {	    
		super(context, attrs);		
	}
	
	public PostGridItem(Context context, AttributeSet attrs, int defStyleAttr)  {
		 
		super(context, attrs, defStyleAttr);
	}
	
	@Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        
		final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
		
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        
    	super.onSizeChanged(w, w, oldw, oldh);
    }
}
