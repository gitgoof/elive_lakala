package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.collection.CollectionsAmountInputActivity;
import com.lakala.shoudan.activity.kaola.KaoLaCreditActivity;
import com.lakala.shoudan.activity.messagecenter.MessageCenter2Activity;
import com.lakala.shoudan.activity.messagecenter.MessageDao;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageCount;
import com.lakala.shoudan.activity.more.MoreActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.UIUtils;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * 首页
 * Created by Administrator on 2017/2/23.
 */
public class FragmentMainTop extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "FragmentMainTop";
    private View view;
    private NavigationBar navigationBar;
    private PopupWindow popupWindow;
    private TextView tvAmount;
    private boolean isCollecting;
    private Double todayAmount;
    private DrawButtonClickListener2 listener;
    private static final Message.MSG_TYPE[] TYPES = {Message.MSG_TYPE.Publish,
            Message.MSG_TYPE.Trade,
            Message.MSG_TYPE.Business};
    private AdvertFragment advertFragment=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_top, container, false);
        initUi();
        return view;
    }

    public void initUi(){
        initNavigation();
        intPopuWindows();
        view.findViewById(R.id.iv_swiping).setOnClickListener(this);//刷卡收款
        view.findViewById(R.id.iv_scan).setOnClickListener(this);//扫码收款
        view.findViewById(R.id.ll_into_d0).setOnClickListener(this);//Do提款
        tvAmount= (TextView) view.findViewById(R.id.tv_into_num);
        listener = new DrawButtonClickListener2((AppBaseActivity) context);
        if(advertFragment==null){
            advertFragment=new AdvertFragment();
        }
        manager.beginTransaction().replace(R.id.fl_ad,advertFragment).commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getServerMsgCount2();
        if(!isCollecting){
            getCollectionAmount();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_swiping:
                //刷卡收款
                if (CommonUtil.isMerchantValid(context)) {
                    Intent intent = new Intent(context, CollectionsAmountInputActivity.class);
                    startActivity(intent);
                    PublicEnum.Business.setHome(true);
                    CollectionEnum.Colletcion.setData(null, true);
                }
                break;
            case R.id.iv_scan:
                // 商户状态,0:未开通;1:正在开通;2:冻结;3:审核未通过;4:修改资料并且审核通过
//                进入C扫B的业务流程；
                if (CommonUtil.isMerchantCompleted(context)) {
                    ScanCodeCollectionEnum.ScanCodeCollection.setData(null, true);
                    BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                    barcodeAccessManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection_Homepage);
                    barcodeAccessManager.check(true, true);
                }
                break;
            case R.id.ll_into_d0:
                listener.setStatistic(ShoudanStatisticManager.Do_Homepage_Detail);
                DoEnum.Do.setData(null,true,false);
                listener.onClick(v);
                break;
        }
    }

    public Double getTodayAmount() {
        return todayAmount;
    }
    private void getCollectionAmount() {
        isCollecting = true;
        tvAmount.setText("");
        tvAmount.post(new Runnable() {
            @Override
            public void run() {
                if(isCollecting){
                    int length = tvAmount.length();
                    if(length >= 5){
                        tvAmount.setText("");
                    }
                    tvAmount.append(".");
                    tvAmount.postDelayed(this,300);
                }
            }
        });
        CommonServiceManager.getInstance().queryTodayCollection(new CommonServiceManager.TodayCollectionCallback() {
            @Override
            public void onSuccess(Double amount) {
                updateCollectionAmount(amount);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                updateCollectionAmount(null);
            }
        });
    }

    private void updateCollectionAmount(Double amount) {
        if (amount != null) {
            todayAmount = amount;
        }
        isCollecting = false;
        DecimalFormat format = new DecimalFormat("0.00");
        tvAmount.setText(format.format(amount == null?0:amount));
    }

    private void darkenBackground(Float bgcolor){
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgcolor;

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getActivity().getWindow().setAttributes(lp);

    }
    private void intPopuWindows(){
        View view=getActivity().getLayoutInflater().inflate(R.layout.menu_right,null);
        popupWindow=new PopupWindow(getActivity());
        popupWindow.setWidth(UIUtils.dip2px(120));
        popupWindow.setHeight(UIUtils.dip2px(145));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f);
            }
        });
        //交易记录
        view.findViewById(R.id.tv_menu0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_Homepage, context);
                if (getTodayAmount()!=null) {//若已经成功获取进入收款金额，直接进交易管理，不需要重新获取
                    toTradeManage(getTodayAmount());
                    return;
                }
                showProgressWithNoMsg();
                CommonServiceManager.getInstance()
                        .queryTodayCollection(new CommonServiceManager.TodayCollectionCallback() {
                            @Override
                            public void onSuccess(Double amount) {
                                hideProgressDialog();
                                toTradeManage(amount);
                            }

                            @Override
                            public void onEvent(HttpConnectEvent connectEvent) {
                                hideProgressDialog();
                                toTradeManage(null);
                            }
                        });
            }
        });
        //用户信息
        view.findViewById(R.id.tv_menu1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                PublicEnum.Business.setPublic(true);
                PublicToEvent.messeage=false;
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.UPDATE_STATUS);
            }
        });
        //钱包
        view.findViewById(R.id.tv_menu2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                getActivity().startActivity(new Intent(getActivity(), KaoLaCreditActivity.class));
//                startActivity(new Intent(getActivity(), WalletHomeActivity.class));
//                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Homepage, context);
            }
        });
        //更多
        view.findViewById(R.id.tv_menu3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                startActivity(new Intent(getActivity(),MoreActivity.class));
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.More_Homenpage, context);
            }
        });
    }

    private void toTradeManage(Double amount) {
        Intent intent;
        intent = new Intent(context, TradeManageActivity.class);
        intent.putExtra("today_amount", amount);
        startActivity(intent);
    }

    private void initNavigation() {
        navigationBar = (NavigationBar) view.findViewById(R.id.main_navigation_bar);
        navigationBar.setBackButtonBackground(R.drawable.selector_message_center);
        navigationBar.setActionBtnBackground(R.drawable.selector_top_right);
        navigationBar.setBackgroundColor(getActivity().getResources().getColor(R.color.main_nav_blue));
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        startActivity(new Intent(context, MessageCenter2Activity.class));
                        break;
                    case action:
                        darkenBackground(0.9f);
                        popupWindow.showAsDropDown(navigationBar.getActionBtn(),-UIUtils.dip2px(80),-UIUtils.dip2px(15));
                        break;
                }
            }
        });
        navigationBar.setBottomImageVisibility(View.VISIBLE);
    }

    MessageDao messageDao;
    MessageCount mc;
    private void updateMsgCount(int server) {

        if (server > 0) {
            navigationBar.setHasMsg(true);
        } else {
            navigationBar.setHasMsg(false);
        }
        mc.setAllCount(server);
        messageDao.saveUnread(context, mc);
    }

    private void getServerMsgCount2() {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        int cnt = jsonObject.optInt("count", 0);
                        updateMsgCount(cnt);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getMessageCount2(
                "", callback
        );
        CommonServiceManager.getInstance().getMessageCount2(TYPES[0].name(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        int cnt = jsonObject.optInt("count", 0);
                        mc.setPublishCount(cnt);
                        messageDao.saveUnread(context, mc);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        CommonServiceManager.getInstance().getMessageCount2(TYPES[1].name(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        int cnt = jsonObject.optInt("count", 0);
                        mc.setTradeCount(cnt);
                        messageDao.saveUnread(context, mc);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        CommonServiceManager.getInstance().getMessageCount2(TYPES[2].name(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        int cnt = jsonObject.optInt("count", 0);
                        mc.setBusinessCount(cnt);
                        messageDao.saveUnread(context, mc);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
    }
}
