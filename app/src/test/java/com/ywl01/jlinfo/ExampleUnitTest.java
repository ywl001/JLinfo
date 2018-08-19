package com.ywl01.jlinfo;

import com.google.gson.Gson;
import com.ywl01.jlinfo.utils.AppUtils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();

        data.put("aaa",1);
        data.put("bbb",false);
        data2.put("ccc",false);
        data2.put("ddd",false);
        data.put("ccc", data2);

        Gson gson = new Gson();
       String str =  gson.toJson(data);
        System.out.println(str);

    }

}