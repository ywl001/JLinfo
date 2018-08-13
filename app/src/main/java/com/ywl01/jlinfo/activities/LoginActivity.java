package com.ywl01.jlinfo.activities;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.LoginFragment;

public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        LoginFragment loginFragment = new LoginFragment();
        addFragment(loginFragment,R.id.main_container);

        if (AppUtils.isNetConnect(this)) {
            AppUtils.showToast("网络正常");
        }else{
            AppUtils.showToast("无网路");
        }
    }

}
