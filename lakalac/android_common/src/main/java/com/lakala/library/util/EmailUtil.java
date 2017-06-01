package com.lakala.library.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vinchaos api on 13-12-12.
 * Email工具类
 */
public class EmailUtil {

    /**
     * checkEmailAddress
     * E-mail验证
     *
     * @param emailString
     * @return
     */
    public static boolean checkEmailAddress(String emailString) {
        if (StringUtil.isEmpty(emailString)){
            return false;
        }

        String regEx = "\\w+([-+._]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Matcher matcherObj = Pattern.compile(regEx).matcher(emailString);
        return matcherObj.matches();
    }
}
