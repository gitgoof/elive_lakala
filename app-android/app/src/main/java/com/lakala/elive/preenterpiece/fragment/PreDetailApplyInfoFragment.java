package com.lakala.elive.preenterpiece.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.preenterpiece.PreEnterMerchanDetailsActivity;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;

/**
 * 商户详情的申请信息
 */
public class PreDetailApplyInfoFragment extends LazyFragment {

    private TextView tvApplyId, tvApplyType, tvProcess, tvResultId, tvResultDesc, tvMerchantId, tvStopId, tvTime;
    private PreEnPieceDetailResponse.ContentBean contentBean;

    public static PreDetailApplyInfoFragment newInstance(PreEnPieceDetailResponse.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN, contentBean);
        PreDetailApplyInfoFragment fragment = new PreDetailApplyInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_pre_apply_info;
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

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (PreEnPieceDetailResponse.ContentBean) bundle.getSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN);
            setTextContent();
        }
    }

    private void setTextContent() {
        PreEnPieceDetailResponse.ContentBean.PartnerApplyInfo merApplyInfo = contentBean.getMerApplyInfo();
        PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo = contentBean.getMerOpenInfo();
        tvApplyId.setText(getNoEmptyString(merApplyInfo.getApplyId()));

        if (merOpenInfo != null) {
            if ("57".equals(merOpenInfo.getAccountKind())) {
                tvApplyType.setText(getNoEmptyString("对公"));
            } else if ("58".equals(merOpenInfo.getAccountKind())) {
                tvApplyType.setText(getNoEmptyString("对私"));
            }
        }
        if (merApplyInfo != null) {
            if ("0".equals(merApplyInfo.getStatus())) {
                tvProcess.setText(getNoEmptyString("待录入"));//0：待录入 1：已提交、2：处理中、3：处理成功、4：处理失败
            } else if ("1".equals(merApplyInfo.getStatus())) {
                tvProcess.setText(getNoEmptyString("已提交"));
            } else if ("2".equals(merApplyInfo.getStatus())) {
                tvProcess.setText(getNoEmptyString("处理中"));
            } else if ("3".equals(merApplyInfo.getStatus())) {
                tvProcess.setText(getNoEmptyString("处理成功"));
            } else if ("4".equals(merApplyInfo.getStatus())) {
                tvProcess.setText(getNoEmptyString("处理失败"));
            }
            tvTime.setText(getNoEmptyString(merApplyInfo.getApplyTimeStr()));//申请时间
        }
    }
}
