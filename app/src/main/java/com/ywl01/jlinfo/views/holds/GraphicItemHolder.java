package com.ywl01.jlinfo.views.holds;

import android.view.View;
import android.widget.TextView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.GraphicItemBean;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphicItemHolder extends BaseRecyclerHolder<GraphicItemBean> {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_telephone)
    TextView tvTelephone;

    @BindView(R.id.tv_type)
    TextView tvType;

    @BindView(R.id.tv_community)
    TextView tvCommunity;

    public GraphicItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    protected void refreshUI(GraphicItemBean data) {
        setTextViewValuc(tvName,data.name);
        if (TableName.MARK.equals(data.tableName)) {
            tvCommunity.setVisibility(View.GONE);
            setTextViewValuc(tvType,data.type);
            setTextViewValuc(tvTelephone,data.telephone);
        } else if (TableName.BUILDING.equals(data.tableName) || TableName.HOUSE.equals(data.tableName)) {
            tvTelephone.setVisibility(View.GONE);
            setTextViewValuc(tvType,data.type);
            setTextViewValuc(tvCommunity,data.community);
        }
    }

    private void setTextViewValuc(TextView tv,String value) {
        if(!StringUtils.isEmpty(value)){
            tv.setVisibility(View.VISIBLE);
            tv.setText(value);
        }else{
            tv.setVisibility(View.GONE);
        }
    }
}
