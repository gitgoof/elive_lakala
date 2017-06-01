package com.lakala.elive.preenterpiece.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.preenterpiece.PreEnterMerchanDetailsActivity;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 商户详情的结算信息
 */
public class PreDetailSettleInfoFragment extends LazyFragment {

    private ImageView imgIdPhoto, imgIdPhoto2, imgProtocolPhoto, imgProtocolPhoto1, imgProtocolPhoto2, imgAccountProve;
    private TextView tvUsername, tvCardNumber, tvIdNumber, tvIdDate, tvBankName;
    private PreEnPieceDetailResponse.ContentBean contentBean;
    private String TAG = getClass().getSimpleName();

    public static PreDetailSettleInfoFragment newInstance(PreEnPieceDetailResponse.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN, contentBean);
        PreDetailSettleInfoFragment fragment = new PreDetailSettleInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_pre_settle_info;
    }

    @Override
    protected void bindView() {
        tvUsername = findView(R.id.tv_username);
        tvCardNumber = findView(R.id.tv_card_number);
        tvIdNumber = findView(R.id.tv_id_number);
        tvIdDate = findView(R.id.tv_id_date);
        tvBankName = findView(R.id.tv_bank_name);

        imgIdPhoto = findView(R.id.img_id_photo);//身份证正面
        imgIdPhoto2 = findView(R.id.img_id_photo2);//身份证反面
        imgProtocolPhoto = findView(R.id.img_protocol_photo);//商户协议
        imgProtocolPhoto1 = findView(R.id.img_protocol_photo1);
        imgProtocolPhoto2 = findView(R.id.img_protocol_photo2);

        //开户许可
        imgAccountProve = findView(R.id.img_account_prove);
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
            setImgContent(contentBean.getMerAttachFileList());
        }
    }


    private void setTextContent() {
        PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo = contentBean.getMerOpenInfo();
        if (merOpenInfo != null) {
            tvUsername.setText(getNoEmptyString(merOpenInfo.getIdName()));
            tvCardNumber.setText(getNoEmptyString(merOpenInfo.getAccountNo()));
            tvIdNumber.setText(getNoEmptyString(merOpenInfo.getIdCard()));
            if (merOpenInfo.getIdCardExpire() != null) {
                tvIdDate.setText(getNoEmptyString(getTime(Long.parseLong(merOpenInfo.getIdCardExpire() + ""))));
            }
            tvBankName.setText(getNoEmptyString(merOpenInfo.getOpenningBankName()));
        }
    }

    private void setImgContent(List<PreEnPieceDetailResponse.ContentBean.MerAttachFile> merAttachFileList) {
        // showProgressDialog("加载中...");
        if (merAttachFileList != null) {
            for (PreEnPieceDetailResponse.ContentBean.MerAttachFile merAttachFileListBean : merAttachFileList) {
                if ("ID_CARD_FRONT".equals(merAttachFileListBean.getFileType()) ||
                        "ID_CARD_BEHIND".equals(merAttachFileListBean.getFileType())
                        || "XY".equals(merAttachFileListBean.getFileType())
                        || "KH".equals(merAttachFileListBean.getFileType())) {
                    showProgressDialog("加载中...");
                    NetAPI.merApply4(_activity, this, new GetPhotoReq(merAttachFileListBean.getFileId()));
                }
            }
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY4) {
            GetPhotoResq photoResq = (GetPhotoResq) obj;
            if (photoResq != null && photoResq.getContent() != null && photoResq.getContent().fileType != null) {
                switch (photoResq.getContent().fileType) {
                    case "ID_CARD_FRONT":
                        imgIdPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                    case "ID_CARD_BEHIND":
                        imgIdPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                    case "XY":
                        if ("2".equals(photoResq.getContent().comments)) {
                            imgProtocolPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        } else if ("3".equals(photoResq.getContent().comments)) {
                            imgProtocolPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        } else {
                            imgProtocolPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        }
                        break;
                    case "KH":
                        imgAccountProve.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                }
            } else {
                Log.e(TAG, "ACTION_MER_APPLY4接口异常");
            }
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(_activity, statusCode);
    }

    private String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        return format.format(d1);
    }

}
