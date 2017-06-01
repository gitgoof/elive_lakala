package com.lakala.shoudan.activity.accountmanagement;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.CountdownInputBoxView;
import com.lakala.ui.component.IconItemView;
import com.lakala.ui.component.LabelEditText;

/**
 * Created by ZhangMY on 2015/3/11.
 */
public abstract class GetSmsModuleActivity extends AppBaseActivity {
    protected LabelEditText editPhone;
    protected CountdownInputBoxView mCountDownInputBoxView;

    protected TextView btnNext;


    protected String token;//登录注册 都需要 一次性令牌

    protected String mUserName;
    protected String smsCode;

    protected PopupWindow mPopupWindow;
    protected HintFocusChangeListener hintChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
    }

    @Override
    protected boolean isRequired2Login() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mCountDownInputBoxView.cancleTime();
    }

    protected abstract void initSpecialView();//初始化特定的界面

    protected abstract void getSmsCode();//获取验证码

    protected abstract void handerNext();//下一步

    protected abstract boolean isInputValidate();//效验数据合法性

    protected abstract void showEditTextTips(ImageView iconView, int width);


    private void initShowType() {
        navigationBar.setTitle(getResources().getString(R.string.phone_verifaction));

        initSpecialView();
    }

    private void initView() {
//        hideNavigationBar();
//        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        editPhone = (LabelEditText) findViewById(R.id.edit_phone_num);
        mCountDownInputBoxView = (CountdownInputBoxView) findViewById(R.id.countdown_inputbox);


        mCountDownInputBoxView.setTitle(getResources().getString(R.string.vercode));
        mCountDownInputBoxView.setEditLength(6);
//        mCountDownInputBoxView.setHintTextColor(getResources().getColor(R.color.hint_or_divider_color));
        mCountDownInputBoxView.setEditInputType(InputType.TYPE_CLASS_NUMBER);
        mCountDownInputBoxView.setGetVerifyCodeListener(this);
//        mCountDownInputBoxView.setVerifyCodeButtonColor(getResources().getColor(R.color.has_input_edittext_color));

//        mCountDownInputBoxView.getVerfyCodeButton().setBackgroundResource(
//                R.drawable.btn_vercode_selector);
        mCountDownInputBoxView.getVerfyCodeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取验证码
                getSmsCode();
            }
        });

        hintChangeListener = new HintFocusChangeListener();
        editPhone.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                editPhone.setSelected(b);
                mCountDownInputBoxView.setSelected(!b);
                if (hintChangeListener != null) {
                    hintChangeListener.onFocusChange(view, b);
                }
//                if(b){
//                    editPhone.setRightIconVisibility(View.VISIBLE);
//                }else {
//                    editPhone.setRightIconVisibility(View.GONE);
//                }
            }
        });
        mCountDownInputBoxView.getVerifycodeEdit().setOnFocusChangeListener(hintChangeListener);
        editPhone.setEnableOnClickItemEvents(true);
        editPhone.setOnClickItemListener(new IconItemView.OnClickItemListener() {
            @Override
            public void OnClickItem(View view, IconItemView.ItemType type) {
                if (null != mPopupWindow) {
                    dismissPopupwindow();
                } else if (type == IconItemView.ItemType.RightIcon) {
                    showEditTextTips(editPhone.getRightIconImageView(), editPhone.getWidth());
//                    PopupWindowFactory.createEditPopupWindowTips(editPhone.getRightIconImageView(), editPhone.getWidth(), GetSmsModuleActivity.this, getResources().getString(R.string.check_phone_edit_tips));
                }
            }
        });

        btnNext = (TextView) findViewById(R.id.id_common_guide_button);
        btnNext.setOnClickListener(this);
        initShowType();//找回密码，注册，登录成功后的验证
    }

    protected void dismissPopupwindow() {
        if (null != mPopupWindow) {
            mPopupWindow.setFocusable(true);
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);

        switch (view.getId()) {
            case R.id.id_common_guide_button://    下一步
                if (isInputValidate()) {
                    // 验证码通过之后 界面跳转
                    handerNext();
                }
                break;
        }
    }

    protected boolean validateSmscodeAndToken() {
        smsCode = mCountDownInputBoxView.getVerifycodeEdit().getText().toString().trim();
        if (StringUtil.isEmpty(smsCode)) {
            ToastUtil.toast(this, getString(R.string.please_input_mobile_vercode), Toast.LENGTH_SHORT);
            return false;
        }

        if (TextUtils.isEmpty(token)) {
            ToastUtil.toast(this, getString(R.string.please_get_checkcode_first));
            return false;
        }

        int len = smsCode.length();
        if (len != 6) {
            ToastUtil.toast(this, getString(R.string.input_code_null), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    protected boolean validatePhoneNum() {
        mUserName = StringUtil.formatString(editPhone.getEditText().getText().toString().trim());
        if (StringUtil.isEmpty(mUserName)) {
            ToastUtil.toast(this, getString(R.string.toast_phone_empty), Toast.LENGTH_SHORT);
            return false;
        }

        if (!PhoneNumberUtil.checkPhoneNumber(mUserName)) {
            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_phonenumber_error), Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

}
