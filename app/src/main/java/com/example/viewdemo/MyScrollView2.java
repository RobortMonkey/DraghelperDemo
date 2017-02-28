package com.example.viewdemo;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by miduo on 2016/12/22.
 */
public class MyScrollView2 extends ScrollView {

  

  
    public MyScrollView2(Context context) {
        super(context);
    }

    public MyScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	// TODO Auto-generated method stub
    	Log.i("touch","Scroll   dispatchTouchEvent");
    	return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	// TODO Auto-generated method stub
    	switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
          
            Log.i("touch","Scroll   dispatchTouchEvent ACTION_DOWN");
            break;

        case MotionEvent.ACTION_MOVE:
            
            Log.i("touch","Scroll  dispatchTouchEvent  ACTION_MOVE");
            break;
    	}
          
    	return super.onInterceptTouchEvent(ev);
    }
    private float xDistance, yDistance, xLast, yLast;
    public boolean onTouchEvent(MotionEvent ev) {
    	switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            xDistance = yDistance = 0f;
            xLast = ev.getX();
            yLast = ev.getY();
            Log.i("touch","Scroll   onTouchEvent ACTION_DOWN");
            break;

        case MotionEvent.ACTION_MOVE:
            final float curX = ev.getX();
            final float curY = ev.getY();
            Log.i("touch","Scroll   onTouchEvent ACTION_MOVE");
            xDistance += Math.abs(curX - xLast);
            yDistance += Math.abs(curY - yLast);
            xLast = curX;
            yLast = curY;

            Log.i("move","getSrollYValue()    "+ getSrollYValue());
            if(xDistance > yDistance&&curY - yLast>0&&getSrollYValue()== 0){
                return false;
            }
    };
    return super.onTouchEvent(ev);
    }
    public int getSrollYValue()
    {
    	return getScrollY();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	// TODO Auto-generated method stub
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
	

}
