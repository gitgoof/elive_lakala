package com.lakala.shoudan.activity.merchantmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;

/**
 * Created by linmq on 2016/3/22.
 */
public class NoneMerchantFragment extends BaseFragment {

    private int tag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_none_merchant,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       TextView tvNum= (TextView) view.findViewById(R.id.tv_name);
       TextView user_state= (TextView) view.findViewById(R.id.user_state);
        Button ll_update= (Button) view.findViewById(R.id.ll_update);
        String mobileNum=getArguments().getString("mobileNum");
        mobileNum= mobileNum.substring(0,3)+"****"+mobileNum.substring(7,11);
        tvNum.setText("用户"+mobileNum);
        ll_update.setText("立即开通");
        user_state.setText("您还没有开通商户");
        MerchantInfo merchantInfo= ApplicationEx.getInstance().getUser().getMerchantInfo();
        view.findViewById(R.id.ll_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        ProtocalActivity.open(context, ProtocalType.GPS_PERMISSION);
            }
        });

    }

}
