package com.lakala.elive.merapply.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.ApplyIdReq;
import com.lakala.elive.beans.ApplyIdResp;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.utils.ActivityTaskCacheUtil;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.qcodeenter.QCodePrivatePhotoOcrActivity;
import com.lakala.elive.qcodeenter.QCodePublicPhotoOcrActivity;
import com.lakala.elive.user.base.BaseActivity;

/**
 * 进件
 * Created by wenhaogu on 2016/12/27.
 */

public class EnterPieceActivity extends BaseActivity {

    private ImageView back;
    private TextView tvPrivateAccount;//对私
    private TextView tvPublicAccount;//对公
    private TextView tvMyCommercialTenant;//我的商户

    private TextView tvPublicQCode;
    private TextView tvPrivateQCode;

    public static final String ACCOUNT_KIND = "accountKind";
    public static final String PRIVATE_ACCOUNT = "58";//对私
    public static final String PUBLIC_ACCOUNT = "57";//对公
    public static final String MERCHANT_ID = "merchantId";
    public static final int REQUEST_REFRESH_CODE = 99;//startActivityForResult 整个进件成功后全部finish掉

    //    public static final String APPLY_ID = "applyId"; //进件id
    public static String accountKind;


    public String qcodePublicResultAccountKind = "";//QCode对公请求返回的
    public String qcodePrivateResultAccountKind = "";//QCode对私请求返回的
    private String qcodePublicApplyId;
    private String qcodePrivateApplyId;
    private String qcodePublicProcess;
    private String qcodePrivateProcess;


