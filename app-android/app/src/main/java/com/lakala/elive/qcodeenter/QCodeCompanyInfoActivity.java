package com.lakala.elive.qcodeenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerApplyInfo;
import com.lakala.elive.beans.MerAttachFile;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq8;
import com.lakala.elive.common.net.req.PhotoDiscernReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.PhotoDiscernResp;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;
import com.lakala.elive.merapply.activity.InformationInputActivity;
import com.lakala.elive.merapply.activity.LicenseEntryPhotoActivity;
import com.lakala.elive.merapply.gallery.util.ImageLoader;
import com.lakala.elive.merapply.merutils.OpenCameraUtil;
import com.lakala.elive.merapply.merutils.PhotoFileFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Q码进件的企业信息
 */

public class QCodeCompanyInfoActivity extends BaseActivity {

    private ImageView imgLicensePhoto1, imgLicensePhoto2, imgProgress, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto,imgOther;
    private TextView tvLicense, tvLicense2, tvDoorHeadPhoto1, tvDoorHeadPhoto2, tvPosPhoto,tvOther;
    private Button btnNext;

    private final String BUSINESS_LICENCE = "BUSINESS_LICENCE";//营业执照照片
    private final String SHOPINNER = "SHOPINNER";//营业场所照片
    private final String MT = "MT";//门头照
    private final String SYT = "SYT";//机器照
    private final String OTHERS = "OTHERS";//其他

    //区分请求类型
    private final int TYPE_BUSINESS_LICENCE = 1;//营业执照照片
    private final int TYPE_SHOPINNER = 2;//营业场所照片
    private final int TYPE_SHOP_FRONT = 3;//门头照
    private final int TYPE_POS_POSITION = 4;//机器照
    private final int TYPE_OTHERS = 5;//其他

    private String comments;//图片排序 同一类型的图片区分

    private ImageLoader instance = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    private PhotoFileFactory photoFileFactory = new PhotoFileFactory(this);

    private PhotoDiscernReq photoDiscernReq;
    private String applyId;//进件id
    private long id;
    private String merchantId;
    private boolean postBusinessLicence = false;//营业执照请求
//    private boolean postShopinner = false;//营业场所请求

    private String biz_license_registration_code;//ocr识别出来的营业执照号
    private MerApplyDetailsResp.ContentBean contentBean;//详情传过来的回显数据

    private boolean   flagIsAccessory=false;//是否上传了附件（除营业执照外的图片）

