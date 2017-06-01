package com.lakala.shoudan.activity.login;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.encryption.Digest;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.GestrueType;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.shoudan.util.ScreenAdapter;
import com.lakala.ui.component.CircleImageView;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.module.lockpattern.LockPatternView;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置手势，输入手势界面
 * Created by ZhangMY on 2015/1/14.
 */
public class GestrueActivity extends FragmentActivity implements LockPatternView.OnPatternListener{

    private final String action = "com.lakala.shoudan.activity.login.GestrueActivity";

    private CircleImageView mCircleImageView;
    private LockPatternView mLockPatternView;
    private LockPatternView mLockPatternViewConfirm;

    //记录手势每个点的列表
    private List<LockPatternView.Cell> mPointList = new ArrayList<LockPatternView.Cell>();

    private TextView tvTopPromptText;

    protected GestrueType mGestrueType;

    private int loginCount = 5;//剩余登录次数,每次登录初始5次机会

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gesture);
        initView();
    }




    private void initView(){

        mGestrueType = (GestrueType)getIntent().getSerializableExtra(ConstValues.IntentKey.From);
        mCircleImageView = (CircleImageView)findViewById(R.id.id_avatar_img);
        mLockPatternViewConfirm = (LockPatternView)findViewById(R.id.lock_pattern_view_confirm);//隐藏手势确认
        tvTopPromptText = (TextView)findViewById(R.id.lock_pattern_top_prompt_text);
        mLockPatternViewConfirm.setEnabled(false);
        if(mGestrueType == GestrueType.RESET_GESTRUE){
        //重新设置
            findViewById(R.id.tv_skip).setVisibility(View.GONE);
            findViewById(R.id.back).setVisibility(View.VISIBLE);
            findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }else if(mGestrueType == GestrueType.LOGIN_GESTRUE){
            //手势登录
            //手势登录统计
//            StatisticManager.getInstance().onEvent(StatisticType.Login_2, GestrueActivity.this);

            findViewById(R.id.layout_gestrue_title).setVisibility(View.GONE);
//            findViewById(R.id.lock_pattern_top_prompt_text).setVisibility(View.INVISIBLE);

            mCircleImageView.setVisibility(View.INVISIBLE);
            mLockPatternViewConfirm.setVisibility(View.GONE);
            findViewById(R.id.lock_pattern_bottom_layout).setVisibility(View.VISIBLE);
        }else if(mGestrueType == GestrueType.SET_GESTRUE){
            ApplicationEx.getInstance().getUser().setGestureSkip();
        }


        mLockPatternView = (LockPatternView)findViewById(R.id.lock_pattern_view);
        mLockPatternView.setOnPatternListener(this);
        ScreenAdapter.lockViewAdapter(this, mLockPatternView);

        //跳过
        findViewById(R.id.tv_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipSetGestrue();
            }
        });
        //忘记手势密码s
        findViewById(R.id.lock_pattern_bottom_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //忘记手势密码统计
//                StatisticManager.getInstance().onEvent(StatisticType.Login_4, GestrueActivity.this);

                showResetGestureDialog();
            }
        });
        //切换账号
        findViewById(R.id.lock_pattern_bottom_right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登录其它账号统计
//                StatisticManager.getInstance().onEvent(StatisticType.Login_3, GestrueActivity.this);

                otherUserLogin();
            }
        });

    }

    private long lastClickTime = 0;

    public void doubleClickExit() {

        if(lastClickTime == 0){
            lastClickTime = System.currentTimeMillis();
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
            return;
        }

        final long interval = System.currentTimeMillis() - lastClickTime;

        com.lakala.library.util.LogUtil.print("Interval = " + interval);
        lastClickTime = System.currentTimeMillis();

        if(interval >2000){
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
        }else{

            ApplicationEx.getInstance().exit();
            finish();

        }

    }


    @Override
    public void onBackPressed() {

        if(mGestrueType == GestrueType.SET_GESTRUE){
            user.setGestureSkip();
            finish();
            return;
        }

        if(mGestrueType == GestrueType.RESET_GESTRUE){//屏蔽back 保留设置界面跳转过来的back
            finish();
            return;
        }

        doubleClickExit();
    }

    private void skipSetGestrue(){// 跳过
        user.updateGesturePwd("");
        toHomeActivity();
    }

    private User user = ApplicationEx.getInstance().getUser();

    private void otherUserLogin(){ // 其它账号登陆
        switchUser();
    }

    /**
     * 切换用户登录
     */
    private void switchUser() {
        clearGestureErrorCount();//清空登录次数

        ApplicationEx.getInstance().getUser().updateGesturePwd("");
        ApplicationEx.getInstance().getUser().retSkipGesture(false);
        LoginUtil.restartLogin(this);//设置重新登录
        finish();
    }

    /**
     * 手势密码登陆失败，清空手势密码信息，并返回
     */
    private void gestureLoginFail() {
        clearGestureErrorCount();

        ApplicationEx.getInstance().getUser().updateGesturePwd("");
        ApplicationEx.getInstance().getUser().retSkipGesture(false);
        LoginUtil.clearSession2Login(this);
        finish();
    }

    private void toHomeActivity(){
    //跳转到首页
//        BusinessLauncher.getInstance().start("home");
        finish();
    }

    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        if (pattern == null) return;

        if(mGestrueType == GestrueType.SET_GESTRUE || mGestrueType == GestrueType.RESET_GESTRUE){
            if (mPointList.size() <= 0) {
                //设置收拾密码不足四个点
                if (pattern.size() > 0 && pattern.size() < 4){
                    tvTopPromptText.setText(R.string.plat_set_gesture_password_error);
                    tvTopPromptText.setTextColor(Color.parseColor("#ff3e3e"));
                    mLockPatternView.clearPattern();
                    return;
                }
                //第一次设置成功
                tvTopPromptText.setText(R.string.plat_reset_gesture_password_prompt);
                tvTopPromptText.setTextColor(Color.WHITE);
                mPointList.clear();
                mPointList.addAll(pattern);
                mLockPatternView.clearPattern();
                mLockPatternViewConfirm.setPattern(LockPatternView.DisplayMode.Correct,mPointList);
                return;
            }

            if (mPointList.equals(pattern)) {
                tvTopPromptText.setText(R.string.plat_set_success);
                tvTopPromptText.setTextColor(Color.WHITE);

                //使用MD5加密存储手势密码
                StringBuilder cellSb = new StringBuilder();
                for (int i = 0; i < mPointList.size(); i++) {
                    LockPatternView.Cell gestureCell = mPointList.get(i);
                    cellSb.append(gestureCell.getRow()).append(gestureCell.getColumn());
                }
                User user = ApplicationEx.getInstance().getUser();
                user.updateGesturePwd(Digest.md5(cellSb.toString()));
//                LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_IGNORE_SET_GESTURE + user.getLoginName(), false);
                //设置不能忽略手势
                ToastUtil.toast(this,
                        getString(R.string.plat_set_success),
                        Toast.LENGTH_SHORT);
                //Toast 之后再返回 2000 对应Toast.LENGTH_SHORT
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);

//                if(mGestrueType == GestrueType.SET_GESTRUE){
//                    //手势密码设置成功
////                    if(getIntent().getBooleanExtra(ConstKey.NEW_GESTURE, false)){
////                        finish();
////                    }else{
////                        toHomeActivity();
////                    }
//
//                }else {
//                    //重新设置手势密码 直接回调到设置界面
//                    finish();
//                }
            } else {
                tvTopPromptText.setText(R.string.plat_reset_gesture_password_error);

                tvTopPromptText.setTextColor(Color.parseColor("#ff3e3e"));
                mPointList.clear();
                mLockPatternViewConfirm.clearPattern();
            }

            mLockPatternView.clearPattern();
        }else if(mGestrueType == GestrueType.LOGIN_GESTRUE){
            //手势密码登陆

            mPointList.clear();
            mPointList.addAll(pattern);
            //使用MD5加密存储手势密码
            StringBuilder cellSb = new StringBuilder();
            for (int i = 0; i < mPointList.size(); i++) {
                LockPatternView.Cell gestureCell = mPointList.get(i);
                cellSb.append(gestureCell.getRow()).append(gestureCell.getColumn());
            }
            User user = UserDao.getInstance().getLoginUser();
            //避免用户密码为null时的错误
            if(Digest.md5(cellSb.toString()).equals(user.getGesturePwd())){//手势密码正确
               clearGestureErrorCount();

               SaveUserLoginInfo.setLoginState(user);
               ApplicationEx.getInstance().getSession().setUser(user);
               //免登录
               finish();
               mLockPatternView.clearPattern();
            }else {//手势密码错误
                saveGestureErrorCount();
//                mLockPatternView.clearPattern();

                tvTopPromptText.setText(getResources().getString(R.string.gesture_error_and_you_have)+(5-getGestureErrorCount())+getResources().getString(R.string.chance));
                tvTopPromptText.setTextColor(Color.RED);

                if(getGestureErrorCount()>=5){

                    showFiveGestureErrorDialog(getString(R.string.plat_gesture_input_five_error));
                }

                //实现错误手势密码
                mPointList.clear();
                mPointList.addAll(pattern);
                mLockPatternView.setPattern(LockPatternView.DisplayMode.Wrong,mPointList);
                mLockPatternView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLockPatternView.clearPattern();
                    }
                },1500);

            }
        }
    }

    private void showFiveGestureErrorDialog(String msg){
        final AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(getResources().getString(R.string.jiaoyi_reminder));
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{getResources().getString(R.string.sure)});
        alertDialog.setCancelable(false);
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                alertDialog.dismiss();
                gestureLoginFail();

            }
        });
        alertDialog.show(getSupportFragmentManager());
    }

    private void showResetGestureDialog(){
        //忘记手势密码,是否去重新登录
        final AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(getResources().getString(R.string.plat_gesture_forgot_pwd));
        alertDialog.setMessage(getString(R.string.plat_if_login));
        alertDialog.setButtons(new String[]{getString(R.string.cancel),getResources().getString(R.string.sure)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch(index){
                    case 0:

                        break;
                    case 1:
                        gestureLoginFail();
                        break;
                }
                alertDialog.dismiss();

            }
        });
        alertDialog.show(getSupportFragmentManager());
    }


    private int getGestureErrorCount(){
        return LklPreferences.getInstance().getInt(ConstValues.IntentKey.KEY_GESTURE_ERROR_COUNT);
    }

    private void saveGestureErrorCount(){
        LklPreferences.getInstance().putInt(ConstValues.IntentKey.KEY_GESTURE_ERROR_COUNT,getGestureErrorCount() + 1);
    }

    private void clearGestureErrorCount(){
        LklPreferences.getInstance().remove(ConstValues.IntentKey.KEY_GESTURE_ERROR_COUNT);
    }

}
