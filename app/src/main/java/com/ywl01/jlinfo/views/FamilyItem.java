package com.ywl01.jlinfo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.PeoplesActivity;
import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.QueryFamilyServices;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;

/**
 * 生产一个home的item
 */

public class FamilyItem extends LinearLayout {

    private FamilyNode data;
    private Context context;
    private List<PeopleBean> peoples;
    private GestureDetector gestureDetector;

    public List<FamilyItem> children;
    public FamilyItem parent;

    public FamilyItem(Context context) {
        super(context);
        this.context = context;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setOrientation(HORIZONTAL);
        setBackgroundColor(0x00000000);
        setLayoutParams(params);
        children = new ArrayList<>();
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setData(FamilyNode data) {
        this.data = data;
        peoples = data.peoples;
        Collections.sort(peoples, new PeopleRelationSort());
        initFace();
    }

    private void initFace() {
        for (int i = 0; i < peoples.size(); i++) {
            PeopleBean p = peoples.get(i);
            View view = View.inflate(context, R.layout.item_family, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvRelation = (TextView) view.findViewById(R.id.tv_relation);
            tvName.setText(p.name);
//            if (p.sex.equals("男")) {
//                tvName.setBackgroundColor(0xffFAA090);
//            } else if (p.sex.equals("女")) {
//                tvName.setBackgroundColor(0xff67BCA5);
//            }

            if (p.isLeave == 1) {
                tvName.setTextColor(0xbbbbbbbb);
            }

            if (p.isDead == 1) {
                tvName.setBackgroundColor(0x55000000);
            }
            tvRelation.setText(p.relation);
            addView(view);
        }
    }

    public FamilyNode getData() {
        return data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint outerLinePaint = createLinePaint(0xff000000, 3);
        Paint innerLinePaint = createLinePaint(0x88000000, 1);

        if (data.sign == QueryFamilyServices.BASE) {
            canvas.drawColor(AppUtils.getResColor(R.color.light_blue));
        }
        //绘制外边框
        canvas.drawRect(1, 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1, outerLinePaint);

        //绘制竖线
        int stepWidth = getMeasuredWidth() / peoples.size();
        for (int i = 1; i < peoples.size(); i++) {
            canvas.drawLine(i * stepWidth, 0, i * stepWidth, getMeasuredHeight(), innerLinePaint);
        }

        //绘制横线
        canvas.drawLine(0, AppUtils.dip2px(35), getMeasuredWidth(), AppUtils.dip2px(35), innerLinePaint);
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
        return gestureDetector.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            System.out.println("double click");
//            String sql = SqlFactory.selectHomePeopleByHomeNumber(data.homeNumber);
//            PeopleObserver homePeopleObserver = new PeopleObserver(PeopleFlag.FROM_FAMILY);
//            HttpMethods.getInstance().getSqlResult(homePeopleObserver, SqlAction.SELECT, sql);
//            homePeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
//                @Override
//                public void onNext(Observer observer,Object data) {
//                    ArrayList<PeopleBean> peoples = (ArrayList<PeopleBean>) data;
//                    CommVar.getInstance().put("peoples",peoples);
//                    AppUtils.startActivity(PeoplesActivity.class);
//                }
//            });
            CommVar.getInstance().put("peoples",peoples);
            AppUtils.startActivity(PeoplesActivity.class);
            return super.onDoubleTap(e);
        }
    }
}
