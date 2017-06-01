package com.lakala.elive.merapply.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.lakala.elive.R;
import com.lakala.elive.beans.MerOpenInfo;
import com.lakala.elive.beans.TerminalInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq3;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.utils.DictionaryUtil;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.common.widget.AmountView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 机具信息
 * Created by wenhaogu on 2017/1/9.
 */

public class MachinesToolsInfoActivity extends BaseActivity {

    private RelativeLayout rlAlPay, rlWx, rlBdWallet, rlLklWallet, rlJhWallet;
    private AmountView terminalCount, avDJRate, avJJRate, avYLRate, avALPayRate, avWXRate,
            avBDWalletRate, avLKLWalletRate, avJhWalletRate;
    private CheckBox cbDJCard, cbJJCard, cbYLCard, cbALPay, cbWX, cbBDWallet, cbLKLWallet, cbJhWallet;
    private EditText edtDJRate, edtJJRate, edtYLRate, edtALPayRate, edtWXRate, edtBDWalletRate, edtLKLWalletRate, edtJhWalletRate,edtCommFee;
    private TextView tvModelsSelector, tvSettlementCycle, tvSalesWay;
    private Button btnNext;
    private ImageView imgProgress;

    private RelativeLayout rlDeposit, rlRent, rlAmount;
    private EditText edtDeposit, edtRent, edtAmount;

    private MerApplyInfoReq3 merApplyInfoReq3;//请求bean

    private OptionsPickerView terminalModelSelector, settlePeriodPickerView, salesWayPickerView;
    private ArrayList<String> terminalList = new ArrayList<>();
    private String[] terminalKeys = {"POS", "POSPLUS", "MPOS"};

    private ArrayList<String> settlePeriodList = new ArrayList<>();//结算周期
    private MerDictionaryResp.ContentBean bmcpSelectorSettlePeriodRes;

    private ArrayList<String> salesWayList = new ArrayList<>();//终端领取方式
    private MerDictionaryResp.ContentBean bmcpSelectorSalesWayRes;

    private static final String DEBIT_CARD = "DEBIT_CARD";//借记卡
    private static final String CREDIT_CARD = "CREDIT_CARD";//贷记卡
    private static final String WECHAT_PAY_FEE = "WECHAT_PAY_FEE";//微信支付
    private static final String ALIPAY_WALLET_FEE = "ALIPAY_WALLET_FEE";//支付宝
    private static final String BAIDU_WALLET_FEE = "BAIDU_WALLET_FEE";//百度钱包
    private static final String LAKALA_WALLET_FEE = "LAKALA_WALLET_FEE";//拉卡拉钱包
    private static final String UNIONPAY_WALLET_FEE = "UNIONPAY_WALLET_FEE";//银联境外卡
    private static final String JIANHANG_WALLET_FEE = "JIANHANG_WALLET_FEE";//建行钱包


    private InputMethodManager imm;//键盘服务

    private String merchantId;
    private String applyId;//进件id
    private long id;
    private String deviceType;
    private String settlePeriod;//结算周期
    private String salesWay;//终端领用方式


    @Override

    protected void setContentViewId() {
        setContentView(R.layout.activity_machines_tools_info);
    }

