package com.ywl01.jlinfo.map;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.HashMap;

/**
 * Created by ywl01 on 2016/12/2.
 */

public class SymbolManager {
    private static final int SYMBOL_SIZE = 14;
    private static PictureMarkerSymbol pms_camera = createPms(AppUtils.getContext(), R.drawable.smb_camera);
    private static PictureMarkerSymbol pms_chezhan = createPms(AppUtils.getContext(),R.drawable.smb_chezhan);
    private static PictureMarkerSymbol pms_fire = createPms(AppUtils.getContext(),R.drawable.smb_fire);
    private static PictureMarkerSymbol pms_fire2 = createPms(AppUtils.getContext(),R.drawable.smb_fire2);
    public static PictureMarkerSymbol pms_fire_blue = createPms(AppUtils.getContext(),R.drawable.smb_fire_blue);
    private static PictureMarkerSymbol pms_xiaofang = createPms(AppUtils.getContext(),R.drawable.smb_xiaofang);
    private static PictureMarkerSymbol pms_fuyin = createPms(AppUtils.getContext(),R.drawable.smb_fuyin);
    private static PictureMarkerSymbol pms_new = createPms(AppUtils.getContext(),R.drawable.smb_new);
    private static PictureMarkerSymbol pms_redLocation = createPms(AppUtils.getContext(),R.drawable.smb_red_location);
    private static PictureMarkerSymbol pms_location = createPms(AppUtils.getContext(),R.drawable.smb_location);
    private static PictureMarkerSymbol pms_anmo = createPms(AppUtils.getContext(),R.drawable.smb_anmo);
    private static PictureMarkerSymbol pms_bangonglou = createPms(AppUtils.getContext(),R.drawable.smb_bangonglou);
    private static PictureMarkerSymbol pms_chaoshi = createPms(AppUtils.getContext(),R.drawable.smb_chaoshi);
    private static PictureMarkerSymbol pms_chaye = createPms(AppUtils.getContext(),R.drawable.smb_chaye);
    private static PictureMarkerSymbol pms_cunzhuang = createPms(AppUtils.getContext(),R.drawable.smb_cunzhuang);
    private static PictureMarkerSymbol pms_diannao = createPms(AppUtils.getContext(),R.drawable.smb_diannao);
    private static PictureMarkerSymbol pms_dianshitai = createPms(AppUtils.getContext(),R.drawable.smb_dianshitai);
    private static PictureMarkerSymbol pms_dianxin = createPms(AppUtils.getContext(),R.drawable.smb_dianxin);
    private static PictureMarkerSymbol pms_fandian = createPms(AppUtils.getContext(),R.drawable.smb_fandian);
    private static PictureMarkerSymbol pms_fucai = createPms(AppUtils.getContext(),R.drawable.smb_fucai);
    private static PictureMarkerSymbol pms_fuzhuang = createPms(AppUtils.getContext(),R.drawable.smb_fuzhuang);
    private static PictureMarkerSymbol pms_geting = createPms(AppUtils.getContext(),R.drawable.smb_geting);
    private static PictureMarkerSymbol pms_gongce = createPms(AppUtils.getContext(),R.drawable.smb_gongce);
    private static PictureMarkerSymbol pms_gongchang = createPms(AppUtils.getContext(),R.drawable.smb_gongchang);
    private static PictureMarkerSymbol pms_hunqing = createPms(AppUtils.getContext(),R.drawable.smb_hunqing);
    private static PictureMarkerSymbol pms_jianhang = createPms(AppUtils.getContext(),R.drawable.smb_jianhang);
    private static PictureMarkerSymbol pms_jiaohang = createPms(AppUtils.getContext(),R.drawable.smb_jiaohang);
    private static PictureMarkerSymbol pms_jiayouzhan = createPms(AppUtils.getContext(),R.drawable.smb_jiayouzhan);
    private static PictureMarkerSymbol pms_liantong = createPms(AppUtils.getContext(),R.drawable.smb_liantong);
    private static PictureMarkerSymbol pms_lifa = createPms(AppUtils.getContext(),R.drawable.smb_lifa);
    private static PictureMarkerSymbol pms_lvdian = createPms(AppUtils.getContext(),R.drawable.smb_lvdian);
    private static PictureMarkerSymbol pms_meirong = createPms(AppUtils.getContext(),R.drawable.smb_meirong);
    private static PictureMarkerSymbol pms_nonghang = createPms(AppUtils.getContext(),R.drawable.smb_nonghang);
    private static PictureMarkerSymbol pms_police = createPms(AppUtils.getContext(),R.drawable.smb_police);
    private static PictureMarkerSymbol pms_qixiu = createPms(AppUtils.getContext(),R.drawable.smb_qixiu);
    private static PictureMarkerSymbol pms_quwei = createPms(AppUtils.getContext(),R.drawable.smb_quwei);
    private static PictureMarkerSymbol pms_rihua = createPms(AppUtils.getContext(),R.drawable.smb_rihua);
    private static PictureMarkerSymbol pms_shangdian = createPms(AppUtils.getContext(),R.drawable.smb_shangdian);
    private static PictureMarkerSymbol pms_shequ = createPms(AppUtils.getContext(),R.drawable.smb_shequ);
    private static PictureMarkerSymbol pms_shichang = createPms(AppUtils.getContext(),R.drawable.smb_shichang);
    private static PictureMarkerSymbol pms_shouji = createPms(AppUtils.getContext(),R.drawable.smb_shouji);
    private static PictureMarkerSymbol pms_shudian = createPms(AppUtils.getContext(),R.drawable.smb_shudian);
    private static PictureMarkerSymbol pms_shuiwu = createPms(AppUtils.getContext(),R.drawable.smb_shuiwu);
    private static PictureMarkerSymbol pms_ticai = createPms(AppUtils.getContext(),R.drawable.smb_ticai);
    private static PictureMarkerSymbol pms_tiyuchang = createPms(AppUtils.getContext(),R.drawable.smb_tiyuchang);
    private static PictureMarkerSymbol pms_wangba = createPms(AppUtils.getContext(),R.drawable.smb_wangba);
    private static PictureMarkerSymbol pms_wenti = createPms(AppUtils.getContext(),R.drawable.smb_wenti);
    private static PictureMarkerSymbol pms_wujin = createPms(AppUtils.getContext(),R.drawable.smb_wujin);
    private static PictureMarkerSymbol pms_xinyongshe = createPms(AppUtils.getContext(),R.drawable.smb_xinyongshe);
    private static PictureMarkerSymbol pms_xiuli = createPms(AppUtils.getContext(),R.drawable.smb_xiuli);
    private static PictureMarkerSymbol pms_xiyu = createPms(AppUtils.getContext(),R.drawable.smb_xiyu);
    private static PictureMarkerSymbol pms_xuexiao = createPms(AppUtils.getContext(),R.drawable.smb_xuexiao);
    private static PictureMarkerSymbol pms_yanjiu = createPms(AppUtils.getContext(),R.drawable.smb_yanjiu);
    private static PictureMarkerSymbol pms_yaodian = createPms(AppUtils.getContext(),R.drawable.smb_yaodian);
    private static PictureMarkerSymbol pms_yidong = createPms(AppUtils.getContext(),R.drawable.smb_yidong);
    private static PictureMarkerSymbol pms_yingyuan = createPms(AppUtils.getContext(),R.drawable.smb_yingyuan);
    private static PictureMarkerSymbol pms_yinhang = createPms(AppUtils.getContext(),R.drawable.smb_yinhang);
    private static PictureMarkerSymbol pms_yiyuan = createPms(AppUtils.getContext(),R.drawable.smb_yiyuan);
    private static PictureMarkerSymbol pms_youeryuan = createPms(AppUtils.getContext(),R.drawable.smb_youeryuan);
    private static PictureMarkerSymbol pms_youzheng = createPms(AppUtils.getContext(),R.drawable.smb_youzheng);
    private static PictureMarkerSymbol pms_zhaoxiang = createPms(AppUtils.getContext(),R.drawable.smb_zhaoxiang);
    private static PictureMarkerSymbol pms_zhengfu = createPms(AppUtils.getContext(),R.drawable.smb_zhengfu);
    private static PictureMarkerSymbol pms_zhensuo = createPms(AppUtils.getContext(),R.drawable.smb_zhensuo);
    private static PictureMarkerSymbol pms_zhonghang = createPms(AppUtils.getContext(),R.drawable.smb_zhonghang);
    private static PictureMarkerSymbol pms_gonghang = createPms(AppUtils.getContext(),R.drawable.smb_gonghang);
    private static PictureMarkerSymbol pms_jiudian = createPms(AppUtils.getContext(),R.drawable.smb_jiudian);
    private static PictureMarkerSymbol pms_default = createPms(AppUtils.getContext(),R.drawable.smb_default);

