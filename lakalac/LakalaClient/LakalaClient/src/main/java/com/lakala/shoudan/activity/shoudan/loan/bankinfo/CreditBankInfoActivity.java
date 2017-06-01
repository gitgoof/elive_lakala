package com.lakala.shoudan.activity.shoudan.loan.bankinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.XAtyTask;
import com.lakala.ui.component.LabelEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 银行信息
 */
public class CreditBankInfoActivity extends BasePwdAndNumberKeyboardActivity implements CreditBankInfoContract.CreditBankInfoView {
    @Bind(R.id.let_card_num)
    EditText letCardNum;
    @Bind(R.id.let_bank_card_num)
    LabelEditText letBankCardNum;
    @Bind(R.id.let_card_type)
    TextView letCardType;
    @Bind(R.id.credit_card_num)
    EditText creditCardNum;
    @Bind(R.id.credit_bank_card_num)
    LabelEditText creditBankCardNum;
    @Bind(R.id.credit_card_type)
    TextView creditCardType;
    @Bind(R.id.view_group)
    RelativeLayout viewGroup;
    private CreditBankInfoContract.Presenter presenter;
    private final String TAG_LETBANK="TAG_LETBANK";
    private final String TAG_CREDITBANK="TAG_CREDITBANK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("银行信息");
        XAtyTask.getInstance().addAty(this);
        presenter = new CreditBankInfoPresenter(this);
        presenter.setLetInfo();
        //键盘
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        initNumberKeyboard();
        //initNumberEdit(letBankCardNum.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        initNumberEdit(creditBankCardNum.getEditText(), CustomNumberKeyboard.EDIT_TYPE_CARD, 30);
        //letBankCardNum.setOnFocusChangeListener(null);
        creditBankCardNum.setOnFocusChangeListener(null);
        //letBankCardNum.getEditText().setTag(TAG_LETBANK);
        creditBankCardNum.getEditText().setTag(TAG_CREDITBANK);
       //presenter.addEditTextChange("1",letBankCardNum.getEditText());
        presenter.addEditTextChange("2",creditBankCardNum.getEditText());
        letBankCardNum.getEditText().setFocusableInTouchMode(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        presenter.setFocus(v,hasFocus,letBankCardNum,creditBankCardNum,TAG_LETBANK,TAG_CREDITBANK);
    }

    @Override
    public void setPresenter(CreditBankInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @OnClick({R.id.next})
    public void clik(View v){
        presenter.toNext(letCardNum,creditCardNum,letBankCardNum.getEditText(),letCardType,creditBankCardNum.getEditText(),creditCardType);
    }
    @Override
    public void showToast(String str) {
        ToastUtil.toast(context, str);
    }

    @Override
    public void showLoading() {
        showProgressWithNoMsg();
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }

    @Override
    public void setCardData(OpenBankInfo mOpenBankInfo) {
            presenter.setCardData(mOpenBankInfo);
    }

    @Override
    public void showLetBankError(String str) {
        letCardType.setText(str);
    }

    @Override
    public void showCreditBankError(String str) {
        creditCardType.setText(str);
    }

    @Override
    public void showLetBank(OpenBankInfo mOpenBankInfo) {
        letBankCardNum.getEditText().setText(mOpenBankInfo.cardNo);
        letCardType.setText(mOpenBankInfo.bankname);
    }

    @Override
    public void showCreditBank(OpenBankInfo mOpenBankInfo) {
        creditBankCardNum.getEditText().setText(mOpenBankInfo.cardNo);
        creditCardType.setText(mOpenBankInfo.bankname);
    }

    @Override
    public void setBankInfo(String realName, MerchantInfo info) {
        letCardNum.setText(realName);
        letBankCardNum.getEditText().setText(info.getAccountNo());
        letCardType.setText(info.getBankName());
        creditCardNum.setText(realName);
    }

    @Override
    public void applayComplet(String data) {
        presenter.applayComplet(data);
    }

    @Override
    protected void onDestroy() {
        presenter.clear();
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
