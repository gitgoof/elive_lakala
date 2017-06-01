package com.lakala.shoudan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.StlmRem;
import com.lakala.platform.bean.StlmRem2;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.quickArrive.CardListActivity;
import com.lakala.shoudan.activity.shoudan.records.DrawMoneyHistoryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 极速到账首页二级列表adapter
 * Created by WangCheng on 2016/6/29.
 */
public class QuickListAdapter extends BaseAdapter{

    private Context context;
    private List<StlmRem> list;

    public QuickListAdapter(Context context, List<StlmRem> list){
        this.context=context;
        this.list=list;

    }

    @Override
    public int getCount() {
        return list==null? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
        if(position<getCount()-1){
            if (null == view) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_quick_list, null);
                holder.title= (TextView) view.findViewById(R.id.tv_title);
                holder.no= (TextView) view.findViewById(R.id.tv_no);
                holder.num= (TextView) view.findViewById(R.id.tv_num);
                holder.right= (ImageView) view.findViewById(R.id.iv_right);
                holder.ll_er=view.findViewById(R.id.ll_er);
                holder.lv_er= (ListView) view.findViewById(R.id.lv_er);
                holder.ll_contnet=view.findViewById(R.id.ll_content);
                holder.shorts=view.findViewById(R.id.view_short);
                holder.more=view.findViewById(R.id.ll_more);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.ll_contnet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(list.get(position).getRemStatus())){
                       return;
                    }
                    if(list.get(position).isOpen()){
                        list.get(position).setOpen(false);
                        notifyDataSetChanged();
                    }else {
                        //网络请求数据成功后刷新adapter
                        if(list.get(position).isHave()){//读取过网络数据不再第二次加载
                            list.get(position).setOpen(true);
                            notifyDataSetChanged();
                        }else {
                            getQuickList(position,"02".equals(list.get(position).getRemStatus()));//"02"表示交易成功数据else表示失败数据
                        }
                    }
                }
            });
            holder.lv_er.setAdapter(new QuickListErAdapter(context,list.get(position).list,list.get(position).getCount()));
            if(list.get(position).isOpen()){
                holder.right.setImageResource(R.drawable.down);
                holder.ll_er.setVisibility(View.VISIBLE);
            }else {
                holder.right.setImageResource(R.drawable.right);
                holder.ll_er.setVisibility(View.GONE);
            }
            if("02".equals(list.get(position).getRemStatus())){
                holder.num.setTextColor(context.getResources().getColor(R.color.font_gray_one2));
            }else {
                holder.num.setTextColor(context.getResources().getColor(R.color.red));
            }
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入划款查询页面
                    context.startActivity(new Intent(context,DrawMoneyHistoryActivity.class));
                }
            });
            if(list.get(position).getCount()>10){
                holder.more.setVisibility(View.VISIBLE);
            }else {
                holder.more.setVisibility(View.GONE);
            }
            holder.title.setText(list.get(position).getTitle());
            holder.no.setText(list.get(position).getCount()+"笔");
            holder.num.setText(list.get(position).getRemAmt()+"元");
            if(position==0){
                holder.shorts.setVisibility(View.VISIBLE);
            }else if(position==1&&!list.get(position).isOpen()){
                holder.shorts.setVisibility(View.GONE);
            }else if(position==1&&list.get(position).isOpen()){
                holder.shorts.setVisibility(View.VISIBLE);
            }
        }else {//最后一个item
            view = LayoutInflater.from(context).inflate(R.layout.quick_down, null);
            view.findViewById(R.id.ll_go).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CardListActivity.class).putExtra("json",list.get(position).getJsonObject().toString()));
                }
            });
            TextView tv_num= (TextView) view.findViewById(R.id.tv_e);
            tv_num.setText(list.get(position).getRemAmt()+".00元");
        }
        return view;
    }
    class ViewHolder{
        private boolean isHave=false;
        ImageView right;
        TextView title,no,num;
        View ll_er,ll_contnet;
        ListView lv_er;
        View shorts,more;

        public boolean isHave() {
            return isHave;
        }

        public void setHave(boolean have) {
            isHave = have;
        }
    }


    public void getQuickList(final int position, boolean isSucce){
        String url="";
        if(isSucce){
            url="v1.0/business/speedArrivalD0/cash/hisDetailQuery/status/SUCCESS";
        }else {
            url="v1.0/business/speedArrivalD0/cash/hisDetailQuery/status/ERROR";
        }
        BusinessRequest businessRequest=BusinessRequest.obtainRequest(context,url, HttpRequest.RequestMethod.GET,true);
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("000000".equals(resultServices.retCode)){
                    LogUtil.print("<S>","onSuccess");
                    try {
                        JSONObject jsonObject=new JSONObject(resultServices.retData);
                        JSONArray js=jsonObject.getJSONArray("datas");
                        list.get(position).list.clear();
                        for (int i=0;i<js.length();i++){
                            JSONObject json=js.getJSONObject(i);
                            list.get(position).list.add(new StlmRem2(json));
                        }
                        list.get(position).setHave(true);
                        if(list.get(position).list.size()==0){
                            ToastUtil.toast(context, "没有更过数据");
                        }else {
                            list.get(position).setOpen(true);
                            notifyDataSetChanged();
                        }
                        LogUtil.print("notifyDataSetChanged");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                ToastUtil.toast(context,R.string.socket_fail);
            }
        }));
        businessRequest.execute();
    }
}
