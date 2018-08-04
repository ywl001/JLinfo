package com.ywl01.jlinfo.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.IdcardUtils;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by ywl01 on 2017/3/15.
 */
public class SearchView extends FrameLayout implements TextWatcher, TextView.OnEditorActionListener{

    private Context context;

    public static final int search_type_people = 1;
    public static final int search_type_geo = 2;

    private static final int num_phone = 1;
    private static final int num_idcard = 2;
    private static final int num_other = 3;

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.btn_clear)
    Button btnClear;

    @BindView(R.id.btn_search_type)
    Button btnSearchType;

    @BindView(R.id.btn_search1)
    Button btnSearch;


    public int searchType = 1;

    public SearchView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true);
        ButterKnife.bind(this);
        etSearch.addTextChangedListener(this);
        etSearch.setOnEditorActionListener(this);
    }

    @OnClick(R.id.btn_clear)
    public void onClear() {
        String inputText = etSearch.getText().toString().trim();
        if (inputText.length() > 0) {
            etSearch.setText("");
        }else{
            setVisibility(GONE);
            TypeEvent.dispatch(TypeEvent.SHOW_BTN_CONTAINER);
        }
    }

    @OnClick(R.id.btn_search_type)
    public void onSearchType() {
        if (searchType == 1) {
            btnSearchType.setBackground(AppUtils.getResDrawable(R.drawable.search_type_geo));
            searchType = 2;
            etSearch.setHint("场所名称");
        } else if (searchType == 2) {
            btnSearchType.setBackground(AppUtils.getResDrawable(R.drawable.search_type_people));
            searchType = 1;
            etSearch.setHint("人员姓名，身份证号");
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            btnSearch.setVisibility(VISIBLE);
        }else{
            btnSearch.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @OnClick(R.id.btn_search1)
    public void onSearch() {
        System.out.println("fffffffffffffff");
    }

    private String getSql(String input) {
        String sql = "";
        if (searchType == 1) {
            if (AppUtils.isNumeric(input)) {
                int numType = getNumType(input);
                switch (numType) {
                    case num_phone:
                        sql = "select * from people where telephone = '" + input + "'";
                        break;
                    case num_idcard:
                        sql = "select * from people where peopleNumber = '" + input + "'";
                        break;
                    case num_other:
                        sql = "select * from people where (peopleNumber like '%" + input + "%' or telephoen like '%" + input +"%'";
                        break;
                }
            } else if (AppUtils.isChinese(input)) {
                sql = "select * from people where name like '%" + input + "%'";
            }
        } else if (searchType == 2) {
            if (AppUtils.isChinese(input)) {
                sql = "select * from mark where name like '%" + input + "%'";
            }
        }
        return sql;
    }

    private void search(){
        String sql = getSql(etSearch.getText().toString().trim());
        if (sql != "") {

        }
    }

    private int getNumType(String num) {
        if (AppUtils.isMobile(num) || AppUtils.isPhone(num)) {
            return num_phone;
        } else if (IdcardUtils.validateCard(num)) {
            return num_idcard;
        }else{
            return num_other;
        }
    }
}
