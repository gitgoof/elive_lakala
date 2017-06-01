package com.lakala.shoudan.activity.keyboard;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.lakala.shoudan.R;

/**
 * Created by HUASHO on 2015/1/26.
 * 自定义键盘View类
 * 使用自定义键盘需要将键盘类布局内嵌到使用的layout中
 */
public class CustomNumberKeyboard  implements View.OnTouchListener{
    public static final int EDIT_TYPE_CARD    = 0;
    public static final int EDIT_TYPE_INTEGER = 1;
    public static final int EDIT_TYPE_FLOAT   = 2;
    public static final int EDIT_TYPE_IDCARD = 3;

    private EditText mEditText;
    private LinearLayout keyboardLayout;

    private ImageButton[] buttons = new ImageButton[15];

    private int[] 	 button_number_ids = {
            R.id.id_keypad_0,
            R.id.id_keypad_1,
            R.id.id_keypad_2,
            R.id.id_keypad_3,
            R.id.id_keypad_4,
            R.id.id_keypad_5,
            R.id.id_keypad_6,
            R.id.id_keypad_7,
            R.id.id_keypad_8,
            R.id.id_keypad_9,
    };

    private ImageButton   button_del;
    private ImageButton   button_comma;
    private ImageButton   button_c;
    private ImageButton   button_00;
    private Button   button_hide;
    private Activity activity;

    private boolean amountKeyboard = false;

    /**
     * 自定义键盘View
     * @param activity 使用的上下文
     * @param editText 使用自定义键盘的view
     * @param isUsedForCard true 卡号键盘  false 为数字金额键盘
     */
    public CustomNumberKeyboard(Activity activity,EditText editText,boolean isUsedForCard) {
        this.activity = activity;

        keyboardLayout = (LinearLayout)activity.findViewById(R.id.id_keypad_root_layout);
        keyboardLayout.setKeepScreenOn(false);

        button_del 		= (ImageButton)activity.findViewById(R.id.id_keypad_del);//删除
        button_comma 		= (ImageButton)activity.findViewById(R.id.id_keypad_comma);//小数点
        button_c 		= (ImageButton)activity.findViewById(R.id.id_keypad_c);//清除键
        button_00 		= (ImageButton)activity.findViewById(R.id.id_keypad_00);//00键
        button_hide 		= (Button)activity.findViewById(R.id.id_keypad_hide);//隐藏键

        button_comma.setTag(".");
        button_00.setTag("00");

        for (int i = 0; i < button_number_ids.length; i++) {
            buttons[i] = (ImageButton)activity.findViewById(button_number_ids[i]);
            buttons[i].setTag(i + "");
        }

        showKeyboardView(editText,isUsedForCard ? EDIT_TYPE_CARD : EDIT_TYPE_FLOAT);
    }

    public void setAmountKeyboard(boolean amountKeyboard){
        this.amountKeyboard = amountKeyboard;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Editable editable 	= mEditText.getEditableText();
        int start 			= mEditText.getSelectionStart();

        if (event.getAction() == MotionEvent.ACTION_UP) {

            if(!amountKeyboard){
                switch(v.getId()){
                    case R.id.id_keypad_del:
                        if (editable != null && editable.length() > 0 && start > 0) {
                            editable.delete(start - 1, start);
                        }
                        break;

                    case R.id.id_keypad_c:
                        editable.clear();
                        break;

                    default:
                        String text =(String)((ImageButton)v).getTag();
                        if (text.equals(".")) {
                            if (!editable.toString().contains(".")) {
                                editable.insert(start, text);
                            }
                        }else{
                            editable.insert(start, text);
                        }

                        break;
                }
            }else{
                switch(v.getId()){
                    case R.id.id_keypad_del:
                        if (editable != null && editable.length() > 0 && start > 0) {
                            StringBuffer amt = new StringBuffer(editable.toString().replace(",", ""));
                            if(amt.length() >0)
                                amt.delete(amt.length()-1, amt.length());
                            else {
                                amt.append("0");
                            }
                            editable.clear();
                            editable.append(addSeparator(amt.toString()));

                        }
                        if(editable.length() ==0){
                            editable.append("0");
                        }
                        break;

                    case R.id.id_keypad_c:
                        editable.clear();
                        editable.append("0");
                        break;

                    default:
                        String text =(String)((ImageButton)v).getTag();
                        if(TextUtils.equals(text,"X")){
                            editable.append(text);
                        }else if (text.equals(".")) {
                            if (!editable.toString().contains(".")) {

                                editable.append(text);
                            }
                        }else{
                            if(editable.length() ==1 && editable.toString().equals("0")){
                                editable.delete(0, editable.length());
                            }
                            if(editable.length() == 0){
                                editable.append(text);
                            }else
                                editable.append(text);
                            String amt = addSeparator(editable.toString().replace(",", ""));
                            editable.delete(0,editable.length());
                            editable.append(amt);
                        }

                        break;
                }
            }


        }

        return false;
    }

