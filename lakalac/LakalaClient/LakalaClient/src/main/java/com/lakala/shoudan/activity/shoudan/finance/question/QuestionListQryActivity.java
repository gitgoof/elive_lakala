package com.lakala.shoudan.activity.shoudan.finance.question;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.adapter.QuestionListAdapter;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.shoudan.finance.bean.QuestionListReturnData;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/10/12.
 */
public class QuestionListQryActivity extends AppBaseActivity {
    private ListView lvQuestion;
    private QuestionListAdapter adapter;
    private List<Question> data = new ArrayList<Question>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionlist_qry);
        initUI();
        initView();
        qryList();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("请选择密保问题");
    }

    private void initView() {
        lvQuestion = (ListView) findViewById(R.id.lv_question);
        adapter = new QuestionListAdapter(data);
        lvQuestion.setAdapter(adapter);
        lvQuestion.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Question question = adapter.getItem(position);
                        Intent data = new Intent();
                        data.putExtra(Constants.IntentKey.TRANS_INFO,JSON.toJSONString(question));
                        setResult(RESULT_OK,data);
                        finish();
                    }
                }
        );
    }

    private void qryList() {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    QuestionListReturnData retData = JSON.parseObject(
                            responseData.toString(), QuestionListReturnData.class
                    );
                    if(retData != null && retData.getList() != null){
                        data.addAll(retData.getList());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
            }
        };
        FinanceRequestManager.getInstance().questionListQry(
                listener
        );
    }

    public static void startForResult(Activity activity, int requestCode){
        Intent intent = new Intent(activity,QuestionListQryActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }
}
