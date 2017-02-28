package com.example.viewdemo;


import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/1/15 0015.
 */

public class JimPullBottomView extends LinearLayout {
    private ViewDragHelper mDragViewHelper;

    private View mDragView;
    private View bottomView;

    private Point mDragViewOri = new Point();

    public JimPullBottomView(Context context) {
        super(context, null);
        init(context);
    }

    public JimPullBottomView(Context context, AttributeSet attrs) {
        super(context, attrs, -1);
        init(context);
    }

    public JimPullBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDragViewHelper = ViewDragHelper.create(this, 1.0f, mDragHelperCallback);
    }

    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDragView;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            int topBound = -1 * bottomView.getMeasuredHeight();

            int bottomBound = getHeight() - mDragView.getMeasuredHeight();

            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            // 左边限定为 100
            int leftBound = 100;
            // 右边限定为 400的位置，由于要使用 left 进行布局,所以需要减去mDragView的宽度
            int rightBound = 400 - mDragView.getWidth();

            return Math.min(Math.max(leftBound, left), rightBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if(releasedChild == mDragView) {
                mDragViewHelper.settleCapturedViewAt(mDragViewOri.x, mDragViewOri.y);
                postInvalidate();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(changedView == mDragView) {
                bottomView.layout(bottomView.getLeft(), top + mDragView.getHeight(), bottomView.getRight(), top+mDragView.getHeight() + bottomView.getMeasuredHeight());
            }
            postInvalidate();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight()-child.getMeasuredHeight();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth()-child.getMeasuredWidth();
        }
    };

    @Override
    public void computeScroll() {
        if(mDragViewHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mDragView.layout(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), mDragView.getMeasuredHeight());
        bottomView.layout(getPaddingLeft(), mDragView.getBottom(), getWidth() - getPaddingRight(), mDragView.getBottom() + bottomView.getMeasuredHeight());

        mDragViewOri.x = mDragView.getLeft();
        mDragViewOri.y = mDragView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = getChildAt(0);
        bottomView = getChildAt(1);
    }

    /**
     * 事件分发
     * @param ev
     * @return
     * 可以根据具体需求决定如何分发事件
    × 这里不做处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w("jimbray", "dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 事件拦截
     * @param ev
     * @return
     * 直接将所有的事件都交由 ViewDragHelper 去处理
     * 可以根据具体需求决定是否交付事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.w("jimbray", "onInterceptTouchEvent");
        return mDragViewHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 事件消费
     * @param event
     * @return
     * 直接将所有的事件都交由 ViewDragHelper 去处理
     * 可以根据具体需求决定是否交付事件
     * 交付事件后直接 return true，结束事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w("jimbray", "onTouchEvent");
        mDragViewHelper.processTouchEvent(event);
        return true;
    }

    private int dp2Px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
