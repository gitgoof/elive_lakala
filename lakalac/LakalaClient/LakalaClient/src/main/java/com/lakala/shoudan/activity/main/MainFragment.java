package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AppConfig;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.UndoEnum;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.collection.CollectionsAmountInputActivity;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceTransInfo;
import com.lakala.shoudan.activity.messagecenter.MessageCenter2Activity;
import com.lakala.shoudan.activity.messagecenter.MessageDao;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageCount;
import com.lakala.shoudan.activity.more.MoreActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.adapter.MainGridMenuAdapter;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.shoudan.component.HomeImageView;
import com.lakala.shoudan.component.TranslationYGridView;
import com.lakala.shoudan.datadefine.MainMenu;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.component.NavigationBar;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LMQ on 2015/12/14.
 */
public class MainFragment extends BaseFragment {

    private static final String TAG = "MainFragment";
    private DrawerFragment drawerFragment;
    private AdvertFragment advertFragment;
    private View rippleLayout;//
    private HomeImageView rippleMenu;
    private View contentView;
    private TranslationYGridView gridRippleMenu;
    private NavigationBar navigationBar;
    private MainGridMenuAdapter menuAdapter;
    private ObjectAnimator bgAnim;
    private View vBg;
    private Bitmap popBgBitmap;
    private List<MainMenu> menuList;
    private static final Message.MSG_TYPE[] TYPES = {Message.MSG_TYPE.Publish,
            Message.MSG_TYPE.Trade,
            Message.MSG_TYPE.Business};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawerFragment = new DrawerFragment();
        advertFragment = new AdvertFragment();
        manager.beginTransaction().replace(R.id.fragment_drawer, drawerFragment)
                .replace(R.id.fragment_advert, advertFragment).commit();
        contentView = view.findViewById(R.id.content_view);
        vBg = view.findViewById(R.id.v_bg);
        messageDao = MessageDao.getInstance(
                ApplicationEx.getInstance().getUser().getLoginName());
        mc = new MessageCount();
        initNavigation(view);
        initBottomMenu(view);
        initGridRippleMenu(view);
        initRippleLayout(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        User user = ApplicationEx.getInstance().getUser();
        AppConfig appConfig = user.getAppConfig();
        if (!appConfig.isRentCollectionEnabled() && !appConfig.isContributePaymentEnabled()) {
            menuAdapter.hideContribute();
        } else {
            menuAdapter.showContribute();
        }
        initUserInfo();
//        getServerMsgCount();
        getServerMsgCount2();
        initAdv2Listener();
        initAdv1();
        checkProgress();
        isShowLoan();
    }

    private void initAdv1() {
        if (advertFragment != null) {
            advertFragment.initAdv1();
        }
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

    private void initUserInfo() {
        LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        User user = ApplicationEx.getInstance().getSession().getUser();
                        JSONObject data = new JSONObject(resultServices.retData);
                        LogUtil.d("MainFragment--------", "data:" + data);
                        user.initMerchantAttrWithJson(data);
                        ApplicationEx.getInstance().getUser().save();
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        ToastUtil.toast(context, "数据解析异常");
                        hideProgressDialog();
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                    hideProgressDialog();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context, R.string.socket_fail);
                hideProgressDialog();
            }
        })).execute();
    }

    private void checkProgress() {
        if (context instanceof MainActivity) {
            ((MainActivity) context).hideProgressDialog();
        }
    }

    private void initAdv2Listener() {
        if (advertFragment != null) {
            AdvertFragment.OnPageChangedListener adv2ChangedListener = new AdvertFragment
                    .OnPageChangedListener() {
                @Override
                public void onPageChanged() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (popBgBitmap != null && !popBgBitmap.isRecycled()) {
                                popBgBitmap.recycle();
                                popBgBitmap = null;
                            }
                            Bitmap bitmap = CommonUtil.takeScreenshot(contentView);
                            popBgBitmap = CommonUtil.doBlur(bitmap, 3, false);
                        }
                    }).start();
                }
            };
            advertFragment.setOnAdv2ChangedListener(adv2ChangedListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (advertFragment != null) {
            advertFragment.setOnAdv2ChangedListener(null);
        }
    }
