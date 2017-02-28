
package com.example.viewdemo;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


public class SwipeLayoutHorizontal extends FrameLayout{

    private ViewDragHelper mDragHelper;
    private View mBackView;
    private View mFrontView;
    private int mWidth;
    private int mHeight;
    private int mBackWidth;
    private int paddsize = 30;
    // 鎻愪緵淇℃伅, 鎺ュ彈浜嬩欢
    private Callback mCallback = new Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // 闄愬畾姘村钩鎷栨嫿鑼冨洿
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mFrontView) {
                if (left < -mBackWidth) {
                    left = -mBackWidth;
                } else if (left > 0) {
                    left = 0;
                }
            } else if (child == mBackView) {
                if (left < mWidth - mBackWidth) {
                    left = mWidth - mBackWidth + paddsize;
                } else if (left > mWidth) {
                    left = mWidth;
                }
            }
            return left;
        }

        ;

        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            // 绉诲姩鏌愪釜View鐨勬椂鍊�, 璁╁彟澶栦竴涓窡鐫�绉诲姩
            if (changedView == mFrontView) {
                mBackView.offsetLeftAndRight(dx);
            } else if (changedView == mBackView) {
                mFrontView.offsetLeftAndRight(dx);
            }
            dispatchDragState(mFrontView.getLeft());
            invalidate();
        }

        ;

        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getWidth();
        }

        ;

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel == 0.0f && mFrontView.getLeft() < -mBackWidth * 0.5f) {
                open();
            } else if (xvel < 0) {
                open();
            } else {
                close();
            }
        }

        ;
    };
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
        void onClose(SwipeLayoutHorizontal layout);

        void onOpen(SwipeLayoutHorizontal layout);

        void onDragging();

        void onStartOpen(SwipeLayoutHorizontal layout);

        void onStartClose(SwipeLayoutHorizontal layout);

        void onitemClick(SwipeLayoutHorizontal layout);
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

    public SwipeLayoutHorizontal(Context context) {
        this(context, null);
    }

    protected void dispatchDragState(int left) {
        State preState = mState;
        mState = updateState(left);
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

    private State updateState(int left) {
        if (left == -mBackWidth) {
            return State.OPEN;
        } else if (left == 0) {
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
            mDragHelper.smoothSlideViewTo(mFrontView, -mBackWidth, 0);
            invalidate();
        } else {
            layoutContent(true);
        }
    }

    private void layoutContent(boolean isOpen) {
        // 璁＄畻frontView鐨勭煩褰�
        Rect frontRect = computeFrontRect(isOpen);
        // 閫氳繃frontRect, 璁＄畻backView鐨勭煩褰�
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
        int left = 0;
        if (isOpen) {
            left = -mBackWidth;
        }
        return new Rect(left, 0, left + mWidth, mHeight);
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

    public SwipeLayoutHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayoutHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this,10f, mCallback);
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
        // 閲嶆柊鎽嗘斁 backView 鐨勪綅缃�
        if (mark) {
            mBackView.layout(mWidth - mBackWidth, 0, mWidth + mBackWidth,
                    mHeight);
            mFrontView.layout(-mBackWidth, 0, mWidth + mBackWidth, mHeight);
        } else {
            mBackView.layout(mWidth, 0, mWidth + mBackWidth, mHeight);
            mFrontView.layout(0, 0, mWidth + mBackWidth, mHeight);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    // 璁￢iewDragHelper澶勭悊瑙︽懜浜嬩欢
    public boolean onTouchEvent(MotionEvent event) {
        //鏄惁寮�鍚晶婊戝姛鑳�

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                // 璁板綍鎸変笅鐨勬椂闂�
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();

                // 姘村钩婊戝姩,涓嶈listView鎷︽埅浜嬩欢
                if (Math.abs(moveY - downY) < Math.abs(moveX - downX)) {
                    // 璇锋眰鐖禫iew涓嶆嫤鎴簨浠�
                    requestDisallowInterceptTouchEvent(true);
                }
                Log.i("move","move x  "+moveX+" moveY "+moveY);

                break;
            case MotionEvent.ACTION_UP:
                // 璁板綍鎶捣鐨勬椂闂寸偣
                long upTime = System.currentTimeMillis();
                // 璁＄畻鎶捣鐨勫潗鏍�
                float upX = event.getX();
                float upY = event.getY();
                // 璁＄畻鎸変笅鍜屾姮璧风殑鏃堕棿宸�
                long touchDuration = upTime - downTime;
                // 璁＄畻鎸変笅鐐瑰拰鎶捣鐐圭殑璺濈
                float touchD = getDistanceBetween2Points(new PointF(downX, downY), new PointF(upX, upY));

                // 妯℃嫙鐐瑰嚮浜嬩欢
                if (touchDuration < 400 && touchD < touchSlop) {
                    // 鎵撳紑鐘舵�佸垯鍏抽棴锛屽惁鍒欐墽琛岀偣鍑讳簨浠�
                    if (mOnDragStateChangeListener != null) {
                        mOnDragStateChangeListener.onitemClick(this);
                    }
                }
                break;
        }
        if (isClose) {
            return true;
        } else {
            mDragHelper.processTouchEvent(event);
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private boolean isClose = false;

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mBackWidth = mBackView.getMeasuredWidth();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
            // invalidate();
        }
    }

    /**
     * 鑾峰緱涓ょ偣涔嬮棿鐨勮窛绂�
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        return (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
    }


}
