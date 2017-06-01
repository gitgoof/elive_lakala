package com.lakala.shoudan.activity.accountmanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.util.IMEUtil;
import com.lakala.ui.common.NextStepTextWatcherControl;
import com.lakala.ui.component.LabelEditText;

import java.util.HashMap;

/**
 * Created by ZhangMY on 2015/3/11.
 * 忘记密码和注册设置密码流程相同，请求接口不同
 */
public abstract class InputPasswordModuleActivity extends AppBaseActivity{

    protected LabelEditText editPassword;
    protected LabelEditText editPasswordConfirm;
    private TextView btnSure;

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initView();
    }

    private void initView(){

        editPassword = (LabelEditText)findViewById(R.id.label_edit_password);
        editPasswordConfirm = (LabelEditText)findViewById(R.id.label_edit_confirm_password);

        editPassword.setEnableOnClickItemEvents(true);

//        mBackRelativeLayout = (BackLinearLayout) findViewById(R.id.back_layout);
//        mBackRelativeLayout.setmSearchActivity(this);
//        mBackRelativeLayout.setTag(true);
//        mBackRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                if((Boolean)mBackRelativeLayout.getTag()){
//
//                    View backImage = findViewById(R.id.layout_navication_bar);
//
//                    ScreenUtil.getScrrenWidthAndHeight(InputPasswordModuleActivity.this);
//                    int height = Parameters.screenHeight-Parameters.statusBarHeight-backImage.getHeight();//marginTop的大小
//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBackRelativeLayout.getLayoutParams();
//                    params.height = height;
//
//                    mBackRelativeLayout.setLayoutParams(params);
//
//                    View layoutBottom = findViewById(R.id.layout_bottom);
//                    LinearLayout.LayoutParams bottomParam = (LinearLayout.LayoutParams) layoutBottom.getLayoutParams();
//                    bottomParam.topMargin = Parameters.screenHeight-Parameters.statusBarHeight-backImage.getHeight()-layoutBottom.getHeight();
//                    layoutBottom.setLayoutParams(bottomParam);
//
//                    mBackRelativeLayout.setTag(false);
//
//
//                    backLayoutY = Parameters.statusBarHeight+backImage.getHeight()+bottomParam.topMargin;
//                }
//
//            }
//        });

        btnSure = (TextView) findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitPassword();
            }
        });
        TextView protoct = (TextView) findViewById(R.id.tv_protoct);
        protoct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProtol();
            }
        });

        HashMap<EditText,Integer> editMap = new HashMap<EditText,Integer>();
        editMap.put(editPassword.getEditText(),getResources().getInteger(R.integer.min_password_limit));
        editMap.put(editPasswordConfirm.getEditText(),getResources().getInteger(R.integer.min_password_limit));
        new NextStepTextWatcherControl(editMap,btnSure);
    }

    private void commitPassword(){
        if(inputValidate()){
           commit();
        }
    }

    @Override
    protected boolean isRequired2Login() {
        return false;
    }

    private void showProtol(){

        ProtocalActivity.open(this, ProtocalType.SERVICE_PROTOCAL);

    }

    protected abstract void commit();

    /**
     * 输入内容校验
     * @return
     */
    private boolean inputValidate(){
        String password = editPassword.getEditText().getText().toString().trim();
        StringUtil.PasswordLvl passwordLvl = StringUtil.checkPWLevel(password, getResources().getInteger(R.integer.max_password_limit), getResources().getInteger(R.integer.min_password_limit));

        switch (passwordLvl){

            case SIMPLE:
                ToastUtil.toast(this, getString(R.string.password_is_too_simple),Toast.LENGTH_SHORT);
                break;
            case LENGTH_ERROR:
                ToastUtil.toast(this, getString(R.string.plat_plese_input_your_password_error),Toast.LENGTH_SHORT);
                break;
            case CHAR_REPEAT_4_TIMES:
                ToastUtil.toast(this, getString(R.string.password_chars_repeat),Toast.LENGTH_SHORT);
                break;
            case EMPTY:
                ToastUtil.toast(InputPasswordModuleActivity.this,getString(R.string.input_lakala_password),Toast.LENGTH_SHORT);
                break;
            case NORMAL:
                break;
        }

        if(passwordLvl != StringUtil.PasswordLvl.NORMAL){
            return false;
        }


        String passWordConfirm = editPasswordConfirm.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(passWordConfirm)){
            ToastUtil.toast(InputPasswordModuleActivity.this,getString(R.string.managepwd_input_login_password_again),Toast.LENGTH_SHORT);
            return false;
        }

        if(!password.equals(passWordConfirm)){
            ToastUtil.toast(InputPasswordModuleActivity.this,getString(R.string.managepwd_input_confirm_login_password_vaild),Toast.LENGTH_SHORT);
            return false;
        }

        //注册 需要判断是否已经同意了服务协议
        if(!((CheckBox)findViewById(R.id.cb_agreen_pro)).isChecked()){
            ToastUtil.toast(InputPasswordModuleActivity.this,getString(R.string.please_check_agreen),Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(mPopupWindow != null){
            IMEUtil.hideIme(this);
            dismissPopupwindow();
            return ;
        }

        int[] location = new int[2];
        findViewById(R.id.layout_bottom).getLocationOnScreen(location);
        super.onBackPressed();
    }

    private void dismissPopupwindow(){
        if(null != mPopupWindow){
            mPopupWindow.setFocusable(true);
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

}
