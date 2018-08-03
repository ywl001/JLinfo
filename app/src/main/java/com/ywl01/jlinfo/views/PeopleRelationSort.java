package com.ywl01.jlinfo.views;


import com.ywl01.jlinfo.beans.PeopleBean;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ywl01 on 2017/2/11.
 */

public class PeopleRelationSort implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        PeopleBean p1 = (PeopleBean) o1;
        PeopleBean p2 = (PeopleBean) o2;

        Map<String, Integer> map = new HashMap<>();
        map.put("户主", 1);
        map.put("妻", 2);
        map.put("妻子", 2);
        map.put("儿子", 3);
        map.put("独生子", 3);
        map.put("女儿", 4);
        map.put("独生女", 4);

        Integer value = map.get(p1.relation);
        Integer value2 = map.get(p2.relation);

        if (value == null && value2 == null) {
            return 0;
        }
        if (value == null) {
            return 1;
        }
        if (value2 == null) {
            return -1;
        }

        return value - value2;
    }
}
