package com.example.viewdemo;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class RefreshListView extends ListView {
    private int headerHeight;
    private View header;
    private int downY = -1;
    private static final int PULLDOWN_STATE = 0;
    private static final int RELEASE_STATE = 1;
    private static final int REFRESHING_STATE = 2;
    private int current_state = PULLDOWN_STATE;
    private ImageView arrow;
    private MyOnRefreshListener mListener;
    private View footer;
    private int footerHeight;
    private boolean isLoadMore = false;
    private TextView tv_footer;
    //    private ProgressBar pb_footer;
    private AnimationDrawable frameAnim;
    private int downX;
    private int diffY;
    private ImageView arror_anim;
    private AnimationDrawable pullAnim;
    private OnItemClickListener onItemClickListener;

    private final static int OFFSET_RADIO = 2;

    
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeader();
        initFooter();
        this.setOnScrollListener(new MyOnScrollListener());

    }


//    private void initAnimation() {
//        up = new TranslateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        up.setDuration(500);
//        up.setFillAfter(true);
//
//        down = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        down.setDuration(500);
//        down.setFillAfter(true);
//    }

    private void initFooter() {
        footer = View.inflate(getContext(), R.layout.footer_layout1, null);
        tv_footer = (TextView) footer.findViewById(R.id.tv);
//        pb_footer = (ProgressBar) footer.findViewById(R.id.pb_footer);
        footer.measure(0, 0);
        footerHeight = footer.getMeasuredHeight();
//        footerHeight = (int) getResources().getDimension(R.dimen.px2dp_80);
        footer.setPadding(0, 0, 0, 0);
        this.addFooterView(footer, null, false);

    }
    

    private void initHeader() {
        header = View.inflate(getContext(), R.layout.listview_header, null);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        pullAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_pull);
        arrow.setBackgroundDrawable(pullAnim);
        pullAnim.start();
        arror_anim = (ImageView) header.findViewById(R.id.arror_anim);
        frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_work);
        arror_anim.setBackgroundDrawable(frameAnim);
        frameAnim.start();

        // onMease > onLyaout > onDraw
        // 娴嬮噺澶村竷灞�
        header.measure(0, 0);// 璁╂鏋跺府鎴戜滑鑷姩娴嬮噺鎺т欢鐨勫楂�
//        // 鑾峰彇娴嬮噺鐨勫楂橈紝蹇呴』鎻愬墠璋冪敤measures鏂规硶
        headerHeight = header.getMeasuredHeight();
//        headerHeight = (int) getResources().getDimension(R.dimen.px2dp_100);
        // 闅愯棌澶村竷灞�
        header.setPadding(0, -headerHeight, 0, 0);
        // 鎶婁笅鎷夊埛鏂板ご甯冨眬娣诲姞鍒癓istview鐨勫ご涓�
        this.addHeaderView(header, null, false);
    }

    private boolean flag = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//         System.out.println("onTouchEvent:" + ev.getAction());
