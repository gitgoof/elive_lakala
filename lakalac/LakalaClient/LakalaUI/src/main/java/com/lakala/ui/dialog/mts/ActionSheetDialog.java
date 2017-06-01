package com.lakala.ui.dialog.mts;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

/**
 * AcitonSheet Dialog
 * Created by lvyong on 15/4/24.
 */
public class ActionSheetDialog extends BaseDialog {

    private View rootView;
    private LinearLayout selectLayout;
    private TextView cancleTextView;

    //表示传入的参数的key
    public static final String BUTTON_TEXT = "BUTTON_TEXT";
    private ActionSheetDialogClickCallback actionSheetDialogClickCallback;

    /**
     * 添加空的构造方法
     * 尝试解决crash #263的问题 com.lakala.ui.a.a; no empty constructor
     */
    public ActionSheetDialog(){
        super();
    }

    /**
     * 默认构造器
     */
    public ActionSheetDialog(FragmentManager fragmentManager){
        super(fragmentManager, DialogFragment.STYLE_NO_TITLE, R.style.transparent_dialog);
    }

    /**
     * 设置AlertDialog的标题样式，及主题样式
     * @param style  style的值只能是 DialogFragement.STYLE_NORMAL,
     *               DialogFragement.STYLE_NO_FRAME,DialogFragement.STYLE_NO_INPUT,DialogFragement.STYLE_NO_TITLE
     * @param theme  样式id
     */
    public ActionSheetDialog(FragmentManager fragmentManager,int style, int theme) {
        super(fragmentManager, style, theme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //红米手机上，弹出Dialog时，容易报空指针异常，v4包的bug，故在这里try catch
        try {
            super.onActivityCreated(savedInstanceState);
        }catch (Exception e){}
    }

    /**
     * 创建view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null == rootView){
            rootView = inflater.inflate(R.layout.action_sheet_dialog_layout,container,false);
            initView();
        }else{
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    /**
     * 初始化view
     */
    private void initView(){
       selectLayout = (LinearLayout) rootView.findViewById(R.id.action_sheet_dialog_middle_layout);
       cancleTextView = (TextView)rootView.findViewById(R.id.action_sheet_dialog_cancel);
       cancleTextView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismiss();
           }
       });

       Bundle bundle = getArguments();
       if(null != bundle){
           String button_text = bundle.getString(BUTTON_TEXT);
           if(!TextUtils.isEmpty(button_text)){
               String[] array = button_text.split("\\|");
               int length = array.length;
               for(int i=0; i < length; i++){
                  View view = createSingleTextView(array[i]);
                  float dimen = getResources().getDimension(R.dimen.dimen_88);
                  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)dimen);
                  params.topMargin = Dimension.dip2px(8, getActivity());
                  selectLayout.addView(view,params);
               }
           }
       }
    }


    /**
     * 创建单个TextView
     * @param buttonLabel
     * @return
     */
    private TextView createSingleTextView(final String buttonLabel){
      final TextView textView = new TextView(getActivity());
      float dimen = getResources().getDimension(R.dimen.dimen_88);
      textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)dimen));
      textView.setBackgroundResource(R.drawable.ui_common_item_selector);
      textView.setTextColor(ColorStateList.valueOf(R.color.color_blue_50bdef));
      textView.setGravity(Gravity.CENTER);


      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP ,16);
      textView.setText(buttonLabel);
      textView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             if(null != actionSheetDialogClickCallback){
                 actionSheetDialogClickCallback.click(buttonLabel,textView,ActionSheetDialog.this);
             }
          }
      });
      return textView;
    }

    /**
     * 设置ActionSheetDialogClickCallBack点击回调
     * @param callback
     */
    public void setActionSheetDialogClickCallback(ActionSheetDialogClickCallback callback){
        this.actionSheetDialogClickCallback = callback;
    }


    /**
     *  ActionSheetDialog button 点击回调
     *
     */
    public static  interface  ActionSheetDialogClickCallback {
        /**
         * button 点击
         * @param buttonLable button's label
         * @param view  被点击的v对象
         */
        public void click(String buttonLable, View view, ActionSheetDialog actionSheetDialog);

    }
}
