package com.test.gf.myandroidtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.gf.viewtest.MyListView;
import com.test.gf.viewtest.NetListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaofeng on 2017/4/21.
 */

public class ListViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_test);

        myListView = (MyListView) findViewById(R.id.self_list_show);
        initData();
    }
    private MyListView myListView;
    private void initData(){

        final List<String> list = new ArrayList<String>();
        for(int i = 0 ;i < 30;i++){
            list.add("列表数据 行号:" + i);
        }

        myListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView textView = new TextView(ListViewActivity.this);
                textView.setText((String)getItem(position));
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setPadding(20,15,25,15);

                return textView;
            }
        });

    }
}
