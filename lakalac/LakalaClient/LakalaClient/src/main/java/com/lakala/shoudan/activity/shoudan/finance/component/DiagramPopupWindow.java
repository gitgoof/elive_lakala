package com.lakala.shoudan.activity.shoudan.finance.component;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProfitBeans;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ProfitList;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJP on 2015/10/12.
 */
public class DiagramPopupWindow extends PopupWindow implements OnClickListener{
    private AppBaseActivity activity;
    View contentView;
    private ImageView ivCancel;
    private View view;
    private CustomDiagram  customDiagram;
    private RadioButton rbSevendays;
    private RadioButton rbIncomew;
    private RadioButton rbSevenDaysDown;
    private RadioButton rbOneMonthDown;
    private View viewIncomew;
    private View viewSevendays;
//    private static String SEVENDAYS_MONTH="4.2";//七日年化收益 月
    private static String SEVENDAYS_DAY="4.1";//七日年化收益 当天
//    private static String INCOMEW_MONTH="3.2";//万份收益 月
    private static String INCOMEW_DAY="3.1";//万份收益 当天
    private static String NUMBER_MONTH="30";
    private static String NUMBER_DAY="7";
    private ProfitBeans profitBeans;
    private List<ProfitList> profitLists;


    public DiagramPopupWindow(AppBaseActivity activity,View view,String TradeType,String PageSize,boolean isSevenDays,boolean isSeven){
        this.activity=activity;
        this.view=view;
        contentView=LayoutInflater.from(activity).inflate(R.layout.diagram_popupwindow,null);
        this.setContentView(contentView);
        init();
        if(!isSevenDays){
            rbIncomew.setChecked(true);
            viewIncomew.setBackgroundColor(activity.getResources().getColor(R.color.title_bar_divider));
        }
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true);

        String[] xData = new String[]{"0","0","0","0","0","0","0",};
        float[] yData = new float[]{0f,0f,0f,0f,0f,0f,0f};
        customDiagram.setData(xData,yData);
        requestXYData(TradeType, PageSize,isSevenDays,isSeven);
    }


    public void init(){
        customDiagram=(CustomDiagram)contentView.findViewById(R.id.customdiagram);
        ivCancel=(ImageView)contentView.findViewById(R.id.iv_cancel);
        rbIncomew=(RadioButton)contentView.findViewById(R.id.rb_incomew);
        rbSevendays=(RadioButton)contentView.findViewById(R.id.rb_sevendays);
        rbSevenDaysDown=(RadioButton)contentView.findViewById(R.id.rb_seven_days_down);
        rbOneMonthDown=(RadioButton)contentView.findViewById(R.id.rb_one_month_down);
        viewIncomew=(View)contentView.findViewById(R.id.view_incomew);
        viewSevendays=(View)contentView.findViewById(R.id.view_sevendays);
        ivCancel.setOnClickListener(this);
        rbIncomew.setOnClickListener(this);
        rbSevendays.setOnClickListener(this);
        rbSevenDaysDown.setOnClickListener(this);
        rbOneMonthDown.setOnClickListener(this);
        profitBeans=new ProfitBeans();
        profitLists=new ArrayList<ProfitList>();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_cancel:
                this.dismiss();
                break;

            case R.id.rb_sevendays:
                setViewSevendays(true);
                if(rbSevenDaysDown.isChecked()){
                    requestXYData(SEVENDAYS_DAY,NUMBER_DAY,true,true);
                }else{
                    requestXYData(SEVENDAYS_DAY,NUMBER_MONTH,true,false);
                }
                break;
            case R.id.rb_incomew:
                setViewSevendays(false);
                if(rbSevenDaysDown.isChecked()){
                    requestXYData(INCOMEW_DAY,NUMBER_DAY,false,true);
                }else{
                    requestXYData(INCOMEW_DAY,NUMBER_MONTH,false,false);
                }
                break;
            case R.id.rb_seven_days_down:
                if(rbSevendays.isChecked()){
                    requestXYData(SEVENDAYS_DAY,NUMBER_DAY,true,true);
                }else{
                    requestXYData(INCOMEW_DAY,NUMBER_DAY,false,true);
                }

                break;
            case R.id.rb_one_month_down:
                if(rbSevendays.isChecked()){
                    requestXYData(SEVENDAYS_DAY, NUMBER_MONTH,true,false);
                }else{
                    requestXYData(INCOMEW_DAY, NUMBER_MONTH,false,false);
                }
                break;
        }


    }
    public void showDiagramPopupWindow(){
        if(!this.isShowing()){
            this.showAtLocation(view, Gravity.CENTER,0,0);
        }else{
            this.dismiss();
        }
    }
    public void setBackgroundAlpha(Activity activity,float al){
        WindowManager.LayoutParams params=activity.getWindow().getAttributes();
        params.alpha=al;
        activity.getWindow().setAttributes(params);
    }

    public void requestXYData(String TradeType,String PageSize,final boolean isSevenDays, final boolean isSeven){
        final String[] day= new String[7];
        final float[] result=new float[7];
        FinanceRequestManager.getInstance().getProfitInfo(TradeType, PageSize, new HttpResponseListener() {
            @Override
            public void onStart() {
                activity.showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                activity.hideProgressDialog();
                if (returnHeader.isSuccess() && responseData != null) {
                    profitBeans = ProfitBeans.parserStringToProfitBeans(responseData.toString());
                    try {
                        JSONArray jsonArray = responseData.getJSONArray("List");
                        int size = jsonArray.length();
                        if(size==0){
                        }else{
                            int k=size/7;
                            int count=0;
                            int j = 0;
                            for (int i = jsonArray.length() - 1; i >= 0; i-=k) {
                                count++;
                                if(count>7){
                                    break;
                                }
                                j=count-1;
                                JSONObject jobj = (JSONObject) jsonArray.get(i);
                                String dayString = jobj.optString("Day");
                                String day1 = dayString.substring(4, 6);
                                String day2 = dayString.substring(6, 8);
                                day[j] = day1 + "-" + day2;
                                String result1String = jobj.optString("Result");
                                result[j] = Float.valueOf(result1String);
                            }
                            customDiagram.invalidate();
                            customDiagram.setData(day, result);
                            if (isSevenDays) {
                                rbSevendays.setChecked(true);
                            } else {
                                rbIncomew.setChecked(true);
                            }
                            if(isSeven){
                                rbSevenDaysDown.setChecked(true);
                            }else {
                                rbOneMonthDown.setChecked(true);
                            }
                            setViewSevendays(isSevenDays);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ToastUtil.toast(activity,returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                activity.hideProgressDialog();
                ToastUtil.toast(activity,R.string.socket_fail);

            }
        });
    }
    public void setViewSevendays(boolean isSevenDays){
        if(isSevenDays){
            viewSevendays.setBackgroundColor(activity.getResources().getColor(R.color.common_blue_color));
            viewIncomew.setBackgroundColor(activity.getResources().getColor(R.color.title_bar_divider));
        }else{
            viewIncomew.setBackgroundColor(activity.getResources().getColor(R.color.common_blue_color));
            viewSevendays.setBackgroundColor(activity.getResources().getColor(R.color.title_bar_divider));
        }
    }
}
