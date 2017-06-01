package com.lakala.shoudan.activity.shoudan.bankitcard.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardAdditionActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardEntranceActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.SMSVerificationActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.BankCardBean;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.IconItemView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HJP on 2015/11/20.
 */
public class BankCardListAdapter extends BaseAdapter{
     FragmentActivity context;
    List<BankCardBean> bankCardBeanList=new ArrayList<BankCardBean>();
    public BankCardListAdapter(FragmentActivity context, List<BankCardBean> creditCardBeans){
        this.context=context;
        this.bankCardBeanList=creditCardBeans;
    }
    @Override
    public int getCount() {
        return bankCardBeanList==null?0:bankCardBeanList.size();
    }

    @Override
    public BankCardBean getItem(int position) {
        return bankCardBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_bank_card_entrance,null);
            viewHolder.ivBankIcon = (ImageView)convertView. findViewById(R.id.iv_bank_icon);
            viewHolder.tvBankName = (TextView)convertView. findViewById(R.id.tv_bank_name);
            viewHolder.tvType = (TextView)convertView. findViewById(R.id.tv__type);
            viewHolder.tvBankNumber = (TextView)convertView. findViewById(R.id.tv_bank_number);
            viewHolder.ivDelete = (ImageView)convertView. findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);

        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        BankCardBean bankCardBean;
        if(bankCardBeanList.size()!=0) {
            bankCardBean = getItem(position);
            bankCardBean.setPosition(position);
            dealData(viewHolder,bankCardBean);
        }
        return convertView;
    }
    public void dealData(ViewHolder viewHolder,final BankCardBean bankCardBean){
        //设置icon
        Bitmap bitmap = com.lakala.library.util.ImageUtil.getBitmapInAssets(context,
                "bank_icon/" + bankCardBean.getBankCode()+ "" +
                        ".png");
        viewHolder.ivBankIcon.setImageBitmap(bitmap);

        viewHolder.tvBankName.setText(bankCardBean.getBankName());
        if((bankCardBean.getAccountType().equals("1"))){
            viewHolder.tvType.setText("储蓄卡");
        }else{
            viewHolder.tvType.setText("信用卡");
        }
        viewHolder.tvBankName.setText(bankCardBean.getBankName());
        viewHolder.tvBankNumber.setText(Util.formatCardNumberWithStar(bankCardBean.getAccountNo()));
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreator.createFullContentDialog(context, "取消", "确认", "您是否删银行卡，银行卡列表将不再显示，您确定继续么？"
                        , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(context, SMSVerificationActivity.class);
                        intent.putExtra(Constants.IntentKey.BANK_CARD_BEAN,bankCardBean);
                        intent.putExtra(Constants.IntentKey.SIGN_OR_UNSIGN,"unsign");
//                        intent.putExtra(Constants.IntentKey.DEL_POSI,position);
                        context.startActivityForResult(intent,BankCardEntranceActivity.REQUEST_BANKS);
                        dialog.dismiss();

                    }
                }).show();

            }
        });
    }
    static class ViewHolder{
        private ImageView ivBankIcon;
        private TextView tvBankName;
        private TextView tvType;
        private TextView tvBankNumber;
        private ImageView ivDelete;
        private LinearLayout llAddNewBankCard;
    }
}
