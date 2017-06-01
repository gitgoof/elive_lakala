package com.lakala.platform.swiper.mts.map;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.LklPreferences;
import com.lakala.ui.common.CommmonSelectData;
import com.lakala.ui.common.CommonSelectListAdapter;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.map.ILocationCallback;
import com.lakala.ui.map.LKLMapabcMapManeger;
import com.lakala.ui.map.MapabcOverItem;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.PoiOverlay;
import com.mapabc.mapapi.poisearch.PoiPagedResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ZDGLBranchMapActivity extends MapActivity implements OnItemClickListener{

    private MapView mMapView;
    private MapController mMapController;
    private GeoPoint point;
    private LinearLayout bankTextLayout, branchTextLayout;
    private TextView banklistTextView, branchTextView;
    private String query = null;
    private PoiPagedResult result;
    private PoiOverlay poiOverlay;
    private NavigationBar navigationBar;
    private ListView banklistListView, branchListView;
    private CommonSelectListAdapter banklistAdapter;
    private CommonSelectListAdapter branchlistAdapter;
    private ArrayList<CommmonSelectData> bankLists = new ArrayList<CommmonSelectData>();
    private ArrayList<CommmonSelectData> branchLists = new ArrayList<CommmonSelectData>();
    private MyLocationOverlay myLocationOverlay;
    private boolean isResume = false;
    private List<PoiItem> poiItems = null;

    /**
     * 显示加载dialog
     */
    private final int DIALOG_SHOW = 0;
    /**
     * 去掉加载dialog
     */
    private final int DIALOG_CANCEL = 1;
    /**
     * 显示数据
     */
    private final int UPDATE_UI = 2;
    /**
     * 错误提示
     */
    private final int ERROR_MSG = 3;
    /**
     * .搜索网点错误
     */
    private final int SEARCH_DATA_ERROR = 4;
    /**
     * 显示设置gps对话框
     */
    private final int SHOW_SET_GPS_DIALOG = 5;
    /**
     * 获取银行列表数据完毕
     */
    private final int GET_BANK_DATA_DONE = 6;

    private String info = "";
    private GeoPoint currentPoint;// 当前点
    private GeoPoint souWesPoint;// 西南点
    private GeoPoint norEasPoint;// 东北点
    private int latHeight = 0;
    private int lonHeight = 0;
    private ArrayList<MapabcOverItem.BranchInfo> branchInfos = new ArrayList<>();// 网点数据
    private String defaultBankSelection = "拉卡拉";
    private LKLMapabcMapManeger mapabcMapManeger;
    private Dialog loadingDialog;

    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_zdgl_map);
        getIntent().addCategory("android.intent.category.MAPABC_DEMO");

        navigationBar = (NavigationBar) findViewById(R.id.id_navigation_bar);
        navigationBar.setTitle("网点地图");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);

        banklistTextView = (TextView) findViewById(R.id.bank_list_text);
        branchTextView = (TextView) findViewById(R.id.branch_text);
        banklistListView = (ListView) findViewById(R.id.bank_list);
        branchListView = (ListView) findViewById(R.id.branch);

        bankTextLayout = (LinearLayout) findViewById(R.id.bank_list_text_layout);
        branchTextLayout = (LinearLayout) findViewById(R.id.branch_text_layout);
        CommmonSelectData commmonSelectData0 = new CommmonSelectData();
        commmonSelectData0.setLeftTopText("全部网点");
        CommmonSelectData commmonSelectData1 = new CommmonSelectData();
        commmonSelectData1.setLeftTopText("营业厅");
        CommmonSelectData commmonSelectData2 = new CommmonSelectData();
        commmonSelectData2.setLeftTopText("ATM");
        branchLists.add(commmonSelectData0);
        branchLists.add(commmonSelectData1);
        branchLists.add(commmonSelectData2);
        branchlistAdapter = new CommonSelectListAdapter(this, branchLists,null,false,true,true);
        branchListView.setAdapter(branchlistAdapter);


        loadingDialog = new Dialog(this, R.style.loading_dialog);
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        LinearLayout layout = (LinearLayout) LinearLayout.inflate(this, R.layout.ui_progress_dialog_layout, null);
        // 设置布局
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        banklistTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                branchListView.setVisibility(View.GONE);
                setSelectView(R.drawable.city_down, branchTextView);
                branchTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                if (banklistListView.isShown()) {
                    banklistListView.setVisibility(View.GONE);
                    setSelectView(R.drawable.city_down, banklistTextView);
                    bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                } else {
                    bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_666666));
                    if (bankLists != null && bankLists.size() > 0) {
                        setSelectView(R.drawable.up_arrow, banklistTextView);
                        if (banklistAdapter == null) {
                            banklistAdapter = new CommonSelectListAdapter(ZDGLBranchMapActivity.this, bankLists,null,false,true,true);
                            banklistListView.setAdapter(banklistAdapter);
                        }
                        banklistListView.setVisibility(View.VISIBLE);
                    } else {
                        getBankList();
                    }
                }
            }
        });

        branchTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                banklistListView.setVisibility(View.GONE);
                setSelectView(R.drawable.city_down, banklistTextView);
                bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                if (branchListView.isShown()) {
                    branchTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                    branchListView.setVisibility(View.GONE);
                    setSelectView(R.drawable.city_down, branchTextView);
                } else {
                    branchTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_666666));
                    setSelectView(R.drawable.up_arrow, branchTextView);
                    branchListView.setVisibility(View.VISIBLE);
                }
            }
        });
        banklistListView.setOnItemClickListener(this);
        branchListView.setOnItemClickListener(this);

        mMapView = (MapView) findViewById(R.id.poisearch_MapView);
        mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
        mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度
        // (度 * 1E6)
        mMapController.setCenter(point); // 设置地图中心点
        mMapController.setZoom(12); // 设置地图zoom级别


        mapabcMapManeger = LKLMapabcMapManeger.getInstance();
        mapabcMapManeger.setiLocationCallback(iLocationCallback);
        mapabcMapManeger.init(this, mMapView, true);

    }

    private ILocationCallback iLocationCallback  = new ILocationCallback() {
        @Override
        public void findLocationSuccessful(double geoLat, double geoLng) {
            GeoPoint centerPoint = mMapView.getMapCenter();
            souWesPoint = new GeoPoint((long) (centerPoint.getLatitudeE6() - latHeight / 2),
                    (long) (centerPoint.getLongitudeE6() - lonHeight / 2));
            norEasPoint = new GeoPoint((long) (centerPoint.getLatitudeE6() + latHeight / 2),
                    (long) (centerPoint.getLongitudeE6() + lonHeight / 2));
            initBranchData();
        }

        @Override
        public void findLocationFailed() {

        }

        @Override
        public void netError() {

        }
    };

    @Override
    public void onBackPressed() {

        if (banklistListView.isShown()) {
            bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
            setSelectView(R.drawable.city_down, banklistTextView);
            banklistListView.setVisibility(View.GONE);
        } else if (branchListView.isShown()) {
            branchTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
            setSelectView(R.drawable.city_down, branchTextView);
            branchListView.setVisibility(View.GONE);
        } else {
            finish();
        }
    }


    /**
     * 设置textview 的drawableRight
     *
     * @param drawableId
     * @param textView
     */
    private void setSelectView(int drawableId, TextView textView) {
        Resources res = getResources();
        Drawable drawable = null;
        if (drawableId != 0) {
            drawable = res.getDrawable(drawableId);
            //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        textView.setCompoundDrawables(null, null, drawable, null); //设置右图标
    }


    private void initBranchData() {
        String bankSelection = LklPreferences.getInstance().getString("wddtSelectionFirst");
        String branchSelection = LklPreferences.getInstance().getString("wddtSelectionSecond");
        if (StringUtil.isEmpty(bankSelection)) bankSelection = defaultBankSelection;

        Intent data = getIntent();
        Bundle bundle = data.getBundleExtra(LakalaConstant.BUSINESS_BUNDLE_KEY);
        String banknName="";
        if (bundle!=null){
            String obj = bundle.getString("data");
            try {
                banknName = new JSONObject(obj).getString("bankName");
            } catch (JSONException e) {
                banknName = "";
            }
        }
        if (StringUtil.isNotEmpty(banknName)){//B端页面跳转网点地图
            banklistTextView.setText(banknName);
            query = String.format("\"%s\"", banknName);
            branchlistAdapter.setSelectposition(0);
            branchTextView.setText(branchLists.get(0).getLeftTopText());
            setSelectView(R.drawable.city_down, branchTextView);
            mapabcMapManeger.searBranch(query);
        }else if (data != null && !StringUtil.isEmpty(data.getStringExtra("bankName"))) {
            query = String.format("\"%s\"", data.getStringExtra("bankName"));
            banklistTextView.setText(query);
            branchlistAdapter.setSelectposition(0);
            branchTextView.setText(branchLists.get(0).getLeftTopText());
            setSelectView(R.drawable.city_down, branchTextView);
            mapabcMapManeger.searBranch(query);
        } else {
            if (bankSelection.equals(defaultBankSelection)) {
                banklistTextView.setText(defaultBankSelection);
                branchTextView.setEnabled(false);
                branchTextView.setTextColor(Color.GRAY);
                branchlistAdapter.setSelectposition(0);
                branchTextView.setText(branchLists.get(0).getLeftTopText());
                setSelectView(0, branchTextView);
                getBranchList();
            } else {
                int position = -1;
                banklistTextView.setText(bankSelection);
                branchTextView.setText(branchSelection);
                branchTextView.setTextColor(Color.WHITE);
                position = getSelectionPosition(branchLists, branchSelection);
                branchlistAdapter.setSelectposition(position);
                switch (position) {
                    case 0://全部网点
                        query = String.format("\"%s\"", banklistTextView.getText().toString());
                        break;
                    case 1://营业厅
                        query = String.format("\"%s\"", banklistTextView.getText().toString());
                        break;
                    case 2://ATM
                        query = String.format("\"%s\"", banklistTextView.getText().toString() + branchLists.get(position).getLeftTopText());
                        break;
                }
                mapabcMapManeger.searBranch(query);
            }
        }
    }

    /**
     * 保存最后搜索的条件
     *
     * @param firstSelection  银行搜索选项
     * @param secondSelection 分支搜索选项
     */
    private void saveSelection(String firstSelection, String secondSelection) {
        LklPreferences.getInstance().putString("wddtSelectionFirst", firstSelection);
        LklPreferences.getInstance().putString("wddtSelectionSecond", secondSelection);
    }

    /**
     * 搜索关键字在列表中的位置
     *
     * @param datas           列表数据
     * @param selectionString 关键字
     * @return
     */
    private int getSelectionPosition(ArrayList<CommmonSelectData> datas, String selectionString) {
        int selection = -1;
        if (datas == null || datas.size() == 0) return selection;
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getLeftTopText().equals(selectionString)) {
                selection = i;
                break;
            }
        }
        return selection;
    }


    @Override
    protected void onResume() {
        isResume = true;
        mapabcMapManeger.enableMyLocation();
        super.onResume();
    }

    @Override
    public void finish() {
        //在Activity finish之前，先dismiss Dialogo
        mapabcMapManeger.dismissDialog();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isResume = false;
        mapabcMapManeger.disableMyLocation();
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DIALOG_SHOW:
                    loadingDialog.show();
                    break;
                case DIALOG_CANCEL:
                    if (loadingDialog != null) {
                        loadingDialog.cancel();
                    }
                    break;
                case ERROR_MSG:
                    bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                    loadingDialog.cancel();
                    ToastUtil.toast(ZDGLBranchMapActivity.this, info);
                    break;
                case UPDATE_UI:// 拉卡拉网点  ---适配数据显示到ＵＩ
                    loadingDialog.cancel();
                    if (branchInfos.size() > 0) {
                        mapabcMapManeger.showMapMakers(branchInfos);
                    }
                    break;
                case SEARCH_DATA_ERROR:
                    loadingDialog.cancel();
                    mapabcMapManeger.clearMapMarkers();
                    break;
                case GET_BANK_DATA_DONE://获取银行列表数据ok
                    loadingDialog.cancel();
                    int position = getSelectionPosition(branchLists, banklistTextView.getText().toString());
                    banklistAdapter = new CommonSelectListAdapter(ZDGLBranchMapActivity.this, bankLists,null,false,true,true);
                    branchlistAdapter.setSelectposition(position);
                    banklistListView.setAdapter(banklistAdapter);
                    banklistListView.setVisibility(View.VISIBLE);
                    setSelectView(R.drawable.up_arrow, banklistTextView);
                    break;
            }
        };
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
            if (parentId == R.id.bank_list){//银行列表
                banklistAdapter.setSelectposition(position);
                banklistTextView.setText(bankLists.get(position).getLeftTopText());
                banklistListView.setVisibility(View.GONE);
                setSelectView(R.drawable.city_down, banklistTextView);
                bankTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                String bankText = banklistTextView.getText().toString();
                String branchText = branchTextView.getText().toString();
                if (bankLists.get(position).getLeftTopText().equals(defaultBankSelection)) {
                    branchTextView.setEnabled(false);
                    branchTextView.setTextColor(Color.GRAY);
                    branchTextView.setText(branchLists.get(0).getLeftTopText());
                    branchlistAdapter.setSelectposition(0);
                    setSelectView(0, branchTextView);
                    getBranchList();
                } else {
                    setSelectView(R.drawable.city_down, branchTextView);
                    branchTextView.setEnabled(true);
                    branchTextView.setTextColor(Color.WHITE);
                    if (branchTextView.getText().toString().equals("全部网点")) {
                        branchlistAdapter.setSelectposition(0);
                        query = String.format("\"%s\"", bankLists.get(position).getLeftTopText());
                        mapabcMapManeger.searBranch(query);
                    } else if (branchTextView.getText().toString().equals("营业厅")) {
                        branchlistAdapter.setSelectposition(1);
                        query = String.format("\"%s\"", bankLists.get(position).getLeftTopText());
                        mapabcMapManeger.searBranch(query);
                    } else {//ATM
                        branchlistAdapter.setSelectposition(2);
                        query = String.format("\"%s\"", bankLists.get(position).getLeftTopText() + branchTextView.getText().toString());
                        mapabcMapManeger.searBranch(query);
                    }
                }
                saveSelection(bankText, branchText);
            } else if (parentId == R.id.branch){//分支
                setSelectView(R.drawable.city_down, branchTextView);
                branchTextLayout.setBackgroundColor(getResources().getColor(R.color.gray_CCCCCC));
                branchlistAdapter.setSelectposition(position);
                branchTextView.setText(branchLists.get(position).getLeftTopText());
                branchListView.setVisibility(View.GONE);
                switch (position) {
                    case 0://全部网点
                        query = String.format("\"%s\"", banklistTextView.getText().toString());
                        break;
                    case 1://营业厅
                        query = String.format("\"%s\"", banklistTextView.getText().toString());
                        break;
                    case 2://ATM
                        query = String.format("\"%s\"", banklistTextView.getText().toString() + branchLists.get(position).getLeftTopText());
                        break;
                }
                saveSelection(banklistTextView.getText().toString(), branchTextView.getText().toString());
                mapabcMapManeger.searBranch(query);
            }

    }


    /**
     * 获取附近拉卡拉的网点分页列表
     */
    private  void getBranchList() {
        handler.sendEmptyMessage(DIALOG_SHOW);
        latHeight = mMapView.getLatitudeSpan();// 地图左右经度跨度
        lonHeight = mMapView.getLongitudeSpan();// 地图上下维度跨度
        currentPoint = mMapView.getMapCenter();
        GeoPoint centerPoint = mMapView.getMapCenter();
        souWesPoint = new GeoPoint((long) (currentPoint.getLatitudeE6() - latHeight / 2),
                (long) (currentPoint.getLongitudeE6() - lonHeight / 2));
        norEasPoint = new GeoPoint((long) (currentPoint.getLatitudeE6() + latHeight / 2),
                (long) (currentPoint.getLongitudeE6() + lonHeight / 2));

        ZDGLRequestFactory.storeInfoQry(
                this,
                (double) norEasPoint.getLongitudeE6() / 1E6 + "",
                (double) norEasPoint.getLatitudeE6() / 1E6 + "",
                (double) souWesPoint.getLongitudeE6() / 1E6 + "",
                (double) souWesPoint.getLatitudeE6() / 1E6 + "",
                "20",
                "1").setIHttpRequestEvents(new IHttpRequestEvents(){

            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                try {
                    JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
                    JSONArray jsonArray = response.getJSONArray("returnList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject dataJsonObject = (JSONObject) jsonArray.get(i);
                        MapabcOverItem.BranchInfo branchInfo = new MapabcOverItem.BranchInfo();
                        branchInfo.city = dataJsonObject.getString("City");
                        branchInfo.branchName = dataJsonObject.getString("SNameFull");
                        branchInfo.titleName = dataJsonObject.getString("SRealName");
                        branchInfo.branchShortName = dataJsonObject.getString("SNameShort");
                        branchInfo.address = dataJsonObject.getString("SAddress");
                        branchInfo.latitude = Double.parseDouble(dataJsonObject.getString("SLatitude"));
                        branchInfo.longitude = Double.parseDouble(dataJsonObject.getString("SLongitude"));
                        branchInfos.add(branchInfo);
                        handler.sendEmptyMessage(UPDATE_UI);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(SEARCH_DATA_ERROR);
                }
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                handler.sendEmptyMessage(SEARCH_DATA_ERROR);
            }
        }).execute();
    }

    /**
     * 获取银行列表数据
     */
    private void getBankList() {
        handler.sendEmptyMessage(DIALOG_SHOW);
        ZDGLRequestFactory.banksQuery(this, "").setIHttpRequestEvents(new IHttpRequestEvents() {
            @Override
            public void onSuccess(HttpRequest request) {
                super.onSuccess(request);
                try {
                    JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
                    JSONArray jsonArray = (JSONArray) response.getJSONArray("BankList");
                    CommmonSelectData commmonSelectData = new CommmonSelectData();
                    commmonSelectData.setLeftTopText("拉卡拉");
                    bankLists.add(commmonSelectData);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String bankName = jsonObject.getString("BankName");
                        CommmonSelectData data = new CommmonSelectData();
                        data.setLeftTopText(bankName);
                        bankLists.add(data);
                    }
                    handler.sendEmptyMessage(GET_BANK_DATA_DONE);
                } catch (Exception e1) {
                    handler.sendEmptyMessage(DIALOG_CANCEL);
                }
            }

            @Override
            public void onFailure(HttpRequest request, BaseException exception) {
                super.onFailure(request, exception);
                handler.sendEmptyMessage(DIALOG_CANCEL);

            }
        }).execute();
    }

}
