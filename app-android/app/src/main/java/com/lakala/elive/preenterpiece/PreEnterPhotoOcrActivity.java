package com.lakala.elive.preenterpiece;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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

import java.io.File;
import java.util.List;

/**
 * 合作商预进件图片识别上传
 */

public class PreEnterPhotoOcrActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();

    private ImageView imgIdPhoto, imgIdPhoto2,
            imgProgress, imgAccountProve, imgProtocolPhoto1;
    private TextView tvId, tvId2, tvProtocol2, tvAccountProve;
    private Button btnNext;

    private final String ID_CARD_FRONT = "ID_CARD_FRONT";//身份证正面
    private final String ID_CARD_BEHIND = "ID_CARD_BEHIND";//身份证反面
    private final String XY = "XY";//商户协议
    private final String KH = "KH";//开户许可

    private final int TYPE_ID_CARD_FRONT = 1;//身份证正面
    private final int TYPE_ID_CARD_BEHIND = 2;//身份证反面
    private final int TYPE_ACCOUNT_LICENSE = 3;//开户许可
    private final int TYPE_XY = 4;//商户协议

    private ImageLoader instance = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);//加载图片的感觉

    private boolean postIdFront = false;//判断是否上传成功
    private boolean postIdBehind = false;
    private boolean postXY = false;
    private boolean postKH = false;

    private String applyId;
    private PrePhotoFileFactory photoFileFactory = new PrePhotoFileFactory(this);
    private PreEnPieceOcrPhoneRequ preEnPieceOcrPhoneRequ;

    private PreEnPieceDetailRequ preEnPieceDetailRequ;

    private String commentsStr;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_cooperpreenter_photo_ocr);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        //身份证正面
        imgIdPhoto = findView(R.id.img_id_photo);
        //身份证反面
        imgIdPhoto2 = findView(R.id.img_id_photo2);
        //商户协议
        imgProtocolPhoto1 = findView(R.id.img_protocol_photo1);
        //开户许可
        imgAccountProve = findView(R.id.img_account_prove);
        tvAccountProve = findView(R.id.tv_account_prove);

        tvId = findView(R.id.tv_id);
        tvId2 = findView(R.id.tv_id2);

        tvProtocol2 = findView(R.id.tv_protocol2);
        //下一步
        btnNext = findView(R.id.btn_next);
        tvTitleName.setText("上传照片");
        iBtnBack.setVisibility(View.VISIBLE);
        imgProgress = findView(R.id.img_progress);
        EliveApplication.setImageProgress(imgProgress, 2);

        //设置下一步按钮可以点击
        btnNext.setBackgroundResource(R.drawable.btn_search_bg);
        btnNext.setEnabled(true);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        imgIdPhoto.setOnClickListener(this);
        imgIdPhoto2.setOnClickListener(this);
        imgProtocolPhoto1.setOnClickListener(this);
        imgAccountProve.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra("APPLYID");

        if (!TextUtils.isEmpty(applyId)) {//如果是编辑进来的，则调用详情接口取数据
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
        merApplyInfo.setApplyChannel("02");
        merApplyInfo.setApplyType("1");
        merApplyInfo.setProcess("2");
        preEnPieceOcrPhoneRequ.setMerApplyInfo(merApplyInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
//                showAlertDialog();
                break;
            case R.id.img_id_photo://身份证正面
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(ID_CARD_FRONT, "APPLY_ACCOUNT", "", TYPE_ID_CARD_FRONT);
                break;
            case R.id.img_id_photo2://身份证反面
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(ID_CARD_BEHIND, "APPLY_ACCOUNT", "", TYPE_ID_CARD_BEHIND);
                break;
            case R.id.img_protocol_photo1://商户协议1
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(XY, "APPLY_ACCOUNT", commentsStr = "1", TYPE_XY);
                break;
            case R.id.img_account_prove://开户许可
                OpenCameraUtil.newInstace().showSelect(this);
                photoFileFactory.setPhotoType(KH, "APPLY_ACCOUNT", "", TYPE_ACCOUNT_LICENSE);
                break;
            case R.id.btn_next:
                next();
                break;
        }
    }

    private void next() {
//        if (postIdFront && postIdBehind && postKH) {
        startActivity(new Intent(this, PreEnterSettleAccountsActivity.class)
                .putExtra("IDNUMBER", idNumber)
                .putExtra("YAILIDITYDATAData", vailidityData)
                .putExtra("IDNAME", IdName)
                .putExtra("APPLYID", applyId)
        );
//        }
    }

    private String idNumber;//身份证号码
    private String vailidityData;//身份证截止日期
    private String IdName;//身份证姓名

    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ELIVE_PARTNER_APPLY_004://预进件的照片上传成功，设置显示图片
                PreEnPieceOcrPhoneResponse preEnPieceOcrPhoneResponse = (com.lakala.elive.preenterpiece.response.PreEnPieceOcrPhoneResponse) obj;
                if (preEnPieceOcrPhoneResponse != null && preEnPieceOcrPhoneResponse.getContent() != null) {
                    PreEnPieceOcrPhoneResponse.RespOcr preEnPieceOcrPhoneContent = preEnPieceOcrPhoneResponse.getContent();
                    switch (merAttachFile.photoUploadType) {
                        case TYPE_ID_CARD_FRONT://身份证正面
                            if (null == preEnPieceOcrPhoneContent.getIdCardInfo() || TextUtils.isEmpty(preEnPieceOcrPhoneContent.getIdCardInfo().getId_number())) {
                                Utils.showToast(this, "身份证比较模糊无法识别,建议重新上传");
                                tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                                idNumber = "";
                            } else {
                                tvId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
//                        photoContent.setIdCardInfo(preEnPieceOcrPhoneContent.getIdCardInfo());
                                if (preEnPieceOcrPhoneContent.getIdCardInfo() != null) {
                                    idNumber = preEnPieceOcrPhoneContent.getIdCardInfo().getId_number();
                                    IdName = preEnPieceOcrPhoneContent.getIdCardInfo().getName();
                                }
                            }
                            postIdFront = true;
                            break;
                        case TYPE_ID_CARD_BEHIND://身份证方面
                            if ((null != preEnPieceOcrPhoneContent.getIdCardInfoBack() && TextUtils.isEmpty(preEnPieceOcrPhoneContent.getIdCardInfoBack().getValidity())) || null == preEnPieceOcrPhoneContent.getIdCardInfoBack()) {
                                Utils.showToast(this, "身份证比较模糊无法识别,建议重新上传");
                                tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                                vailidityData = "";
                            } else {
                                tvId2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
//                        photoContent.setIdCardInfoBack(preEnPieceOcrPhoneContent.getIdCardInfoBack());
                                if (preEnPieceOcrPhoneContent.getIdCardInfoBack() != null) {
                                    vailidityData = preEnPieceOcrPhoneContent.getIdCardInfoBack().getValidity();
                                }
                            }
                            postIdBehind = true;
                            break;
                        case TYPE_XY://协议(可选)
                            postXY = true;
                            tvProtocol2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                            break;
                        case TYPE_ACCOUNT_LICENSE://许可
                            postKH = true;
                            tvAccountProve.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                            break;
                    }
                } else {
                    Log.e(TAG, "preEnPieceOcrPhoneResponse为空或者getContent为空");
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_002://编辑转态获取珊商户信息
                PreEnPieceDetailResponse preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null && preEnPieceDetailResponse.getContent() != null && preEnPieceDetailResponse.getContent().getMerAttachFileList() != null) {//不为空，设置数据
                    setImgContent(preEnPieceDetailResponse.getContent().getMerAttachFileList());
                }
                break;
            case NetAPI.ACTION_MER_APPLY4://编辑状态下设置显示上次图片
                GetPhotoResq photoResq = (GetPhotoResq) obj;
                if (photoResq != null && photoResq.getContent() != null && photoResq.getContent().fileType != null) {
                    switch (photoResq.getContent().fileType) {
                        case "ID_CARD_FRONT":
                            if (photoResq != null && photoResq.getContent() != null) {
                                imgIdPhoto.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                                postIdFront = true;
                            }
                            break;
                        case "ID_CARD_BEHIND":
                            if (photoResq != null && photoResq.getContent() != null) {
                                imgIdPhoto2.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                                postIdBehind = true;
                            }
                            break;
                        case "XY":
                            if (photoResq != null && photoResq.getContent() != null) {
                                imgProtocolPhoto1.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                            }
                            break;
                        case "KH":
                            if (photoResq != null && photoResq.getContent() != null) {
                                imgAccountProve.setImageBitmap(ImageTools.base64ToBitmap(photoResq.getContent().getFileContent()));
                                postKH = true;
                            }
                            break;
                    }
                } else {
                    Log.e(TAG, "ACTION_MER_APPLY4接口异常");
                }
                break;
        }
