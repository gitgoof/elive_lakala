package com.lakala.shoudan.activity.password;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.common.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJP on 2015/11/16.
 */
public class SelectSecurityQuestionActivity extends AppBaseActivity implements AdapterView.OnItemClickListener {
    private ListView lvSecurityQuestion;
    private List<Question> questions = new ArrayList<Question>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question_select);

        init();
    }
    public void init(){
        questions = (List<Question>) getIntent().getSerializableExtra(SetSecurityQuestionActivity.KEY_QUESTION_LIST);
        initUI();
        lvSecurityQuestion=(ListView)findViewById(R.id.lv_security_question);
        lvSecurityQuestion.setAdapter(new QuestionListAdapter());
        lvSecurityQuestion.setOnItemClickListener(this);
    }
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("设置密保问题");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent();
        intent.putExtra(Parameters.SECURITY_QUESTION, questions.get(position));
        setResult(RESULT_OK, intent);
        finish();
    }
    private class QuestionListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Question getItem(int position) {
            return questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView == null) {
                convertView= LayoutInflater.from(SelectSecurityQuestionActivity.this).inflate(R.layout.item_security_question_select,null);
                viewHolder=new ViewHolder();
                viewHolder.tvQuestion=(TextView)convertView.findViewById(R.id.tv_question);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            Question question = getItem(position);
            if(question!=null){
                viewHolder.tvQuestion.setText((question.getQuestionContent()).toString());
            }

            return convertView;
        }
        class ViewHolder{
            TextView tvQuestion;
        }
    }
}
