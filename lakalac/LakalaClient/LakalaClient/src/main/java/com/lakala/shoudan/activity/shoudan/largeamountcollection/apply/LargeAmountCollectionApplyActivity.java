package com.lakala.shoudan.activity.shoudan.largeamountcollection.apply;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.LargeAmountAccess;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.base.CallCameraActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/6/11.
 *
 * 大额收款申请
 */
public class LargeAmountCollectionApplyActivity extends CallCameraActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_amount_collection_apply);
        initUI();
    }


    private EditText merchantName,idCardNum, realName;
    private TextView merchantIndustry;
    private ImageView idPhotoFront,idPhotoBack,merchantCer,merchantCerSample;
    private Button btnApply;
    private CheckBox agreement;
    private TextView tvIndustry;

    @Override
    protected void initUI() {
        super.initUI();
        User user = ApplicationEx.getInstance().getUser();
        navigationBar.setTitle("大额收款申请");
        merchantName = (EditText)findViewById(R.id.merchant_name);
        merchantName.setText(user.getMerchantInfo().getBusinessName());
        merchantName.setFocusable(false);
        merchantIndustry = (TextView)findViewById(R.id.industry);
        realName = (EditText)findViewById(R.id.real_name);
        realName.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
        realName.setFocusable(false);
        idCardNum = (EditText)findViewById(R.id.id_card_num);
        idCardNum.setText(user.getMerchantInfo().getUser().getIdCardInfo().getIdCardId());
        idPhotoFront = (ImageView)findViewById(R.id.id_photo_front);
        idPhotoBack = (ImageView)findViewById(R.id.id_photo_back);
        merchantCer = (ImageView)findViewById(R.id.merchant_cer_photo);
        merchantCerSample = (ImageView)findViewById(R.id.merchant_cer_sample);
        agreement = (CheckBox)findViewById(R.id.agreement);
        agreement.setOnCheckedChangeListener(agreementCheckListener);
        btnApply = (Button)findViewById(R.id.btn_apply);
        tvIndustry = (TextView)findViewById(R.id.industry);
        btnApply.setOnClickListener(onClickListener);
        idPhotoBack.setOnClickListener(onClickListener);
        idPhotoFront.setOnClickListener(onClickListener);
        merchantCer.setOnClickListener(onClickListener);
        merchantCerSample.setOnClickListener(onClickListener);
        findViewById(R.id.industry).setOnClickListener(onClickListener);
        findViewById(R.id.large_amount_collection_protocol).setOnClickListener(onClickListener);

        requestCodeMap.put(R.id.id_photo_front, 1);
        requestCodeMap.put(R.id.id_photo_back, 2);
        requestCodeMap.put(R.id.merchant_cer_photo, 3);
        agreementCheckListener.onCheckedChanged(null, true);
        //根据实名认证状态加载图片,身份号等

        idCardNum.setFocusableInTouchMode(false);
        idPhotoBack.setFocusable(false);
        idPhotoBack.setClickable(false);
        idPhotoFront.setFocusable(false);
        idPhotoFront.setClickable(false);

        updateIdCardPhotoVisibility(shouldUpload);


   }

    private Map<Integer, String> photoFileMap = new HashMap<Integer, String>();
    private Map<Integer, Integer> requestCodeMap = new HashMap<Integer, Integer>();

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btn_apply:
                    btnApply();
                    break;
                case R.id.merchant_cer_sample:
                    ProtocalActivity.open(context, ProtocalType.MERCHANT_CER_CAPTRUE);
                    break;
                case R.id.industry:
                    showIndustrySelection();
                    break;
                case R.id.large_amount_collection_protocol:
                    ProtocalActivity.open(LargeAmountCollectionApplyActivity.this, ProtocalType.LARGE_AMOUNT_COOPERATION_AGREEMENT);
                    break;
                case R.id.id_photo_front:
                case R.id.id_photo_back:
                case R.id.merchant_cer_photo:
                    showCallCameraPopWindow(v.getId());
                    break;
                default:
                    break;
            }

        }
    };

    private CompoundButton.OnCheckedChangeListener agreementCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            btnApply.setEnabled(isChecked);

        }
    };

    private String[] ids;
    private String[] names;


    private void showIndustrySelection(){

        if (names != null && names.length > 0) {
            showSelectionDialog();
            return;
        }


        showProgressWithNoMsg();
        ShoudanService.getInstance().getCommonDict("INDUSTRY", new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultForService) {
                if (resultForService.isRetCodeSuccess()) {

                    try {

                        JSONObject jsonObject = null;
                        jsonObject = new JSONObject(resultForService.retData);

                        JSONArray jsonArray = jsonObject.optJSONArray("INDUSTRY");

                        ids = new String[jsonArray.length()];
                        names = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = null;
                            temp = jsonArray.getJSONObject(i);

                            ids[i] = temp.optString("id", "");
                            names[i] = temp.optString("title", "");
                        }
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }

                    showSelectionDialog();

                } else {
                    toast(resultForService.retMsg);

                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toast(R.string.socket_fail);
                hideProgressDialog();
            }
        });

    }

    private boolean checkValue(){

        if(TextUtils.isEmpty(industry)){

            toast("请选择商户所属行业");

            return false;
        }

        idCardNumStr = idCardNum.getText().toString();

        if(shouldUpload){

            if(TextUtils.isEmpty(idCardNumStr)){
                toast("请输入身份证号");
                return false;
            }

            if(!Util.checkIdCard(idCardNumStr)){
                toast("您输入的身份证号不正确");
                return false;
            }

            if(!has(R.id.id_photo_front)){
                toast("您还未提交身份证正面照片");
                return false;
            }

            if(!has(R.id.id_photo_back)){
                toast("您还未提交身份证反面照片");
                return false;
            }
        }

        if(!has(R.id.merchant_cer_photo)){
            toast("您还未提交纸质开户单照片");
            return false;
        }

        return true;
    }

    private boolean has(int id){

        return !TextUtils.isEmpty(photoFileMap.get(id));
    }

    private String imagesUploadSid;
    private String industry;
    private String idCardNumStr;

    private void btnApply(){

        if(checkValue()){



            showProgressWithNoMsg();

            //Upload Images
            ShoudanService.getInstance().uploadLargeAmountApplyImages(

                    (shouldUpload ? photoFileMap.get(R.id.id_photo_front) : ""),
                    (shouldUpload) ? (photoFileMap.get(R.id.id_photo_back)) : "",
                    photoFileMap.get(R.id.merchant_cer_photo), new ServiceResultCallback() {
                        @Override
                        public void onSuccess(ResultServices resultServices) {
                            if (resultServices.isRetCodeSuccess()) {

                                try {
                                    imagesUploadSid = (new JSONObject(resultServices.retData)).optString("sid");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //apply
                                largeLitmitApply();

                            } else {

                                showMessage(resultServices.retMsg);
                                hideProgressDialog();
                            }
                        }

                        @Override
                        public void onEvent(HttpConnectEvent connectEvent) {
                            showMessage("网络连接异常");
                            hideProgressDialog();
                        }
                    });



        }

    }

    private void largeLitmitApply() {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.LargeAmount_DoApply, context);
        final User user = ApplicationEx.getInstance().getUser();
        ShoudanService.getInstance().largeLitmitApply(imagesUploadSid, industry, idCardNumStr, ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices applyResult) {
                if(applyResult.isRetCodeSuccess()){
                    user.setLargeAmountAccess(LargeAmountAccess.PROCESSING);
                    showMessageAndBackToMain("您已成功申请“大额收款”业务，拉卡拉需要大约1小时来处理您的申请，请您耐心等待！");
                }else if("005019".equals(applyResult.retCode)){

                    showMessage("您的商户实名验证未通过，请重新填写身份证号码并上传身份证照片后提交！");

                    shouldModifyPersonalInfo();

                } else {
                    showMessage(applyResult.retMsg);
                }
                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                showMessage("网络连接异常");
                hideProgressDialog();
            }
        });

    }

    protected void showSelectionDialog(){

        new AlertDialog.Builder(LargeAmountCollectionApplyActivity.this)
                .setTitle("请选择")
                .setItems(names, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvIndustry.setText(names[which]);
                        industry = ids[which];
                    }
                })
                .create().show();
    }


    private void shouldModifyPersonalInfo(){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                idCardNum.setFocusable(true);
                idCardNum.setFocusableInTouchMode(true);
                idCardNum.setText("");
                idPhotoBack.setFocusable(true);
//                idPhotoBack.setImageResource(R.drawable.btn_upload_idcard_face_down);
                idPhotoBack.setClickable(true);
                idPhotoFront.setFocusable(true);
                idPhotoFront.setClickable(true);
//                idPhotoFront.setImageResource(R.drawable.btn_upload_idcard_back_down);
                shouldUpload = true;
                updateIdCardPhotoVisibility(shouldUpload);
            }
        });
    }


    private boolean shouldUpload = false;

    private void updateIdCardPhotoVisibility(boolean visible){

        int photoVisibility = visible ? View.VISIBLE : View.GONE;

        int committedTvVisibility = visible? View.GONE : View.VISIBLE;

        idPhotoBack.setVisibility(photoVisibility);
        idPhotoFront.setVisibility(photoVisibility);
        findViewById(R.id.plus).setVisibility(photoVisibility);

        findViewById(R.id.committed).setVisibility(committedTvVisibility);

    }

    @Override
    protected void onCameraRequestFailed() {

    }

    @Override
    protected void onCameraRequestSucceed(int clickId, String picFilePath) {
        UILUtils.displayWithoutCache(picFilePath,((ImageView)findViewById(clickId)));
        photoFileMap.put(clickId,picFilePath);
    }
}
