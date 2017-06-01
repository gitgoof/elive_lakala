package com.lakala.shoudan.activity.messagecenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.db.DBManager;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.bscanc.BScanCActivity;
import com.lakala.shoudan.activity.messagecenter.messageBean.BusinessMessage;
import com.lakala.shoudan.activity.messagecenter.messageBean.BusinessType;
import com.lakala.shoudan.activity.messagecenter.messageBean.FooterState;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageCount;
import com.lakala.shoudan.activity.messagecenter.messageBean.PublishMessage;
import com.lakala.shoudan.activity.messagecenter.messageBean.ServiceType;
import com.lakala.shoudan.activity.messagecenter.messageBean.TradeMessage;
import com.lakala.shoudan.activity.quickArrive.OnDayLoanClickListener;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.largeamountcollection.LargeAmountInputActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordDetailActivity;
import com.lakala.shoudan.adapter.MessageDetail2Adapter;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.component.NavigationBar;
import com.lidroid.xutils.db.sqlite.Selector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lakala.shoudan.datadefine.Message.MSG_TYPE.Business;
import static com.lakala.shoudan.datadefine.Message.MSG_TYPE.Publish;

/**
 * Created by huwei on 16/8/16.
 * <p>
 * 消息详情页面
 */
public class MessageDetail2Activity extends AppBaseActivity implements MessageDetail2Adapter.OnFunctionBtnClickListener {
    @Bind(R.id.fl_back)
    FrameLayout fl_back;
    @Bind(R.id.listview_msg)
    PullToRefreshListView mMessageList;
    @Bind(R.id.emptyView)
    LinearLayout emptyView;
    MessageDao messageDao;
    private int msg_type = 0;
    DBManager dbManager;
    MessageCount mc;
    private User user;
    //是否为上拉
    private boolean isPullUp = false;
    private boolean isPullDown = false;

    /*
    Publish("系统公告")
    Trade("交易通知")
    Business("业务通知")
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail2);
        ButterKnife.bind(this);

        getBundle(savedInstanceState);
        user = ApplicationEx.getInstance().getUser();
        String loginName = user.getLoginName();
        messageDao = MessageDao.getInstance(loginName);
        mc = messageDao.readUnread(this);
        dbManager = DBManager.getIntance(this, loginName);
        initUI();
        setListViewListener();
        getServiceData();
    }

    List<TradeMessage> mTradeList = new ArrayList<>();
    List<TradeMessage> mTotolTradeList = new ArrayList<>();
    List<BusinessMessage> mBusinessList = new ArrayList<>();
    List<BusinessMessage> mTotalBusinessList = new ArrayList<>();
    List<PublishMessage> mPublishList = new ArrayList<>();
    List<PublishMessage> mTotalPublishList = new ArrayList<>();
    MessageDetail2Adapter adapter;
    private Message.MSG_TYPE MSG_TYPE = Publish;
    private int page = 1;
    private int pageSize = 20;
    private boolean fromLocal;


    /**
     * 获取消息数据
     */


    private void getServiceData() {
        //判定需要请求的消息类型
        showProgressWithNoMsg();
        final CommonServiceManager manager = CommonServiceManager.getInstance();
        manager.getMessageList2(page, pageSize, ApplicationEx.getInstance().getUser().getLoginName(), MSG_TYPE, new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        hideProgressDialog();
                        if (resultServices.isRetCodeSuccess()) {
                            JSONArray array = null;
                            try {
                                JSONObject jsonobject = new JSONObject(resultServices.retData);
                                array = jsonobject.getJSONArray("messages");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mMessageList.onRefreshComplete();
                                ToastUtil.toast(MessageDetail2Activity.this, e.toString());
                            }
                            int arrayLength = 0;
                            if (array != null) {
                                if (array.length() > 0) {
                                    fromLocal = false;
                                    arrayLength = array.length();
                                } else {
                                    fromLocal = true;
                                }
                            }
                            for (int i = 0; i < arrayLength; i++) {
                                try {
                                    JSONObject json = array.getJSONObject(i);
                                    if (checkMsgType() == 0) {
                                        PublishMessage publishMessage = PublishMessage.parseObject(json);
//                                        CommonServiceManager.getInstance().setMessageReaded(publishMessage.getId());
                                        mPublishList.add(publishMessage);
                                    } else if (checkMsgType() == 1) {
                                        TradeMessage tradeMessage = TradeMessage.parseObject(json);
//                                        CommonServiceManager.getInstance().setMessageReaded(tradeMessage.getId());
                                        mTradeList.add(tradeMessage);
                                    } else {
                                        BusinessMessage businessMessage = BusinessMessage.parseObject(json);
//                                        CommonServiceManager.getInstance().setMessageReaded(businessMessage.getId());
                                        mBusinessList.add(businessMessage);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    hideProgressDialog();
                                    mMessageList.onRefreshComplete();
//                                    handleFailure();
                                }
                            }
                            loadData(MSG_TYPE);
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        hideProgressDialog();
                        handleFailure();
                        ToastUtil.toast(MessageDetail2Activity.this, R.string.socket_fail);
                    }
                }

        );
    }

