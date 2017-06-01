package com.lakala.elive.user.activity;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.UserLoginInfo;
import com.lakala.elive.beans.VersionInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.ActivityTaskCacheUtil;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.DownLoadUtil;
import com.lakala.elive.common.utils.EncodeUtil;
import com.lakala.elive.common.utils.PermissionsChecker;
import com.lakala.elive.common.utils.RequestAllDictDataService;
import com.lakala.elive.common.utils.RequestDictionaryService;
import com.lakala.elive.common.utils.UiUtils;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.base.BaseActivity;

import org.xutils.common.util.MD5;

/**
 * 登录界面UI
 *
 * @author hongzhiliang
 */
public class UserLoginActivity extends BaseActivity {

    // 所需的全部权限
    private String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private TextView forgetPwd;
    private String userName;
    private String url;

    //设置用户登录UI
    public void setContentViewId() {
        setContentView(R.layout.activity_user_login);
    }


    private EditText etLoginName, etLoginPwd;  //登录名和登录密码输入框
    private Button btnLoginSubmit;           //提交按钮
    private CheckBox cbLoginRemember;          //记住密码
    private TextView tvVersionInfo;            //当前版本信息
    private Button checkSendBtn; //验证码发送按钮显示

    private ToggleButton tbPwdShow; //密码显示

    public void bindView() {
        etLoginName = (EditText) findViewById(R.id.et_login_name);
        etLoginPwd = (EditText) findViewById(R.id.et_login_pwd);
        tvVersionInfo = (TextView) findViewById(R.id.tv_version_info);
        btnLoginSubmit = (Button) findViewById(R.id.btn_login_submit);
        cbLoginRemember = (CheckBox) findViewById(R.id.cb_login_remember);

        checkSendBtn = (Button) findViewById(R.id.btn_checkcode_send); //验证码发送
        tbPwdShow = (ToggleButton) findViewById(R.id.tb_password_show); //验证码发送
        forgetPwd = (TextView) findViewById(R.id.tv_forget_pwd); //验证码发送

        findViewById(R.id.iv_logo).setOnClickListener(this); //修改服务器地址
    }

