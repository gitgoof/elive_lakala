package com.lakala.platform.common.meiqia;

import android.content.Context;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCOnlineConfig;
import com.mechat.mechatlibrary.MCOptions;
import com.mechat.mechatlibrary.MCUserConfig;
import com.mechat.mechatlibrary.callback.OnInitCallback;
import com.mechat.mechatlibrary.callback.UpdateUserInfoCallback;
import com.mechat.mechatlibrary.callback.UserOnlineCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/2/13.
 */
public class MeiQia {
    private static MeiQia ourInstance = new MeiQia();

    public static MeiQia getInstance() {
        return ourInstance;
    }

    private MeiQia() {
    }

    private final static String appIdPro = "55177c5d4eae35db15000001";//生产
    private final static String appIdDev = "54dd743d4eae359e31000003";//测试
    public Boolean isInitSuccess = null;


    public void init(){
        //should run in the application onCreate method

        MCClient.init(ApplicationEx.getInstance(), appIdPro, new OnInitCallback() {

            @Override
            public void onSuccess(String response) {
                isInitSuccess = true;
                //Success
//                Toast.makeText(getApplicationContext(), "init MCSDK success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String response) {
                //Failed
//                Toast.makeText(getApplicationContext(), "init MCSDK failed " + response, Toast.LENGTH_SHORT).show();
                isInitSuccess = false;
            }
        });



        MCOptions options = new MCOptions(ApplicationEx.getInstance());
        options.setShowAgentJoinEvent(true); //是否显示客服加入聊天事件
        options.setShowVoiceMessage(true); //是否启用语音消息
        //MCClient.getInstance().setPlatformInnerName(innerName);//多渠道商支持


    }
    public void startChat(){
        startChat(null,null);
    }
    /**
     * 启动对话界面
     * @param channel
     * @param group
     */
    public void startChat(String channel, String group){
        //设置上线参数，可选
        MCOnlineConfig onlineConfig = new MCOnlineConfig();
        if(!TextUtils.isEmpty(channel)){
            onlineConfig.setChannel(channel);// 设置渠道
        }
        if(!TextUtils.isEmpty(group)){
            onlineConfig.setSpecifyGroup(group);// 设置指定分组
        }
        //onlineConfig.setSpecifyAgent("4840", isForce); // 设置指定客服，isForce 为是否强制分配到该客服。如果不强制，若该客服不在线，会分配给其他客服。

        //启动对话界面
        MCClient.getInstance().startMCConversationActivity(onlineConfig);
    }


    /**
     * 让用户上线
     */
    public void online(UserOnlineCallback userOnlineCallback){
        //设置上线参数，可选
        MCOnlineConfig onlineConfig = new MCOnlineConfig();
        //onlineConfig.setChannel("收款宝"); // 设置渠道
        //onlineConfig.setSpecifyAgent("4840", false); // 设置指定客服
        //onlineConfig.setSpecifyGroup("1"); // 设置指定分组

        MCClient.getInstance().letUserOnline(userOnlineCallback, onlineConfig);
    }

    private int count = 1;

    /**
     * 更新用户信息
     */
    public void updateUsrInfo(Context context, UpdateUserInfoCallback updateUserInfoCallback){
        MCUserConfig mcUserConfig = new MCUserConfig();
        Map<String,String> userInfo = new HashMap<String,String>();
//        userInfo.put(MCUserConfig.PersonalInfo.REAL_NAME,"real_name");

        userInfo.put(MCUserConfig.Contact.TEL, ApplicationEx.getInstance().getUser().getLoginName());
        Map<String,String> userInfoExtra = new HashMap<String,String>();
//        userInfoExtra.put("mobile",ApplicationEx.getInstance().getUser().getLoginName());
        userInfoExtra.put("渠道","收款宝");
        mcUserConfig.setUserInfo(context,userInfo,userInfoExtra, updateUserInfoCallback);

    }

    public void offline(){
        MCClient.getInstance().letUserOffline();
    }




}