//
//    private void getServerMsgCount() {
//        ServiceResultCallback callback = new ServiceResultCallback() {
//            @Override
//            public void onSuccess(ResultServices resultServices) {
//                hideProgressDialog();
//                if (resultServices.isRetCodeSuccess()) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(resultServices.retData);
//                        int cnt = jsonObject.optInt("count", 0);
//                        updateMsgCount(cnt);
//                    } catch (JSONException e) {
//                        hideProgressDialog();
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onEvent(HttpConnectEvent connectEvent) {
//                hideProgressDialog();
//                LogUtil.print(connectEvent.getDescribe());
//            }
//        };
//        CommonServiceManager.getInstance().getMessageCount(
//                ApplicationEx.getInstance().getUser().getLoginName(), callback
//        );
//    }

    MessageDao messageDao;
    MessageCount mc;

    private void updateMsgCount(int server) {

//        int localCnt = messageDao.getUnreadMsgCount();
//        int cnt = localCnt + server;
        if (server > 0) {
            navigationBar.setHasMsg(true);
        } else {
            navigationBar.setHasMsg(false);
        }
        mc.setAllCount(server);
        messageDao.saveUnread(context, mc);
    }

    private void initGridRippleMenu(View view) {
        gridRippleMenu = (TranslationYGridView) view.findViewById(R.id.grid_ripple_menu);
        menuList = new ArrayList<>();
        menuList.addAll(Arrays.asList(MainMenu.values()));
        menuList.remove(MainMenu.代金券收款);
        menuList.remove(MainMenu.积分购);
        menuList.remove(MainMenu.贷款);
        menuAdapter = new MainGridMenuAdapter(context, menuList);
        gridRippleMenu.setAdapter(menuAdapter);
        gridRippleMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MainMenu menu = menuAdapter.getItem(position);
                reverseRippleAnim();
                gridRippleMenu.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        menuClickedAction(menu);
                    }
                }, 300);
            }
        });
        gridRippleMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int top = gridRippleMenu.getTop();
                int bottom = gridRippleMenu.getBottom();
                int childCount = gridRippleMenu.getChildCount();
                View lastChild = gridRippleMenu.getChildAt(childCount - 1);
                int spacing = 0;
                if (lastChild != null && lastChild.getBottom() < bottom) {
                    spacing = bottom - lastChild.getBottom();
                    top += spacing;
                } else {
                    spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                            getResources().getDisplayMetrics());
                    gridRippleMenu.setPadding(gridRippleMenu.getPaddingLeft(), spacing,
                            gridRippleMenu.getPaddingRight(),
                            gridRippleMenu.getPaddingRight());
                }
                gridRippleMenu.setStartY(bottom).setEndY(top);
            }
        });
    }

    private void menuClickedAction(MainMenu menu) {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Home_Public, getActivity());
        Intent intent;
        PublicEnum.Business.setPublic(true);
        switch (menu) {
            case 理财:
                FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
                financeRequestManager.startFinance((AppBaseActivity) context);
                break;
            case 贷款:
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.LOAN_BUSINESS);
                // forwardActivity(LoanMenuMainActivity.class);
                break;
            case 一块夺宝:
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.TREASURE);
                break;
            case 活动专区:
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.HDZQ);
                break;
            case 特约商户缴费:
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.TE_YUE);
                break;
            case 用户信息:
                PublicEnum.Business.setPublic(true);
                PublicToEvent.messeage = false;
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.UPDATE_STATUS);
                break;
            case 交易记录:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_Homepage, context);
                if (drawerFragment != null && drawerFragment
                        .getTodayAmount() != null) {//若已经成功获取进入收款金额，直接进交易管理，不需要重新获取
                    toTradeManage(drawerFragment.getTodayAmount());
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
                break;
            case 密码管理:
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.AN_QUAN);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Main_Pass_Admin, context);
                break;
            case 更多:
                forwardActivity(MoreActivity.class);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.More_Homenpage, context);
                break;
            case 积分购:
