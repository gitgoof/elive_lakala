package com.lakala.shoudan.activity.shoudan.records;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我要提款页面
 * 
 */
public class DrawMoneyActivity extends BasePwdAndNumberKeyboardActivity {

    private View viewGroup;
	private TextView nextBtn;
//	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
//		@Override
//		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
//			 if (navBarItem == NavigationBar.NavigationBarItem.back) {
//	                finish();
//             } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
//                 ProtocalActivity.open(context, ProtocalType.D0_DESCRIPTION);
//             }
//        }
//	};
    private double accountBalance;
    /**
     * 今日上限
     */
    private double dLimitMax;
    /**
     * 单笔下限
     */
    private double pLimitMin;
    /**
     * 单笔上限
     */
    private double pLimitMax;
    private EditText input_amount;
    private String mAmount = "";
    private String mFee = "";
    private boolean isOne;
    TextView tv_tag1,tv_tag2;
    String remind1="当前可提金额 %s元";
    String remind2="单次提款金额不能低于%s元,并且不能超过%s元\n单日提款金额上限%s元";
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_trade_draw_money);
		initUI();
		
	}

	protected void initUI() {
        isOne=getIntent().getBooleanExtra("isOne",false);
		nextBtn=(TextView) findViewById(R.id.next_draw_money);
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputAmount = input_amount.getText().toString();
                if(TextUtils.isEmpty(inputAmount)){
                    String error="";
                    if(isOne){
                        error="请输入贷款金额";
                    }else {
                        error="请输入提款金额";
                    }
                    ToastUtil.toast(context,error);
                    return;
                }
                mAmount = TextUtils.isEmpty(inputAmount)?"0":inputAmount;
                if(isValidAmount(Double.parseDouble(mAmount))){
                    getFee(mAmount);
                }
            }
        });
		navigationBar.setTitle("立即提款");
//		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
//	    navigationBar.setActionBtnText("业务说明");
        input_amount = (EditText)findViewById(R.id.input_amount);
        viewGroup=(View)findViewById(R.id.view_group);
        tv_tag1= (TextView) findViewById(R.id.tv_tag1);
        tv_tag2= (TextView) findViewById(R.id.tv_tag2);
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(input_amount, CustomNumberKeyboard.EDIT_TYPE_FLOAT, 30);
        if(isOne){
            navigationBar.setTitle("一日贷");
            tv_tag1.setText("贷款信息");
            tv_tag2.setText("贷款金额（元）");
            input_amount.setHint("请输入贷款金额");
            remind1="当前可贷金额 %s元";
            remind2="单次贷款金额不能低于%s元,并且不能超过%s元\n单日贷款金额上限%s元";
        }
        initTips();
	}
    private void initTips(){
        String jsonStr = getIntent().getStringExtra("jsonStr");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            accountBalance = jsonObject.getDouble("amount");
            dLimitMax = jsonObject.getDouble("dLimitMax");
            pLimitMin = jsonObject.getDouble("pLimitMin");
            pLimitMax = jsonObject.getDouble("pLimitMax");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView amount_tip = (TextView) findViewById(R.id.amount_tip);
        amount_tip.setText(String.format(remind1,Util.formatAmount(accountBalance)));
        TextView limit_tip = (TextView) findViewById(R.id.limit_tip);
        limit_tip.setText(String.format(remind2,Util.formatAmount
                (pLimitMin),Util.formatAmount(pLimitMax),Util.formatAmount(dLimitMax)));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);

    }

    /**
     * 获取手续费
     * @param amount
     */
    private void getFee(final String amount){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                analyzeResult(resultServices);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getT0Fee(amount, callback);
    }
    private void analyzeResult(final ResultServices result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                if(result.isRetCodeSuccess()){
                    try {
                        JSONObject retData = new JSONObject(result.retData.toString());
                        mFee = String.valueOf(retData.getDouble("fee"));
                        toReCheckDrawAmount();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    ToastUtil.toast(DrawMoneyActivity.this,result.retMsg);
                }
            }
        });
    }

    private static final int REQUEST_CODE = 0x1000;
    public static final int TO_FINISH = 0x2000;
    /**
     * 跳转到提款确认页面
     */
    private void toReCheckDrawAmount(){
        Intent intent = new Intent(DrawMoneyActivity.this, DrawMoneySubmitActivity.class);
        intent.putExtra("amount",Util.formatAmount(Double.parseDouble(mAmount)));
        intent.putExtra("fee",mFee);
        intent.putExtra("isOne",isOne);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == TO_FINISH){
            finish();
        }
    }

    private boolean isValidAmount(double amount){
        String msg = "";
        boolean isValid = false;
        double realMax = dLimitMax < accountBalance?dLimitMax:accountBalance;
//        realMax = realMax < pLimitMax?realMax:pLimitMax;
        double realMin = pLimitMin;
        LogUtil.print("<v>","\n"+"amount:"+amount+"\nrealMax:"
                +realMax+"\npLimitMax:"+pLimitMax+"\nrealMin:"+realMin
                +"\naccountBalance:"+accountBalance
                +"\ndLimitMax:"+dLimitMax);
        if(amount == 0){
            if(isOne){
                msg = "请输入贷款金额";
            }else{
                msg = "请输入提款金额";
            }
            isValid = false;
        }else if(amount < realMin){
            if(isOne){
                msg = "贷款金额小于最低可贷金额，请修改";
            }else {
                msg = "提款金额小于最低可提金额，请修改";
            }
            isValid = false;
        }else if(realMax<amount){
            if(isOne){
                msg = "贷款金额大于可贷金额,请修改";
            }else {
                msg = "提款金额大于可提金额,请修改";
            }
            isValid = false;
        }else if(amount<=realMax&&amount>pLimitMax){
            if(isOne){
                msg = "贷款金额大于单笔限额，请修改";
            }else {
                msg = "提款金额大于单笔限额，请修改";
            }
            isValid = false;
        }else{
            isValid = true;
        }
        if(!isValid){
            ToastUtil.toast(this,msg);
        }
        return isValid;
    }
}
