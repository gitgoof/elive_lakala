package com.lakala.shoudan.activity.shoudan.records;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.shoudan.QueryRecordsResponse;
import com.lakala.shoudan.bll.service.shoudan.RspDealType;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.RecordsResultCallback;
import com.lakala.shoudan.common.util.DateUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.RecordDetail;
import com.lakala.shoudan.datadefine.SerializableRecords;
import com.lakala.shoudan.datadefine.SerializableRecords.TransTotal;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertListDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 交易查询:类型,时间 选择
 * Created by More on 14-1-14.
 */
public class RecordsQuerySelectionActivity extends AppBaseActivity implements View.OnClickListener {


    public static final String RECORDS_LIST = "RECORDS_LIST";

    public static final String QUERY_DATE = "QUERY_DATE";

    public static final String RECORDS_AMOUNT_SIZE = "RECORDS_AMOUNT_SIZE";
    public static final String DEAL_TYPE = "DEAL_TYPE";
    public static final String DEAL_TYPES = "DEAL_TYPES";

    public static final int RESULT = 0x330;
    public static final int RESULT_MORE = 0x331;
    public static final int DEAL_TYPE_RESULT = 0x4551;

    private int[] clickableViews = {
            R.id.tv_selection_end_date,
            R.id.tv_selection_start_date,
            R.id.selection_type,
            R.id.query_next
    };

    private TextView startDate;//开始日期
    private TextView endDate;//截至日期
    private TextView selectionType;//选择类型
    private int queryType = -1;
    private ArrayList<RspDealType> rspDealTypes = new ArrayList<>();
    private RspDealType rDealType;

    private MyDate strarMyDate;
    private MyDate endMyDate;

    private ListView mListView;
    List<RecordDetail> recordDetailLists;
    RecordDetailListAdapter recordDetailListAdapter;
    private int lastItem = 0;
    private int firstItem = 0;
    private int prepage = 1, totalPage;
    private String successAmount, successCount, cancelAmount, cancelCount;
    private TextView tvCount, tvTotalAmount;
    private RspDealType oldDealType;
    private View noResultLayout, resultLayout;
    private boolean isLast;
    private String qType = "";
    AlertListDialog dialog = null;


    public enum Type {
        NONE(""),
        COLLECTION_RECORD("收款记录"),
        LIFE_RECORD("生活交易");

        private String value;

        Type(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    private void dealMoreResult(SerializableRecords records) {
        List<RecordDetail> recordList = records.getRecordDetailList();
        if (null == recordList || recordList.size() <= 0) {
            showMessage("没有更多的记录");
        } else {
            //显示交易笔数和交易金额
            initViewData(records);

            recordDetailLists.addAll(recordList);
            recordDetailListAdapter.notifyDataSetChanged();//更新界面数据显示
            showResultLayout();
        }
    }

    private void dealResult(SerializableRecords records) {
        List<RecordDetail> recordList = records.getRecordDetailList();
        if (null == recordList || recordList.size() <= 0) {
            showNoResultLayout();
        } else {
            //显示交易笔数和交易金额
            initViewData(records);

            recordDetailLists.clear();
            recordDetailLists.addAll(recordList);
            recordDetailListAdapter.notifyDataSetChanged();//更新界面数据显示
            showResultLayout();
        }
    }

    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                //查询
                selectionQuery();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_query_selection);
        initUI();
    }


