package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.DimenUtil;
import com.lakala.platform.R;
import com.lakala.platform.swiper.mts.SwipeLauncher;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付方式控制器
 * Created by xyz on 14-1-5.
 */
public class PaymentController implements PaymentTypeSelectListener{

    private Context context;

    //展示支付方式的容器
    private LinearLayout paymentContainer;

    //支付总金额
    private double totalAmount = 1000;

    //支付方式指示
    private TextView paymentTypeText;

    //红包支付，红包支付和其他三种支付方式可以合并使用
    private RedPacketPay redPacketPay;

    //支付方式，余额支付，快捷支付，刷卡支付三者之一
    private Payment payment;

    //保存实例化之后的支付方式
    private Map<EPaymentType,Payment> paymentMap = new HashMap<EPaymentType, Payment>();

    //当前选择的支付方式
    private EPaymentType currentPaymentType = null;

    //支付方式view
    private View paymentLayout;

    //刷卡启动器，控制刷卡器逻辑处理
    private SwipeLauncher swipeLauncher;


    public PaymentController(Context context,LinearLayout paymentContainer){
        this.context = context;
        this.paymentContainer = paymentContainer;

        paymentTypeText = (TextView)paymentContainer.findViewById(R.id.payment_type_text);
    }

    /**
     * 加载默认支付方式UI
     */
    public void loadDefaultPaymentView(){

        //初始化红包支付对象
        redPacketPay = new RedPacketPay(context);

        //加载红包支付ui
        if (redPacketPay.isAvailable()){
            paymentContainer.addView(redPacketPay.loadView());
            redPacketPay.setRedPacketListener(redPacketListener);
        }

        payment = new BalancePay(context);

        setCurrentPayment();
    }

    /**
     * 显示支付方式选择框
     */
    public void showPayTypeSelector(){
//        PaymentTypeSelector paymentTypeSelector = new PaymentTypeSelector(context,this);
//        paymentTypeSelector.show();
    }

    /**
     * 设置当前支付方式
     */
    public void setCurrentPayment(){

        //如果支付方式没有发生改变，则不进行处理.如果支付方式为null，则说明是第一次加载，继续进行
        if (currentPaymentType != null && payment.getPaymentType() == currentPaymentType) return;

        //重新选择了支付方式，先移除老的ui
        paymentContainer.removeView(paymentLayout);

        //添加的支付方式和红包支付ui的间距
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int topPx = DimenUtil.dp2px(context, 10);
        layoutParams.setMargins(0,topPx,0,0);

        //设置当前支付模式
        currentPaymentType = payment.getPaymentType();

        paymentTypeText.setText(payment.getPaymentLabel());
        paymentLayout = payment.loadView();
        paymentContainer.addView(paymentLayout);
        setPaymentAmount();
    }

    /**
     * 选择完支付方式之后，点击确认按钮，执行此方法
     * 根据不同的支付方式，进行不同的处理
     */
    public void confirm(){
        //如果为刷卡支付，则启动刷卡器
        if (currentPaymentType == EPaymentType.SWIPE_PAY){

//            swipeLauncher = SwipeLauncher.getInstance();
//            swipeLauncher.setSwipeListener((SwipeLauncher.SwipeListener)context);
//            swipeLauncher.setActivity((FragmentActivity)context);
//            swipeLauncher.launch();
        }else {
        }
    }

    /**
     * 红包支付，红包选择后的事件监听器
     */
    private RedPacketPay.RedPacketListener redPacketListener = new RedPacketPay.RedPacketListener() {
        @Override
        public void onRedPacketSelected() {
            setPaymentAmount();
        }
    };

    /**
     * 设置当前支付方式的支付金额
     * 总金额-红包抵扣金额
     */
    private void setPaymentAmount(){

        double realPayAmount = redPacketPay.isAvailable() ? (totalAmount-redPacketPay.getPaymentAmount()):totalAmount;

        payment.setPaymentAmount(realPayAmount);
    }

    /**
     * 获取支付方式，如果是第一次被选择，则实例化一个新的实例
     * @param paymentType
     * @return
     */
    private Payment getPayment(EPaymentType paymentType){
        Payment returnPayment = paymentMap.get(paymentType);

        if (returnPayment == null){
            if (paymentType == EPaymentType.BALANCE_PAY){
                returnPayment = new BalancePay(context);
            }else if (paymentType == EPaymentType.QUICK_PAY){
                returnPayment = new QuickPay(context);
            }else if (paymentType == EPaymentType.SWIPE_PAY){
                returnPayment = new SwipePay(context);
            }

            if (returnPayment.getPaymentType() != EPaymentType.SWIPE_PAY && swipeLauncher != null){
                swipeLauncher.stopMonitor();
                swipeLauncher = null;
            }

            paymentMap.put(paymentType,returnPayment);
        }

        return returnPayment;
    }

    public void onDestroy(){
        if (swipeLauncher != null)
            swipeLauncher.onDestroy();
    }

    @Override
    public void onBalancePaySelected() {
        //加载余额支付ui
        payment = getPayment(EPaymentType.BALANCE_PAY);
        setCurrentPayment();
    }

    @Override
    public void onQuickPaySelected(QuickCard quickCard) {
        //加载快捷支付ui
        payment = getPayment(EPaymentType.QUICK_PAY);
        setCurrentPayment();
        ((QuickPay)payment).setQuickPayLabel("招商银行（0123）");
    }

    @Override
    public void onSwipePaySelected() {
        //加载刷卡支付ui
        payment = getPayment(EPaymentType.SWIPE_PAY);
        setCurrentPayment();
    }

    @Override
    public void onAddQuickCardSelected() {

    }

    @Override
    public void onItemSelected(Object object) {

    }

    @Override
    public void onCancel() {

    }
}
