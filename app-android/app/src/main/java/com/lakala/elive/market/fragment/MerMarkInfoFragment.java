package com.lakala.elive.market.fragment;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.base.BaseFragment;

import java.util.List;

/**
 *
 * 商户网点基础信息
 * @author hongzhiliang
 *
 */
public class MerMarkInfoFragment extends BaseFragment{

	private final String TAG = MerMarkInfoFragment.class.getSimpleName();

    MerShopInfo merchantInfo = null; //商户详情信息

    protected TextView spinnerMerInfoTruth;
    protected TextView spinnerMerMature;
    protected TextView spinnerMerLevel;
    protected TextView spinnerMerMainSale;
    protected TextView spinnerMerClass;
    protected TextView spinnerMerShopArea;
    protected TextView spinnerCardAppType;
    protected TextView spinnerOtherPosAdvance;
    protected TextView spinnerBindElePlatformResult;
    protected TextView spinnerNoBindElePlatformReason;
    protected TextView spinnerIsExchangeSign;



    public MerMarkInfoFragment() {

    }

    @SuppressLint("ValidFragment")
    public MerMarkInfoFragment(MerShopInfo merchantInfo) {
    	this.merchantInfo = merchantInfo;
    }

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_mer_mark_info;
    }

    @Override
    protected void bindViewIds() {
        spinnerMerInfoTruth = (TextView) mView.findViewById(R.id.tv_mer_info_truth);
        spinnerMerMature = (TextView) mView.findViewById(R.id.tv_mer_mature);
        spinnerMerLevel = (TextView) mView.findViewById(R.id.tv_mer_level);
        spinnerMerMainSale = (TextView)  mView.findViewById(R.id.tv_mer_main_sale);
        spinnerMerClass = (TextView)  mView.findViewById(R.id.tv_mer_class);
        spinnerMerShopArea = (TextView)  mView.findViewById(R.id.tv_mer_shop_area);
        spinnerCardAppType = (TextView)  mView.findViewById(R.id.tv_card_app_type);
        spinnerOtherPosAdvance = (TextView)  mView.findViewById(R.id.tv_other_pos_advance);
        spinnerBindElePlatformResult = (TextView)  mView.findViewById(R.id.tv_bind_ele_platform_result);
        spinnerNoBindElePlatformReason = (TextView)  mView.findViewById(R.id.tv_no_bind_ele_platform_reason);
        spinnerIsExchangeSign = (TextView)  mView.findViewById(R.id.tv_is_exchange_sign);
    }

    @Override
    protected void initData() {
        handlerMerShopInfo(merchantInfo);
        queryMerShopInfoDetail();
    }

    private void handlerMerShopInfo(MerShopInfo merShopInfo) {
        if(merShopInfo == null)return;
        //        商户真实性核查	merInfoTruth
        if(merShopInfo.getMerInfoTruth()!=null
                && !"".equals(merShopInfo.getMerInfoTruth()) && !"-1".equals(merShopInfo.getMerInfoTruth()) ){
            spinnerMerInfoTruth.setText(mSession.getSysDictMap().get(Constants.MER_INFO_TRUTH).get(merShopInfo.getMerInfoTruth()));
        }else{
            spinnerMerInfoTruth.setText("");
        }

//      商户成熟度	merMature
        if(merShopInfo.getMerMature()!=null
                && !"".equals(merShopInfo.getMerMature()) && !"-1".equals(merShopInfo.getMerMature()) ){
            spinnerMerMature.setText(mSession.getSysDictMap().get(Constants.MER_MATURE).get(merShopInfo.getMerMature()));
        }else {
            spinnerMerMature.setText("");
        }

//        商户等级	merLevel
        if(merShopInfo.getMerLevel()!=null
                && !"".equals(merShopInfo.getMerLevel()) && !"-1".equals(merShopInfo.getMerLevel()) ){
            spinnerMerLevel.setText(mSession.getSysDictMap().get(Constants.MER_LEVEL).get(merShopInfo.getMerLevel()));
        }else{
            spinnerMerLevel.setText("");
        }

//        商户主营	merMainSale
        if(merShopInfo.getMerMainSale()!=null
                && !"".equals(merShopInfo.getMerMainSale()) && !"-1".equals(merShopInfo.getMerMainSale()) ){
            spinnerMerMainSale.setText(mSession.getSysDictMap().get(Constants.MER_MAIN_SALE).get(merShopInfo.getMerMainSale()));
        }else{
            spinnerMerMainSale.setText("");
        }


//        商户分类	merClass
        if(merShopInfo.getMerClass()!=null
                && !"".equals(merShopInfo.getMerClass()) && !"-1".equals(merShopInfo.getMerClass()) ){
            spinnerMerClass.setText(mSession.getSysDictMap().get(Constants.MER_CLASS).get(merShopInfo.getMerClass()));
        }else{
            spinnerMerClass.setText("");
        }

//        商铺位置	merShopArea
        if(merShopInfo.getMerShopArea()!=null
                && !"".equals(merShopInfo.getMerShopArea()) && !"-1".equals(merShopInfo.getMerShopArea()) ){
            spinnerMerShopArea.setText(mSession.getSysDictMap().get(Constants.MER_SHOP_AREA).get(merShopInfo.getMerShopArea()));
        }else{
            spinnerMerShopArea.setText("");
        }


//        商户已有产品	cardAppType
        if(merShopInfo.getCardAppType()!=null
                && !"".equals(merShopInfo.getCardAppType()) && !"-1".equals(merShopInfo.getCardAppType()) ){
            spinnerCardAppType.setText(mSession.getSysDictMap().get(Constants.CARD_APP_TYPE).get(merShopInfo.getCardAppType()));
        }else{
            spinnerCardAppType.setText("");
        }

//        其他POS优势	otherPosAdvance
        if(merShopInfo.getOtherPosAdvance()!=null
                && !"".equals(merShopInfo.getOtherPosAdvance()) && !"-1".equals(merShopInfo.getOtherPosAdvance()) ){
            spinnerOtherPosAdvance.setText(mSession.getSysDictMap().get(Constants.OTHER_POS_ADVANCE).get(merShopInfo.getOtherPosAdvance()));
        }else{
            spinnerOtherPosAdvance.setText("");
        }

//        电子平台绑定结果	bindElePlatformResult
        if(merShopInfo.getBindElePlatformResult()!=null
                && !"".equals(merShopInfo.getBindElePlatformResult()) && !"-1".equals(merShopInfo.getBindElePlatformResult()) ){
            spinnerBindElePlatformResult.setText(mSession.getSysDictMap().get(Constants.BIND_ELE_PLATFORM_RESULT).get(merShopInfo.getBindElePlatformResult()));
        }else{
            spinnerBindElePlatformResult.setText("");
        }

//        电子平方台未绑定原因	noBindElePlatformReason
        if(merShopInfo.getNoBindElePlatformReason()!=null
                && !"".equals(merShopInfo.getNoBindElePlatformReason()) && !"-1".equals(merShopInfo.getNoBindElePlatformReason()) ){
            spinnerNoBindElePlatformReason.setText(mSession.getSysDictMap().get(Constants.NO_BIND_ELE_PLATFORM_REASON).get(merShopInfo.getNoBindElePlatformReason()));
        }else {
            spinnerNoBindElePlatformReason.setText("");
        }

//      是否换签	isExchangeSign
        if(merShopInfo.getIsExchangeSign()!=null
                && !"".equals(merShopInfo.getIsExchangeSign()) && !"-1".equals(merShopInfo.getIsExchangeSign()) ){
            spinnerIsExchangeSign.setText(mSession.getSysDictMap().get(Constants.IS_EXCHANGE_SIGN).get(merShopInfo.getIsExchangeSign()));
        }else{
            spinnerIsExchangeSign.setText("");
        }

    }

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求查询接口

    private void queryMerShopInfoDetail() {
        if(merchantInfo == null)return;
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merShopReqInfo.setShopNo(merchantInfo.getShopNo());
        NetAPI.queryEliveMerShopInfoDetail(getActivity(), this, merShopReqInfo);
    }


    @Override
    public void refershUi() {
        queryMerShopInfoDetail();
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                handlerMerShopInfo((MerShopInfo) obj);
                break;
        }
        super.onSuccess(method, obj);
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                Utils.showToast(getActivity(), "商户详情加载失败:" + statusCode + "!");
                break;
        }
    }


}
