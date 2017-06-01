package com.lakala.shoudan.activity.merchant.upgrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.nativeplugin.CameraPlugin;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.TakePicView;
import com.lakala.shoudan.util.CustomCameraPlugin;
import com.lakala.ui.component.LabelEditText;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 商户升级信息填写页
 * Created by huangjp on 2016/3/15.
 */
public class MerchantUpdateActivity extends AppBaseActivity implements CameraPlugin.CameraListener {
    private TakePicView pvDoor;
    private TakePicView pvPlace;
    private TakePicView pvIDCard;
    private TakePicView pvPayCard;
    private TakePicView pvLicense;
    private CommonServiceManager serviceManager;
    private SparseArray<FileEntity> photoMap = new SparseArray<FileEntity>();
    //    private AreaEntity branch;
//    private LabelEditText letBranch;
    private ScrollView svFirstUpdate;
    private ScrollView svTwiceUpdate;
    private LabelEditText letLicenceNum;
    private LabelEditText letIdcard;
    private LabelEditText letAccountNo;
    private MerchantInfo merchantInfo;
    private boolean isUpgradeFirst;//升级级别
    private TextView tvSubmitAudit;
    private TextView tvUpdate;
    private CustomCameraPlugin cameraPlugina;

    private CameraPlugin cameraPlugins;

    @Override
    public void onSuccess(String filePath) {
        int clickId = cameraPlugins.get("id");
        fileUploadTask(clickId, filePath);
    }

    @Override
    public void onFailed() {

    }

    public static void start(Context context, boolean isUpgradeFirst) {
        Intent intent = new Intent(context, MerchantUpdateActivity.class);
        intent.putExtra("isUpgradeFirst", isUpgradeFirst);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_update);
        serviceManager = CommonServiceManager.getInstance();
        cameraPlugins = new CustomCameraPlugin();
        cameraPlugins.register(this, this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        initNavigation();
        initView();
        initData();
    }

    private void initNavigation() {
        navigationBar.setTitle(R.string.mechant_upgrade);
        navigationBar.setActionBtnText("业务说明");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case action:
                        ProtocalActivity.open(MerchantUpdateActivity.this, ProtocalType.MERCHANT_UPGRADE_DESCRIPTION);
                        ShoudanStatisticManager.getInstance()
                                .onEvent(ShoudanStatisticManager.Merchant_Merchant_Info_Upgtate_Introduce, context);
                        break;
                    case back:
                        onBackPressed();
                        break;
                }
            }
        });
    }

    public void initView() {
        tvUpdate = (TextView) findViewById(R.id.tv_update);
        tvSubmitAudit = (TextView) findViewById(R.id.tv_submit_audit);
        letLicenceNum = (LabelEditText) findViewById(R.id.let_licence_num);
        svFirstUpdate = (ScrollView) findViewById(R.id.sv_first_update);
        svTwiceUpdate = (ScrollView) findViewById(R.id.sv_twice_update);
        pvIDCard = (TakePicView) findViewById(R.id.pv_idcard);
        pvIDCard.getLinearLayout().setVisibility(View.GONE);
        pvIDCard.getImageView2().setVisibility(View.VISIBLE);
        pvIDCard.getImageView2().setImageResource(R.drawable.btn_sfz);
        pvPayCard = (TakePicView) findViewById(R.id.pv_paycard);
        pvPayCard.getLinearLayout().setVisibility(View.INVISIBLE);
        pvPayCard.getImageView2().setVisibility(View.VISIBLE);
        pvPayCard.getImageView2().setImageResource(R.drawable.btn_jsk);
        pvLicense = (TakePicView) findViewById(R.id.pv_license);
        pvDoor = (TakePicView) findViewById(R.id.pv_door);
        pvPlace = (TakePicView) findViewById(R.id.pv_place);
        pvPayCard.getTvLine2().setText("结算卡照片");
        pvLicense.getTvLine2().setText("营业执照照片");
        pvDoor.getTvLine2().setText("门头照");
        pvPlace.getTvLine2().setText("营业场所照");
        pvLicense.getTvLine1().setText("点击拍摄");
        pvDoor.getTvLine1().setText("点击拍摄");
        pvPlace.getTvLine1().setText("点击拍摄");
        pvIDCard.setOnClickListener(takePicListener);
        pvPayCard.setOnClickListener(takePicListener);
        pvLicense.setOnClickListener(takePicListener2);
        pvDoor.setOnClickListener(takePicListener2);
        pvPlace.setOnClickListener(takePicListener2);

//        letBranch =(LabelEditText)findViewById(R.id.labelEditText_branch);
//        letBranch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MerchantUpdateActivity.this, BranchInfoActivity.class);
//                startActivityForResult(intent,0);
//            }
//        });

        merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();

        letIdcard = (LabelEditText) findViewById(R.id.labelEditText_idcard);
        letIdcard.setText(Util.formatCardNumberWithStar(merchantInfo.getUser().getIdCardInfo().getIdCardId()));

        letAccountNo = (LabelEditText) findViewById(R.id.labelEditText_accountNo);
        letAccountNo.setText(merchantInfo.getDisplasyAccountNo());

        tvSubmitAudit.setOnClickListener(this);
        tvUpdate.setOnClickListener(this);
    }

    private View.OnClickListener takePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
