package com.lakala.shoudan.activity.shoudan.loan.bankinfo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.CardUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.ui.component.LabelEditText;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CreditBankInfoPresenter extends CreditBankInfoContract.Presenter {
    private CreditBankInfoContract.CreditBankInfoView creditBankInfoView;
    private CreditBankInfoContract.Model model;
    private Context mContex;
    private static String inputType;///**账户类型代码，C:贷记；D:借记*/
    public CreditBankInfoPresenter(CreditBankInfoContract.CreditBankInfoView creditBankInfoView) {
        this.creditBankInfoView = creditBankInfoView;
        this.creditBankInfoView.setPresenter(this);
        model=new CreditBankInfoModel();
        mContex= (Context) creditBankInfoView;
    }


    @Override
    public void setFocus(View v, boolean hasFocus, LabelEditText letBankCardNum, LabelEditText creditBankCardNum, String TAG_LETBANK, String TAG_CREDITBANK) {
        if (v instanceof EditText) {
            String tag = String.valueOf(v.getTag());
            if (TAG_LETBANK.equals(tag)) {//储蓄卡
                inputType="D";
                setFos(hasFocus,letBankCardNum);
            }else  if (TAG_CREDITBANK.equals(tag)){//信用卡
                inputType="C";
                setFos(hasFocus,creditBankCardNum);
            }
        }

    }

    @Override
    public void setCardData(OpenBankInfo mOpenBankInfo) {
        SharedPreferences mySharedPreferences=mContex .getSharedPreferences("bankInfo", Activity.MODE_PRIVATE);
       SharedPreferences.Editor  editor = mySharedPreferences.edit();
            if (inputType.equals("D")){
                if (mOpenBankInfo.acccountType.equals("D")){
                    editor.putString("letBankNo",mOpenBankInfo.cardNo);
                    editor.putBoolean("letBankNoEdit",true);
                     creditBankInfoView.showLetBank(mOpenBankInfo);
                }else {
                     creditBankInfoView.showLetBankError("请输入正确的储蓄卡卡号");
                     //creditBankInfoView.showLetBankError("");
                }
            }else if (inputType.equals("C")){
                if (mOpenBankInfo.acccountType.equals("C")){
                    editor.putString("creditBankNo",mOpenBankInfo.cardNo);
                    creditBankInfoView.showCreditBank(mOpenBankInfo);
                }else {
                    creditBankInfoView.showCreditBankError("请输入正确的信用卡卡号");
                }
            }
        editor.commit();
    }


    @Override
    public void clear() {
        inputType=null;
    }

    @Override
    public void toNext(EditText letCardNum, EditText creditCardNum, EditText editText, TextView letCardType, EditText editText1, TextView creditCardType) {
       boolean check= model.check(mContex,creditBankInfoView,editText,letCardType,editText1,creditCardType);
        if (check){
            CreditBankInfoModel modelInfo= (CreditBankInfoModel) ((Activity)mContex).getIntent().getSerializableExtra("modelInfo");
            modelInfo.setDebitUserName(letCardNum.getText().toString().trim());
            modelInfo.setDebitBankAcctNo(editText.getText().toString().trim());
            modelInfo.setDebitBankName(letCardType.getText().toString().trim());
            modelInfo.setCreditUserName(creditCardNum.getText().toString().trim());
            modelInfo.setCreditBankAcctNo(editText1.getText().toString().trim());
            modelInfo.setCreditBankName(creditCardType.getText().toString().trim());
            model.addUserInfo(mContex,creditBankInfoView,modelInfo);
        }
    }

    @Override
    public void setLetInfo() {
       MerchantInfo info= ApplicationEx.getInstance().getUser().getMerchantInfo();
        if (info.getAccountType().getValue().equals("0")){//个人账号
            creditBankInfoView.setBankInfo(info.getUser().getRealName(),info);
        }else {//对公账号
            creditBankInfoView.setBankInfo(info.getAccountName(),info);
        }
    }

    @Override
    public void applayComplet(String data) {
        createFullShueDialog( data);
    }

    @Override
    public void addEditTextChange(final String str, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString())){
                    if ("1".equals(str)){
                        creditBankInfoView.showLetBankError("");
                    }else {
                        creditBankInfoView.showCreditBankError("");
                    }
                }
            }
        });
    }

    public void setFos(boolean hasFocus, LabelEditText letBankCardNum){
        String cardNum= letBankCardNum.getEditText().getText().toString().trim();
        if (!hasFocus) {
            if (toCheckCardBin(cardNum)) {
                model.checkCreditNum(creditBankInfoView,cardNum);
            } else {
                letBankCardNum.getEditText().setText("");
                creditBankInfoView.showToast("银行卡号输入不正确");
            }

        }
        if (hasFocus) {
            String newText = letBankCardNum.getEditText().getText().toString().replace(" ", "");
            letBankCardNum.getEditText().setText(newText);
            letBankCardNum.getEditText().setSelection(newText.length());
        } else {
            String text = CardUtil.formatCardNumberWithSpace(letBankCardNum.getEditText().getText().toString());
            letBankCardNum.getEditText().setText(text);
        }
    }

    //输入卡号是否规范
    private boolean toCheckCardBin(String text) {
        if (text.length() >= 14 && text.length() <= 19) {
            return true;
        }
        return false;
    }
    private void  createFullShueDialog(final String data){
        String content="资料提交成功，请选择适合您的贷款产品吧";
        DialogCreator.createFullShueDialog(
                (FragmentActivity) mContex, "提示",   "确定", content,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        model.appllySdk(mContex,data);
                        PublicToEvent.normalEvent1( mContex);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }
        ).show();
    }
}
