package com.lakala.shoudan.activity.merchant.register;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
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
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.BankChooseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.AreaInfoCallback;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.PostPicCallback1;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SpaceTextWatcher;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;
import com.lakala.shoudan.util.CardEditFocusChangeListener;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户注册
 * 商户开通
 *
 * @author More
 */
public class MerchantRegisterActivity extends BasePwdAndNumberKeyboardActivity implements OnClickListener {

    private static final String TAG_CARD_NUMBER = "INPUT_CREDIT_NUMBER";
    private ImageView idPhotoFront;//正
    private ImageView idPhotoBack;//反
    private TextView mOpenBankEdit;
    private TextView tvOpenBankType;
    private Button registerComfirm;//确认按钮
    private static final int REQUEST_OPENBANK_LIST = 99;
    private static final int REQUEST_COMPANY = 0x1234;
    private OpenBankInfo mOpenBankInfo;//开户行信息
    private ShoudanRegisterInfo registerInfo = new ShoudanRegisterInfo();//账户信息 Class
    private RadioButton companyAccount, individualAccount;
    private InputMethodManager imm;
    private TextView tvArea;

    private final String COMPANY_ACCOUNT = "1";//对公账号
    private final String INDIVIDUAL_ACCOUNT = "0";//个人账号
    private String accountType;
    private EditText bankAccountNo, bankAccountName, id, realName, email, mrchName, mrchAddress;
    private RadioGroup rgStep;//步骤提示
    private RadioButton rbBaseInfo, rbMerchantInfo, rbBankAccountInfo;
    private TextView btnPre;
    private String personalName = "";//个人账户姓名
    private View layoutBaseInfo, layoutMerchantInfo, layoutBankAccountInfo;
    private ImageView iv_location;
    private boolean isShou = false;

    private View shoudan_openbank;
    private View v_tag;
    private TextView tv_result;

