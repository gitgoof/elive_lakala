package com.lakala.shoudan.bll;

import android.os.AsyncTask;

import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡检测类
 * 此类负责检测用户输入的银行卡号是否合法，并给出卡号对应的卡信息（银行code、银行名称，卡类型）
 * @author xyz
 *
 */
public class CardNumberChecker extends AsyncTask<Void, Void, Object>{
	
	public String bankCode	= "";
	public String bankName	= "";
	public int 	  cardType 	= TYPE_ILLEGAL;
	ResultServices result = null;
	JSONObject jsonObject = null;

	private String cardNumber;
	
	//预付卡
	public static final int TYPE_PREPAID 	= 0;
	//借记卡
	public static final int TYPE_DEBIT 		= 1;
	//贷记卡（信用卡）
	public static final int TYPE_CREDIT 	= 2;
	//非法卡号
	public static final int TYPE_ILLEGAL 	= -1;
	//卡号验证时，异常状态码对应的相关信息
	public static  Map<String, String> unusualCodeMap;
	
	private ICardNumberCheck iCardNumberCheck;
	
	private boolean isException = false;
	
	/**
	 * 验证卡号回调接口
	 * @author xyz
	 *
	 */
	public interface ICardNumberCheck {
		
		/**
		 * 在开始执行请求之前回调此方法
		 */
		public void onPreCardCheck();	
		
		/**
		 * 在执行网络请求过程中，发生异常，回调此方法
		 * @param e
		 */
		public void onCardCheckException(Exception e);
		
		/**
		 * 完成卡号验证，回调此方法
		 * @param cardType	返回的卡类型
		 * @param illegalCode 如果卡号非法，对应的异常code，<br/>
		 * 	通过此code可以在unusualCodeMap中获取对应的提示信息
		 */
		public void onCardCheckComplete(int cardType, String illegalCode, JSONObject jsonObject);
		
	}
	
	public CardNumberChecker(String cardNumber, String bankCode, ICardNumberCheck iCardNumberCheck) {
		this.cardNumber = cardNumber.replaceAll(" ", "");
		this.bankCode = bankCode;
		this.iCardNumberCheck = iCardNumberCheck;
		
		unusualCodeMap = new HashMap<String, String>();
		unusualCodeMap.put("1025", "获取数据失败");
		unusualCodeMap.put("1041", "不支持当前银行");
		unusualCodeMap.put("1042", "卡号长度错误");
		unusualCodeMap.put("1043", "该业务不支持信用卡");
		unusualCodeMap.put("1044", "卡号和银行卡不匹配");
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		iCardNumberCheck.onPreCardCheck();
	}

	@Override
	protected Object doInBackground(Void... params) {
		try {
			bankCode = Util.trim(bankCode);
			CommonServiceManager.getInstance().getbankByCardNo(cardNumber, bankCode, new ServiceResultCallback() {
				@Override
				public void onSuccess(ResultServices resultServices) {
					result = new ResultServices();
				}

				@Override
				public void onEvent(HttpConnectEvent connectEvent) {
//					ToastUtil.toast(this, getString(R.string.socket_error));
				}
			});
		} catch (Exception e) {
			isException = true;
			return e;
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		if (isException) {
			Exception e = (Exception)result;
			iCardNumberCheck.onCardCheckException(e);
			return;
		}

		ResultServices resultServices = (ResultServices)result;
		try {
			jsonObject = new JSONObject(((ResultServices) result).retData);
			if (resultServices.isRetCodeSuccess()) {
				bankCode 	= jsonObject.getString("bankCode");
				bankName 	= jsonObject.getString("bankName");
				String type = jsonObject.getString("cardType");
				if ("0001".equals(type)) {
					cardType = TYPE_DEBIT;
				}else if ("0002".equals(type)) {
					cardType = TYPE_CREDIT;
				}else if ("0000".equals(type)) {
					cardType = TYPE_PREPAID;
				}
			}else {
				cardType = TYPE_ILLEGAL;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		iCardNumberCheck.onCardCheckComplete(cardType,resultServices.retCode,jsonObject);
	}
}
