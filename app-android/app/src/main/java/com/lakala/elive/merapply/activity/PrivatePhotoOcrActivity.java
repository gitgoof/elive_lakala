package com.lakala.elive.merapply.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.lakala.elive.common.net.req.PhotoDiscernReq;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.PhotoDiscernResp;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.ImageTools;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.gallery.util.ImageLoader;
import com.lakala.elive.merapply.merutils.OpenCameraUtil;
import com.lakala.elive.merapply.merutils.PhotoFileFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 对私图片识别上传
 * Created by wenhaogu on 2017/1/19.
 */

public class PrivatePhotoOcrActivity extends BaseActivity {

    private ImageView imgIdPhoto, imgIdPhoto2, imgProtocolPhoto, imgProgress, imgProtocolPhoto1;
    private ImageView imgBankPhoto;
    private ImageView imgProtocolPhoto2;
    private TextView tvId, tvId2, tvProtocol, tvProtocol1;
    private TextView tvBank;
    private TextView tvProtocol2;
    private Button btnNext;

    private final String ID_CARD_FRONT = "ID_CARD_FRONT";//身份证正面
    private final String ID_CARD_BEHIND = "ID_CARD_BEHIND";//身份证反面
    private final String BANK_CARD = "BANK_CARD";//银行卡
    private final String XY = "XY";//商户协议

    private final int TYPE_ID_CARD_FRONT = 1;//身份证正面
    private final int TYPE_ID_CARD_BEHIND = 2;//身份证反面
    private final int TYPE_BANK_CARD = 3;//银行卡
    private final int TYPE_XY = 4;//商户协议

