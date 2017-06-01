package com.lakala.shoudan.activity.shoudan.bankitcard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.adapter.BankCardListAdapter;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.BankCardBean;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.activity.wallet.request.MyBankCardRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户拥有的银行卡显示页
 * Created by HJP on 2015/11/20.
 */
public class BankCardEntranceActivity extends AppBaseActivity {
    public static final int REQUEST_BANKS = 0x2314;
    private ListView lvCreditCard;
    private BankCardListAdapter creditCardListAdapter;
    private List<BankCardBean> bankCardBeanList;
    private int deletePosition;
    private QuickCardBinBean quickCardBinBean;
    private String backResult;
    private int backDeleteResult;
    private BankCardBean newBankCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_entrance);
        init();
    }

    public void init(){
        initUI();
        initData();
        initEvent();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.my_credit_card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryUserType();
    }
    /**
     * 查询商户等级，显示或者隐藏引导语
     */
    private void queryUserType() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_GET_USER_TYPE);
        MyBankCardRequest params = new MyBankCardRequest(context);
        LogUtil.print("------>",params.getTelecode());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String userTypeInfo=jsonObject.optString("userTypeInfo");
                        BankCardEntranceActivity.this.showintroducer(userTypeInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }
    //根据用户等级，显示隐藏引导绑定银行卡的提示
    private void showintroducer(String userTypeInfo) {
        if (userTypeInfo==null||userTypeInfo.equals("EWALLET_USERTYPE_NOT_REALNAME")||userTypeInfo.equals("EWALLET_USERTYPE_FIRST")||userTypeInfo.equals("EWALLET_USERTYPE_SECOND")){
            findViewById(R.id.add_bank_card_hint).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.add_bank_card_hint).setVisibility(View.GONE);
        }
    }

    private void initEvent(){
        lvCreditCard.setAdapter(creditCardListAdapter);
        lvCreditCard.setOnItemClickListener(onItemClickListener);
    }
    private void initData(){
        bankCardBeanList=new ArrayList<BankCardBean>();
        bankCardBeanList= (List<BankCardBean>) getIntent().getSerializableExtra(ConstKey.BANK_CARD_LIST);
        creditCardListAdapter=new BankCardListAdapter(BankCardEntranceActivity.this,bankCardBeanList);
        lvCreditCard = (ListView) findViewById(R.id.lv_credit_card);
        addHeaderView();
    }
    public void addHeaderView(){

//        View view = creditCardListAdapter.getView(0, null, lvCreditCard);
////        int height=100;
//        if(view!=null){
//            view.measure(0, 0);
////            height= view.getMeasuredHeight();
//        }
        View headView= LayoutInflater.from(context).inflate(R.layout.item_add_new_bank_card, lvCreditCard,false);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
//        headView.setLayoutParams(params);
        lvCreditCard.addHeaderView(headView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_BANKS && resultCode == ConstKey.RESULT_DELETE_BACK){
            backDeleteResult=data.getIntExtra("back_delete_result",0);
            if(backDeleteResult!=0){
                LogUtil.print("banck","deletePosition:"+deletePosition);
                bankCardBeanList.remove(backDeleteResult);
                creditCardListAdapter.notifyDataSetChanged();
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Check_Bankcard_Delete_Success,context);
            }
        }
        if(requestCode==ConstKey.REQUEST_ADD && resultCode==ConstKey.RESULT_ADD_BACK){
            backResult=data.getStringExtra("back_result");
            if(backResult.equals("add_success")){
                quickCardBinBean= (QuickCardBinBean) data.getSerializableExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN);
                newBankCard=new BankCardBean();
                newBankCard.setAccountNo(quickCardBinBean.getAccountNo());
                newBankCard.setAccountType(quickCardBinBean.getAccountType());
                newBankCard.setBankName(quickCardBinBean.getBankName());
                newBankCard.setCardId(quickCardBinBean.getCardId());
                newBankCard.setBkMobile(quickCardBinBean.getMobileInBank());
                newBankCard.setBankCode(quickCardBinBean.getBankCode());
                newBankCard.setSignFlag("1");
                bankCardBeanList.add(newBankCard);
                creditCardListAdapter.notifyDataSetChanged();
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Check_Bankcard_Add_Success, context);
            }

        }
    }
    AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtil.print("banck","position:"+position);
//            deletePosition=position;
            if(position==0){
                startActivityForResult(new Intent(BankCardEntranceActivity.this,BankCardAdditionActivity.class),ConstKey.REQUEST_ADD);
            }
        }
    };
}
