package com.ywl01.jlinfo.observers;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.Symbol;
import com.ywl01.jlinfo.beans.GraphicBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.utils.BeanMapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ywl01 on 2017/1/21.
 */

public abstract class GraphicObserver extends BaseObserver<String,List<Graphic>>{

    @Override
    protected List<Graphic> convert(String data) {
        List<GraphicBean> beanList = getBeanList(data);

        List<Graphic> graphics = new ArrayList<>();

        for (GraphicBean bean : beanList) {
            double x = bean.x;
            double y = bean.y;
            Point p = new Point(x, y, CommVar.mapSpatialReference);

            Symbol symbol = getSymbol(bean);
            Map<String, Object> attributes = getAttributes(bean);

            Graphic g = new Graphic(p, attributes, symbol);
            graphics.add(g);
        }
        return graphics;
    }

    private Map<String, Object> getAttributes(GraphicBean bean) {
        Map<String, Object> attributes = BeanMapUtils.beanToMap(bean);
        int flag = getFlag();
        attributes.put("graphicFlag", flag);
        return attributes;
    }

    /**
     * 获取graphic的标识
     * @return
     */
    protected abstract int getFlag();

    /**
     * 子类通过json转换成bean
     * @param json
     * @return
     */
    protected abstract List<GraphicBean> getBeanList(String json);

    /**
     * 子类提供graphic的symbol
     * @param bean
     * @return
     */
    protected abstract Symbol getSymbol(GraphicBean bean);

}