    private ImageView   imgBack;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_business_license_discern);
    }

    @Override
    protected void bindView() {
        imgBack=findView(R.id.btn_iv_back);
        //其他
        imgOther = findView(R.id.img_door_head_photo_other);
        tvOther = findView(R.id.tv_door_head_photo2_other);
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
        //下一步
        btnNext = findView(R.id.btn_next);
        //tvTitleName.setText("补充说明");
        tvTitleName.setText("企业信息");

        imgProgress = findView(R.id.img_progress);

        EliveApplication.setImageQCodeProgress(imgProgress, 4, EliveApplication.PUBLICQCODE);
    }

    @Override
    protected void bindEvent() {
        imgOther.setOnClickListener(this);
        imgLicensePhoto1.setOnClickListener(this);
        imgLicensePhoto2.setOnClickListener(this);
        imgDoorHeadPhoto1.setOnClickListener(this);
        imgDoorHeadPhoto2.setOnClickListener(this);
        imgPosPhoto.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra(InformationInputActivity.APPLY_ID);
        id = intent.getLongExtra(InformationInputActivity.ID, 0);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);

        photoDiscernReq = new PhotoDiscernReq();
        photoDiscernReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        photoDiscernReq.setMerApplyInfo(new MerApplyInfo());
        photoDiscernReq.setMerAttachFileList(new ArrayList<MerAttachFile>());
        photoDiscernReq.getMerApplyInfo().setApplyChannel("03");
        photoDiscernReq.getMerApplyInfo().setApplyType("1");
        photoDiscernReq.getMerApplyInfo().setApplyId(applyId);

        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.img_door_head_photo_other://其他
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(OTHERS, "APPLY_SUP_PROOF", "", TYPE_OTHERS);
                break;
            case R.id.img_license_photo1://营业执照
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(BUSINESS_LICENCE, "APPLY_LICENSE", "", TYPE_BUSINESS_LICENCE);
                break;
            case R.id.img_license_photo2://营业场所
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(SHOPINNER, "APPLY_SUP_PROOF", "", TYPE_SHOPINNER);
                break;
            case R.id.img_door_head_photo1://门头照1
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(MT, "APPLY_SUP_PROOF", comments = "1", TYPE_SHOP_FRONT);
                break;
            case R.id.img_door_head_photo2://门头照2
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(MT, "APPLY_SUP_PROOF", comments = "2", TYPE_SHOP_FRONT);
                break;
            case R.id.img_pos_photo://机器照
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(SYT, "APPLY_SUP_PROOF", "", TYPE_POS_POSITION);
                break;
            case R.id.btn_next:
                if(postBusinessLicence){
                    if(flagIsAccessory){//有附件，掉接口
                        nextRequest();
                    }else{//没有附件，直接跳转
                        next();
                    }
                }
                break;
        }
    }


    private void next() {
        Intent intent = new Intent(this, QCodeLicenseEntryPhotoActivity.class)
                .putExtra(InformationInputActivity.APPLY_ID, applyId)
                .putExtra(InformationInputActivity.ID, id)
                .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                .putExtra("photoDiscernContent", photoDiscernContent);

        if (!TextUtils.isEmpty(biz_license_registration_code)) {
            intent.putExtra("Biz_license_registration_code", biz_license_registration_code);
        }
        startActivity(intent);
    }

    //设置编辑状态的ocr转态
    public void setOcrEditResult(GetPhotoResq photoResq, TextView view) {
        if (photoResq != null && photoResq.getContent() != null&&!TextUtils.isEmpty(photoResq.getContent().getEnabled()+"")) {
            switch (photoResq.getContent().getEnabled()) {
                case 0://没有
                    break;
                case 2://成功
                    view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case 3://失败
                    view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    break;
            }
        }
    }

