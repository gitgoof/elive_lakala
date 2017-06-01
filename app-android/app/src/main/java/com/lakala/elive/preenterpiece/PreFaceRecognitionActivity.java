package com.lakala.elive.preenterpiece;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.beans.FaceRecognitionResp;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyInfoReq16;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.oliveapp.liveness.sample.liveness.SampleLivenessActivity;
import com.oliveapp.liveness.sample.util.UrlUtils;

import java.util.ArrayList;

/**
 * 合作商预进件人脸识别
 */
public class PreFaceRecognitionActivity extends BaseActivity {

    private ImageView imgProgress;
    private EditText edtName, edtIdNumber;
    private Button btnNext, btnSkip, btnStart;
    private ImageView imgResult;
    private TextView tvIdVerify, tvPhotoVerify;

    //如果您还没有customerId、customerKey，请与考拉征信对接人联系。注意：测试与生产的customerId、customerKey是不相同的。
    private static final String customerId = "201505140000000001";
    private static final String customerKey = "N4L30XJ4";

    private String idCardCode;
    private String idCardName;
    private String applyId;//进件id

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_pre_face_recognition);
        XAtyTask.getInstance().addAty(this);
    }

    @Override
    protected void bindView() {
        imgProgress = findView(R.id.img_progress);
        edtName = findView(R.id.edt_name);
        edtIdNumber = findView(R.id.edt_id_number);
        btnStart = findView(R.id.btn_start);
        btnNext = findView(R.id.btn_next);
        btnSkip = findView(R.id.btn_skip);
        imgResult = findView(R.id.tv_result);
        tvIdVerify = findView(R.id.tv_id_verify);
        tvPhotoVerify = findView(R.id.tv_photo_verify);

        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("人像识别");
        EliveApplication.setImageProgress(imgProgress, 4);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        Intent intent = getIntent();
        if (intent != null) {
            applyId = intent.getStringExtra("APPLYID");
            String idName = intent.getStringExtra("IDNAME");
            String idNumber = intent.getStringExtra("IDNUMBER");
            if (!TextUtils.isEmpty(idName) && !"null".equals(idName)) {
                edtName.setText(idName);
            }
            if (!TextUtils.isEmpty(idNumber) && !"null".equals(idNumber)) {
                edtIdNumber.setText(idNumber);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_start:
                startRecognition();
                break;
            case R.id.btn_skip://忽略
            case R.id.btn_next://下一步
                startActivity(new Intent(this, PerEnterpriseInformationActivity.class)//跳转到企业信息界面
                        .putExtra("APPLYID", applyId)
                );
                break;
        }
    }


    //启动人脸识别
    private void startRecognition() {
        idCardCode = edtIdNumber.getText().toString();
        idCardName = edtName.getText().toString();
        //需要添加必要的格式校验，不限于以下条件
        if (TextUtils.isEmpty(idCardName)) {
            Utils.showToast(this, "请输入开户姓名");
            return;
        }
        if (TextUtils.isEmpty(idCardCode)) {
            Utils.showToast(this, "请输入身份证号码");
            return;
        } else if (idCardCode.length() <= 17) {
            Utils.showToast(this, "请输入正确的身份证号码");
            return;
        }

        Intent i = new Intent(PreFaceRecognitionActivity.this, SampleLivenessActivity.class);
        i.putExtra("isDebug", false);
        i.putExtra("methodName", UrlUtils.METHODNAME_VALIDATE_FACE);
        i.putExtra("idCardCode", idCardCode);
        i.putExtra("idCardName", idCardName);
        i.putExtra("customerId", customerId);
        i.putExtra("customerKey", customerKey);

        startActivityForResult(i, 110);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 110 && null != data) {//人脸识别结果
            String ResultJson = data.getStringExtra("RESULT");
            Gson gson = new Gson();
            FaceRecognitionResp faceRecognitionResp = gson.fromJson(ResultJson, FaceRecognitionResp.class);
            tvIdVerify.setVisibility(View.VISIBLE);
            tvPhotoVerify.setVisibility(View.VISIBLE);
            if (faceRecognitionResp.getRetCode().equals("000000") && faceRecognitionResp.getRetMsg().equals("SUCCESS")) {
                //识别成功
                if (faceRecognitionResp.getRetData().getIdCardResult().equals("00")
                        && faceRecognitionResp.getRetData().getPhotoResult().equals("00")) {
                    imgResult.setImageResource(R.drawable.lic_suceed);
                    btnSkip.setVisibility(View.GONE);
                    btnStart.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                } else {//识别出信息当跟照片不一致
                    btnSkip.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    imgResult.setImageResource(R.drawable.wrong);
                    btnStart.setText("重新识别");
                }
                if (faceRecognitionResp.getRetData().getIdCardResult().equals("00")) {
                    tvIdVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok, 0);
                } else {
                    tvIdVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                }

                if (faceRecognitionResp.getRetData().getPhotoResult().equals("00")) {
                    tvPhotoVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok, 0);
                } else {
                    tvPhotoVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                }

            } else {//完全识别不出来
                btnSkip.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                imgResult.setImageResource(R.drawable.wrong);
                tvIdVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                tvPhotoVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wrong, 0);
                btnStart.setText("重新识别");
            }

            uploadResp(faceRecognitionResp);
        }
    }

    //上传识别的结果
    private void uploadResp(FaceRecognitionResp faceRecognitionResp) {
        //上传识别结果
        if (null != faceRecognitionResp) {
            MerApplyInfoReq16 merApplyInfoReq16 = new MerApplyInfoReq16();
            merApplyInfoReq16.authToken = mSession.getUserLoginInfo().getAuthToken();
            merApplyInfoReq16.setMerUserAuthInfo(new MerApplyInfoReq16.MerchantUserAuthInfo());
            merApplyInfoReq16.setAttachments(new ArrayList<MerApplyInfoReq16.AttachmentFileVO>());

            merApplyInfoReq16.getMerUserAuthInfo().setApplyId(applyId);
            merApplyInfoReq16.getMerUserAuthInfo().setIdName(idCardName);
            merApplyInfoReq16.getMerUserAuthInfo().setIdCard(idCardCode);
            merApplyInfoReq16.getMerUserAuthInfo().setSysCode(faceRecognitionResp.getRetCode());
            merApplyInfoReq16.getMerUserAuthInfo().setSysMsg(faceRecognitionResp.getRetMsg());
            if (null != faceRecognitionResp.getRetData()) {
                merApplyInfoReq16.getMerUserAuthInfo().setPhotoResult(getNoEmptyString(faceRecognitionResp.getRetData().getPhotoResult()));
                merApplyInfoReq16.getMerUserAuthInfo().setIdCardResult(getNoEmptyString(faceRecognitionResp.getRetData().getIdCardResult()));
                merApplyInfoReq16.getMerUserAuthInfo().setSimilarity(getNoEmptyString(faceRecognitionResp.getRetData().getSimilarity()));
                merApplyInfoReq16.getMerUserAuthInfo().setResultMsg(getNoEmptyString(faceRecognitionResp.getRetData().getMessage()));
                //用户照片
                merApplyInfoReq16.getAttachments().clear();
                if (!TextUtils.isEmpty(faceRecognitionResp.getRetData().getUserPhotoBase64())) {
                    MerApplyInfoReq16.AttachmentFileVO attachmentFileVO = new MerApplyInfoReq16.AttachmentFileVO();
                    attachmentFileVO.setApplyId(applyId);
                    attachmentFileVO.setFileType("01");
                    attachmentFileVO.setSegment("KLZX_AUTH");
                    attachmentFileVO.setFileName("");
                    attachmentFileVO.setFileContent(getNoEmptyString(faceRecognitionResp.getRetData().getUserPhotoBase64()));
                    merApplyInfoReq16.getAttachments().add(attachmentFileVO);
                }

                //身份证照片
                if (!TextUtils.isEmpty(faceRecognitionResp.getRetData().getUserPhotoBase64())) {
                    MerApplyInfoReq16.AttachmentFileVO attachmentFileVO2 = new MerApplyInfoReq16.AttachmentFileVO();
                    attachmentFileVO2.setApplyId(applyId);
                    attachmentFileVO2.setFileType("02");
                    attachmentFileVO2.setSegment("KLZX_AUTH");
                    attachmentFileVO2.setFileName("");
                    attachmentFileVO2.setFileContent(getNoEmptyString(faceRecognitionResp.getRetData().getUserPhotoBase64()));
                    merApplyInfoReq16.getAttachments().add(attachmentFileVO2);
                }
            }
            showProgressDialog("上传识别结果...");
            NetAPI.merApply16(this, this, merApplyInfoReq16);
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.ACTION_MER_APPLY_016) {
//            Utils.showToast(this, "上传成功");
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode);
    }
}
