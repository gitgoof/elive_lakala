package com.lakala.shoudan.activity.main;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.DateUtil;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.util.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LMQ on 2015/12/14.
 */
public class DrawerFragment extends BaseFragment {
    private TextView tvDate;
    private TextView tvAmount;
    private TextView layoutToDraw;
    private boolean isCollecting;
    private Double todayAmount;
    private View tvUnit;
    private boolean updateOnResume = true;
    private TextView tv_name;
    private TextView tv_name_content;
    private TextView tv_open;
    private ImageView iv_name;
    private LinearLayout.LayoutParams params2;
    private View ll_content;
    private Map<String,Integer> map_ic=new HashMap<>();
    private Map<String,String> map_na=new HashMap<>();
    private boolean isDown=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_drawer,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUnit = view.findViewById(R.id.tv_label_unit);
        tvDate = (TextView)view.findViewById(R.id.tv_date);
        tvAmount = (TextView)view.findViewById(R.id.tv_amount);
        tv_name= (TextView) view.findViewById(R.id.tv_name);
        iv_name= (ImageView) view.findViewById(R.id.iv_name);
        tv_name_content= (TextView) view.findViewById(R.id.tv_name_content);
        tv_open= (TextView) view.findViewById(R.id.tv_open);
        Point amountSize = getViewSize(tvAmount);
        int sizePx = (int)getResources().getDimension(R.dimen.font_size_xlarge)-6;
        int spacing = (amountSize.y-sizePx)/2;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tvUnit.getLayoutParams();
        params2= (LinearLayout.LayoutParams) tvAmount.getLayoutParams();
        params.setMargins(params.leftMargin,spacing,params.rightMargin,params.bottomMargin);
        tvUnit.setLayoutParams(params);
        layoutToDraw = (TextView)view.findViewById(R.id.layout_to_draw);
        ll_content=view.findViewById(R.id.ll_user_info);

        layoutToDraw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isDown=true;
                        Drawable drawable = getResources().getDrawable(R.drawable.white_720);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                        layoutToDraw.setCompoundDrawables(null, null, drawable, null);//画在右边
                        layoutToDraw.setTextColor(getResources().getColor(R.color.white));
                        layoutToDraw.setBackgroundResource(R.drawable.round_blue);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        if(isDown){
                            Drawable drawable2 = getResources().getDrawable(R.drawable.blue_720);
                            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight()); //设置边界
                            layoutToDraw.setCompoundDrawables(null, null, drawable2, null);//画在右边
                            layoutToDraw.setTextColor(getResources().getColor(R.color.main_nav_blue));
                            layoutToDraw.setBackgroundResource(R.drawable.round_whilte);
                        }
                        break;
                }

                if(event.getY()>0
                        &&event.getY()<v.getHeight()
                        &&event.getX()>0
                        &&event.getX()<v.getWidth()){
                }else {
                    isDown=false;
                    layoutToDraw.setTextColor(getResources().getColor(R.color.main_nav_blue));
                    layoutToDraw.setBackgroundResource(R.drawable.round_whilte);
                    Drawable drawable2 = getResources().getDrawable(R.drawable.blue_720);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight()); //设置边界
                    layoutToDraw.setCompoundDrawables(null, null, drawable2, null);//画在右边
                }
                return false;
            }
        });
        initData();
        initListener();
    }
    private Point getViewSize(View view){
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        Point point = new Point();
        point.x = view.getMeasuredWidth();
        point.y = view.getMeasuredHeight();
        return point;
    }

    public void setUpdateOnResume(boolean updateOnResume) {
        this.updateOnResume = updateOnResume;
    }

    private void initListener() {
        DrawButtonClickListener2 listener = new DrawButtonClickListener2((AppBaseActivity) context);
        listener.setStatistic(ShoudanStatisticManager.Do_Homepage_Detail);
        DoEnum.Do.setData(null,true,false);
        layoutToDraw.setOnClickListener(listener);
    }

    private void initData() {
        tvDate.setText(DateUtil.formatSystemDate("MM/dd") + "  今日已收");
        LogUtil.print("<S>","userinfor:"+ApplicationEx.getInstance().getUser().toString());
        String names=ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName();
        if(!TextUtils.isEmpty(names)&&!"null".equals(names)){
            tv_name.setText(names);
            ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//默认显示商户为开通，数据刷新后更新点击功能
                    PublicToEvent.messeage=false;
                ActiveNaviUtils.start((AppBaseActivity) getActivity(), "shgl",null);
                }
            });
        }else {
            ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//默认显示商户为开通，数据刷新后更新点击功能
                    ProtocalActivity.open(context, ProtocalType.GPS_PERMISSION);
                }
            });
            tv_name.setText("商户未开通");
            tv_open.setText("开通商户");
            tv_name_content.setText("");
            iv_name.setImageResource(0);
        }
