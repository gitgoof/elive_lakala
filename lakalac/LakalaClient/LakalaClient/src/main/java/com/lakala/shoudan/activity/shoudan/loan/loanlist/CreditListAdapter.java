package com.lakala.shoudan.activity.shoudan.loan.loanlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.BaseActivity;
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
import com.lakala.platform.statistic.PayForYouEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.quickArrive.OnDayLoanClickListener;
import com.lakala.shoudan.activity.shoudan.loan.LoanTrailActivity;
import com.lakala.shoudan.activity.shoudan.loan.productdetail.CreditProductDetailActivity;
import com.lakala.shoudan.adapter.MyBaseAdapter;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class CreditListAdapter extends MyBaseAdapter<CreditListModel> {
    LocationManager locationManager = LocationManager.getInstance();
    private CreditListContract.Presenter presenter;
    AppBaseActivity mActivity;
    private String advertUrl="http://121.14.103.74:9091/credit-loan-app-shoukuanbao/tnh/application/goto/checkFace?reqData=T/Pme6AWCCx5E2N0R+Oz8hsD+E5faJWulM6hHX8BPwsJm+bKkZ8G1pYKYroxbq0iEVxcg6RMCClgUSsgWzGrv4P13qLSGuBIOH2iEr5jnFWlGjKf6STUzUK2NlzmQBYW";
    public CreditListAdapter(Context context,CreditListContract.Presenter mData,AppBaseActivity mActivity) {
        super(context, mData.getData());
        this.mActivity=mActivity;
        this.presenter=mData;
        UILUtils.init(context);
    }

    @Override
    public int getItemResource() {
        return R.layout.item_credit_list_new;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder viewHolder) {
        TextView next=viewHolder.getView(R.id.applay);
        LinearLayout top_ll=viewHolder.getView(R.id.top_ll);
        TextView tv_name=viewHolder.getView(R.id.tv_name);
        TextView tv_type=viewHolder.getView(R.id.tv_type);
        TextView tv_growth_rate=viewHolder.getView(R.id.tv_growth_rate);//可贷金额
        TextView tv_word=viewHolder.getView(R.id.tv_kashuabao);
//        TextView tv_1=viewHolder.getView(R.id.tv_applay_1);
//        TextView tv_2=viewHolder.getView(R.id.tv_applay_2);
//        TextView tv_3=viewHolder.getView(R.id.tv_applay_3);
//        TextView tv_4=viewHolder.getView(R.id.tv_applay_4);
//        TextView tv_5=viewHolder.getView(R.id.tv_applay_5);
//        LinearLayout tv1=viewHolder.getView(R.id.textView1);
//        LinearLayout tv2=viewHolder.getView(R.id.textView2);
//        LinearLayout tv3=viewHolder.getView(R.id.textView3);
//        LinearLayout tv4=viewHolder.getView(R.id.textView4);
        final ImageView iv_user=viewHolder.getView(R.id.iv_user);

        CreditListModel item= presenter.getData().get(position);
        String wordLabel=item.getWordLabel();
//        LinearLayout[] tvs={tv1,tv2,tv3,tv4};
//        TextView [] rvs={tv_1,tv_2,tv_3,tv_4};
//        setWordLabel(tvs,rvs,wordLabel,tv_5);
        if ("DK_DS".equals(item.getLoanMerId())){//功夫贷加载logo失败后加载默认图片
            UILUtils.loadWithError(item.getLoanLogo(), iv_user);
        }else {
            UILUtils.display(item.getLoanLogo(), iv_user);
        }
        tv_growth_rate.setText(item.getApplyDownLimit()+"～"+item.getApplyUpLimit());
        tv_type.setText(item.getLoanMerName());
        tv_name.setText(item.getLoanProName());
        tv_word.setText(item.getWordLabel());
        next.setOnClickListener(new ClickListner(viewHolder,item));
        top_ll.setOnClickListener(new ClickListner(viewHolder,item));
        return convertView;
    }

    private void setWordLabel(LinearLayout[] icons, TextView[] rvs, String wordLabel, TextView tv_5) {
        if (TextUtils.isEmpty(wordLabel)){
            return;
        }
        String[] label=wordLabel.split(",");
        if (label.length==0){
           return;
        }
        for (int i=0;i<4;i++){
            if (i<label.length-1){
                    rvs[i].setText(label[i]);
                    icons[i].setVisibility(View.VISIBLE);
            }else {
                rvs[i].setText("");
                icons[i].setVisibility(View.GONE);
            }
        }
        tv_5.setText(label[label.length-1]);
    }

    class  ClickListner implements View.OnClickListener {
        private ViewHolder viewHolder;
        private CreditListModel item;
        public  ClickListner(ViewHolder viewHolder, CreditListModel item){
            this.viewHolder=viewHolder;
            this.item=item;
        }
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.applay:
                    if ("DK_LKL".equals(item.getLoanMerId())){
                        ProtocalActivity.open(context, ProtocalType.LOAN_HELP);
                    }else if("DK_YRD".equals(item.getLoanMerId())){//一日贷
                        new OnDayLoanClickListener(mActivity).onClick(null);
                    }
                    else {
                        intent= new Intent(context,CreditProductDetailActivity.class);
                        intent.putExtra("datadetail",item);
                        context.startActivity(intent);
                        PublicToEvent.normalEvent(ShoudanStatisticManager.Loan_List_To_Product, context);
                    }
                    break;
                case R.id.top_ll:


                    if ("DK_LKL".equals(item.getLoanMerId())) {//替你还
                        PayForYouEnum.PayForYou.setLoan(true);
                        LoanTrailActivity.open((AppBaseActivity) context);

                    } else if("DK_YRD".equals(item.getLoanMerId())){//一日贷
                        new OnDayLoanClickListener(mActivity).onClick(null);
                    }else{

                        ((AppBaseActivity) context).showProgressWithNoMsg();
                        LocationManager.getInstance().setFirst2(false).startLocation(new LocationManager.LocationListener() {
                            @Override
                            public void onLocate() {

                                Locationing();

                                ((AppBaseActivity) context).hideProgressDialog();
                            }

                            @Override
                            public void onFailed() {

                                showDialog();
                                ((AppBaseActivity) context).hideProgressDialog();
                            }
                        });
                    }

                    break;
               /* case R.id.pay_for_you:
                    LoanTrailActivity.open((AppBaseActivity) mCotext);
                    break;*/
            }
        }

        private void Locationing() {
            AppOpsManager appOpsManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int checkResult = appOpsManager.checkOpNoThrow(
                        AppOpsManager.OPSTR_FINE_LOCATION, Binder.getCallingUid(), context.getPackageName());
                if(checkResult == AppOpsManager.MODE_IGNORED){
                    // TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
                    showDialog();
                } else if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                        (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                    if (!CreditListActivity.list_support_loan.contains(item.getLoanMerId())) {
                        AppUpgradeController.getInstance().setCtx((FragmentActivity) context).checkAppUpgradeLoan();
                        return;
                    }
                    if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                            (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                        // new KeplerUtil().enterKepler((AppBaseActivity) context);
                        initMeInfo(item);

                      /*if (CommonUtil.isMerchantCompleted((FragmentActivity) mCotext)){
                           presenter.applyCheck(mData.get(position));
                       }*/
                    }

                } else {
                    showDialog();
                }
            }else {
                if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                        (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                    if (!CreditListActivity.list_support_loan.contains(item.getLoanMerId())) {
                        AppUpgradeController.getInstance().setCtx((FragmentActivity) context).checkAppUpgradeLoan();
                        return;
                    }
                    if ((locationManager.getCity() != null && !"".equals(locationManager.getCity())) ||
                            (locationManager.getAddress() != null && !"".equals(locationManager.getAddress()))) {
                        // new KeplerUtil().enterKepler((AppBaseActivity) context);
                        initMeInfo(item);

                      /*if (CommonUtil.isMerchantCompleted((FragmentActivity) mCotext)){
                           presenter.applyCheck(mData.get(position));
                       }*/
                    }

                }
            }

//            else if ("DK_LKL".equals(item.getLoanMerId())) {
//                PayForYouEnum.PayForYou.setLoan(true);
//                LoanTrailActivity.open((AppBaseActivity) context);
//            }

        }

        ;
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

    /**
     * 更新商户信息
     * @param creditListModel
     */
    public void initMeInfo(final CreditListModel creditListModel){
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
                            presenter.applyCheck(creditListModel);
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
