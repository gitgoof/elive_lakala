package com.lakala.shoudan.activity.merchant.upgrade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.base.CallCameraActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.PostPicCallback;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;
import com.sina.weibo.sdk.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//商户升档--我要升档--信息提交页
/**
 * 商户升级
 * 一类升级
 * @author ZhangMY
 *
 */
public class UpgradeResultActivity extends CallCameraActivity implements
		View.OnClickListener {

    private Button upload;

	private AccountType index = AccountType.PRIVATE;// 记录当前选项卡

	// 个人用户
	private ImageView idPhoto_Front;// 身份证正面
	private ImageView idPhoto_Back;// 身份证反面

	private ImageView individual_Photo;// 本人持证照照片
	// 企业用户
	private ImageView companyPhoto_Front;// 身份证正面
	private ImageView companyPhoto_Back;// 身份证反面

	private ImageView open_Photo;// 营业执照
	private ImageView mtou_Phoho;// 门头照片

	private ImageView zuli_Photo;// 租赁照片
    private TextView addIdHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_upgrade);
		initUI();
	}

	protected void initUI() {
        addIdHint=(TextView)findViewById(R.id.add_id_hint);
        addIdHint.setOnClickListener(this);
		navigationBar.setTitle("商户升级");
		upload = (Button) findViewById(R.id.upload);
		upload.setOnClickListener(this);
		// 个人身份证
		idPhoto_Front = (ImageView) findViewById(R.id.individual_id_photo_front);
		idPhoto_Back = (ImageView) findViewById(R.id.individual_id_photo_back);
		idPhoto_Back.setOnClickListener(this);
		idPhoto_Front.setOnClickListener(this);
		// 个人
		individual_Photo = (ImageView) findViewById(R.id.individual_photo);
		individual_Photo.setOnClickListener(this);
		// 企业
		companyPhoto_Front = (ImageView) findViewById(R.id.company_id_photo_front);
		companyPhoto_Back = (ImageView) findViewById(R.id.company_id_photo_back);
		companyPhoto_Back.setOnClickListener(this);
		companyPhoto_Front.setOnClickListener(this);
		mtou_Phoho = (ImageView) findViewById(R.id.mtou_phoho);
		mtou_Phoho.setOnClickListener(this);
		open_Photo = (ImageView) findViewById(R.id.open_photo);
		open_Photo.setOnClickListener(this);
		zuli_Photo = (ImageView) findViewById(R.id.zuli_photo);
		zuli_Photo.setOnClickListener(this);

        if (ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountType() == AccountType.PUBLIC) {//企业
            navigationBar.setTitle("企业商户升级");
            toCompany();
        } else {
            navigationBar.setTitle("个人商户升级");
            toIndividual();
        }

        //对私
        errTips.put(R.id.individual_photo,"请上传本人持证照");
        //对公
        errTips.put(R.id.open_photo, "请上传营业执照");
        errTips.put(R.id.mtou_phoho, "请上传门头照片");
        errTips.put(R.id.zuli_photo, "请上传营业场所产权证明或租赁证明");
        setDefaultMap();

        modifyModeInit();
	}

    private void modifyModeInit(){

        MerchantInfo.UserEntity.IdCardInfoEntity idCardInfoEntity = merchantInfo.getUser().getIdCardInfo();

        UILUtils.getIdcardImg(idCardInfoEntity.getPicPath1(), idPhoto_Front);
        UILUtils.getIdcardImg(idCardInfoEntity.getPicPath2(), idPhoto_Back);


    }

    private void setDefaultMap(){

        uploadMap.put(R.id.open_photo, new PicUploadBean());
        uploadMap.put(R.id.mtou_phoho, new PicUploadBean());
        uploadMap.put(R.id.zuli_photo, new PicUploadBean());

    }

    private Map<Integer,PicUploadBean> uploadMap = new HashMap<>();




	private void toIndividual() {
		index = AccountType.PRIVATE;
		idPhoto_Front.setClickable(false);
		idPhoto_Back.setClickable(false);
		
		findViewById(R.id.individual).setVisibility(View.VISIBLE);
		findViewById(R.id.individual_show).setVisibility(View.VISIBLE);
		//企业
		findViewById(R.id.company).setVisibility(View.GONE);
		findViewById(R.id.zuli_show).setVisibility(View.GONE);
		findViewById(R.id.mtou_show).setVisibility(View.GONE);
	}

	private void toCompany() {
		index = AccountType.PUBLIC;
		
		companyPhoto_Back.setClickable(false);
		companyPhoto_Front.setClickable(false);
		
		
		findViewById(R.id.company).setVisibility(View.VISIBLE);
		findViewById(R.id.mtou_show).setVisibility(View.VISIBLE);
		findViewById(R.id.zuli_show).setVisibility(View.VISIBLE);
		//个人
		findViewById(R.id.individual).setVisibility(View.GONE);
		findViewById(R.id.individual_show).setVisibility(View.GONE);


        UILUtils.getIdcardImg(merchantInfo.getUser().getIdCardInfo().getPicPath1(), companyPhoto_Front);// 身份证正面
        UILUtils.getIdcardImg(merchantInfo.getUser().getIdCardInfo().getPicPath2(), companyPhoto_Back);// 身份证反面

	}

    private MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upload:
			// 这里调用ShoudanService里的merChantUpgrade和idCardImageUpload方法
			// 上传图片和商户升档申请 返回的文件名保存，以备之后调用参数
			if (isInputValid()) {
				startUpgradeThread();
			}
			break;
        case R.id.add_id_hint:
		case R.id.individual_photo:
			// 本人持证照
		case R.id.zuli_photo:
		case R.id.mtou_phoho:
		case R.id.open_photo:
			// 营业执照
            showCallCameraPopWindow(v.getId(), v.getRootView());
            break;
		default:
			break;
		}
	}

	private void startUpgradeThread() {
        showProgressDialog(R.string.committing_register_info);

        if(index == AccountType.PRIVATE){

            postPicDataPri();
            
        }else {

            startCompanyUpgrade();
            
        }
    }
    
    

    ServiceResultCallback callback = new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {
            if (resultServices.isRetCodeSuccess()) {// 成功

                toast("提交成功，请等待审核");
                callMainActivity();
                finish();
            }else{
                toast(resultServices.retMsg);
            }
        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            hideProgressDialog();
            toast("上传图片失败");
            LogUtil.print(connectEvent.getDescribe());
        }
    };

    private String individualPicPath= "";

	protected void postPicDataPri() {

        ShoudanService.getInstance().upgradeUpload(individualPicPath, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        String msg = new JSONObject(resultServices.retData).optString("fileName");
                        ShoudanService.getInstance().merChantUpgradeIndividual(msg, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgressDialog();
            }
        });
	}
    

    private Hashtable<Integer,String> errTips = new Hashtable<>();

	protected boolean isInputValid() {

		if (index == AccountType.PRIVATE) {
			// 个人用户
            if(TextUtils.isEmpty(individualPicPath)){

                toast("请上传本人持证照");
                return false;
            }

		} else {

            if(!uploadMap.get(R.id.open_photo).hasPicked()){
                toast(errTips.get(R.id.open_photo));
                return false;
            }

            if(!uploadMap.get(R.id.mtou_phoho).hasPicked()){
                toast(errTips.get(R.id.mtou_phoho));
                return false;
            }

            if(!uploadMap.get(R.id.zuli_photo).hasPicked()){
                toast(errTips.get(R.id.zuli_photo));
                return false;
            }


		}
		return true;
	}


    @Override
    protected void onCameraRequestFailed() {
        //Seems to do nothings
    }
    
    
    @Override
    protected void onCameraRequestSucceed(int clickId, String picFilePath) {
        if(clickId == R.id.individual_photo){
            individualPicPath = picFilePath;

        }else{
            uploadMap.put(clickId, new PicUploadBean(picFilePath, ""));
        }

        switch (clickId){
            case R.id.add_id_hint:
                clickId=R.id.individual_photo;
        }
        UILUtils.displayWithoutCache(picFilePath, (ImageView)findViewById(clickId));
        if(individual_Photo.getDrawable()!=null){
            individual_Photo.setVisibility(View.VISIBLE);
            addIdHint.setVisibility(View.GONE);
        }else{
            individual_Photo.setVisibility(View.INVISIBLE);
            addIdHint.setVisibility(View.VISIBLE);
        }

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

    private void startCompanyUpgrade() {

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
                toastInternetError();
            }
        };

        ShoudanService.getInstance().merChantUpgradeCompany(uploadMap.get(R.id.open_photo).getUploadedFile(), uploadMap.get(R.id.mtou_phoho).getUploadedFile(), uploadMap.get(R.id.zuli_photo).getUploadedFile(), callback);
    }


    protected void postPicData(final PostPicCallback postPicCallback) {

        final Iterator<Integer> uploadMapKeys = uploadMap.keySet().iterator();

        final MerchantUpgradePicUploadListener merchantUpgradePicUploadListener = new MerchantUpgradePicUploadListener() {
            @Override
            public void onSucceed(PicUploadBean picBean) {

                if(uploadMapKeys.hasNext()){

                    int uploadId = 0;
                    do{
                        uploadId = uploadMapKeys.next();
                    }while (uploadMap.get(uploadId).hasUploaded());

                    upload(uploadMap.get(uploadId), this);
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
        if (uploadMapKeys.hasNext()){

            do{
                uploadId = uploadMapKeys.next();
            }while(uploadMap.get(uploadId).hasUploaded());
        }

        upload(uploadMap.get(uploadId), merchantUpgradePicUploadListener);


    }
    
    
    
}
