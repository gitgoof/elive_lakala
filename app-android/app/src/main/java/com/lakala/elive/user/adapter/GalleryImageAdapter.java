package com.lakala.elive.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.lakala.elive.R;


/**
 * 
 * @author hongzhiliang
 *
 */
public class GalleryImageAdapter extends BaseAdapter {
	
    private Context context;
    
    Uri uri;
    Intent intent;
    ImageView imageView;
    GalleryPointListener mGalleryPointListener;
    
   public static Integer[] imgs = {
 			R.drawable.gg_01,
 			R.drawable.gg_02,
			R.drawable.gg_03
	};
    
    public GalleryImageAdapter(Context context, GalleryPointListener galleryPointListener) {
        this.context = context;  
        this.mGalleryPointListener = galleryPointListener;
    }  
  
    public int getCount() {  
        return Integer.MAX_VALUE;
    }  
  
    public Object getItem(int position) {
        return imgs[position % imgs.length];  
    }  
   
    public long getItemId(int position) {  
        return position;  
    }  
    
    public View getView(int position, View convertView, ViewGroup parent) {
        //Bitmap image;  
        if(convertView==null){  
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gallery_image,null); //实例化convertView
            Gallery.LayoutParams params = new Gallery.LayoutParams(
            		Gallery.LayoutParams.WRAP_CONTENT,
            		Gallery.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(params);
            convertView.setTag(imgs);  
        }else{
        	
        }  
        imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
        imageView.setImageResource(imgs[position % imgs.length]);
        //设置缩放比例：保持原样  
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mGalleryPointListener.changePointView(position % imgs.length);
        return convertView;  
    }  
    
    public interface GalleryPointListener {
		void changePointView(int cur);
	}
}  
