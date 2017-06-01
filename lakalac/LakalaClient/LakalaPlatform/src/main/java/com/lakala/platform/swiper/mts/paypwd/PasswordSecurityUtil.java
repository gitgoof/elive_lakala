package com.lakala.platform.swiper.mts.paypwd;

import android.content.Context;

import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;

/**
 * Created by wangchao on 14/12/3.
 */
public class PasswordSecurityUtil {

    /**
     * 检测密码安全性
     *
     * @param context    context
     * @param password   password
     * @return           是否安全
     */
    public static boolean checkLoginPasswordSecurity(Context context, String password) {

        if (password.contains(" ")){
            ToastUtil.toast(context, com.lakala.platform.R.string.plat_plese_input_your_password_error);
            return false;
        }
        if (StringUtil.isNumeric(password)) {
            ToastUtil.toast(context, "登录密码不能全为数字!");
            return false;
        } else if (StringUtil.isLetters(password)) {
            ToastUtil.toast(context, "登录密码不能全为字母!");
            return false;
        } else if (StringUtil.isSeriesSame(password, 5)) {
            ToastUtil.toast(context, "登录密码中不能包含5位以上连续相同的字符!");
            return false;
        } else if (StringUtil.isOrder(password)) {
            ToastUtil.toast(context, "登录密码不能为连续字符!");
            return false;
        }

        return true;

    }

    /**
     * 检测支付密码安全性
     *
     * @param context     context
     * @param password    password
     * @return            是否安全
     */
    public static boolean checkPayPasswordSecurity(Context context, String password){

        if (StringUtil.isSeriesSame(password, 3)){
            ToastUtil.toast(context, "支付密码不能包含3位以上连续相同数字!");
            return false;
        }else if (StringUtil.isOrder(password)){
            ToastUtil.toast(context, "支付密码不能为连续数字!");
            return false;
        }

        return true;
    }
}
