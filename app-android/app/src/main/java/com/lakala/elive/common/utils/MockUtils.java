package com.lakala.elive.common.utils;

import android.util.Log;

import com.lakala.elive.Constants;
import com.lakala.elive.beans.FunctionMenuInfo;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.ShopVisitInfo;
import com.lakala.elive.beans.TaskInfo;
import com.lakala.elive.beans.PageModel;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.PageReqInfo;

import java.util.ArrayList;
import java.util.List;

public class MockUtils {

    /**
     * 模拟功能菜单列表
     * @return
     */
	public static List<FunctionMenuInfo> getMenuInfoList() {
		// TODO Auto-generated method stub
		 List<FunctionMenuInfo> menuInfoList = new ArrayList<FunctionMenuInfo>();
		 for(int i = 0; i < Constants.HOME_MENU_NAME.length ;i++){
			 FunctionMenuInfo menuInfo = new FunctionMenuInfo();
			 menuInfo.setMenuName(Constants.HOME_MENU_NAME[i]);
             menuInfo.setIconResId(Constants.HOME_MENU_ICON[i]);
			 menuInfoList.add(menuInfo);
		 }
		 return menuInfoList;
	}

    /**
     * 模拟商户列表
     * @return
     */
    public static List<MerShopInfo> getMerInfoList() {
        // TODO Auto-generated method stub
        List<MerShopInfo> merInfoList = new ArrayList<MerShopInfo>();
        for(int i=0; i<3 ;i++){
            MerShopInfo tmpInfo = new MerShopInfo();
            tmpInfo.setMerchantName("上海拉卡拉商服" + i);
            tmpInfo.setShopName("上海拉卡拉商服" + i);
            tmpInfo.setShopAddress("上海市普陀区中江路879号天地软件园");
            merInfoList.add(tmpInfo);
        }
        return merInfoList;
    }

    /**
     * 模拟商户列表
     * @return
     */
    public static List<ShopVisitInfo> getMerVisitInfoList() {
        // TODO Auto-generated method stub
        List<ShopVisitInfo> visitInfoList = new ArrayList<ShopVisitInfo>();
        for(int i=0; i<3 ;i++){
            ShopVisitInfo tmpInfo = new ShopVisitInfo();
            tmpInfo.setVisitData("2016-10-11" + i);
            tmpInfo.setVisitType("01");
            tmpInfo.setComments("上海市普陀区中江路879号天地软件园" + i);
            visitInfoList.add(tmpInfo);
        }
        return visitInfoList;
    }

    /**
     * 模拟商户列表
     * @return
     */
    public static void queryPageMerInfoList(final ApiRequestListener handler, final PageReqInfo pageReqInfo) {
        final PageModel<MerShopInfo> mPageList = new  PageModel<MerShopInfo>();
        new Thread(new Runnable() {
            public void run() {
                List<MerShopInfo> merInfoList = new ArrayList<MerShopInfo>();
                int start = pageReqInfo.getPageNo() * pageReqInfo.getPageSize() ;
                int end =  pageReqInfo.getPageNo() * pageReqInfo.getPageSize() + pageReqInfo.getPageSize() - 1;

                Log.e("queryPageMerInfoList","start:" + start);
                Log.e("queryPageMerInfoList","end:" + end);
                //模拟数据
                for(int i = start; i <= end;i++){
                    MerShopInfo tmpInfo = new MerShopInfo();
                    tmpInfo.setMerchantName("上海拉卡拉商服" + i);
                    tmpInfo.setShopName("上海拉卡拉商服" + i);
                    tmpInfo.setShopAddress("上海市普陀区中江路879号天地软件园");
                    merInfoList.add(tmpInfo);
                }

                mPageList.setRows(merInfoList);
                mPageList.setPageSize(3);
                mPageList.setPageNo(pageReqInfo.getPageNo());
                handler.onSuccess(NetAPI.ACTION_MER_PAGE_LIST,mPageList);
            }
        }).start();
    }


    /**
     * 模拟工单列表
     * @return
     */
    public static List<TaskInfo> getDealInfoList() {
        // TODO Auto-generated method stub
        List<TaskInfo> dealInfoList = new ArrayList<TaskInfo>();
        for(int i=0; i<3 ;i++){
            TaskInfo tmpInfo = new TaskInfo();
/*            tmpInfo.setDealId("客服工单" + i);
            tmpInfo.setDealDesc("紧急机器维修");
            tmpInfo.setStartDate("2016-09-20 11:00");

            MerShopInfo merchantInfo = new MerShopInfo();
            merchantInfo.setMerchantName("上海拉卡拉商服" + i);
            merchantInfo.setShopAddress("上海市普陀区中江路879号天地软件园");
            tmpInfo.setMerInfo(merchantInfo);*/

            dealInfoList.add(tmpInfo);
        }
        return dealInfoList;
    }

}
