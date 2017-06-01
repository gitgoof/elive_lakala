package com.lakala.shoudan.activity.quickArrive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.nativeplugin.CameraPlugin;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.communityservice.transferremittance.CommonBankListActivity;
import com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase.BitmapManager;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.WalletTransferActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 添加信用卡
 * Created by WangCheng on 2016/8/22.
 */
public class AddBankCardActivity extends AppBaseActivity implements View.OnClickListener{

    private TextView name,id_card_no,card_no,card_name;
    private EditText phone;
    private ImageView card_iv;
    private CameraPlugin cameraPlugin;
    private CommonServiceManager serviceManager;
    private JSONObject json=null;
    private String iv_url="";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("添加银行卡");
        findViewById(R.id.tv_check_card).setOnClickListener(this);
        findViewById(R.id.ll_get_card_no).setOnClickListener(this);
        findViewById(R.id.ll_get_card_im).setOnClickListener(this);
        findViewById(R.id.tv_agreement).setOnClickListener(this);
        findViewById(R.id.bt_ok).setOnClickListener(this);
        name= (TextView) findViewById(R.id.tv_name);
        id_card_no= (TextView) findViewById(R.id.tv_id_card_no);
        card_no= (TextView) findViewById(R.id.tv_card_no);
        card_name= (TextView) findViewById(R.id.tv_card_name);
        phone= (EditText) findViewById(R.id.et_phone);
        card_iv= (ImageView) findViewById(R.id.iv_card_im);
        checkBox= (CheckBox) findViewById(R.id.check_agree);
    }

    public void initMyData(){
        cameraPlugin=new CameraPlugin();
        cameraPlugin.register(context,listener);
        serviceManager = CommonServiceManager.getInstance();
        name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        String idNo=ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId();
        id_card_no.setText(idNo.substring(0,1)+"****************"+idNo.substring(17,18));

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_check_card://查看支持银行
                Intent intent = CommonBankListActivity.getIntent(context,"", BankBusid.WALLET_TRANSFER.getValue());
                intent.putExtra(Constants.IntentKey.WALLET_TRANSFER_BANK,true);
                startActivityForResult(intent, WalletTransferActivity.GET_BANK);
                break;
            case R.id.ll_get_card_no://银行卡号
                startActivityForResult(new Intent(this,GetBankNo2Activity.class)
                        .putExtra(Constants.IntentKey.TRANS_INFO, new RevocationTransinfo2())
                        ,1);
                break;
            case R.id.ll_get_card_im://银行卡正面照
                cameraPlugin.showActionChoose();
                break;
            case R.id.tv_agreement://快捷支付服务协议
                startActivity(new Intent(this,QuickPaymentActivity.class));
                break;
            case R.id.bt_ok://确定
                if(isOk()){
                    bindBank();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.print("<S>","onActivityResult:requestCode="+requestCode+"resultCode："+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode== Activity.RESULT_OK){
                String datas=data.getStringExtra("data");
                try {
                    json=new JSONObject(datas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(new Intent(this,InCardNoActivity.class)
                                .putExtra("data",datas)
                        ,2);
            }
        }else if(requestCode==2){
            if(resultCode== Activity.RESULT_OK){
                boolean isReget=data.getBooleanExtra("isReget",false);
                if(isReget){
                    startActivityForResult(new Intent(this,GetBankNo2Activity.class)
                                    .putExtra(Constants.IntentKey.TRANS_INFO, new RevocationTransinfo2())
                            ,1);
                }else {
                    card_no.setText(json.optString("cardno"));
                    card_name.setText(json.optString("bankName"));
                }
            }
        }
    }

    public void bindBank(){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(this,"v1.0/business/speedArrivalD0/bindCard/manager", HttpRequest.RequestMethod.POST,true);
        HttpRequestParams params=businessRequest.getRequestParams();
        params.put("cardNo",json.optString("cardno"));
        params.put("cardHolderName",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        params.put("bankCode",json.optString("bankCode"));
        params.put("bankName",json.optString("bankName"));
        params.put("idNo",ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        params.put("bankMobile",phone.getText().toString().trim());
        params.put("bankCardImageUrl",iv_url);
        params.put("optType","BIND");
        params.put("token",json.optString("token"));
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    ToastUtil.toast(AddBankCardActivity.this,"绑定成功");
                    finish();
                }else {
                    ToastUtil.toast(AddBankCardActivity.this,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(AddBankCardActivity.this,AddBankCardActivity.this.getString(R.string.socket_fail));
            }
        }));
        businessRequest.execute();
    }

    private void fileUploadTask(final String picFilePath) {
        showProgressDialog("正在上传文件");
        LogUtil.print("<S>","图片上传中");
        serviceManager.merLevelFileUpload(picFilePath, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    String dataStr = resultServices.retData;
                    try {
                        JSONObject data = new JSONObject(dataStr);
                        iv_url = data.optString("fileName");
                        LogUtil.print("<S>","上传成功："+iv_url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(getString(R.string.socket_error));
            }
        });
    }


    private CameraPlugin.CameraListener listener=new CameraPlugin.CameraListener() {
        @Override
        public void onSuccess(String filePath) {
            LogUtil.print("<S>","onSuccess"+filePath);
            fileUploadTask(filePath);
            UILUtils.displayWithoutCache(filePath,card_iv);
        }

        @Override
        public void onFailed() {
            LogUtil.print("<S>","onFailed");
        }
    };

    public boolean isOk(){
        boolean isOk=false;
        if(json==null){
            ToastUtil.toast(AddBankCardActivity.this,"请刷卡获取卡号");
        }else if (TextUtils.isEmpty(iv_url)){
            ToastUtil.toast(AddBankCardActivity.this,"请上传银行卡正面照");
        }else if(TextUtils.isEmpty(phone.getText().toString().trim())){
            ToastUtil.toast(AddBankCardActivity.this,"请输入银行预留手机号码");
        }else if(!PhoneNumberUtil.checkPhoneNumber(phone.getText().toString().trim())){
            ToastUtil.toast(AddBankCardActivity.this,"请输入正确的11位手机号码");
        }else if(!checkBox.isChecked()){
            ToastUtil.toast(AddBankCardActivity.this,"请阅读《快捷支付服务协议》");
        }else {
            isOk=true;
        }
        return isOk;
    }
}
