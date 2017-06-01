package com.lakala.elive.qcodeenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.qcodeenter.ercodeview.ErCodeMipcaCaptureActivity;
import com.lakala.elive.qcodeenter.request.QCodeBindRequ;
import com.lakala.elive.qcodeenter.response.QCodeBindResponse;
import com.lakala.elive.qcodeenter.response.QCodeListResponse;
import com.mining.app.zxing.encoding.EncodingHandler;

/**
 * Q码绑定
 */
public class QCodeBindActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();

    private TextView mMechanNameTxt;
    private TextView mMechanNumberTxt;
    private TextView mMechanContentTxt;
    private TextView mSettleNumberTxt;

    //绑定Q码成功
    private LinearLayout mBindSuccessLiner;
    private TextView mBindSuccErCoNumTxt;
    private ImageView mBindSuccErCoImg;

    //选择
    private RadioGroup mRadioGroup;
    private RadioButton mHaveRadio;
    private RadioButton mNoHaveRadio;

    //绑定中
    private LinearLayout mBindProgressLiner;
    private TextView   mBindProTxt;

    private ImageView back;
    private Button mNextBtn;

    public int flagType;//1扫描绑定，2生成二维码，3提交

    private String mechanNo;
    private String mechanName;
    private String content;
    private String accountNo;

    private QCodeListResponse.ContentBean.Qcodes qCodeBean;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_qcodepub_qcodebind);
    }

    @Override
    protected void bindView() {
        back = findView(R.id.btn_iv_back);
        mMechanNameTxt = findView(R.id.qcodepubbind_mechen_name);
        mMechanNumberTxt = findView(R.id.qcodepubbind_mechen_number);
        mMechanContentTxt = findView(R.id.qcodepubbind_mechen_contents);
        mSettleNumberTxt = findView(R.id.qcodepubbind_mechen_settoncount);

        mBindSuccessLiner = findView(R.id.qcodebind_bindsuccess_Linerla);
        mBindSuccErCoImg = findView(R.id.qcodebind_bindsuccess_ercode_img);
        mBindSuccErCoNumTxt = findView(R.id.qcodebind_bindsuccess_number_txt);

        mRadioGroup = findView(R.id.qcodebind_bindsuccess_rediogroup);
        mHaveRadio = findView(R.id.qcodebind_bindsuccess_redio_have);
        mNoHaveRadio = findView(R.id.qcodebind_bindsuccess_redio_nohave);

        mBindProgressLiner = findView(R.id.qcodebind_bindprogress_Linerla);
        mBindProTxt=findView(R.id.qcodebind_bindprogress_txt);

        mNextBtn = findView(R.id.btn_next);
        back.setVisibility(View.VISIBLE);

        //默认设置扫描绑定二维码是选中的
        flagType = 1;
        mHaveRadio.setChecked(true);
        mNextBtn.setText("扫描绑定二维码");
    }

    @Override
    protected void bindEvent() {
        mNextBtn.setOnClickListener(this);
        back.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.qcodebind_bindsuccess_redio_have://有卡台，扫描
                        flagType = 1;
                        mNextBtn.setText("扫描绑定二维码");
                        break;
                    case R.id.qcodebind_bindsuccess_redio_nohave://无卡台，生成
                        flagType = 2;
                        mNextBtn.setText("生成二维码");
                        break;
                }
            }
        });
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("Q码绑定");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            qCodeBean = (QCodeListResponse.ContentBean.Qcodes) bundle.getSerializable("QCODEBEAN");
            mechanNo = bundle.getString("MERCHANNO");
            mechanName = bundle.getString("MERCHANNAME");
            content = bundle.getString("CONTENT");
            accountNo = bundle.getString("ACCONUTNO");

            mMechanNameTxt.setText(mechanName);
            mMechanNumberTxt.setText("商户号 : " + mechanNo);
            mMechanContentTxt.setText("联系人 : " + content);
            mSettleNumberTxt.setText("结算账号 : " + accountNo);
        }
    }

    //绑定Q码
    private void bindQCodeRequest(String merchanNo, String accountNo, String qcode) {//绑定二维码，无卡台qcode传空，有卡台传扫描出来的qcode
        QCodeBindRequ qCodeBindRequ = new QCodeBindRequ();
        qCodeBindRequ.setMerchantNo(merchanNo);
        qCodeBindRequ.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        qCodeBindRequ.setAccountNo(accountNo);
        qCodeBindRequ.setQcode(qcode);
        NetAPI.qCodeBindRequest(this, this, qCodeBindRequ);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_next://
                switch (flagType) {
                    case 1://扫描二维码
                        Intent intent = new Intent();
                        intent.setClass(this, ErCodeMipcaCaptureActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        break;
                    case 2://生成
                        showProgressDialog("提交中...");
                        //绑定二维码
                        bindQCodeRequest(mechanNo, accountNo, "");//无卡台qcode不传值
                        break;
                    case 3://提交
                        finish();
                        break;
                }
                break;
        }
    }

    private final static int SCANNIN_GREQUEST_CODE = 1;

    private  String result;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {//二维码扫描成功
                    String qcode="";
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                             result = bundle.getString("result");
                            if(result!=null){
//                              qcode= result.replace("http://postest.lakala.com.cn/q/","");
                                qcode= result+"";
                            }
                            Log.e(TAG,result);
                            Log.e(TAG,qcode);
                            showProgressDialog("提交中...");
                            bindQCodeRequest(mechanNo, accountNo, qcode);//绑定Q码
//        //显示
//        mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                        }
                    } else {
                        Utils.showToast(this, "扫码结果为空,扫码失败");
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.qCodeApplyBind://绑定成功接口调用成功
                closeProgressDialog();
                QCodeBindResponse   response= (QCodeBindResponse) obj;
                if(1==flagType){//扫码绑定
                    if(response!=null&&response.getContent()!=null&&response.getContent().getSuccess()!=null&&"true".equals(response.getContent().getSuccess())){
                        mRadioGroup.setVisibility(View.GONE);
                        mBindSuccessLiner.setVisibility(View.GONE);
                        mBindProgressLiner.setVisibility(View.VISIBLE);
                        mNextBtn.setText("返回商户详情");
                        flagType=3;
                        mBindProTxt.setText("正在绑定二维码");
                    }else{
                        Utils.showToast(this, response.getContent().getMessage());
                    }
                }else if(2==flagType) {//生成绑定
                    if(response!=null&&response.getContent()!=null&&response.getContent().getSuccess()!=null&&"true".equals(response.getContent().getSuccess())){
                        mRadioGroup.setVisibility(View.GONE);
                    mBindSuccessLiner.setVisibility(View.GONE);
                    mBindProgressLiner.setVisibility(View.VISIBLE);
                    mNextBtn.setText("返回商户详情");
                    flagType=3;
                    mBindProTxt.setText("正在生成二维码");
                    }else{
                        Utils.showToast(this, response.getContent().getMessage());
                    }
                }
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode + "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
