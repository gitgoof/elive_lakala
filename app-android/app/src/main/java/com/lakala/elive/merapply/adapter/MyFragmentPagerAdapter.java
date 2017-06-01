package com.lakala.elive.merapply.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lakala.elive.merapply.fragment.BaseFragment;
import com.lakala.elive.merapply.fragment.MyMerchantsFragment;

import java.util.ArrayList;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<BaseFragment> fragments;
    private String[] titles;
    //ENTER:待录入（未录入完成）,AUDIT:待审核（未同步到bmcp）,AUDITING:审核中（已同步到bmcp）,REBUT:审核失败,STORAGED:审核成功
    private String[] process = {"STORAGED","REBUT","ENTER","AUDIT","AUDITING"};//我的商户待入库状态去掉了

    public MyMerchantsFragment currentFragment;

    public MyFragmentPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> fragments, String[] titles) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (null == fragments || fragments.size() <= 0) {
            return MyMerchantsFragment.newInstance(process[position]);
        } else {
            return fragments.get(position);
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (null == fragments || fragments.size() <= 0) {
            currentFragment = (MyMerchantsFragment) object;
        }

    }
}