    @Override
    protected void initUI() {
        super.initUI();
        //Title
        navigationBar.setTitle("交易记录");
        navigationBar.setActionBtnVisibility(View.VISIBLE);
        navigationBar.setActionBtnText("查询");
        navigationBar.setActionBtnEnabled(true);
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        //要查询的交易类型
        selectionType = (TextView) findViewById(R.id.tv_selection_type);

        mListView = (ListView) findViewById(R.id.record_detail_list);

        //点击事件
        for (int i = 0; i < clickableViews.length; i++) {
            findViewById(clickableViews[i]).setOnClickListener(this);
        }
        String retData = getIntent().getStringExtra("retData");
        setType(retData);
        initDate();
        tvCount = (TextView) findViewById(R.id.tv_total_count);
        tvTotalAmount = (TextView) findViewById(R.id.tv_total_amount);
        noResultLayout = findViewById(R.id.no_record_icon);
        resultLayout = findViewById(R.id.layout_result);
        //交易明细列表：
        recordDetailLists = new ArrayList<RecordDetail>();
        recordDetailListAdapter = new RecordDetailListAdapter(this, recordDetailLists);
        mListView.setAdapter(recordDetailListAdapter);
        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (recordDetailListAdapter != null) {
                    if (prepage == totalPage && isLast) {
                        //加载完毕
                        ToastUtil.toast(context, R.string.load_records_finished);
                    }
                    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isLast && prepage < totalPage) {
                        // 加载下一页数据
                        queryRecords();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
                firstItem = firstVisibleItem;
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    isLast = true;
                } else {
                    isLast = false;
                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            boolean clicking = false;

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int itemIndex,
                                    long arg3) {
                if (!clicking) {
                    clicking = true;
                    Intent intent = new Intent(RecordsQuerySelectionActivity.this, RecordDetailActivity.class);
                    intent.putExtra(Constants.IntentKey.RECORD_DETAL, recordDetailLists.get(itemIndex));
                    intent.putExtra("code", rDealType);
                    startActivity(intent);
                    clicking = false;
                }
            }
        });

        qType = getIntent().getStringExtra("type");
        if (!qType.equals("") || !qType.equals(Type.COLLECTION_RECORD.getValue()) || !qType.equals(Type.LIFE_RECORD.getValue())) {
            for (int i = 0; i < rspDealTypes.size(); i++) {
                RspDealType rt = rspDealTypes.get(i);
                if (rt.getName().equals(qType)) {
                    queryType = i;
                    selectionType.setText(qType);
                    onNavBarClickListener.onNavItemClick(NavigationBar.NavigationBarItem.action);
                    break;
                }
            }
        }
    }

    private void showNoResultLayout() {
        noResultLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
    }

    private void showResultLayout() {
        noResultLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);
    }

    private void changeResultTips(String type, String count, String amount) {
        if (type.equals(rDealType.getName())) {
            tvCount.setText("共" + count + "笔交易");
            tvTotalAmount.setText("总交易金额" + Util.formatDisplayAmount(Util.fen2Yuan(amount)));
        }
    }

    /**
     * 设置头部数据
     *
     * @param serializableRecords
     */
    private void initViewData(SerializableRecords serializableRecords) {

        List<TransTotal> transTotals = serializableRecords.getTransTotalList();
        String revocationCount = "";
        String revocationAmount = "";
        for (int i = 0; i < transTotals.size(); i++) {
            if (transTotals.get(i).getDealTypeName().contains("撤销")) {
                revocationCount = transTotals.get(i).getSuccessCount();
                revocationAmount = transTotals.get(i).getSuccessAmount();
                break;
            }
        }

        int totalCount = 0;
        int totalAmmount = 0;
        for (TransTotal transTotal : transTotals) {
            String detalType = transTotal.getDealTypeName();
            String ammount = transTotal.getSuccessAmount();
            String count = transTotal.getSuccessCount();
            if ("收款".equals(transTotal.getDealTypeName()) || "扫码收款".equals(transTotal.getDealTypeName())) {
                if (null != revocationAmount && !"".equals(revocationAmount) && !"null".equals(revocationAmount)) {
                    ammount = String.valueOf(Integer.parseInt(ammount) - Integer.parseInt(revocationAmount));
                }
                count = String.valueOf(serializableRecords.getTotalCount());
                changeResultTips(transTotal.getDealTypeName(), count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("转账".equals(transTotal.getDealTypeName())) {
                changeResultTips("个人转账", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("手机充值".equals(transTotal.getDealTypeName())) {
                changeResultTips("手机充值", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("信用卡还款".equals(transTotal.getDealTypeName())) {
                changeResultTips("信用卡还款", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("资金归集".equals(transTotal.getDealTypeName())) {
                changeResultTips("资金归集", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("社区商城".equals(transTotal.getDealTypeName())) {
                changeResultTips("社区商城", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("特权购买".equals(transTotal.getDealTypeName())) {
                changeResultTips("特权购买", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            } else if ("大额收款".equals(transTotal.getDealTypeName()) || "P08".equals(transTotal.getDealTypeName())) {
                count = String.valueOf(serializableRecords.getTotalCount());
//                 if(null != revocationAmount && !"".equals(revocationAmount) && !"null".equals(revocationAmount)){
//                     ammount = String.valueOf(Integer.parseInt(ammount)-Integer.parseInt(revocationAmount));
//                 }
                changeResultTips("大额收款", count, ammount);
                totalCount += Integer.parseInt(count);
                totalAmmount += Integer.parseInt(ammount);
            }
        }
        if ("全部".equals(rDealType.getName()) || "".equals(rDealType.getName())) {
            changeResultTips("", totalCount + "", totalAmmount + "");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selection_type:
                //选择查询 类型
                showQueryTypeDialog();
                break;
            case R.id.tv_selection_start_date:
                showDateSelectedDialog(true);
                //开始日期
                break;
            case R.id.tv_selection_end_date:
                showDateSelectedDialog(false);
                //截止日期
                break;
            case R.id.query_next:
                //点击查询
                selectionQuery();
                break;
            default:
                break;
        }
    }


    private String strStartDate;
    private String strEndDate;

    private void initDate() {
        //查询日期初始化
        startDate = (TextView) findViewById(R.id.tv_selection_start_date);
        endDate = (TextView) findViewById(R.id.tv_selection_end_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tempDate = sdf.format(new Date());
        //设置前30天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, -30);
        strStartDate = sdf.format(calendar.getTime());
        strEndDate = tempDate;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        startDate.setText(sdf.format(calendar.getTime()));
        endDate.setText(sdf.format(new Date()));
        //设置时间选择框的时间为当日
        Calendar calendarDate = Calendar.getInstance(Locale.CHINA);
        Date myDate = new Date();//初始化起始日期
        calendarDate.setTime(myDate);
        int cyear = calendarDate.get(Calendar.YEAR);
        int month = calendarDate.get(Calendar.MONTH);
        int day = calendarDate.get(Calendar.DAY_OF_MONTH);
        strarMyDate = new MyDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        endMyDate = new MyDate(cyear, month, day);
    }

    /**
     * 选择日期
     */
    boolean isDatePicCancel = false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    int year;
    int monthOfYear;
    int dayOfMonth;

    protected void showDateSelectedDialog(final boolean ifSetStartDate) {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int y, int m,
                                  int d) {
                isDatePicCancel = false;
                // month should add "1"
                year = y;
                monthOfYear = m + 1;
                dayOfMonth = d;
                //set date
            }
        };
        int cyear, month, day;
        if (ifSetStartDate) {
            cyear = strarMyDate.getYear();
            month = strarMyDate.getMonth();
            day = strarMyDate.getDay();
        } else {
            cyear = endMyDate.getYear();
            month = endMyDate.getMonth();
            day = endMyDate.getDay();
        }
        DatePickerDialog dialog = new DatePickerDialog(this, listener, cyear, month, day);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                isDatePicCancel = true;
                return false;
            }
        });
        dialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        isDatePicCancel = true;
                    }
                }
        );
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isDatePicCancel) {
                    //query records
                    if (year < 1000)        //如果年小于1000，不设置时间
                        return;
                    String dateString = year + "-" + monthOfYear + "-" + dayOfMonth;
                    String dateString2 = "";
                    try {
                        Date date = simpleDateFormat.parse(dateString);
                        dateString2 = simpleDateFormat2.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (ifSetStartDate) {
                        //设置开始日期
                        startDate.setText(dateString2);
                        strStartDate = "" + year + "-" + (monthOfYear) + "-" + dayOfMonth;
                        strarMyDate = new MyDate(year, monthOfYear - 1, dayOfMonth);
                    } else {
                        //设置截止日期
                        endDate.setText(dateString2);
                        strEndDate = "" + year + "-" + (monthOfYear) + "-" + dayOfMonth;
                        endMyDate = new MyDate(year, monthOfYear - 1, dayOfMonth);
                    }
                }
                isDatePicCancel = false;
            }
        });
        dialog.setCancelable(true);
        dialog.show();

    }

    /**
     * 点击查询
     */
    private void selectionQuery(){
    	switch (checkDate()) {
		case 0:

			break;
		case 1:
            ToastUtil.toast(context,"结束时间大于今天，请重新选择结束时间！");
    		return ;
		case -1:
            ToastUtil.toast(context,"开始时间大于结束时间，请重新选择起始时间！");
    		return ;
		default:
			break;
		}
        rDealType=new RspDealType();
        if(queryType>=0){
            oldDealType = rDealType;
            rDealType = rspDealTypes.get(queryType);
        }

        if("".equals(rDealType.getName()) && "".equals(rDealType.getCode())){
            ToastUtil.toast(context, "请选择交易类型");
            return;
        }

        showProgressDialog(R.string.querying_records);
        if(!strStartDate.contains("00:00:00")){//避免重复添加
            strStartDate +=" 00:00:00";
        }
        if(!strEndDate.contains("23:59:59")){//避免重复添加
            strEndDate +=" 23:59:59";
        }
        prepage = 1;
        isLast = false;
        RecordsResultCallback callback = new RecordsResultCallback() {
            @Override
            public void onSuccess(QueryRecordsResponse rsp) {
                hideProgressDialog();
                dealResponse(rsp);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        };


        ShoudanService.getInstance().queryTradeRecords(false, 1, strStartDate, strEndDate, rDealType.getCode(), callback);

    }


    private void dealMoreResponse(QueryRecordsResponse rsp) {
        SerializableRecords records = null;
        if (rsp != null && rsp.isPass()) {// 成功
            totalPage = rsp.getCancelableRecords().getTotalPage();// 总页数
            records = rsp.getCancelableRecords();
//                        recordDetailLists.addAll(records.getRecordDetailList());
            successCount = rsp.getSuccessCount();
            successAmount = rsp.getSuccessAmount();
            cancelCount = rsp.getCancelCount();
            cancelAmount = rsp.getCancelAmount();
            prepage++;
        } else {
            //获取失败
            showMessage(rsp.getErrMsg());
        }
        dealMoreResult(records);
    }

    private void dealResponse(QueryRecordsResponse rsp) {
        SerializableRecords records = null;
        if (rsp != null && rsp.isPass()) {// 成功
            records = rsp.getCancelableRecords();
            totalPage = records.getTotalPage();// 总页数
//                        recordDetailLists.clear();//点击查询，成功与否都需要把原来的删除掉
//                        recordDetailLists.addAll(records.getRecordDetailList());
            successAmount = rsp.getSuccessAmount();
            successCount = rsp.getSuccessCount();
            cancelAmount = rsp.getCancelAmount();
            cancelCount = rsp.getCancelCount();
        } else {
            //获取失败
            if (isUserTokenOk()) {
                showMessage(rsp.getErrMsg());
            }
        }
        dealResult(records);
    }

    protected boolean isUserTokenOk() {
        return true;
    }

    /**
     * 查询交易记录,下拉加载更多
     */
    private synchronized void queryRecords() {

        if (prepage == 1)
            showProgressDialog(R.string.querying_records);
        else {
            showProgressDialog(R.string.querying_records_more);
        }

        int tempPrepage = prepage + 1;
        RecordsResultCallback callback = new RecordsResultCallback() {
            @Override
            public void onSuccess(QueryRecordsResponse rsp) {
                hideProgressDialog();
                dealMoreResponse(rsp);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().queryTradeRecords(false, tempPrepage, strStartDate, strEndDate, rDealType.getCode(), callback);

    }

    private void setType(String retData) {

        rspDealTypes.clear();

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(retData);
            if (jsonArray == null) {
                return;
            }
            JSONObject json = jsonArray.getJSONObject(0);
            String extInfo = json.optString("extInfo");
            JSONArray jarr = new JSONArray(extInfo);
            for (int i = 0; i < jarr.length(); i++) {
                RspDealType rType = new RspDealType();
                try {
                    JSONObject jObject = jarr.getJSONObject(i);
                    rType.setCode(jObject.getString("code"));
                    rType.setName(jObject.getString("name"));
                    rspDealTypes.add(rType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showQueryTypeDialog() {

        if (rspDealTypes == null) {
            rspDealTypes = new ArrayList<>();
        }

        int size = rspDealTypes.size();
        final String[] queryTypeItem = new String[size];
//        queryTypeItem[0]=getString(R.string.all);
        for (int i = 0; i < rspDealTypes.size(); i++) {
            queryTypeItem[i] = rspDealTypes.get(i).getName();
        }
        dialog = DialogCreator.createListDialog(
                context, null, queryTypeItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectionType.setText(queryTypeItem[i]);
                        queryType = i;
                        if (dialog.isShowing()) {
                            dialogInterface.dismiss();
                        }
                    }
                }
        );
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * 检查起始时间的合法性
     *
     * @return
     */
    private int checkDate() {
        int reslt = -1;
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sFormat.parse(strStartDate);
            Date date2 = sFormat.parse(strEndDate);
            if (date1.before(date2))
                reslt = 0;
            else if (date1.equals(date2)) {
                reslt = 0;
            } else {
                reslt = -1;
            }

            Date today = new Date();
            if (date2.after(today))
                reslt = 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return reslt;
    }

    class MyDate {
        private int year;
        private int month;
        private int day;

        public MyDate(int year, int month, int day) {
            super();
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

    }

    /**
     * 交易记录列表适配器
     *
     * @author More
     */
    private class RecordDetailListAdapter extends BaseAdapter {

        private List<RecordDetail> recordInfoList;
        private Context context;
        DateUtil dateUtil = new DateUtil();

        public RecordDetailListAdapter(Context context,
                                       List<RecordDetail> lists) {
            this.recordInfoList = lists;
            this.context = context;
        }

        @Override
        public int getCount() {
            return recordInfoList.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LinearLayout.inflate(context, R.layout.shoudan_recordlist_item, null);
                holder = new Holder();
                holder.amount = (TextView) convertView.findViewById(R.id.amount);

                holder.collectionState = (TextView) convertView.findViewById(R.id.collection_state);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            RecordDetail tempRecord = recordInfoList.get(position);
            String amt = tempRecord.getDealAmount();
            String formatamt = Util.formatAmount(amt);
            String fuhao = "";
            int textColor = Color.BLACK;

            RecordDetail.EPosStatus status = tempRecord.getPosStatus();
            String reslt;
            if (tempRecord.getStatus().equals("SUCCESS"))
                reslt = "成功";
            else {
                reslt = "失败";
            }
            if ("收款".equals(tempRecord.getDealTypeName()) || "扫码收款".equals(tempRecord.getDealTypeName())) {
                switch (status) {
                    case RECEIVE_SUCCESS:
                        //收款成功
                        holder.collectionState.setTextColor(Color.BLACK);
                        holder.collectionState.setText(R.string.collection_success);
                        textColor = Color.RED;
                        fuhao = "+";
                        break;
                    case RECEIVE_FAILURE:
                        //收款失败
                        holder.collectionState.setTextColor(Color.BLACK);
                        holder.collectionState.setText(R.string.collection_fail);
                        break;
                    case REVOCATION_SUCCESS:
                        //撤销成功
                        holder.collectionState.setTextColor(Color.RED);
                        holder.collectionState.setText("收款成功,撤销成功");//R.string.revocation_success);
                        textColor = Color.GREEN;
                        fuhao = "-";
                        break;
                    case REVOCATION_FAILURE:
                        //撤销失败
                        holder.collectionState.setTextColor(Color.BLACK);
                        holder.collectionState.setText("收款成功,撤销失败");//R.string.revocation_fail);
                        break;
                    default:
                        break;
                }
            } else if ("收款撤销".equals(tempRecord.getDealTypeName())) {
                if ("SUCCESS".equals(tempRecord.getStatus())) {
                    holder.collectionState.setTextColor(Color.RED);
                    holder.collectionState.setText("收款成功,撤销成功");//R.string.revocation_success);
                    textColor = Color.GREEN;
                    fuhao = "-";
                } else {
                    //撤销失败
                    holder.collectionState.setTextColor(Color.BLACK);
                    holder.collectionState.setText("收款成功,撤销失败");//R.string.revocation_fail);
                }
            } else {
                holder.collectionState.setTextColor(Color.BLACK);
                if ("转账".equals(tempRecord.getDealTypeName())) {
                    holder.collectionState.setText("转账" + "" + reslt);
                } else if ("社区商城".equals(tempRecord.getDealTypeName())) {
                    holder.collectionState.setText("交易" + "" + reslt);
                } else if ("特权购买".equals(tempRecord.getDealTypeName())) {
                    holder.collectionState.setText("特权购买" + reslt);
                } else {
                    if ("成功".equals(reslt) && "P08".equals(tempRecord.getDealTypeCode())) {
                        textColor = Color.RED;
                        fuhao = "+";
                    }
                    holder.collectionState.setText(tempRecord.getDealTypeName() + "" + reslt);
                }
            }

            holder.amount.setTextColor(textColor);
            holder.amount.setText(fuhao + formatamt);
            String tempDate = tempRecord.getDealDateTime();
            holder.time.setText(DateUtil.formatDateStr(tempDate, "yyyy/MM/dd"));
            return convertView;
        }

        private class Holder {
            public TextView amount;
            public TextView cardNo;
            public TextView collectionState;
            public TextView time;
        }

    }
}

