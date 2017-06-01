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
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.LazyFragment;
import com.lakala.elive.merapply.activity.MerApplyDetailsActivity;

import java.util.List;

/**
 * 商户信息
 * Created by wenhaogu on 2017/2/27.
 */

public class MerchantsInfoFragment extends LazyFragment {

    private ImageView imgLicensePhoto1, imgLicensePhoto2, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto;
    private TextView tvLicenseNumb, tvLicenseName, tvLegalName, tvLicenseAddress, tvValidDate;
    private ImageView imgOtherPhoto;
    private TextView tvOtherPhoto;

    private MerApplyDetailsResp.ContentBean contentBean;

    public static MerchantsInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        MerchantsInfoFragment fragment = new MerchantsInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_merchants_info;
    }

    @Override
    protected void bindView() {

        imgLicensePhoto1 = findView(R.id.img_license_photo1);//营业执照
        imgLicensePhoto2 = findView(R.id.img_license_photo2);//营业场所
        imgDoorHeadPhoto1 = findView(R.id.img_door_head_photo1);//门头照1
        imgDoorHeadPhoto2 = findView(R.id.img_door_head_photo2);//门头照2
        imgPosPhoto = findView(R.id.img_pos_photo); //机器照
        imgOtherPhoto = findView(R.id.img_door_head_other); //其他


        tvLicenseNumb = findView(R.id.tv_license_numb);//营业执照号
        tvLicenseName = findView(R.id.tv_license_name);//营业执照名称
        tvLegalName = findView(R.id.tv_legal_name);//法人姓名
        tvLicenseAddress = findView(R.id.tv_license_address);//地址
        tvValidDate = findView(R.id.tv_valid_date);//营业执照有效期
        tvOtherPhoto = findView(R.id.tv_door_head_other);//其他

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
            setTextContent(merOpenInfo);
            List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList = contentBean.getMerAttachFileList();
            if (null != merAttachFileList && merAttachFileList.size() > 0) {
                setImgContent(merAttachFileList);
            }
        }
    }

    private void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        //showProgressDialog("加载中...");
        for (MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean : merAttachFileList) {
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


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY4) {
            GetPhotoResq photoResq = (GetPhotoResq) obj;
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
                case "OTHERS"://机器照
                    imgOtherPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
            }
        }

    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(_activity, statusCode);
    }


    private void setTextContent(MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo) {

        tvLicenseNumb.setText(getNoEmptyString(merOpenInfo.getMerLicenceNo()));
        tvLicenseName.setText(getNoEmptyString(merOpenInfo.getRegistName()));
        tvLegalName.setText(getNoEmptyString(merOpenInfo.getCorporateRepresentative()));
        tvLicenseAddress.setText(getNoEmptyString(merOpenInfo.getRegistAddress()));
        tvValidDate.setText(getNoEmptyString(merOpenInfo.getRegistAgeLimit()));

    }
}
