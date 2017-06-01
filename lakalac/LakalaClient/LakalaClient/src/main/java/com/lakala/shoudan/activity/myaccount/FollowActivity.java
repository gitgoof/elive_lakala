package com.lakala.shoudan.activity.myaccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.ui.component.NavigationBar;

public class FollowActivity extends AppBaseActivity implements OnClickListener{
	
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if(navBarItem == NavigationBar.NavigationBarItem.back){
				finish();
			}else if(navBarItem == NavigationBar.NavigationBarItem.action){//帮助 关注协议？
                ProtocalActivity.open(context, ProtocalType.FOLLOW_LAKALA_HELP);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoudan_follow);
		
		initUI();
	}
	
	protected void initUI() {
		
		navigationBar.setTitle("关注拉卡拉");
//		navigationBar.setActionBtnText("帮助");
//		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
		
		findViewById(R.id.layout_fuwuzhongxin).setOnClickListener(this);
		findViewById(R.id.layout_weibo).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_fuwuzhongxin:
			startActivity(FollowLakalaActivity.class);
			break;
		case R.id.layout_weibo:
			ProtocalActivity.open(FollowActivity.this, getString(R.string.lakala_weibo), "http://www.weibo.cn/lakala");
			break;
		default:
			break;
		}
		
	}
	
	private void startActivity(Class startActivity) {
		Intent intent = new Intent(FollowActivity.this,startActivity);
		startActivity(intent);
	}

}
