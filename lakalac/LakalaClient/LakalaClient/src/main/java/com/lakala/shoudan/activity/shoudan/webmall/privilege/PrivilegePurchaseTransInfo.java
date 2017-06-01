package com.lakala.shoudan.activity.shoudan.webmall.privilege;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.net.Uri;
import android.text.TextUtils;

import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

/**
 * 特权购买信息
 * @author ZhangMY
 *
 */
public class PrivilegePurchaseTransInfo extends BaseTransInfo{
	
	protected final String PARAMS = "p";

    private String tdtm;

    public String getTdtm() {
        return tdtm;
    }

    public void setTdtm(String tdtm) {
        this.tdtm = tdtm;
    }

    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public boolean isSignatureNeeded() {
        return false;
    }

    @Override
    public boolean ifSupportIC() {
        return true;
    }

    @Override
    public String getTransTitle() {
        return "特权购买";
    }

    @Override
    public String getStatisticTransResult() {
        return null;
    }

	@Override
	public String getStatisticSignPage() {
		return null;
	}

	@Override
	public String getStatisticIsSend() {
		return null;
	}

	private String orderNo;//订单号
	private String ordexpdat;//过期时间
	private String ordtime;//订单时间
	
	public String getOrdexpdat() {
		return ordexpdat;
	}

	public void setOrdexpdat(String ordexpdat) {
		this.ordexpdat = ordexpdat;
	}

	public String getOrdtime() {
		return ordtime;
	}

	public void setOrdtime(String ordtime) {
		this.ordtime = ordtime;
	}

	public String getMsgdigest() {
		return msgdigest;
	}

	public void setMsgdigest(String msgdigest) {
		this.msgdigest = msgdigest;
	}

	private String mercid;//商户id
	private String fee;
//	private String tdtm;
	private String productName;
	private String param;//参数
	private String mobile;
	private String busid;
	private String msgdigest;
	
	private String busstype;
	
	private String expire;//过期
	private String cardsn;// 
	
	private String transTime;//交易时间
	private String buynum;//购买数量

	public String getBuynum() {
		return buynum;
	}

	public void setBuynum(String buynum) {
		this.buynum = buynum;
	}

	public String getTransTime() {
		if(TextUtils.isEmpty(transTime)){
			return Util.getNowTime(); 
		}
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getCardsn() {
		return cardsn;
	}

	public void setCardsn(String cardsn) {
		this.cardsn = cardsn;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMercid() {
		return mercid;
	}

	public void setMercid(String mercid) {
		this.mercid = mercid;
	}
	
	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

//	public String getTdtm() {
//		return tdtm;
//	}
//
//	public void setTdtm(String tdtm) {
//		this.tdtm = tdtm;
//	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBusid() {
		return busid;
	}

	public void setBusid(String busid) {
		this.busid = busid;
	}

	public String getBusstype() {
		return busstype;
	}

	public void setBusstype(String busstype) {
		this.busstype = busstype;
	}

	
	public void unpackCallUri(Uri uri){
		this.param = uri.getQueryParameter("p");
		this.busstype = uri.getQueryParameter("busstype");
	}
	

	@Override
	public String getRepayName() {
		return "购买";
	}

	@Override
	public String getTransTypeName() {
		return "特权购买";
	}

	@Override
	public String getSwipeAmount() {
		String totalAmount;
        if(!TextUtils.isEmpty(getFee())){//有手续费，需要加上手续费
        	totalAmount = String.valueOf(new BigDecimal(getAmount()).add(new BigDecimal(getFee())));
        }else {
        	totalAmount = getAmount();
        }
        return totalAmount;
	}

	@Override
	public List<TransferInfoEntity> getResultInfo() {
		return getBillInfo();
	}

	@Override
	public List<TransferInfoEntity> getConfirmInfo() {
		List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("商品名称:", getProductName()));
        transferInfoEntities.add(new TransferInfoEntity("订单号:", getOrderNo()));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额:", Util.formatDisplayAmount(getSwipeAmount()), true));

        return transferInfoEntities;
	}

	@Override
	public List<TransferInfoEntity> getBillInfo() {
		List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
		infoEntities.add(new TransferInfoEntity("交易类型:", getTransTypeName()));
		infoEntities.add(new TransferInfoEntity("订单号:", getOrderNo()));
		infoEntities.add(new TransferInfoEntity("付款卡号:", StringUtil.formatCardNumberN6S4N4(payCardNo)));
		infoEntities.add(new TransferInfoEntity("交易金额:", Util.formatDisplayAmount(getSwipeAmount()),true));
		infoEntities.add(new TransferInfoEntity("交易时间:", getTransTime()));

		if(!"".equals(getSysRef()) && !"null".equals(getSysRef())){
			infoEntities.add(new TransferInfoEntity("交易流水号:", getSysRef()));
		}
		return infoEntities;
	}
	
	 public void unpackValidateResult(JSONObject jb){
//		 {"amount":"399.00","tdtm":"20150527143319","mercid":"3I100000601","ordno":"T20150527143319993","ordexpdat":"20150527150319","ordtime":"20150527143319","productName":"拉卡拉手机收款宝 ME30S","busid":"1CN","mobile":"13400531556","msgdigest":"70e6407dee1c51a0234f2cb8f49dbc497cd99a23"}
		 setMercid(jb.optString("mercid"));
		 setOrderNo(jb.optString("ordno"));
		 setAmount(jb.optString("amount"));
		 setFee(jb.optString("fee", ""));
         setTdtm(jb.optString("tdtm"));
		 setProductName(jb.optString("productName"));
		 setMobile(jb.optString("mobile"));
		 setBusid(jb.optString("busid"));
		 setOrdexpdat(jb.optString("ordexpdat"));
		 setOrdtime(jb.optString("ordtime"));
		 setMsgdigest(jb.optString("msgdigest"));
		 if(jb.has("buynum")){
			 setBuynum(jb.optString("buynum"));
		 }
	 }
	 
	public void unpackTransResult(JSONObject jb) {
//         if (jb == null) {
//             return;
//         }
//         try {
//             String sid = jb.optString("sid");
//             if (!TextUtils.isEmpty(sid)) {
//                 setSid(sid);
//             }
//             setSysRef((jb.optString("sysref")));
//             String transtime = jb.optString("transtime");
//             if (!TextUtils.isEmpty(transtime)) {
//                 setTransTime(Util.formateDateTransTime(transtime));
//             }
//             setTcAsyFlag(jb.optString("tc_asyflag", SYNC));
//             setAuthCode((jb.optString("authcode")));
//             setIcc55(jb.optString("icc55"));
//             setCardNo(jb.optString("pan"));
//             setExpire(jb.optString("expire"));
//             setCardsn(jb.optString("cardsn"));
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
     }

    @Override
    public TransactionType getType() {
        return TransactionType.PRIVILEGEPURCHASE;
    }

    @Override
    public String getAdditionalMsg() {
        //TODO
        return "";
    }
}
