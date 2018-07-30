package com.ywl01.jlinfo.net;

import com.esri.arcgisruntime.geometry.Envelope;
import com.ywl01.jlinfo.consts.CommVar;

import java.util.Map;

/**
 * Created by ywl01 on 2017/1/21.
 */

public class SqlFactory {

    public static String selectGraphicData(Envelope extent,double mapScale,String table) {
        int mapLevel = getLevelByScale(mapScale);
        String sql = "select * from " + table + " where" +
                " x > " + extent.getXMin() +
                " and x < " + extent.getXMax() +
                " and y > " + extent.getYMin() +
                " and y < " + extent.getYMax() +
                " and displayLevel <= " + mapLevel;
        return sql;
    }

    private static int getLevelByScale(double mapScale) {
        Map<Integer,Double> levelScale = CommVar.getInstance().level_scale;
        for (int key : levelScale.keySet()) {
            if (mapScale > levelScale.get(key)) {
                return key-1;
            }
        }
        return 9;
    }

    public static String selectUser(long userID) {
        String sql = "select * from user where id = " + userID;
        return sql;
    }

    public static String selectPeopleByBuilding(long buildingID) {
        String sql = "SELECT distinct " +
                "p.id,p.peopleNumber,p.name,p.sex,p.nation,p.telephone,p.liveType,p.community,pb.isDelete isLeave," +
                "pb.roomNumber,pb.id pbID,pb.insertUser,pb.insertTime,pb.updateTime," +
//                "phm.id phmID,phm.relation,phm.homeNumber," +
                "b.buildingName " +
                "FROM people_building pb LEFT JOIN people p ON pb.peopleID = p.id " +
                "left join building b on pb.buildingID = b.id " +
                "left join people_home phm on phm.peopleID = p.id " +
                "WHERE pb.buildingID =" + buildingID ;
        //System.out.println(sql);
        return sql;
    }

    public static String selectPeopleByMark(long markID) {
        String sql = "select distinct p.id,p.peopleNumber,p.name,p.sex,p.nation,p.telephone,p.liveType,p.community," +
                "pm.isManager,m.name workPlace,pm.id pmID,pm.isDelete isLeave,pm.insertTime,pm.updateTime,pm.department,pm.job " +
                "from people_mark pm " +
                "left join people p on pm.peopleID = p.id " +
                "left join mark m on pm.markID = m.id " +
                "where pm.markID = " + markID + " order by pm.isManager desc";
        //System.out.println(sql);
        return sql;
    }

    public static String selectPhotosByPeopleID(long peopleID) {
        String sql = "select * from people_photo where peopleID = " + peopleID;
        //System.out.println(sql);
        return sql;
    }

    public static String selectPeopleByHouse(long houseID) {
        String sql = "SELECT DISTINCT " +
                        "p.*," +
                        "phu.isDelete isLeave," +
                        "phu.id phuID," +
                        "phu.insertTime," +
                        "phu.updateTime," +
                        "h.name houseName," +
                        "h.roomNumber " +
                        "FROM people_house phu " +
                        "LEFT JOIN people p ON phu.peopleID = p.id " +
                        "LEFT JOIN house h ON h.id = phu.houseID " +
                        "WHERE phu.houseID = " + houseID;
        System.out.println(sql);
        return sql;
    }

    //根据人员id获取人员的住址************************************************************
    public static String selectAddressByPeopleID(long peopleID){
        String sql =
                "SELECT DISTINCT hu.id,hu.name,hu.x,hu.y,hu.displayLevel,'house' as tableName " +
                        "FROM people_house phu LEFT JOIN house hu ON phu.houseID = hu.id " +
                        "WHERE phu.peopleID = " + peopleID  + " union " +
                        "SELECT DISTINCT b.id,b.buildingName name,b.x,b.y,b.displayLevel,'building' as tableName " +
                        "FROM people_building pb LEFT JOIN building b ON b.id = pb.buildingID " +
                        "where pb.peopleID = " + peopleID ;
       // System.out.println(sql);
        return sql;
    }

