package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LastApplyInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Loan;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录
 * 
 * @author ldm
 * 
 */
public class LoanHistoryActivity extends AppBaseActivity implements
		OnClickListener {
	private static final int PAGE_SIZE = 10;

	private TextView btnSure;
	private ListView listView;

	private InputMethodManager imm;

	private int currentpage = 1;//当前页
	private int pagesize = PAGE_SIZE;//每页大小
	private String[] loantypes = {"L","R"};//L：借款记录  R：还款记录
	private String loantype = loantypes[0];//查询类型

	private List<Loan> loans = new ArrayList<Loan>();
	private BorrowRecordAdapter mBorrowRecordAdapter;
	private int totalsize = 0;
	private boolean isLast;
	private int lastItem = 0;
	private int firstItem = 0;
	
	private LastApplyInfo lastApplyInfo ;

    public static void open(AppBaseActivity context){
        Intent intent = new Intent(context,LoanHistoryActivity.class);
        getLastTransAndHistoryRecord(context,intent);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_history);
        Intent intent = getIntent();
        ResultServices lastTrans = (ResultServices) intent.getSerializableExtra("lastTrans");
        ResultServices historyResult = (ResultServices) intent.getSerializableExtra
                ("historyResult");
		initUI();
        lastTrans(lastTrans);
        historyResult(historyResult);
	}
	
	protected void initUI() {
		navigationBar.setTitle("历史记录");

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		// btnSure.setBtnTextRight();
		btnSure.setOnClickListener(this);
		
		findViewById(R.id.payforyou_listview).setVisibility(View.GONE);
		listView=(ListView) findViewById(R.id.payforyou_listview);
		mBorrowRecordAdapter = new BorrowRecordAdapter(loans);
		listView.setAdapter(mBorrowRecordAdapter);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mBorrowRecordAdapter != null) {
		            if(currentpage == totalsize&&isLast){
		            	//加载完毕
                        ToastUtil.toast(context,getString(R.string.load_records_finished));
		            }
		            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isLast && currentpage < totalsize ) {
		            	// 加载下一页数据
		            	getHistoryRecordThread();
		            }
		        }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount -1;
				firstItem = firstVisibleItem;
				if(firstVisibleItem+visibleItemCount == totalItemCount){
					isLast = true;
				}else {
					isLast = false;
				}
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Loan loan = loans.get(position);
				if(loan.getContractno().equals(lastApplyInfo.getContractno())){
					Intent intent = new Intent(LoanHistoryActivity.this,LoanDetailActivity.class);
					intent.putExtra(LoanDetailActivity.LOAN, loan);
					intent.putExtra(LoanDetailActivity.LAST_APPLY_LOAN, lastApplyInfo);
					startActivity(intent);
				}else{
					// 不是最后一个记录
					Intent intent = new Intent(LoanHistoryActivity.this,LoanDetailNotLastActivity.class);
                    intent.putExtra(LoanDetailActivity.LOAN, loan);
                    startActivity(intent);

				}
			}
		});
	}

	private void showNoResult(){
		findViewById(R.id.norecord).setVisibility(View.VISIBLE);
		findViewById(R.id.bottommenu).setVisibility(View.VISIBLE);
		
		findViewById(R.id.payforyou_listview).setVisibility(View.GONE);
		
	}
	
	private void showResult(){
		findViewById(R.id.norecord).setVisibility(View.GONE);
		findViewById(R.id.bottommenu).setVisibility(View.GONE);
		
		listView.setVisibility(View.VISIBLE);
	}
	
	private static void getLastTransAndHistoryRecord(final AppBaseActivity context, final Intent
            intent){
        context.showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                if(result.isRetCodeSuccess()){
                    intent.putExtra("lastTrans",result);
                    //查询历史
                    getHistoryRecordList(context, intent);
                }else{
                    context.hideProgressDialog();
                    ToastUtil.toast(context, result.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getApplyLatest(callback);
	}

    private void lastTrans(ResultServices result) {
        try {
            JSONObject jsonData = new JSONObject(result.retData);
            lastApplyInfo = new LastApplyInfo();
            if(jsonData.length()>0){
                lastApplyInfo.unpackLastApplyInfo(jsonData);
            }
        }catch (Exception e) {
            e.printStackTrace();
            hideProgressDialog();
        }
    }

    private void getHistoryRecordThread(){
        showProgressWithNoMsg();
        getHistoryRecordList();
	}
    /**
     * 获取历史记录
     * @throws Exception
     */
    private void getHistoryRecordList(){
        //生成历史记录
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonData = new JSONObject(result.retData);
                        totalsize = Integer.parseInt(jsonData.optString("totalsize"));
                        loans.addAll(Loan.unpackLoan(jsonData.getJSONArray("loans")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    currentpage++;
                    if (loans.size() > 0) {
                        mBorrowRecordAdapter.notifyDataSetChanged();
                        showResult();
                    } else {
                        showNoResult();
                    }
                } else {
                    ToastUtil.toast(context, result.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getHistoryTrans(currentpage, pagesize, loantype, callback);

    }

	/**
	 * 获取历史记录
	 * @throws Exception
     * @param context
     * @param intent
	 */
	private static void getHistoryRecordList(final AppBaseActivity context, final Intent intent){
		//生成历史记录
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices result) {
                context.hideProgressDialog();
                if (result.isRetCodeSuccess()) {
                    intent.putExtra("historyResult",result);
                    context.startActivity(intent);
                } else {
                    ToastUtil.toast(context, result.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getHistoryTrans(1, PAGE_SIZE, "L", callback);

	}

    private void historyResult(ResultServices result) {
        try {
            JSONObject jsonData = new JSONObject(result.retData);
            totalsize = Integer.parseInt(jsonData.optString("totalsize"));
            loans.addAll(Loan.unpackLoan(jsonData.getJSONArray("loans")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentpage++;
        if (loans.size() > 0) {
            mBorrowRecordAdapter.notifyDataSetChanged();
            showResult();
        } else {
            showNoResult();
        }
    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			Intent intent = new Intent(LoanHistoryActivity.this,LoanTrailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
