package com.lakala.elive.qcodeenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq2;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerApplyInfoRes2;
import com.lakala.elive.common.net.resp.PhotoDiscernResp;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;
import java.util.Map;

/**
 * Q码进件营业执照信息
 */

public class QCodeLicenseEntryPhotoActivity extends BaseActivity {

    private Button btnNext, btnSubmit;

    private RelativeLayout rlLicenseInfoTab;
    private TextView tvValidDate, tvLicenseName, tvLegalName, tvLicenseAddress, tvLicenseInfo;
    private ImageView imgProgress;

    private EditText edtLicense;
    private String license;//营业执照号
    private String applyId;//进件id
    private long id;
    private MerApplyInfoReq2 merApplyInfoReq2; //营业执照信息
    private String path;//图片路径
    private MerApplyInfoRes2 merApplyInfoRes2;//返回的结果
    //private boolean isSubmitPhoto = false;//是否是提交图片请求
    private Uri imageUri;
    private String merchantId;
    private InputMethodManager imm;//键盘服务
    private MerApplyDetailsResp.ContentBean contentBean;


    private PhotoDiscernResp.ContentBean photoDiscernContent;


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_license_photo_entry);
    }

    protected void bindView() {
        //营业执照号
        edtLicense = findView(R.id.edt_license);
        //提交
        btnSubmit = findView(R.id.btn_submit);

        //营业执照信息
        tvLicenseInfo = findView(R.id.tv_license_info);
        //营业执照信息表
        rlLicenseInfoTab = findView(R.id.rl_license_info_tab);
        //营业执照名称
        tvLicenseName = findView(R.id.tv_license_name);
        //法人姓名
        tvLegalName = findView(R.id.tv_legal_name);
        //营业执照地址
        tvLicenseAddress = findView(R.id.tv_license_address);
        //营业执照到期时间
        tvValidDate = findView(R.id.tv_valid_date);
        //下一步
        btnNext = findView(R.id.btn_next);
        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("营业执照");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imgProgress = findView(R.id.img_progress);
        EliveApplication.setImageQCodeProgress(imgProgress, 5, EliveApplication.PUBLICQCODE);
        setNextEnable(false);
    }

    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        edtLicense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(edtLicense.getText().toString().trim()) ||
//                        edtLicense.getText().toString().length() <= 5) {
//                    btnSubmit.setBackgroundResource(R.drawable.btn_enabled_bg);
//                    btnSubmit.setEnabled(false);
//                } else {
//                    btnSubmit.setBackgroundResource(R.drawable.btn_search_bg);
//                    btnSubmit.setEnabled(true);
//                }
                final String nowLicense = edtLicense.getText().toString();
                if (TextUtils.isEmpty(nowLicense.trim()) ||
                        nowLicense.length() <= 5) {
                    btnSubmit.setBackgroundResource(R.drawable.btn_enabled_bg);
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setBackgroundResource(R.drawable.btn_search_bg);
                    btnSubmit.setEnabled(true);
                }
            }
        });
    }

    protected void bindData() {
        Intent intent = getIntent();
        applyId = intent.getStringExtra(QCodeSettleInfoActivity.APPLY_ID);
        id = intent.getLongExtra(QCodeSettleInfoActivity.ID, 0);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        String biz_license_registration_code = intent.getStringExtra("Biz_license_registration_code");
        if (!TextUtils.isEmpty(biz_license_registration_code)) {
            edtLicense.setText(biz_license_registration_code);//设置营业执照号
            edtLicense.setSelection(biz_license_registration_code.length());
            isGetResult = true;
            getLicenseInfo();
            return;
        }
        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_submit://获取执照信息
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
                }
                isGetResult = true;
                getLicenseInfo();
                break;
            case R.id.btn_next:
//                //判断身份证银行卡三要素
//                if (merApplyInfoRes2!=null&&merApplyInfoRes2.getContent()!=null&&merApplyInfoRes2.getContent().isSuccess()&&!TextUtils.isEmpty(edtLicense.getText().toString().trim())&&merApplyInfoRes2!=null&&merApplyInfoRes2.getContent()!=null&&!merApplyInfoRes2.getContent().isVerifyResult()&&merApplyInfoRes2.getContent().getValidInfoMap() != null &&merApplyInfoRes2.getContent().getValidInfoMap().get("licenseResult")!=null) {
//                    String tip= merApplyInfoRes2.getContent().getValidInfoMap().get("licenseResult");
//                    showAlertDialog(tip);
//                }else{
//                    next();
//                }
                if(merApplyInfoRes2 == null){
                    isGetResult = false;
                    getLicenseInfo();
                    break;
                } else {
                    isGetResult = true;
                    toCheckResult();
                }

                break;
        }
    }

    private boolean isGetResult = true;
    /**
     * 获取营业执照信息
     */
    private void getLicenseInfo() {
        license = edtLicense.getText().toString().trim();
        if (TextUtils.isEmpty(license)) {
            Utils.showToast(this, "请输入营业执照号");
        } else if (license.length() <= 5 && license.length() > 32) {
            Utils.showToast(this, "您输入的营业执照号格式有误");
        } else {
            merApplyInfoReq2 = new MerApplyInfoReq2(mSession.getUserLoginInfo().getAuthToken(), license, id, applyId);
            showProgressDialog("获取中...");
            NetAPI.merApply2(this, this, merApplyInfoReq2);
        }
    }
    private void setNextEnable(boolean nextEnable){
        if(nextEnable){
            btnNext.setBackgroundResource(R.drawable.btn_search_bg);
            btnNext.setEnabled(true);
        } else {
            btnNext.setBackgroundResource(R.drawable.btn_enabled_bg);
            btnNext.setEnabled(false);
        }
    }

    private void next() {
        if (rlLicenseInfoTab.getVisibility() == View.VISIBLE) {
            startActivity(new Intent(this, QCodeBasicInfoActivity.class)
                    .putExtra(QCodeSettleInfoActivity.APPLY_ID, applyId)
                    .putExtra(QCodeSettleInfoActivity.ID, id)
                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                    .putExtra("QCODETYPE", EliveApplication.PUBLICQCODE)
//                    .putExtra("ContentBean", contentBean)
                    .putExtra("MerchantName", tvLicenseName.getText().toString().trim()));
        } else {
            Utils.showToast(this, "请先获取营业执照信息");
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (NetAPI.ACTION_MER_APPLY2 == method) {//不是提交照片的请求
            merApplyInfoRes2 = (MerApplyInfoRes2) obj;
            if(!isGetResult){
                toCheckResult();
                return;
            }
            Utils.showToast(this, merApplyInfoRes2.getContent().getMessage());
            if (merApplyInfoRes2.getContent().isSuccess()) {//商户存在,显示商户信息
                setTextContext(merApplyInfoRes2);
                setNextEnable(true);
            } else {
                merApplyInfoRes2 = null;
                cleanTextContent();
                setNextEnable(false);
            }

        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //如果有数据,就回显到控件上
            if (null != contentBean && null != contentBean.getMerOpenInfo() && !TextUtils.isEmpty(contentBean.getMerOpenInfo().getMerLicenceNo())) {
                MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
                edtLicense.setText(getNoEmptyString(merOpenInfo.getMerLicenceNo()));

                if (!TextUtils.isEmpty(merOpenInfo.getRegistName())) {
                    rlLicenseInfoTab.setVisibility(View.VISIBLE);
                    tvLicenseName.setText(getNoEmptyString(merOpenInfo.getRegistName()));
                    tvLegalName.setText(getNoEmptyString(merOpenInfo.getCorporateRepresentative()));
                    tvLicenseAddress.setText(getNoEmptyString(merOpenInfo.getRegistAddress()));
                    tvValidDate.setText(getNoEmptyString(merOpenInfo.getRegistAgeLimit()));
                    setNextEnable(true);
                } else {
                    setNextEnable(false);
                    rlLicenseInfoTab.setVisibility(View.GONE);
                }

            }
        }
    }


    private void toCheckResult(){
        if(merApplyInfoRes2 == null){
            return;
        }
        Map<String,String> maps = merApplyInfoRes2.getContent().getValidInfoMap();
        boolean resultBl = merApplyInfoRes2.getContent().isVerifyResult();
        if(!resultBl){
            if(maps != null && maps.size() == 1){
                String string = maps.get("licenseResult");
                if(!TextUtils.isEmpty(string)) {
                    DialogUtil.createAlertDialog(
                            this,
                            "注意",
                            string,
                            "取消",
                            "继续",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    switch (which) {
                                        case AlertDialog.BUTTON_POSITIVE:// 确定
                                            startActivity(new Intent(QCodeLicenseEntryPhotoActivity.this, QCodeBasicInfoActivity.class)
                                                    .putExtra(QCodeSettleInfoActivity.APPLY_ID, applyId)
                                                    .putExtra(QCodeSettleInfoActivity.ID, id)
                                                    .putExtra(EnterPieceActivity.MERCHANT_ID, merchantId)
                                                    .putExtra("QCODETYPE", EliveApplication.PUBLICQCODE)
//                    .putExtra("ContentBean", contentBean)
                                                    .putExtra("MerchantName", tvLicenseName.getText().toString().trim()));
                                            break;
                                        case AlertDialog.BUTTON_NEGATIVE:// 取消
                                            break;
                                    }
                                }
                            }
                    ).show();
                    return;
                }
            }
        }
        next();
    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (NetAPI.ACTION_MER_APPLY2 == method) {
            merApplyInfoRes2=null;
            setNextEnable(false);
            if (statusCode.equals("ERROR:null")) {
                Utils.showToast(this, "该营业执照无法查询");
                cleanTextContent();
            } else {
                Utils.showToast(this, statusCode);
                cleanTextContent();
            }
        } else if (method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, statusCode);
        }
    }

    /**
     * 设置营业执照信息
     *
     * @param merApplyInfoRes2
     */
    private void setTextContext(MerApplyInfoRes2 merApplyInfoRes2) {
        tvLicenseInfo.setVisibility(View.VISIBLE);
        rlLicenseInfoTab.setVisibility(View.VISIBLE);

        tvLicenseName.setText(merApplyInfoRes2.getContent().getName());
        tvLegalName.setText(merApplyInfoRes2.getContent().getCorporator());
        tvLicenseAddress.setText(merApplyInfoRes2.getContent().getAddress());
        tvValidDate.setText(merApplyInfoRes2.getContent().getExpire());
    }

    /**
     * 获取营业执照失败后清除上次数据
     */
    private void cleanTextContent() {
        tvLicenseInfo.setVisibility(View.GONE);
        rlLicenseInfoTab.setVisibility(View.GONE);

        tvLicenseName.setText("");
        tvLegalName.setText("");
        tvLicenseAddress.setText("");
        tvValidDate.setText("");
    }


    private void showAlertDialog(String tip) {
        DialogUtil.createAlertDialog(
                this,
                "注意",
                 tip+"",
                "取消",
                "继续",
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
                    next();
                    dialog.dismiss();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