//        //设置按钮的颜色及是否可以点击
//        if (postIdFront && postIdBehind&& postKH) {
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
        if (statusCode.equals("502") && method == NetAPI.ELIVE_PARTNER_APPLY_004) {
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
                case TYPE_XY:
                    Utils.showToast(this, "协议上传失败");
                    break;
                case TYPE_ACCOUNT_LICENSE:
                    postKH = false;
                    tvAccountProve.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wrong, 0, 0, 0);
                    Utils.showToast(this, "开户许可上传失败");
                    break;
                case NetAPI.ACTION_MER_APPLY4:
                    Utils.showToast(this, "获取照片失败");
                    break;
            }
        } else {
            Utils.showToast(this, statusCode);
        }
        if (postIdFront && postIdBehind && postXY && postKH) {
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
                Utils.showToast(PreEnterPhotoOcrActivity.this, "没有图片返回!");
                return;
            }
            final File file = new File(path);
            if(!file.exists()){
                Utils.showToast(PreEnterPhotoOcrActivity.this, "图片不存在!");
                return;
            }
            if(file.isDirectory()){
                Utils.showToast(PreEnterPhotoOcrActivity.this, "图片不存在!");
                return;
            }
            if(file.length() == 0){
                Utils.showToast(PreEnterPhotoOcrActivity.this, "图片不存在!");
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
        switch (merAttachFile.getFileType()) {
            case ID_CARD_FRONT://身份证正面
                instance.loadImage(path, imgIdPhoto);
                break;
            case ID_CARD_BEHIND://身份证反面
                instance.loadImage(path, imgIdPhoto2);
                break;
            case XY://商户协议
                instance.loadImage(path, imgProtocolPhoto1);
                break;
            case KH://开户许可
                instance.loadImage(path, imgAccountProve);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

//    private void showAlertDialog() {
//        DialogUtil.createAlertDialog(
//                this,
//                "提示",
//                "确认退出预进件吗?",
//                "取消",
//                "确定",
//                mListener
//        ).show();
//    }

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

    private void setImgContent(List<PreEnPieceDetailResponse.ContentBean.MerAttachFile> merAttachFileList) {
        // showProgressDialog("加载中...");
        if (merAttachFileList != null) {
            for (PreEnPieceDetailResponse.ContentBean.MerAttachFile merAttachFileListBean : merAttachFileList) {
                if ("ID_CARD_FRONT".equals(merAttachFileListBean.getFileType()) ||
                        "ID_CARD_BEHIND".equals(merAttachFileListBean.getFileType())
                        || "XY".equals(merAttachFileListBean.getFileType())
                        || "KH".equals(merAttachFileListBean.getFileType())) {
                    showProgressDialog("加载中...");
                    NetAPI.merApply4(this, this, new GetPhotoReq(merAttachFileListBean.getFileId()));
                }
            }
        }
    }
}
