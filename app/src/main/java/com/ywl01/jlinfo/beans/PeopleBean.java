package com.ywl01.jlinfo.beans;

/**
 * Created by ywl01 on 2017/1/12.
 */

public class PeopleBean{

    public long id;
    public String peopleNumber;
    public String name;
    public String namePinyin;
    public String sex;
    public String birthday;
    public String nation;
    public String telephone;
    public String liveType;
    public String community;
    public int isDead;
    public String insertUser;
    public String insertTime;
    public String updateUser;
    public String UpdateTime;

    //人员附加属性
    public int isLeave;//和地点关联，表示人员是否还在该位置工作和居住

    public long pmID;
    public String workPlace;//工作单位
    public String department;//所属部门
    public String job; //工作
    public int isManager;//是否是单位负责人。


    public long pbID;
    public String buildingName;//楼号
    public String roomNumber;//房间号、住址

    public long phmID;
    public String homeNumber;//户号
    public String relation;//与户主关系
    public int homeExists;

    public long phuID;

    public int isExists;

    public String photoUrl;
    public String thumbUrl;

    //用于区分从哪里查询到的人员
    public int peopleFlag;//用来存放人员来自哪里的标识：mark，house，building,ui,home

}
