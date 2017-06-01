package com.lakala.shoudan.activity.quickArrive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.DateUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertInputDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 交易详情
 * Created by WangCheng on 2016/9/5.
 */
public class TradeResultActivity extends AppBaseActivity implements View.OnClickListener{

    private String data="";
    private String amout;
    private BaseTransInfo transInfo;
    private boolean isSend;
    private JSONObject jsonObject= null;
    private TextView tv_statu,tv_no,tv_time,tv_num,
            tv_sign_statu,tv_proof,tv_num2,
            tv_shou,tv_not_have,tv_msg,tv_ljtk,tv_ke;
    private View ok,no;
    private String red_code,red_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_result);
        initMyView();
        initMyData();
    }

    public void initMyView(){
        navigationBar.setTitle("交易结果");
        navigationBar.setBackBtnVisibility(View.GONE);
        tv_ljtk= (TextView) findViewById(R.id.tv_ljtk);
        tv_ljtk.setOnClickListener(this);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        tv_statu= (TextView) findViewById(R.id.tv_status);
        tv_no= (TextView) findViewById(R.id.tv_no);
        tv_time= (TextView) findViewById(R.id.tv_time);
        tv_num= (TextView) findViewById(R.id.tv_num);
        tv_sign_statu= (TextView) findViewById(R.id.tv_sign);
        tv_proof= (TextView) findViewById(R.id.tv_proof);
        tv_num2= (TextView) findViewById(R.id.tv_num2);
        tv_shou= (TextView) findViewById(R.id.tv_shou);
        tv_not_have= (TextView) findViewById(R.id.tv_not_have);
        tv_msg= (TextView) findViewById(R.id.tv_msg);
        tv_ke= (TextView) findViewById(R.id.tv_ke);
        ok=findViewById(R.id.ll_ok);
        no=findViewById(R.id.ll_no);
    }

    public void initMyData(){
        transInfo = (BaseTransInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
        if(!TextUtils.isEmpty(getIntent().getStringExtra("code"))){
            red_code=getIntent().getStringExtra("code");
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra("msg"))){
            red_msg=getIntent().getStringExtra("msg");
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra("data"))){
            data=getIntent().getStringExtra("data");
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra("amout"))){
            amout=getIntent().getStringExtra("amout");
        }
        isSend=getIntent().getBooleanExtra("isSend",false);
        initUi();
    }

    public void initUi(){
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (transInfo.getTransResult()){
            case TIMEOUT:
                tv_statu.setText("收款超时");
                break;
            case SUCCESS:
                tv_statu.setText("收款成功");
                break;
            case FAILED:
                tv_statu.setText("收款失败");
                break;
        }
        tv_no.setText(CardUtil.formatCardNumberWithStar(transInfo.getPayCardNo()));
        tv_time.setText(DateUtil.formateDateTransTime2(transInfo.getSyTm()));
        tv_num.setText(transInfo.getSwipeAmount()+"元");
        tv_sign_statu.setText("已签名");
        tv_proof.setText(isSend?"已发送":"未发送");
        if("050506".equals(red_code)){
            tv_msg.setText(red_msg);
            ok.setVisibility(View.VISIBLE);
            no.setVisibility(View.GONE);
            tv_num2.setText("0.00元");
            tv_shou.setText("手续费0元");
            tv_ljtk.setTextColor(getResources().getColor(R.color.font_gray_three4));
            tv_ke.setTextColor(getResources().getColor(R.color.font_gray_three4));
            tv_shou.setTextColor(getResources().getColor(R.color.font_gray_three4));
            tv_num2.setTextColor(getResources().getColor(R.color.font_gray_three4));
            tv_ljtk.setClickable(false);
        }else if(!"isNotOpen".equals(jsonObject.optString("code"))){
            tv_msg.setText(jsonObject.optString("msg"));
            ok.setVisibility(View.VISIBLE);
            no.setVisibility(View.GONE);
            if(!jsonObject.has("data")){
                tv_num2.setText("0.00元");
                tv_shou.setText("手续费0元");
                tv_ljtk.setTextColor(getResources().getColor(R.color.font_gray_three4));
                tv_ke.setTextColor(getResources().getColor(R.color.font_gray_three4));
                tv_shou.setTextColor(getResources().getColor(R.color.font_gray_three4));
                tv_num2.setTextColor(getResources().getColor(R.color.font_gray_three4));
                tv_ljtk.setClickable(false);
            }else {
                tv_ljtk.setClickable(true);
                try {
                    JSONObject jsonObject2=new JSONObject(jsonObject.optString("data"));
                    tv_num2.setText(jsonObject2.optString("canCarsh")+"元");
                    tv_shou.setText("手续费"+jsonObject2.optString("fee")+"元");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            ok.setVisibility(View.GONE);
            no.setVisibility(View.VISIBLE);
            tv_not_have.setText(jsonObject.optString("msg"));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_ljtk://立即提款
                getInfo();
                break;
            case R.id.tv_ok://返回首页
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }


    /**
     * 立即提款
     */
    public void getInfo(){
        String url="v1.0/trade/speedArrivalD0/cash";
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(this,url, HttpRequest.RequestMethod.POST,true);
        businessRequest.getRequestParams().put("sid",transInfo.getSid());
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    showDialog("您已提款成功，感谢您使用拉卡拉收款宝立即提款。");
//                    showDialog("您已提款成功，感谢您使用拉卡拉收款宝提款服务.");
                }else {
                    showDialog("很抱歉，由于系统原因，提款失败，您可稍后在首页“立即提款”进行提款。");
//                    showDialog("很抱歉，由于系统原因，提款失败，您可稍后在客户端首页进行提款。");
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
    @Override
    public void onBackPressed() {

    }

    public void showDialog(String msg){
        DialogCreator.createOneConfirmButtonDialog(
                TradeResultActivity.this, "确定", msg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(TradeResultActivity.this, MainActivity.class));
                    }
                }).show();
    }

}
