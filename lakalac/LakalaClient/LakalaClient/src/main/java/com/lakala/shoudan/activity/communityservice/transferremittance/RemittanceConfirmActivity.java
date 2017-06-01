package com.lakala.shoudan.activity.communityservice.transferremittance;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.StringUtil;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.TransferEnum;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

public class RemittanceConfirmActivity extends AppBaseActivity {

    private RemittanceTransInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_confirm);
        info = (RemittanceTransInfo) getIntent().getSerializableExtra("info");
        initUI();
    }

    protected void initUI() {
        navigationBar.setTitle(R.string.confirm_remit_info);
        //收款银行
        TextView tv_bankname = (TextView) findViewById(R.id.tv_bankname);
        tv_bankname.setText(info.getOpenBank());
        //收款人借记卡
        TextView tv_bankcardnu = (TextView) findViewById(R.id.tv_bankcardnu);
        tv_bankcardnu.setText(info.getCardNumber());
        //收款人姓名
        TextView tv_collectionname = (TextView) findViewById(R.id.tv_collectionname);
        tv_collectionname.setText(info.getName());
        //到帐日期
        TextView tv_collection_date = (TextView) findViewById(R.id.tv_collection_date);
        tv_collection_date.setText(info.getTransTypeText());
        //转账金额
        TextView tv_trans_money = (TextView) findViewById(R.id.tv_trans_money);
        tv_trans_money.setText(new StringBuffer().append(StringUtil.formatAmount(info.getAmount())).append("元").toString());
        //手续费
        TextView tv_trans_fee = (TextView) findViewById(R.id.tv_trans_fee);
        tv_trans_fee.setText(new StringBuffer().append(info.getTransFee()).append("元").toString());
        //刷卡金额
        TextView tv_swiper_money = (TextView) findViewById(R.id.tv_swiper_money);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append(StringUtil.formatAmount(info.getSwipeAmount()))
                .append("元");
        tv_swiper_money.setText(Html.fromHtml(stringBuffer.toString()));
        //手机号码
        if (!TextUtils.isEmpty(info.getPhone())) {
            View layout_phone = findViewById(R.id.layout_phone);
            layout_phone.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_collectin_phone)).setText(info.getPhone());
        }
        findViewById(R.id.id_common_guide_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoudanStatisticManager.getInstance().onEvent(getEvent(), context);
                Intent intent = new Intent(context, TransferRemittancePayActivity.class);
                intent.putExtra(ConstKey.TRANS_INFO, info);
                startActivity(intent);
            }
        });
    }


    private String getEvent() {
        String event = "";
        if (PublicEnum.Business.isAd()) {
            event = ShoudanStatisticManager.Transfer_payPage_advert;
        } else if (PublicEnum.Business.isHome()) {
            event = ShoudanStatisticManager.Transfer_payPage_home;
        } else if (PublicEnum.Business.isPublic()) {
            event = ShoudanStatisticManager.Transfer_payPage_public;
        } else if (PublicEnum.Business.isDirection()) {
            event = ShoudanStatisticManager.Transfer_payPage_direction;
        }
        String contactStatus = TransferEnum.transfer.isContanct() ? "选" : "填";
        String smsStatus = TransferEnum.transfer.isSmsOpen() ? "是" : "否";
        String reachStatus = TransferEnum.transfer.isReachTime() ? "1" : "2";
        String phoneStatus = TransferEnum.transfer.isPhone() ? "填" : "选";
        event = String.format(event, contactStatus, reachStatus, smsStatus, phoneStatus);
        return event;
    }
}
