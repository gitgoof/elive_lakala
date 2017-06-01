package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;


/**
 * 左边Label，右边开关控件
 * Created by xyz on 13-9-16.
 */
public class LabelSwitch extends LinearLayout implements View.OnClickListener{

    private int labelTextRes;
    private TextView tip;
    private String tipText;
    private boolean autoCheck = true;

    /**
    *开关状态
    */
    public enum ESwitchStatus {
        ON,
        OFF
    }

    public ESwitchStatus status = ESwitchStatus.OFF;

    /**
     * 开关状态改变接口
     */
    public interface OnSwitchListener{
        public void onSwitch(ESwitchStatus status);
    }

    private OnSwitchListener onSwitchListener;

    //属性
    private String labelText;       //label文本内容
    private int onImageSrc;         //打开状态图片资源
    private int offImageSrc;        //关闭状态图片资源
    private float labelTextSize;    //文本标签字体大小
    private int labelTextColor;    //文本标签字体颜色

    //控件
    private TextView label;             //左侧文本
    private ImageView switchImage;      //右侧开关

    public ImageView getSwitchView() {
        return switchImage;
    }

    /**
     * 设置开关事件监听器
     * @param onSwitchListener
     */
    public void setOnSwitchListener(OnSwitchListener onSwitchListener){
       this.onSwitchListener = onSwitchListener;
    }

    public LabelSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context,attrs);
        initView();
    }

    public void setAutoCheck(boolean autoCheck) {
        this.autoCheck = autoCheck;
    }

    @Override
    public void onClick(View view) {
        if(!autoCheck){
            return;
        }
        if (status == ESwitchStatus.OFF){
            status = ESwitchStatus.ON;
        } else {
            status = ESwitchStatus.OFF;
        }
        setSwitchStatus(status);
    }

    /**
     * 获得当前开关状态
     * @return
     */
    public ESwitchStatus getSwitchStatus(){
        return status;
    }

    /**
     * 设置开关状态
     * @param newStatus
     */
    public void setSwitchStatus(ESwitchStatus newStatus){
        status = newStatus;

        if (status == ESwitchStatus.OFF){
            switchImage.setImageResource(offImageSrc);
            tip.setText("");
            tip.setVisibility(View.INVISIBLE);
        } else if (status == ESwitchStatus.ON){
            switchImage.setImageResource(onImageSrc);
            tip.setVisibility(View.VISIBLE);
        }

        if (onSwitchListener != null){
            onSwitchListener.onSwitch(status);
        }
    }

    /**
     * 设置标签文本信息
     */
    public void setSwitchLabel(String text) {
        label.setText(text);
    }

    public void setSwitchLabel(int text) {
        label.setText(text);
    }

    public void setTipText(String text){
        tip.setText(text);
    }
    public void setTipText(int text){
        tip.setText(text);
    }

    public void setSwitchLabelTextColor(int color){
        label.setTextColor(color);
    }

    /**
     * 初始控件属性
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context,AttributeSet attrs){
        //获取xml中配置的属性资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelSwitch);
        try {
            labelText       = typedArray.getString(R.styleable.LabelSwitch_label);
            labelTextRes    = typedArray.getResourceId(R.styleable.LabelSwitch_label,0);
            onImageSrc      = typedArray.getResourceId(R.styleable.LabelSwitch_onImageSrc, 0);
            offImageSrc     = typedArray.getResourceId(R.styleable.LabelSwitch_offImageSrc, 0);
            labelTextSize   = typedArray.getDimension(R.styleable.LabelSwitch_labelFontSize, 0);
            labelTextColor  = typedArray.getColor(R.styleable.LabelSwitch_labelFontColor, 0);
            tipText = typedArray.getString(R.styleable.LabelSwitch_tip);

            //设置默认背景图
            if (onImageSrc == 0){
                onImageSrc = R.drawable.btn_open;
            }

            if (offImageSrc == 0){
                offImageSrc = R.drawable.btn_close;
            }
        } finally {
            typedArray.recycle();
        }
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.l_label_switch,this,true);

        label       = (TextView)findViewById(R.id.switch_text);
        switchImage = (ImageView)findViewById(R.id.switch_image);
        tip = (TextView)findViewById(R.id.tip_text);

        if (labelText != null){
            setSwitchLabel(labelText);
        }
        if(tipText != null){

        }

        if (labelTextRes != 0){
            setSwitchLabel(labelTextRes);
        }

        if (Float.compare(labelTextSize, 0.0f)>0){
            label.setTextSize(TypedValue.COMPLEX_UNIT_PX,labelTextSize);
        }

        if (labelTextColor!=0){
            setSwitchLabelTextColor(labelTextColor);
        }

        switchImage.setImageResource(offImageSrc);
        switchImage.setOnClickListener(this);
    }

}