    /**
     * 身份证及头像照片原文件  将原图片保存在本地
     */
    private CheckBox checkBox;
    //    private CheckBox checkBox1;
    private CardEditFocusChangeListener cardChangeListener;
    private View viewGroup;
    private CameraPlugin cameraPlugin;
    private CameraPlugin.CameraListener cameraListener = new CameraPlugin.CameraListener() {
        @Override
        public void onSuccess(String filePath) {
            int id = 0;
            try {
                id = cameraPlugin.get("clickedId");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (id == 0) {
                return;
            }
            photoMap.put(id, filePath);
            UILUtils.displayWithoutCache(filePath, (ImageView) findViewById(id));
        }

        @Override
        public void onFailed() {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_register);
        cameraPlugin = new CameraPlugin()
                .register(context, cameraListener);
        initUI();
    }

    private void showPrivateOrPublicBankType() {
        String[] items = new String[]{"个人账户", "对公账户"};
        DialogCreator.createCancelableListDialog(context, "请选择账户类型", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://个人账户
                        if ("个人账户".equals(tvOpenBankType.getText().toString().trim())) {
                            break;
                        }
//                        if(!COMPANY_ACCOUNT.equals(accountType)){
//                            checkCardBIN(false, StringUtil.removeTrim(bankAccountNo.getText().toString()));
//                        }
                        setCheckIndividual();
                        bankAccountName.setText(personalName);//开户名字
                        bankAccountName.setEnabled(false);
                        break;
                    case 1://企业账号
                        if ("对公账户".equals(tvOpenBankType.getText().toString().trim())) {
                            break;
                        }
                        bankAccountName.setText("");
                        bankAccountName.setEnabled(true);
                        setCheckCompany();
                        break;
                }

            }
        }).show();
    }

    private void setCheckCompany() {
        tvOpenBankType.setText("对公账户");
        cardChangeListener.setCustomFlag(false);//对公账户，设置为不自动查询卡bin
        accountType = COMPANY_ACCOUNT;
        mOpenBankEdit.setHint("请选择开户银行");
        findViewById(R.id.v_bank_empty).setVisibility(View.GONE);
        findViewById(R.id.support_bank_list).setVisibility(View.GONE);
        findViewById(R.id.iv_bank_type_arrow).setVisibility(View.VISIBLE);
        shoudan_openbank.setVisibility(View.VISIBLE);
        v_tag.setVisibility(View.VISIBLE);
        tv_result.setVisibility(View.GONE);
        mOpenBankEdit.setText("");
        tv_result.setText("");
        mOpenBankInfo = null;
        bankAccountNo.setText("");
    }

    private void setCheckIndividual() {
        tvOpenBankType.setText("个人账户");
        accountType = INDIVIDUAL_ACCOUNT;
        cardChangeListener.setCustomFlag(true);
        mOpenBankEdit.setHint("开户银行");
        findViewById(R.id.v_bank_empty).setVisibility(View.VISIBLE);
        findViewById(R.id.support_bank_list).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_bank_type_arrow).setVisibility(View.GONE);
        shoudan_openbank.setVisibility(View.GONE);
        v_tag.setVisibility(View.GONE);
        tv_result.setVisibility(View.VISIBLE);
        mOpenBankEdit.setText("");
        tv_result.setText("");
        mOpenBankInfo = null;
        bankAccountNo.setText("");
    }

    /**
     * 点击事件：开户行，身份证照片插入，确认提交注册按键
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.id_keypad_hide:
                viewGroup.requestFocus();
                if (!COMPANY_ACCOUNT.equals(accountType)) {
                    int leght = bankAccountNo.getText().toString().replace(" ", "").length();
                    if (!(leght >= 14 && leght <= 19)) {
                        tv_result.setTextColor(getResources().getColor(R.color.yelo));
                        tv_result.setText("请输入正确的银行卡号");
                    }
                }
                break;
            case R.id.bank_type:
            case R.id.img_account_type:
                //选择为个人账户或对公账户
                showPrivateOrPublicBankType();

                break;
            case R.id.tv_btn_to_write_f:
                //填写f码
                findViewById(R.id.edit_f_code).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_btn_to_write_f).setVisibility(View.GONE);
                break;
            case R.id.bank:
                if (INDIVIDUAL_ACCOUNT.equals(accountType)) {//个人账号时,不可点击此view选择开户行
                    return;
                }
            case R.id.iv_bank_type_arrow:
            case R.id.support_bank_list://开户行
                //开启支持银行的列表
                if (accountType.equals(COMPANY_ACCOUNT)) {
                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType
                            .PUBLIC, REQUEST_COMPANY);
                } else {

                    BankChooseActivity.openForResult(context, BankBusid.MPOS_ACCT, BankInfoType
                            .PRIVATE, REQUEST_OPENBANK_LIST);
                }
                break;
            case R.id.btn_next_baseinfo://确认提交注册
            case R.id.btn_next_bank:
            case R.id.btn_next_merchant:
                //三层页面的下一步
                if (isInputValid()) {
                    showNext();
                }
                break;
            case R.id.btn_pre_bank:
            case R.id.btn_pre_merchant:
                showPre();
                break;
            case R.id.shoudan_id_photo_front://插入身份证正面
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(MerchantRegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                cameraPlugin.put("clickedId", v.getId());
                cameraPlugin.showActionChoose();
                break;
            case R.id.shoudan_id_photo_back://插入身份证反面
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(MerchantRegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                cameraPlugin.put("clickedId", v.getId());
                cameraPlugin.showActionChoose();
                break;
//            case R.id.tv_area_value://选择地区
            case R.id.img_area_more:
                getChina();
                break;
            case R.id.ll_location:
                if (isShou) {
                    getChinas();
                }
                break;
            default:
                break;
        }
    }

    private void getChina() {
        showProgressWithNoMsg();
        final LocationManager locationManager = LocationManager.getInstance();
        locationManager.setFirst(false);
        locationManager.startLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocate() {
                getArea();
            }

            @Override
            public void onFailed() {
//                getArea();
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
                            registerInfo.setProvince(provinceName);
                            registerInfo.setCity(cityName);
                            registerInfo.setDistrict(districtName);
                            registerInfo.setProvinceCode(jsonObject.optString("province"));
                            registerInfo.setCityCode(jsonObject.optString("city"));
                            registerInfo.setDistrictCode(jsonObject.optString("district"));
                            isShou = false;
                            iv_location.setVisibility(View.GONE);

                        } else {
                            showIsOk();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {
                    LogUtil.print(resultServices.retMsg);
//                    ToastUtil.toast(context, resultServices.retMsg);
                    showIsOk();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
//                ToastUtil.toast(context, context.getString(R.string.socket_error));
                LogUtil.print(connectEvent.getDescribe());
                showIsOk();
            }
        });
        ServiceManagerUtil.getInstance().start(context, request);
    }

    private class SpinnerDialog extends Builder {

        private AreaInfo selectionRegion;

        private int provinceIndex;

        private int cityIndex;

        public AreaInfo getSelectionRegion() {
            return selectionRegion;
        }

        private SpinnerDialog(Context context, List<Region> regions) {
            super(context);

            init(context, regions);
        }

        private void init(final Context context, final List<Region> regions) {
            setTitle("请点击选择");

            final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.spinner_dialog, null);
            Spinner province = (Spinner) linearLayout.findViewById(R.id.province);
            province.setAdapter(new RegionAdapter(context, regions));
            province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Region region = (Region) adapterView.getItemAtPosition(i);
                    selectionRegion = new AreaInfo();
                    selectionRegion.setName(region.getName());
                    selectionRegion.setCode(region.getCode());
                    provinceIndex = i;
                    Spinner city = (Spinner) linearLayout.findViewById(R.id.city);
                    city.setAdapter(new RegionAdapter(context, regions.get(provinceIndex).getChildren()));
                    city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                           Region itemAtPosition = (Region) adapterView.getItemAtPosition(i);

                                                           selectionRegion.getChild().setCode(itemAtPosition.getCode());
                                                           selectionRegion.getChild().setName(itemAtPosition.getName());
                                                           Spinner region = (Spinner) linearLayout.findViewById(R.id.region);
                                                           cityIndex = i;
                                                           //修改没有第三项地区时送得内容
                                                           if (regions.get(provinceIndex).getChildren().get(cityIndex).getChildren() == null
                                                                   || regions.get(provinceIndex).getChildren().get(cityIndex).getChildren().size() == 0) {
                                                               //没有第三项内容，设置第三项内容未空
                                                               selectionRegion.getChild().getChild().setCode("");
                                                               selectionRegion.getChild().getChild().setName("");
                                                           }
                                                           region.setAdapter(new RegionAdapter(context, regions.get(provinceIndex).getChildren().get(cityIndex).getChildren()));
                                                           region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                                                                @Override
                                                                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                                                                }

                                                                                                @Override
                                                                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                                                    Region region = (Region) adapterView.getItemAtPosition(i);

                                                                                                    selectionRegion.getChild().getChild().setCode(region.getCode());
                                                                                                    selectionRegion.getChild().getChild().setName(region.getName());

                                                                                                }
                                                                                            }
                                                           );

                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> adapterView) {

                                                       }
                                                   }
                    );
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            setView(linearLayout);

        }

        public class AreaInfo {

            private String name;

            private String code;

            private AreaInfo child;

            public AreaInfo() {
            }

            public AreaInfo getChild() {
                if (child == null) {
                    child = new AreaInfo();
                }
                return child;
            }

            public void setChild(AreaInfo child) {
                this.child = child;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            @Override
            public String toString() {
                return "AreaInfo{" +
                        "name='" + name + '\'' +
                        ", code='" + code + '\'' +
                        ", child=" + child +
                        '}';
            }

            public String getInfo() {
                StringBuffer sb = new StringBuffer();
                sb.append(name);
                if (child.getName() != null) {
                    sb.append("-");
                    sb.append(child.getName());
                    if (child.getChild().getName() != null && !"".equals(child.getChild().getName())) {
                        sb.append("-");
                        sb.append(child.getChild().getName());
                    }
                }
                return sb.toString();
            }

        }


        private class RegionAdapter extends BaseAdapter {

            private Context context;
            private List<Region> regionList;

            private RegionAdapter(Context context, List<Region> regionList) {
                this.context = context;
                this.regionList = regionList;
            }

            @Override
            public int getCount() {
                return regionList.size();
            }

            @Override
            public Object getItem(int i) {
                return regionList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.region_list_item, null);
                TextView selection = (TextView) view.findViewById(R.id.tv);
                selection.setText(regionList.get(i).getName());
                return view;
            }
        }

    }

    /**
     * 判断基本信息
     *
     * @return
     */
    private boolean isBaseInfoInputValid() {
        //姓名验证
        String realNameStr = realName.getText().toString().trim();
        if (realNameStr.length() == 0) {
            ToastUtil.toast(context, R.string.real_name_input_error);
            return false;
        }

        registerInfo.setRealName(realNameStr);
        //邮箱地址验证
        String emailStr = email.getText().toString().trim();
        if (emailStr.length() == 0 || !Util.checkEmailAddress(emailStr)) {
            ToastUtil.toast(context, R.string.email_input_error);
            return false;
        }
        registerInfo.setEmail(emailStr);

        //非必要字段
        //默认字段
        registerInfo.setIdCardType("ID");
        String idCardNo = id.getText().toString().trim();
        //证件号号码验证
        //默认身份证
        if (idCardNo.length() == 0 || !Util.checkIdCard(idCardNo)) {
            ToastUtil.toast(context, R.string.id_no_input_error);
            return false;
        }
        registerInfo.setIdCardNo(idCardNo);

        //照片上传与否验证
        if (TextUtils.isEmpty(photoMap.get(R.id.shoudan_id_photo_front))) {
            ToastUtil.toast(context, R.string.photo_not_commit_zheng);
            return false;
        }
        if (TextUtils.isEmpty(photoMap.get(R.id.shoudan_id_photo_back))) {
            ToastUtil.toast(context, R.string.photo_not_commit_fan);
            return false;
        }
        return true;
    }

    private boolean isMerchantInfoInputValid() {
        //商户名称验证
        String businessNameStr = mrchName.getText().toString().trim();
        if (businessNameStr.length() == 0) {
            ToastUtil.toast(context, R.string.mrch_name_input_error);
            return false;
        }

        if (!Util.checkMerchantName(businessNameStr)) {
            ToastUtil.toast(context, R.string.merchant_name_has_error);
            return false;
        }

        int chineseSize = StringUtil.matchChinese(businessNameStr).length();
        String patternStr = "[a-zA-Z0-9\\.\\,\\()]";
        int oneWordSize = StringUtil.matchTarget(patternStr, businessNameStr).length();
        if (chineseSize * 2 + oneWordSize < 10) {
            ToastUtil.toast(context, "店铺名称不能少于5个字，支持汉字、字母");
            return false;
        }

        try {
            if (new String(businessNameStr.getBytes("GBK"), "ISO8859_1").length() > 64) {
                ToastUtil.toast(context, R.string.mrch_name_input_length_error);
                return false;
            }
        } catch (Exception e) {
            if (Parameters.debug) {
                LogUtil.d(getClass().getName(), "Unsupported encoding", e);
            }
        }

        registerInfo.setBusinessName(businessNameStr);

        if (TextUtils.isEmpty(tvArea.getText())) {
            ToastUtil.toast(context, R.string.dist_not_selected);
            return false;
        }

        //商户经营地址验证
        String businessAddrStr = mrchAddress.getText().toString().trim();
        if ("".equals(businessAddrStr)) {
            ToastUtil.toast(context, R.string.mrch_address_input_error);
            return false;
        }
        chineseSize = StringUtil.matchChinese(businessAddrStr).length();
        patternStr = "[a-zA-Z0-9]";
        oneWordSize = StringUtil.matchTarget(patternStr, businessAddrStr).length();
        if (chineseSize * 2 + oneWordSize < 10) {
            ToastUtil.toast(context, "详细地址不能少于5个字，支持汉字、字母、数字");
            return false;
        }
        registerInfo.setBusinessAddr(businessAddrStr);
        return true;
    }

    private boolean isBankAccountInfoInputValid() {
        //账户类型选择
        registerInfo.setAccountType(accountType);
        String privatePublicBankType = tvOpenBankType.getText().toString().trim();
        if (null == privatePublicBankType || "".equals(privatePublicBankType)) {
            ToastUtil.toast(context, R.string.pleanse_selected_pri_public_type);
            return false;
        }

        if (!privatePublicBankType.equals("个人账户") && !privatePublicBankType.equals("对公账户")) {
            ToastUtil.toast(context, R.string.pleanse_selected_pri_public_type);
            return false;
        }


        //开户名验证
        String accountNameStr = bankAccountName.getText().toString().trim();
//        if(accountNameStr.length() == 0){
//            ToastUtil.toast(context,R.string.bankaccount_name_input_error);
//            return false;
//        }
        if (INDIVIDUAL_ACCOUNT.equals(accountType) && !accountNameStr.equals(registerInfo.getRealName())) {
            ToastUtil.toast(context, R.string.realname_bankaccount_name_not_match);
            return false;
        }
        registerInfo.setAccountName(accountNameStr);

        //银行账户号验证
        String accountNoStr = bankAccountNo.getText().toString().trim().replace(" ", "");
        if (accountNoStr.length() <= 0 || accountNoStr.length() > 32) {
            ToastUtil.toast(context, R.string.bankaccount_no_input_error);
            return false;
        }
        registerInfo.setAccountNo(accountNoStr);

        //开户行信息验证

        if (registerInfo.getBankNo() == null || registerInfo.getBankNo().length() == 0) {
            ToastUtil.toast(context, R.string.open_account_bank_not_selected);
            return false;
        }

        //补充需求文档要求:商户信息中的真实姓名和账户信息中的户名必须是同名，否则无法提交；
        return true;
    }

    /**
     * 验证输入
     */
    protected boolean isInputValid() {

        int stepId = rgStep.getCheckedRadioButtonId();

        if (stepId == R.id.rb_base_info) {
            if (!isBaseInfoInputValid()) {//判断基本信息是否输入
                return false;
            }
        } else if (stepId == R.id.rb_merchant_info) {
            if (!isMerchantInfoInputValid()) {
                return false;
            }
        } else if (stepId == R.id.rb_bank_account_info) {
            if (!isBankAccountInfoInputValid()) {
                return false;
            }
        }
        return true;
    }

    private void showNext() {
        int stepId = rgStep.getCheckedRadioButtonId();
        if (stepId == R.id.rb_base_info) {
            rbMerchantInfo.setChecked(true);
            btnPre.setVisibility(View.VISIBLE);
            personalName = realName.getText().toString();
//			layoutBaseInfo.setVisibility(View.GONE);
            rbBaseInfo.setBackgroundResource(R.drawable.round_whilte2);
            rbBaseInfo.setTextColor(getResources().getColor(R.color.main_nav_blue));
            layoutBaseInfo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutBaseInfo.setVisibility(View.GONE);
                }
            }, 480);
            layoutBaseInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.left_out));
            layoutMerchantInfo.setVisibility(View.VISIBLE);
            layoutMerchantInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.left_in));
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                    .Merchant_MerchantInfo_Add, context);
            getChina();
