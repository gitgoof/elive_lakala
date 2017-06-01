package com.lakala.shoudan.activity.merchant.register;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.nativeplugin.CameraPlugin;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.activity.myaccount.AreaInfo;
import com.lakala.shoudan.activity.myaccount.Region;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.AreaInfoCallback;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.PostPicCallback;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.IMEUtil;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SpaceTextWatcher;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;
import com.lakala.shoudan.util.CardEditFocusChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 资料修改
 * 资料修改页面
 *
 * @author More
 */
public class UpdateMerchantInfoActivity extends BasePwdAndNumberKeyboardActivity implements OnClickListener, CameraPlugin.CameraListener {

    private static final String TAG_CARD_NUMBER = "INPUT_CREDIT_NUMBER";
    private ImageView idPhotoFront;//正
    private ImageView idPhotoBack;//反
    private TableRow openbankName;//开户行
    View viewGroup;

    private Button doneButton;
    private TextView mOpenBankEdit;
    private TextView registerComfirm;//确认按钮
    private static final int REQUEST_OPENBANK_LIST = 99;
    private static final int REQUEST_COMPANY = 0x1234;
    private OpenBankInfo mOpenBankInfo;//开户行信息
    private ShoudanRegisterInfo registerInfo = new ShoudanRegisterInfo();//账户信息 Class
    private RadioButton companyAccount, individualAccount;
    private InputMethodManager imm;
    private MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
    private CardEditFocusChangeListener cardChangeListener;

    private final String COMPANY_ACCOUNT = "1";//对公账户
    private final String INDIVIDUAL_ACCOUNT = "0";//私人账户
    private String accountType;
    private EditText bankAccountNo, bankAccountName, id, realName, email, mrchName, mrchAddress;
    private ImageView iv_location;
    private boolean isShou = false;


