package com.lakala.shoudan.activity.myaccount;

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.protocal.ProtocalUrl;
import com.lakala.shoudan.activity.AppBaseActivity;

/**
 * Created by More on 14-1-14.
 */
public class FollowLakalaActivity extends AppBaseActivity implements View.OnClickListener{
	WebView wb;
	public final static String weixinPackageNameString="com.tencent.mm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_follow_lakala);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.more_guanzhu_us);
        try{
        	wb = (WebView)findViewById(R.id.web_view_upgrade);
            wb.loadUrl(ProtocalUrl.FOLLOW_LAKALA);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        findViewById(R.id.follow_lakala).setOnClickListener(this);
        
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.follow_lakala:
            	openApp(weixinPackageNameString);
                break;
            default:
                break;
        }
    }
    
     private void openApp(String packageName) {
    	PackageInfo pi=null;
		try {
			pi = getPackageManager().getPackageInfo(packageName, 0);
		
    	PackageManager pm=getPackageManager();
    	Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
    	resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    	resolveIntent.setPackage(pi.packageName);

    	List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

    	ResolveInfo ri = apps.iterator().next();
    	if (ri != null ) {
    	String packageName1 = ri.activityInfo.packageName;
    	String className = ri.activityInfo.name;

    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);

    	ComponentName cn = new ComponentName(packageName1, className);
    	
    	intent.setComponent(cn);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    	}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse(WINXIN_DOWNLOAD));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
    }


    private final String WINXIN_DOWNLOAD = "http://weixin.qq.com/d";
}
