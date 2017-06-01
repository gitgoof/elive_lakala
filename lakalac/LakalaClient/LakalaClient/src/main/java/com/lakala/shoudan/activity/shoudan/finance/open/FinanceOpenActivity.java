package com.lakala.shoudan.activity.shoudan.finance.open;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.FundStateQryReturnData;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundSignUpRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryOTPPasswordRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ShortcutSignRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.question.AddUserQuestionActivity;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.CaptchaTextView;

import org.json.JSONObject;

/**
 * Created by LMQ on 2015/9/6.<br/>
 * 理财开通流程：<br/>
 * 1、获取快捷签约短信验证码(queryOTPPassword)<br/>
 * 2、开始快捷签约(shortcutSign)<br/>
 * 3、进行理财业务开通(fundSignUp)
 */
public class FinanceOpenActivity extends AppBaseActivity {
    private static final int INPUT_PWD_REQUEST = 0x2325;
    private EditText etName;
    private EditText etIdcard;
    private EditText etBankname;
    private EditText etBankcode;
    private EditText etUserphone;
    private CaptchaTextView tvGetCaptcha;
    private View llCaptcha;
    private View llAgreement;
    private EditText etInputCaptcha;
    private boolean needShortCutSign;
    private FundStateQryReturnData stateData;
    private boolean shortCutSignComplete = false;
    private FundSignUpRequest signUpRequest = new FundSignUpRequest();
    private String bankCode = ApplicationEx.getInstance().getUser().getBankNoBeijing();
    private View nextBtn;
    private String captchaSid;
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();

    /**
     * @param context
     * @param needShortCutSign 是否需要进行代收签约
     */
    public static void start(Context context, boolean needShortCutSign, JSONObject stateData) {
        Intent intent = new Intent(context, FinanceOpenActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_STATE, needShortCutSign);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, stateData.toString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_open);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
        navigationBar.setTitle("业务开通");
        needShortCutSign = getIntent().getBooleanExtra(Constants.IntentKey.TRANS_STATE, true);
        String jsonStr = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
        stateData = FundStateQryReturnData.parse(jsonStr);

