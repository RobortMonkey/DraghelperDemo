package com.example.viewdemo;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


public class SwipeLayoutVertical extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private View mBackView;
    private View mFrontView;
    private int mWidth;
    private int mHeight;
    private int mBackWidth;
    private int paddsize = 30;
    // 提供信息, 接受事件
    private Callback mCallback = new Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {


            return child == mBackView ? false : true;
        }

        //        // 限定水平拖拽范围
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            if (child == mFrontView) {
//                if (left < -mBackWidth) {
//                    left = -mBackWidth;
//                } else if (left > 0) {
//                    left = 0;
//                }
//            } else if (child == mBackView) {
//                if (left < mWidth - mBackWidth) {
//                    left = mWidth - mBackWidth + paddsize;
//                } else if (left > mWidth) {
//                    left = mWidth;
//                }
//            }
//            return left;
//        }
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.i("top", top + " " + "mBackHeight " + mBackHeight);
            if (child == mFrontView) {
                if (top > mBackHeight) {
                    top = mBackHeight;
                } else if (top < 0) {
                    top = 0;
                }
            } else if (child == mBackView) {
                if (top > mBackHeight) {
                    top = 0 + paddsize;
                } else if (top < 0) {
                    top = -mBackHeight;
                }
            }
            Log.i("top", top + " " + "mBackHeight " + mBackHeight);
            return top;
        }

        ;

        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            // 移动某个View的时候, 让另外一个跟着移动
            if (changedView == mFrontView) {
//                mBackView.offsetLeftAndRight(dy);
//                mBackView.offsetTopAndBottom(dy);
            } else if (changedView == mBackView) {
//                mFrontView.offsetLeftAndRight(dy);
//                mFrontView.offsetTopAndBottom(dy);
            }
            dispatchDragState(mFrontView.getTop());
            invalidate();
        }

        ;

        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        ;

        public int getViewVerticalDragRange(View child) {
            return 1;

        }

        ;


        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mFrontView.getTop() > mBackHeight * 0.5f) {
                open();
            } else {
                close();
            }
        }

    };
    private MyScrollView2 scrollView;

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    private float daltX;
    private float downX;
    private float downY;
    private long downTime;
    private int touchSlop;

    public enum State {
        CLOSE, OPEN, DRAGGING
    }

    private State mState = State.CLOSE;


    public interface OnDragStateChangeListener {
        void onClose(SwipeLayoutVertical layout);

        void onOpen(SwipeLayoutVertical layout);

        void onDragging();

        void onStartOpen(SwipeLayoutVertical layout);

        void onStartClose(SwipeLayoutVertical layout);

        void onitemClick(SwipeLayoutVertical layout);
    }

    private OnDragStateChangeListener mOnDragStateChangeListener;

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    public OnDragStateChangeListener getOnDragStateChangeListener() {
        return mOnDragStateChangeListener;
    }

    public void setOnDragStateChangeListener(
            OnDragStateChangeListener onDragStateChangeListener) {
        mOnDragStateChangeListener = onDragStateChangeListener;
    }

    public SwipeLayoutVertical(Context context) {
        this(context, null);
    }

    protected void dispatchDragState(int top) {
        State preState = mState;
        mState = updateState(top);
        if (mOnDragStateChangeListener != null) {
            if (mState != preState) {
                if (mState == State.CLOSE) {
                    mOnDragStateChangeListener.onClose(this);
                } else if (mState == State.OPEN) {
                    mOnDragStateChangeListener.onOpen(this);
                } else if (mState == State.DRAGGING) {
                    if (preState == State.CLOSE) {
                        mOnDragStateChangeListener.onStartOpen(this);
                    } else if (preState == State.OPEN) {
                        mOnDragStateChangeListener.onStartClose(this);
                    }
                }
            } else {
                mOnDragStateChangeListener.onDragging();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.i("touch", "SwipeLayout   dispatchTouchEvent ACTION_DOWN");
                break;

            case MotionEvent.ACTION_MOVE:

                Log.i("touch", "SwipeLayout  dispatchTouchEvent  ACTION_MOVE");
                break;
        }


        return super.dispatchTouchEvent(ev);


    }

    private State updateState(int top) {
        if (top == mBackHeight) {
            return State.OPEN;
        } else if (top == 0) {
            return State.CLOSE;
        } else {
            return State.DRAGGING;
        }
    }

    protected void open() {
        // mFrontView.layout(-mBackWidth, 0, -mBackWidth+mWidth, mHeight);
        // mBackView.layout(mWidth-mBackWidth, 0, mWidth, mHeight);
        open(true);
    }

    public void open(boolean isSmooth) {
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mFrontView, 0, mBackHeight);
            invalidate();
        } else {
            layoutContent(true);
        }
    }

    private void layoutContent(boolean isOpen) {
        // 计算frontView的矩形
        Rect frontRect = computeFrontRect(isOpen);
        // 通过frontRect, 计算backView的矩形
        Rect backRect = computeBackRectFromFront(frontRect);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right,
                frontRect.bottom);
        mBackView.layout(backRect.left, backRect.top, backRect.right,
                backRect.bottom);
    }

    private Rect computeBackRectFromFront(Rect frontRect) {
        return new Rect(frontRect.right, frontRect.top, frontRect.right
                + mBackWidth, frontRect.bottom);
    }

    private Rect computeFrontRect(boolean isOpen) {
//        int left = 0;
//        if (isOpen) {
//            left = -mBackWidth;
//        }
        int top = 0;
        if (isOpen)
            top = mBackHeight;

        return new Rect(0, top, mWidth, mHeight + top);
    }

    protected void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mFrontView, 0, 0);
            invalidate();
        } else {
            layoutContent(false);
        }
    }

    public SwipeLayoutVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayoutVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();


    }

    private boolean mark = false;

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 重新摆放 backView 的位置
        //拉出

        if (mark) {
            mBackView.layout(0, 0, mBackWidth,
                    mBackHeight);
            mFrontView.layout(0, mBackHeight, mWidth, mHeight + mBackHeight);
        } else {

            mBackView.layout(0, -mBackHeight, mWidth, 0);
            mFrontView.layout(0, 0, mWidth, mHeight);
        }

    }

    /**
     * 当 getTop 为 0到 mbackheight 滑动 事件拦截
     * 当 getTop 为>mBackheight 向上滑动事件拦截
     * 向下滑动事件分发
     */
    private boolean mIsUnableToDrag;
    private float mInitialMotionX;
    private float mInitialMotionY;
    float xDistance, yDistance, xLast, yLast;
    private boolean flagTouch = true;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
           if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
        	   mDragHelper.cancel();
        	   Log.i("touch","SwipeLayout  onInterceptTouchEvent  ACTION_UP");
               return false;
           }
