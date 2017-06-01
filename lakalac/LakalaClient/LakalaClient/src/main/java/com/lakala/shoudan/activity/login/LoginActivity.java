package com.lakala.shoudan.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.accountmanagement.ForgotPasswordActivity;
import com.lakala.shoudan.activity.accountmanagement.UserRegisterActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.component.ScrollViewContainer;
import com.lakala.shoudan.loginservice.TokenRefreshService;
import com.lakala.shoudan.util.IMEUtil;
import com.lakala.shoudan.util.LoginUtil;

import butterknife.Bind;

/**
 * 登录界面
 * Created by ZhangMY on 2015/1/13.
 */

public class LoginActivity extends AppBaseActivity implements View.OnClickListener {

    private static ImageView iv_return;
    private static View view_seperator;
    private static ImageView iv_more;
    @Bind(R.id.svc)
    ScrollViewContainer svc;
    private EditText editAccount;
    private SecurityEditText editPass;
    //设置标识，来区分哪一个输入框弹的键盘
    String bdTag="loginPwd";

    private String mUserName;
    private String mPassword;

    private TextView btnLogin;

    /**
     * 注册完成设置手势密码
     */
    private final int REQUEST_CODE_GESTURE_PWD = 3;

    private final int REQUEST_CODE_RESET_PWD = 4;

    private String refreshToken;

    private int editAccountY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BusinessLauncher.getInstance().finishAll();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);        //进入login页面 清除登录信息
        ApplicationEx.getInstance().getSession().clear();
        initView();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        editPass.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (!TextUtils.isEmpty(intent.getStringExtra(ConstValues.IntentKey.LoingName))) {
            editAccount.setText(intent.getStringExtra(ConstValues.IntentKey.LoingName));
        }

    }

    public static void setViewStyle(boolean top) {
        if (top) {
            navigationBar.setTitle("登录");
            iv_return.setVisibility(View.INVISIBLE);
            iv_more.setVisibility(View.VISIBLE);
        } else {
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_9,ApplicationEx.getInstance().getActiveContext());
            navigationBar.setTitle("了解拉卡拉");
            iv_more.setVisibility(View.INVISIBLE);
            iv_return.setVisibility(View.VISIBLE);
        }
    }

    public static void setSeprator(boolean autoup) {
        if (autoup) {
            iv_return.setVisibility(View.INVISIBLE);
        } else {
            iv_more.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected boolean isRequired2Login() {
        return false;
    }


    private void initView() {
//        hideNavigationBar();
        if (getIntent() != null) {
            String stringExtra = getIntent().getStringExtra(Constants.fromReset);
            if (!TextUtils.isEmpty(stringExtra) && TextUtils.equals(stringExtra, Constants.fromReset)) {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Modify_Login_czLoginPwd_over, this);
            }
        }

        iv_return = (ImageView) findViewById(R.id.iv_return);
        view_seperator = findViewById(R.id.view_seperator);
        iv_more = (ImageView) findViewById(R.id.iv_more);
        navigationBar.setTitle("登录");
        navigationBar.setBackBtnVisibility(View.GONE);
//        findViewById(R.id.contentView).requestFocus();
        editAccount = (EditText) findViewById(R.id.id_phone_edit);
        editPass = (SecurityEditText) findViewById(R.id.id_passwor_edit);
        editPass.setSecurityManager(SecurityKeyboardUtil.lklPassword(editPass,bdTag));

        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editPass.addTextChangedListener(textWatcher);
        btnLogin = (TextView) findViewById(R.id.login);//登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoginInputVaild()) {
                    //登录成功后进行验证
                    com.lakala.shoudan.common.util.IMEUtil.hideIme(LoginActivity.this);

                    TokenRefreshService.getInstance().setLoginByPwd(true);

                    //ToastUtil.toast(LoginActivity.this,mPassword);
                    LoginUtil.login(mUserName, CommonEncrypt.loginEncrypt(mPassword), LoginActivity.this);
                }
            }
        });

        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {//注册
            @Override
            public void onClick(View view) {
                //注册统计
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_1, LoginActivity.this);

                Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.find_login_PW).setOnClickListener(new View.OnClickListener() {//取回密码
            @Override
            public void onClick(View view) {
                //忘记密码统计

                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_5, LoginActivity.this);

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivityForResult(intent, REQUEST_CODE_RESET_PWD);
            }
        });

        // 记住账号
        checkkeep = (CheckBox) findViewById(R.id.keep_phone);
        boolean checked = LklPreferences.getInstance().getBoolean(LKlPreferencesKey.KEY_SAVE_LOGIN_NAME);
        checkkeep.setChecked(checked);
        checkkeep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_SAVE_LOGIN_NAME, b);

            }
        });


        if (checked) {
            editAccount.setText(LklPreferences.getInstance().getString(LKlPreferencesKey.KEY_LOGIN_NAME));
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(ConstValues.IntentKey.LoingName))) {
            editAccount.setText(getIntent().getStringExtra(ConstValues.IntentKey.LoingName));
        }
    }

    public static boolean ifSave = true;

    private CheckBox checkkeep;

    /**
     * 效验数据合法性
     */
    private boolean isLoginInputVaild() {
        mUserName = editAccount.getText().toString().trim();
        mPassword = editPass.getText(bdTag);
        if (StringUtil.isEmpty(mUserName)) {
            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_phonenumber), Toast.LENGTH_SHORT);
            return false;
        }

        if (!PhoneNumberUtil.checkPhoneNumber(mUserName)) {

            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_phonenumber_error), Toast.LENGTH_SHORT);
            return false;
        }

        if (StringUtil.isEmpty(mPassword)) {

            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_password), Toast.LENGTH_SHORT);
            return false;
        }

        int len = mPassword.length();
        if (len < 6 || len > getResources().getInteger(R.integer.max_password_limit)) {
            ToastUtil.toast(this, "请输入正确的6~32位密码", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private long lastClickTime = 0;

    @Override
    public void onBackPressed() {

        int[] location = new int[2];
//        mBackLinearLayout.getLocationInWindow(location);

        if (editAccountY != location[1]) {
            IMEUtil.hideIme(this);

            return;
        }


        if (lastClickTime == 0) {
            lastClickTime = System.currentTimeMillis();
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
            return;
        }

        final long interval = System.currentTimeMillis() - lastClickTime;

        lastClickTime = System.currentTimeMillis();


        if (interval > 2000) {
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));

        } else {
            ApplicationEx.getInstance().exit();
        }
    }


}
