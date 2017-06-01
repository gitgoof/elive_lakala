package com.lakala.elive.user.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * 图片轮播
 * @author hongzhiliang
 *
 */
public class ImagePageAdapter extends PagerAdapter {
	
	private List<ImageView> imageViews; // 滑动的图片集合
	
	public ImagePageAdapter(List<ImageView> imageViews){
		this.imageViews = imageViews;
	}
	
	@Override
	public int getCount() {
		return imageViews.size() == 0 ? 0 : imageViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(imageViews.get(arg1));
		return imageViews.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	public List<ImageView> getImageViews() {
		return imageViews;
	}

	public void setImageViews(List<ImageView> imageViews) {
		this.imageViews = imageViews;
	}
	
}
