package com.lakala.shoudan.bll.service;


import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.BaseException;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收款宝业务接口
 * @author jack
 *
 * Modify by More @2014-1-15
 *
 */
public class Service_ShouDan extends BaseServiceManager{

	
	/**
	 * 获取商户详细信息
	 * @param verifyType 获取用户信息的类型
						1：使用登录密码获取；
						2：使用手机验证码获取
	 * @param token 	verifyType=2 下发短信时使用的token使用短信获取用户信息时必须填写。
	 * @return
	 * @throws BaseException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	protected void getMerchantInfo(String verifyType, String token, ServiceResultCallback serviceResultCallback) {//, ParseException, IOException{

		
		StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL).append("getMerchantInfo/")
		   .append(ApplicationEx.getInstance().getUser().getLoginName()).append(".json");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		getRequest(url.toString(), nameValuePairs,serviceResultCallback);
		
	}
	
	/**
	 * 商户开通注册
	 * @param merchant 商户信息
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void merchantManage(ShoudanRegisterInfo merchant, ServiceResultCallback serviceResultCallback) {//, ParseException, IOException{
		

		String url = ("merchantRegister");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();//(true);
		nameValuePairs.add(new BasicNameValuePair("realname",merchant.getRealName()));//真实姓名
		nameValuePairs.add(new BasicNameValuePair("idCardType", merchant.getIdCardType()));//证件类型
		nameValuePairs.add(new BasicNameValuePair("idCardId", merchant.getIdCardNo()));//证件号
		nameValuePairs.add(new BasicNameValuePair("email", merchant.getEmail()));//邮箱
		nameValuePairs.add(new BasicNameValuePair("province", merchant.getProvinceCode()));//地址省份 false
		nameValuePairs.add(new BasicNameValuePair("city", merchant.getCityCode()));//城市 false
		nameValuePairs.add(new BasicNameValuePair("homeAddr", merchant.getBusinessAddr()));//详细地址
        nameValuePairs.add(new BasicNameValuePair("provinceName", merchant.getProvince()));//地址省份 false
        nameValuePairs.add(new BasicNameValuePair("cityName", merchant.getCity()));//城市 false
        if(!TextUtils.isEmpty(merchant.getDistrictCode())){
        	//第三级地区不为空的时候才上送
        	nameValuePairs.add(new BasicNameValuePair("district", merchant.getDistrictCode()));//区县 false
        	nameValuePairs.add(new BasicNameValuePair("districtName", merchant.getDistrict()));//区县 false
        }
		nameValuePairs.add(new BasicNameValuePair("zipCode", merchant.getZipCode()));//邮编
//		nameValuePairs.add(new BasicNameValuePair("bussinessAddress.homeAddr", merchant.getBusinessAddr()));//营业地址
		nameValuePairs.add(new BasicNameValuePair("businessName", merchant.getBusinessName()));//商户名称
		nameValuePairs.add(new BasicNameValuePair("accountType", merchant.getAccountType()));//账户类型
		nameValuePairs.add(new BasicNameValuePair("bankNo", merchant.getBankNo()));//开户行号
		nameValuePairs.add(new BasicNameValuePair("bankName", merchant.getBankName()));//开户行名称
		nameValuePairs.add(new BasicNameValuePair("accountNo", merchant.getAccountNo()));//账户号
		nameValuePairs.add(new BasicNameValuePair("accountName", merchant.getAccountName()));//账户名

		postRequest(url.toString(),nameValuePairs,serviceResultCallback);
		
	}


    public void queryCardInfo(String cardNo,ServiceResultCallback callback){

        StringBuffer url = new StringBuffer();
        url.append("common/bankcard/")
                .append(cardNo)
                .append("/bank/nm");
        getRequest(url.toString(), new ArrayList<NameValuePair>(), callback);
    }

    /**
     * 新交易查询接口
     * @param isWithdraw 0、查询；1、撤销
     * @param startPage 起始页
     * @param pageSize 条数，默认20
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @param tradeType 交易类型
     * @param cardNo 转出卡号（收款交易撤销使用必填）
     * @param busId （撤销使用必填：收款18X，扫码：W00001）
     * @param sid
     * @param imagepaybill 扫码订单号（扫码交易并且撤销使用必填）
     * @param psamNo  刷卡器ksn
     */
    protected void getAcquiringTradeList(int isWithdraw,int startPage,String pageSize,String startTime,String endTime,
                                         String tradeType,String cardNo,String busId,String sid,String imagepaybill,String psamNo,ServiceResultCallback callback){
        StringBuffer url = new StringBuffer();
        url.append("getAcquiringTradeList");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("isWithdraw",String.valueOf(isWithdraw)));
        nameValuePairs.add(new BasicNameValuePair("startPage",String.valueOf(startPage)));
        nameValuePairs.add(new BasicNameValuePair("pageSize",String.valueOf(pageSize).equals("")?"20":String.valueOf(pageSize)));
        nameValuePairs.add(new BasicNameValuePair("startTime",startTime));
        nameValuePairs.add(new BasicNameValuePair("endTime",endTime));
        nameValuePairs.add(new BasicNameValuePair("tradeType",tradeType));
        if (!TextUtils.isEmpty(cardNo)){
            nameValuePairs.add(new BasicNameValuePair("cardNo",cardNo));
        }
        nameValuePairs.add(new BasicNameValuePair("busId",busId));
        nameValuePairs.add(new BasicNameValuePair("sid",sid));
        if (!TextUtils.isEmpty(imagepaybill)){
            nameValuePairs.add(new BasicNameValuePair("imagepaybill",imagepaybill));
        }
        nameValuePairs.add(new BasicNameValuePair("psamNo",psamNo));

