package com.lakala.elive.market.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lakala.elive.market.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class ShopFragPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<BaseFragment> fragments;
    private  List<String> titles;


    public ShopFragPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    /**
     * 根据位置返回对应的Fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     * 得到页面的标题
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }


}
