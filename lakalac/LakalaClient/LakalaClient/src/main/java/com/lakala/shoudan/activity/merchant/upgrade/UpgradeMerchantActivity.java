package com.lakala.shoudan.activity.merchant.upgrade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.PostPicCallback;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//商户升档--我要成为商户--信息提交页
/**
 * 商户升档 A3
 * @author ZhangMY
 * @modifier More date 2015-12-01
 */
@Deprecated
public class UpgradeMerchantActivity extends AppBaseActivity implements
		View.OnClickListener {
	private Button upload;

	/** 拍照 */
	private final int REQ_CODE_CAMERA = 0;
	/** 相册 */
	private final int REQ_CODE_PICTURE = 1;
	
	private int clickIndex = 0;

	public static final String IMAGE_UNSPECIFIED = "image/*";

	private ImageView individual_Photo;// 本人持证照照片
	private ImageView open_Photo;// 营业执照
	private ImageView mtou_Phoho;// 门头照片
	private ImageView zuli_Photo;// 租赁照片
	private ImageView store_Photo;// 店内照片

	private EditText openbank_username;//开户姓名
	private EditText openbank_no;//银行账号
	private TextView openBankName;//银行
	private TextView openBankBarnch;//支行
	
	public static final int QUERY_BANK_BRANCH_RESULT = 0x330;

	private String bankBranchCode ;
	private String bankBranchName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_upgrade_input);
		initUI();
	}

	protected void initUI() {
		navigationBar.setTitle("升档");
		upload = (Button) findViewById(R.id.btn_next);
		upload.setOnClickListener(this);
		upload.setText("提交");
		
		// 个人身份证
        uploadTable.put(R.id.shoudan_id_photo_front, new PicUploadBean("fackeData", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath1()));
        uploadTable.put(R.id.shoudan_id_photo_back, new PicUploadBean("fackeData", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath2()));
        uploadTable.put(R.id.individual_photo, new PicUploadBean());
        uploadTable.put(R.id.zuli_photo, new PicUploadBean());
        uploadTable.put(R.id.mtou_phoho, new PicUploadBean());
        uploadTable.put(R.id.open_photo, new PicUploadBean());
        uploadTable.put(R.id.store_photo, new PicUploadBean());

		// 个人

		individual_Photo = (ImageView) findViewById(R.id.individual_photo);
		individual_Photo.setOnClickListener(this);
		mtou_Phoho = (ImageView) findViewById(R.id.mtou_phoho);
		mtou_Phoho.setOnClickListener(this);
		open_Photo = (ImageView) findViewById(R.id.open_photo);
		open_Photo.setOnClickListener(this);
		zuli_Photo = (ImageView) findViewById(R.id.zuli_photo);
		zuli_Photo.setOnClickListener(this);
		store_Photo = (ImageView) findViewById(R.id.store_photo);
		store_Photo.setOnClickListener(this);
		
		//开户姓名 开户银行
		openbank_username = (EditText)findViewById(R.id.account_name);
		openbank_no = (EditText)findViewById(R.id.bank_account);
		openbank_no.setEnabled(false);
		openbank_username.setEnabled(false);
		
		openBankName = (TextView) findViewById(R.id.bank);//开户银行
		openBankBarnch = (TextView) findViewById(R.id.tv_bank_branch);//支行
		
		openBankBarnch.setOnClickListener(this);
		findViewById(R.id.img_area_more_barnch).setOnClickListener(this);
		
		openbank_username.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());//姓名
		openbank_no.setText(Util.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo()));//银行卡号
		openBankName.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());//银行名称

		//获取身份证照片高度，宽度
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.btn_upload_idcard_face_down);

		modifyModeInit();

	}
	
	private void modifyModeInit(){
    	//接收bitmap数据
        if (getIntent() != null) {
            //TODO 将身份证中的照片load到身份证ImageView中
        }

	}



	/**
	 * 获取照片-----从用户相册中获取或拍照获取
	 */
	private void takePhotos(final int clickID) {

		CharSequence[] items = { getString(R.string.photo_album),
				getString(R.string.take_photo), getString(R.string.cancel) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.select_photo_title));
		builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                clickIndex = clickID;
                //TODO
                switch (which) {
                    case 0:// 从用户相册中获取照片
                        Intent intent0 = new Intent(Intent.ACTION_PICK, null);
                        intent0.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                IMAGE_UNSPECIFIED);
                        startActivityForResult(intent0, REQ_CODE_PICTURE);

                        break;
                    case 1:// 拍照获取用户照片
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQ_CODE_CAMERA);
                        break;
                }

            }
        });
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			// 这里调用ShoudanService里的merChantUpgrade和idCardImageUpload方法
			// 上传图片和商户升档申请 返回的文件名保存，以备之后调用参数
			if (isInputValid()) {
				startUpgradeThread();
			}
			break;

		case R.id.img_area_more_barnch://支行
		case R.id.tv_bank_branch:
            //TODO
