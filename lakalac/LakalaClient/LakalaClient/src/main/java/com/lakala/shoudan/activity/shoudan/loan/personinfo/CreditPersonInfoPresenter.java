package com.lakala.shoudan.activity.shoudan.loan.personinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.shoudan.activity.shoudan.loan.RegionInfoAdapter;
import com.lakala.shoudan.activity.shoudan.loan.bankinfo.CreditBankInfoActivity;
import com.lakala.shoudan.activity.shoudan.loan.bankinfo.CreditBankInfoModel;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.ContactBookUtil;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CreditPersonInfoPresenter extends CreditPersonInfoContract.Presenter {
    private CreditPersonInfoContract.CreditBusinessView creditBusinessView;
    private CreditPersonInfoModel model;
    private Context mCotext;
    private static String contactTag;

    public CreditPersonInfoPresenter(CreditPersonInfoContract.CreditBusinessView creditBusinessView) {
        this.creditBusinessView = creditBusinessView;
        this.creditBusinessView.setPresenter(this);
        mCotext= (Context) creditBusinessView;
        model=new CreditPersonInfoModel();
    }

    @Override
    public void btnNext(EditText city, EditText address, EditText email, EditText unitName, EditText atCity, EditText uintAddress, EditText uintTel1, EditText relatName, EditText relatTel, EditText relatRelation, EditText urgencyContactName, EditText urgencyContactTel, EditText urgencyContactRelation) {
        boolean check=model.checkInval(creditBusinessView,city,address,email,unitName,atCity,uintAddress,uintTel1,relatName,relatTel,relatRelation,urgencyContactName,urgencyContactTel,urgencyContactRelation);
        if (check){
            CreditBankInfoModel modelInfo=new CreditBankInfoModel();
            modelInfo.setCity(city.getText().toString().trim());
            modelInfo.setAddress(address.getText().toString().trim());
            modelInfo.setEmail(email.getText().toString().trim());
            modelInfo.setCompanyName(unitName.getText().toString().trim());
            modelInfo.setCompanyCity(atCity.getText().toString().trim());
            modelInfo.setCompanyDetailAddr(uintAddress.getText().toString().trim());
            modelInfo.setCompanyTelephone(uintTel1.getText().toString().trim());
            modelInfo.setRelationName(relatName.getText().toString().trim());
            modelInfo.setRelationNumb(relatTel.getText().toString().trim());
            modelInfo.setRelationShip(relatRelation.getText().toString().trim());
            modelInfo.setEmergContactName(urgencyContactName.getText().toString().trim());
            modelInfo.setEmergContactNumb(urgencyContactTel.getText().toString().trim());
            modelInfo.setEmergContactShip(urgencyContactRelation.getText().toString().trim());
            modelInfo.setLoanMerId(((Activity)mCotext).getIntent().getStringExtra("loanMerId"));
            Intent intent = new Intent(mCotext, CreditBankInfoActivity.class);
            intent.putExtra("modelInfo",modelInfo);
            mCotext.startActivity(intent);
        }
    }

    @Override
    public void contactChoose(String tag) {
        contactTag=tag;
        ContactBookUtil.onContactBookClick(mCotext);
    }

    @Override
    public void cityiChoose(String tag) {
        contactTag=tag;
        model.getProvice(creditBusinessView);
    }

    @Override
    public void setCityi(View rootView, Spinner proviceSpinner, Spinner citySpinner, Spinner disticSppinner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCotext);
        builder.setTitle("请选择城市信息");
        proviceSpinner.setAdapter(new RegionInfoAdapter(mCotext,model.getProviceList(),0));
        proviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                RegionInfo province = model.getProviceList().get(position);
                if(model.getCurrentProvice() != province){
                    model.setCurrentProvice(province);
                    model.getCityListOfProvince(model.getCurrentProvice(),creditBusinessView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        model.setCityAdapter(new RegionInfoAdapter(mCotext, model.getCityList(),1));
        citySpinner.setAdapter(model.getCityAdapter());
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                RegionInfo newcity = model.getCityList().get(position);
                if(newcity != model.getCurrentCity()){
                    model.setCurrentCity(newcity);
                    model.getDistrictListOfCity(model.getCurrentCity(),creditBusinessView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        model.setDisticAdapter(new RegionInfoAdapter(mCotext, model.getDisticList(),2));
        disticSppinner.setAdapter(model.getDisticAdapter());
        disticSppinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                model.setCurrentDistic(model.getDisticList().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(rootView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(model.getCurrentProvice() == null){
                    creditBusinessView.showToast("请选择省");
                    return;
                }
                if(null == model.getCurrentCity()){
                    creditBusinessView.showToast("请选择市");
                    return;
                }
                if(null == model.getCurrentDistic()){
                    creditBusinessView.showToast("请选择地区");
                    return;
                }
                String citys=model.getCurrentProvice().getpNm()+"-"+model.getCurrentCity().getcNm()+"-"+model.getCurrentDistic().getaNm();
                if (contactTag.equals("3")){
                    creditBusinessView.setLiveCity(citys);
                }if (contactTag.equals("4")){
                    creditBusinessView.setAtCity(citys);
                }

                model.setApcrnames(model.getCurrentProvice().getpNm()+"|"+model.getCurrentCity().getcNm()+"|"+model.getCurrentDistic().getaNm());
                model.setApcrcodes(model.getCurrentProvice().getpId()+"|"+model.getCurrentCity().getcId()+"|"+model.getCurrentDistic().getaId());
                model.setDialogOpen(false);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                model.setDialogOpen(false);
            }
        });
        model.setDialogOpen(true);
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    @Override
    public void actResult(int requestCode, final Intent data, final EditText relatives, final EditText linkman) {
        if (requestCode == UniqueKey.PHONE_NUMBER_REQUEST_CODE){
            //通讯录中读取联系人的电话号码
            ContactBookUtil contactBookUtil;
            if (contactTag.equals("1")){
                contactBookUtil = new ContactBookUtil(mCotext, relatives, data);
            }else {
                contactBookUtil = new ContactBookUtil(mCotext, linkman, data);
            }
            contactBookUtil.setOnPhoneNumberSelectedListener(new ContactBookUtil.OnPhoneNumberSelectedListener() {
                @Override
                public void onPhoneNumberSelected(String phoneNumber) {
                    String phoneNum = StringUtil.formatString(phoneNumber);

                    if (!PhoneNumberUtil.checkPhoneNumber(phoneNum)) {
                        creditBusinessView.showToast("此联系人没有可供使用的手机号码，请重新选择");
                    } else {
                        if (contactTag.equals("1")){
                            creditBusinessView.setTex(relatives,phoneNum);////亲属电话号码
                        }else if (contactTag.equals("2")){
                            creditBusinessView.setTex(linkman,phoneNum);////紧急联系人电话号码
                        }
                    }
                }
                @Override
                public void onNoNumber(String hint) {
                    creditBusinessView.showToast("手机号码为空，请重新选择");
                }
            });
        }
    }

    @Override
    public void clearTags() {
        contactTag="";
    }

    /**
     * 亲属关系
     */
    @Override
    public void showRelatRelation() {

            DialogCreator.createCancelableListDialog((FragmentActivity) mCotext,"请选择亲属关系",model.getRelatStr(),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    creditBusinessView.showRelatStr(model.getRelatStr()[which]);
                   // personInfo.setHighestlevel(String.valueOf(which+1));
                }
            }).show();
    }
    /**
     * 紧急联系人关系
     */
    @Override
    public void showUrgencyRelation() {
        DialogCreator.createCancelableListDialog((FragmentActivity) mCotext,"请选择紧急联系人关系",model.getUrgencyRelatStr(),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                creditBusinessView.showUrgencyRelatStr(model.getUrgencyRelatStr()[which]);
            }
        }).show();
    }
/*
    @Override
    public void btnNext(EditText city, EditText address, EditText email, EditText unitName, EditText atCity, EditText uintAddress, EditText uintTel1, EditText uintTel2, EditText uintTel3, EditText relatName, EditText relatTel, EditText relatRelation, EditText urgencyContactName, EditText urgencyContactTel, EditText urgencyContactRelation) {
        boolean check=model.checkInval(creditBusinessView,city,address,email,unitName,atCity,uintAddress,uintTel1,uintTel2,uintTel3,relatName,relatTel,relatRelation,urgencyContactName,urgencyContactTel,urgencyContactRelation);
        if (check){

        }
    }
*/
}