    private void loadData(Message.MSG_TYPE Type) {
        switch (Type) {
            case Trade:
                if (!fromLocal) {
                    dbManager.saveOrupdateAll(mTradeList);
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                    } else {
                        Collections.reverse(mTradeList);
                    }
                    mTotolTradeList.addAll(0, mTradeList);
                } else {
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                        mTotolTradeList.addAll(mTradeList);
                    }
                }
                break;
            case Publish:
                if (!fromLocal) {
                    dbManager.saveOrupdateAll(mPublishList);
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                    } else {
                        Collections.reverse(mPublishList);
                    }
                    mTotalPublishList.addAll(0, mPublishList);
                } else {
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                        mTotalPublishList.addAll(mPublishList);
                    }
                }
                break;
            case Business:
                if (!fromLocal) {
                    dbManager.saveOrupdateAll(mBusinessList);
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                    } else {
                        Collections.reverse(mBusinessList);
                    }
                    mTotalBusinessList.addAll(0, mBusinessList);
                } else {
                    if (adapter == null) {
                        loadCacheMessageData(MSG_TYPE);
                        mTotalBusinessList.addAll(mBusinessList);
                    }
                }
                break;
        }
        fillList();//填充列表
    }

    private void fillList() {
        switch (MSG_TYPE) {
            case Publish:
                mMessageList.setMode(mTotalPublishList.size() >= pageSize ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                if (mTotalPublishList != null && mTotalPublishList.size() > 0) {
                    if (adapter == null) {
                        adapter = new MessageDetail2Adapter(mTotalPublishList, MessageDetail2Activity.this, MSG_TYPE);
                        adapter.setOnFunctionBtnClick(MessageDetail2Activity.this);
                        mMessageList.setAdapter(adapter);
                    } else {
                        adapter.addAll(mTotalPublishList);
                        mMessageList.onRefreshComplete();
                    }
                } else {
                    handleFailure();
                }
                break;
            case Trade:
                mMessageList.setMode(mTotalPublishList.size() >= pageSize ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                if (mTotolTradeList != null && mTotolTradeList.size() > 0) {
                    if (adapter == null) {
                        adapter = new MessageDetail2Adapter(MSG_TYPE, mTotolTradeList, MessageDetail2Activity.this);
                        adapter.setOnFunctionBtnClick(MessageDetail2Activity.this);
                        mMessageList.setAdapter(adapter);
                    } else {
                        adapter.addAll(mTotolTradeList);
                        mMessageList.onRefreshComplete();
                    }
                } else {
                    handleFailure();
                }
                break;
            case Business:
                mMessageList.setMode(mTotalPublishList.size() >= pageSize ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
                if (mTotalBusinessList != null && mTotalBusinessList.size() > 0) {
                    if (adapter == null) {
                        adapter = new MessageDetail2Adapter(MessageDetail2Activity.this, MSG_TYPE, mTotalBusinessList);
                        adapter.setOnFunctionBtnClick(MessageDetail2Activity.this);
                        mMessageList.setAdapter(adapter);
                    } else {
                        adapter.addAll(mTotalBusinessList);
                        mMessageList.onRefreshComplete();
                    }
                } else {
                    handleFailure();
                }
                break;
        }

    }

    private ProgressBar foorter_bar;
    private TextView tv_loadmore;
    View loadmore;

    private void setListViewListener() {
        mMessageList.setMode(PullToRefreshBase.Mode.BOTH);
        mMessageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapter.getItem(i - 1);
                showMsgDeleteDialog(item);
                return true;
            }
        });
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        mMessageList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isPullDown = true;
                page++;
                getServiceData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                setFooterMode(FooterState.GONE);
                handleLoadMore();
            }
        });

