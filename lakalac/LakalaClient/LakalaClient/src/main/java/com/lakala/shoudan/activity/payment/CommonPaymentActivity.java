package com.lakala.shoudan.activity.payment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.BasePaymentActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.ProgressDialog;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by More on 15/1/22.
 */
public abstract class CommonPaymentActivity extends BasePaymentActivity implements ServiceResultCallback {

    protected TextView prompt, errorMsg, secTitle;
    protected ImageView tipsImage;
    protected LinearLayout errorView, swipeLayout;
    protected ProgressBar progressBar;
    protected VerticalListView confirmInfo;
    protected TcSyncManager tcSyncManager;

    protected BaseTransInfo baseTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_common_payment);
        initUI();
        tcSyncManager = new TcSyncManager();
    }

    protected Serializable getSerializableTransInfo() {
        return getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }


    protected boolean divideNeed() {
        return true;
    }

    protected void initUI() {

        baseTransInfo = (BaseTransInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
        confirmInfo = (VerticalListView) findViewById(R.id.confirm_info);
        confirmInfo.addList(this, baseTransInfo.getConfirmInfo(), divideNeed(), getResources().getColor(R.color.white));
        swiperManagerHandler.setAmount(baseTransInfo.getSwipeAmount());
        navigationBar.setTitle(baseTransInfo.getTransTypeName());
        prompt = (TextView) findViewById(R.id.prompt);
        prompt.setText(getString(R.string.connect_yout_card_reader));
//        bottomTips = (TextView)findViewById(R.id.bottom_tips);
        errorMsg = (TextView) findViewById(R.id.failed_reason);
        errorView = (LinearLayout) findViewById(R.id.failed_layout);
        swipeLayout = (LinearLayout) findViewById(R.id.image_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tipsImage = (ImageView) findViewById(R.id.tips_image);
        secTitle = (TextView) findViewById(R.id.sec_title);
        int[] btns = new int[]{
                R.id.goback_app, R.id.re_swip,
        };

        ((TextView) findViewById(R.id.re_swip)).setText("重新" + baseTransInfo.getRepayName());

        for (int id : btns) {
            findViewById(id).setOnClickListener(this);
        }

//        buyLakala = (TextView)findViewById(R.id.buy_lakala_swip);
//        buyLakala.setText(Html.fromHtml("<u>购买拉卡拉手机收款宝</u>"));
//        buyLakala.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                ProtocalActivity.open(CommonPaymentActivity.this, ProtocalType.BUY_LAKALA_SWIPE);
//            }
//        });
    }


    @Override
    public void onMscProcessEnd(SwiperInfo swiperInfo) {
        super.onMscProcessEnd(swiperInfo);
        baseTransInfo.setCardType(swiperInfo.getCardType());
    }

    @Override
    public void onRequestUploadIC55(SwiperInfo swiperInfo) {
        super.onRequestUploadIC55(swiperInfo);
        baseTransInfo.setCardType(swiperInfo.getCardType());
    }

    @Override
    protected void onViewClick(View view) {

        if (view.getId() == R.id.goback_app) {

            startActivity(new Intent(this, MainActivity.class));

        } else if (view.getId() == R.id.re_swip) {
            reStartSwipe();
        }

    }

    protected void showReswipeView(String error) {
        if (errorView.getVisibility() == View.GONE) {
            prompt.setText("交易失败");
            errorView.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        if (error == null) {
            error = "未知错误,请重试";
        }
        errorMsg.setText(error);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TimeCounter.getInstance().may2Gesture(this);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        TimeCounter.getInstance().save2BackgroundTime(this);
    }

    protected void reStartSwipe() {
        startSwipe();
    }

    protected abstract void startPayment(SwiperInfo swiperInfo);

    ProgressDialog progressDialog;

    protected void showProgress() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (progressDialog == null) {
                    progressDialog = DialogCreator.createDialogWithNoMessage(CommonPaymentActivity.this);
                }
                progressDialog.show();
            }
        });

    }

    protected void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                } catch (Exception e) {
                    LogUtil.print(e);
                }
            }
        });
    }

    @Override
    public void requestUploadTc(SwiperInfo swiperInfo) {

        if (baseTransInfo.getTransResult() == TransResult.SUCCESS) {
            uploadTc(swiperInfo);
        } else {
            LogUtil.print("<AS>", "A1");
            toResultPage();
        }


    }

    private void uploadTc(final SwiperInfo swiperInfo) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/trade", HttpRequest.RequestMethod.POST);
                RequestParams requestParams = businessRequest.getRequestParams();
                requestParams.put("busid", "TCCHK");
                requestParams.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
                requestParams.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
                requestParams.put("cardtype", "2");
                requestParams.put("series", Utils.createSeries());
                requestParams.put("tdtm", Utils.createTdtm());
                requestParams.put("tcicc55", swiperInfo.getTcIcc55());
                requestParams.put("scpic55", swiperInfo.getScpicc55());
                requestParams.put("tcvalue", swiperInfo.getTcValue());

                if (baseTransInfo.getTcAsyFlag() == 1)
                    requestParams.put("sid", baseTransInfo.getSid());
                else {
                    requestParams.put("srcsid", baseTransInfo.getSid());
                    requestParams.put("sytm", baseTransInfo.getSyTm());
                    requestParams.put("sysref", baseTransInfo.getSysRef());
                }

                requestParams.put("tc_asyflag", baseTransInfo.getTcAsyFlag());
                requestParams.put("posemc", "1");
                requestParams.put("hsmtrade", "02");
                requestParams.put("acinstcode", baseTransInfo.getAcinstcode());

                businessRequest.setResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {

                        if (baseTransInfo.getTcAsyFlag() == 1) {
                            //同步
                            if (resultServices.isTimeoutCode()) {
                                baseTransInfo.setTransResult(TransResult.TIMEOUT);
                            } else {
                                baseTransInfo.setTransResult(BusinessRequest.SUCCESS_CODE.equals(resultServices.retCode) ? TransResult.SUCCESS : TransResult.FAILED);
                            }

                            try {

                                JSONObject jsonObject = new JSONObject(resultServices.retData);

                                baseTransInfo.setSysRef(jsonObject.optString("sysref"));
                                baseTransInfo.setSyTm(jsonObject.optString("sytm"));
                                baseTransInfo.setSid(jsonObject.optString("sid"));

                            } catch (JSONException e) {
                            }
                            baseTransInfo.setMsg(resultServices.retMsg);
                        }


                        tcSyncManager.setTcSyncListener(new TcSyncManager.TcSyncListener() {
                            @Override
                            public void onFinish() {
                                LogUtil.print("<AS>", "A2");
                                toResultPage();
                            }
                        });

                        tcSyncManager.tcSyncService();

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {

                        if (baseTransInfo.getTcAsyFlag() == 1) {
                            baseTransInfo.setTransResult(TransResult.TIMEOUT);
                        } else {
                            tcSyncManager.saveTcInfo("TCCHK", swiperInfo.getIcc55(), swiperInfo.getScpicc55(), swiperInfo.getTCValue(), baseTransInfo.getSid(), ApplicationEx.getInstance().getSession().getCurrentKSN(), baseTransInfo.getSyTm(), baseTransInfo.getSysRef(), baseTransInfo.getAcinstcode());
                        }
                        LogUtil.print("<AS>", "A3");
                        toResultPage();
                    }
                });

                businessRequest.execute();
            }
        });
    }

    public void onPaymentSucceed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent intent = getIntent();
                intent.putExtra(ConstKey.TRANS_INFO, baseTransInfo);
                if (baseTransInfo.isSignatureNeeded()) {
                    //是否需要电子签名
                    intent.setClass(CommonPaymentActivity.this, BillSignatureActivity.class);
                } else {
                    intent.setClass(CommonPaymentActivity.this, ConfirmResultActivity.class);
                }

                startActivity(intent);
                finish();
            }
        });

    }

    public void onPaymentFailed() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                showErrorView();

            }
        });

    }


    public void onPaymentTimeout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                baseTransInfo.setTransResult(TransResult.TIMEOUT);
                Intent intent = getIntent();
                intent.putExtra(ConstKey.TRANS_INFO, baseTransInfo);
                intent.setClass(CommonPaymentActivity.this, ConfirmResultActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void startSwipe() {

        if (swiperManagerHandler == null) {
            LogUtil.print("Swiper Manger handler = Null");
            return;
        }
        final String amount = baseTransInfo.getSwipeAmount();

        final String transTypeName = baseTransInfo.getTransTypeName();

        MutexThreadManager.runThread("start swipe", new Runnable() {
            @Override
            public void run() {
                if (baseTransInfo.ifSupportIC()) {
                    swiperManagerHandler.startPboc(transTypeName, amount);
                } else {
                    swiperManagerHandler.startMsc(transTypeName, amount);
                }
            }
        });
        return;

    }


    protected void updatePrompt(String tips) {
        prompt.setText(tips);
    }


    protected void showSwipeView() {
        errorView.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
    }

    protected void setProgressVisibility(boolean b) {
        int visibility = b ? (View.VISIBLE) : View.INVISIBLE;
        progressBar.setVisibility(visibility);
    }

    protected void updateTipPic(int id) {
        tipsImage.setImageDrawable(getResources().getDrawable(id));
    }


    protected void showErrorView() {
        updatePrompt("交易失败");
        prompt.setTextColor(getResources().getColor(R.color.orange_ff9900));
        errorMsg.setText(baseTransInfo.getMsg());
        errorView.setVisibility(View.VISIBLE);
        swipeLayout.setVisibility(View.GONE);
    }


    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        switch (swiperProcessState) {
            case DEVICE_PLUGGED:
                updatePrompt(getString(R.string.detect_card_connected));
                break;
            case DEVICE_UNPLUGGED:
                updatePrompt(getString(R.string.dis_connect));
                updateTipPic((R.drawable.lakala_connect));
                break;
            case WAITING_FOR_CARD_SWIPE:
                showSwipeView();
                setProgressVisibility(false);
                prompt.setTextColor(getResources().getColor(R.color.gray_666666));
                String prompt;
                int tipPic;
                if (swiperManagerHandler.isPbocSupported()) {

                    prompt = (getString(R.string.use_card_reader_swip_or_insert));
                    tipPic = (R.drawable.lakala_swipe_both);

                } else {
                    prompt = (getString(R.string.use_card_reader_swipe));
                    tipPic = (R.drawable.lakala_swipe_only);
                }
                updatePrompt(prompt);
                updateTipPic(tipPic);

                break;
            case SWIPE_END:
                baseTransInfo.setPayCardNo(swiperInfo.getMaskedPan());
                break;
            case WAITING_FOR_PIN_INPUT:
                updateTipPic(R.drawable.lakala_input_pin);
                updatePrompt(getString(R.string.use_card_reader_input_pwd));
                break;
            case ONLINE_VALIDATING:
                updatePrompt(getString(R.string.verifacting_your_device));
                break;
            case SIGN_UP_FAILED:
                updatePrompt(getString(R.string.device_verifaction_not_pass));
                break;
            case NEW_DEVICE:
                updatePrompt(getString(R.string.new_device));
                break;
            case BINDING:
                updatePrompt(getString(R.string.binding));
                break;
            case SIGN_UP_SUCCESS:
                updatePrompt(getString(R.string.device_verifaction_passed));
                startSwipe();
                break;
            case SEARCHING:
                updatePrompt(getString(R.string.searing_bluetooth_device));
                break;
            case FINISH_SEARCHING:
//                updatePrompt("蓝牙搜索结束");
                break;
            case NORMAL:
                break;
            case PIN_INPUT_COMPLETE:
                updatePrompt(getString(R.string.now_transacting));
                break;
            case RND_ERROR:
                updatePrompt(getString(R.string.pwd_error_and_reswip));
                handleRndError();
                break;
            case PIN_INPUT_TIMEOUT:
                updatePrompt(getString(R.string.pwd_input_time_out));
                handlePinInputTimeout();
                break;
            case SWIPE_TIMEOUT:
                handleSwipeTimeout();
                updatePrompt(getString(R.string.swiper_time_out));
                break;
            case ON_FALL_BACK:
                handleFallback();
                break;
            case SWIPE_ICCARD_DENIED:
                //do nothing
                break;
            case QPBOC_DENIED:
                alertQPBOCReadErrorDialog();
                break;
        }
    }

    protected void downGradeSwipe() {
        swiperManagerHandler.downGradeSwipe();
    }

    private void handleFallback() {
        if (ApplicationEx.getInstance().getUser().getAppConfig().isIcDownEnabled()) {
            downGradeSwipe();
        } else {
            //读取IC卡信息失败，请重新插卡后点击确认
            alertIcReadErrorDialog();
        }
    }

    private void alertQPBOCReadErrorDialog() {

        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle("提示");
        alertDialog.setButtons(new String[]{(ApplicationEx.getInstance().getString(R.string.cancel)), ApplicationEx.getInstance().getString(R.string.button_ok)});
        alertDialog.setMessage("读卡失败，请重新挥卡");
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        startSwipe();
                        break;

                }
            }
        });
        alertDialog.show(getSupportFragmentManager());


    }

    private void alertIcReadErrorDialog() {

        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle("提示");
        alertDialog.setButtons(new String[]{(ApplicationEx.getInstance().getString(R.string.cancel)), ApplicationEx.getInstance().getString(R.string.button_ok)});
        alertDialog.setMessage("读取IC卡信息失败，请重新插卡后点击确认");
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        startSwipe();
                        break;

                }
            }
        });
        alertDialog.show(getSupportFragmentManager());


    }

    /**
     * pin输入和刷卡返回的随机数不一致的错误
     */
    protected void handleSwipeTimeout() {
        // 显示设备数据错误 , 重新刷卡的对话框

        AlertDialog customDialog = new AlertDialog();
        customDialog.setMessage("刷卡超时, 请重新刷卡");
        customDialog.setTitle("提示");
        customDialog.setButtons(new String[]{"取消", "确定"});
        customDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        startSwipe();
                        break;
                }
                dialog.dismiss();
            }
        });

        customDialog.show((this.getSupportFragmentManager()));
    }

    protected void handlePinInputTimeout() {
        // 显示设备数据错误 , 重新刷卡的对话框

        AlertDialog customDialog = new AlertDialog();
        customDialog.setMessage("密码输入超时, 请重新输密");
        customDialog.setTitle("提示");
        customDialog.setButtons(new String[]{"取消", "确定"});
        customDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        swiperManagerHandler.startInputPin();
                        break;
                }
                dialog.dismiss();
            }
        });

        customDialog.show((this.getSupportFragmentManager()));
    }

    protected void handleRndError() {
        // 显示设备数据错误 , 重新刷卡的对话框

        AlertDialog customDialog = new AlertDialog();
        customDialog.setMessage("数据异常, 请重新刷卡");
        customDialog.setTitle("提示");
        customDialog.setButtons(new String[]{"取消", "确定"});
        customDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        startSwipe();
                        break;
                }
                dialog.dismiss();
            }
        });

        customDialog.show((this.getSupportFragmentManager()));
    }


    protected void onExecuteSuccess(ResultServices resultServices) {

        if (resultServices.isRetCodeSuccess()) {

            baseTransInfo.setTransResult(TransResult.SUCCESS);
            baseTransInfo.optBaseData(resultServices.retData);

        } else if (resultServices.isTimeoutCode()) {
            //受理超时, 支付超时
            baseTransInfo.setTransResult(TransResult.TIMEOUT);
        } else {

            baseTransInfo.setTransResult(TransResult.FAILED);
            baseTransInfo.setMsg(resultServices.retMsg);
        }
        handleTransResult();
    }


    protected void handleTransResult() {

        if (baseTransInfo.getCardType() == SwiperInfo.CardType.ICCard) {

            showProgress();

            doSecIss(baseTransInfo.getTransResult() == TransResult.SUCCESS ? true : false, baseTransInfo.getIcc55(), baseTransInfo.getAuthcode());

        } else {
            LogUtil.print("<AS>", "A4");
            toResultPage();
        }

    }

    protected void toResultPage() {

        hideProgress();
        switch (baseTransInfo.getTransResult()) {
            case SUCCESS:

                onPaymentSucceed();
                break;
            case FAILED:
                onPaymentFailed();
                break;
            case TIMEOUT:
                onPaymentTimeout();
                break;
        }

    }


    @Override
    public void onSuccess(final ResultServices resultServices) {

        onExecuteSuccess(resultServices);

    }

    @Override
    public void onEvent(HttpConnectEvent connectEvent) {

        hideProgress();

        if (connectEvent == HttpConnectEvent.ERROR) {
            baseTransInfo.setTransResult(TransResult.FAILED);
            baseTransInfo.setMsg(getString(R.string.http_error));

        } else {
            baseTransInfo.setTransResult(TransResult.TIMEOUT);

        }
        handleTransResult();

    }


    public void toast(String msg) {
        if (msg == null) {
            return;
        }
        ToastUtil.toast(this, msg);
    }

    public void toast(int msg) {
        ToastUtil.toast(this, msg);
    }

    public void toastInternetError() {
        ToastUtil.toast(this, getString(R.string.socket_fail));
    }

}
