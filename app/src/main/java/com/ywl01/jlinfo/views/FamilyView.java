package com.ywl01.jlinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.consts.CommVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl01 on 2017/2/7.
 */

public class FamilyView extends FrameLayout{
    private List<FamilyNode> data;
    private List<Integer> levels;
    private List<FamilyNode> nodes;

    private List<FamilyItemGroup> levelGroups;
    private List<FamilyItem> items;

    private Context context;
    private int downX;
    private int downY;

    public FamilyView(Context context) {
        super(context);
        this.context = context;
        setBackgroundColor(0xffCEE8F2);
        levels = new ArrayList<>();
        nodes = new ArrayList<>();
        levelGroups = new ArrayList<>();
        items = new ArrayList<>();
    }

    public void setData(List<FamilyNode> data) {
        this.data = data;
        sortBylevel();
        sortAgain();
        //sort两次之后nodes有值了,levels也有值了；
        addFamilyItem();
        System.out.println(data);
    }

    //递归方式排序，获取顶级节点后警醒递归排序
//
//    private List<FamilyNode> temp = new ArrayList<>();
//    private void recursionSort(FamilyNode node) {
//        nodes.add(node);
//        temp.addAll(node.childNodes);
//        temp.remove(0);
//        if (temp.size() > 0) {
//            recursionSort(temp.get(0));
//        }
//    }


    private void sortBylevel() {
        int countNodes = data.size();
        for (int i = 0; i < countNodes; i++) {
            for (int j = i + 1; j < countNodes; j++) {
                if (data.get(i).level < data.get(j).level) {
                    FamilyNode temp = data.get(i);
                    data.set(i, data.get(j));
                    data.set(j, temp);
                }
            }
        }
    }

    private void sortAgain() {
        nodes.add(data.get(0));
        int countNodes = data.size();
        for (int i = 0; i < countNodes; i++) {
            if (!levels.contains(nodes.get(i).level)) {
                levels.add(nodes.get(i).level);
            }
            for (int j = 0; j < countNodes; j++) {
                FamilyNode node = data.get(j);
                FamilyNode parentNode = nodes.get(i);
                if (node.parentNode != null && node.parentNode == parentNode) {
                    nodes.add(node);
                }
            }
        }
    }

