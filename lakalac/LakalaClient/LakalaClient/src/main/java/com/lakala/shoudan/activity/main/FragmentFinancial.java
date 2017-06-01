package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.BaseFragment;
import com.lakala.shoudan.activity.kaola.KaoLaCreditActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.component.IconTextView;
import com.lakala.ui.component.NavigationBar;

/**
 * 金融
 * Created by Administrator on 2017/2/23.
 */
public class FragmentFinancial extends BaseFragment implements View.OnClickListener {
    private View view;
    private NavigationBar navigationBar;
    private IconTextView loan, finance, lcCredit, creditCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_financial, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        navigationBar = (NavigationBar) view.findViewById(R.id.navigation_bar);
        navigationBar.setTitle("金融");
        loan = (IconTextView) view.findViewById(R.id.loan);//贷款
        finance = (IconTextView) view.findViewById(R.id.finance);//理财
        lcCredit = (IconTextView) view.findViewById(R.id.lc_credit);//考拉信用
        creditCard = (IconTextView) view.findViewById(R.id.credit_card);//申办信用卡
        loan.setOnClickListener(this);
        finance.setOnClickListener(this);
        lcCredit.setOnClickListener(this);
        creditCard.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loan://贷款
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.LOAN_BUSINESS);
                break;
            case R.id.finance://理财
                FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
                financeRequestManager.startFinance((AppBaseActivity) context);
                break;
            case R.id.lc_credit://考拉信用
                startActivity(new Intent(context, KaoLaCreditActivity.class));
                break;
            case R.id.credit_card://申办信用卡
                ActiveNaviUtils.start((AppBaseActivity) context, "xyksb", null);

                break;

        }
    }


}
