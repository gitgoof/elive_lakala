package com.lakala.shoudan.activity.payment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.consts.ConstKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.signature.SignatureManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SignatureView;
import com.lakala.ui.dialog.ProgressDialog;


/**
 * Created by More on 14-1-26.
 */
public class SignatrueActivity extends AppBaseActivity implements View.OnClickListener {
    /**
     * Intent数据名称  值为布尔值 用来判断签名版是否有字迹
     */
    public static String RETURN_CHECK_SIGN = "RETURN_CHECK_SIGN";
    private SignatureView signaturelayout;
    private final int[] btnsId = new int[]{
            R.id.re_sign,
            R.id.completed,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_signatrue_layout);
        hideNavigationBar();
        initUI();
    }

    @Override
    protected void setDefaultOrientationPortrait() {

    }

    protected void initUI() {
        BaseTransInfo transInfo = (BaseTransInfo) getIntent().getSerializableExtra(ConstKey.TRANS_INFO);

        signaturelayout = (SignatureView) findViewById(R.id.signature);
        for (int i = 0; i < btnsId.length; i++) {
            findViewById(btnsId[i]).setOnClickListener(this);
        }

        findViewById(R.id.go_portrait_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToBillSignatureActivity();
            }
        });
        ((TextView) findViewById(R.id.go_portrait_signature)).setText(
                Html.fromHtml("<u>" + getString(R.string.portrait) + "</u>"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.completed:
                backToBillSignatureActivity();
//               if(signaturelayout.checkSignPath()){
//                   alertMobileNoInputDialog();
//               }else{
//                   toast("请签名");
//               }
                break;
            case R.id.re_sign:
                signaturelayout.clear();
                break;
            default:
                break;
        }
    }

    private void backToBillSignatureActivity() {
        SignatureManager.getInstance().showPic = signaturelayout.getOriginalBitmap();
        Intent intent = new Intent();
        intent.putExtra(RETURN_CHECK_SIGN, signaturelayout.checkSignPath());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //land
            if (null != SignatureManager.getInstance().showPic)
                signaturelayout.setBackgroundDrawable(new BitmapDrawable(SignatureManager.getInstance().showPic));
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //port
            if (signaturelayout.checkSignPath())
                SignatureManager.getInstance().showPic = signaturelayout.getOriginalBitmap();
        }

    }

    private static final int SHOW_PROGRESS = 10;
    private static final int UPLOAD_FAILED = 11;
    private static final int UPLOAD_SUCCESS = 12;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    showProgressBar();
                    break;
                case UPLOAD_FAILED:

//                    SignatureManager.getInstance().startSignatruePollingSerice(sid, chrttCode, pan, "");//补签不送手机号
                    completeEvent();
                    break;
                case UPLOAD_SUCCESS:

                    completeEvent();
                    break;
            }
        }
    };

    private ProgressDialog dialog;

    private void showProgressBar() {
        dialog = DialogCreator.createProgressDialog(context, "");
        dialog.show();

    }


    private void startUpload() {

//        handler.sendEmptyMessage(SHOW_PROGRESS);
//        SignatureManager.getInstance().uploadCacheSignatrue(sid, chrttCode, pan, mobileNo,new SignatureManager.UploadListener() {
//            @Override
//            public void onUploadFinish(boolean ifSuccess) {
//                if (ifSuccess) {
//                    handler.sendEmptyMessage(UPLOAD_SUCCESS);
//                } else {
//                    handler.sendEmptyMessage(UPLOAD_FAILED);
//                }
//            }
//        });

    }

    private void completeEvent() {
        Intent intent = getIntent();
        intent.putExtra(RETURN_CHECK_SIGN, signaturelayout.checkSignPath());
        setResult(0, intent);
        finish();
        ToastUtil.toast(this, getString(R.string.sumbit_ok));
    }


}
