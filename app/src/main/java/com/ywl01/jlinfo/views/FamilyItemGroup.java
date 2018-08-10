package com.ywl01.jlinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 同级人员的group，实现item的横向拖动
 */

public class FamilyItemGroup extends FrameLayout {

    private ViewDragHelper dragHelper;
    private int isFirstLayout = 0;
    private int downX;
    private int downY;

    public FamilyItemGroup(Context context) {
        super(context);
//        setBackgroundColor(0x44ff0000);
        LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        initDragHelper();
    }

    public FamilyItemGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        LayoutParams params = new LayoutParams(-2, -2);
        setLayoutParams(params);
        initDragHelper();
    }

    private void initDragHelper() {
        dragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int minLeft = 0;
                if (dx < 0) {
                    for (int i = 0; i < getChildCount(); i++) {
                        View v = getChildAt(i);
                        if (v != child) {
                            LayoutParams p = (LayoutParams) v.getLayoutParams();
                            minLeft += (p.leftMargin + p.rightMargin + v.getMeasuredWidth());
                        } else {
                            break;
                        }
                    }
                }

                if (left < minLeft) {
                    left = minLeft;
                }
                invalidate();
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top - dy;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth()-child.getMeasuredWidth();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                int changePosition = indexOfChild(changedView);
                if (dx > 0) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (i > changePosition) {
                            View childView = getChildAt(i);
                            View prevView = getChildAt(i - 1);
                            LayoutParams prevParams = (LayoutParams) prevView.getLayoutParams();
                            LayoutParams childParams = (LayoutParams) childView.getLayoutParams();

                            int childViewLeft = prevView.getRight() + prevParams.rightMargin + childParams.leftMargin;

                            if (childView.getLeft() > childViewLeft) {
                                childViewLeft = childView.getLeft();
                            }

                            childView.layout(childViewLeft, childView.getTop(), childViewLeft + childView.getMeasuredWidth(), childView.getBottom());
                        }
                    }
                } else {
                    for (int i = getChildCount() - 1; i >= 0; i--) {
                        if (i < changePosition) {
                            View childView = getChildAt(i);
                            View prevView = getChildAt(i + 1);
                            LayoutParams prevParams = (LayoutParams) prevView.getLayoutParams();
                            LayoutParams childParams = (LayoutParams) childView.getLayoutParams();

                            int childViewLeft = prevView.getLeft() - prevParams.leftMargin - childParams.rightMargin - childView.getMeasuredWidth();
                            if (childView.getLeft() < childViewLeft) {
                                childViewLeft = childView.getLeft();
                            }
                            childView.layout(childViewLeft, childView.getTop(), childViewLeft + childView.getMeasuredWidth(), childView.getBottom());
                        }
                    }
                }
                FamilyItemGroup.this.requestLayout();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("item group draw");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("item group on measure");

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //计算高度
        int height = getChildAt(0).getMeasuredHeight();

        //计算宽度
        int width = 0;
        int countChild = getChildCount();
        FamilyItem item = null;
        for (int i = 0; i < countChild; i++) {
            item = (FamilyItem) getChildAt(i);
            LayoutParams params = (LayoutParams) item.getLayoutParams();
            if (item.getRight() == 0) {
                width += params.leftMargin + item.getMeasuredWidth() + params.rightMargin;
            } else {
                if (item.getRight() > width) {
                    width = item.getRight() + params.leftMargin;
                }
            }
        }
        System.out.println("item group measure width: " + width + "height:" + height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed,left,top,right,bottom);
        System.out.println("item group on layout");
        if (isFirstLayout < 2) {
            firstLayoutChild();
            isFirstLayout++;
        }
    }

    private void firstLayoutChild() {
        int l = 0;
        int t = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            System.out.println("child is layout:" + v.isLayoutRequested());
            LayoutParams p = (LayoutParams) v.getLayoutParams();
            v.layout(l + p.leftMargin,
                    t + p.topMargin,
                    l + p.leftMargin + v.getMeasuredWidth(),
                    t + p.topMargin + v.getMeasuredHeight());
            l = v.getRight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return dragHelper.shouldInterceptTouchEvent(event);
    }

}
