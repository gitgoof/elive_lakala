package com.lakala.shoudan.activity.login;

import com.lakala.platform.bean.Session;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.swiper.TerminalKey;

/**
 * 保存用户登陆信息
 * Created by andy_lv on 14-2-24.
 */
public class SaveUserLoginInfo {

    /**
     * 保存登录的用户信息
     * @return
     */
    public static void setLoginState(User user) {
        Session session = ApplicationEx.getInstance().getSession();
        session.setUser(user);

        //设置用户登录标志
        session.setUserLogin(true);
        //设置一下进入实名认证的时间
        session.setRealnameTiem(0);

//        TerminalKey.setMac(user.getTerminalId(), user.getMacKey());//保存mac密钥
//        TerminalKey.setPik(user.getTerminalId(), user.getPinKey());//保存工作密钥
//        TerminalKey.setTpk(user.getTerminalId(), user.getWorkKey());//保存主密钥

        //清除退出登录标志
        if (LklPreferences.getInstance().getBoolean(LKlPreferencesKey.KEY_LOGIN_OUT, false)) {
            LklPreferences.getInstance().remove(LKlPreferencesKey.KEY_LOGIN_OUT);
        }
    }

}