//        levelSwi();
    }

    public void levelSwi(){
        map_ic.put("V1",R.drawable.level_icon_ss);
        map_ic.put("V2",R.drawable.level_icon_zg);
        map_ic.put("V3",R.drawable.level_icon_cz);
        map_ic.put("V4",R.drawable.level_icon_yw);
        map_ic.put("V5",R.drawable.level_icon_ag);
        map_ic.put("MV",R.drawable.level_icon_dc);
        map_na.put("V1","初来乍到");
        map_na.put("V2","生财有道");
        map_na.put("V3","日进斗金");
        map_na.put("V4","财源滚滚");
        map_na.put("V5","富甲一方");
        map_na.put("MV","家财万贯");

        LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if(resultServices.isRetCodeSuccess()){
                    LogUtil.print("<S>","onSuccess");
                    try {
                        User user = ApplicationEx.getInstance().getSession().getUser();
                        JSONObject data = new JSONObject(resultServices.retData);
                        user.initMerchantAttrWithJson(data);
                        ApplicationEx.getInstance().getUser().save();

                        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
                        MerchantStatus status = merchantInfoData.getMerchantStatus();
                        switch (status) {
                            case FAILURE:
                            case FROZEN:
                            case PROCESSING:
                            case COMPLETED:
                                LogUtil.print("<S>","queryMerLevelInfo");
                                queryMerLevelInfo((AppBaseActivity) getActivity());
                                break;
                            case NONE:
                                LogUtil.print("<S>","NONE");
                                tv_name.setText("商户未开通");
                                tv_open.setText("开通商户");
                                tv_name_content.setText("");
                                iv_name.setImageResource(0);
                                ll_content.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ProtocalActivity.open(context, ProtocalType.GPS_PERMISSION);
                                    }
                                });
                                break;
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
                ToastUtil.toast(context,"网络请求失败");
            }
        })).execute();
    }

    /**
     * 获取商户升级信息
     * @param context
     */
    private void queryMerLevelInfo(final AppBaseActivity context) {
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.QUERY_MerLevelAndLimit);
        request.setAutoShowToast(false);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                LogUtil.print("<S>","onSuccess");
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    try {
                        LogUtil.print("<S>","onSuccess:"+data);
                        JSONObject jsonObject=new JSONObject(data);
                        if(!TextUtils.isEmpty(jsonObject.optString("merLevel"))){
                            String level=jsonObject.optString("merLevel");
                            tv_name.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName());
                            if (level==null||level.equals("")||!level.startsWith("V")){
                                level="MV";
                            }
                            tv_name_content.setText(map_na.get(level));
                            iv_name.setImageResource(map_ic.get(level));
                            tv_open.setText("");
                            ll_content.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PublicEnum.Business.setHome(true);
//                                    startActivity(new Intent(getActivity(), QuickArriveActivity.class));
                                    ActiveNaviUtils.start((AppBaseActivity) getActivity(), "shgl",null);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    context.toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,context.getString(R.string.socket_fail));
            }
        });
        request.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        levelSwi();
        if(updateOnResume && !isCollecting){
            getCollectionAmount();
//            updateOnResume = false;
        }
    }

    public Double getTodayAmount() {
        return todayAmount;
    }

    private void getCollectionAmount() {
        isCollecting = true;
        tvAmount.setText("");
        params2.width= UIUtils.dip2px(65);
        tvAmount.setLayoutParams(params2);
        tvAmount.post(new Runnable() {
            @Override
            public void run() {
                if(isCollecting){
//                    tvUnit.setVisibility(View.GONE);
                    int length = tvAmount.length();
                    if(length >= 5){
                        tvAmount.setText("");
                    }
                    tvAmount.append(".");
                    tvAmount.postDelayed(this,300);
                }
            }
        });
        CommonServiceManager.getInstance().queryTodayCollection(new CommonServiceManager.TodayCollectionCallback() {
            @Override
            public void onSuccess(Double amount) {
                updateCollectionAmount(amount);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                updateCollectionAmount(null);
            }
        });
    }

    private void updateCollectionAmount(Double amount) {
        if (amount != null) {
            todayAmount = amount;
        }
        isCollecting = false;
        DecimalFormat format = new DecimalFormat("0.00");
        params2.width= -2;
        tvAmount.setLayoutParams(params2);
        tvAmount.setText(format.format(amount == null?0:amount));
//        tvUnit.setVisibility(View.VISIBLE);
    }

    public String getAmout(){
        return tvAmount.getText().toString().trim();
    }

}
