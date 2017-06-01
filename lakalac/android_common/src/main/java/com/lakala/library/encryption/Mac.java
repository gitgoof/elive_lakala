package com.lakala.library.encryption;


import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mab 过滤器
 *
 * @author xyz
 */
public class Mac {


    private static String HexTable = "0123456789ABCDEF";
    /**
     * 银联ANSI9.19--------------------------------------------------------------start>
     */

    private static Map<String, Integer> macPositionMap = new HashMap<String, Integer>();

    static {
        macPositionMap.put("busid", 1);
        macPositionMap.put("termid", 2);
        macPositionMap.put("tdtm", 3);
        macPositionMap.put("series", 4);
        macPositionMap.put("rnd", 5);
        macPositionMap.put("pan", 6);
        macPositionMap.put("inpan", 7);
        macPositionMap.put("billno", 8);
        macPositionMap.put("amount", 9);
        macPositionMap.put("retcode", 10);
        macPositionMap.put("sysref", 11);
        macPositionMap.put("fee", 12);

        //理财MAC域映射
        macPositionMap.put("busId",1);
        macPositionMap.put("busid",1);
        macPositionMap.put("BusId",1);
        macPositionMap.put("PayerAcNo",6);//pan
        macPositionMap.put("Pan",6);//pan
        macPositionMap.put("PayeeAcNo",7);//inpan
//        MACField.put("BillId", 8);//billno
//        MACField.put("Amount",9);//amount
        macPositionMap.put("Fee",12);//fee

    }

    public static String[] resolveMacData(JSONObject jsonObject){
        List<NameValuePair> parameters = getNameValuePair(jsonObject);
        String result[] = {"",""};
        if (parameters == null || parameters.size() == 0)
            return result;

        List<NameValuePair> mabList = new ArrayList<NameValuePair>();

        //版本号>=17,公缴业务，需要用subbusid 的值替换 busid 做MAC计算，
        //此处在遍历参数列表的时候记录下 subbusid 的值
        String subbusid = "";

        //历遍参数列表，找出 macPositionMap 中含有的字段，将它做为MAC域的一部分。
        int size = parameters.size();
        for (int index = 0 ; index < size ; index++) {
            NameValuePair field     = parameters.get(index);
            String        fildeName = field.getName();

            if (TextUtils.isEmpty(field.getValue()))
                continue;

            if (macPositionMap.containsKey(fildeName)) {
                if ("amount".equals(fildeName)) {
                    //对于金额字段，传时来时以“元”为单位，现在转成分，如果不足12位，左边补0。
                    //转换后将以“分”单位的金额，存回参列表中。
                    field = new BasicNameValuePair(fildeName, YuanToFen(field.getValue()));
                }
                else if ("termid".equals(fildeName)){
                    //保存终端号
                    result[1] = field.getValue();
                }
                //根据后参要求，将所有参与MAC的字段全部转大写。
                if (!TextUtils.isEmpty(field.getValue())){
                    field = new BasicNameValuePair(fildeName, field.getValue().toUpperCase());
                }

                //存回修改的参数。
                parameters.set(index, field);

                //将参数添加到 Mac 域列表。
                mabList.add(field);
            }else if ("subbusid".equals(fildeName)) {
                //保存 subbusid 的值
                subbusid = field.getValue();
            }else if ("famount".equals(fildeName)){
                //将famount 由“元”转为“分”
                NameValuePair newFieldValue = new BasicNameValuePair(fildeName, YuanToFen(field.getValue()));
                parameters.set(index, newFieldValue);
            }
        }

        //参数中不包含需要进行MAC 计算的字段。
        if (mabList.isEmpty()) {
            return result;
        }

        //将MAC域中的字段，按后台给出的顺序排序。
        size = mabList.size();
        if (size > 1) {
            Collections.sort(mabList, new Comparator<NameValuePair>() {
                                 @Override
                                 public int compare(NameValuePair object1, NameValuePair object2) {
                                     String o1 = object1.getName();
                                     String o2 = object2.getName();
                                     int p1 = macPositionMap.get(o1);
                                     int p2 = macPositionMap.get(o2);
                                     return p1 - p2;
                                 }
                             });
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            NameValuePair pair = mabList.get(i);
            String name  = pair.getName();
            String value = pair.getValue();

            //添加MAC域分隔符（空格）
            if (0 != i) sb.append(" ");

            //如果busid 字段的值为 “M80003” 或 “M80004”，则用  subbusid，将其替换。
            if ("busid".equals(name) && ("M80003".equals(value) || "M80004".equals(value)) ){
                sb.append(subbusid);
            }else{
                sb.append(value.trim());
            }

            LogUtil.print("MAC", String.format("MAB: %s = %s",name,value));
        }

        result[0] = filterMacData(sb.toString());

        return result;
    }

