package com.example.viewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


@SuppressLint("NewApi") public class RefreshScrollView extends ScrollView {
    public InputMethodManager imm;
    private View contentView;
    private int headerHeight;
    private int downY;
    private int diffY;
    private double downX;
    private boolean isLoadMore = false;// 是否正在加载更多
    private static final int PULLDOWN_STATE = 0;// 下拉刷新状态
    private static final int RELEASE_STATE = 1;// 松开刷新状态
    private static final int REFRESHING_STATE = 2;// 正在刷新状态
    private int current_state = PULLDOWN_STATE;// 当前状态
    private LinearLayout linearLayout;
    private ViewGroup.LayoutParams layoutParams;
    private View header;
    private int headerHeight1;
    private ImageView arrow;
    private AnimationDrawable frameAnim;
    private AnimationSet mAnimationSet;
    private AnimationDrawable pullAnim;
    private ImageView arror_anim;

    public RefreshScrollView(Context context) {
        this(context, null);

    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();


        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);




        //添加下拉头
     
        header = View.inflate(getContext(), R.layout.refresh_header, null);
        mAnimationSet = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_assets);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        pullAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_pull);
        arrow.setBackground(pullAnim);
        arror_anim = (ImageView) header.findViewById(R.id.arror_anim);
        frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_work);
        arror_anim.setBackground(frameAnim);
        pullAnim.start();
        frameAnim.start();
       

        header.measure(0, 0);
        headerHeight = header.getMeasuredHeight();
        header.setPadding(0, -headerHeight, 0, 0);
        linearLayout.addView(header);


        addView(linearLayout);

    }


    private void init() {
        imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }


   
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        contentView = getChildAt(0);
        doOnBorderListener();
        onBorderListener.onMyScrollChanged(x, y, oldx, oldy);
    }


    private void doOnBorderListener() {
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null && !isLoadMore) {
                isLoadMore = true;
                onBorderListener.onLoadingMore();
            }
        } else if (getScrollY() == headerHeight) {
            if (onBorderListener != null) {
                onBorderListener.onRefreshing();
            }
        }
    }

    private OnBorderListener onBorderListener;

    public void getOnBorderListener(OnBorderListener onBorderListener) {
        this.onBorderListener = onBorderListener;
    }

    public interface OnBorderListener {
        // 下拉刷新时回调
        void onRefreshing();

        // 加载更多回调
        void onLoadingMore();

        void onMyScrollChanged(int x, int y, int oldx, int oldy);
    }


    private boolean flag = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                imm.hideSoftInputFromWindow(getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                break;
            case MotionEvent.ACTION_MOVE:

                if (getScrollY() != 0)
                    break;
                if (flag) {
                    downY = (int) ev.getY();
                    flag = false;
                }

                int moveY = (int) ev.getY();
                diffY = moveY - downY;
                if (diffY > 0) {
                    // 下拉刷新头的topPadding值 = 手指移动的距离 - 下拉头的高度
                    int topPadding = diffY - headerHeight;
                    // 根据下拉头布局是否完全展示，设置状态， topPadding = 0 是完全展示
                    if (topPadding >= 0 && current_state != RELEASE_STATE) {
                        current_state = RELEASE_STATE;
                        System.out.println("切换到松开刷新");
                        changeState(current_state);
                    } else if (topPadding < 0 && current_state != PULLDOWN_STATE) {
                        current_state = PULLDOWN_STATE;
                        System.out.println("切换到下拉刷新");
                        changeState(current_state);
                    }
                    Log.i("distance", diffY + "   " + (int) ev.getY() + "    " + topPadding);
                    header.setPadding(0, topPadding, 0, 0);
                    return true;// 自己消费事件
                }

                break;
            case MotionEvent.ACTION_UP:
                downY = -1;
                flag = true;
                if (diffY > 0) {

                    // 手指松开时，根据当前状态判断是否切换到正在刷新
                    if (current_state == PULLDOWN_STATE) {
                        // 直接隐藏头布局
                        header.setPadding(0, -headerHeight, 0, 0);

                    } else if (current_state == RELEASE_STATE) {
                        current_state = REFRESHING_STATE;
                        changeState(current_state);
                        System.out.println("切换到正在刷新");
                        // 让头布局弹到正好完全展示
                        header.setPadding(0, 0, 0, 0);
                        startAnim = true;
                        // 调用外界监听器的具体实现方法
                        if (onBorderListener != null) {
                            onBorderListener.onRefreshing();

                        }
                    }
                    return true;
                } else {

                    break;
                }

        }
        return super.onTouchEvent(ev);
    }

    private void changeState(int currentState) {
        switch (currentState) {
            case PULLDOWN_STATE:
                arror_anim.setVisibility(INVISIBLE);
                arrow.setVisibility(VISIBLE);
                break;
            case RELEASE_STATE:
                arror_anim.setVisibility(INVISIBLE);
                arrow.setVisibility(VISIBLE);
                break;
            case REFRESHING_STATE:
                arror_anim.setVisibility(VISIBLE);
                arrow.setVisibility(INVISIBLE);
                break;

            default:
                break;
        }
    }

    private boolean startAnim = false;

    // 对外提供恢复状态的方法
    public void refreshFinished() {
        if (isLoadMore) {
            isLoadMore = false;
        } else {
            if (startAnim)
            	height=headerHeight;
                startAnimation(mAnimationSet);
            header.setPadding(0, -headerHeight, 0, 0);
            arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.ver4));
            current_state = PULLDOWN_STATE;
        }
    }
    public void autoRefresh()
    {
        current_state = REFRESHING_STATE;
        changeState(current_state);
        System.out.println("切换到正在刷新");
        // 让头布局弹到正好完全展示
        header.setPadding(0, 0, 0, 0);
        startAnim = true;
        // 调用外界监听器的具体实现方法
        if (onBorderListener != null) {
            onBorderListener.onRefreshing();

        }
    }
    int height=headerHeight;
    Runnable runnable =new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				height--;
		
		}
	};

}
