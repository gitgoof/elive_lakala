package com.lakala.elive.preenterpiece.fragment;

import android.os.Bundle;

import com.lakala.elive.R;
import com.lakala.elive.common.widget.LazyFragment;

/**
 */
public class PreDetailFragment extends LazyFragment {

    public static final String PROCESS = "process";

    public static PreDetailFragment newInstance(int process) {
        PreDetailFragment fragment = new PreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PROCESS, process);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_my_merchants;
    }

    @Override
    protected void bindView() {
    }

    @Override
    protected void bindEvent() {
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onSuccess(int method, Object obj) {
    }

    @Override
    public void onError(int method, String statusCode) {
    }

}
