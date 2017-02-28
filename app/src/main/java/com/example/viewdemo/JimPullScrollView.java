package com.example.viewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/1/15 0015.
 */

public class JimPullScrollView extends ScrollView {
    private View contentView;
    private View bottomView;

    private float lastY;

    public JimPullScrollView(Context context) {
        super(context, null);
        init(context);
    }

    public JimPullScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, -1);
        init(context);
    }

    public JimPullScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("jimbray", "scrollview dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("jimbray", "scrollview onInterceptTouchEvent");
        int action = ev.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                int deltaY = (int) (curY - lastY);
                lastY = curY;

                if(deltaY < 0) {
                    if(isScrollToBottom()) {
                        return false;
                    }
                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("jimbray", "scrollview onTouchEvent");
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        contentView.layout(getPaddingLeft(),
                getPaddingTop(),
                getWidth()-getPaddingRight(),
                contentView.getHeight() - (bottomView.getMeasuredHeight()));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        //在这里获取bottomView
        bottomView = ((ViewGroup)contentView).getChildAt(1);
    }

    /*
     * 判断是否划动到了底部
     */
    private boolean isScrollToBottom() {
        return getScrollY() + getHeight() >= computeVerticalScrollRange();
    }

    private int dp2Px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
