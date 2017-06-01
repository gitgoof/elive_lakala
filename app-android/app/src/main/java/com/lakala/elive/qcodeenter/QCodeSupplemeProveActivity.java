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
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;
import com.lakala.elive.merapply.gallery.util.ImageLoader;
import com.lakala.elive.merapply.merutils.OpenCameraUtil;
import com.lakala.elive.merapply.merutils.PhotoFileFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Q码进件的对私补充证明收入证明
 */
public class QCodeSupplemeProveActivity extends BaseActivity {
    
    private ImageView
            imgReplenish1, imgReplenish2,
            imgLicensePhoto, imgDoorHeadPhoto1, imgDoorHeadPhoto2, imgPosPhoto, imgProgress,imgOther;
    private TextView
            tvReplenish1, tvReplenish2,
            tvLicensePhoto, tvDoorHeadPhoto1, tvDoorHeadPhoto2, tvPosPhoto,tvOther;
    private Button btnNext;

        private final int TYPE_OTHERS = 5;//其他
//    private final int TYPE_SUP_PROOF = 1;//补充证明
    private final int TYPE_SHOPINNER = 2;//营业场所照片
    private final int TYPE_SHOP_FRONT = 3;//门头照
    private final int TYPE_POS_POSITION = 4;//机器照


    private final String OTHERS = "OTHERS";
//    private final String SUP_PROOF = "SUP_PROOF";//附件 补充
    private final String SHOPINNER = "SHOPINNER";//营业场所照片
    private final String MT = "MT";//门头照
    private final String SYT = "SYT";//机器照


    private ImageLoader instance = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);

    private PhotoDiscernReq photoDiscernReq;

    private PhotoFileFactory photoFileFactory = new PhotoFileFactory(this);

    private String applyId;//进件id
    private long id;

