package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;

/**
 * 基本信息
 * Created by wenhaogu on 2017/2/27.
 */

public class BasicInfoFragment extends LazyFragment {
    private TextView tvSignOrderName, tvContact, tvPhone, tvEmail, tvSmallClass, tvMerchantsAddress, tvArea, tvBusinessHours, tvBigClass, tvMccCode,tvCentreClass, tvBizContent;
    private MerApplyDetailsResp.ContentBean contentBean;

    public static BasicInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        BasicInfoFragment fragment = new BasicInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_basic_info;
    }

    @Override
    protected void bindView() {
        tvSignOrderName = findView(R.id.tv_sign_order_name);//签购单名单
        tvContact = findView(R.id.tv_contact);//联系人
        tvPhone = findView(R.id.tv_phone);//手机号
        tvEmail = findView(R.id.tv_email);//邮箱
        tvBigClass = findView(R.id.tv_big_class);
        tvCentreClass = findView(R.id.tv_centre_class);
        tvSmallClass = findView(R.id.tv_small_class);//mcc
        tvMccCode = findView(R.id.tv_mcc_code);//mcc
        tvMerchantsAddress = findView(R.id.tv_merchants_address);//地址
        tvArea = findView(R.id.tv_area);//营业面积
        tvBusinessHours = findView(R.id.tv_business_hours);//营业时间
        tvBizContent = findView(R.id.tv_biz_content);//经营内容

    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
            if (null != merOpenInfo) {
                setTextContent(merOpenInfo);
            }
        }
    }


    private void setTextContent(MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo) {
        tvSignOrderName.setText(getNoEmptyString(merOpenInfo.getMerchantName()));
        tvContact.setText(getNoEmptyString(merOpenInfo.getContact()));
        tvPhone.setText(getNoEmptyString(merOpenInfo.getMobileNo()));
        tvEmail.setText(getNoEmptyString(merOpenInfo.getEmailAddr()));
        tvBigClass.setText(getNoEmptyString(merOpenInfo.getMccBigTypeStr()));
        tvCentreClass.setText(getNoEmptyString(merOpenInfo.getMccSmallTypeStr()));
        tvSmallClass.setText(getNoEmptyString(merOpenInfo.getMccCodeStr()));
        tvMerchantsAddress.setText(getNoEmptyString(merOpenInfo.getMerAddr()));
        tvArea.setText(getNoEmptyString(merOpenInfo.getBusinessAreaStr()));
        tvBusinessHours.setText(getNoEmptyString(merOpenInfo.getBusinessTime()));
        tvBizContent.setText(getNoEmptyString(merOpenInfo.getBussinessContentStr()));
        tvMccCode.setText(getNoEmptyString(merOpenInfo.getMccCode()));
    }
}
