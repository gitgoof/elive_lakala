package com.lakala.shoudan.activity.shoudan.finance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.question.VerifyQuestionActivity;
import com.lakala.shoudan.activity.wallet.request.CheckPayPwdParams;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.KeyBoardPopWindow;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by LMQ on 2015/10/14.
 */
public class InputPayPwdDialogActivity extends FragmentActivity {
    protected KeyBoardPopWindow keyBoardPopWindow;
    protected Stack<String> inputStack = new Stack<String>();
    int[] ids = {
            R.id.iv_1, R.id.iv_2, R.id.iv_3, R.id.iv_4, R.id.iv_5, R.id.iv_6};
    ImageView ivClose;
    TextView tvForget;
    protected TextView tvOk;
    private View keyBoardContainer;
    protected InputPayPwdDialogActivity context;
    private ProgressDialog progressDialog;
    private boolean shouldCheckPwd = true;
    private LinearLayout ll_pwd_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.dialog_pay_pwd_input);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
        initAction();
        initKeyBoard();
    }

    private void initKeyBoard() {
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
                            findViewById(viewId).setSelected(true);
                            tvOk.setEnabled(inputStack.size() == 6);
                        }
                    }

                    @Override
                    public void onDelete() {
                        int size = inputStack.size();
                        if (size > 0 && size <= 6) {
                            inputStack.pop();
                            int viewId = ids[size - 1];
                            findViewById(viewId).setSelected(false);
                            tvOk.setEnabled(inputStack.size() == 6);
                        }
                    }

                    @Override
                    public void onComplete() {
                        keyBoardPopWindow.dismiss();
                    }
                }
        );
        keyBoardContainer.post(
                new Runnable() {
                    @Override
                    public void run() {
                        keyBoardPopWindow.show(keyBoardContainer);
                    }
                }
        );
    }

    private void clear() {
        inputStack.clear();
        for (int id : ids) {
            findViewById(id).setSelected(false);
        }
        tvOk.setEnabled(false);
    }

    private void initView() {
        ll_pwd_container = (LinearLayout) findViewById(R.id.ll_pwd_container);
        View v = findViewById(R.id.fl_container);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
        params.height = Parameters.screenHeight;
        params.width = Parameters.screenWidth;
        v.setLayoutParams(params);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvForget = (TextView) findViewById(R.id.tv_forget);
        tvOk = (TextView) findViewById(R.id.tv_ok);
        keyBoardContainer = findViewById(R.id.keyboard_container);
        findViewById(R.id.iv_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!keyBoardPopWindow.isShowing()) {
                    keyBoardPopWindow.show(keyBoardContainer);
                }
            }
        });
    }

    protected void initAction() {
        ivClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
        tvForget.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (keyBoardPopWindow.isShowing()) {
//                            keyBoardPopWindow.dismiss();
//                        }
//                        ll_pwd_container.setVisibility(View.GONE);
                        finish();
                        userQuestionQry();
                    }
                }
        );
        tvOk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String pwd = getString(inputStack);
                        if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                            LogUtil.print("密码长度不为6");
                            return;
                        }
                        if (shouldCheckPwd) {
                            checkPayPwd(pwd);
                        } else {
                            String encryptPwd = CommonEncrypt.pinKeyDesRsaEncrypt(
                                    ApplicationEx.getInstance().getUser().getTerminalId(), pwd);
                            inputEnd(encryptPwd);
                        }
                    }
                }
        );
    }

    /**
     * 查询用户密保问题
     */
    protected void userQuestionQry() {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    Question question = JSON.parseObject(responseData.toString(), Question.class);
                    VerifyQuestionActivity.open(context, question);
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().userQuestionQry(listener);
    }

    public void showProgressWithNoMsg() {
        showProgressDialog("");
    }

    protected Dialog showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = DialogCreator.createDialogWithNoMessage(context);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
        return progressDialog;
    }

    public void hideProgressDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        }, 500);
    }

    protected void toast(String msg) {
        ToastUtil.toast(context, msg);
    }

    protected void toastInternetError() {
        ToastUtil.toast(context, getString(R.string.socket_fail));
    }

    private void checkPayPwd(final String pwd) {
        if (keyBoardPopWindow != null) {
            keyBoardPopWindow.dismiss();
        }
        final String stackPwd = pwd;
        String encryptPwd = CommonEncrypt.pinKeyDesRsaEncrypt(
                ApplicationEx.getInstance().getUser().getTerminalId(), pwd);
        CheckPayPwdParams params = new CheckPayPwdParams(context);
        params.setTrsPassword(encryptPwd);
        ServiceResultCallback listener = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    inputEnd(CommonEncrypt.pinKeyDesRsaEncrypt(
                            ApplicationEx.getInstance().getUser().getTerminalId(), stackPwd));
                } else {
                    toast(resultServices.retMsg);
                    clear();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        };
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.CHECK_PAY_PWD);
        request.setResponseHandler(listener);
        showProgressWithNoMsg();
        WalletServiceManager.getInstance().start(params, request);
    }

    private void inputEnd(String encryptPwd) {
        Intent data = new Intent();
        data.putExtra(Constants.IntentKey.PASSWORD, encryptPwd);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public String getString(Stack<String> stack) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stack.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
        }
        return stringBuilder.toString();
    }

    @Override
    public void onBackPressed() {
        keyBoardPopWindow.dismiss();
        super.onBackPressed();
    }
}