    private TextView tvOpenBankType;
    private TextView tvArea;
    private CameraPlugin cameraPlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_update_merchant_info);
        initUI();
        cameraPlugin = new CameraPlugin();
        cameraPlugin.register(this, this);
    }

    /**
     * 点击事件：开户行，身份证照片插入，确认提交注册按键
     */
    @Override
    public void onClick(View v) {
        IMEUtil.hideIme(UpdateMerchantInfoActivity.this);
        switch (v.getId()) {
            case R.id.id_keypad_hide:
                hideNumberKeyboard();
                viewGroup.requestFocus();

                break;
            case R.id.bank_type://账户类型
            case R.id.img_account_type:
                showPrivateOrPublicBankType();
                break;
            case R.id.bank://开户行
            case R.id.openbank_img:
                                if (accountType.equals(COMPANY_ACCOUNT)) {
                                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType.PUBLIC,
                                            REQUEST_COMPANY);
                                }
//                if (INDIVIDUAL_ACCOUNT.equals(accountType)) {//个人账号时,不可点击此view选择开户行
//                    return;
//                }
                break;
            case R.id.shoudan_register_comfirm://确认提交注册
                if (isInputValid()) {
                    //数据合法，提交注册
//                    disableView(registerComfirm);//注册按钮不可用，通过弹出 dialog 出发 onResume，恢复
                    registerThread();
                }
                break;
            case R.id.shoudan_id_photo_front://插入身份证正面
                cameraPlugin.put("clickId", R.id.shoudan_id_photo_front);
                cameraPlugin.showActionChoose();
                break;
            case R.id.shoudan_id_photo_back://插入身份证反面
                cameraPlugin.put("clickId", R.id.shoudan_id_photo_back);
                cameraPlugin.showActionChoose();
                break;
            case R.id.radio_enterprise_account:
                companyAccount.setBackgroundResource(R.drawable.tab_2way_right_selected);
                individualAccount.setBackgroundResource(R.drawable.tab_2way_left_normal);

                personBankInfo.bankName = mOpenBankEdit.getText().toString();//缓存个人账户信息：开户行，开户行名称
                personBankInfo.bankNo = registerInfo.getBankNo();
                personBankInfo.accountNo = bankAccountNo.getText().toString();
                personBankInfo.accountName = bankAccountName.getText().toString();
                initBankInfo();
                mOpenBankEdit.setText(companyBankInfo.bankName);
                bankAccountName.setText(companyBankInfo.accountName);
                bankAccountNo.setText(companyBankInfo.accountNo);

                registerInfo.setBankName(companyBankInfo.bankName);
                registerInfo.setBankNo(companyBankInfo.bankNo);

                accountType = COMPANY_ACCOUNT;
                disableView(companyAccount);
                enableView(individualAccount);
                break;
            case R.id.radio_private_account:
                companyAccount.setBackgroundResource(R.drawable.tab_2way_right_normal);
                individualAccount.setBackgroundResource(R.drawable.tab_2way_left_selected);

                companyBankInfo.bankName = mOpenBankEdit.getText().toString();//缓存对公账户信息：开户行，开户行名称
                companyBankInfo.bankNo = registerInfo.getBankNo();
                companyBankInfo.accountNo = bankAccountNo.getText().toString();
                companyBankInfo.accountName = bankAccountName.getText().toString();

                initBankInfo();
                mOpenBankEdit.setText(personBankInfo.bankName);
                bankAccountName.setText(personBankInfo.accountName);
                bankAccountNo.setText(personBankInfo.accountNo);

                registerInfo.setBankName(personBankInfo.bankName);
                registerInfo.setBankNo(personBankInfo.bankNo);
                accountType = INDIVIDUAL_ACCOUNT;
                disableView(individualAccount);
                enableView(companyAccount);
                break;
            case R.id.tv_area_value://选择地区
//
//                break;
//            case R.id.img_area_more:
//                getChinad();
//                break;
            case R.id.layout_area:
//                if(isShou){
                    getChinas();
//                }
                break;
            default:
                break;
        }
    }

    private BankInfo personBankInfo = new BankInfo();
    private BankInfo companyBankInfo = new BankInfo();


    private void getChinad() {
        showProgressWithNoMsg();
        final LocationManager locationManager = LocationManager.getInstance();
        locationManager.setFirst(false);
        locationManager.startLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocate() {
                getArea();
                isShou=false;
                iv_location.setVisibility(View.GONE);
            }

            @Override
            public void onFailed() {
                getArea();
                showIsOk();
                hideProgressDialog();
            }
        });

    }

    private void getArea() {
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.MERCHANT_LOCATION);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String provinceName = jsonObject.optString("provinceName");
                        String cityName = jsonObject.optString("cityName");
                        String districtName = jsonObject.optString("districtName");
                        if (!TextUtils.isEmpty(provinceName)) {
                            tvArea.setText(provinceName + cityName + districtName);
                        } else {
                            return;
                        }
                        registerInfo.setProvince(provinceName);
                        registerInfo.setCity(cityName);
                        registerInfo.setDistrict(districtName);
                        registerInfo.setProvinceCode(jsonObject.optString("province"));
                        registerInfo.setCityCode(jsonObject.optString("city"));
                        registerInfo.setDistrictCode(jsonObject.optString("district"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtil.print(resultServices.retMsg);
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        cameraController.handleActivityResult(requestCode,resultCode, data);
        //开户行 返回结果处理
        if (requestCode == REQUEST_COMPANY && UniqueKey.BANK_LIST_CODE == resultCode) {
            mOpenBankInfo = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
            updateBankInfo();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == UniqueKey.BANK_LIST_CODE) {
//            //开户行 返回结果处理
//            if (requestCode == REQUEST_OPENBANK_LIST) {
//                mOpenBankInfo = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
//                updateBankInfo();
//            } else {
//                super.onActivityResult(requestCode, resultCode, data);
//            }
//        }
//    }

    private void getChina() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.MERCHANT_LOCATION);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String provinceName = jsonObject.optString("provinceName");
                        String cityName = jsonObject.optString("cityName");
                        String districtName = jsonObject.optString("districtName");
                        if (!TextUtils.isEmpty(provinceName)) {
                            tvArea.setText(provinceName + cityName + districtName);
                        } else {
                            return;
                        }
                        registerInfo.setProvince(provinceName);
                        registerInfo.setCity(cityName);
                        registerInfo.setDistrict(districtName);
                        registerInfo.setProvinceCode(jsonObject.optString("province"));
                        registerInfo.setCityCode(jsonObject.optString("city"));
                        registerInfo.setDistrictCode(jsonObject.optString("district"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtil.print(resultServices.retMsg);
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, request);

    }


    String[] errorType = {"10000", "10001", "10002", "10003", "10004", "10005"};

    private void showEditableView() {
        int[] editsview = new int[]{
                R.id.id_tap,//身份证号码 4
                R.id.layout_card_pic,//身份证 4

                R.id.mer_name_tap,//商户名称 0

                R.id.email_tap,//email 如果都无需修改，该view隐藏 2
                R.id.mer_addr_tap,//经营地址 1
                R.id.layout_area,//所在地区 1

                //R.id.radio_layout控制整个银行信息，包括类型
                R.id.name_tap,//真实姓名 3
                R.id.bank_tap,//开户银行 3
                R.id.bank_account_tap,//卡号 3
                R.id.account_name_tap//户名 3
        };

        for (int i = 0; i < editsview.length; i++) {
            findViewById(editsview[i]).setVisibility(View.GONE);
        }

        findViewById(R.id.radio_layout).setVisibility(View.GONE);

        for (int i = 0; i < errorType.length; i++) {
            if (ApplicationEx.getInstance().getUser().getMerchantInfo().has(errorType[i])) {
                switch (i) {
                    case 0:
                        findViewById(R.id.mer_name_tap).setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        findViewById(R.id.mer_addr_tap).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout_area).setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        findViewById(R.id.email_tap).setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        if (!merchantInfo.getUser().isIdInfoValid()) {
                            findViewById(R.id.name_tap).setVisibility(View.VISIBLE);
                        }
                        findViewById(R.id.bank_tap).setVisibility(View.VISIBLE);
                        findViewById(R.id.account_name_tap).setVisibility(View.VISIBLE);
                        findViewById(R.id.bank_account_tap).setVisibility(View.VISIBLE);
                        findViewById(R.id.radio_layout).setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        findViewById(R.id.id_tap).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout_card_pic).setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        findViewById(R.id.layout_card_pic).setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private void showPrivateOrPublicBankType() {
        String[] items = new String[]{"个人账户", "对公账户"};
        Builder builder = new Builder(UpdateMerchantInfoActivity.this);
        builder.setTitle("请选择账户类型");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://个人账户
                        if ("个人账户".equals(tvOpenBankType.getText().toString().trim())) {
                            break;
                        }
                        tvOpenBankType.setText("个人账户");
                        companyBankInfo.bankName = mOpenBankEdit.getText().toString();//缓存对公账户信息：开户行，开户行名称
                        companyBankInfo.bankNo = registerInfo.getBankNo();
                        companyBankInfo.accountNo = bankAccountNo.getText().toString();
                        companyBankInfo.accountName = bankAccountName.getText().toString();

                        initBankInfo();
                        mOpenBankEdit.setText(personBankInfo.bankName);
                        bankAccountName.setText(personBankInfo.accountName);
                        bankAccountNo.setText(personBankInfo.accountNo);

                        registerInfo.setBankName(personBankInfo.bankName);
                        registerInfo.setBankNo(personBankInfo.bankNo);
                        accountType = INDIVIDUAL_ACCOUNT;
                        findViewById(R.id.openbank_img).setVisibility(View.GONE);
                        break;
                    case 1://企业账号
                        if ("对公账户".equals(tvOpenBankType.getText().toString().trim())) {
                            break;
                        }
                        tvOpenBankType.setText("对公账户");
                        personBankInfo.bankName = mOpenBankEdit.getText().toString();//缓存个人账户信息：开户行，开户行名称
                        personBankInfo.bankNo = registerInfo.getBankNo();
                        personBankInfo.accountNo = bankAccountNo.getText().toString();
                        personBankInfo.accountName = bankAccountName.getText().toString();
                        initBankInfo();
                        mOpenBankEdit.setText(companyBankInfo.bankName);
                        bankAccountName.setText(companyBankInfo.accountName);
                        bankAccountNo.setText(companyBankInfo.accountNo);

                        registerInfo.setBankName(companyBankInfo.bankName);
                        registerInfo.setBankNo(companyBankInfo.bankNo);

                        accountType = COMPANY_ACCOUNT;
                        findViewById(R.id.openbank_img).setVisibility(View.VISIBLE);
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 验证输入
     */
    protected boolean isInputValid() {

        //非必要字段
        //默认字段
        registerInfo.setIdCardType("ID");
        String idCardNo = id.getText().toString().trim();
        //证件号号码验证
        //默认身份证
        if (idCardNo.length() == 0 || !Util.checkIdCard(idCardNo)) {
            showInputTipsDialog(R.string.id_no_input_error);
            return false;
        }
        registerInfo.setIdCardNo(idCardNo);
        //姓名验证
        String realNameStr = realName.getText().toString().trim();
        if (realNameStr.length() == 0) {
            showInputTipsDialog(R.string.real_name_input_error);
            return false;
        }
        registerInfo.setRealName(realNameStr);
        //邮箱地址验证
        String emailStr = email.getText().toString().trim();
        if (emailStr.length() == 0 || !Util.checkEmailAddress(emailStr)) {
            showInputTipsDialog(R.string.email_input_error);
            return false;
        }
        registerInfo.setEmail(emailStr);

        //照片上传与否验证  需要修改照片的时候才需要
        if (ApplicationEx.getInstance().getUser().getMerchantInfo().has("10004")) {
            if (TextUtils.isEmpty(photoMap.get(R.id.shoudan_id_photo_front)) || TextUtils.isEmpty(photoMap.get(R.id.shoudan_id_photo_back))) {
                showInputTipsDialog(R.string.photo_not_commit);
                return false;
            }
        }


        //商户名称验证
        String businessNameStr = mrchName.getText().toString().trim();
        if (businessNameStr.length() == 0) {
            showInputTipsDialog(R.string.mrch_name_input_error);
            return false;
        }

        if (!Util.checkMerchantName(businessNameStr)) {
            showInputTipsDialog(R.string.merchant_name_has_error);
            return false;
        }

        try {
            if (new String(businessNameStr.getBytes("GBK"), "ISO8859_1").length() > 64) {
                showInputTipsDialog(R.string.mrch_name_input_length_error);
                return false;
            }
        } catch (Exception e) {
            if (Parameters.debug) {
                LogUtil.d(getClass().getName(), "Unsupported encoding", e);
            }
        }

        registerInfo.setBusinessName(businessNameStr);

        if ("".equals(registerInfo.getProvince())) {
            showInputTipsDialog(R.string.dist_not_selected);
            return false;
        }

        //商户经营地址验证
        String businessAddrStr = mrchAddress.getText().toString().trim();
        if ("".equals(businessAddrStr)) {
            showInputTipsDialog(R.string.mrch_address_input_error);
            return false;
        }
        registerInfo.setBusinessAddr(businessAddrStr);

        //账户类型选择
        registerInfo.setAccountType(accountType);

        //开户行信息验证

        if (registerInfo.getBankNo() == null || registerInfo.getBankNo().length() == 0) {
            showInputTipsDialog(R.string.open_account_bank_not_selected);
            return false;
        }

        //银行账户号验证
        String accountNoStr = bankAccountNo.getText().toString().trim().replace(" ", "");
        if (accountNoStr.length() <= 0 || accountNoStr.length() > 32) {
            showInputTipsDialog(R.string.bankaccount_no_input_error);
            return false;
        }
        registerInfo.setAccountNo(accountNoStr);

        //开户名验证
        String accountNameStr = bankAccountName.getText().toString().trim();
        if (accountNameStr.length() == 0) {
            showInputTipsDialog(R.string.bankaccount_name_input_error);
            return false;
        }
        if (INDIVIDUAL_ACCOUNT.equals(accountType) && !accountNameStr.equals(realNameStr)) {
            showInputTipsDialog(R.string.realname_bankaccount_name_not_match);
            return false;
        }
        registerInfo.setAccountName(accountNameStr);

        //补充需求文档要求:商户信息中的真实姓名和账户信息中的户名必须是同名，否则无法提交；
        return true;
    }

    /**
     * 显示输入错误 Dialog
     */
    private void showInputTipsDialog(int stringId) {

        com.lakala.ui.dialog.AlertDialog.AlertDialogDelegate listener = new com.lakala.ui.dialog
                .AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(com.lakala.ui.dialog.AlertDialog dialog, View view,
                                      int index) {
                dialog.dismiss();
            }
        };
        DialogCreator.createAlertDialog(context, "信息错误", getString(stringId),
                listener, "确定").show();

    }

    /**
     * 初始化 UI
     */
    protected void initUI() {
        cardChangeListener = new CardEditFocusChangeListener();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //设置标题
        navigationBar.setTitle("资料修改");
        //个人信息
        id = (EditText) findViewById(R.id.id);
        realName = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        id.addTextChangedListener(new SpaceTextWatcher());
        realName.addTextChangedListener(new SpaceTextWatcher());
        email.addTextChangedListener(new SpaceTextWatcher());
        iv_location = (ImageView) findViewById(R.id.iv_location);
        findViewById(R.id.layout_area).setOnClickListener(this);

        //身份证照片
        idPhotoFront = (ImageView) findViewById(R.id.shoudan_id_photo_front);
        idPhotoFront.setOnClickListener(this);
        idPhotoBack = (ImageView) findViewById(R.id.shoudan_id_photo_back);
        idPhotoBack.setOnClickListener(this);

        //商户信息
        mrchName = (EditText) findViewById(R.id.mer_name);
        mrchName.addTextChangedListener(new SpaceTextWatcher());
        mrchAddress = (EditText) findViewById(R.id.mer_addr);
        mrchAddress.addTextChangedListener(new SpaceTextWatcher());
        //开户行 名称
        openbankName = (TableRow) findViewById(R.id.shoudan_openbank);
        mOpenBankEdit = (TextView) findViewById(R.id.bank);
                mOpenBankEdit.setOnClickListener(this);
        findViewById(R.id.openbank_img).setOnClickListener(this);
        //开户行信息 账号
        bankAccountNo = (EditText) findViewById(R.id.bank_account);
        //        bankAccountNo.addTextChangedListener(new BackAccountNoTextWatcher(bankAccountNo));
        //开户姓名
        bankAccountName = (EditText) findViewById(R.id.account_name);
        bankAccountName.addTextChangedListener(new SpaceTextWatcher());

        //提交注册
        registerComfirm = (TextView) findViewById(R.id.shoudan_register_comfirm);
        registerComfirm.setText("提交");
        registerComfirm.setOnClickListener(this);

        //收款账户类型
        accountType = INDIVIDUAL_ACCOUNT;//默认是个人账户
        companyAccount = (RadioButton) findViewById(R.id.radio_enterprise_account);
        individualAccount = (RadioButton) findViewById(R.id.radio_private_account);
        companyAccount.setOnClickListener(this);
        individualAccount.setOnClickListener(this);

        tvOpenBankType = (TextView) findViewById(R.id.bank_type);
        tvOpenBankType.setOnClickListener(this);
        findViewById(R.id.img_account_type).setOnClickListener(this);
        //地区选择
        findViewById(R.id.tv_area_value).setOnClickListener(this);
        findViewById(R.id.img_area_more).setOnClickListener(this);
        viewGroup = findViewById(R.id.RelativeLayout1);
//        setOnDoneButtonClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewGroup.requestFocus();
//            }
//        });
        initCardListener();


        showEditableView();
        modifyModeInit();

    }

    private void initCardListener() {
        doneButton = (Button) findViewById(R.id.id_keypad_hide);
        doneButton.setOnClickListener(this);

        cardChangeListener.setCustomFlag(true);
        //开户业务
        cardChangeListener.setBankBusid(BankBusid.MPOS_ACCT);
        cardChangeListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if(!accountType.equals(COMPANY_ACCOUNT)){
                    mOpenBankInfo = openBankInfo;
                    if (mOpenBankInfo == null) {
                        ToastUtil.toast(context, errMsg);
                    } else {
                        updateBankInfo();
                    }
                }
            }
        });

        bankAccountNo.setOnFocusChangeListener(null);
        bankAccountNo.addTextChangedListener(new TextWatcher() {

            String before;
            int cursorIndex = bankAccountNo.getSelectionEnd();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                before = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cardNum = com.lakala.library.util.StringUtil.trim(editable.toString());
                if (TextUtils.isEmpty(cardNum)) {
                    mOpenBankEdit.setText("");
                    return;
                }

                StringBuffer str = new StringBuffer(editable.toString());
                if (str.toString().contains(" ")) {
                    if (str.length() > 1) {
                        bankAccountNo.setText(str.toString().replace(" ", ""));
                        bankAccountNo.setSelection(cursorIndex);
                    }
                }
                Pattern pattern = Pattern.compile("[\\d]");
                Matcher matcher = pattern.matcher(str);
                if (!"".equals(matcher.replaceAll("").trim())) {
                    if (matcher.replaceAll("").trim().length() > 1) {
                        bankAccountNo.setText(before);
                        bankAccountNo.setSelection(cursorIndex);

                    }
                }

                String cardNo = StringUtil.removeTrim(bankAccountNo.getText().toString());

                if (COMPANY_ACCOUNT.equals(accountType)) {
                    //对公账户不做卡bin查询 , 需求from
                    return;
                }
                if (cardNo.length() > 19) {
                    ToastUtil.toast(context, R.string.re_input_cardNo);
                }

            }
        });

        bankAccountNo.setTag(TAG_CARD_NUMBER);

        initNumberKeyboard();
        /**
         * @
         */
        initNumberEdit(bankAccountNo, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);


    }


    private void setCheckIndividual() {
        tvOpenBankType.setText("个人账户");
        accountType = INDIVIDUAL_ACCOUNT;
        cardChangeListener.setCustomFlag(true);
        mOpenBankEdit.setHint("开户银行");
        findViewById(R.id.v_bank_empty).setVisibility(View.VISIBLE);
        findViewById(R.id.support_bank_list).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_bank_type_arrow).setVisibility(View.GONE);
    }

    /**
     * 修改资料
     * <p/>
     * 获取的资料预显示
     */
    private void modifyModeInit() {

        registerInfo.setPicPath1(merchantInfo.getUser().getIdCardInfo().getPicPath1());
        registerInfo.setPicPath2(merchantInfo.getUser().getIdCardInfo().getPicPath2());
        registerInfo.setIdCardNo(merchantInfo.getUser().getIdCardInfo().getIdCardId());
        registerInfo.setHomeAddr(merchantInfo.getBusinessAddress().getHomeAddr());
        registerInfo.setAccountType(merchantInfo.getAccountType().getValue());
        registerInfo.setAccountNo(merchantInfo.getAccountNo());
        registerInfo.setBankName(merchantInfo.getBankName());
        registerInfo.setBankNo(merchantInfo.getBankNo());
        registerInfo.setAccountName(merchantInfo.getAccountName());
        registerInfo.setRealName(merchantInfo.getUser().getRealName());
        registerInfo.setEmail(merchantInfo.getEmail());

        MerchantInfo.BusinessAddressEntity businessAddressEntity = merchantInfo.getBusinessAddress();
        registerInfo.setProvince(businessAddressEntity.getProvinceName());
        registerInfo.setProvinceCode(businessAddressEntity.getProvince());
        registerInfo.setDistrict(businessAddressEntity.getDistrictName());
        registerInfo.setDistrictCode(businessAddressEntity.getDistrict());
        registerInfo.setCity(businessAddressEntity.getCityName());
        registerInfo.setCityCode(businessAddressEntity.getCity());
        registerInfo.setBusinessName(merchantInfo.getBusinessName());
        registerInfo.setZipCode(businessAddressEntity.getZipCode());
        registerInfo.setIdCardType(merchantInfo.getUser().getIdCardInfo().getIdCardType());




        id.setText(merchantInfo.getUser().getIdCardInfo().getIdCardId());
        //姓名
        realName.setText(merchantInfo.getUser().getRealName());

        //邮箱
        email.setText(merchantInfo.getEmail());

        //身份证正反面
        //地区
        tvArea = (TextView) findViewById(R.id.tv_area_value);
        tvArea.setText(businessAddressEntity.getDistDetail());

        //商户名
        mrchName.setText(merchantInfo.getBusinessName());

        //商户地址
        mrchAddress.setText(merchantInfo.getBusinessAddress().getHomeAddr());

        //账户类型
        accountType = merchantInfo.getAccountType().getValue();
        if (accountType != null) {
            if (!accountType.equals(INDIVIDUAL_ACCOUNT) && !accountType.equals(COMPANY_ACCOUNT)) {
                accountType = INDIVIDUAL_ACCOUNT;
            }
        } else {
            accountType = INDIVIDUAL_ACCOUNT;
        }
        if (accountType.equals(INDIVIDUAL_ACCOUNT)) {
            tvOpenBankType.setText("个人账户");
            findViewById(R.id.openbank_img).setVisibility(View.GONE);
        } else {
            tvOpenBankType.setText("对公账户");
            findViewById(R.id.openbank_img).setVisibility(View.VISIBLE);
        }

        setOpenBackInfo(merchantInfo.getBankName(), merchantInfo.getBankNo());
        //开户行
        bankAccountNo.setText(merchantInfo.getAccountNo());
        //开户行名
        bankAccountName.setText(merchantInfo.getAccountName());

        //		产品需求,errField时可修改用户信息，不需要判断实名状态
        //        if(!ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getAuthStatus().getValue()){
        //        	setUsrInfoUnEditable();
        //        }
        if (ApplicationEx.getInstance().getUser().getMerchantInfo().getMerchantStatus() != MerchantStatus.FAILURE) {
            //0:未开通;1:已开通;2:冻结;3:审核未通过; 4,修改过注册信息并且通过审核
            setMerchantInfoUnEdita();
        }
    }

    private void setMerchantInfoUnEdita() {
        email.setFocusable(false);
        mrchName.setFocusable(false);
        mrchAddress.setFocusable(false);
        companyAccount.setClickable(false);
        companyAccount.setFocusable(false);
        individualAccount.setClickable(false);
        individualAccount.setFocusable(false);
        bankAccountNo.setClickable(false);
        bankAccountNo.setFocusable(false);
        bankAccountName.setClickable(false);
        bankAccountName.setFocusable(false);
        findViewById(R.id.bank).setClickable(true);
        findViewById(R.id.tv_area_value).setFocusable(false);
        findViewById(R.id.tv_area_value).setClickable(false);
    }

    private void setUsrInfoUnEditable() {

        idPhotoFront.setClickable(false);
        idPhotoBack.setClickable(false);
        id.setFocusable(false);
        realName.setFocusable(false);
    }

    private void setOpenBackInfo(String bankName, String bankNo) {

        mOpenBankEdit.setText(bankName);
        //联网获取图片
        // ImageUtil.loadBankLogo(this, mOpenBankImg, bankNo, "");
    }

    @Override
    protected void onResume() {
        enableView(registerComfirm);//注册 Button 重新可用
        if (accountType.equals(INDIVIDUAL_ACCOUNT)) {
            companyAccount.setBackgroundResource(R.drawable.tab_2way_right_normal);
            individualAccount.setBackgroundResource(R.drawable.tab_2way_left_selected);

        } else {
            companyAccount.setBackgroundResource(R.drawable.tab_2way_right_selected);
            individualAccount.setBackgroundResource(R.drawable.tab_2way_left_normal);
        }
        super.onResume();
    }


    private void registerThread() {
        showProgressDialog(R.string.committing_register_info);
        if (isCardNoLegal(registerInfo.getAccountNo(), registerInfo.getBankNo())) {//匹配卡号
            boolean shouldPostPic = false;
            if (ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().isIdInfoValid()) {//审核中不需要再提交通户信息
                shouldPostPic = false;
            } else if (!merchantInfo.has("10004") && !merchantInfo.has("10005")) {
                shouldPostPic = false;
            } else {
                shouldPostPic = true;
            }
            if (shouldPostPic) {
                postPicData(new PostPicCallback() {
                    @Override
                    public void onCallback(boolean isPostSuccess) {
                        if (isPostSuccess) {//信息提交之前要上传照片
                            startResgister();
                        } else {
                            toastInternetError();
                            hideProgressDialog();
                        }
                    }
                });
            } else {
                startResgister();
            }
        }
    }

    /**
     * 提交注册
     */
    private void startResgister() {
        ShoudanService.getInstance().merchantManage(registerInfo, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {

                    try {
                        ApplicationEx.getInstance().getUser().setMerchantInfo(new MerchantInfo((resultServices.retData)));
//                        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
                        showRegisterSuccessDialog();
                        ShoudanStatisticManager.getInstance()
                                .onEvent(ShoudanStatisticManager.Merchant_Merchant_Info_Change_Comfirm, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if("005087".equals(resultServices.retCode)){
                    ToastUtil.toast(context,resultServices.retMsg);
                } else {
                    showMessage(resultServices.retMsg);
                }

                hideProgressDialog();

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                showMessage(R.string.socket_error);

                hideProgressDialog();
            }
        });
    }


    private void showRegisterSuccessDialog() {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                callMainActivity();
            }
        };
        DialogCreator.createOneConfirmButtonDialog(context, getString(R.string.ok),
                "资料提交成功，您现在可以使用拉卡拉收款宝进行收款。", listener).show();
    }




    /**
     * 获取开户行信息，更新UI
     */
    private void updateBankInfo() {
        mOpenBankEdit.setText(mOpenBankInfo.bankname);
        registerInfo.setBankName(mOpenBankInfo.bankname);
        registerInfo.setBankNo(mOpenBankInfo.bankCode);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        Object tag = v.getTag();
        if (TextUtils.equals(String.valueOf(tag), TAG_CARD_NUMBER)) {
            cardChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.id_input_scroll));
    }

    /**
     * 清空开户行信息
     */
    private void initBankInfo() {
        mOpenBankInfo = null;
        mOpenBankEdit.setText("");
        bankAccountNo.setText("");
        bankAccountName.setText("");
        registerInfo.setBankName("");
        registerInfo.setBankNo("");
    }


    private boolean isCardNoLegal(String cardNo, String bankCode) {

        return true;
    }

    /**
     * 提交照片文件
     * <p/>
     * 返回提交照片是否成功 boolean
     */
    private void postPicData(final PostPicCallback postPicCallback) {


        CommonServiceManager.getInstance().idCardImageUploadPos(ApplicationEx.getInstance().getUser().getLoginName(),
                photoMap.get(R.id.shoudan_id_photo_front),
                photoMap.get(R.id.shoudan_id_photo_back),
                registerInfo.getRealName(),
                registerInfo.getIdCardType(),
                registerInfo.getIdCardNo(),
                registerInfo.getEmail(),
                new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {

                        if (resultServices.isRetCodeSuccess()) {
                            postPicCallback.onCallback(true);
                        } else {
//                            postPicCallback.onCallback(false);
                            ToastUtil.toast(UpdateMerchantInfoActivity.this,resultServices.retMsg);
                        }

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        postPicCallback.onCallback(false);
                    }
                });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    class BankInfo {
        public String bankName = "";//银行名字
        public String bankNo = "";
        public String accountNo = "";//开户账号
        public String accountName = "";//开户人
    }

    private Map<Integer, String> photoMap = new HashMap<>();

    @Override
    public void onSuccess(String picFilePath) {
        int clickId = cameraPlugin.get("clickId");
        photoMap.put(clickId, picFilePath);
        UILUtils.displayWithoutCache(picFilePath, (ImageView) findViewById(clickId));
    }

    @Override
    public void onFailed() {

    }

    private void getChinas() {
        if (areaSelecitonDialog == null) {
            showProgressWithNoMsg();
            AreaInfoCallback callback = new AreaInfoCallback() {
                @Override
                public void onSuccess(List<Region> regions) {
                    hideProgressDialog();
                    showAreaSelectionDialog(regions);
                }

                @Override
                public void onEvent(HttpConnectEvent connectEvent) {
                    hideProgressDialog();
                    LogUtil.print(connectEvent.getDescribe());
                }
            };
            ShoudanService.getInstance().queryAreaInfo(callback);
        } else {
            showAreaSelectionDialog(null);
        }
    }

    private Dialog areaSelecitonDialog;

    private void showAreaSelectionDialog(List<Region> regions) {

        if (areaSelecitonDialog == null) {
            final com.lakala.shoudan.activity.myaccount.SpinnerDialog spinnerDialog = new com.lakala.shoudan.activity.myaccount.SpinnerDialog(context, regions);
            spinnerDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AreaInfo areaInfo = spinnerDialog.getSelectionRegion();
//                    TextView tvArea = (TextView)findViewById(R.id.tv_area_value);
//                    tvArea.setText(areaInfo.getInfo());
                    tvArea.setText(areaInfo.getInfo());
                    registerInfo.setProvince(areaInfo.getName());
                    registerInfo.setProvinceCode(areaInfo.getCode());
                    registerInfo.setCity(areaInfo.getChild().getName());
                    registerInfo.setCityCode(areaInfo.getChild().getCode());
                    registerInfo.setDistrict(areaInfo.getChild().getChild().getName());
                    registerInfo.setDistrictCode(areaInfo.getChild().getChild().getCode());
                }
            });
            spinnerDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            areaSelecitonDialog = spinnerDialog.create();
        }
        areaSelecitonDialog.show();
    }

    public void showIsOk() {
        DialogCreator.createFullContentDialog(context, "取消", "确定", "没有成功获取您当前的位置信息，点击确定可手动选择您所在的区域，点击取消重新定位您的当前位置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        isShou = true;
                        iv_location.setVisibility(View.VISIBLE);
                    }
                }).show(getSupportFragmentManager());
    }
    //
    //    private void getChina(){
    //        if(areaSelecitonDialog == null){
    //
    //            showProgressWithNoMsg();
    //            AreaInfoCallback callback = new AreaInfoCallback() {
    //                @Override
    //                public void onSuccess(List<Region> regions) {
    //                    hideProgressDialog();
    //                    showAreaSelectionDialog(regions);
    //                }
    //
    //                @Override
    //                public void onEvent(HttpConnectEvent connectEvent) {
    //                    hideProgressDialog();
    //                }
    //            };
    //            ShoudanService.getInstance().queryAreaInfo(callback);
    //        }else{
    //            showAreaSelectionDialog(null);
    //        }
    //    }

    //    private Dialog areaSelecitonDialog;
//
//    private void showAreaSelectionDialog(List<Region> regions) {
//
//        if (areaSelecitonDialog == null) {
//            final SpinnerDialog spinnerDialog = new SpinnerDialog(UpdateMerchantInfoActivity.this, regions);
//            spinnerDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    AreaInfo areaInfo = spinnerDialog.getSelectionRegion();
//                    TextView tvArea = (TextView) findViewById(R.id.tv_area_value);
//                    tvArea.setText(areaInfo.getInfo());
//                    registerInfo.setProvince(areaInfo.getName());
//                    registerInfo.setProvinceCode(areaInfo.getCode());
//                    registerInfo.setCity(areaInfo.getChild().getName());
//                    registerInfo.setCityCode(areaInfo.getChild().getCode());
//                    registerInfo.setDistrict(areaInfo.getChild().getChild().getName());
//                    registerInfo.setDistrictCode(areaInfo.getChild().getChild().getCode());
//                }
//            });
//            spinnerDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                }
//            });
//            areaSelecitonDialog = spinnerDialog.create();
//        }
//        areaSelecitonDialog.show();
//    }
}
