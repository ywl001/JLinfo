package com.ywl01.jlinfo.views.holds;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.SymbolBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ywl01 on 2017/2/3.
 */

public class SymbolHolder extends BaseRecyclerHolder<SymbolBean> {

    @BindView(R.id.iv_icon)
    public ImageView ivIcon;

    @BindView(R.id.tv_name)
    public TextView tvName;
    public SymbolHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    protected void refreshUI(SymbolBean data) {
        ivIcon.setImageResource(data.iconID);
        tvName.setText(data.iconName);
    }
}
