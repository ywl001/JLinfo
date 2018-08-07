package com.ywl01.jlinfo.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by ywl01 on 2017/2/28.
 */

public class SearchItemView extends FrameLayout implements RadioGroup.OnCheckedChangeListener {
    private Context context;

    @BindView(R.id.et_field)
    public EditText etField;

    @BindView(R.id.et_operator)
    EditText etOperator;

    @BindView(R.id.et_keyword)
    EditText etKeyword;

    @BindView(R.id.rg_logic)
    RadioGroup rgLogic;

    @BindView(R.id.rb_and)
    RadioButton rbAnd;

    @BindView(R.id.rb_or)
    RadioButton rbOr;

    @BindView(R.id.btn_remove)
    Button btnRemove;

    private Map<String, String> operatorMap;
    private Map<String, String> fieldMap;
    private OnItemChangeListener onItemChangeListener;

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
    }

    public SearchItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SearchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {

        operatorMap = new HashMap<>();
        operatorMap.put("大于", ">");
        operatorMap.put("小于", "<");
        operatorMap.put("等于", "=");
        operatorMap.put("大于等于", ">=");
        operatorMap.put("小于等于", "<=");
        operatorMap.put("不等于", "!=");
        operatorMap.put("开始于", "start");
        operatorMap.put("结束于", "end");
        operatorMap.put("包含", "contain");
        operatorMap.put("不包含", "noContain");

        fieldMap = new HashMap<>();
        fieldMap.put("姓名", "name");
        fieldMap.put("性别", "sex");
        fieldMap.put("身份证号", "peopleNumber");
        fieldMap.put("出生日期", "birthday");
        fieldMap.put("联系电话", "telephone");
        fieldMap.put("居住类型", "liveType");
        fieldMap.put("社区村庄", "community");

        View view = View.inflate(context, R.layout.search_item, this);
        //requestFocus();
        ButterKnife.bind(this);

        //禁止输入，不弹出输入法
        etField.setInputType(InputType.TYPE_NULL);
        etOperator.setInputType(InputType.TYPE_NULL);

        rgLogic.setOnCheckedChangeListener(this);

    }

    private void setRadioGroupEnable(RadioGroup rg, boolean isEnable) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            rg.getChildAt(i).setEnabled(isEnable);
        }
    }

    //获取各个字段的数据，保存到map
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        String keyword = etKeyword.getText().toString().trim();
        String field = etField.getText().toString().trim();
        String operator = etOperator.getText().toString().trim();
        String logic = rgLogic.getCheckedRadioButtonId() == R.id.rb_and ? "and" : "or";

        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(field) || TextUtils.isEmpty(operator)) {
            return null;
        }
        data.put("field", fieldMap.get(field));
        data.put("operator", operatorMap.get(operator));
        data.put("keyword", keyword);
        if (rgLogic.getCheckedRadioButtonId() > 0) {
            data.put("logic", logic);
        }
        return data;
    }

    //是指删除按钮是否可用
    public void setBtnRemoveEnable(boolean enable) {
        btnRemove.setEnabled(enable);
    }

    public void setBtnRemoveVisible(boolean visible) {
        btnRemove.setVisibility(visible ? VISIBLE : GONE);
    }

    public boolean checkAdd() {
        boolean isField = !TextUtils.isEmpty(etField.getText().toString());
        boolean isOperator = !TextUtils.isEmpty(etOperator.getText().toString());
        boolean isKeyword = !TextUtils.isEmpty(etKeyword.getText().toString());
        return isField && isOperator && isKeyword;
    }

    //关闭系统软键盘
    private void closeSoftKey(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.et_field)
    public void onClickField() {
        showFieldSelectDialog();
    }

    @OnClick(R.id.et_operator)
    public void onClickOperator() {
        showOperatorSelectDialog();
    }

    @OnClick(R.id.et_keyword)
    public void onClickKeyword() {
        System.out.println("click keyword");
        if (etField.getText().toString().equals("监控类型")) {
            showCameraTypeDialog();
        } else if (etField.getText().toString().equals("运行状态")) {
            showIsRunningDialog();
        }
    }

    @OnClick(R.id.btn_remove)
    public void onRemove() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (onItemChangeListener != null) {
            onItemChangeListener.onRemove(this);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (onItemChangeListener != null) {
            onItemChangeListener.onAdd(this);
        }
        closeSoftKey(radioGroup);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 几个单选对话框
    ///////////////////////////////////////////////////////////////////////////

    //选择字段对话框
    private void showFieldSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择查询字段：");

        final String[] items = AppUtils.getResArray(R.array.camera);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                etField.setText(items[i]);
                String type = items[i];
                if ("监控类型".equals(type) || "运行状态".equals(type)) {
                    etOperator.setText("等于");
                    etOperator.setEnabled(false);
                    etKeyword.setInputType(InputType.TYPE_NULL);
                } else {
                    etOperator.setEnabled(true);
                    etOperator.setText("");
                    etKeyword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                etKeyword.setText("");
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //选择操作符对话框
    private void showOperatorSelectDialog() {
        if (TextUtils.isEmpty(etField.getText().toString().trim())) {
            Toast.makeText(context, "请先选择查询字段！！", Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] items1 = new String[]{"大于", "小于", "等于", "大于等于", "不等于"};
        final String[] items2 = new String[]{"等于", "包含", "开始于", "不包含", "结束于"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择查询字段：");

        if ("录入时间".equals(etField.getText().toString().trim())) {
            builder.setSingleChoiceItems(items1, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    etOperator.setText(items1[i]);
                    dialog.dismiss();
                }
            });
        } else {
            builder.setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    etOperator.setText(items2[i]);
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    //运行状态对话框
    private void showIsRunningDialog() {
        String title = AppUtils.getResString(R.string.dialog_select_camera_isrunning_title);
        String[] items = AppUtils.getResArray(R.array.isRunning);
        showSelectDialog(title, items);
    }

    //监控类型对话框
    private void showCameraTypeDialog() {
        String title = AppUtils.getResString(R.string.dialog_select_camera_type_title);
        String[] items = AppUtils.getResArray(R.array.cameraType);
        showSelectDialog(title, items);
    }

    //选择对话框
    private void showSelectDialog(String title, final String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                etKeyword.setText(items[i]);
                dialog.dismiss();
                closeSoftKey(etKeyword);
            }
        });
        builder.show();
    }


    //对外监听接口
    public interface OnItemChangeListener {
        void onAdd(SearchItemView view);//监听单选按钮变化，用来添加一个条目

        void onRemove(SearchItemView view);//监听删除按钮，删除一个条目
    }
}
