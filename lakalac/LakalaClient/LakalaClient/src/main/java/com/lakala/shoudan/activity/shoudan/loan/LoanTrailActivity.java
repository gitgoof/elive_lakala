package com.lakala.shoudan.activity.shoudan.loan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Rate;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.LoanInfoCallback;
import com.lakala.shoudan.common.util.IMEUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 还款和还贷信息
 * 
 * @author ldm
 * 
 */
@SuppressLint("NewApi")
public class LoanTrailActivity extends BasePwdAndNumberKeyboardActivity implements
		OnClickListener {
	
	private String creditCardFromRemittaqnce = "";

	private EditText loanMoney;// 借款金额
	private TextView staging;// 分期
	private TextView btnSure;
	
	private TextView loanMoneyText;
	private TextView fee;
	private TextView info1;//还款时间
	private TextView info2;//还款金额

	private InputMethodManager imm;

	private String money;

	private final int HAS_LOAN = 0x03;
	private List<Rate> rateList;
	private int selectindex=0;
	private final int RATE_SELECT = 0x02;
	private String curcode="156";// 币种
	private String orderno;
	private LoanBackShowInfo backShowInfo;

	private Rate currentRate = null;//当前选择的分期
	private String systime;//系统时间，服务端返回，计算还款时间

    public static void open(final AppBaseActivity context, final LoanBackShowInfo info, final int flag){
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
                if(!TerminalKey.hadVirtualSigned()){
                    context.showProgressWithNoMsg();
                }
            }

            @Override
            public void onError(String msg) {
                context.hideProgressDialog();
				LogUtil.print("error",msg);
                ToastUtil.toast(context,msg);
            }

            @Override
            public void onSuccess() {
                context.hideProgressDialog();
                Intent intent = new Intent(context,LoanTrailActivity.class);
                if(info != null){
                    intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO,info);
                }
                if(flag != -1){
                    intent.setFlags(flag);
                }
                context.startActivity(intent);
            }
        });
    }
    public static void open(AppBaseActivity context,LoanBackShowInfo info){
        open(context, info,-1);
    }
    public static void open(AppBaseActivity context){
        open(context,null);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_trail);
		initUI();
		context = this;
		popupWindowFactory.initPopupwindow(navigationBar);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		popupWindowFactory.initPopupwindow(navigationBar);
	}

	protected void initUI() {
		PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Money, context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		navigationBar.setTitle(getString(R.string.payforyou));
		navigationBar.setActionBtnBackground(R.drawable.btn_more_selector);
		navigationBar.setActionBtnVisibility(View.VISIBLE);
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
		
		loanMoneyText=(TextView) findViewById(R.id.loanmoney_text);
		fee=(TextView) findViewById(R.id.fee);
		info1=(TextView) findViewById(R.id.info1);
		info2=(TextView) findViewById(R.id.info2);
		// 分期
		staging = (TextView) findViewById(R.id.staging);
		staging.setOnClickListener(this);
		findViewById(R.id.staging_img).setOnClickListener(this);
		// 借款金额
		loanMoney = (EditText) findViewById(R.id.loanmoney);
		loanMoney.addTextChangedListener(new MoneyInputWatcher());
//		loanMoney.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(!loanMoney.isClickable()){
//					loanMoney.setCursorVisible(true);
//					//小数点后均为0时，再次获取光标，小数点后两位消失
//					loanMoneyTextFocus();
//				}
//			}
//		});
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		btnSure.setOnClickListener(this);
		
		if(null!= getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO)){
			backShowInfo=(LoanBackShowInfo) getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO);
			//申请失败，执行再次试算
			btnSure.setText(getString(R.string.dredge_btn_txt2));
			backShowInfo.setPass(true);//不做信息回显
		}else{
			getLoanInfo();
		}
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.findViewById(R.id.viewgroup).requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(loanMoney, CustomNumberKeyboard.EDIT_TYPE_FLOAT,30);
		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
	}

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView)findViewById(R.id.scrollview_loan_trail));
    }

    /**
	 * 获取信息回显
	 */
	protected void getLoanInfo() {
        showProgressWithNoMsg();
        LoanInfoCallback callback = new LoanInfoCallback() {
            @Override
            public void onSuccess(LoanBackShowInfo loanBackShowInfo) {
				hideProgressDialog();
                backShowInfo = loanBackShowInfo;
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
				hideProgressDialog();
                backShowInfo = new LoanBackShowInfo();
				backShowInfo.setPass(false);
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().loanInfoBackShow(callback);
}
	@Override
	public void onClick(View v) {
        hideNumberKeyboard();
		loanMoneyTextOutFocus();
		switch (v.getId()) {
		case R.id.next:
			if (isInputValid()) {
				if (TextUtils.isEmpty(backShowInfo.getLoanLimits())){
					PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Money_Person, context);
					startToMainInfo();
				}
				else{
					//如果是额度校验失败
					//如果是因为超额失败的话  不用重新试算,直接到填写验证码的那个页面
					String money = loanMoney.getText().toString().trim();
					BigDecimal moneyBig = new BigDecimal(money);
					BigDecimal loanLimitBig = new BigDecimal(backShowInfo.getLoanLimits());
					if(moneyBig.compareTo(loanLimitBig) == 1){//写入额度超限
						ToastUtil.toast(context,"对不起,您的申请超过额度,请申请 "+loanLimitBig+"元以下的额度");
						return ;
					}
					backShowInfo.setApplyamt(Integer.valueOf(Util.yuan2Fen(money)));
					Intent intent = new Intent(LoanTrailActivity.this,LoanConfirmApplyActivity.class);
					intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO, backShowInfo);
					intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
					startActivity(intent);
				}
			}
			break;
		case R.id.staging:
		case R.id.staging_img:
			// 分期  缓存
			IMEUtil.hideIme(this);
			getRateList();
			break;
		default:
			break;
		}
	}
	

	private void loanMoneyTextOutFocus(){
		EditText editText = (EditText) findViewById(R.id.loanmoney);
		String result = editText.getText().toString().trim();
		if(TextUtils.isEmpty(result)){
			return ;
		}
		int indexOfDot = result.indexOf(".");
		if(indexOfDot<0){
			if(Integer.parseInt(result)>0){
				editText.setText(result+".00");
			}else {
				editText.setText("");
			}
		}else if(result.length()-indexOfDot-1 == 1){
			editText.setText(result+"0");
		}else if(result.length()-indexOfDot-1 == 0){
			editText.setText(result+"00");//有小数点，但小数点后没有值
		}
		editText.setSelection(editText.length());
		editText.setCursorVisible(false);
		editText.setClickable(true);
	}

	/**
	 * 显示分期
	 */
	private void showRateList() {
		//显示选择分期 得到period rate

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.loan_please_select_period));
		builder.setItems(getRateLists(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectindex=which;
                currentRate = rateList.get(selectindex);
                staging.setText(formateRate(currentRate));//分期
                changeInfo(currentRate);
			}
		});
		builder.create().show();
	}

	private String[] getRateLists() {
		String[] ratesTemp=new String[rateList.size()];
		for(int i=0;i<rateList.size();i++){
			ratesTemp[i]=formateRate(rateList.get(i));
		}
		return ratesTemp;
		
	}

	/**
	 * 获取分期
	 */
	private void getRateList() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                try {
                    rateList=new ArrayList<>();
                    if(resultServices.isRetCodeSuccess()){
                        JSONObject jsonData = new JSONObject(resultServices.retData);systime = jsonData.optString("systime");
                        JSONArray jsonArrayRate = jsonData.getJSONArray("ratelist");
                        for (int i=0;i<jsonArrayRate.length();i++) {
                            Rate rate = new Rate();

                            JSONObject jsonObject=jsonArrayRate.getJSONObject(i);
                            rate.period=jsonObject.getInt("period");
                            rate.periodlen=jsonObject.getInt("periodlen");
                            rate.lenunit=jsonObject.getString("lenunit");
                            rate.rate=jsonObject.getInt("rate");
                            rateList.add(rate);
                        }
                        if(rateList.size() != 0){
                            showRateList();
                        }else {
							toast("费率获取失败，请重试");
						}
                    }else{
                        toast(resultServices.retMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getRate(callback);
	}
	//获取随机数
	private void startToMainInfo() {
        money = loanMoney.getText().toString().trim();
        money = Util.yuan2Fen(money);
        orderno = Util.getLoanRandom();//客户端随机生成

        backShowInfo.setApplyamt(Integer.valueOf(money));//申请金额
        backShowInfo.setOrderno(orderno);
        trilApply();
	}

	/**
	 * 试算
	 * @throws Exception
	 */
	private void trilApply(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
				hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        orderno = jsonObject.getString("orderno");
                        backShowInfo.setOrderno(orderno);
                        Intent intent = new Intent(LoanTrailActivity.this,LoanIndividualInfoActivity.class);
//					Intent intent = new Intent(LoanTrailActivity.this,LoanMainActivity.class);
                        intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO, backShowInfo);
                        intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                        startActivity(intent);
						PublicToEvent.LoanEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
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
        ShoudanService.getInstance().trial(orderno, money,
                                           String.valueOf(currentRate.periodlen),
                                           String.valueOf(currentRate.rate), curcode, ApplicationEx.getInstance().getUser().getTerminalId(),
                                           callback);
	}
	protected boolean isInputValid() {
		String money = loanMoney.getText().toString().trim();
		if (TextUtils.isEmpty(money)) {
			ToastUtil.toast(context,getString(R.string.please_input_apply_amouont));
			return false;
		} else if (Double.valueOf(money) > 10000
				|| Double.valueOf(money) < 1000) {
            ToastUtil.toast(context,"金额最低1000元，最高10000元");
			return false;
		}
		if (TextUtils.isEmpty(staging.getText().toString().trim())) {
            ToastUtil.toast(context,"请选择还款周期");
			return false;
		}
		return true;
	}

	//格式或分期
	private String formateRate(Rate rate){
		return rate.periodlen+formateLenunit(rate.lenunit);
//		+"   手续费"+Util.fen2Yuan(String.valueOf(rate.rate));
	}
	//获取分期的周期单位 是否固定为周
	private String formateLenunit(String lenunit){
		if("W".equalsIgnoreCase(lenunit)){
			return "周";
		}
		return "周";
	}

	//修改界面内容 实际到账金额，手续费,还款时间提示语
	private void changeInfo(Rate rate) {
		String applyMoney = loanMoney.getText().toString().trim();
		if(TextUtils.isEmpty(applyMoney) || null == rate || ".".equals(applyMoney)){
			return;
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		Double applyAmount = Double.parseDouble(applyMoney);
		Double feeAmount = applyAmount*(currentRate.rate/10000.00);
	
		
		//yyyy-MM-dd
		String formatWithLine = Util.addDate(systime,rate.periodlen,7);
		info1.setText(formatWithLine);//还钱时间
		//yyyy年MM月dd日
		String format2=Util.formatDateStrToPattern(formatWithLine, "yyyy-MM-dd", "yyyy年MM月dd日");
		//时间加减
		info2.setText(String.valueOf(df.format(applyAmount)));//还钱金额
		
//		String feeAmounStr = String.valueOf(feeAmount);
//		int indexOfDot = feeAmounStr.indexOf(".");
//		if (indexOfDot >0 && feeAmounStr.length() - indexOfDot - 1 > 2) {
//			feeAmounStr=feeAmounStr.substring(0, indexOfDot+3);//只取小数后两位
//		}
		fee.setText(df.format(feeAmount));//手续费
		String feeAmountStr = df.format(feeAmount);
		loanMoneyText.setText(df.format(applyAmount-Double.parseDouble(feeAmountStr)));//实际到账金额
		
		backShowInfo.setRatefee(String.valueOf(Util.yuan2Fen(feeAmountStr)));
		backShowInfo.setDueTimeFormat1(formatWithLine);
		backShowInfo.setDueTimeFormat2(format2);
		backShowInfo.setPeriod(rate.periodlen);
		backShowInfo.setRate(rate.rate);
		backShowInfo.setCurcode("156");// 币种
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
	
	class MoneyInputWatcher implements TextWatcher {
		/** 是否允许输入0 */
		private boolean isAllowZero = false;

		public MoneyInputWatcher() {
			isAllowZero = false;
		}

		public void init() {
			isAllowZero = true;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			//实时更新数据
			changeInfo(currentRate);
		}

		@Override
		public void afterTextChanged(Editable s) {
			String input = s.toString();
			if (isAllowZero) {
				if (input.startsWith("0")) {
					s.delete(0, 1);
				}
			}
			if (input.length() == 1 && ".".equals(input)) {
				s.delete(0, 1);
			}
			
			if(input.startsWith(".")){
				s.delete(0, s.length());
			}
			
			// 计算小数点前有效位数
			int length = (input.contains(".")) ? input.indexOf(".") : input
					.length();

			// 如果小数点前的位数大于8，则删除小数点前的那一位数字
			if (length > 9) {
				s.delete(length - 1, length);
			}
			// 小数点后只能输入两位
			int indexOfDot = input.indexOf(".");
			if (indexOfDot <= 0)
				return;
			if (input.length() - indexOfDot - 1 > 2) {
				s.delete(indexOfDot + 3, indexOfDot + 4);
			}
			//判断小数点后数据有是1位
//			if(input.length()-indexOfDot-1==1){
//				CharSequence endNum =s.subSequence(indexOfDot+1, indexOfDot+2);
//				if("0".equals(endNum)){
//					s = s.delete(indexOfDot+1, indexOfDot+2);
//				}
//			}
//			//判断小数点后数据有是2位
//			if(input.length()-indexOfDot-1==2){
//				CharSequence endNum =s.subSequence(indexOfDot+1, indexOfDot+3);
//				if("00".equals(endNum)){
//					s = s.delete(indexOfDot+1, indexOfDot+3);
//				}
//			}
		}
	}

	PopupWindowFactory popupWindowFactory=new PopupWindowFactory(LoanTrailActivity.this);
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {

		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if (navBarItem == NavigationBar.NavigationBarItem.action) {
				hideNumberKeyboard();
				loanMoneyTextOutFocus();
				popupWindowFactory.show();
			}
			if (navBarItem == NavigationBar.NavigationBarItem.back) {// 返回键
				finish();
			}
		}
	};

}
