package com.lakala.shoudan.activity.main;


import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.FileUpgrade.FileUpgradeManager;
import com.lakala.platform.FileUpgrade.UpgradeResultHandler;
import com.lakala.platform.FileUpgrade.ValidateFileHandler;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.FloatingLayerActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.bll.task.SplashTask;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.util.NotificationUtil;
import com.lakala.shoudan.util.UIUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.networkbench.agent.impl.NBSAppAgent;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by LMQ on 2015/11/19.
 */
public class MainActivity extends AppBaseActivity implements RadioGroup.OnCheckedChangeListener{

    private static final String TAG = "MainActivity";
    private long lastClickTime = 0;
    private RadioGroup radioGroup;
    private FragmentManager manager=getSupportFragmentManager();
    private FragmentMain fragment_main=null;//首页
    private FragmentCheckstand fragment_checkstand=null;//收银
    private FragmentFinancial fragment_financial=null;//金融
    private FragmentLife fragment_life=null;//生活

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BusinessLauncher.getInstance().finishAll();
        FinanceRequestManager.getInstance().clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_5_0);
        initUI();
        initSplash();
        getIcDownEnable();
        webAppCheckUpdate();
//        initFloating();
        NBSAppAgent.setLicenseKey("cdfeeb1dc91343fd9ecab251d60e8f7f").withLocationServiceEnabled(true).start(this.getApplicationContext());
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setVisibility(View.GONE);
        radioGroup= (RadioGroup) findViewById(R.id.main_tab_group);
        initRadioButton();
    }

    /**
     * 初始化启动页面
     */
    private void initSplash() {
        new SplashTask(this).execute();
    }

    /**
     * 获取ic降级开关
     */
    private void getIcDownEnable() {
        ShoudanService.getInstance().getIcDownEnable(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jb = null;
                try {
                    jb = new JSONObject(resultServices.retData);
                    ApplicationEx.getInstance().getUser().getAppConfig().setIcDownEnabled(jb.optJSONObject("IC_DOWN_ENABLED").optBoolean("flag", false));
                } catch (Exception e) {
                    LogUtil.print(e);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        });
    }
    /**
     * 初始化RadioButton图片的大小
     */
    public void initRadioButton(){
        radioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(null,R.id.rb_menu_00);
        for (int i=0;i<radioGroup.getChildCount();i++){
            RadioButton radioButton= (RadioButton) radioGroup.getChildAt(i);
            Drawable[] drawables = radioButton.getCompoundDrawables();
            drawables[1].setBounds(0,0, UIUtils.dip2px(60),UIUtils.dip2px(41));
            radioButton.setCompoundDrawables(null,drawables[1],null,null);
        }
    }

    /**
     * 底部导航栏点击事件
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction=manager.beginTransaction();
        hiteFragmengt(transaction);
        switch (checkedId) {
            case R.id.rb_menu_00://首页
                if(fragment_main==null){
                    fragment_main=new FragmentMain();
                    transaction.add(R.id.fl_content,fragment_main);
                }
                transaction.show(fragment_main);
                transaction.commitAllowingStateLoss();
                break;
            case R.id.rb_menu_01://收银
                if(fragment_checkstand==null){
                    fragment_checkstand=new FragmentCheckstand();
                    transaction.add(R.id.fl_content,fragment_checkstand);
                }
                transaction.show(fragment_checkstand);
                transaction.commitAllowingStateLoss();
                break;
            case R.id.rb_menu_02://金融
                if(fragment_financial==null){
                    fragment_financial=new FragmentFinancial();
                    transaction.add(R.id.fl_content,fragment_financial);
                }
                transaction.show(fragment_financial);
                transaction.commitAllowingStateLoss();
                break;
            case R.id.rb_menu_03://生活
                if(fragment_life==null){
                    fragment_life=new FragmentLife();
                    transaction.add(R.id.fl_content,fragment_life);
                }
                transaction.show(fragment_life);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }
    public void hiteFragmengt(FragmentTransaction transaction){
        if(fragment_main!=null){
            transaction.hide(fragment_main);
        }
        if(fragment_checkstand!=null){
            transaction.hide(fragment_checkstand);
        }
        if(fragment_financial!=null){
            transaction.hide(fragment_financial);
        }
        if(fragment_life!=null){
            transaction.hide(fragment_life);
        }
    }

    @Override
    public void onBackPressed() {
        if (lastClickTime == 0) {
            lastClickTime = System.currentTimeMillis();
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
            return;
        }

        final long interval = System.currentTimeMillis() - lastClickTime;

        lastClickTime = System.currentTimeMillis();

        if (interval > 2000) {
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
        } else {
            ApplicationEx.getInstance().exit();
        }
    }

    private void webAppCheckUpdate() {
        /**
         * webapp检查更新
         */
        registerReceiver();
        FileUpgradeManager.getInstance().init(false);
        NotificationUtil.doNotify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();
        appUpgradeController.checkUpgradeMessage(this);
        levelSwi();
    }
    interface OnLevelListener{
        void Succes();
        void Faild();
        void roading(boolean isRoadding);
    }
    private OnLevelListener onLevelListener;
    public void setOnLevelListener(OnLevelListener onLevelListener){
        this.onLevelListener=onLevelListener;
    }
    public void levelSwi(){
        if(onLevelListener!=null){
            onLevelListener.roading(true);
        }
        LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if(onLevelListener!=null){
                    onLevelListener.roading(false);
                }
                if(resultServices.isRetCodeSuccess()){
                    LogUtil.print("<S>","onSuccess");
                    try {
                        User user = ApplicationEx.getInstance().getSession().getUser();
                        JSONObject data = new JSONObject(resultServices.retData);
                        user.initMerchantAttrWithJson(data);
                        ApplicationEx.getInstance().getUser().save();
                        if(onLevelListener!=null){
                            onLevelListener.Succes();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(onLevelListener!=null){
                            onLevelListener.Faild();
                        }
                        ToastUtil.toast(context,"数据解析异常");
                    }
                }else{
                    if(onLevelListener!=null){
                        onLevelListener.Faild();
                    }
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                if(onLevelListener!=null){
                    onLevelListener.roading(false);
                    onLevelListener.Faild();
                }
                ToastUtil.toast(context,"网络请求失败");
            }
        })).execute();
    }

    private ValidateFileHandler validateFileHandler = new ValidateFileHandler();

    private UpgradeResultHandler upgradeResultHandler = new UpgradeResultHandler(this);

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileUpgradeManager.ACTION_CHECK_ALL_FINISHED);
        intentFilter.addAction(FileUpgradeManager.ACTION_CHECK_SINGLE_FINISHED);
        registerReceiver(upgradeResultHandler, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(FileUpgradeManager.ACTION_LOCAL_FILE_INVALIDED);
        registerReceiver(validateFileHandler, intentFilter);

    }

    private void initFloating() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (!LklPreferences.getInstance()
                        .getBoolean(UniqueKey.FLOATINGLAYER_SHOWED, false)) {
                    // 是否显示过浮层
                    Intent intent = new Intent(context, FloatingLayerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }


//
//    /**
//     * 听云SDK自定义事件统计测试
//     */
//
//    public void testTingyun(){
//        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//        singleThreadExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<String>  list=new ArrayList<>();
//                for (int i=0;i<1500;i++){
//                    list.add("听云测试:"+i);
//                }
//                while (true){
//                    Random random=new Random();
//                    int index=random.nextInt(1500);
//                    NBSAppAgent.onEvent(list.get(index));
//                    LogUtil.print("tingyun",list.get(index));
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
}
