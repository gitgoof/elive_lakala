package com.lakala.platform.swiper.mts;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.swiper.mts.paypwd.PayPwdInputView;
import com.lakala.platform.swiper.mts.paypwd.PayPwdProcess;
import com.lakala.ui.common.CommmonSelectData;
import com.lakala.ui.common.CommonSelectListAdapter;
import com.lakala.ui.common.ListUtil;
import com.lakala.ui.common.MuliSelectAdapter;
import com.lakala.ui.component.ClearEditText;
import com.lakala.ui.dialog.mts.ActionSheetDialog;
import com.lakala.ui.dialog.mts.AlertDialog;
import com.lakala.ui.dialog.mts.BaseDialog;
import com.lakala.ui.dialog.mts.DialogUtil;
import com.lakala.ui.dialog.mts.ProgressDialog;

import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Dialog控制器
 * 管理对话框的显示与清除
 * Created by xyz on 14-1-4.
 */
public class DialogController implements DialogInterface.OnDismissListener,ProgressDialog.OnDialogKeyListener {

    private static DialogController instance;

    //保证只有一个对话框实例存在
    private BaseDialog dialogFragment;

    private DialogConfirmClick mDialogConfirmClick;

    //当前对话框所在的页面
    private Activity currentActivity;

    private Map<Activity,BaseDialog> dialogMap = new WeakHashMap<>();

    //上一个对话框弹出后，是否设置了阻塞模式，如果设置，则后续对话框不能弹出
    private boolean isBlocked = false;

    //自定义View布局参数
    private LinearLayout.LayoutParams mLayoutParameters = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    private DialogController(){

    }

