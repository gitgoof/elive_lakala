package com.lakala.elive.preenterpiece.fragment;

import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.List;

/**
 * 商户详情的商户信息
 */

public class PreDetailMerchantsInfoFragment extends LazyFragment {

    private ImageView imgLicensePhoto1, imgLicensePhoto2, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto, imgOther;
    private TextView tvLicenseNumb, tvLicenseName, tvLegalName, tvLicenseAddress, tvValidDate;
    private String TAG = getClass().getSimpleName();
    private PreEnPieceDetailResponse.ContentBean contentBean;

    public static PreDetailMerchantsInfoFragment newInstance(PreEnPieceDetailResponse.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PreEnterMerchanDetailsActivity.CONTENT_BEAN, contentBean);
        PreDetailMerchantsInfoFragment fragment = new PreDetailMerchantsInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_pre_merchants_info;
    }

    @Override
    protected void bindView() {

        imgLicensePhoto1 = findView(R.id.img_license_photo1);//营业执照
        imgLicensePhoto2 = findView(R.id.img_license_photo2);//营业场所
        imgDoorHeadPhoto1 = findView(R.id.img_door_head_photo1);//门头照1
        imgDoorHeadPhoto2 = findView(R.id.img_door_head_photo2);//门头照2
        imgPosPhoto = findView(R.id.img_pos_photo); //机器照
        imgOther = findView(R.id.img_other_photo);//其他

        tvLicenseNumb = findView(R.id.tv_license_numb);//营业执照号
        tvLicenseName = findView(R.id.edt_license_name);//营业执照名称
        tvLegalName = findView(R.id.edt_legal_name);//法人姓名
        tvLicenseAddress = findView(R.id.edt_license_address);//地址
        tvValidDate = findView(R.id.tv_valid_date);//营业执照有效期
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
            setTextContent(merOpenInfo);
            List<PreEnPieceDetailResponse.ContentBean.MerAttachFile> merAttachFileList = contentBean.getMerAttachFileList();
            if (null != merAttachFileList && merAttachFileList.size() > 0) {
                setImgContent(merAttachFileList);
            }
        }
    }

    private void setImgContent(List<PreEnPieceDetailResponse.ContentBean.MerAttachFile> merAttachFileList) {
        //showProgressDialog("加载中...");
        if (merAttachFileList != null) {
            for (PreEnPieceDetailResponse.ContentBean.MerAttachFile merAttachFileListBean : merAttachFileList) {
                if (merAttachFileListBean.getFileType().equals("BUSINESS_LICENCE")
                        || merAttachFileListBean.getFileType().equals("SHOPINNER")
                        || merAttachFileListBean.getFileType().equals("MT")
                        || merAttachFileListBean.getFileType().equals("SYT")
                        || merAttachFileListBean.getFileType().equals("OTHERS")) {
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
                    case "BUSINESS_LICENCE"://营业执照
                        imgLicensePhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                    case "SHOPINNER"://营业场所
                        imgLicensePhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                    case "MT"://门头照
                        if (TextUtils.isEmpty(photoResq.getContent().comments)) {
                            return;
                        }
                        if (photoResq.getContent().comments.equals("1")) {
                            imgDoorHeadPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        } else if (photoResq.getContent().comments.equals("2")) {
                            imgDoorHeadPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        }
                        break;
                    case "SYT"://机器照
                        imgPosPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                        break;
                    case "OTHERS"://其他
                        imgOther.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
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


    private void setTextContent(PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo) {
        if (merOpenInfo != null) {
            tvLicenseNumb.setText(getNoEmptyString(merOpenInfo.getMerLicenceNo()));
            tvLicenseName.setText(getNoEmptyString(merOpenInfo.getRegistName()));
            tvLegalName.setText(getNoEmptyString(merOpenInfo.getCorporateRepresentative()));
            tvLicenseAddress.setText(getNoEmptyString(merOpenInfo.getRegistAddress()));
            tvValidDate.setText(getNoEmptyString(merOpenInfo.getRegistAgeLimit()));
        }
    }
}
