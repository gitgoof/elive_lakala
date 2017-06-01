package com.lakala.shoudan.activity.shoudan.finance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.consts.BankInfoType;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.adapter.BankChooseAdapter;
import com.lakala.shoudan.activity.shoudan.finance.adapter.BankChooseExpandAdapter;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.datadefine.OpenBankInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by fengx on 2015/10/20.
 */
public class BankChooseActivity extends AppBaseActivity {

    private ExpandableListView expandLv;
    private ListView lv;
    private BankChooseAdapter adapter1;
    private BankChooseExpandAdapter adapter2;
    private List<String> lstChat = new ArrayList<String>();
    private List<String> parent = new ArrayList<String>();
    private Map<String, List<OpenBankInfo>> child = new LinkedHashMap<String, List<OpenBankInfo>>();
    private ArrayList<OpenBankInfo> lists = new ArrayList<OpenBankInfo>();
    private float firstY, upY, height;   //首字母Y坐标、手抬起时的Y坐标与总高度
    private int upPosition, size;     //手抬起时选中的position

    private static final String busidKey = "busidKey";
    private static final String infoTypeKey = "infoTypeKey";
    private BankBusid busid = BankBusid.TRANSFER;
    private BankInfoType infoType = BankInfoType.DEFAULT;

    public static void openForResult(Activity context, BankBusid busid, BankInfoType infoType,
                                     int requestCode){
        Intent intent = new Intent(context,BankChooseActivity.class);
        intent.putExtra(busidKey,busid);
        intent.putExtra(infoTypeKey,infoType);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_choose);
        ButterKnife.bind(this);
        //获取业务id
        Intent intent = getIntent();
        busid = (BankBusid) intent.getSerializableExtra(busidKey);
        infoType = (BankInfoType) intent.getSerializableExtra(infoTypeKey);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        expandLv = (ExpandableListView) findViewById(R.id.expand_lv);
        lv = (ListView) findViewById(R.id.lv_char);
        lists = new ArrayList<>();
        navigationBar.setTitle(R.string.receiver_bank);
        switch (busid){
            case MPOS_ACCT:
                navigationBar.setTitle("开户银行");
                break;
            case TRANSFER:
                navigationBar.setTitle(R.string.receiver_bank);
                break;
            case WALLET_TRANSFER:
                navigationBar.setTitle("支持银行");
                break;
            case LOAN_FOR_YOU:
                if(infoType == BankInfoType.CREDIT){
                    navigationBar.setTitle("信用卡银行列表");
                }else if(infoType == BankInfoType.DEBIT){
                    navigationBar.setTitle("储蓄卡银行列表");
                }
                break;
        }
        getBankList();
    }


    private void getBankList() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.BANK_LIST);
        HttpRequestParams params = request.getRequestParams();
        params.put("busid",busid.getValue());
        params.put("infoType",infoType.name());
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray array = null;
                    try {
                        array = new JSONArray(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int length = array == null ? 0 : array.length();
                    for (int i = 0; i < length; i++) {
                        try {
                            JSONObject jo = array.getJSONObject(i);
                            OpenBankInfo openBankInfo = new OpenBankInfo();
                            openBankInfo.bankname = jo.optString("bankName","");
                            openBankInfo.bankCode = jo.optString("bankCode","");
                            openBankInfo.bankpinyin = jo.optString("bankPinyin","");
                            openBankInfo.paymentflag = jo.optBoolean("paymentflag",false)?"1":"0";
                            openBankInfo.commonflag = jo.optBoolean("commonflag",false)?"1":"0";
                            openBankInfo.bankimg = jo.optString("icon","");
                            lists.add(openBankInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    initData();
                } else {
                    showMessage(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        request.setResponseHandler(callback);
        request.execute();
    }

    private void initData() {

        ArrayList<OpenBankInfo> temp;

        for (char c = 'A'; c <= 'Z'; c++) {
            lstChat.add(c + "");
        }
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int h = wm.getDefaultDisplay().getHeight();
        height = h - getResources().getDimension(R.dimen.bank_char_height_to_cut);
        firstY = getResources().getDimension(R.dimen.bank_char_padding_top);

        adapter1 = new BankChooseAdapter(this, lstChat, height);
        lv.setAdapter(adapter1);
        scrollListview();

        for (char c = 'A'; c <= 'Z'; c++) {

            temp = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {

                OpenBankInfo info = lists.get(i);
                //判断字母是否为c
                char[] chars = info.bankpinyin.toUpperCase().toCharArray();
                if (chars.length == 0){
                    return;
                }
                if (chars[0] == c) {
                    temp.add(info);
                }
            }
            if (temp.size() > 0) {
                child.put(c + "", temp);
            }
        }

        if (child.size() == 0){
            return;
        }
        for (Map.Entry<String, List<OpenBankInfo>> entry : child.entrySet()) {
            parent.add(entry.getKey());
        }
        size = parent.size();
        adapter2 = new BankChooseExpandAdapter(parent, child, this);
        expandLv.setAdapter(adapter2);
        expandLv.setGroupIndicator(null);
        int groupCount = expandLv.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandLv.expandGroup(i);
        }
        //禁止关闭
        expandLv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expandLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent1, View v, int groupPosition, int childPosition, long id) {
                if(busid == BankBusid.MPOS_ACCT &&
                        (infoType == BankInfoType.PUBLIC || infoType== BankInfoType.PRIVATE)){
                    OpenBankInfo openBankInfo = child.get(parent.get(groupPosition)).get(childPosition);
                    Intent intent = new Intent();
                    intent.putExtra("openBankInfo", openBankInfo);
                    setResult(UniqueKey.BANK_LIST_CODE, intent);
                    finish();
                }
                return true;
            }
        });
    }

    /**
     * 监听listview滑动事件
     */
    private void scrollListview() {

        lv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        expandLv(event);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        expandLv(event);
                        break;

                    case MotionEvent.ACTION_UP:
                        expandLv(event);
                        break;
                }
                return true;
            }
        });
    }

    public void expandLv(MotionEvent event) {
        String key;
        float rate;
        int i = 0;
        int position = 0;

        upY = event.getY();
        if (upY > 0) {
            rate = upY / height;
            upPosition = (int) (26 * rate);
            if (upPosition >=26){
                upPosition = 25;
            }
            key = lstChat.get(upPosition);
            for (Map.Entry<String, List<OpenBankInfo>> entry : child.entrySet()) {
                if (key.equalsIgnoreCase(entry.getKey())){
                    position = i;
                    expandLv.setSelectedGroup(position);
                    break;
                }
                i++;
            }
        }
    }
}
