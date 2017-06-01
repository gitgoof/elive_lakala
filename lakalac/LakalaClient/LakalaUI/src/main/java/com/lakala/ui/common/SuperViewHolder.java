package com.lakala.ui.common;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/4/9 0009.
 */
/**
 * @author wuj
 *
 */
public class SuperViewHolder {
	 private SparseArray<View>mViews;//item中所有控件
	    private int mPosition;//行号
	    private View mConvertView;
	    private Context mContext;
	    private SuperViewHolder(Context context, ViewGroup parent,int layoutId,int position){
	        this.mPosition=position;
	        this.mContext=context;
	        mViews=new SparseArray<View>();
	        mConvertView= LayoutInflater.from(context).inflate(layoutId,parent,false);
	        mConvertView.setTag(this);
	    }

	    public static SuperViewHolder getViewHolder(Context context,View convertView, ViewGroup parent,int layoutId,int position){
	            if (convertView==null){
	                return new SuperViewHolder(context,parent,layoutId,position);
	            }else {
					SuperViewHolder holder=(SuperViewHolder) convertView.getTag();
					holder.mPosition=position;
	                return holder;
	            }
	    }

	    /**
	     * 通过控件id找到相应控件
	     */

	    public <T extends View> T getView(int viewId){
	        View view = mViews.get(viewId);
	        if (view==null){
	            view=mConvertView.findViewById(viewId);
	            mViews.put(viewId,view);
	        }
	        return (T) view;
	    }
	    
	    /**
	     * 隐藏控件
	     */

	    public void setGone(int viewId) {
	    	 View view =getView(viewId);
	    	 view.setVisibility(View.GONE);
		}
	    /**
	     * 显示控件
	     */
	    
	    public void setShow(int viewId) {
	    	View view =getView(viewId);
	    	view.setVisibility(View.VISIBLE);
	    }
	    /**
	     * 为TextVIew设置文字
	     */

	    public SuperViewHolder setText(int viewId,String text){
	        TextView tv=getView(viewId);
	        tv.setText(text);
	        return this;
	    }
	    /**
	     * 为TextVIew设置文字
	     */
	    
	    public SuperViewHolder setRadioButton(int viewId,String text){
	    	RadioButton tv=getView(viewId);
	    	tv.setText(text);
	    	return this;
	    }

	    /**
	     * 为ImageVIew设置图片
	     */

	    public SuperViewHolder setImage(int viewId,Bitmap bitmap){
	        ImageView iv=getView(viewId);
	        iv.setImageBitmap(bitmap);
	        return this;
	    }
	    
	    /**
	     * 为ImageVIew设置图片
	     */
	    
	    public SuperViewHolder setImageRes(int viewId,int imgId){
	    	ImageView iv=getView(viewId);
	    	iv.setImageResource(imgId);
	    	return this;
	    }
	    
	    /**
	     * 为ImageVIew设置图片
	     */
	    
	    public SuperViewHolder setImagePath(int viewId,String imgPath){
	    	View view=getView(viewId);
				ImageView iv=(ImageView) view;
				ImageLoader.getInstance().displayImage(imgPath, iv);
	    	return this;
	    }

	    /**
	     * 获取行号
	     */

	    public int getPosition(){
	        return mPosition;
	    }

	    /**
	     * 获取每个条目视图
	     */

	    public View getConvertView(){
	        return mConvertView;
	    }
}