        postRequest(url.toString(), nameValuePairs, callback);

    }
	/**
	 * 收单接口 
	 * @param lpmercd	 商户号码  				   true
	 * @param amount 	 总金额(分)  		           true
	 * @param fee 		 手续费   	    		   true
	 * @param otrack  	 磁道信息  				   false
	 * @param pinkey   	 个人密码					   false
	 * @param rnd     	 随机数  					   true
	 * @param issms 	 是否发送短信 0-不发送，1-发送  true
	 * @param mobildno   付款人通知手机号	  		   false
	 * @param tips       消费备注			 		   false
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	protected void acquirerTransaction(String lpmercd, String amount, String fee,
			String otrack, String pinkey, String rnd, String issms, String mobildno, String tips, String chantype, String posmemc,String icc55,String cardsn,   String track2, String pan, String track1, ServiceResultCallback serviceResultCallback) {//, ParseException, IOException{
		

		StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL).append("commitTransaction.json");
		
		List<NameValuePair> nameValuePairs = createNameValuePair(true);


        nameValuePairs.add(new BasicNameValuePair("busid","18X"));					//业务代码ID true
//        nameValuePairs.add(new BasicNameValuePair("busid","TAF"));					//收款T菜单业务代码ID true
		nameValuePairs.add(new BasicNameValuePair("lpmercd", lpmercd));				//商户号码 true
		nameValuePairs.add(new BasicNameValuePair("amount", amount));				//总金额 true
		nameValuePairs.add(new BasicNameValuePair("fee", fee));						//手续费 true
		nameValuePairs.add(new BasicNameValuePair("series",Util.createSeries()));	//交易类型 true
		nameValuePairs.add(new BasicNameValuePair("tdtm",Util.dateForWallet()));	//发送时间 true 
		nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));//手机号码 true
		nameValuePairs.add(new BasicNameValuePair("otrack", otrack));				//磁道（包括二三磁道）
		nameValuePairs.add(new BasicNameValuePair("pinkey", pinkey));				//个人密码 true 
		nameValuePairs.add(new BasicNameValuePair("rnd", rnd));						//随机数
		nameValuePairs.add(new BasicNameValuePair("issms", issms));					//个人密码 true 
		nameValuePairs.add(new BasicNameValuePair("mobileno", mobildno));			//付款人通知手机号
		nameValuePairs.add(new BasicNameValuePair("Tips",tips));					//消费备注
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));//输入条件(跟安全相关
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("posemc", posmemc));//
        nameValuePairs.add(new BasicNameValuePair("icc55", icc55));//
        nameValuePairs.add(new BasicNameValuePair("cardsn", cardsn));
        nameValuePairs.add(new BasicNameValuePair("track2", track2));
        nameValuePairs.add(new BasicNameValuePair("track1", track1));
        if(icc55 != null && icc55.length() != 0)
            nameValuePairs.add(new BasicNameValuePair("pan", pan));
		postRequest(url.toString(), nameValuePairs,serviceResultCallback);
		
	}
	
	/**
	 * 消费撤销
	 * 
	 * @param lpmercd 商户号      对应查询返回的 collectionAccount
	 * @param srcsid 原交易sid    对应         sid	
	 * @param pan    消费撤销					 paymentAccount
	 * @param amount 总金额					 dealAmount
	 * @param otrack 磁道（包括二三磁道）
	 * @param pinkey 个人密码
	 * @param rnd	随机数
	 * @param srcseries 原发送流水(每天不能重复的那个) series
	 * @param srcsysref  原检索参考号 				sysSeq
	 * @param srcauth
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	protected void revocationTransaction(String lpmercd, String srcsid, String pan, String amount, String otrack,
			String pinkey, String rnd, String srcseries, String srcsysref, String srcauth, String mobileNum
            , String posmemc, String icc55, String cardsn, String track2, String chantype, String track1, ServiceResultCallback serviceResultCallback) {//, ParseException, IOException{

		StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL).append("commitTransaction.json");
		
		List<NameValuePair> nameValuePairs = createNameValuePair(true);
//        nameValuePairs.add(new BasicNameValuePair("busid","TAG"));//撤销T菜单业务代码ID
		nameValuePairs.add(new BasicNameValuePair("busid","18Y"));//业务代码ID
		nameValuePairs.add(new BasicNameValuePair("lpmercd",lpmercd));//业务代码ID
		nameValuePairs.add(new BasicNameValuePair("srcsid",srcsid));//交易流水号
		nameValuePairs.add(new BasicNameValuePair("mobile",ApplicationEx.getInstance().getUser().getLoginName()));//手机号码
		nameValuePairs.add(new BasicNameValuePair("pan", pan));//转出银行卡号
		nameValuePairs.add(new BasicNameValuePair("amount", amount));//总金额
		nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));//发送方跟踪号
		nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));//转出卡号
		nameValuePairs.add(new BasicNameValuePair("otrack", otrack));//磁道（包括二三磁道）
		nameValuePairs.add(new BasicNameValuePair("pinkey", pinkey));//个人密码
		nameValuePairs.add(new BasicNameValuePair("rnd", rnd));//随机数
		nameValuePairs.add(new BasicNameValuePair("mobileno",mobileNum));//付款人手机号码
		nameValuePairs.add(new BasicNameValuePair("srcseries", srcseries));//原发送流水
		//nameValuePairs.add(new BasicNameValuePair("srcsysref", srcsysref));//原检索参考号
		nameValuePairs.add(new BasicNameValuePair("srcauth", srcauth));//原授权码
        nameValuePairs.add(new BasicNameValuePair("posemc", posmemc));//
        nameValuePairs.add(new BasicNameValuePair("icc55", icc55));//
        nameValuePairs.add(new BasicNameValuePair("cardsn", cardsn));
        nameValuePairs.add(new BasicNameValuePair("track2", track2));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));//输入条件(跟安全相关
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("track1", track1));
		postRequest(url.toString(),nameValuePairs,serviceResultCallback);
	    
	}
	
	/**
	 * 验证登录密码
	 * @param pwd  登录密码
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	protected void verifyUserPWD(String pwd, ServiceResultCallback serviceResultCallback) {//, ParseException, IOException{
		

		StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL).append("verifyUserPWD/")
		.append(ApplicationEx.getInstance().getUser().getLoginName())
		.append(".json");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("password", pwd));
		getRequest(url.toString(), nameValuePairs, serviceResultCallback);
		
	}
	
	/**
	 * 检验卡的类型,
	 * @param cardno 卡号
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getCardType(String cardno, ServiceResultCallback serviceResultCallback) {//,
	// ParseException, IOException{
		StringBuffer url = new StringBuffer();
		url.append(Parameters.serviceURL).append("getCardOrgByCardNo.json");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("cardno", cardno));
		getRequest(url.toString(), nameValuePairs, serviceResultCallback);
		
	}
	/**
	 * 获得交易类型
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getDealType(ServiceResultCallback serviceResultCallback) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		getRequest("getPOSDealType", nameValuePairs,serviceResultCallback);
		
	}
    /**
     *
     * @param sid
     * @return
     */
    protected void uploadSignatrue(String sid, String chrttCode, String pan, String mobileNo, byte[] eReceiptData, String signatureFilePath,ServiceResultCallback serviceResultCallback) {//, IOException{


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("eReceiptUpload/").append(sid).append(".json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("sid", sid));
        nameValuePairs.add(new BasicNameValuePair("characteristicCode", chrttCode));

        nameValuePairs.add(new BasicNameValuePair("custContact", mobileNo));
        nameValuePairs.add(new BasicNameValuePair("custContactType", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("pan", pan));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("busid", "1CK"));
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("eReceiptData", (signatureFilePath));

        postRequest(url.toString(), fileMap, nameValuePairs, serviceResultCallback);

    }
    /**
     * Upload TC value
     */
    protected void uploadTC(String busid, String tcicc55, String scpic55, String tcvalue, String lastsid, String acinstcode, ServiceResultCallback serviceResultCallback) {//, IOException{


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("queryTrans.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("busid", busid));
        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));
        nameValuePairs.add(new BasicNameValuePair("otrack", ""));
        nameValuePairs.add(new BasicNameValuePair("cardtype", "2"));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("tcicc55", tcicc55));
        nameValuePairs.add(new BasicNameValuePair("scpic55", scpic55));
        nameValuePairs.add(new BasicNameValuePair("tcvalue", tcvalue));
        nameValuePairs.add(new BasicNameValuePair("sid", lastsid));

        nameValuePairs.add(new BasicNameValuePair("tc_asyflag", "1"));
        nameValuePairs.add(new BasicNameValuePair("acinstcode", acinstcode));
        nameValuePairs.add(new BasicNameValuePair("hsmtrade", "02"));
        nameValuePairs.add(new BasicNameValuePair("posemc", "1"));

        //nameValuePairs.add(new BasicNameValuePair("telecode",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        postRequest(url.toString(), nameValuePairs, serviceResultCallback);

        

    }

    /**
     * Upload TC value
     */
    protected void uploadTCForPaymentForCall(String busid, String tcicc55, String scpic55, String tcvalue, String lastsid, String acinstcode
            ,String orderId, String channelCode, String lakalaOrderId, ServiceResultCallback serviceResultCallback) {//, IOException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("partner/commitTransaction.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("busid", busid));
        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));
        nameValuePairs.add(new BasicNameValuePair("otrack", ""));
        nameValuePairs.add(new BasicNameValuePair("cardtype", "2"));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("tcicc55", tcicc55));
        nameValuePairs.add(new BasicNameValuePair("scpic55", scpic55));
        nameValuePairs.add(new BasicNameValuePair("tcvalue", tcvalue));
        nameValuePairs.add(new BasicNameValuePair("sid", lastsid));

        nameValuePairs.add(new BasicNameValuePair("tc_asyflag", "1"));
        nameValuePairs.add(new BasicNameValuePair("acinstcode", acinstcode));
        nameValuePairs.add(new BasicNameValuePair("hsmtrade", "02"));
        nameValuePairs.add(new BasicNameValuePair("posemc", "1"));

        //收银台付款人
        nameValuePairs.add(new BasicNameValuePair("orderId", orderId)); //订单号 fbillno
        nameValuePairs.add(new BasicNameValuePair("channelCode", channelCode));//机构渠道号
        nameValuePairs.add(new BasicNameValuePair("lakalaOrderId", lakalaOrderId));

        //nameValuePairs.add(new BasicNameValuePair("telecode",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        postRequest(url.toString(), nameValuePairs, serviceResultCallback);

        

    }

    /**
     * 
     * @return area info
     * @throws BaseException 
     * @throws IOException 
     * @throws ParseException 
     */
    public void getCommonDict(String type, ServiceResultCallback serviceResultCallback){//  BaseException,
        // IOException{

        String url = ("getCommonDict");
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("param", type));
        getRequest(url.toString(), list, serviceResultCallback);

    }

    /**
     * 特约商户开通
     * @param instbill 第三方编号
     * @return
     * @throws IOException
     * @throws BaseException
     */
    public void openContributePayment(String instbill, ServiceResultCallback serviceResultCallback){// IOException, BaseException{

        StringBuffer url = new StringBuffer();
        url.append("trade");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1CE"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));
        
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));//虚拟终端号

        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("secmercd", ""));//二级商户号
        nameValuePairs.add(new BasicNameValuePair("secmername", ""));//二级商户名称
        nameValuePairs.add(new BasicNameValuePair("sectermid", ""));//二级商户终端号
        nameValuePairs.add(new BasicNameValuePair("instbill", instbill));//第三方编号
        nameValuePairs.add(new BasicNameValuePair("openarea", ""));//交易地区
        this.postRequest(url.toString(), nameValuePairs, serviceResultCallback);

    }

    /**
     * 特约商户查询
     * @param instbill
     * @return
     * @throws IOException
     * @throws BaseException
     */
    public void contributeQuery( String instbill, ServiceResultCallback serviceResultCallback){// IOException, BaseException{

        StringBuffer url = new StringBuffer();
        url.append("trade");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1CF"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("secmercd", ""));//二级商户号
        nameValuePairs.add(new BasicNameValuePair("secmername", ""));//二级商户名称
        nameValuePairs.add(new BasicNameValuePair("sectermid", ""));//二级商户终端号
        nameValuePairs.add(new BasicNameValuePair("instbill", instbill));//第三方编号
        nameValuePairs.add(new BasicNameValuePair("openarea", ""));//交易地区
        postRequest(url.toString(), nameValuePairs,serviceResultCallback);


    }

    public void queryCtrbtFee(String instbill, String srcSid, String amount, String billno, ServiceResultCallback serviceResultCallback) {//, IOException{

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1CG"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("secmercd", ""));//二级商户号
        nameValuePairs.add(new BasicNameValuePair("secmername", ""));//二级商户名称
        nameValuePairs.add(new BasicNameValuePair("sectermid", ""));//二级商户终端号
        nameValuePairs.add(new BasicNameValuePair("instbill", instbill));//第三方编号
        nameValuePairs.add(new BasicNameValuePair("openarea", ""));//交易地区
        //nameValuePairs.add(new BasicNameValuePair("srcsid", srcSid));
        nameValuePairs.add(new BasicNameValuePair("amount", Utils.yuan2Fen(amount)));
        //nameValuePairs.add(new BasicNameValuePair("billno", billno));
        postRequest("trade", nameValuePairs, serviceResultCallback);

    }


    public void contributePayment(SwiperInfo swiperInfo, String instbill, String fee, String srcSid, String amount, String billno, ServiceResultCallback serviceResultCallback) {//, IOException{

        StringBuffer url = new StringBuffer();
        url.append("trade");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1CH"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("amount", Utils.yuan2Fen(amount)));
        nameValuePairs.add(new BasicNameValuePair("fee", fee));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));
        nameValuePairs.add(new BasicNameValuePair("tips", ""));
        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("secmercd", ""));//二级商户号
        nameValuePairs.add(new BasicNameValuePair("secmername", ""));//二级商户名称
        nameValuePairs.add(new BasicNameValuePair("sectermid", ""));//二级商户终端号
        nameValuePairs.add(new BasicNameValuePair("instbill", instbill));//第三方编号
        nameValuePairs.add(new BasicNameValuePair("openarea", ""));//交易地区
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcSid));
        //nameValuePairs.add(new BasicNameValuePair("billno", billno));
        nameValuePairs.add(new BasicNameValuePair("track1", swiperInfo.getTrack1()));
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        if (!"".equals(swiperInfo.getIcc55())) {
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        }
        postRequest(url.toString(), nameValuePairs,serviceResultCallback);

        


    }

    /**
     * 商户开通产品状态查询
     */
    public void getMerchantStatus(ServiceResultCallback serviceResultCallback){

        String url = ("merchantStatus/");
        getRequest(url.toString(), null, serviceResultCallback);
        

    }


    public void queryRentCollectionFee(String  amount, String inpan, ServiceResultCallback serviceResultCallback){


        StringBuffer url = new StringBuffer();
        url.append("trade");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1EM"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));

        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("amount", Utils.yuan2Fen(amount)));
        nameValuePairs.add(new BasicNameValuePair("inpan", inpan));

        postRequest(url.toString(), nameValuePairs, serviceResultCallback);
        

    }


    public void rentCollection(String srcSid, String amount, String fee, String feeFlag, String inpan, String tips, SwiperInfo swiperInfo, ServiceResultCallback serviceResultCallback){


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("queryTrans.json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "1F2"));
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));

        nameValuePairs.add(new BasicNameValuePair("amount", amount));//不包含手续费
        nameValuePairs.add(new BasicNameValuePair("fee", fee));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));

        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));
        nameValuePairs.add(new BasicNameValuePair("track1", swiperInfo.getTrack1()));
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        if (!"".equals(swiperInfo.getIcc55())) {

            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        }
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcSid));
        nameValuePairs.add(new BasicNameValuePair("inpan", inpan));
        nameValuePairs.add(new BasicNameValuePair("feeflag", feeFlag));
        nameValuePairs.add(new BasicNameValuePair("bmercid", ""));
        nameValuePairs.add(new BasicNameValuePair("curcode", ""));
        nameValuePairs.add(new BasicNameValuePair("notesdesc", tips));//消费备注

        postRequest(url.toString(), nameValuePairs, serviceResultCallback);
        


    }


    public void thirdPayemtParamValidate(String param, ServiceResultCallback serviceResultCallback){// Exception{
        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).///bussiness/pay.json
                append("business/pay.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("param", param));

        postRequest(url.toString(), nameValuePairs, serviceResultCallback);
        

    }

    public void mallPaymentValidate(String param, ServiceResultCallback serviceResultCallback){// Exception{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("params", param));


        postRequest("partner/pay", nameValuePairs, serviceResultCallback);

        

    }
    
    /**
     * 第三方消费
     */
    public void thirdPartyCosumption(String orderId,String channelCode,
    		String amount, String tips, String merNo, String lakalaOrderId,SwiperInfo swiperInfo, ServiceResultCallback serviceResultCallback){


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("partner/commitTransaction.json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid","18X"));					//业务代码ID true
//        nameValuePairs.add(new BasicNameValuePair("busid","TAF"));					//收款T菜单业务代码ID true

        nameValuePairs.add(new BasicNameValuePair("lpmercd", merNo));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));//不包含手续费
        nameValuePairs.add(new BasicNameValuePair("fee", "000000000000"));//写死000000000000
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));//交易传输时间YYMMDDhhmmss
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));
        
        nameValuePairs.add(new BasicNameValuePair("issms", "0"));//是否发送短信 0 不发，1发送
        nameValuePairs.add(new BasicNameValuePair("mobileno ", ""));//付款人通知手机号
        nameValuePairs.add(new BasicNameValuePair("tips", tips));//消费备注
        nameValuePairs.add(new BasicNameValuePair("termid",  ApplicationEx.getInstance().getSession().getCurrentKSN()));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        if (!"".equals(swiperInfo.getIcc55())) {

            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));

        }
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("track1", swiperInfo.getTrack1()));
        nameValuePairs.add(new BasicNameValuePair("tagCode", ""));//标签
        //nameValuePairs.add(new BasicNameValuePair("registerPayUserName", registerPayUserName));//收银台付款人
        nameValuePairs.add(new BasicNameValuePair("orderId", orderId)); //订单号 fbillno
        nameValuePairs.add(new BasicNameValuePair("channelCode", channelCode));//机构渠道号
       // nameValuePairs.add(new BasicNameValuePair("pv", pv));//产品版本号
        nameValuePairs.add(new BasicNameValuePair("lakalaOrderId", lakalaOrderId));
        postRequest(url.toString(), nameValuePairs, serviceResultCallback);
        


    }

}
