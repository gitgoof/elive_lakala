package com.lakala.elive.map.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.lakala.elive.R;
import com.lakala.elive.map.activity.MyShop;
import com.lakala.elive.map.bean.MyMarker;

/**
 * Created by xiaogu on 2017/3/13.
 */

public class ImageUtil {
    /**
     * 获得图标样式
     *
     * @param context
     * @return
     */
    public static BitmapDescriptor getTextBitmapDes(Context context, MyMarker myMarker) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.map_item_marker_layout, null);
        TextView shopName = (TextView) view.findViewById(R.id.tv_map_item_marker_name);
        shopName.setText(myMarker.getTitle());
        /*
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_img);

        textView.setText(myMarker.getTitle());
        switch (myMarker.getType()) {
            case 0:
                imageView.setImageResource(R.mipmap.type0);
                break;
            case 1:
                imageView.setImageResource(R.mipmap.type1);
                break;
            case 2:
                imageView.setImageResource(R.mipmap.type2);
                break;
            case 3:
                imageView.setImageResource(R.mipmap.type3);
                break;

        }
        */
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
        return bitmapDescriptor;
    }

    public static BitmapDescriptor getPointBitmapDes(Context context, MyShop shop) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.map_item_marker_layout, null);

        TextView shopName = (TextView) view.findViewById(R.id.tv_map_item_marker_name);
        shopName.setText(shop.name);
        /*
        if(shop.name.equals("店铺二")){
            shopName.setBackgroundResource(R.drawable.map_item_oran_bg);
        }
        */
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
        return bitmapDescriptor;
    }
}
