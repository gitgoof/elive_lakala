package com.lakala.shoudan.activity.merchantmanagement;

import com.lakala.shoudan.R;

/**
 * Created by Administrator on 2016/8/27 0027.
 */
public enum Level {
    //等级icon输出
    V1("初来乍到", R.drawable.level_icon_clzd),
    V2("生财有道", R.drawable.level_icon_scyd),
    V3("日进斗金", R.drawable.level_icon_rjdj),
    V4("财源滚滚", R.drawable.level_icon_cygg),
    V5("富甲一方", R.drawable.level_icon_fjyf),
    MV("家财万贯", R.drawable.level_icon_jcwg),
    //等级帽子icon输出
    V1HAT("初来乍到", R.drawable.level_icon_ss),
    V2HAT("生财有道", R.drawable.level_icon_zg),
    V3HAT("日进斗金", R.drawable.level_icon_cz),
    V4HAT("财源滚滚", R.drawable.level_icon_yw),
    V5HAT("富甲一方", R.drawable.level_icon_ag),
    MVHAT("家财万贯", R.drawable.level_icon_dc);


    private String lvName;
    private int lvImg;

    Level(String lvName, int lvImg) {
        this.lvName=lvName;
        this.lvImg=lvImg;
    }

    public String getLvName() {
        return lvName;
    }

    public void setLvName(String lvName) {
        this.lvName = lvName;
    }

    public int getLvImg() {
        return lvImg;
    }

    public void setLvImg(int lvImg) {
        this.lvImg = lvImg;
    }
}
