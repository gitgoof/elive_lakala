package com.lakala.shoudan.activity.accountmanagement;

import android.widget.EditText;

import com.lakala.shoudan.R;
import com.lakala.shoudan.util.IMEUtil;
import com.lakala.ui.common.NextStepTextWatcherControl;

import java.util.HashMap;

/**
 * Created by ZhangMY on 2015/3/11.
 */
public abstract class InputPhoneGetSmsModuleActivity extends GetSmsModuleActivity {

    protected abstract void resetPassword();

    protected int backLayoutY;

    @Override
    protected void initSpecialView() {
        HashMap<EditText,Integer> editMap = new HashMap<EditText,Integer>();
        editMap.put(editPhone.getEditText(),11);
        editMap.put(mCountDownInputBoxView.getVerifycodeEdit(),6);
        new NextStepTextWatcherControl(editMap,btnNext);

        int dimenTwenty = (int)getResources().getDimension(R.dimen.dimen_20);
        editPhone.getEditText().setPadding(0,dimenTwenty,0,dimenTwenty);
        mCountDownInputBoxView.getVerifycodeEdit().setPadding((int)getResources().getDimension(R.dimen.dimen_20),dimenTwenty,0,dimenTwenty);
        editPhone.setTag(true);


//        mBackRelativeLayout = (BackLinearLayout) findViewById(R.id.back_layout);
//        mBackRelativeLayout.setmSearchActivity(this);
//        mBackRelativeLayout.setTag(true);
//        mBackRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                if((Boolean)mBackRelativeLayout.getTag()){
//
//                    View backImage = findViewById(R.id.back);
//
//                    ScreenUtil.getScrrenWidthAndHeight(InputPhoneGetSmsModuleActivity.this);
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

    }

    @Override
    protected boolean isInputValidate() {
        if(!validatePhoneNum()){
            return false;
        }
        if(!validateSmscodeAndToken()){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mPopupWindow != null){
            IMEUtil.hideIme(this);
            dismissPopupwindow();
            return ;
        }

        int[] location = new int[2];
        findViewById(R.id.layout_bottom).getLocationOnScreen(location);
        if(backLayoutY != location[1]){
            IMEUtil.hideIme(this);
            return ;
        }
        super.onBackPressed();
    }

}
