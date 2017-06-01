package com.lakala.platform.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.Toast;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.ShareFileUtils;
import com.lakala.platform.common.ShareUtils;
import com.lakala.platform.common.sina.AccessTokenKeeper;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * Created by HJP on 2015/9/22.
 */
public class WeiBoFilterRespActivity extends BaseActionBarActivity implements IWeiboHandler.Response {
    /**
     * 微信是否有安装
     * 目前只有本地安装了微信客户端时候才能进行微信分享功能
     */
    boolean isInstalledWeChat;
    private static int WECHAT_SUPPORTED_VERSION = 0x21020001;
    private int wxSdkVersion;//微信版本是否支持朋友圈
    private IWXAPI iwxapi;// 微信通信的openapi接口
    protected IWeiboShareAPI mWeiboShareAPI;// 微博微博分享接口
    int wbSdkVersion;
    private String advertUrl;
    private String advertTitle;
    private String type;
    public Tencent mTencent;
    private int from = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.plat_password_input);

        if (getIntent() != null)
            type = getIntent().getStringExtra("type");
        from = getIntent().getIntExtra("from", 0);
        //首先判断是从哪来的，如果from为1，则是从考拉信用进来的。

        if ("wechat_friend".equals(type)) {
            sendWeChatMessage(true);
        } else if ("wechat_moment".equals((type))) {
            sendWeChatMessage(false);
        } else if ("weibo".equals((type))) {
            sendWeiboMultiMessage();
        } else if ("qq".equals(type)) {
            //如果是qq分享，则先初始化key
            mTencent = Tencent.createInstance(ConstKey.QQ_APP_ID, WeiBoFilterRespActivity.this);
            advertTitle = getIntent().getStringExtra("title");
            advertUrl = getIntent().getStringExtra("url");
            sendQQMessage();

        }

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("weibo".equals(type) || TextUtils.isEmpty(type)) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 当前应用唤起微博分享后，返回当前应用
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    public void initBaseData() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(WeiBoFilterRespActivity.this, ConstKey.APP_KEY);
        wbSdkVersion = mWeiboShareAPI.getWeiboAppSupportAPI();
        mWeiboShareAPI.registerApp();// 注册到新浪微博
        iwxapi = WXAPIFactory.createWXAPI(WeiBoFilterRespActivity.this, ConstKey.WeChat_APP_KEY, true);
        iwxapi.registerApp(ConstKey.WeChat_APP_KEY);
        wxSdkVersion = iwxapi.getWXAppSupportAPI();
        isInstalledWeChat = iwxapi.isWXAppInstalled();
        advertTitle = getIntent().getStringExtra("title");
        advertUrl = getIntent().getStringExtra("url");
    }

    public void sendWeChatMessage(boolean isSendToFriendMessage) {
        if (iwxapi == null) {
            initBaseData();
        }
        if (!isInstalledWeChat) {
            ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.wechat_not_installed);
        } else {
            if (isSendToFriendMessage && (wxSdkVersion < WECHAT_SUPPORTED_VERSION)) {
                ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.moment_not_supported);
            } else {
                // 初始化一个WXWebpageObject对象
                WXWebpageObject webpage = new WXWebpageObject();

                WXMediaMessage msg = new WXMediaMessage(webpage);
                if (from == 1) {
                    msg.description = "  ";
                } else {

                    msg.description = "分享自@拉卡拉手机收款宝";
                }
                webpage.webpageUrl = advertUrl;
                msg.title = advertTitle;
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                msg.thumbData = Util.bmpToByteArray(thumb, true); // 设置缩略图
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());//用于唯一标识一个请求
                if (isSendToFriendMessage) {
                    req.scene = SendMessageToWX.Req.WXSceneSession;//会话
                } else {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
                }
                req.message = msg;
                iwxapi.sendReq(req);
            }

        }
        finish();
    }

//    protected void setAd(String url,String title){
//        this.advertUrl=url;
//        this.advertTitle=title;
//    }

    /**
     * /**
     * 发送多种形式信息的微博
     */
    public void sendWeiboMultiMessage() {
        if (mWeiboShareAPI == null) {
            initBaseData();
        }
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (from == 1) {
            TextObject textObject = new TextObject();
            textObject.text = "【" + advertTitle + "】" + advertUrl + " (分享自@拉卡拉手机收款宝）";
            weiboMessage.textObject = textObject;
        } else {
            TextObject textObject = new TextObject();
            textObject.text = "【" + advertTitle + "】" + advertUrl + " (分享自@拉卡拉手机收款宝）";

//		WebpageObject mediaObject = new WebpageObject();
//		mediaObject.identify = Utility.generateGUID();
//		mediaObject.title=advertTitle;
//		mediaObject.description ="我是一个有信誉的商家，所以我选择拉卡拉收款宝";
////		Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
////		mediaObject.setThumbImage(bitmap1);
//		mediaObject.actionUrl = advertUrl;//广告URL
//		mediaObject.defaultText = "我是一个有信誉的商家，所以我选择拉卡拉收款宝";

            weiboMessage.textObject = textObject;
//		weiboMessage.mediaObject=mediaObject;

        }


        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        AuthInfo authInfo = new AuthInfo(WeiBoFilterRespActivity.this, ConstKey.APP_KEY,
                ConstKey.REDIRECT_URL, ConstKey.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(WeiBoFilterRespActivity.this);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }

        mWeiboShareAPI.sendRequest(WeiBoFilterRespActivity.this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                finish();
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                if (newToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                } else {
                    ToastUtil.toast(getApplicationContext(), "微博认证失败");
                }
            }

            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    //实现QQ分享操作
    public void sendQQMessage() {
        final Bundle params = new Bundle();
        if (TextUtils.isEmpty(advertTitle)) {

            //标题
            params.putString(QQShare.SHARE_TO_QQ_TITLE, getString(R.string.app_name));
        } else {

            //标题
            params.putString(QQShare.SHARE_TO_QQ_TITLE, advertTitle);
        }

        //点击后的url
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, advertUrl);
        if (from == 1) {

            //主题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "  ");
        } else {

            //主题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享自@拉卡拉手机收款宝");


        }
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, ShareUtils.saveDrawableById(this, R.drawable.lkl_icon, "lklLogo.png", Bitmap.CompressFormat.PNG));
        //分享app名称
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
        //类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//默认分享类型
//                doShareToQQ(params);
        mTencent.shareToQQ(WeiBoFilterRespActivity.this, params, qqShareListener);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        }
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.share_canceled);
            WeiBoFilterRespActivity.this.finish();
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.share_success);
            WeiBoFilterRespActivity.this.finish();
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            ToastUtil.toast(WeiBoFilterRespActivity.this, getString(R.string.share_failed) + "Error Message: " + e.errorMessage);
            WeiBoFilterRespActivity.this.finish();
        }
    };

    /**
     * 接收微博客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.weibo, this);
                ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.share_success);
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToastUtil.toast(WeiBoFilterRespActivity.this, R.string.share_canceled);
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ToastUtil.toast(WeiBoFilterRespActivity.this,
                        getString(R.string.share_failed) + "Error Message: " + baseResp.errMsg);
                break;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTencent != null) {
            mTencent.releaseResource();
        }
    }
}
