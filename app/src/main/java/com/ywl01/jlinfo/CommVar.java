package com.ywl01.jlinfo;

import com.esri.arcgisruntime.geometry.SpatialReference;
import com.ywl01.jlinfo.beans.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommVar {

    //服务器的一些参数
    public static String baseUrl = "http://120.92.200.168/";
    //public static String configUrl = baseUrl + "jlInfo/config.xml";
    public static String arcgis_license = "runtimelite,1000,rud7393493088,none,FA0RJAY3FLJ5EBZNR078";
    public static String vecMapUrl = "http://120.92.200.168:8399/arcgis/rest/services/jlmap2/MapServer";
    public static String imgMapUrl;
    public static String serverImageRootUrl = baseUrl + "jlmap_image/";
    public static String sqlUrl = "jlInfo/sql.php";
    public static String sqlUrl2 = "jlInfo/sql2.php";
    public static String delFileUrl = "jlInfo/delFile.php";
    public static String uploadUrl = "jlInfo/uploadImage.php";

    public static String serverImageDir = "jlmap_image/image";
    public static String serverPhotoDir = "jlmap_image/photo";

    public static SpatialReference mapSpatialReference;
    public static User loginUser;

    public static int screenWidth;
    public static int appHeight;


    private Map<Integer, Double> level_scale;
    public static int buildingDisplayLevel = 4;
    public static int houseDisplayLevel = 5;

    private double[] mapscales = new double[]{128000,64000,32000,16000,8000,4000,2000,1000,500,250};
    //单例模式实例
    private static CommVar instance = null;

    //synchronized 用于线程安全，防止多线程同时创建实例
    public synchronized static CommVar getInstance(){
        if(instance == null){
            instance = new CommVar();
        }
        return instance;
    }

    final HashMap<String, Object> mMap;
    private CommVar()
    {
        mMap = new HashMap<String,Object>();
        level_scale = new LinkedHashMap<>();

        for (int i = 0; i < mapscales.length; i++) {
            level_scale.put(i, mapscales[i]);
        }
    }

    public void put(String key,Object value){
        mMap.put(key,value);
    }

    public Object get(String key)
    {
        return mMap.get(key);
    }

    public void clear(){mMap.clear();}

    public double getScaleBylevel(int level) {
        return level_scale.get(level);
    }

    public int getLevelByScale(double mapScale) {
        for (int key : level_scale.keySet()) {
            if (mapScale > level_scale.get(key)) {
                return key-1;
            }
        }
        return 9;
    }
}