    public void bindEvent() {

        checkPermission();

        //提交按钮处理
        btnLoginSubmit.setOnClickListener(this);
        //记住按钮选择
        cbLoginRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbLoginRemember.isChecked()) {
                    sp.edit().putBoolean(Constants.KEY_SP_REMEMBER_PWD_CHECK, true).commit();
                } else {
                    sp.edit().putBoolean(Constants.KEY_SP_REMEMBER_PWD_CHECK, false).commit();
                }
            }
        });

        checkSendBtn.setOnClickListener(this);

        tbPwdShow.setOnCheckedChangeListener(mOnCheckedChangeListener);
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserName();
            }
        });
    }

    private void checkUserName() {
        userName = etLoginName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
        } else {
            UiUtils.startActivity(UserLoginActivity.this, UserCheckActivity.class, "login_name", userName);
        }

    }

    public void bindData() {
        mSession = Session.get(this);
        if (isSetupPwd()) {//设置过密码
            etLoginName.setText(sp.getString(Constants.KEY_SP_USER_NAME, null));
            etLoginPwd.setText(sp.getString(Constants.KEY_SP_USER_PWD, null));
            cbLoginRemember.setChecked(true);
        }
        tvVersionInfo.setText(getString(R.string.current_version_info, Session.get(this).getVersionName()));

        //服务器获取最新版本信息
        UserReqInfo reqInfo = new UserReqInfo();
        reqInfo.setPlatformType("ANDROID");
        NetAPI.getAppVersionCode(this, this, reqInfo);

    }

    //配置信息
    private SharedPreferences sp;

    /**
     * 判断用户是否设置保存密码
     *
     * @return
     */
    private boolean isSetupPwd() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        String spSavePassword = sp.getString(Constants.KEY_SP_USER_PWD, null);
        if (TextUtils.isEmpty(spSavePassword)) {
            return false;
        } else {
            return true;
        }
    }

    // 点击事件处理
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_submit://提交登录按钮
                loginSubmit();
                break;
            case R.id.btn_checkcode_send: //发送动态码
                getCheckCodeSubmit();
                break;
            case R.id.iv_logo: //修改服务器地址
                showUpdateServerUrlDialog();
                break;
            default:
                break;
        }
    }

    String etLoginNameStr; //用户输入登录名
    String etPasswordStr; //用户输入登录密码

    /**
     * 用户登录处理
     */
    private void loginSubmit() {
        etLoginNameStr = etLoginName.getText().toString().trim();
        etPasswordStr = etLoginPwd.getText().toString().trim();

        if (TextUtils.isEmpty(etLoginNameStr) || TextUtils.isEmpty(etPasswordStr)) {
            Utils.showToast(this, "登录名和密码不能为空!");
            return;
        } else {
            UserReqInfo loginReqInfo = new UserReqInfo();
            loginReqInfo.setLoginName(etLoginNameStr);
            loginReqInfo.setPasswordMd5(MD5.md5(etPasswordStr));
            loginReqInfo.setPassword(EncodeUtil.encodeBySha(etPasswordStr));

            if (!isFinishing()) {
                showProgressDialog("登录中...");
            } else { // 如果当前页面已经关闭，不进行登录操作
                return;
            }

            //提交用户认证
            NetAPI.userLoginSubmit(this, this, loginReqInfo);

            //保存数据
            if (cbLoginRemember.isChecked()) {
                sp.edit().putString(Constants.KEY_SP_USER_NAME, etLoginNameStr).commit();
                sp.edit().putString(Constants.KEY_SP_USER_PWD, etPasswordStr).commit();
            }
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_USER_LOGIN_SUBMIT:
                handlerLoginSuccess((UserLoginInfo) obj);
                break;
            case NetAPI.ACTION_GET_CHECK_CODE:
                Utils.showToast(this, "动态码获取成功!");
                break;
            case NetAPI.ACTION_GET_APP_VESION_CODE:
                checkVersion((VersionInfo) obj);
                break;
        }
    }

    /**
     * 应用版本检查
     */
    private void checkVersion(VersionInfo versionInfo) {
        if (versionInfo.getVersionCode() > mSession.getVersionCode()) {
            url = versionInfo.getUrl();
            showVersionDialog();
        }
    }

    private void showVersionDialog() {
        DialogUtil.createAlertDialog(
                this,
                "版本更新通知",
                "新版本发布,需要更新吗?",
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
                    dialog.dismiss();
                    showUpdateDialog();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }

        private void showUpdateDialog() {
            mProgressDialog = new ProgressDialog(UserLoginActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
            mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            mProgressDialog.setTitle("新版本更新...");
            mProgressDialog.show();//设置进度的显示
            DownLoadUtil.downLoadApkFile(url, Constants.saveFileName, UserLoginActivity.this, mProgressDialog);
        }
    };

    /**
     * 处理成功验证结果
     *
     * @param userLoginInfo
     */
    private void handlerLoginSuccess(UserLoginInfo userLoginInfo) {
        //初始化用户登录Session
        Session.get(this).setUserLoginInfo(userLoginInfo);

        startService(new Intent(this, RequestAllDictDataService.class));
        if ("0".equals(userLoginInfo.getDevChkStatus())) {//判断登录页面是否要进入验证
            UiUtils.startActivity(UserLoginActivity.this, UserCheckActivity.class, "login_name", userName);
            finish();
        } else {
            if ("1".equals(userLoginInfo.getIsNeedUpdatePwd())) {//定期修改密码
                UiUtils.startActivity(UserLoginActivity.this, UserPwdSetActivity.class, "login_name", userName);
                finish();
            } else {
                UiUtils.startActivity(UserLoginActivity.this, UserMainActivity.class, "login_name", userName);
                Utils.showToast(this, "登录成功!");
                finish();
            }
        }

    }


    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_USER_LOGIN_SUBMIT:
                Utils.showToast(this, "登录失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_GET_CHECK_CODE:
                Utils.showToast(this, "动态码获取失败:" + statusCode + "!");
                break;
            case NetAPI.ACTION_GET_APP_VESION_CODE:
                Utils.showToast(this, "当前应用版本获取失败:" + statusCode + "!");
                break;
        }
    }

    /**
     * 发送手机号验证码
     */
    private void getCheckCodeSubmit() {
        etLoginNameStr = etLoginName.getText().toString().trim();
        if (TextUtils.isEmpty(etLoginNameStr)) {
            Utils.showToast(this, "登录名不能为空!");
            return;
        } else {
            UserReqInfo reqInfo = new UserReqInfo();
            reqInfo.setDevCode(mSession.getDeviceId());
            reqInfo.setLoginName(etLoginNameStr);
            reqInfo.setAuthToken(etLoginNameStr);
            reqInfo.setCodeType("2");//1、授信码 2、动态密码
            NetAPI.getUserCheckCode(this, this, reqInfo);
        }
    }


    Dialog serverUrlUpdateDialog;
    EditText newServerUrlEt;

    public void showUpdateServerUrlDialog() {
        serverUrlUpdateDialog = new Dialog(this, R.style.edit_AlertDialog_style);
        serverUrlUpdateDialog.setContentView(R.layout.dialog_update_server_url);
        serverUrlUpdateDialog.show();
        serverUrlUpdateDialog.setCanceledOnTouchOutside(false); // Sets whether this dialog is the window's bounds.

        Window w = serverUrlUpdateDialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        serverUrlUpdateDialog.onWindowAttributesChanged(lp);

        Button mClose_btn = (Button) serverUrlUpdateDialog.findViewById(R.id.btn_cancel);
        mClose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverUrlUpdateDialog.cancel();// 关闭弹出框
            }
        });

        Button btnPwdSubmit = (Button) serverUrlUpdateDialog.findViewById(R.id.btn_submit);
        newServerUrlEt = (EditText) serverUrlUpdateDialog.findViewById(R.id.et_new_server_url);
        newServerUrlEt.setText(NetAPI.ESB_HOST);
        btnPwdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(newServerUrlEt.getText().toString())) {
                    NetAPI.ESB_HOST = newServerUrlEt.getText().toString().trim();
                }
                serverUrlUpdateDialog.cancel();// 关闭弹出框
            }
        });

    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.tb_password_show: {
                    if (isChecked) {
                        //普通文本框
                        etLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //密码框
                        etLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    etLoginPwd.postInvalidate();//刷新View
                    //将光标置于文字末尾
                    CharSequence charSequence = etLoginPwd.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }
                }
                break;

            }
        }
    };

    //***********************权限控制*********************************************

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private void checkPermission() {
        mPermissionsChecker = new PermissionsChecker(this);

        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        } else {
            // 全部权限都已获取
        }

    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {

        } else {
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }


    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityTaskCacheUtil.getIntance().clearActExceptLogin();
        startService(new Intent(this,RequestDictionaryService.class));
    }
}
