package com.lakala.elive.preenterpiece;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceSubmitInfoRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceSubmitInfoResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 合作商预进件营业执照信息
 */
public class PreEnterLicenseEntryPhotoActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();

    private Button btnNext, btnSubmit;
    private TextView tvValidDate;
    private ImageView imgProgress;

    private EditText edtLicense;
    private EditText edtLinceName;
    private EditText edtLegalPerson;
    private EditText edtLicenseAddress;

    private String license;//营业执照号
    private String linceName;//
    private String licenseLegerPerson;//
    private String liceseAddress;//
    private String licenValidate;//
    private String applyId;//进件id
    private InputMethodManager imm;//键盘服务

    private TimePickerView pvTime;
    private PreEnPieceDetailRequ preEnPieceDetailRequ;
    private PreEnPieceDetailResponse preEnPieceDetailResponse;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_preenter_licensephotoentry);
        XAtyTask.getInstance().addAty(this);
    }

    protected void bindView() {
        edtLicense = findView(R.id.edt_license);
        edtLinceName = findView(R.id.edt_license_name);
        edtLegalPerson = findView(R.id.edt_legal_name);
        edtLicenseAddress = findView(R.id.edt_license_address);
        tvValidDate = findView(R.id.tv_valid_date);
        btnSubmit = findView(R.id.btn_submit);
        btnNext = findView(R.id.btn_next);
        iBtnBack.setVisibility(View.VISIBLE);
        tvTitleName.setText("营业执照");
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        timePickerView();
        imgProgress = findView(R.id.img_progress);
        EliveApplication.setImageProgress(imgProgress, 6);
    }

    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvValidDate.setOnClickListener(this);
        edtLicense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(edtLicense.getText().toString().trim()) ||
                        edtLicense.getText().toString().length() <= 5) {
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
        if (intent != null) {
            applyId = intent.getStringExtra("APPLYID");
        }

        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");
            preEnPieceDetailRequ = new PreEnPieceDetailRequ();
            preEnPieceDetailRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            preEnPieceDetailRequ.setApplyId(applyId);
            NetAPI.preEnterPieDetailRequest(this, this, preEnPieceDetailRequ);
        }

        String biz_license_registration_code = intent.getStringExtra("BUNISSLINCE");//获取营业执照
        if (!TextUtils.isEmpty(biz_license_registration_code)) {
            edtLicense.setText(biz_license_registration_code);//设置营业执照号
            edtLicense.setSelection(biz_license_registration_code.length());
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_valid_date://有效期
            case R.id.tv_id_date://身份证有效期
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
                }
                pvTime.show();
                break;
            case R.id.btn_next:
                license = edtLicense.getText().toString().trim();
                linceName = edtLinceName.getText().toString().trim();
                licenseLegerPerson = edtLegalPerson.getText().toString().trim();
                liceseAddress = edtLicenseAddress.getText().toString().trim();
                licenValidate = tvValidDate.getText().toString().trim();

//                if(getTextContext()){
                next();
