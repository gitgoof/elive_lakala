package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.DebugConfig;
import com.lakala.library.exception.BaseException;
import com.lakala.library.jni.LakalaNative;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.mts.DialogController;
import com.lakala.ui.dialog.mts.AlertDialog;

import org.json.JSONObject;

/**
 * Created by lianglong on 14-6-6.
 * 支付密码相关流程逻辑
 */
public class PayPwdProcess {
    private static final DialogController builder = DialogController.getInstance();

    public static final int TYPE_WALLET = 0;
    public static final int TYPE_FUND   = 1;

    /**
     * 调用支付密码输入框逻辑
     * @param context
     * @param notice
     * @param listener
     */
    public static void showInputPasswordDialog(final FragmentActivity context,int type,String title,String notice,boolean isEncrypt,DialogController.DialogConfirmClick listener){
        User customer = ApplicationEx.getInstance().getUser();
        if (!customer.isTrsPasswordFlag()){
            switch (type){
                case TYPE_FUND:
                    notice = context.getString(R.string.plat_set_pay_password_notice_fund);
                    break;
                case TYPE_WALLET:
                    notice = context.getString(R.string.plat_set_pay_password_notice_wallet);
                    break;
            }
            showNonePayPasswordDialog(context,notice);
            return;
        }
        if (!customer.isQuestionFlag()){
            switch (type){
                case TYPE_FUND:
                    notice = context.getString(R.string.plat_pay_fund_none_question_prompt);
                    break;
                case TYPE_WALLET:
                    notice = context.getString(R.string.plat_pay_wallet_none_question_prompt);
                    break;
            }
            showNoneQuestionDialog(context,notice);
            return;
        }
        title = StringUtil.isEmpty(title) ? context.getString(R.string.plat_set_pay_input_password) : title;
        builder.showPayPasswordDialog(context,title,notice,isEncrypt,true,listener);
    }

    /**
     * 还未设置支付密码提示
     * @param context
     */
    public static void showNonePayPasswordDialog(final FragmentActivity context,String message){
        final Intent intent = new Intent(context,PayPwdSetActivity.class);
        String title    = context.getString(R.string.plat_prompt);
//        String msg      = context.getString(R.string.plat_set_pay_password_notice);
        String leftBtn  = context.getString(R.string.com_cancel);
        String rightBtn = context.getString(R.string.plat_to_set_pay_password);

        builder.showAlertDialog(context, title, message,leftBtn,rightBtn,new AlertDialog.Builder.AlertDialogClickListener() {

            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                switch (typeEnum){
                    case RIGHT_BUTTON:
                        context.startActivity(intent);
                        break;
                    default:
                        builder.dismiss();
                        break;
                }
                builder.dismiss();
            }
        });
    }

    /**
     * 还未设置密保问题提示
     * @param context   上下文
     * @param notice    Dialog中的message信息
     */
    public static void showNoneQuestionDialog(final FragmentActivity context,String notice){
        showNoneQuestionDialog(context,notice, false);
    }

    /**
     * 还未设置密保问题提示
     * @param context               上下文
     * @param notice                Dialog中的message信息
     * @param isToFindPayPassword   是否为了找回密码而设置密保
     */
    private static void showNoneQuestionDialog(final FragmentActivity context,final String notice,boolean isToFindPayPassword){
        final Intent intent = new Intent(context,PayPwdSetQuestionActivity.class);
        if (isToFindPayPassword){
            intent.putExtra(PayPwdSetQuestionActivity.class.getName(),"Find");
        }
        String title    = context.getString(R.string.plat_prompt);
        String leftBtn  = context.getString(R.string.com_cancel);
        String rightBtn = context.getString(R.string.plat_to_set_pay_password);
        String message  = StringUtil.isEmpty(notice) ? context.getString(R.string.plat_pay_wallet_none_question_prompt) : notice;

        builder.showAlertDialog(context, title, message,leftBtn,rightBtn,new AlertDialog.Builder.AlertDialogClickListener() {

            @Override
            public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                switch (typeEnum){
                    case RIGHT_BUTTON:
                        context.startActivity(intent);
                        break;
                    default:
                        builder.dismiss();
                        break;
                }
                builder.dismiss();
            }
        });
    }

    /**
     * 找回密码操作  没有密保问题需要先设置密保问题,有密保问题先查询密保问题然后去找回密码
     * @param context
     */
    public static void findPayPassword(final FragmentActivity context){
        if (!ApplicationEx.getInstance().getUser().isQuestionFlag()){
            showNoneQuestionDialog(context,context.getString(R.string.plat_password_security_find_pay_pwd_message),true);
            return;
        }
        IHttpRequestEvents getQuestion = new IHttpRequestEvents(){
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
                Intent intent = new Intent(context,PayPwdFindActivity.class);
                intent.putExtra(PayPwdQuestion.class.getName(),new PayPwdQuestion(response));
                context.startActivity(intent);
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                String message = request.getResponseHandler().getResultMessage();
                ToastUtil.toast(context, message);
            }
        };
        RegisterRequestFactory.userQuestionQry(context, ApplicationEx.getInstance().getUser().getLoginName())
                .setIHttpRequestEvents(getQuestion)
                .execute();
    }

    /**
     * 加密交易密码
     * @param password
     * @return
     */
    public static String encryptPayPassword(String password){
        if (StringUtil.isEmpty(password)) {
            return password;
        }
        String terminalId   = ApplicationEx.getInstance().getUser().getMtsTerminalId();
        String masterKey    = TerminalKey.getMasterKey(terminalId);
        String workKey      = TerminalKey.getWorkKey(terminalId);
        return LakalaNative.encryptPwd(masterKey, workKey, password, DebugConfig.DEV_ENVIRONMENT);
    }

}