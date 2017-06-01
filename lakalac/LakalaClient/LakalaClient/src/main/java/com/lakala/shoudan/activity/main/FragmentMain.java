package com.lakala.shoudan.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.BaseFragment;

/**
 * 首页
 * Created by Administrator on 2017/2/23.
 */
public class FragmentMain extends BaseFragment{
    private View view;
    private FragmentMainTop fragmentMainTop=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_5_0, container, false);
        initUi();
        return view;
    }

    public void initUi(){

        if(fragmentMainTop==null){
            fragmentMainTop=new FragmentMainTop();
        }

        manager.beginTransaction().replace(R.id.fl_top,fragmentMainTop).commitAllowingStateLoss();
    }
}
