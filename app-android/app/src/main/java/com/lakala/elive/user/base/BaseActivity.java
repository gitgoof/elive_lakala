package com.lakala.elive.user.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.utils.ActivityTaskCacheUtil;


/**
 *
 * User Activity 基础类放一些公共的方法
 *
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener,ApiRequestListener {

    /**
     *  ** 通用组件  **
     */
    //标题文本框
    protected TextView   tvTitleName;

    //返回图片按钮
    protected ImageView iBtnBack;

    //进度条
    protected ProgressDialog mProgressDialog;

    //提示框
    protected AlertDialog mAlertDialog;

    //返回图片按钮
    protected Button btnCancel;


    //缓存Session
    protected Session mSession;


    /** 应用Session */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTaskCacheUtil.getIntance().addActivity(this);
        mSession = Session.get(this);

        setContentViewId();//设置UI布局文件

        if(findViewById(R.id.tv_title_name)!=null){
            tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        }

        if(findViewById(R.id.btn_iv_back)!=null){
            iBtnBack = (ImageView) findViewById(R.id.btn_iv_back);
        }

        if(findViewById(R.id.btn_action)!=null){
            btnCancel = (Button) findViewById(R.id.btn_action);
        }

        //绑定页面组件
        bindView();

        //处理页面组件事件
        bindEvent();

        //初始化数据
        bindData();
    }

    /**
     * 设置页面UI布局文件
     */
    protected abstract  void setContentViewId();

    /**
     * 绑定页面组件
     */
    protected abstract  void bindView();

    /**
     * 处理页面组件事件
     */
    protected abstract  void bindEvent();

    /**
     * 加载初始化数据
     */
    protected abstract  void bindData();


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public DialogInterface.OnClickListener alertListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    if(mAlertDialog!=null){
                        mAlertDialog.cancel();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public void showAlerTitle(String message,Boolean mCancel) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this).create();
        }
        mAlertDialog.setTitle(getString(R.string.alert_err_title));
        mAlertDialog.setMessage(message);

        if(mCancel!=null){
            mAlertDialog.setCancelable(mCancel);
        }
        mAlertDialog.setButton(getString(R.string.ok), alertListener);
        mAlertDialog.show();
    }

    public void showProgressDialog(String message) {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    public void closeProgressDialog() {
        if(mProgressDialog != null&&mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(int method, Object obj) {

    }

    @Override
    public void onError(int method, String statusCode) {

    }

    @Override
    public void onClick(View v) {

    }


    protected  <VT extends View> VT findView(int id) {
        return (VT) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTaskCacheUtil.getIntance().delActivity(this);
    }
}