    @Override
    protected void bindView() {

        //终端数
        terminalCount = findView(R.id.terminal_count);
        //机型
        tvModelsSelector = findView(R.id.tv_models_selector);
        //结算周期
        tvSettlementCycle = findView(R.id.tv_settlement_cycle);
        //终端领用方式
        tvSalesWay = findView(R.id.tv_sales_way);

        //押金
        rlDeposit = findView(R.id.rl_deposit);
        edtDeposit = findView(R.id.edt_deposit);
        //租金
        rlRent = findView(R.id.rl_rent);
        edtRent = findView(R.id.edt_rent);
        //售价
        rlAmount = findView(R.id.rl_amount);
        edtAmount = findView(R.id.edt_amount);

        //通讯费
        edtCommFee = findView(R.id.edt_comm_fee);

        //贷记卡
        cbDJCard = findView(R.id.cb_dj_card);
        avDJRate = findView(R.id.av_dj_rate);
        edtDJRate = findView(R.id.edt_dj_rate);
        //借记卡
        cbJJCard = findView(R.id.cb_jj_card);
        avJJRate = findView(R.id.av_jj_rate);
        edtJJRate = findView(R.id.edt_jj_rate);
        setPricePoint(edtJJRate);
        //银联
        cbYLCard = findView(R.id.cb_yl_card);
        avYLRate = findView(R.id.av_yl_rate);
        edtYLRate = findView(R.id.edt_yl_rate);
        //支付宝
        rlAlPay = findView(R.id.rl_al_pay);
        cbALPay = findView(R.id.cb_al_pay);
        avALPayRate = findView(R.id.av_al_pay_rate);
        edtALPayRate = findView(R.id.edt_al_pay_rate);
        //微信
        rlWx = findView(R.id.rl_wx);
        cbWX = findView(R.id.cb_wx);
        avWXRate = findView(R.id.av_wx_rate);
        edtWXRate = findView(R.id.edt_wx_rate);
        //百度钱包
        rlBdWallet = findView(R.id.rl_bd_wallet);
        cbBDWallet = findView(R.id.cb_bd_wallet);
        avBDWalletRate = findView(R.id.av_bd_wallet_rate);
        edtBDWalletRate = findView(R.id.edt_bd_wallet_rate);
        //拉卡拉钱包
        rlLklWallet = findView(R.id.rl_lkl_wallet);
        cbLKLWallet = findView(R.id.cb_lkl_wallet);
        avLKLWalletRate = findView(R.id.av_lkl_wallet_rate);
        edtLKLWalletRate = findView(R.id.edt_lkl_wallet_rate);
        //建行钱包
        rlJhWallet = findView(R.id.rl_jh_wallet);
        cbJhWallet = findView(R.id.cb_jh_wallet);
        avJhWalletRate = findView(R.id.av_jh_wallet_rate);
        edtJhWalletRate = findView(R.id.edt_jh_wallet_rate);


        //其它
//        cbElseWay = findView(R.id.cb_else_way);
//        avElseWayRate = findView(R.id.av_else_way_rate);
//        edtElseWayRate = findView(R.id.edt_else_way_rate);
        //下一步
        btnNext = findView(R.id.btn_next);

        iBtnBack.setVisibility(View.VISIBLE);

        terminalModelSelector = new OptionsPickerView(this);

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        tvTitleName.setText("终端信息");

        imgProgress = findView(R.id.img_progress);
        EnterPieceActivity.setImageProgress(imgProgress, 7);
    }

    @Override
    protected void bindEvent() {
        iBtnBack.setOnClickListener(this);
        tvModelsSelector.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvSettlementCycle.setOnClickListener(this);
        tvSalesWay.setOnClickListener(this);
        terminalModelSelector.setOnoptionsSelectListener(terminalModelSelectListener);
    }
    private String mAccountKind;
    @Override
    protected void bindData() {
//        showProgressDialog("加载中....");
        Intent intent = getIntent();
        applyId = intent.getStringExtra(InformationInputActivity.APPLY_ID);
        id = intent.getLongExtra(InformationInputActivity.ID, 0);
        merchantId = intent.getStringExtra(EnterPieceActivity.MERCHANT_ID);
        mAccountKind = intent.getStringExtra("AccountKind");

        //初始化请求数据
        merApplyInfoReq3 = new MerApplyInfoReq3();
        merApplyInfoReq3.setMerOpenInfo(new MerOpenInfo());
        merApplyInfoReq3.getMerOpenInfo().setApplyId(applyId);
        merApplyInfoReq3.setTerminalInfo(new TerminalInfo());
        merApplyInfoReq3.setSubmitInfoType("POS_INFO_SAVE");//BASE_INFO_SAVE.商户基本信息 POS_INFO_SAVE.机具信息
        merApplyInfoReq3.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        merApplyInfoReq3.setCardAppRateInfoList(new ArrayList<MerApplyInfoReq3.CardAppRateInfo>());

        List<String> list = DictionaryUtil.getInstance().getOtherDictList("TERMINAL_TYPE");
        if(list != null && list.size() != 0){
            terminalList.addAll(list);
        } else {
            terminalList.add("传统POS");
            terminalList.add("智能POS");
            terminalList.add("手收POS");
        }

        //结算周期
//        NetAPI.merDictionaryReq(this, this,
//                new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SETTLE_PERIOD"), NetAPI.SETTLE_PERIOD);

        //终端领取方式
//        NetAPI.merDictionaryReq(this, this,
//                new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "DEVICE_DRAW_METHOD"), NetAPI.DEVICE_DRAW_METHOD);
        if(!TextUtils.isEmpty(mAccountKind)){
            mJszq = mAccountKind.equals("58")?"SETTLE_PERIOD_PRIVATE":"SETTLE_PERIOD_PUBLIC";
        }
        if (!TextUtils.isEmpty(applyId)) {
            showProgressDialog("加载中...");//获取进件回显数据
            NetAPI.merApplyDetailsReq(this, this, new MerApplyDetailsReq(mSession.getUserLoginInfo().getAuthToken(), applyId));
        }

    }
    private String mJszq = "SETTLE_PERIOD_PUBLIC";