//			Intent intent = new Intent(UpgradeMerchantActivity.this,BankBranchActivity.class);
//			intent.putExtra(BankBranchActivity.BANK_NAME,ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
//			intent.putExtra(BankBranchActivity.BANK_CODE, Parameters.merchantInfo.getBankNo());
//
//			startActivityForResult(intent, QUERY_BANK_BRANCH_RESULT);
			break;
		default:
            takePhotos(v.getId());
			break;
		}
	}

	private void startUpgradeThread() {

        showProgressDialog(R.string.committing_register_info);
        postPicData(new PostPicCallback() {
            @Override
            public void onCallback(boolean isPostSuccess) {
                if (isPostSuccess) {
                    startUpgrade();
                } else {
                    ToastUtil.toast(context, "上传图片失败");
                    hideProgressDialog();
                }
            }
        });
	}

    protected void startUpgrade() {
        // 升级接口调用
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {// 成功
                    //调回首页
                    ToastUtil.toast(context, "提交成功，请等待审核");
                    finish();
                } else {
                    toast(resultServices.retMsg);
                }

                hideProgressDialog();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                hideProgressDialog();
                toast("上传图片失败");
            }
        };

        List<String> uploadFileList = new ArrayList<>();

        for(int key : uploadTable.keySet()){

            uploadFileList.add(uploadTable.get(key).getUploadedFile());

        }

        ShoudanService.getInstance().UpgradeMerchant(uploadFileList, bankBranchCode, bankBranchName, callback);
    }


	protected void postPicData(final PostPicCallback postPicCallback) {

        final Iterator<Integer> uploadTableKeys = uploadTable.keySet().iterator();

        final MerchantUpgradePicUploadListener merchantUpgradePicUploadListener = new MerchantUpgradePicUploadListener() {
            @Override
            public void onSucceed(PicUploadBean picBean) {

                if(uploadTableKeys.hasNext()){

                    int uploadId = 0;
                    do{
                        uploadId = uploadTableKeys.next();
                    }while (uploadTable.get(uploadId).hasUploaded());

                    upload(uploadTable.get(uploadId), this);
                }else{
                    postPicCallback.onCallback(true);
                }

            }

            @Override
            public void onFailed(String msg) {

                postPicCallback.onCallback(false);

            }
        };

        int uploadId = 0;
        if (uploadTableKeys.hasNext()){

            do{
                uploadId = uploadTableKeys.next();
            }while(uploadTable.get(uploadId).hasUploaded());
        }

        upload(uploadTable.get(uploadId), merchantUpgradePicUploadListener);


	}

    //存放上传照片的路径管理table
    private Hashtable<Integer,PicUploadBean> uploadTable = new Hashtable<>();


    //错误提示代码
    private static Map<Integer, String> errMap = new HashMap<>();

    static {
        errMap.put(R.id.individual_photo, "请上传手执证件照");
        errMap.put(R.id.zuli_photo, "请上传店铺租赁合同");
        errMap.put(R.id.mtou_phoho, "请上传店铺门头照");
        errMap.put(R.id.open_photo, "请上传营业执照");
        errMap.put(R.id.store_photo, "请上传店内照");

    }

	protected boolean isInputValid() {

        for(int key :uploadTable.keySet()){
            if(!uploadTable.get(key).hasPicked()){
                toast(errMap.get(key));
                return false;
            }
        }

        if (TextUtils.isEmpty(openBankName.getText().toString().trim())) {
            toast("请选择银行");
            return false;
        }

        if (TextUtils.isEmpty(openBankBarnch.getText().toString().trim())) {
            toast("请选择支行信息");
            return false;
        }

        if (TextUtils.isEmpty(openbank_no.getText().toString().trim())) {
            toast("请输入银行卡号");
            return false;
        }
        if (TextUtils.isEmpty(openbank_username.getText().toString().trim())) {
            toast("请输入户名");
            return false;
        }
        return true;
    }

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        //支行选择
        if (requestCode == QUERY_BANK_BRANCH_RESULT) {
            Bundle bankBranchBundle = data.getExtras();
            String proviceName = bankBranchBundle.getString("provice_name");//省
            String proviceCode = bankBranchBundle.getString("provice_code");
            String areaName = bankBranchBundle.getString("area_name");//地区
            String areaCode = bankBranchBundle.getString("area_code");
            String bankBranchName = bankBranchBundle.getString("bank_branch_name");//支行
            String bankBranchCode = bankBranchBundle.getString("bank_branch_code");
            this.bankBranchCode = bankBranchCode;
            this.bankBranchName = bankBranchName;
            openBankBarnch.setText(bankBranchName);
            return;
        }

        PicUploadBean picUploadBean = new PicUploadBean();
        switch (requestCode) {
            case REQ_CODE_CAMERA:// 拍照完成后调整到图片选择界面
                //TODO
                break;
            case REQ_CODE_PICTURE:// 图片选择界面，选择上传的图片
                //TODO
                break;
        }
        uploadTable.put(clickIndex, picUploadBean);

        //银行卡信息
//			if (requestCode == REQUEST_OPENBANK_LIST) {
//				mOpenBankInfo = (OpenBankInfo) data.getSerializableExtra("openBankInfo");
//			}

    }


    private interface MerchantUpgradePicUploadListener {
        void onSucceed(PicUploadBean picUploadBean);
        void onFailed(String msg);
    }

    //提交上送的照片, 将返回的服务端照片地址set到picBean中去
    private void upload(final PicUploadBean picUploadBean, final MerchantUpgradePicUploadListener merchantUpgradePicUploadListener){

        ShoudanService.getInstance().upgradeUpload(picUploadBean.getLocalFile(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if(resultServices.isRetCodeSuccess()){

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        picUploadBean.setUploadedFile(jsonObject.optString("fileName"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    merchantUpgradePicUploadListener.onSucceed(picUploadBean);

                }else{
                    merchantUpgradePicUploadListener.onFailed(resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                merchantUpgradePicUploadListener.onFailed(getString(R.string.socket_error));
            }
        });

    }


}
