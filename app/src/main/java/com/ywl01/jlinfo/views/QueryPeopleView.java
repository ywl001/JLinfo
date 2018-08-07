package com.ywl01.jlinfo.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.promeg.pinyinhelper.Pinyin;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.views.adapters.DividerItemDecoration;
import com.ywl01.jlinfo.views.adapters.QueryPeopleListAdapter;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;


/**
 * Created by ywl01 on 2017/2/19.
 */

public class QueryPeopleView extends LinearLayout implements RadioGroup.OnCheckedChangeListener, BaseObserver.OnNextListener {

    private Context context;

    @BindView(R.id.rg)
    RadioGroup rg;

    @BindView(R.id.rb_pnumber)
    RadioButton rbPNumber;

    @BindView(R.id.rb_pname)
    RadioButton rbPName;

    @BindView(R.id.ck_homophone)
    CheckBox ckHomophone;

    @BindView(R.id.et_keyword)
    EditText etKeyWord;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.people_list_view)
    RecyclerView peopleListView;

    private List<PeopleBean> peoples;
    private PeopleBean newPeople;
    private boolean isAddNewPeople;

    private OnItemSelectListener onItemSelectListener;
    private onClickBtnCancelListener onClickBtnCancelListener;

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setOnClickBtnCancelListener(onClickBtnCancelListener onClickBtnCancelListener) {
        this.onClickBtnCancelListener = onClickBtnCancelListener;
    }

    public QueryPeopleView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QueryPeopleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = inflate(context, R.layout.view_query_people, this);
        ButterKnife.bind(this);
        setOrientation(LinearLayout.VERTICAL);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AppUtils.getContext(), LinearLayoutManager.HORIZONTAL, false);
        peopleListView.setLayoutManager(layoutManager);
        peopleListView.addItemDecoration(new DividerItemDecoration(AppUtils.getContext(), LinearLayout.HORIZONTAL));


        rg.setOnCheckedChangeListener(this);
    }

    public void SetIsAddNewPeople(boolean isAddNewPeople) {
        this.isAddNewPeople = isAddNewPeople;
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        String sql = createSql();
        PeopleObserver peopleObserver = new PeopleObserver(PeopleFlag.FROM_QUERY);
        HttpMethods.getInstance().getSqlResult(peopleObserver, SqlAction.SELECT, sql);
        peopleObserver.setOnNextListener(this);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        if (onClickBtnCancelListener != null) {
            onClickBtnCancelListener.onClick();
        }
    }

    private String createSql() {
        String keyword = etKeyWord.getText().toString().trim();
        int rbID = rg.getCheckedRadioButtonId();
        String sql = "";
        newPeople = new PeopleBean();
        if (rbID == R.id.rb_pname) {
            newPeople.name = keyword;
            if (ckHomophone.isChecked()) {
                //同音查询
                String pinyin = Pinyin.toPinyin(keyword,"");
                System.out.println(pinyin);
                sql = "select p.*,pp.photoUrl,pp.thumbUrl,phm.id phmID,phm.homeNumber " +
                        "from people p " +
                        "left join people_photo pp on p.id = pp.peopleID " +
                        "left join people_home phm on phm.peopleID = p.id " +
                        "where p.namePinyin like '%" + pinyin + "%' and phm.isDelete = 0";
            } else {
                //名字查询
                sql = "select p.*,pp.photoUrl,pp.thumbUrl,phm.id phmID,phm.homeNumber " +
                        "from people p " +
                        "left join people_photo pp on p.id = pp.peopleID " +
                        "left join people_home phm on phm.peopleID = p.id " +
                        "where p.name like '%" + keyword + "%' and phm.isDelete = 0";
            }
        } else {
            //身份证号查询
            newPeople.peopleNumber = keyword;
            sql = "select p.*,pp.photoUrl,pp.thumbUrl,phm.id phmID,phm.homeNumber " +
                    "from people p " +
                    "left join people_photo pp on p.id = pp.peopleID " +
                    "left join people_home phm on phm.peopleID = p.id " +
                    "where p.peopleNumber like '%" + keyword + "%' and phm.isDelete = 0";
        }
        return sql;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_pname) {
            ckHomophone.setVisibility(View.VISIBLE);
        } else {
            ckHomophone.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNext(Observer observer,Object data) {
        peoples = (List<PeopleBean>) data;
        peopleListView.setVisibility(VISIBLE);
        if (isAddNewPeople) {
            peoples.add(newPeople);
        }

        QueryPeopleListAdapter adapter = new QueryPeopleListAdapter(peoples);
        peopleListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new QueryPeopleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View itemView, int position) {
                PeopleBean p = peoples.get(position);
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(p);
                }
            }
        });
    }

    public interface OnItemSelectListener {
        void onItemSelect(PeopleBean peopleBean);
    }

    public interface onClickBtnCancelListener {
        void onClick();
    }
}