//			navigationBar.setBackBtnVisibility(View.GONE);
        } else if (stepId == R.id.rb_merchant_info) {
            rbBankAccountInfo.setChecked(true);
            layoutMerchantInfo.setVisibility(View.GONE);
            rbMerchantInfo.setBackgroundResource(R.drawable.round_whilte2);
            rbMerchantInfo.setTextColor(getResources().getColor(R.color.main_nav_blue));
            layoutMerchantInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.left_out));
            layoutBankAccountInfo.setVisibility(View.VISIBLE);
            layoutBankAccountInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.left_in));
            bankAccountName.setText(personalName);
            bankAccountName.setEnabled(false);
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                    .Merchant_BankInfo_Add, context);
        } else if (stepId == R.id.rb_bank_account_info) {
            //是否有F码
            String fCode = ((EditText) findViewById(R.id.edit_f_code)).getText().toString().trim();
            if (null != fCode && !"".equals(fCode) && checkFCodeCount < 3) {//有F码
                if (!Util.checkNumAnd(fCode)) {
                    ToastUtil.toast(context, R.string.f_code_is_error);
                    enableView(registerComfirm);
                    return;
                }
            } else {//无F码
                if (!checkBox.isChecked()) {
                    ToastUtil.toast(context, "请阅读并勾选收款宝业务合作协议");
                    return;
                }

                if (accountType == COMPANY_ACCOUNT) {
                    if (TextUtils.isEmpty(mOpenBankEdit.getText().toString().trim()) || mOpenBankInfo == null) {
                        toast("请选择开户银行");
                    } else {
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                                .Merchant_BankInfo_Add_Sub, context);
                        registerThread();
                    }
                } else {
                    //个人账户
                    checkCardBIN(true, registerInfo.getAccountNo());
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Merchant_BankInfo_Add_Sub, context);
                }
            }
        }
    }

    private void showPre() {
        int stepId = rgStep.getCheckedRadioButtonId();
        if (stepId == R.id.rb_merchant_info) {

            btnPre.setVisibility(View.GONE);
            rbMerchantInfo.setBackgroundResource(R.drawable.bg_radio_selector2);
            rbMerchantInfo.setTextColor(getResources().getColorStateList(R.color.text_blue_white_rg_selector));
            rbBaseInfo.setTextColor(getResources().getColor(R.color.text_blue_white_rg_selector));
            rbBaseInfo.setBackgroundResource(R.drawable.bg_radio_selector2);
            rbBaseInfo.setChecked(true);
            layoutMerchantInfo.setVisibility(View.GONE);
            layoutMerchantInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.right_out));
            layoutBaseInfo.setVisibility(View.VISIBLE);
            layoutBaseInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.right_in));
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                    .Merchant_Info_Add, context);
//			navigationBar.setBackBtnVisibility(View.VISIBLE);
        } else if (stepId == R.id.rb_bank_account_info) {
            rbBankAccountInfo.setBackgroundResource(R.drawable.bg_radio_selector2);
            rbMerchantInfo.setBackgroundResource(R.drawable.bg_radio_selector2);
            rbMerchantInfo.setTextColor(getResources().getColorStateList(R.color.text_blue_white_rg_selector));
            rbMerchantInfo.setChecked(true);
            layoutBankAccountInfo.setVisibility(View.GONE);
            layoutBankAccountInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.right_out));
            layoutMerchantInfo.setVisibility(View.VISIBLE);
            layoutMerchantInfo.startAnimation(AnimationUtils.loadAnimation(MerchantRegisterActivity.this, R.anim.right_in));
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                    .Merchant_MerchantInfo_Add, context);
        }
    }

    /**
     * 初始化 UI
     */
    protected void initUI() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //设置标题
        navigationBar.setTitle("商户开通");

        v_tag = findViewById(R.id.v_tag);
        shoudan_openbank = findViewById(R.id.shoudan_openbank);
        tvArea = (TextView) findViewById(R.id.tv_area_value);
        tv_result = (TextView) findViewById(R.id.tv_result);
        viewGroup = findViewById(R.id.RelativeLayout1);
        setOnDoneButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        //个人信息
        id = (EditText) findViewById(R.id.id);
        realName = (EditText) findViewById(R.id.name);
        MerchantInfo.UserEntity user = ApplicationEx.getInstance().getUser().getMerchantInfo()
                .getUser();
        MerchantInfo.UserEntity.IdCardInfoEntity idCardInfo = user.getIdCardInfo();
        if (user.isIdInfoValid()) {
            realName.setText(user.getRealName());
            realName.setEnabled(false);
            id.setText(idCardInfo.getIdCardId());
            id.setEnabled(false);
        }
        email = (EditText) findViewById(R.id.email);
        id.addTextChangedListener(new SpaceTextWatcher());
        realName.addTextChangedListener(new SpaceTextWatcher());
        email.addTextChangedListener(new SpaceTextWatcher());

        //身份证照片
        idPhotoFront = (ImageView) findViewById(R.id.shoudan_id_photo_front);
        idPhotoFront.setOnClickListener(this);
        idPhotoBack = (ImageView) findViewById(R.id.shoudan_id_photo_back);
        idPhotoBack.setOnClickListener(this);

        iv_location = (ImageView) findViewById(R.id.iv_location);
        findViewById(R.id.ll_location).setOnClickListener(this);

        //商户信息
        mrchName = (EditText) findViewById(R.id.mer_name);
        mrchName.addTextChangedListener(new SpaceTextWatcher());
        mrchAddress = (EditText) findViewById(R.id.mer_addr);
        mrchAddress.addTextChangedListener(new SpaceTextWatcher());
        //mOpenBankImg = (ImageView) findViewById(R.id.id_combination_text_image_text_image_leftImage);
        mOpenBankEdit = (TextView) findViewById(R.id.bank);
        mOpenBankEdit.setOnClickListener(this);
        tvOpenBankType = (TextView) findViewById(R.id.bank_type);
        tvOpenBankType.setOnClickListener(this);
