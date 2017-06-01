package com.lakala.shoudan.activity.shoudan.records;
import android.os.Bundle;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.records.CashHistory.OperatorType;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.ui.component.NavigationBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 划款详情
 *
 */
public class TransferMoneyDetailsActivity extends AppBaseActivity{


//	private Button backHomeBtn,continueDrawBtn;
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			 if (navBarItem == NavigationBar.NavigationBarItem.back) {
	                finish();
			 }
		}
	};
    private CashHistory mHistory = null;
    private VerticalListView mVerticalListView;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoudan_record_detail);
		initUI();

	}

	protected void initUI() {
		mHistory = getIntent().getParcelableExtra(Constants.IntentKey.TRANSFER_INTENT_KEY);
		navigationBar.setTitle("划款详情");
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        mVerticalListView = (VerticalListView) findViewById(R.id.verticallistview_recorddetail);
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("划款类型",mHistory.getOperatorType().getChineseDesc()));
        transferInfoEntities.add(new TransferInfoEntity("交易状态",mHistory.getStatusName()));
        TransferInfoEntity entity = new TransferInfoEntity("商户名称", ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName());
        entity.setNeedDevider(true);
        transferInfoEntities.add(entity);
        transferInfoEntities.add(new TransferInfoEntity("商户编号",ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        transferInfoEntities.add(new TransferInfoEntity("结算账号",Util.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo())));
        transferInfoEntities.add(new TransferInfoEntity("交易时间", mHistory.getTradeTime()));
        transferInfoEntities.add(new TransferInfoEntity("划款金额", StringUtil.formatTwo(mHistory.getAmount()) + "  元",true));

        if(mHistory.getOperatorType() == OperatorType.D0 || mHistory.getOperatorType() ==
                OperatorType.T0){
            double feeAmount = mHistory.getFeeAmount();
            transferInfoEntities.add(new TransferInfoEntity("手续费", StringUtil.formatTwo(feeAmount) + "  元",true));
        }
        mVerticalListView.addList(context,transferInfoEntities);
	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.back_home:
//			 Intent intent = new Intent(TransferMoneyDetailsActivity.this, ShouDanMainActivity.class);
//             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			 startActivity(intent);
//			break;
//		default:
//			break;
//		}
//	}

}
