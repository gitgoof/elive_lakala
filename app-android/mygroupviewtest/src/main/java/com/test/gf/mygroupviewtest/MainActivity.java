package com.test.gf.mygroupviewtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myViewGroup = (MyViewGroup) findViewById(R.id.self_view_group);
        mTvTextSize = (TextView) findViewById(R.id.tv_main_textsize_show);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;

        mEditFocus = (EditText) findViewById(R.id.edit_input_test_focus);
        mEditFocus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("g--","onFocusChange:" + hasFocus);
            }
        });
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Log.i("g--","source:" + source.toString() + "<start>" + start + "<end>" + end
                        + "<spanned>" + dest.toString() + "<dstart>" + dstart + "<dend>" + dend);
                return null;
            }
        };

        final int mMax = 5;
        InputFilter inputFilter2 = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Log.i("g--","source:" + source.toString() + "<start>" + start + "<end>" + end
                        + "<spanned>" + dest.toString() + "<dstart>" + dstart + "<dend>" + dend);
                int keep = mMax - (dest.length() - (dend - dstart));
                Log.i("g--","keep====" + keep);
                if (keep <= 0) {
                    Log.i("g--","run one ====" + keep);
                    return "";
                } else if (keep >= end - start) {
                    return null; // keep original
                } else {
                    keep += start;
                    if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                        --keep;
                        if (keep == start) {
                            return "";
                        }
                    }
                    return source.subSequence(start, keep);
                }
            }
        };
        final int maxLength = 5;
        InputFilter inputFilter3 = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int destLength = 0;
                Log.i("g--","现存的dest:" + dest);
                for(int i = 0;i < dest.length();i++){
                    final char c = dest.charAt(i);
                    if(c< 128){
                        destLength+=1;
                    } else {
                        destLength+=2;
                    }
                    Log.i("g--","source开始:" + source + "<dest>" + dest + "<index>" + i);
                    if(destLength>maxLength){
                        return dest.subSequence(0,i-1);
                    }
                }
//                Log.i("g--","source开始:" + source + "<index>" + destLength);
                if(end>start){

                    int index = 0;
                    while(destLength<maxLength && index<source.length()){
                        char c = source.charAt(index++);
                        if(c< 128){
                            destLength+=1;
                        } else {
                            destLength+=2;
                        }
                    }
                    if(destLength>maxLength){
                        return "";
                    }
                }

                return null;
            }
        };
        final int maxLen = 5;
        InputFilter inputFilter1 = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int dindex = 0;
                int count = 0;
                Log.i("g--","现存的dest:" + dest);
                while (count <= maxLen && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                Log.i("g--","原始字符:" + source + "<index>" + dindex);
                if (count > maxLen) {
                    return dest.subSequence(0, dindex - 1);
                }
                int sindex = 0;
                while (count <= maxLen && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > maxLen) {
                    sindex--;
                }
//                Log.i("g--","字符:" + source + "<index>" + sindex);
                return source.subSequence(0, sindex);
            }
        };
        mEditFocus.setFilters(new InputFilter[]{inputFilter3});
    }

    private float density = 0;

    private EditText mEditFocus;

    private TextView mTvTextSize;
    public void toShowTextSize(View view){
//        initText("第一部分" + String.valueOf(density),"第二部分");

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        alertDialog.setContentView(R.layout.dialog_check_task_layout);
//        alertDialog.getWindow().setContentView(R.layout.dialog_check_task_layout);
        final View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.tv_dialog_check_task_continue:
                        Toast.makeText(MainActivity.this,"continue",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_dialog_check_task_new:
                        Toast.makeText(MainActivity.this,"tonew",Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.tv_dialog_check_task_cancel:
                        Toast.makeText(MainActivity.this,"cancel",Toast.LENGTH_SHORT).show();

                        if( alertDialog!= null && alertDialog.isShowing()){
                            alertDialog.dismiss();
                        }
                        break;
                }
            }
        };
        alertDialog.findViewById(R.id.tv_dialog_check_task_continue).setOnClickListener(listener);
        alertDialog.findViewById(R.id.tv_dialog_check_task_new).setOnClickListener(listener);
        alertDialog.findViewById(R.id.tv_dialog_check_task_cancel).setOnClickListener(listener);

        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();

    }
    public interface OnClickForDialog{
        void continueClick();
        void toNewClick();
        void toCancelClick();
    }

    private void initText(String begin,String end){
        final String content = begin + "" + end;
        // 易变化的--
        Spannable spannable = new SpannableString(content);

        spannable.setSpan(new AbsoluteSizeSpan(14*(int)density),begin.length(),content.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(18*(int)density),0,begin.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mTvTextSize.setText(spannable);

        // 不变的字符串-- 无法改变内部的状态
        Spanned spanned = new SpannedString("ddd");

        // 文字可以增加和减少，更加灵活，但消耗性能
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("dfa");

    }

    private MyViewGroup myViewGroup;

    public void toAddView(View view){
        myViewGroup.addChildView(new String[]{"我的第一个","我的第二个","傻子","二愣子"},
                new MyViewGroup.OnSlefItemClickListener(){
                    @Override
                    public void onClickView(int position, String string) {
                        Toast.makeText(MainActivity.this,"position:" + position + "<>" + string,Toast.LENGTH_SHORT).show();
                    }
                },0);
    }

    public void createFile(View view){
        /*
        File file = new File(Environment.getExternalStorageDirectory(),"GGG");

        if(!file.exists()){
            file.mkdirs();
        }
        */

//        String email = "1uuaisdf你好@23123cc.xx";
//        boolean result = email.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");

        String string = "01234536";
        System.out.println("结果:" + string.matches("^\\+?\\d+$"));
        Log.i("g--","结果:" + string.matches("^\\+?\\d+$"));
//        System.out.println("判断结果:" + result);
//        Log.i("g--","result:" + result);
        popWindowForCall(view);
    }
    private PopupWindow mPopupWindow;
    private void popWindowForCall(View view){
        if(mPopupWindow == null){
            mPopupWindow = new PopupWindow(MainActivity.this);
            mPopupWindow.setContentView(View.inflate(MainActivity.this,R.layout.pop_bottom_layout,null));
            mPopupWindow.setOutsideTouchable(true);

            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mPopupWindow.getContentView();
        if(mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
        mPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM,0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myViewGroup!= null){
            myViewGroup.removeSelfListener();
        }
    }
}
