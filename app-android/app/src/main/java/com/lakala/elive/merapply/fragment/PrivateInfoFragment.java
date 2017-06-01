package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.utils.EditUtil;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class PrivateInfoFragment extends LazyFragment {

    private ImageView imgIdPhoto, imgIdPhoto2, imgProtocolPhoto,imgProtocolPhoto2, imgProtocolPhoto1,imgBack;
    private TextView tvUsername, tvCardNumber, tvIdNumber, tvIdDate, tvBankName;

    private MerApplyDetailsResp.ContentBean contentBean;

    public static PrivateInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        PrivateInfoFragment fragment = new PrivateInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_private_info;
    }
    private TextView mTvUsername;
    private TextView mTvCardName;
    @Override
    protected void bindView() {
        mTvUsername = findView(R.id.username);
        tvUsername = findView(R.id.tv_username);
        mTvCardName = findView(R.id.card_number);
        tvCardNumber = findView(R.id.tv_card_number);
        tvIdNumber = findView(R.id.tv_id_number);
        tvIdDate = findView(R.id.tv_id_date);
        tvBankName = findView(R.id.tv_bank_name);

        imgBack=findView(R.id.img_back);//银行卡
        imgIdPhoto = findView(R.id.img_id_photo);//身份证正面
        imgIdPhoto2 = findView(R.id.img_id_photo2);//身份证反面
        imgProtocolPhoto = findView(R.id.img_protocol_photo);//商户协议
        imgProtocolPhoto1 = findView(R.id.img_protocol_photo1);//商户协议2
        imgProtocolPhoto2 = findView(R.id.img_protocol_photo2);//商户协议3

        mTvCardName.setText("卡号");
        mTvUsername.setText("入账户名");
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            setTextContent();

            setImgContent(contentBean.getMerAttachFileList());
        }
    }


    private void setTextContent() {
        MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
        tvUsername.setText(getNoEmptyString(merOpenInfo.getAccountName()));
        tvCardNumber.setText(getNoEmptyString(merOpenInfo.getAccountNo()));
        tvIdNumber.setText(getNoEmptyString(merOpenInfo.getIdCard()));
        tvIdDate.setText(
//                getTime(Long.parseLong(
                        merOpenInfo.getIdCardExpire()
//        ))
        );
        tvBankName.setText(getNoEmptyString(merOpenInfo.getOpenningBankName()));

    }

    private void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        // showProgressDialog("加载中...");
        for (MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean : merAttachFileList) {
            if ("ID_CARD_FRONT".equals(merAttachFileListBean.getFileType()) ||
                    "ID_CARD_BEHIND".equals(merAttachFileListBean.getFileType())
                    || "XY".equals(merAttachFileListBean.getFileType())
                    ||"BANK_CARD".equals(merAttachFileListBean.getFileType())) {
                showProgressDialog("加载中...");
                NetAPI.merApply4(_activity, this, new GetPhotoReq(merAttachFileListBean.getFileId()));
            }
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY4) {
            GetPhotoResq photoResq = (GetPhotoResq) obj;
            switch (photoResq.getContent().fileType) {
                case "BANK_CARD":
                    imgBack.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case "ID_CARD_FRONT":
                    imgIdPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case "ID_CARD_BEHIND":
                    imgIdPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case "XY":
                    if ("3".equals(photoResq.getContent().comments)) {
                        imgProtocolPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else  if ("2".equals(photoResq.getContent().comments)) {
                        imgProtocolPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else    if ("1".equals(photoResq.getContent().comments)){
                        imgProtocolPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    }
                    break;
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