//		RelativeLayout rightLayout = (RelativeLayout) findViewById(R.id.id_combination_right_layout);
//        rightLayout.setOnClickListener(this);

        findViewById(R.id.support_bank_list).setOnClickListener(this);
        findViewById(R.id.img_account_type).setOnClickListener(this);
        //开户行信息
        bankAccountNo = (EditText) findViewById(R.id.bank_account);
        cardChangeListener = new CardEditFocusChangeListener();
        //开户业务
        cardChangeListener.setBankBusid(BankBusid.MPOS_ACCT);


        cardChangeListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback() {
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {

                if (!accountType.equals(COMPANY_ACCOUNT)) {
                    mOpenBankInfo = openBankInfo;
                    if (mOpenBankInfo == null) {
//                        ToastUtil.toast(context, errMsg);
                        tv_result.setTextColor(getResources().getColor(R.color.yelo));
                        tv_result.setText(errMsg);
                    } else {
                        updateBankInfo();
                    }
                }
            }
        });

        bankAccountNo.setOnFocusChangeListener(null);
//        bankAccountNo.addTextChangedListener(new TextWatcher() {
//
//            String before;
//            int cursorIndex = bankAccountNo.getSelectionEnd();
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                before = charSequence.toString();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String cardNum = com.lakala.library.util.StringUtil.trim(editable.toString());
//                if (TextUtils.isEmpty(cardNum)) {
//                    mOpenBankEdit.setText("");
//                    tv_result.setText("");
//                    return;
//                }
//
//                StringBuffer str = new StringBuffer(editable.toString());
//                if (str.toString().contains(" ")) {
//                    if (str.length() > 1) {
//                        bankAccountNo.setText(str.toString().replace(" ", ""));
//                        bankAccountNo.setSelection(cursorIndex);
//                    }
//                }
//                Pattern pattern = Pattern.compile("[\\d]");
//                Matcher matcher = pattern.matcher(str);
//                if (!"".equals(matcher.replaceAll("").trim())) {
//                    if (matcher.replaceAll("").trim().length() > 1) {
//                        bankAccountNo.setText(before);
//                        bankAccountNo.setSelection(cursorIndex);
//
//                    }
//                }
//
//                String cardNo = StringUtil.removeTrim(bankAccountNo.getText().toString());
//
//                if (COMPANY_ACCOUNT.equals(accountType)) {
//                    //对公账户不做卡bin查询 , 需求from 吴春然
//                    return;
//                }
//                if (cardNo.length() > 19) {
//                    ToastUtil.toast(context, R.string.re_input_cardNo);
//                }
//            }
//        });


        bankAccountName = (EditText) findViewById(R.id.account_name);

        bankAccountNo.setTag(TAG_CARD_NUMBER);


        //收款账户类型
        accountType = INDIVIDUAL_ACCOUNT;
        companyAccount = (RadioButton) findViewById(R.id.radio_enterprise_account);
        individualAccount = (RadioButton) findViewById(R.id.radio_private_account);
        companyAccount.setOnClickListener(this);
        individualAccount.setOnClickListener(this);

        //地区选择
