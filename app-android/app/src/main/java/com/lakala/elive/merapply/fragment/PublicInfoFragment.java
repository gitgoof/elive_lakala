package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
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

public class PublicInfoFragment extends LazyFragment {

    private ImageView imgIdPhoto, imgIdPhoto2, imgProtocolPhoto, imgProtocolPhoto1, imgProtocolPhoto2, imgAccountProve
//            ,imgBack
            ;
    private TextView tvUsername, tvCardNumber, tvIdNumber, tvIdDate, tvBankName
//            ,tvBack
            ;
    private TextView tvCardName;
    private View viewLine;
    private TextView tvCardNameTitle;

    private MerApplyDetailsResp.ContentBean contentBean;

    private TextView mTvUsername;
    private TextView mTvCardName;

    public static PublicInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        PublicInfoFragment fragment = new PublicInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_public_info;
    }

    @Override
    protected void bindView() {
        mTvUsername = findView(R.id.username);
        mTvCardName = findView(R.id.card_number);
        tvUsername = findView(R.id.tv_username);
        tvCardNumber = findView(R.id.tv_card_number);
        tvIdNumber = findView(R.id.tv_id_number);
        tvIdDate = findView(R.id.tv_id_date);
        tvBankName = findView(R.id.tv_bank_name);
        tvCardName = findView(R.id.tv_card_name);
        viewLine = findView(R.id.v21);
        tvCardNameTitle = findView(R.id.id_card_name);
//        tvBack=findView(R.id.tv_back);
//
//        imgBack=findView(R.id.img_back);
        imgIdPhoto = findView(R.id.img_id_photo);//身份证正面
        imgIdPhoto2 = findView(R.id.img_id_photo2);//身份证反面
        imgProtocolPhoto = findView(R.id.img_protocol_photo);//商户协议
        imgProtocolPhoto1 = findView(R.id.img_protocol_photo1);
        imgProtocolPhoto2 = findView(R.id.img_protocol_photo2);

        //开户许可
        imgAccountProve = findView(R.id.img_account_prove);

        mTvCardName.setText("入款账号");
        mTvUsername.setText("账户名称");
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
        tvIdDate.setText(merOpenInfo.getIdCardExpire()+"");
        tvBankName.setText(getNoEmptyString(merOpenInfo.getOpenningBankName()));
        if(getNoEmptyString(merOpenInfo.getAccountKind()).equals("58") ? true : false){
            // 对私
            tvCardNameTitle.setVisibility(View.GONE);
            tvCardName.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else {
            // 对公
            tvCardNameTitle.setVisibility(View.VISIBLE);
            tvCardName.setVisibility(View.VISIBLE);
            viewLine.setVisibility(View.VISIBLE);
            tvCardName.setText(getNoEmptyString(merOpenInfo.getIdName()));
        }
    }

    private void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        // showProgressDialog("加载中...");
        for (MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean : merAttachFileList) {
            if ("ID_CARD_FRONT".equals(merAttachFileListBean.getFileType()) ||
                    "ID_CARD_BEHIND".equals(merAttachFileListBean.getFileType())
                    || "XY".equals(merAttachFileListBean.getFileType())
                    || "KH".equals(merAttachFileListBean.getFileType())
//                    ||"BANK_CARD".equals(merAttachFileListBean.getFileType())
                    ) {
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
//                case "BANK_CARD":
//                    imgBack.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                    break;
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
