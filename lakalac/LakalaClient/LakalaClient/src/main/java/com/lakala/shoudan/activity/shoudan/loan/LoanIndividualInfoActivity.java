package com.lakala.shoudan.activity.shoudan.loan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.base.CallCameraActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RepaymentPersonInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.PostPicCallback;
import com.lakala.shoudan.common.util.IMEUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.HintFocusChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息
 * 
 * @author ldm
 * 
 */
@SuppressLint("NewApi")
public class LoanIndividualInfoActivity extends CallCameraActivity implements
		OnClickListener {
    private RepaymentPersonInfo personInfo;
	private EditText applyName;// 申请人姓名
	private EditText id;// 身份证号
	private TextView city;// 城市
	private EditText address;// 住址
	private TextView education;// 最高学历
	private EditText email;// 电子邮箱
	private EditText contactName;// 联系人姓名
	private EditText contactMobileno;// 联系人手机号

	private ImageView photoFront;// 身份证正面
	private ImageView photoBack;// 身份证反面
	private ImageView newPhoto;// 身份证反面
	private TextView btnSure;
	private InputMethodManager imm;
	
	/** 拍照 */
	private final int REQ_CODE_CAMERA = 0;
	public static final int PHOTO_CLICK_START = 0;// 空
	public static final int PHOTO_1_CLICK_1 = 1;// 拍照
	public static final int PHOTO_2_CLICK_1 = 2; // 缩放
	public static final int PHOTO_1_CLICK_2 = 3;// 结果
	public static final int PHOTO_2_CLICK_2 = 4;// 结果
	public static final int PHOTO_3_CLICK_1 = 5;// 结果
	public static final int PHOTO_3_CLICK_2 = 6;// 结果

	/**
	 * 三态开关指示器（用于同源区分），数值只有0,1,2,3,4五种 （细分初态0，传递态1,2，终态3,4，态的转换一个循环，由初到终，再到原态）
	 * 0---->(1,2）----->（3,4）---->0 中间若有取消行为返为初态0
	 */
	private int clickIndex = PHOTO_CLICK_START;
	private boolean isOneModified = false;
	private boolean isAnotherModified = false;
	private String certfirstimg="";
	private String certsecondimg="";
	private String newphoto="";
	private LoanBackShowInfo backShowInfo;
	
	private String creditCardFromRemittaqnce = "";
	
//	 /** 照片缩略图   用于显示*/
//	protected Bitmap photo1;
//	protected Bitmap photo2;
//	protected Bitmap photo3;
	
    /** 身份证及头像照片原文件  将原图片保存在本地 */
    private String mFacadeImgFile;
    private String mObserveImgFile;
    private String mUserImgFile;
    private int mFiftyDp;
    private boolean isDialogOpen;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_individualinfo);
		initUI();
	}

    private HintFocusChangeListener hintListener = new HintFocusChangeListener();

	protected void initUI() {

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		navigationBar.setTitle(getString(R.string.myperson_info));

		// 申请人姓名
		applyName = (EditText) findViewById(R.id.applyname);
        applyName.setOnFocusChangeListener(hintListener);
		// 身份证号
		id = (EditText) findViewById(R.id.id);
        id.setOnFocusChangeListener(hintListener);
		// 城市
		city = (TextView) findViewById(R.id.city);
		city.setOnClickListener(this);
		findViewById(R.id.city_img).setOnClickListener(this);
		// 住址
		address = (EditText) findViewById(R.id.address);
        address.setOnFocusChangeListener(hintListener);
		// 最高学历
		education = (TextView) findViewById(R.id.education);
		education.setOnClickListener(this);
		findViewById(R.id.education_img).setOnClickListener(this);
		// 电子邮箱
		email = (EditText) findViewById(R.id.email);
        email.setOnFocusChangeListener(hintListener);
		// 联系人姓名
		contactName = (EditText) findViewById(R.id.contact_name);
        contactName.setOnFocusChangeListener(hintListener);
		// 联系人手机号
		contactMobileno = (EditText) findViewById(R.id.contact_mobileno);
        contactMobileno.setOnFocusChangeListener(hintListener);

		photoFront = (ImageView) findViewById(R.id.shoudan_id_photo_front);
		photoFront.setOnClickListener(this);
		photoBack = (ImageView) findViewById(R.id.shoudan_id_photo_back);
		photoBack.setOnClickListener(this);
		newPhoto = (ImageView) findViewById(R.id.newphoto);
		newPhoto.setOnClickListener(this);
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		// btnSure.setBtnTextRight();
		btnSure.setOnClickListener(this);
		findViewById(R.id.info).setOnClickListener(this);
		
		mFiftyDp = Util.dip2px(50, this);
		
		personInfo=new RepaymentPersonInfo();
		backShowInfo=(LoanBackShowInfo) getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO);
		if(backShowInfo.isPass()){
			initData();
		}
		
		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
	}
	/**
	 * 信息回显的初始化
	 */
	private void initData() {
		email.setText(backShowInfo.getEmail());
	}

	@Override
	public void onClick(View v) {
		IMEUtil.hideIme(this);
		switch (v.getId()) {
		case R.id.next:
            boolean isValid = isInputValid();
			if (isValid) {
				PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work1, context);
				startToWorkInfo();
			}
			break;
		case R.id.shoudan_id_photo_front:
//			clickIndex = PHOTO_1_CLICK_2;
//			mFacadeImgFile = createPhotoFile("facadePhoto"+ ApplicationEx.getInstance().getUser().getLoginName()+".jpg");
//			takePicture(mFacadeImgFile);
//			break;
		case R.id.shoudan_id_photo_back:
//			clickIndex = PHOTO_2_CLICK_2;
//			mObserveImgFile = createPhotoFile("observePhoto"+ApplicationEx.getInstance().getUser().getLoginName()+".jpg");
//			takePicture(mObserveImgFile);
//			break;
		case R.id.newphoto:
//			clickIndex = PHOTO_3_CLICK_2;
//			mUserImgFile = createPhotoFile("userPhoto"+ApplicationEx.getInstance().getUser().getLoginName()+".jpg");
//			takePicture(mUserImgFile);
            startActionCapture(v.getId());
			break;
		case R.id.info:
			//示例及说明
            LoanDialogCreator.showLoanSampleDialog(context);
			break;
		case R.id.city:
		case R.id.city_img:// 城市
			getProvice();
			break;
		case R.id.education:
		case R.id.education_img:// 最高学历
			showHighestLevel();
			break;

		default:
			break;
		}
	}
	
	/**
     * 创建照片缓存目录
     */
    private File createPhotoFile(String fileName) {
        String cacheDirPath = "";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            cacheDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator +
                    "lakala"+
                    File.separator +
                    "img";
        }else {
            cacheDirPath = context.getCacheDir().getAbsolutePath() +
                    File.separator +
                    "lakala"+
                    File.separator +
                    "img";
        }
        File dir = new File(cacheDirPath);
        if(!dir.exists()) dir.mkdirs();
        //localTempImgDir和localTempImageFileName是自己定义的名字
        return new File(dir, fileName);
    }

	/**
	 * 显示学历列表
	 */
	private void showHighestLevel() {
		final String levels[]=new String[]{"博士及以上","硕士","大学","大专","高中、中专及以下"};
        DialogCreator.createCancelableListDialog(context,"请选择最高学历",levels,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                education.setText(levels[which]);
                personInfo.setHighestlevel(String.valueOf(which+1));
                backShowInfo.setHighestlevel(String.valueOf(which+1));
            }
        }).show();
	}

	/**
	 * 下一步 个人信息入库
	 */
	private void startToWorkInfo() {
        PostPicCallback postCallback = new PostPicCallback() {
            @Override
            public void onCallback(boolean isPostSuccess) {
                if(isPostSuccess){
                    storagePersonInfo();
                }else{
                    hideProgressDialog();
                }
            }
        };
        showProgressWithNoMsg();
        postPicData(postCallback);
	}

	protected boolean isInputValid() {

		if (TextUtils.isEmpty(applyName.getText().toString().trim())) {
			ToastUtil.toast(context,"请输入申请人姓名");
			return false;
		}
		if(applyName.getText().toString().trim().length()<2 
				|| applyName.getText().toString().trim().length()>10){
            ToastUtil.toast(context,"请输入正确的申请人姓名");
			return false;
		}
		personInfo.setApplicantName(applyName.getText().toString().trim());
		backShowInfo.setApplicant(applyName.getText().toString().trim());
		if (TextUtils.isEmpty(id.getText().toString().trim())) {
            ToastUtil.toast(context,"请输入申请人身份证号码");
			return false;
		}
		if (!Util.checkIdCard(id.getText().toString().trim())) {
            ToastUtil.toast(context,R.string.id_no_input_error);
			return false;
		}
		personInfo.setCertno(id.getText().toString().trim());
		backShowInfo.setCertno(id.getText().toString().trim());
		if (TextUtils.isEmpty(city.getText().toString().trim())) {
            ToastUtil.toast(context,"请先选择申请人居住城市");
			return false;
		}
		if (TextUtils.isEmpty(address.getText().toString().trim())) {
            ToastUtil.toast(context,"请输入申请人住址");
			return false;
		}
		personInfo.setAddress(address.getText().toString().trim());
		backShowInfo.setAddress(address.getText().toString().trim());
		if (TextUtils.isEmpty(education.getText().toString().trim())) {
            ToastUtil.toast(context,"请先选择申请人最高学历");
			return false;
		}
//		personInfo.setHighestlevel(education.getText().toString().trim());
//		backShowInfo.setHighestlevel(education.getText().toString().trim());
		if (TextUtils.isEmpty(email.getText().toString().trim())) {
            ToastUtil.toast(context,"请输入申请人E-mail地址");
			return false;
		}
		if (!Util.checkEmailAddress(email.getText().toString().trim())) {
            ToastUtil.toast(context,"邮箱格式有误");
			return false;
		}
		personInfo.setEmail(email.getText().toString().trim());
		backShowInfo.setEmail(email.getText().toString().trim());
		
		// 照片上传与否验证
		if (!isOneModified || !isAnotherModified) {
            ToastUtil.toast(context,R.string.photo_not_commit);
			return false;
		}
		
		if(mUserImgFile==null){
            ToastUtil.toast(context,"请上传上半身照");
			return false;
		}
		
		if (TextUtils.isEmpty(contactName.getText().toString().trim())) {
            ToastUtil.toast(context,"请输入直系亲属姓名");
			return false;
		}
		if(contactName.getText().toString().trim().length() <2 ||
				contactName.getText().toString().trim().length()>10){
            ToastUtil.toast(context,"请输入正确的联系人姓名");
			return false;
		}
		backShowInfo.setContactname(contactName.getText().toString().trim());
		if (TextUtils.isEmpty(contactMobileno.getText().toString().trim())) {
            ToastUtil.toast(context,"请输入直系亲属手机号");
			return false;
		}
		if(!Util.checkPhoneNumber(contactMobileno.getText().toString().trim())){
            ToastUtil.toast(context,"电话号码格式错误");
			return false;
		}
		if(contactMobileno.getText().toString().trim().equals(ApplicationEx.getInstance().getUser().getLoginName())){
            ToastUtil.toast(context,"联系人手机号不可以和登录号相同");
			return false;
		}
		backShowInfo.setContactmobile(contactMobileno.getText().toString().trim());
		
		return true;
	}
    private boolean isInstalled(String action) {
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(action);
        //检索所有可用于给定的意图进行的活动。如果没有匹配的活动，则返回一个空列表。
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCameraRequestFailed() {
//        toast("无法调用相机");
    }

    @Override
    protected void onCameraRequestSucceed(int clickId, String picFilePath) {
        switch (clickId){
            case R.id.shoudan_id_photo_front:{
                mFacadeImgFile = picFilePath;
                UILUtils.displayBackground(picFilePath,photoFront);
                isOneModified = true;
                break;
            }
            case R.id.shoudan_id_photo_back:{
                mObserveImgFile = picFilePath;
                isAnotherModified = true;
                UILUtils.displayBackground(picFilePath,photoBack);
                break;
            }
            case R.id.newphoto:{
                mUserImgFile = picFilePath;
                UILUtils.displayBackground(picFilePath,newPhoto);
                break;
            }
        }
    }

    /**
	 * 提交照片文件
	 * 
	 * 返回提交照片是否成功 boolean
	 *
     * @param postPicCallback
     */
	private void postPicData(final PostPicCallback postPicCallback){
		ShoudanService upsm = ShoudanService.getInstance();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                boolean isPostSuccess = false;
                if (result.isRetCodeSuccess()) {
                    try {
                        isPostSuccess = true;
                        JSONObject object = new JSONObject(result.retData);
                        certfirstimg = object.getString("certfirstimg");
                        certsecondimg = object.getString("certsecondimg");
                        newphoto = object.getString("nowphoto");
                        personInfo.setCertfirstimg(certfirstimg);
                        personInfo.setCertsecondimg(certsecondimg);
                        personInfo.setNowphoto(newphoto);
                        backShowInfo.setCertfirstimg(certfirstimg);
                        backShowInfo.setCertsecondimg(certsecondimg);
                        backShowInfo.setNowphoto(newphoto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    isPostSuccess = false;
                    ToastUtil.toast(context,result.retMsg);
                }
                if(postPicCallback != null){
                    postPicCallback.onCallback(isPostSuccess);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
                if(postPicCallback != null){
                    postPicCallback.onCallback(false);
                }
            }
        };
        upsm.fileUpload(backShowInfo.getOrderno(), mFacadeImgFile,
                        mObserveImgFile, mUserImgFile,
                        callback
        );
	}
	
//    /**
//     * 获取图片字节流  生成宽为480 高为800 大小为100k 的图片
//     * @param file 文件
//     * @return  字节流
//     */
//    private byte[] getImgByte(File file) {
//        ByteArrayOutputStream out = ImageUtil.getThumbnailByte(
//                file.getAbsolutePath(),
//                600,
//                800,
//                300);
//        byte[] b = out.toByteArray();
//        //关闭流
//        if (out != null){
//            try {
//                out.close();
//            } catch (IOException e) {
//            }
//        }
//        return b;
//    }
//
	/**
	 * 个人信息入库
	 * @throws Exception
	 */
	private void storagePersonInfo(){
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    // 返回orderno
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String orderno = jsonObject.getString("orderno");
                        backShowInfo.setOrderno(orderno);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent=new Intent(LoanIndividualInfoActivity.this,LoanWorkInfoActivity.class);
                    intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO,backShowInfo);
                    intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                    startActivity(intent);
					PublicToEvent.LoanEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work, context);
                }else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance()
                      .personInfoStorage(backShowInfo.getOrderno(), personInfo,callback);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
	
	
	
	private List<RegionInfo> proviceList = new ArrayList<RegionInfo>();//省
	
	private RegionInfo currentProvice;
	private RegionInfo currentCity;
	private RegionInfo currentDistic;
	
	private List<RegionInfo> cityList = new ArrayList<RegionInfo>();//市
	private RegionInfoAdapter cityAdapter;
	
	private List<RegionInfo> disticList = new ArrayList<RegionInfo>();//地区
	private RegionInfoAdapter disticAdapter;
	
	private Spinner proviceSpinner;
	private Spinner citySpinner;
	private Spinner disticSppinner;
	private void initDialog(){
		View rootView = LayoutInflater.from(this).inflate(R.layout.spinner_dialog, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择城市信息");
		proviceSpinner = (Spinner) rootView.findViewById(R.id.province);
		proviceSpinner.setAdapter(new RegionInfoAdapter(LoanIndividualInfoActivity.this,proviceList,0));
		proviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
                RegionInfo province = proviceList.get(position);
                if(currentProvice != province){
                    currentProvice = province;
                    getCityListOfProvince(currentProvice);
                }
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		citySpinner = (Spinner) rootView.findViewById(R.id.city);
		cityAdapter = new RegionInfoAdapter(LoanIndividualInfoActivity.this, cityList,1);
		citySpinner.setAdapter(cityAdapter);
		citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
                RegionInfo newcity = cityList.get(position);
                if(currentCity != newcity){
					currentCity=newcity;
                    getDistrictListOfCity(currentCity);
                }
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		disticSppinner = (Spinner) rootView.findViewById(R.id.region);
		disticAdapter = new RegionInfoAdapter(LoanIndividualInfoActivity.this, disticList,2);
		disticSppinner.setAdapter(disticAdapter);
		disticSppinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				currentDistic = disticList.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});


		builder.setView(rootView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(currentProvice == null){
					toast("请选择省");
					return;
				}
				if(null == currentCity){
					toast("请选择市");
					return;
				}
				if(null == currentDistic){
					toast("请选择地区");
					return;
				}

				TextView tvArea = (TextView)findViewById(R.id.city);
                tvArea.setText(currentProvice.getpNm()+"-"+currentCity.getcNm()+"-"+currentDistic.getaNm());

				personInfo.setApcrnames(currentProvice.getpNm()+"|"+currentCity.getcNm()+"|"+currentDistic.getaNm());
				personInfo.setApcrcodes(currentProvice.getpId()+"|"+currentCity.getcId()+"|"+currentDistic.getaId());
				backShowInfo.setApcrnames(currentProvice.getpNm()+"|"+currentCity.getcNm()+"|"+currentDistic.getaNm());
				backShowInfo.setApcrcodes(currentProvice.getpId()+"|"+currentCity.getcId()+"|"+currentDistic.getaId());
                isDialogOpen = false;
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
                isDialogOpen = false;
			}
		});
        isDialogOpen = true;
		builder.setCancelable(false);
		builder.create();
		builder.show();
	}
	//一次性获取第一个省第一个市的所有地区
	private void getProvice(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    String emptyMsg = "暂无省信息";
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        List<RegionInfo> list = unpackRegions(jsonArray);
                        proviceList.clear();
                        if(list != null && list.size() != 0){
                            proviceList.addAll(list);
                            currentProvice = proviceList.get(0);
                            getCityListOfProvince(currentProvice);
                        }else{
                            ToastUtil.toast(context,emptyMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.toast(context,emptyMsg);
                    }
                }else{
                    hideProgressDialog();
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getProvinceList(callback);
    }
    private void getDistrictListOfCity(RegionInfo city){
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    List<RegionInfo> list = null;
                    try {
                        JSONArray disticsArray = new JSONArray(resultServices.retData);
                        list = unpackRegions(disticsArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    disticList.clear();
                    if(list != null && list.size() != 0){
                        disticList.addAll(list);
                        if(isDialogOpen && disticAdapter != null){
                            disticAdapter.notifyDataSetChanged();
                        }else {
                            initDialog();
                        }
                    }else{
                        ToastUtil.toast(context,"暂无县、地区信息");
                    }
                }else{
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getDistrictList(city.getcId(), callback);
    }

    private void getCityListOfProvince(RegionInfo provice){
        if(provice == null){
            return;
        }
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONArray cityArray = new JSONArray(resultServices.retData);
                        List<RegionInfo> list = unpackRegions(cityArray);
                        cityList.clear();
                        if(list != null && list.size() != 0){
                            cityList.addAll(list);
                            currentCity = cityList.get(0);
                            getDistrictListOfCity(currentCity);
                            if(isDialogOpen && cityAdapter != null){
                                cityAdapter.notifyDataSetChanged();
                            }
                        }else {
                            hideProgressDialog();
                            ToastUtil.toast(context,"暂无市级信息");
                        }
                    } catch (Exception e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }else{
                    hideProgressDialog();
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getCityListOfProvince(provice.getpId(), callback);
    }
	private List<RegionInfo> unpackRegions(JSONArray jsonArray)throws Exception{
		List<RegionInfo> regions = new ArrayList<RegionInfo>();
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonData = (JSONObject) jsonArray.get(i);
			RegionInfo region = new RegionInfo();
			region.setpNm(jsonData.optString("pNm"));
			region.setpId(jsonData.optString("pId"));
			region.setcId(jsonData.optString("cId"));
			region.setcNm(jsonData.optString("cNm"));			
			region.setaId(jsonData.optString("aId"));
			region.setaNm(jsonData.optString("aNm"));
			regions.add(region);
		}
		return regions;
	}
}
