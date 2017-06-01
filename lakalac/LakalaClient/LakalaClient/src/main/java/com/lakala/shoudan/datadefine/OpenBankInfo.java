package com.lakala.shoudan.datadefine;

import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.BankInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 开户行信息类
 * @author ssss
 *
 */
public class OpenBankInfo implements Serializable{

	private static final long serialVersionUID = 2619637253645360275L;
	private static final String SOUPPORT = "1";
	/**	收款人姓名	*/
	public String name = "";
	/**	收款账户id	*/
	public String cardId = "";
	/**	银行名称	*/
	public String bankname = "";
	/**	转账卡号	*/
	public String cardNo = "";
	/** 银行拼音*/
	public String bankpinyin = "";
	
	/**	选择转账方式对应的银行code	*/
	public String bankCode = "";
	
	/**	选择的转账方式标记	*/
	public String  chooseflag= "";
	
	/**	银行code    <br>用于快速转账和慢转账*/
	public String bankaliasb = "";
	
	/**银行code		<br>用于实时转账*/
	public String bankaliasp = "";
	
	/**	实时支付   	<br>值为1则支持该种转账方式  为0不支持*/
	public String paymentflag = "";
	
	/**	快支付开关	<br>值为1则支持该种转账方式  为0不支持*/
	public String batchflag = "";	
	
	/**	普通支付		<br>值为1则支持该种转账方式  为0不支持*/
	public String commonflag = "";
	
	/**	银行图片，图片完整路径*/
	public String bankimg = "";
	
	/**	审核状态*/
	public String verifyStatus = "";
	
	/**	审核状态描述*/
	public String verifyStatusText = "";

    /**账户类型代码，C:贷记；D:借记*/
    public String acccountType ="";
	
	public static OpenBankInfo construct(JSONObject jsonObject){
		OpenBankInfo openBankInfo=new OpenBankInfo();
		try {
			openBankInfo.bankname 		= jsonObject.getString("bankname");
			openBankInfo.bankaliasb 	= jsonObject.getString("bankaliasb");//普通银行code用于快速转账和慢转账
			openBankInfo.bankaliasp 	= jsonObject.getString("bankaliasp");//用于实时转账
			openBankInfo.paymentflag 	= jsonObject.getString("paymentflag");//实时支付   值为1则支持该种转账方式  为0不支持
			openBankInfo.batchflag 		= jsonObject.getString("batchflag");//快支付开关	值为1则支持该种转账方式  为0不支持
			openBankInfo.commonflag 	= jsonObject.getString("commonflag");//普通支付	值为1则支持该种转账方式  为0不支持
			openBankInfo.bankimg 		= jsonObject.getString("bankimg");//普通支付	值为1则支持该种转账方式  为0不支持
			openBankInfo.bankCode 	    = jsonObject.getString("bankcode");
			openBankInfo.bankpinyin     = jsonObject.optString("bankpinyin","");
            openBankInfo.acccountType = jsonObject.optString("accounttype","");
		} catch (JSONException e) {
            LogUtil.print(e);
        }
		return openBankInfo;
	}
    public static BankInfo construct(OpenBankInfo openBankInfo){
        if(openBankInfo == null){
            return null;
        }
        BankInfo info = new BankInfo();
        info.setBankCode(openBankInfo.bankCode);
        info.setBankName(openBankInfo.bankname);
        info.setIcon(openBankInfo.bankimg);
        info.setPaymentflag(TextUtils.equals("1", openBankInfo.paymentflag));
        info.setCommonflag(TextUtils.equals("1",openBankInfo.commonflag));
        info.setIcon(openBankInfo.bankimg);
        return info;
    }

	@Override
	public String toString() {
		return "OpenBankInfo [name=" + name + ", cardId=" + cardId
				+ ", bankname=" + bankname + ", cardNo=" + cardNo
				+ ", bankCode=" + bankCode + ", chooseflag=" + chooseflag
				+ ", bankaliasb=" + bankaliasb + ", bankaliasp=" + bankaliasp
				+ ", paymentflag=" + paymentflag + ", batchflag=" + batchflag
				+ ", commonflag=" + commonflag + "bankimg"+ bankimg + "]";
	}
	
	/**
	 * 银行的支付方式
	 */
	public enum PayType{
		SLOW_MODE("1"),//慢
		FAST_MODE("2"),//快
		JIT_MODE("3") ;//时时
		public String value ;
		
		PayType(String value){
			this.value = value;
		}
	}
	
	/**
	 * 判断是否支持某中支付方式
	 * @param type
	 * @return
	 */
	public boolean validePayType(final PayType type){
		boolean valied = false ;
		switch (type) {
		case SLOW_MODE:
			valied = isSupportType(paymentflag);
			break;
		case FAST_MODE:
			valied = isSupportType(batchflag);
			break;
		case JIT_MODE:
			valied = isSupportType(commonflag);
			break;
		default:
			break;
		}
		
		return valied;
	}
	
	/**
	 * 某项支付方式是否支持，等于1的时候为支持此支付方式
	 * @param bankFlag  支付方式  慢  快 时时
	 * @return
	 */
	private boolean isSupportType(final String bankFlag){
		boolean valied = false ;
		if (SOUPPORT.equals(bankFlag)) {
			valied=true;
		}else {
			valied=false;
		}
		return valied;
	}
}
