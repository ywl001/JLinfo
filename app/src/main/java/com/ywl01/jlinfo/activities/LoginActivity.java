package com.ywl01.jlinfo.activities;

import android.widget.EditText;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.User;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.UserObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.StringUtils;
import com.ywl01.jlinfo.views.LoginFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        LoginFragment loginFragment = new LoginFragment();
        addFragment(loginFragment,R.id.main_container);
    }

}
