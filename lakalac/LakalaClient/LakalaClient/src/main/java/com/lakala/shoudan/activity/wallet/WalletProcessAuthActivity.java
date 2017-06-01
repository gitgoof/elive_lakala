package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.encryption.Digest;
import com.lakala.library.encryption.Mac;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.activity.wallet.datapick.OnWheelScrollListener;
import com.lakala.shoudan.activity.wallet.datapick.WheelView;
import com.lakala.shoudan.activity.wallet.datapick.adapter.NumericWheelAdapter;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.SpaceTextWatcher;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.shoudan.util.XAtyTask;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertMyDialog;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 实名认证
 * Created by fengx on 2015/11/30.
 */
public class WalletProcessAuthActivity extends BasePwdAndNumberKeyboardActivity {

    @Bind(R.id.card_type)
    TextView tvCardType;
    @Bind(R.id.validity_period)
    EditText validityPeriod;
    @Bind(R.id.validity_card)
    LinearLayout validityCard;
    @Bind(R.id.safety_code)
    EditText safetyCode;
    @Bind(R.id.bank_mobile_phone)
    EditText bankMobilePhone;
    @Bind(R.id.btn_add)
    TextView btnAdd;
    @Bind(R.id.id_combinatiion_text_edit_text)
    LinearLayout llSafetyCode;
    @Bind(R.id.cbox_agreement)
    CheckBox cboxAgreement;
    @Bind(R.id.tv_agreement)
    TextView tvAgreement;
    private boolean isCreditCard;//储蓄卡、信用卡判别
    private QuickCardBinBean quickCardBinBean;
    private String cardType;
    private String cardTypeName;
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();
    private BaseDialog dialog;
    private EditText walletTransferName;
    private EditText idCardNo;
    private LabelEditText letBankCardNum;
    private LinearLayout ll_card;
    private LinearLayout ll_phone;
    private View viewGroup;
    private String accountNum;
    private String bankName;
    private String customType;//新老用户用户、已未开通成功标志
    private String bankListbankName;
    private String banListAccountType;//输入卡号的银行支持的类型
    private String accountType;//输入卡的类型
    private String tvSupportBank;//输入卡号类型
    private String tvBankname;//输入卡号
    private static final int REQUEST_BANKS = 0x2360;

    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView timeStart;
    private WheelView timeEnd;
    private WheelView time_line;
    private int mYear = 1996;
    private int mMonth = 0;
    private int mDay = 1;
    private PopupWindow pophead;
    private PopupWindow popheadTime;
    private LinearLayout ll_data;
    private LinearLayout ll_dataTime;
    private View viewPop = null;
    private View viewPopTime = null;
    private String birthday;
    private boolean isAccout=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_auth);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        XAtyTask.getInstance().addAty(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_process_auth, null);
        initDatePop();
        quickCardBinBean = new QuickCardBinBean();
        navigationBar.setTitle("实名认证");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                }
            }
        });
        walletTransferName = (EditText) findViewById(R.id.wallet_transfer_name);
        ll_phone = (LinearLayout) findViewById(R.id.ll_phone);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        idCardNo = (EditText) findViewById(R.id.idCardNo);
        letBankCardNum = (LabelEditText) findViewById(R.id.let_bank_card_num);
        letBankCardNum.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        letBankCardNum.getEditText().setLongClickable(false);
        letBankCardNum.setOnFocusChangeListener(null);
        viewGroup = (View) findViewById(R.id.view_group);
        //键盘
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        initNumberEdit(letBankCardNum.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        idCardNo.addTextChangedListener(new SpaceTextWatcher());
        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (info.getMerchantStatus() == MerchantStatus.COMPLETED) {
            walletTransferName.setText(info.getAccountName());
            idCardNo.setText(info.getUser().getIdCardInfo().getIdCardId());
            setEditFocus(walletTransferName);
            setEditFocus(idCardNo);
        }


        CompoundButton.OnCheckedChangeListener CheckListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (letBankCardNum.getEditText().getText().toString().trim().length()>0)
                    btnAdd.setEnabled(true);
//                btnNext.setBtnBackgroundDrawable(getResources().getDrawable(R.drawable.btn_topline_selector));
                } else {
                    btnAdd.setEnabled(false);
//                btnNext.setBtnBackgroundDrawable(getResources().getDrawable(R.drawable.gray_bg));
                }
            }
        };
        cboxAgreement.setOnCheckedChangeListener(CheckListener);
        tvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocalActivity.open(context, ProtocalType.REAL_NAME_AUTH_SERVICE);
            }
        });
        btnAdd.setEnabled(false);
    }

    private void setEditFocus(EditText et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
    }

    private void setBankS(boolean b, String s) {
        if (b) {
            tvCardType.setTextColor(getResources().getColor(R.color.font_gray_three2));
            ll_phone.setVisibility(View.VISIBLE);
            setCard();
        } else {
            /*if (!isAccout){
                tvCardType.setText("不支持该银行，请您更换银行卡");
            }else {
                tvCardType.setText("请输入正确的银行卡卡号");
            }*/
            if (s!=null)
            tvCardType.setText(s);
            tvCardType.setTextColor(getResources().getColor(R.color.orange_FF8308));
        }
    }

    private void alertMobileNoInputDialog() {
        final AlertMyDialog alertInputDialog = new AlertMyDialog();
        alertInputDialog.setButtons(new String[]{"我知道了"});
        alertInputDialog.setDividerVisibility(View.VISIBLE);
        alertInputDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0:
                        dialog.dismiss();
                        break;
                }
            }
        });

        alertInputDialog.show(getSupportFragmentManager());


    }
    private void setCard() {
        cardType = quickCardBinBean.getAccountType();
        cardTypeName = quickCardBinBean.getBankName();
        //判断银行卡类型 1：借记卡；2：信用卡
        if (cardType.equals("2")) {
            //信用卡.
            ll_card.setVisibility(View.VISIBLE);
            isCreditCard = true;
            tvCardType.setText(quickCardBinBean.getBankName() + "  " + "信用卡");
        } else {
            validityPeriod.setText("");
            safetyCode.setText("");
            ll_card.setVisibility(View.GONE);
            //储蓄卡
            isCreditCard = false;
            tvCardType.setText(quickCardBinBean.getBankName() + "  " + "储蓄卡");
        }
        if (cboxAgreement.isChecked())
        btnAdd.setEnabled(true);
    }

    public void onMyClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                    if (check()) {
                        getQYTZ();
                    }
