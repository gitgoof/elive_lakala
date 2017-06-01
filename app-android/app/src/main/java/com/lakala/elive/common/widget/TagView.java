package com.lakala.elive.common.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.lakala.elive.R;

public class TagView extends ToggleButton {
	
	private boolean mCheckEnable = true;

	public TagView(Context paramContext) {
		super(paramContext);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, 0);
		init();
	}

	private void init() {
		setTextOn(null);
		setTextOff(null);
		setText("");
		setBackgroundResource(R.drawable.item_point_info);
	}

	public void setCheckEnable(boolean paramBoolean) {
		this.mCheckEnable = paramBoolean;
		if (!this.mCheckEnable) {
			super.setChecked(false);
		}
	}

	public void setChecked(boolean paramBoolean) {
		if (this.mCheckEnable) {
			super.setChecked(paramBoolean);
		}
	}
}
