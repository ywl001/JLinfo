package com.ywl01.jlinfo.views;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.views.adapters.BaseAdapter;
import com.ywl01.jlinfo.views.adapters.DividerGridItemDecoration;
import com.ywl01.jlinfo.beans.SymbolBean;
import com.ywl01.jlinfo.events.SelectValueEvent;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.SymbolHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectSymbolDialog extends Dialog implements BaseAdapter.OnItemClickListener{

    private int[] icons = new int[]{
            R.drawable.smb_anmo,
            R.drawable.smb_bangonglou,
            R.drawable.smb_chaoshi,
            R.drawable.smb_chaye,
            R.drawable.smb_chezhan,
            R.drawable.smb_cunzhuang,
            R.drawable.smb_diannao,
            R.drawable.smb_dianshitai,
            R.drawable.smb_dianxin,
            R.drawable.smb_fandian,
            R.drawable.smb_fucai,
            R.drawable.smb_fuyin,
            R.drawable.smb_fuzhuang,
            R.drawable.smb_geting,
            R.drawable.smb_gongce,
            R.drawable.smb_gongchang,
            R.drawable.smb_gonghang,
            R.drawable.smb_hunqing,
            R.drawable.smb_jianhang,
            R.drawable.smb_jiaohang,
            R.drawable.smb_jiayouzhan,
            R.drawable.smb_liantong,
            R.drawable.smb_lifa,
            R.drawable.smb_lvdian,
            R.drawable.smb_meirong,
            R.drawable.smb_nonghang,
            R.drawable.smb_police,
            R.drawable.smb_qixiu,
            R.drawable.smb_quwei,
            R.drawable.smb_rihua,
            R.drawable.smb_shangdian,
            R.drawable.smb_shequ,
            R.drawable.smb_shichang,
            R.drawable.smb_shouji,
            R.drawable.smb_shudian,
            R.drawable.smb_shuiwu,
            R.drawable.smb_ticai,
            R.drawable.smb_tiyuchang,
            R.drawable.smb_tongyong,
            R.drawable.smb_wangba,
            R.drawable.smb_wenti,
            R.drawable.smb_wujin,
            R.drawable.smb_xiaofang,
            R.drawable.smb_xinyongshe,
            R.drawable.smb_xiuli,
            R.drawable.smb_xiyu,
            R.drawable.smb_xuexiao,
            R.drawable.smb_yanjiu,
            R.drawable.smb_yaodian,
            R.drawable.smb_yidong,
            R.drawable.smb_yingyuan,
            R.drawable.smb_yinhang,
            R.drawable.smb_yiyuan,
            R.drawable.smb_youeryuan,
            R.drawable.smb_youzheng,
            R.drawable.smb_zhaoxiang,
            R.drawable.smb_zhengfu,
            R.drawable.smb_zhensuo,
            R.drawable.smb_zhonghang,
    };

    private String[] names = new String[]{"按摩", "办公楼", "超市", "茶叶", "车站", "村庄", "电脑", "电视台", "电信", "饭店", "福彩", "复印", "服装", "歌厅", "公厕", "工厂", "工行", "婚庆", "建行", "交行", "加油站", "联通", "理发", "旅店", "美容", "农行", "警察", "汽修", "区委", "日化", "商店", "社区", "市场", "手机", "书店", "税务", "体彩", "体育场", "通用", "网吧", "文体", "五金", "消防", "信用社", "加工修理", "洗浴", "学校", "烟酒", "药店", "移动", "影院", "银行", "医院", "幼儿园", "邮政", "照相", "政府", "诊所", "中行"};
    private List<SymbolBean> symbols;
    private RecyclerView recyclerView;

    private Context context;

    public SelectSymbolDialog(@NonNull Context context) {
        super(context, R.style.fullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        symbols = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            SymbolBean symbol = new SymbolBean();
            symbol.iconID = icons[i];
            symbol.iconName = names[i];
            symbols.add(symbol);
        }

        recyclerView = new RecyclerView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(-1, -1);
        recyclerView.setLayoutParams(params);
        GridLayoutManager manager = new GridLayoutManager(context,5);

        recyclerView.addItemDecoration(new DividerGridItemDecoration(context));
        SymbolListAdapter adapter = new SymbolListAdapter(symbols);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        setContentView(recyclerView);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onItemClick(RecyclerView parent, View itemView, int position) {
        SymbolBean symbolBean = symbols.get(position);
        dismiss();
        SelectValueEvent event = new SelectValueEvent(SelectValueEvent.SELECT_SYMBOL);
        event.selectValue = symbolBean.iconName;
        event.dispatch();
    }


    class SymbolListAdapter extends BaseAdapter<SymbolBean> {

        public SymbolListAdapter(List<SymbolBean> datas) {
            super(datas);
        }

        @Override
        protected RecyclerView.ViewHolder getHolder() {
            View view = View.inflate(AppUtils.getContext(), R.layout.item_symbol, null);
            SymbolHolder holder = new SymbolHolder(view);
            return holder;
        }

        @Override
        protected int getClickBackground() {
            return AppUtils.getResColor(R.color.light_blue);
        }
    }

}
