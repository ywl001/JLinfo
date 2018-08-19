package com.ywl01.jlinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.events.SelectValueEvent;

import java.util.ArrayList;

public class BuildingRoomNumberView extends ViewGroup {

    private int countFloor;//楼层数
    private int countUnit;//单元数
    private int countHomesInUnit;//每单元户数
    private String sortType;//排序方式，a或b

    private int outLineWidth = 4;//外围线宽度
    private int drawTop = 100;//背景图距离顶部距离
    private int bgWidth;//背景图宽度
    private int bgHeight;//背景图高度

    private String title;
    private BuildingBean building;

    private int stepWidth = 180;
    private int stepHeight = 80;
    private ArrayList<TextView> roomNumbers;

    private Scroller scroller;
    private int downX;
    private int downY;

    private OnClickRoomNumberListener onClickRoomNumberListener;

    public void setOnClickRoomNumberListener(OnClickRoomNumberListener onClickRoomNumberListener) {
        this.onClickRoomNumberListener = onClickRoomNumberListener;
    }

    public BuildingRoomNumberView(Context context, BuildingBean building) {
        super(context);
        this.countFloor = building.countFloor;
        this.countUnit = building.countUnit;
        this.countHomesInUnit = building.countHomesInUnit;
        this.sortType = building.sortType;
        this.building = building;

        bgWidth = countUnit * countHomesInUnit * stepWidth;
        bgHeight = (countFloor +1) * stepHeight ;

        setBackgroundColor(0xffffffff);
        addRoomNumbers();
    }

    private void addRoomNumbers() {
        roomNumbers = new ArrayList<>();
        String roomNumber = "";
        for (int i = 1; i <= countFloor; i++) {
            for (int j = 1; j <= countUnit; j++) {
                for (int k = 1; k <= countHomesInUnit; k++) {
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    final TextView roomTv = new TextView(getContext());
                    if (sortType.equals("a")) {
                        if (((j - 1) * countHomesInUnit + k) < 10)//j最大到8就可以了，因为j+1 已经等于9了。
                            roomNumber = i + "0" + ((j - 1) * countHomesInUnit + k);
                        else
                            roomNumber = i + "" + ((j - 1) * countHomesInUnit + k);
                    } else if (sortType.equals("b")) {
                        if (k < 10)
                            roomNumber = j + "-" + i + "0" + k;
                        else
                            roomNumber = j + "-" + i + "" + k;
                    }
                    System.out.println(roomNumber);

                    roomTv.setText(roomNumber);
                    roomTv.setLayoutParams(layoutParams);
                    addView(roomTv);
                    roomTv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onClickRoomNumberListener != null) {
                                onClickRoomNumberListener.getRoomNumber(roomTv.getText().toString());
                            }
                        }
                    });
                    roomNumbers.add(roomTv);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("on measure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int childWidth = bgWidth + outLineWidth * 2 + 5;
        int childHeight = bgHeight + drawTop + 10;

        setMeasuredDimension(childWidth, childHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("layout");
        for (int i = 0; i < countFloor; i++) {
            for (int j = 0; j < countUnit * countHomesInUnit; j++) {
                TextView tvRoom = roomNumbers.get(countUnit * countHomesInUnit * i + j);
                int roomLeft = j * stepWidth + (stepWidth - tvRoom.getMeasuredWidth()) / 2 + 5;
                int roomTop = bgHeight - (i + 1) * (stepHeight) + stepHeight / 2 - tvRoom.getMeasuredHeight() / 2 + drawTop;
                tvRoom.layout(roomLeft, roomTop, roomLeft + tvRoom.getMeasuredWidth(), roomTop + tvRoom.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("on draw");
        Paint outerLinePaint = createLinePaint(0xff000000, outLineWidth);

        //绘制外围框线
        canvas.drawRect(outLineWidth / 2 + 5,
                drawTop + outLineWidth / 2,
                bgWidth + outLineWidth / 2,
                drawTop + bgHeight + outLineWidth / 2,
                outerLinePaint);

        //绘制单元间分割线
        Paint unitLinePaint = createLinePaint(0xff000000, 2);
        for (int i = 1; i < countUnit; i++) {
            canvas.drawLine(i * stepWidth * countHomesInUnit,
                    drawTop,
                    i * stepWidth * countHomesInUnit,
                    bgHeight + drawTop,
                    unitLinePaint);
        }

        //绘制横线
        Paint innerLinePaint = createLinePaint(0x88666666, 1);
        for (int i = 0; i < countFloor; i++) {
            canvas.drawLine(0,
                    drawTop + stepHeight + i * stepHeight,
                    bgWidth,
                    drawTop + stepHeight + i * stepHeight, innerLinePaint);
            canvas.drawLine(0,
                    drawTop + (i + 1) * stepHeight, bgWidth,
                    drawTop + (i + 1) * stepHeight, innerLinePaint);
        }

        //绘制竖线
        for (int i = 0; i < countUnit; i++) {
            for (int j = 0; j < countHomesInUnit - 1; j++) {
                canvas.drawLine(i * stepWidth * countHomesInUnit + j * stepWidth + stepWidth,
                        drawTop + stepHeight,
                        i * stepWidth * countHomesInUnit + j * stepWidth + stepWidth,
                        drawTop + bgHeight, innerLinePaint);
            }
        }

        Paint unitTextPaint = createTextPaint(0x44000000, 35);
        //绘制单元文字
        for (int i = 0; i < countUnit; i++) {
            String text = numToCN(i + 1) + "单元";
            int stringWidth = getStringWidth(text, unitTextPaint);
            canvas.drawText(text,
                    i * stepWidth * countHomesInUnit + (stepWidth * countHomesInUnit - stringWidth) / 2,
                    drawTop + stepHeight - 20,
                    unitTextPaint);
        }

        //绘制标题文字
        String title = "请选择人员的房间号码";
        Paint titleTextPaint = createTextPaint(0xff000000, 50);
        int titleWidth = getStringWidth(title, titleTextPaint);
        canvas.drawText(title,
                (getMeasuredWidth() - titleWidth) / 2,
                70, titleTextPaint);
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

    private int getStringWidth(String strings, Paint p) {
        float width = p.measureText(strings);
        return (int) (width + 0.5);
    }

    private Paint createTextPaint(int color, float textSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        return paint;
    }

    private String numToCN(int num) {
        String[] arr = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
        String str;
        String numStr = num + "";

        if (numStr.length() == 1) {
            return arr[num - 1];
        } else if (numStr.length() == 2) {
            int secondNum = Character.getNumericValue(numStr.charAt(1));
            int firstNum = Character.getNumericValue(numStr.charAt(0));
            if (firstNum == 1) {
                if (secondNum == 0)
                    return "十";
                else {
                    return "十" + arr[secondNum - 1];
                }
            } else {
                if (secondNum == 0)
                    return arr[firstNum - 1] + "十";
                else {
                    return arr[firstNum - 1] + "十" + arr[secondNum - 1];
                }

            }
        }
        return Integer.toString(num);
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
                int dx = moveX - downX;
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
                int dx = moveX - downX;

                int moveY = (int) event.getY();
                int dy = moveY - downY;

                System.out.println("dx,dy" + dx + dy);
                if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
                    System.out.println("拦截。。。。。。。。。。");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onInterceptTouchEvent(event);
    }

    public interface OnClickRoomNumberListener{
        void getRoomNumber(String roomNumber);
    }
}