//        initFooter();
    }

    /**
     * 初始化listviewfooter
     */
    private void initFooter() {
        loadmore = LayoutInflater.from(this).inflate(R.layout.footer_loadmore, null);
        foorter_bar = (ProgressBar) loadmore.findViewById(R.id.footer_progressbar);
        tv_loadmore = (TextView) loadmore.findViewById(R.id.tv_loadmore);
//        tv_loadmore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setFooterMode(FooterState.LOADING);
//                handleLoadMore();
//            }
//        });
        mMessageList.getRefreshableView().addFooterView(loadmore);
        setFooterMode(FooterState.INVISIBLE);
    }

    private void handleLoadMore() {
        isPullUp = true;
        offset = adapter.getCount();
        mMessageList.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMessageList.onRefreshComplete();
//                setFooterMode(FooterState.NORMAL);
                loadMore(MSG_TYPE);
            }
        }, 500);
    }

    private void setFooterMode(FooterState state) {
        switch (state) {
            case LOADING:
                loadmore.setVisibility(View.VISIBLE);
                tv_loadmore.setVisibility(View.GONE);
                foorter_bar.setVisibility(View.VISIBLE);
                break;
            case NORMAL:
                loadmore.setVisibility(View.VISIBLE);
                tv_loadmore.setVisibility(View.VISIBLE);
                foorter_bar.setVisibility(View.GONE);
                break;
            case REMOVE:
                if (null != loadmore) {
                    mMessageList.getRefreshableView().removeFooterView(loadmore);
                }
                break;
            case GONE:
                loadmore.setVisibility(View.GONE);
                break;
            case INVISIBLE:
                loadmore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 上拉加载更多
     *
     * @param Type
     */
    private void loadMore(Message.MSG_TYPE Type) {
        loadCacheMessageData(Type);
        switch (Type) {
            case Publish:
                if (mPublishList.size() == 0) {
                    ToastUtil.toast(this, R.string.nomore);
                    mMessageList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    setFooterMode(FooterState.GONE);
                }
                mTotalPublishList.addAll(mPublishList);
                adapter.addAll(mTotalPublishList);
                break;
            case Trade:
                if (mTradeList.size() == 0) {
                    ToastUtil.toast(this, R.string.nomore);
                    mMessageList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    setFooterMode(FooterState.GONE);
                }
                mTotolTradeList.addAll(mTradeList);
                adapter.addAll(mTotolTradeList);
                break;
            case Business:
                if (mBusinessList.size() == 0) {
                    ToastUtil.toast(this, R.string.nomore);
                    mMessageList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//                    setFooterMode(FooterState.GONE);
                }
                mTotalBusinessList.addAll(mBusinessList);
                adapter.addAll(mTotalBusinessList);
                break;
        }

    }

    /**
     * 加载本地缓存的消息数据
     *
     * @param Type  消息类型
     */
    private int offset = 0;

    private void loadCacheMessageData(Message.MSG_TYPE Type) {
        Selector selector;
        switch (Type) {
            case Publish:
                selector = Selector.from(PublishMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
                mPublishList = (ArrayList<PublishMessage>) dbManager.getAllObject(PublishMessage.class, selector);
                break;
            case Trade:
                selector = Selector.from(TradeMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
                mTradeList = (ArrayList<TradeMessage>) dbManager.getAllObject(TradeMessage.class, selector);
                break;
            case Business:
                selector = Selector.from(BusinessMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
                mBusinessList = (ArrayList<BusinessMessage>) dbManager.getAllObject(BusinessMessage.class, selector);
                break;
        }
    }


//    private void getLocalBusiness() {
//        Selector selector = Selector.from(BusinessMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
//        mBusinessList = (ArrayList<BusinessMessage>) dbManager.getAllObject(BusinessMessage.class, selector);
//    }
//    private void getLocalTrade() {
//        Selector selector = Selector.from(TradeMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
//        mTradeList = (ArrayList<TradeMessage>) dbManager.getAllObject(TradeMessage.class, selector);
//    }
//    private void getLocalPublish() {
//        Selector selector = Selector.from(PublishMessage.class).orderBy("msgTime", true).limit(pageSize).offset(offset);
//        mPublishList = (ArrayList<PublishMessage>) dbManager.getAllObject(PublishMessage.class, selector);
//    }


    /**
     * 无消息状况
     */

    public void handleFailure() {
        mMessageList.setMode(PullToRefreshBase.Mode.DISABLED);
        mMessageList.onRefreshComplete();
        emptyView.setVisibility(View.VISIBLE);
        mMessageList.getRefreshableView().setEmptyView(emptyView);
    }


    private void getBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            msg_type = savedInstanceState.getInt(Constants.MSG_TYPE, 0);
        else {
            if (getIntent() != null)
                msg_type = getIntent().getIntExtra(Constants.MSG_TYPE, 0);
        }
    }

    private int checkMsgType() {
        return MSG_TYPE.getIndex();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.MSG_TYPE, msg_type);
    }

    private Drawable cornerDrawable(final int bgColor, float cornerradius) {
        final GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(cornerradius);
        bg.setColor(bgColor);
        return bg;
    }

    /**
     * 删除消息对话框
     *
     * @param item
     */

    public void showMsgDeleteDialog(final Object item) {
        DialogCreator.createFullContentDialog2(
                context, "取消",
                "确定", getString(R.string.id_delete_message),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (item instanceof TradeMessage) {
                            final TradeMessage tm = (TradeMessage) item;
                            dbManager.deleteObjectById(TradeMessage.class, tm.getId());
                            CommonServiceManager.getInstance().deleteMessage(tm.getId(), new ServiceResultCallback() {
                                @Override
                                public void onSuccess(ResultServices resultServices) {
                                    hideProgressDialog();
                                    adapter.removeItem(tm);
                                    LogUtil.print("=====TradeMessage已删除");
                                }

                                @Override
                                public void onEvent(HttpConnectEvent connectEvent) {
                                    hideProgressDialog();
                                    LogUtil.print("=====Trade消息删除失败");
                                }
                            });
                        } else if (item instanceof BusinessMessage) {
                            final BusinessMessage bm = (BusinessMessage) item;
                            dbManager.deleteObjectById(BusinessMessage.class, bm.getId());
                            CommonServiceManager.getInstance().deleteMessage(bm.getId(), new ServiceResultCallback() {
                                @Override
                                public void onSuccess(ResultServices resultServices) {
                                    hideProgressDialog();
                                    adapter.removeItem(bm);
                                    LogUtil.print("=====BusinessMessage已删除");
                                }

                                @Override
                                public void onEvent(HttpConnectEvent connectEvent) {
                                    hideProgressDialog();
                                    LogUtil.print("=====BusinessMessage删除失败");
                                }
                            });
//                    adapter.removeItem(bm);
                        } else if (item instanceof PublishMessage) {
                            final PublishMessage pm = (PublishMessage) item;
                            dbManager.deleteObjectById(PublishMessage.class, pm.getId());
                            CommonServiceManager.getInstance().deleteMessage(pm.getId(), new ServiceResultCallback() {
                                @Override
                                public void onSuccess(ResultServices resultServices) {
                                    adapter.removeItem(pm);
                                    hideProgressDialog();
                                    LogUtil.print("=====PublishMessage已删除");
                                }

                                @Override
                                public void onEvent(HttpConnectEvent connectEvent) {
                                    hideProgressDialog();
                                    LogUtil.print("=====PublishMessage已删除");
                                }
                            });
                        }

                        dialog.dismiss();
                    }
                }
        ).show();

    }

    @Override
    protected void initUI() {
        super.initUI();
        initListView();
        switch (msg_type) {
            case 0:
                navigationBar.setTitle("系统公告");
                MSG_TYPE = Publish;
                mc.setPublishCount(0);
                break;
            case 1:
                navigationBar.setTitle("交易通知");
                MSG_TYPE = Message.MSG_TYPE.Trade;
                mc.setTradeCount(0);
                break;
            case 2:
                navigationBar.setTitle("业务通知");
                MSG_TYPE = Business;
                mc.setBusinessCount(0);
                break;
            default:
                break;
        }
        //将消息的未读数量保存到本地
        messageDao.saveUnread(this, mc);
        navigationBar.setActionBtnText("");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
//                        clearTable();
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void clearTable() {
        dbManager.clearTable(BusinessMessage.class);
        dbManager.clearTable(PublishMessage.class);
        dbManager.clearTable(TradeMessage.class);
    }


    private void initListView() {
        // 得到加载刷新的布局，,可以设置全局的
        mMessageList.getRefreshableView().setSelector(R.color.transparent);
        ILoadingLayout loadingLayoutProxy = mMessageList.getLoadingLayoutProxy(
                true, false);
        // 设置释放时的文字提示
        loadingLayoutProxy.setReleaseLabel("松开刷新数据");
        // 设置下拉时的文字提示
        loadingLayoutProxy.setPullLabel("下拉刷新");
        // 设置最后一次更新的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());
        loadingLayoutProxy.setLastUpdatedLabel("更新时间:" + time);
        // 设置刷新中的文字提示
        loadingLayoutProxy.setRefreshingLabel("正在刷新....");
    }

    private final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public String formatTradeTime(String time) {
        try {
            time = sdf.format(parseFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    /**
     * 交易通知点击的回调
     *
     * @param ids
     * @param message
     */

    private Map<String, String> map = new HashMap<>();
    String jsonString = "";

    @Override
    public void OnTradeClickEvent(int ids, final TradeMessage message) {
        switch (ids) {
            case R.id.tv_function://查看交易详情
                showProgressWithNoMsg();
                ShoudanService.getInstance().queryTradeRecord(0, formatTradeTime(message.getTradeTime()), "", message.getTypeName(), message.getSid(), "", "", new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        hideProgressDialog();
                        if (resultServices.isRetCodeSuccess()) {
                            try {
                                JSONObject jsonObject = new JSONObject(resultServices.retData);
                                if (jsonObject == null)
                                    return;
                                jsonString = jsonObject.toString();
//                                map.put(message.getId(), jsonString);
//                                LogUtil.print(message.getId() + "======网络——交易详情=====" + jsonString);
//                                messageDao.saveMessageMap(MessageDetail2Activity.this, map);
                                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_dealDetail, context);
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.json_dealrecord, jsonString);
                                bundle.putBoolean(Constants.isfromMessage, true);
                                gotoActivity(RecordDetailActivity.class, false, bundle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideProgressDialog();
                            }
                        } else {
                            if (resultServices.isDealDetailLimit())
                                ToastUtil.toast(context, "数据正在处理中，请稍后查看");
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        hideProgressDialog();
                        ToastUtil.toast(MessageDetail2Activity.this, R.string.socket_fail);
                        LogUtil.print(connectEvent.getDescribe());

                    }
                });

                break;
        }
    }

    /**
     * 业务通知的点击回调
     *
     * @param message
     */
    @Override
    public void OnBusinessClickEvent(int ids, BusinessMessage message) {
        switch (ids) {
            case R.id.tv_function:
                handleBusinessEvent(message);
                break;
        }
    }

    /**
     * 处理业务消息
     *
     * @param message
     */
    private void handleBusinessEvent(BusinessMessage message) {
        String status = message.getStatus();
        try {
            switch (BusinessType.valueOf(message.getTypeName())) {
                case ONE_DAY_LOAN:
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case FAILURE:
                        case SUCCESS:
                            new OnDayLoanClickListener(context).onClick(null);
                            break;
                    }
                    break;
                case UP_MERCHANT_LEVEL_FIRST:
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case FAILURE:
                            PublicToEvent.setMesseage();
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_firstUpgrade, this);
                            ActiveNaviUtils.start(this, ActiveNaviUtils.Type.UPDATE_STATUS);
                            break;
                    }
                    break;
                case UP_MERCHANT_LEVEL_SECOND:
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case FAILURE:
                            PublicToEvent.setMesseage();
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_secondUpgrade, this);
                            ActiveNaviUtils.start(this, ActiveNaviUtils.Type.UPDATE_STATUS);
                            break;
                    }
                    break;
                case UP_MERINFO_ACCOUNTNO://收款账户变更
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case FAILURE:
                            PublicToEvent.setMesseage();
//                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_secondUpgrade, this);
                            ActiveNaviUtils.start(this, ActiveNaviUtils.Type.UPDATE_STATUS);
                            break;
                    }
                    break;
                case MERCHANT_APPLAY://商户开通
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case FAILURE:
                            PublicToEvent.setMesseage();
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_merchantCheck, this);
                            ActiveNaviUtils.start(this, ActiveNaviUtils.Type.UPDATE_STATUS);
                            break;
                    }
                    break;
                case D0_APPLAY://立即提款
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case SUCCESS:
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_immediateWithdraw, this);
                            new DrawButtonClickListener2(context).onClick(null);
                            break;
                        case FAILURE:
                            new DrawButtonClickListener2(context).onClick(null);
                            break;
                    }
                    break;
                case Wechat_APPLAY:
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case SUCCESS:
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_scanCode, this);
                            getMerQRCode();
                            break;
                        case FAILURE:
                            BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager((AppBaseActivity) context);
                            barcodeAccessManager.check(true, true);
                            break;
                    }
                    break;

                case BLimit_APPLAY:
                    switch (BusinessType.TypeStatus.valueOf(status)) {
                        case SUCCESS:
                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Trade_BLimit, this);
                            startActivity(new Intent(context, LargeAmountInputActivity.class));
                            break;
                        case FAILURE:
                            LargeAmountAccessManager largeAmountAccessManager = new LargeAmountAccessManager((AppBaseActivity) context);
