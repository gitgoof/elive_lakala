package com.lakala.ui.common;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * 有关视图属性的公用方法放在这里。
 */
public class ViewAttribute {
    /**
     * 设置视图的文字颜色，如果 view 是一个 ViewGroup则会递归设置其内部所有TextView文字颜色。
     * 可以设置文字颜色的组件如下：TextView、EditText
     * @param view      View 对象可以是 ViewGroup、TextView、EditText
     * @param textColor 文字颜色
     */
    public static void setTextColor(View view,int textColor){
        if (view instanceof ViewGroup){
            //view 是一个容器，迭代它的所有子元素。
            ViewGroup vg = (ViewGroup)view;
            int count = vg.getChildCount();

            for (int index=0 ;index<count ;index++){
                setTextColor(vg.getChildAt(index),textColor);
            }
        }
        else if (view instanceof TextView){
            ((TextView) view).setTextColor(textColor);
        }
    }

    /**
     * 设置视图的文字颜色，如果 view 是一个 ViewGroup则会递归设置其内部所有TextView文字颜色。
     * 可以设置文字颜色的组件如下：TextView、EditText
     * @param view 　　　　　　View 对象可以是 ViewGroup、TextView、EditText
     * @param colorStateList ColorStateList 对象，参考 {@link android.content.res.ColorStateList}
     */
    public static void setTextColor(View view,ColorStateList colorStateList){
        if (view instanceof ViewGroup){
            //view 是一个容器，迭代它的所有子元素。
            ViewGroup vg = (ViewGroup)view;
            int count = vg.getChildCount();

            for (int index=0 ;index<count ;index++){
                setTextColor(vg.getChildAt(index),colorStateList);
            }
        }
        else if (view instanceof TextView){
            ((TextView) view).setTextColor(colorStateList);
       }

    }

    /**
     * 获取TextView 的文字颜色，如果 view 是一个 ViewGroup则会递归获取其内部所的TextView文字颜色。
     * @param view  View 对象
     * @param stateSet 参考{@link android.content.res.ColorStateList#getColorForState(int[], int)}
     * @return 返回一个以View 的hascode 为主键，其颜色值为值的Map。
     */
    public static Map<Integer,Integer> getTextColorOfTextView(View view,int[] stateSet){
        Map<Integer,Integer> colorMap = new HashMap<Integer, Integer>();

        if (view instanceof ViewGroup){
            //view 是一个容器，迭代它的所有子元素。
            ViewGroup vg = (ViewGroup)view;
            int count = vg.getChildCount();

            for (int index=0 ;index<count ;index++){
                Map<Integer,Integer> map = getTextColorOfTextView(vg.getChildAt(index),stateSet);
                colorMap.putAll(map);
            }
        }
        else if (view instanceof TextView){
            ColorStateList csl = ((TextView) view).getTextColors();

            if (csl != null){
                int color = csl.getColorForState(stateSet, Color.BLACK);
                colorMap.put(view.hashCode(),color);
            }
        }

        return colorMap;
    }

    /**
     * 设置TextView 的文字颜色，如果 view 是一个 ViewGroup则会递归设置其内部所的TextView文字颜色。
     * @param view  View 对象
     * @param colorMap 由 getTextColorOfTextView() 方法返回的结果。
     * @return 返回一个以View 的hascode 为主键，其颜色值为值的Map。
     */
    public static void setTextColorOfTextView(View view,Map<Integer,Integer> colorMap){
        if (view instanceof ViewGroup){
            //view 是一个容器，迭代它的所有子元素。
            ViewGroup vg = (ViewGroup)view;
            int count = vg.getChildCount();

            for (int index=0 ;index<count ;index++){
                setTextColorOfTextView(vg.getChildAt(index),colorMap);
            }
        }
        else if (view instanceof TextView){
            int hasCode = view.hashCode();
            if (colorMap.containsKey(hasCode)){
                ((TextView) view).setTextColor(colorMap.get(hasCode));
            }
        }
    }
}