        llCaptcha = findV(R.id.ll_check_captcha);
        llAgreement = findV(R.id.ll_agreement);
        if (!needShortCutSign) {
            llCaptcha.setVisibility(View.GONE);
            llAgreement.setVisibility(View.GONE);
        } else {
            CheckBox cBoxAgree = findV(R.id.check_agree);
            cBoxAgree.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            nextBtn.setEnabled(isChecked);
                        }
                    }
            );
            TextView agreement1 = findV(R.id.tv_agreement1);
            agreement1.setText(ProtocalType.ALL_LCKH.getTitle());
            agreement1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //赚点钱服务协议
                            ProtocalActivity.open(context, ProtocalType.ALL_LCKH);
                        }
                    }
            );
        }

        View view = findV(R.id.include_name);
        TextView tvNameLabel = getItemLabel(view);
        tvNameLabel.setText("申请人姓名");
        etName = getItemEdit(view);
        etName.setText(Util.formateUserNameKeepLast(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName()));
        etName.setOnFocusChangeListener(hintListener);
        setEditEditableFalse(etName);

        view = findV(R.id.include_idcard);
        TextView tvIdcardLabel = getItemLabel(view);
        tvIdcardLabel.setText("申请人身份证号");
        etIdcard = getItemEdit(view);
        etIdcard.setText(merchantInfo.getUser().getIdCardInfo().getIdCardId());
        etIdcard.setOnFocusChangeListener(hintListener);
        if (!needShortCutSign) {
            setEditEditableFalse(etIdcard);
        }

        view = findV(R.id.include_bankname);
        TextView tvBanknameLabel = getItemLabel(view);
        tvBanknameLabel.setText("开户银行");
        etBankname = getItemEdit(view);
        etBankname.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
        etBankname.setOnFocusChangeListener(hintListener);
        setEditEditableFalse(etBankname);

        view = findV(R.id.include_bankcode);
        TextView tvBankcodeLabel = getItemLabel(view);
        tvBankcodeLabel.setText("储蓄卡卡号");
        etBankcode = getItemEdit(view);
        etBankcode.setText(merchantInfo.getDisplasyAccountNo());
        etBankcode.setInputType(InputType.TYPE_CLASS_NUMBER);
        etBankcode.setOnFocusChangeListener(hintListener);
        setEditEditableFalse(etBankcode);

        view = findV(R.id.include_userphone);
        TextView tvUserphoneLabel = getItemLabel(view);
        tvUserphoneLabel.setText("持卡人手机号");
        etUserphone = getItemEdit(view);
        etUserphone.setHint("请输入银行预留手机号码");
        etUserphone.setInputType(InputType.TYPE_CLASS_PHONE);
        etUserphone.setOnFocusChangeListener(hintListener);

        TextView tvCaptchaLabel = findV(R.id.tv_label);
        tvCaptchaLabel.setText("短信验证码");

        tvGetCaptcha = findV(R.id.tv_get_captcha);
        Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCaptcha();
                    }
                }
        );
        etUserphone.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tvGetCaptcha.setEnabled(s.length() == 11);
                    }
                }
        );
        etInputCaptcha = findV(R.id.et_input_captcha);

        nextBtn = findV(R.id.id_common_guide_button);
        nextBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signUpRequest.setMobileInBank(etUserphone.getText().toString());
                        signUpRequest.setIdentifier(etIdcard.getText().toString());
                        if (needShortCutSign) {
                            if (shortCutSignComplete) {
                                askToSetPayPwdDialog();
                            } else {
                                shortCutSign();
                            }
                        } else {
                            askToSetPayPwdDialog();
                        }
                    }
                }
        );
    }

    private void storageViewSize(View view, Point size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
    }

    private Point getViewSize(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
    }

    /**
     * 代收签约
     */
    private void shortCutSign() {
        String phone = etUserphone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toast(context, "请输入持卡人手机号");
            return;
        }
        String captcha = etInputCaptcha.getText().toString();
        if (TextUtils.isEmpty(captcha)) {
            ToastUtil.toast(context, "请输入验证码");
            return;
        }
        String idCard = etIdcard.getText().toString();
        if (!Util.checkIdCard(idCard)) {
            ToastUtil.toast(context, R.string.lc_invalid_idcard);
            return;
        }
        ShortcutSignRequest request = new ShortcutSignRequest();
        request.setCustomerName(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        request.setIdentifier(idCard);
        request.setBankName(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
        request.setBankCode(bankCode);
        request.setMobileInBank(phone);
        request.setAccountNo(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo());
        request.setOTPPassword(captcha);
        request.setSrcid(captchaSid);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
//                    fundStateQry();
                    shortCutSignComplete = true;
                    askToSetPayPwdDialog();
                } else {
                    shortCutSignFailure();
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toastHttpError();

            }
        };
        tvGetCaptcha.resetCaptchaDown();
        FinanceRequestManager.getInstance().shortCutSign(request, listener);
    }

    private void askToSetPayPwdDialog() {

        switch (stateData.getTrsPasswordFlag()) {
            case 未设置:
                DialogCreator.createFullContentDialog(
                        context, "取消", "设置", "为了保证您的交易安全，在您购买理财的时候，需要先设置支付密码和密保问题",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SetPayPwdActivity.open(context, signUpRequest);
                            }
                        }
                ).show();
                break;
            case 已设置:
                askToSetQuestion();
                break;
        }
    }

    public void askToSetQuestion() {
        switch (stateData.getQuestionFlag()) {
            case 未设置:
                AddUserQuestionActivity.open(context, signUpRequest);
                break;
            case 已设置:
                Intent intent = new Intent(context, InputPayPwdDialogActivity.class);
                startActivityForResult(intent, INPUT_PWD_REQUEST);
                break;
        }
    }

    private void shortCutSignFailure() {
        DialogCreator.createOneConfirmButtonDialog(
                context, "确定", "您提交的信息有误！请确认信息后重新提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    /**
     * 获取短信验证码
     */
    private void getCaptcha() {
        //验证码
        String phone = etUserphone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toast(context, "请输入持卡人手机号");
            return;
        }
        String idCard = etIdcard.getText().toString();
        if (!Util.checkIdCard(idCard)) {
            ToastUtil.toast(context, R.string.lc_invalid_idcard);
            return;
        }
        QueryOTPPasswordRequest request = new QueryOTPPasswordRequest();
        request.setCustomerName(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        request.setIdentifier(idCard);
        request.setBankName(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
        request.setBankCode(bankCode);
        request.setMobileInBank(phone);
        request.setAccountNo(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    captchaSid = responseData.optString("sid", "");
                    tvGetCaptcha.startCaptchaDown(59);
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                    tvGetCaptcha.resetCaptchaDown();
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                toastHttpError();

            }
        };
        FinanceRequestManager.getInstance().queryOTPPassword(request, listener);
    }

    private void toastHttpError() {
        ToastUtil.toast(context, "您的网络不太好，请稍候重试");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_PWD_REQUEST && resultCode == RESULT_OK) {
            String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
            signUpRequest.setTrsPassword(pwd);
            FinanceRequestManager.getInstance().startSignUp(FinanceOpenActivity.this, signUpRequest);
        }
    }

    private void setEditEditableFalse(EditText et) {
        et.setInputType(InputType.TYPE_NULL);
        et.setCursorVisible(false);
        et.setFocusable(false);
    }

    private TextView getItemLabel(View view) {
        return findV(view, R.id.id_combinatiion_text_edit_text);
    }

    private EditText getItemEdit(View view) {
        return findV(view, R.id.id_combination_text_edit_edit);
    }

    private <T extends View> T findV(View view, int id) {
        View v;
        if (view == null) {
            v = findViewById(id);
        } else {
            v = view.findViewById(id);
        }
        return v == null ? null : (T) v;
    }

    private <T extends View> T findV(int id) {
        return findV(null, id);
    }
}
