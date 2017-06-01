package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.ui.component.SingleLineTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lianglong on 14-6-4.
 */
public class PayPwdQuestionListActivity extends BaseActionBarActivity implements AdapterView.OnItemClickListener {
    public static final String KEY_QUESTION_LIST = "KEY_QUESTION_LIST";

    private List<PayPwdQuestion> questions = new ArrayList<PayPwdQuestion>();
    private PayPwdQuestion currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentQuestion = getIntent().getParcelableExtra(PayPwdQuestion.class.getName());
        questions = getIntent().getParcelableArrayListExtra(KEY_QUESTION_LIST);

        setContentView(R.layout.common_list_view);
        navigationBar.setTitle(R.string.plat_password_security_prompt3);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new QuestionListAdapter());
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent backData = new Intent();
        backData.putExtra(PayPwdQuestion.class.getName(), questions.get(position));
        setResult(RESULT_OK, backData);
        finish();
    }

    private class QuestionListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public PayPwdQuestion getItem(int position) {
            return questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PayPwdQuestion question = getItem(position);
            if (convertView == null) {
                SingleLineTextView singleLineTextView = new SingleLineTextView(PayPwdQuestionListActivity.this);
                singleLineTextView.setPadding(20, 20, 20, 20);
                singleLineTextView.setVerticalLine(false);
                singleLineTextView.setEnableOnClickItemEvents(false);
                singleLineTextView.setRightArrowVisibility(View.GONE);
                singleLineTextView.setLeftTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                singleLineTextView.setRightIconResource(R.drawable.btn_choose_blue);
                convertView = singleLineTextView;
            }
            int v = question.equals(currentQuestion) ? View.VISIBLE : View.INVISIBLE;
            ((SingleLineTextView) convertView).setRightIconVisibility(v);
            ((SingleLineTextView) convertView).setLeftText(question.QuestionContent);
            return convertView;
        }

    }
}