private PhotoDiscernResp.ContentBean photoDiscernContent;
    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY8) {//调用接口
            next();
        }else if (method == NetAPI.ACTION_MER_APPLY4) {//编辑 回显
            GetPhotoResq photoResq = (GetPhotoResq) obj;
            switch (photoResq.getContent().fileType) {
                case BUSINESS_LICENCE://营业执照
                    postBusinessLicence = true;
                    setOcrEditResult(photoResq,tvLicense);
                    //  tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    imgLicensePhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case SHOPINNER://营业场所
                    flagIsAccessory=true;
                    //  tvLicense2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    imgLicensePhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case MT://门头照
                    if (TextUtils.isEmpty(photoResq.getContent().comments)) {
                        return;
                    }
                    if (photoResq.getContent().comments.equals("1")) {
                        flagIsAccessory=true;
                        imgDoorHeadPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else if (photoResq.getContent().comments.equals("2")) {
                        flagIsAccessory=true;
                        imgDoorHeadPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    }
                    break;
                case SYT://机器照
                    flagIsAccessory=true;
                    imgPosPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case OTHERS://其他
                    flagIsAccessory=true;
                    imgOther.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
            }

        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //如果有数据,就回显到控件上
            if (null != contentBean && null != contentBean.getMerAttachFileList() && contentBean.getMerAttachFileList().size() > 0) {
//                if(contentBean.getMerOpenInfo()!=null&&contentBean.getMerOpenInfo().getMerLicenceNo()!=null){
//                    biz_license_registration_code= contentBean.getMerOpenInfo().getMerLicenceNo();
//                }
                setImgContent(contentBean.getMerAttachFileList());
            }
        } else {//图片上传
            PhotoDiscernResp photoDiscernResp = (PhotoDiscernResp) obj;
             photoDiscernContent = photoDiscernResp.getContent();
            switch (method) {
                case TYPE_BUSINESS_LICENCE://营业执照
                    postBusinessLicence = true;
                    if (null == photoDiscernContent.getLicenseInfo() || TextUtils.isEmpty(photoDiscernContent.getLicenseInfo().getBiz_license_registration_code())) {
                        Utils.showToast(this, "营业执照比较模糊无法识别,建议重新上传");
                        tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else {
                        tvLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        biz_license_registration_code = photoDiscernContent.getLicenseInfo().getBiz_license_registration_code();
                    }
                    break;
                case TYPE_SHOPINNER://营业场所
                    flagIsAccessory=true;
                    tvLicense2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case TYPE_SHOP_FRONT://门头照1/2
                    flagIsAccessory=true;
                    if ("1".equals(comments)) {
                        tvDoorHeadPhoto1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    } else if ("2".equals(comments)) {
                        tvDoorHeadPhoto2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    }
                    break;
                case TYPE_POS_POSITION://机器照
                    flagIsAccessory=true;
                    tvPosPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case  TYPE_OTHERS:
                    flagIsAccessory=true;
                    tvOther.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
            }
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
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        } else if (statusCode.equals("502")) {
            switch (method) {
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
                case TYPE_OTHERS:
                    tvOther.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    Utils.showToast(this, "机器照上传失败");
                    break;
                case NetAPI.ACTION_MER_APPLY4:
                    Utils.showToast(this, "获取照片失败");
                    break;
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

    private void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        for (MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean : merAttachFileList) {
            if (merAttachFileListBean.getFileType().equals(BUSINESS_LICENCE)
                    || merAttachFileListBean.getFileType().equals(SHOPINNER)
                    || merAttachFileListBean.getFileType().equals(MT)
                    || merAttachFileListBean.getFileType().equals(SYT)
                    || merAttachFileListBean.getFileType().equals(OTHERS)) {
                showProgressDialog("加载中...");
                NetAPI.merApply4(this, this, new GetPhotoReq(merAttachFileListBean.getFileId()));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OpenCameraUtil.newInstace().REQUEST_CODE_PICK_IMAGE && null != data) {//相册获取
            String path = data.getStringExtra("GalleryActivity");


            if(TextUtils.isEmpty(path)){
                Utils.showToast(QCodeCompanyInfoActivity.this, "没有图片返回!");
                return;
            }
            final File file = new File(path);
            if(!file.exists()){
                Utils.showToast(QCodeCompanyInfoActivity.this, "图片不存在!");
                return;
            }
            if(file.isDirectory()){
                Utils.showToast(QCodeCompanyInfoActivity.this, "图片不存在!");
                return;
            }
            if(file.length() == 0){
                Utils.showToast(QCodeCompanyInfoActivity.this, "图片不存在!");
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

    /**
     * 获取上传照片的文件Bean 然后上传
     *
     * @param path
     */
    public void getPhotoFile(final String path) {
        showProgressDialog("上传中...");
        MerAttachFile merAttachFile = photoFileFactory.photoFileCreate(path);
        merAttachFile.setApplyId(applyId);
        photoDiscernReq.getMerAttachFileList().clear();
        photoDiscernReq.getMerAttachFileList().add(merAttachFile);
        NetAPI.photoDiscernReq(this, this, photoDiscernReq, merAttachFile.photoUploadType);
        setImgeContent(path, merAttachFile);
    }

    /**
     * 设置照片
     *
     * @param merAttachFile
     */
    private void setImgeContent(String path, MerAttachFile merAttachFile) {
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
                instance.loadImage(path, imgOther);
                break;
        }

    }


    private void nextRequest() {
        showProgressDialog("提交中...");
        MerApplyInfoReq8 merApplyInfoReq8 = new MerApplyInfoReq8();
        merApplyInfoReq8.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merApplyInfoReq8.setApplyId(applyId);
        merApplyInfoReq8.setId(id);
        merApplyInfoReq8.setAttachments(new ArrayList<MerAttachFile>());
        NetAPI.merApply8(this, this, merApplyInfoReq8);
    }

}
