package com.lakala.shoudan.activity.wallet;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.encryption.Digest;
import com.lakala.library.encryption.Mac;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
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
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.XAtyTask;
import com.lakala.ui.component.CaptchaTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * 短信验证
 * 实名认证
 * Created by HJP on 2015/11/23.
 */
public class SMSVerifiActivity extends AppBaseActivity {
    private TextView tvTips;
    private CaptchaTextView tvGetCaptcha;
    private EditText etInputCaptcha;
    private TextView idCommonGuideButton;
    private String agstat;//签约状态
    private String mobileInBank;//银行预留手机号
    private QuickCardBinBean quickCardBinBean;
    private String sid;
    private String srcid;//SID值
    private String tag ="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_pay);
        init();
    }
    public  void init(){
        initUI();
        quickCardBinBean= (QuickCardBinBean) getIntent().getSerializableExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN);
        sid=getIntent().getStringExtra("sid");
        mobileInBank=quickCardBinBean.getMobileInBank();

        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvGetCaptcha = (CaptchaTextView) findViewById(R.id.tv_get_captcha);
        etInputCaptcha = (EditText) findViewById(R.id.et_input_captcha);
        idCommonGuideButton =(TextView)findViewById(R.id.id_common_guide_button);
        final Point size = getViewSize(tvGetCaptcha);
        size.y = RelativeLayout.LayoutParams.MATCH_PARENT;
        storageViewSize(tvGetCaptcha, size);
        tvTips.setText("请输入手机尾号" + mobileInBank.substring(7, 11) + "收到的短信验证码：");
        etInputCaptcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if((etInputCaptcha.getText().toString().trim()).length()>=6){
                    idCommonGuideButton.setEnabled(true);
                }else{
                    idCommonGuideButton.setEnabled(false);
                }
            }
        });
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            getUnsignSMSCode();
                    }
                }
        );
        idCommonGuideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tag.equals("1")){
                            if(!TextUtils.isEmpty(etInputCaptcha.getText().toString().trim())){
                                    quickUnsign();
                            }else{
                                ToastUtil.toast(context,"验证码不能为空！");
                            }
                        }else {
                            ToastUtil.toast(context,"请先获取验证码");

                        }

                    }
                }
        );
        LogUtil.print("<v>",sid+"");
        if("-1".equals(sid)){
            tvGetCaptcha.setEnabled(true);
            tvGetCaptcha.setText("重新获取");
        }else {
            tvGetCaptcha.startCaptchaDown(59);
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("短信验证");
    }


    private void storageViewSize(View view,Point size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params != null){
            params.width = size.x;
            params.height = size.y;
            view.setLayoutParams(params);
        }
    }

    private Point getViewSize(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
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
                    tvGetCaptcha.startCaptchaDown(59);
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        sid=jsonObject.optString("sid");
                        tag = "1";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    tag = "1";
                    toast(resultServices.retMsg);
                    tvGetCaptcha.setEnabled(true);
                    tvGetCaptcha.setText("重新获取");
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
    /**
     * 实名验证
     */
    private void quickUnsign(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest=RequestFactory.getRequest(this, RequestFactory.Type.REAL_NAME_AHTUS);
        HttpRequestParams params = businessRequest.getRequestParams();
        params.put("realName",quickCardBinBean.getCustomerName() );
        params.put("idCardNo", quickCardBinBean.getIdentifier());
        params.put("bankCode", quickCardBinBean.getBankCode());
        params.put("bankName", quickCardBinBean.getBankName());
        params.put("mobileNum", quickCardBinBean.getMobileInBank());
        params.put("accountNo", quickCardBinBean.getAccountNo());
        params.put("accountType", quickCardBinBean.getAccountType());
        params.put("checkCode",etInputCaptcha.getText().toString().trim() );
        params.put("srcid", sid);
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
      //  params.put("mac", DeviceUtil.getMac(context));
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
                String a=resultServices.retCode;
                if(resultServices.isRetCodeSuccess()){
                    createFullShueDialog("恭喜您，您已实名认证成功",null);
                }else  if(resultServices.retCode.equals("A00001")||resultServices.retCode.equals("SM")){
                    String bracket = resultServices.retMsg.substring(resultServices.retMsg.indexOf("（"),resultServices.retMsg.indexOf("）")+1);
                    createFullShueDialog(resultServices.retMsg.replace(bracket, "")
                            ,"sss");
                    tvGetCaptcha.setEnabled(true);
                    tvGetCaptcha.setText("重新获取");
                    etInputCaptcha.setText("");
                    LogUtil.print("<v>",resultServices.retCode+"1");
                } else{
                    String bracket = resultServices.retMsg.substring(resultServices.retMsg.indexOf("（"),resultServices.retMsg.indexOf("）")+1);
                    LogUtil.print("<v>",resultServices.retCode+"2");
                    createFullShueDialog(resultServices.retMsg.replace(bracket, ""),null);
                }
               // createFullShueDialog("验证码错误，请重新输入");

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.network_fail);
                LogUtil.print(connectEvent.getDescribe());
                //XAtyTask.getInstance().killAty(WalletProcessAuthActivity.class);
                //context.finish();
            }
        });
        businessRequest.execute();
    }
    private void  createFullShueDialog(String content, final String tags){
        DialogCreator.createFullShueDialog(
                context, "提示",   "确定", content,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (tags==null){
                            XAtyTask.getInstance().killAty(WalletProcessAuthActivity.class);
                            context.finish();
                        }

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //  startActi(isUpgradeFirst);
                    }
                }
        ).show();
    }
}
