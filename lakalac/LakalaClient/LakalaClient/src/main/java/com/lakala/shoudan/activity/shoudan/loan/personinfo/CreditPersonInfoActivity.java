package com.lakala.shoudan.activity.shoudan.loan.personinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.util.XAtyTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人信息
 */
public class CreditPersonInfoActivity extends AppBaseActivity implements CreditPersonInfoContract.CreditBusinessView {
    @Bind(R.id.city_tag)
    TextView cityTag;
    @Bind(R.id.city)
    EditText city;
    @Bind(R.id.city_img)
    ImageView cityImg;
    @Bind(R.id.address_tag)
    TextView addressTag;
    @Bind(R.id.address)
    EditText address;
    @Bind(R.id.email_tag)
    TextView emailTag;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.layout_first)
    TableLayout layoutFirst;
    @Bind(R.id.unit_name_tag)
    TextView unitNameTag;
    @Bind(R.id.unit_name)
    EditText unitName;
    @Bind(R.id.place_tag)
    TextView placeTag;
    @Bind(R.id.place)
    EditText place;
    @Bind(R.id.place_img)
    ImageView placeImg;
    @Bind(R.id.uint_address_tag)
    TextView uintAddressTag;
    @Bind(R.id.uint_address)
    EditText uintAddress;
    /*@Bind(R.id.tel_first)
    EditText telFirst;
    @Bind(R.id.tel_mid)
    EditText telMid;
    @Bind(R.id.tel_last)
    EditText telLast;*/
    @Bind(R.id.unit_phone)
    EditText unitPhone;
    @Bind(R.id.layout_second)
    TableLayout layoutSecond;
    @Bind(R.id.contact_name_tag)
    TextView contactNameTag;
    @Bind(R.id.contact_name)
    EditText contactName;
    @Bind(R.id.relatives_tag)
    TextView relativesTag;
    @Bind(R.id.relatives_tel)
    EditText relativesTel;
    @Bind(R.id.relatives_tel_img)
    ImageView relativesTelImg;
    @Bind(R.id.relatives_relation_tag)
    TextView relativesRelationTag;
    @Bind(R.id.relatives_relation)
    EditText relativesRelation;
    @Bind(R.id.relatives_relation_img)
    ImageView relativesRelationImg;
    @Bind(R.id.layout_third)
    TableLayout layoutThird;
    @Bind(R.id.linkmane_tag)
    TextView linkmaneTag;
    @Bind(R.id.linkman_name)
    EditText linkmanName;
    @Bind(R.id.linkman_tag)
    TextView linkmanTag;
    @Bind(R.id.linkman_tel)
    EditText linkmanTel;
    @Bind(R.id.linkman_img)
    ImageView linkmanImg;
    @Bind(R.id.linkman_relation_tag)
    TextView linkmanRelationTag;
    @Bind(R.id.linkman_relation)
    EditText linkmanRelation;
    @Bind(R.id.linkman_relation_img)
    ImageView linkmanRelationImg;
    @Bind(R.id.layout_four)
    TableLayout layoutFour;
    @Bind(R.id.cbox_agreement)
    CheckBox cboxAgreement;
    @Bind(R.id.next)
    TextView next;
    private CreditPersonInfoContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_person_info);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        presenter = new CreditPersonInfoPresenter(this);
        XAtyTask.getInstance().addAty(this);
        navigationBar.setTitle("个人信息");
        CompoundButton.OnCheckedChangeListener CheckListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        next.setEnabled(true);
                    next.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_topline_selector));
                } else {
                    next.setEnabled(false);
                    next.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_bg));
                }
            }
        };
        cboxAgreement.setOnCheckedChangeListener(CheckListener);
    }

    @Override
    public void setPresenter(CreditPersonInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading() {
        context.showProgressWithNoMsg();
    }

    @Override
    public void hideLoading() {
        context.hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        presenter.clearTags();
        ButterKnife.unbind(this);//解除绑定，官方文档只对fragment做了解绑
        super.onDestroy();
    }

    @Override
    public void showToast(String str) {
        ToastUtil.toast(this,str);
    }

    @Override
    public void showRelatStr(String str) {
        relativesRelation.setText(str);//亲属关系
    }

    @Override
    public void initDialog() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.spinner_dialog, null);
         Spinner proviceSpinner= (Spinner) rootView.findViewById(R.id.province);
         Spinner citySpinner= (Spinner) rootView.findViewById(R.id.city);
        Spinner disticSppinner = (Spinner) rootView.findViewById(R.id.region);
        presenter.setCityi(rootView,proviceSpinner,citySpinner,disticSppinner);
    }

    @Override
    public void showUrgencyRelatStr(String str) {
        linkmanRelation.setText(str);//紧急联系人关系
    }

    @Override
    public void setTex(EditText ec, String str) {
        ec.setText(str);//亲属和紧急联系人电话号码
    }

    @Override
    public void setLiveCity(String str) {
        city.setText(str);
    }

    @Override
    public void setAtCity(String str) {
        place.setText(str);
    }


    @OnClick({R.id.city,R.id.city_img,R.id.place,R.id.place_img,R.id.next,R.id.relatives_tel_img,R.id.linkman_img,R.id.relatives_relation,R.id.relatives_relation_img,R.id.linkman_relation,R.id.linkman_relation_img})
    public void OnClick(View view) {
        switch (view.getId()){
            case R.id.next:
                presenter.btnNext(city,address,email,unitName,place,uintAddress,unitPhone,
                        contactName,relativesTel,relativesRelation,linkmanName,linkmanTel,linkmanRelation);
                break;
            case R.id.relatives_tel_img:
                presenter.contactChoose("1");//请选择亲属手机号码
                break;
            case R.id.linkman_img:
                presenter.contactChoose("2");//请选择紧急联系人手机号码
                break;
            case R.id.relatives_relation:
            case R.id.relatives_relation_img:
                presenter.showRelatRelation();//请选择亲属关系
                break;
            case R.id.linkman_relation:
            case R.id.linkman_relation_img:
                presenter.showUrgencyRelation();//请选择紧急联系人关系
                break;
            case R.id.city:
            case R.id.city_img:
                presenter.cityiChoose("3");//请选择紧急联系人关系
                break;
            case R.id.place:
            case R.id.place_img:
                presenter.cityiChoose("4");//请选择紧急联系人关系
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.actResult(requestCode,data,relativesTel,linkmanTel);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