//           if (!isEnabled() || (mIsUnableToDrag && action != MotionEvent.ACTION_DOWN)) {
//        	   mDragHelper.cancel();
//               Log.i("touch","SwipeLayout  onInterceptTouchEvent  ACTION_DOWN");
//               return super.onInterceptTouchEvent(ev);
//           }

        int index = MotionEventCompat.getActionIndex(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("event", "ACTION_DOWN");
                final float x = ev.getX();
                final float y = ev.getY();
                mInitialMotionX = x;
                mInitialMotionY = y;
                Log.i("event", getState() + "       " + (mInitialMotionY));
                break;


            case MotionEvent.ACTION_MOVE:
                Log.i("event", "ACTION_MOVE");
                final float x1 = ev.getX();
                final float y1 = ev.getY();
                final float adx = Math.abs(x1 - mInitialMotionX);
                final float ady = Math.abs(y1 - mInitialMotionY);
                int slop = mDragHelper.getTouchSlop();

                // 状态是 close 向下滑动时  拦截
                if ((getState() == State.DRAGGING || getState() == State.CLOSE) && y1 - mInitialMotionY > 0 &&  scrollView.getScrollY() ==0) {
                    Log.i("event", "状态是 close 向下滑动时  拦截   ");
                    return true;
                }


                // 状态是 open 向上滑动时  拦截
                if ((getState() == State.DRAGGING || getState() == State.OPEN) && y1 - mInitialMotionY < 0&&scrollView.getScrollY() ==0) {
                    Log.i("event", "状态是 open 向上滑动时  拦截               ");
                    return true;
                }


                Log.i("event", getState() + "   " + (y1) + "     " + (mInitialMotionY));
                if (scrollView.getScrollY() !=0) {
                    mIsUnableToDrag = true;
                    mDragHelper.cancel();
                    return false;
                }

                break;


        }
        Log.i("event", "onInterceptTouchEvent");

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    // 让ViewDragHelper处理触摸事件
    public boolean onTouchEvent(MotionEvent event) {
        //是否开启侧滑功能
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("touch", "SwipeLayout   onTouchEvent ACTION_MOVE");


                break;
            case MotionEvent.ACTION_UP:
                Log.i("touch", "SwipeLayout   onTouchEvent ACTION_UP");


                break;
        }
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private boolean isClose = false;
    private int mBackHeight;

    public void setIsClose(boolean bool) {
        isClose = bool;
    }

    public void setIsClose() {
        isClose = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackView = getChildAt(0);

        mFrontView = getChildAt(1);

        scrollView = (MyScrollView2) mFrontView.findViewById(R.id.scroll);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mBackWidth = mBackView.getMeasuredWidth();
        mBackHeight = mBackView.getMeasuredHeight();
    }


    /**
     * 获得两点之间的距离
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        return (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
    }


}
