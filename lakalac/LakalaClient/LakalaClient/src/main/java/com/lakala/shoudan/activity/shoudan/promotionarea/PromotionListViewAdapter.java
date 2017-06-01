package com.lakala.shoudan.activity.shoudan.promotionarea;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.UILUtils;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.shoudan.activity.webbusiness.WebMallContainerActivity;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.List;

/**
 * Created by HJP on 2015/8/25.
 */
public class PromotionListViewAdapter extends BaseAdapter {
    private List<AdDownloadManager.Advertise> mAdvertises;
    private int width;
    Context context;

    public PromotionListViewAdapter(Context context, List<AdDownloadManager.Advertise> advertises, int width) {
        this.context = context;
        mAdvertises = advertises;
        this.width = width;
    }

    @Override
    public int getCount() {
        return mAdvertises == null ? 0 : mAdvertises.size();
    }

    @Override
    public AdDownloadManager.Advertise getItem(int i) {
        return mAdvertises.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder adImageView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_comotionarea, null);
            adImageView=new ViewHolder();
            adImageView.ivComotionareaItem=(ImageView)view.findViewById(R.id.iv_comotionarea_item);
            view.setTag(adImageView);
        } else {
            adImageView = (ViewHolder) view.getTag();
        }
        final View convertView = view;
        final AdDownloadManager.Advertise advert = getItem(i);
        UILUtils.load(advert.getContent(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override

            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.pic_bg_event);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                initImageView(bitmap, convertView, adImageView.ivComotionareaItem);
                adImageView.ivComotionareaItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtil.toast(context,"获取图片失败，请重新进入！");
                    }
                });
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                initImageView(bitmap, convertView, adImageView.ivComotionareaItem);
                adImageView.ivComotionareaItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(advert.getTitle().equals("专享购买")){
//                            WebMallContainerActivity.open(context, advert.getTitle(), advert.getRemark(), ActiveNaviUtils.Type.TEQUAN);
//                            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Promotion_Privilege_Purchase, context);
                            ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.TEQUAN);
                        }else{
                            Intent intent=new Intent(context, AdShareActivity.class);
                            intent.putExtra("url", advert.getClickUrl()).putExtra("title", advert.getTitle());
                            context.startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                File imageFile = UILUtils.findInCache(s);
                if(imageFile != null && imageFile.exists()){
                    Bitmap bitmap = UILUtils.loadSync(s);
                    onLoadingComplete(s,view,bitmap);
                }
            }
        });
        return view;
    }

    public void initImageView(Bitmap bitmap, View convertView, ImageView imageView) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int newH=(width * h) / w;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, newH);
        convertView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(bitmap);
     }
    static class ViewHolder {
        ImageView ivComotionareaItem;
    }
}
