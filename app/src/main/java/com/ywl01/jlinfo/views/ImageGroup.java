package com.ywl01.jlinfo.views;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ywl01 on 2017/1/12.
 * 该容器内的图片可上下滑动，实现上下滑动的监听
 */

public class ImageGroup extends LinearLayout {
    public static final int DELETE_IMAGE = 0;
    public static final int DOWNLOAD_IMAGE = 1;

    private ViewDragHelper dragHelper;

    private int beginLeft;
    private int beginTop;
    private float downX, downY;

    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public ImageGroup(Context context) {
        super(context);
        initDragHelper();
    }

    public ImageGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragHelper();
    }

    private void initDragHelper() {
        dragHelper = ViewDragHelper.create(this, 2, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                beginLeft = capturedChild.getLeft();
                beginTop = capturedChild.getTop();
            }

            //限制水平方向移动
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left - dx;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //向上滑出边界，删除图像
                if (-releasedChild.getTop() > releasedChild.getMeasuredHeight()) {
                    if (dragListener != null) {
                        dragListener.onDrag(releasedChild, DELETE_IMAGE);
                    }
                }
                //向下划出边界
                else {
                    if (releasedChild.getTop() > releasedChild.getMeasuredHeight() / 2) {
                        if (dragListener != null) {
                            dragListener.onDrag(releasedChild, DOWNLOAD_IMAGE);
                        }
                    }
                    returnPosition(releasedChild);
                }
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return 1;
            }
        });
    }

    public void returnPosition(View view) {
        dragHelper.smoothSlideViewTo(view, beginLeft, beginTop);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float dx = moveX - downX;
                float dy = moveY - downY;
                if (Math.abs(dx) < Math.abs(dy)) {
                    requestDisallowInterceptTouchEvent(true);
                }
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    public interface DragListener {
        /**
         * @param view   被拖到的view
         * @param action 向上拖动0，向下拖动1
         */
        void onDrag(View view, int action);
    }
}
