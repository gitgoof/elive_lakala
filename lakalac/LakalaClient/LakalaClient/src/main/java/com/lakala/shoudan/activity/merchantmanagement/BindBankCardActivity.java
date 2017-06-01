package com.lakala.shoudan.activity.merchantmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.CardEditFocusChangeListener;

/**
 * Created by Administrator on 2016/8/17 0017.
 */
public class BindBankCardActivity extends BasePwdAndNumberKeyboardActivity implements View.OnClickListener {
    private CheckBox cb_read_and_accept;
    private TextView tv_lkl_transfer_remit_service_protocol;//快捷支付协议
    private TextView btn_next;//下一步
    private CardEditFocusChangeListener cardChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_bard);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.add_bank_card);

        //checkbox 和 快捷支付协议
        cb_read_and_accept = (CheckBox) findViewById(R.id.cb_read_and_accept);
        tv_lkl_transfer_remit_service_protocol = (TextView) findViewById(R.id.tv_lkl_transfer_remit_service_protocol);
        tv_lkl_transfer_remit_service_protocol.setOnClickListener(this);
        cb_read_and_accept.setChecked(true);
        cb_read_and_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    btn_next.setEnabled(true);
                }else{
                    btn_next.setEnabled(false);
                }
            }
        });
        cardChangeListener = new CardEditFocusChangeListener();
        cardChangeListener.addCheckCardCallback(new CardEditFocusChangeListener
                .SimpleOpenBankInfoCallback(){
            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {
                if(openBankInfo == null){
                    return;
                }
               /* bankInfo = OpenBankInfo.construct(openBankInfo);
                UpdateRemiteModeLayout();
                let_open_bank.getEditText().setText(bankInfo.getBankName());*/
            }
        });
        //下一步
        btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_lkl_transfer_remit_service_protocol://快捷支付协议
                startTransferRemitServiceProtocolActivity();
                break;
            case R.id.btn_next://下一步
                hideNumberKeyboard();
                if(!isInputValid()){
                    return;
                }
               /* OpenBankInfo bankInfo = cardChangeListener.getOpenBankInfo(cardNumber);
                checkCardInfo(bankInfo);*/
                break;
        }
    }

    /**
     * 隐藏数字键盘，键盘关闭后会解除与EditText之间的绑定。
     */
    protected void hideNumberKeyboard(){
        /*if (numberKeyboardOwne != null){
            hideNumberKeyboard(numberKeyboardOwne);
        }*/
    }
    /**
     * 数据检验
     * @return
     */
    private boolean isInputValid(){

        /*//姓名
        name = StringUtil.formatString(let_real_name.getEditText().getText().toString().trim());
        if (name.equals("")) {
            ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.input_name_prompt), Toast.LENGTH_LONG);
            return false;
        }
        //卡号
        cardNumber = StringUtil.formatString(let_input_credit_number.getEditText().getText().toString());
        if (StringUtil.isEmpty(cardNumber)) {
            ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.input_account_prompt),Toast.LENGTH_LONG);
            return false;
        }

        //银行
        openBank = let_open_bank.getEditText().getText().toString().trim();
        if (openBank.length() == 0) {
            ToastUtil.toast(TransferRemittanceActivity.this, getString(R.string.select_receiver_bank), Toast.LENGTH_LONG);
            return false;
        }

        //金额
        amount = let_transfer_money.getEditText().getText().toString().trim().replace(",","");
        if (StringUtil.isEmpty(amount)) {
            ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.please_input_amount),Toast.LENGTH_LONG);
            return false;
        } else {
            if (!StringUtil.isAmountVaild(amount)) {
                ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.toast_money_format_error),Toast.LENGTH_LONG);
                return false;
            }else{
                try{
                    if(new BigDecimal(amount).compareTo(new BigDecimal("9999999.99")) > 0){
                        ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.monty_input_super),Toast.LENGTH_LONG);
                        return false;
                    }

                    if(new BigDecimal(amount).compareTo(new BigDecimal("1")) < 0){
                        ToastUtil.toast(TransferRemittanceActivity.this,"单笔金额小于限制，请重新输入（金额必须不小于1元）。",Toast.LENGTH_LONG);
                        return false;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.money_input_error),Toast.LENGTH_LONG);
                    return false;
                }

            }
        }
        if (selectPosition == -1) {//未选择转账方式
            ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.select_remit_mode_prompt),Toast.LENGTH_LONG);
            return false;
        }
        //手机号码
        phone = let_phone_number.getEditText().getText().toString();
        if (ls_free_sms_notify.getSwitchStatus() == LabelSwitch.ESwitchStatus.ON) {
            if (!PhoneNumberUtil.checkPhoneNumber(StringUtil.formatString(phone))) {
                ToastUtil.toast(TransferRemittanceActivity.this,getString(R.string.phone_illegal_content),Toast.LENGTH_LONG);
                return false;
            }
        }*/
        return true;
    }

    /**
     * 快捷支付协议
     */
    private void startTransferRemitServiceProtocolActivity(){
        //此url原收款宝需要访问接口获取,吴春然已经确认使用商户通url即可
        String title = "快捷支付协议";
        String url = "https://download.lakala.com/lklmbl/html/sht/sht_trans.html";
        ProtocalActivity.open(context, title, url);
    }
}
