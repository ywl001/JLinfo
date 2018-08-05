package com.ywl01.jlinfo.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl01 on 2017/2/6.
 */

public class FamilyNode{
    public int level;//该节点所处的级别
    public ArrayList<PeopleBean> peoples ;//户成员
    public String homeNumber ;//户号
    public FamilyNode parentNode ;//存放父节点列表
    public int sign ;//标识上查得到的节点，还是下查得到的节点
    public List<FamilyNode> childNodes;
    public PeopleBean focusPeople;

    public FamilyNode() {
        childNodes = new ArrayList<>();
    }
}
