package com.lakala.shoudan.activity.shoudan.loan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LMQ on 2015/9/15.
 */
public class LoanMenuMainActivity extends AppBaseActivity {
    @Bind(R.id.lv_load_list)
    ListView lvLoadList;
    private LoanItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_menu_main);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("贷款");
        adapter = new LoanItemAdapter(LoanMenu.values());
        lvLoadList.addFooterView(
                LayoutInflater.from(context).inflate(
                        R.layout.loan_list_bottom_layout, null
                )
        );
        lvLoadList.setAdapter(adapter);
        lvLoadList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        LoanMenu menu = adapter.getItem(position);
                        if(menu == null){
                            return;
                        }
                        switch (menu){
                            case LOAN_TRAIL:
                                LoanTrailActivity.open(context);
                                break;
                        }
                    }
                }
        );
    }

    enum LoanMenu {
        LOAN_TRAIL(R.drawable.icon_loan_trail, "替你还", "信用卡暂时还不上？拉卡拉帮你！");
        private int icon;
        private String title;
        private String description;

        LoanMenu(int icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        public int getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public class LoanItemAdapter extends BaseAdapter {
        private LoanMenu[] data;

        public LoanItemAdapter(LoanMenu[] data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public LoanMenu getItem(int position) {
            if(position >=0 && position < getCount()){
                return data[position];
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.adapter_loan_menu_main, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            LoanMenu menu = getItem(position);
            holder.ivIcon.setImageResource(menu.getIcon());
            holder.tvTitle.setText(menu.getTitle());
            holder.tvDescription.setText(menu.getDescription());
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_icon)
            ImageView ivIcon;
            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_description)
            TextView tvDescription;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
