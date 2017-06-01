package com.lakala.elive.merapply.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener, ApiRequestListener {

    /**
     * ** 通用组件  **
     */
    //标题文本框
    protected TextView tvTitleName;

    //返回图片按钮
    protected ImageButton iBtnBack;

    //进度条
    protected ProgressDialog mProgressDialog;

    //提示框
    protected AlertDialog mAlertDialog;

    //返回图片按钮
    protected Button btnCancel;


    //缓存Session
    protected Session mSession;

    private View mContentView;

    protected Activity _activity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this._activity = activity;

    }

    /**
     * 应用Session
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentView = inflater.inflate(setContentViewId(), container, false);
        mSession = Session.get(_activity);

        setContentViewId();//设置UI布局文件

        if (findView(R.id.tv_title_name) != null) {
            tvTitleName = findView(R.id.tv_title_name);
        }

        if (findView(R.id.btn_iv_back) != null) {
            iBtnBack = findView(R.id.btn_iv_back);
        }

        if (findView(R.id.btn_action) != null) {
            btnCancel = findView(R.id.btn_action);
        }

        //绑定页面组件
        bindView();

        //处理页面组件事件
        bindEvent();

        //初始化数据
        bindData();

        return mContentView;
    }

    /**
     * 设置页面UI布局文件
     */
    protected abstract int setContentViewId();

    /**
     * 绑定页面组件
     */
    protected abstract void bindView();

    /**
     * 处理页面组件事件
     */
    protected abstract void bindEvent();

    /**
     * 加载初始化数据
     */
    protected abstract void bindData();


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public DialogInterface.OnClickListener alertListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    if (mAlertDialog != null) {
                        mAlertDialog.cancel();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public void showAlerTitle(String message, Boolean mCancel) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(_activity).create();
        }
        mAlertDialog.setTitle(getString(R.string.alert_err_title));
        mAlertDialog.setMessage(message);

        if (mCancel != null) {
            mAlertDialog.setCancelable(mCancel);
        }
        mAlertDialog.setButton(getString(R.string.ok), alertListener);
        mAlertDialog.show();
    }

    public void showProgressDialog(String message) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(_activity);
        }
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    public void closeProgressDialog() {
        if (mProgressDialog != null) {
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


    protected <VT extends View> VT findView(int id) {
        return (VT) mContentView.findViewById(id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeProgressDialog();
    }

    /**
     * 字段判空 去空
     *
     * @return
     */
    protected String getNoEmptyString(String s) {
        return TextUtils.isEmpty(s) ? "" : s;
    }


    protected String title;
    /**
     * 得到标题
     * @return
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
