package com.ywl01.jlinfo.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.utils.AppUtils;

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
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
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


//    @Override
//    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            if("".equals(etSearch.getText().toString().trim())){
//                AppUtils.showToast("请输入查询内容。。。");
//                return false;
//            }
//
//            CamerasObserver camerasObserver = new CamerasObserver();
//            String keyword = etSearch.getText().toString().trim();
////            String[] keywords = keyword.split(" ");
//            String sql = SqlFactory.selectMarkerBySearch(keyword);
//            HttpMethods.getInstance().getSqlResult(camerasObserver, SqlAction.SELECT,sql);
//            textView.setText("");
//            camerasObserver.setOnNextListener(new BaseObserver.OnNextListener() {
//                @Override
//                public void onNext(Object data, Observer observer) {
//                    List<CameraBean> markers = (List<CameraBean>) data;
//                    CamerasEvent event = new CamerasEvent();
//                    event.cameraBeans = markers;
//                    event.dispatch();
//                }
//            });
//            return true;
//        }
//        return false;
//    }
}
