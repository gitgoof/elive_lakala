package com.lakala.elive.merapply.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lakala.elive.R;
import com.lakala.elive.merapply.gallery.util.ImageLoader;

import java.util.List;

/**
 * Created by wenhaogu on 16/7/21.
 */
public class ImagerAdapter extends BaseAdapter {
    //private static Set<String> mSeletedImg=new HashSet<String>();
    private Context mContext;
    private List<String> mImgPaths;
    private String mDirPath;
    private LayoutInflater mIndlater;

    public ImagerAdapter(Context context, List<String> mDatas, String dirPath) {
        this.mContext = context;
        this.mImgPaths = mDatas;
        this.mDirPath = dirPath;
        mIndlater = LayoutInflater.from(mContext);
    }

    public void setNewData(List<String> mDatas, String dirPath) {
        //this.mImgPaths.clear();
        this.mImgPaths=mDatas;
        this.mDirPath = dirPath;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mIndlater.inflate(R.layout.item_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
            viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //重置状态
        viewHolder.mImg.setImageResource(R.drawable.pictures_no);
    //    viewHolder.mSelect.setImageResource(R.drawable.pictures_unselected);
        viewHolder.mImg.setColorFilter(null);

        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPath + "/" + mImgPaths.get(position), viewHolder.mImg);
        final String filePath = mDirPath + "/" + mImgPaths.get(position);
        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickListener.itemClickListener(filePath);

//                //已选择
//                if(mSeletedImg.contains(filePath)){
//                   mSeletedImg.remove(filePath);
//                    viewHolder.mImg.setColorFilter(null);
//                    viewHolder.mSelect.setImageResource(R.drawable.pictures_unselected);
//                 //为选择
//                }else {
//                    mSeletedImg.add(filePath);
//                    viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
//                    viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
//                }
            }
        });
//        if (mSeletedImg.contains(filePath)){
//            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
//            viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
//        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView mImg;
        private ImageButton mSelect;
    }

    private ImagerAdapterClickListener clickListener;

    public void setClickListener(ImagerAdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ImagerAdapterClickListener {
        void itemClickListener(String filePath);
    }


}