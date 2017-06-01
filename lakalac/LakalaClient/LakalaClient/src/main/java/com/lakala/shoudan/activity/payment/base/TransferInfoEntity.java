package com.lakala.shoudan.activity.payment.base;

import android.graphics.drawable.Drawable;

import com.lakala.shoudan.component.VerticalListView;

/**
 * Created by More on 14-4-24.
 */
public class TransferInfoEntity {

    private String name;//标签

    private String value;//值

    private String valueHint;//提示

    private boolean isDiffColor;//value字体颜色值
    
    private int valueColor = -1;//
    
    private boolean needDevider;//结尾是否添加横线

    private VerticalListView.IconType type;     //图标
    
	public TransferInfoEntity(String name, String value, int colorId, boolean needdevider){
    	this.name = name;
    	this.value = value;
    	this.valueColor = colorId;
    	this.needDevider = needdevider;
    }
	
	public TransferInfoEntity(String name, String value, int colorId){
    	this.name = name;
    	this.value = value;
    	this.valueColor = colorId;
    }

	public TransferInfoEntity(String name, String value, boolean isDiffColor) {
        this.name = name;
        this.value = value;
        this.isDiffColor = isDiffColor;
    }
    public TransferInfoEntity(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public TransferInfoEntity(String name, String value, VerticalListView.IconType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public TransferInfoEntity() {
    }

    public TransferInfoEntity(String name, String value, String valueHint) {
        this.name = name;
        this.value = value;
        this.valueHint = valueHint;
    }

    public VerticalListView.IconType getType() {
        return type;
    }

    public void setType(VerticalListView.IconType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueHint() {
        return valueHint;
    }

    public void setValueHint(String valueHint) {
        this.valueHint = valueHint;
    }

    public boolean isDiffColor() {
        return isDiffColor;
    }

    public int getValueColor() {
		return valueColor;
	}

	public void setValueColor(int valueColor) {
		this.valueColor = valueColor;
	}

	public void setDiffColor(boolean isDiffColor) {
        this.isDiffColor = isDiffColor;
    }
	
    public boolean isNeedDevider() {
		return needDevider;
	}

	public TransferInfoEntity setNeedDevider(boolean needDevider) {
		this.needDevider = needDevider;
        return this;
	}
}
