package com.ywl01.jlinfo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ywl01 on 2017/1/22.
 */

public class BeanMapUtils {
    public static void mapToBean(Map<String, Object> map, Object bean) {
        Class cls = bean.getClass();
        Field[] fields = cls.getFields();

        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = fields[i];
                String key = f.getName();
                //System.out.println("markInfoBean key:" + key);
                if(map.containsKey(key)){
                    f.set(bean,map.get(key));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        Class cls = bean.getClass();
        Field[] fields = cls.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = fields[i];
                int modifer = f.getModifiers();
                if (modifer == Modifier.PUBLIC) {
                    String key = f.getName();
                    Object value = f.get(bean);
                    map.put(key, value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
