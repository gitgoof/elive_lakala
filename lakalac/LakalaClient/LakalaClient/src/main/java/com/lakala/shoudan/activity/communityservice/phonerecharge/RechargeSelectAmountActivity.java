package com.lakala.shoudan.activity.communityservice.phonerecharge;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MobileChargeCard;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.PhoneUtils;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.ContactBookUtil;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.component.PhoneNumberInputWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by HUASHO on 2015/1/22.
 * 手机充值，选择充值金额页面
 */
public class RechargeSelectAmountActivity extends BasePwdAndNumberKeyboardActivity {
    /**取电话号码图标*/
    private ImageButton phoneImg;
    /**电话号码*/
    private EditText phoneEdit;
    /**确认电话号码*/
    private EditText editPhoneAgain;
    /**电话号码归属地*/
    private TextView phoneAttribution;
    //手机号码归属地
    private String bureau;
    //下一步
    private Button nextStepButton;
    private LinearLayout radioGroup;
    private LinearLayout radioGroup1;
    private ViewGroup viewGroup;
    private List<MobileChargeCard> rechargeList = new ArrayList<MobileChargeCard>();
    private List<RadioButton> lstRadioButton = new ArrayList<RadioButton>();

    private int firstLineSize  = 0;
    private int secondLineSize = 0;
    private final int itemPerLine = 5;
    //最终选择的id
    private int lastCheckId;