//            if (id == R.id.pv_idcard) {
//                cameraPlugin.setOverlayType(1);
//            } else if (id == R.id.pv_paycard) {
//                cameraPlugin.setOverlayType(2);
//            }
            cameraPlugins.put("id", id);
            cameraPlugins.startActionImageCapture();
        }
    };

    private View.OnClickListener takePicListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            cameraPlugins.put("id", id);
            cameraPlugins.startActionImageCapture();
        }
    };

    private void initData() {
        //一、二类升级状态判断
        isUpgradeFirst = getIntent().getBooleanExtra("isUpgradeFirst", true);
        if (isUpgradeFirst) {
            svFirstUpdate.setVisibility(View.VISIBLE);
            svTwiceUpdate.setVisibility(View.GONE);
        } else {
            svTwiceUpdate.setVisibility(View.VISIBLE);
            svFirstUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_update:
                upgradeSecondTask();
                break;
            case R.id.tv_submit_audit:
                upgradeFirstTask();
                break;
        }
    }

    private void upgradeFirstTask() {
        FileEntity idEntity = photoMap.get(R.id.pv_idcard);
        String idCardFileName;
        if (idEntity == null) {
            toast("请上传身份证照！");
            return;
        } else {
            idCardFileName = idEntity.getFileName();
        }
        FileEntity payEntity = photoMap.get(R.id.pv_paycard);
        String payCardFileName;
        if (payEntity == null) {
            toast("请上传结算卡照！");
            return;
        } else {
            payCardFileName = payEntity.getFileName();
        }
        showProgressWithNoMsg();
        updataFirst(idCardFileName, payCardFileName);
    }

    private void updataFirst(String idCardFileName, String payCardFileName) {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.UPGRADE_FIRST);
        HttpRequestParams params = request.getRequestParams();
        params.put("idCardNo", merchantInfo.getUser().getIdCardInfo().getIdCardId());
        params.put("idCardNoFileName", idCardFileName);
        params.put("settleCardNo", merchantInfo.getAccountNo());
        params.put("settleFileName", payCardFileName);
        serviceResultCallback.setTag("first");
        request.setResponseHandler(serviceResultCallback);
        request.execute();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (resultCode) {
//            case ConstKey.RESULT_BRANCH:
//                branch = data.getParcelableExtra("branch");
//                if (branch != null) {
//                    letBranch.setText(branch.getName());
//                    return;
//                }
//                break;
//        }
//    }

    private void upgradeSecondTask() {
//        if(branch == null){
//            toast("请选择支行");
//            return;
//        }
        if (TextUtils.isEmpty(letLicenceNum.getText().toString())) {
            toast("请输入营业执照号码");
            return;
        }
        int pvs[] = {R.id.pv_license, R.id.pv_door, R.id.pv_place};
        String toasts[] = {"请上传营业执照！", "请上传门头照！", "请上传经营场所照！"};

        String fileName[] = new String[3];
        FileEntity file1 = photoMap.get(pvs[0]);
        FileEntity file2 = photoMap.get(pvs[1]);
        FileEntity file3 = photoMap.get(pvs[2]);
        if (file1 == null) {
            toast(toasts[0]);
            return;
        }
        if (file2 == null) {
            toast(toasts[1]);
            return;
        }
        if (file3 == null) {
            toast(toasts[2]);
            return;
        }
        for (int i = 0; i < photoMap.size(); i++) {
            FileEntity file = photoMap.get(pvs[i]);
            fileName[i] = file.getFileName();
        }
        upgradeSecond(fileName);
    }

    private void upgradeSecond(String fileName[]) {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.UPGRADE_SECOND);
        HttpRequestParams params = request.getRequestParams();
//        params.put("openBankBranchCode",branch.getCode());
//        params.put("openBankBranchName",branch.getName());
        params.put("busLicenceCode", letLicenceNum.getText().toString());
        params.put("busLicenceFileName", fileName[0]);
        params.put("facadeFileName", fileName[1]);
        params.put("placeBusFileName", fileName[2]);
        serviceResultCallback.setTag("second");
        request.setResponseHandler(serviceResultCallback);
        request.execute();
    }



    private void fileUploadTask(final int clickId, final String picFilePath) {
        showProgressDialog("正在上传文件");
        LogUtil.print("------>","图片上传中");
        serviceManager.merLevelFileUpload(picFilePath, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    String dataStr = resultServices.retData;
                    try {
                        JSONObject data = new JSONObject(dataStr);
                        String fileName = data.optString("fileName");
                        FileEntity fileEntity = new FileEntity(picFilePath, fileName);
                        photoMap.put(clickId, fileEntity);
                        showPic2View(picFilePath, clickId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtil.print(resultServices.retMsg);
                    fileUploadError(clickId, picFilePath);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(getString(R.string.socket_error));
                fileUploadError(clickId, picFilePath);
            }
        });
    }

    private void showPic2View(String picFilePath, int clickId) {
        TakePicView picView = (TakePicView) findViewById(clickId);
        picView.getLinearLayout().setVisibility(View.INVISIBLE);
        picView.getImageView2().setVisibility(View.GONE);
        ImageView imageView = picView.getImageView();
        imageView.setVisibility(View.VISIBLE);
        LogUtil.print(picFilePath);
        UILUtils.displayWithoutCache(picFilePath, imageView);
    }

    private void fileUploadError(final int clickId, final String picFilePath) {
        DialogCreator.createFullContentDialog(context, "取消", "重新上传",
                "图片上传失败,请检查网络后重试",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        fileUploadTask(clickId, picFilePath);
                    }
                }).show();
    }

    private static class FileEntity {
        String filePath;
        String fileName;

        public FileEntity(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }
    }

    ServiceCallback serviceResultCallback = new ServiceCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            hideProgressDialog();
            if (resultServices.isRetCodeSuccess()) {
                String tag = serviceResultCallback.getTag();
                if (TextUtils.equals(tag, "first")) {
                    ShoudanStatisticManager.getInstance()
                            .onEvent(ShoudanStatisticManager.FIRST_UPGRADE_SUCCESS, context);
                } else {
                    ShoudanStatisticManager.getInstance()
                            .onEvent(ShoudanStatisticManager.SECOND_UPGRADE_SUCCESS, context);
                }
                DialogCreator.createOneConfirmButtonDialog(context, "确定", "资料已提交成功，请等待审核", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusinessLauncher.getInstance().clearTop(MainActivity.class);
                       /* if (getIntent().hasExtra("fromMerchant")){
                            finish();
                        }else {
                            BusinessLauncher.getInstance().clearTop(MainActivity.class);
                        }*/
                    }
                }).show();
            } else {
                toast(resultServices.retMsg);
            }
        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            hideProgressDialog();
            toast(getString(R.string.socket_fail));
        }
    };
}
