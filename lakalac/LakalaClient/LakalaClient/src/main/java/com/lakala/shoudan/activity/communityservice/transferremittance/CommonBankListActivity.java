package com.lakala.shoudan.activity.communityservice.transferremittance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.ImageUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.BankInfo;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase.ImageConfig;
import com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase.ImageUtils;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.request.SupportedBankListRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.ui.component.MenuItemLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by HUASHO on 2015/1/21.
 * 银行列表页面，通用界面
 */
public class CommonBankListActivity extends AppBaseActivity {

    private ListView bankListView;
    private ArrayList<BankInfo> lists = null;
    private BankListAdapter adapter;
    /** 显示加载dialog */
    private final int DIALOG_SHOW = 0;
    /** 去掉加载dialog */
    private final int DIALOG_CANCEL = 1;
    /** 更细数据到界面 */
    private final int UPDATE_UI = 2;
    /**	错误信息	*/
    private final int ERROR_MSG=3;

    private String bank = null;
    private String info = null;
    private int pos = 999;
    private String busId = "";//业务Id

    private static final String busidKey = "busidKey";

    public static final String ADD_NOTHING = "add_nothing";//储蓄卡的标识 ,替你还使用 改成busid不加后缀均用这个
    private boolean addNothing = false;//是否是替你还使用的储蓄卡 1EH
    private boolean isWalletTransfer = false;//是否是零钱转出
    private TextView emptyView;

    /**
     * 这是个公共的银行卡列表页面，跳转到此页面，由此方法获取跳转到此页面的intent
     * @param context
     */
    public static Intent getIntent(Context context,String selectBank,String busid){
        Intent intent = new Intent(context,CommonBankListActivity.class);
        intent.putExtra(UniqueKey.SELECT_BANK, selectBank);
        intent.putExtra(busidKey, busid);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_banklist);
        initUI();
    }

    protected void initUI() {
        //更换加载图片的方法
        ImageConfig config = new ImageConfig();
        config.defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_transparent);
        ImageUtils.getInstance(this).setImageConfig(config);

        //获取业务id
        busId = getIntent().getStringExtra(busidKey);
        if(busId.equals(UniqueKey.XINYONGKA_ID)){
            navigationBar.setTitle(R.string.signing_bank_list);
        }else if(busId.equals(UniqueKey.QIANYUEKA_ID)){
            navigationBar.setTitle(getResources().getString(R.string.sign_card_bank));
        }else{
            // 标题
            navigationBar.setTitle(R.string.receiver_bank);
        }
        bankListView = (ListView) findViewById(R.id.bank_list);
        emptyView = (TextView)findViewById(R.id.emptyView);
        bank = getIntent().getStringExtra(UniqueKey.SELECT_BANK);

        //根据业务id配置数据统计和标题文字
        if (UniqueKey.HUANDAIKUAN_ID.equals(busId)) {//还贷银行列表
            navigationBar.setTitle(R.string.txt_daikuanyinhang);
        }else if (UniqueKey.ZHUANGZHANG_ID.equals(busId)) {//转账银行列表

        }
