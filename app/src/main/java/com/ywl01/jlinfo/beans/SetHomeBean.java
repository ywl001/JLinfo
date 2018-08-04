package com.ywl01.jlinfo.beans;

/**
 * Created by ywl01 on 2017/2/24.
 */

public class SetHomeBean {
    public static final int OWN_CREATE = 0;
    public static final int OWN_JOIN_OTHER = 1;
    public static final int OTHER_JOIN_OWN = 2;


    public int peopleID;
    public String newHomeNumber;
    public String relation;
    public boolean isRealLeave;
    public boolean homeIsExists;
    public int oprationType;
    public int oldHomeID;
}
