package com.lakala.elive.common.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by gaofeng on 2017/5/3.
 */

public class EditUtil {

    private static boolean isChinese(char c,boolean onlyChina) {
        Character.UnicodeBlock unicode = Character.UnicodeBlock.of(c);
        if(onlyChina){
            if(unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || unicode == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || unicode == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || unicode == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ){

                return true;
            }
        } else {
            if(unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || unicode == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || unicode == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || unicode == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || unicode == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || String.valueOf(c).matches("^[a-zA-Z]*")){
                return true;
            }
        }

        return false;
    }

    public static void setEditInputType(EditText editText, final int maxLength, boolean onlyChina){
        if(editText == null)return;
        editText.setFilters(new InputFilter[]{getChinaInputFilter(maxLength,onlyChina)});
    }
    public static InputFilter getChinaInputFilter(final int maxLength, final boolean onlyChina){
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for(int i = start ;i < end;i++){
                    if(!isChinese(source.charAt(i),onlyChina)){
                        return "";
                    }
                }

                int dindex = 0;
                int count = 0;
                while (count <= maxLength && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    return dest.subSequence(0, dindex - 1);
                }
                int sindex = 0;
                while (count <= maxLength && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        };
        return inputFilter;
    }
    public static void setAddressInputFileter(EditText editText,int maxLength){
        if(editText == null)return;
        editText.setFilters(new InputFilter[]{getAddressInputFilter(maxLength)});
    }
    private static InputFilter getAddressInputFilter(final int maxLength){
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                while (count <= maxLength && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    return dest.subSequence(0, dindex - 1);
                }
                int sindex = 0;
                while (count <= maxLength && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLength) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        };
        return inputFilter;
    }
    public static int getTextCharLength(String text){
        if(TextUtils.isEmpty(text))return 0;
        int sindex = 0;
        int count = 0;
        while (sindex < text.length()) {
            char c = text.charAt(sindex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }
        return count;
    }
    public static boolean checkEmail(String email){
        if(TextUtils.isEmpty(email))return true;
        if(!email.contains("@"))return true;
        String[] strings = email.split("@");
        if(strings.length<2 || strings.length > 2){
            return true;
        }
        if(strings[0].length() > 52){
            return true;
        }
        return false;
    }
}
