package com.lakala.elive.user.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.beans.VersionInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.DownLoadUtil;
import com.lakala.elive.common.utils.EncodeUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.user.activity.UserLoginActivity;
import com.lakala.elive.user.base.BaseFragment;

public class SettingFragment extends BaseFragment {
	
	private TextView userNameTv;

    Dialog pwdUpdateDialog;

    private RelativeLayout logoutBtn;
	private RelativeLayout pwdUpdateBtn;
	private RelativeLayout versionCheckBtn;
    private RelativeLayout serverUrlUpdateBtn;
    private TextView organName;

    @Override
    protected void setViewLayoutId() {
        this.viewLayoutId = R.layout.fragment_user_setting;
    }

    @Override
    protected void bindViewIds() {
        //用户名称
        userNameTv =  (TextView) mView.findViewById(R.id.setting_tv_user_name);
        organName =  (TextView) mView.findViewById(R.id.setting_tv_organ_name);
        //退出按钮
        logoutBtn =  (RelativeLayout)mView.findViewById(R.id.rl_setting_logout);
        logoutBtn.setOnClickListener(this);
        //修改密码
        pwdUpdateBtn =  (RelativeLayout)mView.findViewById(R.id.rl_update_pwd);
        pwdUpdateBtn.setOnClickListener(this);
        //版本检查
        versionCheckBtn =  (RelativeLayout)mView.findViewById(R.id.rl_setting_version);
        versionCheckBtn.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        userNameTv.setText(
                Session.get(getActivity()).getUserLoginInfo().getUserName()
                        + "(" +Session.get(getActivity()).getUserLoginInfo().getUserId() + ")"
        );

        organName.setText(Session.get(getActivity()).getUserLoginInfo().getOrganName());
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.rl_setting_logout:
                showLogoutDialog();
				break;
			case R.id.rl_setting_version:
                //服务器获取最新版本信息
                UserReqInfo reqInfo = new UserReqInfo();
                reqInfo.setPlatformType("ANDROID");
                NetAPI.getAppVersionCode(getActivity(), this, reqInfo);
				break;
			case R.id.rl_update_pwd:
                showUpdatePwdDialog();
				break;
			default:
				break;
		}
	}

    Dialog logoutDialog;

    private void showLogoutDialog() {
        logoutDialog = new Dialog(getActivity(), R.style.edit_AlertDialog_style);
        logoutDialog.setContentView(R.layout.dialog_logout);
        logoutDialog.show();

        logoutDialog.setCanceledOnTouchOutside(false); // Sets whether this dialog is the window's bounds.

        Window w = logoutDialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        logoutDialog.onWindowAttributesChanged(lp);

        Button btnClose = (Button) logoutDialog.findViewById(R.id.btn_cancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();// 关闭弹出框
            }
        });

        Button btnChange = (Button) logoutDialog.findViewById(R.id.btn_logout_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();// 关闭弹出框
                //跳转到主界面
                Intent intent=new Intent();
                intent.setClass(getActivity(), UserLoginActivity.class);
                startActivity(intent);
            }
        });

        Button btnExit = (Button) logoutDialog.findViewById(R.id.btn_logout_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();// 关闭弹出框
                getActivity().finish();
                System.exit(0);
            }
        });
    }
    private EditText newPwdEt;
    private EditText newTowPwdEt;

    private LinearLayout llPwdCheckAlert;//密码强度提示

    private TextView tvPwdCheckLow;
    private TextView tvPwdCheckMiddle;
    private TextView tvPwdCheckHigh;

    public void showUpdatePwdDialog(){
        pwdUpdateDialog = new Dialog(getActivity(), R.style.edit_AlertDialog_style);
        pwdUpdateDialog.setContentView(R.layout.dialog_update_pwd);
        pwdUpdateDialog.show();
        pwdUpdateDialog.setCanceledOnTouchOutside(false); // Sets whether this dialog is the window's bounds.

        llPwdCheckAlert = (LinearLayout) pwdUpdateDialog.findViewById(R.id.ll_pwd_check_alert);
        tvPwdCheckLow = (TextView) pwdUpdateDialog.findViewById(R.id.tv_pwd_check_low);
        tvPwdCheckMiddle = (TextView) pwdUpdateDialog.findViewById(R.id.tv_pwd_check_middle);
        tvPwdCheckHigh = (TextView) pwdUpdateDialog.findViewById(R.id.tv_pwd_check_high);

        Window w = pwdUpdateDialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        pwdUpdateDialog.onWindowAttributesChanged(lp);


        Button mClose_btn = (Button) pwdUpdateDialog.findViewById(R.id.btn_cancel);
        mClose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdUpdateDialog.cancel();// 关闭弹出框
            }
        });

        Button btnPwdSubmit = (Button) pwdUpdateDialog.findViewById(R.id.login_btn_submit);
        newPwdEt = (EditText) pwdUpdateDialog.findViewById(R.id.login_new_pwd);
        newTowPwdEt = (EditText) pwdUpdateDialog.findViewById(R.id.login_two_new_pwd);

        //文本编辑框的处理事件
        newPwdEt.addTextChangedListener(textWatcher);


        btnPwdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwdStr = newPwdEt.getText().toString().trim();
                String newTwoPwdStr = newTowPwdEt.getText().toString().trim();

                Log.i("btnPwdSubmit", "pwd:" + newPwdStr +":" + newTwoPwdStr);
                if( TextUtils.isEmpty(newPwdStr.trim()) || TextUtils.isEmpty(newTwoPwdStr.trim()) ){
                    Utils.makeEventToast(getActivity(), "修改密码不能为空！",false);
                    return;
                }else{
                    if(!newPwdStr.trim().equals(newTwoPwdStr.trim())){
                        Utils.makeEventToast(getActivity(), "输入密码不一致！",false);
                        return;
                    }else{
                        if( Utils.passwordStrong(newPwdStr).equals("弱") || newPwdStr.length() < 6){
                            Utils.showToast(getActivity(), "密码设置强度太弱!");
                            return;
                        }else{
                            UserReqInfo reqInfo = new UserReqInfo();
                            reqInfo.setAuthToken(Session.get(getActivity()).getUserLoginInfo().getAuthToken());
                            reqInfo.setPassword(EncodeUtil.encodeBySha(newPwdStr.trim()));
                            NetAPI.userPwdsetSubmit(getActivity(), SettingFragment.this,reqInfo);
                        }

                    }
                }
                pwdUpdateDialog.cancel();// 关闭弹出框
            }
        });

    }
    /**
     *
     * 文本编辑框的处理事件
     *
     */
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() >= 5){
                if(Utils.passwordStrong(s.toString()).equals("弱")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.pwd_check_low);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
                }else if(Utils.passwordStrong(s.toString()).equals("中")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.pwd_check_middle);
                    tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
                }else if(Utils.passwordStrong(s.toString()).equals("强")){
                    llPwdCheckAlert.setVisibility(View.VISIBLE);
                    tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                    tvPwdCheckHigh.setBackgroundResource(R.color.pwd_check_high);
                };
            }else {
                llPwdCheckAlert.setVisibility(View.GONE);
                tvPwdCheckLow.setBackgroundResource(R.color.darkgray);
                tvPwdCheckMiddle.setBackgroundResource(R.color.darkgray);
                tvPwdCheckHigh.setBackgroundResource(R.color.darkgray);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

	@Override
	public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_PWD_SET_SUBMIT:
                Utils.makeEventToast(getActivity(), "密码修改成功！",false);
                //跳转到主界面
                Intent intent=new Intent();
                intent.setClass(getActivity(), UserLoginActivity.class);
                startActivity(intent);
                break;
            case NetAPI.ACTION_GET_APP_VESION_CODE:
                Log.i("ACTION_GET_VERSION_INFO", "onSuccess");
                checkVersion((VersionInfo) obj);
                break;
        }
	}

	@Override
	public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_PWD_SET_SUBMIT:
                Utils.showToast(getActivity(), "密码修改失败:" + statusCode + "！");
                break;
            case NetAPI.ACTION_GET_APP_VESION_CODE:
                Utils.showToast(getActivity(), "版本检查失败:" + statusCode + "！");
                break;
        }
	}

    private VersionInfo versionInfo ;
    //进度条
    private ProgressDialog mProgressDialog;

    private void checkVersion(VersionInfo versionInfo) {
        if(versionInfo.getVersionCode() > Session.get(getActivity()).getVersionCode()){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
            mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            mProgressDialog.setTitle("新版本更新...");
            mProgressDialog.show();//设置进度的显示
            DownLoadUtil.downLoadApkFile(versionInfo.getUrl(), Constants.saveFileName,getActivity(),mProgressDialog);
        }else{
            Utils.showToast(getActivity(), "当前为最新版本！");
        }
    }


}
