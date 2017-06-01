package com.lakala.shoudan.activity.shoudan.loan.productdetail;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListActivity;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListModel;
import com.lakala.shoudan.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.paem.iloanlib.api.SDKExternalAPI;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 产品详情
 */
public class CreditProductDetailActivity extends AppBaseActivity implements CreditProductDetailContract.CreditBusinessView {
    @Bind(R.id.iv_user)
    ImageView ivUser;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.tv_1)
    TextView tv1;
   /* @Bind(R.id.tv_2)
    TextView tv2;
    @Bind(R.id.tv_3)
    TextView tv3;
    @Bind(R.id.tv_4)
    TextView tv4;*/
    @Bind(R.id.tv_loan_rate)
    TextView tvLoanRate;
    @Bind(R.id.tv_loan_income)
    TextView tvLoanIncome;
    @Bind(R.id.tv_loan_perid)
    TextView tvLoanPerid;
    @Bind(R.id.tv_introduce)
    TextView tvIntroduce;
    @Bind(R.id.condition_list)
    TextView conditionList;
    @Bind(R.id.iv_data)
    TextView ivData;
    @Bind(R.id.auditDescription)
    TextView auditDescription;
    @Bind(R.id.per_teamff)
    TextView perTeamff;
    @Bind(R.id.tions)
    ImageView tions;
    @Bind(R.id.onUp)
    LinearLayout onUp;
    private CreditProductDetailContract.Presenter presenter;
    private boolean toUp = false;
    private CreditListModel data;
    LocationManager locationManager = LocationManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditNum.isCreat.setLisDetail(true);
        setContentView(R.layout.activity_credit_product_detail);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        navigationBar.setTitle("产品详情");
        super.initUI();
        presenter = new CreditProductDetailPresenter(this);
        data= (CreditListModel) getIntent().getSerializableExtra("datadetail");
        initDetail(data);
        presenter.setShowIntroduce(onUp,tvIntroduce);
    }

    private void initDetail(CreditListModel data) {
        UILUtils.init(this);
        ImageLoader.getInstance().displayImage(data.getLoanLogo(),ivUser);
        ivData.setText(data.getApplyProcess());
        tvName.setText(data.getLoanProName());
        tvType.setText(data.getLoanMerName());
        tvLoanRate.setText(data.getApplyDownLimit()+"～"+data.getApplyUpLimit());
        tvLoanIncome.setText(data.getRateLimit());
        tvLoanPerid.setText(data.getLoanPeriod());
        tvIntroduce.setText(data.getProIntroduction());
        conditionList.setText(data.getApplyConditions());
        auditDescription.setText(data.getAuditDescription());
        if ("DK_DS".equals(data.getLoanMerId())){
            tv1.setText("热搜产品,信用卡贷,利率低,放款快");
        }else if ("DK_51".equals(data.getLoanMerId())){
            tv1.setText("纯信用,纯线上,无抵押,高额度");
        }else if("DK_YFQ".equals(data.getLoanMerId())){
            tv1.setText("放款快,手续简,现金贷,无抵押");
        }else if("DK_PA".equals(data.getLoanMerId())){
            tv1.setText("纯线上,无抵押,放款快");
        }else if("DK_TNH".equals(data.getLoanMerId())){
            tv1.setText("");
        }
       // tv1.setText(data.getWordLabel());
        //presenter.setWordLabel(tv1,tv2,tv3,tv4,data.getWordLabel());
    }
    @Override
    public void setPresenter(CreditProductDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showToast(String str) {
        ToastUtil.toast(context, str);
    }

    @Override
    public void showLoading() {
        showProgressWithNoMsg();
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }

    public void OnClicks(View v) {
        switch (v.getId()) {
            case R.id.onUp:
                presenter.setIntroduce(tvIntroduce, toUp);
                break;
            case R.id.nextx:
                ((AppBaseActivity) context).showProgressWithNoMsg();
                LocationManager.getInstance().setFirst2(false).startLocation(new LocationManager.LocationListener() {
                    @Override
                    public void onLocate() {
                        locate();
                        ((AppBaseActivity) context).hideProgressDialog();
                    }

                    @Override
                    public void onFailed() {
                        showDialog();
                        ((AppBaseActivity) context).hideProgressDialog();
                    }
                });

                break;
        }
    }

    private void locate() {
        AppOpsManager appOpsManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int checkResult = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_FINE_LOCATION, Binder.getCallingUid(), context.getPackageName());
            if (checkResult == AppOpsManager.MODE_IGNORED) {
                // TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
                showDialog();
            } else if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                    (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                if (!CreditListActivity.list_support_loan.contains(data.getLoanMerId())) {
                    AppUpgradeController.getInstance().setCtx((FragmentActivity) context).checkAppUpgradeLoan();
                    return;
                }
                initMeInfo(data);

            } else {
                showDialog();
            }
        }else {
            if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                    (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                if (!CreditListActivity.list_support_loan.contains(data.getLoanMerId())) {
                    AppUpgradeController.getInstance().setCtx((FragmentActivity) context).checkAppUpgradeLoan();
                    return;
                }
                initMeInfo(data);

            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View title_layout=View.inflate(context,R.layout.dialog_location_title,null);
        builder.setMessage("申请此贷款业务需要您打开位置信息访问权限");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAppDetailSettingIntent(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        builder.setCustomTitle(title_layout);
        builder.create().show();

    }

    private void getAppDetailSettingIntent(Context context) {
        Uri packageURI = Uri.parse("package:" + "com.lakala.shoudan");
        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        CreditNum.isCreat.setLisDetail(false);
        if(!CreditNum.isCreat.isLisDetail()&&!CreditNum.isCreat.isList()){
            SDKExternalAPI.getInstance().destroy();
        }
        super.onDestroy();
    }

    @Override
    public void setDown() {
        toUp = false;
        perTeamff.setText("展开");
        tions.setImageResource(R.drawable.arrow_down);
    }

    @Override
    public void setUp() {
        toUp = true;
        perTeamff.setText("收起");
        tions.setImageResource(R.drawable.arrow_up);
    }

    @Override
    public void showApplay(CreditListModel model) {
        presenter.showApplay(model);
    }

    //更新商户信息
    public void initMeInfo(final CreditListModel loanMerId){
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,"v1.0/getMerchantInfo", HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if(resultServices.isRetCodeSuccess()){
                    try {
                        User user = ApplicationEx.getInstance().getSession().getUser();
                        JSONObject data = new JSONObject(resultServices.retData);
                        user.initMerchantAttrWithJson(data);
                        ApplicationEx.getInstance().getUser().save();
                        if (CommonUtil.isMerchantCompleted((FragmentActivity)context)) {
                            presenter.applay(loanMerId);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(context,"数据解析异常");
                    }
                }else{
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }
            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
}