    //根据数据生成item 并添加到视图
    private void addFamilyItem() {
        int countLevel = levels.size();
        int countNodes = nodes.size();

        for (int i = 0; i < countLevel; i++) {
            FamilyItemGroup itemGroup = new FamilyItemGroup(context);
            addView(itemGroup);
            levelGroups.add(itemGroup);
            for (int j = 0; j < countNodes; j++) {
                FamilyNode node = nodes.get(j);
                if (node.level == levels.get(i)) {
                    FamilyItem item = new FamilyItem(context);
                    item.setData(node);
                    itemGroup.addView(item);
                    LayoutParams params = (LayoutParams) item.getLayoutParams();
                    params.leftMargin = 5;
                    params.rightMargin = 5;
                    items.add(item);

                    //添加父子节点数据
                    for (int k = 0; k < items.size(); k++) {
                        FamilyItem parentItem = items.get(k);
                        if (parentItem.getData().childNodes.contains(node)) {
                            parentItem.children.add(item);
                            item.parent = parentItem;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("family veiw on measure");

        int width = 0;
        int height = 0;

        int countChild = getChildCount();
        for (int i = 0; i < countChild; i++) {
            View child = getChildAt(i);
            if (child.getMeasuredWidth() > width) {
                width = child.getMeasuredWidth();
            }
            height += child.getMeasuredHeight() + 50;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        //传进来的参数是父的layout
        System.out.println("family view onlayout");
        int countGroups = levelGroups.size();
        for (int i = 0; i < countGroups; i++) {
            FamilyItemGroup group = levelGroups.get(i);
            group.layout(0,
                    i * (group.getMeasuredHeight() + 50),
                    group.getMeasuredWidth(),
                    i * (group.getMeasuredHeight() + 50) + group.getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("family veiw on draw");
        int countItem = items.size();
        Paint linePaint = createLinePaint(0xff000000, 1);


        for (int i = 0; i < countItem; i++) {
            FamilyItem parentItem = items.get(i);
            LayoutParams parentParams = (LayoutParams) parentItem.getLayoutParams();
            int parentCenter = getItemCenterX(parentItem);

            List<FamilyItem> childItems = parentItem.children;
            if (childItems.size() > 0) {
                int childrenCenter = getChildItemsCenterX(parentItem);

                //如果父view和子views的中心不对齐
                if (childrenCenter > parentCenter) {

                    //移动父view到子views的中间
                    int l = childrenCenter - parentItem.getMeasuredWidth() / 2;
                    int r = l + parentItem.getMeasuredWidth();
                    parentItem.layout(l, parentItem.getTop(), r, parentItem.getBottom());

                    // 排列同级别的其他view
                    moveOtherViews(parentItem);
                    parentItem.getParent().requestLayout();
                } else if (parentCenter > childrenCenter) {
                    //移动ziviews到父view中间
                    FamilyItem firstChildItem = childItems.get(0);
                    int l = parentCenter - getChildItemsWidth(parentItem) / 2;
                    int r = l + firstChildItem.getMeasuredWidth();
                    firstChildItem.layout(l, firstChildItem.getTop(), r, firstChildItem.getBottom());
                    moveOtherViews(firstChildItem);
                    firstChildItem.getParent().requestLayout();
                }

                //进行坐标转换并画连接线
                int countChild = childItems.size();
                int parentLeft = getRelativeLeft(parentItem);
                int parentTop = getRelativeTop(parentItem);
                int parentX = parentLeft + parentItem.getMeasuredWidth() / 2;
                int parentY = parentTop + parentItem.getMeasuredHeight();

                for (int j = 0; j < countChild; j++) {
                    FamilyItem child = childItems.get(j);

                    int childLeft = getRelativeLeft(child);
                    int childTop = getRelativeTop(child);
                    int childX = childLeft + child.getMeasuredWidth() / 2;
                    int childY = childTop;
                    canvas.drawLine(parentX, parentY, childX, childY, linePaint);
                }
            }
        }
    }

    //移动当前item右侧的所有item，使不重叠
    private void moveOtherViews(FamilyItem currentItem) {
        LayoutParams currentItemParams = (LayoutParams) currentItem.getLayoutParams();

        FamilyItemGroup itemGroup = (FamilyItemGroup) currentItem.getParent();
        //当前item在父容器中的位置索引
        int itemIndex = itemGroup.indexOfChild(currentItem);

        //紧挨着的item的最小起始位置
        int left = currentItem.getRight() + currentItemParams.rightMargin;

        for (int i = itemIndex + 1; i < itemGroup.getChildCount(); i++) {
            View otherItem = itemGroup.getChildAt(i);
            LayoutParams params = (LayoutParams) otherItem.getLayoutParams();

            if (otherItem.getLeft() - params.leftMargin > left) {
                left = otherItem.getRight() + params.rightMargin;
            } else {
                System.out.println("重新布局其他Item");
                otherItem.layout(left + params.leftMargin,
                        otherItem.getTop(),
                        left+ params.leftMargin + otherItem.getMeasuredWidth(),
                        otherItem.getBottom());
                left = otherItem.getRight() + params.rightMargin;
            }
        }
    }

    //获取一个item x方向中心点
    private int getItemCenterX(FamilyItem item) {
        return item.getLeft() + item.getMeasuredWidth() / 2;
    }

    //获取所有 子item 一起的宽度，子item都在一起挨着
    private int getChildItemsWidth(FamilyItem parentItem) {
        List<FamilyItem> childItems = parentItem.children;

        int minLeft = Integer.MAX_VALUE;
        int maxRight = 0;
        for (int i = 0; i < childItems.size(); i++) {
            FamilyItem item = childItems.get(i);
            if (item.getLeft() < minLeft) {
                minLeft = item.getLeft();
            }
            if (item.getRight() > maxRight) {
                maxRight = item.getRight();
            }
        }
        return maxRight - minLeft;
    }

    //获取所有子item的中心
    private int getChildItemsCenterX(FamilyItem parentItem) {
        List<FamilyItem> childItems = parentItem.children;
        int childsWidth = getChildItemsWidth(parentItem);
        return childItems.get(0).getLeft() + childsWidth / 2;
    }

    //重新设置itemgroup的大小
    private void layoutItemGroup(FamilyItemGroup itemGroup) {
        int right = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getRight() > right) {
                right = child.getRight();
            }
        }
        itemGroup.layout(itemGroup.getLeft(), itemGroup.getTop(), right, itemGroup.getBottom());
    }

    //坐标转换
    private int getRelativeLeft(View myView) {
        if (myView.getParent() == this)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == this)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    private Paint createLinePaint(int color, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dx = (int) (moveX - downX);
                int newScrollX = getScrollX() - dx;

                int moveY = (int) event.getY();
                int dy = moveY - downY;
                int newScrollY = getScrollY() - dy;

                if (newScrollX < 0 || getMeasuredWidth() <= CommVar.screenWidth) {
                    newScrollX = 0;
                } else if (newScrollX > getMeasuredWidth() - CommVar.screenWidth) {
                    newScrollX = getMeasuredWidth() - CommVar.screenWidth;
                }

                if (newScrollY < 0 || getMeasuredHeight() <= CommVar.appHeight) {
                    newScrollY = 0;
                } else if (newScrollY > getMeasuredHeight() - CommVar.appHeight) {
                    newScrollY = getMeasuredHeight() - CommVar.appHeight;
                }

                scrollTo(newScrollX, newScrollY);

                downX = moveX;
                downY = moveY;

                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dx = (int) (moveX - downX);

                int moveY = (int) event.getY();
                int dy = moveY - downY;

                if (Math.abs(dy) > 5) {
                    System.out.println("拦截。。。。。。。。。。");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onInterceptTouchEvent(event);
    }

}
