package com.lakala.library.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Vinchaos api on 13-12-12.
 * 基础类型转换工具类
 */
public class TypeConvertionUtil {
    /**
     * String2double
     *
     * @param string
     * @return -1为转换错误
     */
    public static double string2Double(String string) {
        if(!StringUtil.isNotEmpty(string))return -1;
        double temp;
        try {
            temp = Double.parseDouble(string);
        } catch (Exception e) {
            temp = -1;
        }
        return temp;
    }
    /**
     * String2double
     *
     * @param string
     * @param defaultValue
     * @return
     */
    public static double string2Double(String string,double defaultValue){
        if(!StringUtil.isNotEmpty(string))return defaultValue;
        double temp;
        try {
            temp = Double.parseDouble(string);
        } catch (Exception e) {
            temp = defaultValue;
        }
        return temp;
    }

    /**
     * String2int
     *
     * @param string
     * @return -1为转换错误
     */
    public static int string2Int(String string) {
        if(!StringUtil.isNotEmpty(string))return -1;
        int temp;
        try {
            temp = Integer.parseInt(string);
        } catch (Exception e) {
            temp = -1;
        }
        return temp;
    }

    /**
     * String2int
     *
     * @param string
     * @param defaultValue
     * @return
     */
    public static int string2Int(String string,int defaultValue) {
        if(!StringUtil.isNotEmpty(string))return defaultValue;
        int temp;
        try {
            temp = Integer.parseInt(string);
        } catch (Exception e) {
            temp = defaultValue;
        }
        return temp;
    }

    /**
     * 将 Bundle 转换成 JSON，只能转换以下类型的数据：<br>
     *     Integer,Long,Short,Double,Float,String,Boolean
     *
     * @param bundle bundle 对象
     * @return  JSON 对象。
     */
    public static JSONObject bundle2Json(Bundle bundle){
        JSONObject json = new JSONObject();

        if (bundle == null || bundle.isEmpty()){
            return json;
        }

        Iterator<String> keys = bundle.keySet().iterator();
        while (keys.hasNext()){
            String key = keys.next();
            Object value = bundle.get(key);
            if (value instanceof Integer ||
                value instanceof Short   ||
                value instanceof Long    ||
                value instanceof Float   ||
                value instanceof Double  ||
                value instanceof String  ||
                value instanceof Boolean){
                try {
                    json.put(key, value);
                } catch (JSONException e) {}
            }
            else if(value instanceof Bundle){
                try {
                    json.put(key,bundle2Json((Bundle)value));
                } catch (JSONException e) {}
            }
            else if (value instanceof List){
                try {
                    json.put(key,list2JsonArray((List) value));
                } catch (JSONException e) {}
            }
            else if (value instanceof int[]     ||
                     value instanceof short[]   ||
                     value instanceof long[]    ||
                     value instanceof float[]   ||
                     value instanceof double[]  ||
                     value instanceof String[]  ||
                     value instanceof boolean[]){
                JSONArray array = new JSONArray();

                int size = Array.getLength(value);
                for (int index = 0;index<size;index++){
                    array.put(Array.get(value,index));
                }

                try {
                    json.put(key,array);
                } catch (JSONException e) {}
            }
        }

        return json;
    }

    /**
     * 将 JSONObject 转换成 Bundle，只能转换以下类型的数据：<br>
     *     Integer,Long,Short,Double,Float,String,Boolean
     * @param json JSON 对象
     * @return Bundle 对象
     */
    public static Bundle json2Bundle(JSONObject json){
        if (json == null || json.length() == 0){
            return new Bundle();
        }
        Bundle bundle = new Bundle();
        Iterator<String> keys = json.keys();
        while(keys.hasNext()){
            String key = keys.next();
            Object value = json.opt(key);

            if (value == null)
                continue;

            if (value instanceof Integer){
                bundle.putInt(key,(Integer)value);
            }
            else if (value instanceof Short){
                bundle.putShort(key, (Short) value);
            }
            else if (value instanceof Long){
                bundle.putLong(key, (Long) value);
            }
            else if (value instanceof Float){
                bundle.putFloat(key, (Float) value);
            }
            else if (value instanceof Double){
                bundle.putDouble(key, (Double) value);
            }
            else if (value instanceof String){
                bundle.putString(key, (String) value);
            }
            else if (value instanceof Boolean){
                bundle.putBoolean(key, (Boolean) value);
            }
            else if (value instanceof JSONObject){
                bundle.putBundle(key,json2Bundle((JSONObject) value));
            }
            else if (value instanceof JSONArray){
                jsonArrary2List((JSONArray)value,bundle,key);
            }
        }

        return bundle;
    }