//                forwardActivity(IntegralSmsVertifyActivity.class);
//                forwardActivity(IntegralMainActivity.class);
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.INTEGRAL);
                break;
            case 刷卡收款:
                if (CommonUtil.isMerchantValid(context)) {
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
//                            .Collection, context);
//                    Intent intent3 = new Intent(context, CollectionsAmountInputActivity.class);
//                    startActivity(intent3);
                    CollectionEnum.Colletcion.setData(null, false);
                    BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                }
                break;
            case 扫码收款:
                if (CommonUtil.isMerchantCompleted(context)) {
                    ScanCodeCollectionEnum.ScanCodeCollection.setData(null, false);
                    BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                    barcodeAccessManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection_Homepage_Public);
                    barcodeAccessManager.check(true, true);
                }
                break;
            case 撤销交易:
                if (CommonUtil.isMerchantValid(context)) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo, context);
                    UndoEnum.Undo.setAdvertId("");
                    BusinessLauncher.getInstance().start("revocation");
                }
                break;
            case 代金券收款:
                if (CommonUtil.isMerchantValid(context)) {
                    ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.COUPON_STATUS);
                }
                break;
            case 大额收款:
                if (CommonUtil.isMerchantValid(context)) {
                    LargeAmountAccessManager largeAmountAccessManager = new LargeAmountAccessManager((AppBaseActivity) context);
                    largeAmountAccessManager.setStatistic(ShoudanStatisticManager.LargeAmount_Collection);
                    largeAmountAccessManager.check();
                    LargeAmountEnum.LargeAmount.setAdvertId("");
                }
                break;
            case 立即提款:
                new DrawButtonClickListener((AppBaseActivity) context)
                        .setStatistic(ShoudanStatisticManager.Do_Public_Input)
                        .onClick(null);
                DoEnum.Do.setIsCollectionPage(true);
                break;
            case 信用卡还款:
                BusinessLauncher.getInstance().start("creditcard_payment");
                break;
            case 转账汇款:
                BusinessLauncher.getInstance().start("remittance");
                break;
            case 余额查询:
                intent = new Intent();
                intent.putExtra(ConstKey.TRANS_INFO, new BalanceTransInfo());
                BusinessLauncher.getInstance().start("balance_query", intent);
                break;
            case 手机充值:
                BusinessLauncher.getInstance().start("mobile_recharge");
                break;


        }
    }

    private void toTradeManage(Double amount) {
        Intent intent;
        intent = new Intent(context, TradeManageActivity.class);
        intent.putExtra("today_amount", amount);
        startActivity(intent);
    }

    public void forwardActivity(Class forwardActivity) {
        Intent intent = new Intent(context, forwardActivity);
        startActivity(intent);
    }

    private void initRippleLayout(View view) {
        rippleLayout = view.findViewById(R.id.rippleLayout);
        rippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseRippleAnim();
            }
        });
        bgAnim = ObjectAnimator.ofFloat(rippleLayout, "alpha", 0, 1);
        bgAnim.setDuration(500);
        rippleMenu = (HomeImageView) view.findViewById(R.id.iv_ripple_menu);
        rippleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseRippleAnim();
            }
        });
        view.findViewById(R.id.fl_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseRippleAnim();
            }
        });
    }

    private void startRippleAnim() {
        rippleLayout.post(new Runnable() {
            @Override
            public void run() {
                if (rippleLayout != null) {
                    rippleLayout.setVisibility(View.VISIBLE);
                }
                gridRippleMenu.start();
                bgAnim.start();
                if (rippleMenu == null) {
                    return;
                }
            }
        });
    }

    private void reverseRippleAnim() {
        rippleLayout.post(new Runnable() {
            @Override
            public void run() {
                gridRippleMenu.reverse();
                bgAnim.reverse();
                rippleLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleLayout.setVisibility(View.GONE);
                        contentView.setVisibility(View.VISIBLE);
                    }
                }, 300);
            }
        });
    }

    private void initNavigation(View view) {
        navigationBar = (NavigationBar) view.findViewById(R.id.main_navigation_bar);
        navigationBar.setBackButtonBackground(R.drawable.selector_message_center);
        navigationBar.setActionBtnBackground(R.drawable.selector_wallet);
        navigationBar.setBackgroundColor(getActivity().getResources().getColor(R.color.main_nav_blue));
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        forwardActivity(MessageCenter2Activity.class);
                        break;
                    case action:
                        Intent intent = new Intent(getActivity(), WalletHomeActivity.class);
                        startActivity(intent);
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Homepage, context);
                        break;
                }
            }
        });
        navigationBar.setBottomImageVisibility(View.VISIBLE);
    }

    /**
     * @param view 底部view的点击处理
     */
    private void initBottomMenu(View view) {
        view.findViewById(R.id.tv_swiper_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷卡收款
                if (CommonUtil.isMerchantValid(context)) {
                    Intent intent = new Intent(context, CollectionsAmountInputActivity.class);
                    startActivity(intent);
                    PublicEnum.Business.setHome(true);
                    CollectionEnum.Colletcion.setData(null, true);
                    drawerFragment.setUpdateOnResume(true);
                }
            }
        });
        view.findViewById(R.id.tv_barcode_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 商户状态,0:未开通;1:正在开通;2:冻结;3:审核未通过;4:修改资料并且审核通过
//                进入C扫B的业务流程；
                if (CommonUtil.isMerchantCompleted(context)) {
                    ScanCodeCollectionEnum.ScanCodeCollection.setData(null, true);
                    BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                    barcodeAccessManager.setStatistic(ShoudanStatisticManager.Scan_Code_Collection_Homepage);
                    barcodeAccessManager.check(true, true);
                    drawerFragment.setUpdateOnResume(true);
                }
            }
        });
        view.findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示菜单
                updatePopBg();
                gridRippleMenu.setSelection(0);
            }
        });
    }

    private void updatePopBg() {
        if (popBgBitmap == null) {//未取得过模糊背景
            Bitmap bitmap = CommonUtil.takeScreenshot(contentView);
            popBgBitmap = CommonUtil.doBlur(bitmap, 3, false);
        }
        vBg.setBackgroundDrawable(new BitmapDrawable(popBgBitmap));
        ((AppBaseActivity) context).getNavigationBar().setVisibility(View.GONE);
        startRippleAnim();
    }

    /**
     * 查询是否显示贷款
     */
    public void isShowLoan() {
        String url = "v1.0/meta/APP_CONFIG_MPOS_" + BuildConfig.VERSION_CODE;
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(context, url, HttpRequest.RequestMethod.GET, true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if ("000000".equals(resultServices.retCode)) {
                    try {
                        LogUtil.print(TAG, resultServices.retData);
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String str = jsonObject.optString("extInfo");
                        if ("true".equals(str)) {
                            if (!menuList.contains(MainMenu.贷款)) {
                                menuList.add(6, MainMenu.贷款);
                                menuAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (menuList.contains(MainMenu.贷款)) {
                                menuList.remove(MainMenu.贷款);
                                menuAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
            }
        }));
        businessRequest.execute();
    }

}
