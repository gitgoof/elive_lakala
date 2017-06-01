package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.activity.shoudan.finance.adapter.WealthManagementAdAdapter;
import com.lakala.shoudan.activity.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HJP on 2015/9/6.
 */
public class WealthManagementAdFragment extends BaseFragment {
    private List<AdDownloadManager.Advertise> mAdvertises ;
    WealthManagementAdAdapter wealthManagementAdAdapter;
    ListView lvWealthManagementAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wealth_management_ad, null);
        lvWealthManagementAd=(ListView)view.findViewById(R.id.lv_wealth_management_ad);
        mAdvertises= new ArrayList<AdDownloadManager.Advertise>();
        wealthManagementAdAdapter=new WealthManagementAdAdapter(this.getActivity(),mAdvertises);
        lvWealthManagementAd.setAdapter(wealthManagementAdAdapter);
        showProgressWithNoMsg();
        initAds();
        return view;
    }
    public void initAds(){
        AdDownloadManager.getInstance().check(new AdDownloadManager.AdDownloadListener() {
            @Override
            public void onSuccess(List<AdDownloadManager.Advertise> advertises) {
                hideProgressDialog();
                mAdvertises.clear();
                if (advertises != null && advertises.size() > 0) {
                    mAdvertises.addAll(advertises);
                }else{
                    AdDownloadManager.Advertise advertise = new AdDownloadManager.Advertise();
                    advertise.setContent("drawable://" + R.drawable.pic_bg_event);
                    advertise.setClickUrl(null);
                    mAdvertises.add(advertise);

                    advertise = new AdDownloadManager.Advertise();
                    advertise.setContent("drawable://" + R.drawable.pic_bg_event);
                    advertise.setClickUrl(null);
                    mAdvertises.add(advertise);

                }
                wealthManagementAdAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed() {
                hideProgressDialog();
            }
        }, AdDownloadManager.Type.LICAI);
    }

}