    public String publicResultAccountKind = "";//对公请求返回的
    public String privateResultAccountKind = "";//对私请求返回的
    private String publicApplyId;
    private String privateApplyId;
    private String publicProcess;
    private String privateProcess;
    private ApplyIdReq applyIdReq;


    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_enter_piece);
    }

    @Override
    protected void bindView() {
        back = findView(R.id.btn_iv_back);
        TextView tvTitleName = findView(R.id.tv_title_name);
        tvPrivateAccount = findView(R.id.tv_private_account);
        tvPublicAccount = findView(R.id.tv_public_account);
        tvMyCommercialTenant = findView(R.id.tv_my_commercial_tenant);

        tvPublicQCode = findView(R.id.tv_public_qcode);
        tvPrivateQCode = findView(R.id.tv_private_qcode);

        tvTitleName.setText("进件");
        back.setVisibility(View.VISIBLE);

    }

    @Override
    protected void bindEvent() {
        tvPrivateAccount.setOnClickListener(this);
        tvPublicAccount.setOnClickListener(this);
        tvMyCommercialTenant.setOnClickListener(this);
        tvPublicQCode.setOnClickListener(this);
        tvPrivateQCode.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
//      showProgressDialog("加载中");
        applyIdReq = new ApplyIdReq();
        applyIdReq.setUserId(mSession.getUserLoginInfo().getUserId());
        NetAPI.getApplyId(this, this, applyIdReq);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.tv_private_account://对私
                ActivityTaskCacheUtil.getIntance().clearJinJianAct();
                setAccountKind(PRIVATE_ACCOUNT);
                setApplyChnal("01");
                if (privateResultAccountKind.equals(accountKind)) {
                    DialogUtil.createAlertDialog(this, new DialogUtil.OnClickForDialog() {
                        @Override
                        public void continueClick() {
                            /*
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, privateApplyId)
                                    .putExtra("process", privateProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                            */
                            startActivity(new Intent(EnterPieceActivity.this, WaitInputActivity.class).putExtra("APPLYCHNAL",applyChnal+"").putExtra("ACCOUNTKIND",accountKind+""));
                        }

                        @Override
                        public void toNewClick() {
                            startActivityForResult(new Intent(EnterPieceActivity.this, PrivatePhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);
                        }

                        @Override
                        public void toCancelClick() {
                        }
                    });
                    /*
                    DialogUtil.createAlertDialog(
                            this,
                            "提示",
                            "有未完成的进件,是否继续编辑?",
                            "取消",
                            "确定",
                            mListener
                    ).show();
                    */
                } else {
                    startActivityForResult(new Intent(this, PrivatePhotoOcrActivity.class)
                            .putExtra(ACCOUNT_KIND, PRIVATE_ACCOUNT), REQUEST_REFRESH_CODE);
                }

                break;
            case R.id.tv_public_account://对公
                ActivityTaskCacheUtil.getIntance().clearJinJianAct();
                setAccountKind(PUBLIC_ACCOUNT);
                setApplyChnal("01");
                if (publicResultAccountKind.equals(accountKind)) {
                    DialogUtil.createAlertDialog(this, new DialogUtil.OnClickForDialog() {
                        @Override
                        public void continueClick() {
                            /*
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, publicApplyId)
                                    .putExtra("process", publicProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                            */
                            startActivity(new Intent(EnterPieceActivity.this, WaitInputActivity.class).putExtra("APPLYCHNAL",applyChnal+"").putExtra("ACCOUNTKIND",accountKind+""));
                        }

                        @Override
                        public void toNewClick() {
                            startActivityForResult(new Intent(EnterPieceActivity.this, PublicPhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);
                        }

                        @Override
                        public void toCancelClick() {
                        }
                    });
                    /*
                    DialogUtil.createAlertDialog(
                            this,
                            "提示",
                            "有未完成的进件,是否继续编辑?",
                            "取消",
                            "确定",
                            mListener
                    ).show();
                    */
                } else {
                    startActivityForResult(new Intent(this, PublicPhotoOcrActivity.class)
                            .putExtra(ACCOUNT_KIND, PUBLIC_ACCOUNT), REQUEST_REFRESH_CODE);
                }
                break;
            case R.id.tv_my_commercial_tenant:
                startActivityForResult(new Intent(this, MyMerchantsActivity.class), REQUEST_REFRESH_CODE);
                break;

            case R.id.tv_public_qcode://对公的Q码进件
//                Intent intent = new Intent(this, QCodePublicPhotoOcrActivity.class);
//                intent.putExtra(ACCOUNT_KIND, PUBLIC_ACCOUNT);
//                startActivity(intent);

                setAccountKind(PUBLIC_ACCOUNT);
                setApplyChnal("03");
                if (qcodePublicResultAccountKind.equals(accountKind)) {
                    DialogUtil.createAlertDialog(this, new DialogUtil.OnClickForDialog() {
                        @Override
                        public void continueClick() {
                            startActivity(new Intent(EnterPieceActivity.this, WaitInputActivity.class).putExtra("APPLYCHNAL",applyChnal+"").putExtra("ACCOUNTKIND",accountKind+""));
                        }

                        @Override
                        public void toNewClick() {
                            Intent intent = new Intent(EnterPieceActivity.this, QCodePublicPhotoOcrActivity.class);
                            intent.putExtra(ACCOUNT_KIND, PUBLIC_ACCOUNT);
                            startActivityForResult(intent, REQUEST_REFRESH_CODE);
                        }

                        @Override
                        public void toCancelClick() {
                        }
                    });
                } else {
                    startActivityForResult(new Intent(this, QCodePublicPhotoOcrActivity.class)
                            .putExtra(ACCOUNT_KIND, PUBLIC_ACCOUNT), REQUEST_REFRESH_CODE);
                }

                break;
            case R.id.tv_private_qcode://对私的Q码进件
//                Intent intent1 = new Intent(this, QCodePrivatePhotoOcrActivity.class);
//                intent1.putExtra(ACCOUNT_KIND, PRIVATE_ACCOUNT);
//                startActivity(intent1);

                setAccountKind(PRIVATE_ACCOUNT);
                setApplyChnal("03");
                if (qcodePrivateResultAccountKind.equals(accountKind)) {
                    DialogUtil.createAlertDialog(this, new DialogUtil.OnClickForDialog() {
                        @Override
                        public void continueClick() {
                            startActivity(new Intent(EnterPieceActivity.this, WaitInputActivity.class).putExtra("APPLYCHNAL",applyChnal+"").putExtra("ACCOUNTKIND",accountKind+""));
                        }

                        @Override
                        public void toNewClick() {
                            Intent intent1 = new Intent(EnterPieceActivity.this, QCodePrivatePhotoOcrActivity.class);
                            intent1.putExtra(ACCOUNT_KIND, PRIVATE_ACCOUNT);
                            startActivityForResult(intent1, REQUEST_REFRESH_CODE);
                        }

                        @Override
                        public void toCancelClick() {
                        }
                    });
                } else {
                    startActivityForResult(new Intent(this, QCodePrivatePhotoOcrActivity.class)
                            .putExtra(ACCOUNT_KIND, PRIVATE_ACCOUNT), REQUEST_REFRESH_CODE);
                }
                break;
        }
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"
                    if ("01".equals(applyChnal)) {
                        if (PRIVATE_ACCOUNT.equals(accountKind)) {//对私
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, privateApplyId)
                                    .putExtra("process", privateProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                        } else {//对公
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, publicApplyId)
                                    .putExtra("process", publicProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                        }
                    }
                    if ("03".equals(applyChnal)) {
                        if (PRIVATE_ACCOUNT.equals(accountKind)) {//对私
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, qcodePrivateApplyId)
                                    .putExtra("process", qcodePrivateProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                        } else {//对公
                            startActivityForResult(new Intent(EnterPieceActivity.this, MerApplyDetailsActivity.class)
                                    .putExtra(MyMerchantsActivity.APPLYID_ID, qcodePublicApplyId)
                                    .putExtra("process", qcodePublicProcess), EnterPieceActivity.REQUEST_REFRESH_CODE);
                        }
                    }
                    dialog.dismiss();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    if ("01".equals(applyChnal)) {
                        if (PRIVATE_ACCOUNT.equals(accountKind)) {//对私
                            startActivityForResult(new Intent(EnterPieceActivity.this, PrivatePhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);

                        } else {//对公
                            startActivityForResult(new Intent(EnterPieceActivity.this, PublicPhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);
                        }
                    }

                    if ("03".equals(applyChnal)) {
                        if (PRIVATE_ACCOUNT.equals(accountKind)) {//对私
                            startActivityForResult(new Intent(EnterPieceActivity.this, QCodePrivatePhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);

                        } else {//对公
                            startActivityForResult(new Intent(EnterPieceActivity.this, QCodePublicPhotoOcrActivity.class)
                                    .putExtra(ACCOUNT_KIND, accountKind), REQUEST_REFRESH_CODE);
                        }
                    }

                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public static void setAccountKind(String s) {
        accountKind = s;
    }

    private static String applyChnal;

    public static void setApplyChnal(String s) {
        applyChnal = s;
    }

    public static void setImageProgress(ImageView imageView, int progress) {//设置步骤图片
        if (PRIVATE_ACCOUNT.equals(accountKind)) {//由于对私比对公少一部
            switch (progress) {
                case 1:
                    imageView.setImageResource(R.drawable.icon_one);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.icon_two);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.icon_three);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.icon_four);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.icon_five);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.icon_six);
                    break;
            }
        } else if (PUBLIC_ACCOUNT.equals(accountKind)) {
            switch (progress) {
                case 1:
                    imageView.setImageResource(R.drawable.ic_one);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_two);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.ic_three);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.ic_four);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.ic_five);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.ic_six);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.ic_seven);
                    break;
            }
        }
    }


    @Override
    public void onSuccess(int method, Object obj) {
        closeProgressDialog();
        switch (method) {
            case NetAPI.ACTION_APPLYID:
                handlerLoginSuccess((ApplyIdResp) obj);
                break;
        }
    }

    private void handlerLoginSuccess(ApplyIdResp obj) {
        if (null != obj && obj.getContent().size() > 0) {//对公发现有未进完的件,可继续编辑
            for (int x = 0; x < obj.getContent().size(); x++) {
                final String aK = obj.getContent().get(x).getAccountKind();
                final String aC = obj.getContent().get(x).getApplyChannel();
                if ("57".equals(aK) && "01".equals(aC)) {
                    publicResultAccountKind = obj.getContent().get(x).getAccountKind();
                    publicApplyId = obj.getContent().get(x).getApplyId();
                    publicProcess = obj.getContent().get(x).getProcess();
                    Log.d("EnterPieceActivity", publicApplyId);
                    continue;
                } else if ("57".equals(aK) && "03".equals(aC)) {
                    qcodePublicResultAccountKind = obj.getContent().get(x).getAccountKind();
                    qcodePublicApplyId = obj.getContent().get(x).getApplyId();
                    qcodePublicProcess = obj.getContent().get(x).getProcess();
                    Log.d("qcodePublicApplyId", qcodePublicApplyId);
                    continue;
                } else if ("58".equals(aK) && "01".equals(aC)) {
                    privateResultAccountKind = obj.getContent().get(x).getAccountKind();
                    privateApplyId = obj.getContent().get(x).getApplyId();
                    privateProcess = obj.getContent().get(x).getProcess();
                    Log.d("EnterPieceActivity=====", privateApplyId);
                    continue;
                } else if ("58".equals(aK) && "03".equals(aC)) {
                    qcodePrivateResultAccountKind = obj.getContent().get(x).getAccountKind();
                    qcodePrivateApplyId = obj.getContent().get(x).getApplyId();
                    qcodePrivateProcess = obj.getContent().get(x).getProcess();
                    Log.d("qcodePrivaApplyId=====", qcodePrivateApplyId);
                    continue;
                }
            }
        } else {
            publicResultAccountKind = "";
            publicApplyId = "";
            publicProcess = "";

            qcodePublicResultAccountKind = "";
            qcodePublicApplyId = "";
            qcodePublicProcess = "";

            privateResultAccountKind = "";
            privateApplyId = "";
            privateProcess = "";

            qcodePrivateResultAccountKind = "";
            qcodePrivateApplyId = "";
            qcodePrivateProcess = "";
        }
/*

        if (null != obj && obj.getContent().size() > 0) {//对私发现有未进完的件,可继续编辑
            for (int x = 0; x < obj.getContent().size(); x++) {
                if ("58".equals(obj.getContent().get(x).getAccountKind()) && "01".equals(obj.getContent().get(x).getApplyChannel())) {
                    privateResultAccountKind = obj.getContent().get(x).getAccountKind();
                    privateApplyId = obj.getContent().get(x).getApplyId();
                    privateProcess = obj.getContent().get(x).getProcess();
                    Log.d("EnterPieceActivity=====", privateApplyId);
                    break;
                } else if ("58".equals(obj.getContent().get(x).getAccountKind()) && "03".equals(obj.getContent().get(x).getApplyChannel())) {
                    qcodePrivateResultAccountKind = obj.getContent().get(x).getAccountKind();
                    qcodePrivateApplyId = obj.getContent().get(x).getApplyId();
                    qcodePrivateProcess = obj.getContent().get(x).getProcess();
                    Log.d("qcodePrivaApplyId=====", qcodePrivateApplyId);
                    break;
                }
            }
        } else {
            privateResultAccountKind = "";
            privateApplyId = "";
            privateProcess = "";

            qcodePrivateResultAccountKind = "";
            qcodePrivateApplyId = "";
            qcodePrivateProcess = "";
        }
*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EnterPieceActivity.REQUEST_REFRESH_CODE && resultCode == RESULT_OK) {//进件成功后finish
            NetAPI.getApplyId(this, this, applyIdReq);//成功后,出现请求下,刷新applyid列表
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
