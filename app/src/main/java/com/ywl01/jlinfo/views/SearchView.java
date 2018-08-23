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

import com.github.promeg.pinyinhelper.Pinyin;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.PeoplesActivity;
import com.ywl01.jlinfo.beans.GraphicItemBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.events.ShowGraphicListEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.GraphicItemsObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.PeopleNumbleUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;


/**
 * Created by ywl01 on 2017/3/15.
 */
public class SearchView extends FrameLayout implements TextWatcher, TextView.OnEditorActionListener {

    private Context context;

    public static final int search_type_people = 1;
    public static final int search_type_geo    = 2;

    private static final int input_date         = 1;
    private static final int input_phone        = 2;
    private static final int input_peopleNumber = 3;
    private static final int input_num_other    = 4;
    private static final int input_homophone    = 5;
    private static final int input_name         = 6;

    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.btn_clear)
    Button btnClear;

    @BindView(R.id.btn_search_type)
    Button btnSearchType;

    @BindView(R.id.btn_search1)
    Button btnSearch;

    @BindView(R.id.btn_homophone)
    Button btnHomophone;

    private boolean isHomophone;

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
        } else {
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
        } else {
            btnSearch.setVisibility(GONE);
        }

        if (AppUtils.isChinese(charSequence.toString())) {
            btnHomophone.setVisibility(VISIBLE);
            btnClear.setVisibility(GONE);
        } else {
            btnHomophone.setVisibility(GONE);
            btnClear.setVisibility(VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @OnClick(R.id.btn_search1)
    public void onSearch() {
        doSearch();
    }

    @OnClick(R.id.btn_homophone)
    public void onHomophone() {
        isHomophone = !isHomophone;
        if (isHomophone) {
            btnHomophone.setBackground(AppUtils.getResDrawable(R.drawable.homophone_press));
        } else {
            btnHomophone.setBackground(AppUtils.getResDrawable(R.drawable.homophone));
        }
    }

    private void doSearch() {
        String input = etSearch.getText().toString().trim();
        int inputType = getInputType(input);
        if (isHomophone) {
            input = Pinyin.toPinyin(input, "");
            System.out.println("input");
        }
        Map data = getSqlData(inputType, input);
        if (!checkInput(input)) {
            AppUtils.showToast("请输入正确的查询条件");
            return;
        }
        if (searchType == 1) {
            PeopleObserver peopleObserver = new PeopleObserver(PeopleFlag.FROM_SEARCH);
            HttpMethods.getInstance().getSqlResult(peopleObserver, PhpFunction.SELECT_PEOPLES_BY_INPUT, data);
            peopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    etSearch.setText("");
                    List<PeopleBean> peoples = (List<PeopleBean>) data;
                    if (peoples.size() == 0) {
                        AppUtils.showToast("没有查询到相关人员");
                    } else {
                        CommVar.getInstance().put("peoples", data);
                        AppUtils.startActivity(PeoplesActivity.class);
                    }
                }
            });
        } else if (searchType == 2) {
            GraphicItemsObserver observer = new GraphicItemsObserver();
            HttpMethods.getInstance().getSqlResult(observer, PhpFunction.SELECT_GRAPHICS_BY_NAME, data);
            observer.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    etSearch.setText("");
                    List<GraphicItemBean> graphicItems = (List<GraphicItemBean>) data;
                    if (graphicItems.size() > 0) {
                        ShowGraphicListEvent event = new ShowGraphicListEvent();
                        event.graphicItems = graphicItems;
                        event.dispatch();
                    } else {
                        AppUtils.showToast("没有查询到相关场所或单位");
                    }
                }
            });
        }
    }

    private boolean checkInput(String input) {
        if (AppUtils.isEmptyString(input)) {
            return false;
        }
        return true;
    }

    private Map<String, Object> getSqlData(int inputType,String input) {
        Map<String, Object> data = new HashMap<>();
        data.put("inputType", inputType);
        data.put("input", input);
        return data;
    }

    private int getInputType(String input) {
        if (AppUtils.isDate(input)) {
            return input_date;
        } else if (AppUtils.isMobile(input)) {
            return input_phone;
        } else if (PeopleNumbleUtils.validate(input)) {
            return input_peopleNumber;
        } else if (AppUtils.isChinese(input) && isHomophone) {
            return input_homophone;
        } else if (AppUtils.isChinese(input)) {
            return input_name;
        } else {
            return input_num_other;
        }
    }
}