//                else {
//                    ToastUtil.toast(context,"卡号输入有误，请重新输入");
//                }

                break;

            case R.id.support_bank_list:
                qryBusinessBankList();
                break;
            case R.id.img_details_nol:
//                showDetail();
                alertMobileNoInputDialog();
                break;
           case R.id.validity_card:
                getDataPick();
                ll_data.startAnimation(AnimationUtils.loadAnimation(this, R.anim.activity_translate_in));
                pophead.showAtLocation(parentView, Gravity.CENTER, 0, 0);
                break;
            case R.id.validity_period:
                getDataPick();
                ll_data.startAnimation(AnimationUtils.loadAnimation(this, R.anim.activity_translate_in));
                pophead.showAtLocation(parentView, Gravity.CENTER, 0, 0);
                break;
        }
    }
    /**
     *查询支持业务的银行列表
     */
    public void qryBusinessBankList(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.SUPPORTED_BUSINESS_BANK_LIST);
        SupportedBankListRequest params=new SupportedBankListRequest(context);
        params.setBusId("1G2");
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                   String busiBanksString = resultServices.retData.toString();
                    WalletSupportedBankListActivity.open(context, busiBanksString, REQUEST_BANKS);
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }
    private PopupWindow popupWindow;
    private View popupWindow_view;
    private View parentView;


    private void showDetail() {
        if (popupWindow == null) {
            popupWindow_view = getLayoutInflater().inflate(R.layout.pop_name_auth, null,
                    false);
            TextView button = (TextView) popupWindow_view.findViewById(R.id.btn_add);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBackgroundAlpha(1.0f);
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(popupWindow_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.showAtLocation(viewGroup, Gravity.CENTER, 0, 0);
            setBackgroundAlpha(0.5f);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                    popupWindow.dismiss();
                }
            });
        } else {
            setBackgroundAlpha(0.5f);
            popupWindow.showAtLocation(viewGroup, Gravity.CENTER, 0, 0);
        }

    }
    private void nameAuth() {
        context.showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.REAL_NAME_AHTU);
        HttpRequestParams params = request.getRequestParams();
        User user= ApplicationEx.getInstance().getSession().getUser();
        params.put("termid",ApplicationEx.getInstance().getSession().getUser().getTerminalId());
       // params.put("chntype",BusinessRequest.CHN_TYPE);
        params.put("chntype","99999");
        params.put("series", Utils.createSeries());
        params.put("rnd", Mac.getRnd() );
       // params.put("mac", DeviceUtil.getMac(context));
        params.put("tdtm", Utils.createTdtm());
        params.put("gesturePwd","0");
        params.put("platform","android" );
        params.put("refreshToken", user.getRefreshToken());
        params.put("subChannelId","10000027" );
        params.put("telecode", TerminalKey.getTelecode());
        params.put("timeStamp",ApplicationEx.getInstance().getSession().getCurrentKSN() );
        params.put("mac", BusinessRequest.getMac(params));
        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();
        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        params.put("guid",md5Value );

        params.put("realName", walletTransferName.getText().toString().trim());
        params.put("accountNo", letBankCardNum.getEditText().getText().toString().trim());
        params.put("idCardNo",idCardNo.getText().toString().trim() );

        quickCardBinBean.setAccountNo(letBankCardNum.getEditText().getText().toString().trim());
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String isAlreadyAdd= jsonObject.optString("isAlreadyAdd");
                        if (isAlreadyAdd.equals("0")){
                            isAccout = true;
                                quickCardBinBean.setAccountType(jsonObject.optString("supportCardType"));
                                quickCardBinBean.setBankCode(jsonObject.optString("bankCode"));
                            quickCardBinBean.setBankName(jsonObject.optString("bankName"));
                            quickCardBinBean.setCustomerName(walletTransferName.getText().toString().trim());
                            quickCardBinBean.setIdentifier(idCardNo.getText().toString().trim());
                            setBankS(true, "");
                           /* if (!quickCardBinBean.getBankName().equals("招商银行"))
                                setBankS(true, "");
                            else{
                                isAccout = false;
                                setBankS(false,"");
                            }*/
                        }else {
                            ToastUtil.toast(context, "您已添加此银行卡");
                        }
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    isAccout = false;
                    String code=resultServices.retCode;
                  /*  if (code.equals("005084")){
                        isAccout = false;
                    }*/
                    setBankS(false, resultServices.retMsg);
                    // ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
            }
        });
        request.execute();
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (v.getId()==letBankCardNum.getEditText().getId()){


        if (!hasFocus) {
            if (toCheckCardBin(letBankCardNum.getEditText().getText().toString().toString())) {
              //  qryCarBin();
                nameAuth();
            } else {
                ToastUtil.toast(context, "银行卡号输入不正确");
            }

        }
        if (hasFocus) {
            String newText = letBankCardNum.getEditText().getText().toString().replace(" ", "");
            letBankCardNum.getEditText().setText(newText);
            letBankCardNum.getEditText().setSelection(newText.length());
        } else {
            String text = CardUtil.formatCardNumberWithSpace(letBankCardNum.getEditText().getText().toString());
            letBankCardNum.getEditText().setText(text);
        } }
    }

    //输入卡号是否规范
    private boolean toCheckCardBin(String text) {
        if (text.length() >= 14 && text.length() <= 19) {
            return true;
        }
        return false;
    }


    /**
     * 1.1.4.实名认证快捷跳转签约
     */
    private void getQYTZ() {
        quickCardBinBean.setCustomerName(walletTransferName.getText().toString().trim());
        quickCardBinBean.setIdentifier(idCardNo.getText().toString().trim());
        context.showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.GET_QYTZ);
        quickCardBinBean.setMobileInBank(bankMobilePhone.getText().toString().trim());
       if (quickCardBinBean.getCustomerName()==null||quickCardBinBean.getCustomerName().equals("")){
           quickCardBinBean.setCustomerName(walletTransferName.getText().toString().trim());
       }
        HttpRequestParams params = request.getRequestParams();
        params.put("realName", quickCardBinBean.getCustomerName());
        params.put("accountNo", quickCardBinBean.getAccountNo());
        params.put("mobileNum", quickCardBinBean.getMobileInBank());
        params.put("idCardNo", quickCardBinBean.getIdentifier());
        if (cardType.equals("2")) {
            quickCardBinBean.setCVN2(safetyCode.getText().toString());
            // quickCardBinBean.setCardExp( validityPeriod.getText().toString());
            params.put("cVN2", quickCardBinBean.getCVN2());
            params.put("cardExp", quickCardBinBean.getCardExp());
        }

        User user= ApplicationEx.getInstance().getSession().getUser();
        params.put("termid",ApplicationEx.getInstance().getSession().getUser().getTerminalId());
        ///params.put("chntype",BusinessRequest.CHN_TYPE);
        params.put("chntype","99999");
        params.put("series", Utils.createSeries());
        params.put("rnd", Mac.getRnd() );
        params.put("tdtm", Utils.createTdtm());
        params.put("gesturePwd","0");
        params.put("platform","android" );
        params.put("refreshToken", user.getRefreshToken());
        params.put("subChannelId","10000027" );
        params.put("telecode", TerminalKey.getTelecode());
        params.put("timeStamp",ApplicationEx.getInstance().getSession().getCurrentKSN() );

        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();
        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        params.put("guid",md5Value );
        params.put("mac", BusinessRequest.getMac(params));

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        Intent intent = null;

                        jsonObject = new JSONObject(resultServices.retData);
                        String signMode = jsonObject.optString("signMode");

                        switch (signMode) {
                            case "1":
                                getUnsignSMSCode();
                                break;
                            case "3":
                                String yhURL = jsonObject.optString("yhURL");
                                intent = new Intent(context, WalletMBConnectActivity.class);
                                intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN, quickCardBinBean);
                                intent.putExtra("yhURL", yhURL);
                                startActivity(intent);
                                break;
                        }

                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
            }
        });
        request.execute();
    }

    /**
     * 转出先查询收款人信息判断有没绑卡及到账时间
     * 1、有绑卡，则默
     */
    private void startACS(String sid) {
        Intent intent = new Intent(WalletProcessAuthActivity.this, SMSVerifiActivity.class);
        if (isCreditCard) {
            quickCardBinBean.setCVN2(safetyCode.getText().toString());
            //quickCardBinBean.setCardExp(validityPeriod.getText().toString());
        }

        quickCardBinBean.setMobileInBank(bankMobilePhone.getText().toString());
        intent.putExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN, quickCardBinBean);
        intent.putExtra("sid", sid);
        startActivity(intent);


    }

    private boolean check() {
        String idNo = idCardNo.getText().toString();
        if (TextUtils.isEmpty(walletTransferName.getText().toString())){
            toast("姓名不能为空");
            return false;
        } else if (TextUtils.isEmpty(idCardNo.getText().toString())){
            toast("身份证号不能为空");
            return false;
        }else   if(idNo.length() == 0 ||!Util.checkIdCard(idNo)){
            ToastUtil.toast(context,R.string.id_no_input_error);
            return false;
        }else if (isCreditCard) {
            if (TextUtils.isEmpty(validityPeriod.getText())) {
                ToastUtil.toast(context, "有效期不能为空");
                return false;
            }
            if (TextUtils.isEmpty(safetyCode.getText())) {
                ToastUtil.toast(context, "安全码不能为空");
                return false;
            }
        }
        if (bankMobilePhone.getText().length() != 11) {
            ToastUtil.toast(context, "手机号码长度为11位");
            return false;
        }
        if (!Util.checkPhoneNumber(bankMobilePhone.getText().toString())) {
            ToastUtil.toast(context, R.string.plat_plese_input_your_phonenumber_error);
            return false;
        }
        return true;
    }

    /**
     * popwindow 日期
     */
    private void initDatePop() {

        pophead = new PopupWindow(this);
        viewPop = getLayoutInflater().inflate(R.layout.wheel_date_picker, null);

        ll_data = (LinearLayout) viewPop.findViewById(R.id.pLayout);
        pophead.setWidth(ViewPager.LayoutParams.MATCH_PARENT);
        // pop.setHeight(LayoutParams.WRAP_CONTENT);
        pophead.setHeight(ViewPager.LayoutParams.MATCH_PARENT);
        pophead.setBackgroundDrawable(new BitmapDrawable());
        pophead.setFocusable(true);
        pophead.setOutsideTouchable(true);
        pophead.setContentView(viewPop);
        RelativeLayout p = (RelativeLayout) viewPop.findViewById(R.id.parentLayout);
        p.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pophead.dismiss();
                ll_data.clearAnimation();
            }
        });

    }

    private void getDataPick() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
        int curMonth = 0;
        int curDate = 0;
        int curYear = 0;
        if (!TextUtils.isEmpty(validityPeriod.getText().toString().trim()) && validityPeriod.getText().toString().trim().contains("/")) {
            String[] dates = birthday.split("/");

            curMonth = Integer.parseInt(dates[0]);
            curDate = Integer.parseInt(dates[0]);
            curYear = Integer.parseInt(dates[1]);
        } else {
            curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
            curDate = c.get(Calendar.DATE);
            curYear = norYear;

        }
