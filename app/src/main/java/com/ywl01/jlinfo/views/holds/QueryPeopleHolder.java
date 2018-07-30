package com.ywl01.jlinfo.views.holds;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ywl01 on 2017/2/3.
 */

public class QueryPeopleHolder extends BaseRecyclerHolder<PeopleBean> {
    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.text)
    TextView textView;

    @BindView(R.id.root_view)
    LinearLayout rootView;

    public QueryPeopleHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    protected void refreshUI(PeopleBean data) {
        if (data.id == 0) {
            imageView.setImageResource(R.drawable.img_no_select);
        }
        else if (data.thumbUrl != null) {
            ImageLoader.getInstance().displayImage(CommVar.serverImageRootUrl + data.thumbUrl,imageView);
        }else{
            imageView.setImageResource(R.drawable.img_no_photo);
        }

        textView.setText(data.name + "-" + data.birthday);

//        if (position != clickPosition) {
//            holder.rootView.setBackgroundColor(UIUtils.getResColor(R.color.listItemBg));
//        }
    }
}
