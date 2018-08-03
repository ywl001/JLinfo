package com.ywl01.jlinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.ywl01.jlinfo.activities.PeoplesActivity;
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.KeyName;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ywl01 on 2016/12/20.
 */

public class BuildingPlan extends ViewGroup implements View.OnClickListener {

    private String buildingName;//楼号

    private int countFloor = 16;//楼层数
    private int countUnit = 3;//单元数
    private int countHomesInUnit = 2;//每单元户数
    private String sortType = "b";//排序方式，a或b

    public int widthHome = 180;//绘制时每户的宽度

    private int heightHomeNumber = 55;//房间号的高度
    private int heightHome = 80;//户主姓名的高度
    private int bgWidth;//背景图宽度
    private int bgHeight;//背景图高度
    private List<TextView> roomNames;//户主名字集合
    private List<TextView> roomNumbers;//房间号集合
    private int drawTop = 100;//背景图距离顶部距离

    private List<PeopleBean> peoples;
    private Map<String, PeopleBean> homeholderMap;

    private int outLineWidth = 4;//外围线宽度
    private String title = "河阳新村1号楼";


    public BuildingPlan(Context context) {
        super(context);
    }

    public BuildingPlan(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(BuildingBean building, List<PeopleBean> peoples) {
        if (building == null || peoples == null) {
            return;
        }
        countFloor = building.countFloor;
        countUnit = building.countUnit;
        countHomesInUnit = building.countHomesInUnit;
        buildingName = building.buildingName;
        sortType = building.sortType;
        this.peoples = peoples;


        title = buildingName;
        homeholderMap = getHomeholderMap(peoples);
        init();

    }

    private void init() {
        bgWidth = countUnit * countHomesInUnit * widthHome;
        bgHeight = countFloor * (heightHomeNumber + heightHome) + heightHome;
        setBackgroundColor(0x00000000);
        scroller = new Scroller(getContext());
        addTextView();
    }

    private void addTextView() {
        roomNumbers = new ArrayList<>();
        roomNames = new ArrayList<>();

        String roomNumber = "";
        for (int i = 1; i <= countFloor; i++) {
            for (int j = 1; j <= countUnit; j++) {
                for (int k = 1; k <= countHomesInUnit; k++) {
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    TextView roomTv = new TextView(getContext());
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
                    roomNumbers.add(roomTv);

                    PeopleBean homholder = getHomeHolderByRoomNumber(roomNumber);
                    if (homholder != null) {
                        TextView tvName = new TextView(getContext());
                        tvName.setOnClickListener(this);
                        tvName.setText(homholder.name);
                        tvName.setTextColor(0xff000000);
                        tvName.setTextSize(16);
                        tvName.setTag(roomNumber);
                        tvName.setLayoutParams(layoutParams);
                        addView(tvName);

                        roomNames.add(tvName);
                    }
                }
            }
        }
    }

    //获取房间户主或人员
    private List<PeopleBean> getHomeholders(List<PeopleBean> peoples) {
        List<PeopleBean> homeholders = new ArrayList<>();
        List<String> rooms = new ArrayList<>();
        for (PeopleBean people : peoples) {
            if (!rooms.contains(people.roomNumber) && people.relation.equals("户主")) {
                rooms.add(people.roomNumber);
                homeholders.add(people);
            }
        }
        return homeholders;
    }

    private Map<String, PeopleBean> getHomeholderMap(List<PeopleBean> peoples) {
        Map<String, PeopleBean> homeholderMap = new HashMap<>();
        List<String> rooms = new ArrayList<>();
        for (PeopleBean people : peoples) {
            if (people.relation != null && people.relation.equals("户主")) {
                homeholderMap.put(people.roomNumber, people);
            } else {
                if (!homeholderMap.containsKey(people.roomNumber)) {
                    homeholderMap.put(people.roomNumber, people);
                }
            }
        }
        return homeholderMap;
    }

    private PeopleBean getHomeHolderByRoomNumber(String roomNumber) {
        if (homeholderMap.containsKey(roomNumber)) {
            return homeholderMap.get(roomNumber);
        }
        return null;
    }

    private TextView getTvNameByRoomNumber(String roomNumber) {
        for (TextView tvName : roomNames) {
            String tag = (String) tvName.getTag();
            if (tag.equals(roomNumber)) {
                return tvName;
            }
        }
        return null;
    }

    private int downX;
    private int downY;
    private Scroller scroller;

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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("layout");
        for (int i = 0; i < countFloor; i++) {
            for (int j = 0; j < countUnit * countHomesInUnit; j++) {
                TextView tvRoom = roomNumbers.get(countUnit * countHomesInUnit * i + j);
                int roomLeft = j * widthHome + (widthHome - tvRoom.getMeasuredWidth()) / 2;
                int roomTop = bgHeight - (i + 1) * (heightHomeNumber + heightHome) + heightHomeNumber / 2 - tvRoom.getMeasuredHeight() / 2 + drawTop;
                tvRoom.layout(roomLeft, roomTop, roomLeft + tvRoom.getMeasuredWidth(), roomTop + tvRoom.getMeasuredHeight());

                TextView tvName = getTvNameByRoomNumber(tvRoom.getText().toString());

                if (tvName != null) {
                    //System.out.println(roomName.roomNumber + "---" + roomName.tvName.getText().toString());
                    int nameLeft = j * widthHome + (widthHome - tvName.getMeasuredWidth()) / 2;
                    int nameTop = bgHeight - i * (heightHomeNumber + heightHome) - heightHome + (heightHome - tvName.getMeasuredHeight()) / 2 + drawTop;
                    tvName.layout(nameLeft, nameTop, nameLeft + tvName.getMeasuredWidth(), nameTop + tvName.getMeasuredHeight());
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("on draw");
        Paint outerLinePaint = createLinePaint(0xff000000, outLineWidth);

        //绘制外围框线
        canvas.drawRect(outLineWidth / 2,
                drawTop + outLineWidth / 2,
                bgWidth + outLineWidth / 2,
                drawTop + bgHeight + outLineWidth / 2,
                outerLinePaint);

        //绘制单元间分割线
        Paint unitLinePaint = createLinePaint(0xff000000, 2);
        for (int i = 1; i < countUnit; i++) {
            canvas.drawLine(i * widthHome * countHomesInUnit,
                    drawTop,
                    i * widthHome * countHomesInUnit,
                    bgHeight + drawTop,
                    unitLinePaint);
        }

        //绘制横线
        Paint innerLinePaint = createLinePaint(0x88666666, 1);
        for (int i = 0; i < countFloor; i++) {
            canvas.drawLine(0,
                    drawTop + heightHome + i * (heightHome + heightHomeNumber),
                    bgWidth,
                    drawTop + heightHome + i * (heightHome + heightHomeNumber), innerLinePaint);
            canvas.drawLine(0,
                    drawTop + (i + 1) * (heightHome + heightHomeNumber),
                    bgWidth,
                    drawTop + (i + 1) * (heightHome + heightHomeNumber),
                    innerLinePaint);
        }

        //绘制竖线
        for (int i = 0; i < countUnit; i++) {
            for (int j = 0; j < countHomesInUnit - 1; j++) {
                canvas.drawLine(i * widthHome * countHomesInUnit + j * widthHome + widthHome,
                        drawTop + heightHome,
                        i * widthHome * countHomesInUnit + j * widthHome + widthHome,
                        drawTop + bgHeight, innerLinePaint);
            }
        }

        Paint unitTextPaint = createTextPaint(0x88000000, 35);
        //绘制单元文字
        for (int i = 0; i < countUnit; i++) {
            String text = numToCN(i + 1) + "单元";
            int stringWidth = getStringWidth(text, unitTextPaint);
            canvas.drawText(text,
                    i * widthHome * countHomesInUnit + (widthHome * countHomesInUnit - stringWidth) / 2,
                    drawTop + heightHome - 20,
                    unitTextPaint);
        }

        //绘制标题文字
        Paint titleTextPaint = createTextPaint(0xff000000, 50);
        int titleWidth = getStringWidth(title, titleTextPaint);
        canvas.drawText(title,
                (getMeasuredWidth() - titleWidth) / 2,
                70, titleTextPaint);
    }

    private int getStringWidth(String strings, Paint p) {
        float width = p.measureText(strings);
        int maxWidth = (int) (width + 0.5);
        return maxWidth;
    }

    private Paint createTextPaint(int color, float textSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        return paint;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int childWidth = bgWidth + outLineWidth;
        int childHeight = bgHeight + outLineWidth + drawTop;

        setMeasuredDimension(childWidth, childHeight);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            System.out.println(tv.getTag().toString() + "----" + tv.getText().toString());
            ArrayList<PeopleBean> roomPeoples = getRoomPeoples(tv.getTag().toString());

            CommVar.getInstance().put("peoples",roomPeoples);
            AppUtils.startActivity(PeoplesActivity.class);
        }
    }

    private ArrayList<PeopleBean> getRoomPeoples(String roomNumber) {
        int size = peoples.size();
        ArrayList<PeopleBean> roomPeoples = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (roomNumber.equals(peoples.get(i).roomNumber)) {
                roomPeoples.add(peoples.get(i));
            }
        }
        return roomPeoples;
    }
}
