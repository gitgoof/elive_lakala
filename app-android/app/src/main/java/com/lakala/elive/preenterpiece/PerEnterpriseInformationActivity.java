package com.lakala.elive.preenterpiece;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerAttachFile;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.gallery.util.ImageLoader;
import com.lakala.elive.merapply.merutils.OpenCameraUtil;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceOcrPhoneRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceOcrPhoneResponse;
import com.lakala.elive.qcodeenter.QCodeCompanyInfoActivity;

import java.io.File;
import java.util.List;


/**
 * 合作商预进件的企业信息界面
 */
public class PerEnterpriseInformationActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();
    private ImageLoader instance = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    private PrePhotoFileFactory photoFileFactory = new PrePhotoFileFactory(this);

    private ImageView imgLicensePhoto1, imgLicensePhoto2, imgProgress, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto, imgOtherPhoto;
    private TextView tvLicense, tvLicense2, tvDoorHeadPhoto1, tvDoorHeadPhoto2, tvPosPhoto, tvOtherPhoto;
    private Button btnNext;

    private final String BUSINESS_LICENCE = "BUSINESS_LICENCE";//营业执照照片
    private final String SHOPINNER = "SHOPINNER";//营业场所照片
    private final String MT = "MT";//门头照
    private final String SYT = "SYT";//机器照
    private final String OTHERS = "OTHERS";//其他

    //区分请求类型
    private final int TYPE_BUSINESS_LICENCE = 1;//营业执照照片
    private final int TYPE_SHOPINNER = 2;//营业场所照片
    private final int TYPE_POS_POSITION = 3;//机器照
    private final int TYPE_SHOP_FRONT = 4;//门头照
    private final int TYPE_OTHER = 5;//其他

    private String comments;//图片排序 同一类型的图片区分

    private String applyId;//进件id
    private boolean postBusinessLicence = false;//营业执照请求
    private String biz_license_registration_code;//ocr识别出来的营业执照号

    private PreEnPieceOcrPhoneRequ preEnPieceOcrPhoneRequ;

    private PreEnPieceDetailRequ preEnPieceDetailRequ;
    private PreEnPieceDetailResponse preEnPieceDetailResponse;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_preenterpiece_enterpriseinfo);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        //营业执照照片
        imgLicensePhoto1 = findView(R.id.img_license_photo1);
        tvLicense = findView(R.id.tv_license);
        //营业场所照片
        imgLicensePhoto2 = findView(R.id.img_license_photo2);
        tvLicense2 = findView(R.id.tv_license2);
        //门头照1
        imgDoorHeadPhoto1 = findView(R.id.img_door_head_photo1);
        tvDoorHeadPhoto1 = findView(R.id.tv_door_head_photo1);
        //门头照2
        imgDoorHeadPhoto2 = findView(R.id.img_door_head_photo2);
        tvDoorHeadPhoto2 = findView(R.id.tv_door_head_photo2);
        //机器照
        imgPosPhoto = findView(R.id.img_pos_photo);
        tvPosPhoto = findView(R.id.tv_pos_photo);
        //其他
        imgOtherPhoto = findView(R.id.img_other_photo);
        tvOtherPhoto = findView(R.id.tv_other_photo);
        //下一步
        btnNext = findView(R.id.btn_next);

        iBtnBack.setVisibility(View.VISIBLE);
        imgProgress = findView(R.id.img_progress);
        imgProgress.setVisibility(View.VISIBLE);
        EliveApplication.setImageProgress(imgProgress, 5);
        //设置按钮可以点击
        btnNext.setBackgroundResource(R.drawable.btn_search_bg);
        btnNext.setEnabled(true);
    }

    @Override
    protected void bindEvent() {
        imgLicensePhoto1.setOnClickListener(this);
        imgLicensePhoto2.setOnClickListener(this);
        imgDoorHeadPhoto1.setOnClickListener(this);
        imgDoorHeadPhoto2.setOnClickListener(this);
        imgPosPhoto.setOnClickListener(this);
        imgOtherPhoto.setOnClickListener(this);
        iBtnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra("APPLYID");
        tvTitleName.setText("企业信息");

        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");
            preEnPieceDetailRequ = new PreEnPieceDetailRequ();
            preEnPieceDetailRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            preEnPieceDetailRequ.setApplyId(applyId);
            NetAPI.preEnterPieDetailRequest(this, this, preEnPieceDetailRequ);
        }

        preEnPieceOcrPhoneRequ = new PreEnPieceOcrPhoneRequ();
        preEnPieceOcrPhoneRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        PreEnPieceOcrPhoneRequ.PartnerApplyInfo merApplyInfo = new PreEnPieceOcrPhoneRequ.PartnerApplyInfo();
        merApplyInfo.setApplyId(applyId);
        Log.e(TAG, applyId + "");
        merApplyInfo.setApplyChannel("02");
        merApplyInfo.setApplyType("1");
        merApplyInfo.setProcess("5");
        preEnPieceOcrPhoneRequ.setMerApplyInfo(merApplyInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back://返回
                finish();
                break;
            case R.id.img_license_photo1://营业执照
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(BUSINESS_LICENCE, "APPLY_LICENSE", "", TYPE_BUSINESS_LICENCE);
                break;
            case R.id.img_license_photo2://营业场所
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(SHOPINNER, "APPLY_LICENSE", "", TYPE_SHOPINNER);
                break;
            case R.id.img_door_head_photo1://门头照1
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(MT, "APPLY_LICENSE", comments = "1", TYPE_SHOP_FRONT);
                break;
            case R.id.img_door_head_photo2://门头照2
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(MT, "APPLY_LICENSE", comments = "2", TYPE_SHOP_FRONT);
                break;
            case R.id.img_pos_photo://机器照
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(SYT, "APPLY_LICENSE", "", TYPE_POS_POSITION);
                break;
            case R.id.img_other_photo://其他
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(OTHERS, "APPLY_LICENSE", "", TYPE_OTHER);
                break;
            case R.id.btn_next:
//                if (postBusinessLicence) {//下一步
                next();
//                }
                break;
        }
    }


    private void next() {
        Intent intent = new Intent(this, PreEnterLicenseEntryPhotoActivity.class)
                .putExtra("APPLYID", applyId)
                .putExtra("BUNISSLINCE", biz_license_registration_code);//营业执照号
        if (!TextUtils.isEmpty(biz_license_registration_code)) {
            intent.putExtra("Biz_license_registration_code", biz_license_registration_code);
        }
        startActivity(intent);
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ELIVE_PARTNER_APPLY_004://提交图片
                PreEnPieceOcrPhoneResponse preEnPieceOcrPhoneResponse = (com.lakala.elive.preenterpiece.response.PreEnPieceOcrPhoneResponse) obj;
                if (preEnPieceOcrPhoneResponse != null) {
                    PreEnPieceOcrPhoneResponse.RespOcr preEnPieceOcrPhoneContent = preEnPieceOcrPhoneResponse.getContent();
                    if (preEnPieceOcrPhoneContent != null && merAttachFile != null) {
                        switch (merAttachFile.photoUploadType) {
                            case TYPE_BUSINESS_LICENCE://营业执照
                                postBusinessLicence = true;
                                if (null == preEnPieceOcrPhoneContent.getLicenseInfo() || TextUtils.isEmpty(preEnPieceOcrPhoneContent.getLicenseInfo().getBiz_license_registration_code())) {
                                    Utils.showToast(this, "营业执照比较模糊无法识别,建议重新上传");
                                    tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                                } else {
                                    tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                    biz_license_registration_code = preEnPieceOcrPhoneContent.getLicenseInfo().getBiz_license_registration_code();//营业执照号码
                                }
                                break;
                            case TYPE_SHOPINNER://营业场所
                                tvLicense2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                break;
                            case TYPE_SHOP_FRONT://门头照1/2
                                if ("1".equals(comments)) {
                                    tvDoorHeadPhoto1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                } else if ("2".equals(comments)) {
                                    tvDoorHeadPhoto2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                }
                                break;
                            case TYPE_POS_POSITION://机器照
                                tvPosPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                break;
                            case TYPE_OTHER://其他
                                tvOtherPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                                break;
                        }
                    } else {
                        Log.e(TAG, "preEnPieceOcrPhoneContent或merAttachFile为空");
                    }
                }
                break;
            case NetAPI.ACTION_MER_APPLY4://编辑状态获取数据
                GetPhotoResq photoResq = (GetPhotoResq) obj;
                if (photoResq != null && photoResq.getContent() != null && photoResq.getContent().fileType != null) {
                    switch (photoResq.getContent().fileType) {
                        case "BUSINESS_LICENCE"://营业执照
                            imgLicensePhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                            postBusinessLicence = true;
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
                            imgOtherPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                            break;
                    }
                } else {
                    Log.e(TAG, "ACTION_MER_APPLY4接口异常");
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_002://编辑转态获取图片数据
                preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null) {
                    PreEnPieceDetailResponse.ContentBean content = preEnPieceDetailResponse.getContent();
                    if (content != null) {
                        List<PreEnPieceDetailResponse.ContentBean.MerAttachFile> merAttachFileList = content.getMerAttachFileList();
                        if (null != merAttachFileList && merAttachFileList.size() > 0) {
                            setImgContent(merAttachFileList);
                        }
                    }
                } else {
                    Log.e(TAG, "preEnPieceDetailResponse为空");
                }
                break;
        }