//		int curYear = mYear;
//		int curMonth = mMonth + 1;
//		int curDate = mDay;

        year = (WheelView) viewPop.findViewById(R.id.year);
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, norYear, 3000);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);// 是否可循环滑动
        year.addScrollingListener(scrollListener);

        month = (WheelView) viewPop.findViewById(R.id.month);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        month.addScrollingListener(scrollListener);

        day = (WheelView) viewPop.findViewById(R.id.day);
        initDay(curYear, curMonth);
        day.setCyclic(false);

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        year.setCurrentItem(curYear - 2016);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);


        Button btn_queding = (Button) viewPop.findViewById(R.id.btn_queding);
        Button btn_quxiao = (Button) viewPop.findViewById(R.id.btn_quxiao);

        btn_queding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != pophead) {
                    pophead.dismiss();
                    ll_data.clearAnimation();
                }
/*
                String birthday = new StringBuilder().append((year.getCurrentItem() + 1950)).append("-")
                        .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                                : (month.getCurrentItem() + 1))
                        .append("-").append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1)
                                : (day.getCurrentItem() + 1))
                        .toString();
*/
                String ys=String.valueOf(year.getCurrentItem() + 2016);
                ys=ys.substring(ys.length()-2,ys.length());
                birthday = new StringBuilder().append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                                : (month.getCurrentItem() + 1)).append("/")
                        .append(ys)
                        .toString();
                String birthday1 = new StringBuilder() .append((year.getCurrentItem() + 2016)).append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                                : (month.getCurrentItem() + 1))
                        .toString();
                birthday1= birthday1.substring(2,6);
               // birthday= birthday.substring(birthday.length()-2,birthday.length());
                quickCardBinBean.setCardExp(birthday1);
                validityPeriod.setText(birthday);
            }
        });
        btn_quxiao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != pophead) {
                    pophead.dismiss();
                    ll_data.clearAnimation();
                }

                // 取消
            }
        });

    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;// 年
            int n_month = month.getCurrentItem() + 1;// 月

            initDay(n_year, n_month);

            String birthday = new StringBuilder().append((year.getCurrentItem() + 1950)).append("-")
                    .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1)
                            : (month.getCurrentItem() + 1))
                    .append("-").append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1)
                            : (day.getCurrentItem() + 1))
                    .toString();
        }
    };

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /**
     */
    private void initDay(int arg1, int arg2) {
        int maxDay = getDay(arg1, arg2);
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this, 1, maxDay, "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
        if (maxDay <= day.getCurrentItem())
            day.setCurrentItem(maxDay - 1);
    }

    /**
     * 获取快捷解约短信验证码
     */
    private void getUnsignSMSCode(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.APPLY_SMS_UNSIGN);
        HttpRequestParams params = businessRequest.getRequestParams();
        params.put("realName",quickCardBinBean.getCustomerName() );
        params.put("idCardNo", quickCardBinBean.getIdentifier());
        params.put("bankCode", quickCardBinBean.getBankCode());
        params.put("bankName", quickCardBinBean.getBankName());
        params.put("accountNo", quickCardBinBean.getAccountNo());
        params.put("accountType", quickCardBinBean.getAccountType());
        params.put("mobileNum", quickCardBinBean.getMobileInBank());
        if (quickCardBinBean.getAccountType().equals("2")) {
            params.put("cVN2", quickCardBinBean.getCVN2());
            params.put("cardExp", quickCardBinBean.getCardExp());
        }

        User user= ApplicationEx.getInstance().getSession().getUser();
        params.put("termid",ApplicationEx.getInstance().getUser().getTerminalId());
        //  params.put("chntype",BusinessRequest.CHN_TYPE);
        params.put("chntype","99999");
        params.put("series", Utils.createSeries());
        params.put("rnd", Mac.getRnd() );
        //    params.put("mac", DeviceUtil.getMac(context));
        params.put("tdtm", Utils.createTdtm());
        params.put("gesturePwd","0");
        params.put("platform","android" );
        params.put("refreshToken", user.getRefreshToken());
        params.put("subChannelId","10000027" );
        params.put("telecode", TerminalKey.getTelecode());
        params.put("timeStamp",ApplicationEx.getInstance().getSession().getCurrentKSN() );

        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();
        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        params.put("guid",md5Value );
        params.put("mac", BusinessRequest.getMac(params));

        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String sid=jsonObject.optString("sid");
                        startACS(sid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast("数据解析异常");
                    }
                }else{
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        businessRequest.execute();
    }
}
