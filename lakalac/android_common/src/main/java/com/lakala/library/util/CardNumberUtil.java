package com.lakala.library.util;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vinchaos api on 13-12-12.
 * 卡号处理工具类
 * 对身份证,银行卡等卡号的格式化,验证等功能
 */
public class CardNumberUtil {

    /**
     * checkBankCard
     * 银行卡卡号验证start
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * getBankCardCheckCode
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    /**
     * formatCardNumberWithSpace
     * 银行卡格式化
     *
     * @param cardNumber
     * @return
     */
    public static String formatCardNumberWithSpace(String cardNumber) {

        cardNumber = cardNumber.replaceAll(" ", "");
        char[] chars = cardNumber.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i != 0 && i % 4 == 0) {
                builder.append(" ");
            }
            builder.append(chars[i]);
        }

        return builder.toString();
    }


    /**
     * iDCardValidate
     * 身份证号验证
     *
     * @param IDStr
     * @return
     */
    public static boolean iDCardValidate(String IDStr) throws ParseException {

        if (StringUtil.isEmpty(IDStr)){
            return false;
        }

        //防止输入小写的X
        IDStr = IDStr.toUpperCase();

        String[] ValCodeArr = {"1", "0","X","9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";

        //号码的长度 15位或18位
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            return false;
        }

        //数字 除最后以为都为数字
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            return false;
        }

        //出生年月是否有效
        String strYear = Ai.substring(6, 10);
        String strMonth = Ai.substring(10, 12);
        String strDay = Ai.substring(12, 14);
        if (!isDataFormat(strYear + "-" + strMonth + "-" + strDay)) {
            return false;
        }

        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            //身份证生日不在有效范围
            return false;
        }

        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //身份证月份无效
            return false;
        }

        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //身份证日期无效
            return false;
        }

        // 地区码时候有效
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //身份证地区编码错误
            return false;
        }

        //判断最后一位的值
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }

        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;
        if (IDStr.length() == 18) {
            if (!Ai.equals(IDStr)) {
                //身份证无效，不是合法的身份证号码
                return false;
            }
        }

        return true;
    }

    /**
     * 地区码
     *
     * @return HashTable
     */
    private static Hashtable GetAreaCode() {
        Hashtable<String, String> hashTable = new Hashtable<>();
        hashTable.put("11", "北京");
        hashTable.put("12", "天津");
        hashTable.put("13", "河北");
        hashTable.put("14", "山西");
        hashTable.put("15", "内蒙古");
        hashTable.put("21", "辽宁");
        hashTable.put("22", "吉林");
        hashTable.put("23", "黑龙江");
        hashTable.put("31", "上海");
        hashTable.put("32", "江苏");
        hashTable.put("33", "浙江");
        hashTable.put("34", "安徽");
        hashTable.put("35", "福建");
        hashTable.put("36", "江西");
        hashTable.put("37", "山东");
        hashTable.put("41", "河南");
        hashTable.put("42", "湖北");
        hashTable.put("43", "湖南");
        hashTable.put("44", "广东");
        hashTable.put("45", "广西");
        hashTable.put("46", "海南");
        hashTable.put("50", "重庆");
        hashTable.put("51", "四川");
        hashTable.put("52", "贵州");
        hashTable.put("53", "云南");
        hashTable.put("54", "西藏");
        hashTable.put("61", "陕西");
        hashTable.put("62", "甘肃");
        hashTable.put("63", "青海");
        hashTable.put("64", "宁夏");
        hashTable.put("65", "新疆");
        hashTable.put("71", "台湾");
        hashTable.put("81", "香港");
        hashTable.put("82", "澳门");
        hashTable.put("91", "国外");
        return hashTable;
    }

    /**
     * 日期是否有效
     *
     * @return boolean
     */
    private static boolean isDataFormat(String str) {
        boolean flag = false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 是否为数字
     *
     * @return boolean
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    /**
     * 银行卡号输入时格式化，每隔4位空一格显示
     *
     * @param mEditText
     */
    public static void bankCardNumAddSpace(final EditText mEditText) {
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = mEditText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    mEditText.setText(str);
                    Editable etable = mEditText.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
    }
}