    public static DialogController getInstance(){
        if (instance == null){
            instance = new DialogController();
        }

        return  instance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    /**
     * 设置对话框标题
     * @param title
     */
    public void setTitle(String title){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setTitle(title);
    }

    /**
     * 设置对话框标题
     * @param titleId
     */
    public void setTitle(int titleId){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setTitle(titleId);
    }

    /**
     * 设置对话框消息内容
     * @param message
     */
    public void setMessage(String message){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setMessage(message);
    }

    /**
     * 设置对话框中间的view
     * @param middleView
     */
    public void setMiddleView(View middleView){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setMiddleView(middleView);
    }

    /**
     * 设置按钮文字
     * @param buttonTypeEnum
     * @param text
     */
    public void setButtonText(AlertDialog.Builder.ButtonTypeEnum buttonTypeEnum,String text){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setButtonText(buttonTypeEnum, text);
    }

    /**
     * 设置按钮是否可用
     * @param buttonTypeEnum
     * @param isEnable 按钮是否可用
     */
    public void setButtonEnable(AlertDialog.Builder.ButtonTypeEnum buttonTypeEnum,boolean isEnable){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setButtonEnable(buttonTypeEnum, isEnable);
    }

    /**
     * 设置按钮是否可见
     * @param buttonTypeEnum
     * @param visibility 按钮是否可见
     */
    public void setButtonVisable(AlertDialog.Builder.ButtonTypeEnum buttonTypeEnum,int visibility){
        if (!(dialogFragment instanceof AlertDialog)) return;

        AlertDialog alertDialog = (AlertDialog)dialogFragment;

        alertDialog.setButtonVisiblity(buttonTypeEnum, visibility);
    }

    /**
     * 显示一个提示对话框，默认一个确定按钮，点击后取消对话框显示
     * @param activity  对话框依赖的Activity
     * @param message   消息提示内容
     */
    public void showAlertDialog(FragmentActivity activity,String message){

        showAlertDialog(activity,"", message);
    }

    /**
     * 显示一个带title的消息提示框
     * @param activity
     * @param title
     * @param message
     */
    public void showAlertDialog(FragmentActivity activity,String title,String message){

        if (!isCouldShow(activity)) return;

        String middleButtonText = activity.getResources().getString(R.string.com_confirm);

        showAlertDialog(activity, 0, title, message, "", "", middleButtonText, defaultDialogClickListener, false);

    }

    /**
     * 显示一个带title的消息提示框
     * @param activity
     * @param title
     * @param message
     */
    public void showAlertDialog(FragmentActivity activity,String title,String message,AlertDialog.Builder.AlertDialogClickListener clickListener){
        if (!isCouldShow(activity)) return;
        String middleButtonText = activity.getResources().getString(R.string.com_confirm);

        showAlertDialog(activity, 0, title, message, "", "", middleButtonText, clickListener, false);
    }

    /**
     * 显示一个消息对话框,可以定制确认按钮文字和事件
     * @param activity
     * @param title
     * @param message
     * @param middleButtonText  默认为“确定”
     * @param alertDialogClickListener
     */
    public void showAlertDialog(FragmentActivity activity,
                                String title,
                                String message,
                                String middleButtonText,
                                AlertDialog.Builder.AlertDialogClickListener alertDialogClickListener){
        if (!isCouldShow(activity)) return;

        if (StringUtil.isEmpty(middleButtonText)){
            middleButtonText = activity.getResources().getString(R.string.com_confirm);
        }

        showAlertDialog(activity, 0, title, message, "", "", middleButtonText, alertDialogClickListener, false);

    }

    /**
     * 标准对话框  title + message + 两个按钮
     * @param context
     * @param title
     * @param message
     * @param leftButtonText
     * @param rightButtionText
     * @param listener
     */
    public void showAlertDialog(FragmentActivity context,
                                String title,
                                String message,
                                String leftButtonText,
                                String rightButtionText,
                                AlertDialog.Builder.AlertDialogClickListener listener){
        if (!isCouldShow(context)) return;

        showAlertDialog(context, 0, title, message, leftButtonText, rightButtionText, null, listener, false);
    }

    /**
     * 全参构建AlertDialog并显示
     * @param activity
     * @param titleIconId
     * @param title
     * @param message
     * @param leftButtonText
     * @param rightButtonText
     * @param middleButtonText
     * @param clickListener
     */
    public void showAlertDialog(FragmentActivity activity,
                                int titleIconId,
                                String title,
                                String message,
                                String leftButtonText,
                                String rightButtonText,
                                String middleButtonText,
                                AlertDialog.Builder.AlertDialogClickListener clickListener,
                                boolean cancelable){
        if (!isCouldShow(activity)) return;
        dismiss();
        dialogFragment = DialogUtil.createAlertDialog(activity.getSupportFragmentManager(),
                titleIconId,
                title,
                message,
                leftButtonText,
                rightButtonText,
                middleButtonText,
                clickListener);
        dialogFragment.setCancelable(cancelable);
        dialogFragment.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mDialogConfirmClick!=null){
                    mDialogConfirmClick.onDialogCancelClick();
                }
            }
        });
        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);
        if (StringUtil.isNotEmpty(rightButtonText)) setButtonVisable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, View.VISIBLE);
        show(dialogFragment);
    }

    /**
     * 全参构建AlertDialog并显示
     * @param activity
     * @param titleIconId
     * @param title
     * @param middleView
     * @param leftButtonText
     * @param rightButtonText
     * @param middleButtonText
     * @param clickListener
     */
    public void showAlertDialog(FragmentActivity activity,
                                int titleIconId,
                                String title,
                                View middleView,
                                String leftButtonText,
                                final String rightButtonText,
                                String middleButtonText,
                                AlertDialog.Builder.AlertDialogClickListener clickListener,
                                boolean cancelable){
        showAlertDialog(activity, titleIconId, title, middleView, leftButtonText,
                rightButtonText, middleButtonText, clickListener, cancelable,true);

    }

    /**
     * 全参构建AlertDialog并显示
     * @param cancelable 点击对话框之外的区域，是否可以取消对话框
     * @param isCouldAutoDismiss 是否可以自动取消对话框
     */
    public void showAlertDialog(FragmentActivity activity,
                                int titleIconId,
                                String title,
                                View middleView,
                                String leftButtonText,
                                final String rightButtonText,
                                String middleButtonText,
                                AlertDialog.Builder.AlertDialogClickListener clickListener,
                                boolean cancelable,
                                boolean isCouldAutoDismiss){

        if (!isCouldShow(activity)) return;

        dismiss();

        AlertDialog alertDialog = DialogUtil.createAlertDialog(activity.getSupportFragmentManager(),
                titleIconId,
                title,
                "",
                leftButtonText,
                rightButtonText,
                middleButtonText,
                clickListener);
        alertDialog.setMiddleView(middleView, mLayoutParameters);
        dialogFragment = alertDialog;
        dialogFragment.setCancelable(cancelable);
        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);
        alertDialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mDialogConfirmClick != null) {
                    mDialogConfirmClick.onDialogCancelClick();
                }
            }
        });

        alertDialog.setCouldAutoDismiss(isCouldAutoDismiss);

        if (StringUtil.isNotEmpty(rightButtonText)) setButtonVisable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, View.VISIBLE);
        show(dialogFragment);
    }

    /**
     * 提供刷卡统一调用
     */
    public void showProgress(FragmentActivity activity){

        showProgressDialog(activity,"正在识别设备...");

    }

    /**
     * 显示一个进度提示对话框,无进度提示文字
     * @param activity
     */
    public void showProgressDialog(FragmentActivity activity){

        showProgressDialog(activity,"");

    }

    /**
     * 显示一个进度对话框，包含进度提示文字
     * @param activity
     * @param loadingMessage 进度提示文本
     */
    public void showProgressDialog(FragmentActivity activity,String loadingMessage){
        if (!isCouldShow(activity)) return;
        dismiss();
        dialogFragment = DialogUtil.createProgressDialog(activity.getSupportFragmentManager(),loadingMessage);
        ProgressDialog progressDialog = (ProgressDialog)dialogFragment;
        progressDialog.setOnDismissListener(this);
        progressDialog.setOnDialogKeyListener(this);
        show(dialogFragment);
    }

    /**
     * 对话框显示之前预处理
     */
    private boolean isCouldShow(Activity activity){
        currentActivity = activity;
        return activity != null && !activity.isFinishing() && !isBlocked;
    }

    private void show(BaseDialog dialogFragment){
        dialogMap.put(currentActivity,dialogFragment);
        dialogFragment.show();
    }

    /**
     * 取消对话框显示
     */
    public void dismiss(){

        if (isBlocked){
            //如果当前对话框被锁定，直接return
            return;
        }

        //java.lang.IllegalStateException: Activity has been destroyed
        //Crash 报出上述异常，再次做下述判断，Activity没有消失之前，对对话框进行取消操作
        if (currentActivity == null || currentActivity.isFinishing()) {
            return;
        }

        if (dialogMap == null || dialogMap.size() ==0)  return;

        BaseDialog baseDialog = dialogMap.get(currentActivity);

        if (baseDialog == null){
            return;
        }

        //如果对话框设置为不可自动取消，则不取消此对话框
        if (!baseDialog.isCouldAutoDismiss()){
            return;
        }

        //java.lang.NullPointerException DialogFragment 184行，v4包中报空指针，try catch下，避免crash
        try {
            baseDialog.dismissAllowingStateLoss();
            dialogMap.remove(baseDialog);
            baseDialog = null;
        }catch (Exception e){
            CrashlyticsUtil.logException(e);
        }

    }

    /**
     * 默认对话框点击事件处理
     */
    private AlertDialog.Builder.AlertDialogClickListener defaultDialogClickListener = new AlertDialog.Builder.AlertDialogClickListener() {
        @Override
        public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
            switch (typeEnum){
                case LEFT_BUTTON:
                    break;
                case RIGHT_BUTTON:
                    break;
                case MIDDLE_BUTTON:
                    alertDialog.dismiss();
                    break;
            }
        }
    };


    /**
     * 输入登录密码对话框
     * @param activity
     * @param title                 标题
     * @param minLength              输入框最小输入长度
     * @param maxLength              输入框最大输入长度
     * @param isNeedEncrypt         是否加密输入内容
     * @param dialogConfirmClick    点击回调事件
     */
    public void showLoginPasswordDialog(final FragmentActivity activity,
                                        String title,
                                        String message,
                                        String hint,
                                        String okString,
                                        final int minLength,
                                        int maxLength,
                                        final boolean isNeedEncrypt,
                                        final boolean isShowFindPw,
                                        DialogConfirmClick dialogConfirmClick){

//        setDialogConfirmClick(dialogConfirmClick);
//
//        LinearLayout layout = (LinearLayout) RelativeLayout.inflate(activity, R.layout.plat_payment_input, null);
//        TextView titleText = (TextView)layout.findViewById(R.id.id_title_text);
//        if (StringUtil.isNotEmpty(title)) titleText.setVisibility(View.VISIBLE);
//        titleText.setText(title);
//        TextView messageText = (TextView)layout.findViewById(R.id.id_message);
//        if (StringUtil.isEmpty(message)) messageText.setVisibility(View.GONE);
//        messageText.setText(message);
//        TextView missText = (TextView)layout.findViewById(R.id.miss_pw);
//        if (!isShowFindPw) missText.setVisibility(View.INVISIBLE);
//
//        final ClearEditText passwordInputView = (ClearEditText)layout.findViewById(R.id.id_password);
//        if (StringUtil.isEmpty(hint)){
//            passwordInputView.setHint(activity.getResources().getString(R.string.plat_input_pay_password_null));
//        }else {
//            passwordInputView.setHint(hint);
//        }
//        passwordInputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        passwordInputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
//
//        if (StringUtil.isEmpty(okString)){
//            okString = "确定";
//        }
//        showAlertDialog(activity, 0,"",layout,"取消",okString,null,new AlertDialog.Builder.AlertDialogClickListener() {
//            @Override
//            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON) {
//                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(passwordInputView.getWindowToken(), 0);
//                    dismiss();
//                    mDialogConfirmClick.onDialogCancelClick();
//                }
//                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON) {
//                    String data = passwordInputView.getText().toString();
//                    if (data.length() < minLength) {
//                        ToastUtil.toast(activity, String.format(activity.getString(R.string.plat_string_length_not_valid), minLength));
//                    } else {
//                        if (isNeedEncrypt) {
//                            data = CommonEncrypt.loginEncrypt(data);
//                        }
//                        mDialogConfirmClick.onDialogConfirmClick(data);
//                        dismiss();
//                    }
//                }
//            }
//        },false);
//
//        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);
//
//        passwordInputView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().length() >= minLength){
//                    setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);
//                }else {
//                    setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);
//                }
//            }
//        });
//
//        missText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDialogConfirmClick.onDialogCancelClick();
//                dismiss();
//                activity.startActivity(new Intent(activity, ForgetPasswordActivity.class));
//            }
//        });
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                passwordInputView.requestFocus();
//                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        },500);

    }

    /**
     * 。输入支付密码对话框
     *
     * @param activity
     * @param title              标题
     * @param isNeedEncrypt      .是否加密输入内容
     * @param confirmClick 点击回调事件
     */
    public void showPayPasswordDialog(final FragmentActivity activity,
                                      final String title,
                                      final String message,
                                      final boolean isNeedEncrypt,
                                      final boolean isShowFindPw,
                                      final DialogConfirmClick confirmClick) {

        LinearLayout container = (LinearLayout) View.inflate(activity, R.layout.plat_password_input, null);

        TextView tvTitle = (TextView) container.findViewById(R.id.id_title_text);
        tvTitle.setText(title);
        if (StringUtil.isEmpty(title)){
            tvTitle.setVisibility(View.GONE);
        }

        TextView tvMessage = (TextView) container.findViewById(R.id.id_message);
        tvMessage.setText(message);
        if (StringUtil.isEmpty(message)) {
            tvMessage.setVisibility(View.GONE);
        }

        View toFind = container.findViewById(R.id.miss_pw);
        toFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PayPwdProcess.findPayPassword(activity);
            }
        });
        if (!isShowFindPw){
            toFind.setVisibility(View.GONE);
        }

        final PayPwdInputView passwordView = (PayPwdInputView) container.findViewById(R.id.id_password);
        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                boolean enable = s != null && s.length() == 6;
                DialogController.this.setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, enable);
                if (enable) {
                    passwordView.closeCKbd();

                }
            }
        });

        showAlertDialog(activity, 0, "", container, "取消", "确定", null, new AlertDialog.Builder.AlertDialogClickListener() {

            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                dismiss();
                switch (typeEnum) {
                    case LEFT_BUTTON:
                        if (confirmClick != null) {
                            confirmClick.onDialogCancelClick();
                        }
                        break;
                    case RIGHT_BUTTON:
                        String data = passwordView.getPassword();
                        if (isNeedEncrypt) {
                            data = PayPwdProcess.encryptPayPassword(data);
                        }
                        if (confirmClick != null) {
                            confirmClick.onDialogConfirmClick(data);
                        }
                        break;
                }
            }
        }, false);

        this.setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);

        passwordView.openCKbd();
    }

    /**
     *  显示图片的Dialog
     * @param titleString                标题
     * @param bottomString               底部标题
     * @param imgDrawable                图片
     * @param buttonString               按钮文字
     * @param showInput                  是否显示文本编辑框
     * @param dialogConfirmClick         回调
     * */
    public void showImageDialog(final FragmentActivity activity,
                                String titleString,
                                String bottomString,
                                int imgDrawable,
                                String buttonString,
                                String showInput,
                                DialogConfirmClick dialogConfirmClick){

//        setDialogConfirmClick(dialogConfirmClick);
//
//        LinearLayout layout =(LinearLayout) View.inflate(activity, R.layout.plat_image_dialog, null);
//
//        Bitmap bitmap = null;
//        try{
//            bitmap =   BitmapFactory.decodeResource(activity.getResources(), imgDrawable);
//        }catch (Exception e){
//            LogUtil.print(e.getMessage());
//            return ;
//        }
//
//        if(null == bitmap){
//            return ;
//        }
//
//        //如果图片的高度大于270dp,则图片可以支持滑动
//        if(bitmap.getHeight() > DimenUtil.dp2px(activity, 270)){
//               layout.findViewById(R.id.image_dialog_scroolview).setVisibility(View.VISIBLE);
//               layout.findViewById(R.id.id_image).setVisibility(View.GONE);
//               layout.findViewById(R.id.id_scroll_image).setBackgroundResource(imgDrawable);
//        }else{
//            layout.findViewById(R.id.image_dialog_scroolview).setVisibility(View.GONE);
//            layout.findViewById(R.id.id_image).setVisibility(View.VISIBLE);
//            layout.findViewById(R.id.id_image).setBackgroundResource(imgDrawable);
//        }
//        bitmap.recycle();
//        bitmap = null;
//
//        TextView txtTitle    = (TextView)layout.findViewById(R.id.txt_title);
//        if (titleString.equals("null")) {
//            titleString = "";
//        }
//        txtTitle.setText(titleString);
//        TextView txtBottom   = (TextView)layout.findViewById(R.id.txt_bottom);
//        if (bottomString.equals("null")) {
//            bottomString = "";
//        }
//        txtBottom.setText(bottomString);
//        final EditText editBottom  = (EditText)layout.findViewById(R.id.edit_bottom);
//        if(showInput.equals("1")){
//            editBottom.setVisibility(View.VISIBLE);
//        }else {
//            editBottom.setVisibility(View.GONE);
//        }
//        showAlertDialog(activity,
//                0,
//                null,
//                layout,
//                null,
//                buttonString,
//                null,
//                new AlertDialog.Builder.AlertDialogClickListener() {
//                    @Override
//                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                        if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON) {
//                            dismiss();
//                            String data = editBottom.getText().toString();
//                            mDialogConfirmClick.onDialogConfirmClick( data);
//
//                        }
//                    }
//                }, false);
    }

    /**
     *  显示图片的Dialog
     * @param titleString                标题
     * @param bottomString               底部标题
     * @param localImagePath             图片路径
     * @param buttonString               按钮文字
     * @param showInput                  是否显示文本编辑框
     * @param dialogConfirmClick         回调
     * */
    public void showImageDialog(final FragmentActivity activity,
                                String titleString,
                                String bottomString,
                                String localImagePath,
                                String buttonString,
                                String showInput,
                                DialogConfirmClick dialogConfirmClick){

//        setDialogConfirmClick(dialogConfirmClick);
//
//        LinearLayout layout =(LinearLayout) View.inflate(activity, R.layout.plat_image_dialog, null);
//        Bitmap bitmap =null;
//        try{
//             bitmap = BitmapFactory.decodeFile(localImagePath);
//        }catch (Exception e){
//            LogUtil.print(e.getMessage());
//            return ;
//        }
//        //如果图片的高度大于270dp,则图片可以支持滑动
//        if(bitmap.getHeight() > DimenUtil.dp2px(activity, 270)){
//            layout.findViewById(R.id.image_dialog_scroolview).setVisibility(View.VISIBLE);
//            layout.findViewById(R.id.id_image).setVisibility(View.GONE);
//            ((ImageView)(layout.findViewById(R.id.id_scroll_image))).setImageBitmap(bitmap);
//        }else{
//            layout.findViewById(R.id.image_dialog_scroolview).setVisibility(View.GONE);
//            layout.findViewById(R.id.id_image).setVisibility(View.VISIBLE);
//            ((ImageView)(layout.findViewById(R.id.id_image))).setImageBitmap(bitmap);
//        }
//        TextView txtTitle    = (TextView)layout.findViewById(R.id.txt_title);
//        if (titleString.equals("null")) {
//            titleString = "";
//        }
//        txtTitle.setText(titleString);
//        TextView txtBottom   = (TextView)layout.findViewById(R.id.txt_bottom);
//        if (bottomString.equals("null")) {
//            bottomString = "";
//        }
//        txtBottom.setText(bottomString);
//        final EditText editBottom  = (EditText)layout.findViewById(R.id.edit_bottom);
//        if(showInput.equals("1")){
//            editBottom.setVisibility(View.VISIBLE);
//        }else {
//            editBottom.setVisibility(View.GONE);
//        }
//        showAlertDialog(activity,
//                0,
//                null,
//                layout,
//                null,
//                buttonString,
//                null,
//                new AlertDialog.Builder.AlertDialogClickListener() {
//                    @Override
//                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                        if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON) {
//                            dismiss();
//                            String data = editBottom.getText().toString();
//                            mDialogConfirmClick.onDialogConfirmClick( data);
//
//                        }
//                    }
//                }, false);
    }

    /**
     *  显示密码或验证码输入框
     * @param title                标题
     * @param hint                  .输入框hint
     * @param minLengh              .输入框最小输入长度
     * @param maxLengh              .输入框最大输入长度
     * @param isShowVercodeImg  是否显示图片验证码
     * @param mBase64Vercode     Base64编码的图片
     * @param isNeedEncrypt      .是否加密输入内容
     * @param isPassword          .输入是否密码
     * @param dialogConfirmClick        点击回调事件
     * */
    public void showPwVercodeDialog(final FragmentActivity activity,
                                    String title,
                                    String hint,
                                    final int minLengh,
                                    int maxLengh,
                                    boolean isShowVercodeImg,
                                    String mBase64Vercode,
                                    final boolean isNeedEncrypt,
                                    boolean isPassword,
                                    DialogConfirmClick dialogConfirmClick){

        setDialogConfirmClick(dialogConfirmClick);
        LinearLayout layout =(LinearLayout) View.inflate(activity, R.layout.plat_vercode_pwd_dialog, null);
        TextView titleText = (TextView)layout.findViewById(R.id.id_title_text);
        if (StringUtil.isNotEmpty(title)) titleText.setVisibility(View.VISIBLE);
        titleText.setText(title);
        ImageView mDlgItemVercode  = (ImageView)layout.findViewById(R.id.vercode_image);
        final ClearEditText mDlgItemEditText = (ClearEditText)layout.findViewById(R.id.vercode_password);
        mDlgItemEditText.setHint(hint);
        mDlgItemEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengh)});
        if (isPassword) mDlgItemEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showAlertDialog(activity, 0,"",layout,"取消","确定",null,new AlertDialog.Builder.AlertDialogClickListener() {
            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mDlgItemEditText.getWindowToken(), 0);
                    dismiss();
                    mDialogConfirmClick.onDialogCancelClick();

                }
                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON) {
                    String data = mDlgItemEditText.getText().toString();
                    if (data.length()<minLengh){
                        ToastUtil.toast(activity, String.format(activity.getString(R.string.plat_string_length_not_valid), minLengh));
                    }else {
                        if (isNeedEncrypt){
                            data = CommonEncrypt.loginEncrypt(data);
                        }
                        mDialogConfirmClick.onDialogConfirmClick(data);
                        dismiss();
                    }

                }
            }
        },false);

        setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);
        mDlgItemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() >= minLengh){
                    setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);
                }else {
                    setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, false);
                }
            }
        });

        if (isShowVercodeImg){
            mDlgItemVercode.setVisibility(View.VISIBLE);
            try{
                byte[] data = Base64.decode(mBase64Vercode, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                mDlgItemVercode.setImageBitmap(bitmap);
            }
            catch(Exception e){
                ToastUtil.toast(activity, R.string.common_get_image_vercode_fail);
            }
        }else {
            mDlgItemVercode.setVisibility(View.GONE);
        }

    }


    /**
     *  多选择列表对话框
     * * @param datas         列表数据
     * @param dialogConfirmClick 点击回调事件
     */
    public void showMutiSelectDialog(final FragmentActivity activity,
                                     String title,
                                     final ArrayList<CommmonSelectData> datas,
                                     DialogConfirmClick dialogConfirmClick){

        setDialogConfirmClick(dialogConfirmClick);

        LinearLayout layout =(LinearLayout) LinearLayout.inflate(activity, R.layout.plat_select_list_layout_dialog, null);
        final ListView listView = (ListView)layout.findViewById(R.id.id_data_list);
        final MuliSelectAdapter adapter = new MuliSelectAdapter(activity,datas);
        listView.setAdapter(adapter);
        ListUtil.setHeightBaseOnDisplayChildren(listView, 3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.multiple_checkbox);
                checkBox.toggle();
                String mergeFlag = datas.get(i).getMergeFlag();
                if (mergeFlag != null) {
                    if (mergeFlag.equals("0")) {//不可合并，单选
                        for (int j = 0; j < datas.size(); j++) {
                            adapter.getMap().put(j, false);
                        }
                    } else {//可合并项目，可多选，不能和不可合并选项同时使用
                        for (int j = 0; j < datas.size(); j++) {
                            if (datas.get(j).getMergeFlag().equals("0"))
                                adapter.getMap().put(j, false);
                        }
                    }
                }
                adapter.getMap().put(i, checkBox.isChecked());
                adapter.notifyDataSetChanged();
            }
        });

        showAlertDialog(activity,
                        0,
                        title,
                        layout,
                        "取消",
                        "确定",
                        null,
                        new AlertDialog.Builder.AlertDialogClickListener() {
            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON){

                    mDialogConfirmClick.onDialogCancelClick();
                    dismiss();
                }
                if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON){
                    dismiss();

                    Map<Integer,Boolean> map = ((MuliSelectAdapter) listView.getAdapter()).getMap();
                    ArrayList<Integer> selectIndexs = new ArrayList<Integer>();
                    Set<Integer> keys = map.keySet();
                    for (int key : keys){
                        if(map.get(key)) selectIndexs.add(key);
                    }
                    mDialogConfirmClick.onDialogConfirmClick(selectIndexs);
                }
            }
        },false);
    }

    /**
     *  单选列表对话框
     * * @param datas         列表数据
     * @param dialogConfirmClick 点击回调事件
     */
    public void showSingleSelectDialog(final FragmentActivity activity,
                                       String title,
                                       final ArrayList<CommmonSelectData> datas,
                                       DialogConfirmClick dialogConfirmClick){

        setDialogConfirmClick(dialogConfirmClick);

        LinearLayout layout =(LinearLayout) LinearLayout.inflate(activity, R.layout.plat_select_list_layout_dialog, null);
        final ListView listView = (ListView)layout.findViewById(R.id.id_data_list);
        final CommonSelectListAdapter adapter = new CommonSelectListAdapter(activity,datas,null,false,true,false);
        listView.setAdapter(adapter);
        ListUtil.setHeightBaseOnDisplayChildren(listView, 3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  adapter.setSelectposition(i);
            }
        });

        showAlertDialog(activity,
                0,
                title,
                layout,
                "取消",
                "确定",
                null,
                new AlertDialog.Builder.AlertDialogClickListener() {
                    @Override
                    public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                        if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON) {
                            dismiss();
                            mDialogConfirmClick.onDialogCancelClick();

                        }

                        if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON) {

                            dismiss();
                            ArrayList<Integer> selectIndexs = new ArrayList<Integer>();
                            if (adapter.getSelectposition() != -1)
                                selectIndexs.add(adapter.getSelectposition());
                            mDialogConfirmClick.onDialogConfirmClick(selectIndexs);

                        }
                    }
                }, false
        );
    }

    /**
     * 內容webview的对话框
     * @param activity
     * @param content
     */
    public void showWebViewDialog(FragmentActivity activity,
                                    String content){
        LinearLayout layout = (LinearLayout) LinearLayout.inflate(activity, R.layout.plat_webview_dialog, null);
        WebView contentView = (WebView) layout.findViewById(R.id.id_webview);
        contentView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
//        contentView.loadDataWithBaseURL(null,content, "text/html", "UTF-8",null);
        contentView.loadUrl(content);
        showAlertDialog(activity, 0, "", layout, "", "", "关闭", null, false);
    }

    /**
     * 显示ActionSheetDialog
     * @param activity
     * @param buttonText  列表中除过取消按钮，要显示的其他的button
     * @param listener    button点击事件监听
     */
    public  void  showActionSheetDialog(FragmentActivity activity,
                                        String[] buttonText,
                                        ActionSheetDialog.ActionSheetDialogClickCallback listener){
        if (!isCouldShow(activity)) return;
        dismiss();
        ActionSheetDialog actionSheetDialog = new ActionSheetDialog(activity.getSupportFragmentManager());
        Bundle bundle = new Bundle();
        String buttonsStr = "";
        int length = buttonText.length;
        for(int i=0; i< length ;i++){
            buttonsStr += buttonText[i]+"|";
        }
        if(StringUtil.isNotEmpty(buttonsStr)){
            buttonsStr = buttonsStr.substring(0,buttonsStr.length() - 1);
        }
        actionSheetDialog.setActionSheetDialogClickCallback(listener);
        bundle.putString(ActionSheetDialog.BUTTON_TEXT,buttonsStr);
        actionSheetDialog.setArguments(bundle);
        show(actionSheetDialog);
    }


        /**
         *  设置对话框按钮点击监听器
         * @param mDialogConfirmClick
         */
    private void setDialogConfirmClick(DialogConfirmClick mDialogConfirmClick) {
        this.mDialogConfirmClick = mDialogConfirmClick;
    }

    public DialogConfirmClick getmDialogConfirmClick() {
        return mDialogConfirmClick;
    }

    /**
     *    对话框按钮点击回调监听器
     **/
    public  interface  DialogConfirmClick {
        /**  点击确认按钮回调*/
        void onDialogConfirmClick(Object data);

        /**  点击取消输入按钮回调*/
        void onDialogCancelClick();
    }


    public void setCancel(boolean isCancel){
        if(null!= dialogFragment){
            dialogFragment.setCancelable(isCancel);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
//        HttpCancelHandler.cancel();
    }

    @Override
    public void onDialogKeyevent(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            HttpCancelHandler.isBackClicked = true;
        }
    }
}