//        else if (UniqueKey.XINYONGKA_ID.equals(busId)){//信用卡银行列表
//
//        }
        else if (UniqueKey.WOYAOSHOUKUAN_ID.equals(busId)) {//我要收款银行列表

        }else if (UniqueKey.DAEKAITONGOPEN_ID.equals(busId)) {//大额开通转出卡列表
            navigationBar.setTitle(R.string.remit_pay_bank);
        }else if (UniqueKey.QIANYUEKA_XYK_ID.equals(busId)) {//签约卡，信用卡银行列表
            navigationBar.setTitle(R.string.signing_reditCard_bank_list);
        }

        lists = new ArrayList<BankInfo>();
        addNothing = getIntent().getBooleanExtra(ADD_NOTHING, false);
        isWalletTransfer = getIntent().getBooleanExtra(Constants.IntentKey.WALLET_TRANSFER_BANK,false);
        if(addNothing){
            if("1EH".equals(busId)){
                navigationBar.setTitle(getResources().getString(R.string.saving_card_bank_list));
            }else if("1EH_1".equals(busId)) {
                navigationBar.setTitle(getResources().getString(R.string.creadit_card_bank_list));
            }
        }
        if (isWalletTransfer){
            navigationBar.setTitle("支持银行");
            getWalletTransferBankList();
        }else {
            getBankList();
        }
    }

    private void getWalletTransferBankList(){

        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.SUPPORTED_BUSINESS_BANK_LIST);
        SupportedBankListRequest params = new SupportedBankListRequest(this);
        params.setBusId("300007_1");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jobj = new JSONObject(resultServices.retData);
                        JSONArray jsonArray = jobj.getJSONArray("List");
                        lists.clear();
                        if (jsonArray != null && jsonArray.length() > 0){
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                BankInfo bankInfo = new BankInfo();
                                bankInfo.setBankName(jsonObject.optString("bankName"));
                                bankInfo.setBankCode(jsonObject.optString("bankCode"));
                                lists.add(bankInfo);
                            }
                        }
                        getBankOver();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                getBankOver();
            }
        });
        WalletServiceManager.getInstance().start(params,request);
    }

    /**
     * 获取银行列表
     */
    private void getBankList() {
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.BANK_LIST);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid", BankBusid.TRANSFER);
        params.put("infoType", BankInfoType.DEFAULT);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        lists.clear();
                        if(jsonArray != null && jsonArray.length() > 0){
                            int length = jsonArray.length();
                            for(int i = 0;i<length;i++){
                                BankInfo info = JSON.parseObject(jsonArray.getJSONObject(i)
                                                                         .toString(),BankInfo.class);
                                lists.add(info);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.toast(CommonBankListActivity.this, getString(R.string.socket_fail));
                    }
                }
                getBankOver();
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(CommonBankListActivity.this,getString(R.string.socket_fail));
                getBankOver();
            }
        });
        request.execute();
    }

    private void getBankOver(){
        adapter = new BankListAdapter(CommonBankListActivity.this);
        bankListView.setAdapter(adapter);
        bankListView.setEmptyView(emptyView);
        adapter.notifyDataSetChanged();
    }

    public class BankListAdapter extends BaseAdapter {

        private Context context;

        public BankListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public BankInfo getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MenuItemLayout itemLayout;
            if (convertView == null) {
                convertView = LinearLayout.inflate(context, R.layout.listitem_common_banklist, null);
                itemLayout = (MenuItemLayout) convertView.findViewById(R.id.layout);
                ImageView iconView = itemLayout.getLeftIconView();
                ViewGroup.LayoutParams params = iconView.getLayoutParams();
                if(params == null){
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                                                                     .WRAP_CONTENT,
                                                             RelativeLayout.LayoutParams.WRAP_CONTENT);
                }
                params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50.0f,
                                                         context.getResources().getDisplayMetrics
                                                                 ());
                params.height = params.width;
                iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                convertView.setTag(itemLayout);
            } else {
                itemLayout = (MenuItemLayout) convertView.getTag();
            }
            BankInfo bankInfo = lists.get(position);
            //设置icon
            Bitmap bitmap = ImageUtil.getBitmapInAssets(CommonBankListActivity.this,
                                                        "bank_icon/"+bankInfo.getBankCode() + "" +
                                                                ".png");
            if(bitmap == null){
                if (bankInfo.getIcon() != null){
                    String iconUrl = bankInfo.getIcon();
                    ImageUtils.getInstance(parent.getContext()).loadBitmap(iconUrl,
                            itemLayout.getLeftIconView());
                }

            }else{
                itemLayout.setLeftIconBitmap(bitmap);
            }
            //设置text
            itemLayout.setLeftText(bankInfo.getBankName());
            //设置分割线
            int itemCnt = getCount();
            if(position == itemCnt -1){
                itemLayout.getTopDivide().setVisibility(View.INVISIBLE);
                itemLayout.getBottomDivide().setVisibility(View.INVISIBLE);
            }else{
                itemLayout.getTopDivide().setVisibility(View.INVISIBLE);
                itemLayout.getBottomDivide().setVisibility(View.VISIBLE);
                itemLayout.setDivideAlign(MenuItemLayout.ALIGN_BOTTOM_DIVIDE);
            }
            if (!isWalletTransfer){
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BankInfo bankInfo=lists.get(position);
                        Intent intent = new Intent();
                        intent.putExtra("bankInfo", bankInfo);
                        setResult(UniqueKey.BANK_LIST_CODE, intent);
                        finish();
                    }
                });
            }
            return convertView;
        }
    }
}
