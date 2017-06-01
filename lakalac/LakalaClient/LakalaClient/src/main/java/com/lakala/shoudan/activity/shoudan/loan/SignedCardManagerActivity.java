package com.lakala.shoudan.activity.shoudan.loan;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.ScrollBitmap;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Debit;
import com.lakala.shoudan.util.ImageUtil;
import com.lakala.ui.component.NavigationBar;

/**
 * 签约卡管理
 * @author Zhangmy
 *
 */
public class SignedCardManagerActivity extends AppBaseActivity{
	
	public static final String CONTRACTNO = "contractno";
	
	private ImageView imgBank;
	private TextView tvCardNameAndType;
	private TextView tvCardNumber;
	private TextView tvStatus;
	
	private ListView lvCardDealed;
	
	private List<Debit> debits = new ArrayList<Debit>();
	private String contractnoBack;//请求后返回的合同编号
	
	private String contractno;
	
	public static final int  SHOW_SIGNED_CARD_LIST = 0x33;
	private SignedCardAdapter signedCardAdapter;
	
	private List<OpenBankInfo> lists = new ArrayList<OpenBankInfo>();
	private ScrollBitmap mScrollBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signed_card_manager);
		initUI();
		getSignedCard();
		
	}

	protected void initUI() {
		Intent intent = getIntent();
		contractno = intent.getStringExtra(CONTRACTNO);
		
		navigationBar.setTitle("签约银行卡");
		navigationBar.setActionBtnVisibility(View.VISIBLE);
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
		navigationBar.setActionBtnEnabled(true);
		navigationBar.setActionBtnBackground(R.drawable.btn_change_selector);
		
		findViewById(R.id.view_top).setVisibility(View.GONE);
		imgBank = (ImageView) findViewById(R.id.left_icon);
		tvCardNameAndType = (TextView) findViewById(R.id.first_line_text);
		tvCardNumber = (TextView) findViewById(R.id.second_line_text);
		tvStatus = (TextView) findViewById(R.id.right_text);
		
		lvCardDealed = (ListView) findViewById(R.id.list_signed_card);
		mScrollBitmap = new ScrollBitmap(1, 5, null, this,false);
		signedCardAdapter = new SignedCardAdapter(debits,mScrollBitmap);
		lvCardDealed.setAdapter(signedCardAdapter);//数据需要修改
		lvCardDealed.setEnabled(false);
	}
	
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if(navBarItem == NavigationBar.NavigationBarItem.action){
				//更换卡
				Intent intent = new Intent(SignedCardManagerActivity.this,UpdateSignedCardActivity.class);
				intent.putExtra(CONTRACTNO, contractno);
				startActivity(intent);
			}
			if(navBarItem == NavigationBar.NavigationBarItem.back){//返回键
				finish();
			}
		}
	};
	
	private void showCurrentSignedCard(){
		if(debits.size()<=1){//只有一张卡 表不显示
			findViewById(R.id.layout_no_used_cards).setVisibility(View.GONE);
		}
		for(int i=0;i<debits.size();i++){
			Debit debit = debits.get(i);
			if(debit.getContractstatus().equals("1")){//0:处理中，1:生效2:更换失败3：失效

				for(int j=0;j<lists.size();j++){
					OpenBankInfo openBankInfo = lists.get(j);
					if(debit.getDebitbank().equals(openBankInfo.bankCode)){
						Drawable drawable = ImageUtil.getDrawbleFromAssets(context,openBankInfo
                                                                                   .bankCode + ".png");
						if (drawable == null){
							mScrollBitmap.loadImage(imgBank, openBankInfo.bankimg,true);
						}else{
							imgBank.setBackgroundDrawable(drawable);
						}
						
						tvCardNameAndType.setText(openBankInfo.bankname +" 储蓄卡");
						tvCardNumber.setText(Util.formatCardNumberWithStar(debit.getDebitcard()));
						tvStatus.setText(debit.getContractstatus());
						tvStatus.setVisibility(View.GONE);
						debits.remove(debit);
						return;
					}
				}
				return;
			}
		}
		//没有生效的卡 隐藏头部
		findViewById(R.id.layout_curent_signed_card).setVisibility(View.GONE);
	}
	
	private void showNoResult(){
		findViewById(R.id.layout_no_result).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_context).setVisibility(View.GONE);
	}
	
	private void showResult(){
		findViewById(R.id.layout_no_result).setVisibility(View.GONE);
		findViewById(R.id.layout_context).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_context).setBackgroundColor(getResources().getColor(R.color.white));
	}
	
	private void getSignedCard(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonData = new JSONObject(resultServices.retData);
                        List<Debit> list = Debit.unpackDebits(jsonData.getJSONArray("debits"));
                        contractnoBack = jsonData.optString("contractno");
                        debits.clear();
                        if(list != null){
                            debits.addAll(list);
                        }
                        getBankListForLoan_1EH();
                    } catch (Exception e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }else {
                    hideProgressDialog();
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
                ToastUtil.toast(context,R.string.socket_fail);
            }
        };
        ShoudanService.getInstance().getSignedAccount(contractno, callback);
	}

    private void getBankListForLoan_1EH(){
        //获取储蓄卡列表
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONArray array = new JSONArray(resultServices.retData);
                        lists.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jo = array.getJSONObject(i);
                            OpenBankInfo openBankInfo = OpenBankInfo.construct(jo);
                            lists.add(openBankInfo);
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                    if(debits.size()>0){
                        showResult();
                        showCurrentSignedCard();
                        signedCardAdapter.setOpenBankInfos(lists);
                        signedCardAdapter.notifyDataSetChanged();
                    }else {
                        showNoResult();
                    }
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getBankListForLoan("", "1EH", callback);//为替你还定义不同的接口
    }

    @Override
	protected void onDestroy() {
		if (mScrollBitmap != null) {
			mScrollBitmap.recycle();
		}
		super.onDestroy();
	}
}