    /**
     * 将 List 转换成 JSONArray，用于 bundle2Json 方法。
     * @param list List 对象
     * @return JSONArray 对象
     */
    private static JSONArray list2JsonArray(List list){
        if (list == null || list.isEmpty()){
            return new JSONArray();
        }

        JSONArray array = new JSONArray();

        for (Object value:list){
            if (value instanceof Integer ||
                value instanceof Short   ||
                value instanceof Long    ||
                value instanceof Float   ||
                value instanceof Double  ||
                value instanceof String  ||
                value instanceof Boolean){
                array.put(value);
            }
            else if (value instanceof Bundle){
                array.put(bundle2Json((Bundle)value));
            }
        }

        return array;
    }

    /**
     * 将 JSONArray 转成 List，用于 json2Bundle 方法。
     * @param array  JSONArray 对象
     * @param bundle Bundle 对象（输出）
     * @param key    Bundle 对象（输出）
     * @return List 对象
     */
    private static void jsonArrary2List(JSONArray array,Bundle bundle,String key){

        int     intArray[]      = null;
        short   shortArray[]    = null;
        long    longArray[]     = null;
        float   floatArray[]    = null;
        double  doubleArray[]   = null;
        boolean booleanArray[]  = null;
        ArrayList<String> strings = null;
        ArrayList<Bundle> bundles = null;

        int size = array.length();

        //List 中的数据类型，以 List 第一个数据类型为准。
        String valueType = null;

        if (array == null || size == 0){
            return;
        }

        for (int index=0 ;index <size;index++){
            Object value = array.opt(index);

            if (value == null){
                continue;
            }

            if (value instanceof Integer){
                if (valueType == null){
                    valueType = "int";
                    intArray = new int[size];
                }

                if ("int".equals(valueType)){
                    intArray[index] = (int)value;
                }
            }
            else if (value instanceof Short){
                if (valueType == null){
                    valueType = "short";
                    shortArray = new short[size];
                }

                if ("short".equals(valueType)){
                    shortArray[index] = (short)value;
                }
            }
            else if (value instanceof Long){
                if (valueType == null){
                    valueType = "long";
                    longArray = new long[size];
                }

                if ("long".equals(valueType)){
                    longArray[index] = (long)value;
                }
            }
            else if (value instanceof Float){
                if (valueType == null){
                    valueType = "float";
                    floatArray = new float[size];
                }

                if ("float".equals(valueType)){
                    floatArray[index] = (float)value;
                }
            }
            else if (value instanceof Double){
                if (valueType == null){
                    valueType = "double";
                    doubleArray = new double[size];
                }

                if ("double".equals(valueType)){
                    doubleArray[index] = (double)value;
                }
            }
            else if (value instanceof String){
                if (valueType == null){
                    valueType = "String";
                    strings = new ArrayList<String>();
                }

                if ("String".equals(valueType)){
                    strings.add((String)value);
                }
            }
            else if (value instanceof Boolean){
                if (valueType == null){
                    valueType = "boolean";
                    booleanArray = new boolean[size];
                }

                if ("boolean".equals(valueType)){
                    booleanArray[index] = (boolean)value;
                }
            }
            else if (value instanceof JSONObject){
                if (valueType == null){
                    valueType = "JSONObject";
                    bundles = new ArrayList<Bundle>();
                }

                if ("JSONObject".equals(valueType)){
                    bundles.add(json2Bundle((JSONObject)value));
                }
            }
        }

        switch( valueType){
            case "int":
                bundle.putIntArray(key,intArray);
                break;
            case "short":
                bundle.putShortArray(key,shortArray);
                break;
            case "long":
                bundle.putLongArray(key,longArray);
                break;
            case "float":
                bundle.putFloatArray(key,floatArray);
                break;
            case "double":
                bundle.putDoubleArray(key,doubleArray);
                break;
            case "String":
                bundle.putStringArrayList(key,strings);
                break;
            case "boolean":
                bundle.putBooleanArray(key,booleanArray);
                break;
            case "JSONObject":
                bundle.putParcelableArrayList(key,bundles);
                break;
        }
    }
}