    //根据peopleID查询同户人员*****************************************************
    public static String selectHomePeopleByPid(long peopleID){
        String childSql =
                "select homeNumber " +
                        "from people_home " +
                        "where peopleID = " + peopleID  + " and isDelete = 0 limit 1";
        String sql =
                "select distinct p.*,phm.homeNumber,phm.relation,phm.isDelete isLeave " +
                        "from people_home phm left join people p on phm.peopleID = p.id " +
                        "where homeNumber = (" + childSql + ") and phm.isDelete = 0 and p.isDead = 0" ;

        System.out.println(sql);

        return sql;
    }

    //根据peopleID查询所有同户人员*****************************************************
    public static String selectAllHomePeopleByPid(long peopleID){
        String childSql =
                "select homeNumber " +
                        "from people_home " +
                        "where peopleID = " + peopleID  + " and isDelete = 0 limit 1";
        String sql =
                "select distinct p.*,phm.homeNumber,phm.relation,phm.isDelete isLeave " +
                        "from people_home phm left join people p on phm.peopleID = p.id " +
                        "where homeNumber = (" + childSql + ")";

        System.out.println(sql);

        return sql;
    }

    //根据peopleID查询人员的父级户号中的所有人员*****************************************************
    public static String selectParentHomePeoplesByPid(long peopleID){
        //获取父级户号
        String childSql =
                "select distinct homeNumber " +
                        "from people_home where peopleID = " + peopleID  +
                        " and isDelete = 1 limit 1";
        //根据父级户号查询所有父级中的人员
        String sql =
                "select p.*,phm.homeNumber,phm.relation,phm.isDelete isLeave " +
                        "from people_home phm left join people p on phm.peopleID = p.id " +
                        "where homeNumber = (" + childSql + ")";
        System.out.println(sql);
        return sql;
    }

    public static String insert(String tableName, Map<String, String> data) {
        String sql = "insert into " + tableName + " (";
        for(String key: data.keySet()){
            sql += key + ",";
        }
        sql = sql.substring(0,sql.length() -1) + ") values (";

        for(String key: data.keySet()){
            String value = data.get(key);
            if(value == "now()")//php now（）函数，不能带引号
                sql += value + "," ;
            else
                sql += "'" + value + "',";
        }

        sql = sql.substring(0,sql.length() -1) + ")";
        System.out.println(sql);
        return sql;
    }

    public static String delete(String tableName, long id) {
        String sql = "delete from " + tableName + " where id = " + id;
        return sql;
    }


    public static String selectPeoplesByHome(long peopleID) {
        String sql = "select distinct p.* from people_home phm left join people p on p.id = phm.peopleID where p.id = " + peopleID;
        System.out.println(sql);
        return sql;
    }

    public static String selectMarkManager(long markID) {
       String sql = "select p.* from people_mark pm left join people p on pm.peopleID = p.id where pm.markID = " + markID + " and pm.isManager = 1 limit 1";
        return sql;
    }

    public static String selectMarkImages(long markID) {
        String sql = "select * from mark_image where markID = " + markID;
        return sql;
    }

    public static String update(String tableName,Map<String,String> data,long id) {
        String sql = "update " + tableName + " set ";

        for(String key: data.keySet()){
            String value = data.get(key);
            sql += (key + "='" + value + "',");
        }
        sql = sql.substring(0,sql.length() - 1) + " where id =" + id;
        System.out.println(sql);
        return sql;
    }

    public static String selectHomePeopleByHomeNumber(String homeNumber) {
        String sql = "select p.*,phm.relation,phm.isDelete isLeave from people_home phm left join people p on p.id = phm.peopleID where phm.homeNumber = '" + homeNumber + "'";
        System.out.println(sql);
        return sql;
    }
}
