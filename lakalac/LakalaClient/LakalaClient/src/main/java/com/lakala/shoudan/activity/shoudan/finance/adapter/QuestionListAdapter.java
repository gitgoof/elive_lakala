package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.activity.shoudan.finance.bean.Question;

import java.util.List;

/**
 * Created by LMQ on 2015/10/12.
 */
public class QuestionListAdapter extends BaseAdapter {
    private List<Question> data;

    public QuestionListAdapter(List<Question> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null?0:data.size();
    }

    @Override
    public Question getItem(int position) {
        if(position >= 0 && position < getCount()){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1,null);
        }
        Question item = getItem(position);
        tv = (TextView)convertView;
        tv.setText(item.getQuestionContent());
        return convertView;
    }
}
