package com.lakala.shoudan.activity.messagecenter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.messagecenter.messageBean.MessageCount;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.datadefine.Message;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by huwei on 16/8/15.
 */
public class MessageCenter2Activity extends AppBaseActivity {
    @Bind(R.id.rl_bus)
    RelativeLayout rl_bus;
    @Bind(R.id.rl_pub)
    LinearLayout rl_pub;
    @Bind(R.id.rl_trade)
    RelativeLayout rl_trade;
    @Bind(R.id.tv_msgType_trade)
    TextView tv_msgType_trade;
    @Bind(R.id.tv_msgType_pub)
    TextView tv_msgType_pub;
    @Bind(R.id.tv_msgType_bus)
    TextView tv_msgType_bus;
    @Bind(R.id.iv_unread_bus)
    ImageView iv_unread_bus;
    @Bind(R.id.iv_unread_pub)
    ImageView iv_unread_pub;
    @Bind(R.id.iv_unread_trade)
    ImageView iv_unread_trade;
    private MessageDao messageDao;
    private static final Message.MSG_TYPE[] TYPES = {Message.MSG_TYPE.Publish,
            Message.MSG_TYPE.Trade,
            Message.MSG_TYPE.Business};
    MessageCount mc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center2);
        ButterKnife.bind(this);
        mc = new MessageCount();
        messageDao = MessageDao.getInstance(ApplicationEx.getInstance().getUser().getLoginName());
        initUI();
        initListener();
    }

    private void initListener() {
        rl_pub.setOnClickListener(this);
        rl_bus.setOnClickListener(this);
        rl_trade.setOnClickListener(this);
    }

    int msgPos;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rl_pub:
                msgPos = 0;
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .MessageCenter_Publish, context);
                break;
            case R.id.rl_trade:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .MessageCenter_Trade, context);
                msgPos = 1;
                break;
            case R.id.rl_bus:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                        .MessageCenter_Business, context);
                msgPos = 2;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MSG_TYPE, msgPos);
        gotoActivity(MessageDetail2Activity.class, false, bundle);

    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("消息中心");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getServerCount();
//        updateMsgCount();
    }

    private void getServerCount() {
        CommonServiceManager.getInstance().getMessageCount2(TYPES[0].name(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        int cnt = jsonObject.optInt("count", 0);
                        mc.setPublishCount(cnt);
                        iv_unread_pub.setVisibility(cnt > 0 ? View.VISIBLE : View.GONE);
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
                        iv_unread_trade.setVisibility(cnt > 0 ? View.VISIBLE : View.GONE);
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
                        iv_unread_bus.setVisibility(cnt > 0 ? View.VISIBLE : View.GONE);
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

    private void updateMsgCount() {
        MessageCount messageCount = messageDao.readUnread(this);
        if (messageCount != null) {
            int businessCount = messageCount.getBusinessCount();
            int publishCount = messageCount.getPublishCount();
            int tradeCount = messageCount.getTradeCount();
            iv_unread_pub.setVisibility(publishCount > 0 ? View.VISIBLE : View.GONE);
            iv_unread_trade.setVisibility(tradeCount > 0 ? View.VISIBLE : View.GONE);
            iv_unread_bus.setVisibility(businessCount > 0 ? View.VISIBLE : View.GONE);
        }
    }


}
