package com.lakala.shoudan.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.component.KeyBoardPopWindow;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by HJP on 2015/12/8.
 */
public class BaseSafeManageActivity extends AppBaseActivity {
    protected int[] ids = {
            R.id.iv_1, R.id.iv_2, R.id.iv_3, R.id.iv_4, R.id.iv_5, R.id.iv_6};
    protected KeyBoardPopWindow keyBoardPopWindow;
    protected static Stack<String> inputStack = new Stack<String>();
    protected TextView tvNotice;
    protected Button btnNext;
    protected View llTexts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_paypwd);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        clear();
        initView();
        initUI();
        initKeyBoard();
        initListener();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.post(
                new Runnable() {
                    @Override
                    public void run() {
                        keyBoardPopWindow.show();
                    }
                }
        );
    }

    protected void initView(){
        tvNotice=(TextView)findViewById(R.id.notice);
        btnNext=(Button)findViewById(R.id.id_common_guide_button);
        llTexts=(View)findViewById(R.id.ll_texts);
    }
    protected void initListener(){
        llTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!keyBoardPopWindow.isShowing()) {
                    keyBoardPopWindow.show();
                }
            }
        });
    }

    /**
     * 初始化键盘
     */
    protected void initKeyBoard(){
        keyBoardPopWindow = new KeyBoardPopWindow(context);
        keyBoardPopWindow.setNoPressedKeyBoardStyle();
        keyBoardPopWindow.setCommaVisible(false);
        keyBoardPopWindow.setCompleteGone(false);
        keyBoardPopWindow.setOnKeyClickListener(
                new KeyBoardPopWindow.OnKeyClickListener() {
                    @Override
                    public void onKeyClicked(PopupWindow window, View view, String text) {
                        int size = inputStack.size();
                        if (size >= 0 && size < 6) {
                            inputStack.push(text);
                            int viewId = ids[size];
                            setPwdChecked(viewId,true);
                        }
                    }

                    @Override
                    public void onDelete() {
                        int size = inputStack.size();
                        if (size > 0 && size <= 6) {
                            inputStack.pop();
                            int viewId = ids[size - 1];
                            setPwdChecked(viewId,false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        keyBoardPopWindow.dismiss();
                    }
                }
        );
    }
    private void setPwdChecked(int id,boolean checked){
        ImageView view = (ImageView)findViewById(id);
        if (checked){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.INVISIBLE);
        }
    }
    public void clear(){
        inputStack.clear();
        for(int id:ids){
            setPwdChecked(id,false);
        }
    }
    public String getString(Stack<String> stack){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()){
            stringBuilder.append(iterator.next());
        }
        return stringBuilder.toString();
    }
    @Override
    protected void onDestroy() {
        if(keyBoardPopWindow != null){
            keyBoardPopWindow.dismiss();
        }
        super.onDestroy();
    }
}
