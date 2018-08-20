package com.ywl01.jlinfo;

import com.ywl01.jlinfo.net.HttpMethods;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;

public class PhpFunction {
    public static final String SELECT_GRAPHIC_DATA                     = "selectGraphicData";
    public static final String SELECT_USER                             = "selectUser";
    public static final String SELECT_PEOPLES_BY_BUILDING              = "selectPeoplesByBuilding";
    public static final String SELECT_PEOPLES_BY_MARK                  = "selectPeoplesByMark";
    public static final String SELECT_PEOPLES_BY_HOUSE                 = "selectPeoplesByHouse";
    public static final String SELECT_PEOPLES_BY_INPUT                 = "selectPeoplesByInput";
    public static final String SELECT_PEOPLES_BY_KEYWORD               = "selectPeoplesByKeyWord";
    public static final String SELECT_PEOPLES_BY_WHERE                 = "selectPeoplesByWhere";
    public static final String SELECT_GRAPHICS_BY_NAME                 = "selectGraphicsByName";
    public static final String SELECT_PHOTOS_BY_PEOPLE_ID              = "selectPhotosByPeopleID";
    public static final String SELECT_ADDRESS_BY_PEOPLE_ID             = "selectAddressByPeopleID";
    public static final String SELECT_WORKPLACE_BY_PEOPLE_ID           = "selectWorkplaceByPeopleID";
    public static final String SELECT_HOME_PEOPLES_BY_PEOPLE_ID        = "selectHomePeoplesByPeopleID";
    public static final String SELECT_PEOPLE_HOME_INFO                 = "selectPeopleHomeInfo";
    public static final String SELECT_PEOPLE_IN_PARENT_HOME_INFO       = "selectPeopleInParentHomeInfo";
    public static final String SELECT_ALL_HOME_PEOPLES_BY_PEOPLE_ID    = "selectAllHomePeoplesByPeopleID";
    public static final String SELECT_PARENT_HOME_PEOPLES_BY_PEOPLE_ID = "selectParentHomePeoplesByPeopleID";
    public static final String SELECT_USER_BY_ID                       = "selectUserByID";
    public static final String SELECT_MARK_MANAGER                     = "selectMarkManager";
    public static final String SELECT_MARK_IMAGES                      = "selectMarkImages";
    public static final String CHECK_PEOPLE_IS_IN_GRAPHIC              = "checkPeopleIsInGraphic";
    public static final String CHECK_PEOPLE_IS_HAS_PARENT              = "checkPeopleIsHasParent";
    public static final String CHECK_USER_NAME                         = "checkUserName";

    public static final String INSERT                         = "insert";
    public static final String DELETE                         = "deleteRecord";
    public static final String DELETE_PEOPLE_PARENT_HOME_INFO = "deletePeopleParentHomeInfo";
    public static final String UPDATE                         = "update";

    public static void insert(Observer observer, String tableName, Map tableData) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("tableData", tableData);
        HttpMethods.getInstance().getSqlResult(observer, INSERT, data);
    }

    public static void delete(Observer observer, String tableName, int id) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("id", id);
        HttpMethods.getInstance().getSqlResult(observer, DELETE, data);
    }

    public static void update(Observer observer, String tableName, Map tableData, int id) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("tableData", tableData);
        data.put("id", id);
        HttpMethods.getInstance().getSqlResult(observer, UPDATE, data);
    }

}
