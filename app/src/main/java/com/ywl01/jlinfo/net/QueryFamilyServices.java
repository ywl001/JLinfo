package com.ywl01.jlinfo.net;

import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.FamilyDataObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2017/2/6.
 */

public class QueryFamilyServices implements BaseObserver.OnNextListener{
    public static final int BASE = 0;
    public static final int UP = 1;
    public static final int DOWN= -1;

    private int sign ;//标识人员来源，是上查得到的节点还是下查得到的节点

    private  List<PeopleBean> waitCheckPeoples;
    private FamilyNode _data ;
    public List<FamilyNode> nodes;//存放家系节点的数组。

    private FamilyNode currentNode ;
    private int nodeIndex ;
    private int pidIndex ;
    private List<Integer> pidList;//存放peopleid的数组，主要用来检查传入的pid是否已经使用过。

    private PeopleObserver peopleObserver;
    private int times;
    private FamilyDataObserver observer;

    public QueryFamilyServices() {
        nodes = new ArrayList<>();
        pidList = new ArrayList<>();
        peopleObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
    }

    public void setData(FamilyNode value,FamilyDataObserver observer)
    {
        this.observer = observer;
        _data = value;
        nodes.add(_data);
        currentNode = _data;
        waitCheckPeoples = getStartCheckedPeople(_data.peoples, _data.focusPeople);
        queryNodeByPeople(waitCheckPeoples,0);
    }

    private List<PeopleBean> getStartCheckedPeople(List<PeopleBean> peoples,PeopleBean focusPeople){
        List<PeopleBean> arr = new ArrayList<>();
        for (int i = 0; i < peoples.size(); i++) {
            PeopleBean p = peoples.get(i);
            if (p.isLeave == 1 || p.id == focusPeople.id) {
                arr.add(p);
            }
        }
        return arr;
    }

    private void queryNodeByPeople(List<PeopleBean> peoples,int index)
    {
        if(peoples.size() > 0)
        {
            System.out.println(peoples.get(0).name + "需要查询" + peoples.size() + "人");
            PeopleBean p = peoples.get(index);
            int pid = p.id;
            System.out.println("用" + pidIndex + p.name + "查询");
            if(currentNode.sign == DOWN){//通过下查得到的节点只要再向下查就行了，因为都是同一个爹。
                if(p.isLeave == 0){
                    System.out.println("下查节点不上查了，跳过" + p.name);
                    nextPid();
                }
                else{
                    downQuery(pid);
                }
            }
            else{
                if(p.isLeave == 0)//对于节点中分户出去的人往下查，没有分户出去的向上查。分户出去的一般为孩子，没有出去的一般是父亲。
                    upQuery(pid);
                else
                    downQuery(pid);
            }
        }
        else{
            System.out.println("该户没有男性或下级户成员");
            nextPid();
        }
    }

    private void upQuery(int pid) {
        if(pidList.contains(pid)){
            System.out.println("向上查询，已经查过，跳过");
            nextPid();
        }
        else{
            System.out.println("*****************************向上查询*************************************");
            String sql = SqlFactory.selectParentHomePeoplesByPid(pid);
            selectHomePeoplesByPid(sql);
            sign = UP;
            pidList.add(pid);
        }
    }

    private void downQuery(int pid) {
        if(pidList.contains(pid)){
            System.out.println("向下查询，已经查过，跳过");
            nextPid();
        }
        else{
            System.out.println("############################向下查询##################################");
            String sql = SqlFactory.selectAllHomePeopleByPid(pid);
            selectHomePeoplesByPid(sql);
            sign = DOWN;
            pidList.add(pid);
        }
    }

    private void nextPid() {
        pidIndex++;

        if(pidIndex > waitCheckPeoples.size() - 1){
            pidIndex = 0;
            nodeIndex++;

            AppUtils.showToast("查询到" + nodeIndex + "户家族成员");

            if(nodeIndex > nodes.size() - 1){
                System.out.println(nodes);
                //查询结束
                Observable.just(nodes).subscribe(observer);
                return;
            }

            currentNode = nodes.get(nodeIndex);
            waitCheckPeoples = getManPeoples(currentNode.peoples);

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("node数组索引：" + nodeIndex + "node数组长度：" + nodes.size());
        }

        queryNodeByPeople(waitCheckPeoples,pidIndex);
    }

    private List<PeopleBean> getManPeoples(List<PeopleBean> peoples){
        List<PeopleBean> arr = new ArrayList<>();
        for (int i = 0; i < peoples.size(); i++) {
            PeopleBean p = peoples.get(i);
            if (p.sex.equals("男") || p.isLeave == 1) {
                arr.add(p);
            }
        }
        return arr;
    }

    private void selectHomePeoplesByPid(String sql) {
        HttpMethods.getInstance().getSqlResult(peopleObserver, SqlAction.SELECT, sql);
        peopleObserver.setOnNextListener(this);
    }

    @Override
    public void onNext(Observer observer,Object data) {
        ArrayList<PeopleBean> peoples = (ArrayList<PeopleBean>) data;
        if(peoples.size() > 0)
        {
            //根据查询结果生成节点。
            FamilyNode node = new FamilyNode();
            node.peoples = peoples;
            node.sign = sign;
            node.homeNumber = peoples.get(0).homeNumber;

            if(sign == UP){
                node.level = currentNode.level + 1;
                currentNode.parentNode = node;
                node.childNodes.add(currentNode);

                for (int i = 0; i < waitCheckPeoples.size(); i++) {
                    PeopleBean p = waitCheckPeoples.get(i);
                    int currentPeopleID = currentNode.peoples.get(pidIndex).id;
                    if (p.id != currentPeopleID && p.isLeave == 0 && i > pidIndex) {
                        times++;
                        System.out.println("少查" + times+"次" + p.name);
                        waitCheckPeoples.remove(i);
                    }
                }
            }
            else if(sign == DOWN){
                node.level = currentNode.level - 1;
                node.parentNode = currentNode;
                currentNode.childNodes.add(node);
            }
            nodes.add(node);
            System.out.println("增加一个节点" + peoples.get(0).name + "当前有" +  nodes.size() + "个节点");

            for (int i = 0; i < nodes.size(); i++) {
                node = nodes.get(i);
                System.out.println(node.peoples.get(0).name);
            }
            nextPid();
        }
        else{
            System.out.println(waitCheckPeoples.get(pidIndex).name + "没有上下级");
            nextPid();
        }
    }
}