//                  }
                break;
        }
    }

    private PreEnPieceSubmitInfoRequ preEnPieceSubmitInfoRequ;

    private void next() {
        preEnPieceSubmitInfoRequ = new PreEnPieceSubmitInfoRequ();
        preEnPieceSubmitInfoRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        PreEnPieceSubmitInfoRequ.PartnerApplyInfo partnerApplyInfo = new PreEnPieceSubmitInfoRequ.PartnerApplyInfo();
        partnerApplyInfo.setProcess("6");
        partnerApplyInfo.setApplyType("1");
        partnerApplyInfo.setApplyChannel("01");
        partnerApplyInfo.setApplyId(applyId);
        preEnPieceSubmitInfoRequ.setMerApplyInfo(partnerApplyInfo);
        PreEnPieceSubmitInfoRequ.MerOpenInfo merOpenInfo = new PreEnPieceSubmitInfoRequ.MerOpenInfo();
        merOpenInfo.setApplyId(applyId);
        merOpenInfo.setMerLicenceNo(license);
        merOpenInfo.setRegistName(linceName);
        merOpenInfo.setCorporate_representative(licenseLegerPerson);
        merOpenInfo.setRegistAddress(liceseAddress);
        merOpenInfo.setRegistAgeLimit(licenValidate);
        showProgressDialog("提交中...");
        preEnPieceSubmitInfoRequ.setMerOpenInfo(merOpenInfo);
        NetAPI.preEnterPieSubmitInfoRequest(this, this, preEnPieceSubmitInfoRequ);
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ELIVE_PARTNER_APPLY_003://提交商户信息成功
                PreEnPieceSubmitInfoResponse preEnPieceSubmitInfoResponse = (PreEnPieceSubmitInfoResponse) obj;
                if (preEnPieceSubmitInfoResponse != null && preEnPieceSubmitInfoResponse.getContent() != null) {
                    applyId = preEnPieceSubmitInfoResponse.getContent().getApplyId();
                    Intent intent = new Intent(this, PerApplyCompleteActivity.class);
                    intent.putExtra("APPLYID", applyId + "");
                    startActivity(intent);
                }
                break;
            case NetAPI.ELIVE_PARTNER_APPLY_002://编辑转态
                preEnPieceDetailResponse = (PreEnPieceDetailResponse) obj;
                if (preEnPieceDetailResponse != null) {
                    PreEnPieceDetailResponse.ContentBean content = preEnPieceDetailResponse.getContent();
                    if (content != null) {
                        setContent(content.merOpenInfo);
                    }
                } else {
                    Log.e(TAG, "preEnPieceDetailResponse为空");
                }
                break;
        }
    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode);
    }

    /**
     * 选择身份证有效期
     */
    private void timePickerView() {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 100);//今年到以后100年
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(onTimeSelectListener);
    }


    /**
     * 时间选择监听
     */
    TimePickerView.OnTimeSelectListener onTimeSelectListener = new TimePickerView.OnTimeSelectListener() {
        @Override
        public void onTimeSelect(Date date) {
            tvValidDate.setText(getTime(date));
        }
    };

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    private boolean getTextContext() {
        license = edtLicense.getText().toString().trim();
        linceName = edtLinceName.getText().toString().trim();
        licenseLegerPerson = edtLegalPerson.getText().toString().trim();
        liceseAddress = edtLicenseAddress.getText().toString().trim();
        licenValidate = tvValidDate.getText().toString().trim();

        if (TextUtils.isEmpty(license)) {
            Utils.showToast(this, "请输入营业执照号");
        } else if (TextUtils.isEmpty(linceName)) {
            Utils.showToast(this, "请输入营业执照名称");
        } else if (TextUtils.isEmpty(licenseLegerPerson)) {
            Utils.showToast(this, "请输入法人姓名");
        } else if (TextUtils.isEmpty(liceseAddress)) {
            Utils.showToast(this, "请输入营业执照地址");
        } else if (TextUtils.isEmpty(licenValidate)) {
            Utils.showToast(this, "请选择营业执照");
        } else if (isOverdue(licenValidate)) {
            Utils.showToast(this, "营业执照已过期");
        } else {
            return true;
        }
        return false;
    }


    /**
     * 判断身份证时间是否过去
     *
     * @param time
     * @return
     */
    private boolean isOverdue(String time) {
        try {
            if (time.length() == 10) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(time);
                long timeStemp = date.getTime();
                long currentTimeMillis = System.currentTimeMillis();
                if (timeStemp < currentTimeMillis) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Utils.showToast(this, "时间格式不对");
            return true;
        }
        return false;
    }

    private void setContent(PreEnPieceDetailResponse.ContentBean.MerOpenInfo merOpenInfo) {
        if (merOpenInfo != null) {
            edtLicense.setText(getNoEmptyString(merOpenInfo.getMerLicenceNo()));
            edtLinceName.setText(getNoEmptyString(merOpenInfo.getRegistName()));
            edtLegalPerson.setText(getNoEmptyString(merOpenInfo.getCorporateRepresentative()));
            edtLicenseAddress.setText(getNoEmptyString(merOpenInfo.getRegistAddress()));
            tvValidDate.setText(getNoEmptyString(merOpenInfo.getRegistAgeLimit()));
        }
    }

}
