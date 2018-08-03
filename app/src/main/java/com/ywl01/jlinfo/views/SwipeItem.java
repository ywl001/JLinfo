package com.ywl01.jlinfo.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ywl01.jlinfo.events.TypeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ywl01 on 2016/12/26.
 */

public class SwipeItem extends FrameLayout {
    private ViewDragHelper dragHelper;
    private View contentView;
    private View menuView;
    private int contentWidth;
    private int menuWidth;
    private int contentHeight;
    private SwipeState currentState;

    private float downX,downY;

    private OnSwipeStateChangeListener listener;

    public enum SwipeState{
        open,close;
    }

    public SwipeItem(Context context) {
        super(context);
        init();
    }

    public SwipeItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dragHelper = ViewDragHelper.create(this, dragCallback);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        if(!SwipeItemManager.getInstance().isCanSwip(this)){
            requestDisallowInterceptTouchEvent(true);
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float delatX = moveX - downX;//x方向移动的距离
                float delatY = moveY - downY;//y方向移动的距离
                if(Math.abs(delatX) / 3 > Math.abs(delatY)){
                    //表示移动是偏向于水平方向，那么应该SwipeLayout应该处理，请求listview不要拦截
                    //System.out.println("水平滑动，不要拦截");
                    requestDisallowInterceptTouchEvent(true);
                }
                //更新downX，downY
                downX = moveX;
                downY = moveY;
                break;

            case MotionEvent.ACTION_UP:
                break;

        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的，则需要直接拦截，交给onTouch处理
        if(!SwipeItemManager.getInstance().isCanSwip(this)){
            //先关闭已经打开的layout
            SwipeItemManager.getInstance().closeItem();

            result = true;
        }
        return result;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    private ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == menuView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0)
                    left = 0;
                else if (left < -menuWidth)
                    left = -menuWidth;
            } else if (child == menuView) {
                if (left > contentWidth)
                    left = contentWidth;
                else if (left < (contentWidth - menuWidth))
                    left = contentWidth - menuWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //伴随移动
            if (changedView == contentView) {
                menuView.layout(
                        menuView.getLeft() + dx,
                        menuView.getTop() + dy,
                        menuView.getRight() + dx,
                        menuView.getBottom() + dy);
            } else if (menuView == changedView) {
                contentView.layout(
                        contentView.getLeft() + dx,
                        contentView.getTop() + dy,
                        contentView.getRight() + dx,
                        contentView.getBottom() + dy);
            }

            //关闭时清除item
            if (contentView.getLeft() == 0 && currentState != SwipeState.close) {
                currentState = SwipeState.close;
                SwipeItemManager.getInstance().clearItem(SwipeItem.this);
                if (listener != null) {
                    listener.close(getTag());
                }
            }
            //打开时记录状态
            if(contentView.getLeft() == -menuWidth && currentState != SwipeState.open){
                currentState = SwipeState.open;
                SwipeItemManager.getInstance().setItem(SwipeItem.this);

                if (listener != null) {
                    listener.open(getTag());
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -menuWidth / 2) {
                open();
            } else {
                close();
            }
        }
    };

    public void open() {
        dragHelper.smoothSlideViewTo(contentView, -menuWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void close() {
        dragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    //当从menu打开新的activity时要调用，否则返回时还在打开状态，造成listview无法滑动
    public void close2() {
        contentView.layout(0, 0, contentWidth, contentHeight);
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, contentHeight);
        currentState = SwipeState.close;
        SwipeItemManager.getInstance().clearItem(SwipeItem.this);
    }
    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //System.out.println("on layout");
        contentWidth = contentView.getMeasuredWidth();
        contentHeight = contentView.getMeasuredHeight();
        menuWidth = menuView.getMeasuredWidth();
        contentView.layout(0, 0, contentWidth, contentHeight);
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, contentHeight);
    }

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeStateChangeListener{
        void open(Object tag);
        void close(Object tag);
    }

    @Subscribe
    public void resetState(TypeEvent event) {
        if (event.type == TypeEvent.RESET_SWIPEITEM_STATE) {
            close2();
        }
    }
}
