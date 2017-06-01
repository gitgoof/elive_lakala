package com.lakala.elive.map.activity;

import java.io.Serializable;

/**
 * Created by gaofeng on 2017/3/21.
 * 店铺信息
 */

public class MyShop implements Serializable {

    /**
     * 纬度
     */
    public double latitude;
    /**
     * 经度
     */
    public double longitude;
    /**
     * 角标
     */
    public int index;
    /**
     * 店铺名称
     */
    public String name;
    /**
     * 信息存储
     */
    public Object object;
    /**
     * 显示类型
     */
    public int style;
    /**
     * 是否显示
     */
    public boolean isShow;
    /**
     * 店铺id
     */
    public String shopNo;
    /**
     * 工单ID
     */
    public String taskId;

}
