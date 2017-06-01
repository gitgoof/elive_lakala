package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by HJP on 2015/9/6.
 */
public class WealthManagementFragment extends Fragment {

    private View view;
//    private TotalAssets totalAssets;
    private List<ValidprodApplyprodInfo> validprodApplyprodInfosListTotal;
//    private List<ValidprodApplyprodInfo> validprodApplyprodInfosList;
//    private List<ValidprodApplyprodInfo> applyprodListInfosList;
    //活期
    private List<ValidprodApplyprodInfo>applyprodList;
    //定期
    private List<ValidprodApplyprodInfo>validprodList;

    TextView tvCurrentCount;
    TextView tvEarnings;
    TextView tvRegularCount;
    TextView tvEarnings2;
    /**
     * 定期笔数
     */
    int regualNum;
    /**
     * 活期笔数
     */
    int currentNum;
    /**
     * 预期总收益
     */
    double predictIncome=0;
    /**
     * 昨日总收益
     */
    double yesterdayIncome=0;
    DecimalFormat formatter;
    String pattern;
    private Bundle bundleFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_wealth_management_content, null);
        init();
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void init(){
        bundleFragment=getArguments();
        tvCurrentCount=(TextView)view.findViewById(R.id.tv_current_count);
        tvEarnings=(TextView)view.findViewById(R.id.tv_earnings);
        tvRegularCount=(TextView)view.findViewById(R.id.tv_regular_count);
        tvEarnings2=(TextView)view.findViewById(R.id.tv_earnings2);
        pattern= "#0.00";//格式代码，".00"代表保留两位小数，是0的输出0
        formatter= new DecimalFormat();
        formatter.applyPattern(pattern);
        validprodApplyprodInfosListTotal=new ArrayList<ValidprodApplyprodInfo>();
        if (bundleFragment!=null){
            validprodApplyprodInfosListTotal= (List<ValidprodApplyprodInfo>) getArguments().getSerializable("validprodApplyprodInfosListTotal");
        }
    }

    public void setValidprodApplyprodInfosListTotal(List<ValidprodApplyprodInfo> validprodApplyprodInfosListTotal) {
        this.validprodApplyprodInfosListTotal = validprodApplyprodInfosListTotal;
    }

    public void initData(){
            applyprodList=new ArrayList<ValidprodApplyprodInfo>();
            validprodList=new ArrayList<ValidprodApplyprodInfo>();
            for (int i=0;i<validprodApplyprodInfosListTotal.size();i++){
                int prodType=validprodApplyprodInfosListTotal.get(i).getProdType();
                if(prodType==0){
                    applyprodList.add(validprodApplyprodInfosListTotal.get(i));
                }
                else if (prodType==1){
                    validprodList.add(validprodApplyprodInfosListTotal.get(i));
                }
            }
            int applyprodSize=applyprodList.size();
            int validprodSize=validprodList.size();

            List<ValidprodApplyprodInfo> tempApplyrod=applyprodList;
            List<ValidprodApplyprodInfo> tempValidprod=validprodList;

            //活期笔数计算：如果ProductId一样，则算为一笔活期
            for(int i=0;i<applyprodSize;i++){
                yesterdayIncome+=applyprodList.get(i).getDayIncome();

                for(int j=i+1;j<applyprodSize;j++){
                    if(tempApplyrod.get(i).getProductId().equals(tempApplyrod.get(j).getProductId())){
                        tempApplyrod.remove(j);
                        applyprodSize-=1;
                        j-=1;
                    }
                }
            }


            //定期笔数计算：如果ProductId和Period一样，则算为一笔定期
            for(int i=0;i<validprodSize;i++){
                predictIncome+=validprodList.get(i).getPredictIncome();

                for(int j=i+1;j<validprodSize;j++){

                    if(tempValidprod.get(i).getProductId().equals(tempValidprod.get(j).getProductId()
                           )   && tempValidprod.get(i).getPeriod().equals(tempValidprod.get(j).getPeriod())){
                        tempValidprod.remove(j);
                        validprodSize-=1;
                        j-=1;
                    }
                }
            }
            int applyprodCount=tempApplyrod.size();
            tvCurrentCount.setText(applyprodCount+"笔");
            int validprodCount=tempValidprod.size();
            tvRegularCount.setText(validprodCount+"笔");

            tvEarnings.setText(String.valueOf(formatter.format(yesterdayIncome)));
            tvEarnings2.setText(String.valueOf(formatter.format(predictIncome)));

//        }
    }
}
