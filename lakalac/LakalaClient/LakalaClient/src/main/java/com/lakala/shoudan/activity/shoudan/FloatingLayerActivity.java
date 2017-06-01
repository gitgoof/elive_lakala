package com.lakala.shoudan.activity.shoudan;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.lakala.platform.common.LklPreferences;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.util.ScreenUtil;
/**
 * 浮层
 * @author ZhangMY
 *
 */
public class FloatingLayerActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_floating_layer);

        ScreenUtil.getScrrenWidthAndHeight(this);
		ImageView iv = (ImageView) findViewById(R.id.iv_floating);
		LayoutParams params = (LayoutParams) iv.getLayoutParams();
		params.height = Parameters.screenHeight-Parameters.statusBarHeight;
		params.width = Parameters.screenWidth;
		iv.setLayoutParams(params);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		setFloatingLayerShowed();
		finish();
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setFloatingLayerShowed();
	}
	
	private void setFloatingLayerShowed(){
		LklPreferences.getInstance().putBoolean(UniqueKey.FLOATINGLAYER_SHOWED,true);
		
	}
}
