package com.lakala.elive.market.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.R;
import com.lakala.elive.beans.MerShopInfo;
import com.lakala.elive.beans.TermInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.MerShopReqInfo;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.market.activity.MerDetailActivity;
import com.lakala.elive.market.base.BaseFragment;

import java.util.List;

/**
 * 商户网点基础信息
 *
 * @author hongzhiliang
 */
public class MerBaseInfoFragment extends BaseFragment {

    private final String TAG = MerBaseInfoFragment.class.getSimpleName();

    TextView tvMerchantCode; //商户注册名称
    TextView tvMerchantName; //商户注册名称
    TextView tvShopNo; //网点名称
    TextView tvShopName; //网点名称
    TextView tvShopAddr;  //网点地址
    TextView tvShopStatus;  //网点状态

    TextView tvContactor; //网点联系人
    TextView tvTelNo; //网点电话
    TextView tvMobileNo; //网点电话
    TextView tvEmail; //邮箱地址

    MerShopInfo merchantInfo = null; //商户详情信息
    private LinearLayout phone;
    private LinearLayout telephone;

    public MerBaseInfoFragment() {

    }

    @SuppressLint("ValidFragment")
    public MerBaseInfoFragment(MerShopInfo merchantInfo) {
        this.merchantInfo = merchantInfo;
    }

    @Override
    protected void setViewLayoutId() {
        viewLayoutId = R.layout.fragment_mer_base_info;
    }

    @Override
    protected void bindViewIds() {
        tvMerchantCode = (TextView) mView.findViewById(R.id.tv_merchant_code);
        tvMerchantName = (TextView) mView.findViewById(R.id.tv_merchant_name);
        tvShopNo = (TextView) mView.findViewById(R.id.tv_shop_no);
        tvShopName = (TextView) mView.findViewById(R.id.tv_shop_name);
        tvShopAddr = (TextView) mView.findViewById(R.id.tv_shop_addr);
        tvShopStatus = (TextView) mView.findViewById(R.id.tv_shop_status);

        tvContactor = (TextView) mView.findViewById(R.id.tv_contactor);
        tvMobileNo = (TextView) mView.findViewById(R.id.tv_mobile_no);
        tvTelNo = (TextView) mView.findViewById(R.id.tv_tel_no);
        tvEmail = (TextView) mView.findViewById(R.id.tv_email);

        phone = (LinearLayout) mView.findViewById(R.id.ll_phone);
        telephone = (LinearLayout) mView.findViewById(R.id.ll_telephone);

    }

    @Override
    protected void initData() {
        handlerMerShopInfo(merchantInfo);
        queryMerShopInfoDetail();//查询数据
        setOnClick();
    }

    private void setOnClick() {
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tvPhone = tvMobileNo.getText().toString().trim();
                /*Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhone);
                intent.setData(data);
                startActivity(intent);*/
                if(TextUtils.isEmpty(tvPhone))return;
                if(mPopupWindow != null && mPopupWindow.isShowing()){
                    return;
                }
                popWindowForCall(tvPhone);

            }
        });
        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tvPtelephone = tvTelNo.getText().toString().trim();
                /*Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPtelephone);
                intent.setData(data);
                startActivity(intent);
*/
                if(TextUtils.isEmpty(tvPtelephone))return;
                if(mPopupWindow != null && mPopupWindow.isShowing()){
                    return;
                }
                popWindowForCall(tvPtelephone);
            }
        });
    }

    private void handlerMerShopInfo(MerShopInfo merShopInfo) {
        if(merShopInfo == null)return;
        tvMerchantCode.setText(merShopInfo.getMerchantCode());
        tvMerchantName.setText(merShopInfo.getMerchantName());

        tvShopNo.setText(merShopInfo.getShopNo());
        tvShopName.setText(merShopInfo.getShopName());
        tvShopAddr.setText(merShopInfo.getShopAddress());

//        tvShopStatus.setText(merShopInfo.getMerchantStatus());

        tvEmail.setText(merShopInfo.getEmail());

        if ("VALID".equals(merShopInfo.getMerchantStatus())) {
            tvShopStatus.setText("有效");
        } else if ("INVALID".equals(merShopInfo.getMerchantStatus())) {
            tvShopStatus.setText("无效");
        } else {
            tvShopStatus.setText("未知");
        }

        tvContactor.setText(merShopInfo.getContactor());
        tvTelNo.setText(merShopInfo.getTelNo());
        tvMobileNo.setText(merShopInfo.getTelNo1());

    }

    private MerShopReqInfo merShopReqInfo = new MerShopReqInfo(); //请求查询接口

    private void queryMerShopInfoDetail() {
        merShopReqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
        if(merchantInfo == null){
            return;
        }
        merShopReqInfo.setShopNo(merchantInfo.getShopNo());
        NetAPI.queryEliveMerShopInfoDetail(getActivity(), this, merShopReqInfo);
    }


    @Override
    public void refershUi() {
        queryMerShopInfoDetail();
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                handlerMerShopInfo((MerShopInfo) obj);
                break;
        }
        super.onSuccess(method, obj);
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_ELIVE_SHOP_INFO:
                break;
        }
    }

    private PopupWindow mPopupWindow;
    private void popWindowForCall(final String phoneNum){
        if(mPopupWindow == null){
            mPopupWindow = new PopupWindow(getActivity());
            mPopupWindow.setContentView(LayoutInflater.from(getActivity()).inflate(R.layout.pop_task_call_bottom_layout,null));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View tvPh = mPopupWindow.getContentView().findViewById(R.id.tv_pop_task_call_bottom_phonenum);
        if(tvPh != null){
            if(tvPh instanceof TextView){
                ((TextView)tvPh).setText("" + phoneNum);
            }
            tvPh.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String result = startCall(phoneNum);
                    if(!TextUtils.isEmpty(result)){
                        Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
                    }
                    if(mPopupWindow.isShowing()){
                        mPopupWindow.dismiss();
                    }
                }
            });
        }
        View cancelCall = mPopupWindow.getContentView().findViewById(R.id.btn_pop_task_call_bottom_cancel);
        if(cancelCall != null){
            cancelCall.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(mPopupWindow.isShowing()){
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        if(mPopupWindow.isShowing()){
            return;
        }

        mPopupWindow.showAtLocation(getView(), Gravity.BOTTOM,0,0);
    }
    private String startCall(String phoneNum){
        if(TextUtils.isEmpty(phoneNum))return "电话号码不能为空!";
        phoneNum = phoneNum.replace(" ","");
        if(phoneNum.length()<3)return "号码不存在!";
        if(!phoneNum.matches("^\\+?\\d+$"))return "号码不正确!";
        Intent tell = new Intent(Intent.ACTION_DIAL);
        tell.setData(Uri.parse("tel://" + phoneNum));
        startActivity(tell);
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mPopupWindow!= null){
            if(mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }
            mPopupWindow = null;
        }
    }
}
