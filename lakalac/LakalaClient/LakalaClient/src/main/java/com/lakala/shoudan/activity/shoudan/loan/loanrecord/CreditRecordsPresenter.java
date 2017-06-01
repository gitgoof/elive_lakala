package com.lakala.shoudan.activity.shoudan.loan.loanrecord;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.finance.adapter.AllProdAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.ui.component.NavigationBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CreditRecordsPresenter extends CreditRecordsConstract.Presenter {
    private CreditRecordsConstract.CreditBusinessView creditBusinessView;
    private Context context;
    private CreditRecordsConstract.Model model;
    public CreditRecordsPresenter(CreditRecordsConstract.CreditBusinessView creditBusinessView) {
        this.creditBusinessView = creditBusinessView;
        this.context = (Context) creditBusinessView;
        this.creditBusinessView.setPresenter(this);
        model=new CreditRecordsModel();
    }
    public View getEmptyView(String str) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View mVew = inflater.inflate(R.layout.list_empt, null);

        TextView tx = (TextView) mVew.findViewById(com.lakala.platform.R.id.tv_empty_tips);
        if (!TextUtils.isEmpty(str)) {
            tx.setText(str);
        }
        return mVew;
    }

    public void showPopupWindow(final int position, final NavigationBar navigationBar){
        final List<TransDetailProInfo> proList=model.getList();
        proList.get(position).setTick(true);
        LayoutInflater inflater = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_popupwindow, null);
        ListView lvPro = (ListView) view.findViewById(R.id.lv_prod);
        final AllProdAdapter adapter = new AllProdAdapter(context,proList);

        lvPro.setAdapter(adapter);
        final PopupWindow popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        setBackgroundAlpha(0.5f);
        popupWindow.showAsDropDown(navigationBar);
        navigationBar.toggleTitleRightDrawable();

        lvPro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                popupWindow.dismiss();

                proList.get(position).setTick(false);
                //点击标题查询，PAGECOUNT置0
                creditBusinessView.setPop(0,i,proList.get(i).getProName());
                //getRecord(proList.get(i).getProductId(), isDefaultId);
                String event="";
                if (i==0){
                    event= ShoudanStatisticManager.Loan_Apply_Records_end[0]+
                            ShoudanStatisticManager.Loan_Apply_Records_All;
                    model.getLoanApplyRecords(context,creditBusinessView,"LOAN_A");
                }else if (i==1){
                    event= ShoudanStatisticManager.Loan_Apply_Records_end[1]+
                            ShoudanStatisticManager.Loan_Apply_Records_All;
                    model.getLoanApplyRecords(context,creditBusinessView,"LOAN_B");
                }else if (i==2){
                    event= ShoudanStatisticManager.Loan_Apply_Records_end[2]+
                            ShoudanStatisticManager.Loan_Apply_Records_All;
                    model.getLoanApplyRecords(context,creditBusinessView,"LOAN_C");
                }else if (i==3){
                    event= ShoudanStatisticManager.Loan_Apply_Records_end[3]+
                            ShoudanStatisticManager.Loan_Apply_Records_All;
                    model.getLoanApplyRecords(context,creditBusinessView,"LOAN_D");
                }
                ShoudanStatisticManager.getInstance().onEvent(event, context);
            }
//            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                navigationBar.toggleTitleRightDrawable();
                setBackgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public List<CreditRecordsModel> getData() {
        String dataObj=((Activity)context).getIntent().getStringExtra("dataObj");
        List<CreditRecordsModel> mData =model.getDataList(dataObj);
        return mData;
    }

    @Override
    public void setList(PullToRefreshListView recordList) {
        ILoadingLayout loadingLayoutProxy = recordList.getLoadingLayoutProxy(
                true, false);
        // 设置释放时的文字提示
        loadingLayoutProxy.setReleaseLabel("松开刷新数据");
        // 设置下拉时的文字提示
        loadingLayoutProxy.setPullLabel("下拉刷新");
        // 设置最后一次更新的时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());
        loadingLayoutProxy.setLastUpdatedLabel("更新时间:" + time);
        // 设置刷新中的文字提示
        loadingLayoutProxy.setRefreshingLabel("正在刷新....");
    }

    @Override
    public void onItemClick(CreditRecordsModel crModel) {
        model.appllySdk(context,crModel.getLoanMerId(),crModel.getLoanName());
    }

    public void setBackgroundAlpha(float al) {
        WindowManager.LayoutParams params = ((Activity)context).getWindow().getAttributes();
        params.alpha = al;
        ((Activity)context).getWindow().setAttributes(params);
    }

}
