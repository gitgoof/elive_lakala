package com.lakala.shoudan.activity.shoudan.loan.kepler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.shoudan.BuildConfig;
import com.paem.framework.pahybrid.manager.update.callback.Action;
import com.paem.iloanlib.api.KeplerPolicy;
import com.paem.iloanlib.api.LoginInfo;
import com.paem.iloanlib.api.LoginPolicy;
import com.paem.iloanlib.api.PageUIInfo;
import com.paem.iloanlib.api.SDKExternalAPI;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class KeplerUtil {
    private int i;
    private int j;
    private View view;
    private String ssStg,pafStg;
    private Spinner ssSpinner,pafSpinner;
    private ArrayAdapter<String> arrayAdapter;
    private int selectedFruitIndex = 0;
    private final String[] itemssENV={KeplerPolicy.STG1, KeplerPolicy.STG2, KeplerPolicy.STG3, KeplerPolicy.STG4
            , KeplerPolicy.STG5, KeplerPolicy.STG5, KeplerPolicy.STG5, KeplerPolicy.STG5, KeplerPolicy.STG5};
    private String[] itempafENV={KeplerPolicy.STG1, KeplerPolicy.STG2, KeplerPolicy.STG3};

    public void enterKepler(final Activity context, final String title) {
        if(BuildConfig.FLAVOR.equals("备机")||BuildConfig.FLAVOR.equals("生产")){
            prd(context,title);

        }else {
            test(context,title);

        }

    }

    // 备机(生产)SDK 入口
    private void prd(final Activity context, final String title) {
        SDKExternalAPI.getInstance()
                .fromActivity(context)
                .prepare(new KeplerPolicy.Builder()
                        .setEnv(KeplerPolicy.PRD,KeplerPolicy.PRD)//生产时请选择:PRD
                        .setPageInfo(getUIInfo(context,title))//主题UI风格
                        //.setEauthMode(2)//人脸识别检测模式
                             /* int 数据
                            "0"代表：只检测人脸，没有动作检测
                            "1"代表：检测完人脸后，接着检测"嘴部动作"
                            "2"代表：检测完人脸后，随机选择检测"嘴部动作"或检测"头部动作"
                             */
                        .build())
                .login(new LoginPolicy.Builder()
                        .setLoginInfo(getLoginInfo(context))//用户注册或登陆参数
                        .build())
                .success(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//成功回调
                        LogUtil.write("-----成功",msg);
//                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .error(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//失败回调
                        LogUtil.write("-----失败",msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    // 测试SDK 入口
    private void test(final Activity context, final String title) {
        Dialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("请选择测试环境？")
                .setSingleChoiceItems(Envs, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        selectedFruitIndex = which;
                        if (which == 0) {
                            i = 0;
                            j = 0;
                        } else if (which == 1) {
                            i = 1;
                            j = 0;
                        } else if (which == 2) {
                            i = 2;
                            j = 0;
                        } else if (which == 3) {
                            i = 0;
                            j = 1;
                        } else if (which == 4) {
                            i = 1;
                            j = 1;
                        } else if (which == 5) {
                            i = 2;
                            j = 1;
                        }
                    }
                })

                .setPositiveButton("确认", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(context, items[selectedFruitIndex], Toast.LENGTH_SHORT).show();
                        SDKExternalAPI.getInstance()
                                .fromActivity(context)
                                .prepare(new KeplerPolicy.Builder()
//                                      .setEnv(items[selectedFruitIndex],items[selectedFruitIndex])//生产时请选择:PRD
                                        .setEnv(itemssENV[i], itempafENV[j])//生产时请选择:PRD
                                        .setPageInfo(getUIInfo(context, title))//主题UI风格
                                        //.setEauthMode(eauthMode)//人脸识别检测模式
                             /* int 数据
                            "0"代表：只检测人脸，没有动作检测
                            "1"代表：检测完人脸后，接着检测"嘴部动作"
                            "2"代表：检测完人脸后，随机选择检测"嘴部动作"或检测"头部动作"
                             */
                                        .build())
                                .login(new LoginPolicy.Builder()
                                        .setLoginInfo(getLoginInfo(context))//用户注册或登陆参数
                                        .build())
                                .success(new Action() {
                                    @Override
                                    public void doAction(int code, String msg, Object obj) {//成功回调
                                        LogUtil.write("-----成功", msg);
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .error(new Action() {
                                    @Override
                                    public void doAction(int code, String msg, Object obj) {//失败回调
                                        LogUtil.write("-----失败", msg);
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .start();
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .create();
        alertDialog.show();
    }

    //仅供测试时使用,选择环境配置
    private String[] Envs = new String[]{
            "SS_stg1--PAF_stg1",
            "SS_stg2--PAF_stg1",
            "SS_stg3--PAF_stg1",
            "SS_stg1--PAF_stg2",
            "SS_stg2--PAF_stg2",
            "SS_stg3--PAF_stg2"
    };


    // SDK 入口     上线的时候用
    public void enterKepler1(final Activity context,String title) {

        SDKExternalAPI.getInstance()
                .fromActivity(context)
                .prepare(new KeplerPolicy.Builder()
                        .setEnv(KeplerPolicy.PRD,KeplerPolicy.PRD)//生产时请选择:PRD
                        .setPageInfo(getUIInfo(context,title))//主题UI风格
                        //.setEauthMode(eauthMode)//人脸识别检测模式
                             /* int 数据
                            "0"代表：只检测人脸，没有动作检测
                            "1"代表：检测完人脸后，接着检测"嘴部动作"
                            "2"代表：检测完人脸后，随机选择检测"嘴部动作"或检测"头部动作"
                             */
                        .build())
                .login(new LoginPolicy.Builder()
                        .setLoginInfo(getLoginInfo(context))//用户注册或登陆参数
                        .build())
                .success(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//成功回调
                        LogUtil.write("-----成功",msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .error(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//失败回调
                        LogUtil.write("-----失败",msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    private PageUIInfo getUIInfo(Activity context,String title) {
        PageUIInfo mPageUIInfo = PageUIInfo.getInstance();
        //mPageUIInfo.setProName("LKLSKB");
//        mPageUIInfo.setProName(title);
        mPageUIInfo.setProName("平安i贷");//设置标题
        mPageUIInfo.setTitleBarColor("#03aaf3");
        mPageUIInfo.setButtonColor("#03aaf3");
//        mPageUIInfo.setLogoUrl(R.raw.demo_banner_log);
        return  mPageUIInfo;
    }


    public LoginInfo getLoginInfo(Activity context) {
        LoginInfo mLoginInfo =LoginInfo.getInstance();
        LocationManager locationManager = LocationManager.getInstance();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("{\n");
        if (locationManager.getLatitude() != 0) {
            mLoginInfo.setLoginLat(String.valueOf(locationManager.getLatitude()));
            stringBuilder.append("    loginLat:"+mLoginInfo.getLoginLat()+"\n");
        }
        if (locationManager.getLongitude() != 0){
            mLoginInfo.setLoginLng(String.valueOf(locationManager.getLongitude()));
            stringBuilder.append("    longitude:"+mLoginInfo.getLoginLng()+"\n");
        }
//        if (locationManager.getCity() != null&&!"".equals(locationManager.getCity())){
//            mLoginInfo.setLoginCity(locationManager.getCity());
//            stringBuilder.append("    City:"+mLoginInfo.getLoginCity()+"\n");
//        }
//        if (locationManager.getAddress() != null&&!"".equals(locationManager.getAddress())){
//            mLoginInfo.setLoginAddress(locationManager.getAddress());
//            stringBuilder.append("    Address:"+mLoginInfo.getLoginAddress()+"\n");
//        }
        if ((locationManager.getCity() != null&&!"".equals(locationManager.getCity()))||
                (locationManager.getAddress() != null&&!"".equals(locationManager.getAddress()))){
            mLoginInfo.setLoginCity(locationManager.getCity());
            stringBuilder.append("    City:"+mLoginInfo.getLoginCity()+"\n");

            mLoginInfo.setLoginAddress(locationManager.getAddress());
            stringBuilder.append("    Address:"+mLoginInfo.getLoginAddress()+"\n");
        }else{
            ToastUtil.toast(context,"请开启定位权限以获取城市地址信息", Toast.LENGTH_SHORT);
        }
        mLoginInfo.setLoginChannelId("lakala");
        stringBuilder.append("    ChannelId:"+mLoginInfo.getLoginChannelId()+"\n");

        String mobile = ApplicationEx.getInstance().getUser().getLoginName();
        mLoginInfo.setLoginMobile(mobile);
        stringBuilder.append("    Mobile:"+mLoginInfo.getLoginMobile()+"\n");
        String custName = ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName();
        mLoginInfo.setLoginCustName(custName);
        stringBuilder.append("    CustName:"+mLoginInfo.getLoginCustName()+"\n");
        String id = ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getIdCardId();
        mLoginInfo.setLoginId(id);
        stringBuilder.append("    loginId:"+mLoginInfo.getLoginId()+"\n");
        stringBuilder.append("    ProName:平安i贷\n" +
                "    TitleBarColor:#03aaf3\n" +
                "    ButtonColor:#03aaf3\n}");
        LogUtil.d("iii",stringBuilder.toString());
//        ClipboardManager cm = (ClipboardManager) ApplicationEx.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
//         将文本内容放到系统剪贴板里。
//        cm.setText(stringBuilder.toString());
        stringBuilder.reverse();
        return mLoginInfo;
    }


    // SDK 测试入口
    public void enterCeshiKepler(final Activity context) {
        PageUIInfo  mPageUIInfo = PageUIInfo.getInstance();

        LoginInfo mLoginInfo = getLoginInfo(context);
        mPageUIInfo.setProName("LKLSKB");
        mPageUIInfo.setTitleBarColor("#03aaf3");
        mPageUIInfo.setButtonColor("#03aaf3");
       // mPageUIInfo.setLogoUrl(logoUrl);

        SDKExternalAPI.getInstance()
                .fromActivity(context)
                .prepare(new KeplerPolicy.Builder()
                        .setEnv(KeplerPolicy.STG2,KeplerPolicy.STG1)//生产时请选择:PRD
                        .setPageInfo(mPageUIInfo)//主题UI风格
                        //.setEauthMode(eauthMode)//人脸识别检测模式
                             /* int 数据
                            "0"代表：只检测人脸，没有动作检测
                            "1"代表：检测完人脸后，接着检测"嘴部动作"
                            "2"代表：检测完人脸后，随机选择检测"嘴部动作"或检测"头部动作"
                             */
                        .build())
                .login(new LoginPolicy.Builder()
                        .setLoginInfo(mLoginInfo)//用户注册或登陆参数
                        .build())
                .success(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//成功回调
                        LogUtil.write("-----成功",msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .error(new Action() {
                    @Override
                    public void doAction(int code, String msg, Object obj) {//失败回调
                        LogUtil.write("-----失败",msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

}
