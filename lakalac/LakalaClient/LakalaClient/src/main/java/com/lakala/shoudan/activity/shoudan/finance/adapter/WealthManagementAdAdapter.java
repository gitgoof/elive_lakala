package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;
import java.util.Stack;

/**
 * Created by HJP on 2015/9/6.
 */
public class WealthManagementAdAdapter extends BaseAdapter {
    private final DisplayImageOptions options;
    private List<AdDownloadManager.Advertise> mAdvertises;
    private Activity context;
    private Stack<View> textPicStack = new Stack<View>();
    private float adHeight = 0;
    public WealthManagementAdAdapter(Activity context,List<AdDownloadManager.Advertise> advertises){
        this.context=context;
        mAdvertises=advertises;
        adHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 89, context.getResources().getDisplayMetrics()
        );
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnFail(R.drawable.pic_bg_event)
                .showImageForEmptyUri(R.drawable.pic_bg_event)
                .showImageOnLoading(R.drawable.pic_bg_event)
                .build();

    }
    @Override
    public int getCount() {
        return mAdvertises == null ? 0 : mAdvertises.size();
    }

    @Override
    public AdDownloadManager.Advertise getItem(int i) {
        if(i >=0 && i<mAdvertises.size()){
            return mAdvertises.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AdDownloadManager.Advertise adverse = getItem(i);
        String type = adverse.getType();
        if (view == null) {
            view = createView(type, adverse);
        } else {
            ViewHolder holder = (ViewHolder) view.getTag();
            String tagType = holder.advertise.getType();
            if(!TextUtils.equals(type, tagType)){
                if(!"PIC".equals(tagType)){//如果为图文类型，则保存view对象重复利用
                    textPicStack.push(view);
                }
                view = createView(type, adverse);
            }
        }
        if(view instanceof ImageView){
            UILUtils.display(adverse.getContent(), (ImageView)view, options);
        }else{
            updateAdView(context,view,adverse);
        }
        return view;
    }

    private View createView(String type, AdDownloadManager.Advertise advertise) {
        View view;
        ViewHolder holder = new ViewHolder();
        holder.advertise = advertise;
        if("PIC".equals(type)){
            view = createPICView();
            holder.ivAd = (ImageView)view;
        }else{
            view = textPicStack.size() == 0?createAdView():textPicStack.pop();
            holder.ivAd = (ImageView)view.findViewById(R.id.iv_ad_image);
            holder.tvTitle = (TextView)view.findViewById(R.id.tv_ad_title);
            holder.tvContent = (TextView)view.findViewById(R.id.tv_ad_content);
        }
        view.setTag(holder);
        return view;
    }

    private View createAdView(){
        View view = RelativeLayout.inflate(context, R.layout.lc_advertisement_view, null);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null){
            params = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (int)adHeight
            );
        }else{
            params.height = (int)adHeight;
        }
        view.setLayoutParams(params);
        return view;
    }
    private void updateAdView(final Context context,View view,AdDownloadManager.Advertise ad){
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.ivAd.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdDownloadManager.Advertise temp = ((ViewHolder) v.getTag()).advertise;
                        String url = temp.getClickUrl();
                        if(TextUtils.isEmpty(url) ||"null".equals(url)){
                            return;
                        }
                        context.startActivity(
                                new Intent(
                                        context, AdShareActivity.class
                                ).putExtra("url",temp.getClickUrl()).putExtra("title",temp.getTitle())
                        );
                    }
                }
        );

        String newStr = ad.getRemark().replace("\\n", "______");
        String[] strs = newStr.split("______");
        holder.tvTitle.setText(strs[0]);
        if(strs.length >1){
            holder.tvContent.setText(strs[1]);
        }

        UILUtils.display(ad.getContent(), holder.ivAd, options);
    }
    private View createPICView(){
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int)adHeight
        );
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdDownloadManager.Advertise temp = ((ViewHolder) v.getTag()).advertise;
                        String url = temp.getClickUrl();
                        if(TextUtils.isEmpty(url) ||"null".equals(url)){
                            return;
                        }
                        context.startActivity(
                                new Intent(
                                        context, AdShareActivity.class
                                ).putExtra("url", temp.getClickUrl())
                                 .putExtra("title", temp.getTitle())
                        );
                    }
                }
        );
        return imageView;
    }
    class ViewHolder{
        AdDownloadManager.Advertise advertise;
        ImageView ivAd;
        TextView tvTitle;
        TextView tvContent;
    }
}
