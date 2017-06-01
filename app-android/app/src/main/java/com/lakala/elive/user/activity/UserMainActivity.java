package com.lakala.elive.user.activity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lakala.elive.Constants;
import com.lakala.elive.R;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.utils.ActivityTaskCacheUtil;
import com.lakala.elive.common.utils.DialogUtil;
import com.lakala.elive.common.utils.StringUtil;
import com.lakala.elive.user.fragment.ContactFragment;
import com.lakala.elive.user.fragment.HomeFragment;
import com.lakala.elive.user.fragment.SessionFragment;
import com.lakala.elive.user.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 
 * 主功能界面菜单
 * 
 * @author hongzhiliang
 *
 */
public class UserMainActivity extends FragmentActivity implements ApiRequestListener {

    private TextView tvTitleName = null; //标题名称
    private List<Fragment> mFragmentList;
    private RadioGroup mRg_main;
    private int position;//选中的Fragment的对应的位置
    private Fragment mPreFrag;//上次切换的Fragment
    private Session mSession;//共享数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = Session.get(this);
        //初始化View
        initView();
        //初始化Fragment
        initFragment();
        //设置RadioGroup的监听
        setListener();
        //加载常用数据字典,可以提前放在Splash页面
        loadCommonDict();
    }

    private void initFragment() {
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new HomeFragment());//主界面
        mFragmentList.add(new ContactFragment());//通讯录
        mFragmentList.add(new SessionFragment());//会话
        mFragmentList.add(new SettingFragment());//用户设置
    }

    private void setListener() {
        mRg_main.setOnCheckedChangeListener(new HomeOnCheckedChangeListener());
        //设置默认选中常用框架
        mRg_main.check(R.id.rb_home_main);
    }

    //初始化主界面
    private void initView() {
        setContentView(R.layout.activity_user_main);
        tvTitleName = (TextView) findViewById(R.id.tv_title_name);
        mRg_main = (RadioGroup) findViewById(R.id.rg_main);
    }

    /**
     * 加载服务器端常用数据类型
     */
    private void loadCommonDict() {
        UserReqInfo reqInfo = new UserReqInfo();
        if(mSession!=null && StringUtil.isNotNullAndBlank(mSession.getUserLoginInfo().getAuthToken())){
            reqInfo.setAuthToken(mSession.getUserLoginInfo().getAuthToken());
            reqInfo.setDictTypeCode(Constants.dictDataList);
            NetAPI.getDictDataList(this, this,reqInfo);
        }
    }

    /**
     * 根据位置得到对应的Fragment
     * @return
     */
    private Fragment getFragment() {
        Fragment fragment = mFragmentList.get(position);
        return fragment;
    }

    class HomeOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_home_main:
                    position = 0;
                    tvTitleName.setText(R.string.home_main_ui);
                    break;
                case R.id.rb_home_contact:
                    position = 1;
                    tvTitleName.setText(R.string.home_contact_ui);
                    break;
                case R.id.rb_home_session:
                    position = 2;
                    tvTitleName.setText(R.string.home_session_ui);
                    break;
                case R.id.rb_home_my:
                    position = 3;
                    tvTitleName.setText(R.string.home_setting_ui);
                    break;
                default:
                    position = 0;
                    break;
            }
            //根据位置得到对应的Fragment
            Fragment to = getFragment();
            //替换
            switchFrament(mPreFrag,to);

        }
    }
    /**
     *
     * @param from 刚显示的Fragment,马上就要被隐藏了
     * @param to 马上要切换到的Fragment，一会要显示
     */
    private void switchFrament(Fragment from,Fragment to) {
        if(from != to){
            mPreFrag = to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //才切换
            //判断有没有被添加
            if(!to.isAdded()){
                //to没有被添加
                //from隐藏
                if(from != null){
                    ft.hide(from);
                }
                //添加to
                if(to != null){
                    ft.add(R.id.fl_home_main_pager,to).commit();
                }
            }else{
                //to已经被添加
                // from隐藏
                if(from != null){
                    ft.hide(from);
                }
                //显示to
                if(to != null){
                    ft.show(to).commit();
                }
            }
        }
    }

    /**
     * 进行必要的资源回收工作
     */
    private void exit() {
        // 清除所有观察者
        mSession.deleteObservers();
        // 关闭HTTP资源
        // 回收Session
        mSession.close();
        mSession = null;
        ActivityTaskCacheUtil.getIntance().clearAllAct();
        finish();
        finishThisApp();
        System.exit(0);
    }
    private void finishThisApp(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getPackageName());
    }

    @Override
    public void onSuccess(int method, Object obj) {
        switch (method) {
            case NetAPI.ACTION_GET_DICT_DATA:
                Map<String,Map<String,String>> sysDictMap = (Map<String,Map<String,String>>) obj;
                mSession.setSysDictMap(sysDictMap);
                break;
        }
    }

    @Override
    public void onError(int method, String statusCode) {
        switch (method) {
            case NetAPI.ACTION_GET_DICT_DATA:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            DialogUtil.createAlertDialog(
                    this,
                    "应用退出",
                    "确定退出Elive平台",
                    "取消",
                    "确定",
                    mListener
            ).show();
        }
        return false;
    }


    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which){
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    dialog.dismiss();
                    exit();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
