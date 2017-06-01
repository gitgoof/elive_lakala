package com.lakala.shoudan.activity.shoudan.promotionarea;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.activity.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJP on 2015/8/25.
 * 活动专区页面
 */
public class PromotionAreaActivity extends AppBaseActivity {
    private PullToRefreshListView promotionAdList;
    private PromotionListViewAdapter promotionListViewAdapter;
    private List<AdDownloadManager.Advertise> mAdvertises  = new ArrayList<AdDownloadManager.Advertise>();
    private WindowManager windowManager;
    private int width;
    private View emptyAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_promotion_area);
        initUI();
    }

    @Override
    public void initUI(){
        super.initUI();
        navigationBar.setTitle("活动专区");
        navigationBar.setBottomImageVisibility(View.GONE);
        windowManager= this.getWindowManager();
        width= windowManager.getDefaultDisplay().getWidth();
        emptyAdView=findViewById(R.id.layout_empty_promotion_ad);
        ((TextView)emptyAdView.findViewById(R.id.textView)).setText("暂无活动");
        initListView();
        initAds();
        initPoint();
    }

    private void initListView() {

        promotionAdList=(PullToRefreshListView)findViewById(R.id.pull_to_refresh_promotion_ad);
        promotionListViewAdapter=new PromotionListViewAdapter(PromotionAreaActivity.this,mAdvertises,width);
        promotionAdList.getRefreshableView().setAdapter(promotionListViewAdapter);
        promotionAdList.setMode(PullToRefreshBase.Mode.DISABLED);
        promotionAdList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initAds();
                emptyAdView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        promotionAdList.onRefreshComplete();
                    }
                }, 500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    public void initAds(){
        showProgressWithNoMsg();
        AdDownloadManager.getInstance().check(new AdDownloadManager.AdDownloadListener() {
            @Override
            public void onSuccess(List<AdDownloadManager.Advertise> advertises) {
                hideProgressDialog();
                if (advertises != null && advertises.size() > 0) {
                    mAdvertises.clear();
                    mAdvertises.addAll(advertises);
                }
                promotionAdList.setEmptyView(emptyAdView);
                promotionListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed() {
                hideProgressDialog();

            }
        }, AdDownloadManager.Type.EVENT);
    }

    public void initPoint(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Promotion_Home;
        }else if(PublicEnum.Business.isDirection()){
            event= ShoudanStatisticManager.Promotion_De;
        }else if(PublicEnum.Business.isAd()){
            event= ShoudanStatisticManager.Promotion_Ad;
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Promotion_Public;
        }else {
        }
        LogUtil.print("evnet",event);
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }
}
