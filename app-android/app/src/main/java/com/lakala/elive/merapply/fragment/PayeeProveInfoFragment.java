package com.lakala.elive.merapply.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

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
 * 收款人证明信息
 * Created by wenhaogu on 2017/1/13.
 */

public class PayeeProveInfoFragment extends LazyFragment {

    private ImageView imgReplenish1, imgReplenish2, imgLicensePhoto, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto;

    private MerApplyDetailsResp.ContentBean contentBean;

    private String applyId;//进件id
    private String id;


    public static PayeeProveInfoFragment newInstance(MerApplyDetailsResp.ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MerApplyDetailsActivity.CONTENT_BEAN, contentBean);
        PayeeProveInfoFragment fragment = new PayeeProveInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_payee_prove_info;
    }


    @Override
    protected void bindView() {
        //补充1
        imgReplenish1 = findView(R.id.img_replenish1);
        //补充2
        imgReplenish2 = findView(R.id.img_replenish2);
        //营业场所
        imgLicensePhoto = findView(R.id.img_license_photo);
        //门头照2
        imgDoorHeadPhoto1 = findView(R.id.img_door_head_photo1);
        //门头照2
        imgDoorHeadPhoto2 = findView(R.id.img_door_head_photo2);
        //机器照
        imgPosPhoto = findView(R.id.img_pos_photo);
    }

    @Override
    protected void bindEvent() {
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            contentBean = (MerApplyDetailsResp.ContentBean) bundle.getSerializable(MerApplyDetailsActivity.CONTENT_BEAN);
            List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList = contentBean.getMerAttachFileList();
            if (null != merAttachFileList && merAttachFileList.size() > 0) {
                setImgContent(merAttachFileList);
            }
        }
    }


    public void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        //
        for (int y = 0; y < merAttachFileList.size(); y++) {
            MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean = merAttachFileList.get(y);
            if (merAttachFileListBean.getFileType().equals("OTHERS")
                    || merAttachFileListBean.getFileType().equals("SHOPINNER")
                    || merAttachFileListBean.getFileType().equals("MT")
                    || merAttachFileListBean.getFileType().equals("SYT")) {
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
//            if (TextUtils.isEmpty(photoResq.getContent().comments)) {//兼容老数据,以前的照片没有comments
//                photoResq.getContent().comments = i + "";
//                i++;
//            }
            switch (photoResq.getContent().fileType) {
                case "OTHERS"://补充
                    if (TextUtils.isEmpty(photoResq.getContent().comments)) {
                        return;
                    }
                    if (photoResq.getContent().comments.equals("1")) {
                        imgReplenish1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else if (photoResq.getContent().comments.equals("2")) {
                        imgReplenish2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    }
                    break;
                case "SHOPINNER"://营业场所
                    imgLicensePhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
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

            }
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(_activity, statusCode);
    }


}