    private String addSeparator(String amt){
        int dot = amt.indexOf(".") == -1 ? 0 : amt.indexOf(".");
        StringBuffer decimal = new StringBuffer();
        if(dot > 0){
            decimal.append(amt.substring(dot, amt.length()));
        }
        StringBuffer sb = new StringBuffer();
        char[] charArray;
        if(dot == 0){
            charArray = amt.toCharArray();
        }else{
            charArray = amt.substring(0, dot).toCharArray();
        }
        int amtSeparator = 1;
        for (int i = charArray.length -1 ; i >= 0; i--) {

            if(sb.length() == 0){
                sb.append(charArray[i]);
            }else{
                sb.insert(0, charArray[i]);
            }
            if (amtSeparator % 3 == 0 && i != 0)
                sb.insert(0,",");
            amtSeparator++;
        }
        sb.append(decimal);
        return sb.toString();
    }

    /**
     * 显示键盘
     * @param editText 使用自定义键盘的view
     * @param type    输入框类型<br>
     * CustomNumberKeyboard.EDIT_TYPE_CARD    卡号<br>
     * CustomNumberKeyboard.EDIT_TYPE_INTEGER 整数<br>
     * CustomNumberKeyboard.EDIT_TYPE_FLOAT   浮点<br>
     */
    public void showKeyboardView(EditText editText,int type) {
        mEditText = editText;
        setKeyboardStyle(type);

        if (editText == null) return;

        if (keyboardLayout.getVisibility() == View.GONE || keyboardLayout.getVisibility() == View.INVISIBLE) {
            keyboardLayout.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < button_number_ids.length; i++) {
            buttons[i].setOnTouchListener(this);
        }

        button_c.setOnTouchListener(this);
        button_del.setOnTouchListener(this);
        button_del.setLongClickable(true);

    }

    //隐藏键盘
    public void hideKeyboardView() {
        if (keyboardLayout.getVisibility() == View.VISIBLE) {
            keyboardLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 键盘是否显示
     * @return true 显示状态 false 隐藏状态
     */
    public int getVisibility() {
        //键盘没初始化而按返回键时，keyboardLayout为null
        if (keyboardLayout == null) {
            return View.INVISIBLE;
        }else {
            return keyboardLayout.getVisibility();
        }
    }

    private void setKeyboardStyle(int type){
        switch(type){
            case EDIT_TYPE_CARD://卡类型
                button_comma.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_gray_white_selector));
//			button_comma.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_gray_white_selector));
                button_00.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.key_00_disable));
                button_00.setEnabled(false);
                button_00.setVisibility(View.GONE);
                button_comma.setEnabled(false);
                button_00.setOnTouchListener(null);
                button_comma.setOnTouchListener(null);
                break;

            case EDIT_TYPE_INTEGER://数字
                button_comma.setImageDrawable(activity.getResources().getDrawable(R.drawable.btn_gray_white_selector));
                button_00.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.key_00_selector));
                button_00.setEnabled(true);
                button_00.setVisibility(View.GONE);
                button_comma.setEnabled(false);
                button_00.setOnTouchListener(this);
                button_comma.setOnTouchListener(null);
                break;
            case EDIT_TYPE_IDCARD:
                button_comma.setImageResource(R.drawable.lakala_icon_x);
                button_00.setBackgroundResource(R.drawable.key_00_selector);
                button_00.setEnabled(true);
                button_00.setVisibility(View.GONE);
                button_comma.setEnabled(true);
                button_00.setOnTouchListener(this);
                button_comma.setOnTouchListener(this);
                button_comma.setTag("X");
                break;
            case EDIT_TYPE_FLOAT:
            default:
                button_comma.setImageDrawable(activity.getResources().getDrawable(R.drawable.lakala_icon_point));
                button_00.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.key_00_selector));
                button_00.setVisibility(View.GONE);
                button_00.setEnabled(true);
                button_comma.setEnabled(true);
                button_00.setOnTouchListener(this);
                button_comma.setOnTouchListener(this);
                button_comma.setTag(".");
                break;
        }
    }
}
