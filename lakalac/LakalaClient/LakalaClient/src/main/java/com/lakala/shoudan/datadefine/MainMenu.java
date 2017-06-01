package com.lakala.shoudan.datadefine;

import com.lakala.shoudan.R;

/**
 * Created by LMQ on 2015/12/17.
 */
public enum MainMenu {
    刷卡收款("刷卡收款", R.drawable.home_icon_sksk, R.drawable.pic_skmf),
    扫码收款("扫码收款", R.drawable.home_icon_smsk),
    撤销交易("撤销交易", R.drawable.home_icon_cxjy),
    代金券收款("代金券收款", R.drawable.home_icon_djqsk),
    大额收款("大额收款", R.drawable.home_icon_desk),
    立即提款("立即提款", R.drawable.home_icon_ljtk),
    理财("理财", R.drawable.home_icon_lc, R.drawable.pic_cf),
    贷款("贷款", R.drawable.home_icon_dk),
    一块夺宝("一块夺宝", R.drawable.home_icon_ykdb),
    信用卡还款("信用卡还款", R.drawable.home_icon_xykhk, R.drawable.pic_sh),
    转账汇款("转账汇款", R.drawable.home_icon_zzhk),
    余额查询("余额查询", R.drawable.home_icon_yecx),
    积分购("积分购", R.drawable.home_icon_jfg),
    手机充值("手机充值", R.drawable.home_icon_sjcz),
    活动专区("活动专区", R.drawable.home_icon_hdzq, R.drawable.pic_qt),
    特约商户缴费("特约商户缴费", R.drawable.home_icon_tyshjf),
    用户信息("用户信息", R.drawable.home_icon_yhgl),
    交易记录("交易记录", R.drawable.home_icon_jygl),
    密码管理("密码管理", R.drawable.home_icon_aqgl),
    更多("更多", R.drawable.home_icon_gd);

    int icon;
    String menuName;
    int type;

    MainMenu(String name, int icon, int type) {
        this.icon = icon;
        this.menuName = name;
        this.type = type;
    }

    MainMenu(String name, int icon) {
        this.icon = icon;
        this.menuName = name;
    }

    public int getIcon() {
        return icon;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getType() {
        return type;
    }
}
