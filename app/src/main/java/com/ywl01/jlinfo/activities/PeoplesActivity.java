package com.ywl01.jlinfo.activities;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.adapters.DividerItemDecoration;
import com.ywl01.jlinfo.adapters.PeopleListAdapter;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeoplesActivity extends BaseActivity {

    private ArrayList<PeopleBean> peoples;

    @BindView(R.id.recycler_view)
    RecyclerView peopleListView;
    private PeopleListAdapter adapter;

    private ArrayList<ArrayList<PeopleBean>> peopless;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_peoples);
        ButterKnife.bind(this);
        peopless = new ArrayList<>();
        peoples = (ArrayList<PeopleBean>) CommVar.getInstance().get("peoples");
        peopless.add(peoples);

        LinearLayoutManager manager = new LinearLayoutManager(AppUtils.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new PeopleListAdapter(peoples);
        peopleListView.setLayoutManager(manager);
        peopleListView.setAdapter(adapter);
        peopleListView.addItemDecoration(new DividerItemDecoration(AppUtils.getContext(), LinearLayoutManager.VERTICAL));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        peoples = (ArrayList<PeopleBean>) CommVar.getInstance().get("peoples");
        peopless.add(peoples);
        adapter = new PeopleListAdapter(peoples);
        peopleListView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("on back pressed................");
        peopless.remove(peopless.size() - 1);
        if (peopless.size() > 0) {
            peoples = peopless.get(peopless.size() - 1);
            adapter = new PeopleListAdapter(peoples);
            peopleListView.setAdapter(adapter);
        }else{
            finish();
        }
    }

    //    @Subscribe
//    public void onUpdatePeople(UpdatePeopleEvent event) {
//        PeopleBean p = event.people;
//        int countPeople = peoples.size();
//        for (int i = 0; i < countPeople; i++) {
//            if (peoples.get(i).id == p.id) {
//                peoples.set(i, p);
//
//                ListEvent e = new ListEvent();
//                e.action = ListEvent.update;
//                e.position  = i;
//                e.dispatch();
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onStart() {
//        System.out.println("people activity on start");
//        super.onStart();
//        if(!EventBus.getDefault().isRegistered(this))
//             EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void onStop() {
//        System.out.println("people activity on stop");
//        super.onStop();
//    }

}