//    private boolean postProof = false;
    private String merchantId;
    private MerApplyDetailsResp.ContentBean contentBean;//详情传过来的回显数据
    private String comments;//图片排序

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_payee_prove_info);
    }

    @Override
    protected void bindView() {
        //补充1
        imgReplenish1 = findView(R.id.img_replenish1);
        tvReplenish1 = findView(R.id.tv_replenish1);
        //补充2
        imgReplenish2 = findView(R.id.img_replenish2);
        tvReplenish2 = findView(R.id.tv_replenish2);

        //营业场所
        imgLicensePhoto = findView(R.id.img_license_photo);
        tvLicensePhoto = findView(R.id.tv_license_photo);
        //门头照2
        imgDoorHeadPhoto1 = findView(R.id.img_door_head_photo1);
        tvDoorHeadPhoto1 = findView(R.id.tv_door_head_photo1);
        //门头照2
        imgDoorHeadPhoto2 = findView(R.id.img_door_head_photo2);
        tvDoorHeadPhoto2 = findView(R.id.tv_door_head_photo2);
        //机器照
        imgPosPhoto = findView(R.id.img_pos_photo);
        tvPosPhoto = findView(R.id.tv_pos_photo);

        btnNext = findView(R.id.btn_next);

        iBtnBack.setVisibility(View.VISIBLE);

        tvTitleName.setText("补充证明");

        imgProgress = findView(R.id.img_progress);
        EliveApplication.setImageQCodeProgress(imgProgress, 4, EliveApplication.PRIVICEQCODE);

        btnNext.setBackgroundResource(R.drawable.btn_search_bg);
        btnNext.setEnabled(true);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        imgReplenish1.setOnClickListener(this);
        imgReplenish2.setOnClickListener(this);
        imgLicensePhoto.setOnClickListener(this);
        imgDoorHeadPhoto1.setOnClickListener(this);
        imgDoorHeadPhoto2.setOnClickListener(this);
        imgPosPhoto.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra(QCodeSettleInfoActivity.APPLY_ID);
        id = intent.getLongExtra(QCodeSettleInfoActivity.ID, 0);
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
            case R.id.img_replenish1://补充1
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(OTHERS, "APPLY_SUP_PROOF", comments = "1", TYPE_OTHERS);
                break;
            case R.id.img_replenish2://补充2
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(OTHERS, "APPLY_SUP_PROOF", comments = "2", TYPE_OTHERS);
                break;
            case R.id.img_license_photo://营业场所
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
            case R.id.img_pos_photo://收银台照
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(SYT, "APPLY_SUP_PROOF", "", TYPE_POS_POSITION);
                break;
            case R.id.btn_next:
//                if (postProof) {
                if(!flagIsUp){
                    next();
                }else{
                    startActivity(new Intent(this, QCodeBasicInfoActivity.class)
                                    .putExtra(QCodeSettleInfoActivity.APPLY_ID, applyId)
                                    .putExtra(QCodeSettleInfoActivity.ID, id)
                                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                                    .putExtra("QCODETYPE", EliveApplication.PRIVICEQCODE)
//                    .putExtra("ContentBean", contentBean)
                    );
                }
//                }
                break;
        }
    }

    private void next() {
        showProgressDialog("提交中...");
        MerApplyInfoReq8 merApplyInfoReq8 = new MerApplyInfoReq8();
        merApplyInfoReq8.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merApplyInfoReq8.setApplyId(applyId);
        merApplyInfoReq8.setId(id);
        merApplyInfoReq8.setAttachments(new ArrayList<MerAttachFile>());
        NetAPI.merApply8(this, this, merApplyInfoReq8);
    }
    //是否上传了图片
    private boolean flagIsUp=false;
    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY8) {
            startActivity(new Intent(this, QCodeBasicInfoActivity.class)
                            .putExtra(QCodeSettleInfoActivity.APPLY_ID, applyId)
                            .putExtra(QCodeSettleInfoActivity.ID, id)
                            .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                            .putExtra("QCODETYPE", EliveApplication.PRIVICEQCODE)
//                    .putExtra("ContentBean", contentBean)
            );
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //回显
            if (null != contentBean && null != contentBean.getMerAttachFileList() && contentBean.getMerAttachFileList().size() > 0) {
                flagIsUp=true;
                setImgContent(contentBean.getMerAttachFileList());
            }
        } else if (method == NetAPI.ACTION_MER_APPLY4) {
            GetPhotoResq photoResq = (GetPhotoResq) obj;
            switch (photoResq.getContent().fileType) {
                case OTHERS://补充
                    if (TextUtils.isEmpty(photoResq.getContent().comments)) {
                        return;
                    }
                    if (photoResq.getContent().comments.equals("1")) {
                        flagIsUp=true;
//                        postProof = true;
                        imgReplenish1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else if (photoResq.getContent().comments.equals("2")) {
                        flagIsUp=true;
                        imgReplenish2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    }
                    break;
                case SHOPINNER://营业场所
                    flagIsUp=true;
                    imgLicensePhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
                case MT://门头照
                    if (TextUtils.isEmpty(photoResq.getContent().comments)) {
                        return;
                    }
                    if (photoResq.getContent().comments.equals("1")) {
                        flagIsUp=true;
                        imgDoorHeadPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    } else if (photoResq.getContent().comments.equals("2")) {
                        flagIsUp=true;
                        imgDoorHeadPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    }
                    break;
                case SYT://机器照
                    flagIsUp=true;
                    imgPosPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                    break;
            }
        } else {
            switch (method) {
                case TYPE_OTHERS://补充1/2
                    if ("1".equals(comments)) {
//                        postProof = true;
                        tvReplenish1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    } else if ("2".equals(comments)) {
                        tvReplenish2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    }
                    break;
                case TYPE_SHOPINNER://营业场所
                    tvLicensePhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
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
            }
        }
//        if (postProof) {
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
//        } else {
//            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
//            btnNext.setEnabled(false);
//        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
            return;
        }
        if (method == NetAPI.ACTION_MER_APPLY8) {
            Utils.showToast(this, statusCode);
            return;
        }
        if (method == NetAPI.ACTION_MER_APPLY4) {
            Utils.showToast(this, "获取图片失败");
            return;
        }
        Utils.showToast(this, "上传失败");
        switch (method) {
            case TYPE_OTHERS://补充1/2
                if ("1".equals(comments)) {
//                    postProof = false;
                    tvReplenish1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                } else if ("2".equals(comments)) {
                    tvReplenish2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                }
                break;
            case TYPE_SHOPINNER://营业场所
                tvLicensePhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                break;
            case TYPE_SHOP_FRONT://门头照1/2
                if ("1".equals(comments)) {
                    tvDoorHeadPhoto1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                } else if ("2".equals(comments)) {
                    tvDoorHeadPhoto2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                }
                break;
            case TYPE_POS_POSITION://机器照
                tvPosPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                break;
        }
//        if (postProof) {
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
//        } else {
//            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
//            btnNext.setEnabled(false);
//        }
    }

    public void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        for (int y = 0; y < merAttachFileList.size(); y++) {
            MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean = merAttachFileList.get(y);
            if (
                    merAttachFileListBean.getFileType().equals(OTHERS)
                    ||
                            merAttachFileListBean.getFileType().equals(SHOPINNER)
                    || merAttachFileListBean.getFileType().equals(MT)
                    || merAttachFileListBean.getFileType().equals(SYT)) {
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
                Utils.showToast(QCodeSupplemeProveActivity.this, "没有图片返回!");
                return;
            }
            final File file = new File(path);
            if(!file.exists()){
                Utils.showToast(QCodeSupplemeProveActivity.this, "图片不存在!");
                return;
            }
            if(file.isDirectory()){
                Utils.showToast(QCodeSupplemeProveActivity.this, "图片不存在!");
                return;
            }
            if(file.length() == 0){
                Utils.showToast(QCodeSupplemeProveActivity.this, "图片不存在!");
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
            case OTHERS://补充1/2
                instance.loadImage(path, merAttachFile.getComments().equals("1") ? imgReplenish1 : imgReplenish2);
                break;
            case SHOPINNER://营业场所
                instance.loadImage(path, imgLicensePhoto);
                break;
            case MT://门头照1/2
                instance.loadImage(path, merAttachFile.getComments().equals("1") ? imgDoorHeadPhoto1 : imgDoorHeadPhoto2);
                break;
            case SYT://机器照
                instance.loadImage(path, imgPosPhoto);
                break;
        }
    }
}
