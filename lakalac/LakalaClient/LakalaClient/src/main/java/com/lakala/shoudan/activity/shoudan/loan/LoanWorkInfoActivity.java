package com.lakala.shoudan.activity.shoudan.loan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RepaymentWorkInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.IMEUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.SpaceTextWatcher;
import com.lakala.shoudan.util.HintFocusChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作信息
 * 
 * @author ldm
 * 
 */
public class LoanWorkInfoActivity extends AppBaseActivity implements
		OnClickListener {

	private EditText companyName;// 单位名称
	private TextView city;// 城市
	private EditText address;// 地址
	private EditText phonenum;// 电话
	private EditText position;// 职位
	private TextView month_income;// 月收入
	private TextView btnSure;

	private InputMethodManager imm;
	
	private RepaymentWorkInfo workInfo;
	private LoanBackShowInfo backShowInfo;
	
	private String creditCardFromRemittaqnce = "";
    private HintFocusChangeListener hintListener = new HintFocusChangeListener();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_workinfo);
		initUI();
	}

	protected void initUI() {
		PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work_Bank_Info1, context);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		navigationBar.setTitle(getString(R.string.company_info));
		// 单位名称
		companyName = (EditText) findViewById(R.id.companyname);
		companyName.addTextChangedListener(new SpaceTextWatcher());//
        companyName.setOnFocusChangeListener(hintListener);
		// 城市
		city = (TextView) findViewById(R.id.city);
		city.setOnClickListener(this);
		findViewById(R.id.city_img).setOnClickListener(this);
		// 地址
		address = (EditText) findViewById(R.id.address);
		address.addTextChangedListener(new SpaceTextWatcher());
        address.setOnFocusChangeListener(hintListener);
		// 电话
		phonenum = (EditText) findViewById(R.id.phonenum);
		phonenum.addTextChangedListener(new SpaceTextWatcher());
		phonenum.setOnClickListener(this);
        phonenum.setOnFocusChangeListener(hintListener);
		// 职位
		position = (EditText) findViewById(R.id.position);
		position.addTextChangedListener(new SpaceTextWatcher());//
        position.setOnFocusChangeListener(hintListener);
		// 月收入
		month_income = (TextView) findViewById(R.id.month_income);
		month_income.setOnClickListener(this);
		findViewById(R.id.month_income_img).setOnClickListener(this);
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		// btnSure.setBtnTextRight();
		btnSure.setOnClickListener(this);
		
		workInfo=new RepaymentWorkInfo();
		backShowInfo=(LoanBackShowInfo) getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO);
		if(backShowInfo.isPass()){
			initData();
		}
		
		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
	}

	/**
	 * 信息回显
	 */
	private void initData() {
		companyName.setText(backShowInfo.getCompanyname());
		//地区信息回显
		city.setText(backShowInfo.getCpcrnames().replace("|", "-"));
		workInfo.setApcrnames(backShowInfo.getCpcrnames());
		workInfo.setApcrcodes(backShowInfo.getCpcrcodes());
		
		address.setText(backShowInfo.getCompanyaddress());
		phonenum.setText(backShowInfo.getCompanytel());
//		position.setText(backShowInfo.getPosition());
//		month_income.setText(backShowInfo.getIncome());
	}

	@Override
	public void onClick(View v) {
		IMEUtil.hideIme(this);
		switch (v.getId()) {
		case R.id.next:
			if (isInputValid()) {
				startToApply();
			}
			break;
		case R.id.month_income:
		case R.id.month_income_img://月收入
			showMonthlyIncome();
			break;
		case R.id.city:
		case R.id.city_img://获取工作所在城市信息
//			getChina();
			getProvice();
			break;

		default:
			break;
		}
	}
	
	private void showMonthlyIncome(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择月收入");
		final String levels[]=new String[]{"3000元以下","3000-6000元","6000-10000元","10000-15000元","15000-20000元","20000元以上"};
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				month_income.setText(levels[which]);
				workInfo.setIncome(String.valueOf(which+1));
				backShowInfo.setIncome(String.valueOf(which+1));
			}
		});
		builder.create().show();
	}

	/**
	 * 单位信息入库
	 */
	private void startToApply() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    //返回orderno
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String orderno = jsonObject.getString("orderno");
                        backShowInfo.setOrderno(orderno);
                        Intent intent=new Intent(LoanWorkInfoActivity.this,LoanMainActivity.class);
                        intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO, backShowInfo);
                        intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                        startActivity(intent);
						PublicToEvent.LoanEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Work_Bank_Info, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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
                      .workInfoStorage(backShowInfo.getOrderno(), workInfo,callback);
	}

	protected boolean isInputValid() {

		if(TextUtils.isEmpty(companyName.getText().toString().trim())){
			ToastUtil.toast(context,"请输入所在单位名称");
			return false;
		}
		if(companyName.getText().toString().trim().length()<2){
            ToastUtil.toast(context,"请输入正确的单位名称");
			return false;
		}
		workInfo.setCompanyname(companyName.getText().toString().trim());
		backShowInfo.setCompanyname(companyName.getText().toString().trim());
		if(TextUtils.isEmpty(city.getText().toString().trim())){
            ToastUtil.toast(context,"请先选择单位所在城市");
			return false;
		}
		if(TextUtils.isEmpty(address.getText().toString().trim())){
            ToastUtil.toast(context,"请输入单位地址");
			return false;
		}
		workInfo.setCompanyaddress(address.getText().toString().trim());
		backShowInfo.setCompanyaddress(address.getText().toString().trim());
		if(TextUtils.isEmpty(phonenum.getText().toString().trim())){
            ToastUtil.toast(context,"请输入单位电话");
			return false;
		}
		if(phonenum.getText().toString().trim().length()>25){
            ToastUtil.toast(context,"请输入正确的座机号码");
			return false;
		}
		if(phonenum.getText().toString().trim().equals(ApplicationEx.getInstance().getUser().getLoginName())){
            ToastUtil.toast(context,"单位号码不可以和登录手机号相同");
			return false;
		}
		
		//不是座机，不是手机
		String phoneNum = phonenum.getText().toString().trim();
		if(!Util.checkPhoneNumber(phoneNum) && !Util.isPhoneNumberValid(phoneNum)){
            ToastUtil.toast(context,"电话格式有误");
			return false;
		}
		workInfo.setCompanytel(phonenum.getText().toString().trim());
		backShowInfo.setCompanytel(phonenum.getText().toString().trim());
		if(TextUtils.isEmpty(position.getText().toString().trim())){
            ToastUtil.toast(context,"请输入申请人职位名称");
			return false;
		}
		backShowInfo.setPosition(position.getText().toString().trim());
		workInfo.setPosition(position.getText().toString().trim());
		if(TextUtils.isEmpty(month_income.getText().toString().trim())){
            ToastUtil.toast(context,"请先选择收入范围");
			return false;
		}
//		workInfo.setIncome(month_income.getText().toString().trim());
//		backShowInfo.setIncome(month_income.getText().toString().trim());
		//亲属信息由个人信息界面得到
		workInfo.setContactname(backShowInfo.getContactname());
		workInfo.setContactmobile(backShowInfo.getContactmobile());
		return true;
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
    private boolean isDialogOpen = false;
	private void initDialog(){
		View rootView = LayoutInflater.from(this).inflate(R.layout.spinner_dialog,null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择城市信息");
		proviceSpinner = (Spinner) rootView.findViewById(R.id.province);
		proviceSpinner.setAdapter(new RegionInfoAdapter(LoanWorkInfoActivity.this,proviceList,0));
		proviceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
		cityAdapter = new RegionInfoAdapter(LoanWorkInfoActivity.this, cityList,1);
		citySpinner.setAdapter(cityAdapter);
		citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
                RegionInfo newcity = cityList.get(position);
                if(currentCity != newcity){
                    currentCity = newcity;
                    getDistrictListOfCity(currentCity);
                }
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		disticSppinner = (Spinner) rootView.findViewById(R.id.region);
		disticAdapter = new RegionInfoAdapter(LoanWorkInfoActivity.this, disticList,2);
		disticSppinner.setAdapter(disticAdapter);
		disticSppinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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

				workInfo.setApcrnames(currentProvice.getpNm()+"|"+currentCity.getcNm()+"|"+currentDistic.getaNm());
				workInfo.setApcrcodes(currentProvice.getpId()+"|"+currentCity.getcId()+"|"+currentDistic.getaId());
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
                            if(isDialogOpen && cityArray != null){
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