//         System.out.println("绗竴涓彲瑙佺殑浣嶇疆:" + getFirstVisiblePosition());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                downX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:

                if (getFirstVisiblePosition() != 0)
                    break;
                if (flag) {
                    downY = (int) ev.getY();
                    flag = false;
                }

                int moveY = (int) ev.getY();
                int moveX = (int) ev.getX();
                // 鎵嬫寚绉诲姩鐨勮窛绂�
                diffY = moveY - downY;
                int diffX = moveX - downX;


                if (diffY > 0) {
                    // 涓嬫媺鍒锋柊澶寸殑topPadding鍊� = 鎵嬫寚绉诲姩鐨勮窛绂� - 涓嬫媺澶寸殑楂樺害
                    int topPadding = (diffY - headerHeight) / OFFSET_RADIO;
                    // 鏍规嵁涓嬫媺澶村竷灞�鏄惁瀹屽叏灞曠ず锛岃缃姸鎬侊紝 topPadding = 0 鏄畬鍏ㄥ睍绀�
                    if (topPadding >= 0 && current_state != RELEASE_STATE) {
                        current_state = RELEASE_STATE;
                        System.out.println("鍒囨崲鍒版澗寮�鍒锋柊");
                        changeState(current_state);
                    } else if (topPadding < 0 && current_state != PULLDOWN_STATE) {
                        current_state = PULLDOWN_STATE;
                        System.out.println("鍒囨崲鍒颁笅鎷夊埛鏂�");
                        changeState(current_state);

                    }
                        header.setPadding(0, topPadding, 0, 0);
//                    Log.i("topPadding", "topPadding    " + topPadding + "   headerHeight  "
//                            + headerHeight + "  moveY  " + moveY + "   downY " + downY + "  diffY  " + diffY);
                    return true;// 鑷繁娑堣垂浜嬩欢
                }
                break;
            case MotionEvent.ACTION_UP:
                flag = true;

                if (diffY > 0) {

                    // 鎵嬫寚鏉惧紑鏃讹紝鏍规嵁褰撳墠鐘舵�佸垽鏂槸鍚﹀垏鎹㈠埌姝ｅ湪鍒锋柊
                    if (current_state == PULLDOWN_STATE) {
                        // 鐩存帴闅愯棌澶村竷灞�

                        header.setPadding(0, -headerHeight, 0, 0);
                        if (onItemClickListener != null && getOnItemClickListener() == null)
                            setOnItemClickListener(onItemClickListener);
                    } else if (current_state == RELEASE_STATE) {
                        current_state = REFRESHING_STATE;
                        changeState(current_state);
                        System.out.println("鍒囨崲鍒版鍦ㄥ埛鏂�");
                        // 璁╁ご甯冨眬寮瑰埌姝ｅソ瀹屽叏灞曠ず
                        header.setPadding(0, 0, 0, 0);
                        // 璋冪敤澶栫晫鐩戝惉鍣ㄧ殑鍏蜂綋瀹炵幇鏂规硶
                        if (mListener != null) {
                            mListener.onRefreshing();
                            if (getOnItemClickListener() != null) {
                                onItemClickListener = getOnItemClickListener();
                                setOnItemClickListener(null);
                            }
                        }
                    }
                } else {
                    if (onItemClickListener != null && getOnItemClickListener() == null)
                        setOnItemClickListener(onItemClickListener);
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


    // 瀵瑰鏆撮湶鎺ュ彛锛岃澶栫晫瀹炵幇涓嬫媺鍒锋柊鐨勪笟鍔�
    public interface MyOnRefreshListener {
        // 涓嬫媺鍒锋柊鏃跺洖璋�
        void onRefreshing();

        // 鍔犺浇鏇村鍥炶皟
        void onLoadingMore();

        boolean hasMore();
    }

    public void setMyOnRefreshListener(MyOnRefreshListener listener) {
        this.mListener = listener;
    }


    // 瀵瑰鎻愪緵鎭㈠鐘舵�佺殑鏂规硶
    public void refreshFinished() {

        if (isLoadMore) {

            if (!mListener.hasMore()) {
                tv_footer.setText("宸插姞杞藉叏閮ㄦ暟鎹�...");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        tv_footer.setText("");
                    }
                }, 500);
            }
            isLoadMore = false;
        } else {
            header.setPadding(0, -headerHeight, 0, 0);
            footer.setVisibility(View.INVISIBLE);
//            arrow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ver4));
            current_state = PULLDOWN_STATE;
        }
    }

    class MyOnScrollListener implements OnScrollListener {

        // 婊戝姩鐘舵�佸彂鐢熷彉鍖栨椂璋冪敤
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 鍒ゆ柇褰撳墠鐨勫仠姝㈢姸鎬佸拰鎯�у仠姝㈢姸鎬�

            if (OnScrollListener.SCROLL_STATE_IDLE == scrollState
                    || OnScrollListener.SCROLL_STATE_FLING == scrollState) {
                // 鍒ゆ柇褰撳墠Listview鏄剧ず鐨勬渶鍚庝竴鏉℃暟鎹槸鍚︽槸Adapter涓渶鍚庝竴鏉�
                if (getLastVisiblePosition() == getCount() - 1 && !isLoadMore && current_state != REFRESHING_STATE) {
                    isLoadMore = true;
                    // 鍔犺浇鏇村
                    tv_footer.setText("鍔犺浇涓�. . .");
//                    pb_footer.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    footer.setPadding(0, 0, 0, 0);
                    // 璁╁姞杞芥洿澶氬竷灞�鑷姩鏄剧ず鍑烘潵
                    setSelection(getCount());
                    // 鍥炶皟澶栫晫鍔犺浇鏇村鐨勫叿浣撳疄鐜�
                    if (mListener != null) {
                        mListener.onLoadingMore();
                    }
                }
            }
        }

        // 褰撴粦鍔ㄦ椂璋冪敤
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }

    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private boolean isAnimationRunning = false;

    private TranslateAnimation refreshAnimation(int fromeYDelta) {
        TranslateAnimation animation = new TranslateAnimation(0, 0,0, -headerHeight);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                header.setPadding(0, -headerHeight, 0, 0);
                isAnimationRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }
}
