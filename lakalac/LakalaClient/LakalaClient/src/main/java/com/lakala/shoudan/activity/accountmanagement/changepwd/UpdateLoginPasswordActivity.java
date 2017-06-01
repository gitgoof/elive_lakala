package com.lakala.shoudan.activity.accountmanagement.changepwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.ui.component.LabelEditText;

import butterknife.OnClick;

/**
 * Created by HUASHO on 2015/1/14.
 * 设置-密码管理-修改登入密码
 */
public class UpdateLoginPasswordActivity extends AppBaseActivity {

    private LabelEditText cetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_update_login_password);
        initUI();
    }

    /**
     * 初始化UI
     */
    protected void initUI(){
        navigationBar.setTitle(getString(R.string.managepwd_change_loginpwd));
        cetPassword = (LabelEditText) findViewById(R.id.edit_password);
        cetPassword.getEditText().setTag(cetPassword.getEditText().getHint());
//        cetPassword.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(view instanceof EditText){
//                    EditText editText = (EditText) view;
//                    if(b){
//                       editText.setHint("");
//                    }else {
//                        editText.setHint((String)editText.getTag());
//                    }
//                }
//            }
//        });
    }

    //下一步
    @OnClick(R.id.btn_next) void next(){
        if(verifyPassword()){
            Intent intent = new Intent(UpdateLoginPasswordActivity.this, InputNewPasswordActivity.class);
            intent.putExtra(ConstValues.IntentKey.Password, CommonEncrypt.loginEncrypt(cetPassword.getEditText().getText().toString().trim()));
            startActivity(intent);
        }
    }

    /**
     * 验证原始密码是否正确
     * @return
     */
    private boolean verifyPassword(){
        String oriPassword = cetPassword.getEditText().getText().toString().trim();

        if(TextUtils.isEmpty(oriPassword)){
            ToastUtil.toast(UpdateLoginPasswordActivity.this,getString(R.string.input_lakala_password), Toast.LENGTH_SHORT);
            return false;
        }
        int len = oriPassword.length();
        if (len < getResources().getInteger(R.integer.min_password_limit) || len > getResources().getInteger(R.integer.max_password_limit)) {
            ToastUtil.toast(this, getString(R.string.plat_plese_input_your_password_error),Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
}