    private boolean isFromHistory = false;
    private boolean isFromPhoneBook = false;
    private String phone = "";
    private View layoutAgain;
    private View ivSeperator;
    private ContactBookUtil contactBookUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_select_amount);
        initUI();
        requestData();
        getMobileBureau();
    }

    protected void initUI(){
        viewGroup = (ViewGroup) findViewById(R.id.group);
        //设置自定义键盘“完成键”事件
        setOnDoneButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.requestFocus();
            }
        });
        navigationBar.setTitle(R.string.recharge_money);
        navigationBar.setActionBtnText(R.string.history_data);
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        //调用通讯录按钮
        phoneImg                =(ImageButton)findViewById(R.id.img_btn_u);
        phoneEdit				=(EditText)findViewById(R.id.edit_phone);
        editPhoneAgain = (EditText) findViewById(R.id.edit_phoneagain);
        radioGroup 				= (LinearLayout)findViewById(R.id.id_sjcz1_amount_radio_group);
        radioGroup1 			= (LinearLayout)findViewById(R.id.id_sjcz1_amount_radio_group1);
        nextStepButton      	=(Button) findViewById(R.id.id_common_guide_button);
        layoutAgain = findViewById(R.id.layout_again);
        ivSeperator = findViewById(R.id.iv_seperator);
        phoneAttribution        = (TextView) findViewById(R.id.tv_phone_attribution);
        phoneEdit.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneEdit.setText(getIntent().getStringExtra(UniqueKey.phoneNumber));
        phoneEdit.addTextChangedListener(new PhoneNumberInputWatcher(new PhoneNumberInputWatcher.CallBackPhoneInterface() {
            @Override
            public void callBackPhone(String text, int length) {
                if (length == 0) {
                    isInputValid();
                    setRadioButtonGray();
                } else{
                    text = Util.formatString(text);
                    length = text.length();
                    if(length != 11){
                        return;
                    }
                    if (!isInputValid()) {
                        setRadioButtonGray();
                    } else {
                        setRadioButtonBlue();
                        phone = Util.formatString(phoneEdit.getText().toString());
                        getMobileBureau();
                    }
                }
            }
        }));
        phoneEdit.setLongClickable(false);
        //键盘
        initNumberKeyboard();
        initNumberEdit(phoneEdit, CustomNumberKeyboard.EDIT_TYPE_INTEGER, 13);
        nextStepButton.setOnClickListener(this);
        phoneImg.setOnClickListener(this);
        InputFilter.LengthFilter[] filters2={new InputFilter.LengthFilter(13)};
        editPhoneAgain.setFilters(filters2);
        editPhoneAgain.addTextChangedListener(new PhoneNumberInputWatcher());
        initOwnersPhone();
    }

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView) findViewById(R.id.id_input_scroll));
    }


    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {

            if (navBarItem == NavigationBar.NavigationBarItem.back){
               finish();
            }else if (navBarItem == NavigationBar.NavigationBarItem.action){
                startActivityForResult(new Intent(RechargeSelectAmountActivity.this, RechargeHistoryRecordDataActivity.class), 1988);
            }
        }
    };

    /**
     * 列出金额列表
     */
    private void listRechargeAmount(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        LinearLayout.LayoutParams lpRadioButton = new LinearLayout.LayoutParams((int)(dm.density*80), (int)(dm.density*40));
        lpRadioButton.setMargins((int) (dm.density * 15), 0, (int) (dm.density * 15), 0);
        int sum = rechargeList.size();
        firstLineSize  = sum > itemPerLine ? itemPerLine : sum;
        secondLineSize = sum > itemPerLine ? sum - itemPerLine : 0;
        for (int i = 0; i < sum; i++) {
            RadioButton radioButton = (RadioButton)RadioButton.inflate(this, R.layout.radio_button, null);
            radioButton.setLayoutParams(lpRadioButton);
            radioButton.setText(rechargeList.get(i).getCardName() + "元");
            radioButton.setId(i);
            radioButton.setTag("radioButtonTag");
            radioButton.setOnClickListener(this);
            lstRadioButton.add(radioButton);

            //第一行
            if (i < itemPerLine) {
                setRadioButtonBackground(radioButton, i, firstLineSize,false);
            }else if ((i-itemPerLine) < itemPerLine) { //第二行
                setRadioButtonBackground(radioButton, i-itemPerLine,secondLineSize,false);
            }

            //设置默认选择的金额
            if ( i == 1) {
                setRadioButtonBackground(radioButton, i,firstLineSize, true);
                lastCheckId = i;
            }

            //超出一行显示容量，则显示在第二行
            if (i < itemPerLine) {
                radioGroup.addView(radioButton);
                if (i < itemPerLine-1 && i < rechargeList.size()-1 && rechargeList.size() != 1) {
                    addDivideLine(radioGroup);
                }
            }else {
                radioGroup1.addView(radioButton);
                if ((i-itemPerLine) < itemPerLine-1 && (rechargeList.size() - itemPerLine) != 1) {
                    addDivideLine(radioGroup1);
                }
            }
        }
    }
    /**
     * 设置RadioButton为置灰状态
     */
    private void setRadioButtonGray(){
        if(lstRadioButton==null)return;
        for(RadioButton radioButton : lstRadioButton){
            radioButton.setBackgroundResource(R.drawable.radio_button_gray_bg);
            radioButton.setTextColor(getResources().getColor(R.color.disable_btn_bg));
            radioButton.setEnabled(false);
        }

    }

    /**
     * 设置RadioButton为高亮状态
     */
    private void setRadioButtonBlue(){
        if(lstRadioButton==null)return;
        for(int i = 0; i < lstRadioButton.size(); i++){
            lstRadioButton.get(i).setBackgroundResource(R.drawable.radio_button_blue_bg);
            lstRadioButton.get(i).setTextColor(getResources().getColor(R.color.common_blue_color));
            lstRadioButton.get(i).setEnabled(true);
            //设置默认选择的金额
            if(i == firstLineSize/2){
                onRadioButtonChanged(i);
            }
        }
    }
    private void initOwnersPhone(){
        isFromHistory = true;
        isFromPhoneBook = false;
        goneView();

        phone=ApplicationEx.getInstance().getSession().getUser().getLoginName();
        phoneEdit.setText(phone);
        phoneEdit.setSelection(phoneEdit.getText().toString().length());
    }

    private void goneView() {
        layoutAgain.setVisibility(View.GONE);
        ivSeperator.setVisibility(View.GONE);
    }


    /**
     * 画分隔符
     * @param layout 一行的容器
     */
    private void addDivideLine(LinearLayout layout){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.dividing_lines);
        imageView.setVisibility(View.GONE);
        layout.addView(imageView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String tag = (String)v.getTag();
        //如果点击的是radioButton
        if ("radioButtonTag".equals(tag)) {
            int id = v.getId();
            onRadioButtonChanged(id);
        }else {
            switch (v.getId()) {
                case R.id.id_common_guide_button:
                    nextStep();
                    break;
                case R.id.img_btn_u:
                    ContactBookUtil.onContactBookClick(RechargeSelectAmountActivity.this);
                    break;
                default:
                    break;
            }
        }
    }
    protected void nextStep() {
        if (firstLineSize == 0) {
            ToastUtil.toast(RechargeSelectAmountActivity.this, getResources().getString(R.string.no_get_recharge_money));
            return;
        }
        if (isInputValid()) {
            MobileChargeCard rechargeInfo = rechargeList.get(lastCheckId);
            MobileRechargeInfo info = new MobileRechargeInfo();
            info.setPhoneNumber(phone);
            info.setChargeCard(rechargeInfo);
            Intent intent = new Intent(this, RechargePaymentActivity.class);
            intent.putExtra(UniqueKey.TRANS_INFO, info);
            startActivity(intent);
        }

    }

    /**
     * 切换当前选择的RadioButton到点击的RadioButton
     * @param checkedId
     */
    private void onRadioButtonChanged(int checkedId){
        RadioButton radioButton = null;
        int currentLineSize = 0;
        if (checkedId > itemPerLine - 1) {
            //第二行的RadioButton
            int index = checkedId - itemPerLine;
            currentLineSize = secondLineSize;
            radioButton = (RadioButton)radioGroup1.getChildAt(index*2);
        }else {
            //第一行的RadioButton
            currentLineSize = firstLineSize;
            radioButton = (RadioButton)radioGroup.getChildAt(checkedId*2);
        }
        radioButton.setChecked(true);
        //设置当前选中状态
        int currentCheckId = checkedId;

        checkedId = (checkedId > (itemPerLine - 1)) ? (checkedId - itemPerLine) : checkedId;
        setRadioButtonBackground(radioButton, checkedId, currentLineSize, true);
        //取消先前选中的状态
        if (lastCheckId != currentCheckId) {
            RadioButton previousCheckedButton = null;
//			int previousSize = 0;
            if (lastCheckId > itemPerLine - 1) { //之前选择的是第二行的
                int index = lastCheckId - itemPerLine;
                previousCheckedButton = (RadioButton)radioGroup1.getChildAt(index*2);
                setRadioButtonBackground(previousCheckedButton,index,secondLineSize,false);
            }else {
                previousCheckedButton = (RadioButton)radioGroup.getChildAt(lastCheckId*2);
                setRadioButtonBackground(previousCheckedButton,lastCheckId,firstLineSize,false);
            }

        }
        lastCheckId = currentCheckId;
    }

    /**
     * 设置RadioButton的背景图
     * @param radioButton
     * @param position radioButton在radioGroup中的位置
     * @param size 本行显示多少个RadioButton
     * @param isChecked true，选中状态；false，非选中状态
     */
    private void setRadioButtonBackground(RadioButton radioButton,int position,int size,boolean isChecked){
        if (position== 0) {
            //本行只有一个充值金额
            if (size == 1) {
                if (isChecked) {
                    radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg_down);
                    radioButton.setTextColor(getResources().getColor(R.color.white));
                }else {
                    radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg);
                    radioButton.setTextColor(getResources().getColor(R.color.common_blue_color));
                }
            }else {
                if (isChecked) {
                    radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg_down);
                    radioButton.setTextColor(getResources().getColor(R.color.white));
                }else {
                    radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg);
                    radioButton.setTextColor(getResources().getColor(R.color.common_blue_color));
                }
            }
        }else if (position == size - 1) {
            if (isChecked) {
                radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg_down);
                radioButton.setTextColor(getResources().getColor(R.color.white));
            }else {
                radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg);
                radioButton.setTextColor(getResources().getColor(R.color.common_blue_color));
            }
        }else {
            if (isChecked) {
                radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg_down);
                radioButton.setTextColor(getResources().getColor(R.color.white));
            }else {
                radioButton.setBackgroundResource(R.drawable.radio_button_blue_bg);
                radioButton.setTextColor(getResources().getColor(R.color.common_blue_color));
            }
        }
    }


    /**
     * 调用系统通讯录之后，返回的结果在这里处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode ==1988) {
                //为了使用从通讯录取值的手机号修改后重新确认号码，拷贝从通讯录取值的以下两句
                isFromHistory = true;
                isFromPhoneBook = false;
                goneView();
                phone = data.getStringExtra("phone");
                phoneEdit.setText(phone);
                phoneEdit.setSelection(phoneEdit.getText().toString().length());
            }else {//从通讯录中获取电话号码
                goneView();
                isFromHistory = false;
                isFromPhoneBook = true;
//                hideSurePhoneNumberView();
                contactBookUtil = new ContactBookUtil(this, phoneEdit, data);
                contactBookUtil.setOnPhoneNumberSelectedListener(new ContactBookUtil.OnPhoneNumberSelectedListener() {
                    @Override
                    public void onPhoneNumberSelected(String phoneNumber) {
                        String phoneNum = StringUtil.formatString(phoneNumber);

                        if (!PhoneNumberUtil.checkPhoneNumber(phoneNum)) {
                            ToastUtil.toast(context, "此联系人没有可供使用的手机号码，请重新选择", Toast.LENGTH_LONG);
                        } else {
                            phoneEdit.setText(phoneNum);
                            phoneEdit.setSelection(phoneEdit.getText().toString().length());
                            phone = phoneNumber;
                        }
                    }
                    @Override
                    public void onNoNumber(String hint) {
                    }
                });

            }
        }
    }
    /**
     * 获取手机号码归属地
     * @param
     */
    protected void getMobileBureau() {
//        showProgressWithNoMsg();
        ServiceResultCallback serviceResultCallback=new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
//                hideProgressDialog();
                try {
                    if(resultServices.isRetCodeSuccess()){
                        JSONObject jsonObject = new JSONObject( resultServices.retData);
                        bureau = jsonObject.getString("ext1");
                        if(!Util.isEmpty(bureau)) {
                            phoneAttribution.setText(bureau);
                            phoneAttribution.setTextColor(getResources().getColor(R.color.has_input_edittext_color));
                            phoneAttribution.setVisibility(View.VISIBLE);
                        }
                    }else{
                        final String errMsg = resultServices.retMsg;
                        phoneAttribution.setText(errMsg);
                        phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                        phoneAttribution.setVisibility(View.VISIBLE);
                        setRadioButtonGray();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
//                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
                phoneAttribution.setText("");
                phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                phoneAttribution.setVisibility(View.VISIBLE);
            }
        };
        CommonServiceManager commonServiceManager = CommonServiceManager.getInstance();
        commonServiceManager.getMobileBureau(phone,serviceResultCallback);

    }

    protected boolean isInputValid() {
        boolean isInputValid = false;
        boolean isPhoneFromBookEdit = false;
        String phoneString = phoneEdit.getText().toString();
        //由于手机号格式化后，中间加‘-’字符，需要去除后再做比较
        phoneString = Util.formatString(phoneString).trim();
        phone = phoneString;
        String repeatPhoneString = editPhoneAgain.getText().toString();
        repeatPhoneString = Util.formatString(repeatPhoneString).trim();

        //从电话薄得到的号码,且不为空,且做了修改,则显示重复输入框
/*		if (isFromPhoneBook && !Util.isEmpty(phoneString) && !contactBookHandler.numberFromPhoneBook.equals(phoneString)) {
			visibleView();
//			editPhoneAgain.setText(contactBookHandler.numberFromPhoneBook);
			editPhoneAgain.setText("");
			Util.toast(getString(R.string.toast_phone_changed));
			isPhoneFromBookEdit = true;
			isFromPhoneBook = false;
		}

		if (isFromHistory && !Util.isEmpty(phoneString) && !phone.equals(phoneString)) {
			visibleView();
//			editPhoneAgain.setText(phone);
			editPhoneAgain.setText("");
			Util.toast(getString(R.string.toast_phone_changed));
			isPhoneFromBookEdit = true;
			isFromPhoneBook = false;
			isFromHistory = false;
		}*/

        if (!isPhoneFromBookEdit) {
            if (Util.isEmpty(phoneEdit.getText().toString())) {
                ToastUtil.toast(context, getString(R.string.toast_phone_empty));
                phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                phoneAttribution.setText(getString(R.string.toast_phone_empty));
            }else if (!PhoneUtils.checkPhoneNumber(Util.formatString(phoneString))) {
                ToastUtil.toast(context,getString(R.string.toast_phone_length_error));
                phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                phoneAttribution.setText(getString(R.string.toast_phone_length_error));
            }else if (layoutAgain.getVisibility() == View.VISIBLE) {  //确认栏是否显示
                if (Util.isEmpty(repeatPhoneString)) {
                    ToastUtil.toast(context,getString(R.string.toast_phone_confirm));
                    phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                    phoneAttribution.setText(getString(R.string.toast_phone_confirm));
                }else if (!repeatPhoneString.equals(phoneString)) {
                    ToastUtil.toast(context,getString(R.string.toast_phone_not_equal));
                    phoneAttribution.setTextColor(getResources().getColor(R.color.amount_color));
                    phoneAttribution.setText(getString(R.string.toast_phone_not_equal));
                }else {
                    isInputValid = true;
                }
            }else {  //没有确认栏的话，只要手机号合法即可
                isInputValid = true;
            }
        }

        return isInputValid;
    }

    private void initData(String retData) {
        try {
            JSONArray jsonArray = new JSONArray(retData);
            int length = 0;
            if (jsonArray != null) {
                length = jsonArray.length();
            }
            MobileChargeCard[] arrays = new MobileChargeCard[length];
            int index = 0;
            for (int i = 0; i < length; i++) {
                String json = jsonArray.getString(i);
                MobileChargeCard card = null;
                try {
                    card = JSON.parseObject(json, MobileChargeCard.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (card == null) {
                    continue;
                }
                arrays[index] = card;
                index++;
            }
            arrays = Arrays.copyOf(arrays, index);
            Arrays.sort(arrays, new Comparator<MobileChargeCard>() {
                @Override
                public int compare(MobileChargeCard lhs, MobileChargeCard rhs) {
                    int lhsOrder = lhs.getCardOrder();
                    int rhsOrder = rhs.getCardOrder();
                    return lhsOrder - rhsOrder;
                }
            });
            rechargeList.addAll(Arrays.asList(arrays));
            listRechargeAmount();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求数据
     */
    private void requestData(){
        BusinessRequest request = RequestFactory
                .getRequest(this, RequestFactory.Type.MOBILE_CHARGE_AMOUNT);
        //TODO
//        request.setAutoShowProgress(true);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    initData(resultServices.retData);
                }else{
                    String msg = resultServices.retMsg;
                    if (TextUtils.isEmpty(msg)){
                        msg = resultServices.retMsg;
                    }
                    ToastUtil.toast(RechargeSelectAmountActivity.this,msg);
                    setRadioButtonGray();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(RechargeSelectAmountActivity.this,getString(R.string.socket_fail));
                setRadioButtonGray();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        request.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPoint();
    }

    /**
     * 埋点
     */
    public void initPoint(){
        if(PublicEnum.Business.isHome()){
        }else if(PublicEnum.Business.isDirection()){
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Phone_Recharge_De,this);
        }else if(PublicEnum.Business.isAd()){
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Phone_Recharge_Ad,this);
        }else if(PublicEnum.Business.isPublic()){
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Phone_Recharge_Public,this);
        }else {
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Phone_Recharge_Succes,this);
        }
    }

}
