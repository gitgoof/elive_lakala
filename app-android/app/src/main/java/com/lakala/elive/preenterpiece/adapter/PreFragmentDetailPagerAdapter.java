package com.lakala.elive.preenterpiece.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lakala.elive.merapply.fragment.BaseFragment;
import com.lakala.elive.preenterpiece.fragment.PreDetailFragment;

import java.util.ArrayList;

/**
 */
public class PreFragmentDetailPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> fragments;
    private String[] titles;
    public PreDetailFragment currentFragment;

    public PreFragmentDetailPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    public PreFragmentDetailPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> fragments, String[] titles) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if (null == fragments || fragments.size() <= 0) {
            return PreDetailFragment.newInstance((position + 1));
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
            currentFragment = (PreDetailFragment) object;
        }
    }


}
