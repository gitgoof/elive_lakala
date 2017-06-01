package com.lakala.shoudan.activity.shoudan;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.platform.common.UILUtils;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.shoudan.R;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;
import java.util.Stack;

/**
 * 首页 广告adapter
 * @author ZhangMY
 *
 */
public class AdViewpageAdapter extends PagerAdapter{

	private SparseArray<View> imageViews = new SparseArray<View>();
	private List<AdDownloadManager.Advertise> mAdvertises = null;
	private Stack<View> cachPicTextStack = new Stack<View>();
	private Stack<View> cachPicStack = new Stack<View>();
	private DisplayImageOptions options;

	public AdViewpageAdapter(List<AdDownloadManager.Advertise> advertises){
		this.mAdvertises = advertises;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.showImageOnFail(R.drawable.pic_advertising)
				.showImageForEmptyUri(R.drawable.pic_advertising)
				.showImageOnLoading(R.drawable.pic_advertising)
				.build();
	}
	public void clear(){
		if(mAdvertises != null){
			mAdvertises.clear();
		}
		if(cachPicTextStack != null){
			cachPicTextStack.clear();
		}
        if (cachPicStack != null){
            cachPicStack.clear();
        }
	}

	@Override
	public int getCount() {
		if(mAdvertises == null || mAdvertises.size() == 0
                || mAdvertises.size() == 1){
			return 1;
		}else{
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		AdDownloadManager.Advertise ad = null;
		int adPosition = 0;
		if(mAdvertises == null || mAdvertises.size() == 0){
			ad = new AdDownloadManager.Advertise();
		}else{
			adPosition = position%mAdvertises.size();
			ad = mAdvertises.get(adPosition);
		}
		final Context context = container.getContext();
        View adView = null;
        String type = ad.getType();
        if("PIC".equals(type)){
            adView = cachPicStack.size() > 0 ? cachPicStack.pop() : null;
            if(adView == null){
                adView = createDefaultAdView(context);
            }
        }else {
            adView = cachPicTextStack.size() > 0 ? cachPicTextStack.pop() : null;
            if(adView == null){
                adView = createAdView(context, ad);
            }
        }
        if(adView instanceof ImageView){
            UILUtils.display(ad.getContent(), (ImageView)adView, options);
        }else{
            updateAdView(context,adView,ad);
        }
        adView.setTag(ad);
        imageViews.put(position,adView);
		container.addView(adView);
		return adView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imageViews.get(position));
        View view = imageViews.get(position);
        if(view instanceof ImageView){
            cachPicStack.push(view);
        }else{
            cachPicTextStack.push(view);
        }
	}
	private void updateAdView(final Context context,View view,AdDownloadManager.Advertise ad){
		ImageView ivAd = (ImageView)view.findViewById(R.id.iv_ad_image);
		TextView tvTitle = (TextView)view.findViewById(R.id.tv_ad_title);
		TextView tvContent = (TextView)view.findViewById(R.id.tv_ad_content);
		ivAd.setScaleType(ImageView.ScaleType.FIT_XY);
		view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdDownloadManager.Advertise temp = (AdDownloadManager.Advertise) v
                                .getTag();
                        String url = temp.getClickUrl();
                        if(TextUtils.isEmpty(url) ||"null".equals(url)){
                            return;
                        }
                        PublicEnum.Business.setIsAd(temp.getId());
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
        tvTitle.setText(strs[0]);
        if(strs.length >1){
            tvContent.setText(strs[1]);
        }

		UILUtils.display(ad.getContent(), ivAd, options);
	}
    private View createAdView(final Context context, AdDownloadManager.Advertise ad){

        View view = RelativeLayout.inflate(context, R.layout.advertisement_view,null);
		updateAdView(context, view, ad);
        return view;
    }
    private View createDefaultAdView(final Context context){
        ImageView imageView = new ImageView(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdDownloadManager.Advertise temp = (AdDownloadManager.Advertise) v.getTag();
                        String url = temp.getClickUrl();
                        if(TextUtils.isEmpty(url)){
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
        return imageView;
    }


	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
