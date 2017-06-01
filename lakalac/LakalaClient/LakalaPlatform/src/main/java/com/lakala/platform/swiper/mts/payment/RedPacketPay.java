package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.R;
import com.lakala.platform.swiper.mts.DialogController;
import com.lakala.ui.common.CommmonSelectData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 红包支付
 * Created by xyz on 14-1-5.
 */
public class RedPacketPay extends Payment implements View.OnClickListener{

    //红包支付item
    private View layoutView;

    //红包支付金额
    private TextView amountText;

    //红包支付是否有效，根据是用户是否有红包
    private boolean isAvailable = true;

    private RedPacketListener redPacketListener;

    //红包数据列表
    private List<RedPacket> redPackets = new ArrayList<RedPacket>();

    //选中的item索引列表
    private ArrayList<Integer> selectedIndexList = new ArrayList<Integer>();

    /**
     * 红包事件监听器
     */
    public interface RedPacketListener{
        /**
         * 红包选择完成之后，不管是选择使用，还是选择不使用，都回调此方法
         */
        public void onRedPacketSelected();
    }

    public RedPacketPay(Context context) {
        super(context);
        setPaymentType(EPaymentType.RED_PACKET_PAY);
        initRedPackets();

    }

    @Override
    public void setPaymentAmount(double paymentAmount) {
        super.setPaymentAmount(paymentAmount);
        amountText.setText(formatAmountToString());
    }

    public void setRedPacketListener(RedPacketListener redPacketListener) {
        this.redPacketListener = redPacketListener;
    }

    /**
     * 是否支持红包支付
     * @return
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * 设置是否支持红包
     * @param isAvailable
     */
    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public View loadView() {
        layoutView = View.inflate(context, R.layout.plat_layout_red_packet_pay, null);
        layoutView.setOnClickListener(this);

        amountText = (TextView)layoutView.findViewById(R.id.red_packet_pay_amount);

        setPaymentAmount(0);
        return layoutView;
    }

    @Override
    public Map<String, String> getPaymentParams() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void onClick(View view) {
        //弹出红包选择框
        ArrayList<CommmonSelectData> datas = new ArrayList<CommmonSelectData>();
        for (int index=0;index < redPackets.size();index++){
            RedPacket redPacket = redPackets.get(index);

            CommmonSelectData commmonSelectData = new CommmonSelectData();
            commmonSelectData.setLeftTopText(redPacket.getDescription());

            if (selectedIndexList.contains(index))
                commmonSelectData.setSelected(true);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("到期时间：yyyy-MM-dd");
            commmonSelectData.setLeftBottomText(simpleDateFormat.format(redPacket.getExpiredDate()));

            datas.add(commmonSelectData);
        }

        DialogController.getInstance().showMutiSelectDialog((FragmentActivity)context,"选择红包",datas,dialogConfirmClick);

    }

    private DialogController.DialogConfirmClick dialogConfirmClick = new DialogController.DialogConfirmClick() {
        @Override
        public void onDialogConfirmClick(Object data) {

            selectedIndexList = (ArrayList<Integer>)data;

            //先将红包抵扣金额设置为0，然后根据选择设置红包抵扣金额
            paymentAmount = 0;

            for (int index : selectedIndexList){
                paymentAmount += redPackets.get(index).getAmount();
            }

            setPaymentAmount(paymentAmount);
            redPacketListener.onRedPacketSelected();
        }

        @Override
        public void onDialogCancelClick() {
        }
    };

    private void initRedPackets(){
        Date date = new Date();
        for (int i=0;i<5;i++){
            RedPacket redPacket = new RedPacket(i+1,i+1+"元彩票红包",date,true);
            redPackets.add(redPacket);
        }
    }

}
