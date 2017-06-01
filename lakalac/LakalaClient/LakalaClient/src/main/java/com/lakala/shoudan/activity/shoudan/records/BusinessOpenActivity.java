package com.lakala.shoudan.activity.shoudan.records;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.AreaEntity;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务开通
 * 
 */
public class BusinessOpenActivity extends AppBaseActivity implements OnClickListener {
	private View openBusinessBtn;
	private TextView agreeProtocal;
	private CheckBox agree;
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                ProtocalActivity.open(context, ProtocalType.D0_DESCRIPTION);
                PublicToEvent.MerchantlEvent(ShoudanStatisticManager.Merchant_jsType_Rule, context);
            }
        }
    };
    private EditText idCardNo;
    private TextView tvProvice;
    private TextView tvCity;
    private TextView tvBank;
    private List<AreaEntity> provices = new ArrayList<AreaEntity>();
    private Map<String,List<AreaEntity>> cities = new HashMap<String, List<AreaEntity>>();
    private Map<String,List<AreaEntity>> banks = new HashMap<String, List<AreaEntity>>();

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_open_business);
		initUI();
	}
	protected void initUI() {
		openBusinessBtn=findViewById(R.id.open_business);
		navigationBar.setTitle("提前划款开通申请");
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
	    navigationBar.setActionBtnText("业务说明");
        initMerchantInfo();
	    agree=(CheckBox) findViewById(R.id.check_agree);
	    agreeProtocal=(TextView) findViewById(R.id.scan_agreement);
	    agreeProtocal.setOnClickListener(this);
	    openBusinessBtn.setOnClickListener(this);
        tvProvice = (TextView)findViewById(R.id.provice);
        tvProvice.setOnClickListener(this);
        tvCity = (TextView)findViewById(R.id.city);
        tvCity.setOnClickListener(this);
        tvBank = (TextView)findViewById(R.id.bank_list);
        tvBank.setOnClickListener(this);
	}
    private void initMerchantInfo(){
        TextView merchantName = (TextView)findViewById(R.id.merchantName);

        MerchantInfo info = ApplicationEx.getInstance().getUser().getMerchantInfo();

        MerchantInfo.BusinessAddressEntity addressInfo = info.getBusinessAddress();
        if(addressInfo != null){
            TextView detailAddress = (TextView)findViewById(R.id.detailAddress);
            detailAddress.setText(addressInfo.getFullDisplayAddress());

        }

        merchantName.setText(info.getBusinessName());

        TextView collectionBank = (TextView) findViewById(R.id.collectionBank);
        collectionBank.setText(String.valueOf(info.getBankName()));

        TextView collectCardNo = (TextView) findViewById(R.id.collectCardNo);
        collectCardNo.setText(String.valueOf(info.getAccountNo()));

        MerchantInfo.UserEntity userInfo = info.getUser();

        TextView realName = (TextView) findViewById(R.id.realName);
        realName.setText(userInfo.getRealName());

        MerchantInfo.UserEntity.IdCardInfoEntity idcardInfo = userInfo.getIdCardInfo();
        String idCard = idcardInfo.getIdCardId();
        if (!TextUtils.isEmpty(idCard)) {
            idCard = idCard.toUpperCase();
            idCardNo = (EditText) findViewById(R.id.idCardNo);
            setIdCardNoEditEnable(false);
            idCardNo.setText(idCard);
        }

    }
    private void setIdCardNoEditEnable(boolean isEnable){
        if(isEnable){
            idCardNo.setText(idCardNo.getText().toString(), TextView.BufferType.EDITABLE);
        }else{
            idCardNo.setText(idCardNo.getText().toString(), TextView.BufferType.NORMAL);
        }
        idCardNo.setEnabled(isEnable);
        idCardNo.setFocusableInTouchMode(isEnable);
        idCardNo.setFocusable(isEnable);
    }

    /**
     * T0开户方法
     * @param idcardNo
     */
    private void doOpenMethod(final String idcardNo){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                String msg;
                boolean isUserVerified = true;
                if(result.isRetCodeSuccess()){
                    msg = "您已成功申请“立即提款”业务，拉卡拉需要大约2小时来处理您的申请，请您耐心等待！";
                }else if("005019".equals(result.retCode)){//实名认证未通过
                    msg = "您的商户实名验证未通过，请填写正确的身份证号码后重新提交！";
                    isUserVerified = false;
                }else{
                    msg = result.retMsg;
                }
                hideProgressDialog();
                final boolean isVerified = isUserVerified;
                DialogCreator.createOneConfirmButtonDialog(
                        context, "确定", msg, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                if (!isVerified) {
                                    PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_jsType_Open, context);
                                    idCardNo.setText("");
                                    setIdCardNoEditEnable(true);
                                } else {
                                    finish();
                                }
                            }
                        }).show();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().toOpenD0(idcardNo, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_business:
                if (agree.isChecked()) {
//                    根据需求取消了开户支行的选择
//                    String idNo = idCardNo.getText().toString();
//                    AreaEntity bank = (AreaEntity) tvBank.getTag();
//                    if(bank == null){
//                        Util.toast("请选择银行");
//                    }else{
//                        doOpenMethod(bank.getCode,idNo);
//                    }
                    String idNo = idCardNo.getText().toString();

                    if(idNo.length() == 0 ||!Util.checkIdCard(idNo)){
                        ToastUtil.toast(context,R.string.id_no_input_error);
                        return;
                    }
                    doOpenMethod(idNo);
                } else {
                    ToastUtil.toast(context,"请阅读并同意划款服务协议");
                }
                break;
            case R.id.scan_agreement:
                ProtocalActivity.open(context,ProtocalType.D0_SERVICE);
                PublicToEvent.MerchantlEvent( ShoudanStatisticManager.Merchant_jsType_Rule_Agreee, context);
                break;
            case R.id.right_arrow1:
            case R.id.provice: {//省列表
                if(provices == null || provices.size() == 0){
                    getProviceList();
                }else{
                    showListDialog(provices,proviceListener);
                }
                break;
            }
            case R.id.right_arrow2:
            case R.id.city:{//市列表
                AreaEntity provice = (AreaEntity) tvProvice.getTag();
                if(provice == null){
                    ToastUtil.toast(context,"请选择省份");
                    return;
                }
                String code = provice.getCode();
                List<AreaEntity> codeCities = cities.get(code);
                if(codeCities == null || codeCities.size() == 0){
                    getCityList(code);
                }else{
                    showListDialog(codeCities,cityListener);
                }
                break;
            }
            case R.id.right_arrow3:
            case R.id.bank_list:{//银行列表
                AreaEntity city = (AreaEntity) tvCity.getTag();
                if(city == null){
                    ToastUtil.toast(context,"请选择城市");
                    return;
                }
                String code = city.getCode();
                List<AreaEntity> codeBanks = banks.get(code);
                if(codeBanks == null || codeBanks.size() == 0){
                    getBankList(code);
                }else{
                    showListDialog(codeBanks,bankListener);
                }
                break;
            }
            default:
                break;
        }
    }
    private void getBankList(final String areaCode){
        showProgressWithNoMsg();
        final String bankCode = ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if(jsonArray != null){
                        length = jsonArray.length();
                    }
                    final List<AreaEntity> codeBanks = new ArrayList<AreaEntity>();
                    banks.put(areaCode,codeBanks);
                    AreaEntity area = null;
                    for(int i = 0;i<length;i++){
                        area = new AreaEntity();
                        codeBanks.add(area);
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if(name.contains("\"")){
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(codeBanks, bankListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getBankList(bankCode, areaCode, callback);
    }
    private void getCityList(final String code){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if(jsonArray != null){
                        length = jsonArray.length();
                    }
                    final List<AreaEntity> codeCities = new ArrayList<AreaEntity>();
                    cities.put(code,codeCities);
                    AreaEntity area = null;
                    for(int i = 0;i<length;i++){
                        area = new AreaEntity();
                        codeCities.add(area);

                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if(name.contains("\"")){
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(codeCities,cityListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAreaList(code, callback);
    }

    private void getProviceList(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = 0;
                    if(jsonArray != null){
                        length = jsonArray.length();
                    }
                    AreaEntity area = null;
                    for(int i = 0;i<length;i++){
                        area = new AreaEntity();
                        provices.add(area);

                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String name = jsonObject.optString("name");
                        if(name.contains("\"")){
                            name = name.split("\"")[0];
                        }
                        area.setName(name);
                        area.setCode(jsonObject.optString("code"));
                    }
                    showListDialog(provices,proviceListener);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager.getInstance().getAreaList(null, callback);
    }
    private DialogInterface.OnClickListener proviceListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity provice = provices.get(which);
            AreaEntity oldProvice = (AreaEntity)tvProvice.getTag();
            if(oldProvice == null || !oldProvice.equals(provice)){
                tvProvice.setText(provice.getName());
                tvProvice.setTag(provice);
                tvCity.setText("");
                tvCity.setTag(null);
                tvBank.setText("");
                tvBank.setTag(null);
            }
        }
    };
    private DialogInterface.OnClickListener cityListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity provice = (AreaEntity) tvProvice.getTag();
            List<AreaEntity> codeCities = cities.get(provice.getCode());
            AreaEntity city = codeCities.get(which);
            AreaEntity oldCity = (AreaEntity)tvCity.getTag();
            if(oldCity == null || !city.equals(oldCity)){//未选中城市或者修改了所在城市
                //设置所在城市
                tvCity.setText(city.getName());
                tvCity.setTag(city);
                //初始化银行名称
                tvBank.setText("");
                tvBank.setTag(null);
            }
        }
    };
    private DialogInterface.OnClickListener bankListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AreaEntity city = (AreaEntity)tvCity.getTag();
            List<AreaEntity> codeBanks = banks.get(city.getCode());
            AreaEntity bank = codeBanks.get(which);
            tvBank.setText(bank.getName());
            tvBank.setTag(bank);
        }
    };
    private void showListDialog(List<AreaEntity> infos, DialogInterface.OnClickListener listener){
        int size = infos.size();
        if(size == 0){
            ToastUtil.toast(context,"没有获取到信息");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[size];
        for(int i = 0;i<size;i++){
            items[i] = infos.get(i).getName();
        }
        builder.setItems(items,listener);
        builder.show();
    }
}
