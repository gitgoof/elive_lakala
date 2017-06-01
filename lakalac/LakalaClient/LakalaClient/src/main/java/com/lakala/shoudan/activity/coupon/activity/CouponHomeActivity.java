package com.lakala.shoudan.activity.coupon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.coupon.CouponListAdapter;
import com.lakala.shoudan.activity.coupon.bean.CouponBean;
import com.lakala.shoudan.activity.integral.IntegralController;
import com.lakala.shoudan.activity.integral.activity.IntegralMainActivity;
import com.lakala.shoudan.activity.integral.activity.IntegralSmsVertifyActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.datadefine.Voucher;
import com.lakala.ui.component.NavigationBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huangjp on 2016/5/31.
 */
public class CouponHomeActivity extends AppBaseActivity {
    private ListView couponList;
    private View emptyView;
    private CouponListAdapter couponListAdapter;
    private List<Voucher> couponBeanList=new ArrayList<Voucher>();
    private TextView tvGoExchange;
    private TextView tvBusinessDescription;
    private int first;//是否开通过积分购
    public static void start(Context context, List<Voucher> data,IsFirst first){
        Intent intent = new Intent(context,CouponHomeActivity.class);
        intent.putExtra("couponBeenList",(Serializable)data);
        intent.putExtra("isFirst",first.value);
        if(TextUtils.equals(context.getClass().getName(),
                            IntegralMainActivity.class.getName())){
            intent.putExtra("fromIntegralMain",true);
        }
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_home);
        init();
    }
    private void init() {
        initView();
        initUI();
        initData();
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("代金券");
        navigationBar.setActionBtnText("账单明细");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem== NavigationBar.NavigationBarItem.action){
                    ProtocalActivity.open(context, ProtocalType.FOLLOW_LAKALA_HELP);
                }else if (navBarItem== NavigationBar.NavigationBarItem.back){
                    onBackPressed();
                }
            }
        });
    }
    private void initView(){
        couponList=(ListView)findViewById(R.id.coupon_list);
        emptyView= findViewById(R.id.layout_empty);
        ((TextView)emptyView.findViewById(R.id.textView)).setText("您还没有代金券");
        ((ImageView)emptyView.findViewById(R.id.imageVew)).setImageResource(R.drawable.btn_no_kq);
        tvGoExchange=(TextView) findViewById(R.id.tv_go_exchange);
        tvBusinessDescription=(TextView) findViewById(R.id.tv_business_description);
        tvBusinessDescription.setOnClickListener(this);
        tvGoExchange.setOnClickListener(this);
    }
    private void initData(){
        couponBeanList= (List<Voucher>) getIntent().getSerializableExtra("couponBeenList");
        first=getIntent().getIntExtra("isFirst",0);
        if (null!=couponBeanList){
            Collections.sort(couponBeanList,new UsedListComparator());
            couponListAdapter=new CouponListAdapter(context,couponBeanList);
            couponList.setAdapter(couponListAdapter);
        }else{
            couponList.setEmptyView(emptyView);
        }

    }

    class UsedListComparator implements Comparator<Voucher>{
        @Override
        public int compare(Voucher couponBean1, Voucher couponBean2) {
            String beanStr1=couponBean1.getState();
            String beanStr2=couponBean2.getState();
            return beanStr1.compareTo(beanStr2);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_go_exchange:
                boolean fromIntegralMain = getIntent().getBooleanExtra("fromIntegralMain", false);
                if(fromIntegralMain){
                    onBackPressed();
                }else {
                    Class enterClazz = null;
                    if(first==0){
                        enterClazz = IntegralSmsVertifyActivity.class;
                    }else {
                        enterClazz = IntegralMainActivity.class;
                    }
                    Intent intent = new Intent(context, enterClazz);
                    context.startActivity(intent);
                }
                break;
            case R.id.tv_business_description:
                ProtocalActivity.open(context,ProtocalType.ALL_LCKH);
                break;
        }
    }
    public enum IsFirst{
        ZERO(0),
        ONE(1),
        TWO(2);
        private int value;
        IsFirst(int i) {
            value=i;
        }
    }
}