//        if (postBusinessLicence) {
//            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
//            btnNext.setEnabled(true);
//        } else {
//            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
//            btnNext.setEnabled(false);
//        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (statusCode.equals("502") && method == NetAPI.ELIVE_PARTNER_APPLY_004) {//如果是提交图片
            if (merAttachFile != null) {
                switch (merAttachFile.photoUploadType) {
                    case TYPE_BUSINESS_LICENCE:
                        postBusinessLicence = false;
                        tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                        Utils.showToast(this, "营业执照上传失败");
                        break;
                    case TYPE_SHOPINNER:
                        tvLicense2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                        Utils.showToast(this, "营业场所上传失败");
                        break;
                    case TYPE_SHOP_FRONT:
                        if ("1".equals(comments)) {
                            tvDoorHeadPhoto1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                            Utils.showToast(this, "门头照上传失败");
                        } else if ("2".equals(comments)) {
                            tvDoorHeadPhoto1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                            Utils.showToast(this, "门头照2上传失败");
                        }
                        break;
                    case TYPE_POS_POSITION:
                        tvPosPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                        Utils.showToast(this, "机器照上传失败");
                        break;
                    case TYPE_OTHER://其他
                        tvOtherPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                        Utils.showToast(this, "其他照片上传失败");
                        break;
                    case NetAPI.ACTION_MER_APPLY4:
                        Utils.showToast(this, "获取照片失败");
                        break;
                }
            } else {
                Log.e(TAG, "merAttachFile为空");
            }
        } else {
            Utils.showToast(this, statusCode);
        }
        if (postBusinessLicence) {
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
        } else {
            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
            btnNext.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OpenCameraUtil.newInstace().REQUEST_CODE_PICK_IMAGE && null != data) {//相册获取
            String path = data.getStringExtra("GalleryActivity");


            if(TextUtils.isEmpty(path)){
                Utils.showToast(PerEnterpriseInformationActivity.this, "没有图片返回!");
                return;
            }
            final File file = new File(path);
            if(!file.exists()){
                Utils.showToast(PerEnterpriseInformationActivity.this, "图片不存在!");
                return;
            }
            if(file.isDirectory()){
                Utils.showToast(PerEnterpriseInformationActivity.this, "图片不存在!");
                return;
            }
            if(file.length() == 0){
                Utils.showToast(PerEnterpriseInformationActivity.this, "图片不存在!");
                return;
            }


            if (new File(path).getName().contains(".gif")) {
                return;
            }
            getPhotoFile(path);
        } else if (requestCode == OpenCameraUtil.newInstace().REQUEST_CAMERA && OpenCameraUtil.newInstace().imageUri != null) {//拍照
            String path = OpenCameraUtil.newInstace().imageUri.getPath();
            if (new File(path).exists()) {
                getPhotoFile(path);
            } else {
                OpenCameraUtil.newInstace().getImageFromAlbum(this);
            }
        }
    }


    private MerAttachFile merAttachFile;

    /**
     * 获取上传照片的文件Bean 然后上传
     *
     * @param path
     */
    public void getPhotoFile(final String path) {
        showProgressDialog("上传中...");
        merAttachFile = photoFileFactory.photoFileCreate(path);
        preEnPieceOcrPhoneRequ.setMerAttachFile(merAttachFile);
        NetAPI.preEnterPieOcrOrPhoneRequest(this, this, preEnPieceOcrPhoneRequ);
        setImgeContent(path, merAttachFile);
    }

    /**
     * 设置照片
     *
     * @param merAttachFile
     */
    private void setImgeContent(String path, MerAttachFile merAttachFile) {
        if (merAttachFile != null) {
            switch (merAttachFile.getFileType()) {
                case BUSINESS_LICENCE://营业执照
                    instance.loadImage(path, imgLicensePhoto1);
                    break;
                case SHOPINNER://营业场所照
                    instance.loadImage(path, imgLicensePhoto2);
                    break;
                case MT://门头照
                    instance.loadImage(path, merAttachFile.getComments().equals("1") ? imgDoorHeadPhoto1 : imgDoorHeadPhoto2);
                    break;
                case SYT://机器照片
                    instance.loadImage(path, imgPosPhoto);
                    break;
                case OTHERS://其他
                    instance.loadImage(path, imgOtherPhoto);
                    break;
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
                    NetAPI.merApply4(this, this, new GetPhotoReq(merAttachFileListBean.getFileId()));
                }
            }
        }
    }
}
