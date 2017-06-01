package com.lakala.shoudan.activity.merchant.upgrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.AreaEntity;
import com.lakala.ui.component.LabelEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支行信息页面
 * Created by huangjp on 2016/3/15.
 */
public class BranchInfoActivity extends AppBaseActivity {
    private LabelEditText letCity;
    private LabelEditText letArea;
    private LabelEditText letBranch;
    private CommonServiceManager serviceManager;
    private AreaEntity currentProvice = null;
    private List<AreaEntity> provices = new ArrayList<>();
    private Map<String, List<AreaEntity>> areasMap = new HashMap();
    private AreaEntity currentArea = null;
    private Map<String, List<AreaEntity>> branchesMap = new HashMap<>();
    private AreaEntity currentBankBranchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_info);
        serviceManager = CommonServiceManager.getInstance();
        initUI();
        initEvent();
        initOkBtn();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.branch_info);

        MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();

        LabelEditText letBank = (LabelEditText) findViewById(R.id.labelEditText_bank);
        letBank.setText(merchantInfo.getBankName());

        letCity = (LabelEditText) findViewById(R.id.labelEditText_city);
        letArea = (LabelEditText) findViewById(R.id.labelEditText_area);
        letBranch = (LabelEditText) findViewById(R.id.labelEditText_branch);
    }

    private void initOkBtn() {
        TextView tvOk = (TextView) findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVaild()){
                    Intent intent = new Intent();
                    intent.putExtra("branch",currentBankBranchInfo);
                    setResult(ConstKey.RESULT_BRANCH, intent);
                    finish();
                }
            }
        });
    }

    private boolean isVaild(){
        if (TextUtils.isEmpty(letCity.getText().toString())){
            toast(R.string.city_hint);
            return false;
        }
        if (TextUtils.isEmpty(letArea.getText().toString())){
            toast(R.string.area_hint);
            return false;
        }
        if (TextUtils.isEmpty(letBranch.getText().toString())){
            toast(R.string.branch_hint);
            return false;
        }
        return true;
    }
    private void initEvent() {

        //选择省市
        View.OnClickListener cityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProvicePicker();
            }

        };
        letCity.getContentView().setOnClickListener(cityListener);
        letCity.getRightIconImageView().setOnClickListener(cityListener);

        //选择地区
        View.OnClickListener areaListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreaPicker();
            }

        };
        letArea.getContentView().setOnClickListener(areaListener);
        letArea.getRightIconImageView().setOnClickListener(areaListener);

        //选择支行
        View.OnClickListener branchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBranchPicker();
            }
        };
        letBranch.getContentView().setOnClickListener(branchListener);
        letBranch.getRightIconImageView().setOnClickListener(branchListener);
    }

    private void showBranchPicker() {
        if (currentProvice == null) {
            ToastUtil.toast(context, "请选择所在省市");
            return;
        }
        if (currentArea == null) {
            ToastUtil.toast(context, "请选择所在地区");
            return;
        }
        final String areaCode = currentArea.getCode();
        List<AreaEntity> branchList = null;
        if (branchesMap.containsKey(areaCode)) {
            branchList = branchesMap.get(areaCode);
        }

        AreaRunnable nextRun = new AreaRunnable(branchList) {
            @Override
            public void run() {
                if(getAreas() != null){
                    branchesMap.put(areaCode, getAreas());
                }
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentBankBranchInfo = getAreas().get(which);
                        letBranch.setText(currentBankBranchInfo.toString());
                    }
                };
                showItemsDialog(getAreas(),listener);
            }
        };
        if (branchList == null || branchList.size() == 0) {
            getBranchBank(areaCode,nextRun);
        } else {
            nextRun.run();
        }
    }

    private void showItemsDialog(List<AreaEntity> list, DialogInterface.OnClickListener listener){
        int size = list.size();
        String[] items = new String[size];
        for(int i = 0;i< size;i++){
            items[i] = list.get(i).toString();
        }
        DialogCreator.createCancelableListDialog(context,null,items,listener).show();
    }

    private abstract class AreaRunnable implements Runnable {
        private List<AreaEntity> areas;

        public AreaRunnable(List<AreaEntity> areas) {
            this.areas = areas;
        }

        public List<AreaEntity> getAreas() {
            return areas;
        }

        public AreaRunnable setAreas(List<AreaEntity> areas) {
            this.areas = areas;
            return this;
        }
    }

    private void showAreaPicker() {
        if (currentProvice == null) {
            toast("请选择所在省市");
            return;
        }
        final String code = currentProvice.getCode();
        List<AreaEntity> areaList = null;
        if (areasMap.containsKey(code)) {
            areaList = areasMap.get(code);
        }
        AreaRunnable nextRun = new AreaRunnable(areaList) {
            @Override
            public void run() {
                List<AreaEntity> areas = getAreas();
                if(areas != null){
                    areasMap.put(code, areas);
                }
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentArea = getAreas().get(which);
                        letArea.setText(currentArea.toString());

                        currentBankBranchInfo = null;
                        letBranch.setText("");
                    }
                };
                showItemsDialog(areas,listener);
            }
        };
        if (areaList == null || areaList.size() == 0) {
            getAreasInfo(code,nextRun);
        } else {
            nextRun.run();
        }
    }

    private void showProvicePicker() {
        AreaRunnable nextRun = new AreaRunnable(provices) {
            @Override
            public void run() {
                if(getAreas() != null){
                    provices = getAreas();
                }
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentProvice = getAreas().get(which);
                        letCity.setText(currentProvice.toString());
                        currentArea = null;
                        letArea.setText("");
                        letBranch.setText("");
                    }
                };
                showItemsDialog(getAreas(),listener);
            }
        };
        if (null == provices || provices.size() == 0) {
            getAreasInfo(null,nextRun);
        } else {
            nextRun.run();
        }
    }

    /**
     * @param code
     */
    private void getAreasInfo(final String code, final AreaRunnable nextRun) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<AreaEntity> areaList = AreaEntity.parseList(jsonArray);
                    if (nextRun != null) {
                        nextRun.setAreas(areaList);
                        nextRun.run();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toast(getString(R.string.socket_fail));
            }
        };
        serviceManager.getAreaList(code, callback);
    }


    private void getBranchBank(final String areaCode, final AreaRunnable nextRun) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<AreaEntity> areas = AreaEntity.parseList(jsonArray);
                    if (nextRun != null) {
                        nextRun.setAreas(areas);
                        nextRun.run();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toast(getString(R.string.socket_fail));
            }
        };
        serviceManager.getBankList(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo(), areaCode, callback);
    }
}
