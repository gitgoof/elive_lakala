package com.lakala.elive.preenterpiece.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.preenterpiece.PreEnterMerchanDetailsActivity;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;

/**
 * 商户详情的基本信息
 */
public class PreDetailBasicInfoFragment extends LazyFragment {

    private TextView tvContact, tvPhone, tvMerchantsAddress;
    private PreEnPieceDetailResponse.ContentBean contentBean;

    public static PreDetailBasicInfoFragment newInstance(PreEnPieceDetailResponse.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN, contentBean);
        PreDetailBasicInfoFragment fragment = new PreDetailBasicInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_pre_basic_info;
    }

    @Override
    protected void bindView() {
        tvContact = findView(R.id.tv_contact);//联系人
        tvPhone = findView(R.id.tv_phone);//手机号
        tvMerchantsAddress = findView(R.id.tv_merchants_address);//地址
    }

    @Override
    protected void bindEvent() {
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (PreEnPieceDetailResponse.ContentBean) bundle.getSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN);
            PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo = contentBean.getMerOpenInfo();
            if (null != merOpenInfo) {
                setTextContent(merOpenInfo);
            }
        }
    }

    private void setTextContent(PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo) {
        if (merOpenInfo != null) {
            tvContact.setText(getNoEmptyString(merOpenInfo.getContact()));
            tvPhone.setText(getNoEmptyString(merOpenInfo.getMobileNo()));
            tvMerchantsAddress.setText(getNoEmptyString(merOpenInfo.getMerAddr()));
        } else {
            tvContact.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            tvMerchantsAddress.setVisibility(View.GONE);
        }
    }
}
