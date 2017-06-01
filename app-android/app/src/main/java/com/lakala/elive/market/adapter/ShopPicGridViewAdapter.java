package com.lakala.elive.market.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lakala.elive.R;
import com.lakala.elive.beans.ImageItemInfo;

import java.util.ArrayList;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/21 18:07
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class ShopPicGridViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private int selectedPosition = -1;

    private boolean shape;

    private Context mContext;

    ArrayList<ImageItemInfo> tempSelectBitmapList = new ArrayList<ImageItemInfo>();   //选择的图片的临时列表

    public ArrayList<ImageItemInfo> getTempSelectBitmap() {
        return tempSelectBitmapList;
    }

    public ArrayList<ImageItemInfo> getTempSelectBitmapList() {
        return tempSelectBitmapList;
    }

    public void setTempSelectBitmapList(ArrayList<ImageItemInfo> tempSelectBitmapList) {
        this.tempSelectBitmapList = tempSelectBitmapList;
    }

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    public ShopPicGridViewAdapter(Context context,ArrayList<ImageItemInfo> tempSelectBitmapList) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.tempSelectBitmapList = tempSelectBitmapList;
    }


    public int getCount() {
        return tempSelectBitmapList.size();
    }

    public Object getItem(int id) {
        return tempSelectBitmapList.get(id);
    }

    public long getItemId(int id) {
        return id;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageItemInfo itemInfo = tempSelectBitmapList.get(position);
        if (itemInfo.isSelected) {
            holder.image.setImageBitmap(tempSelectBitmapList.get(position).getBitmap());
        } else {
            holder.image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_open_camera));
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }


}
