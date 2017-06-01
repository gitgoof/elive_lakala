package com.lakala.shoudan.activity.loan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeActivity;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.UrlLoaderActivity;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchaseTransInfo2;
import com.lakala.shoudan.activity.treasure.TreasureBuyActivity2;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.component.NavigationBar;

import org.java_websocket.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by HJP on 2015/9/23.
 */
public class CameraGetActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_url_loader);
        ToastUtil.toast(this, "进来了");
    }
}
