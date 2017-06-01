package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.swiper.mts.DialogController;
import com.lakala.ui.common.ListUtil;
import com.lakala.ui.dialog.mts.AlertDialog;

import java.util.List;

/**
 * 支付方式选择器
 * Created by xyz on 14-1-19.
 */
public class PaymentTypeSelector implements View.OnClickListener,AdapterView.OnItemClickListener{

    private Context context;

    //支付方式选择监听器
    private PaymentTypeSelectListener paymentTypeSelectListener;

    //添加银行卡区域
    private View footerView;

    private List<PaymentType> paymentTypes;
    private String title;

    public PaymentTypeSelector(Context context,String title,List<PaymentType> paymentTypes,PaymentTypeSelectListener paymentTypeSelectListener){
        this.context = context;
        this.title = title;
        this.paymentTypes = paymentTypes;
        this.paymentTypeSelectListener = paymentTypeSelectListener;
    }

    /**
     * 显示支付方式选择器
     */
    public void show(){

        //初始化支付方式布局
        View contentView = View.inflate(context, R.layout.plat_layout_pay_switch, null);

        ListView payTypeList = (ListView)contentView.findViewById(R.id.payment_type_list);

//        //构建测试数据
//        paymentTypes = new ArrayList<PaymentType>();
//        PaymentType paymentType = new PaymentType(EPaymentType.BALANCE_PAY,"余额支付");
//        paymentTypes.add(paymentType);
//
//        paymentType = new PaymentType(EPaymentType.QUICK_PAY,"招商银行储蓄卡（尾号1234）");
//        paymentTypes.add(paymentType);
//
//        paymentType = new PaymentType(EPaymentType.QUICK_PAY,"招商银行储蓄卡（尾号1234）");
//        paymentTypes.add(paymentType);
//
//        paymentType = new PaymentType(EPaymentType.QUICK_PAY,"招商银行储蓄卡（尾号1234）");
//        paymentTypes.add(paymentType);
//
//        paymentType = new PaymentType(EPaymentType.SWIPE_PAY,"刷卡支付");
//        paymentTypes.add(paymentType);

//        //添加footer
//        footerView = View.inflate(context,R.layout.plat_layout_pay_select_item,null);
//        TextView textView = (TextView)footerView.findViewById(R.id.pay_type_text);
//        textView.setText("添加银行卡");
//        footerView.setOnClickListener(this);
//        payTypeList.addFooterView(footerView);

        PaymentTypeAdapter paymentTypeAdapter = new PaymentTypeAdapter(context,paymentTypes);
        payTypeList.setAdapter(paymentTypeAdapter);
        payTypeList.setOnItemClickListener(this);
        setListViewHeightDependOnChildren(payTypeList);

        DialogController.getInstance().showAlertDialog(
                (FragmentActivity)context,
                0,
                title,
                contentView,
                "",
                "",
                "取消",
                alertDialogClickListener,
                false);
    }

    /**
     * list item点击事件回调
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DialogController.getInstance().dismiss();

//        EPaymentType ePaymentType = paymentTypes.get(position).ePaymentType;
//        if (ePaymentType!=null){
//            if (ePaymentType == EPaymentType.BALANCE_PAY){
//                paymentTypeSelectListener.onBalancePaySelected();
//            }else if (ePaymentType == EPaymentType.QUICK_PAY){
//                paymentTypeSelectListener.onQuickPaySelected(null);
//            }else if (ePaymentType == EPaymentType.SWIPE_PAY){
//                paymentTypeSelectListener.onSwipePaySelected();
//            }
//        }
        paymentTypeSelectListener.onItemSelected(position);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(footerView)){
            //跳转到添加银行卡页面
            ToastUtil.toast(context, "add card");
            paymentTypeSelectListener.onAddQuickCardSelected();
        }
    }

    /**
     * 取消按钮点击事件
     */
    private AlertDialog.Builder.AlertDialogClickListener alertDialogClickListener = new AlertDialog.Builder
            .AlertDialogClickListener() {
                @Override
                public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
                    if (typeEnum == AlertDialog.Builder.ButtonTypeEnum.MIDDLE_BUTTON){
                        alertDialog.dismiss();
                        paymentTypeSelectListener.onCancel();
                    }
                }
    };

    /**
     * 根据listview中item个数动态控制listView的高度
     * 这里控制ListView的高度为5个Item的高度
     * @param listView
     */
    private void setListViewHeightDependOnChildren(ListView listView){

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) return;

        int count = listAdapter.getCount();
        //最高5个item高度，因为加了一个footerView 所以ListView的内容最多为4个
        if (count <= 5) return;

        ListUtil.setHeightBaseOnDisplayChildren(listView, 5);

    }

    /**
     * 支付方式实体类
     */
    public static class PaymentType{

        public PaymentType(EPaymentType ePaymentType,String description){
            this.ePaymentType = ePaymentType;
            this.description = description;
        }

        public PaymentType(){
        }

        //支付类型
        private EPaymentType ePaymentType;


        public void setePaymentType(EPaymentType ePaymentType) {
            this.ePaymentType = ePaymentType;
        }

        public EPaymentType getPaymentType() {
            return ePaymentType;
        }

        //支付类型描述
        private String description;

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 支付方式适配器
     */
    private class PaymentTypeAdapter extends BaseListAdapter<PaymentType> {

        public PaymentTypeAdapter(Context context,List<PaymentType> datas){
            mContext = context;
            mdatas = datas;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        protected View bindView(PaymentType data, int position, View currentView, ViewGroup root) {
            Holder holder = null;

            if (currentView == null){
                currentView = mInflater.inflate(R.layout.plat_layout_pay_select_item,null);
                holder = new Holder();
                holder.textView = (TextView)currentView.findViewById(R.id.pay_type_text);

                currentView.setTag(holder);

            }else {
                holder = (Holder)currentView.getTag();
            }

            //设置支付方式描述
            holder.textView.setText(data.description);

            return currentView;
        }

        private class Holder{
            public TextView textView;
        }
    }

}