    @Override
    public void onClick(View v) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//强制隐藏键盘
        }
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_models_selector:
                selectModel();
                break;
            case R.id.tv_settlement_cycle://结算周期
                settlePeriodList = DictionaryUtil.getInstance().getOtherDictList(mJszq);
                if(settlePeriodList == null || settlePeriodList.size() == 0){
                    settlePeriodList = new ArrayList<String>();
                    NetAPI.merDictionaryReq(this, this,
                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "SETTLE_PERIOD"), NetAPI.SETTLE_PERIOD);
                    break;
                }
                selectAddress(settlePeriodPickerView, settlePeriodList, settlePeriodSelectListener);
                break;
            case R.id.tv_sales_way://终端领取方式
                salesWayList = DictionaryUtil.getInstance().getOtherDictList("DEVICE_DRAW_METHOD");
                if(salesWayList == null || salesWayList.size() == 0){
                    salesWayList = new ArrayList<String>();
                    NetAPI.merDictionaryReq(this, this,
                            new MerDictionaryReq(mSession.getUserLoginInfo().getAuthToken(), "dic", "PARAM", "DEVICE_DRAW_METHOD"), NetAPI.DEVICE_DRAW_METHOD);
                    break;
                }
                selectAddress(salesWayPickerView, salesWayList, salesWayListener);
                break;
            case R.id.btn_next:
                if (getTextContext()) {
                    next();
                }
                break;

        }
    }

    private boolean getTextContext() {
        merApplyInfoReq3.getTerminalInfo().setDeviceCnt(terminalCount.getText());
        int selectorCount = 0;//选择支付种类的数量   如果如果为零,提示只是选择一种支付类型
        if (TextUtils.isEmpty(tvModelsSelector.getText().toString().trim())) {
            Utils.showToast(this, "请选择机型");
        } else if (TextUtils.isEmpty(tvSettlementCycle.getText().toString().trim())) {
            Utils.showToast(this, "请选择结算周期");
        } else if (TextUtils.isEmpty(tvSalesWay.getText().toString().trim())) {
            Utils.showToast(this, "请选择终端领用方式");
        } else if (rlDeposit.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edtDeposit.getText().toString().trim())
                || rlDeposit.getVisibility() == View.VISIBLE && Integer.parseInt(edtDeposit.getText().toString().trim()) <= 0) {
            Utils.showToast(this, "请输入押金");
        } else if (rlRent.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edtRent.getText().toString().trim())
                || rlRent.getVisibility() == View.VISIBLE && Integer.parseInt(edtRent.getText().toString().trim()) <= 0) {
            Utils.showToast(this, "请输入租金");
        } else if (rlAmount.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edtAmount.getText().toString().trim())
                || rlAmount.getVisibility() == View.VISIBLE && Integer.parseInt(edtAmount.getText().toString().trim()) <= 0) {
            Utils.showToast(this, "请输入售价");
        } else if(!cbDJCard.isChecked() && !cbJJCard.isChecked()){
            Utils.showToast(this, "借记卡和贷记卡必须选择一种");
        } else if(TextUtils.isEmpty(edtCommFee.getText().toString().trim())){
            Utils.showToast(this, "请输入通讯费");
        } else {

            merApplyInfoReq3.getTerminalInfo().setApplyId(applyId);
            merApplyInfoReq3.getTerminalInfo().setDeviceDrawMethod(salesWay);
            merApplyInfoReq3.getTerminalInfo().setDeviceDeposit(edtDeposit.getText().toString().trim());
            merApplyInfoReq3.getTerminalInfo().setDeviceRent(edtRent.getText().toString().trim());
            merApplyInfoReq3.getTerminalInfo().setDeviceSaleAmount(edtAmount.getText().toString().trim());
            merApplyInfoReq3.getTerminalInfo().setCommFee(edtCommFee.getText().toString().trim());//commFee
            merApplyInfoReq3.getTerminalInfo().setSettlePeriod(merApplyInfoReq3.getMerOpenInfo().getSettlePeriod());
            List<MerApplyInfoReq3.CardAppRateInfo> cardAppRateInfoList = merApplyInfoReq3.getCardAppRateInfoList();
            cardAppRateInfoList.clear();

            if (cbDJCard.isChecked()) {//贷记卡
                selectorCount++;
                String dJRate = (new BigDecimal(avDJRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(CREDIT_CARD, dJRate, edtDJRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                // cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbJJCard.isChecked()) {//借记卡
                selectorCount++;
                String jJRate = (new BigDecimal(avJJRate.getText()).divide(new BigDecimal(100))).toString();
                if (!TextUtils.isEmpty(edtJJRate.getText().toString().trim())) {
                    double maxRate = Double.parseDouble(edtJJRate.getText().toString().trim());
                    if (maxRate < 20) {
                        Utils.showToast(this, "借记卡封顶不能小于20");
                        return false;
                    } else {
                        MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(DEBIT_CARD, jJRate, edtJJRate.getText().toString().trim());
                        cardAppRateInfo.setApplyId(applyId);
                        // cardAppRateInfo.setTerminalId("");
                        cardAppRateInfoList.add(cardAppRateInfo);
                    }
                }

            }
            if (cbYLCard.isChecked()) {//银联
                selectorCount++;
                String yLRate = (new BigDecimal(avYLRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(UNIONPAY_WALLET_FEE, yLRate, edtYLRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                //cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbALPay.isChecked()) {//支付宝
                selectorCount++;
                String aLPayRate = (new BigDecimal(avALPayRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(ALIPAY_WALLET_FEE, aLPayRate, edtALPayRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                //cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbWX.isChecked()) {//微信
                selectorCount++;
                String wXRate = (new BigDecimal(avWXRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(WECHAT_PAY_FEE, wXRate, edtWXRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbBDWallet.isChecked()) {//百度钱包
                selectorCount++;
                String bDWalletRate = (new BigDecimal(avBDWalletRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(BAIDU_WALLET_FEE, bDWalletRate, edtBDWalletRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                //cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbLKLWallet.isChecked()) {//拉卡拉
                selectorCount++;
                String lKLWalletRate = (new BigDecimal(avLKLWalletRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(LAKALA_WALLET_FEE, lKLWalletRate, edtLKLWalletRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                // cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (cbJhWallet.isChecked()) {//建行钱包
                selectorCount++;
                String jhWalletRate = (new BigDecimal(avJhWalletRate.getText()).divide(new BigDecimal(100))).toString();
                MerApplyInfoReq3.CardAppRateInfo cardAppRateInfo = new MerApplyInfoReq3.CardAppRateInfo(JIANHANG_WALLET_FEE, jhWalletRate, edtJhWalletRate.getText().toString().trim());
                cardAppRateInfo.setApplyId(applyId);
                // cardAppRateInfo.setTerminalId("");
                cardAppRateInfoList.add(cardAppRateInfo);
            }
            if (selectorCount <= 0) {
                Utils.showToast(this, "至少选择一种支付方式");
                return false;
            }

            return true;
        }
        return false;

    }

    private void next() {
        showProgressDialog("提交中...");
        NetAPI.merApply3(this, this, merApplyInfoReq3);
    }
    private MerApplyDetailsResp.ContentBean contentBean;
    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        if (method == NetAPI.SETTLE_PERIOD) {//结算周期
            bmcpSelectorSettlePeriodRes = ((MerDictionaryResp) obj).getContent();
            settlePeriodList.clear();
            for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorSettlePeriodRes.getItems()) {
                settlePeriodList.add(itemsBean.getValue());
            }
            selectAddress(settlePeriodPickerView, settlePeriodList, settlePeriodSelectListener);
        } else if (method == NetAPI.DEVICE_DRAW_METHOD) {//终端领用方式
            bmcpSelectorSalesWayRes = ((MerDictionaryResp) obj).getContent();
            salesWayList.clear();
            for (MerDictionaryResp.ContentBean.ItemsBean itemsBean : bmcpSelectorSalesWayRes.getItems()) {
                salesWayList.add(itemsBean.getValue());
            }
            selectAddress(salesWayPickerView, salesWayList, salesWayListener);
        } else if(method == NetAPI.ACTION_MER_APPLY_DETAILS){
            contentBean = ((MerApplyDetailsResp) obj).getContent();
            //如果有数据,就回显到控件上
            //编辑回显
            if (null != contentBean && null != contentBean.getMerOpenInfo() && !TextUtils.isEmpty(contentBean.getMerOpenInfo().getMerchantName())) {
                MerApplyDetailsResp.ContentBean.MerOpenInfoBean merOpenInfo = contentBean.getMerOpenInfo();
                mAccountKind = merOpenInfo.getAccountKind();
                if(!TextUtils.isEmpty(mAccountKind)){
                    mJszq = mAccountKind.equals("58")?"SETTLE_PERIOD_PRIVATE":"SETTLE_PERIOD_PUBLIC";
                }
                merApplyInfoReq3.getMerOpenInfo().setAccountKind(merOpenInfo.getAccountKind());
                merApplyInfoReq3.getMerOpenInfo().setMerchantName(merOpenInfo.getMerchantName());
                merApplyInfoReq3.getMerOpenInfo().setContact(merOpenInfo.getContact());
                merApplyInfoReq3.getMerOpenInfo().setMobileNo(merOpenInfo.getMobileNo());
                merApplyInfoReq3.getMerOpenInfo().setBusinessContent(merOpenInfo.getBusinessContent());
                merApplyInfoReq3.getMerOpenInfo().setMccCode(merOpenInfo.getMccCode());
                merApplyInfoReq3.getMerOpenInfo().setProvinceCode(merOpenInfo.getProvinceCode());
                merApplyInfoReq3.getMerOpenInfo().setCityCode(merOpenInfo.getCityCode());
                merApplyInfoReq3.getMerOpenInfo().setDistrictCode(merOpenInfo.getDistrictCode());
                merApplyInfoReq3.getMerOpenInfo().setMerAddr(merOpenInfo.getMerAddr());
                merApplyInfoReq3.getMerOpenInfo().setEmailAddr(merOpenInfo.getEmailAddr());
                merApplyInfoReq3.getMerOpenInfo().setBusinessArea(merOpenInfo.getBusinessArea());
                merApplyInfoReq3.getMerOpenInfo().setBusinessTime(merOpenInfo.getBusinessTime());
            }
        } else {
            setResult(Activity.RESULT_OK);//让前面一个页面finish掉
            startActivity(new Intent(this, MerApplyCompleteActivity.class));
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        if (method == NetAPI.DEVICE_DRAW_METHOD
                || method == NetAPI.SETTLE_PERIOD
                || method == NetAPI.ACTION_MER_APPLY_DETAILS) {
            Utils.showToast(this, "获取数据失败");
        } else {
            Utils.showToast(this, "提交失败");
        }
    }

    /**
     * 选择终端型号
     */
    private void selectModel() {
        terminalModelSelector.setPicker(terminalList);
        terminalModelSelector.setCyclic(false);
        terminalModelSelector.setCancelable(true);
        terminalModelSelector.show();
    }

    /**
     * 选择
     */

    private void selectAddress(OptionsPickerView pickerView, ArrayList<String> list, OptionsPickerView.OnOptionsSelectListener listener) {
        pickerView = new OptionsPickerView(this);
        pickerView.setPicker(list);
        pickerView.setCyclic(false);
        pickerView.setCancelable(true);
        pickerView.setOnoptionsSelectListener(listener);
        pickerView.show();
    }

    OptionsPickerView.OnOptionsSelectListener terminalModelSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            String string = terminalList.get(options1);
            tvModelsSelector.setText(string);
            List<String> list = DictionaryUtil.getInstance().getOtherDictList("TERMINAL_TYPE");
            if(list != null && list.size() != 0){
                deviceType = DictionaryUtil.getInstance().getMapValueByType("TERMINAL_TYPE").get(string);;
            } else {
                deviceType = terminalKeys[options1];
            }

            merApplyInfoReq3.getTerminalInfo().setDeviceType(deviceType);
            if(!TextUtils.isEmpty(string) && string.equalsIgnoreCase("智能POS")){//智能pos机才有  支付宝微信等扫码功能
                rlAlPay.setVisibility(View.VISIBLE);
                rlWx.setVisibility(View.VISIBLE);
                rlBdWallet.setVisibility(View.VISIBLE);
                rlLklWallet.setVisibility(View.VISIBLE);
                rlJhWallet.setVisibility(View.VISIBLE);
                //初始化选择
                cbDJCard.setChecked(true);
                cbJJCard.setChecked(true);
                cbYLCard.setChecked(false);
                cbALPay.setChecked(true);
                cbWX.setChecked(true);
                cbBDWallet.setChecked(true);
                cbLKLWallet.setChecked(true);
                cbJhWallet.setChecked(true);
            } else {//其他两种pos机
                rlAlPay.setVisibility(View.GONE);
                rlWx.setVisibility(View.GONE);
                rlBdWallet.setVisibility(View.GONE);
                rlLklWallet.setVisibility(View.GONE);
                rlJhWallet.setVisibility(View.GONE);
                //初始化选择
                cbDJCard.setChecked(true);
                cbJJCard.setChecked(true);
                cbYLCard.setChecked(false);
                cbALPay.setChecked(false);
                cbWX.setChecked(false);
                cbBDWallet.setChecked(false);
                cbLKLWallet.setChecked(false);
                cbJhWallet.setChecked(false);
            }
        }
    };


    //结算周期
    OptionsPickerView.OnOptionsSelectListener settlePeriodSelectListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            if (settlePeriodList.size() < 1) {
                return;
            }
            String string = settlePeriodList.get(options1);
            tvSettlementCycle.setText(string);
            List<String> list = DictionaryUtil.getInstance().getOtherDictList(mJszq);
            if(list != null && list.size() != 0){
                String id = DictionaryUtil.getInstance().getMapValueByType(mJszq).get(string);
                merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(id);
                return;
            }
            merApplyInfoReq3.getMerOpenInfo().setSettlePeriod(bmcpSelectorSettlePeriodRes.getItems().get(options1).getId());
           // settlePeriod = bmcpSelectorSettlePeriodRes.getItems().get(options1).getId();
        }
    };


    //终端领取方式
    OptionsPickerView.OnOptionsSelectListener salesWayListener = new OptionsPickerView.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            String string = salesWayList.get(options1);
            tvSalesWay.setText(string);
            List<String> list = DictionaryUtil.getInstance().getOtherDictList("DEVICE_DRAW_METHOD");
            if(list != null && list.size() != 0){
                salesWay = DictionaryUtil.getInstance().getMapValueByType("DEVICE_DRAW_METHOD").get(string);
            } else {
                salesWay = bmcpSelectorSalesWayRes.getItems().get(options1).getId();
            }
            if (salesWayList.get(options1).equals("租赁")) {
                rlDeposit.setVisibility(View.VISIBLE);
                rlRent.setVisibility(View.VISIBLE);
                rlAmount.setVisibility(View.GONE);
            } else if (salesWayList.get(options1).equals("出售")) {
                rlDeposit.setVisibility(View.GONE);
                rlRent.setVisibility(View.GONE);
                rlAmount.setVisibility(View.VISIBLE);
            } else {
                rlDeposit.setVisibility(View.GONE);
                rlRent.setVisibility(View.GONE);
                rlAmount.setVisibility(View.GONE);
            }
            edtDeposit.setText("");
            edtRent.setText("");
            edtAmount.setText("");
        }
    };


    /**
     * 现在只能输入两位小数
     *
     * @param editText
     */
    public void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 1) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 2);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }

        });

    }


    /**
     * 乘,BigDecimal用
     *
     * @param v1
     * @param v2
     * @return result
     */
    public static BigDecimal mul(Object v1, Object v2) {
        BigDecimal result = null;
        // if(v1!=null && v2!=null){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        result = b1.multiply(b2);
        // }
        return result;
    }


}
