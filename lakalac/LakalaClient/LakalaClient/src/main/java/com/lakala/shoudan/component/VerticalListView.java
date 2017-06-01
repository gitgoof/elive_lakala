package com.lakala.shoudan.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import com.lakala.ui.component.FitTextWatcher;

import java.util.List;

/**
 * Created by More on 14-4-30.
 */
public class VerticalListView extends TableLayout {

    private Context context;
    private List<TransferInfoEntity> transferInfoEntities;
    private LinearLayout lookSignOrder;
    private SignListener signListener = null;

    public void setSignListener(SignListener signListener) {
        this.signListener = signListener;
    }

    public VerticalListView(Context context) {
        super(context);
        this.context = context;
    }

    public VerticalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public enum IconType{
        UNKNOWN("未知"),
        WECHAT("微信"),
        ALIPAY("支付宝"),
        BAIDUPAY("百度钱包"),
        UNIONPAY("银联");

        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        IconType(String type) {
            this.type = type;
        }
    }

    /**
     * 如果item项需要添加横线，才去添加//交易详情
     * @param context
     * @param mTransferInfoEntities
     */
    public void addList(final Context context,List<TransferInfoEntity> mTransferInfoEntities){
    	this.context = context;
    	if(mTransferInfoEntities == null || mTransferInfoEntities.size() == 0)return;
    	this.transferInfoEntities = mTransferInfoEntities;
    	setOrientation(VERTICAL);
        this.setColumnShrinkable(1, true);
        
    	for(int i=0; i<mTransferInfoEntities.size(); i++){
    	     final TransferInfoEntity entity = mTransferInfoEntities.get(i);
             TableRow view = (TableRow)LayoutInflater.from(context).inflate(R.layout.transfer_info_list_item, null);
             TextView name = (TextView) view.findViewById(R.id.name);
             ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setVisibility(View.GONE);
             final TextView value = (TextView)view.findViewById(R.id.value);
            lookSignOrder = (LinearLayout) view.findViewById(R.id.look_sign_order);
            lookSignOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (signListener != null){
                        signListener.onLookSignClick();
                    }
                }
            });

            IconType type = entity.getType();
            if (type != null){
                imageView.setVisibility(View.VISIBLE);
                if (type == IconType.WECHAT){
                    imageView.setBackgroundResource(R.drawable.pic_weixin);
                }else if (type == IconType.ALIPAY){
                    imageView.setBackgroundResource(R.drawable.pic_zhifubao);
                }else if (type == IconType.BAIDUPAY){
                    imageView.setBackgroundResource(R.drawable.pic_baidu);
                }else if (type == IconType.UNIONPAY){
                    imageView.setBackgroundResource(R.drawable.pic_yinlian);
                }else {
                    imageView.setVisibility(View.GONE);
                }
            }
             value.setTag(true);
             value.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
 				
 				@Override
 				public boolean onPreDraw() {
 					if((Boolean) value.getTag()){
 						//System.out.println("onPre width:"+entity.getValue()+",width:"+value.getWidth());
// 					    value.reFitText(entity.getValue(), value.getWidth());
 						value.setTag(false);
 					}
 					return true;
 				}
 			});
             String tempname=entity.getName();
             if(null!=tempname&&!tempname.equals("")&&tempname.contains(":")){
            	 tempname=tempname.replaceAll(":", "");
             }else if(null==tempname){
            	 tempname="";
             }
             name.setText(tempname);
             name.setTextColor(Color.BLACK);
             if(entity.getValueHint() != null){
                 value.setHint(entity.getValueHint());
             }
             if(entity.isDiffColor()){
                 value.setTextColor(context.getResources().getColor(R.color.amount_color));
             }else{
                 value.setTextColor(Color.BLACK);
             }
             
             if(entity.getValueColor() != -1){
            	 value.setTextColor(entity.getValueColor());
             }
             if(!"".equals(entity.getValue())){
	                 value.setText(entity.getValue());
	         }
            if ("已签名".equals(entity.getValue())){
                lookSignOrder.setVisibility(View.VISIBLE);
            }else {
                lookSignOrder.setVisibility(View.GONE);
            }
             LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
             addView(view,param);
             
             if(entity.isNeedDevider()){
            	View seperator = new View(context);
             	LinearLayout.LayoutParams seperatorParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(1, context));
             	seperator.setBackgroundResource(R.drawable.lakala_divide_line);
             	seperator.setLayoutParams(seperatorParams);
             	addView(seperator);
             }
    	 }
    }

    /**
     * 给所有的item想添加分割线
     * @param context
     * @param transferInfoEntityList
     * @param addDivide
     * @param itemBackground
     */
    public void addList(final Context context, List<TransferInfoEntity> transferInfoEntityList, boolean addDivide,int itemBackground){

        this.context = context;
        if(transferInfoEntityList == null || transferInfoEntityList.size() == 0)
            return;
        this.transferInfoEntities = transferInfoEntityList;
        setOrientation(VERTICAL);
        this.setColumnShrinkable(1, true);
        for(int i=0; i<transferInfoEntityList.size(); i++){
            final TransferInfoEntity entity = transferInfoEntityList.get(i);
            TableRow view = (TableRow)LayoutInflater.from(context).inflate(R.layout.transfer_info_list_item, null);
            view.setBackgroundColor(itemBackground);
            TextView name = (TextView) view.findViewById(R.id.name);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setVisibility(View.GONE);
            final TextView value = (TextView)view.findViewById(R.id.value);

            lookSignOrder = (LinearLayout) view.findViewById(R.id.look_sign_order);
            lookSignOrder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (signListener != null) {
                        signListener.onLookSignClick();
                    }
                }
            });

            IconType type = entity.getType();
            if (type != null){
                imageView.setVisibility(View.VISIBLE);
                if (type == IconType.WECHAT){
                    imageView.setBackgroundResource(R.drawable.pic_weixin);
                }else if (type == IconType.ALIPAY){
                    imageView.setBackgroundResource(R.drawable.pic_zhifubao);
                }else if (type == IconType.BAIDUPAY){
                    imageView.setBackgroundResource(R.drawable.pic_baidu);
                }else if (type == IconType.UNIONPAY){
                    imageView.setBackgroundResource(R.drawable.pic_yinlian);
                }else {
                    imageView.setVisibility(View.GONE);
                }
            }
            value.setTag(true);
            value.addTextChangedListener(new FitTextWatcher(context,value));
            value.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				
				@Override
				public boolean onPreDraw() {
					if((Boolean) value.getTag()){
						//System.out.println("onPre width:"+entity.getValue()+",width:"+value.getWidth());
// 					    value.reFitText(entity.getValue(), value.getWidth());
 						value.setTag(false);
					}
					return true;
				}
			});

            name.setText(entity.getName());
            if(entity.getValueHint() != null){
                value.setHint(entity.getValueHint());
            }
            if(entity.isDiffColor()){
                value.setTextColor(context.getResources().getColor(R.color.amount_color));
            }else{
                value.setTextColor(context.getResources().getColor(R.color.gray_666666));
            }
          
            if(!"".equals(entity.getValue())){
                value.setText(entity.getValue());
            }
            if ("已签名".equals(entity.getValue())){
                lookSignOrder.setVisibility(View.VISIBLE);
            }else {
                lookSignOrder.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            addView(view,param);
            
            /** 添加分割线**/
            if(i!=transferInfoEntityList.size()-1 && addDivide){
                addSeparator();
            }
            
        }
    }

    public void addSeparator(){
        View separator = new View(context);
        LinearLayout.LayoutParams seperatorParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(1, context));
        separator.setBackgroundResource(R.drawable.lakala_divide_line);
        separator.setLayoutParams(seperatorParams);
        addView(separator);
    }

    //撤销修改
    public void updateList(Context context,String dealAmount,String dealDateTime, boolean addDivide,int itembg){
    	
    	this.transferInfoEntities.get(0).setValue(dealAmount);//收款金额
        this.transferInfoEntities.get(0).setName(getResources().getString(R.string.receivable_amount));
    	this.transferInfoEntities.get(0).setDiffColor(true);
    	this.transferInfoEntities.get(1).setValue(dealDateTime);//交易时间
        this.transferInfoEntities.get(1).setName(getResources().getString(R.string.transaction_time));
    	
    	removeAllViews();
    	addList(context, this.transferInfoEntities, addDivide,itembg);
    }

    //TODO
    public void addListWithDivide(Context context, List<TransferInfoEntity> transferInfoEntityList){



    }

    public  int dip2px(float dipValue, Context context) {
        float scale = getDensity(context);
        return (int) (dipValue * scale + 0.5f);
    }
    public float getDensity(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    public interface SignListener{
        void onLookSignClick();
    }
}
