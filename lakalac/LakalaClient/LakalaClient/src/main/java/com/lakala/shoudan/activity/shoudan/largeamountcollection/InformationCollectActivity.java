package com.lakala.shoudan.activity.shoudan.largeamountcollection;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lakala.platform.common.UILUtils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.base.CallCameraActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.PhoneNumberInputWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/6/11.
 * <p>
 * 大额收款 信息采集
 */
public class InformationCollectActivity extends CallCameraActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_amount_info_collect);
        initUI();

    }

    private EditText phoneNum;

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("大额收款");
        navigationBar.setActionBtnText("业务说明");

        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                    case title:
                        break;
                    case action:
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.LargeAmount_Rule, context);
                        ProtocalActivity.open(context, ProtocalType.LARGE_AMOUNT_RULES);
                        break;
                }
            }
        });

        phoneNum = (EditText) findViewById(R.id.phone_num);

        int[] clickIds = new int[]{
                R.id.confirm,
                R.id.bank_card_photo_front,
                R.id.personal_photo,
                R.id.merchant_cer_photo,
                R.id.merchant_cer_sample

        };
        for (int id : clickIds) {
            findViewById(id).setOnClickListener(onClickListener);
        }
        phoneNum.addTextChangedListener(new PhoneNumberInputWatcher());

    }


    private Map<Integer, String> photoFileMap = new HashMap<Integer, String>();
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.confirm:
                    if (checkValue()) {

                        largeAmountInfoUpload();

                    }
                    break;
                case R.id.merchant_cer_sample:
                    ProtocalActivity.open(context, ProtocalType.SALES_CER_CAPTURE);
                    break;
                default:
                    requestCamera(v.getId());
                    break;


            }

        }
    };

    private void requestCamera(int id) {

        startActionCapture(id);
    }

    private boolean checkValue() {


        ImageView bankPhoto = (ImageView) findViewById(R.id.bank_card_photo_front);
        ImageView personalPhoto = (ImageView) findViewById(R.id.personal_photo);
        ImageView merchantCerPhoto = (ImageView) findViewById(R.id.merchant_cer_photo);

        if (!checkTag(bankPhoto)) {
            toast("您还未提交银行卡照片");
            return false;
        }


        if (!checkTag(personalPhoto)) {
            toast("您还未提交本人持卡照");
            return false;
        }


        if (!checkTag(merchantCerPhoto)) {
            toast("您还未提交销售凭证照");
            return false;
        }

        phoneString = phoneNum.getText().toString();
        //由于手机号格式化后，中间加‘-’字符，需要去除后再做比较
        phoneString = Util.formatString(phoneString).trim();

        if (TextUtils.isEmpty(phoneString)) {
            toast("请输入持卡人手机号");
            return false;
        }

        if (!Util.checkPhoneNumber(Util.formatString(phoneString))) {
            toast(getString(R.string.toast_phone_length_error));
            return false;
        }


        return true;

    }

    private boolean checkTag(View v) {

        return !TextUtils.isEmpty(photoFileMap.get(v.getId()));

    }

    private String largeAmountUploadSid;
    private String phoneString;

    private void largeAmountInfoUpload() {

        showProgressDialog(R.string.uploading_pics);

        ShoudanService.getInstance().
                uploadLargeAmountTradeInfoImages(photoFileMap.get(R.id.bank_card_photo_front),
                        photoFileMap.get(R.id.personal_photo),
                        photoFileMap.get(R.id.merchant_cer_photo), new ServiceResultCallback() {
                            @Override
                            public void onSuccess(ResultServices imagesUploadResult) {
                                hideProgressDialog();
                                if (imagesUploadResult.isRetCodeSuccess()) {

                                    try {
                                        largeAmountUploadSid = new JSONObject((imagesUploadResult.retData)).optString("sid");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    infoUpload();

                                } else {
                                    showMessage(imagesUploadResult.retMsg);
                                }
                            }

                            @Override
                            public void onEvent(HttpConnectEvent connectEvent) {
                                hideProgressDialog();
                            }
                        });

    }

    private void infoUpload() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LargeAmountTransInfo) getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO)).setInfoCollectSid(largeAmountUploadSid);
                ((LargeAmountTransInfo) getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO)).setMobileNo(phoneString);

                startActivity(getIntent().setClass(InformationCollectActivity.this, LargeAmountCollectionPaymentActivity.class));

            }
        });


    }

    @Override
    protected void onCameraRequestFailed() {

    }

    @Override
    protected void onCameraRequestSucceed(int clickId, String picFilePath) {
        photoFileMap.put(clickId, picFilePath);
        UILUtils.displayWithoutCache(picFilePath, ((ImageView) findViewById(clickId)));
    }
}
