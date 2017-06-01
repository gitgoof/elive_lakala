package com.lakala.shoudan.activity.shoudan.records;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.signature.SignatureManager;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.bean.TransDetail;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.VerticalListView;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertInputDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 交易详情
 *
 * @author More
 */
public class RecordDetailActivity extends AppBaseActivity implements OnClickListener {

    private TextView transResult;
    private String code;

    private VerticalListView mVerticalListView;
    private View layoutTips;

    private Button btnHome;
    private Button btnAgain;

    private DealDetails recordDetail;
    private TransDetail transDetail;
    private String balance;
    boolean fromMessage;
    private String signStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_record_detail);
        initUI();
    }

    JSONObject jsonObeject;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("交易详情");

        String json = "";
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            json = bundle.getString(Constants.json_dealrecord, "");
            fromMessage = bundle.getBoolean(Constants.isfromMessage, false);
        }

        recordDetail = new DealDetails();
        // 插入需要显示的内容
        if (!fromMessage)
            getRecord();
        else {
            handleDetails(json);
        }

        transDetail = (TransDetail) getIntent().getSerializableExtra("detail");
        balance = getIntent().getStringExtra("balance");
        code = getIntent().getStringExtra("code");
        //标题
        mVerticalListView = (VerticalListView) findViewById(R.id.verticallistview_recorddetail);
        layoutTips = findViewById(R.id.layout_tips);

        btnHome = (Button) findViewById(R.id.transaction_results_button_backhome);
        btnAgain = (Button) findViewById(R.id.re_collection);
        btnHome.setOnClickListener(this);
        btnAgain.setOnClickListener(this);

        initViewData(recordDetail);
    }


    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        switch (navBarItem) {
            case back:
                super.onNavItemClick(navBarItem);
                break;
            case action:
                completeClick();
                break;
        }
    }

    /**
     * 发送凭证
     */
    private void completeClick() {
        alertMobileNoInputDialog();
    }

    private String mobileNo = "";

    private void alertMobileNoInputDialog() {
        final AlertInputDialog alertInputDialog = new AlertInputDialog();
        alertInputDialog.setButtons(new String[]{"不发送", "发送"});
        alertInputDialog.setTitle("发送凭证");
        alertInputDialog.setDividerVisibility(View.VISIBLE);
        alertInputDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        mobileNo = "";
                        dialog.dismiss();
                        break;
                    case 1:
                        EditText editInput = alertInputDialog.getEditInput();
                        if (editInput.getText() == null) {
                            mobileNo = "";
                        } else {
                            mobileNo = editInput.getText().toString();
                        }

                        if (!PhoneNumberUtil.checkPhoneNumber(mobileNo)) {
                            ToastUtil.toast(RecordDetailActivity.this, getString(R.string.toast_input_correct_phone_num));
                            return;
                        }

                        startUpload();
                        dialog.dismiss();
                        break;
                }
            }
        });
        alertInputDialog.show(getSupportFragmentManager());
    }

    private String sid;
    private String chrttCode;
    private String pan;

    protected void initSignatureInfo() {
        if (1 == recordDetail.getIsWithdrawInfo()) {
            sid = recordDetail.getWithdrawInfo().getSid();
        } else {
            sid = recordDetail.getSid();
        }
        if (recordDetail.getDealTypeName().contains("扫码收款")) {
            //系统交易特征码
            chrttCode = SignatureManager.getTransCode(recordDetail.getDealStartDateTime(), recordDetail.getSysSeq());
        } else {
            chrttCode = SignatureManager.getTransCode(recordDetail.getDealStartDateTime(), "000000000000");
        }
        pan = recordDetail.getPaymentAccount();

    }

    private void startUpload() {
        initSignatureInfo();
        // handler.sendEmptyMessage(UPLOAD_SUCCESS);
        // SignatureManager.getInstance().showPic = signaturelayout.getOriginalBitmap();
        //   LogUtil.print("Bill", SignatureManager.getInstance().showPic.getConfig() + "");
        showProgressDialog(null);
        SignatureManager.getInstance().sendSignatrue(recordDetail.getPasm(), recordDetail.getDealStartDateTime(), signStatus, sid, chrttCode, pan, mobileNo, new SignatureManager.UploadListener() {
            @Override
            public void onUploadFinish(boolean ifSuccess) {
                hideProgressDialog();
                if (ifSuccess) {
                    handler.sendEmptyMessage(UPLOAD_SUCCESS);
                } else {
                    handler.sendEmptyMessage(UPLOAD_FAILED);
                }
            }
        });
    }

    private static final int SHOW_PROGRESS = 10;
    private static final int UPLOAD_FAILED = 11;
    private static final int UPLOAD_SUCCESS = 12;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    showProgressDialog(null);
                    break;
                case UPLOAD_FAILED:
                    //   SignatureManager.getInstance().startSignatruePollingSerice(sid, chrttCode, pan, mobileNo);//补签不送手机号
                    ToastUtil.toast(context, context.getResources().getString(R.string.send_error));
                    // startActivity(new Intent(RecordDetailActivity.this, MainActivity.class));

                    break;
                case UPLOAD_SUCCESS:
                    ToastUtil.toast(context, "电子凭证已发送");
                    //startActivity(new Intent(RecordDetailActivity.this, MainActivity.class));

                    break;
            }
        }
    };

    private void completeEvent() {

        ToastUtil.toast(this, getString(R.string.send_ok));
        startActivity(new Intent(RecordDetailActivity.this, MainActivity.class));

    }


    /**
     * 处理请求的json
     *
     * @param json
     */
    private void handleDetails(String json) {
        try {
            jsonObeject = new JSONObject(json);
            recordDetail = recordDetail.parseObject(jsonObeject);
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressDialog();
        }
    }

    private void getRecord() {
        String jsonString = getIntent().getStringExtra(Constants.IntentKey.RECORD_DETAL);
        int which = getIntent().getIntExtra("which", 0);
        if (which > 0) {
            which--;
        }
        recordDetail = TradeQueryActivity.tradeInfo.getDealDetails().get(which);
    }

    private void initViewData(DealDetails record) {

        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();

        if (transDetail != null && balance != null) {

            transferInfoEntities.add(new TransferInfoEntity("交易类型", transDetail.getTransName()));
//			transferInfoEntities.add(new TransferInfoEntity("交易状态"))
        }


        if ("转账".equals(record.getDealTypeName())) {
            transferInfoEntities.add(new TransferInfoEntity("交易类型", "转账"));//
        } else {
            transferInfoEntities.add(new TransferInfoEntity("交易类型", record.getDealTypeName()));//
        }
        TipsResult tipsResult = formatState(record);

        if ("刷卡收款".equals(record.getDealTypeName()) || "大额收款".equals(record.getDealTypeName()) || record.getDealTypeName().contains("扫码收款")) {
            if (tipsResult.textColorFlag) {
                if (!"P09".equals(record.getTradeType())) {//扫码收款不发送凭证
                    navigationBar.setActionBtnText("发送凭证");
                }
                transferInfoEntities.add(new TransferInfoEntity("交易状态", formatState(record).msg, getResources().getColor(R.color.blue_button_nor)));//交易状态
            } else {
                transferInfoEntities.add(new TransferInfoEntity("交易状态", formatState(record).msg, getResources().getColor(R.color.orange)));//交易状态
            }

            if (record.getDealTypeName().contains("扫码收款")) {
                transferInfoEntities.add(new TransferInfoEntity("用户名", ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName()));
                transferInfoEntities.add(new TransferInfoEntity("用户号", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
            } else {
                transferInfoEntities.add(new TransferInfoEntity("商户名称", record.getShopName()));
                transferInfoEntities.add(new TransferInfoEntity("商户编号", record.getShopNo()));
            }
            if (!record.getDealTypeName().contains("扫码收款"))
                transferInfoEntities.add(new TransferInfoEntity("终端参数", record.getPasm()));
//			if (record.getDealTypeName().contains("扫码收款")){
//				transferInfoEntities.add(new TransferInfoEntity("收单机构号",""));
//			}else {
            transferInfoEntities.add(new TransferInfoEntity("收单机构号", record.getPosOgName()));
//			}
            transferInfoEntities.add(new TransferInfoEntity("检索参考号", record.getSysSeq()));
            transferInfoEntities.add(new TransferInfoEntity("授权号", record.getAuthCode()));
            transferInfoEntities.add(new TransferInfoEntity("批次号", record.getBatchNo()));
            transferInfoEntities.add(new TransferInfoEntity("凭证号", record.getVoucherCode(), getResources().getColor(R.color.black), true));


            if (record.getDealTypeName().contains("扫码")) {
                transferInfoEntities.add(new TransferInfoEntity("支付渠道", record.getBusName()));
            } else {
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
            }
            transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
            transferInfoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));

            String withdrawStatus = record.getWithdrawInfo().getStatus();
            String status = record.getStatus();
            if (record.getDealTypeName().contains("扫码收款")) {
                if (TextUtils.equals(status, "SUCCESS") && !TextUtils.equals(withdrawStatus, "SUCCESS")) {
                    if (TextUtils.equals(record.getSign(), "1"))
                        transferInfoEntities.add(new TransferInfoEntity("电子签购单", record.getSign().equals("1") ? "已签名" : "未签名", true));
                    if (record.getSign().equals("1"))
                        signStatus = "1";
                    else
                        signStatus = "0";
                }
            } else {
                if (record.getStatus().equals("SUCCESS") ||
                        record.getWithdrawInfo().getStatus().equals("SUCCESS")) {
                    transferInfoEntities.add(new TransferInfoEntity("电子签购单", record.getSign().equals("1") ? "已签名" : "未签名", true));
                    if (record.getSign().equals("1"))
                        signStatus = "1";
                    else
                        signStatus = "0";
                } else {
                    signStatus = "0";
                    transferInfoEntities.add(new TransferInfoEntity("电子签购单", "未签名", true));
                }
            }

            if (record.getWithdrawInfo().getStatus().equals("SUCCESS")) {
                layoutTips.setVisibility(View.VISIBLE);
            } else {
                layoutTips.setVisibility(View.INVISIBLE);
            }
        } else {
            if (tipsResult.textColorFlag) {
                navigationBar.setActionBtnText("发送凭证");
                transferInfoEntities.add(new TransferInfoEntity("交易状态", formatState(record).msg, getResources().getColor(R.color.blue_button_nor), true));//交易状态
            } else {
                transferInfoEntities.add(new TransferInfoEntity("交易状态", formatState(record).msg, getResources().getColor(R.color.orange), true));//交易状态
            }

            if ("信用卡还款".equals(record.getDealTypeName())) {
                navigationBar.setActionBtnText("");
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_CreditPaybackDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
                transferInfoEntities.add(new TransferInfoEntity("还款信用卡", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("充值金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(record.getFee()), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
            } else if ("手机充值".equals(record.getDealTypeName())) {
                navigationBar.setActionBtnText("");
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_RechargeDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
                transferInfoEntities.add(new TransferInfoEntity("充值手机号", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("充值金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
            } else if ("个人转账".equals(record.getDealTypeName())) {
                navigationBar.setActionBtnText("");
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_TransferDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
                transferInfoEntities.add(new TransferInfoEntity("转入卡号", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("转账金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(record.getFee()), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
            } else if ("商户缴费".equals(record.getDealTypeName())) {//商户缴费
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_JiaofeiDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
                transferInfoEntities.add(new TransferInfoEntity("转入卡号", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("刷卡卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(record.getFee()), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
                if (!"".equals(record.getTips()) && !"null".equals(record.getTips())) {
                    transferInfoEntities.add(new TransferInfoEntity("备注", record.getTips()));
                }
            } else if ("社区商城".equals(record.getDealTypeName())) {
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getCollectionAccount()));
//				transferInfoEntities.add(new TransferInfoEntity("转入卡号",record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("充值金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
            } else if ("特权购买".equals(record.getDealTypeName())) {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_ZhuanXiangDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("订单号", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
                transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
//				transferInfoEntities.add(new TransferInfoEntity("转入卡号",record.getCollectionAccount()));
            } else if ("一块夺宝".equals(record.getDealTypeName())) {
                navigationBar.setActionBtnText("");
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.TradeRecord_FeesOfLife_DuobaoDetail, this);
                transferInfoEntities.add(new TransferInfoEntity("订单号", record.getCollectionAccount()));
                if (recordDetail.getTreasureType() == DealDetails.TreasureType.SWIPPER
                        || recordDetail.getTreasureType() == DealDetails.TreasureType.KUAIJIE) {
                    transferInfoEntities.add(new TransferInfoEntity("付款卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                } else if (recordDetail.getTreasureType() == DealDetails.TreasureType.WALLET_AND_RED) {
                    transferInfoEntities.add(new TransferInfoEntity("支付方式", "零钱加红包"));
                } else {
                    transferInfoEntities.add(new TransferInfoEntity("支付方式", "零钱支付"));
                }
                transferInfoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));

            } else {//Default
                transferInfoEntities.add(new TransferInfoEntity("交易流水号", record.getSysSeq()));
                transferInfoEntities.add(new TransferInfoEntity("转入卡号", record.getCollectionAccount()));
                transferInfoEntities.add(new TransferInfoEntity("刷卡卡号", StringUtil.formatCardNumberN6S4N4(record.getPaymentAccount())));
                transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formatDetailDate(record.getDealStartDateTime())));
                transferInfoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(String.valueOf(record.getDealAmount())), true));
                transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(record.getFee()), true));
                transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(Util.addAmount(String.valueOf(record.getDealAmount()), record.getFee())), true));
                if (!"".equals(record.getTips()) && !"null".equals(record.getTips())) {
                    transferInfoEntities.add(new TransferInfoEntity("备注", record.getTips()));
                }
            }
        }

        mVerticalListView.addList(context, transferInfoEntities);
        mVerticalListView.setSignListener(new VerticalListView.SignListener() {
            @Override
            public void onLookSignClick() {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.SignatureBills, context);
                ProtocalActivity.open(RecordDetailActivity.this, "电子签购单", recordDetail.getSignurl());
            }
        });
    }

    ;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transaction_results_button_backhome:
//
//            Intent intent = getIntent();
//            intent.setClass(CollectionsRecordDetailActivity.this, Re.class);
//                                intent.putExtra(Constants.IntentKey.REVOCATION_SUBMIT, recordDetail);
//            CollectionsRecordDetailActivity.this.finish();
//            startActivity(intent);
                break;
            case R.id.re_collection:
                //重新收款
//
//          Intent intentAgain = new Intent(CollectionsRecordDetailActivity.this, CollectionsAmoutInputActivity.class);
//          intentAgain.putExtra(IntentKey.AMOUNT_INTENT_KEY, recordDetail.getDealAmount());
//          startActivity(intentAgain);
//          finish();
                break;

            default:
                break;
        }
    }

    /**
     * 返回日期字符串，格式为： HH:mm
     *
     * @param dateString 毫秒字符串（纯数字字符串）
     * @return 格式化后的日期字符串
     * @deprecated 使用DateUtil类中的方法
     */
    public static String date(String dateString) {
        long timeString = Long.parseLong(dateString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeString);
        String date = simpleDateFormat.format(c.getTime());
        return date;
    }


    public TipsResult formatState(DealDetails record) {
        TipsResult tipsResult = new TipsResult();
        if ("刷卡收款".equals(record.getDealTypeName()) || "扫码收款".equals(record.getDealTypeName())) {
            if (record.getStatus().equals("SUCCESS")) {
                if (record.getWithdrawInfo().getStatus().equals("SUCCESS")) {
                    if (fromMessage) {
                        if (TextUtils.equals(record.getQueryBusId(), "18X")) {
                            tipsResult.msg = "收款成功";
                        } else {
                            tipsResult.msg = "收款成功撤销成功";
                        }
                    } else {
                        tipsResult.msg = "收款成功撤销成功";
                    }
                    tipsResult.textColorFlag = true;
                } else if (record.getWithdrawInfo().getStatus().equals("FAILURE")) {
                    tipsResult.msg = "收款成功撤销失败";
                    tipsResult.textColorFlag = false;
                } else if (record.getWithdrawInfo().getStatus().equals("null")) {
                    tipsResult.msg = "收款成功";
                    tipsResult.textColorFlag = true;
                }
            } else {
                tipsResult.msg = "收款失败";
                tipsResult.textColorFlag = false;
            }

        } else {
            if ("SUCCESS".equals(record.getStatus())) {
                tipsResult.msg = record.getDealTypeName() + "成功";
                tipsResult.textColorFlag = true;
            } else {
                tipsResult.msg = record.getDealTypeName() + "失败";
                tipsResult.textColorFlag = false;
            }
        }
        return tipsResult;
    }

    /**
     * 提示状态
     *
     * @author zmy
     */
    class TipsResult {
        public String msg = "";//当前交易状态
        public boolean textColorFlag = true;//对应成功，失败，使用不一样的颜色
    }
}
