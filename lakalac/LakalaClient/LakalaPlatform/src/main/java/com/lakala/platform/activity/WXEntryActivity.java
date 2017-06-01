package com.lakala.platform.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.consts.ConstKey;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by HJP on 2015/8/28.
 * 在WXEntryActivity中将接收到的intent及实现了IWXAPIEventHandler接口的对象传递给IWXAPI接口的 handleIntent方法。
 * 应用请求微信的响应结果将通过onResp回调，
 * 类似的，当微信发送请求到你的应用，将通过IWXAPIEventHandler接口的onReq方法进行回调。
 */
public class WXEntryActivity extends BaseActionBarActivity implements IWXAPIEventHandler {
        private IWXAPI api;
        @Override

        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                api = WXAPIFactory.createWXAPI(this, ConstKey.WeChat_APP_KEY, true);
                api.registerApp(ConstKey.WeChat_APP_KEY);
                api.handleIntent(getIntent(), this);
        }

        @Override
        public void onReq(BaseReq req) {
        }

        @Override
        public void onResp(BaseResp resp) {
                int result = 0;
                switch (resp.errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                                result = R.string.share_success;
                                break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                                result = R.string.share_canceled;
                                break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                result = R.string.share_deny;
                                break;
                        default:
                                result = R.string.share_unknown;
                                break;
                }
                ToastUtil.toast(this, result);
                finish();
        }
}