    public static PictureMarkerSymbol getPmsByName(String name){
       HashMap<String,PictureMarkerSymbol> map = getPmsMap();
       PictureMarkerSymbol pms =  map.get(name);
        if(pms == null || name == "" || name =="null") {
            return pms_default;
        }
        return pms;
    }

    private static HashMap<String,PictureMarkerSymbol> getPmsMap(){
        HashMap<String,PictureMarkerSymbol> map = new HashMap<>();
        map.put("警察",pms_police);
        map.put("五金",pms_wujin);
        map.put("体育馆",pms_tiyuchang);
        map.put("公厕",pms_gongce);
        map.put("办公楼",pms_bangonglou);
        map.put("加工修理",pms_xiuli);
        map.put("加油站",pms_jiayouzhan);
        map.put("区委",pms_quwei);
        map.put("医院",pms_yiyuan);
        map.put("商店",pms_shangdian);
        map.put("学校",pms_xuexiao);
        map.put("工厂",pms_gongchang);
        map.put("市场",pms_shichang);
        map.put("手机",pms_shouji);
        map.put("按摩",pms_anmo);
        map.put("文体",pms_wenti);
        map.put("旅店",pms_lvdian);
        map.put("服装",pms_fuzhuang);
        map.put("村庄",pms_cunzhuang);
        map.put("洗浴",pms_xiyu);
        map.put("烟酒",pms_yanjiu);
        map.put("照相",pms_zhaoxiang);
        map.put("理发",pms_lifa);
        map.put("电信",pms_dianxin);
        map.put("影院",pms_yingyuan);
        map.put("电脑",pms_diannao);
        map.put("电视台",pms_dianshitai);
        map.put("社区",pms_shequ);
        map.put("书店",pms_shudian);
        map.put("幼儿园",pms_youeryuan);
        map.put("交行",pms_jiaohang);
        map.put("歌厅",pms_geting);
        map.put("建行",pms_jianhang);
        map.put("中行",pms_zhonghang);
        map.put("邮政",pms_youzheng);
        map.put("信用社",pms_xinyongshe);
        map.put("体彩",pms_ticai);
        map.put("福彩",pms_fucai);
        map.put("汽修",pms_qixiu);
        map.put("政府",pms_zhengfu);
        map.put("农行",pms_nonghang);
        map.put("工行",pms_gonghang);
        map.put("日化",pms_rihua);
        map.put("婚庆",pms_hunqing);
        map.put("饭店",pms_fandian);
        map.put("银行",pms_yinhang);
        map.put("酒店",pms_jiudian);
        map.put("超市",pms_chaoshi);
        map.put("诊所",pms_zhensuo);
        map.put("药店",pms_yaodian);
        map.put("茶叶",pms_chaye);
        map.put("联通",pms_liantong);
        map.put("美容",pms_meirong);
        map.put("网吧",pms_wangba);
        map.put("税务",pms_shuiwu);
        map.put("移动",pms_yidong);
        return map;
    }

    private static PictureMarkerSymbol createPms(Context context,int resoureID){
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, resoureID);
        PictureMarkerSymbol pms =  new PictureMarkerSymbol(drawable);

        pms.setWidth(SYMBOL_SIZE);
        pms.setHeight(SYMBOL_SIZE);
        return pms;
    }
}