    private ImageLoader instance = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);//加载图片的感觉

    private PhotoDiscernReq photoDiscernReq;
    private String accountKind;

    public static final String PHOTO_DISCERN_RESP = "photoDiscernResp";
    private String applyId;
    private String merchantId;


    private boolean postIdFront = false;//判断是否上传成功
    private boolean postIdBehind = false;
    private boolean postXY = false;
    private boolean postBank = false;

    private PhotoDiscernResp.ContentBean photoContent = new PhotoDiscernResp.ContentBean();
    private String comments;//判断第几个协议
    private String name;
    private MerApplyDetailsResp.ContentBean contentBean;////详情传过来的回显数据
    private boolean isDisplay;//是否显示


    private PhotoFileFactory photoFileFactory = new PhotoFileFactory(this);

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_private_photo_ocr);
    }

    @Override
    protected void bindView() {
        //身份证正面
        imgIdPhoto = findView(R.id.img_id_photo);
        tvId = findView(R.id.tv_id);
        //身份证反面
        imgIdPhoto2 = findView(R.id.img_id_photo2);
        tvId2 = findView(R.id.tv_id2);
        //银行卡照片
        imgBankPhoto = findView(R.id.img_bank_photo);
        tvBank = findView(R.id.tv_bank);

        //商户协议
        imgProtocolPhoto = findView(R.id.img_protocol_photo);
        tvProtocol = findView(R.id.tv_protocol);
        imgProtocolPhoto1 = findView(R.id.img_protocol_photo1);
        tvProtocol1 = findView(R.id.tv_protocol1);
        imgProtocolPhoto2 = findView(R.id.img_protocol_photo2);
        tvProtocol2 = findView(R.id.tv_protocol2);

        //下一步
        btnNext = findView(R.id.btn_next);

        tvTitleName.setText("上传照片");
        iBtnBack.setVisibility(View.VISIBLE);

        imgProgress = findView(R.id.img_progress);
        EnterPieceActivity.setImageProgress(imgProgress, 1);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        imgIdPhoto.setOnClickListener(this);
        imgIdPhoto2.setOnClickListener(this);
        imgBankPhoto.setOnClickListener(this);
        imgProtocolPhoto.setOnClickListener(this);
        imgProtocolPhoto1.setOnClickListener(this);
        imgProtocolPhoto2.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        accountKind = intent.getStringExtra(EnterPieceActivity.ACCOUNT_KIND);
        applyId = intent.getStringExtra(InformationInputActivity.APPLY_ID);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        photoDiscernReq = new PhotoDiscernReq();
        photoDiscernReq.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        photoDiscernReq.setMerApplyInfo(new MerApplyInfo());
        photoDiscernReq.setMerAttachFileList(new ArrayList<MerAttachFile>());
        photoDiscernReq.getMerApplyInfo().setApplyChannel("01");
        photoDiscernReq.getMerApplyInfo().setApplyType("1");
        photoDiscernReq.getMerApplyInfo().setAccountKind(accountKind);

        if (!TextUtils.isEmpty(applyId)) {
            photoDiscernReq.getMerApplyInfo().setApplyId(applyId);

            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }
//        contentBean = (MerApplyDetailsResp.ContentBean) intent.getSerializableExtra("ContentBean");

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!isDisplay) {
//
//        }
//        isDisplay = true;
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                showAlertDialog();
                break;
            case R.id.img_id_photo://身份证正面
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(ID_CARD_FRONT, "APPLY_ACCOUNT", "", TYPE_ID_CARD_FRONT);
                break;
            case R.id.img_id_photo2://身份证反面
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(ID_CARD_BEHIND, "APPLY_ACCOUNT", "", TYPE_ID_CARD_BEHIND);
                break;
            case R.id.img_bank_photo://银行卡照片
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(BANK_CARD, "APPLY_ACCOUNT", "", TYPE_BANK_CARD);
                break;
            case R.id.img_protocol_photo://商户协议
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(XY, "APPLY_ACCOUNT", comments = "1", TYPE_XY);
                break;
            case R.id.img_protocol_photo1://商户协议1
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(XY, "APPLY_ACCOUNT", comments = "2", TYPE_XY);
                break;
            case R.id.img_protocol_photo2://商户协议2
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(XY, "APPLY_ACCOUNT", comments = "3", TYPE_XY);
                break;
            case R.id.btn_next:
                next();
                break;
        }
    }

    private void next() {
        if (postIdFront && postIdBehind && postXY && postBank) {
            startActivity(new Intent(this, InformationInputActivity.class)
                    .putExtra(PHOTO_DISCERN_RESP, photoContent)
                    .putExtra(EnterPieceActivity.ACCOUNT_KIND, accountKind)
                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                    // .putExtra("ContentBean", contentBean)
                    .putExtra("applyId", applyId)
            );
        }
    }
    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY4) {//成功  回显照片
            GetPhotoResq photoResq = (GetPhotoResq) obj;
            switch (photoResq.getContent().fileType) {
                case ID_CARD_FRONT:
                    postIdFront = true;
                    imgIdPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                    tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case ID_CARD_BEHIND:
                    postIdBehind = true;
                    imgIdPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                    tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case BANK_CARD:
                    postBank = true;
                    imgBankPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                    tvBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    break;
                case XY:
                    if ("2".equals(photoResq.getContent().comments)) {
                        imgProtocolPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                        tvProtocol1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    } else if ("3".equals(photoResq.getContent().comments)) {
                        imgProtocolPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                        tvProtocol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    }else if ("1".equals(photoResq.getContent().comments)) {
                        postXY = true;
                        imgProtocolPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
//                        tvProtocol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    }
                    break;
            }
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //如果有数据,就回显到控件上
            if (null != contentBean && null != contentBean.getMerAttachFileList() && contentBean.getMerAttachFileList().size() > 0) {
                setImgContent(contentBean.getMerAttachFileList());
                accountKind = contentBean.getMerOpenInfo().getAccountKind();
                photoDiscernReq.getMerApplyInfo().setAccountKind(accountKind);
            }
        } else {//上传照片成功
            PhotoDiscernResp photoDiscernResp = (PhotoDiscernResp) obj;
            PhotoDiscernResp.ContentBean photoDiscernContent = photoDiscernResp.getContent();
            applyId = photoDiscernContent.getApplyId();
            if (!TextUtils.isEmpty(applyId)) {
                photoDiscernReq.getMerApplyInfo().setApplyId(applyId);
            }

            switch (method) {
                case TYPE_ID_CARD_FRONT:
                    if (null == photoDiscernContent.getIdCardInfo() || TextUtils.isEmpty(photoDiscernContent.getIdCardInfo().getId_number())) {
                        Utils.showToast(this, "身份证比较模糊无法识别,建议重新上传");
                        tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else {
                        tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        photoContent.setIdCardInfo(photoDiscernContent.getIdCardInfo());
                    }
                    postIdFront = true;
                    break;
                case TYPE_ID_CARD_BEHIND:
                    if ((null != photoDiscernContent.getIdCardInfoBack() && TextUtils.isEmpty(photoDiscernContent.getIdCardInfoBack().getValidity())) || null == photoDiscernContent.getIdCardInfoBack()) {
                        Utils.showToast(this, "身份证比较模糊无法识别,建议重新上传");
                        tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else {
                        tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        photoContent.setIdCardInfoBack(photoDiscernContent.getIdCardInfoBack());
                    }
                    postIdBehind = true;
                    break;
                case TYPE_BANK_CARD:
                    if (null == photoDiscernContent.getBankCardInfo() || (null != photoDiscernContent.getBankCardInfo() && TextUtils.isEmpty(photoDiscernContent.getBankCardInfo().getCard_number()))) {
                        Utils.showToast(this, "银行卡比较模糊无法识别,建议重新上传");
                        tvBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else {
                        tvBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                        photoContent.setBankCardInfo(photoDiscernContent.getBankCardInfo());
                    }
                    postBank = true;
                    break;
                case TYPE_XY:
                    if (comments.equals("1")) {
                        postXY = true;
                        tvProtocol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    } else if (comments.equals("2")) {
                        tvProtocol1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    } else if (comments.equals("3")) {
                        tvProtocol2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                    }
                    break;
            }
        }

        if (postIdFront && postIdBehind && postXY&&postBank) {
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
        } else {
            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
            btnNext.setEnabled(false);
        }

        photoContent.setApplyId(applyId);
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        } else if (statusCode.equals("502")) {
            switch (method) {
                case TYPE_ID_CARD_FRONT:
                    postIdFront = false;
                    tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    imgIdPhoto.setImageResource(R.drawable.selector_photo);
                    Utils.showToast(this, "身份证正面上传失败");
                    break;
                case TYPE_ID_CARD_BEHIND:
                    postIdBehind = false;
                    tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    imgIdPhoto2.setImageResource(R.drawable.selector_photo);
                    Utils.showToast(this, "身份证反面上传失败");
                    break;
                case TYPE_BANK_CARD:
                    postBank = false;
                    imgBankPhoto.setImageResource(R.drawable.selector_photo);
                    Utils.showToast(this, "银行卡上传失败");
                    break;
                case TYPE_XY:
                    if (comments.equals("1")) {
                        postXY = false;
                        tvProtocol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else if (comments.equals("2")) {
                        tvProtocol1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    } else if (comments.equals("3")) {
                        tvProtocol2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    }
                    Utils.showToast(this, "协议上传失败");
                    break;
                case NetAPI.ACTION_MER_APPLY4:
                    Utils.showToast(this, "获取照片失败");
                    break;
            }
        } else {
            Utils.showToast(this, statusCode);
        }

        if (postIdFront && postIdBehind && postXY&&postBank) {
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
        } else {
            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
            btnNext.setEnabled(false);
        }

    }


    private void setImgContent(List<MerApplyDetailsResp.ContentBean.MerAttachFileListBean> merAttachFileList) {
        for (MerApplyDetailsResp.ContentBean.MerAttachFileListBean merAttachFileListBean : merAttachFileList) {
            if (ID_CARD_FRONT.equals(merAttachFileListBean.getFileType()) ||
                    ID_CARD_BEHIND.equals(merAttachFileListBean.getFileType())
                    || BANK_CARD.equals(merAttachFileListBean.getFileType())
                    || XY.equals(merAttachFileListBean.getFileType())) {
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
                Utils.showToast(PrivatePhotoOcrActivity.this, "没有图片返回!");
                return;
            }
            final File file = new File(path);
            if(!file.exists()){
                Utils.showToast(PrivatePhotoOcrActivity.this, "图片不存在!");
                return;
            }
            if(file.isDirectory()){
                Utils.showToast(PrivatePhotoOcrActivity.this, "图片不存在!");
                return;
            }
            if(file.length() == 0){
                Utils.showToast(PrivatePhotoOcrActivity.this, "图片不存在!");
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
            case ID_CARD_FRONT://身份证正面
                instance.loadImage(path, imgIdPhoto);
                break;
            case ID_CARD_BEHIND://身份证反面
                instance.loadImage(path, imgIdPhoto2);
                break;
            case BANK_CARD://银行卡
                instance.loadImage(path, imgBankPhoto);
                break;
            case XY://商户协议
                String comments = merAttachFile.getComments();
                if(TextUtils.isEmpty(comments))break;
                if(comments.equals("1")){
                    instance.loadImage(path, imgProtocolPhoto );
                } else if (comments.equals("2")){
                    instance.loadImage(path,imgProtocolPhoto1);
                } else if (comments.equals("3")){
                    instance.loadImage(path,imgProtocolPhoto2);
                }
//                instance.loadImage(path, merAttachFile.getComments().equals("1")
//                        ? imgProtocolPhoto : imgProtocolPhoto1);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showAlertDialog();
        }
        return false;
    }

    private void showAlertDialog() {
        DialogUtil.createAlertDialog(
                this,
                "提示",
                name = accountKind.equals("57") ? "确认退出对公进件吗?" : "确认退出对私进件吗?",
                "取消",
                "确定",
                mListener
        ).show();
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    setResult(RESULT_OK);
                    dialog.dismiss();
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EliveApplication.getHttpQueue().cancelAll(NetAPI.ACTION_PHOTO_DISCERN);
    }
}
