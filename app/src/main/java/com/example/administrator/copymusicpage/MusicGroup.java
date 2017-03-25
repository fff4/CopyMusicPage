package com.example.administrator.copymusicpage;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/3/25.
 */

public class MusicGroup extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private View mRelativeLayout;
    private View mImageView;
    private View mLinearLayout;
    //转盘的位置
    private int mTop;
    private int mLeft;
    private int mMax;


    public MusicGroup(Context context) {
        this(context, null);
    }

    public MusicGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
//        View.inflate(context, R.layout.musci_start, this);

        mViewDragHelper = ViewDragHelper.create(this, mCallback);

        //监听是否完成布局
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //获取最开始是图片的位置
                mLeft = mImageView.getLeft();
                mTop = mImageView.getTop();
                //计算拖拽的最大距离
                mMax = getHeight() - mLinearLayout.getHeight();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //整个布局
        mRelativeLayout = findViewById(R.id.cover);
        //转盘
        mImageView = findViewById(R.id.cricle);
        //下面功能
        mLinearLayout = findViewById(R.id.use);
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //设置只有播放页面可以移动
            return child == mRelativeLayout;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int main = getHeight() - mLinearLayout.getHeight();

            //设置上下滑动区间
            if (top <= 0) {
                top = 0;
            }

            if (top >= main) {
                top = main;
            }

            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            float change = top * 1.0f / mMax;

            //页面向下移动，下面的功能布局向上移动
            mLinearLayout.setTranslationY(-top);

            //对功能布局进行缩放 从1 到 3/5
            mLinearLayout.setScaleX(1 - change * 2 / 5);
            mLinearLayout.setScaleY(1 - change * 2 / 5);
            //设置位移中心点
            mLinearLayout.setPivotX(mLinearLayout.getWidth());

            //缩放转盘图片
            mImageView.setScaleX(1 - change * 3/4);
            mImageView.setScaleY(1 - change * 3/4);

            mImageView.setPivotX(10);
            mImageView.setPivotY(10);

            //转盘图片的位移
            mImageView.setTranslationX(change * - mLeft);
            mImageView.setTranslationY(change * - mTop);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if(mRelativeLayout.getTop() > mMax / 2) {
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0 , mMax);
            }else {
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0 , 0);
            }
            invalidate();
        }
    };

    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