//        findViewById(R.id.tv_area_value).setOnClickListener(this);
        findViewById(R.id.img_area_more).setOnClickListener(this);
        //步骤提示
        rgStep = (RadioGroup) findViewById(R.id.rg_step);
        rbBaseInfo = (RadioButton) findViewById(R.id.rb_base_info);
        rbMerchantInfo = (RadioButton) findViewById(R.id.rb_merchant_info);
        rbBankAccountInfo = (RadioButton) findViewById(R.id.rb_bank_account_info);

        btnPre = (TextView) findViewById(R.id.btn_pre_bank);
        btnPre.setOnClickListener(this);
        findViewById(R.id.btn_pre_merchant).setOnClickListener(this);

        findViewById(R.id.btn_next_merchant).setOnClickListener(this);
        //提交注册
        registerComfirm = (Button) findViewById(R.id.btn_next_bank);
        registerComfirm.setOnClickListener(this);
        findViewById(R.id.btn_next_baseinfo).setOnClickListener(this);

        layoutBaseInfo = findViewById(R.id.layout_base_info);
        layoutMerchantInfo = findViewById(R.id.layout_merchant_info);
        layoutBankAccountInfo = findViewById(R.id.layout_bank_account_info);

        TextView tvFCodeDescription = (TextView) findViewById(R.id.tv_description_f_code);
        tvFCodeDescription.getPaint().setUnderlineText(true);
        tvFCodeDescription.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        findViewById(R.id.tv_btn_to_write_f).setOnClickListener(this);

        findViewById(R.id.layout_edit_f_code).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        EditText editFCode = (EditText) findViewById(R.id.edit_f_code);
                        if (editFCode.isEnabled()) {
                            editFCode.setFocusable(true);
                            editFCode.requestFocus();
                            // 接受软键盘输入的编辑文本或其它视图
                            imm.showSoftInput(editFCode, InputMethodManager.SHOW_FORCED);
                        }
                    }
                }
        );

        findViewById(R.id.lkl_cooperation_agreement).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MERCHANT_OPEN_RULE, context);
                ProtocalActivity.open(context, ProtocalType.COOPERATION_AGREEMENT);
            }
        });

        checkBox = (CheckBox) findViewById(R.id.agreement);

        initNumberKeyboard();
        /**
         * @
         */
        initNumberEdit(bankAccountNo, CustomNumberKeyboard.EDIT_TYPE_CARD, 30);

        setCheckIndividual();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        Object tag = v.getTag();
        if (TextUtils.equals(String.valueOf(tag), TAG_CARD_NUMBER)) {
            cardChangeListener.onFocusChange(v, hasFocus);
        }
        LogUtil.print("<v>", hasFocus + "");
        if (hasFocus) {
            String newText = bankAccountNo.getText().toString().replace(" ", "");
            bankAccountNo.setText(newText);
            bankAccountNo.setSelection(newText.length());
            LogUtil.print("<v>", newText);
        } else {
            String text = CardUtil.formatCardNumberWithSpace(bankAccountNo.getText().toString());
            bankAccountNo.setText(text);
            LogUtil.print("<v>", text);
        }
    }

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.id_input_scroll));
    }

    /**
     * 卡号与开户银行根据卡bin进行验证
     * <p/>
     * boolean checkOrRequest   同时标识是否显示进度条,以及检测当前银行卡匹配情况而后注册
     *
     * @param cardNo 卡号
     */
    private void checkCardBIN(final boolean checkOrRequest, final String cardNo) {

        if (!checkOrRequest && (TextUtils.isEmpty(cardNo) || cardNo.length() != 16 && cardNo.length() != 19)) {

            return;
        }

        if (checkOrRequest) {
            showProgressWithNoMsg();
        }
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject == null) {
//                        if(accountType != INDIVIDUAL_ACCOUNT){
//                            ToastUtil.toast(context, R.string.not_support_bank);
//                        }else {
                        tv_result.setTextColor(getResources().getColor(R.color.yelo));
                        tv_result.setText(R.string.not_support_bank);
//                        }
                    } else {
                        mOpenBankInfo = OpenBankInfo.construct(jsonObject);

                        if (checkOrRequest) {
                            if (mOpenBankInfo.bankname != null && mOpenBankInfo.bankname.equals(tv_result.getText().toString().trim())) {
                                registerThread();
                            } else {
//                                if(accountType != INDIVIDUAL_ACCOUNT){
//                                    ToastUtil.toast(context, R.string.open_bank_not_same);
//                                }else {
                                tv_result.setTextColor(getResources().getColor(R.color.yelo));
                                tv_result.setText(R.string.open_bank_not_same);
//                                }
                            }
                        } else {
                            updateBankInfo();
                        }

                    }
                } else {

                    if (checkOrRequest) {
//                        if(accountType != INDIVIDUAL_ACCOUNT){
//                            ToastUtil.toast(context, resultServices.retMsg);
//                        }else {
                        tv_result.setTextColor(getResources().getColor(R.color.yelo));
                        tv_result.setText(resultServices.retMsg);
//                        }
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgressDialog();
            }
        };
        CommonServiceManager.getInstance().getCardBIN(BankBusid.MPOS_ACCT, cardNo, callback);
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

    private int checkFCodeCount = 0;


    private void registerThread() {
        showProgressDialog(R.string.committing_register_info);
        if (isCardNoLegal(registerInfo.getAccountNo(), registerInfo.getBankNo())) {//匹配卡号

            if (ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().isIdInfoValid()) {//审核中不需要再提交通户信息
                startResgister();
            } else {
                postPicData(new PostPicCallback1() {
                    @Override
                    public void onCallback(boolean isPostSuccess, String msg) {
                        if (isPostSuccess) {
                            startResgister();
                        } else {
                            hideProgressDialog();
                            ToastUtil.toast(context, msg);
                        }
                    }
                });
            }
        }
        enableView(registerComfirm);
    }


    /**
     * 提交注册
     */
    private void startResgister() {

        ShoudanService.getInstance().merchantManage(registerInfo, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {
                    ShoudanStatisticManager.getInstance()
                            .onEvent(ShoudanStatisticManager.REGISTER_SUCCESS, context);
                    try {
                        ApplicationEx.getInstance().getUser().setMerchantInfo(new MerchantInfo((resultServices.retData)));
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.MERCHANT_OPEN_ApplySuceess, context);
                        forwardActivity(RegisterSuccessActivity.class);
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

    private void registerSuccess() {
        BaseDialog dialog = DialogCreator.createConfirmDialog(context, "确定", "商户开通申请成功！\n请等待审核结果");
        dialog.show();
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

    /**
     * 获取开户行信息，更新UI
     */
    private void updateBankInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!COMPANY_ACCOUNT.equals(accountType)) {
                    tv_result.setTextColor(getResources().getColor(R.color.font_gray_three2));
                    tv_result.setText(mOpenBankInfo.bankname);
                } else {
                    mOpenBankEdit.setText(mOpenBankInfo.bankname);
                }
                //ImageUtil.loadBankLogo(this, mOpenBankImg, mOpenBankInfo.bankCode, mOpenBankInfo.bankimg);
                registerInfo.setBankName(mOpenBankInfo.bankname);
                registerInfo.setBankNo(mOpenBankInfo.bankCode);
            }
        });
    }


    /**
     * 判断输入的卡号和开户行是否一致
     * 0000：成功 1025：获取数据失败
     * 1041：不支持当前银行
     * 1042：卡号长度错误
     * 1043：该业务不支持信用卡
     * 1044：卡号和银行卡不匹配
     */
    private boolean isCardNoLegal(String cardNo, String bankCode) {
        return true;
    }

    /**
     * 提交照片文件
     * <p/>
     * 返回提交照片是否成功 boolean
     */
    private void postPicData(final PostPicCallback1 postPicCallback) {

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
                            postPicCallback.onCallback(true, "");
                        } else {
                            postPicCallback.onCallback(false, resultServices.retMsg);
                        }

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        postPicCallback.onCallback(false, getString(R.string.toast_socket_error));
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

    private Map<Integer, String> photoMap = new HashMap<>();

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

}
