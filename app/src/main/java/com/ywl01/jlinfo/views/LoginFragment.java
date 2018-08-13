package com.ywl01.jlinfo.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.MainActivity;
import com.ywl01.jlinfo.beans.User;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.UserObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class LoginFragment extends Fragment {

    @BindView(R.id.et_user)
    EditText etUser;

    @BindView(R.id.et_password)
    EditText etPasswored;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_login)
    public void onLogin() {
        if (validateInput()) {
            UserObserver userObserver = new UserObserver();
            final String userName = etUser.getText().toString().trim();
            String password = etPasswored.getText().toString().trim();
            String sql = SqlFactory.selectUser(userName, password);
            HttpMethods.getInstance().getSqlResult(userObserver, SqlAction.SELECT, sql);
            userObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    User user = (User) data;
                    if (user != null) {
                        CommVar.loginUser = user;
                        AppUtils.startActivity(MainActivity.class);
                        getActivity().finish();
                    } else {
                        AppUtils.showToast("用户名或密码错误");
                    }
                }
            });
        }
    }

    @OnClick({R.id.btn_register})
    public void onRegister() {
        getFragmentManager().beginTransaction().add(R.id.main_container, new RegisterFragment()).commit();
    }

    private boolean validateInput() {
        String userName = etUser.getText().toString().trim();
        String password = etPasswored.getText().toString().trim();
        if (AppUtils.isEmptyString(userName)) {
            AppUtils.showToast("用户名不能为空");
            return false;
        }
        if (AppUtils.isEmptyString(password)) {
            AppUtils.showToast("密码不能为空");
            return false;
        }

        for (int i = 0; i < userName.length(); i++) {
            char c = userName.charAt(i);
            if (c == '\'' || c == '"' || c == '/' || c == '<') {
                AppUtils.showToast("用户名有非法字符");
                return false;
            }
        }

        for (int i = 0; i < password.length(); i++) {
            char c = userName.charAt(i);
            if (c == '\'' || c == '"' || c == '/' || c == '<') {
                AppUtils.showToast("密码有非法字符");
                return false;
            }
        }

        return true;
    }
}
