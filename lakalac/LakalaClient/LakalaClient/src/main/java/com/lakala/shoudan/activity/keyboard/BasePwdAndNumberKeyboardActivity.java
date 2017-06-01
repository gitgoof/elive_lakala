package com.lakala.shoudan.activity.keyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.ClearEditText;

import java.util.HashMap;
import java.util.Map;

public class BasePwdAndNumberKeyboardActivity extends AppBaseActivity
        implements View.OnTouchListener, View.OnFocusChangeListener  {
    private final HintFocusChangeListener hintFocusListener;
    /**当前数字键盘的拥有者*/
    private EditText   numberKeyboardOwne;
    /**数字键盘上的完成按钮*/
    public  Button     doneButton;
    /**数字键盘的根布局*/
    private ViewGroup  numberKeyboardRoot;
    /**数字键盘所在的Activity*/
    private Activity   numberKeyboardActivity;
    /**文本框列表*/
    private Map<Integer,Integer> editTextMap;
    /**Activity 根布局*/
    private ViewGroup  rootLayout;

    private CustomNumberKeyboard numberKeyboard;

    /**
     * false:按返回按键时键盘正在显示就隐藏
     * true:按返回按键时键盘正在显示就隐藏，再回调完成
     */
    private boolean isHintOnBack=false;

    @SuppressLint("UseSparseArrays")
    public BasePwdAndNumberKeyboardActivity (){
        super();

        editTextMap = new HashMap<Integer,Integer>();
        hintFocusListener = new HintFocusChangeListener();
        hintFocusListener.setUseCustomKeyboard(true);
    }

    protected void setAmountKeyboard(boolean amountKeyboard){
        numberKeyboard.setAmountKeyboard(amountKeyboard);
    }
    protected void hideSoftInputFromWindow(){
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v instanceof ClearEditText){
            ClearEditText clearEditText = (ClearEditText)v;
            clearEditText.onFocusChange(v,hasFocus);
        }
        if (v instanceof EditText){
            hintFocusListener.onFocusChange(v, hasFocus);
            if(hasFocus) {
                //数字输入框得到焦点，显示键盘。
                if (editTextMap.containsKey(v.hashCode())){
                    CommonUtil.hideKeyboard(v);
                    showNumberKeyboard((EditText)v,editTextMap.get(v.hashCode()));
                }
            }else{
                //如果是输入框焦点丢失隐藏键盘
                hideNumberKeyboard((EditText)v);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (editTextMap.containsKey(v.hashCode())){
            //当输入框被触碰时，如果有系统键盘，先隐藏系统键盘
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    CommonUtil.hideKeyboard(v);
                    return false;

                case MotionEvent.ACTION_UP :
                    //只在up事件弹出数字键盘
                    showNumberKeyboard((EditText)v,editTextMap.get(v.hashCode()));
                    if(v instanceof ClearEditText){
                        ClearEditText clearEditText = (ClearEditText)v;
                        clearEditText.clearText(event);
                    }
                    v.requestFocus();
                    //点击可以修改edittex光标位置
                    EditText editText = (EditText) v;
                    int off = getOffsetAtText(editText, event);
                    Selection.setSelection(editText.getEditableText(), off);
                    return true;
            }
        }
        return false;
    }
    int getOffsetAtText(EditText editText,MotionEvent event) {
        Layout layout = editText.getLayout();
        int offset = 0;
        if(layout == null) {
            return offset;
        }
        float x = event.getX()+editText.getScrollX();
        offset = layout.getOffsetForHorizontal(0,x);
        if(offset > 0){
            if(x > layout.getLineMax(0)){
                return offset;
            }else {
                return offset-1;
            }
        }
        //Log.i(TAG, "line = " + line + ", offset = " + offset);
        return offset;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_keypad_hide:
                //点击自定义数字键盘"完成"按钮，隐藏键盘
                hideNumberKeyboard();
                if(onDoneButtonClickListener != null){
                    onDoneButtonClickListener.onClick(v);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isInitNumberKeyboard()){
            //用户按下了物理返回键，如果自定义键盘正在显示，则它隐藏并忽略返回操作。
            if (isShowNumberKeyboard()){
                if(isHintOnBack){
                    if(onDoneButtonClickListener != null){
                        onDoneButtonClickListener.onClick(doneButton);
                    }
                }
                //隐藏键盘
                hideNumberKeyboard();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化数字键盘，调用此方法时必须保证数字键盘组件（@layout/keyboard）、
     * 已经加载到当前布局中，否则会抛出异常。
     */
    public void initNumberKeyboard(){
        if (isInitNumberKeyboard()) return;

        numberKeyboardActivity = this;

        rootLayout = (ViewGroup)((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

        //数字键盘根布局
        numberKeyboardRoot = (ViewGroup)findViewById(R.id.id_keypad_root_layout);

        //自定义数字键盘完成按钮
        doneButton = (Button)findViewById(R.id.id_keypad_hide);
        doneButton.setOnClickListener(this);

        numberKeyboard = new CustomNumberKeyboard(numberKeyboardActivity, null,false);
    }
    private View.OnClickListener onDoneButtonClickListener;

    public void setOnDoneButtonClickListener(View.OnClickListener onDoneButtonClickListener) {
        this.onDoneButtonClickListener = onDoneButtonClickListener;
    }

    /**
     * 初始化数字键盘，keyboard 必须密键盘组件所在的 Activity
     * （必须包含布局文件  @layout/keyboard），否则会抛出异常。
     * @param keyboard
     */
    public void initNumberKeyboard(Activity keyboard){
        if (isInitNumberKeyboard()) return;

        numberKeyboardActivity = keyboard;

        rootLayout = (ViewGroup)((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

        //数字键盘根布局
        numberKeyboardRoot = (ViewGroup)numberKeyboardActivity.findViewById(R.id.id_keypad_root_layout);

        //自定义数字键盘完成按钮
        doneButton = (Button)numberKeyboardActivity.findViewById(R.id.id_keypad_hide);
        doneButton.setOnClickListener(this);

        numberKeyboard = new CustomNumberKeyboard(numberKeyboardActivity, null,false);
    }

    /**
     * 添加数字编辑框
     * @param edit    数字输入框
     * @param type    输入框类型<br>
     * CustomNumberKeyboard.EDIT_TYPE_CARD    卡号<br>
     * CustomNumberKeyboard.EDIT_TYPE_INTEGER 整数<br>
     * CustomNumberKeyboard.EDIT_TYPE_FLOAT   浮点<br>
     * CustomNumberKeyboard.EDIT_TYPE_IDCARD  身份证<br>
     * @param length  数字最大长度，<= 0 不限制。
     */
    protected void initNumberEdit(EditText edit,int type,int length){
        edit.setLongClickable(false);
        editTextMap.put(edit.hashCode(), type);

        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);

        edit.setOnTouchListener(this);
        edit.setOnFocusChangeListener(this);

        if(length > 0){
            TextWatcher textWatcher = new NumberInputChangedListener(length,type);
            edit.addTextChangedListener(textWatcher);
        }
    }

    /**
     * 当数字键盘的可见性改变时触发该事件。
     * @param isShow  键盘是否显示。
     * @param height  键盘的高度，像素值。
     */
    protected void onNumberKeyboardVisibilityChanged(boolean isShow,int height){}

    /**
     * 显示数字键盘，并将它与 EditText 绑定。
     * @param editText 要与数字键盘绑定的 EditText
     * @param type    输入框类型<br>
     * CustomNumberKeyboard.EDIT_TYPE_CARD    卡号<br>
     * CustomNumberKeyboard.EDIT_TYPE_INTEGER 整数<br>
     * CustomNumberKeyboard.EDIT_TYPE_FLOAT   浮点<br>
     */
    protected void showNumberKeyboard(EditText editText,int type){
        if (!isInitNumberKeyboard()) return;
        numberKeyboard.showKeyboardView(editText,type);
        numberKeyboardOwne = editText;
        doneButton.setOnClickListener(this);
        onNumberKeyboardVisibilityChanged(true,numberKeyboardRoot.getHeight());
    }

    /**
     * 隐藏数字键盘，如果数字键盘没有显示该方法不做任何操作，如果数字
     * 键盘已显示，则 target 必须是与数字键盘绑定的 EditText 才能关
     * 闭数字键盘，否则不会关闭数字键盘。键盘关闭后会解除与EditText
     * 之间的绑定。
     * @param edit 要解除绑定的 EditTextt
     */
    protected void hideNumberKeyboard(EditText edit){
        if (!isInitNumberKeyboard()) return;

        //如果将要与数字键盘解除绑定EditText与当前拥有者
        //不同，则不能隐藏键盘。
        if (edit.equals(numberKeyboardOwne)){
            numberKeyboard.hideKeyboardView();
            numberKeyboardOwne = null;
            onNumberKeyboardVisibilityChanged(false,numberKeyboardRoot.getHeight());
        }
    }

    /**
     * 隐藏数字键盘，键盘关闭后会解除与EditText之间的绑定。
     */
    protected void hideNumberKeyboard(){
        if (numberKeyboardOwne != null){
            hideNumberKeyboard(numberKeyboardOwne);
        }
    }

    protected void resizeScrollView(ViewGroup layoutView){
        //键盘高
        int height = 0;

        if (isShowNumberKeyboard()){
            height = getNumberKeyboardHeight();
        }

        //获得状态栏的高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //ScrollView 的 top值。
        int scrollViewtop = layoutView.getTop();

        int newHeight;

        //将ScrollView 的高度调整为 ： 屏幕高度 - 键盘高度 - 状态栏高 - ScrollView的Top。
        newHeight = Parameters.screenHeight - scrollViewtop - statusBarHeight - height-100;
        LayoutParams params = layoutView.getLayoutParams();
        params.height = newHeight;
        layoutView.setLayoutParams(params);
    }

    /**
     * 判断数字键盘是否显示
     * @return
     */
    protected boolean isShowNumberKeyboard(){
        if (!isInitNumberKeyboard()) return false;

        return numberKeyboard.getVisibility() == View.VISIBLE;
    }

    /**
     * 获取数字键盘的高
     * @return
     */
    protected int getNumberKeyboardHeight(){
        if (!isInitNumberKeyboard()) return 0;

        return numberKeyboardRoot.getHeight();
    }

    /**
     * 移动焦点到下个组件
     * @param focused 当前拥有焦点的view.
     */
    private void moveFocusToNext(View focused){
        if (!isInitNumberKeyboard()) return;

        View view = rootLayout.focusSearch(focused,View.FOCUS_DOWN);
        if (view != null){
            view.requestFocus();
            return;
        }
    }

    /**
     * 是否已经初始化过键盘
     * @return
     */
    public boolean isInitNumberKeyboard(){
        return numberKeyboardRoot != null;
    }

    /**
     * 数字输入完成
     */
    private void inputNumberComplete(){
        View focused = numberKeyboardOwne;

        hideNumberKeyboard();

        moveFocusToNext(focused);
    }

    /**
     * 数字框输入监听器
     * @author 葛威
     */
    protected class NumberInputChangedListener implements TextWatcher {
        //数字最大长度
        private int maxLength;
        //输入类型
        private int type;

        public NumberInputChangedListener (int maxLength,int type){
            this.maxLength = maxLength;
            this.type = type;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {
            if ( type == CustomNumberKeyboard.EDIT_TYPE_INTEGER && s.length() > 0 && s.charAt(0) == '0'){
                //如果是输入整数，则不充许在第一位输入0
                s.clear();
            }
            else if (s.length() == maxLength) {
                //输入六位后自动隐藏键盘
                inputNumberComplete();
            }
            else if (s.length() > maxLength) {
                //再次弹出键盘，尝试输入的话，禁止输入第7位
                s.delete(maxLength, maxLength + 1);
            }
        }
    }

    public void setHintOnBack(boolean hintOnBack) {
        isHintOnBack = hintOnBack;
    }

}
