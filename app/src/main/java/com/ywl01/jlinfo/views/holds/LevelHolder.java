package com.ywl01.jlinfo.views.holds;

import android.view.View;
import android.widget.Button;

public class LevelHolder extends BaseRecyclerHolder<Integer> {

    private Button btn;
    public LevelHolder(View itemView) {
        super(itemView);
        btn = (Button) itemView;
    }

    @Override
    protected void refreshUI(Integer data) {
        btn.setText(data + "");
    }
}
