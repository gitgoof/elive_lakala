package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;

/**
 * Created by wenhaogu on 2017/3/3.
 */

public class ApplyInfoFragment extends LazyFragment {

    private TextView tvApplyId, tvApplyType, tvProcess , tvResultId, tvResultDesc, tvMerchantId, tvStopId, tvTime;

    private MerApplyDetailsResp.ContentBean contentBean;

    public static ApplyInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        ApplyInfoFragment fragment = new ApplyInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_apply_info;
    }

    @Override
    protected void bindView() {
        tvApplyId = findView(R.id.tv_applyid);
        tvApplyType = findView(R.id.tv_apply_type);
        tvProcess = findView(R.id.tv_process);
        tvResultId = findView(R.id.tv_result_id);
        tvResultDesc = findView(R.id.tv_result_desc);
        tvMerchantId = findView(R.id.tv_merchant_id);
        tvStopId = findView(R.id.tv_stop_id);
        tvTime = findView(R.id.tv_time);

    }

    @Override
    protected void bindEvent() {

    }

//    @Override
//    protected void bindData() {
//        Bundle bundle = getArguments();
//        if (null != bundle) {
//            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
//            setTextContent();
//        }
//    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            setTextContent();
        }
    }

    private void setTextContent() {
        MerApplyDetailsResp.ContentBean.MerApplyInfoBean merApplyInfo = contentBean.getMerApplyInfo();
        MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
        tvApplyId.setText(getNoEmptyString(merApplyInfo.getApplyId()));
        tvApplyType.setText(getNoEmptyString(merOpenInfo.getAccountKind()).equals("58") ? "对私" : "对公");
        tvProcess.setText(getNoEmptyString(merApplyInfo.getProcessName()));
        tvResultId.setText(getNoEmptyString(merApplyInfo.getResultId()));

        final String process = merApplyInfo.getProcess();
        if(!TextUtils.isEmpty(process)&&!process.equals("REBUT")){
            findView(R.id.linear_apply_info_v6_content).setVisibility(View.GONE);
            findView(R.id.v7).setVisibility(View.GONE);
        } else {
            tvResultDesc.setText(getNoEmptyString(merApplyInfo.getResultDesc()));
        }

        tvMerchantId.setText(getNoEmptyString(merOpenInfo.getMerchantId()));
        tvStopId.setText(getNoEmptyString(merOpenInfo.getShopId()));
        tvTime.setText(getNoEmptyString(merApplyInfo.getApplyTimeStr()));

    }


}
