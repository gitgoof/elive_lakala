package com.lakala.elive.merapply.gallery;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.merapply.gallery.bean.FolderBean;
import com.lakala.elive.merapply.gallery.util.ImageLoader;

import java.util.List;

/**
 * Created by wenhaogu on 16/7/22.
 */
public class ListImageDirPopuWindow extends PopupWindow {
    private int mWidth;
    private int mHeigth;
    private View mConvertView;
    private ListView mListView;

    private List<FolderBean> mDatas;

    public int checkItem;

    public interface OnDirSelecteListener {
        void onSeleted(FolderBean folderBean);
    }

    public OnDirSelecteListener mListener;

    public void setOnDirSelectedListener(OnDirSelecteListener mListener) {
        this.mListener = mListener;
    }


    public ListImageDirPopuWindow(Context context, List<FolderBean> datas, String currentDirName) {

        calWidthAndeHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popu_main, null);
        mDatas = datas;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeigth);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        //初始化是默认选择的条目
        for (int x = 0; x < mDatas.size(); x++) {
            if (mDatas.get(x).getName().equals(currentDirName)) {
                checkItem = x;
            }
        }

        initView(context);
        initEvent();
    }

    private void initView(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context, mDatas));
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView != null) {
                    mListener.onSeleted(mDatas.get(position));
//                    for (int i=0;i<mListView.getChildCount();i++){
//                        if (position==1){
//                            ImageView imgCheck = (ImageView) view.findViewById(R.id.id_imge_check);
//                            imgCheck.s
//                        }
//                    }
                    checkItem = position;
                }
            }
        });

    }

    /**
     * 计算宽高
     *
     * @param context
     */
    private void calWidthAndeHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeigth = (int) (outMetrics.heightPixels * 0.7);
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean> {

        private LayoutInflater mInflater;
        private List<FolderBean> mDatas;

        public ListDirAdapter(Context context, List<FolderBean> objects) {

            super(context, 0, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_popu_main, parent, false);
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_id_dir_item_image);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                viewHolder.mImgCheck = (ImageView) convertView.findViewById(R.id.id_imge_check);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FolderBean bean = getItem(position);
            //重置
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(bean.getFirstImgPath(), viewHolder.mImg);

            viewHolder.mDirName.setText(bean.getName());
            viewHolder.mDirCount.setText(bean.getCount() + "");

            if (checkItem == position) {
                viewHolder.mImgCheck.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mImgCheck.setVisibility(View.GONE);
            }

            return convertView;
        }

        public void setSelector() {

        }

        private class ViewHolder {
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
            ImageView mImgCheck;
        }
    }
}