//                        largeAmountAccessManager.setStatistic(ShoudanStatisticManager.Advert_Finance_Purchance);
                            largeAmountAccessManager.check();
//                        startActivity(new Intent(context, LargeAmountInstructionActivity.class));
                            break;
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String merQrCode;

    /**
     * 微信开户开通入口
     */
    private void getMerQRCode() {
        showProgressWithNoMsg();
        ShoudanService.getInstance().getMerQRCode(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        merQrCode = jsonObject.optString("merQrcode");
                        context.hideProgressDialog();
                        BScanCActivity.start(context, "COMPLETED", merQrCode, null);
                    } catch (JSONException e) {
                        context.hideProgressDialog();
                        e.printStackTrace();
                    }
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
            }
        });

    }

    private AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();

    /**
     * 检查更新
     */
    private void checkAppUpdate() {
        appUpgradeController.setCtx(this);
        appUpgradeController.check(true, true);
    }

    /**
     * 系统公告功能区点击回调
     *
     * @param ids
     * @param message
     */
    @Override
    public void OnPublishClickEvent(int ids, PublishMessage message) {
        switch (ids) {
            case R.id.tv_function_left://查看详情
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_detailsShare, this);
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_right://立即体验
                handleServiceAction(message);
                break;
            case R.id.iv_content_publish://点击图片
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_middle://版本升级
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_versionUpdate, this);
                checkAppUpdate();
                break;
        }
    }

    /**
     * 功能区单个按钮居中为立即体验
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnPublishClickEvent1(int ids, PublishMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                handleServiceAction(message);
                break;
        }
    }

    /**
     * 功能区单个按钮居中为查看详情
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnPublishClickEvent2(int ids, PublishMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                forwardAdShareActivity(url, message.getContentImageTitle());
                break;
        }
    }

    /**
     * 版本升级在右边的回调
     *
     * @param ids
     * @param
     */
    @Override
    public void OnPublishClickEvent3(int ids) {
        switch (ids) {
            case R.id.tv_function_right:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_versionUpdate, this);
                checkAppUpdate();
                break;
        }
    }

    private void handleServiceAction(PublishMessage message) {
        int serVersionCode = 0;
        if (!TextUtils.isEmpty(message.getVersion()))
            serVersionCode = Integer.parseInt(message.getVersion());
        int curVersionCode = Integer.parseInt(Util.getVersionCode());
        if (serVersionCode > curVersionCode) {
            checkAppUpdate();
        } else {
            skipToBusinessPage(message);
        }
    }

    /**
     * 系统公告进入各业务
     *
     * @param message
     */
    private void skipToBusinessPage(PublishMessage message) {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MessageCenter_Publish_business, this);
        String serviceActionURL = message.getServiceActionURL();
        try {
            switch (ServiceType.valueOf(serviceActionURL)) {
                case businessTypeImmediateWithdrawal://立即提款
                    new DrawButtonClickListener2(this).onClick(null);
                    break;
                case businessTypeCreditCardCollection://刷卡收款
                    if (CommonUtil.isMerchantValid(context)) {
                        BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                    }
                    break;
                case businessTypeSweepCodeCollection://扫码收款
                    if (CommonUtil.isMerchantValid(context)) {
                        getMerQRCode();
                    }
                    break;
                case businessTypeConductFinancialTransactions://理财
                    FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
//                financeRequestManager.setStatistic(ShoudanStatisticManager.Finance_HomePage);
                    financeRequestManager.startFinance(this);
                    break;
                case businessTypeLoan://贷款
                    ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.LOAN_BUSINESS);
                    break;
                case businessTypeAPieceOfLndiana://一块夺宝
                    ActiveNaviUtils.start(this, ActiveNaviUtils.Type.TREASURE);
                    break;
                case businessTypeCreditCardPayment://信用卡还款
                    BusinessLauncher.getInstance().start("creditcard_payment");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forwardActivity(Class forwardActivity) {
        Intent intent = new Intent(context, forwardActivity);
        startActivity(intent);
    }

    private void forwardAdShareActivity(String url, String title) {
        startActivity(new Intent(this, AdShareActivity.class).putExtra("url", url).putExtra("title", title));
    }


    /**
     * 业务中_系统回调
     */
    @Override
    public void OnPublishBusinessClickEvent(int ids, BusinessMessage message) {
        switch (ids) {
            case R.id.tv_function_left://查看详情
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_right://立即体验
                handleServiceAction_bp(message);
                break;
            case R.id.iv_content_publish://点击图片
                forwardAdShareActivity(message.getDetailsClickURL(), message.getContentImageTitle());
                break;
            case R.id.tv_function_middle://版本升级
                checkAppUpdate();
                break;
        }
    }


    /**
     * 功能区单个按钮居中为立即体验
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnPublishBusinessClickEvent1(int ids, BusinessMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                handleServiceAction_bp(message);
                break;
        }
    }

    /**
     * 功能区单个按钮居中为查看详情
     *
     * @param ids
     * @param message
     * @param url
     */
    @Override
    public void OnPublishBusinessClickEvent2(int ids, BusinessMessage message, String url) {
        switch (ids) {
            case R.id.tv_function_middle:
                forwardAdShareActivity(url, message.getContentImageTitle());
                break;
        }
    }

    /**
     * 版本升级在右边的回调
     *
     * @param ids
     * @param
     */
    @Override
    public void OnPublishBusinessClickEvent3(int ids) {
        switch (ids) {
            case R.id.tv_function_right:
                checkAppUpdate();
                break;
        }
    }

    private void handleServiceAction_bp(BusinessMessage message) {
        int serVersionCode = 0;
        if (!TextUtils.isEmpty(message.getVersion()))
            serVersionCode = Integer.parseInt(message.getVersion());
        int curVersionCode = Integer.parseInt(Util.getVersionCode());
        LogUtil.d("MessageDetail2Activity", "serVersionCode:" + serVersionCode + "curVersionCode:" + curVersionCode);
        if (serVersionCode > curVersionCode) {
            checkAppUpdate();
        } else {
            skipToBusinessPage(message);
        }
    }

    private void skipToBusinessPage(BusinessMessage message) {
        String serviceActionURL = message.getServiceActionURL();
        try {
            switch (ServiceType.valueOf(serviceActionURL)) {
                case businessTypeImmediateWithdrawal://立即提款
                    new DrawButtonClickListener2(this).onClick(null);
                    break;
                case businessTypeCreditCardCollection://刷卡收款
                    if (CommonUtil.isMerchantValid(context)) {
                        BusinessLauncher.getInstance().start("collection_transaction");//收款交易
                    }
                    break;
                case businessTypeSweepCodeCollection://扫码收款
                    if (CommonUtil.isMerchantValid(context)) {
                        getMerQRCode();
                    }
                    break;
                case businessTypeConductFinancialTransactions://理财
                    FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
//                financeRequestManager.setStatistic(ShoudanStatisticManager.MessageCenter_Trade_firstUpgrade);
                    financeRequestManager.startFinance(this);
                    break;
                case businessTypeLoan://贷款
                    ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.LOAN_BUSINESS);
                    break;
                case businessTypeAPieceOfLndiana://一块夺宝
                    ActiveNaviUtils.start(this, ActiveNaviUtils.Type.TREASURE);
                    break;
                case businessTypeCreditCardPayment://信用卡还款
                    BusinessLauncher.getInstance().start("creditcard_payment");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