    /**
     * 拼接银联ANSI9.19 mac效验参数
     * 在域和域之间插入一个空格
     * 删去所有域的起始空格和结尾空格
     */
    public static String resolveMacData(List<BasicNameValuePair> parameters) {
        if (parameters == null || parameters.size() == 0)
            return "";

        List<BasicNameValuePair> mabList = new ArrayList<BasicNameValuePair>();

        //版本号>=17,公缴业务，需要用subbusid的值替换busid 做MAC计算，
        //此处在遍历参数列表的时候记录下subbusid的值
        String subbusid = "";

        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            String key = parameters.get(i).getName();
            if (isContainMacData(key)) {
                if ("Amount".equals(key)) {
                    //如果金额的值为null，则不继续进行，调试时公缴业务出现amount为null的情况
                    String value = parameters.get(i).getValue();
                    if (value == null || "".equals(value.trim())) {
                        continue;
                    }
                    value = StringUtil.formatString(value);
                    BasicNameValuePair pair = new BasicNameValuePair("Amount", value);
                    mabList.add(pair);
                    //改变金额
                    parameters.set(i, pair);
                } else {
                    mabList.add(parameters.get(i));
                }
            } else if ("subbusid".equals(key)) {
                //保存subbusid的值
                subbusid = parameters.get(i).getValue();
            }
        }

        if (mabList.isEmpty()) {
            return "";
        }

        size = mabList.size();
        if (size > 1) {//按文档顺序排序
            Collections.sort(mabList, new Comparator<NameValuePair>() {
                @Override
                public int compare(NameValuePair object1, NameValuePair object2) {
                    String o1 = object1.getName();
                    String o2 = object2.getName();
                    int p1 = macPositionMap.get(o1);
                    int p2 = macPositionMap.get(o2);
                    return p1 - p2;
                }
            });
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            NameValuePair pair = mabList.get(i);
            sb.append(pair.getValue().trim());
            if (i != size - 1) {
                sb.append(" ");
            }
        }
        return filterMacData(sb.toString());
    }

    /**
     * 将一个 double 型的以“元”为单位的金额转换成“分”字符串。
     * @param yuan 以“元”为单位的金额
     * @return 按12位补齐的 “分”字符串。
     */
    public static String YuanToFen(String yuan){
        if (yuan == null || yuan.length() ==0){
            yuan = "0.0";
        }

        BigDecimal bigDecimal = new BigDecimal(yuan);

        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        bigDecimal = bigDecimal.setScale(0);

        Long fen = bigDecimal.longValue();

        return String.format("%012d", fen);
    }

    private static List<NameValuePair> getNameValuePair(JSONObject jsonObject){
        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        if(jsonObject == null){
            return null;
        }
        Iterator<String> keys = jsonObject.keys();
        String value = "";
        String key = "";
        NameValuePair pair = null;
        while(keys.hasNext()){
            key = keys.next();
            value = jsonObject.optString(key);
            pair = new BasicNameValuePair(key,value);
            parameter.add(pair);
        }
        return parameter;
    }

    /**
     * 获取随机数
     * @return 返回一个16个字符长的随机数（全大写）
     */
    public static String getRnd(){
        long    t              = System.nanoTime() ^ System.currentTimeMillis();
        Random random         = new Random(t);
        char[]  rnd            = new char[16];
        int     hexTableLength =  HexTable.length();

        for (int i=0;i< rnd.length;i++){
            rnd[i] =  HexTable.charAt(random.nextInt(hexTableLength));
        }

        return String.valueOf(rnd);
    }
    /**
     * 是否存在需要银联ANSI9.19 需要效验的参数
     *
     * @param name
     *
     * @return
     */
    public static boolean isContainMacData(String name) {
        return  macPositionMap.containsKey(name);
    }

    /**
     * 银联mac效验过滤多余字符
     * 除了字母(A-Z，a-z)，数字(0-9)，空格，逗号(，)和点号(.)以外的字符都删去
     */
    public static String filterMacData(String str) {
        if (str == null) return null;
        // 只允数字
        String regEx = "[^0-9A-Za-z,. *]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        //替换与模式匹配的所有字符（即非数字的字符将被""替换）
        return m.replaceAll("").trim();
    }
    /**
     * <end--------------------------------------------------------------
     */
}
