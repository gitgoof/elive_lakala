package com.lakala.shoudan.activity.shoudan.records;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.NoRecordsFragment;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.AlertListDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fengxuan on 2015/12/28.
 */
public class TradeQueryActivity extends AppBaseActivity implements View.OnClickListener {

    private TextView startTime;
    private TextView endTime;
    private TextView transType;
    private FrameLayout content;
    private String[] codes;
    private String[] names;
    private NoRecordsFragment noRecordsFragment;
    private TradeListFragment tradeListFragment;
    private AlertListDialog dialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int selectPos = -1;
    public static TradeQueryInfo tradeInfo = new TradeQueryInfo();

    private Date today = new Date();

    private MyDate startMyDate;
    private MyDate endMyDate;

    private Date startDate;
    private Date endDate;
    private DatePickerDialog datePickerDialog;

    private LinearLayout resultLayout;
    private TextView tvCount, tvAmount;

    private boolean isFirstQuery = true;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_query);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("交易记录");
        navigationBar.setActionBtnEnabled(true);
        navigationBar.setActionBtnText("查询");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    if (check()) {
                        tradeInfo = new TradeQueryInfo();
                        query();
                    }
                }
            }
        });

        initView();
        initType();
        initDate();
    }

    private void initDate() {
        //初始化结束日期
        endDate = today;
        endTime.setText(dateFormat.format(today));
        Calendar calendarDate = Calendar.getInstance(Locale.CHINA);
        calendarDate.setTime(today);
        int cyear = calendarDate.get(Calendar.YEAR);
        int month = calendarDate.get(Calendar.MONTH);
        int day = calendarDate.get(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(calendar.DATE, -30);
        //初始化开始日期
        startDate = calendar.getTime();
        startTime.setText(dateFormat.format(calendar.getTime()));

        startMyDate = new MyDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        endMyDate = new MyDate(cyear, month, day);
    }

    private void initView() {
        startTime = (TextView) findViewById(R.id.tv_selection_start_date);
        endTime = (TextView) findViewById(R.id.tv_selection_end_date);
        transType = (TextView) findViewById(R.id.tv_selection_type);
        content = (FrameLayout) findViewById(R.id.trade_content);
        resultLayout = (LinearLayout) findViewById(R.id.layout_result);
        tvCount = (TextView) findViewById(R.id.tv_total_count);
        tvAmount = (TextView) findViewById(R.id.tv_total_amount);

        transType.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);

    }


    private void initType() {

        String retData = getIntent().getStringExtra("retData");
        if (retData == null)
            return;
        try {
            JSONArray jsonArray = new JSONArray(retData);
            if (jsonArray == null) {
                return;
            }
            JSONObject json = jsonArray.getJSONObject(0);
            String extInfo = json.optString("extInfo");
            JSONArray jarr = new JSONArray(extInfo);
            if (jarr.length() <= 0) {
                codes = new String[0];
                names = new String[0];
            } else {
                codes = new String[jarr.length()];
                names = new String[jarr.length()];
                for (int i = 0; i < jarr.length(); i++) {
                    try {
                        JSONObject jObject = jarr.getJSONObject(i);
                        codes[i] = jObject.getString("code");
                        names[i] = jObject.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean check() {

        try {
            Date fromDate = dateFormat.parse(startTime.getText().toString());
            Date toDate = dateFormat.parse(endTime.getText().toString());
            Date date = new Date();
            if (fromDate.after(toDate)) {
                toast("开始时间大于结束时间，请重新选择起始时间！");
                return false;
            } else if (toDate.after(date)) {
                toast("结束时间大于今天，请重新选择结束时间！");
                return false;
            } else if (transType.getText().toString().equals(getResources().getString(R.string.please_choose_date))) {
                toast(R.string.please_choose_date);
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void query() {
        showProgressWithNoMsg();
        String start = startTime.getText().toString();
        String end = endTime.getText().toString();
        if (!start.contains("00:00:00")) {//避免重复添加
            start += " 00:00:00";
        }
        if (!end.contains("23:59:59")) {//避免重复添加
            end += " 23:59:59";
        }

        TradeListFragment.page = 1;
        ShoudanService.getInstance().queryTradeRecords(false, TradeListFragment.page, start, end, codes[selectPos], new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        tradeInfo = new TradeQueryInfo();
                        tradeInfo = tradeInfo.parseObject(jsonObject);
                        if (tradeInfo.getTotalCount() > 0) {
                            resultLayout.setVisibility(View.VISIBLE);
                            tvCount.setText(String.valueOf("共" + tradeInfo.getSuccessCount() + "笔成功交易"));
                            tvAmount.setText(String.valueOf("总交易金额" + tradeInfo.getSuccessAmount()) + "元");
                            toListFragment();
                        } else {
                            resultLayout.setVisibility(View.GONE);
                            toNoRerordFragment();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_selection_start_date:
                showDateSelectedDialog(true);
                break;
            case R.id.tv_selection_end_date:
                showDateSelectedDialog(false);
                break;
            case R.id.tv_selection_type:
                showListDialog();
                break;

        }
    }

    private void toListFragment() {

        tradeListFragment = new TradeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("start", startTime.getText().toString());
        bundle.putString("end", endTime.getText().toString());
        bundle.putString("code", codes[selectPos]);
        tradeListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.trade_content, tradeListFragment);
        transaction.commit();
    }

    private void toNoRerordFragment() {
        noRecordsFragment = new NoRecordsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isTrade", true);
        noRecordsFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.trade_content, noRecordsFragment);
        transaction.commit();

    }

    private void shoudanStatics(String type) {
        String event = "";
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "刷卡收款":
                    event = ShoudanStatisticManager.TradeRecord_SwipeCollection;
                    break;
                case "扫码收款":
                    event = ShoudanStatisticManager.TradeRecord_scanCodeCollection;
                    break;
                case "大额收款":
                    event = ShoudanStatisticManager.TradeRecord_bigLimitCollection;
                    break;
                case "信用卡还款":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_CreditPayback;
                    break;
                case "个人转账":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_Transfer;
                    break;
                case "手机充值":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_Recharge;
                    break;
                case "一块夺宝":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_Duobao;
                    break;
                case "商户缴费":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_Jiaofei;
                    break;
                case "专享购买":
                    event = ShoudanStatisticManager.TradeRecord_FeesOfLife_ZhuanXiang;
                    break;
            }
            ShoudanStatisticManager.getInstance().onEvent(event, this);
        }
    }

    private void showListDialog() {

        dialog = DialogCreator.createListDialog(this, "", names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectPos = i;
                transType.setText(names[i]);
                /**
                 * 埋点统计
                 */
                shoudanStatics(names[i]);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(true);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tradeInfo = new TradeQueryInfo();
    }

    /**
     * 选择日期
     */
    boolean isDatePicCancel = false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int year;
    int monthOfYear;
    int dayOfMonth;
    String dateString;

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
                String strMonth = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                dateString = year + "-" + strMonth + "-" + strDay;
                //set date

                //更新另外一个时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, m, dayOfMonth);
                if (ifSetStartDate) {
                    //如果设置开始时间，则结束时间+6个月
                    calendar.add(Calendar.MONTH, 6);
                    Date tempDate = calendar.getTime();
                    if (tempDate.after(today)) {
                        tempDate = today;
                    }
                    try {
                        Date endDate = simpleDateFormat.parse(endTime.getText().toString());
                        //如果结束时间大于开始时间的6个月后，更新结束时间
                        if (endDate.after(tempDate)) {
                            String dateStr = simpleDateFormat.format(tempDate);

                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime(tempDate);
                            endMyDate.setYear(tempCal.get(Calendar.YEAR));
                            endMyDate.setMonth(tempCal.get(Calendar.MONTH));
                            endMyDate.setDay(tempCal.get(Calendar.DATE));

                            endTime.setText(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {

                    calendar.add(Calendar.MONTH, -6);
                    Date tempDate = calendar.getTime();

                    try {
                        Date startDate = simpleDateFormat.parse(startTime.getText().toString());
                        //如果开始时间小于结束时间的6个月前，更新开始时间
                        if (startDate.before(tempDate)) {

                            Calendar lastYearC = Calendar.getInstance();
                            lastYearC.setTime(today);
                            lastYearC.add(Calendar.YEAR, -1);
                            Date lastYearDate = lastYearC.getTime();
                            if (tempDate.before(lastYearDate)) {
                                tempDate = lastYearDate;
                            }
                            String dateStr = simpleDateFormat.format(tempDate);

                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime(tempDate);
                            startMyDate.setYear(tempCal.get(Calendar.YEAR));
                            startMyDate.setMonth(tempCal.get(Calendar.MONTH));
                            startMyDate.setDay(tempCal.get(Calendar.DATE));

                            startTime.setText(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        final int cyear, month, day;
        if (ifSetStartDate) {
            cyear = startMyDate.getYear();
            month = startMyDate.getMonth();
            day = startMyDate.getDay();
        } else {
            cyear = endMyDate.getYear();
            month = endMyDate.getMonth();
            day = endMyDate.getDay();
        }

        datePickerDialog = new DatePickerDialog(this, listener, cyear, month, day) {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                super.onDateChanged(view, year, month, day);
                //设置时间范围

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                setDate(view, calendar);
            }
        };
        datePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                isDatePicCancel = true;
                return false;
            }
        });
        datePickerDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        isDatePicCancel = true;
                    }
                }
        );
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isDatePicCancel) {
                    //query records
                    if (year < 1000)        //如果年小于1000，不设置时间
                        return;
                    if (ifSetStartDate) {
                        //设置开始日期
                        startTime.setText(dateString);
                        startMyDate = new MyDate(year, monthOfYear - 1, dayOfMonth);
                    } else {
                        //设置截止日期
                        endTime.setText(dateString);
                        endMyDate = new MyDate(year, monthOfYear - 1, dayOfMonth);
                    }
                }
                isDatePicCancel = false;
            }
        });
        datePickerDialog.setCancelable(true);
        datePickerDialog.show();

    }

    /**
     * @param calendar 根据产品的需求，日期设置的规则
     *                 1、最久只能查询到一年前的记录
     *                 2、一次查询的时间范围为半年
     *                 3、设置当前时间的时候，另外一个日期跟随变动。例如，设置开始时间为8个月前，则结束时间自动调整为2个月前。
     *                 4、如果时间超过限制，自动回滚到限制的范围内
     */
    private void setDate(DatePicker view, Calendar calendar) {


        //得到日期控件上的时间
        Date currentDate = calendar.getTime();

        //得到今天一年前的日期
        Calendar todayC = Calendar.getInstance();
        todayC.setTime(today);

        Calendar lastYearTodayC = Calendar.getInstance();
        lastYearTodayC.setTime(today);
        lastYearTodayC.add(Calendar.YEAR, -1);
        Date lastYearToday = lastYearTodayC.getTime();

        if (currentDate.before(lastYearToday)) {
            //如果当前时间小于最小时间，则设置为最小时间
//            datePickerDialog.onDateChanged(view,lastYearToday.getYear(),lastYearToday.getMonth(),lastYearToday.getDay());

            int year = lastYearTodayC.get(Calendar.YEAR);
            int month = lastYearTodayC.get(Calendar.MONTH);
            int day = lastYearTodayC.get(Calendar.DATE);

            view.init(year, month, day, datePickerDialog);
        } else if (currentDate.after(today)) {
            //如果当前时间大于今天，则设置为今天
//            datePickerDialog.onDateChanged(view,today.getYear(),today.getMonth(),today.getDay());

            int year = todayC.get(Calendar.YEAR);
            int month = todayC.get(Calendar.MONTH);
            int day = todayC.get(Calendar.DATE);

            view.init(year, month, day, datePickerDialog);
        }

    }
}
