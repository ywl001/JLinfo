package com.ywl01.jlinfo.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class RegisterFragment extends Fragment {
    @BindView(R.id.et_user)
    EditText etUserName;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;

    @BindView(R.id.et_real_name)
    EditText etRealName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_register, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_register)
    public void submit() {
        if(!checkPassword()){
            AppUtils.showToast("两次密码输入不一致");
            return;
        }

        checkUserName(etUserName.getText().toString().trim());
    }

    @OnClick(R.id.btn_exit)
    public void exit() {
        getActivity().finish();
    }

    private boolean checkPassword() {
        return etPassword.getText().toString().equals(etRepeatPassword.getText().toString());
    }

    private void checkUserName(String userName) {
        IntObserver userObserver = new IntObserver();
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("userName", userName);
        HttpMethods.getInstance().getSqlResult(userObserver, PhpFunction.CHECK_USER_NAME, tableData);
        userObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int num = (int) data;
                if (num == 0) {
                    submitRegister();
                }else{
                    AppUtils.showToast("用户名已经存在");
                }
            }
        });
    }

    private void submitRegister() {
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String realName = etRealName.getText().toString().trim();

        IntObserver insertObserver = new IntObserver();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("userName", userName);
        tableData.put("password", password);
        tableData.put("realName", realName);

        PhpFunction.insert(insertObserver,TableName.USER,tableData);
        insertObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int returnID = (int) data;
                if (returnID > 0) {
                    AppUtils.showToast("注册成功，请等待管理员审核通过");
                }
                getActivity().finish();
            }
        });
    }
}
