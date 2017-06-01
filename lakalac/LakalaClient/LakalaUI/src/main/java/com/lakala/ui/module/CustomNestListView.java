package com.lakala.ui.module;

/**
 * com.ll.lottery.view
 * ──────────────────────────────────
 * 	Function:	[Add Description of Function] 
 *	@Date		2013-5-23	
 * ──────────────────────────────────
 * Copyright (c) 2013, LL All Rights Reserved.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
 * 自定义嵌套listView类（用于ScrollView嵌套GridView或者是ListView）		<br>
 * 
 * 在Android开发当中经常会用到了需要ScrollView嵌套GridView或者是ListView的情况，
 * 由于这两款控件都自带滚动条，当他们碰到一起的时候便会出问题，即GridView或ListView会显示不全。		<br><br>
 *  
 *	解决方案： 自定义GridView或ListView，去掉其滚动条
 * 
 * @author LL
 * 
 */
public class CustomNestListView extends ListView {

	public CustomNestListView(Context context) {
		super(context);
	}

	public CustomNestListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomNestListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
