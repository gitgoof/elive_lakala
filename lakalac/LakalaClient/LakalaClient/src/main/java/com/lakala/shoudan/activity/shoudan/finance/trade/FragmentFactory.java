package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.support.v4.app.Fragment;

/**
 * Created by HJP on 2015/9/6.
 */
public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index){
        Fragment fragment = null;
        switch (index){
            case 1:
                fragment=new WealthManagementFragment();
                break;
            case 0:
            case 2:
                fragment=new WealthManagementAdFragment();
                break;
        }
        return fragment;
    }
}
